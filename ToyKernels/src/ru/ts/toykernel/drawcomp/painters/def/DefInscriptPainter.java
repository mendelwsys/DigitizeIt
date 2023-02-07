package ru.ts.toykernel.drawcomp.painters.def;

import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;

import java.util.List;
import java.util.LinkedList;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;


/**
 * Позволяет рисовать надписи которые не пересекаются друг с другом
 * Experimental Inscript painter
 */
public class DefInscriptPainter extends DefPointTextPainter
{

	protected List<MRect> busyrect=new LinkedList<MRect>();
	public DefInscriptPainter()
	{
		System.out.println("Call new DefInscriptPainter");
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

			Rectangle2D rect = graphics.getFontMetrics().getStringBounds(text, graphics);

			int x = pobj.x;
			int y = pobj.y;
			setFillPainter(graphics,paintFill);

			MRect currentRect=new MRect(
						new MPoint(x,y-rect.getHeight()+3),
						new MPoint(Math.ceil(x+rect.getWidth()+2),Math.ceil(y+3))
					);
			for (MRect mRect : busyrect)
				if (mRect.isIIntersect(currentRect))
						return;

			graphics.fillRoundRect((int)currentRect.p1.x,(int)currentRect.p1.y,(int)currentRect.getWidth(),(int)rect.getHeight(),(int)(currentRect.getWidth()*0.1),(int)(currentRect.getHeight()*0.1));
			graphics.setColor(colorText);
			graphics.drawString(text, x, y);
			busyrect.add(currentRect);
		}
		finally
		{
			if (oldtransform!=null)
				((Graphics2D)graphics).setTransform(oldtransform);
		}
	}


}
