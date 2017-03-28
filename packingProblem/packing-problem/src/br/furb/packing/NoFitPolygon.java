package br.furb.packing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.furb.common.MathHelper;
import br.furb.common.Point;
import br.furb.common.Polygon;
import br.furb.common.Transform;
import br.furb.packing.jnfp.NFPCache;

public class NoFitPolygon extends NFPImplementation {

	private static final boolean DEBUG = false;

	private Polygon noFitPolygon;
	private Polygon movingPolygon;
	private Polygon stationaryPolygon;
	private Point lastUsedPoint;

	public Polygon calculateNotFitPolygon(Polygon polygonA, Polygon polygonB) {
		if (DEBUG) {
			System.out.println("stationary");
			System.out.println(polygonA);
			System.out.println("moving");
			System.out.println(polygonB);
		}
		try {
			this.stationaryPolygon = (Polygon) polygonA.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}

		noFitPolygon = new Polygon();

		Transform transform = new Transform();
		movingPolygon = transform.executeTranslation(polygonA.minY(), polygonB.maxY(), polygonB);

		noFitPolygon.addPoint(movingPolygon.maxY());
		lastUsedPoint = movingPolygon.maxY();

		do {
			createCombinations();
		} while (!(polygonA.minYRight().equals(movingPolygon.maxY()) && //
		noFitPolygon.getPoints().size() > movingPolygon.getPoints().size()));

		return noFitPolygon;
	}

	private void createCombinations() {
		TouchingEdge touchingEdge = new TouchingEdge();

		List<TouchingPoints> touchingPoints = touchingEdge.getTouchingPoins(stationaryPolygon, movingPolygon);

		Map<Line, PotencialTranslation> derivedFromMap = new HashMap<Line, PotencialTranslation>();

		List<PotencialTranslation> potencialTranslations = new ArrayList<PotencialTranslation>();

		Line[] stationariesLines = null;
		Line[] movingLines = null;

		for (TouchingPoints touchingPt : touchingPoints) {
			if (touchingPt.isMiddleEdge) {
				stationariesLines = null;
				movingLines = null;
				if (touchingPt.fromStationary) {
					Line stationarieLine = new Line(touchingPt.sPoint, touchingPt.sPoint.next);
					Line movingLine0 = new Line(touchingPt.mPoint.prior, touchingPt.mPoint);
					Line movingLine1 = new Line(touchingPt.mPoint, touchingPt.mPoint.next);
					touchingEdge.generatePotencialTranslation(potencialTranslations, derivedFromMap, stationarieLine, movingLine0, TouchingEdgeVertex.START_END, touchingPt.isMiddleEdge, touchingPt.fromStationary);
					touchingEdge.generatePotencialTranslation(potencialTranslations, derivedFromMap, stationarieLine, movingLine1, TouchingEdgeVertex.START_START, touchingPt.isMiddleEdge, touchingPt.fromStationary);
				} else {
					Line stationarieLine0 = new Line(touchingPt.sPoint.prior, touchingPt.sPoint);
					Line stationarieLine1 = new Line(touchingPt.sPoint, touchingPt.sPoint.next);
					Line movingLine0 = new Line(touchingPt.mPoint, touchingPt.mPoint.next);
					touchingEdge.generatePotencialTranslation(potencialTranslations, derivedFromMap, stationarieLine0, movingLine0, TouchingEdgeVertex.END_START, touchingPt.isMiddleEdge, touchingPt.fromStationary);
					touchingEdge.generatePotencialTranslation(potencialTranslations, derivedFromMap, stationarieLine1, movingLine0, TouchingEdgeVertex.START_START, touchingPt.isMiddleEdge, touchingPt.fromStationary);
				}
			} else {
				stationariesLines = new Line[2];
				stationariesLines[0] = new Line(touchingPt.sPoint.prior, touchingPt.sPoint);
				stationariesLines[1] = new Line(touchingPt.sPoint, touchingPt.sPoint.next);

				movingLines = new Line[2];
				movingLines[0] = new Line(touchingPt.mPoint.prior, touchingPt.mPoint);
				movingLines[1] = new Line(touchingPt.mPoint, touchingPt.mPoint.next);
			}

			if (stationariesLines != null) {
				for (int k = 0; k < stationariesLines.length; k++) {
					for (int l = 0; l < movingLines.length; l++) {
						touchingEdge.generatePotencialTranslation(potencialTranslations, derivedFromMap, stationariesLines[k], movingLines[l], null, false, false);
					}
				}
			}
		}

		PotencialTranslation feasibleTranslation = getFeasibleTranslation(derivedFromMap, potencialTranslations);
		translate(feasibleTranslation.touchingEdge, feasibleTranslation.translation, feasibleTranslation.stationary, feasibleTranslation.moving);
		lastUsedPoint = feasibleTranslation.stationary.start;
	}

	private PotencialTranslation getFeasibleTranslation(Map<Line, PotencialTranslation> derivedFromMap, List<PotencialTranslation> potencialTranslations) {
		List<PotencialTranslation> feasibleTranlations = new ArrayList<PotencialTranslation>();

		outter: for (Line derivedFromLine : derivedFromMap.keySet()) {
			boolean isFeasible = true;
			PotencialTranslation potencialTrans = null;
			for (int i = 0; i < potencialTranslations.size(); i++) {
				potencialTrans = potencialTranslations.get(i);
				PotencialTranslation derivedFomTrans = derivedFromMap.get(derivedFromLine);
				isFeasible &= isFeasibleTranslation(potencialTrans, derivedFromLine, derivedFomTrans);
				if (!isFeasible) {
					continue outter;
				}
			}
			if (isFeasible) {
				feasibleTranlations.add(derivedFromMap.get(derivedFromLine));
			}
		}
		// descobrir qual a melhor translação
		int minDist = Integer.MAX_VALUE;
		PotencialTranslation potencialTransMinDist = null;
		for (PotencialTranslation potencialTranslation : feasibleTranlations) {
			int dist = 0;
			Point currentPoint = potencialTranslation.stationary.start;
			while (!currentPoint.equals(lastUsedPoint)) {
				dist++;
				currentPoint = currentPoint.next;
			}
			if (dist < minDist) {
				minDist = dist;
				potencialTransMinDist = potencialTranslation;
			}
		}
		if (potencialTransMinDist == null) {
			System.out.println("stationary");
			System.out.println(stationaryPolygon);
			System.out.println("moving");
			System.out.println(movingPolygon);
			throw new IllegalStateException();
		}

		return potencialTransMinDist;
	}

	private boolean translate(TouchingEdgeVertex touchingEdgeVertex, Translation translation, Line stationayLine, Line movingLine) {
		Transform transform = new Transform();
		Line translateLine = null;

		switch (translation) {
		case ORBITING_EDGE:
			if (touchingEdgeVertex == TouchingEdgeVertex.START_START) {
				translateLine = new Line(movingLine.end, stationayLine.start);
				if (!treatFeasibleTranslation(movingLine, translateLine)) {
					movingPolygon = transform.executeTranslation(stationayLine.start, movingLine.end, movingPolygon);
				}
			} else {
				translateLine = new Line(movingLine.start.next, movingLine.start);
				if (!treatFeasibleTranslation(movingLine, translateLine)) {
					movingPolygon = transform.executeTranslation(movingLine.start, movingLine.start.next, movingPolygon);
				}
			}

			break;
		case STATIONARY_EDGE:
			if (touchingEdgeVertex == TouchingEdgeVertex.START_START) {
				translateLine = new Line(movingLine.start, stationayLine.end);
				if (!treatFeasibleTranslation(translateLine, stationayLine)) {
					movingPolygon = transform.executeTranslation(stationayLine.end, movingLine.start, movingPolygon);
				}
			} else {
				translateLine = new Line(movingLine.end, stationayLine.end);
				if (!treatFeasibleTranslation(translateLine, stationayLine)) {
					movingPolygon = transform.executeTranslation(stationayLine.end, movingLine.end, movingPolygon);
				}
			}
			break;
		case EITHER_EDGE:
			if (touchingEdgeVertex == TouchingEdgeVertex.END_START) {
				translateLine = new Line(movingLine.end, stationayLine.end);
				if (!treatFeasibleTranslation(translateLine, translateLine)) {
					movingPolygon = transform.executeTranslation(stationayLine.end, movingLine.end, movingPolygon);
				}

			} else if (touchingEdgeVertex == TouchingEdgeVertex.START_END) {
				translateLine = new Line(movingLine.end, stationayLine.start.next);
				if (!treatFeasibleTranslation(translateLine, translateLine)) {
					movingPolygon = transform.executeTranslation(stationayLine.start.next, movingLine.end, movingPolygon);
				}
			} else if (touchingEdgeVertex == TouchingEdgeVertex.START_START) {
				if (stationayLine.start.equals(movingLine.start)) {
					translateLine = new Line(movingLine.end, movingLine.start);
					if (!treatFeasibleTranslation(movingLine, translateLine)) {
						movingPolygon = transform.executeTranslation(movingLine.start, movingLine.end, movingPolygon);// 01/05
					}
				} else {

					translateLine = new Line(movingLine.end, stationayLine.end);
					if (!treatFeasibleTranslation(movingLine, translateLine)) {
						movingPolygon = transform.executeTranslation(stationayLine.end, movingLine.end, movingPolygon);// 29/04 comentado
					}
				}
			}
			break;
		default:
			throw new IllegalStateException("Não conseguiu transladar");
		}
		if (DEBUG) {
			System.out.println(movingPolygon.maxY());
		}
		noFitPolygon.addPoint(movingPolygon.maxY());
		return true;
	}

	private boolean treatFeasibleTranslation(Line translateLine, Line diffLine) {

		FeasibleTranslation fTrans = calculateFeasibleTranslation(diffLine);

		if (fTrans.getIntersectedPoint() == null) {
			return false;
		}

		Transform transform = new Transform();

		Point pointToTranslate = MathHelper.subPoints(fTrans.getTranslationLine().end, fTrans.getTranslationLine().start);

		if (fTrans.isFromStationary()) {
			Point target = MathHelper.subPoints(translateLine.start, pointToTranslate);
			movingPolygon = transform.executeTranslation(target, translateLine.start, movingPolygon);

		} else {
			Point target = MathHelper.sumPoints(translateLine.start, pointToTranslate);
			if (target.equals(translateLine.start)) {
				return false;
			}
			movingPolygon = transform.executeTranslation(target, translateLine.start, movingPolygon);
		}
		return true;
	}

	public static boolean isFeasibleTranslation(PotencialTranslation potencialTrans, Line derivedFromLine, //
			PotencialTranslation derivedFomTrans) {

		Translation translation = derivedFomTrans.translation;
		Point sStart = potencialTrans.stationary.start;
		Point sEnd = potencialTrans.stationary.end;
		Point mStart = potencialTrans.moving.start;
		Point mEnd = potencialTrans.moving.end;
		Point transStart = derivedFromLine.start;
		Point transEnd = derivedFromLine.end;
		TouchingEdgeVertex touchingEdge = potencialTrans.touchingEdge;

		Direction direction = TouchingEdge.calculateDirection(potencialTrans.stationary,
		/**/potencialTrans.moving, touchingEdge);

		double angle = MathHelper.getNormalAngle(sStart, sEnd);
		Transform transform = new Transform();
		List<Point> rotatedPoints = transform.executeRotation(sStart, angle, -1,//
				mStart, mEnd, transStart, transEnd);

		boolean doubleTest = false;

		double minAngle = 0;
		double maxAngle = 0;
		double minAngle2 = 0;
		double maxAngle2 = 0;

		double angleMovingLine = 0;
		double angleTransLine = MathHelper.getNormalAngle(rotatedPoints.get(2), rotatedPoints.get(3));

		if (Math.abs(angleTransLine) <= Transform.THRESHOLD) {// angleTransLine == 0
			return true;
		}

		if (translation == Translation.ORBITING_EDGE) {
			angleTransLine = (angleTransLine + 180) % 360;
		} else if (translation == Translation.EITHER_EDGE && //
				(derivedFomTrans.touchingEdge == TouchingEdgeVertex.END_START || derivedFomTrans.touchingEdge == TouchingEdgeVertex.END_END)) {
			double angle2 = MathHelper.getNormalAngle(sStart, sEnd);
			Transform transform2 = new Transform();
			List<Point> rotatedPoints2 = transform2.executeRotation(derivedFomTrans.stationary.start, angle2, -1,//
					derivedFomTrans.moving.start, derivedFomTrans.moving.end);

			double angleDerivedFromMoving = MathHelper.getNormalAngle(rotatedPoints2.get(0), rotatedPoints2.get(1));
			if (angleDerivedFromMoving < 90 || angleDerivedFromMoving > 270) {
				angleTransLine = (angleTransLine + 180) % 360;
			} else {
				if (derivedFomTrans.touchingEdge == TouchingEdgeVertex.END_START) {
					return false;
				}
			}
		}

		if (touchingEdge == TouchingEdgeVertex.START_START) {
			angleMovingLine = MathHelper.getNormalAngle(rotatedPoints.get(0), rotatedPoints.get(1));
			if (direction == Direction.RIGHT) {
				minAngle = Math.abs(angleMovingLine - 180);
				maxAngle = 360;
			} else {
				minAngle = 0;
				maxAngle = angleMovingLine + 180;
			}
		} else if (touchingEdge == TouchingEdgeVertex.START_END) {
			angleMovingLine = MathHelper.getNormalAngle(rotatedPoints.get(1), rotatedPoints.get(0));
			if (direction == Direction.RIGHT) {
				minAngle = Math.abs(angleMovingLine - 180);
				maxAngle = 360;
			} else {
				minAngle = 0;
				maxAngle = (angleMovingLine + 180) % 360;
			}

		} else if (touchingEdge == TouchingEdgeVertex.END_START) {
			angleMovingLine = MathHelper.getNormalAngle(rotatedPoints.get(0), rotatedPoints.get(1));
			if (direction == Direction.RIGHT) {
				minAngle = 0;
				maxAngle = Math.abs(angleMovingLine - 180);
				minAngle2 = 180;
				maxAngle2 = 360;
			} else {
				minAngle = (angleMovingLine + 180) % 360;
				maxAngle = 360;
				minAngle2 = 0;
				maxAngle2 = 180;
			}
			doubleTest = true;
		} else if (touchingEdge == TouchingEdgeVertex.END_END) {
			angleMovingLine = MathHelper.getNormalAngle(rotatedPoints.get(1), rotatedPoints.get(0));
			if (direction == Direction.RIGHT) {
				minAngle = 0;
				maxAngle = Math.abs(angleMovingLine - 180);
				minAngle2 = 180;
				maxAngle2 = 360;
			} else {
				minAngle = (angleMovingLine + 180) % 360;
				maxAngle = 360;
				minAngle2 = 0;
				maxAngle2 = 180;
			}
			doubleTest = true;
		} else {
			throw new IllegalStateException();
		}

		if (direction == Direction.RIGHT) {

			if (potencialTrans.isMiddleEdge) {
				if (potencialTrans.fromStationary) {
					minAngle = 180;
					maxAngle = 360;
				} else {
					minAngle = 0;
					maxAngle = Math.abs(angleMovingLine - 180);
					minAngle2 = angleMovingLine;
					maxAngle2 = 360;
				}
			}
		} else if (direction == Direction.LEFT) {
			if (potencialTrans.isMiddleEdge) {
				if (potencialTrans.fromStationary) {
					minAngle = 0;
					maxAngle = 180;
				} else {
					minAngle = angleMovingLine;
					maxAngle = (angleMovingLine + 180) % 360;
				}
			}
		}

		if (direction == Direction.PARALLEL) {
			minAngle = 180;
			maxAngle = 360;
		}

		if (doubleTest) {
			return MathHelper.compareDouble(angleTransLine, minAngle, Transform.THRESHOLD) >= 0 && //
					MathHelper.compareDouble(angleTransLine, maxAngle, Transform.THRESHOLD) <= 0 || //
					MathHelper.compareDouble(angleTransLine, minAngle2, Transform.THRESHOLD) >= 0 && //
					MathHelper.compareDouble(angleTransLine, maxAngle2, Transform.THRESHOLD) <= 0;

		}
		return MathHelper.compareDouble(angleTransLine, minAngle, Transform.THRESHOLD) >= 0 && //
				MathHelper.compareDouble(angleTransLine, maxAngle, Transform.THRESHOLD) <= 0;
	}

	private FeasibleTranslation calculateFeasibleTranslation(Line refLine) {
		FeasibleTranslation feasibleTranslation = new FeasibleTranslation();
		feasibleTranslation.calculateFeasibleTranslation(movingPolygon, stationaryPolygon, refLine);
		return feasibleTranslation;
	}

	enum TouchingEdgeVertex {
		START_START, //
		START_END, //
		END_START, //
		END_END
	}

	enum Translation {
		ORBITING_EDGE, //
		STATIONARY_EDGE, //
		EITHER_EDGE, //
		NONE
	}

	@Override
	public NFPImplementation getnewInstance() {
		return new NoFitPolygon();
	}
}
