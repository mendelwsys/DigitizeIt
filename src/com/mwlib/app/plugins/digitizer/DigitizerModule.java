package com.mwlib.app.plugins.digitizer;

import com.mwlib.app.plugins.common.ShowMessagesUtils;
import com.mwlib.app.plugins.cselector.CSelectorModule;
import com.mwlib.app.plugins.median.MedianProcessor;
import com.mwlib.app.storages.mem.DigitizedStorage;
import com.mwlib.app.storages.mem.IPathDefContainer;
import com.mwlib.app.utils.PathUtils;
import com.mwlib.utils.Enc;
import reclass.IProgressObserver;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;
import ru.ts.toykernel.pcntxt.gui.defmetainfo.MainformMonitor;
import ru.ts.toykernel.storages.raster.BindStruct;
import ru.ts.toykernel.storages.raster.IRasterContainer;
import com.mwlib.ptrace.*;
import ru.ts.res.ImgResources;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.plugins.IAnswerBean;
import ru.ts.toykernel.plugins.ICommandBean;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.xml.def.ParamDescriptor;
import ru.ts.utils.data.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 02.01.14
 * Time: 21:49
 * com.mwlib.app.plugins.digitizer.DigitizerModule
 */
public class DigitizerModule
        extends CSelectorModule
{
    public static final String MODULENAME = "DIGITIZER";
//    public static final String menuName = Enc.get("$88");
    private static String DIGITIZER = Enc.get("$89");
    private static String PARAMETERS = Enc.get("$90");
    private static String RESET = Enc.get("$91");
    private static String ERR_CAPTION = Enc.get("$92");

    public String getMenuName()
    {
        return Enc.get("$88");
//        return menuName;
    }

      interface IProgressViewer extends IProgressObserver,com.mwlib.ptrace.IProgressObserver
      {
          void setBitCount(int cnt);

      }

//    IProgressObserver progressObserver = new IProgressObserver()
//    {
//        public void showProgress(float value) {
//            //To change body of implemented methods use File | Settings | File Templates.
//        }
//    };
//
//    com.mwlib.ptrace.IProgressObserver progressObserver2 = new com.mwlib.ptrace.IProgressObserver()
//    {
//        public void setTraceProgress(float value)
//        {
//
//        }
//    };

    protected Utils u= new Utils();
    private PoTraceJ poTraceJ;
    private ParamEx param;

    public ParamEx getParam()
    {
        return param;
    }

    public  void setParam(ParamEx param)
    {
        this.param=param;
    }

    public DigitizerModule()
    {
        poTraceJ = new PoTraceJ();
        param = new ParamEx(poTraceJ.createDefaultParams());
    }

    public JMenu addMenu(JMenu inmenu) throws Exception
    {
        DIGITIZER = Enc.get("$89");
        PARAMETERS = Enc.get("$90");
        RESET = Enc.get("$91");

        JMenuItem menuItem = new JMenuItem(DIGITIZER, KeyEvent.VK_O);
        inmenu.add(menuItem);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                   digitizer(getParam());
            }
        });

        menuItem = new JMenuItem(RESET, KeyEvent.VK_R);
        inmenu.add(menuItem);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                reset(true);
            }
        });

        inmenu = super.addMenu(inmenu);


        menuItem = new JMenuItem(PARAMETERS, KeyEvent.VK_S);
        inmenu.add(menuItem);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                paramters();
            }
        });

        return inmenu;
    }

    public IObjectDesc getObjectDescriptor()
    {
        if (getParam()!=null)
        {
            ParamDescriptor paramDescriptor =new ParamDescriptor(desc);
            List<IParam> params = paramDescriptor.getParams();
            for (int i = 0; i < params.size();)
            {
                IParam iParam = params.get(i);
                if (ParamEx.isAcceptedTags(iParam.getName()))
                    params.remove(i);
                else
                    i++;
            }
            params.addAll(getParam().getDescParameters());
            paramDescriptor.setParams(params);
            return paramDescriptor;
        }
        return desc;
    }


    protected void reset(boolean refresh)
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

            IObjectDesc digitDesc = digit.getObjectDescriptor();
            List<IParam> params = digitDesc.getParams();
            for (int i = 0, paramsSize = params.size(); i < paramsSize; i++)
            {
                IParam param = params.get(i);
                if (DigitizedStorage.PO_TRACE_FILE.equals(param.getName()))
                {
                    params.remove(i);
                    String fName = (String) param.getValue();
                    fName = PathUtils.getAbsolutePath(fName, MainformMonitor.workDir);
                    new File(fName).delete();
                    break;
                }
            }

            digit.init(new DefAttrImpl(DigitizedStorage.PO_TRACE_PATH,null));

            if (refresh)
                mainmodule.refresh(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    protected Pair<IRasterContainer,IPathDefContainer> getContainers() throws Exception
    {
        IProjContext proj = mainmodule.getProjContext();
        INodeStorage mainstor = (INodeStorage) proj.getStorage();

        Collection<INodeStorage> childStorages = mainstor.getChildStorages();
        IPathDefContainer digit=null;
        IRasterContainer stor=null;
        for (INodeStorage st : childStorages) {
            if (st instanceof IRasterContainer)
                stor=(IRasterContainer)st;
            else if (st instanceof IPathDefContainer)
                digit=(DigitizedStorage)st;
            if (digit!=null && stor!=null)
                break;
        }
        return new Pair<IRasterContainer, IPathDefContainer>(stor,digit);
    }
    protected void digitizer(ParamEx param)
    {
        ERR_CAPTION = Enc.get("$92");
        try
        {

//            IProjContext proj = mainmodule.getProjContext();
//            INodeStorage mainstor = (INodeStorage) proj.getStorage();
            Pair<IRasterContainer, IPathDefContainer> pr = getContainers();
//            Collection<INodeStorage> childStorages = mainstor.getChildStorages();
//            IPathDefContainer digit=null;
//            IRasterContainer stor=null;
//            for (INodeStorage st : childStorages) {
//                if (st instanceof IRasterContainer)
//                    stor=(IRasterContainer)st;
//                else if (st instanceof IPathDefContainer)
//                    digit=(DigitizedStorage)st;
//                if (digit!=null && stor!=null)
//                    break;
//            }

            if (pr.first==null)
                throw new Exception("Can't find storage for raster");
            if (pr.second==null)
                throw new Exception("Can't find storage for raster");


            Pair<BindStruct,Integer> currentStruct = pr.first.getCurrentStruct();
            if (currentStruct!=null)
            {
                BindStruct bindStruct = currentStruct.getKey();
                if (bindStruct.flnames.length>0)
                {

                    BufferedImage bufferedImage = ImageIO.read(new File(bindStruct.pictdir + "/" + bindStruct.flnames[0][0]));
                    {
                    }

                    doDigitize(param, pr, bufferedImage,new IProgressViewer() {
                        public void setTraceProgress(PathDef pathDef) {
                        }

                        public void showProgress(int val) {
                        }

                        public void setProgress(float value) {
                        }

                        public void setBitCount(int cnt) {
                        }
                    });

                }
            }

        } catch (Exception e)
        {
            ShowMessagesUtils.showException(e, ERR_CAPTION);
        }

    }

    protected void doDigitize(ParamEx param, Pair<IRasterContainer, IPathDefContainer> pr, BufferedImage bufferedImage,IProgressViewer viewer
    ) throws Exception {

        Bitmap bmp=null;
        {
            BufferedImage _bufferedImage=bufferedImage;
            if (param.median!=0)
            { //Предварительно применить медианную фильтрацию
                Color selColor = new Color(param.rColor);
                bufferedImage= MedianProcessor.processMedian(bufferedImage, param.mSize, selColor, param.colorDist, selColor, param.borderType, viewer);
            }

            boolean[] emptyBmp = new boolean[]{true};
            if (bufferedImage!=null)
                bmp = u.getBmpByImageWithColor(bufferedImage, param.rColor, emptyBmp,viewer);

            if (emptyBmp[0])
            {
                int res = JOptionPane.showConfirmDialog(MainformMonitor.getFrame(),Enc.get("$93"), Enc.get("$94"),
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (res==JOptionPane.CANCEL_OPTION)
                    return;
            }

            if (bmp==null)
                bmp = u.getBmpByImage(_bufferedImage,viewer);
        }
        viewer.setBitCount(bmp.getBitCount());

        PathDef def = poTraceJ.trace(param, bmp,viewer);

        if (def==null)
            ShowMessagesUtils.showMessage(MainformMonitor.getFrame(),Enc.get("$95"), new String[]{Enc.get("$96")});
        pr.second.setPathDef(def);
        mainmodule.refresh(null);
    }

    protected void paramters()
    {
        try {

                {
                    ParamEx param= getParam();
                    param.rColor=getSelectedRGB();
                    setParam(param);
                }

            ParamEx param=(ParamEx)getParam().clone();
            ParametersForm frm=new ParametersForm(this);
            frm.setData(param);
            frm.pack();
            frm.setVisible(true);

            if (frm.isOkStatus() && frm.getData(param))
            {
                if (getParam().rColor!=param.rColor)
                    setSelectedRGB(param.rColor);
                setParam(param);
            }
        }
        catch (CloneNotSupportedException e)
        {
            //
        }
    }

    public JToolBar addInToolBar(JToolBar systemtoolbar) throws Exception
    {
        DIGITIZER = Enc.get("$89");
        RESET = Enc.get("$91");

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
                    digitizer(getParam());
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
                    reset(true);
                }
            });
            button.setMargin(new Insets(0, 0, 0, 0));
            systemtoolbar.add(button);
        }
        JToolBar rv = super.addInToolBar(systemtoolbar);
        setSelectedRGB(getParam().rColor);
        return rv;
    }

    public String getModuleName()
    {
        return MODULENAME;
    }

    public Object init(Object obj) throws Exception
    {
        IParam attr=(IParam)obj;
        String name = attr.getName();
        if (ParamEx.isAcceptedTags(name))
        {
            Object value = attr.getValue();
            if (value!=null)
            {
                if (param==null)
                    param=new ParamEx();
                param.parseByTagName(name, (String)value);
            }
        }
        return super.init(obj);
    }

    public IAnswerBean execute(ICommandBean cmd)
    {
        throw new UnsupportedOperationException();
    }

    public void setSelectedRGB(int rColor)
    {
        super.setSelectedRGB(rColor);
        ParamEx param= getParam();
        param.rColor=rColor;
        setParam(param);
    }

}
