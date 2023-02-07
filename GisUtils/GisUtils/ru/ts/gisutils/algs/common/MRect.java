package ru.ts.gisutils.algs.common;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

/**
 * Представление прямоугольника
 */
public class MRect
{


	public MRect getMBB(MRect boundrect)
	{

		if (boundrect == null)
			return new MRect(this);
		else
		{
			if (p1.x < boundrect.p1.x)
				boundrect.p1.x = p1.x;
			if (p4.x > boundrect.p4.x)
				boundrect.p4.x = p4.x;

			if (p1.y < boundrect.p1.y)
				boundrect.p1.y = p1.y;
			if (p4.y > boundrect.p4.y)
				boundrect.p4.y = p4.y;
		}
		boundrect.resetInternalPoint();
		return boundrect;
	}


	public double active = 0;
	public MPoint p1;
	public MPoint p4;
	private MPoint p2;
	private MPoint p3;


	public double getMidlX()
	{
		return (p1.x + p4.x) / 2;
	}

	public String toString()
	{
		return new StringBuffer().append("p1 ").append(p1).append("\n").append(" p2 ").append(p2).append(
				"\n").append(" p3 ").append(p3).append("\n").append(" p4 ").append(p4).toString();
	}

	public MPoint getMidle()
	{
		return new MPoint(getMidlX(),getMidlY());
	}
	
	public double getMidlY()
	{
		return (p1.y + p4.y) / 2;
	}

	public MRect()
	{
		p1=new MPoint();
		p2=new MPoint();
		p3=new MPoint();
		p4=new MPoint();
	}

	public MRect(Rectangle rec)
	{
		this(new MPoint(rec.getX(),rec.getY()), new MPoint(rec.getX()+rec.getWidth(),rec.getY()+rec.getHeight()));
	}

	public MRect(Rectangle2D rec)
	{
		this(new MPoint(rec.getX(),rec.getY()), new MPoint(rec.getX()+rec.getWidth(),rec.getY()+rec.getHeight()));
	}

	public MRect(MRect rec)
	{
		this(new MPoint(rec.p1), new MPoint(rec.p4));
		this.active = rec.active;
	}

	public void setRect(MRect rec)
	{
		p1.setXY(rec.p1);
		p4.setXY(rec.p4);
		this.active = rec.active;
		resetInternalPoint();
	}

	public MRect(MPoint p1, MPoint p4)
	{
		this.p1 = p1;
		this.p4 = p4;
		p2 = new MPoint(p1.x, p4.y);
		p3 = new MPoint(p4.x, p1.y);
	}

	public MRect(Point p1, Point p4)
	{
		this.p1 = new MPoint(p1.x, p1.y);
		this.p4 = new MPoint(p4.x, p4.y);
		p2 = new MPoint(p1.x, p4.y);
		p3 = new MPoint(p4.x, p1.y);
	}

	public MRect(Point.Double p1, Point.Double p4)
	{
		this.p1 = new MPoint(p1.x, p1.y);
		this.p4 = new MPoint(p4.x, p4.y);
		p2 = new MPoint(p1.x, p4.y);
		p3 = new MPoint(p4.x, p1.y);
	}

	//++yg
	// rounding of bounds to the nearest long values
	public void round() {
	  p1.round();
	  p2.round();
	  p3.round();
	  p4.round();
	}
	// rounding up of bounds to the nearest long values
	public void roundUp() {
	  p1.floor();
	  p2.x = Math.floor(p2.x);
	  p2.y = Math.ceil (p2.y);
	  p3.x = Math.ceil (p3.x);
	  p3.y = Math.floor(p3.y);
	  p4.ceil();
	}
	//--yg: rounding

	public void resetInternalPoint()
	{
		p2.x = p1.x;
		p2.y = p4.y;
		p3.x = p4.x;
		p3.y = p1.y;
	}

	public void setP1X(double X)
	{
		double dx = X - p1.x;
		scroolX(dx);
	}

	public void setP1Y(double Y)
	{
		double dy = Y - p1.y;
		scroolY(dy);
	}

	public void scroolX(double dX)
	{
		p1.x += dX;
		p4.x += dX;
		p2.x += dX;
		p3.x += dX;
	}

	public void scroolY(double dY)
	{
		p1.y += dY;
		p4.y += dY;
		p2.y += dY;
		p3.y += dY;
	}

	public boolean isinY(double coord)
	{
		return ((p1.y < coord) && (coord < p4.y)) || ((p1.y > coord) && (coord > p4.y)); //TODO 3
	}

	public boolean isinX(double coord)
	{
		return ((p1.x < coord) && (coord < p4.x)) || ((p1.x > coord) && (coord > p4.x));  //TODO 4
	}

	public boolean isinYI(double coord)
	{
		return ((p1.y <= coord) && (coord <= p4.y)) || ((coord <= p1.y) && (p4.y <= coord));
	}

	public boolean isinXI(double coord)
	{
		return ((p1.x <= coord) && (coord <= p4.x)) || ((coord <= p1.x) && (p4.x <= coord));
	}


	public boolean isInRect(double x, double y)
	{
		return isinX(x) && isinY(y);
	}

	public boolean isInRectI(double x, double y)
	{
		return isinXI(x) && isinYI(y);
	}


	public boolean isInRect(MPoint p)
	{
		return isinX(p.x) && isinY(p.y);
	}


	public boolean isInRectI(MPoint p)
	{
		return isinXI(p.x) && isinYI(p.y);
	}


	public boolean isIntersect(MPoint ps1, MPoint ps2)
	{
		return (isinX(ps1.x) && isinY(ps1.y)) || (isinX(ps2.x) && isinY(ps2.y)) ||
				GeomAlgs.isIntersect_с(p1, p4,
						ps1, ps2) ||
				GeomAlgs.isIntersect_с(p2, p3,
						ps1, ps2);

//					GeomAlgs.isIntersect_с(p1, p2,
//							ps1, ps2) ||
//							GeomAlgs.isIntersect_с(p3, p4,
//									ps1, ps2) ||
//							GeomAlgs.isIntersect_с(p1, p3,
//									ps1, ps2) ||
//							GeomAlgs.isIntersect_с(p2, p4,
//									ps1, ps2);
	}


	public boolean isIIntersect(MPoint ps1, MPoint ps2)
	{
		return (isinX(ps1.x) && isinY(ps1.y)) || (isinX(ps2.x) && isinY(ps2.y)) ||
				GeomAlgs.isIIntersect_с(p1, p4,
						ps1, ps2) ||
				GeomAlgs.isIIntersect_с(p2, p3,
						ps1, ps2);
	}

	public boolean  isIIntersect(MPoint[] pss)
	{
		for (int i = 0; i < pss.length-1; i++)
			if (isIIntersect(pss[i],pss[i+1]))
				return true;
		return false;
	}

	public boolean  isIIntersect(double[] psX,double[] psY)
	{
		for (int i = 0; i < psX.length-1; i++)
			if (isIIntersect(new MPoint(psX[i],psY[i]),new MPoint(psX[i+1],psY[i+1])))
				return true;
		return false;
	}

	public boolean isIntersect(MRect rect)
	{
		return rect.isInRect(p1) || rect.isInRect(p2) || rect.isInRect(p3) || rect.isInRect(p4) || isInRect(
				rect.p1) || isInRect(rect.p2) || isInRect(rect.p3) || isInRect(rect.p4) || rect.isIntersect(
				p1, p2) || rect.isIntersect(p2, p3) || rect.isIntersect(p3, p4) || rect.isIntersect(
				p4, p1);

	}


	public boolean isIIntersect(double x, double y, double w, double h)
	{
		if (w < 0.0 || h < 0.0 )
			return false;
		double x0 = p1.x;
		if ( x > ( x0 + getWidth() ) )
			return false;
		double y0 = p1.y;
		if ( y > ( y0 + getHeight() ) )
			return false;
		if ( ( x + w ) < x0 )
			return false;
		if ( ( y + h ) < y0 )
			return false;
		return true;
	}

	public boolean isIIntersect(MRect rect)
	{
		return isIIntersect(rect.p1.x,rect.p1.y,rect.getWidth(),rect.getHeight());
//		return
//				rect.isInRect(p1) || rect.isInRect(p2) || rect.isInRect(p3) || rect.isInRect(p4) ||
//				isInRect(rect.p1) || isInRect(rect.p2) || isInRect(rect.p3) || isInRect(
//				rect.p4) || rect.isIIntersect(p1, p2) || rect.isIIntersect(p2, p3) || rect.isIIntersect(p3,
//				p4) || rect.isIIntersect(p4, p1);
	}

	public void saveToStream(DataOutputStream dos) throws IOException
	{
		p1.savetoStream(dos);
		p4.savetoStream(dos);
	}

	static public MRect loadFromStream(DataInputStream dis) throws IOException
	{
		return new MRect(MPoint.loadFromStream(dis), MPoint.loadFromStream(dis));
	}

	public double getWidth()
	{
		return p4.x - p1.x;
	}

	public double getHeight()
	{
		return p4.y - p1.y;
	}

	public boolean equals(Object o)
	{
		if (o instanceof MRect)
		{
			MRect rec = (MRect) o;
			if ((this.p1.x == rec.p1.x) && (this.p1.y == rec.p1.y) && (this.p4.x == rec.p4.x) && (this.p4.y == rec.p4.y))
				return true;
		}
		return super.equals(o);
	}

	public int hashCode()
	{
		return (new StringBuffer().append("X1").append(p1.x).append("Y1").append(p1.y).append("X4").append(
				p4.x).append("Y4").append(p4.y).toString()).hashCode();
	}
}
