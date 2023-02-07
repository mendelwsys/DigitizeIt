package com.mwlib.app.plugins.digitizer;

import com.mwlib.app.storages.mem.DigitizedStorage;
import com.mwlib.app.storages.mem.IPathDefContainer;
import com.mwlib.ptrace.*;
import com.mwlib.utils.Enc;
import ru.ts.factory.IFactory;
import ru.ts.factory.IParam;
import ru.ts.res.ImgResources;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 02.01.14
 * Time: 21:49
 * com.mwlib.app.plugins.digitizer.DigitizerModule
 */
public class DigitizerModule_BU extends BaseInitAble implements IGuiModule
{
    public static final String MODULENAME = "DIGITIZER";
    public static final String menuName = Enc.get("$107");
    public static final String DIGITIZER = Enc.get("$108");
    public static final String PARAMETERS = Enc.get("$109");
    public static final String RESET = Enc.get("$110");

    protected IViewControl mainmodule;

    public String getMenuName()
    {
        return menuName;
    }


    private Utils u= new Utils();
    private PoTraceJ poTraceJ;
    public DigitizerModule_BU()
    {
        poTraceJ = new PoTraceJ();
    }

    public JMenu addMenu(JMenu inmenu) throws Exception
    {
        JMenuItem menuItem = new JMenuItem(DIGITIZER, KeyEvent.VK_O);
        inmenu.add(menuItem);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                   digitizer();
            }
        });

        menuItem = new JMenuItem(PARAMETERS, KeyEvent.VK_S);
        inmenu.add(menuItem);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                paramters();
            }
        });

        menuItem = new JMenuItem(RESET, KeyEvent.VK_R);
        inmenu.add(menuItem);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                reset();
            }
        });

        return inmenu;
    }

    protected void reset()
    {
        try
        {
            IProjContext proj = mainmodule.getProjContext();
            INodeStorage mainstor = (INodeStorage) proj.getStorage();
            Collection<INodeStorage> stors = mainstor.getChildStorages();

            DigitizedStorage digit=null;
            for (INodeStorage st : stors)
                if (st instanceof IPathDefContainer)
                {
                    digit=(DigitizedStorage)st;
                    break;
                }
            if (digit==null)
                throw new Exception("Can't find storage for raster");
            digit.init(new DefAttrImpl(DigitizedStorage.PO_TRACE_PATH,null));
            mainmodule.refresh(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    protected void digitizer()
    {
        try
        {
            IProjContext proj = mainmodule.getProjContext();

            INodeStorage mainstor = (INodeStorage) proj.getStorage();
            Collection<INodeStorage> stors = mainstor.getChildStorages();

            IRasterContainer stor=null;
            IPathDefContainer digit=null;
            for (INodeStorage st : stors) {
                if (st instanceof IRasterContainer)
                    stor=(IRasterContainer)st;
                else if (st instanceof IPathDefContainer)
                    digit=(DigitizedStorage)st;
                if (digit!=null && stor!=null)
                    break;
            }

            if (stor==null)
                throw new Exception("Can't find storage for raster");
            if (digit==null)
                throw new Exception("Can't find storage for raster");


            Pair<BindStruct,Integer> currentStruct = stor.getCurrentStruct();
            if (currentStruct!=null)
            {
                BindStruct bindStruct = currentStruct.getKey();
                if (bindStruct.flnames.length>0)
                {
                    Bitmap bmp = u.getBmpByImage(ImageIO.read(new File(bindStruct.pictdir +"/"+bindStruct.flnames[0][0])));
                    Param param = poTraceJ.createDefaultParams();

                    param.turdsize=4;

                    PathDef def = poTraceJ.trace(param, bmp);
                    digit.setPathDef(def);
                    mainmodule.refresh(null);
                }
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    protected void paramters()
    {

    }

    public JPopupMenu addPopUpMenu(JPopupMenu inmenu) throws Exception
    {
        return inmenu;
    }

    public void registerListeners(JComponent component) throws Exception
    {
    }

    public JToolBar addInToolBar(JToolBar systemtoolbar) throws Exception
    {
        systemtoolbar.addSeparator();
        {
            JButton button = new JButton();
            ImageIcon icon = ImgResources.getIconByName("images/class.png", "digitize");
            if (icon != null)
                button.setIcon(icon);
            button.setToolTipText(DIGITIZER);//TODO воспользоваться конвертором для имен
            button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    digitizer();
                }
            });
            button.setMargin(new Insets(0, 0, 0, 0));
            systemtoolbar.add(button);
        }
        {
            JButton button = new JButton();
            ImageIcon icon = ImgResources.getIconByName("images/cancel.png", "resetall");
            if (icon != null)
                button.setIcon(icon);
            button.setToolTipText(RESET);//TODO воспользоваться конвертором для имен
            button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    reset();
                }
            });
            button.setMargin(new Insets(0, 0, 0, 0));
            systemtoolbar.add(button);
        }

        return systemtoolbar;
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
