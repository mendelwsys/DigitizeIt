package ru.ts.toykernel.gui.panels;

import com.mwlib.utils.Enc;
import ru.ts.gisutils.datamine.ProjBaseConstatnts;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;

import javax.swing.*;

import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.converters.*;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.gui.IApplication;
import ru.ts.toykernel.plugins.IGuiModule;
import ru.ts.toykernel.gui.IViewPort;
import ru.ts.xml.IXMLObjectDesc;
import ru.ts.factory.IObjectDesc;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;



/**
 * Panel of Map Viewer
 */
public class ViewPicturePanel extends BasePicture
{


    protected IProjContext projectctx;//Контекст проекта
	protected IProjConverter converter;//Конвертер
	private IApplication application;//application of
	private ConvB64Initializer initializer;//initilizer
	private List<IGuiModule> drawmoduls=new LinkedList<IGuiModule>();

	public static final int WK_MODE = 0x1;

	protected JLabel crdStatus;//Строка статуса для отображения текущих координат мыши
	protected JLabel scaleStatus;//Строка статуса для отображения текущего масштаба
	protected int mode = WK_MODE;

	protected boolean google_switch = false;//Показвать гугловый растр
	protected boolean map_switch = true;//Показывать карту


	protected MRect wholerect;//Охватывающий все точки прямоугольник

	protected BufferedImage buffimg;//Буфер векторной графики
	protected BufferedImage buffimglegend;//Буффер легенд
	protected IXMLObjectDesc desc;


	public ViewPicturePanel()
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
	 * @param projectctx - контекст проекта
	 * @param initializer - инициализатор конвертера
	 * @param drawmoduls - модули приложения
	 * @throws Exception -
	 */
	public ViewPicturePanel(IProjContext projectctx, ConvB64Initializer initializer,List<IGuiModule> drawmoduls) throws Exception
	{
		this();
		this.initializer=initializer;
		if (initializer!=null && initializer.getB_converter() != null)
		{
			converter = (IProjConverter) initializer.createByTypeName(initializer.getS_convertertype());
			converter.initByBase64Point(initializer.getB_converter());
			converter.getAsShiftConverter().setBindP0(MPoint.getByBase64Point(initializer.getB_currentP0()));

//++DEBUG Проверка нового конвертора
//			IScaledConverter scaledConv = converter.getAsScaledConverter();
//			IShiftConverter shiftconverter = converter.getAsShiftConverter();
//			MPoint pnt0 = shiftconverter.getBindP0();
//			MPoint scale = scaledConv.increaseMap(1.0);
//			MPoint drawpnt = new MPoint(pnt0.x * scale.x, pnt0.y  * scale.y);
//			CrdConverterFactory.LinearConverterRSS l_converter = new CrdConverterFactory.LinearConverterRSS
//					(
//							converter.getAsRotateConverter().getRotMatrix(),
//							scaledConv.increaseMap(1.0),
//							drawpnt
//					);
//
//			MRect drawrect = new MRect(new MPoint(),
//					new MPoint(1000, 1000));
//			MRect proj_rect = converter.getRectByDstRect(drawrect,null);
//			MRect proj_rect2 = l_converter.getRectByDstRect(drawrect,null);

			System.out.println("");
//--DEBUG
		}
		this.drawmoduls = drawmoduls;
		this.initializer=initializer;
		setProjectContext(projectctx,converter,false);
	}

	public ViewPicturePanel(IProjContext projectctx,IProjConverter converter, List<IGuiModule> drawmoduls) throws Exception
	{
		this();
		this.drawmoduls = drawmoduls;
		setProjectContext(projectctx,converter,false);
	}

	public void setProjectContext(IProjContext project, IProjConverter converter,boolean isinit)
			throws Exception
	{
		this.projectctx =project;
		if (isinit ||  converter==null)
			setInitScale(converter);
		else
			this.converter=converter;
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
				if (converter==null)
					this.converter = (IProjConverter) new CrdConverterFactory().createByTypeName(CrdConverterFactory.LinearConverterAB.LINEARPROJAB);
				int[] sz = {1000, 1000};//Грубая оценка масштаба
				converter.getAsShiftConverter().setViewSize(new MPoint(sz[0],sz[1]));
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

		if (punitonpixel.x!=punitonpixel.y)
			throw new UnsupportedOperationException("Can't show Meters on pixel scale by X and Y not equals");

		double unitonpixel=punitonpixel.x;
		double deve = Math.log(10);
		int pw10 = (int) (Math.log(unitonpixel) / deve);
		if (pw10 < 0)
			pw10 -= 1;

		double mant = unitonpixel;
		if (Math.abs(pw10) > 3)
			mant = unitonpixel / Math.pow(10, pw10);
		if (scaleStatus != null)
		{

			scaleStatus.setText(Enc.get("$186") +
					String.valueOf(
							((int) (mant * 1000)) / 1000.0) + ((Math.abs(pw10) > 3) ? ("E" + (pw10)) : "")
					+
                    " "
                    +
					ProjBaseConstatnts.getNameUnitsByUnitsName(projectctx.getProjMetaInfo().getS_MapUnitsName()) + Enc.get("$187")
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
			if (arg==null)
			{
				dXdY[0]=dXdY[1]=0;
				buffimg = null;
			}
		}
		try
		{
			showMetersOnPixel();
		} catch (Exception e)
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
			public Point getDrawSize() throws Exception
			{
				Dimension dm = getSize();
				Point rv = new Point((int) Math.ceil(dm.getWidth()), (int) Math.ceil(dm.getHeight()));
				converter.getAsShiftConverter().setViewSize(new MPoint(rv));
				return rv;
			}

			public IProjConverter getCopyConverter()
			{
				return (IProjConverter) converter.createCopyConverter();
			}

			public void setCopyConverter(IProjConverter _converer) throws Exception
			{
//			converter= (IProjConverter) _converer.createCopyConverter();
				converter.setByConverter(_converer);
			}

		};
	}

	protected int dXdY[]={0,0};
	public void shiftPictureXY(int[] dXdY)
	{
		synchronized (ViewPicturePanel.this)
		{
			this.dXdY[0]+=dXdY[0];
			this.dXdY[1]+=dXdY[1];
		}
	}

	public boolean mapSwitch()
	{
		this.map_switch = !this.map_switch;
		synchronized (this)
		{
			buffimg = null;
		}
		repaint();
		return map_switch;
	}

	protected void drawVectorImage(BufferedImage buffimg) throws Exception
	{

		long tm=System.currentTimeMillis();

		Graphics graphics = buffimg.getGraphics();

        List<ILayer> layers = projectctx.getLayerList();
		int[] rv=new int[]{0,0,0};
		for (ILayer layer : layers)
		{
			int[] rvv = layer.paintLayer(graphics, getViewPort());
			for (int i = 0; i < rv.length; i++)
				rv[i]+= rvv[i];
		}
		System.out.print("Picture panel tm:"+(System.currentTimeMillis()-tm)+" ");
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

		System.out.print("pnts = " + rv[0]+" ");
		System.out.print("lines = " + rv[1]+" ");
		System.out.println("poly = " + rv[2]);

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
		int backcolor= projectctx.getProjMetaInfo().getBackgroundColor();
		if (istransparent)
			backcolor&=0x00FFFFFF;
		graphics.setColor(new Color(backcolor,true));
        graphics.fillRect(0, 0, l_buffimg.getWidth(), l_buffimg.getHeight());
		graphics.setColor(new Color(projectctx.getProjMetaInfo().getBoxColor()));
		graphics.drawRect(2, 2, l_buffimg.getWidth() - 5, l_buffimg.getHeight() - 5);
		return l_buffimg;
	}

	public void setAllowDraw(boolean allowDraw)
	{
		this.allowDraw = allowDraw;
	}

	boolean allowDraw=true;

	protected void paintComponent(Graphics g)
	{

		if (allowDraw)
		{
				try
				{
					drawVectorMap(g);
					for (IGuiModule drawmodul : drawmoduls)
						drawmodul.paintMe(g);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		}
		else
			callsuperpaint(g);
	}


	protected void drawVectorMap(Graphics g) throws Exception
	{
		callsuperpaint(g);
		Point drwsize = getViewPort().getDrawSize();

		BufferedImage l_buffimg = null;
		int[] dXY = new int[]{0, 0};
		synchronized (this)
		{
			if (buffimg!=null && (buffimg.getWidth()!= drwsize.x || buffimg.getHeight()!=drwsize.y))
				buffimg=null;//Перерисуем
			l_buffimg=buffimg;
			dXY[0]=this.dXdY[0];
			dXY[1]=this.dXdY[1];
		}


		if (l_buffimg == null)
		{
            List<ILayer> layers = projectctx.getLayerList();
            if (layers.size() != 0)
			{
				if (map_switch)
				{
					try
					{

						if (l_buffimg == null)//Нужно проверять из за того что может быть нарисована уже подложка гуглом или растром
							l_buffimg = drawBackground(false);
						drawVectorImage(l_buffimg);
//						//++DEBUG
//							long tm= System.currentTimeMillis();
//								ByteArrayOutputStream bos = new ByteArrayOutputStream();
//								ImageIO.write(l_buffimg,"PNG", bos);
//								bos.flush();
//								bos.close();
//								byte[] bt=bos.toByteArray();
//							System.out.println("PNG tm:"+(System.currentTimeMillis()-tm));
//
//							FileOutputStream fos = new FileOutputStream("D:\\tmp.png");
//							fos.write(bt);
//							fos.close();
//						//--DEBUG
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		if (l_buffimg != null)
		{
			if (mode == WK_MODE)
			{
				g.drawImage(l_buffimg, dXY[0], dXY[1],l_buffimg.getWidth(),l_buffimg.getHeight(), this);
			}
		}


		synchronized (this)
		{
			if (l_buffimg != null)
				buffimg = l_buffimg;
		}
	}

	protected void callsuperpaint(Graphics g)
	{
		super.paintComponent(g);
	}

	/**
	 * Установить слушателей мыши
	 *
	 * @param crdStatus - строка состояния координат
	 * @param scaleStatus- строка состояния масштаба
	 */
	public void setPictureListeners(JLabel crdStatus,JLabel scaleStatus)
	{
		this.crdStatus = crdStatus;
		this.scaleStatus=scaleStatus;
		this.addMouseMotionListener(new MouseMotionListener()
		{
			public void mouseDragged(MouseEvent e)
			{
			}
			public void mouseMoved(MouseEvent e)
			{
				MPoint radGeoPoint = null;
				try
				{
					IProjContext project = getProjContext();
					java.util.List<ILayer> layers = project.getLayerList();
					if (layers != null && layers.size() != 0)
					{
						Point mousepnt = e.getPoint();
//						radGeoPoint = CrdConvertorsUtils.getRadGeoPntByDrawPnt(project, mousepnt, converter);
					}
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
//				if (ViewPicturePanel.this.crdStatus!=null)
//					ViewPicturePanel.this.crdStatus.setText(CrdConvertorsUtils.getCrdString(radGeoPoint));
			}
		});
		this.addMouseListener(new MouseListener()
		{

			public void mouseClicked(MouseEvent e)
			{
				ViewPicturePanel.this.requestFocus();
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
			IDefAttr attr=(IDefAttr)obj;
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
		IDefAttr attr=(IDefAttr)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.CONVERTER_TAGNAME))
			converter=(IProjConverter)attr.getValue();
		else if (attr.getName().equalsIgnoreCase(KernelConst.PROJCTXT_TAGNAME))
			projectctx=(IProjContext)attr.getValue();
		return null;
	}

	public IObjectDesc getObjectDescriptor()
	{
		return desc;
	}
}
