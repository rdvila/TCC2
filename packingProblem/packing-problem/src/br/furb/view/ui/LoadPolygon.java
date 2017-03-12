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

		XMLReader xmlReader = new XMLReader();

		polygons = xmlReader.readXML(file);
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
