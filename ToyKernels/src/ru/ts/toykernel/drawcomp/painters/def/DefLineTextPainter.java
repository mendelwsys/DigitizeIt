package ru.ts.toykernel.drawcomp.painters.def;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.utils.data.Pair;
import ru.ts.utils.gui.elems.TextStroke;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.GeneralPath;

/**
 * Default  Line Text Painter
 * draws text along line
 */
public class DefLineTextPainter extends DefPointTextPainter
{

	public DefLineTextPainter()
	{

	}
	public DefLineTextPainter(String text, Font font, Color colorFill, Color colorLine,Color colorText)
    {
        super(text, font, colorFill, colorLine,colorText);
    }

    protected void drawLineName(Graphics graphics, int[] x, int[] y)
    {
        GeneralPath gp = new GeneralPath();
        gp.moveTo((float) x[0],
                (float) y[0]);
        for (int i = 1; i < x.length; i++) {
            gp.lineTo((float) x[i],
                    (float) y[i]);
        }
        try
        {                    //new Font(font.getName(), Font.BOLD, font.getSize())
            graphics.setFont(font);
            Graphics2D g = (Graphics2D) graphics;
            FontRenderContext frc = g.getFontRenderContext();
            GlyphVector gv = font.createGlyphVector(frc, text);
            TextStroke ts = new TextStroke(gv, frc, TextStroke.ALIGN_START, font.getSize() / 2, 3,
                    text);
            Shape tac = ts.createStrokedShape(gp);
            graphics.setColor(new Color(0xFF606060));
            g.fill(tac);
        }
        catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public int[] paint(Graphics graphics, IBaseGisObject drawMe, ILinearConverter converter, Point drawSize) throws Exception
    {
		setPainterParams(graphics, drawMe, converter, drawSize);
		if (text != null && text.length() > 0)
        {
            Pair<int[][], int[][]> pr = getDrawCrdPoint(drawMe, converter);
            for (int i = 0; i < pr.first.length; i++)
                drawLineName(graphics,pr.first[i],pr.second[i]);
			return new int[]{0,pr.first.length,0};
		}
		return new int[]{0,0,0};
    }

    protected Pair<int[][], int[][]> getDrawCrdPoint(IBaseGisObject obj, ILinearConverter converter)
    {
        double[][][] arrpoints = obj.getRawGeometry();
        int[][] xarr = new int[arrpoints[0].length][];
        int[][] yarr = new int[arrpoints[1].length][];
		MPoint cnvPnt=new MPoint();
        for (int i = 0; i < arrpoints[0].length; i++)
        {
            xarr[i] = new int[arrpoints[0][i].length];
            yarr[i] = new int[arrpoints[1][i].length];

            for (int j = 0; j < arrpoints[0][i].length; j++)
            {
				cnvPnt.x=arrpoints[0][i][j];
				cnvPnt.y=arrpoints[1][i][j];
				Point pi = converter.getDstPointByPoint(cnvPnt);
                xarr[i][j] = pi.x;
                yarr[i][j] = pi.y;
            }
        }
        Pair<int[][],int[][]> pr= new Pair<int[][],int[][]>(xarr,yarr);
        return pr;
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
