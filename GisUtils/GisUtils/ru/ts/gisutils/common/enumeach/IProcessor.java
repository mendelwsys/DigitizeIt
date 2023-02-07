/**
 * Created on 16.01.2008 11:22:14 2008 by Syg for project in
 * 'ru.ts.gisutils.common.enumeach' of 'test'
 */
package ru.ts.gisutils.common.enumeach;

/**
 * @author Syg
 * 
 * class stands to process each item in some collection having IEnumerable
 * 
 */
public interface IProcessor
{
	/**
	 * any kind collection with IEnumerable interface uses this interface object
	 * to visit all its items by calling this method with each item as argument.
	 * This proceeds while method returns true. If it returns false, enumeration
	 * exits.
	 * 
	 * @param item
	 *            item of collection. May be of any class depending on the
	 *            programmer goal.
	 * @return <code>true</code> if user needs to continue enumerating items,
	 *         else <code>false</code>.
	 */
	boolean processItem( Object item );
}
