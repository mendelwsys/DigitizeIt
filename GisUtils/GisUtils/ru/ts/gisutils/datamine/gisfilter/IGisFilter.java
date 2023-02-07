/**
 * Created on 27.03.2008 10:46:08 2008 by Syg
 * for project in 'gisfilter' of 'test' 
 */
package ru.ts.gisutils.datamine.gisfilter;

import java.util.List;

/**
 * basic GIS filter interface
 * @author Syg
 */
public interface IGisFilter
{
	/**
	 * filter class. Now only one class is supported - IGisVolume, that is
	 * filter by bounding volume (in 2D case by bounding rect)
	 * @return Class of the filter object.
	 */
	Class getFilterClass();
	
	/**
	 * main method to check object by this filter
	 * @param object IFilterable interface object to check with the filter 
	 * @return <code>true</code> if object is IN filter or <code>false</code> if not 
	 */
	boolean isGoodForFilter( IFilterable object );
	
	/**
	 * filter object collection by internal filter
	 * @param collection of objects implementing {@link ru.ts.gisutils.datamine.gisfilter.IFilterable} interface 
	 * @return corrected collection with objects filtered in
	 */
	List	filterCollection(List collection);
	
}
