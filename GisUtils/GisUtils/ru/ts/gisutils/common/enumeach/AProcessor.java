/**
 * Created on 16.01.2008 11:46:52 2008 by Syg
 * for project in 'ru.ts.gisutils.common.enumeach' of 'test' 
 */
package ru.ts.gisutils.common.enumeach;

/**
 * @author Syg
 * 
 * an abstract dumb class to helps implement IProcessor descendants  
 */
public abstract class AProcessor implements IProcessor
{

	/* (non-Javadoc)
     * @see ru.ts.gisutils.common.enumeach.IProcessor#processItem(java.lang.Object)
     */
    public abstract boolean processItem( Object item );

}
