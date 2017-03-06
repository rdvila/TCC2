#include "TouchingEdgePair.h"

#include "Edge.h"

const double PI = 3.14159265359;

TouchingEdgePair::TouchingEdgePair(Edge& statEdge, Edge& orbEdge, Coordinate& touchPoint) {

	this->statEdge = statEdge;
	this->orbEdge = orbEdge;
	this->touchPoint = touchPoint;

	if (statEdge.getStartPoint().equalValuesRounded(touchPoint)) {
		touchStatStart = true;
	}
	else if (statEdge.getEndPoint().equalValuesRounded(touchPoint)) {
		touchStatEnd = true;
	}

	if (orbEdge.getStartPoint().equalValuesRounded(touchPoint)) {
		touchOrbStart = true;
	}
	else if (orbEdge.getEndPoint().equalValuesRounded(touchPoint)) {
		touchOrbEnd = true;
	}
	if (orbEdge.getStartPoint().equalValuesRounded(statEdge.getStartPoint())) {
		touchStatStart = true;
		touchOrbStart = true;
	}
	if (orbEdge.getEndPoint().equalValuesRounded(statEdge.getEndPoint())) {
		touchStatEnd = true;
		touchOrbEnd = true;
	}
	if (orbEdge.getStartPoint().equalValuesRounded(statEdge.getEndPoint())) {

		touchStatEnd = true;
		touchOrbStart = true;
	}
	if (orbEdge.getEndPoint().equalValuesRounded(statEdge.getStartPoint())) {
		touchStatStart = true;
		touchOrbEnd = true;
	}
}

Edge TouchingEdgePair::getStatEdge() {
	return statEdge;
}

void TouchingEdgePair::setStatEdge(Edge& statEdge) {
	this->statEdge = statEdge;
}

Edge TouchingEdgePair::getOrbEdge() {
	return orbEdge;
}

void TouchingEdgePair::setOrbEdge(Edge& orbEdge) {
	this->orbEdge = orbEdge;
}

Coordinate TouchingEdgePair::getTouchPoint() {
	return touchPoint;
}

void TouchingEdgePair::setTouchPoint(Coordinate& touchPoint) {
	this->touchPoint = touchPoint;
}


Nullable<Vector> TouchingEdgePair::getPotentialVector() {

	/*
	* there are four possible ways that end or start points can be
	* touching: stat orb ------------- end end start start start end end
	* start
	*/
	// ---------------------------------------------------------------------------------------------------------------------
	// if the touching point is at the end of both edges, there will be no
	// potential vector
	
	if (touchStatEnd && touchOrbEnd)
		return{};



	// ---------------------------------------------------------------------------------------------------------------------
	// if both startpoints are touching, the translationvector will be the
	// orbiting edge if the relative position
	// of the orbiting edge is left to the stationary edge (can be
	// determined with the D-function)
	// this by looking if the endpoint of the orbiting edge is located left
	// or right
	else if (touchStatStart && touchOrbStart) {
		// if Dfunction returns value > 0 the orbiting edge is left of the
		// stationary edge, and the translation
		// vector will be derived from the orbiting edge
		if (orbEdge.getEndPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) > 0) {
			Vector potentialVector = orbEdge.makeFullVector(statEdge.getEdgeNumber());
			potentialVector.setParentEdge(orbEdge);
			return{ potentialVector };
		}
		else {
			// if the D-function returns 0, edges are parallel, either edge
			// can be used.
			Vector potentialVector = statEdge.makeFullVector(statEdge.getEdgeNumber());
			potentialVector.setParentEdge(statEdge);
			return{ potentialVector };

		}
	}
	// ---------------------------------------------------------------------------------------------------------------------
	else if (touchStatStart && touchOrbEnd) {
		// in this case, if the orbiting edge is located left of the
		// stationary edge, no vector will be possible
		// if it is on the right, the stationary edge will provide the
		// vector.
		if (orbEdge.getStartPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) > 0) {
			//potentialVector = null;
		}
		else {
			Vector potentialVector = statEdge.makeFullVector(statEdge.getEdgeNumber());
			potentialVector.setParentEdge(statEdge);
			return potentialVector;
		}
	}
	// ---------------------------------------------------------------------------------------------------------------------
	else if (touchStatEnd && touchOrbStart) {
		if (orbEdge.getEndPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) > 0) {
			return{};
		}
		else {
			Vector potentialVector = orbEdge.makeFullVector(statEdge.getEdgeNumber());
			potentialVector.setParentEdge(orbEdge);
			return potentialVector;
		}
	}
	// ---------------------------------------------------------------------------------------------------------------------
	// the two other cases left are when one of the edges is touching the
	// other somewhere in between start and end point

	else if (touchStatStart || touchStatEnd) {
		Vector potentialVector = orbEdge.makePartialVector(touchPoint, statEdge.getEdgeNumber());
		potentialVector.setParentEdge(orbEdge);
		return potentialVector;
	}
	// ---------------------------------------------------------------------------------------------------------------------
	else if (touchOrbStart || touchOrbEnd) {
		Vector potentialVector = statEdge.makePartialVector(touchPoint, statEdge.getEdgeNumber());
		potentialVector.setParentEdge(statEdge);
		return potentialVector;
	}
	return{};
}

void TouchingEdgePair::calcFeasibleAngleRange() {

	double stationaryAngle = statEdge.getAngle();
	if (stationaryAngle < 0)
		stationaryAngle = stationaryAngle + PI * 2;

	double orbitingAngle = orbEdge.getAngle();
	if (orbitingAngle < 0)
		orbitingAngle = orbitingAngle + PI * 2;

	//Situation 8: one edge is parallel with the other but starts at the end of the other one
	if ((stationaryAngle <= orbitingAngle + angleRound && stationaryAngle >= orbitingAngle - angleRound) && ((touchStatEnd && touchOrbStart) || (touchStatStart&&touchOrbEnd))) {
		startAngle = stationaryAngle - PI;
		endAngle = stationaryAngle + PI;

		return;
	}
	//Situation 8.2: edges are parallel and end or start in the same point
	if (((stationaryAngle <= orbitingAngle - PI + 1e-6 && stationaryAngle >= orbitingAngle - PI - angleRound)
		|| (stationaryAngle <= orbitingAngle + PI + 1e-6 && stationaryAngle >= orbitingAngle + PI - angleRound)
		&& ((touchStatEnd && touchOrbEnd) || (touchStatStart && touchOrbStart)))) {
		if (touchStatEnd && touchOrbEnd && touchStatStart && touchOrbStart) {
			startAngle = stationaryAngle - PI;
			endAngle = stationaryAngle;
			return;
		}
		startAngle = stationaryAngle - PI;
		endAngle = stationaryAngle + PI;

		return;
	}

	//Situation 7
	if ((stationaryAngle <= orbitingAngle + 1e-6 && stationaryAngle >= orbitingAngle - angleRound)
		|| (stationaryAngle <= orbitingAngle - PI + 1e-6 && stationaryAngle >= orbitingAngle - PI - angleRound)
		|| (stationaryAngle <= orbitingAngle + PI + 1e-6 && stationaryAngle >= orbitingAngle + PI - angleRound)) {
		startAngle = stationaryAngle - PI;
		endAngle = stationaryAngle;
		return;
	}


	// situation 1
	if (!touchStatStart && !touchStatEnd) {
		startAngle = stationaryAngle - PI;
		endAngle = stationaryAngle;

		return;
	}

	// situation 2
	if (!touchOrbStart && !touchOrbEnd) {
		// stationary edge is located to the right of orbiting edge
		//we have to check the D-function for the start and end of the stationary edge to see if it is left or right, one of them will be zero, 
		//the other one will be smaller or bigger then zero
		if (statEdge.getEndPoint().dFunction(orbEdge.getStartPoint(), orbEdge.getEndPoint()) <= round
			&& statEdge.getStartPoint().dFunction(orbEdge.getStartPoint(), orbEdge.getEndPoint()) <= round) {
			startAngle = orbitingAngle;
			endAngle = orbitingAngle + PI;
		}
		// stationary edge is located to the right of orbiting edge
		else {
			startAngle = orbitingAngle - PI;
			endAngle = orbitingAngle;
		}
		return;
	}

	// both angles are positive
	// situation 3
	if (touchStatStart && touchOrbStart) {

		// orbEdge is right of statEdge
		if (orbEdge.getEndPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) < 0) {

			// I work with the negative angle of orbiting edge
			orbitingAngle -= 2 * PI;

			startAngle = orbitingAngle - PI;
			endAngle = stationaryAngle;

			while (endAngle - 2 * PI >= startAngle)
				startAngle += 2 * PI;
			while (endAngle < startAngle)
				endAngle += 2 * PI;
		}
		// orbEdge left of statEdge
		else {
			startAngle = stationaryAngle;
			endAngle = orbitingAngle + PI;

			while (endAngle < startAngle)
				startAngle -= 2 * PI;
			while (endAngle - 2 * PI >= startAngle)
				startAngle += 2 * PI;

		}
		return;
	}

	// situation 4
	if (touchStatStart && touchOrbEnd) {

		// orbEdge is right of statEdge
		if (orbEdge.getStartPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) < 0) {

			startAngle = orbitingAngle;
			endAngle = stationaryAngle;

			while (endAngle < startAngle)
				startAngle -= 2 * PI;
			while (endAngle - 2 * PI >= startAngle)
				startAngle += 2 * PI;
		}
		// orbEdge is left of statEdge
		else {
			startAngle = stationaryAngle;
			endAngle = orbitingAngle;

			while (endAngle < startAngle)
				startAngle -= 2 * PI;
			while (endAngle - 2 * PI >= startAngle)
				startAngle += 2 * PI;
		}
		return;
	}

	// situation 5
	if (touchStatEnd && touchOrbEnd) {

		// orbEdge is right of statEdge
		if (orbEdge.getStartPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) < 0) {

			startAngle = stationaryAngle - PI;
			endAngle = orbitingAngle;

			while (endAngle < startAngle)
				startAngle -= 2 * PI;
			while (endAngle - 2 * PI >= startAngle)
				startAngle += 2 * PI;
		}
		// orbEdge is left of statEdge
		else {
			startAngle = orbitingAngle;
			endAngle = stationaryAngle + PI;

			while (endAngle < startAngle)
				startAngle -= 2 * PI;
			while (endAngle - 2 * PI >= startAngle)
				startAngle += 2 * PI;
		}
		return;
	}

	// situation 6
	if (touchStatEnd && touchOrbStart) {

		// orbEdge is right of statEdge
		if (orbEdge.getEndPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) < 0) {

			startAngle = stationaryAngle - PI;
			endAngle = orbitingAngle + PI;

			while (endAngle < startAngle)
				startAngle -= 2 * PI;
			while (endAngle - 2 * PI >= startAngle)
				startAngle += 2 * PI;
		}
		// orbEdge is left of statEdge
		else {
			startAngle = orbitingAngle - PI;
			endAngle = stationaryAngle + PI;

			while (endAngle < startAngle)
				startAngle -= 2 * PI;
			while (endAngle - 2 * PI >= startAngle)
				startAngle += 2 * PI;
		}
		return;
	}
}

bool TouchingEdgePair::isFeasibleVector(Vector& vector) {

	// test all possible ranges
	double vectorAngle = vector.getVectorAngle();

	if (startAngle <= vectorAngle && vectorAngle <= endAngle)
		return true;

	double rotatedVectorAngle = vectorAngle + 2 * PI;
	if (startAngle <= rotatedVectorAngle && rotatedVectorAngle <= endAngle)
		return true;

	double negativeRotatedVectorAngle = vectorAngle - 2 * PI;
	if (startAngle <= negativeRotatedVectorAngle && negativeRotatedVectorAngle <= endAngle)
		return true;

	return false;
}

bool TouchingEdgePair::isFeasibleVectorWithRounding(Vector& vector) {

	// test all possible ranges
	double vectorAngle = vector.getVectorAngle();

	if (startAngle - angleRound <= vectorAngle && vectorAngle <= endAngle + angleRound)
		return true;

	double rotatedVectorAngle = vectorAngle + 2 * PI;
	if (startAngle - angleRound <= rotatedVectorAngle && rotatedVectorAngle <= endAngle + angleRound)
		return true;

	double negativeRotatedVectorAngle = vectorAngle - 2 * PI;
	if (startAngle - angleRound <= negativeRotatedVectorAngle && negativeRotatedVectorAngle <= endAngle + angleRound)
		return true;

	return false;
}

