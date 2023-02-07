package ru.ts.toykernel.drawcomp.painters.def;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.geom.def.ObjGeomUtils;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.toykernel.converters.ILinearConverter;

import java.awt.*;

/**
 * Base Polygon painter with holes
 */
//ru.ts.toykernel.drawcomp.painters.def.DefPolyHPainter

public class DefPolyHPainter extends DefLinePainter
{

	ObjGeomUtils algs = new ObjGeomUtils();

	public DefPolyHPainter()
	{
	}

	public DefPolyHPainter(Color colorfill, Color colorLine, Stroke stroke)
	{
		super(colorfill, colorLine, stroke);

	}

	public int[] paint(Graphics graphics, IBaseGisObject drawMe, ILinearConverter converter, Point drawSize) throws Exception
	{
		setPainterParams(graphics, drawMe, converter, drawSize);

		double[][][] arrpoint = drawMe.getRawGeometry();

		MPoint cnvPnt = new MPoint();


		int[] rv = new int[]{0, 0, 0};

		//От потока геометрии мы ожидаем что внутренние полигоны идут сразу после внешних,

		double[] outer_pX = null;
		double[] outer_pY = null;


		boolean fillinternal = true;


		Boolean isrigth_outer = null;
		//Перечисление частей полигона
		for (int i = 0; i < arrpoint[0].length; i++)
		{
			int[] x = new int[arrpoint[0][i].length];
			int[] y = new int[arrpoint[1][i].length];

			if (x.length > 1)
			{
				cnvPnt.x = arrpoint[0][i][0];
				cnvPnt.y = arrpoint[1][i][0];

				if (outer_pX != null)
				{
					if (algs.isInPolyGon(cnvPnt, outer_pX, outer_pY))
					{ //Надо определить является ли область обхода справа
						if (isrigth_outer == null)
							isrigth_outer = algs.isRigthInternal(outer_pX, outer_pY);
						boolean isrigth_internal = algs.isRigthInternal(arrpoint[0][i], arrpoint[1][i]);
						//Внешний полигон мы всегда заполняем, а внутренний заполняем если isrigth_internal==isrigth_outer
						fillinternal = (isrigth_internal == isrigth_outer);
					}
					else //Если текушая точка не в охватывающем полигоне тогда текущий массив становится охватывающим
					{
						outer_pX = arrpoint[0][i];
						outer_pY = arrpoint[1][i];
						isrigth_outer = null;
						fillinternal = true;
					}
				}
				else
				{
					outer_pX = arrpoint[0][i];
					outer_pY = arrpoint[1][i];
					isrigth_outer = null;
					fillinternal = true;
				}
			}

			for (int j = 0; j < x.length; j++)
			{
				cnvPnt.x = arrpoint[0][i][j];
				cnvPnt.y = arrpoint[1][i][j];

				Point pi = converter.getDstPointByPoint(cnvPnt);
				x[j] = pi.x;
				y[j] = pi.y;
			}
			paintPolyGon(graphics, x, y, fillinternal, rv);
		}

		return rv;
	}


	boolean deb_exit = false;

	public void paintPolyGon(Graphics graphics, int[] x, int[] y, boolean fillInternal, int[] rv) throws Exception
	{
		if (deb_exit)
			return;

		if (x.length < 4 || x.length != y.length || x[0] != x[x.length - 1] || y[0] != y[y.length - 1])
			throw new Exception("dirty Polygon: ring x length:" + x.length + " y length" + y.length + " startend point eq:" + new MPoint(x[0], y[0]).equals(new MPoint(x[x.length - 1], y[y.length - 1])));

		//Определим как рисовать полигон
		int rule = fillInternal ? AlphaComposite.SRC_OVER : AlphaComposite.DST_OUT;

		if (graphics instanceof Graphics2D)
			((Graphics2D) graphics).setComposite(AlphaComposite.getInstance(rule));

		//Если векторное произведение больше нуля значит возможно что текущий прямоугольник включен в другой прямоугольник
		//graphics.setColor(paintFill);
		setFillPainter(graphics, paintFill);
		graphics.fillPolygon(x, y, x.length);
		rv[2]++;
		if ((paintFill instanceof Color) && ((Color) paintFill).getRGB() != colorLine.getRGB())
		{
			((Graphics2D) graphics).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			rv[1]++;
			drawPolyLine(graphics, x, y, stroke);
		}
	}

}