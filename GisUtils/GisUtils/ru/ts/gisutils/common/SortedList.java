/**
 * 
 */
package ru.ts.gisutils.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author sygsky
 * 
 * class to store sorted array. Allows to find objects very fast. You can
 * add multiple times sequentially with efficiency, 
 * and find sequentialaly also with efficiency.
 * Of course, all elements of list should be of Comparable type
 */
//@SuppressWarnings("unchecked")
public class SortedList extends ArrayList
{
	/**
	 * @param arg0
	 */
	public SortedList(Collection arg0)
	{
		super(arg0);
		_sorted = false;
	}

	private static final long	serialVersionUID	= 1944552026230188536L;

	/*
	 * defines is internal list sorted or not
	 */
	private boolean	          _sorted;

	/*
	 * sort internal list only if that is not sorted before
	 */
	public void sort()
	{
		if (_sorted)
			return;
		Collections.sort(this);
		_sorted = true;
	}

	/*
	 * make list unique - removes duplicated entries
	 */
	public void unique()
	{
		Colls.unique(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
    public boolean add(Object arg0)
	{
		_sorted = false;
		return super.add(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection arg0)
	{
		_sorted = false;
		return super.addAll(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int arg0, Collection arg1)
	{
		_sorted = false;
		return super.addAll(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#set(int, java.lang.Object)
	 */
	public Object set(int arg0, Object arg1)
	{
		_sorted = false;
		return super.set(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#contains(java.lang.Object)
	 */
	public boolean contains(Object elem)
	{
		sort();
		return indexOf(elem) >= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#indexOf(java.lang.Object)
	 */
	public int indexOf(Object elem)
	{
		sort();
		return Collections.binarySearch(this, elem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object elem)
	{
		int index, lastindex;
		if ((index = indexOf(elem)) < 0 )
			return index;
		if ( index >= (lastindex = size() - 1))
			return index;

		/* 
		 * check if some righter elems with the same value exists
		 * in our sorted list
		 */
		
		do
		{
			if (!elem.equals(get(++index)))
				return --index;
		} while (index < lastindex);
		return index;
	}

}
