/*
 * Created on 18 Oct 2007
 *
 */
package ru.ts.gisutils.geometry;


/**
 * @author yugl
 *
 * Base class for geometric operations used by package.
 */
public class XYGeometry implements IXYGeometry {

	//----------------------
	// constants
	//----------------------
	
	// small value to calculate with error  
	protected final static double EPS =  0.000001;
	protected final static double EPS2 = EPS*EPS;

	//----------------------
	// fields
	//----------------------
	
	// current error value to calculate with error  
	protected double _eps;
	protected double _eps2;
	
	// current error value to calculate with error  
	protected boolean _calcBoundInclusive;
	
	//----------------------
	// static fields
	//----------------------
	
	// два статических прямоугольника для промежуточных вычислений,
	// !!! если будет много нитей, надо учесть эту шнягу !!!
	static protected Rect _rect1;
	static protected Rect _rect2;
	static {
		_rect1 = new Rect();
		_rect2 = new Rect();
	}
	// точка для промежуточных вычислений,
	// !!! если будет много нитей, надо учесть эту шнягу !!!
	static protected XY _xy;
	static {
		_xy = new XY();
	}
	
	//----------------------
	// constructors
	//----------------------
	public XYGeometry () {
		setEps(EPS);
		setCalcBoundInclusive(true);
	}
	
	//----------------------
	// configuration
	//----------------------	

	// Gets/Sets a small value to calculate with error
	
	public double getEps() {
		return _eps;
	}

	public double getEps2() {
		return _eps2;
	}

	public double setEps(double eps) {
		if (eps <= 0) return _eps;
		double old = _eps;
		_eps = eps;
		_eps2 = eps*eps;
		return old;
	}

	// Gets/Sets a flag of calculation of nearness (or similar) including the bounds	
	
	public boolean getCalcBoundInclusive() {
		return _calcBoundInclusive;
	}

	public boolean setCalcBoundInclusive(boolean includeBound) {
		boolean old = _calcBoundInclusive;
		_calcBoundInclusive = includeBound;
		return old;
	}

	//----------------------
	// comparison
	//----------------------

	/* 
	 * The following methods are used to sort and compare. All of them 
	 * suppose the 2 compared objects as parameters. Result <0 means that
	 * the 1st object stands before the 2nd one, >0 - after, and =0 - that
	 * they have the same position.
	 */
	
	static public int Compare (double v1, double v2) {
		if ( v1 < v2 ) return -1;
		if ( v1 > v2 ) return  1;
		return 0;
	}

	public int compareX (double x1, double x2) {
		return Compare(x1, x2);
	}

	public int compareX(IGetXY xy1, IGetXY xy2) {
		return compareX(xy1.getX(), xy2.getX());
	}

	public int compareY(double y1, double y2) {
		return Compare(y1, y2);
	}

	public int compareY(IGetXY xy1, IGetXY xy2) {
		return compareY(xy1.getY(), xy2.getY());
	}

	public int compareYthenX(IGetXY xy1, IGetXY xy2) {
		int res = compareY(xy1, xy2);
		if (res == 0)	return compareX(xy1, xy2);
		return res;
	}

	//----------------------
	// distance
	//----------------------
	
	public double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(distance2(x1,x2, y1,y2));
	}

	public double distance(IGetXY xy1, IGetXY xy2) {
		return distance(xy1.getX(), xy1.getY(), xy2.getX(), xy2.getY());
	}

	public double distance2(double x1, double y1, double x2, double y2) {
		return ((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}

	public double distance2(IGetXY xy1, IGetXY xy2) {
		return distance2(xy1.getX(), xy1.getY(), xy2.getX(), xy2.getY());
	}

	/**
	 * +++ Sygsky<br>
	 * Euclid distance between 2 points in the Euclidean space :o)
	 * @param xy1 IGetXY instance with point 1
	 * @param xy2 IGetXY instance with point 2
	 * @return Euclid distance in the Euclidean space between 2 points
	 */
	public static double distanceEuclid(IGetXY xy1, IGetXY xy2) {
		return Math.sqrt( distanceEuclid2(xy1.getX(), xy1.getY(), xy2.getX(), xy2.getY()));
	}

	/**
	 * +++ Sygsky<br>
	 * Euclid distance in square
	 * @param x1 point 1 x
	 * @param y1 point 1 y
	 * @param x2 point 2 x
	 * @param y2 point 2 y
	 * @return Euclid distance in square
	 */
	public static double distanceEuclid2(double x1, double y1, double x2, double y2) {
		return ((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}


	//----------------------
	// nearness
	//----------------------

	// nearness of values
	
	// check nearness, including bounds
	static public boolean neari(double v1, double v2, double d) {
		if (Math.abs(v1-v2) <= d) return true;
		return false;
	}
	// check nearness, excluding bounds
	static public boolean neare(double v1, double v2, double d) {
		if (Math.abs(v1-v2) < d) return true;
		return false;
	}
	
	// check nearness according to configuration
	public boolean near(double v1, double v2, double d) {
		if (_calcBoundInclusive) return neari(v1, v2, d);
		return neare(v1, v2, d);
	}
	public boolean near(double v1, double v2) {
		return near(v1, v2, _eps);
	}

	/*
	 * Проверка малости значения с заданным порогом малости (параметр d),
	 * если порогом малости не указан, вместо него используется Eps (см. конфигурацию).
	 */
	public boolean small (double v, double d) {
		return near(v, 0, d);
	}
	public boolean small (double v){
		return near(v, 0);
	}
	
	// nearness of points, using nearness of coordinates 
	
	public boolean nearBoth(double x1, double y1, double x2, double y2, double d) {
		if (!near(x1, x2, d)) return false;
		if (!near(y1, y2, d)) return false;
		return true;
	}

	public boolean nearBoth(double x1, double y1, double x2, double y2) {
		return nearBoth(x1,y1, x2,y2, _eps);
	}

	public boolean nearBoth(IGetXY xy1, IGetXY xy2, double d) {
		return nearBoth(xy1.getX(), xy1.getY(), xy2.getX(), xy2.getY(), d);
	}

	public boolean nearBoth(IGetXY xy1, IGetXY xy2) {
		return nearBoth(xy1, xy2, _eps);
	}

	// nearness of points, using distance between them 
	
	public boolean nearDist(double x1, double y1, double x2, double y2, double d) {
		if (near(distance2(x1,y1, x2,y2), 0, d*d)) return true;
		return false;
	}

	public boolean nearDist(double x1, double y1, double x2, double y2) {
		return nearDist(x1,y1, x2,y2, _eps);
	}

	public boolean nearDist(IGetXY xy1, IGetXY xy2, double d) {
		return nearDist(xy1.getX(), xy1.getY(), xy2.getX(), xy2.getY(), d);
	}

	public boolean nearDist(IGetXY xy1, IGetXY xy2) {
		return nearDist(xy1, xy2, _eps);
	}

	//----------------------
	// perpendicular
	//----------------------
	
	/**
	 * Строит перпендикуляр, опущенный из точки ox,oy на отрезок ax,ay - bх,by.
	 * Возвращает параметр точки пересечения.
	 */
	public double perpendicular(double ox, double oy, double ax, double ay,	double bx, double by) {
		if (nearBoth(ax,ay, bx,by)) return 0;
		// получим вектор ab
		bx -= ax;
		by -= ay;
		return (ox*bx + oy*by - ax*bx - ay*by) / (bx*bx + by*by);
	}

	public double perpendicular(IGetXY oxy, IGetXY axy, IGetXY bxy) {
		return perpendicular(oxy.getX(), oxy.getY(), axy.getX(), axy.getY(), bxy.getX(), bxy.getY());		
	}

	/**
	 * Строит перпендикуляр, опущенный из точки ox,oy на отрезок ax,ay - bх,by.
	 * Возвращает параметр точки пересечения, а саму точку пишет в result.
	 */
	public double perpendicular(IXY result, double ox, double oy, double ax, double ay, double bx, double by) 
	{
		double t = perpendicular(ox,oy, ax,ay, bx,by);
		result.setX(ax + t*(bx-ax)); 
		result.setY(ay + t*(by-ay)); 
		return t;		
	}

	public double perpendicular(IXY result, IGetXY oxy, IGetXY axy, IGetXY bxy) {
		return perpendicular(result, oxy.getX(), oxy.getY(), axy.getX(), axy.getY(), bxy.getX(), bxy.getY());		
	}

	//----------------------
	// intersection
	//----------------------
	
	/**
	 * Ищет пересечение двух отрезков [a,b] и [c,d].
	 * Возвращает 0, если нет пересечений, 1 - одно пересечение, 2 - частичное или полное совпадение.
	 * @param res1 - точка пересечения или начало совпадения
	 * @param res2 - конец совпадения
	 */
	public int intersection(IXY res1, IXY res2, 
			double ax, double ay, double bx, double by, 
			double cx, double cy, double dx, double dy) 
	{
		// проверим границы
		_rect1.set(ax, bx, ay, by);
		_rect1.arrange();
		_rect2.set(cx, dx, cy, dy);
		_rect2.arrange();
		_rect1.cutBy(_rect2);
		if (_rect1.isEmpty()) return 0;
		// имеем систему:
		// t1*(bx-ax) + t2(cx-dx) + (ax-cx) = 0 
		// t1*(by-ay) + t2(cy-dy) + (ay-cy) = 0 
		// вычислим коэффициенты
		double Ax = bx-ax;
		double Bx = cx-dx;
		double Cx = ax-cx;
		double Ay = by-ay;
		double By = cy-dy;
		double Cy = ay-cy;
		// проверим главный определитель
		double D = Ax*By - Ay*Bx;
		if (!near(D, 0)) {
			// одно решение
			double D1 = Bx*Cy - By*Cx;			
			double D2 = Cx*Ay - Cy*Ax;
			double t1 = D1 / D;
			double t2 = D2 / D;
			// ищем пересечение отрезков, а не прямых
			if (t1 < 0) return 0;
			if (t1 > 1) return 0;
			if (t2 < 0) return 0;
			if (t2 > 1) return 0;
			res1.setX(ax + Ax*t1);
			res1.setY(ay + Ay*t1);
			return 1;
		}
		// нет решений или совпадение
		double t1 = perpendicular(_xy, cx,cy, ax,ay, bx,by);
		// если проекция далеко от оригинала, отрезки не рядом (не совпадают)
		if (!nearBoth(_xy.x,_xy.y, cx,cy)) return 0;
		double t2 = perpendicular(dx,dy, ax,ay, bx,by);
		// теперь самое нудное, разобраться, какой кусок внутри (если есть)
		
		// упорядочим с и d
		if (t1 > t2) {
			D = t1; t1 = t2; t2 = D;
			D = cx; cx = dx; dx = D;  
			D = cy; cy = dy; dy = D;
			// теперь с < d   
		}
		if (t1 < 0) { // с < а 
			if (t2 < 0) return 0; // d < а
			// [a,...]
			res1.setX(ax);
			res1.setY(ay);
		}
		else { // c >= a
			// [c,...]
			res1.setX(cx);
			res1.setY(cy);
		}
		if (t2 > 1) { // d > b
			if (t1 > 1) return 0; // c > b
			// [...,b]	
			res2.setX(bx);
			res2.setY(by);
		}
		else { // d <= b 
			// [...,d]
			res2.setX(dx);
			res2.setY(dy);
		}
		// можно еще проверить две точки результата на близость, но выглядит лишним
		return 2;
	}

	public int intersection(XY res1, XY res2, IGetXY axy, IGetXY bxy, IGetXY cxy,	IGetXY dxy) {
		return intersection(res1, res2, 
				axy.getX(), axy.getY(), bxy.getX(), bxy.getY(), 
				cxy.getX(), cxy.getY(), dxy.getX(), dxy.getY());		
	}

}
