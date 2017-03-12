package br.furb.packing;

import br.furb.common.Polygon;

public class PackingResult {
	private final Polygon[] packing;

	private final double height;

	public PackingResult(Polygon[] packing, double height) {
		super();
		this.packing = packing;
		this.height = height;
	}

	public Polygon[] getPacking() {
		return packing;
	}

	public double getHeight() {
		return height;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new PackingResult(packing, height);
	}
}
