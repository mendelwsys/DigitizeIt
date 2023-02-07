/*
 * Created on 23.08.2007
 *
 */
package ru.ts.gisutils.tc;

import java.util.*;

import ru.ts.gisutils.geometry.Rect;

/**
 * @author yugl
 *
 * Информация о "небольшой" полилиниии, чьи начало и конец находятся в одной точке.
 * Такая ситуация может возникнуть, например, если линия была короткой и в результате 
 * обработки "близких" концевых вершин выродилась в отрезок нулевой длины или
 * (при наличии нескольких отрезков) в "небольшую" замкнутую линию. В сетевом графе 
 * такие линии приведут к образованию "лишних" петель, лучше бы от них избавиться.  
 */
public class TcLocSmallLoop extends TcLocInfo {

	//----------------------
	// fields
	//----------------------

	/**
	 * Небольшая замкнутая полилиния.  
	 */ 
	public ITcObjPolyline pline;
	
	//----------------------
	// constructors
	//----------------------
		
	/**
	 * защищенный пустой конструктор 
	 */
	protected TcLocSmallLoop () {
		_type = TcLocInfo.SMALL_LOOP_LINE;
	}
	
	/**
	 * изначально список отрезков пуст 
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param node - узел
	 */
	public TcLocSmallLoop (TcIteration iteration, ITcObjPolyline pline) {
		super(iteration);
		_type = TcLocInfo.SMALL_LOOP_LINE;
		this.pline = pline;
		pline.attachTcdata(this);
		pline.verts().rect().copyTo(place);
	}
	
	//----------------------
	// methods
	//----------------------
	
	public String toString () {
		return "SMALL_LOOP_LINE=" + _type + ", place=" + place + ", pline " + pline.tcid();
	}

	//----------------------
	// static
	//----------------------
	
	static protected int[] _autoCorrections = {
		TcLocInfo.SKIP_DEFECT, 
		TcLocInfo.DEL_SMALL_LOOP
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
		case DEL_SMALL_LOOP:
			TcActDelObject action = new TcActDelObject(this, DEL_SMALL_LOOP, pline);
			actions.add(action);
			break;
		default:
		// ToDo - throw an exception
		}		
		return actions;
	}
		
	//----------------------
	// check of smallness
	//----------------------
	
	/**
	 * Проверка прямоугольника на малость в смысле данного дефекта 
	 * @param delta - расстояние "малости"
	 * @return true, если прямоугольник мал
	 */
	static public boolean check (double delta, Rect rect) {		
		return rect.small(delta);
	}
	/**
	 * Проверка двух точек на близость в смысле данного дефекта 
	 * @param delta - расстояние "близости"
	 * @return true, если координаты xy1 близки к xy2
	 */
	static public boolean check (double delta, ITcObjPolyline pline) {
		return check(delta, pline.verts().rect());
	}

	//----------------------
	// iteration
	//----------------------
	
	/**
	 * Поиск мелких замкнутых полилиний. 
	 * @param iteration - итерация, в рамках которой идет поиск
	 * @param plines - полилинии
	 */
	static public void search (TcIteration iteration, List plines) {
		// for every polyline
		Iterator plinesItr = plines.iterator();		
		while (plinesItr.hasNext()) {
			// берем очередной узел			
			ITcObjPolyline pline = (ITcObjPolyline) plinesItr.next();
			if (TcLocSmallLoop.check(iteration._delta, pline)) {
				addNewInstance(iteration, pline);
			}
		}		
	}
	
	/**
	 * добавить данные об отрезке рядом с узлом
	 */
	static public void addNewInstance (TcIteration iteration, ITcObjPolyline pline) {
		TcLocSmallLoop info = new TcLocSmallLoop(iteration, pline);
		iteration.addFound(info); 
	}
	
}
