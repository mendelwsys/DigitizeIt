/*
 * Created on 15.08.2007
 *
 */
package ru.ts.gisutils.tc;

import java.util.*;

import ru.ts.gisutils.geometry.IXY;
import ru.ts.gisutils.geometry.Rect;
import ru.ts.gisutils.geometry.XY;

/**
 * @author yugl
 *
 * Информация о пересекающизся полилиниях 
 */
public class TcLocIntersection extends TcLocInfo {

	//----------------------
	// fields
	//----------------------

	/**
	 * Первый пересекающийся отрезок  
	 */ 
	public TcObjLine line1;
	
	/**
	 * Второй пересекающийся отрезок  
	 */ 
	public TcObjLine line2;
	
	/**
	 * Вариант пересечения, 1 - пересечение, 2 - перекрытие  
	 */ 
	public int n = 0;
	
	/**
	 * Координаты пересечения, x2,y2 используются при перекрытии  
	 */ 
	public double x, y, x2, y2;
	
	//----------------------
	// constructors
	//----------------------
			
	/**
	 * защищенный пустой конструктор 
	 */
	protected TcLocIntersection () {
		_type = TcLocInfo.INTERSECTION;
	}
	
	/**
	 * есть пересечение, но его вариант не указан 
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param line1 - первый отрезок
	 * @param line2 - второй отрезок
	 */
	public TcLocIntersection (TcIteration iteration, TcObjLine line1, TcObjLine line2) {
		super(iteration);
		_type = TcLocInfo.INTERSECTION;
		this.line1 = line1;
		this.line2 = line2;
		line1.attachTcdata(this);
		line2.attachTcdata(this);
		place.extend(line1.p1());
		place.extend(line1.p2());
		place.extend(line2.p1());
		place.extend(line2.p2());
		//	точка пересечения отмечена на обоих отрезках	
	}
	/**
	 * пересечение с указанием варианта (1 или 2) 
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param line1 - первый отрезок
	 * @param line2 - второй отрезок
	 * @param n - вариант: 1 - пересечение, 2 - перекрытие
	 * @param x,y, x2,y2 - координаты
	 */
	public TcLocIntersection (TcIteration iteration, TcObjLine line1, TcObjLine line2, int n, double x, double y, double x2, double y2) {
		this(iteration, line1, line2);
		this.n = n;
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
	}

	//----------------------
	// methods
	//----------------------
	
	public String toString () {
		return "INTERSECTION=" + _type + ", place=" + place 
			+ ", line1 " + line1.index() + " of " + line1.pline().tcid() 
			+ ", line2 " + line2.index() + " of " + line2.pline().tcid();
	}

	//----------------------
	// static
	//----------------------
	
	static protected int[] _autoCorrections = {
		TcLocInfo.SKIP_DEFECT, 
		TcLocInfo.BREAK_LINES
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
		case BREAK_LINES:
		// содержательная обработка совпадения линий (хотя бы частичного) должна проходить  
		// в духе чистки областных топологий (выделение и удаление линий-дубликатов),
		// здесь будем рассматривать только обычное пересечение с разбиением линий в
		// точке пересечения (если понадобится, потом к этой теме вернемся)
			if (n != 1) break;	
			XY xy = new XY(x,y);
			TcActBreakPline action = _makeBreakLineAction(xy, line1);
			if (action != null) actions.add(action);				
			action = _makeBreakLineAction(xy, line2);
			if (action != null) actions.add(action);				
			break;
		default:
		// ToDo - throw an exception
		}		
		return actions;
	}
	
	// returns null when there is no action
	protected TcActBreakPline _makeBreakLineAction (IXY xy, TcObjLine line) {
		TcActBreakPline action = super._makeBreakLineAction(xy, line, BREAK_LINES);
		return action; 
	}
	
	//----------------------
	// iteration
	//----------------------
	
	/**
	 * Поиск пересечений отрезков полилиний. 
	 * @param iteration - итерация, в рамках которой идет поиск
	 * @param plines - полилинии
	 */
	static public void search (TcIteration iteration, List plines) {
		// to keep a rectangle around a line
		Rect rect = Rect.empty();
		// to keep intersection data 
		XY xy1 = new XY();		
		XY xy2 = new XY();		
		// for every pline
		Iterator plinesItr = plines.iterator();
		while (plinesItr.hasNext()) {			
			ITcObjPolyline pline = (ITcObjPolyline) plinesItr.next();
			pline.verts().copyRect(rect);
			// prepare list of polylines that are to be considered with the polyline
			List actives = iteration.getTcStore().filter(plines, rect);
			
			// get first vertex that is a start of first line
			double ax = pline.verts().getXY(0).getX();
			double ay = pline.verts().getXY(0).getY();
			// теперь последовательно формируем отрезки из вершин и проверяем на пересечение
			int size = pline.verts().size();
			for (int i=1; i<size; i++) {
				// get next vertex that is a finish of line
				double bx = pline.verts().getXY(i).getX();
				double by = pline.verts().getXY(i).getY();
				Iterator activesItr = actives.iterator();
				while (activesItr.hasNext()) {			
					ITcObjPolyline active = (ITcObjPolyline) activesItr.next();
					if (active.tcdata() != null) continue;	// tcdata уже обработанных линий будет иметь значение
					boolean theSame = false;
					if ((pline.tcid()).compareTo(active.tcid()) == 0) theSame = true;					 
					// get first vertex that is a start of first line
					double cx = active.verts().getXY(0).getX();
					double cy = active.verts().getXY(0).getY();
					// теперь последовательно формируем отрезки из вершин и проверяем на пересечение
					int size2 = active.verts().size();
					for (int j=1; j<size2; j++) {
						// get next vertex that is a finish of line
						double dx = active.verts().getXY(j).getX();
						double dy = active.verts().getXY(j).getY();
						
						if ((!theSame) || (i != j)) {
							// ищем пересечение
							int n = iteration.geometry().intersection(xy1, xy2, ax,ay, bx,by, cx,cy, dx,dy);
							boolean doAdd = true;
							if (n == 0) doAdd = false;
							if (theSame && (n == 1)) {
								// исключим смежные отрезки
								if (i == (j-1)) doAdd = false;
								if (i == (j+1)) doAdd = false;
							}
							if (doAdd) {
								addNewInstance(iteration, new TcObjLine(pline, i-1), new TcObjLine(active, j-1), n, xy1.x, xy1.y, xy2.x, xy2.y);
							}
						}
						// make a finish of the current line to be a start of the next line
						cx = dx;
						cy = dy;
					}
				}
				// make a finish of the current line to be a start of the next line
				ax = bx;
				ay = by;
			}
			pline.attachTcdata(iteration); // неважно, что присваивать, лишь бы не null 
		}
	}			
	
	/**
	 * добавить данные об отрезке рядом с узлом
	 */
	static public void addNewInstance (
		TcIteration iteration, TcObjLine line1, TcObjLine line2, 
		int n, double x, double y, double x2, double y2
	) {
		TcLocIntersection info = new TcLocIntersection(iteration, line1, line2, n, x, y, x2, y2);
		iteration.addFound(info); 
	}
	
}
