package br.furb.view.elements;

import java.awt.Color;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

public class ViewPolygon extends Element {

	public ViewPolygon(Double initialPoint, Color color) {
		super(color);
		setGLTool(GL.GL_LINE_STRIP);

		ArrayList<Double> points = new ArrayList<Double>();
		points.add(initialPoint);
		points.add(initialPoint);
		setPoints(points);
	}

	@Override
	public void resize(Double point) {
		List<Double> points = new ArrayList<Double>(getPoints());

		// Verifica se o último ponto é perto do ponto final. Se for, junta os dois.
		Double firstPoint = points.get(0);
		double distance = Math.sqrt(Math.pow(point.x - firstPoint.x, 2) + Math.pow(point.y - firstPoint.y, 2));
		if (distance < 1) {
			point = firstPoint;
		}
		points.set(points.size() - 1, point);
		setPoints(points);
	}
}
