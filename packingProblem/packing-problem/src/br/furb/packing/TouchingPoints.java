package br.furb.packing;

import br.furb.common.Point;

public class TouchingPoints {
	public Point sPoint;
	public Point mPoint;
	public boolean fromStationary;
	public boolean isMiddleEdge;

	public TouchingPoints(Point sPoint, Point mPoint) {
		super();
		this.sPoint = sPoint;
		this.mPoint = mPoint;
	}

	public TouchingPoints(Point sPoint, Point mPoint, boolean fromStationary, boolean isMiddleEdge) {
		super();
		this.sPoint = sPoint;
		this.mPoint = mPoint;
		this.fromStationary = fromStationary;
		this.isMiddleEdge = isMiddleEdge;
	}

	@Override
	public boolean equals(Object obj) {
		TouchingPoints touchingP = (TouchingPoints) obj;

		boolean equalsPoints = sPoint.equals(touchingP.sPoint) && mPoint.equals(touchingP.mPoint);

		if (isMiddleEdge) {
			return equalsPoints && fromStationary == touchingP.fromStationary;
		}
		return equalsPoints;

	}
}