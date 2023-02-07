/*
 * Created on 09.08.2007
 *
 */
package ru.ts.gisutils.tcstore;

import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.IXY;
import ru.ts.gisutils.geometry.XY;
import ru.ts.gisutils.tc.*;

/**
 * @author yugl
 *
 * Представляет вершину геометрической линии, которая после топологической чистки  
 * будет использована для построения дуги в сетевом графе.
 * Вершина похожа на узел, но узел - это самостоятельный объект, а вершина принадлежит
 * линии, на нее должен указывать идентификатор родительского объекта.   
 */
public class TcObjVertex extends TcObjBase implements IGetXY, ITcObjVertex {

	//----------------------
	// fields
	//----------------------
 
	/**
	 * Индекс вершины среди вершин линии.
	 */ 
	protected int _index;
	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcObjVertex#index()
	 */
	public int index () { return _index; }
	
	/**
	 * Координаты вершины. 
	 */ 
	protected IGetXY _xy;
	
	//----------------------
	// constructors
	//----------------------
		
	/**
	 * Создается объект без привязанных данных
	 * @param tcid - идентификатор
	 * @param index - индекс вершины
	 * @param xy - координаты
	 */
	public TcObjVertex (TcId tcid, int index, IGetXY xy) {
		super(tcid);
		this._index = index;
		this._xy = xy;
	}
	
	/**
	 * Создается объект с родителем
	 * @param tcid - идентификатор
	 * @param index - индекс вершины
	 * @param xy - координаты
	 * @param tcparent - родитель объекта, это полилиния, 
	 * 				которой он принадлежит
	 */
	public TcObjVertex (TcId tcid, int index, IGetXY xy, TcId tcparent) {
		this( tcid, index, xy );
		_tcparent = tcparent;
	}
	/**
	 * Создается объект с привязанными данными
	 * @param tcid - идентификатор
	 * @param index - индекс вершины
	 * @param xy - координаты
	 * @param tcdata - привязанные данные
	 */
	public TcObjVertex(TcId tcid, int index, IGetXY xy, Object tcdata) {
		super(tcid, tcdata);
		this._index = index;
		this._xy = xy;
	}

	//----------------------
	// IGetXY
	//----------------------
	
	public double getX() { return _xy.getX(); }
	public double getY() { return _xy.getY(); }
	public void copyTo (IXY he) { _xy.copyTo(he); }

	// includes Comparable
	public int compareTo (Object obj) { 
		TcObjVertex he = (TcObjVertex)obj; 
		return XY.geometry().compareYthenX(this, he); 
//		return _xy.compareTo(he._xy); 
	}
	
	public static int compareByParent(Object v1, Object v2)
	{
		return ((TcObjVertex)v1)._tcparent.compareTo(((TcObjVertex)v2)._tcparent);
	}

	/*
	/*
	 * returns string representation of this vertex - its TcId and parent ЕcId,
	 * separated by colon
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		return super._tcid.toString() + ":" + 
		(super._tcparent.toString() == null ? "null" : super._tcparent.toString());
	}

}


