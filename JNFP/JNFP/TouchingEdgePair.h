#pragma once

#include "Nullable.h"
#include "Edge.h"
#include "Coordinate.h"

/**
*         Pairs of edges that are touching used in the orbiting method are
*         stored with the coordinates of the touching point
*/
struct TouchingEdgePair {

	Edge statEdge;
	Edge orbEdge;
	Coordinate touchPoint;

	const double angleRound = 1e-6;
	const double round = 1e-4;

	// bools saying if the touching point equals a start or end point from an
	// edge
	bool touchStatStart = false;
	bool touchStatEnd = false;

	bool touchOrbStart = false;
	bool touchOrbEnd = false;

	double startAngle;
	double endAngle;

	TouchingEdgePair();

	TouchingEdgePair(Edge& statEdge, Edge& orbEdge, Coordinate& touchPoint);

	Edge getStatEdge();

	void setStatEdge(Edge& statEdge);

	Edge getOrbEdge();

	void setOrbEdge(Edge& orbEdge);

	Coordinate getTouchPoint();

	void setTouchPoint(Coordinate& touchPoint);

	Nullable<Vector> getPotentialVector();

	void calcFeasibleAngleRange();

	bool isFeasibleVector(Vector& vector);

	bool isFeasibleVectorWithRounding(Vector& vector);

};