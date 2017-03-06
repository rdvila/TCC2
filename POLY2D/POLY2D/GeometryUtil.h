#pragma once

/*!
* General purpose geometry functions for polygon/Bezier calculations
* Copyright 2015 Jack Qiao
* Licensed under the MIT license
* port to c++
*/

#include <cmath>
#include <algorithm>
#include <list>
#include <stdexcept>

const double PI = 3.14159265;
const double TOL = std::pow(10, -9); // doubleing polong error is likely to be above 1 epsilon

template<typename T> struct __javascript_nullable {
	T value;
	bool is_empty_value = true;

	__javascript_nullable() { this->is_empty_value = true; }

	__javascript_nullable(T value) : value(value) {
		this->is_empty_value = false;
	}

	inline T& getValue() {
		if (this->is_empty_value) {
			throw std::exception("empty value access.");
		}
		return this->value;
	}

	inline T& getValueOrDefault(T defaultValue) {
		T __return = defaultValue;
		if (!this->is_empty_value) {
			__return = this->value;
		}
		return __return;
	}

	inline T& operator*() {
		return this->getValue();
	}
	inline bool has_value() { return this->is_empty_value; }
};


template<typename T>
struct __type_javascript_array {

	std::list<T> __array;

	void construct(std::initializer_list<T> values) {
		for (T value : values) {
			__array.push_back(value);
		}
	}

	T& operator[](size_t index) {
		return get(index);
	}

	long length() {
		return __array.size();
	}

	T shift() {
		T front = __array.back();
		__array.pop_back();
		return front;
	}

	//TOdo push, unshift
	long unshift(T values...) {
		__array.push_back(values);
		return __array.size();
	}

	T& get(size_t index) {
		return *std::next(__array.begin(), index);
	}

	void push(T values) {
		__array.push_back(values);
	}

	__type_javascript_array<T> slice(long start = 0, long end = -1) {
		long oldlen = __array.size();
		long newlen = 0;

		if (end == -1 || end >= oldlen) {
			newlen = oldlen - start;
		}
		else {
			newlen = end - start;
		}

		__type_javascript_array<T> nv;

		for (long i = 0; i < newlen; i++) {
			T value = get(start + i);
			nv.__array.push_back(value);
		}

		return nv;
	}

	//from http://stackoverflow.com/questions/17179988/similar-splice-in-c-like-the-splice-in-javascript
	__type_javascript_array<T> splice(long start, long howmuch, std::initializer_list<T> ar = {}) {
		auto result = slice(start, start + howmuch);

		auto erase_start = std::begin(__array); std::advance(erase_start, start);
		auto erase_end = std::begin(__array);   std::advance(erase_end, start + howmuch);
		__array.erase(erase_start, erase_end);

		auto insert_start = std::begin(__array); std::advance(insert_start, start);
		__array.insert(insert_start, std::begin(ar), std::end(ar));

		return result;

	}
};

struct vector2f {

	double x;
	double y;

	bool marked = false;

	vector2f(double x, double y) : x(x), y(y)
	{}

	vector2f() : x(0), y(0)
	{}

	inline bool operator==(vector2f& rhs) { return (x == rhs.x) && (y == rhs.y); }
	inline bool operator!=(vector2f& rhs) { return (x != rhs.x) || (y != rhs.y); }
};

struct polygon2d : __type_javascript_array<vector2f> {

	static polygon2d from(__type_javascript_array<vector2f> __array) {
		polygon2d polygon;
		polygon.__array = __array.__array;
		return polygon;
	}

	double offsetx = 0;
	double offsety = 0;

};



class GeometryUtil {
public:
	static bool _almostEqual(double a, double b, double tolerance = TOL) {
		return std::abs(a - b) < tolerance;
	}

	// returns true if points are within the given distance
	static bool _withinDistance(vector2f p1, vector2f p2, double distance) {
		double dx = p1.x - p2.x;
		double dy = p1.y - p2.y;
		return ((dx*dx + dy*dy) < distance*distance);
	}

	static double _degreesToRadians(long angle) {
		return angle*(PI / 180);
	}

	static double _radiansToDegrees(double angle) {
		return angle*(180 / PI);
	}

	// normalize vector into a unit vector
	static vector2f _normalizeVector(vector2f v) {
		if (_almostEqual(v.x*v.x + v.y*v.y, 1.0f)) {
			return v; // given vector was already a unit vector
		}
		double len = std::sqrt(v.x*v.x + v.y*v.y);
		double inverse = 1 / len;

		return vector2f{
			v.x*inverse,
			v.y*inverse
		};
	}

	// returns true if p lies on the line segment defined by AB, but not at any endpoints
	// may need work!
	static bool _onSegment(vector2f A, vector2f B, vector2f p) {

		// vertical line
		if (_almostEqual(A.x, B.x) && _almostEqual(p.x, A.x)) {
			if (!_almostEqual(p.y, B.y) && !_almostEqual(p.y, A.y) && p.y < std::max(B.y, A.y) && p.y > std::min(B.y, A.y)) {
				return true;
			}
			else {
				return false;
			}
		}

		// horizontal line
		if (_almostEqual(A.y, B.y) && _almostEqual(p.y, A.y)) {
			if (!_almostEqual(p.x, B.x) && !_almostEqual(p.x, A.x) && p.x < std::max(B.x, A.x) && p.x > std::min(B.x, A.x)) {
				return true;
			}
			else {
				return false;
			}
		}

		//range check
		if ((p.x < A.x && p.x < B.x) || (p.x > A.x && p.x > B.x) || (p.y < A.y && p.y < B.y) || (p.y > A.y && p.y > B.y)) {
			return false;
		}


		// exclude end points
		if ((_almostEqual(p.x, A.x) && _almostEqual(p.y, A.y)) || (_almostEqual(p.x, B.x) && _almostEqual(p.y, B.y))) {
			return false;
		}

		auto cross = (p.y - A.y) * (B.x - A.x) - (p.x - A.x) * (B.y - A.y);

		if (std::abs(cross) > TOL) {
			return false;
		}

		auto dot = (p.x - A.x) * (B.x - A.x) + (p.y - A.y)*(B.y - A.y);



		if (dot < 0 || _almostEqual(dot, 0)) {
			return false;
		}

		auto len2 = (B.x - A.x)*(B.x - A.x) + (B.y - A.y)*(B.y - A.y);



		if (dot > len2 || _almostEqual(dot, len2)) {
			return false;
		}

		return true;
	}


	// returns the intersection of AB and EF
	// or null if there are no intersections or other numerical error
	// if the infinite flag is set, AE and EF describe infinite lines without endpoints, they are finite line segments otherwise
	static bool _hasLineIntersect(vector2f A, vector2f B, vector2f E, vector2f F) {
		double a1, a2, b1, b2, c1, c2, x, y;

		a1 = B.y - A.y;
		b1 = A.x - B.x;
		c1 = B.x*A.y - A.x*B.y;
		a2 = F.y - E.y;
		b2 = E.x - F.x;
		c2 = F.x*E.y - E.x*F.y;

		auto denom = a1*b2 - a2*b1;

		x = (b1*c2 - b2*c1) / denom;
		y = (a2*c1 - a1*c2) / denom;

		// TODO verifica o isFInite, os valores que passam aqui
		//if (!isFinite(x) || !isFinite(y)) {
		//	return null;
		//}

		// lines are colinear
		/*var crossABE = (E.y - A.y) * (B.x - A.x) - (E.x - A.x) * (B.y - A.y);
		var crossABF = (F.y - A.y) * (B.x - A.x) - (F.x - A.x) * (B.y - A.y);
		if(_almostEqual(crossABE,0) && _almostEqual(crossABF,0)){
		return null;
		}*/


		// coincident points do not count as intersecting



		return true; // { x, y };
	}
	static __javascript_nullable<vector2f>
		_lineIntersect(vector2f A, vector2f B, vector2f E, vector2f F, bool infinite = false) {
		double a1, a2, b1, b2, c1, c2, x, y;

		a1 = B.y - A.y;
		b1 = A.x - B.x;
		c1 = B.x*A.y - A.x*B.y;
		a2 = F.y - E.y;
		b2 = E.x - F.x;
		c2 = F.x*E.y - E.x*F.y;

		auto denom = a1*b2 - a2*b1;

		x = (b1*c2 - b2*c1) / denom;
		y = (a2*c1 - a1*c2) / denom;


		if (!infinite) {
			if (std::abs(A.x - B.x) > TOL && ((A.x < B.x) ? x < A.x || x > B.x : x > A.x || x < B.x)) return{};
			if (std::abs(A.y - B.y) > TOL && ((A.y < B.y) ? y < A.y || y > B.y : y > A.y || y < B.y)) return{};

			if (std::abs(E.x - F.x) > TOL && ((E.x < F.x) ? x < E.x || x > F.x : x > E.x || x < F.x)) return{};
			if (std::abs(E.y - F.y) > TOL && ((E.y < F.y) ? y < E.y || y > F.y : y > E.y || y < F.y)) return{};
		}

		return{ vector2f({ x, y }) };
	}

};


////////////////////////////////////////////////// QuadraticBezier
struct __type_beziers_to_divide_QuadraticBezier {
	vector2f p1;
	vector2f p2;
	vector2f c1;
};


// Bezier algos from http://algorithmist.net/docs/subdivision.pdf
class QuadraticBezier {
public:

	// Roger Willcocks bezier flatness criterion
	bool isFlat(vector2f p1, vector2f p2, vector2f c1, double tol) {
		tol = 4 * tol*tol;

		double ux = 2 * c1.x - p1.x - p2.x;
		ux *= ux;

		double uy = 2 * c1.y - p1.y - p2.y;
		uy *= uy;

		return (ux + uy <= tol);
	}

	__type_javascript_array<vector2f>
		linearize(vector2f p1, vector2f p2, vector2f c1, double tol) {

		__type_javascript_array<vector2f> finished;
		finished.construct({ p1 });// list of points to return

		__type_javascript_array<__type_beziers_to_divide_QuadraticBezier> todo;
		todo.construct({ {p1, p2, c1} }); // list of Beziers to divide

										// recursion could stack overflow, loop instead
		while (todo.length() > 0) {
			auto segment = todo.get(0);

			if (isFlat(segment.p1, segment.p2, segment.c1, tol)) { // reached subdivision limit
				finished.push({ segment.p2.x, segment.p2.y });
				todo.shift();
			}
			else {
				auto divided = subdivide(segment.p1, segment.p2, segment.c1, 0.5);
				todo.splice(0, 1, { divided.get(0), divided.get(1) });
			}
		}
		return finished;
	}

	// subdivide a single Bezier
	// t is the percent along the Bezier to divide at. eg. 0.5
	__type_javascript_array<__type_beziers_to_divide_QuadraticBezier>
		subdivide(vector2f p1, vector2f p2, vector2f c1, double t) {
		vector2f mid1 = {
			p1.x + (c1.x - p1.x)*t,
			p1.y + (c1.y - p1.y)*t
		};

		vector2f mid2 = {
			c1.x + (p2.x - c1.x)*t,
			c1.y + (p2.y - c1.y)*t
		};

		vector2f mid3 = {
			mid1.x + (mid2.x - mid1.x)*t,
			mid1.y + (mid2.y - mid1.y)*t
		};

		__type_beziers_to_divide_QuadraticBezier
			seg1 = { p1,   mid3, mid1 };
		__type_beziers_to_divide_QuadraticBezier
			seg2 = { mid3, p2,   mid2 };

		__type_javascript_array<__type_beziers_to_divide_QuadraticBezier>
			__return;

		__return.construct({ seg1, seg2 });

		return __return;

	}
};
////////////////////////////////////////////////// CubicBezier
struct __type_beziers_to_divide_CubicBezier {
	vector2f p1;
	vector2f p2;
	vector2f c1;
	vector2f c2;
};

class CubicBezier {

	bool isFlat(vector2f p1, vector2f p2, vector2f c1, vector2f c2, double tol) {
		tol = 16 * tol*tol;

		double ux = 3 * c1.x - 2 * p1.x - p2.x;
		ux *= ux;

		double uy = 3 * c1.y - 2 * p1.y - p2.y;
		uy *= uy;

		double vx = 3 * c2.x - 2 * p2.x - p1.x;
		vx *= vx;

		double vy = 3 * c2.y - 2 * p2.y - p1.y;
		vy *= vy;

		if (ux < vx) {
			ux = vx;
		}
		if (uy < vy) {
			uy = vy;
		}

		return (ux + uy <= tol);
	}

	__type_javascript_array<vector2f>
		linearize(vector2f p1, vector2f p2, vector2f c1, vector2f c2, double tol) {

		__type_javascript_array<vector2f> finished;
		finished.construct({ p1 }); // list of points to return

		__type_javascript_array<__type_beziers_to_divide_CubicBezier> todo;
		todo.construct({ { p1, p2, c1, c2 } }); // list of Beziers to divide

											// recursion could stack overflow, loop instead

		while (todo.length() > 0) {
			auto segment = todo.get(0);

			if (isFlat(segment.p1, segment.p2, segment.c1, segment.c2, tol)) { // reached subdivision limit
				finished.push({ segment.p2.x, segment.p2.y });
				todo.shift();
			}
			else {
				auto divided = subdivide(segment.p1, segment.p2, segment.c1, segment.c2, 0.5);
				todo.splice(0, 1, { divided.get(0), divided.get(1) });
			}
		}
		return finished;
	}

	__type_javascript_array<__type_beziers_to_divide_CubicBezier>
		subdivide(vector2f p1, vector2f p2, vector2f c1, vector2f c2, double t) {
		vector2f mid1 = {
			p1.x + (c1.x - p1.x)*t,
			p1.y + (c1.y - p1.y)*t
		};

		vector2f mid2 = {
			c2.x + (p2.x - c2.x)*t,
			c2.y + (p2.y - c2.y)*t
		};

		vector2f mid3 = {
			c1.x + (c2.x - c1.x)*t,
			c1.y + (c2.y - c1.y)*t
		};

		vector2f mida = {
			mid1.x + (mid3.x - mid1.x)*t,
			mid1.y + (mid3.y - mid1.y)*t
		};

		vector2f midb = {
			mid3.x + (mid2.x - mid3.x)*t,
			mid3.y + (mid2.y - mid3.y)*t
		};

		vector2f midx = {
			mida.x + (midb.x - mida.x)*t,
			mida.y + (midb.y - mida.y)*t
		};

		__type_beziers_to_divide_CubicBezier seg1 = { p1, midx, mid1, mida };
		__type_beziers_to_divide_CubicBezier seg2 = { midx, p2, midb, mid2 };

		__type_javascript_array<__type_beziers_to_divide_CubicBezier> __return;
		__return.construct({ seg1, seg2 });

		return __return;
	}

};
////////////////////////////////////////////////// Arc
struct __type_centerToSvg_Arc {
	vector2f p1;
	vector2f p2;
	double rx;
	double ry;
	long angle;
	long largearc;
	long sweep;
};

struct __type_svgToCenter_Arc {
	vector2f center;
	double rx;
	double ry;
	long theta;
	long extent;
	long angle;
};

struct __type_getPolygonBounds_Arc {
	double x;
	double y;
	double width;
	double height;
};

class Arc {


	__type_javascript_array<vector2f>
		linearize(vector2f p1, vector2f p2, double rx, double ry, long angle, long largearc, long sweep, double tol) {

		__type_javascript_array<vector2f> finished;
		finished.construct({ p2 }); // list of points to return

		__type_svgToCenter_Arc arc = svgToCenter(p1, p2, rx, ry, angle, largearc, sweep);

		__type_javascript_array<__type_svgToCenter_Arc> todo;
		todo.construct({ arc }); // list of arcs to divide

							 // recursion could stack overflow, loop instead
		while (todo.length() > 0) {
			arc = todo.get(0);

			auto fullarc = centerToSvg(arc.center, arc.rx, arc.ry, arc.theta, arc.extent, arc.angle);
			auto subarc = centerToSvg(arc.center, arc.rx, arc.ry, arc.theta, 0.5 * arc.extent, arc.angle);
			auto arcmid = subarc.p2;

			vector2f mid{
				0.5f * (fullarc.p1.x + fullarc.p2.x),
				0.5f * (fullarc.p1.y + fullarc.p2.y)
			};

			// compare midpolong of line with midpolong of arc
			// this is not 100% accurate, but should be a good heuristic for flatness in most cases
			if (GeometryUtil::_withinDistance(mid, arcmid, tol)) {
				finished.unshift(fullarc.p2);
				todo.shift();
			}
			else {
				__type_svgToCenter_Arc arc1 = {
					arc.center,
					arc.rx,
					arc.ry,
					arc.theta,
					0.5 * arc.extent,
					arc.angle
				};
				__type_svgToCenter_Arc arc2 = {
					arc.center,
					arc.rx,
					arc.ry,
					arc.theta + 0.5 * arc.extent,
					0.5 * arc.extent,
					arc.angle
				};
				todo.splice(0, 1, { arc1, arc2 });
			}
		}
		return finished;
	}


	__type_centerToSvg_Arc
		centerToSvg(vector2f center, double rx, double ry, double theta1, double extent, long angleDegrees) {

		double theta2 = theta1 + extent;

		theta1 = GeometryUtil::_degreesToRadians(theta1);
		theta2 = GeometryUtil::_degreesToRadians(theta2);
		auto angle = GeometryUtil::_degreesToRadians(angleDegrees);

		double cos = std::cos(angle);
		double sin = std::sin(angle);

		double t1cos = std::cos(theta1);
		double t1sin = std::sin(theta1);

		double t2cos = std::cos(theta2);
		double t2sin = std::sin(theta2);

		double x0 = center.x + cos * rx * t1cos + (-sin) * ry * t1sin;
		double y0 = center.y + sin * rx * t1cos + cos * ry * t1sin;

		double x1 = center.x + cos * rx * t2cos + (-sin) * ry * t2sin;
		double y1 = center.y + sin * rx * t2cos + cos * ry * t2sin;

		long largearc = (extent > 180) ? 1 : 0;
		long sweep = (extent > 0) ? 1 : 0;


		__type_centerToSvg_Arc __return = {

			{ x0, y0 },{ x1, y1 }, rx, ry, angle, largearc, sweep
		};

		return __return;
	}

	// convert from SVG format arc to center polong arc
	__type_svgToCenter_Arc
		svgToCenter(vector2f p1, vector2f p2, double rx, double ry, long angleDegrees, long largearc, long sweep) {

		vector2f mid{
			0.5f * (p1.x + p2.x),
			0.5f * (p1.y + p2.y)
		};

		vector2f diff{
			0.5f * (p2.x - p1.x),
			0.5f * (p2.y - p1.y)
		};

		double angle = GeometryUtil::_degreesToRadians(angleDegrees % 360);

		double cos = std::cos(angle);
		double sin = std::sin(angle);

		double x1 = cos * diff.x + sin * diff.y;
		double y1 = -sin * diff.x + cos * diff.y;

		rx = std::abs(rx);
		ry = std::abs(ry);
		double Prx = rx * rx;
		double Pry = ry * ry;
		double Px1 = x1 * x1;
		double Py1 = y1 * y1;

		double radiiCheck = Px1 / Prx + Py1 / Pry;
		double radiiSqrt = std::sqrt(radiiCheck);
		if (radiiCheck > 1) {
			rx = radiiSqrt * rx;
			ry = radiiSqrt * ry;
			Prx = rx * rx;
			Pry = ry * ry;
		}

		double sign = (largearc != sweep) ? -1 : 1;
		double sq = ((Prx * Pry) - (Prx * Py1) - (Pry * Px1)) / ((Prx * Py1) + (Pry * Px1));

		sq = (sq < 0) ? 0 : sq;

		double coef = sign * std::sqrt(sq);
		double cx1 = coef * ((rx * y1) / ry);
		double cy1 = coef * -((ry * x1) / rx);

		double cx = mid.x + (cos * cx1 - sin * cy1);
		double cy = mid.y + (sin * cx1 + cos * cy1);

		double ux = (x1 - cx1) / rx;
		double uy = (y1 - cy1) / ry;
		double vx = (-x1 - cx1) / rx;
		double vy = (-y1 - cy1) / ry;
		double n = std::sqrt((ux * ux) + (uy * uy));
		double p = ux;
		sign = (uy < 0) ? -1 : 1;

		long theta = sign * std::acos(p / n);
		theta = GeometryUtil::_radiansToDegrees(theta);

		n = std::sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
		p = ux * vx + uy * vy;
		sign = ((ux * vy - uy * vx) < 0) ? -1 : 1;
		long delta = sign * std::acos(p / n);
		delta = GeometryUtil::_radiansToDegrees(delta);

		if (sweep == 1 && delta > 0)
		{
			delta -= 360;
		}
		else if (sweep == 0 && delta < 0)
		{
			delta += 360;
		}

		delta %= 360;
		theta %= 360;

		__type_svgToCenter_Arc __return = {
			{ cx, cy }, rx, ry, theta, delta, angleDegrees
		};

		return __return;
	}

	// returns the rectangular bounding box of the given polygon
	__javascript_nullable<__type_getPolygonBounds_Arc>
		getPolygonBounds(polygon2d polygon) {

		if (polygon.length() < 3) {
			return{};
		}

		double xmin = polygon.get(0).x;
		double xmax = polygon.get(0).x;
		double ymin = polygon.get(0).y;
		double ymax = polygon.get(0).y;

		for (long i = 1; i < polygon.length(); i++) {
			if (polygon.get(i).x > xmax) {
				xmax = polygon.get(i).x;
			}
			else if (polygon.get(i).x < xmin) {
				xmin = polygon.get(i).x;
			}

			if (polygon.get(i).y > ymax) {
				ymax = polygon.get(i).y;
			}
			else if (polygon.get(i).y < ymin) {
				ymin = polygon.get(i).y;
			}
		}

		__type_getPolygonBounds_Arc __return{
			xmin,
			ymin,
			xmax - xmin,
			ymax - ymin
		};

		return{ __return };
	}


	// return true if polong is in the polygon, false if outside, and null if exactly on a polong or edge
	__javascript_nullable<bool>
		pointInPolygon(vector2f point, polygon2d polygon) {
		if (polygon.length() < 3) {
			return{};
		}

		bool inside = false;
		double offsetx = polygon.offsetx;
		double offsety = polygon.offsety;

		for (long i = 0, j = polygon.length() - 1; i < polygon.length(); j = i++) {
			double xi = polygon.get(i).x + offsetx;
			double yi = polygon.get(i).y + offsety;
			double xj = polygon.get(j).x + offsetx;
			double yj = polygon.get(j).y + offsety;

			if (GeometryUtil::_almostEqual(xi, point.x) && GeometryUtil::_almostEqual(yi, point.y)) {
				return{}; // no result
			}

			if (GeometryUtil::_onSegment({ xi, yi }, { xj, yj }, point)) {
				return{}; // exactly on the segment
			}

			if (GeometryUtil::_almostEqual(xi, xj) && GeometryUtil::_almostEqual(yi, yj)) { // ignore very small lines
				continue;
			}

			bool intersect = ((yi > point.y) != (yj > point.y)) && (point.x < (xj - xi) * (point.y - yi) / (yj - yi) + xi);
			if (intersect) inside = !inside;
		}

		return{ inside };
	}

	// returns the area of the polygon, assuming no self-intersections
	// a negative area indicates counter-clockwise winding direction
	double polygonArea(polygon2d polygon) {
		double area = 0;
		long i, j;

		for (i = 0, j = polygon.length() - 1; i < polygon.length(); j = i++) {
			area += (polygon[j].x + polygon[i].x) * (polygon[j].y - polygon[i].y);
		}

		return 0.5 * area;
	}

	// todo: swap this for a more efficient sweep-line implementation
	// returnEdges: if set, return all edges on A that have intersections

	bool intersect(polygon2d polyA, polygon2d polyB) {
		double Aoffsetx = polyA.offsetx;
		double Aoffsety = polyA.offsety;

		double Boffsetx = polyB.offsetx;
		double Boffsety = polyB.offsety;

		auto A = polyA.slice(0);
		auto B = polyB.slice(0);

		for (long i = 0; i < A.length() - 1; i++) {
			for (long j = 0; j < B.length() - 1; j++) {
				vector2f a1 = { A[i].x + Aoffsetx, A[i].y + Aoffsety };
				vector2f a2 = { A[i + 1].x + Aoffsetx, A[i + 1].y + Aoffsety };
				vector2f b1 = { B[j].x + Boffsetx, B[j].y + Boffsety };
				vector2f b2 = { B[j + 1].x + Boffsetx, B[j + 1].y + Boffsety };

				size_t prevbindex = (j == 0) ? B.length() - 1 : j - 1;
				size_t prevaindex = (i == 0) ? A.length() - 1 : i - 1;
				size_t nextbindex = (j + 1 == B.length() - 1) ? 0 : j + 2;
				size_t nextaindex = (i + 1 == A.length() - 1) ? 0 : i + 2;

				// go even further back if we happen to hit on a loop end point
				if (B[prevbindex] == B[j] || (GeometryUtil::_almostEqual(B[prevbindex].x, B[j].x) && GeometryUtil::_almostEqual(B[prevbindex].y, B[j].y))) {
					prevbindex = (prevbindex == 0) ? B.length() - 1 : prevbindex - 1;
				}

				if (A[prevaindex] == A[i] || (GeometryUtil::_almostEqual(A[prevaindex].x, A[i].x) && GeometryUtil::_almostEqual(A[prevaindex].y, A[i].y))) {
					prevaindex = (prevaindex == 0) ? A.length() - 1 : prevaindex - 1;
				}

				// go even further forward if we happen to hit on a loop end point
				if (B[nextbindex] == B[j + 1] || (GeometryUtil::_almostEqual(B[nextbindex].x, B[j + 1].x) && GeometryUtil::_almostEqual(B[nextbindex].y, B[j + 1].y))) {
					nextbindex = (nextbindex == B.length() - 1) ? 0 : nextbindex + 1;
				}

				if (A[nextaindex] == A[i + 1] || (GeometryUtil::_almostEqual(A[nextaindex].x, A[i + 1].x) && GeometryUtil::_almostEqual(A[nextaindex].y, A[i + 1].y))) {
					nextaindex = (nextaindex == A.length() - 1) ? 0 : nextaindex + 1;
				}

				vector2f a0 = { A[prevaindex].x + Aoffsetx, A[prevaindex].y + Aoffsety };
				vector2f b0 = { B[prevbindex].x + Boffsetx, B[prevbindex].y + Boffsety };

				vector2f a3 = { A[nextaindex].x + Aoffsetx, A[nextaindex].y + Aoffsety };
				vector2f b3 = { B[nextbindex].x + Boffsetx, B[nextbindex].y + Boffsety };

				if (GeometryUtil::_onSegment(a1, a2, b1) || (GeometryUtil::_almostEqual(a1.x, b1.x) && GeometryUtil::_almostEqual(a1.y, b1.y))) {
					// if a polong is on a segment, it could intersect or it could not. Check via the neighboring points
					auto b0in = pointInPolygon(b0, polygon2d::from(A));
					auto b2in = pointInPolygon(b2, polygon2d::from(A));
					if ((*b0in == true && *b2in == false) || (*b0in == false && *b2in == true)) {
						return true;
					}
					else {
						continue;
					}
				}

				if (GeometryUtil::_onSegment(a1, a2, b2) || (GeometryUtil::_almostEqual(a2.x, b2.x) && GeometryUtil::_almostEqual(a2.y, b2.y))) {
					// if a polong is on a segment, it could intersect or it could not. Check via the neighboring points
					auto b1in = pointInPolygon(b1, polygon2d::from(A));
					auto b3in = pointInPolygon(b3, polygon2d::from(A));

					if ((*b1in == true && *b3in == false) || (*b1in == false && *b3in == true)) {
						return true;
					}
					else {
						continue;
					}
				}

				if (GeometryUtil::_onSegment(b1, b2, a1) || (GeometryUtil::_almostEqual(a1.x, b2.x) && GeometryUtil::_almostEqual(a1.y, b2.y))) {
					// if a polong is on a segment, it could intersect or it could not. Check via the neighboring points
					auto a0in = pointInPolygon(a0, polygon2d::from(B));
					auto a2in = pointInPolygon(a2, polygon2d::from(B));

					if ((*a0in == true && *a2in == false) || (*a0in == false && *a2in == true)) {
						return true;
					}
					else {
						continue;
					}
				}

				if (GeometryUtil::_onSegment(b1, b2, a2) || (GeometryUtil::_almostEqual(a2.x, b1.x) && GeometryUtil::_almostEqual(a2.y, b1.y))) {
					// if a polong is on a segment, it could intersect or it could not. Check via the neighboring points
					auto a1in = pointInPolygon(a1, polygon2d::from(B));
					auto a3in = pointInPolygon(a3, polygon2d::from(B));

					if ((*a1in == true && *a3in == false) || (*a1in == false && *a3in == true)) {
						return true;
					}
					else {
						continue;
					}
				}

				auto   __return = GeometryUtil::_lineIntersect(b1, b2, a1, a2);
				return __return.has_value();

			}
		}

		return false;
	}

	// placement algos as outlined in [1] http://www.cs.stir.ac.uk/~goc/papers/EffectiveHueristic2DAOR2013.pdf

	// returns a continuous polyline representing the normal-most edge of the given polygon
	// eg. a normal vector of [-1, 0] will return the left-most edge of the polygon
	// this is essentially algo 8 in [1], generalized for any vector direction
	__javascript_nullable<__type_javascript_array<vector2f>>
		polygonEdge(polygon2d polygon, vector2f normal) {

		if (polygon.length() < 3) {
			return{};
		}

		normal = GeometryUtil::_normalizeVector(normal);

		vector2f direction = {
			-normal.y,
			normal.x
		};

		// find the max and min points, they will be the endpoints of our edge
		__javascript_nullable<double> min;
		__javascript_nullable<double> max;

		__type_javascript_array<double> dotproduct;

		for (long i = 0; i < polygon.length(); i++) {
			double dot = polygon[i].x*direction.x + polygon[i].y*direction.y;
			dotproduct.push(dot);
			if (!min.has_value() || dot < *min) {
				min = __javascript_nullable<double>(dot);
			}
			if (!max.has_value() || dot > *max) {
				max = __javascript_nullable<double>(dot);
			}
		}

		// there may be multiple vertices with min/max values. In which case we choose the one that is normal-most (eg. left most)
		long indexmin = 0;
		long indexmax = 0;

		__javascript_nullable<double> normalmin;
		__javascript_nullable<double> normalmax;

		for (long i = 0; i < polygon.length(); i++) {
			if (min.has_value() && GeometryUtil::_almostEqual(dotproduct[i], *min)) {
				double dot = polygon[i].x*normal.x + polygon[i].y*normal.y;
				if (!normalmin.has_value() || dot > *normalmin) {
					normalmin = __javascript_nullable<double>(dot);
					indexmin = i;
				}
			}
			else if (max.has_value() && GeometryUtil::_almostEqual(dotproduct[i], *max)) {
				double dot = polygon[i].x*normal.x + polygon[i].y*normal.y;
				if (!normalmax.has_value() || dot > *normalmax) {
					normalmax = __javascript_nullable<double>(dot);
					indexmax = i;
				}
			}
		}

		// now we have two edges bound by min and max points, figure out which edge faces our direction vector

		long indexleft = indexmin - 1;
		long indexright = indexmin + 1;

		if (indexleft < 0) {
			indexleft = polygon.length() - 1;
		}
		if (indexright >= polygon.length()) {
			indexright = 0;
		}

		vector2f minvertex = polygon[indexmin];
		vector2f left = polygon[indexleft];
		vector2f right = polygon[indexright];

		vector2f leftvector = {
			left.x - minvertex.x,
			left.y - minvertex.y
		};

		vector2f rightvector = {
			right.x - minvertex.x,
			right.y - minvertex.y
		};

		double dotleft = leftvector.x*direction.x + leftvector.y*direction.y;
		double dotright = rightvector.x*direction.x + rightvector.y*direction.y;

		// -1 = left, 1 = right
		long scandirection = -1;

		if (GeometryUtil::_almostEqual(dotleft, 0)) {
			scandirection = 1;
		}
		else if (GeometryUtil::_almostEqual(dotright, 0)) {
			scandirection = -1;
		}
		else {
			double normaldotleft;
			double normaldotright;

			if (GeometryUtil::_almostEqual(dotleft, dotright)) {
				// the points line up exactly along the normal vector
				normaldotleft = leftvector.x*normal.x + leftvector.y*normal.y;
				normaldotright = rightvector.x*normal.x + rightvector.y*normal.y;
			}
			else if (dotleft < dotright) {
				// normalize right vertex so normal projection can be directly compared
				normaldotleft = leftvector.x*normal.x + leftvector.y*normal.y;
				normaldotright = (rightvector.x*normal.x + rightvector.y*normal.y)*(dotleft / dotright);
			}
			else {
				// normalize left vertex so normal projection can be directly compared
				normaldotleft = leftvector.x*normal.x + leftvector.y*normal.y * (dotright / dotleft);
				normaldotright = rightvector.x*normal.x + rightvector.y*normal.y;
			}

			if (normaldotleft > normaldotright) {
				scandirection = -1;
			}
			else {
				// technically they could be equal, (ie. the segments bound by left and right points are incident)
				// in which case we'll have to climb up the chain until lines are no longer incident
				// for now we'll just not handle it and assume people aren't giving us garbage input..
				scandirection = 1;
			}
		}

		// connect all points between indexmin and indexmax along the scan direction
		auto edge = __type_javascript_array<vector2f>();
		long count = 0;
		long i = indexmin;
		while (count < polygon.length()) {
			if (i >= polygon.length()) {
				i = 0;
			}
			else if (i < 0) {
				i = polygon.length() - 1;
			}

			edge.push(polygon[i]);

			if (i == indexmax) {
				break;
			}
			i += scandirection;
			count++;
		}

		return edge;
	}

	// returns the normal distance from p to a line segment defined by s1 s2
	// this is basically algo 9 in [1], generalized for any vector direction
	// eg. normal of [-1, 0] returns the horizontal distance between the polong and the line segment
	// sxinclusive: if true, include endpoints instead of excluding them

	__javascript_nullable<double>
		pointLineDistance(vector2f p, vector2f s1, vector2f s2, vector2f normal, bool s1inclusive, bool s2inclusive) {
		normal = GeometryUtil::_normalizeVector(normal);

		vector2f dir = {
			normal.y,
			-normal.x
		};

		double pdot = p.x*dir.x + p.y*dir.y;
		double s1dot = s1.x*dir.x + s1.y*dir.y;
		double s2dot = s2.x*dir.x + s2.y*dir.y;

		double pdotnorm = p.x*normal.x + p.y*normal.y;
		double s1dotnorm = s1.x*normal.x + s1.y*normal.y;
		double s2dotnorm = s2.x*normal.x + s2.y*normal.y;


		// polong is exactly along the edge in the normal direction
		if (GeometryUtil::_almostEqual(pdot, s1dot) && GeometryUtil::_almostEqual(pdot, s2dot)) {
			// polong lies on an endpoint
			if (GeometryUtil::_almostEqual(pdotnorm, s1dotnorm)) {
				return{};
			}

			if (GeometryUtil::_almostEqual(pdotnorm, s2dotnorm)) {
				return{};
			}

			// polong is outside both endpoints
			if (pdotnorm > s1dotnorm && pdotnorm > s2dotnorm) {
				return std::min(pdotnorm - s1dotnorm, pdotnorm - s2dotnorm);
			}
			if (pdotnorm < s1dotnorm && pdotnorm < s2dotnorm) {
				return -std::min(s1dotnorm - pdotnorm, s2dotnorm - pdotnorm);
			}

			// polong lies between endpoints
			double diff1 = pdotnorm - s1dotnorm;
			double diff2 = pdotnorm - s2dotnorm;
			if (diff1 > 0) {
				return diff1;
			}
			else {
				return diff2;
			}
		}
		// polong 
		else if (GeometryUtil::_almostEqual(pdot, s1dot)) {
			if (s1inclusive) {
				return pdotnorm - s1dotnorm;
			}
			else {
				return{};
			}
		}
		else if (GeometryUtil::_almostEqual(pdot, s2dot)) {
			if (s2inclusive) {
				return pdotnorm - s2dotnorm;
			}
			else {
				return{};
			}
		}
		else if ((pdot < s1dot && pdot < s2dot) || (pdot > s1dot && pdot > s2dot)) {
			return{}; // polong doesn't collide with segment
		}

		return (pdotnorm - s1dotnorm + (s1dotnorm - s2dotnorm)*(s1dot - pdot) / (s1dot - s2dot));
	}

	__javascript_nullable<double>
		pointDistance(vector2f p, vector2f s1, vector2f s2, vector2f normal, bool infinite) {
		normal = GeometryUtil::_normalizeVector(normal);

		// TODO review, x any y inverted?
		vector2f dir = {
			normal.y,
			-normal.x
		};

		double pdot = p.x*dir.x + p.y*dir.y;
		double s1dot = s1.x*dir.x + s1.y*dir.y;
		double s2dot = s2.x*dir.x + s2.y*dir.y;

		double pdotnorm = p.x*normal.x + p.y*normal.y;
		double s1dotnorm = s1.x*normal.x + s1.y*normal.y;
		double s2dotnorm = s2.x*normal.x + s2.y*normal.y;

		if (!infinite) {
			if (((pdot < s1dot || GeometryUtil::_almostEqual(pdot, s1dot)) && (pdot < s2dot || GeometryUtil::_almostEqual(pdot, s2dot))) || ((pdot > s1dot || GeometryUtil::_almostEqual(pdot, s1dot)) && (pdot > s2dot || GeometryUtil::_almostEqual(pdot, s2dot)))) {
				return{}; // dot doesn't collide with segment, or lies directly on the vertex
			}
			if ((GeometryUtil::_almostEqual(pdot, s1dot) && GeometryUtil::_almostEqual(pdot, s2dot)) && (pdotnorm > s1dotnorm && pdotnorm > s2dotnorm)) {
				return{ std::min(pdotnorm - s1dotnorm, pdotnorm - s2dotnorm) };
			}
			if ((GeometryUtil::_almostEqual(pdot, s1dot) && GeometryUtil::_almostEqual(pdot, s2dot)) && (pdotnorm < s1dotnorm && pdotnorm < s2dotnorm)) {
				return{ -std::min(s1dotnorm - pdotnorm, s2dotnorm - pdotnorm) };
			}
		}

		return -(pdotnorm - s1dotnorm + (s1dotnorm - s2dotnorm)*(s1dot - pdot) / (s1dot - s2dot));
	}

	__javascript_nullable<double>
		segmentDistance(vector2f A, vector2f B, vector2f E, vector2f F, vector2f direction) {

		vector2f normal = {
			direction.y,
			-direction.x
		};

		vector2f reverse = {
			-direction.x,
			-direction.y
		};

		double dotA = A.x*normal.x + A.y*normal.y;
		double dotB = B.x*normal.x + B.y*normal.y;
		double dotE = E.x*normal.x + E.y*normal.y;
		double dotF = F.x*normal.x + F.y*normal.y;

		double crossA = A.x*direction.x + A.y*direction.y;
		double crossB = B.x*direction.x + B.y*direction.y;
		double crossE = E.x*direction.x + E.y*direction.y;
		double crossF = F.x*direction.x + F.y*direction.y;

		double crossABmin = std::min(crossA, crossB);
		double crossABmax = std::max(crossA, crossB);

		double crossEFmax = std::max(crossE, crossF);
		double crossEFmin = std::min(crossE, crossF);

		double ABmin = std::min(dotA, dotB);
		double ABmax = std::max(dotA, dotB);

		double EFmax = std::max(dotE, dotF);
		double EFmin = std::min(dotE, dotF);

		// segments that will merely touch at one point
		if (GeometryUtil::_almostEqual(ABmax, EFmin, TOL) || GeometryUtil::_almostEqual(ABmin, EFmax, TOL)) {
			return{};
		}
		// segments miss eachother completely
		if (ABmax < EFmin || ABmin > EFmax) {
			return{};
		}

		long overlap;

		if ((ABmax > EFmax && ABmin < EFmin) || (EFmax > ABmax && EFmin < ABmin)) {
			overlap = 1;
		}
		else {
			double minMax = std::min(ABmax, EFmax);
			double maxMin = std::max(ABmin, EFmin);

			double maxMax = std::max(ABmax, EFmax);
			double minMin = std::min(ABmin, EFmin);

			overlap = (minMax - maxMin) / (maxMax - minMin);
		}

		double crossABE = (E.y - A.y) * (B.x - A.x) - (E.x - A.x) * (B.y - A.y);
		double crossABF = (F.y - A.y) * (B.x - A.x) - (F.x - A.x) * (B.y - A.y);

		// lines are colinear
		if (GeometryUtil::_almostEqual(crossABE, 0) && GeometryUtil::_almostEqual(crossABF, 0)) {

			vector2f ABnorm = { B.y - A.y, A.x - B.x };
			vector2f EFnorm = { F.y - E.y, E.x - F.x };

			double ABnormlength = std::sqrt(ABnorm.x*ABnorm.x + ABnorm.y*ABnorm.y);
			ABnorm.x /= ABnormlength;
			ABnorm.y /= ABnormlength;

			double EFnormlength = std::sqrt(EFnorm.x*EFnorm.x + EFnorm.y*EFnorm.y);
			EFnorm.x /= EFnormlength;
			EFnorm.y /= EFnormlength;

			// segment normals must polong in opposite directions
			if (std::abs(ABnorm.y * EFnorm.x - ABnorm.x * EFnorm.y) < TOL && ABnorm.y * EFnorm.y + ABnorm.x * EFnorm.x < 0) {
				// normal of AB segment must polong in same direction as given direction vector
				double normdot = ABnorm.y * direction.y + ABnorm.x * direction.x;
				// the segments merely slide along eachother
				if (GeometryUtil::_almostEqual(normdot, 0, TOL)) {
					return{};
				}
				if (normdot < 0) {
					return 0;
				}
			}
			return{};
		}

		__type_javascript_array<double> distances;

		// coincident points
		if (GeometryUtil::_almostEqual(dotA, dotE)) {
			distances.push(crossA - crossE);
		}
		else if (GeometryUtil::_almostEqual(dotA, dotF)) {
			distances.push(crossA - crossF);
		}
		else if (dotA > EFmin && dotA < EFmax) {
			auto d = pointDistance(A, E, F, reverse, false);
			if (d.has_value() && GeometryUtil::_almostEqual(*d, 0)) { //  A currently touches EF, but AB is moving away from EF
				auto dB = pointDistance(B, E, F, reverse, true);
				if (dB.has_value() && (*dB < 0 || GeometryUtil::_almostEqual((*dB)*overlap, 0))) {
					d = {};
				}
			}
			if (d.has_value()) {
				distances.push(*d);
			}
		}

		if (GeometryUtil::_almostEqual(dotB, dotE)) {
			distances.push(crossB - crossE);
		}
		else if (GeometryUtil::_almostEqual(dotB, dotF)) {
			distances.push(crossB - crossF);
		}
		else if (dotB > EFmin && dotB < EFmax) {
			auto d = pointDistance(B, E, F, reverse, false);

			if (d.has_value() && GeometryUtil::_almostEqual(*d, 0)) { // crossA>crossB A currently touches EF, but AB is moving away from EF
				auto dA = pointDistance(A, E, F, reverse, true);
				if (dA.has_value() && (*dA < 0 || GeometryUtil::_almostEqual((*dA)*overlap, 0))) {
					d = {};
				}
			}
			if (d.has_value()) {
				distances.push(*d);
			}
		}

		if (dotE > ABmin && dotE < ABmax) {
			auto d = pointDistance(E, A, B, direction, false);
			if (d.has_value() && GeometryUtil::_almostEqual(*d, 0)) { // crossF<crossE A currently touches EF, but AB is moving away from EF
				auto dF = pointDistance(F, A, B, direction, true);
				if (dF.has_value() && (*dF < 0 || GeometryUtil::_almostEqual((*dF)*overlap, 0))) {
					d = {};
				}
			}
			if (d.has_value()) {
				distances.push(*d);
			}
		}

		if (dotF > ABmin && dotF < ABmax) {
			auto d = pointDistance(F, A, B, direction, false);
			if (d.has_value() && GeometryUtil::_almostEqual(*d, 0)) { // && crossE<crossF A currently touches EF, but AB is moving away from EF
				auto dE = pointDistance(E, A, B, direction, true);
				if (dE.has_value() && (*dE < 0 || GeometryUtil::_almostEqual((*dE)*overlap, 0))) {
					d = {};
				}
			}
			if (d.has_value()) {
				distances.push(*d);
			}
		}

		if (distances.length() == 0) {
			return{};
		}

		return{ *std::min_element(std::begin(distances.__array), std::end(distances.__array)) };
	}

	__javascript_nullable<double>
		polygonSlideDistance(polygon2d polyA, polygon2d polyB, vector2f direction, bool ignoreNegative) {

		double  Aoffsetx, Aoffsety, Boffsetx, Boffsety;

		Aoffsetx = polyA.offsetx;
		Aoffsety = polyA.offsety;

		Boffsetx = polyB.offsetx;
		Boffsety = polyB.offsety;

		auto A = polyA.slice(0);
		auto B = polyB.slice(0);

		// close the loop for polygons
		if (A[0] != A[A.length() - 1]) {
			A.push(A[0]);
		}

		if (B[0] != B[B.length() - 1]) {
			B.push(B[0]);
		}

		auto edgeA = A;
		auto edgeB = B;

		__javascript_nullable<double> distance{};

		vector2f dir = GeometryUtil::_normalizeVector(direction);

		vector2f normal = {
			dir.y,
			-dir.x
		};

		vector2f reverse = {
			-dir.x,
			-dir.y,
		};

		for (long i = 0; i < edgeB.length() - 1; i++) {
			for (long j = 0; j < edgeA.length() - 1; j++) {
				vector2f A1 = { edgeA[j].x + Aoffsetx,     edgeA[j].y + Aoffsety };
				vector2f A2 = { edgeA[j + 1].x + Aoffsetx, edgeA[j + 1].y + Aoffsety };
				vector2f B1 = { edgeB[i].x + Boffsetx,     edgeB[i].y + Boffsety };
				vector2f B2 = { edgeB[i + 1].x + Boffsetx, edgeB[i + 1].y + Boffsety };

				if ((GeometryUtil::_almostEqual(A1.x, A2.x) && GeometryUtil::_almostEqual(A1.y, A2.y)) || (GeometryUtil::_almostEqual(B1.x, B2.x) && GeometryUtil::_almostEqual(B1.y, B2.y))) {
					continue; // ignore extremely small lines
				}

				auto d = segmentDistance(A1, A2, B1, B2, dir);

				if (d.has_value() && (!distance.has_value() || *d < *distance)) {
					if (!ignoreNegative || *d > 0 || GeometryUtil::_almostEqual(*d, 0)) {
						distance = d;
					}
				}
			}
		}
		return distance;
	}

	// project each polong of B onto A in the given direction, and return the

	__javascript_nullable<double>
		polygonProjectionDistance(polygon2d polyA, polygon2d polyB, vector2f direction) {
		double Boffsetx = polyB.offsetx;
		double Boffsety = polyB.offsety;

		double Aoffsetx = polyA.offsetx;
		double Aoffsety = polyA.offsety;

		auto A = polyA.slice(0);
		auto B = polyB.slice(0);

		// close the loop for polygons
		if (A[0] != A[A.length() - 1]) {
			A.push(A[0]);
		}

		if (B[0] != B[B.length() - 1]) {
			B.push(B[0]);
		}

		auto edgeA = A;
		auto edgeB = B;

		__javascript_nullable<double> distance = {};
		vector2f p, s1, s2;


		for (long i = 0; i < edgeB.length(); i++) {
			// the shortest/most negative projection of B onto A
			__javascript_nullable<double>    minprojection = {};
			__javascript_nullable<vector2f> minp = {};
			for (long j = 0; j < edgeA.length() - 1; j++) {
				p = { edgeB[i].x + Boffsetx,     edgeB[i].y + Boffsety };
				s1 = { edgeA[j].x + Aoffsetx,     edgeA[j].y + Aoffsety };
				s2 = { edgeA[j + 1].x + Aoffsetx, edgeA[j + 1].y + Aoffsety };

				if (std::abs((s2.y - s1.y) * direction.x - (s2.x - s1.x) * direction.y) < TOL) {
					continue;
				}

				// project point, ignore edge boundaries
				auto d = pointDistance(p, s1, s2, direction, false);

				if (d.has_value() && (!minprojection.has_value() || *d < *minprojection)) {
					minprojection = d;
					minp = { p }; //TODO minp not used??
				}
			}
			if (minprojection.has_value() && (!distance.has_value() || *minprojection > *distance)) {
				distance = minprojection;
			}
		}

		return distance;
	}

	// returns true if polong already exists in the given nfp
	bool inNfp(vector2f p, __type_javascript_array<polygon2d> nfp) {
		if (nfp.length() == 0) {
			return false;
		}

		for (long i = 0; i < nfp.length(); i++) {
			for (long j = 0; j < nfp[i].length(); j++) {
				if (GeometryUtil::_almostEqual(p.x, nfp[i][j].x) && GeometryUtil::_almostEqual(p.y, nfp[i][j].y)) {
					return true;
				}
			}
		}

		return false;
	}

	// searches for an arrangement of A and B such that they do not overlap
	// if an NFP is given, only search for startpoints that have not already been traversed in the given NFP
	__javascript_nullable<vector2f>
		searchStartPoint(polygon2d polyA, polygon2d polyB, bool inside, __javascript_nullable<__type_javascript_array<polygon2d>> NFP) {
		// clone arrays
		auto A = polygon2d::from(polyA.slice(0));
		auto B = polygon2d::from(polyB.slice(0));

		// close the loop for polygons
		if (A[0] != A[A.length() - 1]) {
			A.push(A[0]);
		}

		if (B[0] != B[B.length() - 1]) {
			B.push(B[0]);
		}

		for (long i = 0; i < A.length() - 1; i++) {
			if (!A[i].marked) {
				A[i].marked = true;
				for (long j = 0; j < B.length(); j++) {
					B.offsetx = A[i].x - B[j].x;
					B.offsety = A[i].y - B[j].y;

					__javascript_nullable<bool> Binside = {};
					for (long k = 0; k < B.length(); k++) {
						auto inpoly = pointInPolygon({ B[k].x + B.offsetx, B[k].y + B.offsety }, A);
						if (inpoly.has_value()) {
							Binside = inpoly;
							break;
						}
					}

					if (!Binside.has_value()) { // A and B are the same
						return{};
					}

					vector2f startPoint{ B.offsetx, B.offsety };
					if (((Binside.getValueOrDefault(false) && inside) || (!Binside.getValueOrDefault(false) && !inside)) && !intersect(A, B) && !inNfp(startPoint, *NFP)) {
						return{ startPoint };
					}

					// slide B along vector
					double vx = A[i + 1].x - A[i].x;
					double vy = A[i + 1].y - A[i].y;

					auto d1 = polygonProjectionDistance(A, B, { vx,  vy });
					auto d2 = polygonProjectionDistance(B, A, { -vx, -vy });

					__javascript_nullable<double> d = {};

					// todo: clean this up
					if (!d1.has_value() && !d2.has_value()) {
						// nothin
					}
					else if (!d1.has_value()) {
						d = d2;
					}
					else if (!d2.has_value()) {
						d = d1;
					}
					else {
						d = { std::min(*d1, *d2) };
					}

					// only slide until no longer negative
					// todo: clean this up
					if (d.has_value() && !GeometryUtil::_almostEqual(*d, 0) && *d > 0) {

					}
					else {
						continue;
					}

					double vd2 = vx*vx + vy*vy;

					//TODO possible exception 'd' most be null
					if ((*d)*(*d) < vd2 && !GeometryUtil::_almostEqual((*d)*(*d), vd2)) {
						double vd = std::sqrt(vx*vx + vy*vy);
						vx *= *d / vd;
						vy *= *d / vd;
					}

					B.offsetx += vx;
					B.offsety += vy;

					for (long k = 0; k < B.length(); k++) {
						auto inpoly = pointInPolygon({ B[k].x + B.offsetx, B[k].y + B.offsety }, A);
						if (inpoly.has_value()) {
							Binside = inpoly;
							break;
						}
					}
					startPoint = { B.offsetx, B.offsety };
					if (((Binside.getValueOrDefault(false) && inside) || (!Binside.getValueOrDefault(false) && !inside)) && !intersect(A, B) && !inNfp(startPoint, *NFP)) {
						return startPoint;
					}
				}
			}
		}

		return{};
	}

	bool isRectangle(polygon2d poly, double tolerance = TOL) {
		auto bb = getPolygonBounds(poly); //TODO possible exception
		tolerance = tolerance;

		for (long i = 0; i < poly.length(); i++) {
			if (!GeometryUtil::_almostEqual(poly[i].x, (*bb).x) && !GeometryUtil::_almostEqual(poly[i].x, (*bb).x + (*bb).width)) {
				return false;
			}
			if (!GeometryUtil::_almostEqual(poly[i].y, (*bb).y) && !GeometryUtil::_almostEqual(poly[i].y, (*bb).y + (*bb).height)) {
				return false;
			}
		}

		return true;
	}

	// returns an interior NFP for the special case where A is a rectangle
	__javascript_nullable<polygon2d>
		noFitPolygonRectangle(polygon2d A, polygon2d B) {
		auto minAx = A[0].x;
		auto minAy = A[0].y;
		auto maxAx = A[0].x;
		auto maxAy = A[0].y;

		for (long i = 1; i < A.length; i++) {
			if (A[i].x < minAx) {
				minAx = A[i].x;
			}
			if (A[i].y < minAy) {
				minAy = A[i].y;
			}
			if (A[i].x > maxAx) {
				maxAx = A[i].x;
			}
			if (A[i].y > maxAy) {
				maxAy = A[i].y;
			}
		}

		auto minBx = B[0].x;
		auto minBy = B[0].y;
		auto maxBx = B[0].x;
		auto maxBy = B[0].y;
		for (long i = 1; i < B.length; i++) {
			if (B[i].x < minBx) {
				minBx = B[i].x;
			}
			if (B[i].y < minBy) {
				minBy = B[i].y;
			}
			if (B[i].x > maxBx) {
				maxBx = B[i].x;
			}
			if (B[i].y > maxBy) {
				maxBy = B[i].y;
			}
		}

		if (maxBx - minBx > maxAx - minAx) {
			return{};
		}
		if (maxBy - minBy > maxAy - minAy) {
			return{};
		}

		polygon2d __return;
		__return.construct({
		{ minAx - minBx + B[0].x, minAy - minBy + B[0].y },
		{ maxAx - maxBx + B[0].x, minAy - minBy + B[0].y },
		{ maxAx - maxBx + B[0].x, maxAy - maxBy + B[0].y },
		{ minAx - minBx + B[0].x, maxAy - maxBy + B[0].y }
		});

		return{ __return };
	}

	// given a static polygon A and a movable polygon B, compute a no fit polygon by orbiting B about A
	// if the inside flag is set, B is orbited inside of A rather than outside
	// if the searchEdges flag is set, all edges of A are explored for NFPs - multiple
	__javascript_nullable<__type_javascript_array<polygon2d>>
	noFitPolygon(__javascript_nullable<polygon2d> polyA, __javascript_nullable<polygon2d> polyB, bool inside, bool searchEdges) {
	if (!polyA.has_value() || (*polyA).length() < 3 || !polyB.has_value() || (*polyB).length() < 3) {
		return{};
	}

	auto A = *polyA;
	auto B = *polyB;

	A.offsetx = 0;
	A.offsety = 0;

	auto minA = A[0].y;
	auto minAindex = 0;

	auto maxB = B[0].y;
	auto maxBindex = 0;

	for (long i = 1; i<A.length(); i++) {
		A[i].marked = false;
		if (A[i].y < minA) {
			minA = A[i].y;
			minAindex = i;
		}
	}

	for (long i = 1; i<B.length(); i++) {
		B[i].marked = false;
		if (B[i].y > maxB) {
			maxB = B[i].y;
			maxBindex = i;
		}
	}

	
	// shift B such that the bottom-most point of B is at the top-most point of A. This guarantees an initial placement with no intersections
	vector2f startpoint {
		A[minAindex].x - B[maxBindex].x,
		A[minAindex].y - B[maxBindex].y
	};

	if (inside){
		// no reliable heuristic for inside
		startpoint = *searchStartPoint(A, B, true);
	}

	var NFPlist = [];

	while (startpoint != = null) {

		B.offsetx = startpoint.x;
		B.offsety = startpoint.y;

		// maintain a list of touching points/edges
		var touching;

		var prevvector = null; // keep track of previous vector
		var NFP = [{
		x: B[0].x + B.offsetx,
			y : B[0].y + B.offsety
		}];

		var referencex = B[0].x + B.offsetx;
		var referencey = B[0].y + B.offsety;
		var startx = referencex;
		var starty = referencey;
		var counter = 0;

		while (counter < 10 * (A.length + B.length)) { // sanity check, prevent infinite loop
			touching = [];
			// find touching vertices/edges
			for (i = 0; i<A.length; i++) {
				var nexti = (i == A.length - 1) ? 0 : i + 1;
				for (j = 0; j<B.length; j++) {
					var nextj = (j == B.length - 1) ? 0 : j + 1;
					if (_almostEqual(A[i].x, B[j].x + B.offsetx) && _almostEqual(A[i].y, B[j].y + B.offsety)) {
						touching.push({ type: 0, A : i, B : j });
					}
					else if (_onSegment(A[i], A[nexti], { x: B[j].x + B.offsetx, y : B[j].y + B.offsety })) {
						touching.push({ type: 1, A : nexti, B : j });
					}
					else if (_onSegment({ x: B[j].x + B.offsetx, y : B[j].y + B.offsety }, { x: B[nextj].x + B.offsetx, y : B[nextj].y + B.offsety }, A[i])) {
						touching.push({ type: 2, A : i, B : nextj });
					}
				}
			}

			// generate translation vectors from touching vertices/edges
			var vectors = [];
			for (i = 0; i<touching.length; i++) {
				var vertexA = A[touching[i].A];
				vertexA.marked = true;

				// adjacent A vertices
				var prevAindex = touching[i].A - 1;
				var nextAindex = touching[i].A + 1;

				prevAindex = (prevAindex < 0) ? A.length - 1 : prevAindex; // loop
				nextAindex = (nextAindex >= A.length) ? 0 : nextAindex; // loop

				var prevA = A[prevAindex];
				var nextA = A[nextAindex];

				// adjacent B vertices
				var vertexB = B[touching[i].B];

				var prevBindex = touching[i].B - 1;
				var nextBindex = touching[i].B + 1;

				prevBindex = (prevBindex < 0) ? B.length - 1 : prevBindex; // loop
				nextBindex = (nextBindex >= B.length) ? 0 : nextBindex; // loop

				var prevB = B[prevBindex];
				var nextB = B[nextBindex];

				if (touching[i].type == 0) {

					var vA1 = {
					x: prevA.x - vertexA.x,
					   y : prevA.y - vertexA.y,
						   start : vertexA,
								   end : prevA
					};

					var vA2 = {
					x: nextA.x - vertexA.x,
					   y : nextA.y - vertexA.y,
						   start : vertexA,
								   end : nextA
					};

					// B vectors need to be inverted
					var vB1 = {
					x: vertexB.x - prevB.x,
					   y : vertexB.y - prevB.y,
						   start : prevB,
								   end : vertexB
					};

					var vB2 = {
					x: vertexB.x - nextB.x,
					   y : vertexB.y - nextB.y,
						   start : nextB,
								   end : vertexB
					};

					vectors.push(vA1);
					vectors.push(vA2);
					vectors.push(vB1);
					vectors.push(vB2);
				}
				else if (touching[i].type == 1) {
					vectors.push({
					x: vertexA.x - (vertexB.x + B.offsetx),
					   y : vertexA.y - (vertexB.y + B.offsety),
						   start : prevA,
								   end : vertexA
					});

					vectors.push({
					x: prevA.x - (vertexB.x + B.offsetx),
					   y : prevA.y - (vertexB.y + B.offsety),
						   start : vertexA,
								   end : prevA
					});
				}
				else if (touching[i].type == 2) {
					vectors.push({
					x: vertexA.x - (vertexB.x + B.offsetx),
					   y : vertexA.y - (vertexB.y + B.offsety),
						   start : prevB,
								   end : vertexB
					});

					vectors.push({
					x: vertexA.x - (prevB.x + B.offsetx),
					   y : vertexA.y - (prevB.y + B.offsety),
						   start : vertexB,
								   end : prevB
					});
				}
			}

			// todo: there should be a faster way to reject vectors that will cause immediate intersection. For now just check them all

			var translate = null;
			var maxd = 0;

			for (i = 0; i<vectors.length; i++) {
				if (vectors[i].x == 0 && vectors[i].y == 0) {
					continue;
				}

				// if this vector points us back to where we came from, ignore it.
				// ie cross product = 0, dot product < 0
				if (prevvector && vectors[i].y * prevvector.y + vectors[i].x * prevvector.x < 0) {

					// compare magnitude with unit vectors
					var vectorlength = Math.sqrt(vectors[i].x*vectors[i].x + vectors[i].y*vectors[i].y);
					var unitv = { x: vectors[i].x / vectorlength, y : vectors[i].y / vectorlength };

					var prevlength = Math.sqrt(prevvector.x*prevvector.x + prevvector.y*prevvector.y);
					var prevunit = { x: prevvector.x / prevlength, y : prevvector.y / prevlength };

					// we need to scale down to unit vectors to normalize vector length. Could also just do a tan here
					if (Math.abs(unitv.y * prevunit.x - unitv.x * prevunit.y) < 0.0001) {
						continue;
					}
				}

				var d = this.polygonSlideDistance(A, B, vectors[i], true);
				var vecd2 = vectors[i].x*vectors[i].x + vectors[i].y*vectors[i].y;

				if (d == = null || d*d > vecd2) {
					var vecd = Math.sqrt(vectors[i].x*vectors[i].x + vectors[i].y*vectors[i].y);
					d = vecd;
				}

				if (d != = null && d > maxd) {
					maxd = d;
					translate = vectors[i];
				}
			}


			if (translate == = null || _almostEqual(maxd, 0)) {
				// didn't close the loop, something went wrong here
				NFP = null;
				break;
			}

			translate.start.marked = true;
			translate.end.marked = true;

			prevvector = translate;

			// trim
			var vlength2 = translate.x*translate.x + translate.y*translate.y;
			if (maxd*maxd < vlength2 && !_almostEqual(maxd*maxd, vlength2)) {
				var scale = Math.sqrt((maxd*maxd) / vlength2);
				translate.x *= scale;
				translate.y *= scale;
			}

			referencex += translate.x;
			referencey += translate.y;

			if (_almostEqual(referencex, startx) && _almostEqual(referencey, starty)) {
				// we've made a full loop
				break;
			}

			// if A and B start on a touching horizontal line, the end point may not be the start point
			var looped = false;
			if (NFP.length > 0) {
				for (i = 0; i<NFP.length - 1; i++) {
					if (_almostEqual(referencex, NFP[i].x) && _almostEqual(referencey, NFP[i].y)) {
						looped = true;
					}
				}
			}

			if (looped) {
				// we've made a full loop
				break;
			}

			NFP.push({
			x: referencex,
			   y : referencey
			});

			B.offsetx += translate.x;
			B.offsety += translate.y;

			counter++;
		}

		if (NFP && NFP.length > 0) {
			NFPlist.push(NFP);
		}

		if (!searchEdges) {
			// only get outer NFP or first inner NFP
			break;
		}

		startpoint = this.searchStartPoint(A, B, inside, NFPlist);

	}

	return NFPlist;
}


};