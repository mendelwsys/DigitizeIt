package su.gis.utils.svg;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Created by IntelliJ IDEA.
 * User: Syg
 * Date: 02.07.2012
 * Time: 18:35:52
 * Builds {@link java.awt.geom.AffineTransform} with reference point in TAB file
 * VMendelevich На базе ATBuilder
 */
public class ATBuilder2
{

    static public AffineTransform build(
            Point2D.Double minDegXY, Point2D.Double maxDegXY, int svgW, int svgH)
    {
        return build(minDegXY,maxDegXY,svgW,svgH,false);
    }

  static public AffineTransform build(
          Point2D.Double minDegXY, Point2D.Double maxDegXY, int svgW, int svgH,
          boolean doSubPixelShift
  )
  {
    AffineTransform at;

    Point2D dst = new Point2D.Double( );
    if (doSubPixelShift )
    {
      at = new AffineTransform();
      // first calculate 0.5 pixel value offset
      at.scale( ( maxDegXY.x - minDegXY.x ) / ( svgW - 1 ), ( minDegXY.y - maxDegXY.y ) / ( svgH - 1 ) );
      Point2D src = new Point2D.Double( 0.5, 0.5 );
      dst = new Point2D.Double();
      at.transform( src, dst );
    }
    // create main correct transformation
    at = new AffineTransform();
    // if sub-pixel shift needed, it is important to set translate BEFORE scaling
    at.translate( minDegXY.x + dst.getX(), maxDegXY.y + dst.getY());
    at.scale( (maxDegXY.x - minDegXY.x)/ svgW, (minDegXY.y - maxDegXY.y)/ svgH );
    return at;
  }
}
