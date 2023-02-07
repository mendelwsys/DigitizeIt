/**
 * 
 */
package ru.ts.gisutils.tcstore;

import java.util.Comparator;

import ru.ts.gisutils.tc.*;

/**
 * @author sygsky
 *
 */
public class ComparatorTcId implements Comparator
{

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1)
	{
		return ((TcObjBase)arg0)._tcid.compareTo(((TcObjBase)arg1)._tcid);
	}

}
