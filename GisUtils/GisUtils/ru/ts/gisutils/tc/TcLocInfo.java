/*
 * Created on 26.07.2007
 *
 * TC is a shortening of Topology Cleaning
 */
package ru.ts.gisutils.tc;

import java.util.ArrayList;
import java.util.List;

import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.IXY;
import ru.ts.gisutils.geometry.Rect;

/**
 * @author yugl
 *
 * Базовый класс для собранной информации о каком-то особенном месте, обычно о дефекте.
 */
public class TcLocInfo implements Cloneable {

	//----------------------
	// constants
	//----------------------
	
	// info types
	//----------------------
	// it is an original state (no defect)  
	public final static int INFO_NONE = 0;
	// nodes with the same coordinates 
	public final static int DUPE_NODES = TcIteration.PROC_DUPE_NODES;
	// nodes with the near coordinates 
	public final static int NEAR_NODES = TcIteration.PROC_NEAR_NODES;
	// node with vertices 
	public final static int NODE_VERTS = TcIteration.MATCH_NODES_WITH_VERTS;
	// node without vertices 
	public final static int DANGLING_NODE = TcIteration.MATCH_NODES_WITH_VERTS + 10;
	// vertex without node 
	public final static int DANGLING_VERT = TcIteration.MATCH_NODES_WITH_VERTS + 20;
	// vertices (with the same coordinates) without node 
	public final static int DANGLING_VERTS = TcIteration.MATCH_NODES_WITH_VERTS + 30;
	// nodes near the dangling vertex 
	public final static int VERT_NEAR_NODES = TcIteration.PROC_VERTS_NEAR_NODES;
	// dangling vertices with the near coordinates 
	public final static int NEAR_VERTS = TcIteration.PROC_NEAR_VERTS;
	// lines near the dangling node  
	public final static int NODE_NEAR_LINES = TcIteration.PROC_NODES_NEAR_LINES;
	// small line whose end vertices have the same coordinates 
	public final static int SMALL_LOOP_LINE = TcIteration.PROC_SMALL_LOOP_LINES;
	// two intersecting lines 
	public final static int INTERSECTION = TcIteration.PROC_LINE_INTERSECTIONS;

	// correction types
	//----------------------
	// no info about correction    
	public final static int NO_CORRECTION_INFO = INFO_NONE + 0;
	// no correction (used as auto-correction when there are no any rational variants)   
	public final static int SKIP_DEFECT = INFO_NONE + 1;
	// correction by hand   
	public final static int DO_MANUALLY = INFO_NONE + 2;
	// corrections for nodes with the same coordinates 
	public final static int DEL_DUPE_NODES_BUT_FIRST = DUPE_NODES;
	public final static int DEL_DUPE_NODES_AND_BUILD_NEW = DUPE_NODES + 1;
	// corrections for nodes with the near coordinates 
	public final static int DEL_NEAR_NODES_BUT_FIRST = NEAR_NODES;
	public final static int DEL_NEAR_NODES_AND_BUILD_NEW = NEAR_NODES + 1;
	// "corrections" for node with vertices 
	public final static int MARK_NODES_AND_VERTS_AS_MATCHED = NODE_VERTS;
	// corrections for node without vertices 
	public final static int MARK_NODE_AS_DANGLING = DANGLING_NODE;
	// corrections for vertex without node 
	public final static int MARK_VERT_AS_DANGLING = DANGLING_VERT;
	public final static int MAKE_NODE_FOR_VERT = DANGLING_VERT + 1;
	// corrections for vertices (with the same coordinates) without node 
	public final static int MARK_VERTS_AS_DANGLING = DANGLING_VERTS;
	public final static int MAKE_NODE_FOR_VERTS = DANGLING_VERTS + 1;
	public final static int JOIN_PLINES_OR_MAKE_NODE = DANGLING_VERTS + 2;
	// corrections for nodes near the dangling vertex 
	public final static int MOVE_VERT_TO_NEAREST_NODE = VERT_NEAR_NODES;
	// corrections for dangling vertices with the near coordinates 
	public final static int MOVE_VERTS_TO_FIRST_ONE = NEAR_VERTS;
	public final static int MOVE_VERTS_TO_CENTER = NEAR_VERTS + 1;
	// corrections for lines near the dangling node  
	public final static int MOVE_NODE_TO_NEAREST_LINE = NODE_NEAR_LINES;
	public final static int MOVE_NEAREST_LINE_TO_NODE = NODE_NEAR_LINES + 1;	// ???
	// corrections for small line whose end vertices have the same coordinates 
	public final static int DEL_SMALL_LOOP = SMALL_LOOP_LINE;
	// corrections for two intersecting lines 
	public final static int BREAK_LINES = INTERSECTION;

	// states
	//----------------------
	// initial state  
	public final static int STATE_TODO = 0;
	// after processing
	public final static int STATE_DONE = 1;
	// cancel processing 
	public final static int STATE_SKIP = 2;

	//----------------------
	// fields
	//----------------------
	 
	/**
	 * Итерация, во время которой был найден дефекта (содержит параметры поиска)
	 */ 
	protected TcIteration _iteration = null;
	public TcIteration iteration() { return _iteration; }
	
	/**
	 * тип информации (тип дефекта), обычно задается неявно реальным типом данных 
	 */ 
	protected int _type = INFO_NONE;
	public int type() { return _type; }
	
	/**
	 * Состояние (обработан/нет)
	 */ 
	public int state = STATE_TODO;
			
	/**
	 * место, где дефект обнаружен
	 */ 
	public Rect place = null;						


	//----------------------
	// constructors
	//----------------------
	// empty defect
	protected TcLocInfo () {
	}
	/**
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param state - состояние (обработан/нет)
	 * @param place - место, где дефект обнаружен
	 */
	public TcLocInfo (TcIteration iteration, int state, Rect place) {
		_init( this,  iteration, state, place );
	}
	/**
	 * используется состояние по умолчанию (не обработан)
	 * @param iteration - итерация (номер и параметры поиска)
	 * @param place - место, где дефект обнаружен
	 */
	public TcLocInfo (TcIteration iteration, Rect place) {
		_init( this,  iteration, STATE_TODO, place );
	}
	/**
	 * используются состояние по умолчанию и пустой прямоугольник
	 * @param iteration - итерация (номер и параметры поиска)
	 */
	public TcLocInfo (TcIteration iteration) {
		_init( this,  iteration, STATE_TODO, Rect.empty() );
	}
	
	//----------------------
	// static
	//----------------------

	// sets data of the instance, using parameters   
	static protected void _init (TcLocInfo me,  TcIteration iteration, int state, Rect place) {
		me._iteration = iteration;
		me.state = state;
		me.place = place;
	}
	static protected void _init (TcLocInfo me, TcLocInfo he) {
		_init( me,  he._iteration, he.state,  he.place );
	}
	
	//----------------------
	// static / instance
	//----------------------

	/**
	 * Предлагает автоматические способы исправления дефекта указанного типа. 
	 * Набор всегда не пуст, так как включает операцию Skip. 
	 * @param defectType - тип исправляемого дефекта
	 * @return - массив возможных операций
	 */
	static public TcLocInfo getInstance (int defectType) {
		switch (defectType) {
		case DUPE_NODES:
			return new TcLocDupeNodes();
		case NEAR_NODES:
			return new TcLocNearNodes();
		case NODE_VERTS:
			return new TcLocNodeVerts();
		case DANGLING_NODE:
			return new TcLocDanglingNode();
		case DANGLING_VERT:
			return new TcLocDanglingVert();
		case DANGLING_VERTS:
			return new TcLocDanglingVerts();
		case VERT_NEAR_NODES:
			return new TcLocVertNearNodes();
		case NEAR_VERTS:
			return new TcLocNearVerts();
		case NODE_NEAR_LINES:
			return new TcLocNodeNearLines();
		case SMALL_LOOP_LINE:
			return new TcLocSmallLoop();
		case INTERSECTION:
			return new TcLocIntersection();
		}		
		return new TcLocInfo();
	}
	
	//----------------------
	// correction
	//----------------------

	static protected int[] _autoCorrections = {
		TcLocInfo.SKIP_DEFECT 
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
		return _autoCorrections[0];
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
	 * Предлагает автоматические способы исправления дефекта указанного типа. 
	 * Набор всегда не пуст, так как включает операцию Skip. 
	 * @param defectType - тип исправляемого дефекта
	 */
	static public int[] getAutoCorrections (int defectType) {
		TcLocInfo info = getInstance(defectType);
		return info.getAutoCorrections();
	}
	/**
	 * Предлагает способ исправления дефекта указанного типа "по умолчанию". 
	 */
	static public int getDefaultCorrection (int defectType) {
		TcLocInfo info = getInstance(defectType);
		return info.getDefaultCorrection();
	}
		
	/**
	 * Возвращает автоматически сформированный набор операций исправления дефекта (возможно, пустой). 
	 * @param correction - способ исправления дефекта
	 */
	public List getAutoActions (int correction) {
		ArrayList actions = new ArrayList();
		return actions;
	}
	
	//----------------------
	// Cloneable
	//----------------------

	// sets data of the instance, using parameters   
	public Object clone () {
		try {
			TcLocInfo defect = (TcLocInfo) super.clone();
			defect.place = place.copyOf();
			return defect; 
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return this; 
		}
	}
	
	//----------------------
	// copy
	//----------------------
	
	// makes a new instance of the class with the same data 
	public TcLocInfo copyOf () {
		return (TcLocInfo) this.clone(); 
	}
	
	// copies the data to another instance  
	public void copyTo (TcLocInfo he) {
		_init( he,  this );
	}
	
	//----------------------
	// check of nearness
	//----------------------
	
	/**
	 * Типовая проверка двух точек на близость  
	 * @param delta - расстояние "близости"
	 * @return true, если координаты x1,y1 близки к x2,y2
	 */
	public boolean check (double delta, double x1, double y1, double x2, double y2) {		
		return _iteration.geometry().nearBoth(x1,y1, x2,y2, delta);
	}
	/**
	 * Типовая проверка двух точек на близость  
	 * @param delta - расстояние "близости"
	 * @return true, если координаты xy1 близки к xy2
	 */
	public boolean check (double delta, IGetXY xy1, IGetXY xy2) {
		return _iteration.geometry().nearBoth(xy1, xy2, delta);
	}

	//----------------------
	// methods
	//----------------------
	
	public String toString () {
		return "TcLocInfo: type=" + _type + ", place=" + place;
	}

	//----------------------
	// check of nearness
	//----------------------
	
	/**
	 * Проверка двух точек на близость в смысле некоторого дефекта 
	 * @param iteration содержит параметры "близости"
	 * @return true, если координаты x1,y1 близки к x2,y2
	 */
	static public boolean checkNearness (
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
	static public boolean checkNearness (IGetXY xy1, IGetXY xy2, TcIteration iteration) {
		return checkNearness(xy1.getX(), xy1.getY(),  xy2.getX(), xy2.getY(), iteration);
	}

	//----------------------
	// break line
	//----------------------

	/**
	 * Возвращает операцию разбиения линии для дефекта (null, если операция не требуется). 
	 * @param xy - точка разбиения
	 * @param line - разбиваемый отрезок полилинии
	 * @param correction TODO
	 * @return TODO
	 */
	// returns null when there is no action
	protected TcActBreakPline _makeBreakLineAction (IXY xy, TcObjLine line, int correction) {
		// найдем параметрический индекс точки пересечения  
		double t = line.index() + _iteration.geometry().perpendicular(xy, line.p1(), line.p2());
		ITcObjPolyline pline = line.pline();
		// ignore intersection near the end vertices
		if (_iteration.geometry().near(t, 0)) return null;	
		if (_iteration.geometry().near(t, pline.verts().size()-1)) return null;	
		TcActBreakPline action = new TcActBreakPline(this, correction, pline, t, xy);
		return action; 
	}
	
}
