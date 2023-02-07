/*
 * Created on 6 Sep 2007
 *
 */
package ru.ts.gisutils.tc;

/**
 * @author yugl
 *
 * Операция задания значения атрибуту. 
 * Здесь значение атрибута - всегда строка, а там видно будет.
 */
public class TcActSetAttr extends TcAction {

	//----------------------
	// fields
	//----------------------

	/**
	 * Объект, атрибут которого получает значение  
	 */ 
	public ITcObjBase obj;
	
	/**
	 * Имя атрибута  
	 */ 
	public String attrName;
	
	/**
	 * Значение атрибута  
	 */ 
	public String attrValue;
	
	//----------------------
	// constructors
	//----------------------
	
	/**
	 * более общий вариант 
	 * @param defect - исправляемый дефект
	 * @param type - тип операции исправления
	 * @param obj - объект
	 * @param attrName - имя атрибута 
	 * @param attrValue - значение атрибута 
	 */
	public TcActSetAttr (TcLocInfo defect, int correction, int type, ITcObjBase obj, String attrName, String attrValue) {
		super(defect, correction, type);	// type = TcAction.OBJ_SET_ATTR или спец.случай
		this.obj = obj;
		this.attrName = attrName;
		this.attrValue = attrValue;
	}

	//----------------------
	// particular builders
	//----------------------
	
	/**
	 * частный случай висячего / не висячего узла  
	 * @param defect - исправляемый дефект
	 * @param correction - способ исправления (для информации)
	 * @param node - объект
	 * @param state - значение атрибута DANGLING
	 */
	static public TcActSetAttr DanglingNode (TcLocInfo defect, int correction, ITcObjNode node, boolean state) {
		if (state)
			return new TcActSetAttr(defect, correction, TcAction.NODE_MARK_AS_DANGLING, node, "DANGLING", "1");
		else
			return new TcActSetAttr(defect, correction, TcAction.NODE_MARK_AS_CONNECTED, node, "DANGLING", "0");
	}
	/**
	 * частный случай висячего / не висячего узла  
	 * @param defect - исправляемый дефект
	 * @param node - объект
	 * @param state - значение атрибута DANGLING
	 */
	static public TcActSetAttr DanglingNode (TcLocInfo defect, ITcObjNode node, boolean state) {
		return DanglingNode(defect, TcLocInfo.DO_MANUALLY, node, state);
	}

	/**
	 * частный случай висячей / не висячей вершины  
	 * @param defect - исправляемый дефект
	 * @param correction - способ исправления (для информации)
	 * @param vert - объект
	 * @param state - значение атрибута DANGLING
	 */
	static public TcActSetAttr DanglingVert (TcLocInfo defect, int correction, ITcObjVertex vert, boolean state) {
		if (state)
			return new TcActSetAttr(defect, correction, TcAction.VERT_MARK_AS_DANDLING, vert, "DANGLING", "1");
		else
			return new TcActSetAttr(defect, correction, TcAction.VERT_MARK_AS_CONNECTED, vert, "DANGLING", "0");
	}
	/**
	 * частный случай висячей / не висячей вершины  
	 * @param defect - исправляемый дефект
	 * @param vert - объект
	 * @param state - значение атрибута DANGLING
	 */
	static public TcActSetAttr DanglingVert (TcLocInfo defect, ITcObjVertex vert, boolean state) {
		return DanglingVert(defect, TcLocInfo.DO_MANUALLY, vert, state);
	}

}
