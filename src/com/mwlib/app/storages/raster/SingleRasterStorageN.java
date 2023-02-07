package com.mwlib.app.storages.raster;

import com.mwlib.app.plugins.shp.PoTrace2Shp;
import com.mwlib.utils.raster.PartialImageReader;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.converters.IRProjectConverter;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.storages.raster.BindStruct;
import ru.ts.toykernel.storages.raster.RasterStorage;
import ru.ts.toykernel.xml.def.ParamDescriptor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 02.01.14
 * Time: 19:21
 * com.mwlib.app.storages.raster.SingleRasterStorageN
 */
public class SingleRasterStorageN extends RasterStorage
    implements IRasterContainerEx
{
    public static final String PATHIMG_TAG = "imgpath";
    public static final String  RESOLUTION_TAG ="RESOLUTION";

    public SingleRasterStorageN() {
    }

    public SingleRasterStorageN(IRProjectConverter converter, List<BindStruct> bstr_list, INodeStorage parent, String nodeId) {
        super(converter, bstr_list, parent, nodeId);
    }


    public MPoint getImageSize() throws Exception
    {
        if (bstr_list!=null && bstr_list.size()>0)
        {
            DynamicBindStruct bindStruct = (DynamicBindStruct)bstr_list.get(0);
            return new MPoint(bindStruct.totalsize);
        }
        else
            return null;
    }


    private double resolution = PoTrace2Shp.DEF_RESOLUTION;
    public double getResolution() {
        return resolution;
    }

    public void setResolution(double resolution) {
        this.resolution=resolution;
    }


    public BufferedImage getImageByRectangle(Rectangle rectangle) throws Exception
    {
        DynamicBindStruct bindStruct = (DynamicBindStruct)bstr_list.get(0);
        PartialImageReader pr=bindStruct.pr;
        if (pr!=null)
            return pr.getImageByRectangle(rectangle);
       throw new Exception("Wrong state of RasterStorage partial reader is null");
    }


    public BindStruct reInitByRaster(File raster) throws Exception
    {
        if (raster==null)
        {
            DynamicBindStruct bindStruct = (DynamicBindStruct)bstr_list.get(0);
            bindStruct.pictdir=bindStruct.pathdesc=null;
            bindStruct.loadDesc();
            return bindStruct;
        }
        else
        {
            DynamicBindStruct bindStruct = (DynamicBindStruct)bstr_list.get(0);
            bindStruct.reBindByRaster(raster);
            return bindStruct;
        }
    }



    public class DynamicBindStruct extends BindStruct
    {
        PartialImageReader pr;
        public void reBindByRaster(File raster) throws Exception
        {
            try
            {
                String flname = raster.getName();
                String extensionName = flname.substring(flname.lastIndexOf('.') + 1);
                InputStream inputStream = new FileInputStream(raster);
                resetPartialReader();
                pr = new PartialImageReader(inputStream,extensionName);
                Point szXY=pr.getImageSize();

                totalsize=new MPoint(szXY.getX(),szXY.getY());
                picsize=new MPoint(szXY.getX(),szXY.getY());
                flnames = new String[1][1];
                pictdir=raster.getParent();
                flnames[0][0]= flname;
            }
            catch (Throwable e)
            {
                resetPartialReader();
            }
        }

        private void resetPartialReader() {
            if (pr!=null)
            {
                pr.free();
                pr=null;
            }
        }

        public void loadDesc()
                throws Exception
        {
            try
            {

                if (pathdesc!=null && pathdesc.length()>0)
                {
                    super.loadDesc();
                }
                else  if (this.flnames==null || this.flnames.length==0 || this.flnames[0].length==0)
                {
                    this.flnames=new String[0][];
                    this.totalsize=this.picsize=new MPoint(0,0);
                }
                else
                {
                    pathdesc=this.pictdir;
                    if (pathdesc==null)
                    {
                        this.flnames=new String[0][];
                        this.totalsize=this.picsize=new MPoint(0,0);
                        resetPartialReader();
                    }
                }
            } catch (Exception e)
            {
                throw new Exception("Can't load description of raster by path " + pathdesc, e);
            }
        }
    }

    public Object init(Object obj) throws Exception
	{
        IParam attr=(IParam)obj;
        if (attr.getName().equalsIgnoreCase(PATHIMG_TAG))
        {
            Object val=attr.getValue();
            if (val!=null)
            {
                String pathVal=val.toString();
                File file = new File(pathVal);
                if (file.isFile())
                {
                    if (currentBindStruct==null)
                        currentBindStruct=createBindStruct();
                    ((DynamicBindStruct)currentBindStruct).reBindByRaster(file);
                }
            }
            return null;
        }
        else if (attr.getName().equalsIgnoreCase(RESOLUTION_TAG))
        {
            try
            {
                resolution =Double.parseDouble((String)attr.getValue());
            }
            catch (NumberFormatException e)
            {//
            }
        }


        return obj;
	}

    protected BindStruct createBindStruct()
    {
        return new DynamicBindStruct();
    }




    public IObjectDesc getObjectDescriptor()
    {
        if (bstr_list.size()>0)
        {
            ParamDescriptor paramDescriptor =new ParamDescriptor(desc);

            List<IParam> params = paramDescriptor.getParams();
            DynamicBindStruct bindStruct = (DynamicBindStruct)bstr_list.get(0);
            if (bindStruct.pictdir!=null && bindStruct.flnames.length>0 && bindStruct.flnames[0].length>0)
            {
                String imgPath = bindStruct.pictdir + File.separator + bindStruct.flnames[0][0];
                br:
                {
                    for (IParam param : params)
                        if (PATHIMG_TAG.equalsIgnoreCase(param.getName()))
                        {
                            param.setValue(imgPath);
                            break br;
                        }
                    params.add(new DefAttrImpl(PATHIMG_TAG,imgPath));
                }
            }
            else
            {
                for (int i = 0; i < params.size();)
                {
                    IParam param=params.get(i);
                    if (PATHIMG_TAG.equalsIgnoreCase(param.getName()))
                        params.remove(i);
                    else
                        i++;
                }
            }

            {
                String sResolution = String.format(Locale.ENGLISH, "%.2f", resolution);
                br:
                {
                    for (IParam param : params)
                        if (RESOLUTION_TAG.equalsIgnoreCase(param.getName()))
                        {
                            param.setValue(sResolution);
                            break br;
                        }
                     params.add(new DefAttrImpl(RESOLUTION_TAG,sResolution));
                }
            }
            paramDescriptor.setParams(params);
            return paramDescriptor;
        }
        return desc;
    }



}
