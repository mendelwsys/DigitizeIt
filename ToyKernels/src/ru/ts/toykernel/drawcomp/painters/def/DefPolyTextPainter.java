package ru.ts.toykernel.drawcomp.painters.def;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.utils.data.Pair;
import ru.ts.utils.RawImageOperations;

import java.awt.*;

/**
 * Draw PolyGonText
 * draws text on the midle of polygon
 * There is not fit for polygones with holes
 */
public class DefPolyTextPainter extends DefLineTextPainter
{
	public DefPolyTextPainter()
	{
	}
	public DefPolyTextPainter(String text, Font font, Color colorFill, Color colorLine,Color colorText)
    {
        super(text, font, colorFill, colorLine,colorText);
    }

    protected void drawPolygonName(Graphics graphics, int[] x, int[] y)
    {
            Point pobj = RawImageOperations.getMidleByImage(x, y);
            drawInscription(font, colorLine, paintFill,text, graphics, pobj, null);
    }

    public int[] paint(Graphics graphics, IBaseGisObject drawMe, ILinearConverter converter, Point drawSize) throws Exception
    {
		setPainterParams(graphics, drawMe, converter, drawSize);
		
		if (text != null && text.length() > 0)
        {
            Pair<int[][], int[][]> pr = getDrawCrdPoint(drawMe, converter);
            for (int i = 0; i < pr.first.length; i++)
                drawPolygonName(graphics,pr.first[i],pr.second[i]);
			return new int[]{0,0,pr.first.length};
		}
	   return new int[]{0,0,0};
    }
}
