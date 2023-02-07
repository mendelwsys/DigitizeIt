/**
 * Created 06.10.2008 11:55:32 by Syg for the "TrainsGraph" project
 */
package ru.ts.gisutils.common.CSV;

/**
 * Interface works to visit each line in all CSV files selected by the user
 * call. Its functionality is near to iterator but you are visiting all line in
 * collection without any user defined loops
 * 
 * @author Syg
 * 
 */
public interface ICSVVisitor
{
	/**
	 * Method called for each item in iStatSet collection
	 * 
	 * @param item
	 *            ICSVLine interface instance. You know that this is the line
	 *            access interface. You can do anything with it - change, count
	 *            etc, but please not do it, as it is useless, only read fields
	 *            you are interested, no more.
	 */
	void visitLine( ICSVLine item );
}
