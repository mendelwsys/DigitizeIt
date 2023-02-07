package ru.ts.toykernel.pcntxt;

import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.DefNameConverter;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.gisutils.proj.transform.IMapTransformer;
import ru.ts.utils.data.Pair;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class SimpleProjContext extends BaseInitAble implements IProjContext
{
	private IMetaInfoBean metainfo= new MetaInfoBean();
	private List<ILayer> layerList;
	private IBaseStorage storage;
	protected Map<Pair<String, Boolean>, IMapTransformer> transformers=new HashMap<Pair<String, Boolean>, IMapTransformer>();//projection transformers
	private INameConverter nameConverter;


	public SimpleProjContext(IMetaInfoBean metainfo,List<ILayer> layerList,IBaseStorage storage,INameConverter nameConverter,Map<Pair<String, Boolean>, IMapTransformer> transformers)
	{
		if (metainfo!=null)
			this.metainfo=metainfo;
		this.layerList = layerList;
		this.storage = storage;
		if (nameConverter == null)
			nameConverter = new DefNameConverter();
		 this.nameConverter=nameConverter;
		if (transformers!=null)
			this.transformers=transformers;

	}
	public List<ILayer> getLayerList()
	{
		return layerList;
	}

	public Map<Pair<String, Boolean>, IMapTransformer> getMapTransformers()
	{
		return transformers;
	}

	public IMetaInfoBean getProjMetaInfo()
	{
		return metainfo;
	}
	public IBaseStorage getStorage()
	{
		return storage;
	}

	public INameConverter getNameConverter()
	{
		if (nameConverter == null)
			nameConverter = new DefNameConverter();
		return nameConverter;
	}

	public String getProjectlocation()
	{
		return "/";
	}

	public void setProjectlocation(String projectlocation)
	{
	}

	public Object init(Object obj) throws Exception
	{
		throw new UnsupportedOperationException();
	}
}
