package ru.ts.toykernel.gui.panels;

import com.mwlib.utils.Enc;
import ru.ts.gisutils.datamine.ProjBaseConstatnts;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.utils.data.Pair;

import javax.swing.*;

import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.converters.*;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.gui.IApplication;
import ru.ts.toykernel.plugins.IGuiModule;
import ru.ts.toykernel.plugins.ICommandBean;
import ru.ts.toykernel.gui.IViewPort;
import ru.ts.xml.IXMLObjectDesc;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.*;


/**
 * Panel of Map Viewer
 */
public class ViewPicturePanel2
		extends BasePicture
{


	protected IProjContext projectctx;//Контекст проекта
	protected IProjConverter converter;//Конвертер
	private IApplication application;//application of
	private ConvB64Initializer initializer;//initilizer
	private List<IGuiModule> drawmoduls = new LinkedList<IGuiModule>();

	public static final int WK_MODE = 0x1;

	protected JLabel crdStatus;//Строка статуса для отображения текущих координат мыши
	protected JLabel scaleStatus;//Строка статуса для отображения текущего масштаба
	protected int mode = WK_MODE;

	protected boolean google_switch = false;//Показвать гугловый растр
	protected boolean map_switch = true;//Показывать карту


	protected MRect wholerect;//Охватывающий все точки прямоугольник
	protected IXMLObjectDesc desc;


	class DrawMapT extends Thread
	{

		BufferedImage buffimg;
		int dXdY[] = {0, 0};//Сдвиг буфера относительно предедущего значения

		IProjConverter prc;

		boolean isterminate = false;

		public void setTerminate()
		{
			this.isterminate = true;
		}

		/**
		 * @param dXdY	  - смещение относительно последней сгенерированной картинки
		 * @param totaldXdY - смещение относительно ViewPorta
		 * @throws Exception -
		 */
		public void shiftPictureXY(int[] dXdY, int[] totaldXdY) throws Exception
		{
			this.dXdY[0] = dXdY[0];
			this.dXdY[1] = dXdY[1];

//			System.out.println("shiftPictureXY:"+dXdY[0]+" "+dXdY[1]);
//поскольку пока мышь не была отжата у нас не пересчитывается точка привязки  поэтому каждый раз пересчитаваем локальную точку привязки
			prc.getAsShiftConverter().recalcBindPointByDrawDxDy(new double[]{-totaldXdY[0], -totaldXdY[1]});
		}


		DrawMapT(IViewPort vp) throws Exception
		{
			prc = (IProjConverter) vp.getCopyConverter();
		}

		IViewPort getViewPort()
		{
			return new IViewPort()
			{

				public IProjConverter getCopyConverter()
				{
					return (IProjConverter) prc.createCopyConverter();
				}

				public void setCopyConverter(IProjConverter converer)
				{
					prc = (IProjConverter) converer.createCopyConverter();
				}

				public Point getDrawSize() throws Exception
				{
					MPoint l_drwSize = prc.getAsShiftConverter().getViewSize();
					return new Point((int) Math.round(l_drwSize.x), (int) Math.round(l_drwSize.y));
				}

				public boolean equals(Object obj)
				{
					try
					{
						if (obj instanceof IViewPort)
						{
							IViewPort vp = (IViewPort) obj;
							return prc.equals(vp.getCopyConverter()) && this.getDrawSize().equals(vp.getDrawSize());
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					return false;
				}
			};
		}

		public void run()
		{
			try
			{
				buffimg = drawVectorImage(null, this.getViewPort());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				isterminate = true;
			}

		}

		protected BufferedImage drawVectorImage(BufferedImage buffimg, IViewPort vp) throws Exception
		{
			List<ILayer> layers = new LinkedList<ILayer>(projectctx.getLayerList());

			if (layers.size() != 0)
			{
				if (map_switch)
				{
					try
					{

						if (buffimg == null)
							buffimg = drawBackground(false);
						long tm = System.currentTimeMillis();

						Graphics graphics = buffimg.getGraphics();

						int[] rv = new int[]{0, 0, 0};
						for (ILayer layer : layers)
						{
							if (isterminate)
								break;//Если установлен флаг окончания прервать рисование
							int[] rvv = layer.paintLayer(graphics, vp);
							for (int i = 0; i < rv.length; i++)
								rv[i] += rvv[i];
						}
						System.out.print("Picture panel tm:" + (System.currentTimeMillis() - tm) + " ");

//		tm=System.currentTimeMillis();
//		try
//		{
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			ImageIO.write(buffimg,"PNG", bos);
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//		System.out.print("tm2:"+(System.currentTimeMillis()-tm)+" ");

						System.out.print("pnts = " + rv[0] + " ");
						System.out.print("lines = " + rv[1] + " ");
						System.out.println("poly = " + rv[2]);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			return buffimg;
		}
	}


	DrawThread drawThread = new DrawThread();

	class DrawThread extends Thread
	{
		DrawMapT drawVectorMapT;

		BufferedImage buffimg;//Буфер векторной графики
		int dXdY[] = {0, 0};//Текущий Сдвиг буффера относительно послденей картинки
		int totaldXdY[] = {0, 0};//Общий Сдвиг буффера
		boolean isrefresh = true;//Явное указание перерисовать

		public int[] getTotaldXdY()
		{
			return totaldXdY;
		}


		public synchronized void shiftPictureXY(int[] dXdY)
		{
			this.dXdY[0] += dXdY[0];
			this.dXdY[1] += dXdY[1];

			this.totaldXdY[0] += dXdY[0];
			this.totaldXdY[1] += dXdY[1];

		}

		public synchronized Pair<BufferedImage, int[]> getPictureXY()
		{
			return new Pair<BufferedImage, int[]>(buffimg, new int[]{this.dXdY[0], this.dXdY[1]});
		}

		public synchronized void setRefresh()
		{
			isrefresh = true;
			totaldXdY[0] = 0;
			totaldXdY[1] = 0;
		}

		boolean isterminate = true;

		public void setTerminate()
		{
			this.isterminate = true;
		}

		public synchronized void start()
		{

			if (isterminate)
			{
				isterminate = false;
				super.start();
			}

		}

		public void run()
		{
			while (!isterminate)
			{
				long tm = System.currentTimeMillis();
				try
				{
					trunrefrsh();
					long spend = 200 - (System.currentTimeMillis() - tm);
					sleep((spend < 0 ? 50 : spend));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}


		private int[] wasdXdY = new int[]{0, 0}; //Измеритель скорости смещени картинки

		synchronized void trunrefrsh() throws Exception
		{
			if (
					isrefresh
							||
							(drawVectorMapT == null && dXdY[0] != 0 && dXdY[1] != 0)
							||
							(drawVectorMapT != null &&
									(dXdY[0] != drawVectorMapT.dXdY[0] || dXdY[1] != drawVectorMapT.dXdY[1]))
					)
			{
				int speed[] = {Math.abs(wasdXdY[0] - dXdY[0]), Math.abs(wasdXdY[1] - dXdY[1])};
				if (speed[0] < 3 && speed[1] < 3)//Если скорость движения меньше порога тогда
				{
					if (drawVectorMapT != null) //Произошли изменения (удалить текущую прорисовку)
					{
						if (!drawVectorMapT.isterminate || isrefresh)
							//Закончить поток drawVectorMapT
							drawVectorMapT.setTerminate();
						else  //Копируем реузудьтаты работы TODO Вставить критерий провери целисообразности копирования
							copyResults();
						drawVectorMapT = null;
					}


					if (drawVectorMapT == null)
					{
						drawVectorMapT = new DrawMapT(getViewPort());//Произвести копирование ViewPort перед рисованием и начать рисование с этим вьюпортом
						drawVectorMapT.shiftPictureXY(dXdY, totaldXdY);

						drawVectorMapT.start();
						isrefresh = false;
					}
				}
				else
				{
					if (drawVectorMapT != null) //Произошли изменения (удалить текущую прорисовку)
					{
						//Закончить поток
						drawVectorMapT.setTerminate();
						drawVectorMapT = null;
					}
				}
			}
			else
				copyResults();
			wasdXdY[0] = dXdY[0];
			wasdXdY[1] = dXdY[1];
		}

		private void copyResults()
		{
			if (drawVectorMapT != null && drawVectorMapT.isterminate)
			{
				buffimg = drawVectorMapT.buffimg;
				dXdY[0] = dXdY[0] - drawVectorMapT.dXdY[0];
				dXdY[1] = dXdY[1] - drawVectorMapT.dXdY[1];
				drawVectorMapT = null;
				repaint();
			}
		}
	}


	public ViewPicturePanel2()
	{
		this.addComponentListener(new ComponentListener()
		{

			public void componentResized(ComponentEvent e)
			{
				try
				{
					getViewPort().getDrawSize();//Извещаем конвертер что изменился размер окна
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}

			public void componentMoved(ComponentEvent e)
			{
			}

			public void componentShown(ComponentEvent e)
			{
			}

			public void componentHidden(ComponentEvent e)
			{
			}
		});
	}

	public List<IGuiModule> getGuiModules()
	{
		return drawmoduls;
	}

	public void setGuiModules(List<IGuiModule> drawmoduls)
	{
		this.drawmoduls = drawmoduls;
	}

	/**
	 * ViewPicturePanel
	 *
	 * @param projectctx  -
	 * @param initializer -
	 * @param drawmoduls  -
	 * @throws Exception -
	 */
	public ViewPicturePanel2(IProjContext projectctx, ConvB64Initializer initializer, List<IGuiModule> drawmoduls) throws Exception
	{
		this();
		this.initializer = initializer;
		if (initializer != null && initializer.getB_converter() != null)
		{
			converter = (IProjConverter) initializer.createByTypeName(initializer.getS_convertertype());
			converter.initByBase64Point(initializer.getB_converter());
			converter.getAsShiftConverter().setBindP0(MPoint.getByBase64Point(initializer.getB_currentP0()));

//++DEBUG Проверка нового конвертора
			IScaledConverter scaledConv = converter.getAsScaledConverter();
			IShiftConverter shiftconverter = converter.getAsShiftConverter();
			MPoint pnt0 = shiftconverter.getBindP0();
			MPoint mPoint = scaledConv.increaseMap(1.0);
			MPoint drawpnt = new MPoint(pnt0.x * mPoint.x, pnt0.y * mPoint.y);
			converter = new CrdConverterFactory.LinearConverterRSS
					(
							converter.getAsRotateConverter().getRotMatrix(),
							scaledConv.increaseMap(1.0),
							drawpnt
					);
			System.out.println("");
//--DEBUG
		}
		this.drawmoduls = drawmoduls;
		this.initializer = initializer;
		setProjectContext(projectctx, converter, false);
	}

	public ViewPicturePanel2(IProjContext projectctx, IProjConverter converter, List<IGuiModule> drawmoduls) throws Exception
	{
		this();
		this.drawmoduls = drawmoduls;
		setProjectContext(projectctx, converter, false);
	}

	public void setProjectContext(IProjContext project, IProjConverter converter, boolean isinit)
			throws Exception
	{
		this.projectctx = project;
		if (isinit || converter == null)
			setInitScale(converter);
		else
			this.converter = converter;
		refresh(null);
	}

	protected void setInitScale(IProjConverter converter) throws Exception
	{
		List<ILayer> layers = projectctx.getLayerList();
		if (layers.size() != 0)
			wholerect = null;
		for (ILayer layer : layers)
			wholerect = layer.getMBBLayer(wholerect);
		try
		{
			{
				if (converter == null)
					this.converter = (IProjConverter) new CrdConverterFactory().createByTypeName(CrdConverterFactory.LinearConverterAB.LINEARPROJAB);
				int[] sz = {1000, 1000};//Грубая оценка масштаба
				converter.getAsShiftConverter().setViewSize(new MPoint(sz[0], sz[1]));
				converter.getAsScaledConverterCtrl().recalcScale(wholerect, sz);
				converter.getAsScaledConverterCtrl().increaseMap(2.5);
//				MRect drawRect = converter.getDstRectByRect(wholerect);
//				MPoint lp=drawRect.p1;
//				converter.getAsShiftConverter().recalcBindPointByDrawDxDy(new double[]{-lp.x,-lp.y});
//				converter.getAsShiftConverter().recalcBindPointByDrawDxDy(new double[]{0,400});
//				converter.getAsShiftConverter().recalcBindPointByDrawDxDy(new double[]{0,400});
//				converter.getAsShiftConverter().recalcBindPointByDrawDxDy(new double[]{0,400});
			}
			showMetersOnPixel();
		}
		catch (Exception e)
		{//
			e.printStackTrace();
		}
	}

	protected void showMetersOnPixel() throws Exception
	{
		MPoint punitonpixel = converter.getAsScaledConverterCtrl().getUnitsOnPixel();

		if (punitonpixel.x != punitonpixel.y)
			throw new UnsupportedOperationException("Can't show Meters on pixel scale by X and Y not equals");

		double unitonpixel = punitonpixel.x;
		double deve = Math.log(10);
		int pw10 = (int) (Math.log(unitonpixel) / deve);
		if (pw10 < 0)
			pw10 -= 1;

		double mant = unitonpixel;
		if (Math.abs(pw10) > 3)
			mant = unitonpixel / Math.pow(10, pw10);
		if (scaleStatus != null)
		{

			scaleStatus.setText(Enc.get("$188") +
					String.valueOf(
							((int) (mant * 1000)) / 1000.0) + ((Math.abs(pw10) > 3) ? ("E" + (pw10)) : "")
					+
					" "
					+
					ProjBaseConstatnts.getNameUnitsByUnitsName(projectctx.getProjMetaInfo().getS_MapUnitsName()) + Enc.get("$189")
			);
		}
	}

	/**
	 * Конвертирует в гео-координаты события мыши
	 *
	 * @param event - с обытие мыши
	 * @return - гео координаты
	 */
	public MPoint convertMouseEvent(MouseEvent event)
	{
		return convertdrawPoint(event.getPoint());
	}

	public Point convertToDrawCrd(MPoint pt)
	{
		return converter.getDstPointByPoint(pt);
	}

	public MPoint convertdrawPoint(Point point)
	{
		List<ILayer> layers = projectctx.getLayerList();
		if (layers == null || layers.size() == 0)
			return null;

		if (point != null)
			return converter.getPointByDstPoint(point);//getPointByMousePoint(point, currentP0);
		return null;
	}

	public Pair<Integer, String> savePointsByGPS(DataInputStream dis, int layerindex) throws Exception
	{
//TODO		if (layers.size() == 0)
//			throw new Exception("Default layer is not define");
//
//		if (layerindex < 0)
//			layerindex = 0;
//
//		final Layer layer = layers.get(layerindex);
//		GpsLoaderImpl loader = new GpsLoaderImpl();
//		int orderindex = loader.loadLayer(dis, Layer.getIMap(), layer,
//				new ICurveFactory()
//				{
//					public ISerializer createEmptyCurve() throws Exception
//					{
//						return new Curve(layer, AttrsConst.LINESTRING, layer.getnextObjId("NEWOBJ"));
//					}
//				}
//		);
//		String curveId = layer.getCurveIdByOrderIndex(orderindex);
//		layer.setSelectObj(curveId);
//		return new Pair<Integer, String>(layerindex, curveId);
		return null;
	}


	protected JPopupMenu buildPopUp()
	{
		return null;
	}

	public List<ILayer> getLayers()
	{
		return projectctx.getLayerList();
	}


	public void setApplication(IApplication application)
	{
		this.application = application;
	}

	public IApplication getApplication()
	{
		return application;
	}

	public JComponent getComponent()
	{
		return this;
	}

	public void refresh(Object arg)
	{
		synchronized (this)
		{
			modbuffimage = null;
		}
		if (!(arg instanceof ICommandBean) || !((ICommandBean) arg).getCommand().equals(KernelConst.P_SKIPDRAWWM))
			drawThread.setRefresh();
		try
		{
			showMetersOnPixel();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		repaint();
	}

	public IProjContext getProjContext()
	{
		return projectctx;
	}

	public IViewPort getViewPort()
	{
		return new IViewPort()
		{

            public synchronized Point getDrawSize() throws Exception
			{
				Dimension dm = getSize();
				Point rv = new Point((int) Math.ceil(dm.getWidth()), (int) Math.ceil(dm.getHeight()));
				converter.getAsShiftConverter().setViewSize(new MPoint(rv));
				return rv;
			}

//			public synchronized IProjConverter getConverter()
//			{
//				getDrawSize();//Установка в конвертер объема рисования
//				return converter;  //TODO Использовать копию конвертора для того что
//				// бы развязать процедуру рисования и процедуру изменения
//			}

			public synchronized IProjConverter getCopyConverter() throws Exception
			{
				getDrawSize();//Установка в конвертер объема рисования
				return (IProjConverter) converter.createCopyConverter();
			}

			public void setCopyConverter(IProjConverter _converer) throws Exception
			{
				converter.setByConverter(_converer);
			}


		};
	}


	public void shiftPictureXY(int[] dXdY)
	{
		drawThread.shiftPictureXY(dXdY);
	}

	public boolean mapSwitch()
	{
		this.map_switch = !this.map_switch;
		synchronized (this)
		{
///			drawThread..buffimg = null;
		}
		repaint();
		return map_switch;
	}

	public void setRasterImageSwitch(boolean rastrImage_switch)
	{
//TODO		if (rasterLayer.setRasterImageSwitch(rastrImage_switch, this, currentP0, converter))
//			refresh();
	}


	public void projection()
	{

//		IMap map = Layer.getIMap();
//		Projections dialog = new Projections("Проекции", map);
//		dialog.pack();
//		dialog.setModal(true);
//		dialog.setVisible(true);
//		if (dialog.retKey != null)
//		{
//			try
//			{
//				IProjConverter l_converter = Layer.applyProjection(map, dialog.retKey, layers);
//				if (l_converter != null)
//				{
//					converter = l_converter;
//					setInitScale();
//					refresh();
//				}
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
	}

	protected void drawRule(Graphics graphics)
	{
		graphics.setColor(new Color(0x0));
		graphics.drawLine(10, 29, 330 + 10, 29);
		for (int k = 0; k <= 10; k++)
			graphics.drawLine((int) (33 * k) + 10, 23, (int) (33 * k) + 10, 36);
	}


	protected BufferedImage drawBackground(boolean istransparent) throws Exception
	{
		Point drwsize = getViewPort().getDrawSize();
		BufferedImage l_buffimg = new BufferedImage(drwsize.x,
				drwsize.y,
				BufferedImage.TYPE_INT_ARGB);

		Graphics graphics = l_buffimg.getGraphics();
		int backcolor = projectctx.getProjMetaInfo().getBackgroundColor();
		if (istransparent)
			backcolor &= 0x00FFFFFF;
		graphics.setColor(new Color(backcolor, true));
		graphics.fillRect(0, 0, l_buffimg.getWidth(), l_buffimg.getHeight());
		graphics.setColor(new Color(projectctx.getProjMetaInfo().getBoxColor()));
		graphics.drawRect(2, 2, l_buffimg.getWidth() - 5, l_buffimg.getHeight() - 5);
		return l_buffimg;
	}

	public void setAllowDraw(boolean allowDraw)
	{
		this.allowDraw = allowDraw;
	}

	boolean allowDraw = true;//Флаг показывающий что рисовать можно
	protected void paintComponent(Graphics g)
	{

		drawThread.start();
		if (allowDraw)
		{
			try
			{
				drawModulesImage();
				drawGeneratedImages(g);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
			callsuperpaint(g);
	}

	protected BufferedImage modbuffimage;//Изображение сегенрированное модулями
	protected void drawModulesImage()
			throws Exception
	{
		if (modbuffimage == null)
		{
			Point drwsize = getViewPort().getDrawSize();

			BufferedImage l_modbuffimage = new BufferedImage(drwsize.x,
					drwsize.y,
					BufferedImage.TYPE_INT_ARGB);
			for (IGuiModule drawmodul : drawmoduls)
				drawmodul.paintMe(l_modbuffimage.getGraphics());

			synchronized (this)
			{
				modbuffimage = l_modbuffimage;
			}
		}
	}


	protected void drawGeneratedImages(Graphics g) throws Exception
	{
		callsuperpaint(g);
		Point drwsize = getViewPort().getDrawSize();

		//Получили текущую структуру
		Pair<BufferedImage, int[]> pr = drawThread.getPictureXY();
		if (pr != null && pr.first != null && (pr.first.getWidth() != drwsize.x || pr.first.getHeight() != drwsize.y))
		{
			synchronized (this)
			{
				modbuffimage = null;
			}
			drawThread.setRefresh();
		}
		else if (pr != null && pr.first != null && mode == WK_MODE)
		{
			g.drawImage(pr.first, pr.second[0], pr.second[1], pr.first.getWidth(), pr.first.getHeight(), this);

			BufferedImage l_modbuffimage = null;
			synchronized (this)
			{
				if (modbuffimage != null)
					l_modbuffimage = modbuffimage;
			}
			if (l_modbuffimage!=null)
			{
				int[] dXdY = drawThread.getTotaldXdY();
				g.drawImage(l_modbuffimage, dXdY[0], dXdY[1], drwsize.x, drwsize.y, this);
			}
		}
	}

	protected void callsuperpaint(Graphics g)
	{
		super.paintComponent(g);
	}

	public void loadRasterManager(String bgdesc) throws Exception
	{
		//TODO rasterLayer = RasterLayer.loadRasterManager(bgdesc, this, converter, currentP0);
	}


	public boolean isLoadedRaster()
	{
		return false;////TODO rasterLayer != null && rasterLayer.isLoaded();
	}

	/**
	 * Установить слушателей мыши
	 *
	 * @param crdStatus	- строка состояния координат
	 * @param scaleStatus- строка состояния масштаба
	 */
	public void setPictureListeners(JLabel crdStatus, JLabel scaleStatus)
	{
		this.crdStatus = crdStatus;
		this.scaleStatus = scaleStatus;
		this.addMouseMotionListener(new MouseMotionListener()
		{
			public void mouseDragged(MouseEvent e)
			{
			}

			public void mouseMoved(MouseEvent e)
			{
				MPoint geoPoint = null;
				try
				{
					IProjContext project = getProjContext();
					List<ILayer> layers = project.getLayerList();
					if (layers != null && layers.size() != 0)
					{
						Point mousepnt = e.getPoint();
//						geoPoint = CrdConvertorsUtils.getRadGeoPntByDrawPnt(project, mousepnt, converter);
					}
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}

//				if (ViewPicturePanel2.this.crdStatus != null)
//					ViewPicturePanel2.this.crdStatus.setText(CrdConvertorsUtils.getCrdString(geoPoint));

			}
		});
		this.addMouseListener(new MouseListener()
		{

			public void mouseClicked(MouseEvent e)
			{
				ViewPicturePanel2.this.requestFocus();
			}

			public void mousePressed(MouseEvent e)
			{
			}

			public void mouseReleased(MouseEvent e)
			{
			}

			public void mouseEntered(MouseEvent e)
			{
			}

			public void mouseExited(MouseEvent e)
			{
			}
		});

	}

	protected String ObjName;

	public String getObjName()
	{
		return ObjName;
	}

	public Object[] init(Object... objs) throws Exception
	{
		for (Object obj : objs)
		{
			IParam attr=(IParam)obj;
			if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
				ObjName = (String) attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
				this.desc=(IXMLObjectDesc)attr.getValue();
			else
				init(obj);
		}
		return null;
	}

	public Object init(Object obj) throws Exception
	{
		IParam attr=(IParam)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.CONVERTER_TAGNAME))
			converter = (IProjConverter) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(KernelConst.PROJCTXT_TAGNAME))
			projectctx = (IProjContext) attr.getValue();
		return null;
	}

	public IObjectDesc getObjectDescriptor()
	{
		return desc;
	}

}