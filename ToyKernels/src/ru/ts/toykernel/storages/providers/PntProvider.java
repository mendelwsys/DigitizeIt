package ru.ts.toykernel.storages.providers;

import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.raster.providers.IRPRovider;
import ru.ts.toykernel.converters.*;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.geom.IGisObject;
import ru.ts.toykernel.gui.IViewPort;
import ru.ts.utils.data.Pair;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Провайдер точечных объектов, представленых растровыми картинками
 *
 * ru.ts.toykernel.storages.providers.PntProvider
 */
public class PntProvider extends BaseInitAble
		implements IRPRovider
{
	protected int lrindex=0;


	public IProjConverter getConverter()
	{
		return converter;
	}

	protected ILayer layer;
	protected IProjConverter converter;
	protected String imgformat="PNG";
	public static final String IMG_FORMAT = "imgFormat";

	public Object init(Object obj) throws Exception
	{
		IDefAttr attr=(IDefAttr)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.LAYER_TAGNAME))
			layer=(ILayer)attr.getValue();
		else if (attr.getName().equalsIgnoreCase(KernelConst.CONVERTER_TAGNAME))
			converter=(IProjConverter)attr.getValue();
		else if (attr.getName().equalsIgnoreCase(IMG_FORMAT))
			imgformat=(String)attr.getValue();
		return null;
	}

	public void getRasterParameters(double[] dXdY, double[] szXszY, int[] nXnY) throws Exception
	{
		MRect proj_rect = layer.getMBBLayer(null);
		MRect drwrect = converter.getDstRectByRect(proj_rect);
		szXszY[0]=drwrect.getWidth();
		szXszY[1]=drwrect.getHeight();
		dXdY[0]=dXdY[1]=-1;
		nXnY[0]=nXnY[1]=-1;
	}

	public Pair<BufferedImage, String> getRawRasterByImgIndex(int[] imgindex, Map<String, String[]> args) throws Exception
	{
		return getRawRaster(getObjIndexByIndexImage(imgindex), args);
	}

	public Pair<BufferedImage, String> getRawRaster(int[] iXiY, Map<String, String[]> args) throws Exception
	{
		Pair<BufferedImage, String> rv=new Pair<BufferedImage, String>(null,null);
		//Рисуем растр по индексу
		IBaseStorage storage = layer.getStorage();
		rv.second=index2CurveId.get(iXiY[0]);
		IBaseGisObject giso = storage.getBaseGisByCurveId(rv.second);
		if (giso !=null)
		{
			//Рисуем и отдаем объект

			//TODO Здесь создаем графический контекст с соответсвующей метрикой текста и др.
			BufferedImage bimg=new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB);
			//получить прямоугольник рисования
			final MRect drwbx=layer.getObjectDrawRect(giso,bimg.getGraphics(),new IViewPort()
			{
				public Point getDrawSize() throws Exception
				{
					return new Point(1,1);
				}

				public IProjConverter getCopyConverter() throws Exception
				{
					List<ILinearConverter> convchain = converter.getConverterChain();
					//Карта  нас плоская поэтому размер объекта будет одинаковм для любого вьюпорта
					//а вью порт установлен в ноль, тогда координаты возврата функции будут
					// относительно начала поля карты, что нам и нужно для установки вью порта таким образом,
					// что бы прорисовывать только данный объект
					return new CrdConverterFactory.LinearConverterRSS(
							(IRotateConverter) convchain.get(0),
							(IScaledConverter) convchain.get(1),
							new CrdConverterFactory.ShiftConverter(new MPoint(0,0)));
				}

				public void setCopyConverter(IProjConverter converer) throws Exception
				{
					throw new UnsupportedOperationException();
				}
			});

			final Point drwsz=new Point((int)Math.ceil(drwbx.getWidth()),(int)Math.ceil(drwbx.getHeight()));
			bimg=new BufferedImage(drwsz.x,drwsz.y,BufferedImage.TYPE_INT_ARGB);



			layer.paintLayerObject(
					giso,bimg.getGraphics(),
					new IViewPort()
					{
						public Point getDrawSize() throws Exception
						{
							return drwsz;
						}

						public IProjConverter getCopyConverter() throws Exception
						{

							List<ILinearConverter> convchain = converter.getConverterChain();
							//Установка конвертора в верхнюю левую точку прорисовки картинки объекта
							MPoint shiftp = new MPoint(drwbx.p1.x, drwbx.p1.y);
							return new CrdConverterFactory.LinearConverterRSS(
									(IRotateConverter) convchain.get(0),
									(IScaledConverter) convchain.get(1),
									new CrdConverterFactory.ShiftConverter(shiftp));
						}

						public void setCopyConverter(IProjConverter converer) throws Exception
						{
							throw new UnsupportedOperationException();
						}
					});

//			ImageIO.write(bimg,"PNG",new File("D:/tst.png"));
			rv.first=bimg;
		}
		return rv;
	}

	public Pair<InputStream, String> getStreamRawRaster(int[] iXiY, Map<String, String[]> args) throws Exception
	{
		Pair<InputStream, String> rv=new Pair<InputStream, String>(null,null);
		Pair<BufferedImage, String> pr = getRawRaster(iXiY, args);
		if (pr.first!=null)
		{
			long tm=System.currentTimeMillis();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(pr.first,imgformat, os);
			System.out.println("compress time:"+(System.currentTimeMillis()-tm));
			os.flush();
			os.close();

			rv.first=new ByteArrayInputStream(os.toByteArray());
			rv.second=pr.second;
		}
		return rv;
	}


	protected  Map<Integer,String>  index2CurveId = new HashMap<Integer,String>();
	protected Map<String,Integer> curveId2index = new HashMap<String,Integer>();

	private int counter=0;
	protected synchronized int  getNextObjectId()
	{
		return ++counter;
	}

	public Pair<Boolean, String> getStreamRawRaster(int[] iXiY, OutputStream os, Map<String, String[]> args) throws Exception
	{
		Pair<Boolean, String> rv=new Pair<Boolean, String>(false,null);
		Pair<BufferedImage, String> pr = getRawRaster(iXiY, args);

		if (pr.first!=null)
		{
			long tm=System.currentTimeMillis();
			ImageIO.write(pr.first,imgformat, os);
			System.out.println("compress time:"+(System.currentTimeMillis()-tm));
			os.flush();
			rv.first=true;
			rv.second=pr.second;
		}
		return rv;
	}

	public int[] getObjIndexByIndexImage(int[] iXiY) throws Exception
	{
		return iXiY;
	}

	public String getInitScript(double[] dXdY, double[] szXszY, int[] nXnY, int lrindex, final Point rCenter, final Point rSize) throws Exception
	{

		String script = "\n";
		this.lrindex=lrindex;
		Iterator<IBaseGisObject> objs = getObjectsForDraw(rCenter, rSize);

		if (objs.hasNext())
			script += "mapstruct.layerarr["+lrindex+"]=new rlayer2(mapdiv,"+lrindex+",'"+String.valueOf(lrindex)+"');\n ";
		while (objs.hasNext())
		{
			IGisObject giso = (IGisObject) objs.next();

			String curveId = giso.getCurveId();

			Integer index = curveId2index.get(curveId);
			if (index==null)
			{
				index = getNextObjectId();
				curveId2index.put(curveId, index);
				index2CurveId.put(index,curveId);
			}

			MPoint mpnt = giso.getMidlePoint();
			Point drwpnt = converter.getDstPointByPoint(mpnt); //Получаем точку рисования в координатах листа карты.

			//Именно в координатах картового листа мы здесь создаем объект
			String urlp="\"A_XT/\" + mapstruct.keyres + \"/cmd=area&iy=0&ix="+index+"&w=\" + (mapstruct.w_wh.x) + \"&h=\" + (mapstruct.w_wh.y) + \"&cntscale=\" + mapstruct.cntscale + \"&lrid=\"+"+lrindex;
			//script += "mapstruct.layerarr["+lrindex+"].addObject("+drwpnt.x+","+drwpnt.y+","+urlp+");\n ";
			script += "mapstruct.layerarr["+lrindex+"].addObjWithIndex("+drwpnt.x+","+drwpnt.y+","+urlp+","+index+");\n ";
		}

		return script;
	}

	protected Iterator<IBaseGisObject> getObjectsForDraw(final Point rCenter, final Point rSize)
			throws Exception
	{
		//TODO Здесь создаем графический контекст с соответсвующей метрикой текста и др.
		Graphics graphics = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB).getGraphics();
		//Спрашивается на кой суда нужно графический интерфейс передавать?
		//а вот на кой:, даже точечный объект может занимать протяженную область,
		//например если сама точка выходит за рамки области видимости, а помечаем мы ее надпиью,
		// тогда может быть виден кусок надписи (например ее конец)
		Iterator<IBaseGisObject> objs = layer.getVisibleObjects(graphics,

				new IViewPort()
				{
					public Point getDrawSize() throws Exception
					{
						return rSize;
					}

					public IProjConverter getCopyConverter() throws Exception
					{
						//Здесь что бы понять видимость объектов нужно передать весь конверор вместе
						//с вью портом который определяется браузером

						List<ILinearConverter> convchain = converter.getConverterChain();
						return new CrdConverterFactory.LinearConverterRSS((IRotateConverter)convchain.get(0),
								(IScaledConverter)convchain.get(1),
								new CrdConverterFactory.ShiftConverter(
										new MPoint(rCenter.x - Math.ceil((1.0 * getDrawSize().x) / 2),
												rCenter.y - Math.ceil((1.0 * getDrawSize().y) / 2))));
					}


					public void setCopyConverter(IProjConverter converer) throws Exception
					{
						throw new UnsupportedOperationException();
					}
				});
		return objs;
	}

	public double[] getScaleRange()
	{
		return new double[]{-1,-1}; // Пока не ограничиваем
	}
}
