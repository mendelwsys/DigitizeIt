/*
 * Created on 03.08.2007
 *
 */
package ru.ts.gisutils.tc;

import java.util.*;
//import java.util.ArrayList;

import ru.ts.gisutils.geometry.IGetXY;

/**
 * @author yugl
 *
 * Дефект дублирования узлов
 */
public class TcLocDupeNodes extends TcLocInfo {

	//----------------------
	// fields
	//----------------------

	/**
	 * Дублированные узлы (коллекция элементов TcNode)
	 */ 
	public List nodes;
	
	//----------------------
	// constructors
	//----------------------

	/**
	 * защищенный пустой конструктор 
	 */
	protected TcLocDupeNodes () {
		_type = TcLocInfo.DUPE_NODES;
	}
	
	/**
	 * изначально список узлов пуст 
	 * @param iteration - итерация (номер и параметры поиска)
	 */
	public TcLocDupeNodes (TcIteration iteration) {
		super(iteration);
		_type = TcLocInfo.DUPE_NODES;
		nodes = new ArrayList();
	}

	/**
	 * нормальный случай - дефект фиксируется, когда есть хотя бы два узла-дубля 
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param node1 - первый узел-дубль
	 * @param node2 - второй узел-дубль
	 */
	public TcLocDupeNodes (TcIteration iteration, ITcObjNode node1, ITcObjNode node2) {
		this(iteration);
		add(node1);
		add(node2);
	}

	//----------------------
	// methods
	//----------------------
	
	/**
	 * Добавить узел в список дублей 
	 * @param node - узел-дубль
	 */
	public void add (ITcObjNode node) {
		place.extend(node);
		nodes.add(node);
		node.attachTcdata(this);
	}
	
	public String toString () {
		return "DUPE_NODES=" + _type + ", place=" + place + ", " + nodes.size() + " nodes";
	}

	//----------------------
	// correction
	//----------------------
	
	static protected int[] _autoCorrections = {
		TcLocInfo.SKIP_DEFECT, 
		TcLocInfo.DEL_DUPE_NODES_BUT_FIRST,
		TcLocInfo.DEL_DUPE_NODES_AND_BUILD_NEW
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
		ListIterator itr = null;
		ITcObjNode node = null;
		switch (correction) {
		case SKIP_DEFECT:
			break;
		case DEL_DUPE_NODES_BUT_FIRST:
			itr = nodes.listIterator();
			if (itr.hasNext()) itr.next();
			while (itr.hasNext()) {
				node = (ITcObjNode) itr.next();
				TcActDelObject action = new TcActDelObject(this, DEL_DUPE_NODES_BUT_FIRST, node);
				actions.add(action);				
			}
			break;
		case DEL_DUPE_NODES_AND_BUILD_NEW:
			itr = nodes.listIterator();
			IGetXY xy = null;
			while (itr.hasNext()) {
				node = (ITcObjNode) itr.next();
				if (xy == null) xy = node;
				TcActDelObject action = new TcActDelObject(this, DEL_DUPE_NODES_AND_BUILD_NEW, node);
				actions.add(action);				
			}
			if (xy != null) { 
				TcActNewNode action = new TcActNewNode(this, DEL_DUPE_NODES_AND_BUILD_NEW, xy);
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
	 * Поиск узлов-дубликатов (узлов с повторяющимися координатами)
	 * @param iteration - итерация, в рамках которой идет поиск
	 * @param nodes - сортированные узлы
	 */
	static public void search (TcIteration iteration, List nodes) {
		ITcObjNode original = null;
		
		Iterator nodesItr = nodes.iterator();
		if (nodesItr.hasNext()) original = (ITcObjNode) nodesItr.next();
		
		while (nodesItr.hasNext()) {
			ITcObjNode node = (ITcObjNode) nodesItr.next();
			// поиск дубликатов для узла
			if (iteration.geometry().compareYthenX(original, node) == 0) {
				// найден дубликат, это дефект
				TcLocDupeNodes.addNewInstance(iteration, original, node);
			}
			else {
				// нет дублирования - очень хорошо
				original = node;
			}
		}
	}
	
	/**
	 * добавить дефект
	 */
	static public void addNewInstance (TcIteration iteration, ITcObjNode node1, ITcObjNode node2) {
		TcLocDupeNodes info = (TcLocDupeNodes) node1.tcdata();
		if (info == null) {
			// новый дефект
			info = new TcLocDupeNodes(iteration, node1, node2);
			iteration.addFound(info);
		}
		else {
			// данные уже есть, просто добавляем узел
			info.add(node2);
		}
	}
	

}
