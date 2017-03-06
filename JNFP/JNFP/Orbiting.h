#pragma once

#include "MultiPolygon.h"
#include "NoFitPolygon.h"

class Orbiting {

	const int numberOfFails = 0;
	const int numberOfSecFails = 0;
	const int numberStuckInfinite = 0;

	static NoFitPolygon generateNFP(MultiPolygon& statPoly, MultiPolygon& orbPoly);

	static bool perfectOverlap(MultiPolygon& statPoly, MultiPolygon& orbPoly);

	static void orbitPolygon(NoFitPolygon& nfp, MultiPolygon& statPoly, MultiPolygon& orbPoly, bool outer);
};