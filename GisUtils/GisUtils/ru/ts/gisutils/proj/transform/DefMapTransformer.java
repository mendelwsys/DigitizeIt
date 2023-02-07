package ru.ts.gisutils.proj.transform;

import org.opengis.referencing.operation.TransformException;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;

import ru.ts.utils.data.Pair;
import ru.ts.gisutils.datamine.ProjBaseConstatnts;
import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.IXY;

import javax.units.Converter;

/**
 * One to one stub
 */
public class DefMapTransformer implements IMapTransformer
{
	public String getTransformerFactoryType()
	{
		return TrasformerFactory.USERRANFORMER;
	}

	public String[] getParams()
	{
		return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void TransformDirect(IGetXY pntSrc, IXY pntDst) throws TransformException
	{
		pntDst.setXY(pntSrc.getX(),pntSrc.getY());
	}

	public void TransformDirect(double[] srcPnts, int srcOff, double[] dstPnts, int dstOfft,
								int numPnt) throws TransformException
	{
		System.arraycopy(srcPnts, 0, dstPnts, 0, srcPnts.length);
	}

	public void TransformDirect(double[] pnts, int srcOff, int dsrOff, int numPnts) throws TransformException
	{
	}

	public void TransformDirect(double[] pnts, int srcOff, int numPnts) throws TransformException
	{
	}

	public void TransformInverse(IGetXY pntSrc, IXY pntDst) throws TransformException
	{
		pntDst.setXY(pntSrc.getX(),pntSrc.getY());
	}

	public void TransformInverse(double[] srcPnts, int srcOff, double[] dstPnts, int dstOff,
								 int numPnt) throws TransformException
	{
		System.arraycopy(srcPnts, 0, dstPnts, 0, srcPnts.length);
	}

	public void TransformInverse(double[] pnts, int srcOff, int dstOff, int numPnts) throws TransformException
	{
	}

	public String getWKT()
	{
		return "";  //To change body of implemented methods use File | Settings | File Templates.
	}

	public Pair<String,String> getTransformerType()
	{
		return new Pair<String,String>(ProjBaseConstatnts.USER, ProjBaseConstatnts.USER);
	}

	public int getXDstDirection()
	{

		return IMapTransformer.DIRECTION_NORTH;
	}

	public int getYDstDirection()
	{

		return IMapTransformer.DIRECTION_EAST;
	}

	public void saveTransformer(DataOutputStream dos) throws Exception
	{
		dos.writeUTF(TrasformerFactory.USERRANFORMER);
	}

	public IMapTransformer loadTransformer(DataInputStream dis) throws Exception
	{
		return this;
	}

	public IMapTransformer loadTranformerByTextFile(BufferedReader isr, String charsetName) throws Exception
	{
		return this;
	}

	public String getSrcWKT()
	{
		return "";
	}

	public String getDstWKT()
	{
		return "";
	}

	public Converter getUnitConverter(String unitname, boolean fromsrc)
	{
		return null;
	}
}
