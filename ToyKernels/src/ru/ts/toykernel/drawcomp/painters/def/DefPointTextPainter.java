package ru.ts.toykernel.drawcomp.painters.def;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.toykernel.drawcomp.ITextParamPainter;
import ru.ts.toykernel.converters.ILinearConverter;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

/**
 * Простой рисователь текста
 * Point text painter
 */
public class DefPointTextPainter extends DefPointPainter implements ITextParamPainter
{


	public Font getFont()
	{
		return font;
	}

	public void setFont(Font font)
	{
		this.font = font;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	protected String text;
	protected Font font;
	protected Color colorText;

	public Color getColorText()
	{
		return colorText;
	}

	public void setColorText(Color colorText)
	{
		this.colorText = colorText;
	}


	public DefPointTextPainter()
	{

	}
	public DefPointTextPainter(String text,Font font,Color colorFill, Color colorLine,Color colorText)
    {
        super(colorFill, colorLine, null, 0, null);
        this.text=text;
        this.font = font;
		this.colorText = colorText;
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

			int x = pobj.x + 3;
			int y = pobj.y - 3;
			//graphics.setColor(paintFill);


			setFillPainter(graphics,paintFill);
			graphics.fillRect(x,y-(int)rect.getHeight(),(int)rect.getWidth()+2,(int)rect.getHeight()+2);
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

			AffineTransform transform = getTransform(drawMe, new MPoint(pobj.x, pobj.y));

			drawInscription(font, colorLine, paintFill,text, graphics, pobj, transform);
			return new int[]{1,0,0};
		}
		return new int[]{0,0,0};
	}


	public MRect getRect(Graphics graphics, IBaseGisObject obj, ILinearConverter converter) throws Exception
	{
		if (text != null && text.length() > 0)
			return super.getRect(graphics,obj,converter);
		return obj.getMBB(null);
	}

	public MRect getDrawRect(Graphics graphics,IBaseGisObject obj, ILinearConverter converter) throws Exception
	{
		if (text != null && text.length() > 0)
		{
			double[][][] pnts = obj.getRawGeometry();
			Point pobj = converter.getDstPointByPoint(new MPoint(pnts[0][0][0],pnts[1][0][0]));

			Font oldfont=graphics.getFont();
			graphics.setFont(font);
			Rectangle2D rect = graphics.getFontMetrics().getStringBounds(text, graphics);
			graphics.setFont(oldfont);
			return new MRect(new Point(pobj.x,pobj.y-(int)rect.getHeight()),new Point(pobj.x+(int)rect.getWidth()+2,pobj.y+2));
		}
		return converter.getDstRectByRect(obj.getMBB(null));
	}

}
