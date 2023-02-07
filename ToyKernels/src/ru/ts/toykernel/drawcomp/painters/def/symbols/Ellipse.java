package ru.ts.toykernel.drawcomp.painters.def.symbols;

import ru.ts.toykernel.drawcomp.painters.def.DefPointPainter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.toykernel.converters.ILinearConverter;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 05.03.2009
 * Time: 17:43:13
 * Ellipse symbol driver (Base on Vasily classes)
 */
public class Ellipse  extends DefPointPainter
{
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

	private int width;
	private int height;

	public Ellipse()
	{
	}

	public Ellipse(Color colorFill, Color colorLine, Stroke stroke, int width,int height)
	{
		super(colorFill, colorLine, stroke, 0, null);

		this.width = width;
		this.height = height;
	}

	protected void drawPoint(Graphics raphics,int x,int y)
	{
		Color tempColor=raphics.getColor();
		//raphics.setColor(paintFill);
		setFillPainter(raphics, paintFill);
		raphics.fillOval(x-width/2,y-height/2,width,height);
		raphics.setColor(colorLine);
		raphics.drawOval(x-width/2,y-height/2,width,height);
		raphics.setColor(tempColor);
	}

	public MRect getDrawRect(Graphics graphics,IBaseGisObject obj, ILinearConverter converter)
	{
		double [][][] arrpoint = obj.getRawGeometry();
		Point pobj = converter.getDstPointByPoint(new MPoint(arrpoint[0][0][0],arrpoint[1][0][0]));
		return new MRect(new MPoint(pobj.x-width/2,pobj.y-height/2),new MPoint(pobj.x+width/2,pobj.y+height/2));

	}


}
