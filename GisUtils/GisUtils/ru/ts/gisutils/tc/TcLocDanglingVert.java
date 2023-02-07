/*
 * Created on 09.08.2007
 *
 */
package ru.ts.gisutils.tc;

import java.util.*;

import ru.ts.gisutils.geometry.IGetXY;

/**
 * @author yugl
 *
 * Информация о вершине без узлов 
 */
public class TcLocDanglingVert extends TcLocInfo {

	//----------------------
	// fields
	//----------------------

	/**
	 * вершина без узлов
	 */ 
	public ITcObjVertex vert;
	
	//----------------------
	// constructors
	//----------------------
	
	/**
	 * защищенный пустой конструктор 
	 */
	protected TcLocDanglingVert () {
		_type = TcLocInfo.DANGLING_VERT;
	}
	
	/**
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param vertex - вершина без узла
	 */
	public TcLocDanglingVert (TcIteration iteration, ITcObjVertex vertex) {
		super(iteration);
		_type = TcLocInfo.DANGLING_VERT;
		this.vert = vertex;
		place.extend(vertex);
	}

	//----------------------
	// methods
	//----------------------
	
	public String toString () {
		return "DANGLING_VERT=" + _type + ", place=" + place + ", vertex " + vert.tcid();
	}
	
	//----------------------
	// static
	//----------------------
	
	static protected int[] _autoCorrections = {
		TcLocInfo.SKIP_DEFECT, 
		TcLocInfo.MARK_VERT_AS_DANGLING, 
		TcLocInfo.MAKE_NODE_FOR_VERT
	};
	/**
	 * Предлагает автоматические способы исправления дефекта. 
	 * Набор всегда не пуст, так как включает операцию Skip. 
	 */
	static public int[] autoCorrections () {
		return (int[])_autoCorrections.clone();
	}	
	/**
	 * Предлагает способ исправления дефекта "по умолчанию". 
	 */
	static public int defaultCorrection () {
		return _autoCorrections[1];
	}	
	/**
	 * Предлагает автоматические способы исправления дефекта. 
	 * Набор всегда не пуст, так как включает операцию Skip. 
	 */
	public int[] getAutoCorrections () {
		return autoCorrections();
	}	
	/**
	 * Предлагает способ исправления дефекта "по умолчанию". 
	 */
	public int getDefaultCorrection () {
		return defaultCorrection();
	}
	
	/**
	 * Возвращает автоматически сформированный набор операций исправления дефекта (возможно, пустой). 
	 * @param correction - способ исправления дефекта
	 */
	public List getAutoActions (int correction) {
		ArrayList actions = new ArrayList();
		TcAction action = null;
		switch (correction) {
		case SKIP_DEFECT:
			break;
		case MARK_VERT_AS_DANGLING:
			action = TcActSetAttr.DanglingVert(this, MARK_VERT_AS_DANGLING, vert, true);
			actions.add(action);				
			break;
		case MAKE_NODE_FOR_VERT:
			IGetXY xy = vert;
			if (xy != null) {
				action = new TcActNewNode(this, MAKE_NODE_FOR_VERT, xy);
				actions.add(action);
			}
			break;
		default:
		// ToDo - throw an exception
		}		
		return actions;
	}
	
	//----------------------
	// iteration
	//----------------------
	
	/**
	 * добавить висячую вершину
	 */
	static public void addNewInstance (TcIteration iteration, ITcObjVertex vert) {
		TcLocDanglingVert info = new TcLocDanglingVert(iteration, vert);
		iteration.addFound(info); 
		vert.attachTcdata(info);
	}
	
	
}
