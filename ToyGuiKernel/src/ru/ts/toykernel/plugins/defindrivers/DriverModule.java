package ru.ts.toykernel.plugins.defindrivers;

import ru.ts.toykernel.plugins.IGuiModule;
import ru.ts.toykernel.gui.IViewControl;
import ru.ts.toykernel.gui.IViewPort;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.factory.IFactory;
import ru.ts.factory.IParam;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.converters.IProjConverter;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.toykernel.converters.IScaledConverterCtrl;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.plugins.ICommandBean;
import ru.ts.toykernel.plugins.IAnswerBean;
import ru.ts.toykernel.plugins.CommandBean;
import ru.ts.utils.gui.elems.IActionPerform;
import ru.ts.utils.gui.elems.AEventDriver;
import ru.ts.utils.gui.elems.PictureCtrl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Driver keyboard and mouse module
 */
public class DriverModule extends BaseInitAble implements IGuiModule
{

	public static final String MODULENAME = "MOUSEKEY_DRIVE";
    public static final String MOUSE_CURSOR_TYPE = "MOUSE_CURSOR";
    public static final String WHEEL_CTRL= "WHEEL_CTRL";

    public  static final int WHEEL_CTRL_DEF=0;
    public  static final int WHEEL_CTRL_INVERSE=1;
    public  static final int WHEEL_CTRL_NON=2;

    public int getWheelPolicy() {
        if (wheelPolicy==null)
            wheelPolicy=WHEEL_CTRL_DEF;
        return wheelPolicy;
    }

    public void setWheelPolicy(int wheelPolicy) {
        this.wheelPolicy = wheelPolicy;
    }

    private Integer wheelPolicy=WHEEL_CTRL_DEF;

	private IViewControl mainmodule;

	public DriverModule(IViewControl mainmodule)
	{
		this.mainmodule = mainmodule;
	}

	public DriverModule()
	{
	}


	protected IActionPerform actionEmpty = new IActionPerform()
	{
		public void actionPerformed(String fromState, EventObject event)
		{
		}
	};

	protected MRect smallRect;//Прямоугольник масштабирования в координатах рисования
	private IActionPerform actionSetP1Scale = new IActionPerform()
	{
		public void actionPerformed(String fromState, EventObject event)
		{
			try
			{
				if (event instanceof MouseEvent)
				{
//					ILinearConverter converter = mainmodule.getViewPort().getConverter();
					//TODO				if (mode != PicturePanel.OVERRASTER_MOVE)
					//					rasterLayer.terminateLoadRasters();

					Point drawpoint = ((MouseEvent) event).getPoint();
					smallRect = new MRect(drawpoint, drawpoint);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	};
	private IActionPerform actionMoveScale = new IActionPerform()
	{
		public void actionPerformed(String fromState, EventObject event)
		{
			try
			{
				if (event instanceof MouseEvent)
				{
					Point drawpoint = ((MouseEvent) event).getPoint();
					synchronized (DriverModule.this)
					{
						if (smallRect != null)
							smallRect = new MRect(smallRect.p1, new MPoint(drawpoint));
						mainmodule.refresh(new CommandBean(KernelConst.P_SKIPDRAWWM, null, 0, ""));//Передать команду спец. отрисовки
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	};
	private IActionPerform actionSetP2Scale = new IActionPerform()
	{
		public void actionPerformed(String fromState, EventObject event)
		{
			MRect l_smallRect;
			synchronized (DriverModule.this)
			{
				l_smallRect = smallRect;
			}

			if (l_smallRect != null)
			{
				try
				{
					Point size = mainmodule.getViewPort().getDrawSize();
					IProjConverter iProjConverter = mainmodule.getViewPort().getCopyConverter();
					IScaledConverterCtrl converter = iProjConverter.getAsScaledConverterCtrl();
					if (Math.abs(l_smallRect.p1.x - l_smallRect.p4.x) >= 30 && Math.abs(l_smallRect.p1.x - l_smallRect.p4.x) >= 30)
						converter.recalcScale(iProjConverter.getRectByDstRect(l_smallRect, null), new int[]{size.x, size.y});
					else
						smallRect = null;
					mainmodule.getViewPort().setCopyConverter(iProjConverter);
					mainmodule.refresh(null);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			synchronized (DriverModule.this)
			{
				smallRect = null;
			}

//TODO			if (mode != PicturePanel.OVERRASTER_MOVE)
//				rasterLayer.resetLoadedRasters(converter, currentP0);
		}
	};

	private Point oldmspntdraw;
	private Point newmspntdraw;

    private Integer cursorType;

    private IActionPerform actionSetForMove = new IActionPerform()
	{
		public void actionPerformed(String fromState, EventObject event)
		{
			if (event instanceof MouseEvent)
			{
				synchronized (DriverModule.this)
				{
					newmspntdraw = oldmspntdraw = ((MouseEvent) event).getPoint();
				}
                if (cursorType!=null)
                {
                    try
                    {
                        Cursor predefinedCursor = Cursor.getPredefinedCursor(cursorType);
                        mainmodule.getComponent().setCursor(predefinedCursor);
                    } catch (IllegalArgumentException e) {
                        //
                    }

                }
			}

		}
	};
	private IActionPerform actionMoveForMove = new IActionPerform()
	{
		public void actionPerformed(String fromState, EventObject event)
		{
			try
			{
				Point l_newmspntdraw = null;
				synchronized (DriverModule.this)
				{
					l_newmspntdraw = newmspntdraw;
				}

				if (l_newmspntdraw != null && event instanceof MouseEvent)
				{
					Point pnt = ((MouseEvent) event).getPoint();
					if (mainmodule.getProjContext().getLayerList().size() > 0)
					{
						synchronized (DriverModule.this)
						{

							mainmodule.shiftPictureXY(new int[]{pnt.x - newmspntdraw.x, pnt.y - newmspntdraw.y});
							newmspntdraw = pnt;
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	};
	private IActionPerform actionResetForMove = new IActionPerform()
	{
		public void actionPerformed(String fromState, EventObject event)
		{
			try
			{
                if (cursorType!=null)
                    mainmodule.getComponent().setCursor(Cursor.getDefaultCursor());
				synchronized (DriverModule.this)
				{
					smallRect = null;
				}
				double[] dXdY;
				synchronized (DriverModule.this)
				{
					dXdY = new double[]{oldmspntdraw.x - newmspntdraw.x, oldmspntdraw.y - newmspntdraw.y};
					newmspntdraw = oldmspntdraw = null;

				}
				IProjConverter converter = mainmodule.getViewPort().getCopyConverter();
				converter.getAsShiftConverter().recalcBindPointByDrawDxDy(dXdY);
				mainmodule.getViewPort().setCopyConverter(converter);
				mainmodule.refresh(null);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	};

	protected IActionPerform[] actions_scale = {
			actionSetP1Scale,
			actionMoveScale,
			actionSetP2Scale
	};
	protected IActionPerform[] actions_move = {
			actionSetForMove,
			actionMoveForMove,
			actionResetForMove
	};

	protected IActionPerform[] actions_default = actions_move;
	protected AEventDriver currentDriver = new ViewEventDriver();

	public Object init(Object obj) throws Exception
	{
		IParam attr=(IParam)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.VIEWCNTRL_TAGNAME))
			this.mainmodule = (IViewControl) attr.getValue();
        else if (attr.getName().equalsIgnoreCase(WHEEL_CTRL))
        {
            String wheelTypeIn = (String)attr.getValue();
            wheelPolicy=getReflectIntOption(wheelTypeIn, this.getClass());
        }
        else if (attr.getName().equalsIgnoreCase(MOUSE_CURSOR_TYPE))
        {
            String cursorTypeIn = (String)attr.getValue();
            Class<Cursor> cursorClass = Cursor.class;
            cursorType=getReflectIntOption(cursorTypeIn, cursorClass);
        }
		return null;
	}

    private Integer getReflectIntOption(String stringOption, Class clazz) {
        try {
            Field field= clazz.getField(stringOption);
            if (field!=null)
                    return field.getInt(null);

        }
        catch (RuntimeException e) {
            e.printStackTrace();//TODO Выдавать предупреждение о не верном аргументе
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


    public class ViewEventDriver
			extends AEventDriver
	{
		protected PictureCtrl[] pictureCtrls; //Автоматы для управления манипулированием кнопками мыши
		protected PictureCtrl currPictureCtrl;

		public ViewEventDriver()
		{
			pictureCtrls = new PictureCtrl[]
					{
							new PictureCtrl(
									actions_default), //Автомат для управления манипулированием экраном по кнопке B1
							null,
							null,
					};
		}

		public void mousePressed(MouseEvent event)
		{

			if (!event.isShiftDown())
			{
				if (event.isControlDown())
					pictureCtrls[0] = new PictureCtrl(actions_scale);
				else
				{
					oldmspntdraw = null;
					smallRect = null;
					pictureCtrls[0] = new PictureCtrl(actions_default);
				}
				if (event.getButton() == MouseEvent.BUTTON1)
					currPictureCtrl = pictureCtrls[0];
				else if (event.getButton() == MouseEvent.BUTTON3)
					currPictureCtrl = pictureCtrls[2];
				else
					currPictureCtrl = null;
				super.mousePressed(event);
			}
			else
				currPictureCtrl = null;
		}

		public void setIndicesByLayers()
		{
		}

		public void keyPressed(KeyEvent e)
		{
			switch (e.getKeyCode())
			{
				case 16://Shift
					synchronized (DriverModule.this)
					{
						currPictureCtrl = null;
					}
					break;
				default:
					CommonProcessor(e);
			}
		}

		private void CommonProcessor(KeyEvent e)
		{
			try
			{
				IProjConverter converter = mainmodule.getViewPort().getCopyConverter();
				IProjContext project = mainmodule.getProjContext();

//				System.out.println("e.keyChar() = " + e.getKeyChar());
				switch (e.getKeyCode())
				{
					//Операции по скролингу картинки в окне
					case 39:
						if (project.getLayerList().size() > 0)
							converter.getAsShiftConverter().recalcBindPointByDrawDxDy(new double[]{50, 0});
						mainmodule.getViewPort().setCopyConverter(converter);
						mainmodule.refresh(null);
						break;
					case 37:
						if (project.getLayerList().size() > 0)
							converter.getAsShiftConverter().recalcBindPointByDrawDxDy(new double[]{-50, 0});
						mainmodule.getViewPort().setCopyConverter(converter);
						mainmodule.refresh(null);
						break;
					case 38:
						if (project.getLayerList().size() > 0)
							converter.getAsShiftConverter().recalcBindPointByDrawDxDy(new double[]{0, 50});
						mainmodule.getViewPort().setCopyConverter(converter);
						mainmodule.refresh(null);
						break;
					case 40:
						if (project.getLayerList().size() > 0)
							converter.getAsShiftConverter().recalcBindPointByDrawDxDy(new double[]{0, -50});
						mainmodule.getViewPort().setCopyConverter(converter);
						mainmodule.refresh(null);
						break;
						//Операции над списком шаблонов
				}
				if (e.getKeyChar() == '+')
				{
					converter.getAsScaledConverterCtrl().increaseMap(1.3);
					mainmodule.getViewPort().setCopyConverter(converter);
					mainmodule.refresh(null);
				}
				else if (e.getKeyChar() == '-')
				{
					converter.getAsScaledConverterCtrl().decreaseMap(1.3);
					mainmodule.getViewPort().setCopyConverter(converter);
					mainmodule.refresh(null);
				}
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}

		public void keyReleased(KeyEvent e)
		{
			switch (e.getKeyCode())
			{
				case 17: //Cntrl
					synchronized (DriverModule.this)
					{
						oldmspntdraw = null;
						smallRect = null;
						pictureCtrls[0] = new PictureCtrl(actions_default);
					}
					break;
			}
		}


		public void mouseMoved(MouseEvent e)
		{
		}

		public PictureCtrl getPictureCtrl()
		{
			return currPictureCtrl;
		}

		public void repaintPicture()
		{
			mainmodule.getComponent().repaint();
		}

		public void mouseClicked(MouseEvent event)
		{
			if (!event.isShiftDown() && !event.isControlDown() && !event.isAltDown())
			{
//				int clickcount = event.getClickCount();
				if (event.getButton() == MouseEvent.BUTTON1)
				{
				}
				else if (event.getButton() == MouseEvent.BUTTON3)
				{
//					JPopupMenu popupmenu = buildPopUp();
//					if (popupmenu != null)
//					{ //Показываем всплывающее меню
//						currPictureCtrl = null;
//						popupmenu.show(ViewPicturePanel.this, event.getX(), event.getY());
//					}
				}
			}
		}
	}

	protected void drawRect(Graphics g, int r_color, int f_color, MRect rect) throws Exception
	{
		g.setColor(new Color(r_color, true));
//		IProjConverter converter = mainmodule.getViewPort().getConverter();
//		Point.Double p11 = converter.getDrawPointByLinearPointD(rect.p1);
//		Point.Double p44 = converter.getDrawPointByLinearPointD(rect.p4);

//		Point p1 = new Point((int) p11.x, (int) p11.y);
//		Point p4 = new Point((int) p44.x, (int) p44.y);

		Point p1 = new Point((int) Math.round(rect.p1.x), (int) Math.round(rect.p1.y));
		Point p4 = new Point((int) Math.round(rect.p4.x), (int) Math.round(rect.p4.y));


		p1 = new Point(Math.min(p1.x, p4.x), Math.min(p1.y, p4.y));
		p4 = new Point(Math.max(p1.x, p4.x), Math.max(p1.y, p4.y));

		g.drawRect(p1.x, p1.y, (p4.x - p1.x), (p4.y - p1.y));

		g.setColor(new Color(f_color, true));

		g.fillRect(p1.x, p1.y, (p4.x - p1.x), (p4.y - p1.y));

//		g.drawLine(p1.x, p4.y, p1.x, p1.y);
//		g.drawLine(p4.x, p1.y, p1.x, p1.y);
//
//		g.drawLine(p4.x, p1.y, p4.x, p4.y);
//		g.drawLine(p1.x, p4.y, p4.x, p4.y);


	}

	public String getMenuName()
	{
		return null;//Нет меню
	}

	public JMenu addMenu(JMenu inmenu) throws Exception
	{
		return inmenu;
	}

	public JPopupMenu addPopUpMenu(JPopupMenu inmenu) throws Exception
	{
		return inmenu;
	}

	public void registerListeners(JComponent component) throws Exception
	{
		component.addMouseWheelListener(new MouseWheelListener()
		{

			public void mouseWheelMoved(MouseWheelEvent e)
			{
                if (wheelPolicy ==null)
                    wheelPolicy=0;

                if (wheelPolicy ==WHEEL_CTRL_NON)
                    return;

				try
				{
					int cnt = e.getWheelRotation();

					IViewPort port = mainmodule.getViewPort();
					IProjConverter iProjConverter = port.getCopyConverter();
					IScaledConverterCtrl converter = iProjConverter.getAsScaledConverterCtrl();
                    if (wheelPolicy ==WHEEL_CTRL_INVERSE)
                        cnt*=-1;
					if (cnt < 0)
					{
						converter.increaseMap(1.05);
						port.setCopyConverter(iProjConverter);
						mainmodule.refresh(null);
					}
					else
					{
						converter.decreaseMap(1.05);
						port.setCopyConverter(iProjConverter);
						mainmodule.refresh(null);
					}
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});

		component.addMouseListener(currentDriver);
//Добавить слушателя событий передвижения крысы, для того что бы рисовать области интересов
		component.addMouseMotionListener(currentDriver);
		component.addKeyListener(currentDriver);
	}

	public JToolBar addInToolBar(JToolBar systemtoolbar)
	{
		return systemtoolbar;
	}

	public void paintMe(Graphics g) throws Exception
	{
		synchronized (DriverModule.this)
		{
			if (smallRect != null)
				drawRect(g, 0xFFFF0000, 0x88AA1010, smallRect);
		}
	}

	public void registerNameConverter(INameConverter nameConverter, IFactory<INameConverter> factory) throws Exception
	{
	}

	public void unload()
	{
	}

	public String getModuleName()
	{
		return MODULENAME;
	}

	public IAnswerBean execute(ICommandBean cmd)
	{
		throw new UnsupportedOperationException();
	}


}
