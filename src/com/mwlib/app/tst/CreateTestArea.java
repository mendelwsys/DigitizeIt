package com.mwlib.app.tst;

import com.mwlib.app.plugins.digitizer.ParamEx;
import com.mwlib.ptrace.*;
import shp.core.ShpPoint;
import su.gis.utils.shp.ShpPgon;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 17.05.14
 * Time: 12:04
 * To change this template use File | Settings | File Templates.
 */
public class CreateTestArea
{


 static public double polygonArea(ShpPoint[] points)
    {
       double area = 0;         // Accumulates area in the loop
       int j = points.length-1;  // The last vertex is the 'previous' one to the first

        for (int i=0; i<points.length; i++)
        {
            ShpPoint pointI = points[i];
            ShpPoint pointJ = points[j];
            area = area +  (pointJ.x+pointI.x) * (pointJ.y-pointI.y);
            j = i;  //j is previous vertex to i
        }
        return Math.abs(area/2);
}


    public static void main(String[] args) throws Exception
    {
        BufferedImage bufferedImage = getImage();

        Utils u= new Utils();
        PoTraceJ poTraceJ = new PoTraceJ();
        ParamEx param = new ParamEx();
        param.setResolution(1);

        boolean[] emptyBmp = new boolean[]{true};
        Bitmap bmp = u.getBmpByImageWithColor(bufferedImage, param.rColor, emptyBmp, null);

        param.turdsize=1;


        PathDef def = poTraceJ.trace(param, bmp,null);
        int totalArea = u.getTotalArea(def);
        System.out.println("totalArea = " + totalArea);


        double total=10;
        ShpPgon pgon =null;
        Vector<Vector<ShpPoint>> pgons = new Vector<Vector<ShpPoint>>();

        while(def!=null)
        {
            PathIterator pathItr = u.getPath(def).first.getPathIterator(new AffineTransform(),0.0002);

            while (!pathItr.isDone())
            {
                double[] coords = new double[6];

                int segType = pathItr.currentSegment(coords);
                pgon=processPathPart(segType, coords,pgon,pgons);
                pathItr.next();
            }


            //Считаем площадь

            if (pgon==null)
                continue;

            double area=polygonArea(pgon.toArray(new ShpPoint[pgon.size()]));


            if (def.getSign() == '+')
                total+=area;
            else
                total-=area;

            def=def.getNext();
        }

        double abs = Math.abs(total - totalArea);
        System.out.println("pgons = " + total+" diff ="+ abs +" %="+(abs*100.0)/Math.min(total,totalArea));



}

//    private static BufferedImage getImage() throws IOException {
//        BufferedImage bufferedImage=new BufferedImage(100,100, BufferedImage.TYPE_INT_RGB);
//        Graphics gr = bufferedImage.getGraphics();
//        gr.setColor(new Color(0xFF0000));
//        gr.fillRect(20,20,50,50);
//
//
//        gr.setColor(new Color(0x000000));
//        gr.fillRect(30,30,10,10);
//
//
//        gr.setColor(new Color(0x000000));
//        gr.fillRect(50,50,10,10);
//
//        ImageIO.write(bufferedImage, "PNG", new File("C:\\PapaWK\\Projects\\JavaProj\\Victor\\2Victor\\test.png"));
//        return bufferedImage;
//    }

    private static BufferedImage getImage() throws IOException {
        return ImageIO.read(new File("C:\\PapaWK\\Projects\\JavaProj\\Victor\\DigitizeItRes\\MAPDIR\\e1.bmp"));
    }


    static protected ShpPgon processPathPart(int segType, double[] coords,ShpPgon pgon,Vector<Vector<ShpPoint>> pgons)
    {
      switch ( segType )
      {
        case PathIterator.SEG_MOVETO:
          // check if polygon already exists and not empty, so we should first dump its content
          if ( (pgon != null) && (!pgon.isEmpty()) )
          {
            // it is not first polygon in the shape
            pgon.close();
            pgons.add(pgon);
          }
          pgon = new ShpPgon();
          pgon.add(new ShpPoint(coords[0], coords[1]));
          break;
        case PathIterator.SEG_LINETO:
          pgon.add(new ShpPoint(coords[ 0 ], coords[ 1 ]));
          break;
        case PathIterator.SEG_QUADTO:
          break;
        case PathIterator.SEG_CUBICTO:
          break;
        case PathIterator.SEG_CLOSE:
          // complete polygon and dump it
          pgon.close();
          pgons.add(pgon);
          break;
      }
        return pgon;
    }

}


