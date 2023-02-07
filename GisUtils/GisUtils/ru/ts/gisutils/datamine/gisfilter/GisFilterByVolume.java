/**
 * Created on 27.03.2008 17:34:39 2008 by Syg
 * for project in 'ru.ts.gisutils.datamine' of 'test' 
 */
package ru.ts.gisutils.datamine.gisfilter;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import ru.ts.gisutils.geometry.GisVolume;
import ru.ts.gisutils.geometry.IGisVolumeGet;

/**
 * @author Syg
 */
public class GisFilterByVolume extends GisVolume implements IGisFilter
{

	/**
	 * 
	 */
	public GisFilterByVolume(IGisVolumeGet volume)
	{
		super( volume );
	}

	/**
	 * creates new class instance from a volume
	 * @param volume volume of filter
	 * @return new class instance
	 */
	static public IGisFilter getNewGisFilter( IGisVolumeGet volume )
	{
		return new GisFilterByVolume( volume ) ;
	}
	
	/**
	 * creates new class instance from a rectangle  (2D case)
	 * @param rect {@link java.awt.geom.Rectangle2D} rectangle of a filter
	 * @return new class instance
	 */
	static public IGisFilter getNewGisFilter( Rectangle2D rect )
	{
		return new GisFilterByVolume( new GisVolume( rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight() ) );
	}

	/**
	 * creates new class instance from a rectangle parameters (2D case)
	 * @param x origin of rectangle of a filter
	 * @param y origin of rectangle of a filter
	 * @param width of a rectangle of a filter
	 * @param height of a rectangle of a filter
	 * @return new class instance
	 */
	static public IGisFilter getNewGisFilter( double x, double y, double width, double height )
	{
		return new GisFilterByVolume( new GisVolume( x, y, width, height ) );
	}
	
	/* (non-Javadoc)
	 * @see ru.ts.gisutils.datamine.gisfilter.IGisFilter#canFilter(java.lang.Object)
	 */
	public boolean canFilter( Object objToCheck )
	{
		return objToCheck instanceof IFilterable;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.datamine.gisfilter.IGisFilter#getFilterClass()
	 */
	public Class getFilterClass()
	{
		return IGisVolumeGet.class;
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.datamine.gisfilter.IGisFilter#isGoodForFilter(ru.ts.gisutils.datamine.gisfilter.IFilterable)
	 */
	public boolean isGoodForFilter( IFilterable object )
	{
		return object.isGoodForFilter( this );
	}

	/* (non-Javadoc)
     * @see ru.ts.gisutils.datamine.gisfilter.IGisFilter#filterCollection(java.util.List)
     */
    public List filterCollection( List collection )
    {
    	Class cls = collection.getClass();
    	List list = new ArrayList();
    	for ( int i = 0; i < list.size(); i++ )
        {
	        IFilterable obj = (IFilterable)collection.get( i );
	        if ( obj.isGoodForFilter( this ) )
	        	list.add( obj );
        }
    	return list;
    }

}
