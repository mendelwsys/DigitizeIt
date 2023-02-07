package ru.ts.gisutils.algs.common;

import ru.ts.gisutils.geometry.ICoordinate;
import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.IXY;

import java.io.*;
import java.awt.*;

import org.apache.xerces.utils.Base64;

/**
 * Point of object
 */
public class MPoint implements ICoordinate, IGetXY
{
	public double x;
	public double y;


	public MPoint()
	{
	}

	//++yg
	public MPoint(IGetXY xy)
	{
		this.x = xy.getX();
		this.y = xy.getY();
	}

	//--yg
	public MPoint(ICoordinate crd)
	{
		this.x = crd.getX();
		this.y = crd.getY();
	}

	public MPoint(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public MPoint(MPoint p)
	{
		this.x = p.x;
		this.y = p.y;
	}

	public MPoint(Point p)
	{
		this.x = p.x;
		this.y = p.y;
	}

	public MPoint(Point.Double p)
	{
		this.x = p.x;
		this.y = p.y;
	}

	//--yg: rounding

	public int hashCode()
	{
		return toString().hashCode();
	}

	public boolean equals(Object obj)
	{
		return (obj instanceof ICoordinate) && ((ICoordinate) obj).getX() == this.getX()
				&& ((ICoordinate) obj).getY() == this.getY() && ((ICoordinate) obj).getZ()==this.getZ();
	}

	public String toString()
	{
		return "X " + x + " Y " + y;
	}

	public byte[] getBase64Point() throws IOException
	{
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		this.savetoStream(new DataOutputStream(bas));
		return Base64.encode(bas.toByteArray());
	}

	static public MPoint getByBase64Point(byte[] bbase64) throws IOException
	{
		ByteArrayInputStream bas = new ByteArrayInputStream(Base64.decode(bbase64));
		return loadFromStream(new DataInputStream(bas));
	}

	public void savetoStream(DataOutputStream dos) throws IOException
	{
		dos.writeDouble(x);
		dos.writeDouble(y);
	}

	public static MPoint loadFromStream(DataInputStream dis) throws IOException
	{
		return new MPoint(dis.readDouble(), dis.readDouble());
	}

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void copyTo(IXY xy)
	{
		xy.setX(x);
		xy.setY(y);
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public void setXY(MPoint p)
	{
		this.x = p.x;
		this.y = p.y;
	}

	public void setXY(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public MPoint getCopyOfObject()
	{
		return new MPoint(this);
	}

	public double getZ()
	{
		return 0;
	}

	public void setZ(double val)
	{

	}//++yg: rounding

	public void round()
	{
		x = Math.round(x);
		y = Math.round(y);
	}

	public void ceil()
	{
		x = Math.ceil(x);
		y = Math.ceil(y);
	}

	public void floor()
	{
		x = Math.floor(x);
		y = Math.floor(y);
	}

	public void setByICoordinate(ICoordinate crd)
	{
		this.x = crd.getX();
		this.y = crd.getY();
	}

	public int getDimensions()
	{
		return 2;
	}

	public double getDimension(int index) throws IndexOutOfBoundsException
	{
		switch (index)
		{
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return 0.0;
			default:
				return Double.NaN;
		}
	}

	public boolean setDimension(int index, double val)
	{
		switch (index)
		{
			case 0:
				x = val;
				break;
			case 1:
				y = val;
				break;
			case 2:
				break;
			default:
				return false;
		}
		return true;
	}
}
