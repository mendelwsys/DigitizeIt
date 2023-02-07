package com.mwlib.ptrace;


import ru.ts.utils.data.Pair;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 29.12.13
 * Time: 22:13
 * To change this template use File | Settings | File Templates.
 */
public class Utils
{

    public  Bitmap getBmpByImage(BufferedImage sourceImage)
    {
        return getBmpByImage(sourceImage,null);
    }
    public  Bitmap getBmpByImage(BufferedImage sourceImage,IProgressObserver observer) {
        Raster raster = sourceImage.getRaster();
        int[] iarr = new int[4];
        Bitmap bmp = new Bitmap(sourceImage.getWidth(),sourceImage.getHeight());
        int totalPixels=sourceImage.getHeight()*sourceImage.getWidth();

        for(int y=0; y<sourceImage.getHeight(); y++) {
            for(int x=0; x<sourceImage.getWidth(); x++)
            {
                int[] pixel = raster.getPixel(x, y, iarr);
                if (pixel[0] + pixel[1] + pixel[2] + pixel[3] != 0) {
                    bmp.put(x, y, 1);
                }

                viewProcess(observer, totalPixels, y, x, sourceImage.getWidth());
            }
        }
        return bmp;
    }

    private void viewProcess(IProgressObserver observer, int totalPixels, int y, int x, int width) {
        if (observer!=null)
        {
            double value = y * width + x;
            if (value%5000==0) {
                double l = (value / totalPixels) * 100;
                observer.showProgress((int) Math.round(l));
            }
        }
    }

    public  Bitmap getBmpByImageWithColor(BufferedImage sourceImage,int rgb)
    {
        return getBmpByImageWithColor(sourceImage,rgb,null);
    }

    public  Bitmap getBmpByImageWithColor(BufferedImage sourceImage,int rgb,boolean[] emptyBmp)
    {
        return getBmpByImageWithColor(sourceImage,rgb,emptyBmp,null);
    }

    public  Bitmap getBmpByImageWithColor(BufferedImage sourceImage,int rgb,boolean[] emptyBmp,IProgressObserver observer)
    {
        if (emptyBmp!=null && emptyBmp.length>0) emptyBmp[0]=true;

        Bitmap bmp = new Bitmap(sourceImage.getWidth(),sourceImage.getHeight());

        int totalPixels=sourceImage.getHeight()*sourceImage.getWidth();

        for(int y=0; y<sourceImage.getHeight(); y++)
        {
            for(int x=0; x<sourceImage.getWidth(); x++)
            {
                if(sourceImage.getRGB(x,y)==rgb)
                {
                    if (emptyBmp!=null && emptyBmp.length>0) emptyBmp[0]=false;
                    bmp.put(x, y, 1);
                }
                else
                    bmp.put(x, y, 0);
                viewProcess(observer, totalPixels, y, x, sourceImage.getWidth());
            }
        }
        return bmp;
    }



    public void drawBmp(Bitmap bmp, BufferedImage result) {
        for(int y=0; y<bmp.getHeight(); y++) {
            for(int x=0; x<bmp.getWidth(); x++) {
                boolean pixel = bmp.get(x, y);
                if (pixel)
                    result.setRGB(x,y,0x0);
                else
                    result.setRGB(x,y,0xFFFFFF);
            }
        }
    }

    public int getTotalArea(PathDef def)
    {
        int areaCnt =0;
        while(def!=null)
        {
            if (def.getSign() == '+')
                areaCnt+=def.getArea();
            else
                areaCnt-=def.getArea();
            def=def.getNext();
        }
        return areaCnt;
    }

    public void drawByNext(PathDef def, double scale, BufferedImage result)
    {

        Graphics2D g2 = (Graphics2D)result.getGraphics();
        g2.scale(scale, scale);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, result.getWidth(), result.getHeight());


        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        while(def!=null)
        {

            drawPath(def, g2);

            def=def.getNext();
        }
    }

    public void drawBySibling(PathDef def, double scale, BufferedImage result) {

        Graphics2D g2 = (Graphics2D)result.getGraphics();
        g2.scale(scale, scale);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, result.getWidth(), result.getHeight());


        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        drawIt(def, g2);
    }

    private void drawIt(PathDef def, Graphics2D g2) {

        if (def==null)
            return;

        drawPath(def, g2);
        drawIt(def.getFirstChild(),g2);
        drawIt(def.getSibling(),g2);
    }

    public void drawPath(PathDef def, Graphics2D g2) {
        SegmentDef[] segs= def.getCurve().getSegmentDefs();

        int n=segs.length;
        GeneralPath path = new GeneralPath();
        double[] xy=segs[n-1].getXY(2);
        path.moveTo(xy[0], xy[1]);
        for (int i = 0; i < n; i++)
        {
            SegmentDef seg = segs[i];
            int cmd = seg.getCmd();
            double[] xy0=seg.getXY(0);
            double[] xy1=seg.getXY(1);
            double[] xy2=seg.getXY(2);

            switch (cmd)
            {
                case SegmentDef.POTRACE_CORNER:
                    path.lineTo(xy1[0],xy1[1]);
                    path.lineTo(xy2[0],xy2[1]);
                    break;
                case SegmentDef.POTRACE_CURVETO:
                    path.curveTo(
                            xy0[0],xy0[1],
                            xy1[0],xy1[1],
                            xy2[0],xy2[1]
                    );
                    break;
                default:
                    System.err.println("Error cmd:" + cmd);
            }
        }
//                path.closePath();


        if (def.getSign() == '+')
            g2.setColor(Color.BLACK);
        else
            g2.setColor(Color.WHITE);

        g2.fill(path);
    }


    public Pair<Shape,PathDef> getPath(PathDef def) {
        SegmentDef[] segs= def.getCurve().getSegmentDefs();

        int n=segs.length;
        GeneralPath path = new GeneralPath();
        double[] xy=segs[n-1].getXY(2);
        path.moveTo(xy[0], xy[1]);
        for (int i = 0; i < n; i++)
        {
            SegmentDef seg = segs[i];
            int cmd = seg.getCmd();
            double[] xy0=seg.getXY(0);
            double[] xy1=seg.getXY(1);
            double[] xy2=seg.getXY(2);

            switch (cmd)
            {
                case SegmentDef.POTRACE_CORNER:
                    path.lineTo(xy1[0],xy1[1]);
                    path.lineTo(xy2[0],xy2[1]);
                    break;
                case SegmentDef.POTRACE_CURVETO:
                    path.curveTo(
                            xy0[0],xy0[1],
                            xy1[0],xy1[1],
                            xy2[0],xy2[1]
                    );
                    break;
                default:
                    System.err.println("Error cmd:" + cmd);
            }
        }
        path.closePath();
        return new Pair<Shape, PathDef>(path,def);
    }

}
