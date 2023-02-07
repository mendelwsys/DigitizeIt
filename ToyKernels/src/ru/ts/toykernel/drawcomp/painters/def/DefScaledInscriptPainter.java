package ru.ts.toykernel.drawcomp.painters.def;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.toykernel.drawcomp.rules.def.stream.CnSerialDrawRule;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by IntelliJ IDEA.
 * User: HP
 * Date: 25.02.2012
 * Time: 18:36:00
 * Рисовальщик для масштабируемого фонта
 * Т.е. размер фонта будет изменяться пропорционально масштабу
 */
public class DefScaledInscriptPainter  extends DefPointTextPainter
{

	public DefScaledInscriptPainter()
	{
	}

	public DefScaledInscriptPainter(String text, Font font, Color colorFill, Color colorLine, Color colorText)
	{
		super(text, font, colorFill, colorLine, colorText);
	}

	protected void drawInscription(Font font, Color colorText, Paint paintFill, String text, Graphics graphics, Point pobj, AffineTransform transform)
	{

		AffineTransform oldtransform=null;
		try
		{
			if (transform!=null)
			{
				oldtransform=((Graphics2D)graphics).getTransform();
				((Graphics2D)graphics).setTransform(transform);
			}

			graphics.setFont(font);

			int x = pobj.x;
			int y = pobj.y;

			graphics.setColor(colorText);
			graphics.drawString(text, x, y);
		}
		finally
		{
			if (oldtransform!=null)
				((Graphics2D)graphics).setTransform(oldtransform);
		}
	}



	public int[] paint(Graphics graphics, IBaseGisObject drawMe, ILinearConverter converter, Point drawSize) throws Exception
	{
		if (text != null && text.length() > 0)
		{
			double[][][] pnts = drawMe.getRawGeometry();
			Point pobj = converter.getDstPointByPoint(new MPoint(pnts[0][0][0],pnts[1][0][0]));

			IDefAttr attr = drawMe.getObjAttrs().get(CnSerialDrawRule.FONT_SIZE);
			Object val=attr.getValue();
			double fontsize=0;
			if (val instanceof Double)
				fontsize= (Double)attr.getValue();
			else if (val !=null)
			{
				try
				{
					fontsize= Double.parseDouble(val.toString());
				}
				catch (NumberFormatException e)
				{//
				}
			}
			double[] xy=converter.getDstSzBySz(new MRect(new MPoint(),new MPoint(fontsize, fontsize)));
			if (font==null)
				font=graphics.getFont();
			font=font.deriveFont((float)xy[0]);

			drawInscription(font, colorLine, paintFill,text, graphics, pobj, getTransform(drawMe, new MPoint(pobj.x, pobj.y)));
			return new int[]{1,0,0};
		}
		return new int[]{0,0,0};
	}

}
