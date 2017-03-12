package br.furb.view.ui;

import br.furb.common.Polygon;

public class LoadListener {

	private Polygon[] polygons;
	private double width;
	private double height;

	public void notifyLoaded(Polygon[] polygons, double width, double height) {
		this.polygons = polygons;
		this.width = width;
		this.height = height;
	}

	public Polygon[] getPolygons() {
		return polygons;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

}
