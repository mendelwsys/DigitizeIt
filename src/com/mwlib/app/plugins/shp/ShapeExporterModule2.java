package com.mwlib.app.plugins.shp;

import com.mwlib.app.layers.DigitizerLayer;
import com.mwlib.app.plugins.common.ProgressDialog;
import com.mwlib.app.plugins.common.ShowMessagesUtils;
import com.mwlib.app.plugins.common.ExporterData;
import com.mwlib.app.storages.mem.IPathDefContainer;
import com.mwlib.app.storages.raster.IRasterContainerEx;
import com.mwlib.app.utils.InitAbleUtils;
import com.mwlib.app.utils.ObjectBuilderUtils;
import com.mwlib.ptrace.IProgressObserver;
import com.mwlib.ptrace.PathDef;
import com.mwlib.utils.Enc;
import ru.ts.factory.IFactory;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;
import ru.ts.res.ImgResources;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.drawcomp.IDrawObjRule;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.drawcomp.rules.def.CnStyleRuleImpl;
import ru.ts.toykernel.drawcomp.rules.def.CommonStyle;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.gui.IViewControl;
import ru.ts.toykernel.gui.deftable.TDefaultHeader;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.pcntxt.gui.defmetainfo.MainformMonitor;
import ru.ts.toykernel.plugins.IAnswerBean;
import ru.ts.toykernel.plugins.ICommandBean;
import ru.ts.toykernel.plugins.IGuiModule;
import ru.ts.toykernel.plugins.consts.DefNameConverter2;
import ru.ts.toykernel.plugins.styles.ISupplyerFactory;
import ru.ts.toykernel.proj.xml.IXMLProjBuilder;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.storages.raster.BindStruct;
import ru.ts.toykernel.storages.raster.IRasterContainer;
import ru.ts.toykernel.xml.def.ParamDescriptor;
import ru.ts.utils.data.Pair;
import ru.ts.utils.gui.tables.IHeaderSupplyer;
import ru.ts.utils.gui.tables.THeader;
import ru.ts.utils.gui.tables.TNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

/**
 * Export to Shape format module
 * com.mwlib.app.plugins.shp.ShapeExporterModule
 */
public class ShapeExporterModule2 extends BaseInitAble implements IGuiModule
{
	public static final String MODULENAME = "TOOLMODULE";

//    public static final String EXPORT = Enc.get("$130");
//    public static final String HEADER = Enc.get("$131");
//    public static final String DLG_TITLE = Enc.get("$132");
//    public static final String ERR_CAPTION = Enc.get("$133");


    protected ILayer layer4export;
    protected IBaseStorage imagestorage;

    private IXMLProjBuilder builder;
    private IViewControl mainmodule;
    private ISupplyerFactory headerFactory;

    private ExporterData defExporterData = new ExporterData();


    protected String getOpenCaption()
    {
        return Enc.get("$130");
        //return EXPORT;
    }

    protected String getOpenHeader()
    {
        return Enc.get("$131");
        //return HEADER;
    }

	public ShapeExporterModule2()
	{
	}

//	public ShapeExporterModule2(ILayer layer4export, IBaseStorage imagestorage)
//	{
//		this.layer4export = layer4export;
//        this.imagestorage=imagestorage;
//	}

	public String getMenuName()
	{
		return Enc.get("$134");
	}

	public JMenu addMenu(JMenu inmenu) throws Exception
	{
        {
            JMenuItem menuItem = new JMenuItem(getOpenCaption(), KeyEvent.VK_O);
            inmenu.add(menuItem);
            menuItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    export4Shape2(getOpenHeader());
                }
            });
        }

		return inmenu;
	}

    private INameConverter getStorNmWithCodeNm(IProjContext projContext)
    {
        return  new DefNameConverter2()
          {
              public String codeAttrNm2ViewNm(String attrName)
              {
                  if (attrName.equals(DigitizerLayer.NICKNAME))
                      return LAYERNAMEHNAME;
                  return super.codeAttrNm2ViewNm(attrName);
              }

          };
    }

    private ISupplyerFactory getHeaderFactory()
    {
        if (this.headerFactory==null)
        {
            headerFactory=new ISupplyerFactory()
            {
                public IHeaderSupplyer getHeaderSupplyer() throws Exception
                {
                    final INameConverter storNm2attrNm = getStorNmWithCodeNm(mainmodule.getProjContext());

                    return  new IHeaderSupplyer()
                    {
                        final static String layerVisible = KernelConst.LAYER_VISIBLE;
                        final static String layerName = DigitizerLayer.NICKNAME;

                        private THeader[] styleheaders = new THeader[]
                            {
                                    new TDefaultHeader(new TNode(storNm2attrNm.codeAttrNm2ViewNm(layerName)), storNm2attrNm.codeAttrNm2StorAttrNm(layerName),true, String.class),
                                    new TDefaultHeader(new TNode(storNm2attrNm.codeAttrNm2ViewNm(layerVisible)), storNm2attrNm.codeAttrNm2StorAttrNm(layerVisible),true, Boolean.class)
                            };



                        public THeader[] getOptionsRepresent()
                        {
                            return styleheaders;
                        }
                    };

                            //new DefHeaderSupplyer(getStorNmWithCodeNm(mainmodule.getProjContext()));
                }
            };
        }
        return this.headerFactory;
    }


    protected void export4Shape2(String header)
    {

        if (mainmodule!=null)
        {
            try
            {
                IProjContext ctx = mainmodule.getProjContext();
                final List<ILayer> lrs = ctx.getLayerList();


                if (lrs!=null && lrs.size()>0)
                {

                    final IRasterContainer rasterContainer = (IRasterContainer) imagestorage;

                    ViewLayerOptions dlg = new ViewLayerOptions(mainmodule, getHeaderFactory().getHeaderSupplyer(),builder);
                    ImageIcon icon= ImgResources.getIconByName("images/poolball.gif","Title");
                    if (icon!=null)
                    {
                        Frame frame = (Frame) dlg.getOwner();
                        if (frame!=null)
                            frame.setIconImage(icon.getImage());
                    }


                    {
                        String commonName = defExporterData.getCommonName();
                        String pathName=defExporterData.getPathName();
                        if (
                                commonName==null || commonName.length()==0
                                ||
                                pathName == null || pathName.length()==0
                           )
                        {
                            CommonStyle style = null;
                            if (layer4export!=null)
                            {
                                IDrawObjRule drwRule = layer4export.getDrawRule();
                                style = ((CnStyleRuleImpl) drwRule).getDefStyle();
                            }
                           defExporterData.setDefExportData(style, rasterContainer);
                        }
                    }

                    dlg.setTitle(Enc.get("$132"));
//                    dlg.setTitle(DLG_TITLE);
                    dlg.setDefExporter(defExporterData);
                    dlg.pack();
                    dlg.setModal(true);
                    dlg.setVisible(true);

                    if ((rasterContainer instanceof IRasterContainerEx) && (dlg.isOk || dlg.isExportStatus()))
                    { //Установка контейнера растров
                        try {
                            ((IRasterContainerEx)rasterContainer).setResolution(Double.parseDouble(defExporterData.getResolution()));
                        } catch (NumberFormatException e)
                        {
                            throw new Exception("Error parsing of raster resolution", e);
                        }
                    }

                    if (dlg.isExportStatus())
                    {
    //Собираем все опции со всех слоев
                        final List<Pair<ExporterData,PathDef>> lrs4export=new LinkedList<Pair<ExporterData,PathDef>>();
                        final PoTrace2Shp poTrace2Shp = getConverter();

                        final ExporterData defExportData = new ExporterData();
                        dlg.setCommonFields2ExportData(defExportData);


                        int totalObjCount=0;
                        for (ILayer lr : lrs)
                        {
                            IAttrs map = lr.getLrAttrs();
                            if (lr.isVisible())
                            {
                                Map<String, Object> lrParams = InitAbleUtils.getMapParams(map);
                                ExporterData exporterData=defExportData.clone();

                                ObjectBuilderUtils.setObjectBySettersParams(exporterData, lrParams, ObjectBuilderUtils.ToCase.CaseIgnore, new HashSet<String>(Arrays.asList(ExporterData.getExcludeFiles())));

                                if (defExportData.isUnionAll() && (defExportData.getCommonName()==null || defExportData.getCommonName().length()==0))
                                    defExportData.setCommonName(exporterData.getFname());

                                IBaseStorage storage = lr.getStorage();
                                PathDef pathDef = ((IPathDefContainer) storage).getPathDef();
                                lrs4export.add(new Pair<ExporterData, PathDef>(exporterData,pathDef));
                                totalObjCount+=storage.getObjectsCount();
                            }
                        }




                        if (lrs4export.size()>0)
                        {
                            try
                            {
                                JFrame frame;
                                final ProgressDialog pd = new ProgressDialog(Enc.get("$135"),frame= MainformMonitor.getFrame());
                                pd.pack();
                                pd.setSize(2*pd.getWidth(),pd.getHeight());
                                pd.setLocation((frame.getWidth()-pd.getWidth())/2,(frame.getHeight()-pd.getHeight())/2);
                                final JProgressBar progressBar = pd.getProgressView();


                                final int _totalObjCount=totalObjCount;


                                final IProgressObserver progressViewer=new IProgressObserver()
                                {
                                    int cntVal;
                                    {
                                        progressBar.setStringPainted(true);
                                        progressBar.setMinimum(0);
                                        progressBar.setMaximum(100);
                                        cntVal=0;
                                    }


                                    public void setTraceProgress(PathDef pathDef) {
                                    }

                                    public void showProgress(int val)
                                    {
                                         cntVal+=val;

                                         if (cntVal%300==0)
                                         {
                                             double _val=1.0*cntVal/_totalObjCount;
                                             int round = (int) Math.round(100 * _val);
                                             progressBar.setString(Enc.get("$136")+ round +"%");
                                             progressBar.setValue(round);
                                         }
                                    }
                                };


                                Thread runnable = new Thread()
                                {
                                    public void run() {
                                        try
                                        {
                                            try {
                                                Pair<BindStruct, Integer> struct = rasterContainer.getCurrentStruct();
                                                BindStruct bindStruct = struct.getKey();
                                                if (defExportData.getUnionAll())
                                                {
                                                    //Транслируем это в один shp файл
                                                    poTrace2Shp.createShapeFile(lrs4export.toArray(new Pair[lrs.size()]),bindStruct.totalsize, progressViewer);
                                                }
                                                else
                                                {
                                                    for (Pair<ExporterData, PathDef> export2PathDef : lrs4export)
                                                    {
                                                        String shpFile=poTrace2Shp.setByExportData(export2PathDef.first, bindStruct.totalsize);
                                                        poTrace2Shp.createShapeFile(shpFile,export2PathDef.second, progressViewer);
                                                    }
                                                }
                                            }
                                            finally
                                            {
                                                pd.setVisible(false);
                                            }
                                        }
                                        catch (Exception e)
                                        {

                                            ShowMessagesUtils.showException(e, Enc.get("$133"));
                                        }
                                    }
                                };
                                runnable.start();
                                pd.setVisible(true);
                            }
                            catch (Exception e)
                            {
                                ShowMessagesUtils.showException(e, Enc.get("$133"));
                            }
                        }
                    }
                }
                else
                {
                    //TODO Высветить диалог
                }
            }
            catch (Exception e1)
            {
                ShowMessagesUtils.showException(e1, Enc.get("$133"));
            }
        }
    }

    protected PoTrace2Shp getConverter() {
        return new PoTrace2Shp();
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
            ImageIcon icon = ImgResources.getIconByName("images/deploy.png", "Convert");
            if (icon != null)
                button.setIcon(icon);
            button.setToolTipText(getOpenCaption());//TODO воспользоваться конвертором для имен
            button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    export4Shape2(getOpenHeader());
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
        if (ExporterData.isAcceptedModuleTags(attr.getName()))
             defExporterData.parseByTagName(attr.getName(),(String)attr.getValue());
        else if (attr.getName().equalsIgnoreCase(KernelConst.LAYER_TAGNAME))
            this.layer4export = (ILayer) attr.getValue();
        else if (attr.getName().equalsIgnoreCase(KernelConst.STORAGE_TAGNAME))
        {
            this.imagestorage = (IBaseStorage) attr.getValue();
            if (this.imagestorage instanceof IRasterContainer)
                defExporterData.setParametersFromRaster((IRasterContainer)this.imagestorage);
        }
        else if (attr.getName().equalsIgnoreCase(KernelConst.VIEWCNTRL_TAGNAME))
            this.mainmodule = (IViewControl) attr.getValue();
        else if (attr.getName().equalsIgnoreCase(KernelConst.APPBUILDER_TAGNAME))
            this.builder=(IXMLProjBuilder) attr.getValue();
		return null;
	}



    public IObjectDesc getObjectDescriptor()
    {
        ParamDescriptor paramDescriptor =new ParamDescriptor(desc);
        List<IParam> params = paramDescriptor.getParams();
        for (int i = 0; i < params.size();)
        {
            IParam iParam = params.get(i);
            if (ExporterData.isAcceptedModuleTags(iParam.getName()))
                params.remove(i);
            else
                i++;
        }
        params.addAll(defExporterData.getDescModuleParameters());
        paramDescriptor.setParams(params);
        return paramDescriptor;
    }
}
