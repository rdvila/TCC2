package br.furb.view;

public class Bound {

	public final double left;
	public double right;
	public final double bottom;
	public double top;

	public Bound(double left, double right, double botton, double top) {
		this.left = left;
		this.right = right;
		this.bottom = botton;
		this.top = top;
	}

	@Override
	public String toString() {
		return String.format("L:%f R:%f B:%f T:%f", left, right, bottom, top);
	}
}
