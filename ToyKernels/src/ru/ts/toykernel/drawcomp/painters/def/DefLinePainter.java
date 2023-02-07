package ru.ts.toykernel.drawcomp.painters.def;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.gisutils.algs.common.MRect;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 02.03.2009
 * Time: 13:07:13
 * Base PolyLine painter
 */
public class DefLinePainter extends DefPointPainter {


	public DefLinePainter()
	{
		radPnt=0;
	}

	public DefLinePainter(Color colorfill, Color colorLine, Stroke stroke)
    {
        super(colorfill, colorLine,stroke,0, null);
    }

    public int[] paint(Graphics graphics, IBaseGisObject drawMe, ILinearConverter converter, Point drawSize) throws Exception
    {
		setPainterParams(graphics, drawMe, converter, drawSize);

		double[][][] pnts = drawMe.getRawGeometry();

		int[][] x = new int[pnts[0].length][];
        int[][] y = new int[pnts[1].length][];

		convert2drawCrds(converter, pnts, x, y);

        for (int i = 0; i < x.length; i++)
			drawPolyLine(graphics, x[i],y[i], stroke);

		int pntcnt = drawPoints(graphics, x, y);
		return new int[]{pntcnt,x.length,0};
	}


	public Shape createShape(IBaseGisObject drawMe, ILinearConverter converter) throws Exception
	{

		Path2D p2d=new Path2D.Double();

		double[][][] pnts = drawMe.getRawGeometry();

		int[][] x = new int[pnts[0].length][];
        int[][] y = new int[pnts[1].length][];

		convert2drawCrds(converter, pnts, x, y);

		for (int i = 0; i < x.length; i++)
		{
			p2d.moveTo(x[i][0],y[i][0]);
			for (int j = 1; j < x[i].length; j++)
				p2d.lineTo(x[i][j],y[i][j]);
		}
		return p2d;
	}

	protected int drawPoints(Graphics graphics, int[][] x, int[][] y)
	{
		int pntcnt=0;
		if (radPnt!=0)
		{
			for (int i = 0; i < x.length; i++)
                for (int j = 0; j < x[i].length; j++)
				{
					drawPoint(graphics,x[i][j],y[i][j]);
					pntcnt++;
				}
		}
		return pntcnt;
	}


	protected void drawPolyLine(Graphics graphics, int[] x, int[] y, Stroke stroke)
    {
		if (graphics instanceof Graphics2D  && stroke !=null)
			((Graphics2D) graphics).setStroke(stroke);
        graphics.setColor(colorLine);
		graphics.drawPolyline(x, y, Math.min(x.length,y.length));
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
