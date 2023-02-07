package ru.ts.toykernel.converters;

import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.awt.*;
import java.awt.geom.Point2D;

import org.apache.xerces.utils.Base64;

/**
 * Базовая реализация конвертера
 */
public abstract class BBaseConverter implements ILinearConverter
{

	protected List<ILinearConverter> converterchain=new LinkedList<ILinearConverter>();

	public boolean equals(Object obj)
	{
		try
		{
			if (obj instanceof ILinearConverter)
			{
				List<ILinearConverter> fconvchain = ((ILinearConverter) obj).getConverterChain();
				List<ILinearConverter> thisconvchain = this.getConverterChain();
				if (fconvchain.size()!=thisconvchain.size())
					return false;
				for (int i = 0; i < thisconvchain.size(); i++)
					if (!thisconvchain.get(i).equals(fconvchain.get(i)))
						return false;
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public double[] getDstSzBySz(MRect rect)
	{
		Point.Double pt1=getDstPointByPointD(rect.p1);
		Point.Double pt4=getDstPointByPointD(rect.p4);

		pt1.setLocation(Math.min(pt1.x,pt4.x),Math.min(pt1.y,pt4.y));
		pt4.setLocation(Math.max(pt4.x,pt1.x),Math.max(pt4.y,pt1.y));



		Point2D.Double pt=getDstPointByPointD(new MPoint(rect.p4.x,rect.p1.y));
		pt1.setLocation(Math.min(pt1.x,pt.x),Math.min(pt1.y,pt.y));
		pt4.setLocation(Math.max(pt4.x,pt.x),Math.max(pt4.y,pt.y));

		pt=getDstPointByPointD(new MPoint(rect.p1.x,rect.p4.y));
		pt1.setLocation(Math.min(pt1.x,pt.x),Math.min(pt1.y,pt.y)); 
		pt4.setLocation(Math.max(pt4.x,pt.x),Math.max(pt4.y,pt.y));


		return (new double[]{Math.abs(pt1.x-pt4.x),Math.abs(pt1.y-pt4.y)});
	}

	public  void setConverterChain(List<ILinearConverter> converterchain)
	{
		if (converterchain==null)
			this.converterchain.clear();
		else
			this.converterchain=converterchain;
	}

	public List<ILinearConverter> getConverterChain()
	{
		return converterchain;
	}

	public MPoint getPointByDstPoint(MPoint pnt)
	{
		return getPointByDstPoint(new Point.Double(pnt.x, pnt.y));
	}

	public MRect getRectByDstRect(MRect drawrect,
								   MRect wholeRect)
	{
		MPoint pmin=null;
		MPoint pmax=null;

		MPoint[] pnts = {getPointByDstPoint(drawrect.p1), getPointByDstPoint(drawrect.p4),
				getPointByDstPoint(new MPoint(drawrect.p1.x, drawrect.p4.y)),
				getPointByDstPoint(new MPoint(drawrect.p4.x, drawrect.p1.y))};

		if (wholeRect!=null)
		{
			pmin = new MPoint(wholeRect.p4);
			pmax = new MPoint(wholeRect.p1);
		}
		else
		{
			pmin = new MPoint(pnts[0]);
			pmax = new MPoint(pnts[0]);
		}

		for (MPoint pnt : pnts)
		{
			if (pnt.x < pmin.x)
				pmin.x = pnt.x;
			if (pnt.y < pmin.y)
				pmin.y = pnt.y;
			if (pnt.x > pmax.x)
				pmax.x = pnt.x;
			if (pnt.y > pmax.y)
				pmax.y = pnt.y;
		}

		if (wholeRect!=null)
		{
			pmin.x = Math.max(wholeRect.p1.x, pmin.x);
			pmin.y = Math.max(wholeRect.p1.y, pmin.y);

			pmax.x = Math.min(wholeRect.p4.x, pmax.x);
			pmax.y = Math.min(wholeRect.p4.y, pmax.y);
		}

		return new MRect(new MPoint(pmin.x, pmin.y),
				new MPoint(pmax.x, pmax.y));
	}

	public MPoint getPointByDstPoint(Point pnt)
	{
		return getPointByDstPoint(new Point.Double(pnt.x, pnt.y));
	}

	public MRect getDstRectByRect(MRect rect)
	{
		Point2D.Double p1 = getDstPointByPointD(rect.p1);
		Point2D.Double p4 = getDstPointByPointD(rect.p4);

		return new MRect(new MPoint(Math.min(p1.x, p4.x), Math.min(p1.y, p4.y)),
				new MPoint(Math.max(p1.x, p4.x), Math.max(p1.y, p4.y)));
	}

	public Point getDstPointByPoint(MPoint pt)
	{
		Point.Double rp = getDstPointByPointD(pt);
		return new Point((int) Math.round(rp.x), (int) Math.round(rp.y));
	}


	public byte[] getBase64Converter()
			throws Exception
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream ddos = new DataOutputStream(bos);

		saveToStream(ddos);

		ddos.flush();
		ddos.close();
		return Base64.encode(bos.toByteArray());
	}

	public void initByBase64Point(byte[] bbase64) throws Exception
	{
		   loadFromStream(new DataInputStream(new ByteArrayInputStream(Base64.decode(bbase64))));
	}

	public void translatedata(DataOutputStream dos) throws Exception
	{
		byte[] b = getBase64Converter();
		dos.write(b);
		dos.flush();
	}
}