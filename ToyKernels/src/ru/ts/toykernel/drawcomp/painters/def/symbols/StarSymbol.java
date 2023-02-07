package ru.ts.toykernel.drawcomp.painters.def.symbols;

import ru.ts.toykernel.drawcomp.painters.def.DefPointPainter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;

import java.awt.*;

/**
 * Star symbol driver (Base on Vasily classes)
 */
public class StarSymbol extends DefPointPainter
{
	public StarSymbol()
	{
	}

	public StarSymbol(Color colorFill, Color colorLine, Stroke stroke, int radPnt)
	{
		super(colorFill, colorLine, stroke, radPnt, null);
		calculateR();
	}


	private double R=-1;
	private void calculateR()
	{
		double  sin18=Math.sin(Math.PI/10);
		double  ctg72=1.0/Math.tan(2*Math.PI/5);
		double  backSin=1.0-sin18;
		double square=sin18*sin18+backSin*backSin*ctg72*ctg72;
		R=(int)(radPnt*Math.sqrt(square));
	}

	protected void drawPoint(Graphics g,int x_center,int y_center)
	{
		 int[][] xy = getXY(x_center, y_center);
		 Color tempColor=g.getColor();
		 //g.setColor(paintFill);
		 setFillPainter(g,paintFill);
		 g.fillPolygon(xy[0],xy[1],xy[0].length);
		 g.setColor(colorLine);
		 g.drawPolygon(xy[0],xy[1],xy[0].length);
		 g.setColor(tempColor);
	}

	protected int[][] getXY(int x_center, int y_center)
	{
		if (R==-1)
			calculateR();
		int[][] xy=new int[2][10];

		for(int i=0;i<xy[0].length;i++)
		{
		   if((i%2)==0)
			{
				xy[0][i]=x_center-(int)(radPnt*Math.sin(2*i*Math.PI/xy[0].length));
				xy[1][i]=y_center-(int)(radPnt*Math.cos(2*i*Math.PI/xy[1].length));
			}
		   else
			{
			   xy[0][i]=x_center-(int)(R*Math.sin(2*i*Math.PI/xy[0].length));
			   xy[1][i]=y_center-(int)(R*Math.cos(2*i*Math.PI/xy[1].length));
			}
		}
		return xy;
	}

	public MRect getDrawRect(Graphics graphics, IBaseGisObject obj, ILinearConverter converter) throws Exception
	{
		double [][][] arrpoint = obj.getRawGeometry();
		Point pobj = converter.getDstPointByPoint(new MPoint(arrpoint[0][0][0],arrpoint[1][0][0]));

		int[][] xy = getXY(pobj.x, pobj.y);

		int minX=0;
		int minY=0;

		int maxX=0;
		int maxY=0;


		for (int i=0;i<xy[0].length;i++)
		{
			if (i==0)
			{
				maxX=minX=xy[0][0];
				maxY=minY=xy[1][0];
			}

			if (maxX<xy[0][i])
				maxX=xy[0][i];

			if (minX>xy[0][i])
				minX=xy[0][i];

			if (maxY<xy[1][i])
				maxY=xy[1][i];

			if (minY>xy[1][i])
				minY=xy[1][i];
		}
		
		return new MRect(new MPoint(minX,minY),new MPoint(maxX,maxY));
	}
}
