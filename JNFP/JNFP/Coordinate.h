#pragma once

#include <cmath>

#include "Vector.h"

struct Coordinate {

	double xCoord;
	double yCoord;

	double round = 1e-4;

	Coordinate();

	Coordinate(double x, double y);

	Coordinate(Coordinate &coodinate);

	double getxCoord();

	void setxCoord(double xCoord);

	double getyCoord();

	void setyCoord(double yCoord);
	
	bool operator==(Coordinate& coord) {
		return (coord.xCoord == xCoord && coord.yCoord == yCoord);
	}

	double distanceTo(Coordinate& coord);

	double calculateAngle(Coordinate& coord2, Coordinate& coord3);

	double dFunction(Coordinate& startPoint, Coordinate& endPoint);

	bool dFunctionCheck(Coordinate& startPoint, Coordinate& endPoint);

	void move(double x, double y);

	bool equalValuesRounded(Coordinate& coord);

	Coordinate subtract(Coordinate& point);

	Coordinate subtract(Vector& vector);

	//TODO why add creates a new Coodinate
	Coordinate add(Coordinate& point);

	Coordinate add(Vector& vector);

	bool isBiggerThen(Coordinate& biggestCoord);

	double getLengthSquared();

	Coordinate translatedTo(Vector& vector);

	void translate(Vector& vector);

	void replaceByNegative();

	void rotateNinety();
};