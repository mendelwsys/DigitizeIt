package ru.ts.gisutils.algs.common;

import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.ICoordinate;

import java.awt.*;


/**
 * MPointZM
 */
public class MPointZM  extends MPointZ implements IMCoordinate
{

	public MPoint getCopyOfObject()
	{
		return new MPointZM(this);
	}

	public boolean equals(Object obj)
	{
		return super.equals(obj) && (obj instanceof IMCoordinate) &&
				((IMCoordinate) obj).getM() == this.getM();
	}

	public MPointZM()
	{

	}

	public MPointZM(IGetXY xy)
	{
		super(xy);
	}

	public MPointZM(ICoordinate crd)
	{
		super(crd);
	}

	public MPointZM(IMCoordinate crd)
	{
		super(crd);
		this.m=crd.getM();
	}

	public MPointZM(double x, double y, double z,double m)
	{
		super(x, y, z);
		this.m=m;
	}


	public MPointZM(MPointZM p)
	{
		super(p);
		m=p.getM();
	}

	public MPointZM(MPointZ p)
	{
		super(p);
	}

	public MPointZM(MPoint p)
	{
		super(p);
	}

	public MPointZM(Point p)
	{
		super(p);
	}

	public MPointZM(Point.Double p)
	{
		super(p);
	}

	protected double m;
	public double getM()
	{
		return m;
	}

	public void setM(double val)
	{
		this.m=val;
	}

	public int getDimensions()
	{
		return 4;
	}

	public double getDimension(int index) throws IndexOutOfBoundsException
	{
		if (index==3)
			return m;
		else
			return super.getDimension(index);
	}

	public boolean setDimension(int index, double val)
	{
		if (index==3)
		{
			m=val;
			return false;
		}
		else
			return super.setDimension(index,val);
	}

}
