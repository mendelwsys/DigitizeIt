package com.mwlib.app.plugins.digitizer;

import com.mwlib.app.layers.DigitizerLayer;
import com.mwlib.app.plugins.common.ExporterData;
import com.mwlib.app.plugins.common.ProgressDialog;
import com.mwlib.app.plugins.common.ShowMessagesUtils;
import com.mwlib.app.plugins.shp.PoTrace2Shp;
import com.mwlib.app.storages.mem.DigitizedStorage;
import com.mwlib.app.storages.mem.IPathDefContainer;
import com.mwlib.app.storages.raster.IRasterContainerEx;
import com.mwlib.app.utils.InitAbleUtils;
import com.mwlib.app.utils.LayerUtils;
import com.mwlib.ptrace.PathDef;
import com.mwlib.utils.Enc;
import ru.ts.factory.IParam;
import ru.ts.res.ImgResources;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.drawcomp.rules.def.CnStyleRuleImpl;
import ru.ts.toykernel.drawcomp.rules.def.CommonStyle;
import ru.ts.toykernel.gui.IViewControl;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.pcntxt.gui.defmetainfo.MainformMonitor;
import ru.ts.toykernel.proj.xml.IXMLProjBuilder;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.storages.raster.IRasterContainer;
import ru.ts.utils.data.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 06.04.14
 * Time: 18:52
 *
 */
public class DigitizerModuleExt extends DigitizerModule
{
    protected IViewControl mainmodule2;
//    public static final String SAVELAYER = Enc.get("$97");
    protected LayerUtils layerUtils;
    protected IXMLProjBuilder builder;

    private JTextField layerName;

    public String getNickLayerName() {
        return layerName.getText();
    }


    public DigitizerModuleExt()
    {
        ParamEx param = getParam();
        if (param.getResolution() <=0)
            param.setResolution(1);
        param.dturdsize=ParamEx.getGaByPixel(param.turdsize,param.getResolution());
    }

    protected void digitizer(ParamEx param)
    {

        try {
            param=param.clone();
            param.turdsize= param.getPixelsByGa(param.dturdsize,param.getResolution());
            super.digitizer(param);

        } catch (CloneNotSupportedException e)
        {
            //
        }

    }

    protected void doDigitize( final ParamEx param, final Pair<IRasterContainer, IPathDefContainer> pr, final BufferedImage bufferedImage,IProgressViewer viewer)
            throws Exception
    {

            JFrame frame;
            final ProgressDialog pd = new ProgressDialog(Enc.get("$98"),frame=MainformMonitor.getFrame());
            pd.pack();
            pd.setSize(2*pd.getWidth(),pd.getHeight());
            pd.setLocation((frame.getWidth()-pd.getWidth())/2,(frame.getHeight()-pd.getHeight())/2);


            final JProgressBar progressBar = pd.getProgressView();


            final IProgressViewer _viewer=new IProgressViewer()
            {


                int cnt;
                int total;
                {
                    progressBar.setStringPainted(true);
                    progressBar.setMinimum(0);
                    progressBar.setMaximum(100);
                }

                public void setBitCount(int cnt)
                {
                    total=0;
                    this.cnt=cnt;
                    progressBar.setValue(0);
                }

                public void setTraceProgress(PathDef pathDef)
                {
                    if (pathDef.getSign()=='+')
                        total+=pathDef.getArea();
                    else
                        total-=pathDef.getArea();
                    double val = 1.0*total/cnt;
//                    if (val>1)
//                        System.out.println("val = " + val);
                    int round =(int)Math.round(100*val);
                    progressBar.setString(Enc.get("$99")+round+"%");
                    progressBar.setValue(round);
                }

                public void showProgress(int val) {

                     progressBar.setString(Enc.get("$100")+val+"%");
                     progressBar.setValue(val);

                }

                int oldVal;
                public void setProgress(float value) {
                    int round = Math.round(100*value);
                    if (this.oldVal>round)
                    {
                        this.oldVal=200;
                        progressBar.setString(Enc.get("$101")+round+"%");
                    }
                    else
                    {
                        progressBar.setString(Enc.get("$102")+round+"%");
                        oldVal=round;
                    }
                    progressBar.setValue(round);
                }
            };
            Thread runnable = new Thread() {
                public void run() {

                    try {
                        try {
                            DigitizerModuleExt.super.doDigitize(param, pr, bufferedImage, _viewer);
                        }
                        finally {
                            pd.setVisible(false);
                        }
                    } catch (Exception e) {
                        ShowMessagesUtils.showException(e,Enc.get("$103"));
                    }
                }
            };
            runnable.start();
            pd.setVisible(true);
    }


//    protected void digitizer_(ParamEx param)
//    {
//
//        try {
//            param=param.clone();
//            param.turdsize= param.getPixelsByGa(param.dturdsize,param.getResolution());
//
//            final JProgressBar progressBar = new JProgressBar();
//            progressBar.setStringPainted(true);
//
//            toolBar.add(progressBar);
//            toolBar.addSeparator();
//            progressBar.updateUI();
//
//            Thread runnable = new Thread() {
//                public void run() {
//                    for (int i=0;i<=50;i++)
//                    {
//                        progressBar.setString(i*2+"%");
//                        progressBar.setValue(i);
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                        }
//                    }
//                    toolBar.remove(progressBar);
//                    toolBar.updateUI();
//                }
//            };
//            runnable.start();
////            super.digitizer(param);
//
//        } catch (CloneNotSupportedException e)
//        {
//            //
//        }
//    }


    public JMenu addMenu(JMenu inmenu) throws Exception
    {
        inmenu=super.addMenu(inmenu);
        JMenuItem menuItem = new JMenuItem(Enc.get("$97"), KeyEvent.VK_O);
//        JMenuItem menuItem = new JMenuItem(SAVELAYER, KeyEvent.VK_O);
        inmenu.add(menuItem);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                saveLayer();
            }
        });


//        JMenuItem menuItemT = new JMenuItem("Тест Сериализации", KeyEvent.VK_O);
//        inmenu.add(menuItemT);
//        menuItemT.addActionListener(new ActionListener()
//        {
//
//            public void actionPerformed(ActionEvent e) {
//
//                try {
//                    IOUtils iou = new IOUtils();
//                    IProjContext proj = mainmodule.getProjContext();
//                    INodeStorage mainstor = (INodeStorage) proj.getStorage();
//                    Collection<INodeStorage> childStorages = mainstor.getChildStorages();
//
//                    IPathDefContainer digit=null;
//                    for (INodeStorage st : childStorages) {
//                        if (st instanceof IPathDefContainer)
//                        {
//                            digit=(DigitizedStorage)st;
//                            break;
//                        }
//                    }
//                    PathDef def = digit.getPathDef();
//                    if (def!=null)
//                    {
//                        def=iou.testIOUtils(def);
//                        digit.setPathDef(def);
//                        mainmodule.refresh(null);
//                    }
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
//
//            }
//        });

        return inmenu;
    }

    private void saveLayer() {
        try
        {
            if (layerUtils==null)
            {
                InputStream is = DigitizerModule.class.getResourceAsStream("/res/template.xml");
                StringBuilder builder = new StringBuilder();
                String str;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "WINDOWS-1251"));
                while((str= bufferedReader.readLine())!=null)
                     builder.append(str);
                layerUtils= new LayerUtils(builder.toString());
            }
            IPathDefContainer digit = getPathContainer();
            if (digit!=null && digit.getPathDef()!=null)
            {
                String nickLayerName = getNickLayerName();


                br:
                for (int k=0;k<1;k++)
                { //Проверка того что с таким именем слоев еще нет
                    List<ILayer> ll = mainmodule2.getProjContext().getLayerList();
                    if (ll!=null)
                    {
                        for (ILayer iLayer : ll)
                        {
                            IAttrs attrs = iLayer.getLrAttrs();
                            if (attrs!=null)
                            {
                                IDefAttr attr = attrs.get(DigitizerLayer.NICKNAME);
                                if (    nickLayerName==null || nickLayerName.length()==0
                                                                                        ||
                                        (attr!=null && nickLayerName.equals(attr.getValue()))
                                        )
                                {
                                    JFrame frame;
                                    String title = Enc.get("$104");
                                    if (nickLayerName==null || nickLayerName.length()==0)
                                        title = Enc.get("$105");

                                    ConfirmLayerName pd=new ConfirmLayerName(title,frame=MainformMonitor.getFrame());
                                    pd.setNickName(nickLayerName);
                                    pd.pack();
                                    pd.setSize(2*pd.getWidth(),pd.getHeight());
                                    pd.setLocation((frame.getWidth()-pd.getWidth())/2,(frame.getHeight()-pd.getHeight())/2);

                                    pd.setVisible(true);
                                    nickLayerName=pd.getNickName();

                                    if (nickLayerName!=null && nickLayerName.length()>0 && pd.isOkStatus())
                                        break br;
                                    k=-1;
                                }
                            }
                        }
                    }
                }


                List<ILayer> layers = layerUtils.addLayerStorage(mainmodule2.getProjContext(), digit,builder);


                for (ILayer layer : layers)
                {
                    CnStyleRuleImpl cnStyleRule = (CnStyleRuleImpl)layer.getDrawRule();

                    CommonStyle style = cnStyleRule.getDefStyle();
                    style.setColorFill(getParam().rColor);
                    style.setColorLine(getParam().rColor);
                    cnStyleRule.setDefStyle(style);

                    Map<String,Object> name2Object = new HashMap<String,Object>();

                    name2Object.put(DigitizerLayer.NICKNAME, nickLayerName);
                    {
                        ExporterData exporterData=new ExporterData();


                        Pair<IRasterContainer, IPathDefContainer> pr = getContainers();
                        //1. Добавляем растр
                        exporterData.setDefExportData(style,pr.first);
//                        exporterData.setResolution(String.valueOf(getParam().resolution));

                        exporterData.setAttributeName(DigitizerLayer.NICKNAME);
                        exporterData.setSquareName(PoTrace2Shp.DEF_NAME_TGAAREA);
                        exporterData.setColorName(PoTrace2Shp.DEF_NAME_COLOR);


                        exporterData.setAttribute(nickLayerName);
                        //2. Формируем флаг существования
                        InitAbleUtils.setInitAbleByParams(layer, exporterData.getDescParameters());
                    }
                    InitAbleUtils.setInitAbleByParams(layer, name2Object);
                }
                mainmodule2.refresh(null);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private IPathDefContainer getPathContainer() throws Exception {
        IProjContext proj = mainmodule.getProjContext();
        INodeStorage mainstor = (INodeStorage) proj.getStorage();
        Collection<INodeStorage> childStorages = mainstor.getChildStorages();
        IPathDefContainer digit=null;
        for (INodeStorage st : childStorages)
        {
            if (st instanceof IPathDefContainer)
            {
                digit=(DigitizedStorage)st;
                break;
            }
        }
        return digit;
    }

    public Object init(Object obj) throws Exception
    {
        IParam attr=(IParam)obj;
        if (attr.getName().equalsIgnoreCase(KernelConst.VIEWCNTRL_TAGNAME) &&  mainmodule!=null)
                this.mainmodule2 = (IViewControl) attr.getValue();
        else if (attr.getName().equalsIgnoreCase(KernelConst.APPBUILDER_TAGNAME))
            this.builder=(IXMLProjBuilder) attr.getValue();
        else
            return super.init(obj);
        return null;
    }

    JToolBar toolBar;
    public JToolBar addInToolBar(JToolBar systemtoolbar) throws Exception
    {
        toolBar =super.addInToolBar(systemtoolbar);

        layerName = new JTextField(Enc.get("$106"));
        int aligment=layerName.getHorizontalAlignment();

        Dimension sz = layerName.getMinimumSize();
        layerName.setMaximumSize(new Dimension(sz.width*30,sz.height));
        toolBar.add(layerName);

        {
            JButton button = new JButton();
            ImageIcon icon = ImgResources.getIconByName("images/er-state.png", "SaveLayer");
            if (icon != null)
                button.setIcon(icon);
            button.setToolTipText(Enc.get("$97"));
//            button.setToolTipText(SAVELAYER);
            button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    saveLayer();
                }
            });
            button.setMargin(new Insets(0, 0, 0, 0));
            systemtoolbar.add(button);
        }

        return toolBar;
    }

    protected void setResolutionByParams(ParamEx param) throws Exception
    {
        Pair<IRasterContainer, IPathDefContainer> pr = getContainers();
        if (pr.first instanceof IRasterContainerEx)
            ((IRasterContainerEx)pr.first).setResolution(param.getResolution());
    }

    protected void setResolutionByContainer(ParamEx param) throws Exception
    {
        Pair<IRasterContainer, IPathDefContainer> pr = getContainers();
        if (pr.first instanceof IRasterContainerEx)
        {
            param.setResolution(((IRasterContainerEx)pr.first).getResolution());
            param.turdsize= param.getPixelsByGa(param.dturdsize,param.getResolution());
        }
    }

    protected void paramters()
    {
        try {

                {
                    ParamEx param= getParam();
                    param.rColor=getSelectedRGB();
                    setParam(param);
                }

            ParamEx param=getParam().clone();
            setResolutionByContainer(param);
            CursorOptions frm=new CursorOptions(this);
            frm.setData(param);
            frm.pack();
            frm.setVisible(true);
            if (frm.isOkStatus() && frm.getData(param))
            {
                if (getParam().rColor!=param.rColor)
                    setSelectedRGB(param.rColor);
                setResolutionByParams(param);
                setParam(param);
            }
        }
        catch (Exception e)
        {
            //
        }
    }

}
