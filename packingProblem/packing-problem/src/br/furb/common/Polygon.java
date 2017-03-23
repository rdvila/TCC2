package br.furb.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Polygon implements Cloneable {

	private final List<Point> points;
	
	private int r=0;
	private int g=0;
	private int b=0;

	private Point prior;

	private Point first;

	private Point minY;

	private Point minYRight;

	private Point maxY;

	private Point minX;

	private Point maxX;

	private int id;

	public Polygon(int id) {
		this();
		this.id = id;
	}

	public Polygon() {
		points = new ArrayList<Point>();
		minY = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
		minYRight = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
		maxY = new Point(Integer.MAX_VALUE, Integer.MIN_VALUE);
		minX = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
		maxX = new Point(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public int getId() {
		return id;
	}

	public Point getRefPoint() {
		return maxY;
	}

	public Point minY() {
		return minY;
	}

	/**
	 * Quando é paralelo, usa o menor x.
	 * 
	 * @return
	 */
	public Point maxY() {
		return maxY;
	}

	public Point minX() {
		return minX;
	}

	public Point maxX() {
		return maxX;
	}

	public Point minYRight() {
		return minYRight;
	}

	public double getHeight() {
		return maxX.x - minX.x;
	}

	public double getWidth() {
		return maxY.y - minY.y;
	}

	public List<Point> getPoints() {
		return points;
	}

	public Point getFirst() {
		return first;
	}

	public void addPoint(double x, double y) {
		
		r = Double.valueOf((r + x)).intValue() % 256;
		g = Double.valueOf((g + y)).intValue() % 256;
		b = Double.valueOf((b + x + y)).intValue() % 256;
		
		Point point = new Point(x, y);

		if (first == null) {
			first = point;
		}
		point.setNext(first);
		first.setPrior(point);

		if (points.contains(point)) {
			point = points.get(0);
		} else {
			points.add(point);
		}

		point.setPrior(prior);
		if (prior != null) {
			prior.setNext(point);
		}

		prior = point;

		int compareMinY = MathHelper.compareDouble(point.y, minY.y,
				Transform.THRESHOLD);
		if (compareMinY < 0) {
			minY = point;
		} else if (compareMinY == 0) {
			int compareMinX = MathHelper.compareDouble(point.x, minY.x,
					Transform.THRESHOLD);
			if (compareMinX < 0) {
				minY = point;
			}
		}

		int compareMinYRight = MathHelper.compareDouble(point.y, minYRight.y,
				Transform.THRESHOLD);
		if (compareMinYRight < 0) {
			minYRight = point;
		} else if (compareMinYRight == 0) {
			int compareMinYRightX = MathHelper.compareDouble(point.x,
					minYRight.x, Transform.THRESHOLD);
			if (compareMinYRightX > 0) {
				minYRight = point;
			}
		}

		int compareMaxY = MathHelper.compareDouble(point.y, maxY.y,
				Transform.THRESHOLD);
		if (compareMaxY > 0) {
			maxY = point;
		} else if (compareMaxY == 0) {
			int compareX = MathHelper.compareDouble(point.x, maxY.x,
					Transform.THRESHOLD);
			if (compareX < 0) {
				maxY = point;
			}
		}

		int compareMinX = MathHelper.compareDouble(point.x, minX.x,
				Transform.THRESHOLD);
		if (compareMinX < 0) {
			minX = point;
		} else if (compareMinX == 0) {
			int compareMinXY = MathHelper.compareDouble(point.y, minX.y,
					Transform.THRESHOLD);
			if (compareMinXY < 0) {
				minX = point;
			}
		}
		int compareMaxX = MathHelper.compareDouble(point.x, maxX.x,
				Transform.THRESHOLD);
		if (compareMaxX > 0) {
			maxX = point;
		}
	}

	public void addPoint(Point point) {
		addPoint(point.x, point.y);
	}

	public Point getPoint(Point point) {
		for (Point p : points) {
			if (p.equals(point)) {
				return p;
			}
		}
		throw new IllegalArgumentException("Ponto não encontrado: " + point);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ID: " + id).append("\n");
		for (Point point : points) {
			sb.append(point).append("\n");
		}
		return sb.toString();
	}

	public static class HeightComparator implements Comparator<Polygon> {

		@Override
		public int compare(Polygon o1, Polygon o2) {
			int compareHeight = Double.compare(o2.getHeight(), o1.getHeight());
			return compareHeight;
		}
	}

	public static class WidthComparator implements Comparator<Polygon> {

		@Override
		public int compare(Polygon o1, Polygon o2) {
			return Double.compare(o2.getWidth(), o1.getWidth());
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Polygon polygon = new Polygon(id);

		for (Point point : points) {
			polygon.addPoint(point);
		}

		assert points.size() == polygon.getPoints().size();

		return polygon;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Polygon other = (Polygon) obj;
		if (id != other.id)
			return false;
		if (points == null) {
			if (other.points != null)
				return false;
		} else if (!points.equals(other.points))
			return false;
		return true;
	}
	
	public Polygon normalize() {
		Polygon p = new Polygon(getId());
		double minx = minX.x * -1.0;
		double miny = minY.y * -1.0;
		for (Point po : getPoints()) {
			p.addPoint(po.getX()+minx, po.getY()+miny);
		}
		
		if (MathHelper.compareDouble(getWidth(), p.getWidth(), 0.1) != 0 || MathHelper.compareDouble(getHeight(), p.getHeight(), 0.1) != 0) {
			throw new RuntimeException("Error: polygon with diferent bound box");
		}
		
		return p;
	}
	
	public String color() {
		return String.format("#%02X%02X%02X", r,g,b);
	}

}
