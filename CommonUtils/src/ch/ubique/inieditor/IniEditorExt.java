/**
 * Created 28.07.2008 12:02:29 by Syg for the "GisUtils" project
 */
package ch.ubique.inieditor;

import ru.ts.utils.DateTime;

import java.util.Date;


/**
 * Extends IniEditor to allow reading default values if absent and read specific
 * primitive type values (int, double etc)
 * 
 * @author Syg
 * 
 */
public class IniEditorExt extends IniEditor
{

	public IniEditorExt()
    {
	    super();
    }

	public IniEditorExt( boolean isCaseSensitive )
    {
	    super( isCaseSensitive );
    }

	public IniEditorExt( String commonName, boolean isCaseSensitive )
    {
	    super( commonName, isCaseSensitive );
    }

	public IniEditorExt( String commonName )
    {
	    super( commonName );
    }

	/**
	 * Gets value from designated section an option of INI file. If no such
	 * value exists in the file and in common section, default value is returned
	 * 
	 * @param section
	 *            String with a section name
	 * @param option
	 *            String of an option name
	 * @param def_value
	 *            String default value to return if no entry exists
	 * @return String with found value else def_value is returned
	 */
	public String get( String section, String option, String def_value )
	{
		final String res = super.get( section, option );
		return res == null ? def_value : res;
	}

	/**
	 * Gets integer from an designated section and option of an INI file. If no
	 * such value exists in the file and in common section, default value is
	 * returned
	 * 
	 * @param section
	 *            String with a section name
	 * @param option
	 *            String of an option name
	 * @param def_value
	 *            String default value to return if no entry exists
	 * @return int value if found else def_value
	 * @throws NumberFormatException
	 *             if not an integer representation string is read from INI
	 */
	public int get_int( String section, String option, int def_value )
	        throws NumberFormatException
	{
		final String res = super.get( section, option );
		return res == null  ? def_value : Integer.parseInt( res );
	}
	
	/**
	 * Sets the integer value for the designated section, option of an INI file
	 * @param section
	 *            String with a section name
	 * @param option
	 *            String of an option name
	 * @param value new integer value to set for the option
	 */
	public void set_int(String section, String option, int value )
	{
		super.set( section, option, Integer.toString( value ) );
	}
	
	/**
	 * Gets Date from an designated section and option of an INI file. If no
	 * such value exists in the file and in common section, default value is
	 * returned
	 * 
	 * @param section
	 *            String with a section name
	 * @param option
	 *            String of an option name
	 * @param def_value
	 *            Date default value to return if no entry exists
	 * @return Date instance  if found else def_value
	 * @throws NumberFormatException
	 *             if not an Date representation string is read from INI
	 */
	public Date get_date( String section, String option, Date def_value ) throws NumberFormatException
	{
		final String res = super.get( section, option );
		return res == null ? def_value : DateTime.parseDate( res );
	}
	
	/**
	 * Sets the Date value for the designated section and option of an INI file
	 * @param section
	 *            String with a section name
	 * @param option
	 *            String of an option name
	 * @param value new Date value to set for the option
	 */
	public void set_date(String section, String option, Date value )
	{
		super.set( section, option, DateTime.date2StdString( value ) );
	}
	
	/**
	 * Gets long from an designated section and option of an INI file. If no
	 * such value exists in the file and in common section, default value is
	 * returned
	 * 
	 * @param section
	 *            String with a section name
	 * @param option
	 *            String of an option name
	 * @param def_value
	 *            long default value to return if no entry exists
	 * @return long value  if found else def_value
	 * @throws NumberFormatException
	 *             if not an long representation string is read from INI
	 */
	public long get_long( String section, String option, long def_value ) throws NumberFormatException
	{
		final String res = super.get( section, option );
		return res == null ? def_value: Long.parseLong( res ); 
	}
	
	/**
	 * Sets the long value for the designated section and option of an INI file
	 * @param section
	 *            String with a section name
	 * @param option
	 *            String of an option name
	 * @param value a long value to set for the option
	 */
	public void set_long(String section, String option, long value )
	{
		super.set( section, option, Long.toString( value ) );
	}
}
