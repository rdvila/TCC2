#pragma once

#include "Coordinate.h"
#include "Edge.h"
#include "MultiPolygon.h"

struct Vector {
	double xCoord;
	double yCoord;
	double vectorAngle;
	//the number of the edge that the vector slides over
	int edgeNumber;
	bool fromStatEdge;
	Edge parentEdge;

	Vector(double x, double y);

	Vector(Vector &vect);

	Vector(Coordinate& coord, int eN, bool fromStat);

	Vector(Coordinate& startPoint, Coordinate& endPoint);

	double getxCoord();

	void setxCoord(double xCoord);

	double getyCoord();

	void setyCoord(double yCoord);

	double getVectorAngle();

	void setVectorAngle(double vectorAngle);

	int getEdgeNumber();

	void setEdgeNumber(int edgeNumber);

	bool isFromStatEdge();

	void setFromStatEdge(bool fromStatEdge);

	double distanceTo(Vector& vect);

	bool operator==(Vector& vec) {
		return (vec.xCoord == xCoord && vec.yCoord == yCoord);
	}

	double dFunction(Vector& startPoint, Vector& endPoint);
	
	bool dFunctionCheck(Vector& startPoint, Vector& endPoint);

	void move(double x, double y);

	Vector subtract(Vector& point);

	Vector add(Vector& point);

	bool isBiggerThen(Vector& biggestCoord);

	void calculateVectorAngle();

	Vector* reflect();

	void trimTo(Coordinate& intersectionCoord, Coordinate& startPoint);

	double getLengthSquared();

	bool testAndTrimVector(Edge& edge, Edge& testEdge, Coordinate& coord);

	Edge getParentEdge();

	void setParentEdge(Edge& parentEdge);

};
