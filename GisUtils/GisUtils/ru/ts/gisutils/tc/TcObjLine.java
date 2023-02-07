/*
 * Created on 15.08.2007
 *
 */
package ru.ts.gisutils.tc;

import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.IXYGeometry;
import ru.ts.gisutils.geometry.XY;

/**
 * @author yugl
 *
 * Представляет отрезок (сегмент) линии, которая после топологической чистки будет 
 * использована для построения дуги в сетевом графе.
 * Отрезок не является самостоятельным объектом, он принадлежит линии, на нее 
 * должен указывать идентификатор родительского объекта. 
 * На отрезке можно ставить метку, которая представляет точку на отрезке.   
 */
public class TcObjLine extends TcObjBase {

	//----------------------
	// fields
	//----------------------
 
	/**
	 * Вроде бы сам по себе отрезок не бывает нужен, 
	 * сохраним при нем сразу и родительскую полилинию.
	 */ 
	protected ITcObjPolyline _pline;
	public ITcObjPolyline pline () { return _pline; }
	
	/**
	 * Индекс отрезка (совпадает с индексом первой вершины).
	 */ 
	protected int _index;
	public int index () { return _index; }
	
	/**
	 * Пометка на отрезке, задается как параметр (внутри отрезка имеет  
	 * значение от 0 до 1), пометки на маленьких отрезках - просто числа
	 */ 
	protected double _notch;
	
	//----------------------
	// constructors
	//----------------------
		
	/**
	 * Создается объект без привязанных данных
	 * @param pline - родительская полилиния
	 * @param index - индекс вершины
	 */
	public TcObjLine (ITcObjPolyline pline, int index) {
		super(pline.tcid());
		this._pline = pline;
		this._index = index;
	}
	
	/**
	 * Создается объект с привязанными данными
	 * @param pline - родительская полилиния
	 * @param index - индекс вершины
	 * @param notch - пометка на отрезке
	 */
	public TcObjLine(ITcObjPolyline pline, int index, double notch) {
		this(pline, index);
		this._notch = notch;
	}

	/**
	 * Создается объект с привязанными данными
	 * @param pline - родительская полилиния
	 * @param index - индекс вершины
	 * @param tcdata - привязанные данные
	 */
	public TcObjLine(ITcObjPolyline pline, int index, Object tcdata) {
		super(pline.tcid(), tcdata);
		this._pline = pline;
		this._index = index;
	}

	//----------------------
	// notch
	//----------------------
	
	/**
	 * Проверка на "малость" (на малых отрезках пометки - просто числа)
	 */ 
	public boolean tooSmall (IXYGeometry geometry) {
		if (geometry.nearBoth(p1(), p2())) return true;
		return false;
	}
	public boolean tooSmall () {
		if (XY.geometry().nearBoth(p1(), p2())) return true;
		return false;
	}
	
	/**
	 * Возвращает пометку на отрезке, как параметр
	 */ 
	public double getNotch () { return _notch; }
	/**
	 * Возвращает пометку на отрезке, как точку
	 */ 
	public void getNotchXY (XY xy) { 
		double x1 = p1().getX();
		double y1 = p1().getY();
		double x2 = p2().getX();
		double y2 = p2().getY();
		xy.x = x1 + _notch*(x2-x1);
		xy.y = y1 + _notch*(y2-y1);
	}
	/**
	 * Возвращает пометку на отрезке, как точку
	 */ 
	public XY getNotchXY () {
		XY xy = new XY(); 
		getNotchXY(xy);
		return xy;
	}
	/**
	 * Поставить метку на отрезке
	 */ 
	public double setNotch (double notch) {
		double old = _notch; 
		_notch = notch;
		return old;
	}
	/**
	 * Поставить метку на отрезке точкой (имеет смысл для точки, лежащей
	 * на линии отрезка, поэтому для малых отрезков пометки точкой смысла не имеет)
	 */ 
	public IGetXY setNotch (IGetXY xy, IXYGeometry geometry) {
		if (tooSmall()) return p1();
		IGetXY old = getNotchXY();
		_notch = geometry.perpendicular(xy, p1(), p2());
		return old;
	}
	public IGetXY setNotch (IGetXY xy) {
		if (tooSmall()) return p1();
		IGetXY old = getNotchXY();
		_notch = XY.geometry().perpendicular(xy, p1(), p2());
		return old;
	}
	
	//----------------------
	// methods
	//----------------------
	
	/**
	 * Координаты первой вершины. 
	 */ 
	public IGetXY p1 () { 
		return _pline.verts().getXY(_index);
	}
	/**
	 * Координаты второй вершины. 
	 */ 
	public IGetXY p2 () { 
		return _pline.verts().getXY(_index+1);
	}
	
}
