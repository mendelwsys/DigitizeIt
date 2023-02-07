/**
 * Created on 06.06.2008 15:58:31 2008 by Syg for project in
 * 'ru.ts.gisutils.pcntxt.transform' of 'test'
 */
package ru.ts.gisutils.proj.transform;

/**
 * @author Syg
 */
public interface IPolyTransformer extends ITransformer
{
	/**
	 * number of coefficients
	 * 
	 * @return number of coefficient used for this polynomial transformation
	 */
	int size();

	/**
	 * gets polynomial coefficients
	 * 
	 * @param coeffs
	 *            coefficients of the polynomial. Should have size >= size(). If
	 *            size of the array is less than needed, only part of
	 *            coefficients is returned
	 * @param direct
	 *            if <code>true</code> direct polynomial coefficients are
	 *            returned, if <code>false</code>, inverse ones are returned
	 */
	void getPolynomial( double[] coeffs, boolean direct );
}
