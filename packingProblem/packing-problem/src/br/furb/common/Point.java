package br.furb.common;

public class Point {

	public double x;

	public double y;

	public Point next;

	public Point prior;

	public Point(double x, double y) {

		this.x = x;
		this.y = y;
	}

	public Point() {
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Point getNext() {
		return next;
	}

	public void setNext(Point next) {
		this.next = next;
	}

	public Point getPrior() {
		return prior;
	}

	public void setPrior(Point prior) {
		this.prior = prior;
	}

	@Override
	public boolean equals(Object obj) {
		Point point = (Point) obj;
		return MathHelper.compareDouble(this.x, point.x, Transform.THRESHOLD) == 0
				&& //
				MathHelper.compareDouble(this.y, point.y, Transform.THRESHOLD) == 0;
	}

	@Override
	public String toString() {
		return "x=" + x + " y=" + y;
	}

	public int compareX(Point point) {
		return MathHelper.compareDouble(this.x, point.x, Transform.THRESHOLD);
	}

	public int compareY(Point point) {
		int compareDouble = MathHelper.compareDouble(this.y, point.y,
				Transform.THRESHOLD);
		return compareDouble;
	}

}
