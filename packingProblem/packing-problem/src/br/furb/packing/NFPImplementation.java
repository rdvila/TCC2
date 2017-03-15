package br.furb.packing;

import br.furb.common.Polygon;

public abstract class NFPImplementation {
	
	public abstract Polygon calculateNotFitPolygon(Polygon polygonA, Polygon polygonB);
	public abstract NFPImplementation getnewInstance();

}
