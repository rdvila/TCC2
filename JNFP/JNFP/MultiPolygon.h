#pragma once

#include <vector>

#include "Edge.h"
#include "Coordinate.h"
#include "NoFitPolygon.h"

//https://docs.oracle.com/javase/7/docs/api/constant-values.html#java.lang.Double.MAX_VALUE
const double Double_MAX_VALUE = 1.7976931348623157e308;

/**
* this class contains a polygon to be used to generate the no-fit
*         polygon the polygon exists of coordinates and can have holes
*/
struct MultiPolygon {

	int nHoles; // the number of holes

	std::vector<Coordinate> outerPolygon; // the polygon that envelops the holes
	std::vector<Edge> outerPolygonEdges;

	std::vector<std::vector<Coordinate>> holes;
	std::vector<std::vector<Edge>> holeEdges;

	const double round = 1e-4;

	double biggestX = 0;
	double biggestY = 0;
	double smallestX = Double_MAX_VALUE;
	double smallestY = Double_MAX_VALUE;

	MultiPolygon();

	void createEdges();

	double getSmallestX();

	void setSmallestX(double smallestX);

	double getSmallestY();

	void setSmallestY(double smallestY);

	double getBiggestX();

	void setBiggestX(double biggestX);

	double getBiggestY();

	void setBiggestY(double biggestY);

	std::vector<Coordinate> getOuterPolygon();

	void setOuterPolygon(std::vector<Coordinate>& outerPolygon);

	std::vector<std::vector<Coordinate>> getHoles();

	void setHoles(std::vector<std::vector<Coordinate>>& holes);

	int getnHoles();

	void setnHoles(int nHoles);

	std::vector<Edge> getOuterPolygonEdges();

	void setOuterPolygonEdges(std::vector<Edge>& outerPolygonEdges);

	std::vector<std::vector<Edge>> getHoleEdges();

	void setHoleEdges(std::vector<std::vector<Edge>>& holeEdges);

	void translate(double x, double y);

	void translate(Vector& vect);
	
	Coordinate findBottomCoord();

	Coordinate findTopCoord();

	std::vector<TouchingEdgePair> findTouchingEdges(MultiPolygon& orbPoly);

	std::vector<TouchingEdgePair> findTouchingEdgesWithoutTravMark(MultiPolygon& orbPoly);

	void isStationary();

	bool checkClockwise(std::vector<Coordinate>& polygon);

	void changeClockOrientation(std::vector<Coordinate>& polygon);

	Edge findUntraversedEdge();

	Coordinate searchStartPoint(Edge& possibleStartEdge, MultiPolygon& orbPoly);

	Coordinate searchOrbStartPoint(Edge& possibleStartOrbEdge, MultiPolygon& orbPoly);

	Coordinate searchStartPoint(Edge& possibleStartEdge, MultiPolygon& orbPoly, NoFitPolygon& nfp);

	Coordinate searchOrbStartPoint(Edge& possibleStartOrbEdge, MultiPolygon& orbPoly, NoFitPolygon& nfp);

	std::vector<std::vector<Coordinate>> searchStartPointList(Edge& possibleStartEdge, MultiPolygon& orbPoly);

	std::vector<std::vector<Coordinate>> searchOrbStartPointList(Edge& possibleStartOrbEdge, MultiPolygon& orbPoly);

	bool polygonsIntersectEdgeIntersect(MultiPolygon& statPoly, MultiPolygon& orbPoly);

	bool polygonsIntersectPointInPolygon(MultiPolygon& statPoly, MultiPolygon& orbPoly);

	bool polygonsIntersectEdgeOverlap(MultiPolygon& statPoly, MultiPolygon& orbPoly);

	bool pointInPolygon(Coordinate& coord);

	void replaceByNegative();

	void shiftNinety();
};
