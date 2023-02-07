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
 * Date: 19.03.2009
 * Time: 17:14:36
 * Simple rhombus
 */
public class SimpleRombus extends DefPointPainter
{
	protected void drawPoint(Graphics g,int x_center,int y_center)
	{
		Point central =new Point(x_center,y_center);
		int size=getSizePnt();
		int[] x=new int[]{(int)central.x- size/2,(int)central.x,(int)central.x+ size/2,(int)central.x,(int)central.x- size/2};
		int[] y=new int[]{(int)central.y,(int)central.y- size/2,(int)central.y,(int)central.y+ size/2,(int)central.y};

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
