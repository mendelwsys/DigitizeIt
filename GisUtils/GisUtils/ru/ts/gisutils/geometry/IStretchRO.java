/**
 * Created on 03.04.2008 17:42:19 2008 by Syg
 * for project in 'ru.ts.gisutils.geometry' of 'test' 
 */
package ru.ts.gisutils.geometry;

/**
 * ReadOnly interface to keep the space in one dimension. 
 * E.g. in X direction only, or in Y etc. 
 * Any stretch has 2 points, left and right. 
 * The positional relationship always is determined by minimum of stretch point.
 *  
 * if 1st strecth has its min point smaller than 2nd stretch one, it is named <b>'smaller stretch'</b>.
 * And vice versa, if 1st stretch min point is bigger then 2nd one, it is named <b>bigger one</b>.
 * @author Syg
 */
public interface IStretchRO
{
	/**
	 * min extension for the stretch
	 * @return value of minimum for the stretch
	 */
	double getMin();

	/**
	 * max extension for the stretch
	 * @return value of maximum for the stretch
	 */
	double getMax();
	
	/**
	 * checks if this stretch intersects with other one
	 * @param stretch to check on intersection with a current one
	 * @return <code>true</code> if stretches intersect
	 */
	boolean intersects( IStretchRO stretch );
	
	/**
	 * checks if designated stretch object in totaly laying in our one
	 * @param stretch to check being totally interior to our one
	 * @return <code>true</code> if designated stretch lays in our one
	 */
	boolean contains( IStretchRO stretch );
	
	/**
	 * compares this <b>stretch</b> min to the designated ont
	 * @param stretch to check their position according to our min point. 
	 * So you can use this method to sort stretches by their min points
	 * @return -1 if out stretch is smaller, 0 if is equal, +1 if is bigger
	 */
	 int compareByMin(IStretchRO stretch);

	/**
	 * compares this <b>stretch</b> max to the designated ont
	 * @param stretch to check their position according to our min point. 
	 * So you can use this method to sort stretches by their min points
	 * @return -1 if out stretch is smaller, 0 if is equal, +1 if is bigger
	 */
	 int compareByMax(IStretchRO stretch);
}
