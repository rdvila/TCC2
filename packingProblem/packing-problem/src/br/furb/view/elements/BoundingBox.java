package br.furb.view.elements;

import java.awt.geom.Point2D.Double;
import java.util.List;

import javax.media.opengl.GL;

public class BoundingBox {

    private double maxX;
    private double maxY;
    private double minX;
    private double minY;

    public void update(List<Double> points) {
        maxX = Integer.MIN_VALUE;
        maxY = Integer.MIN_VALUE;
        minX = Integer.MAX_VALUE;
        minY = Integer.MAX_VALUE;
        for (Double point : points) {
            if (point.x > maxX) {
                maxX = point.x;
            }
            if (point.y > maxY) {
                maxY = point.y;
            }
            if (point.x < minX) {
                minX = point.x;
            }
            if (point.y < minY) {
                minY = point.y;
            }
        }
    }

    public boolean isInside(Double point) {
        return (point.x >= minX) && (point.y >= minY) && (point.x <= maxX) && (point.y <= maxY);
    }

    public void drawBoundingBox(GL gl) {
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex2d(minX, minY);
        gl.glVertex2d(minX, maxY);
        gl.glVertex2d(maxX, maxY);
        gl.glVertex2d(maxX, minY);
        gl.glEnd();
    }

}
