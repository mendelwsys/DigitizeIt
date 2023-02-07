/**
 * Created 22.07.2008 16:05:15 by Syg for the "GisUtils" project
 */
package ru.ts.gisutils.common;

/**
 * Interface works to visit each station info item in the collection. To visit
 * other info file, call
 * 
 * {@see ru.ts.train.statengine.IStatEngine#select_set(java.util.Date,
 *      java.util.Date, ru.ts.gisutils.common.CSV.ICSVVisitor, int)}. Its
 *      functionality is near to this visitor but you are visiting all info masked by int parameter.
 * 
 * @author Syg
 * 
 */
public interface IItemVisitor
{
	/**
	 * Method called for each item in iStatSet collection
	 * 
	 * @param item
	 *            item of an IStatSet collection existing. You know that this
	 *            should be an IStatItem instance You can do anything with it -
	 *            change, count etc.
	 */
	void visitItem( Object item );
}
