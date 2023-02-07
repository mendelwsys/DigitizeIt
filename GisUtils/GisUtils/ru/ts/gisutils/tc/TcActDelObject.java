/*
 * Created on 4 Sep 2007
 *
 */
package ru.ts.gisutils.tc;

/**
 * @author yugl
 *
 * Операция удаления объекта
 */
public class TcActDelObject extends TcAction {

	//----------------------
	// fields
	//----------------------

	/**
	 * Удаляемый объект
	 */ 
	public ITcObjBase obj;
	
	//----------------------
	// constructors
	//----------------------
	
	/**
	 * более общий вариант 
	 * @param defect - исправляемый дефект
	 * @param correction - способ исправления (для информации)
	 * @param type - тип операции исправления
	 * @param obj - объект
	 */
	public TcActDelObject (TcLocInfo defect, int correction, int type, ITcObjBase obj) {
		super(defect, correction, type);	// type = TcAction.OBJ_DELETE или спец.случай
		this.obj = obj;
	}

	/**
	 * частный случай узла  
	 * @param defect - исправляемый дефект
	 * @param correction - способ исправления (для информации)
	 * @param node - объект
	 */
	public TcActDelObject (TcLocInfo defect, int correction, ITcObjNode node) {
		this(defect, correction, TcAction.NODE_DELETE, node);
	}
	/**
	 * частный случай узла  
	 * @param defect - исправляемый дефект
	 * @param node - объект
	 */
	public TcActDelObject (TcLocInfo defect, ITcObjNode node) {
		this(defect, TcLocInfo.DO_MANUALLY, node);
	}

	/**
	 * частный случай полилинии  
	 * @param defect - исправляемый дефект
	 * @param correction - способ исправления (для информации)
	 * @param node - объект
	 */
	public TcActDelObject (TcLocInfo defect, int correction, ITcObjPolyline pline) {
		this(defect, correction, TcAction.PLINE_DELETE, pline);
	}
	/**
	 * частный случай полилинии  
	 * @param defect - исправляемый дефект
	 * @param node - объект
	 */
	public TcActDelObject (TcLocInfo defect, ITcObjPolyline pline) {
		this(defect, TcLocInfo.DO_MANUALLY, pline);
	}

}
