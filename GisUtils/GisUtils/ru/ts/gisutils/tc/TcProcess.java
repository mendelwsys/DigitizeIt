/*
 * Created on 24.07.2007
 *
 * TC is a shortening of Topology Cleaning
 */
package ru.ts.gisutils.tc;

import java.util.*;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.ArrayList;
//import java.lang.Math;

import ru.ts.gisutils.geometry.IXYGeometry;
import ru.ts.gisutils.geometry.Rect;

/**
 * @author yugl
 *
 * To keep the current state and control the operations of TC process. 
 */

public class TcProcess implements ITcProcess {

	//----------------------
	// fields
	//----------------------
	
	/**
	 * объект, где реализована вся геометрия
	 */
	protected IXYGeometry _geometry;
	public IXYGeometry geometry() { return _geometry; }
	
	/**
	 * Доступ к хранилищу данных
	 */
	protected ITcDataStore _tcStore = null;
	public ITcDataStore getTcStore () { return _tcStore; }
	public void setTcStore (ITcDataStore store) { 
		_tcStore = store;
		_geometry = store.getGeometry();
	}
	
	/**
	 * Номер последней итерации, должен загружаться извне, если процесс
	 * является продолжением когда-то отложенного процесса.
	 */
	protected int _itrNumber = 0;
	
	/**
	 * Текущая итерация
	 */
	protected TcIteration _iteration = null;
	public TcIteration iteration() { return _iteration; }
	
	//----------------------
	// constructors
	//----------------------
	
	protected TcProcess () {
	}
	public TcProcess (ITcDataStore store) {
		this();
		setTcStore(store);
		// ToDo - надо определиться с идентификатором первой итерации,
		// по-хорошему его надо загружать из tcStore.
	}

	//----------------------
	// ITcProcess members
	//----------------------
	
	/**
	 * Начинает очередной шаг топологической чистки по всем данным. 
	 * @param taskType - задание (тип дефектов) 
	 * @param taskDelta - дельта, используется для определения "близости" объектов 
	 * @return - список найденных дефектов
	 */
	public List newIteration (int taskType, double taskDelta) {
		_iteration = new TcIteration(++_itrNumber, _tcStore, taskType, taskDelta);
		return _newIteration();
	}
	/**
	 * Начинает очередной шаг топологической чистки в прямоугольнике. 
	 * @param taskType - задание (тип дефектов) 
	 * @param taskDelta - дельта, используется для определения "близости" объектов 
	 * @param taskRect - прямоугольник поиска 
	 * @return - список найденных дефектов
	 */
	public List newIteration (int taskType, double taskDelta, Rect taskRect) {
		_iteration = new TcIteration(++_itrNumber, _tcStore, taskType, taskDelta, taskRect);
		return _newIteration();
	}
	
	
	
	/**
	 * Завершает шаг топологической чистки. Все назначенные операции чистки исполняются. 
	 */
	public void endIteration () {
		_tcStore.doActions(_iteration, _iteration._doing);
		_iteration.clearDoing();
		_iteration.clearFound();
		_iteration._state = TcIteration.STATE_DONE;
	}
	
	/**
	 * Завершает шаг топологической чистки. Все назначенные операции чистки отменяются. 
	 */
	public void cancelIteration () {
		_iteration.clearDoing();
		_iteration.clearFound();
		_iteration._state = TcIteration.STATE_SKIP;
	}
	
	/**
	 * Предлагает автоматические способы исправления дефекта указанного типа. 
	 * Набор всегда не пуст, так как включает операцию Skip. 
	 * @param defectType - тип исправляемого дефекта
	 * @return - массив возможных операций
	 */
	public int[] getAutoCorrections (int defectType) {
		return TcLocInfo.getAutoCorrections(defectType);
	}
	
	/**
	 * Возвращает автоматически сформированный набор операций исправления дефекта (возможно, пустой). 
	 * @param defect - исправляемый дефект
	 * @param correction - способ исправления дефекта
	 */
	public List getAutoActions (TcLocInfo defect, int correction) {
		return defect.getAutoActions(correction);
	}
	
	/**
	 * Задает операцию топологической чистки для исправления дефекта.
	 * Один дефект может обрабатываться серией операций. 
	 * @param defect - исправляемый дефект
	 * @param action - операция исправления 
	 */
	public void processDefect (TcLocInfo defect, TcAction action) {
		action.defect = defect;
		_iteration.addDoing(action);
	}
	
	/**
	 * Задает операции топологической чистки для исправления дефекта.
	 * @param defect - исправляемый дефект
	 * @param actions - набор операций исправления 
	 */
	public void processDefect (TcLocInfo defect, List actions) {
		ListIterator itr = actions.listIterator();
		while (itr.hasNext()) {
			TcAction action = (TcAction) itr.next();
			processDefect(defect, action);
		}
	}
	
	/**
	 * Автоматически формирует и задает набор операций для исправления дефекта.
	 * @param defect - исправляемый дефект
	 * @param autoAction - способ исправления 
	 */
	public void autoprocessDefect (TcLocInfo defect, int correction) {
		List actions = getAutoActions (defect, correction);
		processDefect(defect, actions);
	}
		
	/**
	 * Автоматически формирует и задает набор операций для исправления дефектов.
	 * @param defects - список дефектов
	 * @param autoAction - способ исправления 
	 */
	public void autoprocessDefects (List defects, int correction) {
		ListIterator itr = defects.listIterator();
		while (itr.hasNext()) {
			TcLocInfo defect = (TcLocInfo) itr.next();
			autoprocessDefect(defect, correction);
		}
	}
	
	/**
	 * Автоматически формирует и задает набор операций для исправления дефектов.
	 * Способ исправления выбирается по умолчанию. Возможно, что ничего и не делается.
	 * @param defects - список дефектов
	 */
	public void autoprocessDefects (List defects) {
		ListIterator itr = defects.listIterator();
		while (itr.hasNext()) {
			TcLocInfo defect = (TcLocInfo) itr.next();
			int correction = defect.getDefaultCorrection();
			autoprocessDefect(defect, correction);
		}
	}
	
	/**
	 * Сохраняет сделанные изменения в хранилище данных.
	 */
	public void saveResults () {
		// TODO - it is unclear now, do it later
	}
	
	
	//----------------------
	// methods
	//----------------------
	
	/**
	 * Запускает шаг топологической чистки. Ищет дефекты по данным итерации. 
	 * @return - список найденных дефектов
	 */
	protected List _newIteration () {
		// run a procedure that fills up the list depending of type of defect
		switch (_iteration._task) {
			case TcIteration.PROC_DUPE_NODES:
				_findDupeNodes();
				break;
			case TcIteration.PROC_NEAR_NODES:
				_findNearNodes();
				break;
			case TcIteration.MATCH_NODES_WITH_VERTS:
				_matchVertsWithNodes();
				break;
			case TcIteration.PROC_VERTS_NEAR_NODES:
				_findVertsNearNodes();
				break;
			case TcIteration.PROC_NEAR_VERTS:
				_findNearVerts();
				break;
			case TcIteration.PROC_NODES_NEAR_LINES:
				_findNodesNearLines();
				break;
			case TcIteration.PROC_SMALL_LOOP_LINES:
				_findSmallLoopLines();
				break;
			case TcIteration.PROC_LINE_INTERSECTIONS:
				_findIntersections();
				break;
			default:
			// ToDo - throw an exception
		}		
		return _iteration._found;
	}	
	
	/**
	 * Получение сортированных узлов из хранилища
	 */
	protected List _getSortedNodes () {
		List objects;
		if (_iteration._place == null) 
			objects = _tcStore.getSortedNodes();
		else 
			objects = _tcStore.getSortedNodes(_iteration._place);
		_clearTcData(objects);
		return objects;
	}
	
	/**
	 * Получение сортированных концевых вершин из хранилища
	 */
	protected List _getSortedEndVertices () {
		List objects;
		if (_iteration._place == null)
			objects = _tcStore.getSortedEndVertices();
		else 
			objects = _tcStore.getSortedEndVertices(_iteration._place);
		_clearTcData(objects);
		return objects;
	}
	
	/**
	 * Получение сортированных висячих узлов из хранилища
	 */
	protected List _getSortedDanglingNodes () {
		List objects;
		if (_iteration._place == null)
			objects = _tcStore.getSortedDanglingNodes();
		else 
			objects = _tcStore.getSortedDanglingNodes(_iteration._place);
		_clearTcData(objects);
		return objects;
	}
	
	/**
	 * Получение сортированных висячих вершин из хранилища
	 */
	protected List _getSortedDanglingVertices () {
		List objects;
		if (_iteration._place == null)
			objects = _tcStore.getSortedDanglingVertices();
		else 
			objects = _tcStore.getSortedDanglingVertices(_iteration._place);
		_clearTcData(objects);
		return objects;
	}
	
	/**
	 * Получение полилиний из хранилища
	 */
	protected List _getPolylines () {
		List objects;
		if (_iteration._place == null)
			objects = _tcStore.getPolylines();
		else 
			objects = _tcStore.getPolylines(_iteration._place);
		_clearTcData(objects);
		return objects;
	}
	
	/**
	 * Чистка привязанных к объектам данных
	 * @param objects - список элементов типа TcObject
	 */
	protected void _clearTcData (List objects) {
		try {
			Iterator objsItr = objects.iterator();
			while (objsItr.hasNext()) {
				ITcObjBase obj = (ITcObjBase) objsItr.next();
				obj.attachTcdata(null);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//----------------------
	// PROC_DUPE_NODES
	//----------------------
	
	/**
	 * Поиск узлов-дубликатов (узлов с повторяющимися координатами)
	 */
	protected void _findDupeNodes () {
		List nodes = _getSortedNodes();
		TcLocDupeNodes.search(_iteration, nodes);
	}
	
	//----------------------
	// PROC_NEAR_NODES
	//----------------------
	
	/**
	 * Поиск близких друг к другу узлов
	 */
	protected void _findNearNodes () {
		List nodes = _getSortedNodes();
		TcLocNearNodes.search(_iteration, nodes);
	}
	
	//----------------------
	// MATCH_NODES_WITH_VERTS
	//----------------------
	
	/**
	 * Сопоставление концевых вершин с узлами. Узлы-дубли уже должны быть вычищены.
	 */
	protected void _matchVertsWithNodes () {
		List nodes = _getSortedNodes();
		List verts = _getSortedEndVertices();
		TcLocNodeVerts.search(_iteration, nodes, verts);
	}
	
	//----------------------
	// PROC_VERTS_NEAR_NODES
	//----------------------
	
	/**
	 * Поиск узлов близких к висячим концевым вершинам.
	 */
	protected void _findVertsNearNodes () {
		List nodes = _getSortedNodes();
		List verts = _getSortedDanglingVertices();
		TcLocVertNearNodes.search(_iteration, nodes, verts);
	}
	
	//----------------------
	// PROC_NEAR_VERTS
	//----------------------
	
	/**
	 * Поиск близких висячих концевых вершин.
	 */
	protected void _findNearVerts () {
		List verts = _getSortedDanglingVertices();
		TcLocNearVerts.search(_iteration, verts);
	}
	
	//----------------------
	// PROC_NODES_NEAR_LINES
	//----------------------
	// Возможно три подхода к решению задачи:
	// 1. Полилинии отбираются сразу (все или по прямоугольнику области поиска),
	// 		потом при необходимости фильтруются, для каждого узла. 
	// 2. Полилинии отбираются для каждого узла отдельно.
	// 3. Правильный, когда узлы и полилинии группируются по пространственным
	// 		критериям. Но он сложнее и требует хороших инструментов. Пока что его  
	// 		отложим и сделаем как проще. 
	// Пока склоняюсь к первому подходу, у второго меньше возможностей для
	// оптимизации, если она понадобится. Хотя на хорошо выстроенной, мощной 
	// системе хранения геометрии, второй подход, возможно, будет лучше работать.  
	
	/**
	 * Поиск отрезков близких к висячим узлам.
	 */
	protected void _findNodesNearLines () {
		// в идеале не сортировка нужна, а разбиение множеств узлов и полилиний на 
		// совместно обрабатываемые подмножества (например, по ячейкам грида), но пока так... 
		List nodes = _getSortedDanglingNodes();	 
		List plines = _getPolylines();
		TcLocNodeNearLines.search(_iteration, nodes, plines);
	}
	
	//----------------------
	// PROC_SMALL_LOOP_LINES
	//----------------------
	
	/**
	 * Поиск мелких замкнутых линий.
	 */
	protected void _findSmallLoopLines () {
		List plines = _getPolylines();
		TcLocSmallLoop.search(_iteration, plines);
	}
	
	//----------------------
	// PROC_LINES_NEAR_LINES
	//----------------------
	// Комментарий к PROC_NODES_NEAR_LINES имеет место и здесь.
	
	/**
	 * Поиск пересекающихся отрезков.
	 */
	protected void _findIntersections () {
		List plines = _getPolylines();
		TcLocIntersection.search(_iteration, plines);
	}
	
	
}
