/**
 * Created on 26.03.2008 13:06:27 2008 by Syg
 * for project in 'ru.ts.gisutils.geometry' of 'test' 
 */
package ru.ts.gisutils.geometry;

/**
 * Dimension Read Only interface
 * 
 * @author Syg
 */
public interface IDimensionRO
{
	/**
	 * gets value for the indicated dimension
	 * @param dim_index index of the dimension
	 * @return value for the indicated dimension
	 * @throws IndexOutOfBoundsException if <code>dim_index</code> 
	 * is out of bounds 
	 */
	double getDimension(int dim_index) throws IndexOutOfBoundsException;

}
