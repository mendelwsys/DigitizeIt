/*
 * Created on 6 Sep 2007
 *
 */
package ru.ts.gisutils.tc;

import ru.ts.gisutils.geometry.IGetXY;

/**
 * @author yugl
 *
 * Операция перемещения объекта в новую точку
 */
public class TcActMoveXY extends TcAction {

	//----------------------
	// fields
	//----------------------

	/**
	 * Перемещаемый объект
	 */ 
	public ITcObjBase obj;
	
	/**
	 * Координаты точки
	 */ 
	public IGetXY xy;
	
	//----------------------
	// constructors
	//----------------------
	
	/**
	 * более общий вариант 
	 * @param defect - исправляемый дефект
	 * @param correction - способ исправления (для информации)
	 * @param type - тип операции исправления
	 * @param obj - объект
	 * @param xy - координаты 
	 */
	public TcActMoveXY (TcLocInfo defect, int correction, int type, ITcObjBase obj, IGetXY xy) {
		super(defect, correction, type);	// type = TcAction.OBJ_MOVE_TO или спец.случай
		this.obj = obj;
		this.xy = xy;
	}

	/**
	 * частный случай узла  
	 * @param defect - исправляемый дефект
	 * @param correction - способ исправления (для информации)
	 * @param node - объект
	 * @param xy - координаты 
	 */
	public TcActMoveXY (TcLocInfo defect, int correction, ITcObjNode node, IGetXY xy) {
		this(defect, correction, TcAction.NODE_MOVE_TO, node, xy);
	}
	/**
	 * частный случай узла  
	 * @param defect - исправляемый дефект
	 * @param node - объект
	 * @param xy - координаты 
	 */
	public TcActMoveXY (TcLocInfo defect, ITcObjNode node, IGetXY xy) {
		this(defect, TcLocInfo.DO_MANUALLY, node, xy);
	}

	/**
	 * частный случай вершины  
	 * @param defect - исправляемый дефект
	 * @param correction - способ исправления (для информации)
	 * @param node - объект
	 * @param xy - координаты 
	 */
	public TcActMoveXY (TcLocInfo defect, int correction, ITcObjVertex vert, IGetXY xy) {
		this(defect, correction, TcAction.VERT_MOVE_TO, vert, xy);
	}
	/**
	 * частный случай вершины  
	 * @param defect - исправляемый дефект
	 * @param node - объект
	 * @param xy - координаты 
	 */
	public TcActMoveXY (TcLocInfo defect, ITcObjVertex vert, IGetXY xy) {
		this(defect, TcLocInfo.DO_MANUALLY, vert, xy);
	}

}
