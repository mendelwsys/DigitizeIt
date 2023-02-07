package com.mwlib.app.utils;

import com.mwlib.app.storages.mem.IPathDefContainer;
import ru.ts.factory.IInitAble;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;
import ru.ts.toykernel.app.xml.IXMLBuilderContext;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.drawcomp.IDrawObjRule;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.proj.xml.IXMLProjBuilder;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.xml.IXMLBuilder;
import ru.ts.xml.IXMLObjectDesc;

import java.util.LinkedList;
import java.util.List;
/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 06.04.14
 * Time: 15:42
 * Утилиты оперирования динамическим слоем проекта
 */
public class LayerUtils
{
    public static final String VARCNT = "VARCNT";
    public static final String CNT_HOLDER = "$V$";
    public static final String RM_MARK="RM_MARK";

    private String templateXML;

    public LayerUtils(String templateXML)
    {
        this.templateXML=templateXML;
    }



    public List<ILayer> addLayerStorage(IProjContext ctx, IPathDefContainer srcStor,IXMLProjBuilder builder4Update) throws Exception
    //Сначала создаем контейнер для хранения данных
    {
        String templateXML;
        int ix=0;
        {
            IObjectDesc desc = ctx.getObjectDescriptor();
            List<IParam> params = desc.getParams();
            br:
            {
                for (IParam param : params)
                {
                    if (VARCNT.equalsIgnoreCase(param.getName()))
                    {

                        ix=Integer.parseInt((String)param.getValue());
                        ix++;
                        param.setValue(String.valueOf(ix));
                        break br;
                    }
                }
                params.add(new DefAttrImpl(VARCNT,String.valueOf(ix)));
                desc.setParams(params);
            }
            templateXML=this.templateXML.replace(CNT_HOLDER,String.valueOf(ix));
        }

        IXMLBuilderContext templates = InitAbleUtils.getIXMLBuilderContext(templateXML);

        INodeStorage mainStorage = (INodeStorage)ctx.getStorage();
        IXMLObjectDesc storageDesc = (IXMLObjectDesc)mainStorage.getObjectDescriptor();

        IXMLBuilder<IInitAble> storageBuilder4update = builder4Update.getBuilderContext().getBuilderByTagName(KernelConst.STORAGE_TAGNAME);

        List<INodeStorage> storagesTempl = templates.getBuilderByTagName(KernelConst.STORAGE_TAGNAME).getLT();
        for (INodeStorage storage : storagesTempl)
        {
            if (storage instanceof IPathDefContainer)
            {
                ((IPathDefContainer)storage).setPathDef(srcStor.getPathDef());

                DefAttrImpl e = new DefAttrImpl(KernelConst.STORAGE_TAGNAME, storage);
                List<IParam> params = storageDesc.getParams();
                params.add(e);
                storageDesc.setParams(params);
                mainStorage.init(new Object[]{e});

                IXMLObjectDesc objectDescriptor = (IXMLObjectDesc) storage.getObjectDescriptor();

                setInitOrder(ix, objectDescriptor);

                storageBuilder4update.getParamDescs().add(objectDescriptor);
                storageBuilder4update.getInitables().put(storage.getObjName(),storage);
                break;
            }
        }



        IXMLBuilder<IInitAble> layerBuilder4update = builder4Update.getBuilderContext().getBuilderByTagName(KernelConst.LAYER_TAGNAME);

        IXMLBuilder<IInitAble> ruleBuilder4update = builder4Update.getBuilderContext().getBuilderByTagName(KernelConst.RULE_TAGNAME);
        IXMLBuilder<IInitAble> filterBuilder4update = builder4Update.getBuilderContext().getBuilderByTagName(KernelConst.FILTER_TAGNAME);

        List<ILayer> addLayers = templates.getBuilderByTagName(KernelConst.LAYER_TAGNAME).getLT();
        List<IParam> ctxLrParams=new LinkedList<IParam>();

        for (ILayer layer : addLayers)
        {
            DefAttrImpl storageParam = new DefAttrImpl(KernelConst.STORAGE_TAGNAME, mainStorage);
            IObjectDesc lrDesc = layer.getObjectDescriptor();
            List<IParam> params = lrDesc.getParams();
            params.add(storageParam);
            lrDesc.setParams(params);
            layer.init(storageParam);
            ctxLrParams.add(new DefAttrImpl(KernelConst.LAYER_TAGNAME, layer));
        }

        IXMLObjectDesc ctxDesc = (IXMLObjectDesc) ctx.getObjectDescriptor();
        for (ILayer layer : addLayers)
        {
            layer.setFilters(layer.getFilters());
            IXMLObjectDesc layerObjectDescriptor = (IXMLObjectDesc) layer.getObjectDescriptor();
            layerObjectDescriptor.setInitOrder(storageDesc.getInitOrder()+1);

            layerBuilder4update.getParamDescs().add(layerObjectDescriptor);
            layerBuilder4update.getInitables().put(layer.getObjName(),layer);

            IDrawObjRule drawRule = layer.getDrawRule();
            IXMLObjectDesc ruleObjectDescriptor = (IXMLObjectDesc) drawRule.getObjectDescriptor();
            setInitOrder(ix,ruleObjectDescriptor);
            ruleBuilder4update.getParamDescs().add(ruleObjectDescriptor);
            ruleBuilder4update.getInitables().put(drawRule.getObjName(),drawRule);

            List<IBaseFilter> filters = layer.getFilters();
            for (IBaseFilter filter : filters)
            {
                IXMLObjectDesc filterObjectDescriptor = (IXMLObjectDesc) filter.getObjectDescriptor();
                setInitOrder(ix,filterObjectDescriptor);
                filterBuilder4update.getParamDescs().add(filterObjectDescriptor);
                filterBuilder4update.getInitables().put(filter.getObjName(),filter);
            }
        }

        List<IParam> params = ctxDesc.getParams();
        params.addAll(ctxLrParams);
        ctxDesc.setParams(params);

        ctx.init(ctxLrParams.toArray(new IParam[ctxLrParams.size()]));
//        setInitOrder(ix,ctxDesc);
        return addLayers;
    }

    private void setInitOrder(int ix, IXMLObjectDesc objectDescriptor) {
        objectDescriptor.setInitOrder(objectDescriptor.getInitOrder()-(ix+1)*1000);
    }




}
