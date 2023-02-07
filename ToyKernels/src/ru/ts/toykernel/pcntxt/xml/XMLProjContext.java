package ru.ts.toykernel.pcntxt.xml;

import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.pcntxt.IMetaInfoBean;
import ru.ts.toykernel.pcntxt.MetaInfoBean;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.DefNameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.utils.data.Pair;
import ru.ts.utils.gui.elems.IViewProgress;
import ru.ts.utils.gui.elems.EmptyProgress;
import ru.ts.gisutils.proj.transform.IMapTransformer;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * XML loadable project context
 * ru.ts.toykernel.pcntxt.xml.XMLProjContext
 */
public class XMLProjContext extends BaseInitAble implements IProjContext
{

	protected Map<Pair<String, Boolean>, IMapTransformer> transformers=new HashMap<Pair<String, Boolean>, IMapTransformer>();//projection transformers
	protected IMetaInfoBean metaInfo;//meta information of project

	protected INodeStorage storage;//the main storage
	protected List<ILayer> layers=new LinkedList<ILayer>();//layers list

	protected INameConverter nameConverter;
	protected String projectlocation="";


	protected IViewProgress progress;


	public String getProjectlocation()
	{
		return projectlocation;
	}

	/* (non-Javadoc)
     * @see ru.ts.toykernel.pcntxt.IProjContext#getNameConverter()
     */
	public INameConverter getNameConverter()
	{
		return nameConverter;
	}

	/* (non-Javadoc)
     * @see ru.ts.toykernel.pcntxt.IProjContext#getStorage()
     */
	public IBaseStorage getStorage()
	{
		return storage;
	}

	/* (non-Javadoc)
     * @see ru.ts.toykernel.pcntxt.IProjContext#setProjectlocation(String projectlocation)
     */
	public void setProjectlocation(String projectlocation)
	{
		this.projectlocation = projectlocation;
	}

	public XMLProjContext()
	{
	}
	/**
	 * XML Project implementation
	 * @param nameConverter - converter of names
	 * @param projectlocation - loaction of project
	 * @param progress - progress indicator
	 */
	public XMLProjContext(
			INameConverter nameConverter,
			String projectlocation,
			IViewProgress progress
	)
	{
		this.projectlocation = projectlocation;
		if (progress==null)
			progress=new EmptyProgress();
		this.progress = progress;

		if (nameConverter == null)
			nameConverter = new DefNameConverter();
		this.nameConverter = nameConverter;
	}

	/* (non-Javadoc)
     * @see ru.ts.toykernel.pcntxt.IProjContext#getLayerList()
     */
	public List<ILayer> getLayerList()
	{
		return layers;
	}

	/* (non-Javadoc)
     * @see ru.ts.toykernel.pcntxt.IProjContext#getMapTransformers()
     */
	public Map<Pair<String, Boolean>, IMapTransformer> getMapTransformers()
	{
		return transformers;
	}

	/* (non-Javadoc)
     * @see ru.ts.toykernel.pcntxt.IProjContext#getProjMetaInfo()
     */
	public IMetaInfoBean getProjMetaInfo()
	{
		return metaInfo;
	}

	public Object[] init(Object ...objs)
			throws Exception
	{
		super.init(objs);
		if (nameConverter==null)
			nameConverter = new DefNameConverter();
		if (metaInfo==null)
			metaInfo=new MetaInfoBean();
		return null;
	}

	public Object init(Object obj) throws Exception
	{
		IDefAttr attr=(IDefAttr)obj;
		if (attr!=null)
		{
			if (attr.getName().equalsIgnoreCase(KernelConst.STORAGE_TAGNAME))
				storage= (INodeStorage) attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.LAYER_TAGNAME))
				layers.add((ILayer)attr.getValue());
			else  if (attr.getName().equalsIgnoreCase(KernelConst.META_TAGNAME))
				metaInfo= (IMetaInfoBean) attr.getValue();
			else  if (attr.getName().equalsIgnoreCase(KernelConst.NAMECONVERTER_TAGNAME))
				nameConverter=(INameConverter)attr.getValue();
			else  if (attr.getName().equalsIgnoreCase(KernelConst.TRANSFORMER_TAGNAME))
			{
				IMapTransformer transformer= (IMapTransformer) attr.getValue();
				Pair<String, String> pr = transformer.getTransformerType();
				if (pr.second.equals(getProjMetaInfo().getS_MapUnitsName()))
					getMapTransformers().put(new Pair<String, Boolean>(pr.first, true), transformer);
				else if (pr.first.equals(getProjMetaInfo().getS_MapUnitsName()))
					getMapTransformers().put(new Pair<String, Boolean>(pr.second, false), transformer);
			}
		}
		return null;
	}
}
