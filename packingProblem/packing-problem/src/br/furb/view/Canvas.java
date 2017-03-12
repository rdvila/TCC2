package br.furb.view;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import br.furb.view.elements.Element;

/**
 * Classe responsável pelo desenho.
 * 
 * @author Denise Brandt
 */
public class Canvas implements GLEventListener {

	public double HEIGHT = 60.0;
	public double WIDTH = 60.0;

	/**
	 * Objeto GL.
	 */
	private GL gl;

	/**
	 * Objeto GLU.
	 */
	private GLU glu;

	/**
	 * Dimensão do {@link #glDrawable}.
	 */
	private Bound frameBound;

	/**
	 * Dimensão do universo de desenho do {@link #gl}.
	 */
	public Bound universeBound;

	/**
	 * Display que está sendo desenhando.
	 */
	private GLAutoDrawable glDrawable;

	/**
	 * Coleção de elementos.
	 */
	private final List<Element> elements;

	public Canvas() {
		this.universeBound = new Bound(0.0, HEIGHT, 0.0, WIDTH);
		this.elements = new ArrayList<Element>();
	}

	public Canvas(double height, double width) {
		this.universeBound = new Bound(0.0, height, 0.0, width);
		this.elements = new ArrayList<Element>();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		glDrawable = drawable;
		gl = drawable.getGL();
		glu = new GLU();
		glDrawable.setGL(new DebugGL(gl));
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		glu.gluOrtho2D(universeBound.left, universeBound.right, universeBound.bottom, universeBound.top);
		gl.glColor3d(0.0, 0.0, 0.0);

		gl.glEnable(GL.GL_POINT_SMOOTH);
		gl.glEnable(GL.GL_LINE_SMOOTH);

		for (Element element : elements) {
			element.draw(gl);
		}
	}

	@Override
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		Bound oldFrame = frameBound;

		frameBound = new Bound(x, width, height, y);

		double frameWidth = frameBound.right - frameBound.left;
		double universeWidth = universeBound.right - universeBound.left;

		double frameHeight = frameBound.bottom - frameBound.top;
		double universeHeight = universeBound.top - universeBound.bottom;

		double frameDeltaX = frameWidth / universeWidth;
		double frameDeltaY = frameHeight / universeHeight;

		// Verifica se a razão foi aumentada ou diminuida.
		boolean calculteGreater = false;
		if (oldFrame != null) {
			double oldFrameWidth = oldFrame.right - oldFrame.left;
			double oldFrameHeight = oldFrame.bottom - oldFrame.top;

			double differenceX = frameDeltaX - (oldFrameWidth / universeWidth);
			double differenceY = frameDeltaY - (oldFrameHeight / universeHeight);
			if (differenceX != 0) {
				calculteGreater = differenceX > 0;
			} else {
				calculteGreater = differenceY > 0;
			}
		}

		if ((calculteGreater && frameDeltaY > frameDeltaX) || (!calculteGreater && frameDeltaY < frameDeltaX)) {
			double newY = ((frameDeltaY / frameDeltaX) * universeHeight);
			universeBound = new Bound(universeBound.left, universeBound.right, 0, +newY);
		} else if ((calculteGreater && frameDeltaX > frameDeltaY) || (!calculteGreater && frameDeltaX < frameDeltaY)) {
			double newX = ((frameDeltaX / frameDeltaY) * universeWidth);
			universeBound = new Bound(0, +newX, universeBound.bottom, universeBound.top);
		}
	}

	public void addElement(Element element) {
		this.elements.add(element);
	}

	public void display() {
		glDrawable.display();
	}

	public void deleteAll() {
		elements.clear();
		glDrawable.display();
	}

}
