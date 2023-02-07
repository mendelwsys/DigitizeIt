/*
 * Created on 31 Aug 2007
 *
 */
package ru.ts.gisutils.tc;

import java.util.*;

import ru.ts.gisutils.geometry.IGetXY;

/**
 * @author yugl
 *
 * Информация о совпадающих "висячих" концевых вершинах  
 */
public class TcLocDanglingVerts extends TcLocInfo {

	//----------------------
	// fields
	//----------------------

	/**
	 * Близкие вершины  
	 */ 
	public List verts;
	
	//----------------------
	// constructors
	//----------------------
	
	/**
	 * защищенный пустой конструктор 
	 */
	protected TcLocDanglingVerts () {
		_type = TcLocInfo.DANGLING_VERTS;
	}
	
	/**
	 * изначально список вершин пуст 
	 * @param iteration - итерация (номер и параметры поиска)
	 */
	public TcLocDanglingVerts (TcIteration iteration) {
		super(iteration);
		_type = TcLocInfo.DANGLING_VERTS;
		verts = new ArrayList();
	}

	/**
	 * нормальный случай - информация фиксируется, когда есть две совпадающие вершины 
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param vert1 - вершина1
	 * @param vert2 - вершина2
	 */
	public TcLocDanglingVerts (TcIteration iteration, ITcObjVertex vert1, ITcObjVertex vert2) {
		this(iteration);
		add(vert1);
		add(vert2);
	}

	//----------------------
	// methods
	//----------------------
	
	/**
	 * Добавить вершину в список вершин  
	 * @param vert - вершина
	 */
	public void add (ITcObjVertex vert) {
		place.extend(vert);
		verts.add(vert);
		vert.attachTcdata(this);
	}
	
	//----------------------
	// static
	//----------------------
	
	static protected int[] _autoCorrections = {
		TcLocInfo.SKIP_DEFECT, 
		TcLocInfo.MARK_VERTS_AS_DANGLING, 
		TcLocInfo.MAKE_NODE_FOR_VERTS, 
		TcLocInfo.JOIN_PLINES_OR_MAKE_NODE
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
		switch (correction) {
		case SKIP_DEFECT:
			break;
		case MARK_VERTS_AS_DANGLING:
			itr = verts.listIterator();
			while (itr.hasNext()) {
				ITcObjVertex vert = (ITcObjVertex) itr.next();
				TcActSetAttr action = TcActSetAttr.DanglingVert(this, MARK_VERTS_AS_DANGLING, vert, true);
				actions.add(action);				
			}
			break;
		case MAKE_NODE_FOR_VERTS:
			_makeNode4Verts(MAKE_NODE_FOR_VERTS, actions);
			break;
		case JOIN_PLINES_OR_MAKE_NODE:
			if (verts.size() == 2) {
				itr = verts.listIterator();
				ITcObjVertex vert1 = (ITcObjVertex) itr.next();
				ITcObjVertex vert2 = (ITcObjVertex) itr.next();
				// сравним их tcparent, то есть tcid родительских полилиний
				if ((vert1.tcparent()).compareTo(vert2.tcparent()) != 0) {	 
					// разные полилинии - можно делать JOIN
					TcActJoin2Plines action = new TcActJoin2Plines(this, JOIN_PLINES_OR_MAKE_NODE, vert1, vert2);
					actions.add(action);				
					break;
				}
			}
			_makeNode4Verts(JOIN_PLINES_OR_MAKE_NODE, actions);
			break;
		default:
		// ToDo - throw an exception
		}		
		return actions;
	}
	
	protected void _makeNode4Verts (int correction, List actions) {
		ListIterator itr = verts.listIterator();
		IGetXY xy = null;
		if (itr.hasNext()) {
			ITcObjVertex vert = (ITcObjVertex) itr.next();
			xy = vert;
		}
		if (xy != null) {
			TcActNewNode action = new TcActNewNode(this, correction, xy);
			actions.add(action);				
		}
	}
	
	//----------------------
	// iteration
	//----------------------
	
	/**
	 * добавить данные о совпадающих "висячих" вершинах
	 */
	static public void addNewInstance (TcIteration iteration, ITcObjVertex vert1, ITcObjVertex vert2) {
		TcLocDanglingVerts info = (TcLocDanglingVerts) vert1.tcdata();
		if (info == null) {
			// новый дефект
			info = new TcLocDanglingVerts(iteration, vert1, vert2);
			iteration.addFound(info);
		}
		else {
			// данные уже есть, просто добавляем вершину
			info.add(vert2);
		}
	}
	
}
