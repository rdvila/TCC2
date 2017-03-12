package br.furb.view.elements;

import java.awt.Color;
import java.awt.geom.Point2D.Double;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL;

/**
 * Representa um elemento que é desenhado.
 * 
 */
public abstract class Element {

	/**
	 * Os pontos que foram esse elemento.
	 */
	private List<Double> points;

	/**
	 * A cor desse elemento.
	 */
	private Color color;

	/**
	 * Ferramente a ser utilizada pelo elemento
	 */
	private int glTool;

	public Element(Color color) {
		assert color != null;
		this.points = Collections.emptyList();
		this.color = color;
		this.glTool = GL.GL_LINE_LOOP;
	}

	/**
	 * Desenha o elemento utilizando o <code>gl</code>.
	 * 
	 * @param gl
	 */
	public void draw(GL gl) {
		gl.glPointSize(1);
		gl.glLineWidth(1);

		gl.glColor3d((double) color.getRed() / 255,
				(double) color.getGreen() / 255, (double) color.getBlue() / 255);
		gl.glBegin(glTool);
		for (Double point : points) {
			gl.glVertex2d(point.x, point.y);
		}

		gl.glEnd();
	}

	/**
	 * Recalcula o elemento de acordo com a mudança do ponto selecionado para
	 * <code>point</code>.
	 * 
	 * @param point
	 *            o novo ponto
	 */
	public abstract void resize(Double point);

	/**
	 * Altera a lista de pontos desse elemento.
	 * 
	 * @param points
	 */
	public void setPoints(List<Double> points) {
		this.points = points;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public List<Double> getPoints() {
		return points;
	}

	public void setGLTool(int glTool) {
		this.glTool = glTool;
	}

	public Double getPoint(int index) {
		return points.get(index);
	}

}
