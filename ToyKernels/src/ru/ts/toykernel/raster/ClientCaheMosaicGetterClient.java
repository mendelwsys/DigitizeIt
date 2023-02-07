package ru.ts.toykernel.raster;

import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.utils.data.Pair;

import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 */
public class ClientCaheMosaicGetterClient implements IClientRasterGetter
{
	private IServerRasterGetter raster;


	protected double[] dXdY= new double[2];//Размер сегмета
	protected double[] szXszY= new double[]{-1,-1};//размер по X и по Y
	protected int[] nXnY= new int[]{-1,-1};//кол-во сегментов по X и по Y

	Map<int[],BufferedImage> mosaicCache = new HashMap<int[],BufferedImage>();

	public ClientCaheMosaicGetterClient(IServerRasterGetter raster)
	{
		this.raster = raster;
	}

	public MPoint getRasterSize() throws Exception
	{
		if (nXnY[0]==-1 || nXnY[1]==-1)
			throw new Exception("Try to request images before getting raster size");
		return new MPoint(szXszY[0],szXszY[1]);
	}

	public void multScale(BindStructure struct,double dscale) throws Exception
	{
		requestRasterParameters(struct,dscale);
	}

	public java.util.List<Pair<BufferedImage, MPoint>> getRasters(BindStructure struct) throws Exception
	{
		requestRasterParameters(struct,1.0);
		//Вычисляем номера и запрашиваем изображения если его нет в кеше

		java.util.List<Pair<BufferedImage, MPoint>> rv= new LinkedList<Pair<BufferedImage, MPoint>>();
		int xIB=(int)Math.floor((struct.bindpt.x)/dXdY[0]);
		int yJB=(int)Math.floor((struct.bindpt.y)/dXdY[1]);

		int xIE=(int)Math.ceil((struct.bindpt.x+struct.szWindow.x)/dXdY[0]);
		int yJE=(int)Math.ceil((struct.bindpt.y+struct.szWindow.y)/dXdY[1]);

		for (int i=Math.max(xIB,0);i<=Math.min(xIE,nXnY[0]-1);i++)
			for (int j=Math.max(yJB,0);j<=Math.min(yJE,nXnY[1]-1);j++)
			{
				int[] imgindex = {i, j};
				BufferedImage buf=mosaicCache.get(imgindex);
				if (buf==null)
				{
					Pair<BufferedImage, String> img_string = raster.getImageRequest(imgindex);
					buf= img_string.first;

					if (buf==null)                      	
						buf=new BufferedImage((int)Math.round(dXdY[0]),(int) Math.round(dXdY[1]), BufferedImage.TYPE_INT_ARGB);
					mosaicCache.put(imgindex,buf);
				}
				double sx=dXdY[0]/buf.getWidth();
				double sy=dXdY[1]/buf.getHeight();
				if (sx!=1.0 ||sy!=1.0)
				{

					AffineTransform xformscale=new AffineTransform();
					xformscale.setToScale(sx, sy);
					AffineTransformOp tranopscale = new AffineTransformOp(xformscale, AffineTransformOp.TYPE_BILINEAR);
					buf=tranopscale.filter(buf,null);
				}
				rv.add(new Pair<BufferedImage, MPoint>(buf,new MPoint(i*dXdY[0]-struct.bindpt.x,j*dXdY[1]-struct.bindpt.y)));
			}
		return rv;
	}

	private void requestRasterParameters(BindStructure struct,double dscale)
			throws Exception
	{
		if (struct.bindpt==null || nXnY[0]==-1  || nXnY[1]==-1 || dscale!=1.0)
		{
			raster.getRasterParamters(struct,dscale,dXdY,szXszY,nXnY);
			mosaicCache.clear();
		}
	}
}
