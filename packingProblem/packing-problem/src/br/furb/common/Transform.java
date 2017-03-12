package br.furb.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável pelas transformações geométricas
 */

public final class Transform {

	public static final double THRESHOLD2 = 0.0000000001;

	public static final double THRESHOLD = 0.0000001;

	public Point refPoint;

	/**
	 * Matriz identidade da transformação
	 */
	private final double[] matrix = {
	/**/1, 0, 0, 0,
	/**/0, 1, 0, 0,
	/**/0, 0, 1, 0,
	/**/0, 0, 0, 1 };

	public Transform() {
	}

	/**
	 * Aplica a(s) transformação(s) realizadas na matriz ao ponto.
	 * 
	 * @param point
	 *            o ponto a ser transformado.
	 * @return o ponto transformado
	 */
	public Point transformPoint(Point point) {
		Point pointResult = new Point(matrix[0] * point.x + matrix[4] * point.y
				+ matrix[8] * 0 + matrix[12] * 1, matrix[1] * point.x
				+ matrix[5] * point.y + matrix[9] * 0 + matrix[13] * 1);

		// Caso haja Necessidade de Trabalhar com 3D
		// matrix[2]*point.getX() + matrix[6]*point.getY() + matrix[10]* 0 +
		// matrix[14]*1,
		// matrix[3]*point.getX() + matrix[7]*point.getY() + matrix[11]* 0 +
		// matrix[15]*1);
		return pointResult;
	}

	/**
	 * Efetua a translação do ponto
	 * 
	 * @param point
	 *            o ponto a ser transladado
	 */
	public void translate(Point point) {

		this.setElement(12, point.x);
		this.setElement(13, point.y);
		this.setElement(10, 0);
		this.setElement(15, 1);

	}

	/**
	 * 
	 * Aplica um fator de escala a matriz.
	 * 
	 * @param scaleFactor
	 *            um fator de escala <br/>
	 *            scaleFactor < 1 Reduz escala <br/>
	 *            scaleFactor > 1 Aumenta escala.
	 */
	public void scale(double scaleFactor) {

		this.setElement(0, scaleFactor);
		this.setElement(5, scaleFactor);
		this.setElement(10, 0);
		this.setElement(15, 1);

	}

	/**
	 * Atualiza a matriz para efetuar a rotação
	 * 
	 * @param direction
	 *            a direção para o qual quer efetuar a rotação <br/>
	 *            <b>-1</b> Rotaciona para Direita <br/>
	 *            <b> 1</b> Rotaciona para Esquerda
	 */
	public void rotate(double direction, double angle) {
		this.setElement(0, Math.cos(Math.toRadians(angle)));
		this.setElement(1, Math.sin(Math.toRadians(angle)) * direction);
		this.setElement(4, -(Math.sin(Math.toRadians(angle))) * direction);
		this.setElement(5, Math.cos(Math.toRadians(angle)));
		this.setElement(10, 0);
		this.setElement(15, 1);

	}

	/**
	 * Faz a multipliacação da matriz [x, y, 1] com as coordenadas homoegêneas e
	 * efetua a conversão para as transformações necessárias
	 * 
	 * @param t
	 *            matriz de transformação
	 * @return a matriz transformada
	 */
	public Transform transformMatrix(Transform t) {
		Transform result = new Transform();
		for (int i = 0; i < 16; ++i)
			/**/result.matrix[i] = matrix[i % 4] * t.matrix[i / 4 * 4]
					+ matrix[(i % 4) + 4] * t.matrix[i / 4 * 4 + 1] +
					/**/matrix[(i % 4) + 8] * t.matrix[i / 4 * 4 + 2]
					+ matrix[(i % 4) + 12] * t.matrix[i / 4 * 4 + 3];
		return result;
	}

	/**
	 * Seta o ponto na matriz, para a posição cuja transformação deseja utilizar
	 * 
	 * @see br.furb.common.cg.Transform
	 * 
	 */
	public void setElement(int index, double value) {
		matrix[index] = value;
	}

	/**
	 * Faz a translação da matriz para a origem, utilizado para as operações de
	 * Rotação e Escala
	 * 
	 * @param point
	 *            o ponto a ser transladado.
	 */
	public void translateInverse(Point point) {
		this.setElement(12, -point.x);
		this.setElement(13, -point.y);
		this.setElement(10, 0);
		this.setElement(15, 1.0);

	}

	public List<Point> executeRotation(Point pointRef, double angle,
			int rotationDirection, Point... points) {
		Transform transform = new Transform();
		List<Point> newPoints = new ArrayList<Point>();

		for (Point p : points) {

			Transform translateInverse = new Transform();
			Transform translateBack = new Transform();
			Transform rotate = new Transform();

			transform.translateInverse(pointRef);
			translateInverse = translateInverse.transformMatrix(transform);
			p = translateInverse.transformPoint(p);

			rotate.rotate(rotationDirection, angle / 2);
			rotate = rotate.transformMatrix(rotate);
			p = rotate.transformPoint(p);

			transform.translate(pointRef);
			translateBack = translateBack.transformMatrix(transform);
			p = translateBack.transformPoint(p);

			newPoints.add(p);
		}

		return newPoints;
	}

	public Polygon executeRotation(Point pointRef, double angle,
			int rotationDirection, Polygon polygon) {
		Transform transform = new Transform();
		Polygon newPolygon = new Polygon(PolygonGenerator.getId());

		for (Point p : polygon.getPoints()) {

			Transform translateInverse = new Transform();
			Transform translateBack = new Transform();
			Transform rotate = new Transform();

			transform.translateInverse(pointRef);
			translateInverse = translateInverse.transformMatrix(transform);
			Point newPoint = translateInverse.transformPoint(p);

			rotate.rotate(rotationDirection, angle / 2);
			rotate = rotate.transformMatrix(rotate);
			newPoint = rotate.transformPoint(newPoint);

			transform.translate(pointRef);
			translateBack = translateBack.transformMatrix(transform);
			newPoint = translateBack.transformPoint(newPoint);

			newPolygon.addPoint(newPoint);
		}

		assert newPolygon.getPoints().size() == polygon.getPoints().size();

		return newPolygon;
	}

	public Polygon executeTranslation(Point target, Point pointRef,
			Polygon polygon) {
		// Cria uma lista para armazenar as novas coordenadas dos pontos.
		Polygon newPolygon = new Polygon(polygon.getId());
		Transform transform = new Transform();

		// Pega as coordenadas do ponto 0 do elemento e descobre qual o
		// deslocamento nos eixos.
		Point desloc = new Point();
		desloc.x = target.x - pointRef.x;
		desloc.y = target.y - pointRef.y;
		for (Point p : polygon.getPoints()) {

			// Cria uma matriz para executar a translação
			Transform translate = new Transform();
			// Atribui o valor do deslocamento a matriz de translação
			transform.translate(desloc);
			// Efetua o calculo da translação do ponto
			translate = translate.transformMatrix(transform);
			// Aplica o deslocamento em cada ponto.
			Point newPoint = translate.transformPoint(p);
			newPolygon.addPoint(newPoint);

			if (newPoint.equals(target)) {
				refPoint = newPolygon.getPoints().get(
						newPolygon.getPoints().size() - 1);
			}
		}
		if (refPoint == null) {
			refPoint = new Point(target.x, target.y);
			executeTranslation(target, pointRef, pointRef.next);
			refPoint.next = newPolygon.getPoint(pointRef.next);
			executeTranslation(target, pointRef, pointRef.prior);
			refPoint.prior = newPolygon.getPoint(pointRef.prior);
			// throw new RuntimeException("Target não encontrada");
		}

		assert newPolygon.getPoints().size() == polygon.getPoints().size();

		return newPolygon;
	}

	public void executeTranslationPolygon(Point target, Point pointRef,
			Polygon polygon) {
		int countPoints = polygon.getPoints().size();

		// Cria uma lista para armazenar as novas coordenadas dos pontos.
		Transform transform = new Transform();

		// Pega as coordenadas do ponto 0 do elemento e descobre qual o
		// deslocamento nos eixos.
		Point desloc = new Point();
		desloc.x = target.x - pointRef.x;
		desloc.y = target.y - pointRef.y;
		for (Point p : polygon.getPoints()) {

			// Cria uma matriz para executar a translação
			Transform translate = new Transform();
			// Atribui o valor do deslocamento a matriz de translação
			transform.translate(desloc);
			// Efetua o calculo da translação do ponto
			translate = translate.transformMatrix(transform);
			// Aplica o deslocamento em cada ponto.
			Point newPoint = translate.transformPoint(p);
			p.x = newPoint.x;
			p.y = newPoint.y;

			if (newPoint.equals(target)) {
				refPoint = polygon.getPoints().get(
						polygon.getPoints().size() - 1);
			}
		}
		if (refPoint == null) {
			throw new RuntimeException("Target não encontrada");
		}
		assert countPoints == polygon.getPoints().size();
	}

	public void executeTranslation(Point target, Point pointRef, Point point) {

		// Cria uma lista para armazenar as novas coordenadas dos pontos.
		Transform transform = new Transform();

		// Pega as coordenadas do ponto 0 do elemento e descobre qual o
		// deslocamento nos eixos.
		Point desloc = new Point();
		desloc.x = target.x - pointRef.x;
		desloc.y = target.y - pointRef.y;

		// Cria uma matriz para executar a translação
		Transform translate = new Transform();
		// Atribui o valor do deslocamento a matriz de translação
		transform.translate(desloc);
		// Efetua o calculo da translação do ponto
		translate = translate.transformMatrix(transform);
		// Aplica o deslocamento em cada ponto.
		Point newPoint = translate.transformPoint(point);
		point.x = newPoint.x;
		point.y = newPoint.y;

	}

}
