package ru.ts.toykernel.storages.providers;

import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.raster.providers.IRPRovider;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.converters.IProjConverter;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.gui.IViewPort;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.utils.data.Pair;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.io.*;

/**
 * Провайдер
 * ru.ts.toykernel.storages.providers.ProjProvider;
 */
public class ProjProvider extends BaseInitAble
		implements IRPRovider
{
	protected Integer boxColor;
	protected Integer backgroundColor;

	public Integer getBoxColor()
	{
		return boxColor;
	}

	public void setBoxColor(Integer boxColor)
	{
		this.boxColor = boxColor;
	}


	public Integer getBackgroundColor()
	{
		return backgroundColor;
	}

	public void setBackgroundColor(Integer backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}

	public IProjContext getProjectctx()
	{
		return projectctx;
	}

	public IProjConverter getConverter()
	{
		return converter;
	}

	protected IProjContext projectctx;
	protected IProjConverter converter;
	protected String urlBase;
	protected String imgformat = "PNG";
	public static final String IMG_FORMAT = "imgFormat";

	public ProjProvider()
	{

	}

	/**
	 * @param projectctx - проект который
	 * @param converter  - Здесь сдвиговый конвертер должен быть уже включен в систему
	 * @param urlBase	- базовый урл относительно которых производится запрос изображений
	 */
	public ProjProvider(IProjContext projectctx, IProjConverter converter, String urlBase)
	{
		this.projectctx = projectctx;
		this.converter = converter;
		this.urlBase = urlBase;
	}


	public void getRasterParameters(double[] dXdY, double[] szXszY, int[] nXnY) throws Exception
	{
		//Подсчет размера растра
		List<ILayer> layers = projectctx.getLayerList();


		MRect wholerect = null;
		for (ILayer layer : layers)
			wholerect = layer.getMBBLayer(wholerect);

		if (wholerect == null)
			wholerect = new MRect();

		dXdY[0] = dXdY[1] = -1;

		szXszY[0] = wholerect.p1.x;
		szXszY[1] = wholerect.p1.y;
		szXszY[2] = wholerect.p4.x;
		szXszY[3] = wholerect.p4.y;

		nXnY[0] = nXnY[1] = -1;
	}

	public Pair<BufferedImage, String> getRawRasterByImgIndex(int[] imgindex, Map<String, String[]> args) throws Exception
	{
		return getRawRaster(getObjIndexByIndexImage(imgindex), args);
	}

	/**
	 * Отдаем сгенерированный растр, на вход получаем координаты верхнего
	 * левого угла и размер сегмента
	 *
	 * @param iXiY индекс растра(в данном случае не используется),координаты верхнего  левого угла и размер сегмента
	 * @param args
	 * @return -
	 * @throws Exception
	 */
	public Pair<BufferedImage, String> getRawRaster(int[] iXiY, Map<String, String[]> args) throws Exception
	{
		converter.getAsShiftConverter().setBindP0(new MPoint(iXiY[0], iXiY[1]));
		BufferedImage buffimg = new BufferedImage(iXiY[2], iXiY[3], BufferedImage.TYPE_INT_ARGB);
		buffimg = drawVectorImage(buffimg, new Point(iXiY[2], iXiY[3]), args);
		return new Pair<BufferedImage, String>(buffimg, iXiY[0] + "_" + iXiY[1]);
	}

	public Pair<InputStream, String> getStreamRawRaster(int[] iXiY, Map<String, String[]> args) throws Exception
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		Pair<Boolean, String> bool2s = getStreamRawRaster(iXiY, bos, args);
		Pair<InputStream, String> rv = new Pair<InputStream, String>(null, bool2s.second);

		if (bool2s.first)
		{
			bos.close();
			rv.first = new ByteArrayInputStream(bos.toByteArray());
		}
		return rv;
	}

	public Pair<Boolean, String> getStreamRawRaster(int[] iXiY, OutputStream os, Map<String, String[]> args) throws Exception
	{
		Pair<BufferedImage, String> b2s = getRawRaster(iXiY, args);
		Pair<Boolean, String> rv = new Pair<Boolean, String>(false, b2s.second);
		if (b2s.first != null)
		{
			if (b2s.first.getType() == BufferedImage.TYPE_INT_ARGB && imgformat.equalsIgnoreCase("BMP"))
			{
				BufferedImage buffimg = new BufferedImage(iXiY[2], iXiY[3], BufferedImage.TYPE_INT_RGB);
				Graphics g = buffimg.getGraphics();
				g.setColor(new Color(0x0FFFFFF));
				g.fillRect(0, 0, iXiY[2], iXiY[3]);
				g.drawImage(b2s.first, 0, 0, new ImageObserver()
				{

					public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
					{
						return false;
					}
				});
				b2s.first = buffimg;
			}

			long tm = System.currentTimeMillis();
			try
			{
				ImageIO.write(b2s.first, imgformat, os);
			}
			catch (IOException e)
			{
				System.out.println("It's possible access denied to temp folder of tomcat or folder does not exist");
				throw e;
			}
			System.out.println("compress time:" + (System.currentTimeMillis() - tm));

			os.flush();
			rv.first = true;
		}
		return rv;
	}

	public int[] getObjIndexByIndexImage(int[] iXiY) throws Exception
	{
		return iXiY;
	}

	public String getInitScript(double[] dXdY, double[] szXszY, int[] nXnY, int lrindex, Point rCenter, Point rSize) throws Exception
	{
		String script = "\n";
		script += "mapstruct.layerarr[" + lrindex + "]=new vlayer(mapdiv," + lrindex + ");\n ";
		return script;
	}

	public double[] getScaleRange()
	{
		return new double[]{-1, -1}; //Для векторных слоев пока не ограничиваем
	}

	protected BufferedImage drawVectorImage(BufferedImage buffimg, final Point drwSz, Map<String, String[]> args) throws Exception
	{

		drawBackGround(buffimg, drwSz);


//		//DEBUG_
//		MRect rect= null;
//		List<ILayer> layers = projectctx.getLayerList();
//		for (ILayer layer : layers)
//			rect = layer.getMBBLayer(rect);
//		//DEBUG_


		IViewPort viewPort = new IViewPort()
		{

			public Point getDrawSize() throws Exception
			{
				return drwSz;
			}

			public IProjConverter getCopyConverter()
			{
				return (IProjConverter) converter.createCopyConverter();
			}

			public void setCopyConverter(IProjConverter _converer)
			{
				converter = (IProjConverter) _converer.createCopyConverter();
			}
		};

		long tm = System.currentTimeMillis();

		Graphics graphics = buffimg.getGraphics();
		boolean ie = args != null && args.get("ie") != null && args.get("ie")[0].equalsIgnoreCase("true");
		if (ie)
			setIE6Opa(buffimg, graphics);

//		System.out.println("paint layer rect = " + rect);
//		//DEBUG_
//		rect=converter.getDstRectByRect(rect);

//		graphics.setColor(new Color(0xFFAAAAAA));
//		graphics.fillRect((int)rect.p1.x,(int)rect.p1.y,(int)rect.getWidth(),(int)rect.getHeight());
//		graphics.setColor(new Color(0xFF0000FF));
//		graphics.drawRect((int)rect.p1.x,(int)rect.p1.y,(int)rect.getWidth(),(int)rect.getHeight());
//		//DEBUG_

		List<ILayer> layers = projectctx.getLayerList();
		int[] rv = new int[]{0, 0, 0};
		for (ILayer layer : layers)
		{
			int[] rvv = layer.paintLayer(graphics, viewPort);
			for (int i = 0; i < rv.length; i++)
				rv[i] += rvv[i];
		}

		System.out.print("drawVectorImage Project Provider tm:" + (System.currentTimeMillis() - tm) + " ");
		System.out.print("pnts = " + rv[0] + " ");
		System.out.print("lines = " + rv[1] + " ");
		System.out.println("poly = " + rv[2]);
		if (rv[0] == 0 && rv[1] == 0 && rv[2] == 0) //Это сделано для того что бы не рисовать сверху то чего нет, что особенно важно для IE6???
		{
			buffimg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			if (ie)
				setIE6Opa(buffimg, buffimg.getGraphics());
		}
		return buffimg;
	}

	protected void drawBackGround(BufferedImage buffimg, Point drwSz)
	{
		Graphics graphics = buffimg.getGraphics();
		Color wascolor = graphics.getColor();
		if (boxColor != null)
		{
			graphics.setColor(new Color(boxColor, true));
			graphics.drawRect(0, 0, drwSz.x, drwSz.y);
		}
		if (backgroundColor != null)
		{
			graphics.setColor(new Color(backgroundColor, true));
			graphics.fillRect(0, 0, drwSz.x, drwSz.y);
		}
		graphics.setColor(wascolor);
	}

	protected void setIE6Opa(BufferedImage buffimg, Graphics graphics)
	{
		Color dcolor = graphics.getColor();
		graphics.setColor(new Color(0xFF));
		graphics.fillRect(0, 0, buffimg.getWidth(), buffimg.getHeight());
		graphics.setColor(dcolor);
	}

	public Object init(Object obj) throws Exception
	{
		IDefAttr attr = (IDefAttr) obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.PROJCTXT_TAGNAME))
			projectctx = (IProjContext) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(KernelConst.CONVERTER_TAGNAME))
			converter = (IProjConverter) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(URL_BASE))
			urlBase = (String) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(IMG_FORMAT))
			imgformat = (String) attr.getValue();
		else if (attr.getName().equalsIgnoreCase("boxColor"))
			boxColor = (int) Long.parseLong((String) attr.getValue(), 16);
		else if (attr.getName().equalsIgnoreCase("backColor"))
			backgroundColor = (int) Long.parseLong((String) attr.getValue(), 16);
	   return null;
	}

}
