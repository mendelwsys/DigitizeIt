/*
 * Created on 4 Sep 2007
 *
 */
package ru.ts.gisutils.tc;

import ru.ts.gisutils.geometry.IGetXY;

/**
 * @author yugl
 *
 * Операция создания нового узла
 */
public class TcActNewNode extends TcAction {

	//----------------------
	// fields
	//----------------------

	/**
	 * Координаты создаваемого узла
	 */ 
	public IGetXY xy;
	
	//----------------------
	// constructors
	//----------------------
	
	/**
	 * @param defect - исправляемый дефект
	 * @param correction - способ исправления (для информации)
	 * @param xy - координаты нового узла
	 */
	public TcActNewNode (TcLocInfo defect, int correction, IGetXY xy) {
		super(defect, correction, TcAction.NODE_MAKE_NEW);
		this.xy = xy;
	}

	/**
	 * @param defect - исправляемый дефект
	 * @param xy - координаты нового узла
	 */
	public TcActNewNode (TcLocInfo defect, IGetXY xy) {
		super(defect, TcAction.NODE_MAKE_NEW);
		this.xy = xy;
	}

}
