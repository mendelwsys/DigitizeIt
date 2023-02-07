/*
 * Created on 19.07.2007
 *
 * TC is a shortening of Topology Cleaning
 */
package ru.ts.gisutils.geometry;


/**
 * @author yugl
 *
 * To keep 2 coordinates: x and y
 */
public class XY implements IXY, Cloneable {
	
	//----------------------
	// constants
	//----------------------
	
	//----------------------
	// fields
	//----------------------
	public double x = 0;
	public double y = 0;
	
	//----------------------
	// static fields
	//----------------------
	
	// статический объект, где реализована вся геометрия
	static protected XYGeometry _geometry;
	static {
		_geometry = new XYGeometry();
	}
	static public XYGeometry geometry() { return _geometry; }
//	static public XYGeometry setGeometry(ITcGeometry geometry) { 
//		return _geometry; 
//	}
	
	//----------------------
	// constructors
	//----------------------
	public XY () {
	}
	public XY (double x, double y) {
		_init(this, x, y);
	}
	public XY (IGetXY xy) {
		_init(this, xy);
	}
	
	//----------------------
	// static
	//----------------------

	// sets data of the instance, using parameters   
	static protected void _init (XY me, double x, double y) {
		me.x = x;
		me.y = y;
	}
	static protected void _init (XY me, IGetXY xy) {
		xy.copyTo(me);
	}

	//----------------------
	// IGetXY
	//----------------------
	public double getX() { return x; }
	public double getY() { return y; }
	public void copyTo (IXY he) { he.setXY(x, y); }

	// includes Comparable
	public int compareTo (Object obj) {
		return _geometry.compareYthenX( this, (IGetXY)obj );
	}
	
	//----------------------
	// IXYSet
	//----------------------
	public void setX(double x) { this.x = x; }
	public void setY(double y) { this.y = y; }
	public void setXY(double x, double y) { 
		this.x = x; 
		this.y = y; 
	}

	//----------------------
	// toString
	//----------------------

  // sets data of the instance, using parameters
  public String toString () {
//    String s = "[" + x + "; " + y + "]";
    return "["+  x + " " + y + "]";
  }

  //----------------------
  // methods
  //----------------------

  // sets data of the instance, using parameters
	public void set (double x, double y) {
		_init(this, x, y);
	}
	public void set (IGetXY xy) {
		_init(this, xy);
	}
	
	// makes a new instance of the class with the same data 
	public XY copyOf () {
		try {
			return (XY) this.clone(); 
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return this; 
		}
	}
	
//	// copies the data to another instance  
//	public void copyTo (XY he) {
//		he.set(this); 
//	}
	
	//----------------------
	// operations
	//----------------------
	
	// adds a vector to this one   
	public void add (double x, double y) {
		this.x += x;
		this.y += y;
	}
	public void add (IGetXY xy) {
		add(xy.getX(), xy.getY());
	}
	
	// subtracts a vector from this one   
	public void sub (double x, double y) {
		this.x -= x;
		this.y -= y;
	}
	public void sub (IGetXY xy) {
		sub(xy.getX(), xy.getY());
	}
	
	// multiplies this by a factor   
	public void mul (double k) {
		this.x *= k;
		this.y *= k;
	}
	
	//----------------------
	// check of nearness
	//----------------------
	
	/**
	 * Проверяет близость х2,y2 покоординатно с точностью до d (включительно)
	 */
	public boolean nearBoth (double x2, double y2, double d) {
		return _geometry.nearBoth(x, y, x2, y2, d);		
	}
	/**
	 * Проверяет близость х2,y2 покоординатно с точностью до MIN_EPS (включительно)
	 */
	public boolean nearBoth (double x2, double y2) {
		return _geometry.nearBoth(x, y, x2, y2);
	}
	
	/**
	 * Проверяет близость хy2 покоординатно с точностью до d (включительно)
	 */
	public boolean nearBoth (IGetXY xy2, double d) {
		return nearBoth(xy2.getX(), xy2.getY(), d);		
	}
	/**
	 * Проверяет близость хy2 покоординатно с точностью до MIN_EPS (включительно)
	 */
	public boolean nearBoth (IGetXY xy2) {
		return nearBoth(xy2.getX(), xy2.getY());		
	}
	
	/**
	 * Проверяет близость х2,y2 по расстоянию с точностью до d (включительно)
	 */
	public boolean nearDist (double x2, double y2, double d) {
		return _geometry.nearDist(x, y, x2, y2, d);		
	}
	/**
	 * Проверяет близость х2,y2 по расстоянию с точностью до MIN_EPS (включительно)
	 */
	public boolean nearDist (double x2, double y2) {
		return _geometry.nearDist(x, y, x2, y2);
	}
	
	/**
	 * Проверяет близость хy2 по расстоянию с точностью до d (включительно)
	 */
	public boolean nearDist (IGetXY xy2, double d) {
		return nearDist(xy2.getX(), xy2.getY(), d);		
	}
	/**
	 * Проверяет близость хy2 по расстоянию с точностью до MIN_EPS (включительно)
	 */
	public boolean nearDist (IGetXY xy2) {
		return nearDist(xy2.getX(), xy2.getY());		
	}
	
	//----------------------
	// calculations
	//----------------------

	/**
	 * Квадрат расстояния между точками.
	 */
	public double distance2 (IGetXY xy) {
		return _geometry.distance2(this, xy);
	}	
	/**
	 * Расстояние между точками.
	 */
	public double distance (IGetXY xy) {
		return _geometry.distance(this, xy);
	}
	
	/**
	 * Строит перпендикуляр, опущенный из точки на отрезок ax,ay - bх,by.
	 * Возвращает параметр точки пересечения.
	 */
	public double perpendicular (double ax, double ay, double bx, double by) {
		return _geometry.perpendicular(x,y, ax,ay, bx,by);
	}
	/**
	 * Строит перпендикуляр, опущенный из точки на отрезок axy - bхy.
	 * Возвращает параметр точки пересечения.
	 */
	public double perpendicular (IGetXY axy, IGetXY bxy) {
		return _geometry.perpendicular(this, axy, bxy);
	}
	/**
	 * Строит перпендикуляр, опущенный из точки на отрезок ax,ay - bх,by.
	 * Возвращает параметр точки пересечения, а саму точку пишет в result.
	 */
	public double perpendicular (XY result, double ax, double ay, double bx, double by) {
		return _geometry.perpendicular(result, x,y, ax,ay, bx,by);
	}
	/**
	 * Строит перпендикуляр, опущенный из точки на отрезок axy - bхy.
	 * Возвращает параметр точки пересечения, а саму точку пишет в result.
	 */
	public double perpendicular (XY result, IGetXY oxy, IGetXY axy, IGetXY bxy) {
		return _geometry.perpendicular(result, this, axy, bxy);
	}

	
}
