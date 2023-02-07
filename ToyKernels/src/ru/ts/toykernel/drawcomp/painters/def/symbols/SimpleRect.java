package ru.ts.toykernel.drawcomp.painters.def.symbols;

import ru.ts.toykernel.drawcomp.painters.def.DefPointPainter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;

import java.awt.*;

/**
 * простой прямоугольник
 */
public class SimpleRect   extends DefPointPainter
{
	private int width;
	private int height;

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public int getWidth()
	{
		return width;
	}


	public void setWidth(int width)
	{
		this.width = width;
	}

	public SimpleRect()
	{
		height=width=getSizePnt();
	}
	protected void drawPoint(Graphics g,int x_center,int y_center)
	{
		Point central =new Point(x_center,y_center);
		int[] x=new int[]{(int)central.x- width/2,(int)central.x- width/2,(int)central.x+ width/2,(int)central.x+ width/2,(int)central.x- width/2};
		int[] y=new int[]{(int)central.y- height/2,(int)central.y+ height/2,(int)central.y+ height/2,(int)central.y- height/2,(int)central.y- height/2};

		 Color tempColor=g.getColor();
		 //g.setColor(paintFill);
		 setFillPainter(g,paintFill);
		 g.fillPolygon(x,y,x.length);
		 g.setColor(colorLine);
		 g.drawPolygon(x,y,x.length);
		 g.setColor(tempColor);
	}

	public MRect getDrawRect(Graphics graphics, IBaseGisObject obj, ILinearConverter converter) throws Exception
	{
		double [][][] arrpoint = obj.getRawGeometry();
		Point pobj = converter.getDstPointByPoint(new MPoint(arrpoint[0][0][0],arrpoint[1][0][0]));
		int size=getSizePnt();
		return new MRect(new MPoint(pobj.x-size/2,pobj.y-size/2),new MPoint(pobj.x+size/2,pobj.y+size/2));
	}

}
