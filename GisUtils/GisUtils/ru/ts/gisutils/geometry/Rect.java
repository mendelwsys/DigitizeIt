package ru.ts.gisutils.geometry;

/**
 * @author yugl
 *         Created on 26.07.2007
 *         To keep extents of rectangle: left, right, bottom, and top.
 *         changes 06.05.08 - made inheritance from Envelope.
 *         changes 14.11.08 - removed inheritance from Envelope.
 */
public class Rect implements Cloneable {

  //----------------------
  // fields
  //----------------------
  // now not used fields from Envelope
	public double minx = 0;
  public double maxx = 0;
  public double miny = 0;
	public double maxy = 0;

  //----------------------
  // getters
  //----------------------
  public double getMinX() {
    return minx;
  }

  public double getMaxX() {
    return maxx;
  }

  public double getMinY() {
    return miny;
  }

  public double getMaxY() {
    return maxy;
  }

  //----------------------
  // static fields
  //----------------------

  // geometry is used for some rectangle operations
  static protected XYGeometry _geometry;

  static {
    _geometry = XY.geometry();
  }

  static public XYGeometry geometry() {
    return _geometry;
  }

  //----------------------
  // constructors
  //----------------------
  /**
   * creates an empty rectangle
   */
  public Rect() {
    super();
  }

  /**
   * creates a rectangle with given limits
   */
  public Rect(double x1, double x2, double y1, double y2) {
    _init(this, x1, x2, y1, y2);
  }

  public Rect(IGetXY xy1, IGetXY xy2) {
    _init(this, xy1, xy2);
  }

//  public Rect(Envelope env) {
//    _init(this, env);
//  }

  //----------------------
  // static
  //----------------------

  // sets data of the instance, using parameters

  static protected void _init(Rect me, double x1, double y1, double x2, double y2) {
		me.minx = x1;
		me.maxx = x2;
		me.miny = y1;
		me.maxy = y2;
		me.arrange();
//    me.init(x1, x2, y1, y2);
  }

  static protected void _init(Rect me, IGetXY xy1, IGetXY xy2) {
    _init(me, xy1.getX(), xy1.getY(), xy2.getX(), xy2.getY());
  }

//  static protected void _init(Rect me, Envelope he) {
//    _init(me, he.getMinX(), he.getMaxX(), he.getMinY(), he.getMaxY());
//  }

  /**
   * @return empty rectangle (to be sure)
   */
  static public Rect empty() {
    Rect rect = new Rect();
    rect.setEmpty();
    return rect;
  }

  /**
   * @return rectangle with all zero limits
   */
  static public Rect zero() {
    Rect rect = new Rect();
    rect.setZero();
    return rect;
  }

  //----------------------
  // methods
  //----------------------

  // sets data of the instance, using parameters

  public void setEmpty() {
		minx = 1; maxx = -1;	miny = 1; maxy = -1;
//    init();
  }

  // sets data of the instance, using parameters
  public void setZero() {
    minx = 0; maxx = 0;	miny = 0; maxy = 0;
//    init(0, 0, 0, 0);
  }

  public void set (double x1, double x2, double y1, double y2) {
		_init( this,  x1, y1, x2, y2 );
	}
	public void set (IGetXY xy1, IGetXY xy2) {
		_init( this,  xy1, xy2 );
	}
//  public void init(IGetXY xy1, IGetXY xy2) {
//    _init(this, xy1, xy2);
//  }
	public void set (Rect he) {
		set( he.minx, he.maxy, he.maxx, he.miny);
	}

//  public void init(Envelope he) {
//    _init(this, he);
//  }

  // makes a new instance of the class with the same data
  public Rect copyOf() {
    try {
      return (Rect) this.clone();
    }
    catch (CloneNotSupportedException e) {
      e.printStackTrace();
      return this;
    }
  }

  // copies the data to another instance
  public void copyTo(Rect he) {
		he.set(this);
//    he.init(this);
  }

  public String toString() {
    if (isEmpty()) return "[empty]";
    return "[" + minx + ":" + maxx + ", " + miny + ":" + maxy + "]";
  }

  //----------------------
  // extents processing
  //----------------------

  // makes left less than right and bottom less than top
	public void arrange () {
		double v;
		if (minx > maxx) { v = minx; minx = maxx; maxx = v;	}
		if (miny > maxy) { v = miny; miny = maxy; maxy = v;	}
	}

  public boolean isEmpty() {
		if (minx > maxx) return true;
		if (miny > maxy) return true;
		return false;
//    return isNull();
  }

  public double width() {
		if (minx >= maxx) return 0;
		return maxx - minx;
//    return getWidth();
  }

  public double height() {
		if (miny >= maxy) return 0;
		return maxy - miny;
//    return getHeight();
  }

  // Envelope cannot work in this manner
    public void extendX (double x) {
		if (minx > maxx) {
            minx = x; maxx = x;}
		else {
			if (minx > x) minx = x;
			if (maxx < x) maxx = x;
		}
	}
  // Envelope cannot work in this manner
	public void extendY (double y) {
		if (miny > maxy) {
            miny = y; maxy = y;}
		else {
			if (miny > y) miny = y;
			if (maxy < y) maxy = y;
		}
  }
  public void extend(double x, double y) {
		extendX(x);
		extendY(y);
//    expandToInclude(x, y);
  }

  public void extend(IGetXY xy) {
    extend(xy.getX(), xy.getY());
  }

  public void extend(Rect he) {
    if (he.isEmpty()) return;
    extend(he.minx, he.miny);
    extend(he.maxx, he.maxy);
  }

  public void inflate(double d) {
    if (isEmpty()) return;
		minx -= d;
    maxx += d;
    miny -= d;
    maxy += d;
//    expandBy(d);
  }

  public Rect inflated(double d) {
    Rect he = this.copyOf();
    he.inflate(d);
    return he;
  }

  public void translate(double dx, double dy) {
    minx += dx;
    maxx += dx;
    miny += dy;
    maxy += dy;
  }
  public Rect translated(double dx, double dy) {
    Rect he = this.copyOf();
    he.translate(dx, dy);
    return he;
  }

  // It is painful to implement this via Envelope. Use this if there are a lot of intersections
  // and lack of memory is possible.
  public void cutBy(Rect he) {
		if (he.minx > minx) minx = he.minx;
		if (he.maxx < maxx) maxx = he.maxx;
		if (he.miny > miny) miny = he.miny;
		if (he.maxy < maxy) maxy = he.maxy;
//    if (isEmpty()) return;
//    if (he.isEmpty() || !intersects(he)) {
//      setEmpty();
//    } else {
//      double minX = getMinX() > he.getMinX() ? getMinX() : he.getMinX();
//      double minY = getMinY() > he.getMinY() ? getMinY() : he.getMinY();
//      double maxX = getMaxX() < he.getMaxX() ? getMaxX() : he.getMaxX();
//      double maxY = getMaxY() < he.getMaxY() ? getMaxY() : he.getMaxY();
//      init(minX, maxX, minY, maxY);
//    }
  }

  public Rect intersected(Rect he) {
		Rect res = this.copyOf();
		res.cutBy(he);
		return res;
//    return new Rect(intersection(he));
  }

  //----------------------
  // extents evaluation
  //----------------------

  public boolean small(double d, IXYGeometry geometry) {
    if (!geometry.small(width(), d)) return false;
    if (!geometry.small(height(), d)) return false;
    return true;
  }

  public boolean small(double d) {
    return small(d, _geometry);
  }

  public boolean has(double x, double y) {
			if (x <= minx) return false;
			if (x >= maxx) return false;
			if (y <= miny) return false;
			if (y >= maxy) return false;
          return true;
//    return contains(x, y);
  }

  public boolean has(IGetXY xy) {
    return has(xy.getX(), xy.getY());
  }

  public boolean has(Rect he) {
    if (he.isEmpty()) return false;
    if (he.minx <= minx) return false;
    if (he.maxx >= maxx) return false;
    if (he.miny <= miny) return false;
    if (he.maxy >= maxy) return false;
        return true;
//    return contains(he);
	}

}
