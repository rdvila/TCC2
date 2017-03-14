package br.furb.packing;

import br.furb.common.Polygon;

public class PackingResult implements Comparable<PackingResult> {
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

	@Override
	public int compareTo(PackingResult o) {
		if (getHeight() > o.getHeight()) {
			return 1;
		}
		
		if (getHeight() < o.getHeight()) {
			return -1;
		}
		
		return 0;
	}
	
	@Override
	public String toString() {
		return String.valueOf(getHeight());
	}
}
