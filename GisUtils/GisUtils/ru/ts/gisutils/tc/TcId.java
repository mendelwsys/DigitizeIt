/*
 * Created on 19.07.2007
 *
 * TC is a shortening of Topology Cleaning
 */
package ru.ts.gisutils.tc;

/**
 * @author yugl
 *
 * To keep an object/feature id (in TC sense) 
 */
public class TcId implements Cloneable, Comparable {

	//----------------------
	// constants
	//----------------------
	
	// object (feature) states
	//----------------------
	// it is original state  
	public final static int ObjACTUAL = 0;
	// after deleting 
	public final static int ObjDELETED = 1;
	// after modification (changes in coordinates) 
	public final static int ObjMODIFIED = 2;
	// after splitting in several features
	public final static int ObjSPLITTED = 3;
	// after merging with another feature
	public final static int ObjMERGED = 4;

	//----------------------
	// fields
	//----------------------
	// number of iteration when the feature was created or zero, if the feature is original 
	protected int _iteration = 0;
	public int iteration () { return _iteration; }
	
	// internal number (into an iteration), the pair (iteration, number) should be unique among all the processed features
	protected int _number = 0;
	public int number () { return _number; }
	
	// feature status during TC process
	public int state = TcId.ObjACTUAL;
	
	//----------------------
	// constructors
	//----------------------
	public TcId (int iteration, int number) {
		_init( this,  iteration, number, TcId.ObjACTUAL );
	}
	public TcId (int iteration, int number, int state) {
		_init( this,  iteration, number, state );
	}

	//----------------------
	// static
	//----------------------

	// sets data of the instance, using parameters   
	static protected void _init (TcId me, int iteration, int number, int state) {
		me._iteration = iteration;
		me._number = number;
		me.state = state;
	}
	static protected void _init (TcId me, TcId he) {
		_init( me,  he._iteration, he._number, he.state );
	}
	
	/**
	 * Сравнение идентификаторов, меньше значит более ранний.
	 * @param id1 - первая точка
	 * @param id2 - вторая точка
	 * @return
	 */	
	static public int compare (TcId id1, TcId id2) {
		int i1;
		int i2;
		if ( (i1 = id1.iteration()) < (i2 = id2.iteration()) ) return -1;
		if ( i1 > i2 ) return  1;
		int n1;
		int n2;
		if ( (n1 = id1.number()) < (n2 = id2.number()) ) return -1;
		if ( n1 > n2 ) return  1;
		return 0;
	}
	
	//----------------------
	// Comparable
	//----------------------

	public int compareTo (Object obj) {
		return compare( this, (TcId)obj );
	}
	
	//----------------------
	// methods
	//----------------------

	// creates string representation of this object  
	public String toString () {
		return "i" + _iteration + "n" + _number;
	}
	
	// makes a new instance of the class with the same data 
	public TcId copyOf () {
		try {
			return (TcId) this.clone(); 
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return this; 
		}
	}
	
	// copies the data to another instance  
	public void copyTo (TcId he) {
		_init( he,  this );
	}
	
}
