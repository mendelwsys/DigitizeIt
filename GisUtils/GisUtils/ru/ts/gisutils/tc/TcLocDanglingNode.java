/*
 * Created on 09.08.2007
 *
 */
package ru.ts.gisutils.tc;

import java.util.*;

/**
 * @author yugl
 *
 * Информация об узле без вершин
 */
public class TcLocDanglingNode extends TcLocInfo {

	//----------------------
	// fields
	//----------------------

	/**
	 * Узел без вершин
	 */ 
	public ITcObjNode node;
	
	//----------------------
	// constructors
	//----------------------
	
	/**
	 * защищенный пустой конструктор 
	 */
	protected TcLocDanglingNode () {
		_type = TcLocInfo.DANGLING_NODE;
	}
	
	/**
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param node - узел без вершин
	 */
	public TcLocDanglingNode (TcIteration iteration, ITcObjNode node) {
		super(iteration);
		_type = TcLocInfo.DANGLING_NODE;
		this.node = node;
		place.extend(node);
	}

	//----------------------
	// methods
	//----------------------
	
	public String toString () {
		return "DANGLING_NODE=" + _type + ", place=" + place + ", node " + node.tcid();
	}
	
	//----------------------
	// static
	//----------------------
	
	static protected int[] _autoCorrections = {
		TcLocInfo.SKIP_DEFECT, 
		TcLocInfo.MARK_NODE_AS_DANGLING
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
		switch (correction) {
		case SKIP_DEFECT:
			break;
		case MARK_NODE_AS_DANGLING:
			TcActSetAttr action = TcActSetAttr.DanglingNode(this, MARK_NODE_AS_DANGLING, node, true);
			actions.add(action);				
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
	 * добавить висячий узел
	 */
	static public void addNewInstance (TcIteration iteration, ITcObjNode node) {
		TcLocDanglingNode info = new TcLocDanglingNode(iteration, node);
		iteration.addFound(info); 
		node.attachTcdata(info);
	}
	
}
