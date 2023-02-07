/**
 * 
 */
package ru.ts.gisutils.tcstore;

import java.util.Collection;
import java.util.Iterator;
import ru.ts.gisutils.common.Colls;

import ru.ts.gisutils.tc.*;

/**
 * @author sygsky
 * handles with sorted list of TcObject objects. It has only unique values
 */
public class TcObjectListSorted extends TcObjectList
{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
     * @see java.util.ArrayList#add(int, java.lang.Object)
     */
    public void add(int arg0, Object arg1)
    {
	    add(arg1);
    }

	/* (non-Javadoc)
     * @see java.util.ArrayList#add(java.lang.Object)
     */
    public boolean add(Object arg0)
    {
	    int index = binarySearch(arg0);
	    if ( index < 0 )
	    {
	    	super.add(Math.abs( index + 1 ), arg0);
	    	return true;
	    }
	    return false;
    }

	/* (non-Javadoc)
     * @see java.util.ArrayList#addAll(java.util.Collection)
     */
    public boolean addAll(Collection arg0)
    {
	    if( super.addAll(arg0) )
	    {
	    	sort();
	    	return true;
	    }
	    return false;
    }

	/* (non-Javadoc)
     * @see java.util.ArrayList#addAll(int, java.util.Collection)
     */
    public boolean addAll(int arg0, Collection arg1)
    {
	    return addAll(arg1);
    }

	/* (non-Javadoc)
     * @see java.util.ArrayList#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
    	final int index = indexOf(o);
    	if ( index < 0)
    		return false;
	    super.remove(index);
	    return true;
    }

	/* (non-Javadoc)
     * @see java.util.ArrayList#set(int, java.lang.Object)
     */
    public Object set(int arg0, Object arg1)
    {
    	if (arg0 > size() || arg0 < 0)
    	    throw new IndexOutOfBoundsException(
    		"Index: "+arg0+", Size: "+size());

    	TcObjBase obj = (TcObjBase) arg1, listobj;
    	boolean isSorted = false;
    	/*
    	 * check if previous <= set <= follow
    	 */
    	// check previous value
    	if ( arg0 > 0)
    	{
    		listobj = (TcObjBase)this.get(arg0 - 1);
    		isSorted = listobj._tcid.compareTo(obj._tcid) <= 0;
    	}
   		//check follow value
    	if( arg0 < (size() - 1) )
		{
	   		listobj = (TcObjBase)this.get(arg0 + 1);
	   		isSorted &= obj._tcid.compareTo(listobj._tcid) <= 0;
		}
   		if ( isSorted )	// then consistency wasn't violated
   			return super.set(arg0, arg1);
   		// remove old value and add a new one
   		listobj = (TcObjBase)get(arg0);
   		super.remove(arg0);
   		add(arg1);
	    return listobj; //return previous value
    }

	/* (non-Javadoc)
     * @see java.util.AbstractCollection#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection arg0)
    {
    	Iterator it = arg0.iterator();
    	
    	while(it.hasNext())
    		if ( indexOf(it.next()) < 0 )
    			return false;
	    return true;
    }

	/* (non-Javadoc)
     * @see java.util.AbstractCollection#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection arg0)
    {
	    return Colls.removeAll(this, arg0);
    }

	/* (non-Javadoc)
     * @see java.util.AbstractCollection#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection arg0)
    {
	    return Colls.retainAll(this, arg0);
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.tc.TcObjectList#indexOf(java.lang.Object)
     */
    public int indexOf(Object elem)
    {
	    return this.binarySearch(elem);
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.tc.TcObjectList#lastIndexOf(java.lang.Object)
     */
    public int lastIndexOf(Object elem)
    {
	    return indexOf(elem);
    }

}
