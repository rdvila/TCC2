package br.furb.packing;

import br.furb.common.MathHelper;
import br.furb.common.Point;
import br.furb.common.Polygon;
import br.furb.common.Transform;
import br.furb.packing.NoFitPolygon.TouchingEdgeVertex;

public class FeasibleTranslation {

	private Line translationLine;

	private boolean fromStationary;

	private IntersectionCalculator intersectionCalculated;

	public boolean isHasEqualsPoints() {
		return intersectionCalculated.isHasEqualsPoints();
	}

	/**
	 * Ponto do qual partiu a linha que interseccionou outra linha
	 * 
	 * @return
	 */
	public Point getIntersectorPoint() {
		return intersectionCalculated.getIntersectorPoint();
	}

	/**
	 * Linha com o qual foi colidido
	 * 
	 * @return
	 */
	public Point getIntersectedPoint() {
		if (intersectionCalculated == null) {
			return null;
		}
		return intersectionCalculated.getIntersectedPoint();
	}

	/**
	 * Segmento de reta retirado da linha usada como referência.<br>
	 * Refere-se a reta que deverá ser percorrida.<br>
	 * É uma nova instância de Line, não pertence aos polígonos.
	 * 
	 * @return
	 */
	public Line getTranslationLine() {
		return translationLine;
	}

	public boolean isFromStationary() {
		return fromStationary;
	}

	/**
	 * Se <code>translationLine</code> estiver nulo quer dizer que não tem intersecção.
	 * 
	 * @param movingPolygon
	 * @param stationaryPolygon
	 * @param refLine
	 */
	public void calculateFeasibleTranslation(Polygon movingPolygon, Polygon stationaryPolygon, Line refLine) {

		/*
		 * Ponto com a diferença entre o ponto inicial e final sobre o qual será feita a translação
		 */
		Point distancePoint = MathHelper.subPoints(refLine.end, refLine.start);
		IntersectionCalculator intersectionFromMoving = new IntersectionCalculator();
		Line lineMovingPolygon = intersectionFromMoving.calculateIntersections(movingPolygon, stationaryPolygon, distancePoint, false, refLine);
		IntersectionCalculator intersectionFromStationary = new IntersectionCalculator();
		Line lineStationaryPolygon = intersectionFromStationary.calculateIntersections(stationaryPolygon, movingPolygon, distancePoint, true, refLine);

		if (lineMovingPolygon != null) {
			if (lineStationaryPolygon != null) {
				double distLineMovingPolygon = MathHelper.calcularDistancia(lineMovingPolygon.start, lineMovingPolygon.end);
				double distLineStationaryPolygon = MathHelper.calcularDistancia(lineStationaryPolygon.start, lineStationaryPolygon.end);
				if (intersectionFromMoving.isHasEqualsPoints() || distLineMovingPolygon <= distLineStationaryPolygon) {
					translationLine = lineMovingPolygon;
					fromStationary = false;
					intersectionCalculated = intersectionFromMoving;
				} else {
					translationLine = lineStationaryPolygon;
					fromStationary = true;
					intersectionCalculated = intersectionFromStationary;
				}
			} else {
				translationLine = lineMovingPolygon;
				fromStationary = false;
				intersectionCalculated = intersectionFromMoving;
			}
		} else if (lineStationaryPolygon != null) {
			translationLine = lineStationaryPolygon;
			fromStationary = true;
			intersectionCalculated = intersectionFromStationary;
		}
	}

}

class IntersectionCalculator {

	private Point intersectorPoint;

	private Point intersectedPoint;

	private boolean hasEqualsPoints;

	public boolean isHasEqualsPoints() {
		return hasEqualsPoints;
	}

	public Point getIntersectorPoint() {
		return intersectorPoint;
	}

	public Point getIntersectedPoint() {
		return intersectedPoint;
	}

	public Line calculateIntersections(Polygon polygonA, Polygon polygonB, Point distancePoint, boolean isFromStationary, Line refLine) {
		// Line2D transLine = new Line2D.Double(refLine.start.x,
		// refLine.start.y, refLine.end.x, refLine.end.y);

		double maxDistance = 0;
		Line maxLine = null;
		Point sumPoint = null;

		for (Point pointA : polygonA.getPoints()) {

			/**/
			if (isFromStationary) {
				sumPoint = MathHelper.subPoints(pointA, distancePoint);
			} else {
				sumPoint = MathHelper.sumPoints(pointA, distancePoint);
			}

			for (Point pointB : polygonB.getPoints()) {

				if (sumPoint.equals(pointB.next)) {
					continue;
				}

				Line line = new Line(pointA, sumPoint);
				if (line.intersectsLine(pointB, pointB.next)) {
					Point intersectionPoint = MathHelper.findIntersection(pointA, sumPoint, pointB, pointB.next);

					Line lineTrans = new Line(refLine.start, refLine.end);
					if (lineTrans.ptSegDist(intersectionPoint.x, intersectionPoint.y) == 0 && //
							lineTrans.ptSegDist(pointA.x, pointA.y) == 0) {
						continue;
					}

					// Ponto com o qual está sendo testado não é considerado
					// colisão.
					if (pointB.equals(intersectionPoint) || //
							(pointB.next.equals(intersectionPoint) && sumPoint.equals(pointB.next))) {//
						continue;
					}

					if (line.relativeCCW(intersectionPoint.x, intersectionPoint.y) != 0) {
						// continue;
						// se o fim da linha encostou na projeção, desconsidera
						// pois não cruzou com a projeção 30/04
						if (intersectionPoint.equals(pointB.next)) {// isFromStationary
																	// &&
							if (new Line(pointA, sumPoint).ptSegDist(intersectionPoint.x, intersectionPoint.y) != 0) {
								// continue;
							}
							if (Direction.RIGHT == TouchingEdge.calculateDirection(new Line(pointB.next, pointB.next.next),
							/**/new Line(intersectionPoint, sumPoint), TouchingEdgeVertex.START_START)) {
								continue;
							}
						}
					}

					// Linhas paralelas não é considerado colisão
					if (lineTrans.relativeCCW(pointA.x, pointA.y) == 0 &&
					/**/lineTrans.relativeCCW(intersectionPoint.x, intersectionPoint.y) == 0) {
						// Linhas paralelas que iniciam no mesmo ponto. A linha
						// da projeção maior que a aresta testada.
						// Então intersecção é no fim da aresta.
						if (pointA.equals(pointA) && line.ptSegDist(pointB.next.x, pointB.next.y) == 0) {
							if (Direction.RIGHT == TouchingEdge.calculateDirection(new Line(pointB.next, pointB.next.next),
							/**/new Line(intersectionPoint, sumPoint), TouchingEdgeVertex.START_START)) {
								continue;
							}
							intersectionPoint = pointB.next;
						} else {
							continue;
						}
					}

					// Linhas paralelas não é considerado colisão.
					// Projeção interseccionou o fim de uma aresta.
					if (refLine.relativeCCW(sumPoint.x, sumPoint.y) == 0 &&
					/**/refLine.relativeCCW(intersectionPoint.x, intersectionPoint.y) == 0 && //
							refLine.start.equals(intersectionPoint)) {
						continue;
					}

					// Linhas paralelas onde o ponto inicial é igual ao ponto
					// final do outro segmento de reta.
					if (pointA.equals(pointB.next)) {
						continue;
					}

					// Se a intersecção não foi no meio do segmento de reta.
					if (pointA.equals(intersectionPoint)) {
						continue;
					}

					// Ocorre quando a aresta do moving é paralela a aresta da
					// translação.
					// E a aresta da intersção econta seu final na projeção.
					if (intersectionPoint.equals(pointB.next) && //
							intersectionPoint.equals(refLine.start) && //
							intersectionPoint.equals(pointA.prior)) {
						continue;
					}

					if (sumPoint.equals(pointB)) {
						if (Double.isNaN(intersectionPoint.x) && sumPoint.equals(pointB)) {
							intersectionPoint = new Point(sumPoint.x, sumPoint.y);
						}
					}

					double distance = MathHelper.calcularDistancia(intersectionPoint, sumPoint);
					if (Double.isInfinite(distance) || Double.isNaN(distance)) {
						continue;
					}
					if (isFromStationary && pointB.next.equals(intersectionPoint) && //
							line.relativeCCW(pointB.next.x, pointB.next.y) == 0) {
						continue;

					}
					if (Math.abs(distance) <= Transform.THRESHOLD) {
						distance = 0;
					}
					// Significa que a aresta está sendo interseccionada no meio
					// e exatamente por um vértice
					boolean hasInterseccion = distance == 0 && intersectorPoint == null && !isFromStationary && //
							intersectionPoint.equals(sumPoint) && //
							!intersectionPoint.equals(pointB.next);

					if (hasInterseccion || //
							MathHelper.compareDouble(distance, maxDistance, Transform.THRESHOLD) > 0) {
						maxDistance = distance;
						maxLine = new Line(pointA, intersectionPoint);
						intersectorPoint = pointA;
						intersectedPoint = pointB;
					}
				}
			}
		}
		return maxLine;
	}

}
