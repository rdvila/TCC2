#include "MultiPolygon.h"


//TODO
//MultiPolygon(File file) throws FileNotFoundException

//TODO
//MultiPolygon(MultiPolygon mp)

MultiPolygon::MultiPolygon() {

}

void MultiPolygon::createEdges() {
	for (int i = 0; i < outerPolygon.length; i++) {
		if (i == outerPolygon.size() - 1) {
			outerPolygonEdges[i] = { outerPolygon[i], outerPolygon[0], i };
		}
		else {
			outerPolygonEdges[i] = { outerPolygon[i], outerPolygon[i + 1], i };
		}
	}

	for (int i = 0; i < nHoles; i++) {
		for (int j = 0; j < holes[i].size(); j++) {

			if (j == holes[i].size() - 1) {
				holeEdges[i][j] = { holes[i][j], holes[i][0], i };
			}
			else {
				holeEdges[i][j] = { holes[i][j], holes[i][j + 1], i };
			}

		}
	}
}

double MultiPolygon::getSmallestX() {
	return smallestX;
}

void MultiPolygon::setSmallestX(double smallestX) {
	this->smallestX = smallestX;
}

double MultiPolygon::getSmallestY() {
	return smallestY;
}

void MultiPolygon::setSmallestY(double smallestY) {
	this->smallestY = smallestY;
}

double MultiPolygon::getBiggestX() {
	return biggestX;
}

void MultiPolygon::setBiggestX(double biggestX) {
	this->biggestX = biggestX;
}

double MultiPolygon::getBiggestY() {
	return biggestY;
}

void MultiPolygon::setBiggestY(double biggestY) {
	this->biggestY = biggestY;
}

//TODO verify these get and sets
std::vector<Coordinate> MultiPolygon::getOuterPolygon() {
	return outerPolygon;
}

void MultiPolygon::setOuterPolygon(std::vector<Coordinate>& outerPolygon) {
	this->outerPolygon = outerPolygon;
}

std::vector<std::vector<Coordinate>> MultiPolygon::getHoles() {
	return holes;
}

void MultiPolygon::setHoles(std::vector<std::vector<Coordinate>>& holes) {
	this->holes = holes;
}

int MultiPolygon::getnHoles() {
	return nHoles;
}

void MultiPolygon::setnHoles(int nHoles) {
	this->nHoles = nHoles;
}

//TODO verify these get and sets
std::vector<Edge> MultiPolygon::getOuterPolygonEdges() {
	return outerPolygonEdges;
}

void MultiPolygon::setOuterPolygonEdges(std::vector<Edge>& outerPolygonEdges) {
	this->outerPolygonEdges = outerPolygonEdges;
}

std::vector<std::vector<Edge>> MultiPolygon::getHoleEdges() {
	return holeEdges;
}

void MultiPolygon::setHoleEdges(std::vector<std::vector<Edge>>& holeEdges) {
	this->holeEdges = holeEdges;
}

void MultiPolygon::translate(double x, double y) {

	for (Coordinate coord : outerPolygon) {
		coord.move(x, y);
	}

	for (auto hole : holes) {
		for (auto coord : hole) {
			coord.move(x, y);
		}
	}
	//for the edges the new minimum and maximum values need to be recalculated
	for (Edge edge : outerPolygonEdges) {
		edge.changeRangeValues(x, y);
	}
	for (auto edgeList : holeEdges) {
		for (auto edge : edgeList) {
			edge.changeRangeValues(x, y);
		}
	}
}

//translate with vector
void MultiPolygon::translate(Vector& vect) {
	double x = vect.getxCoord();
	double y = vect.getyCoord();

	for (Coordinate coord : outerPolygon) {
		coord.move(x, y);
	}

	for (auto hole : holes) {
		for (auto coord : hole) {
			coord.move(x, y);
		}
	}

	//for the edges the new minimum and maximum values need to be recalculated
	for (Edge edge : outerPolygonEdges) {
		edge.changeRangeValues(x, y);
	}
	for (auto edgeList : holeEdges) {
		for (auto edge : edgeList) {
			edge.changeRangeValues(x, y);
		}
	}
}

Coordinate MultiPolygon::findBottomCoord() {
	Coordinate bottomCoord = outerPolygon[0];
	for (Coordinate coord : outerPolygon) {
		// if the y-value of coord is lower then the current bottomCoord,
		// replace bottomCoord
		if (coord.getyCoord() < bottomCoord.getyCoord())
			bottomCoord = coord;
		else if (coord.getyCoord() == bottomCoord.getyCoord()) {
			if (coord.getxCoord() < bottomCoord.getxCoord()) {
				bottomCoord = coord;
			}
		}
	}
	return bottomCoord;
}

Coordinate MultiPolygon::findTopCoord() {
	Coordinate topCoord = outerPolygon[0];
	for (Coordinate coord : outerPolygon) {
		// if the y-value of coord is higher then the current topCoord,
		// replace topCoord
		if (coord.getyCoord() > topCoord.getyCoord())
			topCoord = coord;
		else if (coord.getyCoord() == topCoord.getyCoord()) {
			if (coord.getxCoord() > topCoord.getxCoord()) {
				topCoord = coord;
			}
		}
	}
	return topCoord;
}

std::vector<TouchingEdgePair> MultiPolygon::findTouchingEdges(MultiPolygon& orbPoly) {

	std::vector<TouchingEdgePair> touchingEdges;
	Nullable<TouchingEdgePair> tEP;
	// the outer polygon of the orbiting multipolygon
	auto orbOuterPolygonEdges = orbPoly.getOuterPolygonEdges();
	auto orbHoleEdges = orbPoly.getHoleEdges();
	// check for every point of orb if it touches an edge of stat
	for (Edge orbEdge : orbOuterPolygonEdges) {

		for (Edge statEdge : outerPolygonEdges) {

			tEP = statEdge.touching(orbEdge);
			if (tEP.has_value()) {
				touchingEdges.push_back(*tEP);
				statEdge.markTraversed();
				orbEdge.markTraversed();
			}
		}
	}

	for (Edge orbEdge : orbOuterPolygonEdges) {

		for (auto statHole : holeEdges) {

			for (auto statEdge : statHole) {

				tEP = statEdge.touching(orbEdge);
				if (tEP.has_value()) {
					touchingEdges.push_back(*tEP);
					statEdge.markTraversed();
					orbEdge.markTraversed();
				}
			}
		}
	}
	for (auto orbHole : orbHoleEdges) {
		for (auto orbEdge : orbHole) {

			for (Edge statEdge : outerPolygonEdges) {

				tEP = statEdge.touching(orbEdge);
				if (tEP.has_value()) {
					touchingEdges.push_back(*tEP);
					statEdge.markTraversed();
					orbEdge.markTraversed();
				}
			}
		}
	}
	for (auto orbHole : orbHoleEdges) {
		for (auto orbEdge : orbHole) {

			for (auto statHole : holeEdges) {

				for (auto statEdge : statHole) {

					tEP = statEdge.touching(orbEdge);
					if (tEP.has_value()) {
						touchingEdges.push_back(*tEP);
						statEdge.markTraversed();
						orbEdge.markTraversed();
					}
				}
			}
		}
	}

	return touchingEdges;
}


std::vector<TouchingEdgePair> MultiPolygon::findTouchingEdgesWithoutTravMark(MultiPolygon& orbPoly) {

	std::vector<TouchingEdgePair> touchingEdges;
	Nullable<TouchingEdgePair> tEP;
	// the outer polygon of the orbiting multipolygon
	auto orbOuterPolygonEdges = orbPoly.getOuterPolygonEdges();
	auto orbHoleEdges = orbPoly.getHoleEdges();
	// check for every point of orb if it touches an edge of stat
	for (Edge orbEdge : orbOuterPolygonEdges) {

		for (Edge statEdge : outerPolygonEdges) {

			tEP = statEdge.touching(orbEdge);
			if (tEP.has_value()) {
				touchingEdges.push_back(*tEP);
			}
		}
	}

	for (Edge orbEdge : orbOuterPolygonEdges) {

		for (auto statHole : holeEdges) {

			for (auto statEdge : statHole) {

				tEP = statEdge.touching(orbEdge);
				if (tEP.has_value()) {
					touchingEdges.push_back(*tEP);
				}
			}
		}
	}
	for (auto orbHole : orbHoleEdges) {
		for (auto orbEdge : orbHole) {

			for (auto statEdge : outerPolygonEdges) {

				tEP = statEdge.touching(orbEdge);
				if (tEP.has_value()) {
					touchingEdges.push_back(*tEP);
				}
			}
		}
	}
	for (auto orbHole : orbHoleEdges) {
		for (auto orbEdge : orbHole) {

			for (auto statHole : holeEdges) {

				for (auto statEdge : statHole) {

					tEP = statEdge.touching(orbEdge);
					if (tEP.has_value()) {
						touchingEdges.push_back(*tEP);
					}
				}
			}
		}
	}

	return touchingEdges;
}

void MultiPolygon::isStationary() {
	for (Edge e : outerPolygonEdges) {
		e.setStationary(true);
	}
	for (auto eA : holeEdges) {
		for (Edge e : eA) {
			e.setStationary(true);
		}
	}
}
//the next method returns true if the polygon is clockwise
bool MultiPolygon::checkClockwise(std::vector<Coordinate>& polygon) {
	double clockwiseValue = 0;

	double xDiff;
	double ySum;

	//If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
	for (int i = 0; i < polygon.size(); i++) {
		if (i < polygon.size() - 1) {
			//Sum over the edges, (x2-x1)(y2+y1). If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
			xDiff = polygon[i + 1].getxCoord() - polygon[i].getxCoord();
			ySum = polygon[i + 1].getyCoord() + polygon[i].getyCoord();
			clockwiseValue += xDiff*ySum;

		}
		else {
			xDiff = polygon[0].getxCoord() - polygon[i].getxCoord();
			ySum = polygon[0].getyCoord() + polygon[i].getyCoord();
			clockwiseValue += xDiff*ySum;
		}
	}

	if (clockwiseValue > 0) return true;
	else return false;
}

void MultiPolygon::changeClockOrientation(std::vector<Coordinate>& polygon) {
	std::vector<Coordinate> changedPolygon;
	changedPolygon.push_back(polygon[0]);
	for (int i = 1; i < polygon.size(); i++) {
		changedPolygon.push_back(polygon[polygon.size() - i]);
	}
	for (int i = 0; i < polygon.size(); i++) {
		polygon[i] = changedPolygon[i];
	}

}

bool MultiPolygon::allEdgesTraversed() {
	for (auto e : outerPolygonEdges) {
		if (!e.isTraversed())return false;
	}

	for (auto hole : holeEdges) {
		for (Edge h : hole) {
			if (!h.isTraversed())return false;
		}
	}

	return true;
}

Edge MultiPolygon::findUntraversedEdge() {
	//this is true when an untraversed edge is found
	Edge untraversedEdge = null;

	int i = 0;
	while (untraversedEdge == null && i < outerPolygonEdges.length) {
		if (!outerPolygonEdges[i].isTraversed()) untraversedEdge = outerPolygonEdges[i];
		i++;

	}
	i = 0;
	int j;
	while (untraversedEdge == null && i < holeEdges.length) {
		j = 0;
		while (untraversedEdge == null && j < holeEdges[i].length) {
			if (!holeEdges[i][j].isTraversed()) untraversedEdge = holeEdges[i][j];
			j++;

		}
		i++;
	}
	return untraversedEdge;
}

Coordinate searchStartPoint(Edge possibleStartEdge, MultiPolygon orbPoly) {

	Coordinate currentStartPoint = new Coordinate(possibleStartEdge.getStartPoint());
	System.out.println(currentStartPoint);
	//for every point of the orbiting polygon that can be placed at that spot
	int orbPointIndex = 0;
	bool startPointPossible = false;
	Vector placeOrbPolyVector;

	Vector nextPossibleSpotVector;

	for (int i = 0; i < orbPoly.getOuterPolygon().length; i++) {
		Coordinate orbPoint = new Coordinate(orbPoly.getOuterPolygon()[i]);
		//translation to the startpoint from the current orbiting point
		placeOrbPolyVector = new Vector(orbPoint, currentStartPoint);

		orbPoly.translate(placeOrbPolyVector);

		if (!polygonsIntersectPointInPolygon(this, orbPoly)) {
			return currentStartPoint;
		}
		else {//test 1, check if it is ever possible

			if (orbPointIndex == 0) {
				startPointPossible = possibleStartEdge.edgesOrientatedRight(orbPoly.getOuterPolygonEdges()[orbPoly.getOuterPolygonEdges().length - 1],
					orbPoly.getOuterPolygonEdges()[orbPointIndex]);
			}
			else {
				startPointPossible = possibleStartEdge.edgesOrientatedRight(orbPoly.getOuterPolygonEdges()[orbPointIndex - 1],
					orbPoly.getOuterPolygonEdges()[orbPointIndex]);
			}
		}
		//keep looking for a place where the polygons don't overlap, till the end of the line has been reached
		if (startPointPossible) {
			//trim the vector made by the possible startEdge and the current start point
			while (!currentStartPoint.equals(possibleStartEdge.getEndPoint()) && polygonsIntersectPointInPolygon(this, orbPoly)) {
				nextPossibleSpotVector = new Vector(currentStartPoint, possibleStartEdge.getEndPoint());

				nextPossibleSpotVector.trimFeasibleVector(orbPoly, this, true);
				nextPossibleSpotVector.trimFeasibleVector(this, orbPoly, false);

				orbPoly.translate(nextPossibleSpotVector);
				currentStartPoint.translate(nextPossibleSpotVector);
			}
			if (currentStartPoint.equals(possibleStartEdge.getEndPoint()) && !polygonsIntersectPointInPolygon(this, orbPoly)) {
				return currentStartPoint;
			}
			else if (!currentStartPoint.equals(possibleStartEdge.getEndPoint())) {
				return currentStartPoint;
			}
		}
		orbPointIndex++;
	}
	return null;

}

Coordinate searchOrbStartPoint(Edge possibleStartOrbEdge, MultiPolygon orbPoly) {
	Coordinate currentStartPoint;
	Coordinate orbitingStartPoint = possibleStartOrbEdge.getStartPoint();
	Vector placeOrbPolyVector;
	int statPointIndex = 0;
	bool startPointPossible;

	for (int i = 0; i < getOuterPolygon().length; i++) {
		currentStartPoint = outerPolygon[i];
		placeOrbPolyVector = new Vector(orbitingStartPoint, currentStartPoint);

		Vector nextPossibleSpotVector;

		orbPoly.translate(placeOrbPolyVector);

		if (!polygonsIntersectPointInPolygon(this, orbPoly)) {

			return currentStartPoint;
		}

		else {//test 1, check if it is ever possible	

			if (statPointIndex == 0) {
				startPointPossible = possibleStartOrbEdge.edgesOrientatedRight(getOuterPolygonEdges()[getOuterPolygonEdges().length - 1],
					getOuterPolygonEdges()[statPointIndex]);
			}
			else {
				startPointPossible = possibleStartOrbEdge.edgesOrientatedRight(getOuterPolygonEdges()[statPointIndex - 1],
					getOuterPolygonEdges()[statPointIndex]);
			}
			if (startPointPossible) {
				//trim the vector made by the possible startEdge and the current start point
				while (!currentStartPoint.equals(possibleStartOrbEdge.getEndPoint()) && polygonsIntersectPointInPolygon(this, orbPoly)) {
					nextPossibleSpotVector = new Vector(possibleStartOrbEdge.getEndPoint(), currentStartPoint);

					nextPossibleSpotVector.trimFeasibleVector(orbPoly, this, true);
					nextPossibleSpotVector.trimFeasibleVector(this, orbPoly, false);
					orbPoly.translate(nextPossibleSpotVector);

				}

				if (currentStartPoint.equals(possibleStartOrbEdge.getEndPoint()) && !polygonsIntersectPointInPolygon(this, orbPoly)) {
					return currentStartPoint;
				}
				else if (!currentStartPoint.equals(possibleStartOrbEdge.getEndPoint())) {

					return currentStartPoint;
				}
			}

		}
		statPointIndex++;
	}
	return null;
}

Coordinate searchStartPoint(Edge possibleStartEdge, MultiPolygon orbPoly, NoFitPolygon nfp) {

	Coordinate currentStartPoint = new Coordinate(possibleStartEdge.getStartPoint());
	//for every point of the orbiting polygon that can be placed at that spot
	int orbPointIndex = 0;
	bool startPointPossible = false;
	Vector placeOrbPolyVector;

	Vector nextPossibleSpotVector;

	for (int i = 0; i < orbPoly.getOuterPolygon().length; i++) {
		Coordinate orbPoint = new Coordinate(orbPoly.getOuterPolygon()[i]);
		//translation to the startpoint from the current orbiting point
		placeOrbPolyVector = new Vector(orbPoint, currentStartPoint);

		orbPoly.translate(placeOrbPolyVector);

		if (!polygonsIntersectPointInPolygon(this, orbPoly) && !nfp.containsPoint(currentStartPoint)) {
			return currentStartPoint;
		}
		else {//test 1, check if it is ever possible

			if (orbPointIndex == 0) {
				startPointPossible = possibleStartEdge.edgesOrientatedRight(orbPoly.getOuterPolygonEdges()[orbPoly.getOuterPolygonEdges().length - 1],
					orbPoly.getOuterPolygonEdges()[orbPointIndex]);
			}
			else {
				startPointPossible = possibleStartEdge.edgesOrientatedRight(orbPoly.getOuterPolygonEdges()[orbPointIndex - 1],
					orbPoly.getOuterPolygonEdges()[orbPointIndex]);
			}
		}
		//keep looking for a place where the polygons don't overlap, till the end of the line has been reached
		if (startPointPossible) {
			//trim the vector made by the possible startEdge and the current start point
			do {
				nextPossibleSpotVector = new Vector(currentStartPoint, possibleStartEdge.getEndPoint());

				nextPossibleSpotVector.trimFeasibleVector(orbPoly, this, true);
				nextPossibleSpotVector.trimFeasibleVector(this, orbPoly, false);

				orbPoly.translate(nextPossibleSpotVector);
				currentStartPoint.translate(nextPossibleSpotVector);

			} while (!currentStartPoint.equals(possibleStartEdge.getEndPoint()) && (polygonsIntersectPointInPolygon(this, orbPoly) || nfp.containsPoint(currentStartPoint)));

			if (currentStartPoint.equals(possibleStartEdge.getEndPoint()) && !polygonsIntersectPointInPolygon(this, orbPoly) && !nfp.containsPoint(currentStartPoint)) {

				return currentStartPoint;
			}
			else if (!currentStartPoint.equals(possibleStartEdge.getEndPoint()) && !nfp.containsPoint(currentStartPoint)) {
				return currentStartPoint;
			}
		}
		orbPointIndex++;
	}
	return null;

}



Coordinate searchOrbStartPoint(Edge possibleStartOrbEdge, MultiPolygon orbPoly, NoFitPolygon nfp) {
	Coordinate currentStartPoint;
	Coordinate orbitingStartPoint = possibleStartOrbEdge.getStartPoint();
	Vector placeOrbPolyVector;
	int statPointIndex = 0;
	bool startPointPossible;

	for (int i = 0; i < getOuterPolygon().length; i++) {
		currentStartPoint = outerPolygon[i];
		placeOrbPolyVector = new Vector(orbitingStartPoint, currentStartPoint);

		Vector nextPossibleSpotVector;

		orbPoly.translate(placeOrbPolyVector);

		if (!polygonsIntersectPointInPolygon(this, orbPoly) && !nfp.containsPoint(currentStartPoint)) {

			return currentStartPoint;
		}

		else {//test 1, check if it is ever possible	

			if (statPointIndex == 0) {
				startPointPossible = possibleStartOrbEdge.edgesOrientatedRight(getOuterPolygonEdges()[getOuterPolygonEdges().length - 1],
					getOuterPolygonEdges()[statPointIndex]);
			}
			else {
				startPointPossible = possibleStartOrbEdge.edgesOrientatedRight(getOuterPolygonEdges()[statPointIndex - 1],
					getOuterPolygonEdges()[statPointIndex]);
			}
			if (startPointPossible) {
				//trim the vector made by the possible startEdge and the current start point
				do {
					nextPossibleSpotVector = new Vector(possibleStartOrbEdge.getEndPoint(), currentStartPoint);

					nextPossibleSpotVector.trimFeasibleVector(orbPoly, this, true);
					nextPossibleSpotVector.trimFeasibleVector(this, orbPoly, false);
					orbPoly.translate(nextPossibleSpotVector);

				} while (!currentStartPoint.equals(possibleStartOrbEdge.getEndPoint()) && (polygonsIntersectPointInPolygon(this, orbPoly) || nfp.containsPoint(currentStartPoint)));

				if (currentStartPoint.equals(possibleStartOrbEdge.getEndPoint()) && !polygonsIntersectPointInPolygon(this, orbPoly) && !nfp.containsPoint(currentStartPoint)) {

					return currentStartPoint;
				}
				else if (!currentStartPoint.equals(possibleStartOrbEdge.getEndPoint()) && !nfp.containsPoint(currentStartPoint)) {

					return currentStartPoint;
				}
			}

		}
		statPointIndex++;
	}
	return null;
}

std::vector<Coordinate[]> searchStartPointList(Edge possibleStartEdge, MultiPolygon orbPoly) {

	std::vector<Coordinate[]> startPointList = new Arraystd::vector<>();
	Coordinate[] startOrbStat;
	Coordinate currentStartPoint = new Coordinate(possibleStartEdge.getStartPoint());
	//for every point of the orbiting polygon that can be placed at that spot
	int orbPointIndex = 0;
	bool startPointPossible = false;
	Vector placeOrbPolyVector;

	Vector nextPossibleSpotVector = null;

	for (int i = 0; i < orbPoly.getOuterPolygon().length; i++) {
		Coordinate orbPoint = new Coordinate(orbPoly.getOuterPolygon()[i]);
		//translation to the startpoint from the current orbiting point
		placeOrbPolyVector = new Vector(orbPoint, currentStartPoint);

		orbPoly.translate(placeOrbPolyVector);


		if (!polygonsIntersectPointInPolygon(this, orbPoly)) {
			startOrbStat = new Coordinate[2];

			startOrbStat[0] = new Coordinate(currentStartPoint);
			startOrbStat[1] = new Coordinate(orbPoly.getOuterPolygon()[0]);

			startPointList.add(startOrbStat);
		}
		else {//test 1, check if it is ever possible

			if (orbPointIndex == 0) {
				startPointPossible = possibleStartEdge.edgesOrientatedRight(orbPoly.getOuterPolygonEdges()[orbPoly.getOuterPolygonEdges().length - 1],
					orbPoly.getOuterPolygonEdges()[orbPointIndex]);
			}
			else {
				startPointPossible = possibleStartEdge.edgesOrientatedRight(orbPoly.getOuterPolygonEdges()[orbPointIndex - 1],
					orbPoly.getOuterPolygonEdges()[orbPointIndex]);
			}
		}
		//keep looking for a place where the polygons don't overlap, till the end of the line has been reached
		if (startPointPossible) {
			//trim the vector made by the possible startEdge and the current start point
			while (!currentStartPoint.equals(possibleStartEdge.getEndPoint()) && polygonsIntersectPointInPolygon(this, orbPoly)) {
				nextPossibleSpotVector = new Vector(currentStartPoint, possibleStartEdge.getEndPoint());

				nextPossibleSpotVector.trimFeasibleVector(orbPoly, this, true);
				nextPossibleSpotVector.trimFeasibleVector(this, orbPoly, false);

				if (nextPossibleSpotVector.getLengthSquared() < round)break;

				orbPoly.translate(nextPossibleSpotVector);
				currentStartPoint.translate(nextPossibleSpotVector);
			}
			if (nextPossibleSpotVector.getLengthSquared() < round) {
				//it wil not lead to a start point
			}
			else if (currentStartPoint.equals(possibleStartEdge.getEndPoint()) && !polygonsIntersectPointInPolygon(this, orbPoly)) {
				startOrbStat = new Coordinate[2];
				startOrbStat[0] = new Coordinate(currentStartPoint);
				startOrbStat[1] = new Coordinate(orbPoly.getOuterPolygon()[0]);
				startPointList.add(startOrbStat);
			}
			else if (!currentStartPoint.equals(possibleStartEdge.getEndPoint())) {
				startOrbStat = new Coordinate[2];
				startOrbStat[0] = new Coordinate(currentStartPoint);
				startOrbStat[1] = new Coordinate(orbPoly.getOuterPolygon()[0]);
				startPointList.add(startOrbStat);
			}
		}
		orbPointIndex++;
	}
	return startPointList;

}

std::vector<Coordinate[]> searchOrbStartPointList(Edge possibleStartOrbEdge, MultiPolygon orbPoly) {
	std::vector<Coordinate[]> startPointList = new Arraystd::vector<>();
	Coordinate[] startOrbStat;

	Coordinate currentStartPoint;
	Coordinate orbitingStartPoint = possibleStartOrbEdge.getStartPoint();
	Vector placeOrbPolyVector;
	int statPointIndex = 0;
	bool startPointPossible;

	for (int i = 0; i < getOuterPolygon().length; i++) {
		currentStartPoint = outerPolygon[i];
		placeOrbPolyVector = new Vector(orbitingStartPoint, currentStartPoint);

		Vector nextPossibleSpotVector;

		orbPoly.translate(placeOrbPolyVector);

		if (!polygonsIntersectPointInPolygon(this, orbPoly)) {

			startOrbStat = new Coordinate[2];

			startOrbStat[0] = new Coordinate(currentStartPoint);
			startOrbStat[1] = new Coordinate(orbPoly.getOuterPolygon()[0]);

			startPointList.add(startOrbStat);
		}

		else {//test 1, check if it is ever possible	

			if (statPointIndex == 0) {
				startPointPossible = possibleStartOrbEdge.edgesOrientatedRight(getOuterPolygonEdges()[getOuterPolygonEdges().length - 1],
					getOuterPolygonEdges()[statPointIndex]);
			}
			else {
				startPointPossible = possibleStartOrbEdge.edgesOrientatedRight(getOuterPolygonEdges()[statPointIndex - 1],
					getOuterPolygonEdges()[statPointIndex]);
			}
			if (startPointPossible) {
				//trim the vector made by the possible startEdge and the current start point
				do {
					nextPossibleSpotVector = new Vector(possibleStartOrbEdge.getEndPoint(), currentStartPoint);

					nextPossibleSpotVector.trimFeasibleVector(orbPoly, this, true);
					nextPossibleSpotVector.trimFeasibleVector(this, orbPoly, false);
					orbPoly.translate(nextPossibleSpotVector);
				} while (!currentStartPoint.equals(possibleStartOrbEdge.getEndPoint()) && (polygonsIntersectPointInPolygon(this, orbPoly))
					&& nextPossibleSpotVector.getLengthSquared() >= round);
				if (nextPossibleSpotVector.getLengthSquared() < round) {
					//it wil not lead to a start point
				}
				else if (currentStartPoint.equals(possibleStartOrbEdge.getEndPoint()) && !polygonsIntersectPointInPolygon(this, orbPoly)) {
					startOrbStat = new Coordinate[2];

					startOrbStat[0] = new Coordinate(currentStartPoint);
					startOrbStat[1] = new Coordinate(orbPoly.getOuterPolygon()[0]);

					startPointList.add(startOrbStat);
				}
				else if (!currentStartPoint.equals(possibleStartOrbEdge.getEndPoint())) {

					startOrbStat = new Coordinate[2];

					startOrbStat[0] = new Coordinate(currentStartPoint);
					startOrbStat[1] = new Coordinate(orbPoly.getOuterPolygon()[0]);

					startPointList.add(startOrbStat);
				}
			}

		}
		statPointIndex++;
	}
	return startPointList;
}

bool polygonsIntersectEdgeIntersect(MultiPolygon statPoly, MultiPolygon orbPoly) {

	for (Edge outerStatEdge : statPoly.getOuterPolygonEdges()) {
		for (Edge outerOrbEdge : orbPoly.getOuterPolygonEdges()) {

			if (outerOrbEdge.testIntersect(outerStatEdge)) {
				return true;
			}
		}
	}
	//check orb outer with stat holes
	for (Edge outerOrbEdge : orbPoly.getOuterPolygonEdges()) {
		for (Edge[] statHoles : statPoly.getHoleEdges()) {
			for (Edge statHoleEdge : statHoles) {
				if (outerOrbEdge.testIntersect(statHoleEdge)) {
					return true;
				}
			}

		}
	}
	//check stat outer with orb holes
	for (Edge outerStatEdge : statPoly.getOuterPolygonEdges()) {
		for (Edge[] orbHoles : orbPoly.getHoleEdges()) {
			for (Edge orbHoleEdge : orbHoles) {
				if (outerStatEdge.testIntersect(orbHoleEdge)) {
					return true;
				}
			}
		}
	}
	//check holes
	for (Edge[] orbHoles : orbPoly.getHoleEdges()) {
		for (Edge orbHoleEdge : orbHoles) {
			for (Edge[] statHoles : statPoly.getHoleEdges()) {
				for (Edge statHoleEdge : statHoles) {
					if (orbHoleEdge.testIntersect(statHoleEdge)) {
						return true;
					}
				}
			}
		}
	}
	return false;

}



bool polygonsIntersectPointInPolygon(MultiPolygon statPoly, MultiPolygon orbPoly) {
	bool isOnEdge;
	bool middlePointOnEdge;
	bool touchedOuterEdge = false;
	bool touchedHoleEdge = false;
	int i = 1;
	if (polygonsIntersectEdgeIntersect(statPoly, orbPoly))return true;
	if (polygonsIntersectEdgeOverlap(statPoly, orbPoly))return true;

	for (Coordinate coord : orbPoly.getOuterPolygon()) {
		isOnEdge = false;
		for (Edge statEdge : statPoly.getOuterPolygonEdges()) {
			if (statEdge.containsPoint(coord)) {
				isOnEdge = true;
				touchedOuterEdge = true;
			}
		}
		for (Edge[] holes : statPoly.getHoleEdges()) {
			for (Edge statEdge : holes) {
				if (statEdge.containsPoint(coord)) {
					isOnEdge = true;
					touchedHoleEdge = true;
				}

			}
		}
		if (touchedHoleEdge && touchedOuterEdge)return true;
		//our method for seeing if a point is in the polygon does not give a certain result for points that fall on the edge
		if (statPoly.pointInPolygon(coord) && !isOnEdge) {
			return true;
		}
		if (isOnEdge) {
			middlePointOnEdge = false;
			Edge edgeToTest = new Edge(coord, orbPoly.getOuterPolygon()[i]);
			for (Edge statEdge : statPoly.getOuterPolygonEdges()) {
				if (statEdge.testIntersect(edgeToTest)) return true;
			}
			for (Edge[] holes : statPoly.getHoleEdges()) {
				for (Edge statEdge : holes) {
					if (statEdge.testIntersect(edgeToTest)) return true;
				}
			}

			Coordinate middlePoint = edgeToTest.getMiddlePointEdge();
			for (Edge statEdge : statPoly.getOuterPolygonEdges()) {
				if (statEdge.containsPoint(middlePoint)) {
					middlePointOnEdge = true;
				}

			}
			for (Edge[] holes : statPoly.getHoleEdges()) {
				for (Edge statEdge : holes) {
					if (statEdge.containsPoint(middlePoint)) {
						middlePointOnEdge = true;
					}

				}
			}
			if (!middlePointOnEdge) {
				if (statPoly.pointInPolygon(middlePoint))return true;
			}
		}
		i++;
		if (i > orbPoly.getOuterPolygon().length - 1)i = 0;
	}
	i = 1;
	for (Coordinate coord : statPoly.getOuterPolygon()) {
		isOnEdge = false;
		for (Edge statEdge : orbPoly.getOuterPolygonEdges()) {
			if (statEdge.containsPoint(coord))isOnEdge = true;
		}
		//our method for seeing if a point is in the polygon does not give a certain result for points that fall on the edge
		if (!isOnEdge && orbPoly.pointInPolygon(coord)) {
			return true;
		}
		if (isOnEdge) {
			middlePointOnEdge = false;
			Edge edgeToTest = new Edge(coord, statPoly.getOuterPolygon()[i]);
			for (Edge orbEdge : orbPoly.getOuterPolygonEdges()) {
				if (orbEdge.testIntersect(edgeToTest)) return true;
			}
			for (Edge[] holes : orbPoly.getHoleEdges()) {
				for (Edge orbEdge : holes) {
					if (orbEdge.testIntersect(edgeToTest)) return true;
				}
			}
			Coordinate middlePoint = edgeToTest.getMiddlePointEdge();
			for (Edge orbEdge : orbPoly.getOuterPolygonEdges()) {
				if (orbEdge.containsPoint(middlePoint)) middlePointOnEdge = true;
			}
			for (Edge[] holes : orbPoly.getHoleEdges()) {
				for (Edge orbEdge : holes) {
					if (orbEdge.containsPoint(middlePoint)) middlePointOnEdge = true;
				}
			}
			if (!middlePointOnEdge) {
				if (orbPoly.pointInPolygon(middlePoint))return true;
			}
		}
		i++;
		if (i > statPoly.getOuterPolygon().length - 1)i = 0;
	}
	return false;
}


bool polygonsIntersectEdgeOverlap(MultiPolygon statPoly, MultiPolygon orbPoly) {
	for (Edge e : statPoly.getOuterPolygonEdges()) {
		for (Edge f : orbPoly.getOuterPolygonEdges()) {
			if (Math.abs(e.getAngle() - f.getAngle()) % (Math.PI * 2) == 0) {
				if (!e.getStartPoint().equalValuesRounded(f.getEndPoint()) && !e.getEndPoint().equalValuesRounded(f.getStartPoint())) {
					if (e.getEndPoint().equalValuesRounded(f.getEndPoint())) {
						return true;
					}
					if (e.getStartPoint().equalValuesRounded(f.getStartPoint())) {
						return true;
					}
					if (e.containsPoint(f.getStartPoint()) || e.containsPoint(f.getEndPoint())) {
						return true;
					}
					if (f.containsPoint(e.getStartPoint()) || f.containsPoint(e.getEndPoint()))return true;
				}
			}
		}
	}
	return false;
}

//-----------------------------------------------------------------------------------------------------------------------------
//	http://alienryderflex.com/polygon/

//  The function will return true if the point x,y is inside the polygon, or
//  false if it is not.  If the point is exactly on the edge of the polygon,
//  then the function may return true or false.


bool pointInPolygon(Coordinate coord) {

	int polyCorners = outerPolygon.length;
	int i, j = polyCorners - 1;
	bool oddNodes = false;

	for (i = 0; i < polyCorners; i++) {
		if ((outerPolygon[i].getyCoord() < coord.getyCoord() && outerPolygon[j].getyCoord() >= coord.getyCoord()
			|| outerPolygon[j].getyCoord() < coord.getyCoord()
			&& outerPolygon[i].getyCoord() >= coord.getyCoord())
			&& (outerPolygon[i].getxCoord() <= coord.getxCoord()
				|| outerPolygon[j].getxCoord() <= coord.getxCoord())) {
			// ^= is bitwise XOR assignement
			oddNodes ^= (outerPolygon[i].getxCoord() + (coord.getyCoord() - outerPolygon[i].getyCoord())
				/ (outerPolygon[j].getyCoord() - outerPolygon[i].getyCoord())
				* (outerPolygon[j].getxCoord() - outerPolygon[i].getxCoord()) < coord.getxCoord());
		}
		j = i;
	}
	bool inHole = false;
	//if a hole contains the point it isn't contained by the polygon
	if (nHoles != 0) {
		for (Coordinate[] hole : holes) {
			polyCorners = hole.length;
			i = polyCorners - 1;
			j = polyCorners - 1;
			for (i = 0; i < polyCorners; i++) {
				if ((hole[i].getyCoord() < coord.getyCoord() && hole[j].getyCoord() >= coord.getyCoord()
					|| hole[j].getyCoord() < coord.getyCoord()
					&& hole[i].getyCoord() >= coord.getyCoord())
					&& (hole[i].getxCoord() <= coord.getxCoord()
						|| hole[j].getxCoord() <= coord.getxCoord())) {
					// ^= is bitwise XOR assignement
					inHole ^= (hole[i].getxCoord() + (coord.getyCoord() - hole[i].getyCoord())
						/ (hole[j].getyCoord() - hole[i].getyCoord())
						* (hole[j].getxCoord() - hole[i].getxCoord()) < coord.getxCoord());
				}
				j = i;
			}
			if (inHole) return false;
		}
	}
	return oddNodes;
}

void replaceByNegative() {

	for (Coordinate coord : outerPolygon) {
		coord.replaceByNegative();
	}
	for (Coordinate[] hole : holes) {
		for (Coordinate coord : hole) {
			coord.replaceByNegative();
		}
	}
	createEdges();

}

void shiftNinety() {
	for (Coordinate coord : outerPolygon) {
		coord.rotateNinety();
	}
	for (Coordinate[] hole : holes) {
		for (Coordinate coord : hole) {
			coord.rotateNinety();
		}
	}

	createEdges();
}
	