package com.mwlib.app.plugins.cselector;

import com.mwlib.utils.Enc;
import com.mwlib.utils.raster.PartialImageReader;
import ru.ts.factory.IFactory;
import ru.ts.factory.IParam;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.res.ImgResources;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.gui.IViewControl;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.plugins.IAnswerBean;
import ru.ts.toykernel.plugins.ICommandBean;
import ru.ts.toykernel.plugins.IGuiModule;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.storages.raster.BindStruct;
import ru.ts.toykernel.storages.raster.IRasterContainer;
import ru.ts.utils.data.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 *
 * com.mwlib.app.plugins.cselector.CSelectorModule
 */
public class CSelectorModule extends BaseInitAble implements IGuiModule
{
    public static final String MODULENAME = "CSELECTMODULE";

      private static String MENU_CAPTION_ON = Enc.get("$81");
      private static String MENU_CAPTION_OFF = Enc.get("$82");

      private static String HINT_CAPTION_ON = MENU_CAPTION_ON;
      private static String HINT_CAPTION_OFF = MENU_CAPTION_OFF;


    protected IViewControl mainmodule;

	//-------------------------------------------------------------------------------------------//
	public static final int NON_SELECT = 0;
	//-------------------------------------------------------------------------------------------//
    public static final int SELECT_BY_MOUSE = 1;
	//-------------------------------------------------------------------------------------------//
    private int mode = NON_SELECT;//Режим выбора цвета пикселя

//    private BufferedImage raster;

    private BufferedImage imgCursor;
    private JButton bCursor;

    private BufferedImage imgSelected;
    private JButton bSelected;
    private JMenuItem moduleMenu;

    //private int rcolor;

    public CSelectorModule()
	{
	}

	public CSelectorModule(IViewControl mainmodule)
	{
		this.mainmodule = mainmodule;
	}

	public String getMenuName()
	{
		return Enc.get("$83");
	}

	public JMenu addMenu(JMenu inmenu) throws Exception
	{
        MENU_CAPTION_ON = Enc.get("$81");
        if (inmenu.getItemCount()>0)
            inmenu.addSeparator();
        {
            moduleMenu = new JMenuItem(MENU_CAPTION_ON, KeyEvent.VK_S);
            inmenu.add(moduleMenu);
            moduleMenu.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                        switchOnOffSelection();
                }
            });
        }
		return inmenu;
	}

//    private PartialImageReader pr;
//    private Point pt;

    IRasterContainer stor;

	protected void colorSelect()  throws Exception
    {

        MENU_CAPTION_OFF = Enc.get("$82");

        resetRReader();

        IProjContext proj = mainmodule.getProjContext();
        INodeStorage mainstor = (INodeStorage) proj.getStorage();
        Collection<INodeStorage> stors = mainstor.getChildStorages();

//        IRasterContainer stor=null;
        for (INodeStorage st : stors) {
            if (st instanceof IRasterContainer)
            {
                stor=(IRasterContainer)st;
                break;
            }
        }

        if (stor==null)
            throw new Exception("Can't find storage for raster");


//        Pair<BindStruct,Integer> currentStruct = stor.getCurrentStruct();
//        if (currentStruct!=null)
//        {
//            BindStruct bindStruct = currentStruct.getKey();
//            if (bindStruct.flnames.length>0)
//            {
//                File raster=new File(bindStruct.pictdir + "/" + bindStruct.flnames[0][0]);
//                String flname = raster.getName();
//                String extensionName = flname.substring(flname.lastIndexOf('.') + 1);
//                InputStream inputStream = new FileInputStream(raster);
//
//
//
//                pr = new PartialImageReader(inputStream,extensionName);
//                pt = pr.getImageSize();
//                if (pt.x<=0 || pt.y<=0)
//                {
//                    pr.free();
//                    pr=null;
//                    pt=null;
//                }
//                else
//                {
//                    pr.getImageByRectangle(new Rectangle(0,0,1,1));
//                    mainmodule.refresh(null);
//                }
//            }
//        }


//        if (pr!=null)
        {
            mode=SELECT_BY_MOUSE;
            bCursor.setEnabled(true);
            moduleMenu.setText(MENU_CAPTION_OFF);
        }
    }

    private void resetRReader()
    {
//        if (pr!=null)
//        {
//            pr.free();
//            pr=null;
//            pt=null;
//        }
    }


//    protected boolean isRasterChanged() throws Exception
//    {
//        Pair<BindStruct,Integer> currentStruct = stor.getCurrentStruct();
//        if (currentStruct!=null)
//        {
//            BindStruct bindStruct = currentStruct.getKey();
//            if (bindStruct.flnames.length>0)
//            {
//                String fname=bindStruct.pictdir + "/" + bindStruct.flnames[0][0]);
//            }
//        }
//    }

	public JPopupMenu addPopUpMenu(JPopupMenu inmenu) throws Exception
	{
        MenuElement[] subElements = inmenu.getSubElements();
        if (subElements !=null && subElements.length>0)
            inmenu.addSeparator();

		return inmenu;
	}

	public void registerListeners(JComponent component) throws Exception
	{

        component.addPropertyChangeListener("RESETRASTER",new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                resetModule();
            }
        });
        component.addMouseMotionListener(new MouseMotionListener()
        {

            public void mouseDragged(MouseEvent e)
            {
            }

            public void mouseMoved(MouseEvent e)
            {
                try {
                    if (!e.isConsumed() && mode==SELECT_BY_MOUSE && stor!=null)
                    {
                        Point drwPnt=e.getPoint();

                        int rColor;
                        ILinearConverter linearConverter = mainmodule.getViewPort().getCopyConverter();
                        MPoint rPnt = linearConverter.getPointByDstPoint(drwPnt);
                        try
                        {
                            MPoint pt=stor.getImageSize();

                            if (rPnt.getX()>=0 && rPnt.getX()<pt.getX() &&
                                rPnt.getY()>=0 && rPnt.getY()<pt.getY()
                                )
                            {
                                BufferedImage raster = stor.getImageByRectangle(new Rectangle((int) rPnt.getX(), (int) rPnt.getY(),
                                        (int) Math.min(pt.getX() - rPnt.getX(), 10),
                                        (int) Math.min(pt.getY() - rPnt.getY(), 10)
                                ));
                                //rColor=raster.getRGB((int)rPnt.getX(),(int)rPnt.getY());
                                rColor=raster.getRGB(0,0);
                            }
                            else
                                rColor=0x0;
                        } catch (ArrayIndexOutOfBoundsException e1)
                        {
                            rColor=0x0;//Skip the exseption
                        }

                        Graphics g = imgCursor.getGraphics();
                        g.setColor(new Color(rColor));
                        g.fillRect(0, 0, imgCursor.getWidth(), imgCursor.getHeight());
                        bCursor.repaint();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        component.addMouseListener(new MouseListener()
        {

            public void mouseClicked(MouseEvent e)
            {
                if (!e.isConsumed() && mode==SELECT_BY_MOUSE)
                {
                    setSelectedRGB(getCurrentRGB());
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });

	}

    public int getCurrentRGB() {
        return imgCursor.getRGB(imgCursor.getWidth()/2,imgCursor.getHeight()/2);
    }

    public int getSelectedRGB() {
        return imgSelected.getRGB(imgSelected.getWidth()/2,imgSelected.getHeight()/2);
    }


    public Icon getSelectedIcon() {
        return bSelected.getIcon();
    }


    public void setSelectedRGB(int rColor) {
        Graphics g = imgSelected.getGraphics();
        g.setColor(new Color(rColor));
        g.fillRect(0, 0, imgSelected.getWidth(), imgSelected.getHeight());
        bSelected.repaint();
    }




    public JToolBar addInToolBar(JToolBar systemtoolbar) throws Exception
	{

       MENU_CAPTION_ON = Enc.get("$81");
       MENU_CAPTION_OFF = Enc.get("$82");

       HINT_CAPTION_ON = MENU_CAPTION_ON;
       HINT_CAPTION_OFF = MENU_CAPTION_OFF;


        systemtoolbar.addSeparator();

        bCursor = new JButton();
        int w=20,h=20;
        ImageIcon icon = ImgResources.getIconByName("images/deploy.png", "Sample");
        if (icon != null)
        {
            w=icon.getIconWidth();
            h = icon.getIconHeight();
        }

        {
            bCursor.setIcon(new ImageIcon(imgCursor = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)));
            Graphics graphics = imgCursor.getGraphics();
            graphics.setColor(new Color(0x000000));
            graphics.fillRect(0,0,w,h);
            bCursor.setToolTipText(HINT_CAPTION_OFF);//TODO воспользоваться конвертором для имен
            bCursor.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    switchOnOffSelection();
                }
            });
            bCursor.setMargin(new Insets(0, 0, 0, 0));
            bCursor.setEnabled(false);
            systemtoolbar.add(bCursor);

        }

        {
            bSelected = new JButton();
            bSelected.setIcon(new ImageIcon(imgSelected = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)));
            Graphics graphics = imgSelected.getGraphics();
            graphics.setColor(new Color(0xFF0000));
            graphics.fillRect(0,0,w,h);
            bSelected.setToolTipText(HINT_CAPTION_ON);//TODO воспользоваться конвертором для имен
            bSelected.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    try {
                        colorSelect();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
            bSelected.setMargin(new Insets(0, 0, 0, 0));
            systemtoolbar.add(bSelected);
        }
        return systemtoolbar;
	}

    private void switchOnOffSelection()
    {
        try
        {
            if (mode==SELECT_BY_MOUSE)
            {
                resetModule();
            }
            else
                colorSelect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetModule()
    {
        MENU_CAPTION_ON = Enc.get("$81");
        resetRReader();
        mode=NON_SELECT;
        bCursor.setEnabled(false);
        moduleMenu.setText(MENU_CAPTION_ON);
    }


    public void paintMe(Graphics g)
	{
	}

	public void registerNameConverter(INameConverter nameConverter, IFactory<INameConverter> factory)
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


	public Object init(Object obj) throws Exception
	{
		IParam attr=(IParam)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.VIEWCNTRL_TAGNAME))
			this.mainmodule = (IViewControl) attr.getValue();

		return null;
	}
}
