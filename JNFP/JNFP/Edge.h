#pragma once

#include "Edge.h"
#include "Coordinate.h"
#include "TouchingEdgePair.h"
#include "Nullable.h"

struct Edge {

	bool stationary = false;
	Coordinate startPoint;
	Coordinate endPoint;
	int edgeNumber;
	bool traversed = false;

	const double round = 1e-4;

	// values to be used for bounding box intersection
	double smallX;
	double bigX;
	double smallY;
	double bigY;

	Edge();

	Edge(Coordinate& s, Coordinate& e, int eN);

	Edge(Edge &edge);

	Edge(Coordinate& s, Coordinate& e);

	bool isStationary();

	void setStationary(bool stationary);

	Coordinate getStartPoint();

	void setStartPoint(Coordinate& startPoint);

	Coordinate getEndPoint();

	void setEndPoint(Coordinate& endPoint);

	double getSmallX();

	void setSmallX(double smallX);

	double getBigX();

	void setBigX(double bigX);

	double getSmallY();

	void setSmallY(double smallY);

	double getBigY();

	void setBigY(double bigY);

	int getEdgeNumber();

	void setEdgeNumber(int edgeNumber);

	void calculateRanges();

	Nullable<TouchingEdgePair> touching(Edge& orbEdge);

	Nullable<TouchingEdgePair> touchingV2(Edge& orbEdge);

	bool contains(Coordinate& coord);

	bool containsRounded(Coordinate& coord);

	Vector makeFullVector(int eN);

	Vector makePartialVector(Coordinate& touchPoint, int eN);

	double getAngle();

	bool boundingBoxIntersect(Edge& edge);

	bool lineIntersect(Edge& testEdge);

	Coordinate calcIntersection(Edge& testEdge);

	void changeRangeValues(double x, double y);

	bool containsIntersectionPoint(Coordinate& intersectionCoord);

	bool containsPoint(Coordinate& intersectionCoord);

	double calcClockwiseValue();

	void markTraversed();

	bool isTraversed();

	void setTraversed(bool traversed);

	bool testIntersect(Edge& edge);

	Coordinate getMiddlePointEdge();

	bool edgesOrientatedRight(Edge& preEdge, Edge& postEdge);

	void replaceByNegative();
};