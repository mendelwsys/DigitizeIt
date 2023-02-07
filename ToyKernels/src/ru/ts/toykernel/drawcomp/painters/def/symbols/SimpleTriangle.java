package ru.ts.toykernel.drawcomp.painters.def.symbols;

import ru.ts.toykernel.drawcomp.painters.def.DefPointPainter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 19.03.2009
 * Time: 17:26:12
 * Simple triangle
 */
public class SimpleTriangle extends DefPointPainter
{
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;

	private int width;
	private int height;
	private int direction=UP;

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


	public int getDirection()
	{
		return direction;
	}

	public void setDirection(int direction)
	{
		this.direction = direction;
	}

	private int[][] getCoordinatesBydirection(Point central)
	{

		int[] x;
		int[] y;

		switch (direction)
		{
			case DOWN:
				x = new int[]{(int) central.x - getWidth() / 2, (int) central.x + getWidth() / 2, (int) central.x};
				y = new int[]{(int) central.y - getHeight() / 2, (int) central.y - getHeight() / 2, (int) central.y + getHeight() / 2};
				break;
			case RIGHT:
				x = new int[]{(int) central.x - getWidth() / 2, (int) central.x - getWidth() / 2, (int) central.x + getWidth() / 2};
				y = new int[]{(int) central.y - getHeight() / 2, (int) central.y + getHeight() / 2, (int) central.y};
				break;
			case LEFT:
				x = new int[]{(int) central.x + getWidth() / 2, (int) central.x + getWidth() / 2, (int) central.x - getWidth() / 2};
				y = new int[]{(int) central.y - getHeight() / 2, (int) central.y + getHeight() / 2, (int) central.y};
				break;
			default: //UP
				x = new int[]{(int) central.x - getWidth() / 2, (int) central.x + getWidth() / 2, (int) central.x};
				y = new int[]{(int) central.y + getHeight() / 2, (int) central.y + getHeight() / 2, (int) central.y - getHeight() / 2};
				break;

		}
		return new int[][]{x, y};
	}

	protected void drawPoint(Graphics g, int x_center, int y_center)
	{
		int[][] xy = getCoordinatesBydirection(new Point(x_center, y_center));

		int[] x = xy[0];
		int[] y = xy[1];

		Color tempColor = g.getColor();
		//g.setColor(paintFill);
		setFillPainter(g,paintFill);
		g.fillPolygon(x, y, x.length);
		g.setColor(colorLine);
		g.drawPolygon(x, y, x.length);
		g.setColor(tempColor);
	}

	public MRect getDrawRect(Graphics graphics, IBaseGisObject obj, ILinearConverter converter) throws Exception
	{
		double [][][] arrpoint = obj.getRawGeometry();
		Point pobj = converter.getDstPointByPoint(new MPoint(arrpoint[0][0][0],arrpoint[1][0][0]));
		int[][] xy = getCoordinatesBydirection(new Point(pobj.x, pobj.y));

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

			if (maxY<xy[1][i])
				maxY=xy[1][i];

			if (minX>xy[0][i])
				minX=xy[0][i];

			if (minY>xy[1][i])
				minY=xy[1][i];

		}
		return new MRect(new MPoint(minX,minY),new MPoint(maxX,maxY));
	}
}
