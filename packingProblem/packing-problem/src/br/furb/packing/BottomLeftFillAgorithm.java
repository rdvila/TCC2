package br.furb.packing;

import java.awt.geom.Line2D;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.map.MultiKeyMap;

import br.furb.common.MathHelper;
import br.furb.common.Point;
import br.furb.common.Polygon;
import br.furb.common.Transform;

public class BottomLeftFillAgorithm {

	private final MultiKeyMap noFitPolygonMap = new MultiKeyMap();

	private Map<Integer, Polygon> polygonMap;

	public double resolution;

	private double currentXPosition;

	private double sheetHeight;

	private Map<Integer, Polygon[]> rotadedPolygonMap;

	private NFPImplementation noFitPolygon;
	
	private static boolean USE_CACHE = true;
	
	public BottomLeftFillAgorithm(NFPImplementation noFitPolygon) {
		this.noFitPolygon = noFitPolygon.getnewInstance(); //TODO rever isso
	}
	
	public static ConcurrentHashMap<String, PackingResult> cache = new ConcurrentHashMap<>();	
	public PackingResult doPacking(Polygon[] polygonsList, int rotationsNumber,
			double sheetHeight) {
		
		String key = "";
		if (USE_CACHE) {
			key = tokey(polygonsList, rotationsNumber, sheetHeight);
			if (isOnCache(key)) {
				return getValueFromCache(key);
			}
		}
		
		resolution = sheetHeight * 0.01;

		this.sheetHeight = sheetHeight;

		int sheetShapeIndex = 0;
		int bestorientation = 0;
		double maxHeight = 0;

		Map<Integer, Polygon[]> rotadedPolygonMapCopy;

		if (rotadedPolygonMap == null) {
			generatePolygons(polygonsList, rotationsNumber);
			loadPolygonMap(rotadedPolygonMap);
		}
		rotadedPolygonMapCopy = cloneMap(rotadedPolygonMap);

		Polygon[] sheetShapes = new Polygon[polygonsList.length];

		for (int i = 0; i < polygonsList.length; i++) {

			Polygon[] polygonsRotaded = rotadedPolygonMapCopy
					.get(polygonsList[i].getId());
			assert polygonsRotaded != null : polygonsList[i].getId();

			// try all rotations configured
			for (int j = 0; j < polygonsRotaded.length; j++) {

				currentXPosition = 0;

				// place shape[i][j] at bottom left of sheet;
				placeShape(polygonsRotaded[j], currentXPosition, 0);

				boolean overlapped = false;
				do {
					overlapped = comparePolygons(polygonsRotaded[j],
							sheetShapes, sheetShapeIndex);
				} while (overlapped);

				// if (shape i in orientation j is least costly orientation seen
				// so far) {bestorientation = j; // record best orientation seen
				// so far } }
				if (compareBestOrientation(polygonsRotaded[bestorientation],
						polygonsRotaded[j])) {
					bestorientation = j;
				}
			}

			// assign shape i in best orientation to sheet
			sheetShapes[sheetShapeIndex] = polygonsRotaded[bestorientation];
			if (sheetShapes[sheetShapeIndex].maxX().x > maxHeight) {
				maxHeight = sheetShapes[sheetShapeIndex].maxX().x;
			}
			sheetShapeIndex++;
		}
		
		PackingResult result = new PackingResult(sheetShapes, maxHeight);
		if (USE_CACHE) {
			addTocache(key, result);
		}
		return result;
		
		// Return Evaluation (total length of packing);
	}

	private void addTocache(String key, PackingResult result) {
		cache.put(key, result);		
		if (cache.size() > 200000) {
			dumpCache(cache);
			cache = new ConcurrentHashMap<>();
		}		
	}

	private void dumpCache(ConcurrentHashMap<String, PackingResult> cache) {		
		try{
			 UUID uuid = UUID.randomUUID();
		     String randomUUIDString = uuid.toString();
		    PrintWriter writer = new PrintWriter(PackingResult.dataset + "-" + randomUUIDString +".txt", "UTF-8");
		    for (String key : cache.keySet()) {
		    	PackingResult result = cache.get(key);
		    	writer.println(key + ',' + result.getHeight() + ',' + result.count);
		    }
		    writer.close();
		} catch (IOException e) {
		   e.printStackTrace();
		}
	}

	private PackingResult getValueFromCache(String key) {
		PackingResult value = cache.get(key);
		value.count += 1;
		return value;
	}

	private boolean isOnCache(String key) {
		return cache.containsKey(key);
	}

	private String tokey(Polygon[] polygonsList, int rotationsNumber, double sheetHeight) {
		StringBuilder builder = new StringBuilder();
		for (Polygon p: polygonsList) {
			builder.append(p.getId());
			builder.append("-");
		}
		builder.append(rotationsNumber);
		builder.append("-");
		builder.append(sheetHeight);
		return builder.toString();
	}

	private boolean compareBestOrientation(Polygon currentBestpolygon,
			Polygon newPolygon) {
		if (newPolygon.maxX().x < currentBestpolygon.maxX().x) {
			return true;
		} else if (newPolygon.maxX().x == currentBestpolygon.maxX().x) {
			if (newPolygon.maxY().y < currentBestpolygon.maxY().y) {
				return true;
			}
		}
		return false;
	}

	private boolean comparePolygons(Polygon polygon, Polygon[] sheetShapes,
			int sheetShapeIndex) {
		boolean resolvedOverlapping = false;

		// while (Overlap(shapes[i][j], sheetshape[1..q])) // find feasible
		// position
		for (int j = 0; j < sheetShapeIndex; j++) {

			if (overlap(polygon, sheetShapes[j])) {
				// Resolve Overlapping
				resolvedOverlapping |= resolveOverlapping(polygon,
						sheetShapes[j]);
				if (overlap(polygon, sheetShapes[j])) {
					assert resolveOverlapping(polygon, sheetShapes[j]) == false : "Overlapping error: sheetShape: "
							+ sheetShapes[j] + "polygon: " + polygon;
				}
			}
			// if (shape[i][j] off top of sheet) { x = x + resolution; place
			// shape[i][j] at (x,0); }
			if (polygon.maxY().getY() > sheetHeight) {
				currentXPosition += resolution;
				// Restart comparisons
				j = -1;
				// place shape[i][j] at bottom left of sheet plus
				// currentXPosition;
				placeShape(polygon, currentXPosition, 0);

			}
		}
		return resolvedOverlapping;
	}

	private boolean resolveOverlapping(Polygon polygon, Polygon sheetShape) {
		Polygon noFitPolygonCached = getNoFitPolygon(polygon, sheetShape);

		double refX = polygon.getRefPoint().x;
		double refY = polygon.getRefPoint().y;

		boolean overlapping = false;
		double maxDistance = 0;

		List<Point> points = noFitPolygonCached.getPoints();
		for (Point point : points) {

			Line2D line2d = new Line2D.Double(point.x, point.y, point.next.x,
					point.next.y);
			if (line2d.intersectsLine(refX, refY, refX, Double.MAX_VALUE)) {

				// intersecting lines segment intersection point
				Point intesectionPoint = MathHelper.findIntersection(
				/**/polygon.getRefPoint(), new Point(refX, Float.MAX_VALUE),
				/**/new Point(line2d.getX1(), line2d.getY1()), new Point(
						line2d.getX2(), line2d.getY2()));

				double distance = MathHelper.calcularDistancia(
						polygon.getRefPoint(), intesectionPoint);
				distance = Double.isNaN(distance) ? 0 : distance;
				// System.out.println(distance);

				// if (distance > Transform.THRESHOLD) {
				// placeShape(polygon, refX, refY + distance);
				// overlapping = true;
				// break;
				// }

				if (distance > maxDistance) {
					maxDistance = distance;
				}

			}
		}
		if (maxDistance > Transform.THRESHOLD) {
			placeShape(polygon, refX, refY + maxDistance);
			overlapping = true;
			// break;
		}
		return overlapping;
	}

	private boolean overlap(Polygon polygon, Polygon sheetShape) {
		Polygon noFitPolygonCached = getNoFitPolygon(polygon, sheetShape);
		return MathHelper.isInside(polygon.getRefPoint(), noFitPolygonCached);
	}

	private void placeShape(Polygon polygon, double x, double y) {
		Transform transform = new Transform();
		if (y == 0) {
			transform.executeTranslationPolygon(new Point(polygon.minY().x
					- polygon.minX().x + x, y), polygon.minY(), polygon);
		} else {
			transform.executeTranslationPolygon(new Point(x, y),
					polygon.getRefPoint(), polygon);
		}
	}

	private Polygon getNoFitPolygon(Polygon polygon, Polygon sheetShape) {
		Polygon noFitPolygonCached = (Polygon) noFitPolygonMap.get(
				polygon.getId(), sheetShape.getId());
		if (noFitPolygonCached == null) {
			Polygon polygonCopy = polygonMap.get(polygon.getId());
			Polygon sheetShapeClone = polygonMap.get(sheetShape.getId());
			
			if (polygonCopy == null || sheetShapeClone == null) {
				throw new RuntimeException("=(");
			}

			noFitPolygonCached = noFitPolygon.calculateNotFitPolygon(
					sheetShapeClone, polygonCopy);
			noFitPolygonMap.put(polygon.getId(), sheetShape.getId(),
					noFitPolygonCached);
		}
		Polygon noFitPolygonCachedCopy;
		try {
			noFitPolygonCachedCopy = (Polygon) noFitPolygonCached.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		moveShape(noFitPolygonCachedCopy, sheetShape.minYRight());
		return noFitPolygonCachedCopy;
	}

	private void moveShape(Polygon polygon, Point target) {
		Transform transform = new Transform();
		transform.executeTranslationPolygon(target, polygon.minYRight(),
				polygon);
	}

	private void moveShape(Polygon polygon, double x, double y) {
		moveShape(polygon, new Point(x, y));
	}

	private void loadPolygonMap(Map<Integer, Polygon[]> rotadedPolygonMap2) {
		polygonMap = new HashMap<Integer, Polygon>();
		for (Integer key : rotadedPolygonMap2.keySet()) {

			for (int j = 0; j < rotadedPolygonMap2.get(key).length; j++) {

				Polygon polygon = rotadedPolygonMap2.get(key)[j];
				if (polygonMap.containsKey(polygon.getId())) {
					continue;
				}
				Polygon polygonClone;
				try {
					polygonClone = (Polygon) polygon.clone();
				} catch (CloneNotSupportedException e) {
					throw new RuntimeException(e);
				}
				moveShape(polygonClone, 0, 0);
				// System.out.println("Clone");
				// System.out.println(polygonClone);
				polygonMap.put(polygon.getId(), polygonClone);
			}
		}
	}

	/**
	 * Gera os polígonos rotacionados
	 * 
	 * @param polygonsList
	 * @param rotationsNumber
	 * @return
	 */
	private void generatePolygons(Polygon[] polygonsList, int rotationsNumber) {
		if (rotationsNumber < 1) {
			throw new IllegalArgumentException(
					"rotationsNumber deve ser maior que 0: " + rotationsNumber);
		}

		// Polygon[][] polygonsVec = new
		// Polygon[polygonsList.length][rotationsNumber];
		rotadedPolygonMap = new HashMap<Integer, Polygon[]>();

		double angle = 360 / rotationsNumber;

		for (int i = 0; i < polygonsList.length; i++) {

			Polygon[] polygonsVec2 = new Polygon[rotationsNumber];
			try {
				// polygonsVec[i][0] = (Polygon) polygonsList[i].clone();
				polygonsVec2[0] = (Polygon) polygonsList[i].clone();
				rotadedPolygonMap.put(polygonsList[i].getId(), polygonsVec2);
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
			for (int j = 1; j < rotationsNumber; j++) {
				Transform transform = new Transform();
				Polygon originalPolygon = polygonsList[i];
				Polygon rotatedPolygon = transform.executeRotation(
						originalPolygon.getRefPoint(),//
						angle * (rotationsNumber - j), -1, originalPolygon);

				assert originalPolygon.getPoints().size() == rotatedPolygon
						.getPoints().size();

				// polygonsVec[i][j] = rotatedPolygon;
				polygonsVec2[j] = rotatedPolygon;
				rotadedPolygonMap.put(polygonsVec2[j].getId(), polygonsVec2);
			}
		}
	}

	private Map<Integer, Polygon[]> cloneMap(
			Map<Integer, Polygon[]> rotadedPolygonMap2) {
		Map<Integer, Polygon[]> mapa = new HashMap<Integer, Polygon[]>(
				rotadedPolygonMap2.size());

		for (Integer key : rotadedPolygonMap2.keySet()) {
			Polygon[] polygons = rotadedPolygonMap2.get(key);

			final Polygon[] polygonsCopy = new Polygon[polygons.length];
			for (int i = 0; i < polygons.length; i++) {
				try {
					polygonsCopy[i] = (Polygon) polygons[i].clone();
				} catch (CloneNotSupportedException e) {
					throw new RuntimeException(e);
				}
			}
			mapa.put(key, polygonsCopy);
		}
		return mapa;
	}

}
