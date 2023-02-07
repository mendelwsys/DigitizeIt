/**
 * Created on 21.05.2008 17:40:01 2008 by Syg
 * for project in 'ru.ts.gisutils.common' of 'test' 
 */
package ru.ts.gisutils.common;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Syg
 */
public class InstanceList implements IInstanceList
{

	private ArrayList _list;

	/**
	 * the only needed constructor
	 */
	public InstanceList()
	{
		_list = new ArrayList();
	}
	
	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.IInstanceList#get(int)
	 */
	public Object get( int index )
	{
		return _list.get( index );
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.IInstanceList#get_class_name(int)
	 */
	public String get_class_name( int index )
	{
		return get( index ).getClass().getName();
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.IInstanceList#get_index(java.lang.Class)
	 */
	public int get_index( Class cls )
	{
		for ( int i = 0; i < size(); i++ )
	        if ( cls.isAssignableFrom( get(i).getClass()) )
	        	return i;
		return -1;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.IInstanceList#isInstanceOf(int, java.lang.Class)
	 */
	public boolean isInstanceOf( int index, Class cls )
	{
		return cls.isAssignableFrom( get(index).getClass() );
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.common.IInstanceList#size()
	 */
	public int size()
	{
		return _list.size();
	}

	/**
	 * clears the internal list
	 */
	public void clear()
	{
		_list.clear();
	}
	
	/**
	 * append new instance
	 * @param inst instance to add
	 * @return new {@link #size()}
	 */
	public int add( Object inst )
	{
		_list.add( inst );
		return	size();
	}
	
	/**
	 * remove instance at specified index
	 * @param index to remove from a list
	 * @return new {@link #size()}
	 */
	public int remove( int index )
	{
		_list.remove( index );
		return size();
	}
	
}
