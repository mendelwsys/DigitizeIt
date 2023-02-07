package com.mwlib.app.utils;

import org.xml.sax.InputSource;
import ru.ts.factory.IInitAble;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;
import ru.ts.toykernel.app.xml.IXMLBuilderContext;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.proj.xml.IXMLProjBuilder;
import ru.ts.toykernel.proj.xml.def.XMLProjBuilder;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.xml.IXMLBuilder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 12.04.14
 * Time: 11:24
 * Утилиты для объектов наследующих интерфейс IInitAble
 */
public class InitAbleUtils
{
    public static IXMLBuilderContext getIXMLBuilderContext(String templateXML) throws Exception
    {
        SAXParser parser= SAXParserFactory.newInstance().newSAXParser();
        Reader rd=new StringReader(templateXML);//new InputStreamReader(in,"WINDOWS-1251");

        XMLProjBuilder builder = new XMLProjBuilder();
        parser.parse(new InputSource(rd), builder.getProjBuilderHandler(parser.getXMLReader()));
        return builder.getBuilderContext();
    }

    public static void saveProject2File(IXMLProjBuilder builder,File xmldesc) throws Exception
    {
        OutputStream os = null;
        try
        {
            if (xmldesc != null)
            {
                os =  new FileOutputStream(xmldesc);
                IXMLBuilderContext context = builder.getBuilderContext();
                final String enc = "WINDOWS-1251";
                os.write(context.getFullXML(enc, false).getBytes(enc));
                os.flush();
            }
        }
        finally
        {
            try
            {
                if (os!=null)
                    os.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static Map<String, Object> getMapParams(IAttrs map) {
        Map<String,Object> lrParams= new HashMap<String, Object>();
        for (IDefAttr iDefAttr : map.values())
            lrParams.put(iDefAttr.getName(),iDefAttr.getValue());
        return lrParams;
    }

    public static void setAttrByParams(IAttrs attrs, Map<String, Object> params)
    {
        for (String pname : params.keySet())
        {
            IDefAttr attr = attrs.get(pname);
            if (attr!=null)
            {
                attr.setValue(params.get(pname));
                attrs.put(pname,attr);
            }
            else
                attrs.put(pname,new DefAttrImpl(pname,params.get(pname)));
        }
    }
    public static void setInitAbleByParams(IInitAble initAble,Map<String,Object> name2Object) throws Exception
    {
        List<IParam> addParams = new ArrayList<IParam>(name2Object.size());
        for (String name : name2Object.keySet())
            addParams.add(new DefAttrImpl(name,name2Object.get(name)));
        setInitAbleByParams(initAble,addParams);
    }

    public static List<IParam> removeParamFromInitAble(IInitAble initAble,IParam addParams) throws Exception
    {
        return removeParamFromInitAble(initAble, Arrays.asList(addParams));
    }

    public static List<IParam> removeParamFromInitAble(IInitAble initAble, List<IParam> addParams) throws Exception
    {
        IObjectDesc desc = initAble.getObjectDescriptor();
        List<IParam> params = desc.getParams();

        List<IParam> rv=new LinkedList<IParam>(params);
        for (IParam addParam : addParams)
        {
            for (IParam param : params)
            {
                if (addParam.getName().equals(param.getName()))
                    rv.remove(param);
            }
        }
        desc.setParams(rv);
        return rv;
    }

    public static void addInitAbleByParams(IInitAble initAble,List<IParam> addParams) throws Exception
    {
        IObjectDesc desc = initAble.getObjectDescriptor();
        List<IParam> params = desc.getParams();
        for (IParam addParam : addParams)
            params.add(addParam);
        desc.setParams(params);
        initAble.init(addParams.toArray(new IParam[addParams.size()]));
    }

    public static void setInitAbleByParams(IInitAble initAble,List<IParam> addParams) throws Exception
    {
        IObjectDesc desc = initAble.getObjectDescriptor();
        List<IParam> params = desc.getParams();

        for (IParam addParam : addParams)
        {
           br:
            {
                for (IParam param : params)
                {
                    if (addParam.getName().equals(param.getName()))
                    {
                       param.setValue(addParam.getValue());
                       break br;
                    }
                }
                params.add(addParam);
            }
        }
        desc.setParams(params);
        initAble.init(addParams.toArray(new IParam[addParams.size()]));
    }


    public static void removeStorage(INodeStorage removeStorage,IProjContext projContext)
    {


        INodeStorage mainStorage=(INodeStorage)projContext.getStorage();
        Collection<INodeStorage> childStorages = mainStorage.getChildStorages();
        childStorages.remove(removeStorage);
        IObjectDesc mainStorageDesc = mainStorage.getObjectDescriptor();
        List<IParam> mainStorageParams = mainStorageDesc.getParams();
        for (int i = 0; i < mainStorageParams.size(); i++)
        {
            IParam param = mainStorageParams.get(i);
            if (
                    param.getValue() instanceof IInitAble
                    &&
                    removeStorage.getObjName().equals(((IInitAble) param.getValue()).getObjName())
                )
            {
                    mainStorageParams.remove(i);
                    break;
            }
        }
        mainStorageDesc.setParams(mainStorageParams);
    }

    public static void removeByParam(IParam rmMark, IXMLProjBuilder builder,String tag_name)
    {
        IXMLBuilder builderByTagName = builder.getBuilderContext().getBuilderByTagName(tag_name);
        if (builderByTagName!=null)
            removeByParam(rmMark, builderByTagName.getInitables());
    }

    public static void removeByParam(IParam rmMark, Map<String,IInitAble> lrs)
    {
        if (lrs!=null)
        {
            Set<String> keys=new HashSet<String>(lrs.keySet());
            for (String key : keys)
            {
                IInitAble rmCandidate = lrs.get(key);
                if (InitAbleUtils.isInitAbleContainsParam(rmCandidate, rmMark))
                    lrs.remove(key);
            }
        }
    }

    public static boolean isInitAbleContainsParam(IInitAble initAble,IParam param)
    {
        IParam _param=getParamByDescriptor(initAble,param.getName());
        return _param!=null && _param.getValue()!=null && _param.getValue().equals(param.getValue());
    }

    public static IParam getParamByDescriptor(IInitAble initAble,String name)
    {
        IObjectDesc projectDesc = initAble.getObjectDescriptor();
        List<IParam> projParams = projectDesc.getParams();
        for (IParam param : projParams)
            if (name.equals(param.getName()))
                return param;
        return null;
    }

//    public static void moveLayrInProject(ILayer lr,IProjContext projContext)
//    {
//        IObjectDesc projectDesc = projContext.getObjectDescriptor();
//        List<IParam> projParams = projectDesc.getParams();
//        int ixP = 0;
//        for ( paramsSize = projParams.size(); ix < paramsSize; ix++)
//        {
//            IParam param = projParams.get(ix);
//            if (
//                    param.getValue() instanceof IInitAble
//                    &&
//                    lr.getObjName().equals(((IInitAble) param.getValue()).getObjName())
//                )
//            {
//
//                    break;
//            }
//        }
//        projectDesc.setParams(projParams);
//    }

    public static void reOrderLayerProject(IProjContext projContext)
    {
        IObjectDesc projectDesc = projContext.getObjectDescriptor();
        List<ILayer> lrList = projContext.getLayerList();
        Map<String,Integer> name2Order = new HashMap<String, Integer>();

        for (int i = 0, lrListSize = lrList.size(); i < lrListSize; i++)
        {
            ILayer iLayer = lrList.get(i);
            name2Order.put(iLayer.getObjName(),i);
        }
        Map<Integer,IParam> order2Param = new TreeMap<Integer, IParam>();

        List<IParam> projParams = projectDesc.getParams();
        List<IParam> newprojParams = new LinkedList<IParam>();
        for (IParam projParam : projParams)
        {
            if (!projParam.getName().equals(KernelConst.LAYER_TAGNAME))
                newprojParams.add(projParam);
            else
            {
                ILayer lr=(ILayer)projParam.getValue();
                String lrName=lr.getObjName();
                order2Param.put(name2Order.get(lrName),projParam);
            }
        }
        for (Integer order : order2Param.keySet())
            newprojParams.add(order2Param.get(order));
        projectDesc.setParams(newprojParams);
    }

    public static void removeFromProject(ILayer lr,IProjContext projContext)
    {
        IObjectDesc projectDesc = projContext.getObjectDescriptor();
        List<IParam> projParams = projectDesc.getParams();
        for (int i = 0, paramsSize = projParams.size(); i < paramsSize; i++)
        {
            IParam param = projParams.get(i);
            if (
                    param.getValue() instanceof IInitAble
                    &&
                    lr.getObjName().equals(((IInitAble) param.getValue()).getObjName())
                )
            {
                    projParams.remove(i);
                    break;
            }
        }
        projectDesc.setParams(projParams);
    }


}
