package ru.ts.gisutils.algs.common;

import ru.ts.gisutils.geometry.ICoordinate;

/**
 * To change this template use File | Settings | File Templates.
 */
public interface IMCoordinate extends ICoordinate
{
	/**
	 * Measure getter
	 *
	 * @return Measure value
	 */
	double getM();

	/**
	 * Measure setter
	 *
	 * @param val
	 *            new double value for Measure
	 */
	void setM( double val );

}
