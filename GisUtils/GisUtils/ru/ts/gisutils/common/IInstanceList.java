/**
 * Created on 21.05.2008 17:21:27 2008 by Syg
 * for project in 'ru.ts.gisutils.common' of 'test' 
 */
package ru.ts.gisutils.common;

import java.util.ArrayList;

/**
 * this interface is used to store any number of instances of classes for common
 * purposes. It is created due to absence of pointers to pointers in Java, so
 * you can't return anything except single variable (address) in method and you
 * could need to return many ones.
 * 
 * @author Syg
 */
public interface IInstanceList
{
	
	/**
	 * size of storage
	 * 
	 * @return
	 */
	int size();
	
	/**
	 * get class instance at specified index
	 * 
	 * @param index
	 *            index of instance to return
	 * @return instance address or <code>null</code> if index out of range
	 */
	Object get( int index );
	
	/**
	 * returns first occurrence of the specified class/interface in the list.
	 * 
	 * @param cls
	 *            class instance to get its index in the list
	 * @return index of found class or -1 if no such class/interface found. If
	 *         instance is inherited to the specified class it is returned as if
	 *         it is the direct class itself
	 */
	int get_index( Class cls );
	
	/**
	 * returns class name at specified index
	 * 
	 * @param index
	 *            index of instance to return its name
	 * @return name of the instance as after call to the
	 *         <code>Instance.class.getName()</code>
	 */
	String get_class_name( int index );
	
	/**
	 * detects if instance in the list at specified index is an instance of the
	 * class, that user specified as second parameter
	 * 
	 * @param index
	 *            index of instance to check
	 * @param cls
	 *            class to check to be inherited at instance in the list
	 * @return <code>true</code> if instance at specified index is instance of
	 *         the specified class else <code>false</code>
	 */
	boolean isInstanceOf( int index, Class cls);
}
