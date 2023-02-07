package com.mwlib.app.painters;

import com.mwlib.utils.raster.PartialImageReader;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.toykernel.drawcomp.IPainter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.geom.def.RasterObject;
import ru.ts.utils.data.Pair;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 05.04.14
 * Time: 18:30
 * Немного изменена политика чтения растра
 */
public class RasterPainterN_bu implements IPainter
{

        private PartialImageReader pr;
        private  IDefAttr attr;
        private Point szWH;

    public synchronized int[] paint(Graphics g, IBaseGisObject drawMe, ILinearConverter converter, Point drawSize) throws Exception
	{
		if (drawMe instanceof RasterObject)
		{
			RasterObject rasterObject = (RasterObject) drawMe;
			MRect projRect= rasterObject.getMBB(null);

			MRect drwRect=converter.getDstRectByRect(projRect);
            IDefAttr rattr = rasterObject.getObjAttrs().get(RasterObject.RFNAME);

            if (pr==null || rattr==null || attr==null || !(attr.getValue().equals(rattr.getValue())))
            {
                if (pr!=null)
                {
                    pr.free();
                    attr=null;
                    pr = null;
                    szWH=null;
                }

                Pair<InputStream, String> rs = rasterObject.getStreamRawRaster();
                if (rs.first!=null)
                {
                    attr=rattr;
                    String flName=rs.second;
                    String extensionName = flName.substring(flName.lastIndexOf('.') + 1);
                    pr = new PartialImageReader(rs.first,extensionName);
                    szWH=pr.getImageSize();
                }
            }


			int[] ints = {0, 0, 0};
			if (pr!=null && szWH.getX()>0 && szWH.getY()>0)
			{
                double scale[]=new double[]{drwRect.getWidth()/szWH.getX(),drwRect.getHeight()/szWH.getY()};
                double[] wh=new double[]{drawSize.x/scale[0],drawSize.y/scale[1]};//ширина и высота вью порта координтах растра
                double imgX = drwRect.p1.x / scale[0];
                double imgY = drwRect.p1.y / scale[1];
//                if (
//                        !Double.isInfinite(wh[0]) && !Double.isInfinite(wh[0])
//                        &&
//                        !Double.isInfinite(imgX) && !Double.isInfinite(imgY)
//                        &&
//                        wh[0]*wh[1]< drwRect.getWidth()*drwRect.getHeight())
//                {
//
//
//                    BufferedImage img1=new BufferedImage((int)Math.round(wh[0]),(int)Math.round(wh[1]), BufferedImage.TYPE_INT_ARGB);
//                    img1.getGraphics().drawImage(img,(int)Math.round(imgX),(int)Math.round(imgY),new ImageObserver()
//                    {
//                        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
//                        {
//                            return false;
//                        }
//                    });
//
//
//                    AffineTransform xformscale = AffineTransform.getScaleInstance(scale[0],scale[1]);
//                    AffineTransformOp tranopscale = new AffineTransformOp(xformscale, AffineTransformOp.TYPE_BILINEAR);
//                    img1 = tranopscale.filter(img1, null);
//
//                    g.drawImage(img1,0,0,new ImageObserver()
//                    {
//                        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
//                        {
//                            return false;
//                        }
//                    });
//                }
//                else
                {
                    {
                        int sX,w,drX;
                        int sY,h,drY;
                        if (imgX>=0)
                        {
                            sX=0;
                            w = (int)Math.max(1.0,wh[0]-imgX);
                            drX=(int)Math.round(drwRect.p1.x);
                        }
                        else
                        {
                            sX=(int)Math.round(-imgX);

                            if (sX>szWH.getX())
                                sX=(int)(szWH.getX()-1);

                            double dW = szWH.getX() - (wh[0] + sX);
                            if (dW>=0)
                                w = (int)Math.round(wh[0]);
                            else
                            {
                                w = (int)Math.round(szWH.getX() - sX);
                                if (w <0) w = 0;
                            }
                            drX=0;
                        }


                        if (imgY>=0)
                        {
                            sY=0;
                            h = (int)Math.max(1.0,wh[1]-imgY);
                            drY=(int)Math.round(drwRect.p1.y);
                        }
                        else
                        {
                            sY=(int)Math.round(-imgY);
                            if (sY>szWH.getY())
                                sY=(int)(szWH.getY()-1);


                            double dH = szWH.getY() - (wh[1] + sY);
                            if (dH>=0)
                                h = (int)Math.round(wh[1]);
                            else
                            {
                                h = (int)Math.round(szWH.getY() - sY);
                                if (h <0) h = 0;
                            }
                            drY=0;
                        }

                        if (sX<0 || sY<0 || w<0 || h<0)
                            System.out.println("sY = " + sY);

                       BufferedImage img = pr.getImageByRectangle(new Rectangle(sX,sY,w,h));

                        System.out.println("Getting image by Rectangle w = " + w+" h="+h);

                       {
    //                    if (scale[0]!=1.0 || scale[1]!=1.0)
                            {
                                AffineTransform xformscale = AffineTransform.getScaleInstance(scale[0],scale[1]);
                                AffineTransformOp tranopscale = new AffineTransformOp(xformscale, AffineTransformOp.TYPE_BILINEAR);
                                img = tranopscale.filter(img, null);
                            }

                            g.drawImage(img,drX,drY,new ImageObserver()
                            {
                                public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
                                {
                                    return false;
                                }
                            });
                        }
                    }
                }
				ints[2]++;
			}
			return ints;
		}
		throw new UnsupportedOperationException();
	}

	public MRect getRect(Graphics graphics, IBaseGisObject obj, ILinearConverter converter) throws Exception
	{
		return null;
	}

	public MRect getDrawRect(Graphics graphics, IBaseGisObject obj, ILinearConverter converter) throws Exception
	{
		return null;
	}

	public Shape createShape(IBaseGisObject drawMe, ILinearConverter converter) throws Exception
	{
		return null;
	}
}
