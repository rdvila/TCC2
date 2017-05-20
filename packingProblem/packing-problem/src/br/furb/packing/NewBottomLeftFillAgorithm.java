package br.furb.packing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.MultiKeyMap;

import br.furb.common.Polygon;
import br.furb.common.Transform;
import de.lighti.clipper.Clipper.ClipType;
import de.lighti.clipper.Clipper.PolyFillType;
import de.lighti.clipper.Clipper.PolyType;
import de.lighti.clipper.DefaultClipper;
import de.lighti.clipper.Paths;

public class NewBottomLeftFillAgorithm {

	private final MultiKeyMap noFitPolygonMap = new MultiKeyMap();

	private Map<Integer, Polygon> polygonMap;

	public double resolution;

	private Map<Integer, Polygon[]> rotadedPolygonMap;

	private NFPImplementation noFitPolygon;

	private double sheetHeight;
	
	public NewBottomLeftFillAgorithm(NFPImplementation noFitPolygon) {
		this.noFitPolygon = noFitPolygon.getnewInstance(); //TODO rever isso
	}
	
	public PackingResult doPacking(Polygon[] polygonsList, int rotationsNumber, double sheetHeight) {

		this.sheetHeight = sheetHeight;
		
		ArrayList<Polygon> sheetShapes = new ArrayList<>();		
		generatePolygons(polygonsList, rotationsNumber);

		for (int i = 0; i < polygonsList.length; i++) {

			Polygon[] polygonsRotaded = rotadedPolygonMap.get(polygonsList[i].getId());
			
			assert polygonsRotaded != null : polygonsList[i].getId();

			// try all rotations configured
			for (int j = 0; j < polygonsRotaded.length; j++) {
				Polygon polygon = polygonsRotaded[j];
				
				Paths allNFPClipUnion      =  unionAllNFPClip(polygon, sheetShapes, sheetHeight);
				Paths polygonPaths         = polygon.toPathsClosedFromPolygon();
				
				
			}
		}
			
		return null;
	}

	private Paths unionAllNFPClip(Polygon polygon, ArrayList<Polygon> sheetShapes, double sheetHeight) {
		
		ArrayList<Paths> allNFP = new ArrayList<>();
		for (Polygon p : sheetShapes) {
			Polygon nfpResult = noFitPolygon.calculateNotFitPolygon(p, polygon);
			allNFP.add(nfpResult.toPathsClosedFromPolygon());
		}
		
		DefaultClipper clipper = new DefaultClipper();
		Paths result = new Paths();
		for (Paths ps : allNFP) {
			clipper.addPaths(ps, PolyType.SUBJECT, true);
		}
		
		Polygon innerClipPolygon = createInnerPolygon(sheetShapes, sheetHeight);		
		clipper.execute( ClipType.UNION, result, PolyFillType.NON_ZERO, PolyFillType.NON_ZERO );
		clipper.addPaths(innerClipPolygon.toPathsClosedFromPolygon(), PolyType.SUBJECT, true);
		clipper.execute( ClipType.INTERSECTION, result, PolyFillType.NON_ZERO, PolyFillType.NON_ZERO );
		return result;		
	}

	private Polygon createInnerPolygon(ArrayList<Polygon> sheetShapes, double sheetHeight) {
		double maxX = sheetHeight;
		double maxY = -1.0;
		for (Polygon p : sheetShapes) {
			if (p.maxY().y > maxY) {
				maxY = p.maxY().y;
			}
		}
		Polygon result = new Polygon();
		result.addPoint(0   , 0);
		result.addPoint(0   , maxY);
		result.addPoint(maxX, maxY);
		result.addPoint(maxX, 0);
		return result;
	}

	/**
	 * Gera os polígonos rotacionados
	 */
	private void generatePolygons(Polygon[] polygonsList, int rotationsNumber) {

		rotadedPolygonMap = new HashMap<Integer, Polygon[]>();
		double angleAlpha = 0;
		if (rotationsNumber > 0) {
			angleAlpha = 360.0 / (rotationsNumber + 1);
		}

		for (int i = 0; i < polygonsList.length; i++) {
			Polygon[] allPolygonsRotations = new Polygon[rotationsNumber+1];
			double angle = 0;
			
			for (int j = 0; j < (rotationsNumber+1); j++) {
				Transform transform = new Transform();				
				Polygon originalPolygon = polygonsList[i];				
				Polygon rotatedPolygon = transform.executeRotation(
						originalPolygon.getRefPoint(),//
						angle, -1, originalPolygon);
				allPolygonsRotations[j] = rotatedPolygon;
				angle += angleAlpha;
			}

			rotadedPolygonMap.put(allPolygonsRotations[0].getId(), allPolygonsRotations);
		}
	}
}
