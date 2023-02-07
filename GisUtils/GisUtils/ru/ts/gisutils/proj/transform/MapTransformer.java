/**
 *
 */
package ru.ts.gisutils.proj.transform;

import java.io.*;

import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.operation.*;
import ru.ts.utils.data.Pair;
import ru.ts.gisutils.datamine.ProjBaseConstatnts;
import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.IXY;

import javax.units.*;

/**
 * @author sygsky
 */
public class MapTransformer implements IMapTransformer
{
	private CoordinateOperation co;
	private MathTransform _transform;

	static
	{
		UnitFormat.alias(Unit.valueOf("°"),ProjBaseConstatnts.DEGREE);
		UnitFormat.label(Unit.valueOf("°"),ProjBaseConstatnts.DEGREE);

		UnitFormat.alias(Unit.valueOf("rad"),ProjBaseConstatnts.GEORADIANS);
		UnitFormat.label(Unit.valueOf("rad"),ProjBaseConstatnts.GEORADIANS);

		UnitFormat.alias(Unit.valueOf("m"),ProjBaseConstatnts.METERS);
		UnitFormat.label(Unit.valueOf("m"),ProjBaseConstatnts.METERS);
	}

	public String getSrcWKT()
	{
		return _srcWKT;
	}

	public String getDstWKT()
	{
		return _dstWKT;
	}

	protected String _srcWKT, _dstWKT;
	private static CoordinateOperationFactory _coFactory =
			ReferencingFactoryFinder.getCoordinateOperationFactory(null);

	public String getTransformerFactoryType()
	{
		return TrasformerFactory.GEOTRANFORMER;
	}

	/**
	 * yg: need to get the transformer parameters from server delivering them to client
	 *
	 * @return transformer parameters
	 */
	public String[] getParams()
	{
		return new String[]{_srcWKT, _dstWKT};
	}

	public MapTransformer()//get empty tranformer
	{

	}

	MapTransformer(SingleCRS srcCRS, SingleCRS dstCRS) throws OperationNotFoundException, FactoryException
	{
		initTransformer(srcCRS, dstCRS);
	}

	MapTransformer(CoordinateReferenceSystem sourceCRS,
				   CoordinateReferenceSystem targetCRS) throws OperationNotFoundException, FactoryException
	{
		initTransformer(sourceCRS, targetCRS);
	}

	public MapTransformer(String sourceWKT,
				   String targetWKT) throws OperationNotFoundException, FactoryException
	{
		initTransformer(sourceWKT, targetWKT);
	}

	public void initTransformer(String sourceWKT, String targetWKT)
			throws FactoryException
	{
		final CoordinateReferenceSystem sourceCRS = MapTransform._crsFactory.createFromWKT(sourceWKT);
		final CoordinateReferenceSystem targetCRS = MapTransform._crsFactory.createFromWKT(targetWKT);
		initTransformer(sourceCRS, targetCRS);
	}


	protected void initTransformer(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS)
			throws FactoryException
	{

//		CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
		//String s = crs.toWKT();
		//System.out.println(s);


		 co = _coFactory.createOperation(sourceCRS, targetCRS);
		_transform = co.getMathTransform();
		_srcWKT = sourceCRS.toWKT();
		_dstWKT = targetCRS.toWKT();
	}

	/**
	 * static method to create CRS from a WKT directly
	 *
	 * @param WKT Well Known Text definition for a CoordinateReferenceSystem
	 * @return created CRS or throws exception
	 * @throws FactoryException -
	 */
	public static CoordinateReferenceSystem getCRSFromWKT(String WKT) throws FactoryException
	{
		return MapTransform._crsFactory.createFromWKT(WKT);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#TransformDirect(java.awt.geom.Point2D, java.awt.geom.Point2D)
	 */
	public void TransformDirect(IGetXY pntSrc, IXY pntDst) throws TransformException
	{
		final double[] arr = new double[]{pntSrc.getX(), pntSrc.getY(), 0.0, 0.0};
		TransformDirect(arr, 0, arr, 2, 1);
		pntDst.setXY(arr[2], arr[3]);
	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#TransformInverse(java.awt.geom.Point2D, java.awt.geom.Point2D)
	 */
	public void TransformInverse(IGetXY pntSrc, IXY pntDst) throws TransformException
	{
		final double[] arr = new double[]{pntSrc.getX(), pntSrc.getY(), 0.0, 0.0};
		TransformInverse(arr, 0, arr, 2, 1);
		pntDst.setXY(arr[2], arr[3]);
	}

	/* (non-Javadoc)
		 * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#TransformDirect(double[] srcPnts, int srcOff, double[] dstPnts, int dstOff, int numPnts)
		 */
	public void TransformDirect(double[] srcPnts, int srcOff, double[] dstPnts, int dstOff, int numPnts) throws TransformException
	{
		_transform.transform(srcPnts, srcOff, dstPnts, dstOff, numPnts);
	}

	public void TransformDirect(double[] pnts, int srcOff, int dsrOff, int numPnts) throws TransformException
	{
		double[] in=new double[pnts.length];
		System.arraycopy(pnts,0,in,0,pnts.length);
		TransformDirect(in,srcOff,pnts,dsrOff,numPnts);

	}

	/* (non-Javadoc)
	 * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#TransformDirect(double[] pnts, int srcOff, int numPnts)
	 */
	public void TransformDirect(double[] pnts, int srcOff, int numPnts) throws TransformException
	{
		TransformDirect(pnts, srcOff, pnts, srcOff, numPnts);
	}


	/* (non-Javadoc)
		 * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#TransformInverse(double[] srcPnts, int srcOff, double[] dstPnts, int dstOff, int numPnts)
		 */
	public void TransformInverse(double[] srcPnts, int srcOff, double[] dstPnts, int dstOff, int numPnts) throws TransformException
	{
		_transform.inverse().transform(srcPnts, srcOff, dstPnts, dstOff, numPnts);
	}

	public void TransformInverse(double[] pnts, int srcOff, int dstOff, int numPnts) throws TransformException
	{
		TransformInverse(pnts, srcOff, pnts, srcOff, numPnts);
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

//		Unit unit = Unit.valueOf("m");
//		Converter res = null;
//		try
//		{
//			res = systemAxis.getUnit().getConverterTo(unit);
//		}
//		catch (ConversionException e)
//		{
//			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//		}
//
//		double rc = res.convert(1);
//		double rc1 = res.inverse().convert(1);


	public Converter getUnitConverter(String unitname,boolean fromsrc)
	{
		try
		{
			Unit unit = Unit.valueOf(unitname);
			if (fromsrc)
				return co.getSourceCRS().getCoordinateSystem().getAxis(0).getUnit().getConverterTo(unit);
			else
				return co.getTargetCRS().getCoordinateSystem().getAxis(0).getUnit().getConverterTo(unit);
		}
		catch (Exception e)
		{//

		}
		return null;
	}

	public Pair<String, String> getTransformerType()
	{

		CoordinateSystemAxis sAxis = co.getSourceCRS().getCoordinateSystem().getAxis(0);
		CoordinateSystemAxis tAxis = co.getTargetCRS().getCoordinateSystem().getAxis(0);

		Unit srcUnit = sAxis.getUnit();
		Unit tgtUnit=tAxis.getUnit();

		UnitFormat instance = UnitFormat.getInstance();
		return new Pair<String, String>(instance.labelFor(srcUnit), instance.labelFor(tgtUnit));
	}

	private final String[] _sdir = new String[]{
			"NORTH", "EAST", "SOUTH", "WEST"
	};
	private final int[] _dir = new int[]{
			IMapTransformer.DIRECTION_NORTH, IMapTransformer.DIRECTION_EAST,
			IMapTransformer.DIRECTION_SOUTH, IMapTransformer.DIRECTION_WEST
	};

	private int _parseDirection(String direction)
	{
		if (direction != null)
			for (int i = 0; i < _dir.length; i++)
				if (direction.equalsIgnoreCase(_sdir[i]))
					return _dir[i];
		return IMapTransformer.DIRECTION_UNKNOWN;
	}

	/* (non-Javadoc)
		 * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#getXDstDirection()
		 */
	public int getXDstDirection()
	{
		return _parseDirection(WKTHandler.getParameterValue(_dstWKT, "AXIS", "x"));
	}

	/* (non-Javadoc)
     * @see ru.ts.gisutils.pcntxt.transform.IMapTransformer#getYDstDirection()
     */
	public int getYDstDirection()
	{
		return _parseDirection(WKTHandler.getParameterValue(_dstWKT, "AXIS", "y"));
	}

	public static void main(String[] args)
	{
		try
		{
			IMapTransformer trm = new MapTransformer(
					WKTHandler.getGeographicWKT(),
					WKTHandler.getLambertAsimuthal_WKT(60, 100));
			int dirx = trm.getXDstDirection();
			String dir;
			switch (dirx)
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
			System.out.println("AXIS x for dst = \"" + dir + "\"");
			int diry = trm.getYDstDirection();
			switch (diry)
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
			System.out.println("AXIS y for dst = \"" + dir + "\"");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void saveTransformer(DataOutputStream dos) throws Exception
	{
		dos.writeUTF(TrasformerFactory.GEOTRANFORMER);
		dos.writeUTF(_srcWKT + ";" + _dstWKT);
	}

	public IMapTransformer loadTransformer(DataInputStream dis) throws Exception
	{
		String WKT = dis.readUTF();
		String[] source = WKT.split(";");
		initTransformer(source[0], source[1]);
		return this;
	}

	public IMapTransformer loadTranformerByTextFile(BufferedReader isr, String charsetName) throws Exception
	{
		StringBuffer sourceWKT = new StringBuffer();
		String strrd = null;
		while ((strrd = isr.readLine()) != null && !strrd.equalsIgnoreCase(";"))
			sourceWKT.append(strrd);

		StringBuffer targetWKT = new StringBuffer();
		while ((strrd = isr.readLine()) != null && !strrd.equalsIgnoreCase(";"))
			targetWKT.append(strrd);

		initTransformer(sourceWKT.toString(), targetWKT.toString());
		return this;
	}

}
