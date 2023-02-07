/*
 * Created on 02.08.2007
 *
 */
package ru.ts.gisutils.tc;

/**
 * @author yugl
 *
 * Базовый класс для представления объектов, обрабатываемых процессом
 * топологической чистки.
 */
public class TcObjBase implements ITcObjBase {

	//----------------------
	// fields
	//----------------------
 
	/**
	 * Идентификатор объекта (в рамках процесса чистки)
	 */ 
	public TcId _tcid = null;
	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcObject#tcid()
	 */
	public TcId tcid () { return _tcid; }
	
	/**
	 * Идентификатор объекта-родителя (для самостоятельных объектов = null)
	 */ 
	protected TcId _tcparent = null;
	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcObject#tcparent()
	 */
	public TcId tcparent () { return _tcparent; }
	
	/**
	 * Данные, связанные с объектом в процессе чистки.
	 * Содержание данных зависит от содержания процесса обработки.
	 */ 
	protected Object _tcdata = null;		
	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcObject#tcdata()
	 */
	public Object tcdata() { return _tcdata;	}
	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcObject#attachTcdata(java.lang.Object)
	 */
	public void attachTcdata(Object tcdata) {	this._tcdata = tcdata; }

	//----------------------
	// constructors
	//----------------------
	
	/**
	 * В пустом объекте маловато смысла, это на всякий случай.
	 */
	protected TcObjBase () {
	}
	/**
	 * Объект без привязанных данных
	 * @param tcid - идентификатор
	 */
	public TcObjBase (TcId tcid) {
		this._tcid = tcid;
	}
	/**
	 * Объект без привязанных данных
	 * @param tcid - идентификатор
	 * @param tcparent - идентификатор родителя
	 */
	public TcObjBase (TcId tcid, TcId tcparent) {
		this._tcid = tcid;
		this._tcparent = tcparent;
	}
	/**
	 * Объект с привязанными данными
	 * @param tcid - идентификатор
	 * @param tcdata - привязанные данные
	 */
	public TcObjBase (TcId tcid, Object tcdata) {
		this._tcid = tcid;
		this._tcdata = tcdata;
	}
	/**
	 * Объект с привязанными данными
	 * @param tcid - идентификатор
	 * @param tcparent - идентификатор родителя
	 * @param tcdata - привязанные данные
	 */
	public TcObjBase (TcId tcid, TcId tcparent, Object tcdata) {
		this._tcid = tcid;
		this._tcparent = tcparent;
		this._tcdata = tcdata;
	}

	// creates string representation of this object  
	/* (non-Javadoc)
	 * @see ru.ts.gisutils.tc.ITcObject#toString()
	 */
	public String toString () {
		return _tcid.toString();
	}

}
