/*
 * Created on 7 Sep 2007
 *
 */
package ru.ts.gisutils.tc;

/**
 * @author yugl
 *
 * Операция соединения двух полилиний в одну. 
 * При исполнении операции в TcDataStore существенным является учет транзакционности.
 * Поскольку полилиния может соединяться сразу с двумя другими, а те в свою очередь
 * еще с другими, операция должна пройти по возможности "каскадно": то есть, все
 * соответствующие линии сольются в одну.
 */
public class TcActJoin2Plines extends TcAction {

	//----------------------
	// fields
	//----------------------

	/**
	 * Концевая вершина одной полилинии  
	 */ 
	public ITcObjVertex vert1;
	
	/**
	 * Концевая вершина второй полилинии  
	 */ 
	public ITcObjVertex vert2;
	
	//----------------------
	// constructors
	//----------------------
	
	/**
	 * @param defect - исправляемый дефект
	 * @param correction - способ исправления (для информации)
	 * @param vert1 - концевая вершина одной полилинии
	 * @param vert2 - концевая вершина второй полилинии
	 */
	public TcActJoin2Plines (TcLocInfo defect, int correction, ITcObjVertex vert1, ITcObjVertex vert2) {
		super(defect, correction, TcAction.PLINE_JOIN_TWO);	
		this.vert1 = vert1;
		this.vert2 = vert2;
	}

	/**
	 * @param defect - исправляемый дефект
	 * @param vert1 - концевая вершина одной полилинии
	 * @param vert2 - концевая вершина второй полилинии
	 */
	public TcActJoin2Plines (TcLocInfo defect, ITcObjVertex vert1, ITcObjVertex vert2) {
		super(defect, TcAction.PLINE_JOIN_TWO);	
		this.vert1 = vert1;
		this.vert2 = vert2;
	}

}
