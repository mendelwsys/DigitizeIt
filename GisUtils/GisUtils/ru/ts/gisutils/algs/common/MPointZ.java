package ru.ts.gisutils.algs.common;

import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.ICoordinate;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * container with z coordinates
 */
public class MPointZ extends MPoint
{

	public MPoint getCopyOfObject()
	{
		return new MPointZ(this);
	}

	protected double z;

	public double getZ()
	{
		return z;
	}


	public void setZ( double val )
	{
		z=val;
	}

	public MPointZ()
	{
	}

	public MPointZ(IGetXY xy)
	{
		super(xy);
		z=0;
	}

	public MPointZ(ICoordinate crd)
	{
		super(crd);
		z=crd.getZ();
	}

	public MPointZ(double x, double y,double z)
	{
		super(x, y);
		this.z=z;
	}

	public MPointZ(MPointZ p)
	{
		super(p);
		z=p.getZ();
	}

	public MPointZ(MPoint p)
	{
		super(p);
	}

	public MPointZ(MPoint p,double z)
	{
		super(p);
		this.z=z;
	}

	public MPointZ(Point p)
	{
		super(p);
	}

	public MPointZ(Point.Double p)
	{
		super(p);
	}

	public int getDimensions()
	{
		return 3;
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
				return z;
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
				z=val;
				break;
			default:
				return false;
		}
		return true;
	}

}
