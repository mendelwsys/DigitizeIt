/**
 * Created on 16.01.2008 11:41:20 2008 by Syg
 * for project in 'ru.ts.gisutils.common.enumeach' of 'test' 
 */
package ru.ts.gisutils.common.enumeach;

import java.util.Enumeration;

/**
 * @author Syg
 * 
 * abstract class to helps implement IEnumerable interface. 
 * It implements IEnumerable in abstract manner
 * 
 */
public abstract class AEnumerable implements IEnumerable
{

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.enumeach.IEnumerable#doForEach(ru.ts.gisutils.common.enumeach.IProcessor)
     */
    public void doForEach( IProcessor proc )
    {
    	while ( hasMoreElements() )
    		if ( ! proc.processItem( nextElement() ) ) 
    				return;
    }

	/* (non-Javadoc)
     * @see java.util.Enumeration#hasMoreElements()
     */
    public abstract boolean hasMoreElements();

	/* (non-Javadoc)
     * @see java.util.Enumeration#nextElement()
     */
    public abstract Object nextElement();

    public abstract Enumeration getEnumeration();
    
}