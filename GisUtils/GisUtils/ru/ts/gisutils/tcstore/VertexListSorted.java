/**
 * 
 */
package ru.ts.gisutils.tcstore;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import ru.ts.gisutils.geometry.IGetXY;

/**
 * @author sygsky, created on 11.09.2007 11:28:34, (@)copyright by JHC, 1987-2007
 * It is possible that this class will be removed as useless for our purposes 
 */
public class VertexListSorted extends VertexList
{
	private boolean _sorted;
	/**
	 * @param list
	 */
	public VertexListSorted(List list)
	{
		super(list);
		_sorted = false;
	}

	/**
	 * 
	 */
	public VertexListSorted()
	{
		super();
		_sorted = false;
	}

	/* (non-Javadoc)
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int arg0, Object arg1)
	{
		if ( _list.size() > 1)
			if (arg0 > 0)
			{
				if ( arg0 < _list.size() - 1)
				{
					_sorted = 
						(((TcObjVertex)arg1)._tcid.compareTo(_list.get(arg0-1)) >= 0)
						&&
						(((TcObjVertex)arg1)._tcid.compareTo(_list.get(arg0)) <= 0);
				}
				else
					_sorted = (((TcObjVertex)arg1)._tcid.compareTo(_list.get(_list.size() - 1)) >= 0); 
			}
			else
				_sorted = (((TcObjVertex)arg1).compareTo(_list.get(0)) <= 0);
		super._list.add(arg0, arg1);
		super.OnAddIGetXY((IGetXY)arg1);
	}

	/* (non-Javadoc)
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int arg0, Collection c)
	{		
		boolean res = super._list.addAll(arg0, c);
		if ( res )
		{
			super.OnChange();
			_sorted = false;
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o)
	{
		if (!_sorted)
		{
			Collections.sort(_list);
			_sorted = true;
		}
		return Collections.binarySearch(_list,o);
	}
	
	/* (non-Javadoc)
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o)
	{
		return indexOf(o);
	}
	
	public boolean contains(Object o)
    {
		return indexOf(o) >= 0;
    }

	public boolean remove(Object o)
	{
		int index = indexOf(o);
		if( index >= 0)
		{
			remove(index);
			return true; 
		}
		return false;
	}

	public boolean removeAll(Collection c)
    {
		final Object[] arr = c.toArray();
		Arrays.sort(arr);
		if (!_sorted)
		{
			Collections.sort(_list);
			_sorted = true;
		}
		
		final int old_size = _list.size();
		int ind;
		for( int index = arr.length; index >= 0; index--)
		{
			ind = Collections.binarySearch(_list, arr[index]);
			if ( ind >= 0)
				_list.remove(ind);
		}
		
	    if (old_size != _list.size())
	    {
	    	super.OnChange();
	    	return true;
	    }
	    return false;
    }
	
	/* (non-Javadoc)
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public Object set(int arg0, Object arg1)
	{
		final Object o = super.set(arg0, arg1);
		if ( _list.size() > 1)
			if (arg0 > 0)
			{
				if ( arg0 < _list.size() - 1)
				{
					_sorted = 
						(((TcObjVertex)arg1)._tcid.compareTo(_list.get(arg0-1)) >= 0)
						&&
						(((TcObjVertex)arg1)._tcid.compareTo(_list.get(arg0 + 1)) <= 0);
				}
				else
					_sorted = (((TcObjVertex)arg1)._tcid.compareTo(_list.get(_list.size() - 2)) >= 0); 
			}
			else
				_sorted = (((TcObjVertex)arg1).compareTo(_list.get(1)) <= 0);

		super.OnChange();
		return o;
	}

	public boolean retainAll(Collection c)
    {
		final Object[] arr = c.toArray();
		Arrays.sort(arr);
		Object key;
		final int old_size = _list.size(); 
		for( int i = _list.size() - 1; i >= 0; i++)
		{
			key = _list.get(i);
			if ( Arrays.binarySearch(arr, key) < 0 )
				_list.remove(i);
		}
		if (_list.size() != old_size)
		{
			super.OnChange();
			return true;
		}
		return false;
    }


}
