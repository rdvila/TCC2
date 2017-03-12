package br.furb.packing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.furb.common.MathHelper;
import br.furb.common.Point;
import br.furb.common.Polygon;
import br.furb.common.Transform;
import br.furb.packing.NoFitPolygon.TouchingEdgeVertex;
import br.furb.packing.NoFitPolygon.Translation;

public class TouchingEdge {

	private List<TouchingPoints> touchingPointsList;

	public List<TouchingPoints> getTouchingPoins(Polygon stationaryPolygon, Polygon movingPolygon) {

		touchingPointsList = new ArrayList<TouchingPoints>();

		detectTouchingPoints(stationaryPolygon, movingPolygon, true);
		detectTouchingPoints(movingPolygon, stationaryPolygon, false);

		return touchingPointsList;
	}

	private void detectTouchingPoints(Polygon polygon1, Polygon polygon2, boolean fromStationay) {

		for (Point sPoint : polygon1.getPoints()) {
			for (Point mPoint : polygon2.getPoints()) {
				if (sPoint.equals(mPoint)) {
					TouchingPoints touchingPoints = null;
					if (fromStationay) {
						touchingPoints = new TouchingPoints(sPoint, mPoint);
					} else {
						touchingPoints = new TouchingPoints(mPoint, sPoint);
					}
					if (!touchingPointsList.contains(touchingPoints)) {
						touchingPointsList.add(touchingPoints);
					}
					continue;
				}
				if (mPoint.equals(sPoint.next)) {
					continue;
				}
				Line line = new Line(sPoint, sPoint.next);
				if (line.relativeCCW(mPoint.x, mPoint.y) == 0) {
					TouchingPoints touchingPoints = null;
					if (fromStationay) {
						touchingPoints = new TouchingPoints(sPoint, mPoint, fromStationay, true);
					} else {
						touchingPoints = new TouchingPoints(mPoint, sPoint, fromStationay, true);
					}
					if (!touchingPointsList.contains(touchingPoints)) {
						touchingPointsList.add(touchingPoints);
					}
				}
			}
		}
	}

	public void generatePotencialTranslation(List<PotencialTranslation> potencialTranslations, //
			Map<Line, PotencialTranslation> derivedFromMap, Line sLine, Line mLine, TouchingEdgeVertex touchingEdgeVert, boolean isMiddleEdge, boolean fromStationary) {

		TouchingEdgeVertex touchingEdgeVertex = touchingEdgeVert;
		if (touchingEdgeVertex == null) {
			touchingEdgeVertex = sLine.compare(mLine);
		}

		Direction direction = calculateDirection(sLine, mLine, touchingEdgeVertex);
		Translation translation = getTranslationDerivedFrom(touchingEdgeVertex, direction);

		// derivedFrom pode ser nulo
		Line derivedFrom = getDerivedFrom(translation, sLine, mLine);
		PotencialTranslation potencialTranslation = null;

		potencialTranslation = new PotencialTranslation(translation, sLine, mLine, derivedFrom, touchingEdgeVertex, isMiddleEdge, fromStationary);
		potencialTranslations.add(potencialTranslation);
		// }
		if (derivedFrom != null) {// && !derivedFromMap.containsKey(derivedFrom)
			derivedFromMap.put(derivedFrom, potencialTranslation);
		}
	}

	public static Direction calculateDirection(Line stationaryLine, Line movingLine, TouchingEdgeVertex touchingEdgeVertex) {
		double angle = MathHelper.getAngulo(stationaryLine.start, stationaryLine.end);
		Transform transform = new Transform();
		List<Point> rotatedPoints = transform.executeRotation(stationaryLine.start, angle, -1,//
				stationaryLine.getStart(), stationaryLine.getEnd(), movingLine.start, movingLine.end);

		double stationaryY = rotatedPoints.get(0).getY();
		double movingY = 0;

		if (touchingEdgeVertex == TouchingEdgeVertex.END_START || //
				touchingEdgeVertex == TouchingEdgeVertex.START_START) {
			// pega o end
			movingY = rotatedPoints.get(3).getY();
		} else if (touchingEdgeVertex == TouchingEdgeVertex.START_END || //
				touchingEdgeVertex == TouchingEdgeVertex.END_END) {
			// pega o start
			movingY = rotatedPoints.get(2).getY();
		} else {
			throw new IllegalStateException();
		}

		double diff = movingY - stationaryY;
		if (Math.abs(diff) < Transform.THRESHOLD || Math.abs(diff) == 0) {
			return Direction.PARALLEL;
		} else if (movingY < stationaryY) {
			return Direction.RIGHT;
		} else {
			return Direction.LEFT;
		}
	}

	/**
	 * <pre>
	 * Start	Start	Left	=	Orbiting edge
	 * Start	Start	Right	=	Stationary edge
	 * Start	End		Left	=	-
	 * Start	End		Right	=	Stationary edge
	 * End		Start	Left	=	-
	 * End		Start	Right	=	Orbiting edge
	 * End		End		-		=	-
	 * 					Parallel=	Either edge
	 * </pre>
	 * 
	 * @param direction
	 * @param touchingEdgeVertex
	 */
	private Translation getTranslationDerivedFrom(TouchingEdgeVertex touchingEdgeVertex, Direction direction) {

		if (touchingEdgeVertex == TouchingEdgeVertex.END_END) {
			return Translation.NONE;
		}

		switch (direction) {
		case LEFT:
			if (TouchingEdgeVertex.START_START == touchingEdgeVertex) {
				return Translation.ORBITING_EDGE;
			} else if (TouchingEdgeVertex.START_END == touchingEdgeVertex) {
				return Translation.NONE;
			} else if (TouchingEdgeVertex.END_START == touchingEdgeVertex) {
				return Translation.NONE;
			}
			break;
		case RIGHT:
			if (TouchingEdgeVertex.START_START == touchingEdgeVertex) {
				return Translation.STATIONARY_EDGE;
			} else if (TouchingEdgeVertex.START_END == touchingEdgeVertex) {
				return Translation.STATIONARY_EDGE;
			} else if (TouchingEdgeVertex.END_START == touchingEdgeVertex) {
				return Translation.ORBITING_EDGE;
			}
			break;
		case PARALLEL:
			return Translation.EITHER_EDGE;
		}
		return Translation.NONE;
	}

	private Line getDerivedFrom(Translation translation, Line stationary, Line moving) {
		Line derived = null;
		switch (translation) {
		case STATIONARY_EDGE:
			derived = stationary;
			break;
		case EITHER_EDGE:
			derived = stationary;
			break;
		case ORBITING_EDGE:
			derived = moving;
			break;
		case NONE:
			break;
		default:
			throw new IllegalStateException("Inexistente: " + translation);
		}
		return derived;
	}

}
