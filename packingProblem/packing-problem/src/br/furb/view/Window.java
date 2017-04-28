package br.furb.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Double;
import java.util.List;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLJPanel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RepaintManager;
import javax.swing.WindowConstants;

import br.furb.common.Point;
import br.furb.common.Polygon;
import br.furb.packing.LocalSearch;
import br.furb.packing.PackingExecutor;
import br.furb.packing.PackingResult;
import br.furb.packing.StopCriteria;
import br.furb.view.elements.ViewPolygon;
import br.furb.view.ui.IDataChangeListener;
import br.furb.view.ui.LoadListener;
import br.furb.view.ui.LoadPolygon;

public class Window extends JFrame implements IDataChangeListener {

	private static final long serialVersionUID = 1L;

	private Canvas canvas;

	private GLJPanel panel;

	private JLabel lblStatus;

	private JLabel lblTempo;

	private JLabel lblAltura;

	private JComboBox cmbParada;

	private JComboBox cmbRotacoes;

	private JComboBox cmbBusca;

	private JTextField edtParada;

	private JPanel panelBottom;

	private Window() {
		super("Tela Principal");
		setPreferredSize(new Dimension(1000, 700));
		canvas = new Canvas();

		JPanel panelNorth = new JPanel();// layout
		panelNorth.setPreferredSize(new Dimension(getPreferredSize().width, 120));

		JPanel pnlTool = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		pnlTool.setPreferredSize(new Dimension(getPreferredSize().width, 30));
		panelNorth.add(pnlTool, BorderLayout.NORTH);

		int tamanhoPainel = 80;
		JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 5));
		painel.setPreferredSize(new Dimension(getPreferredSize().width - 10, tamanhoPainel));
		painel.setBackground(Color.WHITE);
		JScrollPane sb1 = new JScrollPane(painel);
		sb1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sb1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		sb1.setPreferredSize(new Dimension(new Dimension(getPreferredSize().width, tamanhoPainel)));
		sb1.repaint();
		panelNorth.add(sb1, BorderLayout.CENTER);
		add(panelNorth, BorderLayout.NORTH);

		int infoHeight = 25;
		panelBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 120, 0));
		panelBottom.setPreferredSize(new Dimension(getPreferredSize().width, infoHeight));
		add(panelBottom, BorderLayout.SOUTH);

		JPanel pnlStatus = new JPanel();
		JLabel lblCptStatus = new JLabel("Status:");
		lblStatus = new JLabel();
		pnlStatus.add(lblCptStatus);
		pnlStatus.add(lblStatus);
		panelBottom.add(pnlStatus);

		JPanel pnlTempo = new JPanel();
		JLabel lblCptTempo = new JLabel("Tempo:");
		lblTempo = new JLabel();
		pnlTempo.add(lblCptTempo);
		pnlTempo.add(lblTempo);
		panelBottom.add(pnlTempo);

		JPanel pnlAltura = new JPanel();
		JLabel lblCptAltura = new JLabel("Altura:");
		lblAltura = new JLabel();
		pnlAltura.add(lblCptAltura);
		pnlAltura.add(lblAltura);
		panelBottom.add(pnlAltura);

		LoadListener loadListener = new LoadListener();

		JButton button = new JButton("Carregar arquivo");
		button.addActionListener(new LoadPolygon(painel, loadListener, this));
		pnlTool.add(button);

		JPanel pnlBusca = new JPanel();
		JLabel lblBusca = new JLabel("Busca:");
		cmbBusca = new JComboBox(new Object[] {"Jenetic", "Hill Climbing", "Tabu Search"  });
		pnlBusca.add(lblBusca);
		pnlBusca.add(cmbBusca);
		pnlTool.add(pnlBusca);

		JPanel pnlParada = new JPanel();
		JLabel lblParada = new JLabel("Critério parada:");
		cmbParada = new JComboBox(new Object[] { "Loop", "Tempo" });
		pnlParada.add(lblParada);
		pnlParada.add(cmbParada);
		pnlTool.add(pnlParada);

		edtParada = new JTextField("1");
		edtParada.setPreferredSize(new Dimension(60, 25));
		edtParada.setHorizontalAlignment(JTextField.RIGHT);
		edtParada.repaint();
		pnlTool.add(edtParada);

		JPanel pnlRotacoes = new JPanel();
		JLabel lblRotacoes = new JLabel("Rotações:");
		cmbRotacoes = new JComboBox(new Object[] { "1", "2", "4" });
		pnlRotacoes.add(lblRotacoes);
		pnlRotacoes.add(cmbRotacoes);
		pnlTool.add(pnlRotacoes);

		button = new JButton("Gerar empacotamento");
		button.addMouseListener(new GeneratorKeyListener(loadListener));
		pnlTool.add(button);

		GLCapabilities glCapabilities = new GLCapabilities();
		glCapabilities.setRedBits(8);
		glCapabilities.setBlueBits(8);
		glCapabilities.setGreenBits(8);
		glCapabilities.setAlphaBits(8);

		panel = new GLJPanel(glCapabilities);
		panel.addGLEventListener(canvas);
		panel.requestFocus();
		add(panel);

		pack();
		RepaintManager.setCurrentManager(new MyRepaintManager());
	}

	private void createCanvas(double width, double height) {
		GLCapabilities glCapabilities = new GLCapabilities();
		glCapabilities.setRedBits(8);
		glCapabilities.setBlueBits(8);
		glCapabilities.setGreenBits(8);
		glCapabilities.setAlphaBits(8);

		double percent = height * 0.95;
		canvas.universeBound.right = height + percent;
		canvas.universeBound.top = width;
		panel.repaint();
		panel.revalidate();
		notifyChanged();
	}

	private void printPolygon(Polygon polygon, Color color) {
		ViewPolygon viewPolygon = null;
		List<Point> points = polygon.getPoints();
		for (int i = 0; i < points.size(); i++) {
			Double viewPoint = new Double(points.get(i).x, points.get(i).y);
			if (i == 0) {
				viewPolygon = new ViewPolygon(viewPoint, color);
			}
			viewPolygon.getPoints().add(viewPoint);
		}
		Double viewPoint = new Double(points.get(0).x, points.get(0).y);
		viewPolygon.getPoints().add(viewPoint);
		canvas.addElement(viewPolygon);
	}

	public static void main(String[] args) {
		Window window = new Window();
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	class MyRepaintManager extends RepaintManager {
		@Override
		public void addDirtyRegion(java.awt.Window window, int x, int y, int w, int h) {
			super.addDirtyRegion(window, x, y, w, h);
			paintDirtyRegions();
		}
	}

	class GeneratorKeyListener extends MouseListenerAdapter {

		private final LoadListener loadListener;

		public GeneratorKeyListener(LoadListener loadListener) {
			this.loadListener = loadListener;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			updateStatus("Gerando empacotamento");
			updateData(0, 0);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (loadListener.getPolygons() != null) {

				createCanvas(loadListener.getWidth(), loadListener.getHeight());

				int rotations = Integer.parseInt((String) cmbRotacoes.getSelectedItem());
				StopCriteria stopCriteria = StopCriteria.getValue((String) cmbParada.getSelectedItem());
				LocalSearch localSearch = LocalSearch.getValue((String) cmbBusca.getSelectedItem());
				int stopValue = Integer.parseInt(edtParada.getText());

				long start = System.currentTimeMillis();

				PackingExecutor packingExecutor = new PackingExecutor();
				PackingResult packingResult = packingExecutor.executePacking(loadListener.getPolygons(),//
						loadListener.getHeight(), rotations, stopCriteria, stopValue, localSearch, Window.this);

				long end = System.currentTimeMillis() - start;
				canvas.deleteAll();
				int i = 0;
				for (Polygon polygon : packingResult.getPacking()) {
					printPolygon(polygon, Color.BLACK);
					i++;
				}

				System.out.println("Resultado ***********************");
				System.out.println("Tempo:" + end);
				System.out.println(packingResult.getHeight());

				updateStatus("Empacotamento finalizado");
				updateData(end, packingResult.getHeight());

				canvas.display();
			}
		}

	}

	@Override
	public void notifyChanged() {
		pack();
		repaint();
		canvas.deleteAll();
		updateData(0, 0);
		updateStatus("");
	}

	@Override
	public void notifyChanged(PackingResult packingResult) {
		int i = 0;
		canvas.deleteAll();
		for (Polygon polygon : packingResult.getPacking()) {
			printPolygon(polygon, Color.BLACK);
			i++;
		}
		updateStatus("Executando empacotamento");
		updateData(0, packingResult.getHeight());
		canvas.display();
	}

	private void updateStatus(String msg) {
		lblStatus.setText(msg);
	}

	private void updateData(long time, double height) {
		int seconds = (int) (time / 1000);

		int minutes = seconds / 60;
		int second = seconds - minutes * 60;
		lblTempo.setText(String.format("%02d:%02d", minutes, second));
		lblAltura.setText(String.valueOf((int) height));
		lblAltura.repaint();
	}

}
