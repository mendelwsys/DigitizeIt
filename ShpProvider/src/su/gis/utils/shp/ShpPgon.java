package su.gis.utils.shp;

import shp.core.ShpPoint;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Syg
 * Date: 27.06.2012
 * Time: 14:19:59
 * To change this template use File | Settings | File Templates.
 */
public class ShpPgon extends Vector<ShpPoint>
{
  /**
   * Checks if polygon is not closed and close by connecting last point with first if needed
   */
  public ShpPgon close()
  {
    // check if polygon already exists and not empty, so we should first dump its content
    if ( !isEmpty() )
    {
      // not empty, dump it to the polygon collection
      // check if first point not connected with a lst one
      if ( !firstElement().equals( lastElement() ) )
      {
        add( new ShpPoint( firstElement()) );
      }
    }
    return this;
  }
}
