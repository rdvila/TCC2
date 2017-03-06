#include "Coordinate.h"


Coordinate::Coordinate() {
}

Coordinate::Coordinate(double x, double y) {
	xCoord = x;
	yCoord = y;
}

Coordinate::Coordinate(Coordinate& coordinate) {
	xCoord = coordinate.getxCoord();
	yCoord = coordinate.getyCoord();
}

double Coordinate::getxCoord() {
	return xCoord;
}

void Coordinate::setxCoord(double xCoord) {
	this->xCoord = xCoord;
}

double Coordinate::getyCoord() {
	return yCoord;
}

void Coordinate::setyCoord(double yCoord) {
	this->yCoord = yCoord;
}

double Coordinate::distanceTo(Coordinate& coord) {
	double dX = xCoord - coord.getxCoord();
	double dY = yCoord - coord.getyCoord();
	double distance = std::sqrt(dX * dX + dY * dY);
	return distance;
}

// calculating the angle: the coordinate that calls the method is the one
// where the angle needs to be calculated
double Coordinate::calculateAngle(Coordinate& coord2, Coordinate& coord3) {

	double distA = coord2.distanceTo(coord3);
	double distB = this->distanceTo(coord3);
	double distC = this->distanceTo(coord2);

	double cosAngle = (distB * distB + distC * distC - distA * distA) / (2 * distB * distC);
	double angle = std::acos(cosAngle);
	return angle;
}

// D-function is used to calculate where a point is located in reference to
// a vector
// if the value is larger then 0 the point is on the left
// Dabp = ((Xa-Xb)*(Ya-Yp)-(Ya-Yb)*(Xa-Xp))
double Coordinate::dFunction(Coordinate& startPoint, Coordinate& endPoint) {

	double dValue = (startPoint.getxCoord() - endPoint.getxCoord()) * (startPoint.getyCoord() - yCoord)
		- (startPoint.getyCoord() - endPoint.getyCoord()) * (startPoint.getxCoord() - xCoord);

	return dValue;
}

//check if the value is zero or not (trying to cope with very small deviation values)
bool Coordinate::dFunctionCheck(Coordinate& startPoint, Coordinate& endPoint) {
	bool touching = false;
	double dValue = (startPoint.getxCoord() - endPoint.getxCoord()) * (startPoint.getyCoord() - yCoord)
		- (startPoint.getyCoord() - endPoint.getyCoord()) * (startPoint.getxCoord() - xCoord);
	if (dValue < round && dValue > -round)touching = true;
	return touching;
}

void Coordinate::move(double x, double y) {
	xCoord += x;
	yCoord += y;
}

//check if two coordinates are equal (use round to make sure mistakes by rounding in the calculations are ignored
bool Coordinate::equalValuesRounded(Coordinate& coord) {


	if (std::abs(xCoord - coord.getxCoord()) > round)
		return false;
	if (std::abs(yCoord - coord.getyCoord()) > round)
		return false;
	return true;
}

// this coordinate minus the given coordinate
Coordinate Coordinate::subtract(Coordinate& point) {

	return{ xCoord - point.getxCoord(), yCoord - point.getyCoord() };
}

Coordinate Coordinate::subtract(Vector& vector) {

	return{ xCoord - vector.getxCoord(), yCoord - vector.getyCoord() };
}

Coordinate Coordinate::add(Coordinate &point) {

	return{ xCoord + point.getxCoord(), yCoord + point.getyCoord() };
}

Coordinate Coordinate::add(Vector& vector) {

	return{ xCoord + vector.getxCoord(), yCoord + vector.getyCoord() };
}


//TODO why is false?
bool Coordinate::isBiggerThen(Coordinate& biggestCoord) {
	return false;
}

double Coordinate::getLengthSquared() {

	return xCoord*xCoord + yCoord*yCoord;
}

Coordinate Coordinate::translatedTo(Vector& vector) {
	Coordinate transCoord{ xCoord + vector.getxCoord(), yCoord + vector.getyCoord() };
	return transCoord;
}

void Coordinate::translate(Vector& vector) {
	xCoord += vector.getxCoord();
	yCoord += vector.getyCoord();
}


void Coordinate::replaceByNegative() {

	this->xCoord = -xCoord;
	this->yCoord = -yCoord;

}

void Coordinate::rotateNinety() {
	double helpXCoord = -yCoord;

	this->yCoord = xCoord;
	this->xCoord = helpXCoord;

}
