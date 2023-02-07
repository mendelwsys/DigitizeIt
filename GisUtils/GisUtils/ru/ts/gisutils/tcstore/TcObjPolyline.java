/*
 * Created on 15.08.2007
 *
 */
package ru.ts.gisutils.tcstore;

import ru.ts.gisutils.tc.*;

/**
 * @author yugl
 *
 * Представляет линию, заданную последовательностью вершин, которая после топологической 
 * чистки будет использована для построения дуги в сетевом графе.
 */
public class TcObjPolyline extends TcObjBase implements Comparable, ITcObjPolyline {

	//----------------------
	// fields
	//----------------------
 
	/**
	 * Доступ к координатам вершин. 
	 */ 
	protected IGetVertices _verts;
	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcObjPolyline#verts()
	 */
	public IGetVertices verts () { return _verts; }
	
	//----------------------
	// constructors
	//----------------------
		
	/**
	 * Создается объект без привязанных данных
	 * @param tcid - идентификатор
	 * @param verts - координаты вершин
	 */
	public TcObjPolyline (TcId tcid, IGetVertices verts) {
		super(tcid);
		this._verts = verts;
	}
	
	/**
	/**
	 * Создается объект с привязанными данными
	 * @param tcid - идентификатор
	 * @param verts - координаты вершин
	 * @param tcdata - привязанные данные
	 */
	public TcObjPolyline(TcId tcid, IGetVertices verts, Object tcdata) {
		super(tcid, tcdata);
		this._verts = verts;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(Object arg0)
    {
		return _tcid.compareTo(((TcObjPolyline)arg0)._tcid);
    }

}
