/*
 * Created on 30.07.2007
 *
 */
package ru.ts.gisutils.tc;

/**
 * @author yugl
 *
 * Base class for TC actions (defect corrections).
 */
public class TcAction implements Cloneable {

	//----------------------
	// constants
	//----------------------
	
	// action types
	//----------------------
	// it is an original state (no action)
	public final static int NONE = 0;
	
	// common operations
	
	// удаление некоторого объекта (на будущее)
	public final static int OBJ_DELETE = 1;
	// перемещение некоторого объекта в точку (на будущее)
	public final static int OBJ_MOVE_TO = 2;
	// создание некоторого объекта (на будущее)
	public final static int OBJ_MAKE_NEW = 3;
	// установка значения атрибута некоторого объекта (на будущее)
	public final static int OBJ_SET_ATTR = 4;
	
	// node operations 
	
	// удаление узла
	public final static int NODE_DELETE = 11;
	// перемещение узла в новую точку 
	public final static int NODE_MOVE_TO = 12;
	// создание узла (в точке)
	public final static int NODE_MAKE_NEW = 13;
	// пометка узла как "висячего"
	public final static int NODE_MARK_AS_DANGLING = 14;
	// пометка узла как не "висячего"
	public final static int NODE_MARK_AS_CONNECTED = 15;
	
	// end vertices (of polylines) operations
	
	// перемещение вершины в точку 
	public final static int VERT_MOVE_TO = 22;
	// пометка вершины как "висячей"
	public final static int VERT_MARK_AS_DANDLING = 24;
	// пометка вершины как не "висячей"
	public final static int VERT_MARK_AS_CONNECTED = 25;
	
	// polylines operations
	
	// удаление полилинии
	public final static int PLINE_DELETE = 31;
	// соединение двух полилиний в одну
	public final static int PLINE_JOIN_TWO = 36;
	// разбиение полилинии в точке (на две*)	* - примечание спроси у Югла 
	public final static int PLINE_BREAK_AT = 37;

	// action states
	//----------------------
	// initial state  
	public final static int STATE_TODO = 0;
	// after processing 
	public final static int STATE_DONE = 1;

	//----------------------
	// fields
	//----------------------
	
	// type of action, the constants above should be used 
	/**
	 * тип операции исправления
	 */ 
	protected int _type; // = TcAction.NONE;
	public int type() { return _type; }
	
	// state of action, the constants above should be used 
	/**
	 * состояние (исполнено/нет)
	 */ 
	public int state; // = TcAction.STATE_TODO;
	/**
	 * способ исправления
	 */ 
	public int correction; // = TcLocInfo.DO_MANUALLY;
	// defect to be cleaned by the action
	/**
	 * исправляемый дефект
	 */ 
	public TcLocInfo defect; // = null;

	//----------------------
	// constructors
	//----------------------
	// empty defect
	public TcAction () {
	}
	/**
	 * @param defect - исправляемый дефект
	 * @param correction - способ исправления (для информации)
	 * @param type - тип операции исправления
	 * @param state - состояние (исполнено/нет)
	 */
	public TcAction (TcLocInfo defect, int correction, int type, int state) {
		_init( this,  correction, type, state, defect );
	}
	/**
	 * используется состояние по умолчанию (не исполнено)
	 * @param defect - исправляемый дефект
	 * @param correction - способ исправления (для информации)
	 * @param type - тип операции исправления
	 */
	public TcAction (TcLocInfo defect, int correction, int type) {
		this(defect, correction, type, STATE_TODO);
	}
	/**
	 * используется коррекция по умолчанию (ручная)
	 * @param defect - исправляемый дефект
	 * @param type - тип операции исправления
	 */
	public TcAction (TcLocInfo defect, int type) {
		this(defect, TcLocInfo.DO_MANUALLY, type);
	}
	
	//----------------------
	// static
	//----------------------

	// sets data of the instance, using parameters   
	static protected void _init (TcAction me, int correction, int type, int state, TcLocInfo defect) {
		me.correction = correction;
		me._type = type;
		me.state = state;
		me.defect = defect;
	}
	static protected void _init (TcAction me, TcAction he) {
		_init( me,  he.correction, he._type, he.state, he.defect );
	}
	
	//----------------------
	// methods
	//----------------------
	
	// sets data of the instance, using parameters   
	public void set (int state, TcLocInfo defect) {
		_init( this,  correction, _type, state, defect );
	}
	
	// makes a new instance of the class with the same data 
	public TcAction copyOf () {
		try {
			return (TcAction) this.clone(); 
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return this; 
		}
	}
	
	// copies the data to another instance  
	public void copyTo (TcAction he) {
		_init( he,  this );
	}
	
}
