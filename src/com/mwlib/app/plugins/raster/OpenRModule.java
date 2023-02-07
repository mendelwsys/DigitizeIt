package com.mwlib.app.plugins.raster;

import com.mwlib.app.plugins.OpenModule;
import com.mwlib.app.utils.PathUtils;
import com.mwlib.utils.Enc;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.storages.raster.IRasterContainer;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.toykernel.converters.IProjConverter;
import ru.ts.toykernel.gui.IViewPort;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.pcntxt.gui.defmetainfo.MainformMonitor;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.xml.def.ParamDescriptor;
import ru.ts.utils.Operation;

import javax.swing.*;
import java.io.File;
import java.util.*;

/**
 * Open Project module
 */
public class OpenRModule extends OpenModule
{

    JComponent componentBus;
    public void registerListeners(JComponent component) throws Exception
    {
        super.registerListeners(component);
        componentBus=component;
    }

//    public static final String RCAPTION = Enc.get("$116");
//    public static final String IHEADER = Enc.get("$117");
    public static final String PATH_TAG = "PATH";

    public static final String DEF_PATH = MainformMonitor.workDir;
    private String path= DEF_PATH;

    protected String getOpenCaption()
    {
        return Enc.get("$116");
//        return RCAPTION;
    }

    protected String getOpenHeader()
    {
        return Enc.get("$117");
//        return IHEADER;
    }

    protected String getCleanCaption()
    {
        return Enc.get("$118");
    }


//    this.firePropertyChange()

    private boolean b;
    protected void cleanAll()
    {
        try {
            IProjContext proj = mainmodule.getProjContext();
            INodeStorage mainstor = (INodeStorage) proj.getStorage();
            Collection<INodeStorage> stors = mainstor.getChildStorages();

            IRasterContainer stor=null;
            for (INodeStorage st : stors) {
                if (st instanceof IRasterContainer)
                {
                    stor=(IRasterContainer)st;
                    break;
                }
            }
            if (stor==null)
                throw new Exception("Can't find storage for raster");

            stor.reInitByRaster(null);
            if (componentBus!=null)
            {
                componentBus.firePropertyChange("RESETRASTER",b,!b);
                b = !b;
            }

            mainmodule.refresh(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    protected void loadFile(String header)
	{
        try {
            IProjContext proj = mainmodule.getProjContext();

            this.path= PathUtils.getInitPath(this.path,DEF_PATH);


            File inFile = Operation.getFilePath(MainformMonitor.frame, getOpenHeader(), header, "png;jpeg;jpg;bmp;tiff",this.path);
            if (inFile!=null)
            {
                INodeStorage mainstor = (INodeStorage) proj.getStorage();
                Collection<INodeStorage> stors = mainstor.getChildStorages();

                IRasterContainer stor=null;
                for (INodeStorage st : stors) {
                    if (st instanceof IRasterContainer)
                    {
                        stor=(IRasterContainer)st;
                        break;
                    }
                }
                if (stor==null)
                    throw new Exception("Can't find storage for raster");

//                BindStruct bindStruct=stor.reInitByRaster(inFile);
//                MPoint pictsz = bindStruct.totalsize;
                stor.reInitByRaster(inFile);
                IViewPort viewPort = mainmodule.getViewPort();
//                Point dsz = viewPort.getDrawSize();
                IProjConverter conv = viewPort.getCopyConverter();

                conv.getAsScaledConverter().setScale(new MPoint(1,1));
                conv.getAsShiftConverter().setBindP0(new MPoint(0,0));
                viewPort.setCopyConverter(conv);
                this.path=inFile.getAbsolutePath();
                mainmodule.refresh(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public Object init(Object obj) throws Exception
    {
        IParam attr=(IParam)obj;

        if (attr.getName().equalsIgnoreCase(PATH_TAG))
            this.path = (String)attr.getValue();
        else
            return super.init(obj);
        return null;
    }

    public IObjectDesc getObjectDescriptor()
    {
        if (!DEF_PATH.equals(path))
        {
            ParamDescriptor paramDescriptor =new ParamDescriptor(desc);
            java.util.List<IParam> params = paramDescriptor.getParams();
            for (int i = 0; i < params.size();)
            {
                IParam iParam = params.get(i);
                if (PATH_TAG.equalsIgnoreCase(iParam.getName()))
                    params.remove(i);
                else
                    i++;
            }
            params.add(new DefAttrImpl(PATH_TAG,path));
            paramDescriptor.setParams(params);
            return paramDescriptor;
        }
        return desc;
    }

}
