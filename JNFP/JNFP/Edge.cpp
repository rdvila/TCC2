#include "Edge.h"
#include "Coordinate.h"


Edge::Edge() {

}

Edge::Edge(Coordinate& s, Coordinate& e, int eN) {
	startPoint = s;
	endPoint = e;
	edgeNumber = eN;
	calculateRanges();
}

Edge::Edge(Edge& edge) {
	startPoint = { edge.getStartPoint() };
	endPoint = { edge.getEndPoint() };
	edgeNumber = edge.getEdgeNumber();
	calculateRanges();
}

Edge::Edge(Coordinate& s, Coordinate& e) {
	startPoint = s;
	endPoint = e;
	edgeNumber = -1;
	calculateRanges();
}

bool Edge::isStationary() {
	return stationary;
}

void Edge::setStationary(bool stationary) {
	this->stationary = stationary;
}

Coordinate Edge::getStartPoint() {
	return startPoint;
}

void Edge::setStartPoint(Coordinate& startPoint) {
	this->startPoint = startPoint;
}

Coordinate Edge::getEndPoint() {
	return endPoint;
}

void Edge::setEndPoint(Coordinate& endPoint) {
	this->endPoint = endPoint;
}

double Edge::getSmallX() {
	return smallX;
}

void Edge::setSmallX(double smallX) {
	this->smallX = smallX;
}

double Edge::getBigX() {
	return bigX;
}

void Edge::setBigX(double bigX) {
	this->bigX = bigX;
}

double Edge::getSmallY() {
	return smallY;
}

void Edge::setSmallY(double smallY) {
	this->smallY = smallY;
}

double Edge::getBigY() {
	return bigY;
}

void Edge::setBigY(double bigY) {
	this->bigY = bigY;
}

int Edge::getEdgeNumber() {
	return edgeNumber;
}

void Edge::setEdgeNumber(int edgeNumber) {
	this->edgeNumber = edgeNumber;
}

void Edge::calculateRanges() {

	Coordinate start = getStartPoint();
	Coordinate end = getEndPoint();

	if (start.getxCoord() < end.getxCoord()) {
		smallX = start.getxCoord();
		bigX = end.getxCoord();
	}
	else {
		smallX = end.getxCoord();
		bigX = start.getxCoord();
	}

	if (start.getyCoord() < end.getyCoord()) {
		smallY = start.getyCoord();
		bigY = end.getyCoord();
	}
	else {
		smallY = end.getyCoord();
		bigY = start.getyCoord();
	}
}

Nullable<TouchingEdgePair> Edge::touching(Edge& orbEdge) {

	if (startPoint.dFunctionCheck(orbEdge.getStartPoint(), orbEdge.getEndPoint())) {
		if (orbEdge.containsRounded(startPoint)) {
			TouchingEdgePair tEP { *this, orbEdge, startPoint };
			return{ tEP };
		}
	}
	if (endPoint.dFunctionCheck(orbEdge.getStartPoint(), orbEdge.getEndPoint())) {
		if (orbEdge.containsRounded(endPoint)) {
			TouchingEdgePair tEP{ *this, orbEdge, endPoint };
			return{ tEP };
		}
	}
	if (orbEdge.getStartPoint().dFunctionCheck(startPoint, endPoint)) {
		if (containsRounded(orbEdge.getStartPoint())) {
			TouchingEdgePair tEP = { *this, orbEdge, orbEdge.getStartPoint() };
			return{ tEP };
		}
	}
	if (orbEdge.getEndPoint().dFunctionCheck(startPoint, endPoint)) {
		if (containsRounded(orbEdge.getEndPoint())) {
			TouchingEdgePair tEP { *this, orbEdge, orbEdge.getEndPoint() };
			return{ tEP };
		}
	}

	return{};//null
}



Nullable<TouchingEdgePair> Edge::touchingV2(Edge& orbEdge) {
	//first look if the start or end points are equal, if this is the case they are certainly touching
	if (startPoint == orbEdge.startPoint) {
		TouchingEdgePair tEP{ *this, orbEdge, startPoint };
		return{ tEP };
	}
	if (startPoint == orbEdge.endPoint) {
		TouchingEdgePair tEP { *this, orbEdge, startPoint };
		return{ tEP };
	}
	if (endPoint == orbEdge.startPoint) {
		TouchingEdgePair tEP { *this, orbEdge, endPoint };
		return{ tEP };
	}
	if (endPoint == orbEdge.endPoint) {
		TouchingEdgePair tEP { *this, orbEdge, endPoint };
		return{ tEP };
	}
	//after checking those points, check if it is somewhere in between start and end

	if (startPoint.dFunctionCheck(orbEdge.getStartPoint(), orbEdge.getEndPoint())) {
		if (orbEdge.contains(startPoint)) {
			TouchingEdgePair tEP{ *this, orbEdge, startPoint };
			return{ tEP };
		}
	}
	if (endPoint.dFunctionCheck(orbEdge.getStartPoint(), orbEdge.getEndPoint())) {
		if (orbEdge.contains(endPoint)) {
			TouchingEdgePair tEP{ *this, orbEdge, endPoint };
			return{ tEP };
		}
	}
	if (orbEdge.getStartPoint().dFunctionCheck(startPoint, endPoint)) {
		if (contains(orbEdge.getStartPoint())) {
			TouchingEdgePair tEP{ *this, orbEdge, orbEdge.getStartPoint() };
			return{ tEP };
		}
	}
	if (orbEdge.getEndPoint().dFunctionCheck(startPoint, endPoint)) {
		if (contains(orbEdge.getEndPoint())) {
			TouchingEdgePair tEP{ *this, orbEdge, orbEdge.getEndPoint() };
			return{ tEP };
		}
	}

	return{}; //null
}

bool Edge::contains(Coordinate& coord) {

	bool containsX = false;
	bool containsY = false;
	// check x
	// coordinate-----------------------------------------------------------------------------------------------------
	if (startPoint.getxCoord() < endPoint.getxCoord()) {
		if (startPoint.getxCoord() <= coord.getxCoord() && endPoint.getxCoord() >= coord.getxCoord())
			containsX = true;
	}
	else if (startPoint.getxCoord() >= coord.getxCoord() && endPoint.getxCoord() <= coord.getxCoord())
		containsX = true;

	// check
	// y-coordinate-----------------------------------------------------------------------------------------------------
	if (startPoint.getyCoord() < endPoint.getyCoord()) {
		if (startPoint.getyCoord() <= coord.getyCoord() && endPoint.getyCoord() >= coord.getyCoord())
			containsY = true;
	}
	else if (startPoint.getyCoord() >= coord.getyCoord() && endPoint.getyCoord() <= coord.getyCoord())
		containsY = true;

	return containsX && containsY;
}

bool Edge::containsRounded(Coordinate& coord) {

	bool containsX = false;
	bool containsY = false;

	// check x
	// coordinate-----------------------------------------------------------------------------------------------------
	if (startPoint.getxCoord() < endPoint.getxCoord() + round) {
		if (startPoint.getxCoord() <= coord.getxCoord() + round && endPoint.getxCoord() >= coord.getxCoord() - round)
			containsX = true;
	}
	else if (startPoint.getxCoord() >= coord.getxCoord() - round && endPoint.getxCoord() <= coord.getxCoord() + round)
		containsX = true;

	// check
	// y-coordinate-----------------------------------------------------------------------------------------------------
	if (startPoint.getyCoord() < endPoint.getyCoord() + round) {
		if (startPoint.getyCoord() <= coord.getyCoord() + round && endPoint.getyCoord() >= coord.getyCoord() - round)
			containsY = true;
	}
	else if (startPoint.getyCoord() >= coord.getyCoord() - round && endPoint.getyCoord() <= coord.getyCoord() + round)
		containsY = true;

	return containsX && containsY;
}

// if the vector will be created from the whole edge
Vector Edge::makeFullVector(int eN) {

	// if the orbiting edge is being used for the vector, it needs to be
	// inversed
	// this means startPoint-endPoint in stead of endPoint-startPoint
	if (!stationary) {
		Vector vector = { startPoint.subtract(endPoint), eN, stationary };
		return vector;
	}
	else {
		Vector vector = { endPoint.subtract(startPoint), eN, stationary };
		return vector;
	}


}

Vector Edge::makePartialVector(Coordinate& touchPoint, int eN) {
	// if the orbiting edge is being used for the vector, it needs to be
	// inversed
	// this means startPoint-endPoint in stead of endPoint-startPoint
	if (!stationary) {
		//TODO:the edgenumber from the orbiting edge may be wrong and cause errors
		Vector vector{ touchPoint.subtract(endPoint), eN, stationary };
		return vector;
	}
	else {
		Vector vector{ endPoint.subtract(touchPoint), eN, stationary };
		return vector;
	}

}

double Edge::getAngle() {
	// we can't use the method makeFullVector, this will reverse the vector
	// if it's from the orbiting polygon
	Vector vector { endPoint.subtract(startPoint), edgeNumber, stationary };

	return vector.getVectorAngle();
}

bool Edge::boundingBoxIntersect(Edge& edge) {

	bool intersect = true;

	if (edge.getBigX() <= smallX - round || edge.getSmallX() >= bigX + round || edge.getBigY() <= smallY - round
		|| edge.getSmallY() >= bigY + round)
		intersect = false;

	return intersect;
}

bool Edge::lineIntersect(Edge& testEdge) {
	bool intersect = true;
	// the lines intersect if the start coordinate and the end coordinate
	// of one of the edges are not both on the same side
	//in most cases this will guarantee an intersection, but there are cases where the intersection point will not be part of one of the lines
	if (testEdge.getStartPoint().dFunction(startPoint, endPoint) <= round
		&& testEdge.getEndPoint().dFunction(startPoint, endPoint) <= round) {
		intersect = false;
	}
	else if (testEdge.getStartPoint().dFunction(startPoint, endPoint) >= -round
		&& testEdge.getEndPoint().dFunction(startPoint, endPoint) >= -round) {
		intersect = false;
	}

	return intersect;
}

Coordinate Edge::calcIntersection(Edge& testEdge) {
	/*
	* the used formula is
	* x=((x1*y2-y1*x2)*(x3-x4)-(x1-x2)*(x3*y4-y3*x4))/((x1-x2)*(y3-y4)-(y1-
	* y2)*(x3-x4));
	* y=((x1*y2-y1*x2)*(y3-y4)-(y1-y2)*(x3*y4-y3*x4))/((x1-x2)*(y3-y4)-(y1-
	* y2)*(x3-x4));
	*/

	double x1 = startPoint.getxCoord();
	double x2 = endPoint.getxCoord();
	double y1 = startPoint.getyCoord();
	double y2 = endPoint.getyCoord();

	double x3 = testEdge.getStartPoint().getxCoord();
	double x4 = testEdge.getEndPoint().getxCoord();
	double y3 = testEdge.getStartPoint().getyCoord();
	double y4 = testEdge.getEndPoint().getyCoord();

	// x1 - x2
	double dx1 = x1 - x2;
	// x3 - x4
	double dx2 = x3 - x4;
	// y1 - y2
	double dy1 = y1 - y2;
	// y3 - y4
	double dy2 = y3 - y4;

	// (x1*y2-y1*x2)
	double pd1 = x1 * y2 - y1 * x2;
	// (x3*y4-y3*x4)
	double pd2 = x3 * y4 - y3 * x4;

	// (x1*y2-y1*x2)*(x3-x4)-(x1-x2)*(x3*y4-y3*x4)
	double xNumerator = pd1 * dx2 - dx1 * pd2;
	// (x1*y2-y1*x2)*(y3-y4)-(y1-y2)*(x3*y4-y3*x4)
	double yNumerator = pd1 * dy2 - dy1 * pd2;

	// (x1-x2)*(y3-y4)-(y1-y2)*(x3-x4)
	double denominator = dx1 * dy2 - dy1 * dx2;

	double xCoord = xNumerator / denominator;
	double yCoord = yNumerator / denominator;

	return{ xCoord, yCoord };
}

//when a translation is taking place, the values of min and max have to be adjusted
void Edge::changeRangeValues(double x, double y) {
	smallX += x;
	bigX += x;
	smallY += y;
	bigY += y;

}

bool Edge::containsIntersectionPoint(Coordinate& intersectionCoord) {
	if (intersectionCoord.getxCoord() < smallX - round) {
		return false;
	}
	if (intersectionCoord.getxCoord() > bigX + round) {
		return false;
	}
	if (intersectionCoord.getyCoord() < smallY - round) {
		return false;
	}
	if (intersectionCoord.getyCoord() > bigY + round) {
		return false;
	}
	return true;
}

bool Edge::containsPoint(Coordinate& intersectionCoord) {
	bool onLine;
	onLine = intersectionCoord.dFunctionCheck(startPoint, endPoint);
	if (onLine == false)return false;
	if (intersectionCoord.getxCoord() < smallX - round) {
		return false;
	}
	if (intersectionCoord.getxCoord() > bigX + round) {
		return false;
	}
	if (intersectionCoord.getyCoord() < smallY - round) {
		return false;
	}
	if (intersectionCoord.getyCoord() > bigY + round) {
		return false;
	}
	return true;
}

double Edge::calcClockwiseValue() {

	//Sum over the edges, (x2-x1)(y2+y1). If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
	double xDiff = endPoint.getxCoord() - startPoint.getxCoord();
	double ySum = endPoint.getyCoord() + startPoint.getyCoord();

	return xDiff*ySum;
}

void Edge::markTraversed() {

	traversed = true;

}

bool Edge::isTraversed() {
	return traversed;
}

void Edge::setTraversed(bool traversed) {
	this->traversed = traversed;
}

bool Edge::testIntersect(Edge& edge) {
	Coordinate intersectionCoord;
	bool intersection = false;

	// if the bounding boxes intersect, line intersection
	// has to be checked and the edge may need to be trimmed
	if (boundingBoxIntersect(edge)) {
		if (lineIntersect(edge)) {
			intersectionCoord = calcIntersection(edge);
			if (containsIntersectionPoint(intersectionCoord) && edge.containsIntersectionPoint(intersectionCoord)) {
				if (intersectionCoord.equalValuesRounded(edge.getStartPoint()) || intersectionCoord.equalValuesRounded(edge.getEndPoint())
					|| intersectionCoord.equalValuesRounded(startPoint) || intersectionCoord.equalValuesRounded(endPoint)) {

				}
				else intersection = true;
			}
		}

	}
	return intersection;
}

Coordinate Edge::getMiddlePointEdge() {
	double midxCoord = (startPoint.getxCoord() + endPoint.getxCoord()) / 2;
	double midyCoord = (startPoint.getyCoord() + endPoint.getyCoord()) / 2;
	return{ midxCoord, midyCoord };
}

bool Edge::edgesOrientatedRight(Edge& preEdge, Edge& postEdge) {
	//the edges are right of or parallel with this edge 
	if (preEdge.getStartPoint().dFunction(startPoint, endPoint) <= round &&
		postEdge.getEndPoint().dFunction(startPoint, endPoint) <= round)
		return true;
	return false;
}

void Edge::replaceByNegative() {
	startPoint.replaceByNegative();
	endPoint.replaceByNegative();
}

