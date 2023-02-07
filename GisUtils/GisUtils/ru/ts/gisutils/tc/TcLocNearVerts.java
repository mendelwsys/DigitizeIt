/*
 * Created on 13.08.2007
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
 * Информация о близких "висячих" концевых вершинах 
 */
public class TcLocNearVerts extends TcLocInfo {

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
	protected TcLocNearVerts () {
		_type = TcLocInfo.NEAR_VERTS;
	}
	
	/**
	 * изначально список вершин пуст 
	 * @param iteration - итерация (номер и параметры поиска)
	 */
	public TcLocNearVerts (TcIteration iteration) {
		super(iteration);
		_type = TcLocInfo.NEAR_VERTS;
		verts = new ArrayList();
	}

	/**
	 * нормальный случай - информация фиксируется, когда есть две близкие вершины 
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param vert1 - вершина1
	 * @param vert2 - вершина2
	 */
	public TcLocNearVerts (TcIteration iteration, ITcObjVertex vert1, ITcObjVertex vert2) {
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
	
	public String toString () {
		return "NEAR_VERTS=" + _type + ", place=" + place + ", " + verts.size() + " vertices";
	}

	//----------------------
	// static
	//----------------------
	
	static protected int[] _autoCorrections = {
		TcLocInfo.SKIP_DEFECT, 
		TcLocInfo.MOVE_VERTS_TO_FIRST_ONE, 
		TcLocInfo.MOVE_VERTS_TO_CENTER
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
		ITcObjVertex vert = null;
		XY xy = null;
		switch (correction) {
		case SKIP_DEFECT:
			break;
		case MOVE_VERTS_TO_FIRST_ONE:
			itr = verts.listIterator();
			if (!itr.hasNext()) break;
			vert = (ITcObjVertex) itr.next();
			xy = new XY(vert);
			while (itr.hasNext()) {
				vert = (ITcObjVertex) itr.next();
				TcActMoveXY action = new TcActMoveXY(this, MOVE_VERTS_TO_FIRST_ONE, vert, xy);
				actions.add(action);				
			}
			break;
		case MOVE_VERTS_TO_CENTER:
			itr = verts.listIterator();
			while (itr.hasNext()) {
				vert = (ITcObjVertex) itr.next();
				if (xy == null) { 
					xy = new XY(vert);
				}
				else {
					xy.add(vert);
				}
			}
			if (xy != null) {
				double k = 1.0 / verts.size();
				xy.mul( k );
				itr = verts.listIterator();
				while (itr.hasNext()) {
					vert = (ITcObjVertex) itr.next();
					TcActMoveXY action = new TcActMoveXY(this, MOVE_VERTS_TO_CENTER, vert, xy);
					actions.add(action);				
				}
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
	 * Поиск близких висячих концевых вершин.
	 * @param iteration - итерация, в рамках которой идет поиск
	 * @param verts - сортированные концевые вершины
	 */
	static public void search (TcIteration iteration, List verts) {
		// prepare list of vertices that are to be compared with others
		ArrayList actives = new ArrayList();
		
		Iterator vertsItr = verts.iterator();		
		while (vertsItr.hasNext()) {			
			ITcObjVertex vert = (ITcObjVertex) vertsItr.next();
			double x1 = vert.getX();
			double y1 = vert.getY();
			// вершины, чья у-координата далеко, надо убрать из списка активных,
			// с остальными надо провести проверку на близость по х.
			Iterator activesItr = actives.iterator();
			boolean closeY = false;
			while (activesItr.hasNext()) {			
				ITcObjVertex active = (ITcObjVertex) activesItr.next();
				double x2 = active.getX();
				double y2 = active.getY();
				if (!closeY) {
					if (!iteration._geometry.near(y1, y2, iteration._delta)) activesItr.remove();
					else closeY = true;
				}
				if (closeY) {
					if (!iteration._geometry.near(x1, x2, iteration._delta)) continue;
					// ага! мы близко, нужна окончательная проверка
					if (TcLocNearVerts.addNewInstance(iteration, active, vert)) break;
					// если вершина отнесена к одному дефекту, к другому ее относить уже нельзя
					break;
				}
			}
				
			// если вершина была близка к одной из активных (создан дефект), то сама она в активные не попадает
			if (vert.tcdata() != null) continue;
			// иначе она должна быть добавлена к активным вершинам последней
			actives.add(actives.size(), vert);
		}
	}
	
	/**
	 * добавить данные о "близких" "висячих" вершинах
	 */
	static public boolean addNewInstance (TcIteration iteration, ITcObjVertex vert1, ITcObjVertex vert2) {
		TcLocNearVerts info = (TcLocNearVerts) vert1.tcdata();
		if (info == null) {
			if ( !TcLocNearVerts.checkPair(vert1, vert2, iteration) ) return false;					
			// новый дефект
			info = new TcLocNearVerts(iteration, vert1, vert2);
			iteration.addFound(info);
		}
		else {
			if ( !TcLocNearVerts.checkNext(info.place, vert2, iteration) ) return false;					
			// данные уже есть, просто добавляем вершину
			info.add(vert2);
		}
		return true;
	}
	
}
