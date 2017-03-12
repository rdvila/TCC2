package br.furb.packing;

import br.furb.packing.NoFitPolygon.TouchingEdgeVertex;
import br.furb.packing.NoFitPolygon.Translation;

public class PotencialTranslation {
	Translation translation;

	Line stationary;

	Line moving;

	Line derivedFrom;

	TouchingEdgeVertex touchingEdge;

	boolean isMiddleEdge;

	boolean fromStationary;

	public PotencialTranslation(Translation translation, Line stationary, Line moving, Line derivedFrom,//
			TouchingEdgeVertex potencialTrans, boolean isMiddleEdge, boolean fromStationary) {
		super();
		this.translation = translation;
		this.stationary = stationary;
		this.moving = moving;
		this.derivedFrom = derivedFrom;
		this.touchingEdge = potencialTrans;
		this.isMiddleEdge = isMiddleEdge;
		this.fromStationary = fromStationary;
	}

	@Override
	public String toString() {
		return "s-> " + stationary + "\n" + "m-> " + moving;
	}

}
