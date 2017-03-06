#pragma once

#include <vector>

#include "Coordinate.h"
#include "MultiPolygon.h"

class NoFitPolygon {

	//a list of the polygons that are contained in the no-fit polygon
	std::vector<std::vector<Coordinate>>nfpPolygonsList;

	//this gives the polygon that is currently being created
	std::vector<Coordinate> activeList;

	MultiPolygon stationaryPolygon;
	MultiPolygon orbitingPolygon;

	NoFitPolygon(Coordinate& coordinate, MultiPolygon& stat, MultiPolygon& orb);

	NoFitPolygon(NoFitPolygon& nfp);

	std::vector<std::vector<Coordinate>> getNfpPolygonsList();

	void setNfpPolygonsList(std::vector<std::vector<Coordinate>>& nfpPolygonsList);

	std::vector<Coordinate> getActiveList();

	void setActiveList(std::vector<Coordinate>& activeList);

	MultiPolygon getStationaryPolygon();

	void setStationaryPolygon(MultiPolygon& stationaryPolygon);

	MultiPolygon getOrbitingPolygon();

	void setOrbitingPolygon(MultiPolygon& orbitingPolygon);

	void addTranslation(Coordinate& coord);

	void startNewActiveList(Coordinate& coord);

	void removeExcessivePoints();

	bool containsPoint(Coordinate& coordinate);

	void removeLastDoubleCoordinate();
};
