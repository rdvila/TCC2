#include "Vector.h"

#include "Coordinate.h"

Vector::Vector(double x, double y) {
	xCoord = x;
	yCoord = y;
}

Vector::Vector(Vector& vect) {
	xCoord = vect.getxCoord();
	yCoord = vect.getyCoord();
	edgeNumber = vect.getEdgeNumber();
	calculateVectorAngle();
	fromStatEdge = vect.isFromStatEdge();
}

Vector::Vector(Coordinate& coord, int eN, bool fromStat) {
	xCoord = coord.getxCoord();
	yCoord = coord.getyCoord();
	calculateVectorAngle();
	edgeNumber = eN;
	fromStatEdge = fromStat;
}

Vector::Vector(Coordinate& startPoint, Coordinate& endPoint) {
	Coordinate vectorCoord = endPoint.subtract(startPoint);
	xCoord = vectorCoord.getxCoord();
	yCoord = vectorCoord.getyCoord();
	calculateVectorAngle();
	edgeNumber = -1;
	fromStatEdge = false;
}

double Vector::getxCoord() {
	return xCoord;
}

void Vector::setxCoord(double xCoord) {
	this->xCoord = xCoord;
}

double Vector::getyCoord() {
	return yCoord;
}

void Vector::setyCoord(double yCoord) {
	this->yCoord = yCoord;
}

double Vector::getVectorAngle() {
	return vectorAngle;
}

void Vector::setVectorAngle(double vectorAngle) {
	this->vectorAngle = vectorAngle;
}

int Vector::getEdgeNumber() {
	return edgeNumber;
}

void Vector::setEdgeNumber(int edgeNumber) {
	this->edgeNumber = edgeNumber;
}

bool Vector::isFromStatEdge() {
	return fromStatEdge;
}

void Vector::setFromStatEdge(bool fromStatEdge) {
	this->fromStatEdge = fromStatEdge;
}

double Vector::distanceTo(Vector& vect) {
	double dX = xCoord - vect.getxCoord();
	double dY = yCoord - vect.getyCoord();
	double distance = std::sqrt(dX * dX + dY * dY);
	return distance;
}

// D-function is used to calculate where a point is located in reference to
// a vector
// if the value is larger then 0 the point is on the left
// Dabp = ((Xa-Xb)*(Ya-Yp)-(Ya-Yb)*(Xa-Xp))
double Vector::dFunction(Vector& startPoint, Vector& endPoint) {

	double dValue = (startPoint.getxCoord() - endPoint.getxCoord()) * (startPoint.getyCoord() - yCoord)
		- (startPoint.getyCoord() - endPoint.getyCoord()) * (startPoint.getxCoord() - xCoord);

	return dValue;
}

//check if the value is zero or not (trying to cope with very small deviation values)
bool Vector::dFunctionCheck(Vector& startPoint, Vector& endPoint) {
	bool touching = false;
	double dValue = (startPoint.getxCoord() - endPoint.getxCoord()) * (startPoint.getyCoord() - yCoord)
		- (startPoint.getyCoord() - endPoint.getyCoord()) * (startPoint.getxCoord() - xCoord);
	if (dValue < 1e-4 && dValue > -1e-4)touching = true;
	return touching;
}

void Vector::move(double x, double y) {
	xCoord += x;
	yCoord += y;
}

// this vector minus the given vector
Vector Vector::subtract(Vector& point) {

	return{ xCoord - point.getxCoord(), yCoord - point.getyCoord() };
}

Vector Vector::add(Vector& point) {

	return{ xCoord + point.getxCoord(), yCoord + point.getyCoord() };
}


//TODO Why is false
bool Vector::isBiggerThen(Vector& biggestCoord) {

	return false;
}

void Vector::calculateVectorAngle() {

	vectorAngle = std::atan2(yCoord, xCoord);

}

Vector* Vector::reflect() {
	xCoord = 0 - xCoord;
	yCoord = 0 - yCoord;
	return this;
}

void Vector::trimTo(Coordinate& intersectionCoord, Coordinate& startPoint) {
	xCoord = intersectionCoord.getxCoord() - startPoint.getxCoord();
	yCoord = intersectionCoord.getyCoord() - startPoint.getyCoord();
}

double Vector::getLengthSquared() {

	return xCoord*xCoord + yCoord*yCoord;
}


bool Vector::testAndTrimVector(Edge& edge, Edge& testEdge, Coordinate& coord) {
	Coordinate intersectionCoord;
	bool trimmed = false;
	// if the bounding boxes intersect, line intersection
	// has to
	// be checked and the vector may need to be trimmed
	if (edge.boundingBoxIntersect(testEdge)) {
		if (edge.lineIntersect(testEdge)) {
			intersectionCoord = edge.calcIntersection(testEdge);
			if (edge.containsIntersectionPoint(intersectionCoord) && testEdge.containsIntersectionPoint(intersectionCoord)) {
				// trim the vector with
				// endpoint = intersectionCoordinate
				trimTo(intersectionCoord, coord);
				trimmed = true;
				//because the vector gets trimmed the testEdge changes, this will result in less intersection because of the shorter vector
				//also the Vector will not be overwritten by every new intersection if the testEdge is changed, only when it has to be shorter
			}


		}

	}
	return trimmed;
}

Edge Vector::getParentEdge() {
	return parentEdge;
}

void Vector::setParentEdge(Edge& parentEdge) {
	this->parentEdge = parentEdge;
}


