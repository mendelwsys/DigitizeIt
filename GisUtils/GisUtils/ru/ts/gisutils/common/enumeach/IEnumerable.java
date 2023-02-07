/**
 * Created on 16.01.2008 11:24:56 2008 by Syg for project in
 * 'ru.ts.gisutils.common.enumeach' of 'test'
 */
package ru.ts.gisutils.common.enumeach;

import java.util.Enumeration;

/**
 * @author Syg
 * 
 * interface works to process each item of some internal collection
 * implementing this interface
 * 
 * common example is as follow: 
 * <code><br><br>
 *  IEnumerable coll = new SomeColl(...)<br>
 *  IProcessor proc = new SomeProc(...);<br>
 *  coll.doForEach(proc);<br>
 *  </code>
 */
public interface IEnumerable extends Enumeration
{

	/**
	 * enumerate through all collection and call the method
	 * <code>doForEach</code> with each item of a collection
	 * 
	 * @param proc
	 *            class to call its method <code>proc.processItem</code> with
	 *            each item of collection as the argument.
	 */
	void doForEach( IProcessor proc );
	
	/**
	 * gets enumeration for the collection supported by implementing class
	 * @return Enumeratijn interface
	 */
	Enumeration getEnumeration();
}
