package ru.ts.toykernel.filters;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;

/**
 * Default Maximal bounding box implementation
 */
public class DefMBBFilter extends BaseInitAble implements IMBBFilter
{
	private double l_x0;
	private double l_y0;
	private double l_w;
	private double l_h;

	public static final String TYPENAME ="F_MBB";//minimal boundig box filter
	
	public String getTypeName()
	{
		return TYPENAME;
	}

	DefMBBFilter()
	{
		l_w =-1;
		l_h =-1;
	}

	/**
	 * create MBB filter
	 * @param proj_rect - project rect of filter
	 */
	public DefMBBFilter(MRect proj_rect)
	{
		setRect(proj_rect);
	}

	public void setRect(MRect lrect)
	{
		this.l_x0 = lrect.p1.x;
		this.l_y0 = lrect.p1.y;
		this.l_w = lrect.getWidth();
		this.l_h = lrect.getHeight();
	}

	public MRect getRect()
	{
		return new MRect(new MPoint(l_x0,l_y0),new MPoint(l_x0+l_w,l_y0+l_h));
	}
	/* (non-Javadoc)
	 * @see ru.ts.toykernel.filters.IMBBFilter#acceptObject(MRect rect)
	 */
	public boolean acceptObject(MRect proj_rect)
	{
		return proj_rect.isIIntersect(l_x0, l_y0, l_w,l_h);
	}
	/* (non-Javadoc)
	 * @see ru.ts.toykernel.filters.IBaseFilter#acceptObject(IBaseGisObject obj)
	 */
	public boolean acceptObject(IBaseGisObject obj)
	{
		return acceptObject(obj.getMBB(null));
	}

	public Object init(Object obj) throws Exception
	{
		IDefAttr attr=(IDefAttr)obj;
		if (attr.getName().equalsIgnoreCase("X0"))
			l_x0= Double.parseDouble((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase("Y0"))
			l_y0= Double.parseDouble((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase("W"))
			l_w= Double.parseDouble((String) attr.getValue());
		else if (attr.getName().equalsIgnoreCase("H"))
			l_h= Double.parseDouble((String) attr.getValue());
		return null;
	}
}
