/*
 * Created on 25.07.2007
 *
 * TC is a shortening of Topology Cleaning
 */
package ru.ts.gisutils.tc;

//import java.util.Collections;
//import java.util.Collection;
import java.util.List;

import ru.ts.gisutils.geometry.IXYGeometry;
import ru.ts.gisutils.geometry.Rect;

/**
 * @author yugl
 *
 * Interface to load/save data used by TC process
 */
public interface ITcDataStore {

	//----------------------
	// geometry
	//----------------------
	/**
	 * Возвращает объект для ведения геометрических расчетов, согласованный с хранилищем данных
	 */
	public IXYGeometry getGeometry ();
	
	//----------------------
	// nodes
	//----------------------
	/**
	 * Возвращает список узлов (без гарантии их сортировки).
	 */
	public List getNodes ();
	/**
	 * Возвращает список узлов в прямоугольнике, включая границы.
	 * @param rect - прямоугольник поиска
	 */
	public List getNodes (Rect rect);
	
	/**
	 * Возвращает отсортированный по возрастанию (в смысле XY) список узлов.
	 */
	public List getSortedNodes ();
	/**
	 * Возвращает отсортированный по возрастанию (в смысле XY) список узлов 
	 * в прямоугольнике, включая границы.
	 * @param rect - прямоугольник поиска
	 */
	public List getSortedNodes (Rect rect);
	
	//----------------------
	// end vertices
	//----------------------
	/**
	 * Возвращает список концевых вершин (без гарантии их сортировки).
	 */
	public List getEndVertices ();
	/**
	 * Возвращает список концевых вершин в прямоугольнике, включая границы.
	 * @param rect - прямоугольник поиска
	 */
	public List getEndVertices (Rect rect);
	
	/**
	 * Возвращает отсортированный по возрастанию (в смысле XY) список концевых вершин.
	 */
	public List getSortedEndVertices ();
	/**
	 * Возвращает отсортированный по возрастанию (в смысле XY) список концевых вершин 
	 * в прямоугольнике, включая границы.
	 * @param rect - прямоугольник поиска
	 */
	public List getSortedEndVertices (Rect rect);
	
	//----------------------
	// dangling nodes
	//----------------------
	/**
	 * Возвращает список "висячих" узлов (без гарантии их сортировки).
	 */
	public List getDanglingNodes ();
	/**
	 * Возвращает список "висячих" узлов в прямоугольнике, включая границы.
	 * @param rect - прямоугольник поиска
	 */
	public List getDanglingNodes (Rect rect);
	
	/**
	 * Возвращает отсортированный по возрастанию (в смысле XY) список "висячих" 
	 * узлов.
	 */
	public List getSortedDanglingNodes ();
	/**
	 * Возвращает отсортированный по возрастанию (в смысле XY) список "висячих" 
	 * узлов в прямоугольнике, включая границы.
	 * @param rect - прямоугольник поиска
	 */
	public List getSortedDanglingNodes (Rect rect);
	
	//----------------------
	// dangling end vertices
	//----------------------
	/**
	 * Возвращает список "висячих" концевых вершин (без гарантии их сортировки).
	 */
	public List getDanglingVertices ();
	/**
	 * Возвращает список "висячих" концевых вершин в прямоугольнике, включая границы.
	 * @param rect - прямоугольник поиска
	 */
	public List getDanglingVertices (Rect rect);
	
	/**
	 * Возвращает отсортированный по возрастанию (в смысле XY) список "висячих" 
	 * концевых вершин.
	 */
	public List getSortedDanglingVertices ();
	/**
	 * Возвращает отсортированный по возрастанию (в смысле XY) список "висячих" 
	 * концевых вершин в прямоугольнике, включая границы.
	 * @param rect - прямоугольник поиска
	 */
	public List getSortedDanglingVertices (Rect rect);
	
	//----------------------
	// polylines
	//----------------------
	/**
	 * Возвращает список полилиний.
	 */
	public List getPolylines ();
	/**
	 * Возвращает список полилиний, "задевающих" прямоугольник (имеющих с ним 
	 * хотя бы одну общую точку).
	 * @param rect - прямоугольник поиска
	 */
	public List getPolylines (Rect rect);
	
	//----------------------
	// filter
	//----------------------
	/**
	 * Возвращает список объектов, отфильтрованный по прямоугольнику.
	 * @param objs - исходный список объектов
	 * @param rect - прямоугольник поиска
	 */
	public List filter (List objs, Rect rect);
	
	//----------------------
	// actions
	//----------------------
	/**
	 * Задает набор операций, которые надо исполнить аля транзакция
	 * @param actions - список объектов типа TcAction
	 * @param iteration - итерация, в рамках которой производятся действия
	 */
	public void doActions (TcIteration iteration, List actions);
	
}
