package br.furb.common;

import java.util.List;

public class MathHelper {

	// http://javafree.uol.com.br/topic-876022-Calcular-angulo-imagem.html
	/**
	 * // pra baixo<br>
	 * MathHelper.getAngulo(new Point(10, 10), new Point(10, 0));<br>
	 * // (double) -90.0, -1,5<br>
	 * // pra direita<br>
	 * MathHelper.getAngulo(new Point(10, 10), new Point(20, 10));<br>
	 * // (double) 0.0, 0<br>
	 * // pra cima<br>
	 * MathHelper.getAngulo(new Point(10, 10), new Point(10, 20));<br>
	 * // (double) 90.0, 1,5<br>
	 * // pra esquerda<br>
	 * MathHelper.getAngulo(new Point(10, 10), new Point(0, 10));<br>
	 * // (double) 180.0, 3,14<br>
	 */
	public static double getAngulo(Point a, Point b) {
		double dx = b.x - a.x;
		double dy = b.y - a.y;
		return Math.toDegrees(Math.atan2(dy, dx)); // 4 quadrantes, -pi até pi
	}

	/**
	 * Da direita para a esquerda aumenta o ângulo
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getNormalAngle(Point a, Point b) {
		double angle = getAngulo(a, b);

		if (angle >= 0) {
			return angle;
		}
		return 180 + 180 + angle;
	}

	public static double calcularDistancia(Point ponto1, Point ponto2) {
		return Math.sqrt(Math.pow(ponto1.x - ponto2.x, 2)
				+ (Math.pow(ponto1.y - ponto2.y, 2)));
	}

	public static Point subPoints(Point ponto1, Point ponto2) {
		return new Point(ponto1.x - ponto2.x, ponto1.y - ponto2.y);
	}

	public static Point sumPoints(Point ponto1, Point ponto2) {
		return new Point(ponto1.x + ponto2.x, ponto1.y + ponto2.y);
	}

	public static int compareDouble(double double1, double double2,
			double threshold) {
		double diff = double1 - double2;
		if (Math.abs(diff) <= threshold) {
			return 0;
		}
		if (double1 < double2) {
			return -1;
		}
		return 1;
	}

	public static boolean isInside(Point point, Polygon polygon) {
		int crossing = 0;
		int nextPoint;
		List<Point> points = polygon.getPoints();
		for (int i = 0; i < points.size(); i++) {

			if (i == points.size() - 1)
				nextPoint = 0;
			else
				nextPoint = i + 1;

			if (((points.get(i).compareY(point) <= 0) && (points.get(nextPoint)
					.compareY(point) > 0))
					||
					/**/((points.get(i).compareY(point) > 0) && (points.get(
							nextPoint).compareY(point) <= 0))) {
				double vt = (point.y - points.get(i).y)
						/ (points.get(nextPoint).y - points.get(i).y);
				if (compareDouble(point.x,/**/
				points.get(i).x + vt
						* (points.get(nextPoint).x - points.get(i).x),
						Transform.THRESHOLD) < 0) {
					crossing++;
				}
			}
		}

		if (crossing != 0 && crossing % 2 != 0) {
			return true;
		}
		return false;

	}

	// calculates intersection and checks for parallel lines.
	// also checks that the intersection point is actually on
	// the line segment p1-p2
	// http://workshop.evolutionzone.com/2007/09/10/code-2d-line-intersection/
	public static Point findIntersection(Point p1, Point p2, Point p3, Point p4) {
		double xD1, yD1, xD2, yD2, xD3, yD3;
		double ua, div;

		// calculate differences
		xD1 = p2.x - p1.x;
		xD2 = p4.x - p3.x;
		yD1 = p2.y - p1.y;
		yD2 = p4.y - p3.y;
		xD3 = p1.x - p3.x;
		yD3 = p1.y - p3.y;

		// find intersection Pt between two lines
		Point pt = new Point(0, 0);
		div = yD2 * xD1 - xD2 * yD1;
		if (Math.abs(div) < Transform.THRESHOLD2) {
			return new Point(Double.NaN, Double.NaN);
		}
		ua = (xD2 * yD3 - yD2 * xD3) / div;
		pt.x = p1.x + ua * xD1;
		pt.y = p1.y + ua * yD1;

		return pt;
	}

	public static Point intersect(Point a, Point b, Point c, Point d) {
		Point k = a;
		Point l = b;
		Point m = c;
		Point n = d;

		double det = (n.x - m.x) * (l.y - k.y) - (n.y - m.y) * (l.x - k.x);
		if (det == 0.0) {
			return null;
		}

		double s = ((n.x - m.x) * (m.y - k.y) - (n.y - m.y) * (m.x - k.x))
				/ det;
		double t = ((l.x - k.x) * (m.y - k.y) - (l.y - k.y) * (m.x - k.x))
				/ det;
		if (s < 0 || s > 1 || t < 0 || t > 1) {
			return null;
		}
		Point point = new Point();
		point.x = (int) (k.x + (l.x - k.x) * s);
		point.y = (int) (k.y + (l.y - k.y) * s);
		return point;
	}
}
