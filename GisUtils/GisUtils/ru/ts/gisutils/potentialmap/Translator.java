/**
 * Created on 14.05.2008 14:40:08 2008 by Syg for project in
 * 'ru.ts.gisutils.potentialmap' of 'test'
 */
package ru.ts.gisutils.potentialmap;

/**
 * class to translate potential values for output only
 * 
 * @author Syg
 */
public class Translator
{
	/**
	 * override to change translate before output. Used for legen max setting
	 * and in getPotential method of Builder class
	 * 
	 * @param value
	 *            value to translate
	 * @return new value. Now action is empty, input value is returned as a
	 *         result
	 */
	public double translatevalue( double value )
	{
		return value;
	}
}
