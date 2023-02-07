package ru.ts.toykernel.drawcomp.painters.def;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;


import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.*;
/**
 * Кульман для рисования кругов и эллипсов
 */
public class DefOvalPainter extends DefPointPainter
{
	public DefOvalPainter()
	{
	}

	public DefOvalPainter(Color colorFill, Color colorLine, Stroke stroke, int radPnt)
	{
		super(colorFill, colorLine, stroke, radPnt, null);
	}


	public int[] paint(Graphics graphics, IBaseGisObject drawMe, ILinearConverter converter, Point drawSize) throws Exception
	{
		setPainterParams(graphics, drawMe, converter, drawSize);

		double[][][] arrpoint = drawMe.getRawGeometry();

		//Все овалы задаются двумя точками

		MPoint cnvPnt=new MPoint();

		int[] rv=new int[]{0,0,0};
		for (int i = 0; i < arrpoint[0].length; i++) // arrpoint[0].length - колличество овалов
		{
			int[] x = new int[arrpoint[0][i].length];
			int[] y = new int[arrpoint[1][i].length];
			MPoint center = new MPoint();
			for (int j = 0; j < x.length; j++)
			{
				cnvPnt.x = arrpoint[0][i][j];
				cnvPnt.y = arrpoint[1][i][j];
				center.x+=cnvPnt.x;
				center.y+=cnvPnt.y;

				Point pi = converter.getDstPointByPoint(cnvPnt);

				x[j] = pi.x;
				y[j] = pi.y;
			} //Каждый овал задается четырмя точками, сделано для того что бы получить совместимость по MВВ, рисуем мы
			// овалы по нулевой и второй точке
			if (x.length>0)
			{
				center.x/=x.length;
				center.y/=y.length;
			}
			//graphics.setColor(paintFill);
			String typeoval = drawMe.getGeotype();
			Point cp = converter.getDstPointByPoint(center);
			AffineTransform transform=getTransform(drawMe, new MPoint(cp.x, cp.y));

			if (typeoval.equals(KernelConst.RECTF) || typeoval.equals(KernelConst.ELLIPSF))
			{
				setFillPainter(graphics,paintFill);
				drawOval(graphics, typeoval,x, y,true, transform);
				rv[2]++;
			}
			if (
					(!typeoval.equals(KernelConst.RECTF) && !typeoval.equals(KernelConst.ELLIPSF))
					||
							(
					(paintFill instanceof  Color)
					&&
					((Color)paintFill).getRGB() != colorLine.getRGB()
							)

				)
			{

				rv[1]++;
				graphics.setColor(colorLine);
				drawOval(graphics, typeoval,x, y,false, transform);
			}
		}
		return rv;
	}



	public Shape createShape(IBaseGisObject drawMe, ILinearConverter converter) throws Exception
	{
		Path2D p2d=new Path2D.Double();
		double[][][] arrpoint = drawMe.getRawGeometry();
		MPoint cnvPnt=new MPoint();
		for (int i = 0; i < arrpoint[0].length; i++) // arrpoint[0].length - колличество овалов
		{
			int[] x = new int[arrpoint[0][i].length];
			int[] y = new int[arrpoint[1][i].length];
			MPoint center = new MPoint();
			for (int j = 0; j < x.length; j++)
			{
				cnvPnt.x = arrpoint[0][i][j];
				cnvPnt.y = arrpoint[1][i][j];
				center.x+=cnvPnt.x;
				center.y+=cnvPnt.y;

				Point pi = converter.getDstPointByPoint(cnvPnt);

				x[j] = pi.x;
				y[j] = pi.y;
			} //Каждый овал задается четырмя точками, сделано для того что бы получить совместимость по MВВ, рисуем мы
			// овалы по нулевой и второй точке
			if (x.length>0)
			{
				center.x/=x.length;
				center.y/=y.length;
			}
			//graphics.setColor(paintFill);
			String typeoval = drawMe.getGeotype();
			Point cp = converter.getDstPointByPoint(center);
			AffineTransform transform=getTransform(drawMe, new MPoint(cp.x, cp.y));

			int w=x[2]-x[0];
			int h=y[2]-y[0];

			int xx=x[0];
			int yy=y[0];
			if (w<0)
			{
				w=-w;
				xx=xx-w;
			}

			if (h<0)
			{
				h=-h;
				yy=yy-h;
			}


			Shape lsh =null;
			if (
					typeoval.equals(KernelConst.ELLIPS) ||
					typeoval.equals(KernelConst.ELLIPSF)
				)
					lsh=new Ellipse2D.Double(xx,yy,w,h);
			else if (typeoval.equals(KernelConst.RECT) ||
					typeoval.equals(KernelConst.RECTF))
				lsh=new Rectangle2D.Double(xx,yy,w,h);
			else
				throw new UnsupportedOperationException("type \""+typeoval+"\" is not supported by DefOvalPainter");
			if (transform!=null)
				lsh=transform.createTransformedShape(lsh);
			p2d.append(lsh,false);
		}
		return p2d;
	}

	protected void drawOval(Graphics graphics, String typeoval, int[] x, int[] y, boolean fillit, AffineTransform transform)
	{
		AffineTransform oldtransform=null;
		try
		{
			if (x.length==0 || y.length==0)
			{
				System.out.println("Empty segment");
				return;
			}

			if (transform!=null)
			{
				oldtransform=((Graphics2D)graphics).getTransform();
				((Graphics2D)graphics).setTransform(transform);
			}

			if (graphics instanceof Graphics2D && stroke!=null)
				((Graphics2D) graphics).setStroke(stroke);

			int w=x[2]-x[0];
			int h=y[2]-y[0];

			int xx=x[0];
			int yy=y[0];
			if (w<0)
			{
				w=-w;
				xx=xx-w;
			}

			if (h<0)
			{
				h=-h;
				yy=yy-h;
			}

			if (
					typeoval.equals(KernelConst.ELLIPS) ||
					typeoval.equals(KernelConst.ELLIPSF)
				)
			{

				if (fillit)
					graphics.fillOval(xx, yy,w,h);
				else
					graphics.drawOval(xx, yy,w,h);

			}
			else if (typeoval.equals(KernelConst.RECT) ||
					typeoval.equals(KernelConst.RECTF))
			{
				if (fillit)
					graphics.fillRect(xx, yy,w,h);
				else
					graphics.drawRect(xx, yy,w,h);
			}
			else
				throw new UnsupportedOperationException("Can't use the Painter for object type:"+typeoval);
		}
		finally
		{
			if (oldtransform!=null)
				((Graphics2D)graphics).setTransform(oldtransform);
		}


	}

	public MRect getRect(Graphics graphics, IBaseGisObject obj, ILinearConverter converter) throws Exception
	{
		return obj.getMBB(null);
	}

	public MRect getDrawRect(Graphics graphics,IBaseGisObject obj, ILinearConverter converter)
	{
		return converter.getDstRectByRect(obj.getMBB(null));
	}


}
