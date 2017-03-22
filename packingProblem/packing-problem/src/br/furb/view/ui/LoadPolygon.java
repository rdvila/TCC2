package br.furb.view.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import br.furb.common.Point;
import br.furb.common.Polygon;
import br.furb.dataset.DatasetReader;
import br.furb.dataset.SVGReader;
import br.furb.dataset.XMLReader;

public class LoadPolygon implements ActionListener {

	private Polygon[] polygons;
	private final JPanel panelMain;

	private static final int SIZE = 50;
	private final LoadListener listener;
	private final IDataChangeListener listener2;

	public LoadPolygon(JPanel scrollPane, LoadListener listener, IDataChangeListener listener2) {
		this.panelMain = scrollPane;
		this.listener = listener;
		this.listener2 = listener2;
	}

	public Polygon[] getPolygons() {
		return polygons;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String file = null;
		JFileChooser fileChooser = new JFileChooser("./src/br/furb/dataset/source/");
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile().getPath();
		} else {
			return;
		}
		panelMain.removeAll();

		DatasetReader xmlReader = new XMLReader();
		int end = file.length();
		
		String ext = String.valueOf(file.charAt(end-3)) + String.valueOf(file.charAt(end-2)) + String.valueOf(file.charAt(end-1));
		if (ext.equals("svg")) {
			xmlReader = new SVGReader();
		}

		polygons = xmlReader.readXML(file);
		printPolygons(polygons);
		for (int i = 0; i < polygons.length; i++) {
			Polygon polygon = polygons[i];
			PolygonPanel polygonPanel = new PolygonPanel(polygon);
			polygonPanel.setPreferredSize(new Dimension(SIZE + 2, SIZE + 2));
			panelMain.add(polygonPanel);
		}
		panelMain.setPreferredSize(new Dimension(polygons.length * (SIZE + 3), panelMain.getPreferredSize().height));

		listener.notifyLoaded(polygons, xmlReader.getBorderX(), xmlReader.getBorderY());
		listener2.notifyChanged();
	}

	private void printPolygons(Polygon[] polygons) {
		for (Polygon p : polygons) {
			System.out.println(p.getId());
			for (Point po : p.getPoints()) {
				System.out.println(String.format("%f x %f", po.getX(), po.getY()));
			}
			System.out.println();
		}
		
	}

	class PolygonPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		private final java.awt.Polygon polygonView;

		public PolygonPanel(Polygon polygon) {
			polygonView = new java.awt.Polygon();

			double max = Math.max(polygon.getHeight(), polygon.getWidth());

			for (Point point : polygon.getPoints()) {
				polygonView.addPoint((int) (point.x * SIZE / max), SIZE - (int) (point.y * SIZE / max));
			}
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(Color.MAGENTA);
			g.fillPolygon(polygonView);
			g.setColor(Color.BLACK);
			g.drawPolygon(polygonView);
		}

	}

}
