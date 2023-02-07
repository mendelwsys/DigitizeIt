package com.mwlib.app.plugins.shp;

import com.mwlib.app.plugins.common.ExporterData;
import com.mwlib.app.storages.mem.IPathDefContainer;
import com.mwlib.ptrace.PathDef;
import com.mwlib.utils.Enc;
import ru.ts.factory.IFactory;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.res.ImgResources;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.drawcomp.IDrawObjRule;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.drawcomp.rules.def.CnStyleRuleImpl;
import ru.ts.toykernel.drawcomp.rules.def.CommonStyle;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.plugins.IAnswerBean;
import ru.ts.toykernel.plugins.ICommandBean;
import ru.ts.toykernel.plugins.IGuiModule;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.storages.raster.BindStruct;
import ru.ts.toykernel.storages.raster.IRasterContainer;
import ru.ts.toykernel.xml.def.ParamDescriptor;
import ru.ts.utils.Files;
import ru.ts.utils.data.Pair;
import shp.core.ShpCoreLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.*;

/**
 * Export to Shape format module
 * com.mwlib.app.plugins.shp.ShapeExporterModule
 */
public class ShapeExporterModule extends BaseInitAble implements IGuiModule
{
	public static final String MODULENAME = "TOOLMODULE";

    public static final String EXPORT = Enc.get("$125");

    public static final String HEADER = Enc.get("$126");
    public static final String DLG_TITLE = Enc.get("$127");

    public static final String FLATNESS_TAG ="FLATNESS";
    public static final String SHAPEOBJ_TAG="SHAPEOBJ";
    public static final String ERR_CAPTION = Enc.get("$128");

    private PoTrace2Shp poTrace2Shp = new PoTrace2Shp();

    protected String getOpenCaption()
    {
        return EXPORT;
    }

    protected String getOpenHeader()
    {
        return HEADER;
    }

    protected ILayer layer4export;
    protected IBaseStorage imagestorage;

	public ShapeExporterModule()
	{
	}

	public ShapeExporterModule(ILayer layer4export,IBaseStorage imagestorage)
	{
		this.layer4export = layer4export;
        this.imagestorage=imagestorage;
	}

	public String getMenuName()
	{
		return Enc.get("$129");
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
                    export4Shape(getOpenHeader());
                }
            });
        }

		return inmenu;
	}

	protected void export4Shape(String header)
    {
        try {

            if (imagestorage instanceof IRasterContainer)
            {
                IBaseStorage storage = layer4export.getStorage();
                if (storage instanceof IPathDefContainer)
                {
                    IRasterContainer rasterContainer=(IRasterContainer)imagestorage;
                    PathDef pathDef = ((IPathDefContainer) storage).getPathDef();
                    if (pathDef!=null)
                    {
                        IDrawObjRule drwRule = layer4export.getDrawRule();
    //TODO Ввести интерфейс стиль контейнер
    //TODO Если правило не содержит стиль  контейнер установить данные по умолчанию (переданные через метод Init)
                        CommonStyle style = ((CnStyleRuleImpl) drwRule).getDefStyle();

                        Pair<BindStruct, Integer> struct = rasterContainer.getCurrentStruct();
                        BindStruct bindStruct = struct.getKey();
                        String fName= bindStruct.flnames[0][0];
                        String pictDesc=bindStruct.pictdir;
                        String fNameNoExt = Files.getNameNoExt(fName);


                        ExporterData exporterData=new ExporterData();

                        exporterData.setBlackColor(style.getsHexColorFill());
                        exporterData.setWhiteColor(Integer.toHexString(poTrace2Shp.getWhiteColor()));
                        exporterData.setFlatness(String.format(Locale.ENGLISH,"%.5f",poTrace2Shp.getFlatness()));


                        exporterData.setFname(fNameNoExt);
                        exporterData.setPathName(pictDesc);
                        exporterData.setAsShapeObject(poTrace2Shp.getAsShapeObject());

                        exporterData.setTabFile(pictDesc + File.separator + fNameNoExt + ".tab");
                        exporterData.setTranslate(new File(exporterData.getTabFile()).exists());

                        ExpOptions dlg = new ExpOptions();
                        dlg.setTitle(DLG_TITLE);
                        dlg.setData(exporterData);
                        dlg.pack();
                        dlg.setVisible(true);

                        if (dlg.isOkStatus())
                        {
                            dlg.getData(exporterData);
                            try {
                                poTrace2Shp.setFlatness(Double.parseDouble(exporterData.getFlatness()));
                                poTrace2Shp.setBlackColor((int) Long.parseLong(exporterData.getBlackColor(), 16));
                                poTrace2Shp.setWhiteColor((int) Long.parseLong(exporterData.getWhiteColor(), 16));
                                poTrace2Shp.setAsShapeObject(exporterData.getAsShapeObject());

                                if (exporterData.isTranslate())
                                {
                                    poTrace2Shp.setConvert(true);
                                    poTrace2Shp.setfTabName(exporterData.getTabFile());
                                    MPoint pt=bindStruct.totalsize;
                                    poTrace2Shp.setwImg((int)Math.round(pt.getX()));
                                    poTrace2Shp.sethImg((int)Math.round(pt.getY()));
                                }
                                ShpCoreLogger.disableLogging();
                                String shpPath = exporterData.getPathName() + File.separator + exporterData.getFname();
                                poTrace2Shp.createShapeFile(shpPath, pathDef, null);

                            }
                            catch (Exception e)
                            {
                                ImageIcon icon = ImgResources.getIconByName("images/breakpoint.png", "Error");
                                JOptionPane.showMessageDialog(null, new String[]{e.getMessage()}, ERR_CAPTION,
                                        JOptionPane.INFORMATION_MESSAGE, icon
                                );
                                e.printStackTrace();
                            }
                        }


                    }
                }
            }


        } catch (Exception e)
        {
            e.printStackTrace();
        }

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
                    export4Shape(getOpenHeader());
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
		if (attr.getName().equalsIgnoreCase(KernelConst.LAYER_TAGNAME))
			this.layer4export = (ILayer) attr.getValue();
        else if (attr.getName().equalsIgnoreCase(KernelConst.STORAGE_TAGNAME))
			this.imagestorage = (IBaseStorage) attr.getValue();
        else if (attr.getName().equalsIgnoreCase(FLATNESS_TAG))
        {
            try {
                poTrace2Shp.setFlatness(Double.parseDouble((String) attr.getValue()));
            } catch (Throwable e) {
                //
            }
        }
        else if (attr.getName().equalsIgnoreCase(SHAPEOBJ_TAG))
        {
            try {
                poTrace2Shp.setAsShapeObject((String) attr.getValue());
            } catch (Throwable e) {
                //
            }
        }

		return null;
	}


    public IObjectDesc getObjectDescriptor()
    {
        {
            boolean isChange=
            (
                    !ShapeObj2Shp.DEF_SHAPEOBJ.equalsIgnoreCase(poTrace2Shp.getAsShapeObject())
                    ||
                    ShapeObj2Shp.DEF_M_FLATNESS !=poTrace2Shp.getFlatness()
            );

            ParamDescriptor paramDescriptor =new ParamDescriptor(desc);
            java.util.List<IParam> params = paramDescriptor.getParams();
            for (int i = 0; i < params.size();)
            {
                IParam iParam = params.get(i);
                if (
                        SHAPEOBJ_TAG.equalsIgnoreCase(iParam.getName())
                        ||
                        FLATNESS_TAG.equalsIgnoreCase(iParam.getName())
                   )
                {
                   params.remove(i);
                   isChange=true;
                }
                else
                    i++;
            }
            if (!ShapeObj2Shp.DEF_SHAPEOBJ.equalsIgnoreCase(poTrace2Shp.getAsShapeObject()))
                params.add(new DefAttrImpl(SHAPEOBJ_TAG,poTrace2Shp.getAsShapeObject()));
            if (ShapeObj2Shp.DEF_M_FLATNESS !=poTrace2Shp.getFlatness())
                params.add(new DefAttrImpl(FLATNESS_TAG,String.format(Locale.ENGLISH, "%.8f",poTrace2Shp.getFlatness())));
            paramDescriptor.setParams(params);
            if (isChange)
                return paramDescriptor;
        }
        return desc;
    }
}
