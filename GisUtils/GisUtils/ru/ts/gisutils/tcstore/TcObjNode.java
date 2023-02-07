/*
 * Created on 02.08.2007
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
 * Представляет объект, который после топологической чистки будут использован 
 * для построения узла в сетевом графе.
 */
public class TcObjNode extends TcObjBase implements IGetXY, ITcObjNode {

	//----------------------
	// fields
	//----------------------
 
	/**
	 * Узел должен иметь координаты. Вот они.
	 */ 
	protected IGetXY _xy;
	
	//----------------------
	// constructors
	//----------------------
	
	/**
	 * Создается объект без привязанных данных
	 * @param tcid - идентификатор
	 * @param xy - координаты
	 */
	public TcObjNode (TcId tcid, IGetXY xy) {
		super(tcid);
		this._xy = xy;
	}

	/**
	/**
	 * Создается объект с привязанными данными
	 * @param tcid - идентификатор
	 * @param xy - координаты
	 * @param tcdata - привязанные данные
	 */
	public TcObjNode(TcId tcid, IGetXY xy, Object tcdata) {
		super(tcid, tcdata);
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
		TcObjNode he = (TcObjNode)obj; 
		return XY.geometry().compareYthenX(this, he); 
//		return _xy.compareTo(he._xy); 
	}
	


}
