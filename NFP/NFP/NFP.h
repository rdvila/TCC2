#pragma once

#include <vector>

struct Point;
struct Edge;
struct Polygon;
struct Vector;


Point FindPointOfMinY(const Polygon& P) {
	return{ 0,0 };
}


Point FindPointOfMaxY(const Polygon& P) {
	return{ 0,0 };
}

Point InitialFeasiblePosition(const Point ymin, const Point ymax) {
	return{};
}

Polygon TranslateTo(const Polygon& P, Edge l) {
	return{};
}

std::vector<Toucher> FindTouchers(const Polygon& A, const Polygon& B) {
	return{};
}

std::vector<Toucher> CanMove(const Polygon& A, const Polygon& B, const std::vector<Toucher>& toucherStructures) {
	return{};
}

std::vector<Toucher> Trim(const std::vector<Toucher>& feasibleTouchers, const Polygon& A, const Polygon& B) {
	return{};
}

std::vector<Toucher> Sort(const std::vector<Toucher>& trimmedTouchers) {
	return{};
}

std::vector<Toucher> SortbyTranslation(const std::vector<Toucher>& trimmedTouchers) {
	return{};
}

void MarkEdge(Polygon &P, int StaticEdgeID) {

}

Toucher MakeToucher(Point p) {
	return{};
}

Toucher Trim(Toucher &currentToucher, Polygon &A, Polygon &B) {
	return{};
}

bool FindNextStartPoint(Polygon &A, Polygon &B, Point &nextStartPoint, Point &PolygonB_RefPoint) {

	int A_EdgeCount = A.EdgeCount;
	int B_EdgeCount = B.EdgeCount;
	Edge staticEdge;
	Edge movingEdge;

	for (int i = 0; i < A_EdgeCount; i++)
	{
		if (A.IsEdgeMarked(i)) {
			continue;
		}
		else {
			staticEdge = A.GetEdge(i);
		}

		for (int j = 0; j < B_EdgeCount; j++)
		{
			movingEdge = B.GetEdge(j);

			// translate the PolygonB so that movingEdge start is on the start of the static edge
			B = TranslateTo(B, movingEdge.Start - staticEdge.Start);
			bool bFinishedEdge = false;
			bool bIntersects = B.IntersectsWith(A);


			while (bIntersects && !bFinishedEdge)
			{
				// Edge slide until not intersecting or end of staticEdge reached
				Toucher currentToucher = MakeToucher(staticEdge.Start);
				Toucher trimmedToucher = Trim(currentToucher, A, B);

				B = TranslateTo(B, trimmedToucher.Translation);
				bIntersects = PolygonB.IntersectsWith(PolygonA);
				if (bIntersects)
				{
					If(movingEdge.Start == staticEdge.End)
						bFinishedEdge = true;
				}
			}
		}

	}

}

Polygon Complete(std::vector<Edge> nfpEdges) {
	return{};
}

struct Point {

	float x;
	float y;

	Point() : x(0.0f), y(0.0f)
	{}

	Point(float x, float y) : x(x), y(y)
	{}

	bool operator==(Point& other) {
		return (x == other.x && y == other.y);
	}

	Point operator-(Point& other) {
		return{ x - other.x,  y - other.y };
	}
};

struct Polygon {

	std::vector<Point> points;
	std::vector<Edge>  edges;

	int EdgeCount;

	Polygon()
	{}

	Polygon(std::initializer_list<Point> values) {
		points = values;
	}

	bool IsEdgeMarked(int index) {
		return edges[index].Marked;
	}

	Edge GetEdge(int index) {
		return edges[index];
	}

	bool IntersectsWith(Polygon &P) {
		return false;
	}
};

struct Edge {
	Point Start;
	Point End;
	bool Marked;

	Edge()
	{}

	Edge(Point start, Point end)
		: Start(start), End(end)
	{}
};

struct Toucher {
	Polygon polygon;

	Edge Translation;
	int  StaticEdgeID;

	Toucher() {
	}
};


Polygon NFP(const Polygon& polyA, const Polygon& polyB) {

	auto ptA_ymin = FindPointOfMinY(polyA);
	auto ptB_ymax = FindPointOfMaxY(polyB);

	auto IFP = InitialFeasiblePosition(ptA_ymin, ptB_ymax);

	bool bStartPointAvailable = true;

	Point NFPLoopStartRefPoint;
	Point PolygonB_RefPoint;

	std::vector<Edge> nfpEdges;
		
	// translate polygons to IFP
	auto A = TranslateTo(polyA, IFP);
	auto B = TranslateTo(polyB, IFP);

	auto NFPLoopStartRefPoint = FindPointOfMaxY(B);
	auto PolygonB_RefPoint = NFPLoopStartRefPoint;


	while (bStartPointAvailable) {
		bStartPointAvailable = false;

		// find touching points & segments touching those points, generate touching structures
		std::vector<Toucher> toucherStructures = FindTouchers(A, B);

		// Eliminate non-feasible touchers, ones that cause immediate intersection
		std::vector<Toucher> feasibleTouchers = CanMove(A, B, toucherStructures);

		// Trim feasible translations against polygon A and B
		std::vector<Toucher> trimmedTouchers = Trim(feasibleTouchers, A, B);

		// Sort trimmed translations by length
		std::vector<Toucher> lengthSortedTouchers = Sort(trimmedTouchers);

		std::vector<Toucher> lengthSortedTranslations = SortbyTranslation(trimmedTouchers);

		B = TranslateTo(B, lengthSortedTouchers[0].Translation.End);

		nfpEdges.push_back(lengthSortedTranslations[0].Translation);

		MarkEdge(A, lengthSortedTouchers[0].StaticEdgeID);


		if (NFPLoopStartRefPoint == PolygonB_RefPoint) {

			Point nextStartPoint;
			// find next feasible start point – reset PolygonB_RefPoint to relevant point
			bStartPointAvailable = FindNextStartPoint(A, B, nextStartPoint, PolygonB_RefPoint);
			if (bStartPointAvailable)
			{
				// Translate polygon B to nextStartPoint
				B = TranslateTo(B, PolygonB_RefPoint - nextStartPoint);
				NFPLoopStartRefPoint = nextStartPoint;
			}
			else
			{
				bStartPointAvailable = true; // allow edge traversal to continue
			}
		}

	}

	auto NFP_AB = Complete(nfpEdges);

	return NFP_AB;
}

