package ru.ts.gisutils.tcstore;

import java.util.ArrayList;
import java.util.Collections;

import ru.ts.gisutils.tc.*;

/**
 * serves to store different object inherited from TcObject
 * and help to seek them by TcId, not X,Y coordinates
 * 
 * @author sygsky
 * 
 */

public class TcObjectList extends ArrayList
{
	public transient static final ComparatorTcId	_cmpTcId;

	static
	{
		_cmpTcId = new ComparatorTcId();
	}

	/**
     * 
     */
    private static final long serialVersionUID = 1;

	/**
	 * Searches for the first occurence of the given argument, testing for
	 * equality using the <tt>equals</tt> method.
	 * 
	 * @param elem
	 *            an object.
	 * @return the index of the first occurrence of the argument in this
	 *         list; returns <tt>-1</tt> if the object is not found.
	 * @see Object#equals(Object)
	 */
	public int indexOf(Object elem)
	{
		if (elem == null)
		{
			for (int i = 0; i < size(); i++)
				if (get(i) == null)
					return i;
		}
		else
		{
			for (int i = 0; i < size(); i++)
				if ( _cmpTcId.compare(get(i), elem) == 0)
					return i;
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified object in
	 * this list.
	 * 
	 * @param elem
	 *            the desired element.
	 * @return the index of the last occurrence of the specified object in
	 *         this list; returns -1 if the object is not found.
	 */
	public int lastIndexOf(Object elem)
	{
		if (elem == null)
		{
			for (int i = size() - 1; i >= 0; i--)
				if (get(i) == null)
					return i;
		}
		else
		{
			for (int i = size() - 1; i >= 0; i--)
				if ( _cmpTcId.compare(get(i), elem) == 0)
					return i;
		}
		return -1;
	}
	
	/**
	 * sorts {@link TcId} list according to their native order by {@link ComparatorTcId}
	 *
	 */
	public void sort()
	{
		Collections.sort(this, _cmpTcId);
	}
	
	/**
	 * this method will work only if the list was previously sorted in ascending order
	 * @param obj - object to find
	 * @return index of found object if >= 0, else index of (-(insertion) -1)
	 */
	public int binarySearch(Object obj)
	{
		return Collections.binarySearch(this, obj, _cmpTcId);
	}
}
