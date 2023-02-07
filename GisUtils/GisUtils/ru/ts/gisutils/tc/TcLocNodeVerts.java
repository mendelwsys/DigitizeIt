/*
 * Created on 09.08.2007
 *
 */
package ru.ts.gisutils.tc;

import java.util.*;

import ru.ts.gisutils.geometry.XY;

/**
 * @author yugl
 *
 * Информация о концевых вершинах в узле
 */
public class TcLocNodeVerts extends TcLocInfo {

	//----------------------
	// fields
	//----------------------

	/**
	 * Узел с вершинами
	 */ 
	public ITcObjNode node;
	
	/**
	 * Вершины узла 
	 */ 
	public List verts;
	
	//----------------------
	// constructors
	//----------------------
	
	/**
	 * защищенный пустой конструктор 
	 */
	protected TcLocNodeVerts () {
		_type = TcLocInfo.NODE_VERTS;
	}
	
	/**
	 * изначально список вершин пуст 
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param node - узел с вершинами
	 */
	public TcLocNodeVerts (TcIteration iteration, ITcObjNode node) {
		super(iteration);
		_type = TcLocInfo.NODE_VERTS;
		this.node = node;
		node.attachTcdata(this);
		place.extend(node);
		verts = new ArrayList();
	}

	/**
	 * нормальный случай - информация фиксируется, когда у узла есть хотя бы одна вершина 
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param node - узел с вершинами
	 * @param vert - вершина
	 */
	public TcLocNodeVerts (TcIteration iteration, ITcObjNode node, ITcObjVertex vert) {
		this(iteration, node);
		add(vert);
	}

	//----------------------
	// methods
	//----------------------
	
	/**
	 * Добавить вершину в список вершин узла 
	 * @param vert - вершина
	 */
	public void add (ITcObjVertex vert) {
		//? place.extend(vert.xy);
		verts.add(vert);
		vert.attachTcdata(this);
	}
	
	public String toString () {
		return "NODE_VERTS=" + _type + ", place=" + place + ", node " + node.tcid() + " + " + verts.size() + " vertices";
	}

	//----------------------
	// static
	//----------------------
	
	static protected int[] _autoCorrections = {
		TcLocInfo.SKIP_DEFECT, 
		TcLocInfo.MARK_NODES_AND_VERTS_AS_MATCHED
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
		case MARK_NODES_AND_VERTS_AS_MATCHED:
			TcActSetAttr action = TcActSetAttr.DanglingNode(this, MARK_NODES_AND_VERTS_AS_MATCHED, node, false);
			actions.add(action);				
			ListIterator itr = verts.listIterator();
			while (itr.hasNext()) {
				ITcObjVertex vert = (ITcObjVertex) itr.next();
				action = TcActSetAttr.DanglingVert(this, MARK_NODES_AND_VERTS_AS_MATCHED, vert, false);
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
	 * Сопоставление концевых вершин с узлами. Узлы-дубли уже должны быть вычищены.
	 * @param iteration - итерация, в рамках которой идет поиск
	 * @param nodes - сортированные узлы
	 * @param verts - сортированные концевые вершины
	 */
	static public void search (TcIteration iteration, List nodes, List verts) {
		if ((nodes.size() == 0) && (verts.size() == 0)) return;

		boolean hasMore = true;				
		ITcObjNode node = null;
		ITcObjVertex vert = null;
		ITcObjVertex prev = null;
		XY xy1 = new XY();
		XY xy2 = new XY();
		XY xy3 = new XY();
		
		Iterator nodesItr = nodes.iterator();
		Iterator vertsItr = verts.iterator();
		while (hasMore) {			
			// получение узла, если надо
			if (node == null) {			
				if (nodesItr.hasNext())	{
					node = (ITcObjNode) nodesItr.next();
					node.copyTo(xy1);
				} 
			}
			// получение вершины, если надо
			if (vert == null) {			
				if (vertsItr.hasNext()) {
					vert = (ITcObjVertex) vertsItr.next();
					vert.copyTo(xy2);
				}
			}
			
			// сопоставим вершину с предыдущей вершиной, если возможно 
			if (prev != null) {
				// ага, перед этим была вершина, не присоединенная к узлу ("висячая")
				if ((vert != null) && (iteration._geometry.compareYthenX(xy3, xy2) == 0)) {
					// координаты новой и старой повторяются
					TcLocDanglingVerts.addNewInstance(iteration, prev, vert);
					// идем на новый цикл с новой вершиной
					vert = null;
					continue;
				}
				// новая вершина к старой не монтируется
				if (prev.tcdata() == null) {
					// дефект не формировался, прежняя вершина была одиночной
					TcLocDanglingVert.addNewInstance(iteration, prev);
				}
				// прежняя вершина теперь не актуальна
				prev = null;
			}
				
			// сопоставим вершину с узлом, если возможно
			if (node != null) {
				// ага, узел есть 
				if ((vert != null) && (iteration._geometry.compareYthenX(xy1, xy2) == 0)) {
					// координаты узла и вершины совпадают
					TcLocNodeVerts.addNewInstance(iteration, node, vert);
					// идем на новый цикл с новой вершиной
					vert = null;
					continue;
				}
				
				// вершина к узлу не монтируется
				if ((vert == null) || (iteration._geometry.compareYthenX(xy1, xy2) < 0)) {
					// вершин нет или они дальше - узел устарел
					if (node.tcdata() == null) {
						// информация не создавалась, узел был одиночным
						TcLocDanglingNode.addNewInstance(iteration, node);
					}
					// идем на новый цикл с новым узлом
					node = null;
					continue;
				}
			}
				
			// вершина ни с чем не состыковалась
			if (vert != null) {
				// сохраним ее для сравнения в следующем цикле
				prev = vert;
				xy2.copyTo(xy3);
				// идем на новый цикл с новой вершиной
				vert = null;
				continue;
			}
			
			// нет больше ни узлов, ни вершин
			hasMore = false;
		}		
	}
	
	/**
	 * добавить данные о связанных узле и вершине
	 */
	static public void addNewInstance (TcIteration iteration, ITcObjNode node, ITcObjVertex vert) {
		TcLocNodeVerts info = (TcLocNodeVerts) node.tcdata();
		if (info == null) {
			// узел не имеет привязанных данных, значит, надо привязать
			info = new TcLocNodeVerts(iteration, node, vert);
			iteration.addFound(info);
		}
		else {
			// данные уже есть, просто добавляем вершину
			info.add(vert);
		}
	}
	
}
