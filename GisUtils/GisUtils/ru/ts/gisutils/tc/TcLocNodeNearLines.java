/*
 * Created on 15.08.2007
 *
 */
package ru.ts.gisutils.tc;

import java.util.*;

import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.Rect;
import ru.ts.gisutils.geometry.XY;

/**
 * @author yugl
 *
 * Информация о полилиниях рядом с "висячим" узлом
 */
public class TcLocNodeNearLines extends TcLocInfo {

	//----------------------
	// fields
	//----------------------

	/**
	 * Висячий узел  
	 */ 
	public ITcObjNode node;
	
	/**
	 * Отрезки рядом с узлом 
	 */ 
	public List lines;
	
	//----------------------
	// constructors
	//----------------------
		
	/**
	 * защищенный пустой конструктор 
	 */
	protected TcLocNodeNearLines () {
		_type = TcLocInfo.NODE_NEAR_LINES;
	}
	
	/**
	 * изначально список отрезков пуст 
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param node - узел
	 */
	public TcLocNodeNearLines (TcIteration iteration, ITcObjNode node) {
		super(iteration);
		_type = TcLocInfo.NODE_NEAR_LINES;
		this.node = node;
		node.attachTcdata(this);
		place.extend(node);
		lines = new ArrayList();
	}
	
	/**
	 * нормальный случай - информация фиксируется, когда рядом с узлом есть линия 
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param node - узел 
	 * @param line - отрезок
	 */
	public TcLocNodeNearLines (TcIteration iteration, ITcObjNode node, TcObjLine line) {
		this(iteration, node);
		add(line);
	}

	//----------------------
	// methods
	//----------------------
	
	/**
	 * Добавить отрезок в список  
	 * @param line - отрезок
	 */
	public void add (TcObjLine line) {
		place.extend(line.getNotchXY());	// надо учесть точку пересечения с перпендикуляром
		lines.add(line);
		line.attachTcdata(this);		
	}
	
	public String toString () {
		return "NODE_NEAR_LINES=" + _type + ", place=" + place + ", node " + node.tcid() + " + " + lines.size() + " lines";
	}

	//----------------------
	// static
	//----------------------
	
	static protected int[] _autoCorrections = {
		TcLocInfo.SKIP_DEFECT, 
		TcLocInfo.MOVE_NODE_TO_NEAREST_LINE, 
		TcLocInfo.MOVE_NEAREST_LINE_TO_NODE
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
		XY xy = new XY();
		TcObjLine line = null;
		TcAction action = null;
		switch (correction) {
		case SKIP_DEFECT:
			break;
		case MOVE_NODE_TO_NEAREST_LINE:
			line = _findNearestLine(xy);
			if (line == null) break;
			action = _makeBreakLineAction(xy, line, MOVE_NODE_TO_NEAREST_LINE);
			if (action != null) actions.add(action);				
			action = new TcActMoveXY(this, node, xy);
			actions.add(action);
			action = TcActSetAttr.DanglingNode(this, MARK_NODE_AS_DANGLING, node, false);
			actions.add(action);				
			break;
		case MOVE_NEAREST_LINE_TO_NODE:
			line = _findNearestLine(xy);
			if (line == null) break;
			action = _makeBreakLineAction(xy, line, MOVE_NEAREST_LINE_TO_NODE);
			if (action != null) {
				((TcActBreakPline)action).xy = node;	// разорванные концы надо передвинуть к узлу
				actions.add(action);				
				action = TcActSetAttr.DanglingNode(this, MARK_NODE_AS_DANGLING, node, false);
				actions.add(action);				
			}
			break;
		default:
		// ToDo - throw an exception
		}		
		return actions;
	}
	
	// returns line that is the nearest to the node, xy is a point of line breaking
	protected TcObjLine _findNearestLine (XY xy) {
		TcObjLine nearest = null;
		double dmin = 0;
		IGetXY nxy = node;
		ListIterator itr = lines.listIterator();
		while (itr.hasNext()) {
			TcObjLine line = (TcObjLine) itr.next();
			line.getNotchXY(xy);
			double d = _iteration.geometry().distance2(nxy, xy);
			if (nearest != null) {
				if (d >= dmin) continue;
			}
			dmin = d;
			nearest = line;
		}
		return nearest; 
	}
		
	//----------------------
	// iteration
	//----------------------
	
	/**
	 * Сопоставление "висячих" узлов с отрезками полилиний. 
	 * @param iteration - итерация, в рамках которой идет поиск
	 * @param nodes - узлы
	 * @param plines - полилинии
	 */
	static public void search (TcIteration iteration, List nodes, List plines) {
		// to keep a rectangle around a node
		Rect rect = Rect.empty();
		// nearest point on the line 
		XY xy = new XY();		
		// for every node
		Iterator nodesItr = nodes.iterator();		
		while (nodesItr.hasNext()) {
			// берем очередной узел			
			ITcObjNode node = (ITcObjNode) nodesItr.next();
			double nx = node.getX();
			double ny = node.getY();
			// готовим прямоугольник, в котором будем искать близкие отрезки 
      rect.set(nx, nx, ny, ny);
			rect.inflate(iteration._delta);
			// prepare list of polylines that are to be considered with the node
			List actives = iteration.getTcStore().filter(plines, rect);
			Iterator activesItr = actives.iterator();
			while (activesItr.hasNext()) {			
				ITcObjPolyline pline = (ITcObjPolyline) activesItr.next();
				// get first vertex that is a start of first line
				double x1 = pline.verts().getXY(0).getX();
				double y1 = pline.verts().getXY(0).getY();
				// теперь последовательно формируем отрезки из вершин и проверяем
				int size = pline.verts().size();
				for (int i=1; i<size; i++) {
					// get next vertex that is a finish of line
					double x2 = pline.verts().getXY(i).getX();
					double y2 = pline.verts().getXY(i).getY();
					// если узел рядом с концом отрезка и вершина не последняя, используем ее
					if (TcLocInfo.checkNearness(nx,ny, x2,y2, iteration)) {
						if (i != (size-1)) {
							addNewInstance(iteration, node, new TcObjLine(pline, i-1, 1));
						}
					}
					else {
						// находим пересечение перпендикуляра с отрезком
						double t = iteration._geometry.perpendicular(xy, nx,ny, x1,y1, x2,y2);
						if ((t>0) && (t<1)) {
							// перпендикуляр попал внутрь отрезка, проверим его
							if (TcLocInfo.checkNearness(nx,ny, xy.x,xy.y, iteration)) {
								// берем
								addNewInstance(iteration, node, new TcObjLine(pline, i-1, t));
							}
						}
					}
					// make a finish of the current line to be a start of the next line
					x1 = x2;
					y1 = y2;
				}
			}			
		}		
	}
	
	/**
	 * добавить данные об отрезке рядом с узлом
	 */
	static public void addNewInstance (TcIteration iteration, ITcObjNode node, TcObjLine line) {
		TcLocNodeNearLines info = (TcLocNodeNearLines) node.tcdata();
		if (info == null) {
			info = new TcLocNodeNearLines(iteration, node, line);
			iteration.addFound(info);
		}
		else {
			info.add(line);
		}
	}
	
}

