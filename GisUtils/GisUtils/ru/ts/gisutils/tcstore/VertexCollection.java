/**
 * 
 */
package ru.ts.gisutils.tcstore;

import java.lang.Cloneable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.IXY;
import ru.ts.gisutils.geometry.Rect;
import ru.ts.gisutils.tc.*;

/**
 * @author sygsky
 * 
 */
// @SuppressWarnings(value="unchecked")
public class VertexCollection implements IGetVertices, Collection, Cloneable
{

	/**
	 * ArrayList of internally stored IGetXY interface objects
	 */
	protected ArrayList	_list;

	/**
	 * bounding rectangle around all objects in the
	 * {@link ru.ts.gisutils.IGetXY}. Supported in actual state after all
	 * operations with objects
	 */
	protected Rect	    _rect;

	public VertexCollection()
	{
		_list = new ArrayList();
		_rect = Rect.empty();
	}

	public VertexCollection(Collection coll)
	{
		this();
		if (_list.addAll(coll))
			calcRect();
	}

	protected VertexCollection(VertexCollection coll)
	{
		this();
		_list.addAll(coll);
		_rect = coll.rect();
	}

	/**
	 * calculate bounding rect around the currect data
	 */
	protected Rect calcRect()
	{
		_rect.setEmpty();
		for (int index = 0; index < _list.size(); index++)
			_rect.extend((IGetXY) _list.get(index));
		return _rect;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.tc.IGetVertices#copyRect(ru.ts.gisutils.tc.Rect)
	 */
	public void copyRect(Rect rect)
	{
		_rect.copyTo(rect);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.tc.IGetVertices#copyXY(int, ru.ts.gisutils.tc.XY)
	 */
	public void copyXY(int index, IXY xy)
	{
		((IGetXY) _list.get(index)).copyTo(xy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.tc.IGetVertices#getXY(int)
	 */
	public IGetXY getXY(int index)
	{
		return (IGetXY) _list.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.tc.IGetVertices#rect()
	 */
	public Rect rect()
	{
		return _rect.copyOf();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.tc.IGetVertices#size()
	 */
	public int size()
	{
		return _list.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#add(Object o)
	 */
	public boolean add(Object o)
	{
		IGetXY xy = (IGetXY) o;
		_rect.extend(xy);
		return _list.add((IGetXY) o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#addAll(Collection c)
	 */
	public boolean addAll(Collection c)
	{
		if (_list.addAll(c))
		{
			calcRect();
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#clear()
	 */
	public void clear()
	{
		_list.clear();
		_rect.setEmpty();
	}

	public boolean contains(Object o)
	{
		return _list.contains(o);
	}

	public boolean containsAll(Collection c)
	{
		Iterator e = c.iterator();
		while (e.hasNext())
			if (!contains(e.next()))
				return false;
		return true;
	}

	public boolean isEmpty()
	{
		return _list.isEmpty();
	}

	public Iterator iterator()
	{
		return _list.iterator();
	}

	public boolean remove(Object o)
	{
		if (contains(o))
		{
			final IGetXY xy = (IGetXY) o;
			if (!_rect.has(xy)) // if point is on the boundary, reset bounding
								// rect
				calcRect();
			return _list.remove(o);
		}
		return false;
	}

	public boolean removeAll(Collection c)
	{
		if (_list.removeAll(c))
		{
			calcRect(); // reset bounding rect
			return true;
		}
		return false;
	}

	public boolean retainAll(Collection c)
	{
		if (_list.retainAll(c))
		{
			OnChange();
			return true;
		}
		return false;
	}

	public Object[] toArray()
	{
		return _list.toArray();
	}

	public Object[] toArray(Object[] a)
	{
		return _list.toArray(a);
	}

	/**
	 * @param xy
	 *            IGetXY object to add
	 */
	protected void OnAddIGetXY(IGetXY xy)
	{
		_rect.extend(xy);
	}

	/**
	 * call this proc on any change of not defined results
	 */
	protected void OnChange()
	{
		calcRect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException
	{
		return new VertexCollection(this);
	}

}
