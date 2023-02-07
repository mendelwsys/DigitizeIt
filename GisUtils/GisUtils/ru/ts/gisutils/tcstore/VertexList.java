package ru.ts.gisutils.tcstore;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
//import ru.ts.gisutils.common.Sys;

import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.XY;
import ru.ts.gisutils.tc.*;
import ru.ts.utils.FunctEmul;

/**
 * @author sygsky
 * 
 * used to store vertices
 */
public class VertexList extends VertexCollection implements List
{

	/**
	 * main constructor for this object with external list object as parent
	 * 
	 * @param list
	 *            lists objects to store in this object
	 */
	public VertexList(List list)
	{
		super(list);
	}

	/**
	 * main constructor for this object without parameters
	 */
	public VertexList()
	{
		super();
	}

	protected VertexList(VertexList vl)
	{
		super(vl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int arg0, Object arg1)
	{
		super._list.add(arg0, arg1);
		super.OnAddIGetXY((IGetXY) arg1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int arg0, Collection arg1)
	{
		if (super._list.addAll(arg0, arg1))
		{
			super.OnChange();
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#get(int)
	 */
	public Object get(int index)
	{
		return _list.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o)
	{
		return _list.indexOf(o);
	}
	
	public int indexOfTcObject(ITcObjBase o)
	{
		for(int i = 0; i < size(); i++)
			if ( TcObjectList._cmpTcId.compare( get(i), o) == 0 )
				return i;
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o)
	{
		return _list.lastIndexOf(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#listIterator()
	 */
	public ListIterator listIterator()
	{
		return _list.listIterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator listIterator(int index)
	{
		return _list.listIterator(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#remove(int)
	 */
	public Object remove(int index)
	{
		final Object o = _list.remove(index);
		super.OnChange();
		return o;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public Object set(int arg0, Object arg1)
	{
		final Object o = _list.set(arg0, arg1);
		super.OnAddIGetXY((IGetXY)arg1);
		return o;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#subList(int, int)
	 */
	public List subList(int fromIndex, int toIndex)
	{
		return new VertexList(_list.subList(fromIndex, toIndex));
	}

	/**
	 * returns last vertext in the list
	 * 
	 * @return Object (IGetXY interface of TcObjVertex) which is last in the
	 *         list of polyline
	 */
	public Object getLastVertex()
	{
		return get(size() - 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ts.gisutils.tc.VertexCollection#clone()
	 */
	public Object clone() throws CloneNotSupportedException
	{
		return new VertexList(this);
	}

	/**
	 * gets coordinates of vertices and add them to the list in indicated order
	 * 
	 * @param startIndex -
	 *            starting point index to get coordinates
	 * @param length -
	 *            length of extraction, if value of this argument < 0 - 
	 *            extraction is going in opposite direction, that is from right to left.
	 */
	public double[] getPoints(int startIndex, int length)
	{
		int len = Math.abs(length);
		final double[] ret = new double[len * 2];
		final int step = FunctEmul.isignum(length);
		for (int i = 0; length != 0; startIndex += step, length -= step)
		{
			XY obj = (XY) (((TcObjVertex) this.get(startIndex))._xy);
			ret[ i++ ] = obj.x;
			ret[ i++ ] = obj.y;
		}
		return ret;
	}

	/**
	 * @param dst -
	 *            destination array
	 * @param dstoff -
	 *            destination offset
	 * @param srcoff -
	 *            source offset
	 * @param length -
	 *            number of points coordinates (always even value!) in sub-array
	 *            to copy, if negative, copy would be produced by decrementing
	 *            array pointer from srcoff position down to the length absolute
	 *            value
	 * @return number of coordinates copied to the destination buffer
	 */
	public int getPoints(final double[] dst, int dstoff, final int srcoff, final int length )
	{
		TcObjVertex vert;
		final int step = FunctEmul.isignum(length);
		for( int i = srcoff; i != srcoff + length; i += step)
		{
			vert = (TcObjVertex)this.get(i);
			dst[dstoff++] = vert.getX();
			dst[dstoff++] = vert.getY();
		}
		return length * 2;
	}
}
