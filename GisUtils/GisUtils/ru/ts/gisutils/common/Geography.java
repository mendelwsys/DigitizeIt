/**
 * Created on 14.11.2007 15:51:29 2007 by Syg
 * for project in 'ru.ts.gisutils.common' of 'test' 
 */
package ru.ts.gisutils.common;

/**
 * @author Syg
 *
 * special geography objects are serviced here
 */
public class Geography
{
	public final static char DegreeChar = 0xB0; //'\°';
	public final static char MinuteChar = '\'';
	public final static char SecondChar = '"';
	
	/**
	 * creates string with standard form of degrees notation, i.e. 52°46'50.245"
	 * @param value value of degree to convert into a text
	 * @return String with text representation of a degree value
	 */
	public static String DecimalDegreeToString(double value)
	{
		final double degrees = Math.floor(value);
		final int idegrees = (int)Math.round(degrees);
		value = (value - degrees) * 60.0d;
		
		final double mins = Math.floor(value);
		final int imins = (int)Math.round(mins);
		value = (value - mins) * 60.0d;
		
		final double secs = Math.floor(value) * 60.0d;
		final int isecs =  (int)Math.round(secs);
		value = (value - secs) * 100000.0d;
		final int imillis = (int)Math.floor(value); 
		return String.valueOf(idegrees) + DegreeChar +  imins + MinuteChar +
		isecs + ( (imillis > 0) ? "." + imillis : "") + SecondChar;
	}

}
