/**
 * Created on 25.03.2008 14:33:32 2008 by Syg
 * for project in 'ru.ts.gisutils.geometry' of 'test' 
 */
package ru.ts.gisutils.geometry;

/**
 * handles with some geometry dimension, for example, 2D or 3D
 * @author Syg
 */
public interface IDimension extends IDimensionRO
{
	
	/**
	 * sets dimension to a new value
	 * @param dim_index index of dimension to set 
	 * @param new_val new value for the dimension
	 * @throws IndexOutOfBoundsException if <code>dim_index</code> 
	 * is out of bounds 
	 */
	void   setDimension(int dim_index, double new_val) throws IndexOutOfBoundsException;
}
