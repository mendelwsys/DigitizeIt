package com.mwlib.app.painters;

import com.mwlib.app.geom.DigitizedWrapperGeom;
import com.mwlib.ptrace.PathDef;
import com.mwlib.ptrace.SegmentDef;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.toykernel.drawcomp.painters.def.DefPointPainter;
import ru.ts.toykernel.geom.IBaseGisObject;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * Default point painter
 */
public class DigitizerPainter extends DefPointPainter {


	public DigitizerPainter()
	{
		radPnt=0;
	}

	public DigitizerPainter(Color colorFill,Color colorLine, Stroke stroke)
    {
        super(colorFill, colorLine,stroke,0, null);
    }

    public int[] paint(Graphics graphics, IBaseGisObject drawMe, ILinearConverter converter, Point drawSize) throws Exception
    {
		setPainterParams(graphics, drawMe, converter, drawSize);
        int cnt=0;

        if (drawMe instanceof DigitizedWrapperGeom)
        {

            DigitizedWrapperGeom _drawMe=(DigitizedWrapperGeom)drawMe;


            cnt=drawPath(_drawMe.getPathDef(), (Graphics2D) graphics,converter);

//            int pntcnt = drawPoints(graphics, x, y);
//            return new int[]{pntcnt,x.length,0};
        }
        return new int[]{0,0,cnt};
	}



    public int drawPath(PathDef def, Graphics2D graphics,ILinearConverter converter)
    {
        SegmentDef[] segs= def.getCurve().getSegmentDefs();

        int n=segs.length;
        GeneralPath path = new GeneralPath();
        double[] xy=segs[n-1].getXY(2);
        Point2D.Double ptn=converter.getDstPointByPointD(new MPoint(xy[0], xy[1]));
        path.moveTo( ptn.getX(), ptn.getY());
        for (int i = 0; i < n; i++)
        {
            SegmentDef seg = segs[i];
            int cmd = seg.getCmd();

            double[] xy0=seg.getXY(0);
            double[] xy1=seg.getXY(1);
            double[] xy2=seg.getXY(2);

            Point2D.Double pt1 = converter.getDstPointByPointD(new MPoint(xy1[0], xy1[1]));
            Point2D.Double pt2 = converter.getDstPointByPointD(new MPoint(xy2[0], xy2[1]));


            switch (cmd)
            {
                case SegmentDef.POTRACE_CORNER:
                    path.lineTo(pt1.x,pt1.y);
                    path.lineTo(pt2.x,pt2.y);
                    break;
                case SegmentDef.POTRACE_CURVETO:

                    Point2D.Double pt0 = converter.getDstPointByPointD(new MPoint(xy0[0], xy0[1]));

                    path.curveTo(
                            pt0.x,pt0.y,
                            pt1.x,pt1.y,
                            pt2.x,pt2.y
                    );
                    break;
                default:
                    System.err.println("Error cmd:" + cmd);
            }
        }
//                path.closePath();
        if (def.getSign() == '+')
            setFillPainter(graphics, paintFill);
        else
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        graphics.fill(path);
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        return n;
    }


	public Shape createShape(IBaseGisObject drawMe, ILinearConverter converter) throws Exception
	{
	    throw new UnsupportedOperationException("Can't get shape bound for DigitizerPainter");
	}

//	protected int drawPoints(Graphics graphics, int[][] x, int[][] y)
//	{
//		int pntcnt=0;
//		if (radPnt!=0)
//		{
//			for (int i = 0; i < x.length; i++)
//                for (int j = 0; j < x[i].length; j++)
//				{
//					drawPoint(graphics,x[i][j],y[i][j]);
//					pntcnt++;
//				}
//		}
//		return pntcnt;
//	}


	public MRect getRect(Graphics graphics, IBaseGisObject obj, ILinearConverter converter) throws Exception
	{
		return obj.getMBB(null);
	}

	public MRect getDrawRect(Graphics graphics,IBaseGisObject obj, ILinearConverter converter)
	{
		return converter.getDstRectByRect(obj.getMBB(null));
	}
}
