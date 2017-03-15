package br.furb.packing.jnfp;

import java.util.ArrayList;
import java.util.List;

import br.furb.common.Point;
import br.furb.common.Polygon;
import br.furb.packing.NFPImplementation;


public class JNFP extends NFPImplementation {

	public Polygon calculateNotFitPolygon(Polygon polygonA, Polygon polygonB) {

		List<Double> xListA = new ArrayList<>();
		List<Double> yListA = new ArrayList<>();
		List<Double> xListB = new ArrayList<>();
		List<Double> yListB = new ArrayList<>();

		for (Point po : polygonA.getPoints()) {
			xListA.add(po.getX());
			yListA.add(po.getY());
		}

		for (Point po : polygonB.getPoints()) {
			xListB.add(po.getX());
			yListB.add(po.getY());
		}

		MultiPolygon stat = new MultiPolygon(xListA, yListA);
		MultiPolygon orb = new MultiPolygon(xListB, yListB);

		NoFitPolygon nfp = Orbiting.generateNFP(new MultiPolygon(stat), new MultiPolygon(orb));
		return toPolygon(nfp);
	}

	private Polygon toPolygon(NoFitPolygon nfp) {

		Polygon p = new Polygon();
		for (List<Coordinate> partList : nfp.getNfpPolygonsList()) {
			for (Coordinate coord : partList) {
				p.addPoint(new Point(coord.getxCoord(), coord.getyCoord()));
			}
		}

		return p;

	}
	
	public NFPImplementation getnewInstance() {
		return new JNFP();
	}

}
