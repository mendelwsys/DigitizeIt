/*
 * Created on 17 Oct 2007
 *
 */
package ru.ts.gisutils.geometry;


/**
 * @author yugl
 *
 * Interface to list geometric operations used by package.
 * It allows to exclude the explicit calculations from the code of 
 * cleaning process or at least to decrease their presence. 
 */
public interface IXYGeometry {
	
	//----------------------
	// comparison
	//----------------------
	
	/* 
	 * Some methods of cleaning use explicitly the fact that the source data
	 * are the sorted point objects. 
	 * The following methods are used to sort and compare these objects. All
	 * of them suppose 2 compared objects as parameters. Result <0 means that
	 * the 1st object stands before the 2nd one, >0 - after, and =0 - that
	 * they have the same position.
	 */
	
	// X-comparing
	public int compareX(double x1, double x2); 
	public int compareX(IGetXY xy1, IGetXY xy2); 
	// Y-comparing
	public int compareY(double y1, double y2); 
	public int compareY(IGetXY xy1, IGetXY xy2); 
	// XY-comparing
	public int compareYthenX(IGetXY xy1, IGetXY xy2); 

	//----------------------
	// distance
	//----------------------
	
	/**
	 * Квадрат расстояния между точками.
	 */
	public double distance2 (double x1, double y1, double x2, double y2);
	public double distance2 (IGetXY xy1, IGetXY xy2);
	/**
	 * Расстояние между точками.
	 */
	public double distance (double x1, double y1, double x2, double y2);
	public double distance (IGetXY xy1, IGetXY xy2);
	
	//----------------------
	// configuration
	//----------------------	
	
	// Gets/Sets a small value to calculate with error	
	public double getEps ();
	public double getEps2 ();
	public double setEps (double eps);	// returns an old value
	
	// Gets/Sets a flag of calculation of nearness (or similar) including the bounds	
	public boolean getCalcBoundInclusive();
	public boolean setCalcBoundInclusive(boolean includeBound);	// returns an old value
	
	//----------------------
	// nearness
	//----------------------
	
	// All functions have two variants:
	// including bounds (abs(v1-v2) <= d), name has "i",
	// and excluding bounds (abs(v1-v2) < d), name has "e".
	// Все функции имеют два варианта: 
	// включающий границу (abs(v1-v2) <= d), в названии "i"
	// и не включающий границу (abs(v1-v2) < d), в названии "e".
	 
	/*
	 * Проверка близости значений координат с заданной точностью (параметр d),
	 * если точность не указана, вместо нее используется Eps (см. конфигурацию).
	 */
	public boolean near (double v1, double v2, double d);
	public boolean near (double v1, double v2);	
	
	/*
	 * Проверка малости значения с заданным порогом малости (параметр d),
	 * если порогом малости не указан, вместо него используется Eps (см. конфигурацию).
	 */
	public boolean small (double v, double d);
	public boolean small (double v);	
	
	/*
	 * Проверка близости точек с заданной точностью (параметр d),
	 * если точность не указана, вместо нее используется Eps (см. конфигурацию).
	 * Предлагается два вида проверок: покоординатная и по расстоянию.
	 */	
	public boolean nearBoth (double x1, double y1, double x2, double y2, double d);
	public boolean nearBoth (double x1, double y1, double x2, double y2);
	public boolean nearBoth (IGetXY xy1, IGetXY xy2, double d);
	public boolean nearBoth (IGetXY xy1, IGetXY xy2);
	
	public boolean nearDist (double x1, double y1, double x2, double y2, double d);
	public boolean nearDist (double x1, double y1, double x2, double y2);
	public boolean nearDist (IGetXY xy1, IGetXY xy2, double d);
	public boolean nearDist (IGetXY xy1, IGetXY xy2);
	
	//----------------------
	// perpendicular
	//----------------------
	
	/*
	 * Строит перпендикуляр, опущенный из точки O на отрезок AB.
	 * Возвращает параметр точки пересечения.
	 */
	public double perpendicular (double ox, double oy, double ax, double ay, double bx, double by);
	public double perpendicular (IGetXY oxy, IGetXY axy, IGetXY bxy);
	
	/*
	 * Строит перпендикуляр, опущенный из точки O на отрезок AB.
	 * Возвращает параметр точки пересечения, а саму точку пишет в result.
	 */
	public double perpendicular (IXY result, double ox, double oy, double ax, double ay, double bx, double by);
	public double perpendicular (IXY result, IGetXY oxy, IGetXY axy, IGetXY bxy);
	
	//----------------------
	// intersection
	//----------------------
	
	/* 
	 * Ищет пересечение отрезков AB и CD.
	 * Возвращает 0, если нет пересечений, 1 - есть одно, 2 - наложение отрезков.
	 * Точка пересечения пишется в res1, границы общего отрезка в res1 и res2.
	 */ 
	
	public int intersection (
			IXY res1, IXY res2,
			double ax, double ay, double bx, double by, 
			double cx, double cy, double dx, double dy	
	);
	public int intersection (XY res1, XY res2,	IGetXY axy, IGetXY bxy,	IGetXY cxy, IGetXY dxy);
	
}
