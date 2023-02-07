package ru.ts.toykernel.proj.stream.def;

import com.mwlib.utils.Enc;
import ru.ts.toykernel.drawcomp.rules.def.stream.CnStyleRuleFactory;

import ru.ts.utils.logger.SimpleLogger;
import ru.ts.utils.gui.elems.IViewProgress;
import ru.ts.utils.gui.elems.EmptyProgress;

import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.drawcomp.IDrawObjRule;
import ru.ts.toykernel.drawcomp.layers.def.DrawOnlyLayer;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.attrs.AObjAttrsFactory;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.storages.mem.*;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.filters.stream.NodeFilterFactory;
import ru.ts.factory.IFactory;
import ru.ts.factory.DefIFactory;
import ru.ts.stream.ISerializer;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.DefNameConverter;
import ru.ts.toykernel.pcntxt.IMetaInfoBean;
import ru.ts.toykernel.pcntxt.MetaInfoBean;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.converters.ConvB64Initializer;
import ru.ts.toykernel.converters.CrdConverterFactory;
import ru.ts.gisutils.proj.transform.IMapTransformer;
import ru.ts.gisutils.proj.transform.TrasformerFactory;
import ru.ts.utils.data.Pair;

import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import java.io.*;

/**
 * Memory Project implementation
 */
public class StreamProjImpl extends BaseInitAble implements IStreamAbleProj
{


	protected INodeStorage storage;//the main storage
	protected Map<Pair<String, Boolean>, IMapTransformer> transformers;//projection transformers

	protected List<ILayer> layers = new LinkedList<ILayer>();

	public ConvB64Initializer getConvInitializer()
	{
		return convInitializer;
	}

	protected IMetaInfoBean metaInfo;
	protected ConvB64Initializer convInitializer;

	protected INameConverter nameConverter;
	protected String projectlocation;
	protected IViewProgress progress;


	protected IFactory<IDrawObjRule> ruleFactory;//rule factory
	protected IFactory<IBaseFilter> filterFactory;//filter factory
	protected AObjAttrsFactory objectAttrsFactory;
	protected IFactory<INodeStorage> storagesfactory;
	protected IFactory<ILayer> layerFactory;

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

	/**
	 * Simple Project implementation
	 *
	 * @param projectlocation	- loaction of project
	 * @param ruleFactory		- draw rule factory
	 * @param filterFactory	  - filter factory
	 * @param storagesfactory	-storages factory
	 * @param layerFactory	   - layer factory
	 * @param objectAttrsFactory - factory of object attributes
	 * @param nameConverter	  - converter of names
	 * @param progress		   - progress indicator
	 */
	public StreamProjImpl(
			String projectlocation,
			IFactory<IDrawObjRule> ruleFactory,
			IFactory<IBaseFilter> filterFactory,
			IFactory<INodeStorage> storagesfactory,
			IFactory<ILayer> layerFactory,
			AObjAttrsFactory objectAttrsFactory,
			INameConverter nameConverter,
			IViewProgress progress
	)
	{
		this.projectlocation = projectlocation;

		if (layerFactory == null)
			layerFactory = new DefIFactory<ILayer>()
			{
				public ILayer createByTypeName(String typeStorage) throws Exception
				{
					return new DrawOnlyLayer();
				}
			};
		this.layerFactory = layerFactory;

		this.objectAttrsFactory = objectAttrsFactory;
		if (progress == null)
			progress = new EmptyProgress();
		this.progress = progress;

		if (storagesfactory == null)
			storagesfactory = new DefIFactory<INodeStorage>()
			{
				public INodeStorage createByTypeName(String typeStorage) throws Exception
				{
					if (typeStorage.equalsIgnoreCase(NodeStorageImpl.TYPENAME))
						return new NodeStorageImpl();
					return new MemStorageLr2(StreamProjImpl.this.projectlocation);
				}
			};
		this.storagesfactory = storagesfactory;

		if (ruleFactory == null)
			ruleFactory = new CnStyleRuleFactory();
		this.ruleFactory = ruleFactory;

		if (nameConverter == null)
			nameConverter = new DefNameConverter();
		this.nameConverter = nameConverter;

		if (filterFactory == null)
			filterFactory = new NodeFilterFactory();
		this.filterFactory = filterFactory;
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


	/* (non-Javadoc)
     * @see ru.ts.stream.ISerializer#saveToStream()
     */
	public void saveToStream(DataOutputStream dos) throws Exception
	{
		progress.setTittle(Enc.get("$194"));
		saveMetaInfo(dos);
		storage.saveToStream(dos);
		savelayers(dos);
	}

//	public void testLoaders() throws Exception
//	{
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		DataOutputStream dos=new DataOutputStream(bos);
//		savelayers(dos);
//		dos.flush();
//		dos.close();
//		loadlayers(new DataInputStream(new ByteArrayInputStream(bos.toByteArray())),emptyProgress);
//	}

	/* (non-Javadoc)
     * @see ru.ts.stream.ISerializer#loadFromStream()
     */

	public void loadFromStream(DataInputStream dis) throws Exception
	{
		layers = loadLayers(dis, null, progress);
		progress.setTittle(Enc.get("$195"));
		progress.setProgress(0.9 * progress.getMaxProgress());
	}


	/**
	 * Load map layers
	 *
	 * @param dis		  - data input stream
	 * @param layers_e	 - list of layers previosly red
	 * @param viewProgress - for show loading progress
	 * @return - list of loaded layers
	 * @throws Exception -
	 */
	public List<ILayer> loadLayers(DataInputStream dis, List<ILayer> layers_e, IViewProgress viewProgress) throws Exception
	{

		if (viewProgress == null)
			viewProgress = new EmptyProgress();

		int val = viewProgress.getMaxProgress();

		long beg = System.currentTimeMillis();


		try
		{
			viewProgress.setCurrentOperation("Loading map MetaInfo");
			loadMetaInfo(dis);
			viewProgress.setProgress(0.05 * val);
			viewProgress.setCurrentOperation("Loading Storage");
			storage = storagesfactory.createByTypeName(NodeStorageImpl.TYPENAME);
			storage.setStoragesfactory(storagesfactory);
			storage.setViewProgress(viewProgress);
			storage.setObjAttrsFactory(objectAttrsFactory);
			storage.setNameConverter(getNameConverter());
			storage.loadFromStream(dis);
			viewProgress.setProgress(0.15 * val);
			viewProgress.setCurrentOperation("Loading Layes");
			loadlayers(dis, viewProgress);
			viewProgress.setProgress(0.8 * val);
		}
		finally
		{
			if (dis != null)
				dis.close();
		}


		SimpleLogger.Singleton.getLoger().getLog().println(
				"read layers = " + (System.currentTimeMillis() - beg) / 1000);
		if (layers_e != null)
		{
			for (ILayer layer : layers)
				layers_e.add(layer);
			return layers_e;
		}
		return layers;
	}

	protected void savelayers(DataOutputStream dos)
			throws Exception
	{
		dos.writeInt(layers.size());
		for (ILayer layer : layers)
		{
			IAttrs lrattr = layer.getLrAttrs();
			dos.writeInt(lrattr.size());
			for (IDefAttr lrAttr : lrattr.values())
			{
				ObjectOutputStream objos = new ObjectOutputStream(dos);
				objos.writeUTF(lrAttr.getName());
				objos.writeObject(lrAttr.getValue());
				objos.flush();
			}
		}

		for (ILayer layer : layers)
		{
			IBaseFilter filter = layer.getFilters().get(0);
			if (filter instanceof ISerializer)
				((ISerializer) filter).saveToStream(dos);
			else
				throw new UnsupportedOperationException("Can't save unserailazable filter");

			IDrawObjRule rule = layer.getDrawRule();
			if (rule instanceof ISerializer)
				((ISerializer) rule).saveToStream(dos);
			else
				throw new UnsupportedOperationException("Can't save unserailazable rule");
		}

	}

	protected void loadlayers(DataInputStream dis, IViewProgress viewProgress)
			throws Exception
	{
		layers.clear();
		List<ILayer> ll = new LinkedList<ILayer>();
		viewProgress.setCurrentOperation("Loading Layers Attributes");
		List<IAttrs> attrlayerlist = new LinkedList<IAttrs>();
		int cnt = dis.readInt();//count of layers
		while (cnt > 0)
		{
			int cntattrs = dis.readInt();
			IAttrs attr = new DefaultAttrsImpl();
			while (cntattrs > 0)
			{
				ObjectInputStream obis = new ObjectInputStream(dis);
				String attrname = obis.readUTF();
				Object val = obis.readObject();
				attr.put(attrname, new DefAttrImpl(attrname, val));
				cntattrs--;
			}
			attrlayerlist.add(attr);
			cnt--;
		}

		viewProgress.setCurrentOperation("Loading styles an create Layers");
		cnt = attrlayerlist.size();
		while (cnt > 0)
		{
			String name = dis.readUTF();
			IBaseFilter filter = filterFactory.createByTypeName(name);
			if (!(filter instanceof ISerializer))
				throw new UnsupportedOperationException("Can't load unserailazable filter");
			((ISerializer) filter).loadFromStream(dis);//Генерируем фильтр из входного потока

			IDrawObjRule rule = ruleFactory.createByTypeName(dis.readUTF());
			if (!(rule instanceof ISerializer))
				throw new UnsupportedOperationException("Can't load unserailazable rule");
			((ISerializer) rule).loadFromStream(dis);

			ILayer layer = layerFactory.createByTypeName(DrawOnlyLayer.TYPENAME);//TODO Заместить параметр вычитанным именем из файла ????

			List<IBaseFilter> filters=new LinkedList<IBaseFilter>();
			if (filter!=null)
				filters.add(filter);
			layer.initLayer(storage, filters, attrlayerlist.get(attrlayerlist.size() - cnt), rule);
			ll.add(layer);
			cnt--;
		}
		layers = ll;
	}


	protected void saveMetaInfo(DataOutputStream dos)
			throws Exception
	{

		//Считываем версию файла
		dos.writeUTF(metaInfo.getFormatVersion());
		dos.writeInt(metaInfo.getMajor());
		dos.writeInt(metaInfo.getMinor());

		//-3.Загрузка цвета рамки
		dos.writeInt(metaInfo.getBoxColor());
		//-2.Загрузка цвета подложки
		dos.writeInt(metaInfo.getBackgroundColor());
		//-1.Загрузка имени проекта
		dos.writeUTF(metaInfo.getProjName());
		//0.Загрузка версии карты
		dos.writeUTF(metaInfo.getS_mapversion());

		//1. Загрузка конвертера и точки привязки
		{
			dos.writeUTF(convInitializer.getS_convertertype());
			dos.writeInt(convInitializer.getB_converter().length);
			dos.write(convInitializer.getB_converter());
			dos.writeInt(convInitializer.getB_currentP0().length);
			dos.write(convInitializer.getB_currentP0());
		}
		//2. Загрузка единиц проекта
		dos.writeUTF(metaInfo.getS_MapUnitsName());
		//3. Загрузка трансляторов в карту
		dos.writeInt(transformers.size());
		for (IMapTransformer iMapTransformer : transformers.values())
			iMapTransformer.saveTransformer(dos);
	}

	protected void loadMetaInfo(DataInputStream dis)
			throws Exception
	{
		//Считываем версию файла
		metaInfo = new MetaInfoBean();

		metaInfo.setFormatVersion(dis.readUTF());
		metaInfo.setMajor(dis.readInt());
		metaInfo.setMinor(dis.readInt());

		if (metaInfo.getMajor() >= 1 && metaInfo.getMinor() >= 4)
		{
			//-3.Загрузка цвета рамки
			metaInfo.setBoxColor(dis.readInt());
			//-2.Загрузка цвета подложки
			metaInfo.setBackgroundColor(dis.readInt());
			//-1.Загрузка имени проекта
			metaInfo.setProjName(dis.readUTF());
			//0.Загрузка версии карты
			metaInfo.setS_mapversion(dis.readUTF());

			//1. Загрузка конвертера и точки привязки
			{
				convInitializer=new ConvB64Initializer(new CrdConverterFactory()); 
				convInitializer.setS_convertertype(dis.readUTF());

				convInitializer.setB_converter(new byte[dis.readInt()]);
				dis.readFully(convInitializer.getB_converter(), 0, convInitializer.getB_converter().length);
				convInitializer.setB_currentP0(new byte[dis.readInt()]);
				dis.readFully(convInitializer.getB_currentP0(), 0, convInitializer.getB_currentP0().length);
			}
			//2. Загрузка единиц проекта
			metaInfo.setS_MapUnitsName(dis.readUTF());
			//3. Загрузка трансляторов в карту
			int lenght = dis.readInt();
			transformers = new HashMap<Pair<String, Boolean>, IMapTransformer>();
			while (lenght > 0)
			{
				IMapTransformer transformer = TrasformerFactory.createTransformer(dis);
				Pair<String, String> pr = transformer.getTransformerType();
				if (transformers != null)
				{

					if (pr.second.equals(metaInfo.getS_MapUnitsName()))
						transformers.put(new Pair<String, Boolean>(pr.first, true), transformer);
					else if (pr.first.equals(metaInfo.getS_MapUnitsName()))
						transformers.put(new Pair<String, Boolean>(pr.second, false), transformer);
				}
				lenght--;
			}
		} else //Old version - GEOPROJECTION
			throw new UnsupportedOperationException();
	}

	public Object init(Object obj) throws Exception
	{
		throw new UnsupportedOperationException();
	}
}
