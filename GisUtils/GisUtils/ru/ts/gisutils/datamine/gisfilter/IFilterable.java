/**
 * Created on 27.03.2008 10:42:30 2008 by Syg
 * for project in 'ru.ts.gisutils.datamine' of 'test' 
 */
package ru.ts.gisutils.datamine.gisfilter;

/**
 * interface for any GIS object to check itself with a IGisFilter
 * 
 * @author Syg
 */
public interface IFilterable
{
	/**
	 * checks itself against parameter
	 * @param filter IGisFilter to check with
	 * @return <code>true</code> if object is satisfied by filter, else <code>false</code>.
	 * E.g. if this method is called at IGisObject with some IGisFilter and
	 * <code>true</code> is returned, it mean that object is IN the filter, namely,
	 * is in the drawing rectangle 
	 */
	boolean	isGoodForFilter(IGisFilter filter);
}
