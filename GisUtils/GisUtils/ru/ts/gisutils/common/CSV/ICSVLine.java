/**
 * Created 06.10.2008 12:00:04 by Syg for the "GisUtils" project
 */
package ru.ts.gisutils.common.CSV;

/**
 * Works to handle with a CSV separate line
 * 
 * @author Syg
 * 
 */
public interface ICSVLine
{
	/**
	 * Gets the field counter for this CSV line
	 * 
	 * @return int value for field count
	 */
	int field_count();

	/**
	 * Gets the field name at the index
	 * 
	 * @param index
	 *            index of field to get its name
	 * @return field name or <code>null</code> if no such name
	 */
	String field_name( int index );

	/**
	 * Gets the field value for the designated index of the field
	 * 
	 * @param index
	 *            int value for the field index
	 * @return String with value of the field or <code>null</code> if no such
	 *         field in the CSV file
	 */
	String field_value( int index ) throws IndexOutOfBoundsException;

	/**
	 * Gets the field value for the designated name of the field
	 * 
	 * @param field_name
	 *            the name for the field to get its value
	 * @return String with value of the field or <code>null</code> if no such
	 *         field in the CSV file
	 */
	String field_value( String field_name );
	
	/**
	 * Gets underlying CSV file path
	 * @return String with an underlying CSV file path
	 */
	String file_path();
}
