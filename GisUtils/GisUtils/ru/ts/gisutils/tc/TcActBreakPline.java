/*
 * Created on 7 Sep 2007
 *
 */
package ru.ts.gisutils.tc;

import ru.ts.gisutils.geometry.IGetXY;

/**
 * @author yugl
 *
 * Операция разбиения полилинии на две. 
 * При исполнении операции в TcDataStore существенным является учет транзакционности.
 * Поскольку полилиния может разбиваться в нескольких местах, операция должна пройти 
 * по возможности "каскадно": то есть, в полилинии должны оформиться все "кусочки".
 */
public class TcActBreakPline extends TcAction {

	//----------------------
	// fields
	//----------------------

	/**
	 * разбиваемая полилиния  
	 */ 
	public ITcObjPolyline pline;
	
	/**
	 * параметрический индекс места разбиения, равный к + т, где к - индекс отрезка,
	 * где идет разбиение, а т - параметр места разбиения от 0 до 1, 0 соответствует
	 * начальной точке отрезка, 1 - конечной.    
	 */ 
	public double index;
	
	/**
	 * точка разбиения, в нее должны приходить концевые точки новых полилиний, 
	 * образуемых на месте старой.  
	 */ 
	public IGetXY xy;
	
	//----------------------
	// constructors
	//----------------------
	
	/**
	 * @param defect - исправляемый дефект
	 * @param correction - способ исправления (для информации)
	 * @param pline - разбиваемая полилинии
	 * @param index - параметрический индекс точки разбиения
	 * @param xy - точка для концов полилинии
	 */
	public TcActBreakPline (TcLocInfo defect, int correction, ITcObjPolyline pline, double index, IGetXY xy) {
		super(defect, correction, PLINE_BREAK_AT);			
		this.pline = pline;
		this.index = index;
		this.xy = xy;
	}
	
	/**
	 * @param defect - исправляемый дефект
	 * @param pline - разбиваемая полилинии
	 * @param index - параметрический индекс точки разбиения
	 * @param xy - точка для концов полилинии
	 */
	public TcActBreakPline (TcLocInfo defect, ITcObjPolyline pline, double index, IGetXY xy) {
		super(defect, PLINE_BREAK_AT);			
		this.pline = pline;
		this.index = index;
		this.xy = xy;
	}
	

}
