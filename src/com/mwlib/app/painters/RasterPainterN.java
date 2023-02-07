package com.mwlib.app.painters;

import com.mwlib.utils.raster.PartialImageReader;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.geometry.Rect;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.toykernel.drawcomp.IPainter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.geom.def.RasterObject;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.storages.raster.IRasterContainer;
import ru.ts.utils.data.Pair;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 05.04.14
 * Time: 18:30
 * Немного изменена политика чтения растра
 */
public class RasterPainterN implements IPainter
{

//        private PartialImageReader pr;
//        private  IDefAttr attr;
//        private Point szWH;

    public synchronized int[] paint(Graphics g, IBaseGisObject drawMe, ILinearConverter converter, Point drawSize) throws Exception
	{
		if (drawMe instanceof RasterObject)
		{
			RasterObject rasterObject = (RasterObject) drawMe;
			MRect projRect= rasterObject.getMBB(null);

			MRect drwRect=converter.getDstRectByRect(projRect);
            //IDefAttr rattr = rasterObject.getObjAttrs().get(RasterObject.RFNAME);

            IBaseStorage stor = rasterObject.getStorage();
            if (!(stor instanceof IRasterContainer))
                throw new UnsupportedOperationException("Can't use type of the raster conatiner: " + stor.getClass().getCanonicalName());

            MPoint szWH = ((IRasterContainer) stor).getImageSize();

			int[] ints = {0, 0, 0};
			if (szWH.getX()>0 && szWH.getY()>0)
			{
                double scale[]=new double[]{drwRect.getWidth()/szWH.getX(),drwRect.getHeight()/szWH.getY()};
                double[] wh=new double[]{drawSize.x/scale[0],drawSize.y/scale[1]};//ширина и высота вью порта координтах растра
                double imgX = drwRect.p1.x / scale[0];
                double imgY = drwRect.p1.y / scale[1];
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

                       BufferedImage img = ((IRasterContainer) stor).getImageByRectangle(new Rectangle(sX, sY, w, h));

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
