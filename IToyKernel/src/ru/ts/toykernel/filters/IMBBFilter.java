package ru.ts.toykernel.filters;

import ru.ts.gisutils.algs.common.MRect;

/**
 * Spatial Filter by maximal bounding box
 */
public interface IMBBFilter extends IBaseFilter
{
	/**
	 * accept object with given MBB
	 * @param proj_rect - project coordinates rect
	 * @return - true if accept false otherwise
	 */
	boolean acceptObject(MRect proj_rect);

	/**
	 * @return get project rect of filter
	 */
	MRect getRect();
}
