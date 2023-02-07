package ru.ts.toykernel.drawcomp.painters.def.symbols;

import ru.ts.toykernel.drawcomp.painters.def.DefPointTextPainter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;

import java.awt.*;
/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 05.03.2009
 * Time: 17:51:05
 * Numbered Rect
 */
public class NumberedRect  extends DefPointTextPainter
{

	protected int width;
	protected int height;
	int swidth;
	int sheight;

	public NumberedRect()
	{
	}

	public NumberedRect(String text,Color colorFill, Color colorLine, Color colorText,int width,int height)
	{
		super(text, null, colorFill, colorLine,colorText);
		this.width = width;
		this.height = height;
	}

	protected void drawInscription(Font font, Color color, String attrname, Graphics raphics, Point pobj)
	{
	   Color tempColor=raphics.getColor();
	   Font tempFont=raphics.getFont();

		//raphics.setColor(paintFill);
	   setFillPainter(raphics,paintFill);
	   raphics.fillRect(pobj.x,pobj.y,width,height);
	   raphics.setColor(colorLine);
	   raphics.drawRect(pobj.x,pobj.y,width,height);
	   raphics.setColor(colorText);
	   calculateFont(raphics);
	   raphics.setFont(font);
	   raphics.drawString(attrname,pobj.x+(int)(width/2.0-swidth/2.0),pobj.y+(int)(height/2.0+sheight/2.0));
	   raphics.setColor(tempColor);
	   raphics.setFont(tempFont);
	}

	private void calculateFont(Graphics g)
	{
		  int i=10;
		  Font f=new Font("sunserif",Font.BOLD,i);
		  Font previousFont=f;
		  FontMetrics fm=g.getFontMetrics(f);
		  while(fm.stringWidth(text)<(int)(0.8*width) && fm.getHeight()<(int)(0.9*height))
		  {
			  previousFont=f;
			  i+=2;
			  f=new Font("sunserif",Font.BOLD,i);
			  fm=g.getFontMetrics(f);
		  }
		fm=g.getFontMetrics(previousFont);
		font=previousFont;
		sheight=fm.getHeight();
		swidth=fm.stringWidth(text);
	}

	public MRect getRect(Graphics graphics, IBaseGisObject obj, ILinearConverter converter) throws Exception
	{
		MRect rect = getDrawRect(graphics,obj, converter);
		return converter.getRectByDstRect(rect, null);
	}

	public MRect getDrawRect(Graphics graphics,IBaseGisObject obj, ILinearConverter converter)
	{
		double [][][] arrpoint = obj.getRawGeometry();
		Point pobj = converter.getDstPointByPoint(new MPoint(arrpoint[0][0][0],arrpoint[1][0][0]));
		return new MRect(new MPoint(pobj.x,pobj.y),new MPoint(pobj.x+width,pobj.y+height));
	}




}
