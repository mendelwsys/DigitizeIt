/*
 * Created on 06.08.2007
 *
 */
package ru.ts.gisutils.tc;

import java.util.*;
//import java.util.ArrayList;

import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.Rect;
import ru.ts.gisutils.geometry.XY;

/**
 * @author yugl
 *
 * "Дефект" близко расположенных узлов (попадающих в квадрат со стороной "дельта") 
 */
public class TcLocNearNodes extends TcLocInfo {

	//----------------------
	// fields
	//----------------------

	/**
	 * Близко расположенные узлы (коллекция элементов TcNode)
	 */ 
	public List nodes;
	
	//----------------------
	// constructors
	//----------------------
	
	/**
	 * защищенный пустой конструктор 
	 */
	protected TcLocNearNodes () {
		_type = TcLocInfo.NEAR_NODES;
	}
	
	/**
	 * изначально список узлов пуст 
	 * @param iteration - итерация (номер и параметры поиска)
	 */
	public TcLocNearNodes (TcIteration iteration) {
		super(iteration);
		_type = TcLocInfo.NEAR_NODES;
		nodes = new ArrayList();
	}

	/**
	 * нормальный случай - дефект фиксируется, когда есть хотя бы два близко расположенных узла 
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param node1 - первый узел
	 * @param node2 - второй узел
	 */
	public TcLocNearNodes (TcIteration iteration, ITcObjNode node1, ITcObjNode node2) {
		this(iteration);
		add(node1);
		add(node2);
	}

	//----------------------
	// methods
	//----------------------
	
	/**
	 * Добавить узел в список близко расположенных 
	 * @param node - узел
	 */
	public void add (ITcObjNode node) {
		place.extend(node);
		nodes.add(node);
		node.attachTcdata(this);
	}
	
	public String toString () {
		return "NEAR_NODES=" + _type + ", place=" + place + ", " + nodes.size() + " nodes";
	}

	//----------------------
	// static
	//----------------------
	
	static protected int[] _autoCorrections = {
		TcLocInfo.SKIP_DEFECT, 
		TcLocInfo.DEL_NEAR_NODES_BUT_FIRST,
		TcLocInfo.DEL_NEAR_NODES_AND_BUILD_NEW
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
		case DEL_NEAR_NODES_BUT_FIRST:
			itr = nodes.listIterator();
			if (itr.hasNext()) itr.next();
			while (itr.hasNext()) {
				node = (ITcObjNode) itr.next();
				TcActDelObject action = new TcActDelObject(this, DEL_NEAR_NODES_BUT_FIRST, node);
				actions.add(action);				
			}
			break;
		case DEL_NEAR_NODES_AND_BUILD_NEW:
			itr = nodes.listIterator();
			XY xy = null;
			while (itr.hasNext()) {
				node = (ITcObjNode) itr.next();
				if (xy == null) { 
					xy = new XY(node);
				}
				else {
					xy.add(node);
				}
				TcActDelObject action = new TcActDelObject(this, DEL_NEAR_NODES_AND_BUILD_NEW, node);
				actions.add(action);				
			}
			if (xy != null) {
				double k = 1.0 / nodes.size();
				xy.mul( k );
				TcActNewNode action = new TcActNewNode(this, DEL_NEAR_NODES_AND_BUILD_NEW, xy);
				actions.add(action);				
			}
			break;
		default:
		// ToDo - throw an exception
		}		
		return actions;
	}
	
	//----------------------
	// check of nearness
	//----------------------
	
	/**
	 * Проверка двух точек на близость в смысле данного дефекта 
	 * @param iteration содержит параметры "близости"
	 * @return true, если координаты x1,y1 близки к x2,y2
	 */
	static public boolean checkPair (
			double x1, double y1, double x2, double y2, TcIteration iteration) 
	{
		if (iteration._geometry.nearBoth(x1, y1, x2, y2, iteration._delta)) return true;
		return false;
	}
	/**
	 * Проверка двух точек на близость в смысле данного дефекта 
	 * @param iteration содержит параметры "близости"
	 * @return true, если координаты xy1 близки к xy2
	 */
	static public boolean checkPair (IGetXY xy1, IGetXY xy2, TcIteration iteration) {
		return checkPair(xy1.getX(), xy1.getY(),  xy2.getX(), xy2.getY(), iteration);
	}

	/**
	 * Проверка точки на возможность попадания в "близкие"  
	 * @param iteration содержит параметры "близости"
	 * @param rect - место дефекта
	 * @return true, если координаты точки x,y подходят
	 */
	static public boolean checkNext (Rect rect, double x1, double y1, TcIteration iteration) {
		Rect test = rect.copyOf();
		test.extend(x1, y1);
		if (!test.small(iteration._delta, iteration._geometry)) return false;
		return true;
	}
	/**
	 * Проверка точки на возможность попадания в "близкие"  
	 * @param iteration содержит параметры "близости"
	 * @param rect - место дефекта
	 * @return true, если координаты точки xy подходят
	 */
	static public boolean checkNext (Rect rect, IGetXY xy, TcIteration iteration) {
		return checkNext(rect, xy.getX(), xy.getY(), iteration);
	}
	
	/**
	 * Проверка точки на возможность включения в данный экземпляр дефекта 
	 * @return true, если координаты точки x,y подходят
	 */
	public boolean checkNext (double x, double y) {
		return checkNext(this.place, x, y, _iteration);
	}
	/**
	 * Проверка точки на возможность включения в данный экземпляр дефекта 
	 * @return true, если координаты точки xy подходят
	 */
	public boolean checkNext (IGetXY xy) {
		return checkNext(xy.getX(), xy.getY());
	}
	
	//----------------------
	// iteration
	//----------------------
	
	/**
	 * Поиск близких друг к другу узлов.
	 * @param iteration - итерация, в рамках которой идет поиск
	 * @param nodes - сортированные узлы
	 */
	static public void search (TcIteration iteration, List nodes) {
		// prepare list of nodes that are to be compared with others
		ArrayList actives = new ArrayList();
		
		Iterator nodesItr = nodes.iterator();		
		while (nodesItr.hasNext()) {			
			ITcObjNode node = (ITcObjNode) nodesItr.next();
			double x1 = node.getX();
			double y1 = node.getY();
			// узлы, чья у-координата далеко, надо убрать из списка активных,
			// с остальными надо провести проверку на близость по х.
			Iterator activesItr = actives.iterator();
			boolean closeY = false;
			while (activesItr.hasNext()) {			
				ITcObjNode active = (ITcObjNode) activesItr.next();
				double x2 = active.getX();
				double y2 = active.getY();
				if (!closeY) {
					if (!iteration._geometry.near(y1, y2, iteration._delta)) activesItr.remove();
					else closeY = true;
				}
				if (closeY) {
					if (!iteration._geometry.near(x1, x2, iteration._delta)) continue;
					// ага! мы близко, нужна окончательная проверка
					if (TcLocNearNodes.addNewInstance(iteration, active, node)) break;
					// если узел отнесен к одному дефекту, к другому его относить уже нельзя
				}
			}
				
			// если узел был близок к одному из активных (создан дефект), то сам он в активные не попадает
			if (node.tcdata() != null) continue;
			// иначе он должен быть добавлен к активным узлам последним
			actives.add(actives.size(), node);
		}
	}
	
	/**
	 * добавить дефект
	 */
	static public boolean addNewInstance (TcIteration iteration, ITcObjNode node1, ITcObjNode node2) {
		TcLocNearNodes info = (TcLocNearNodes) node1.tcdata();
		if (info == null) {
			if ( !TcLocNearNodes.checkPair(node1, node2, iteration) ) return false;					
			// новый дефект
			info = new TcLocNearNodes(iteration, node1, node2);
			iteration.addFound(info);
		}
		else {
			if ( !TcLocNearNodes.checkNext(info.place, node2, iteration) ) return false;					
			// данные уже есть, просто добавляем узел
			info.add(node2);
		}
		return true;
	}
	

}
