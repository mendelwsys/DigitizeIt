package ru.ts.toykernel.drawcomp.painters.def;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.toykernel.converters.ILinearConverter;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 02.03.2009
 * Time: 12:54:46
 * Base Polygon painter
 */
public class DefPolyPainter extends DefLinePainter
{

	private MPoint anchorpoint;

	public DefPolyPainter()
	{
	}

	public DefPolyPainter(Color colorfill, Color colorLine, Stroke stroke)
    {
        super(colorfill,colorLine,stroke);
    }

    public int[] paint(Graphics graphics, IBaseGisObject drawMe, ILinearConverter converter, Point drawSize) throws Exception
    {
		setPainterParams(graphics, drawMe, converter, drawSize);

		double[][][] arrpoint = drawMe.getRawGeometry();
		MPoint cnvPnt=new MPoint();

		int[] rv=new int[]{0,0,0};

		for (int i = 0; i < arrpoint[0].length; i++)
		{
			int[] x = new int[arrpoint[0][i].length];
			int[] y = new int[arrpoint[1][i].length];

			for (int j = 0; j < x.length; j++)
			{
				cnvPnt.x = arrpoint[0][i][j];
				cnvPnt.y = arrpoint[1][i][j];
				Point pi = converter.getDstPointByPoint(cnvPnt);
				x[j] = pi.x;
				y[j] = pi.y;
			}

			if (paintFill instanceof TexturePaint)
			{
				final TexturePaint paint = (TexturePaint) paintFill;
				if (anchorpoint==null)
				{
					Rectangle2D rrect = paint.getAnchorRect();
					anchorpoint =converter.getPointByDstPoint(new MPoint(rrect.getX(),rrect.getY()));
				}
				else
				{
					Rectangle2D rrect = paint.getAnchorRect();
					Point p0=converter.getDstPointByPoint(anchorpoint);
					rrect.setRect(p0.getX(), p0.getY(),rrect.getWidth(),rrect.getHeight());
					paintFill=new TexturePaint(paint.getImage(),rrect);
				}
			}

			setFillPainter(graphics,paintFill);
			graphics.fillPolygon(x, y, x.length);
			rv[2]++;
			if ((paintFill instanceof  Color) && ((Color)paintFill).getRGB() != colorLine.getRGB())
			{
				rv[1]++;
				drawPolyLine(graphics, x, y, stroke);
//				if (x[0]!=x[x.length-1] || y[0]!=y[y.length-1]) //Замыкаем (Это не правильно геометрия должна обеспечить замыкание, а правила отрисовки)
//					drawPolyLine(graphics, new int[]{x[0],x[x.length-1]}, new int[]{y[0],y[y.length-1]}, stroke);
			}
		}
		return rv;
	}
}