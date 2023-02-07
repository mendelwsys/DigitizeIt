/*
 * Created on 07.08.2007
 *
 */
package ru.ts.gisutils.tc;

import java.util.*;

import ru.ts.gisutils.geometry.IXYGeometry;
import ru.ts.gisutils.geometry.Rect;

/**
 * @author yugl
 *
 * Данные, описывающие итерацию процесса чистки.
 */
public class TcIteration implements Cloneable {

	//----------------------
	// constants
	//----------------------
	
	// iteration tasks
	//----------------------
	// no task  
	public final static int PROC_NONE = 0;
	// process nodes with the same coordinates 
	public final static int PROC_DUPE_NODES = 100;
	// process nodes with the near coordinates 
	public final static int PROC_NEAR_NODES = 200;
	// match vertices with nodes 
	public final static int MATCH_NODES_WITH_VERTS = 300;
	// process dangling vertices near nodes 
	public final static int PROC_VERTS_NEAR_NODES = 400;
	// process dangling vertices near (dangling) vertices 
	public final static int PROC_NEAR_VERTS = 500;
	// process dangling nodes near lines 
	public final static int PROC_NODES_NEAR_LINES = 600;
	// process small lines whose end vertices have the same coordinates 
	public final static int PROC_SMALL_LOOP_LINES = 700;
	// process lines intersection 
	public final static int PROC_LINE_INTERSECTIONS = 800;

	// iteration states
	//----------------------
	// initial state  
	public final static int STATE_TODO = 0;
	// processing done
	public final static int STATE_DONE = 1;
	// processing canceled  
	public final static int STATE_SKIP = 2;

	// small value to limit the delta  
	public final static double MIN_DELTA = 0.001;

	//----------------------
	// fields
	//----------------------
	
	/**
	 * номер итерации
	 */ 
	protected int _id = 0;
	public int id() { return _id; }
	
	/**
	 * номер объекта, создаваемого внутри итерации
	 */ 
	protected int _number = 0;
	public int nextNumber() { return ++_number; }
	
	/**
	 * новый идентификатор
	 */ 
	public TcId newTcId() { return new TcId(id(), nextNumber()); }
	
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
	
	/**
	 * задание 
	 */ 
	protected int _task = PROC_NONE;
	public int task() { return _task; }
	
	// state of iteration, the constants above should be used
	/**
	 * состояние 
	 */ 
	protected int _state = STATE_TODO;
	public int state() { return _state; }
			
	// precision of calculations 
	/**
	 * дельта, используемая при определении дефекта
	 */ 
	protected double _delta = 0;		
	public double delta() { return _delta; }
	
	/**
	 * место, где идет чистка (если не задано, то везде)
	 */ 
	protected Rect _place = null;						
	public Rect place() { 
		if (_place != null) return _place.copyOf(); 
		else return null; 
	}

	/**
	 * Обнаруженные на данном шаге дефекты (TcLocInfo)
	 */	
	protected List _found = null;
	public List found() { return _found; }
	public void addFound(TcLocInfo info) { _found.add(info); }
	public void clearFound() { _found = new ArrayList(); }
	
	/**
	 * Предлагаемые для исправления дефектов операции (TcAction)
	 */	
	protected List _doing = null;
	public List doing() { return _doing; }
	public void addDoing(TcAction action) { _doing.add(action); }
	public void clearDoing() { _doing = new ArrayList(); }
	
	//----------------------
	// constructors
	//----------------------
	
	protected TcIteration () {
	}
	/**
	 * @param id - идентификатор итерации
	 * @param tcStore - доступ к хранилищу данных
	 * @param task - задание на итерацию (тип дефекта)
	 * @param delta - погрешность, используемая при определении дефекта
	 * @param place - место поиска дефектов
	 */
	public TcIteration (int id, ITcDataStore tcStore, int task, double delta, Rect place) {
		_init( this,  id, tcStore, task, STATE_TODO, delta, place );
	}
	/**
	 * @param id - идентификатор итерации
	 * @param tcStore - доступ к хранилищу данных
	 * @param task - задание на итерацию (тип дефекта)
	 * @param delta - дельта, используемая при определении дефекта
	 */
	public TcIteration (int id, ITcDataStore tcStore, int task, double delta) {
		this( id, tcStore, task, delta, null );
	}
	/**
	 * @param tcStore - доступ к хранилищу данных
	 * @param task - задание на итерацию (тип дефекта)
	 * @param delta - погрешность, используемая при определении дефекта
	 * @param place - место поиска дефектов
	 */
	public TcIteration (ITcDataStore tcStore, int task, double delta, Rect place) {
		this( 1, tcStore, task, delta, place );
	}
	/**
	 * @param tcStore - доступ к хранилищу данных
	 * @param task - задание на итерацию (тип дефекта)
	 * @param delta - дельта, используемая при определении дефекта
	 */
	public TcIteration (ITcDataStore tcStore, int task, double delta) {
		this( tcStore, task, delta, null );
	}
	/**
	 * @param tcStore - доступ к хранилищу данных
	 * @param task - задание на итерацию (тип дефекта)
	 * @param place - место поиска дефектов
	 */
	public TcIteration (ITcDataStore tcStore, int task, Rect place) {
		this( tcStore, task, 0, place );
	}
	/**
	 * @param tcStore - доступ к хранилищу данных
	 * @param task - задание на итерацию (тип дефекта)
	 */
	public TcIteration (ITcDataStore tcStore, int task) {
		this( tcStore, task, 0, null );
	}
	
	//----------------------
	// static
	//----------------------

	// sets data of the instance, using parameters   
	static protected void _init (TcIteration me, int id, ITcDataStore tcStore, int task, int state, double delta, Rect place) {
		me._id = id;
		me._tcStore = tcStore;
		me._geometry = tcStore.getGeometry();
		me._task = task;
		me._state = state;
		me._delta = delta;
		me._place = place;
		// check delta
		if (me._delta < TcIteration.MIN_DELTA) me._delta = TcIteration.MIN_DELTA;		
		// check iteration id
		if (me._id < 1) me._id = 1;		
		// prepare empty list of defects
		me.clearFound();				
		// prepare empty list of correction actions
		me.clearDoing();				
	}
	static protected void _init (TcIteration me, TcIteration he) {
		_init( me,  he._id, he._tcStore, he._task, he._state,  he._delta, he._place );
	}
	
	//----------------------
	// Cloneable
	//----------------------

	// sets data of the instance, using parameters   
	public Object clone () {
		try {
			TcIteration he = (TcIteration) super.clone();
			if (_place != null) he._place = _place.copyOf();
			return he; 
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return this; 
		}
	}
	
	//----------------------
	// methods
	//----------------------
	
	// makes a new instance of the class with the same data 
	public TcIteration copyOf () {
		return (TcIteration) this.clone(); 
	}
	
	// copies the data to another instance  
	public void copyTo (TcIteration he) {
		_init( he,  this );
	}
	
}
