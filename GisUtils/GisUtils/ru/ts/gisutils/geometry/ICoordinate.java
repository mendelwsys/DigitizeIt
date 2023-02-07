/**
 * Created on 07.11.2007 16:20:59 2007 by Syg for project in
 * 'ru.ts.gisutils.datamine' of 'test'
 */
package ru.ts.gisutils.geometry;


/**
 * handles with single coordinate in 3D dimension
 * 
 * @author Syg
 */
public interface ICoordinate extends IXY
{
	/**
	 * z coordinate getter
	 * 
	 * @return Z coordinate value
	 */
	double getZ();

	/**
	 * Z coordinate setter
	 * 
	 * @param val
	 *            new double value for Z
	 */
	void setZ( double val );
	
	/**
	 * returns number of dimensions stored in this object
	 * 
	 * @return int value of 1, 2 or 3 depending on realization
	 */
	int getDimensions();
	
	/**
	 * gets indicated dimension value
	 * @param index index of dimension, must be in range [0.. getDimensions()-1]
	 * @return <code>double</code> value of the dimension of Double.NaN if 
	 * no such dimension 
	 */
	double getDimension(int index) throws IndexOutOfBoundsException;
	
	/**
	 * sets indicated dimension value
	 * @param index index of dimension, must be in range [0.. getDimensions()-1]
	 * @param value new <code>double</code> value for the dimension
	 * @returns <code>true</code> if index is in range and value is set, else <code>false</code>
	 */
	boolean setDimension(int index, double value);

}
