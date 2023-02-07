/*
 * Created on 13.08.2007
 *
 */
package ru.ts.gisutils.tc;

import java.util.*;

import ru.ts.gisutils.geometry.IGetXY;

/**
 * @author yugl
 *
 * Информация об узлах рядом с "висячей" концевой вершиной
 */
public class TcLocVertNearNodes extends TcLocInfo {

	//----------------------
	// fields
	//----------------------

	/**
	 * Висячая вершина  
	 */ 
	public ITcObjVertex vert;
	
	/**
	 * Узлы рядом с вершиной 
	 */ 
	public List nodes;
	
	//----------------------
	// constructors
	//----------------------
	
	/**
	 * защищенный пустой конструктор 
	 */
	protected TcLocVertNearNodes () {
		_type = TcLocInfo.VERT_NEAR_NODES;
	}
	
	/**
	 * изначально список вершин пуст 
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param vert - вершина
	 */
	public TcLocVertNearNodes (TcIteration iteration, ITcObjVertex vert) {
		super(iteration);
		_type = TcLocInfo.VERT_NEAR_NODES;
		this.vert = vert;
		vert.attachTcdata(this);
		place.extend(vert);
		nodes = new ArrayList();
	}

	/**
	 * нормальный случай - информация фиксируется, когда рядом с вершиной есть узел 
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param vert - вершина
	 * @param node - узел около вершины
	 */
	public TcLocVertNearNodes (TcIteration iteration, ITcObjVertex vert, ITcObjNode node) {
		this(iteration, vert);
		add(node);
	}

	//----------------------
	// methods
	//----------------------
	
	/**
	 * Добавить узел в список  
	 * @param node - узел
	 */
	public void add (ITcObjNode node) {
		place.extend(node);
		nodes.add(node);
		//узел может быть около нескольких вершин, нет смысла привязывать данные
		//node.tcdata = this;		
	}
	
	public String toString () {
		return "VERT_NEAR_NODES=" + _type + ", place=" + place + ", vertex " + vert.tcid() + " + " + nodes.size() + " nodes";
	}

	//----------------------
	// static
	//----------------------
	
	static protected int[] _autoCorrections = {
		TcLocInfo.SKIP_DEFECT, 
		TcLocInfo.MOVE_VERT_TO_NEAREST_NODE
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
		case MOVE_VERT_TO_NEAREST_NODE:
			ITcObjNode node = _findNearestNode();
			if (node == null) break;
			TcAction action = new TcActMoveXY(this, MOVE_VERT_TO_NEAREST_NODE, vert, node);
			actions.add(action);				
			action = TcActSetAttr.DanglingVert(this, MARK_VERT_AS_DANGLING, vert, false);
			actions.add(action);				
			action = TcActSetAttr.DanglingNode(this, MARK_NODE_AS_DANGLING, node, false);
			actions.add(action);				
			break;
		default:
		// ToDo - throw an exception
		}		
		return actions;
	}
	
	// returns line that is the nearest to the node, xy is a point of line breaking
	protected ITcObjNode _findNearestNode () {
		ITcObjNode nearest = null;
		double dmin = 0;
		IGetXY xy = vert;
		ListIterator itr = nodes.listIterator();
		while (itr.hasNext()) {
			ITcObjNode node = (ITcObjNode) itr.next();
			double d = _iteration._geometry.distance2(node, xy);
			if (nearest != null) {
				if (d >= dmin) continue;
			}
			dmin = d;
			nearest = node;
		}
		return nearest; 
	}
	
	//----------------------
	// check of nearness
	//----------------------
		
	/**
	 * Проверка точки на возможность включения в данный экземпляр дефекта 
	 * @return true, если координаты точки x,y подходят
	 */
	public boolean checkNearness (double x, double y) {
		return checkNearness(vert.getX(), vert.getY(), x, y, _iteration);
	}
	/**
	 * Проверка точки на возможность включения в данный экземпляр дефекта 
	 * @return true, если координаты точки xy подходят
	 */
	public boolean checkNearness (IGetXY xy) {
		return checkNearness(xy.getX(), xy.getY());
	}
	
	//----------------------
	// iteration
	//----------------------
	
	/**
	 * Сопоставление "висячих" концевых вершин с узлами. 
	 * @param iteration - итерация, в рамках которой идет поиск
	 * @param nodes - сортированные узлы
	 * @param verts - сортированные висячие вершины
	 */
	static public void search (TcIteration iteration, List nodes, List verts) {
		// prepare list of nodes that are to be compared with vertices
		ArrayList actives = new ArrayList();
		
		Iterator nodesItr = nodes.iterator();		
		Iterator vertsItr = verts.iterator();		
		ITcObjNode node = null;
		while (vertsItr.hasNext()) {
			// берем очередную вершину			
			ITcObjVertex vert = (ITcObjVertex) vertsItr.next();
			double x1 = vert.getX();
			double y1 = vert.getY();
			// узлы, чья у-координата далеко, надо убрать из списка активных
			Iterator activesItr = actives.iterator();
			while (activesItr.hasNext()) {			
				ITcObjNode active = (ITcObjNode) activesItr.next();
				double y2 = active.getY();
				if (!iteration._geometry.near(y1, y2, iteration._delta)) activesItr.remove();
			}
			// дополним список активных узлами из основного списка
			if (node != null) {
				double y2 = node.getY();
				if (iteration._geometry.near(y1, y2, iteration._delta)) {
					// добавим узел в конец списка активных
					actives.add(actives.size(), node);
					node = null;
				} 
				else {
					// the node has become not actual now, forget about it
					if (iteration.geometry().compareY(y2, y1) < 0) node = null;
				}
			}
			if (node == null) {
				while (nodesItr.hasNext()) {			
					node = (ITcObjNode) nodesItr.next();
					double y2 = node.getY();
					if (!iteration._geometry.near(y1, y2, iteration._delta)) {
						if (iteration.geometry().compareY(y2, y1) < 0) continue;
						else break;
					}
					// добавим узел в конец списка активных
					actives.add(actives.size(), node);
				}
			}
			
			// список активных узлов актуализирован, вершину надо проверить на близость к ним
			activesItr = actives.iterator();
			while (activesItr.hasNext()) {			
				ITcObjNode active = (ITcObjNode) activesItr.next();
				double x2 = active.getX();
				double y2 = active.getY();
				if (!iteration._geometry.near(x1, x2, iteration._delta)) continue;
									
				// ага! мы близко, нужна окончательная проверка
				if ( !checkNearness(x1,y1, x2,y2, iteration) ) continue;					
										
				// это дефект					
				TcLocVertNearNodes.addNewInstance(iteration, vert, active);
			}
		}
		
	}
	
	/**
	 * добавить данные об узле рядом с вершиной
	 */
	static public void addNewInstance (TcIteration iteration, ITcObjVertex vert, ITcObjNode node) {
		TcLocVertNearNodes info = (TcLocVertNearNodes) vert.tcdata();
		if (info == null) {
			// новый дефект
			info = new TcLocVertNearNodes(iteration, vert, node);
			iteration.addFound(info);
		}
		else {
			info.add(node);
		}
	}
	
}
