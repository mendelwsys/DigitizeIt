/**
 * 
 */
package ru.ts.gisutils.proj.transform;

import java.awt.geom.Point2D;

import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.TransformException;

/**
 * @author sygsky
 *
 */
public class MapTransformerBase implements IMapTransformerBase
{
	private MathTransform _transform;
	private String _srcWKT,_dstWKT;
	private static CoordinateOperationFactory _coFactory =
		ReferencingFactoryFinder.getCoordinateOperationFactory(null);

  /**
   * yg: need to get the transformer parameters from server delivering them to client
   * @return transformer parameters
   */
  public String[] getParams() {
    return new String[] {_srcWKT, _dstWKT };
  }

  MapTransformerBase(SingleCRS srcCRS, SingleCRS dstCRS) throws OperationNotFoundException, FactoryException
	{
		
		final CoordinateOperation co = _coFactory.createOperation(srcCRS, dstCRS);
		_transform = co.getMathTransform();
		_srcWKT = srcCRS.toWKT();
		_dstWKT = dstCRS.toWKT();
	}
	
  MapTransformerBase(CoordinateReferenceSystem sourceCRS,
			CoordinateReferenceSystem targetCRS) throws OperationNotFoundException, FactoryException 
	{
		_transform = CRS.findMathTransform(sourceCRS, targetCRS);
		_srcWKT = sourceCRS.toWKT();
		_dstWKT = targetCRS.toWKT();
	}
	
  MapTransformerBase(String sourceWKT,
			String targetWKT) throws OperationNotFoundException, FactoryException 
	{
		final CoordinateReferenceSystem sourceCRS = MapTransform._crsFactory.createFromWKT(sourceWKT);
		final CoordinateReferenceSystem targetCRS = MapTransform._crsFactory.createFromWKT(targetWKT);
		_transform = CRS.findMathTransform(sourceCRS, targetCRS);
		_srcWKT = sourceCRS.toWKT();
		_dstWKT = targetCRS.toWKT();
	}

	/**
	 * static method to create CRS from a WKT directly
	 * @param WKT Well Known Text defintion for a CoordinateReferenceSystem
	 * @return created CRS or throws exception
	 * @throws FactoryException
	 */
	public static CoordinateReferenceSystem getCRSFromWKT(String WKT) throws FactoryException
	{
		return MapTransform._crsFactory.createFromWKT(WKT);
	}
	/* (non-Javadoc)
     * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#TransformDirect(java.awt.geom.Point2D, java.awt.geom.Point2D)
     */
    /**
     * @deprecated Use {@link #TransformDirect(Point2D,Point2D)} instead
     */
//    public void TransformDirect(Point2D.Double pntSrc, Point2D.Double pntDst) throws TransformException
//    {
//        TransformDirect(pntSrc, pntDst);
//    }

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#TransformDirect(java.awt.geom.Point2D, java.awt.geom.Point2D)
	 */
	public void TransformDirect(Point2D pntSrc, Point2D pntDst) throws TransformException
	{
		final double[] arr = new double[] { pntSrc.getX(), pntSrc.getY(), 0.0, 0.0 };
		TransformDirect(arr, 0, arr, 2, 1);
		pntDst.setLocation(arr[2], arr[3]);
	}

	/* (non-Javadoc)
     * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#TransformInverse(java.awt.geom.Point2D, java.awt.geom.Point2D)
     */
    /**
     * @deprecated Use {@link #TransformInverse(Point2D,Point2D)} instead
     */
//    public void TransformInverse(Point2D.Double pntSrc, Point2D.Double pntDst) throws TransformException
//    {
//        TransformInverse(new Point2D(pntSrc.x,pntSrc.y), pntDst);
//    }

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#TransformInverse(java.awt.geom.Point2D, java.awt.geom.Point2D)
	 */
	public void TransformInverse(Point2D pntSrc, Point2D pntDst) throws TransformException
	{
		final double[] arr = new double[] { pntSrc.getX(), pntSrc.getY(), 0.0, 0.0 };
		TransformInverse(arr, 0, arr, 2, 1);
		pntDst.setLocation(arr[2], arr[3]);
	}
	
	/* (non-Javadoc)
	 * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#TransformDirect(double[] srcPnts, int srcOff, double[] dstPnts, int dstOff, int numPnts)
	 */
	public void TransformDirect(double[] srcPnts, int srcOff, double[] dstPnts, int dstOff, int numPnts) throws TransformException
	{
		_transform.transform(srcPnts, srcOff, dstPnts, dstOff, numPnts);
    }

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#TransformDirect(double[] pnts, int srcOff, int numPnts)
	 */
	public void TransformDirect(double[] pnts, int srcOff, int numPnts) throws TransformException
    {
		TransformDirect(pnts, srcOff, pnts, srcOff, numPnts );
    }

	
	/* (non-Javadoc)
	 * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#TransformInverse(double[] srcPnts, int srcOff, double[] dstPnts, int dstOff, int numPnts)
	 */
	public void TransformInverse(double[] srcPnts, int srcOff, double[] dstPnts, int dstOff, int numPnts)  throws TransformException
    {
		_transform.inverse().transform(srcPnts, srcOff, dstPnts, dstOff, numPnts);
    }

	public void TransformInverse(double[] pnts, int srcOff, int dstOff, int numPnts) throws TransformException
    {
		TransformInverse(pnts, srcOff, pnts, srcOff, numPnts );
    }
	
	/**
     * Returns a <cite>Well Known Text</cite> (WKT) for this object. Well know text are
     * <A HREF="../doc-files/WKT.html">defined in extended Backus Naur form</A>.
     * This operation may fails if an object is too complex for the WKT format capability.
     *
     * @return The <A HREF="../doc-files/WKT.html"><cite>Well Known Text</cite> (WKT)</A> for this object.
     * @throws UnsupportedOperationException If this object can't be formatted as WKT.
     */
	public String getWKT()
    {
		return _transform.toWKT();
    }

	private final String[] _sdir = new String[] {
			"NORTH", "EAST", "SOUTH", "WEST"
	};
	private final int[] _dir = new int[] {
			IMapTransformer.DIRECTION_NORTH, IMapTransformer.DIRECTION_EAST, 
			IMapTransformer.DIRECTION_SOUTH, IMapTransformer.DIRECTION_WEST
	};
	
	private int _parseDirection( String direction )
	{
		if ( direction != null )
			for( int i = 0; i < _dir.length; i++ )
				if ( direction.equalsIgnoreCase( _sdir[i] ) )
					return _dir[ i ];
		return IMapTransformer.DIRECTION_UNKNOWN;
	}
	/* (non-Javadoc)
     * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#getXDstDirection()
     */
    public int getXDstDirection()
    {
	    return _parseDirection( WKTHandler.getParameterValue( _dstWKT, "AXIS", "x" ) );
    }

	/* (non-Javadoc)
     * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#getYDstDirection()
     */
    public int getYDstDirection()
    {
	    return _parseDirection( WKTHandler.getParameterValue( _dstWKT, "AXIS", "y" ) );
    }
    
    public static void main( String[] args )
    {
    	try
    	{
	    	IMapTransformer trm = new MapTransformer(
	    			WKTHandler.getGeographicWKT(), 
	    			WKTHandler.getLambertAsimuthal_WKT( 60,100) );
	    	int dirx = trm.getXDstDirection();
	    	String dir;
	    	switch ( dirx )
            {
            case IMapTransformer.DIRECTION_NORTH:
	            dir = "NORTH";
	            break;
            case IMapTransformer.DIRECTION_EAST:
	            dir = "EAST";
	            break;
            case IMapTransformer.DIRECTION_SOUTH:
	            dir = "SOUTH";
	            break;
            case IMapTransformer.DIRECTION_WEST:
	            dir = "WEST";
	            break;

            default:
            	dir = "UNKNOWN";
	            break;
            }
	    	System.out.println( "AXIS x for dst = \"" + dir + "\"" ); 
	    	int diry = trm.getYDstDirection();
	    	switch ( diry )
            {
            case IMapTransformer.DIRECTION_NORTH:
	            dir = "NORTH";
	            break;
            case IMapTransformer.DIRECTION_EAST:
	            dir = "EAST";
	            break;
            case IMapTransformer.DIRECTION_SOUTH:
	            dir = "SOUTH";
	            break;
            case IMapTransformer.DIRECTION_WEST:
	            dir = "WEST";
	            break;

            default:
            	dir = "UNKNOWN";
	            break;
            }
	    	System.out.println( "AXIS y for dst = \"" + dir + "\"" ); 
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		return;
    	}
    }
	
}
