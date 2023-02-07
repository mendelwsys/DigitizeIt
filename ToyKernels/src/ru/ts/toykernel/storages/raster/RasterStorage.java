package ru.ts.toykernel.storages.raster;

import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.filters.IMBBFilter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.geom.def.RasterObject;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.AObjAttrsFactory;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.converters.CrdConverterFactory;
import ru.ts.toykernel.converters.IRProjectConverter;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.utils.gui.elems.IViewProgress;
import ru.ts.utils.data.Pair;
import ru.ts.factory.IFactory;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;
import ru.ts.xml.IXMLObjectDesc;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.io.*;


/**
 * Растровое хранилище
 */
abstract public class RasterStorage implements INodeStorage,IRasterContainer
{
    public static final String RASTER_TAG = "rast";
    public static final String PATHDESC_TAG = "path";
    public static final String SCALE_TAG = "scale";
    public static final String PR_0_PAR = "pr0";
    public static final String PR_1_PAR = "pr1";
    protected IRProjectConverter converter;
	//Список структур привязки
	protected List<BindStruct> bstr_list = new LinkedList<BindStruct>();
	protected INodeStorage parent;
	protected String nodeId;
	protected IXMLObjectDesc desc;

	public IAttrs getObjAttrs(String curveid) throws Exception
	{
		return getBaseGisByCurveId(curveid).getObjAttrs();
	}

    public BindStruct reInitByRaster(File raster) throws Exception {
        throw new UnsupportedOperationException();
    }

	public RasterStorage()
	{

	}

	public double[] getScaleRange()
	{
		if (bstr_list.size()>0)
			return new double[]{bstr_list.get(0).scaleRange[0],bstr_list.get(bstr_list.size()-1).scaleRange[1]};
		return new double[]{-1,-1};
	}

	public RasterStorage(IRProjectConverter converter, List<BindStruct> bstr_list, INodeStorage parent, String nodeId)
	{
		this.converter = converter;
		this.bstr_list = bstr_list;
		if (bstr_list == null)
			bstr_list = new LinkedList<BindStruct>();
		this.parent = parent;
		this.nodeId = nodeId;
	}

	public int getObjectsCount()
	{
		try
		{
			Pair<BindStruct, Integer> pr = getCurrentStruct();
			String[][] flnames=pr.first.flnames;
			int cnt=0;
			for (String[] flname : flnames)
			{
				if (flname!=null)
					for (String fl : flname)
						if (fl != null)
							cnt++;
			}
			return cnt;
		}
		catch (Exception e)
		{
			throw new UnsupportedOperationException(e);
		}
	}

	public long getLastModified()
	{
		return -1;
	}

	public IBaseStorage filter(IBaseFilter filter) throws Exception
	{
		if (filter == null)
			return this;
		throw new UnsupportedOperationException();
	}

	public IBaseGisObject getBaseGisByCurveId(String curvId) throws Exception
	{
		return getGisObject(getIndexByCurveId(curvId));
	}

	protected ILinearConverter getRaster2SyncConverter(BindStruct bstr)
	{
		List<ILinearConverter> rv = new LinkedList<ILinearConverter>();

		double scalex = (bstr.lpnt[0].x - bstr.lpnt[1].x) / (bstr.rpnt[0].x - bstr.rpnt[1].x);
		double scaley = (bstr.lpnt[0].y - bstr.lpnt[1].y) / (bstr.rpnt[0].y - bstr.rpnt[1].y);
		rv.add(new CrdConverterFactory.ScaledConverter(new MPoint(scalex, scaley)));

		double px = -scalex * bstr.rpnt[0].x + bstr.lpnt[0].x;
		double py = -scaley * bstr.rpnt[0].y + bstr.lpnt[0].y;
		rv.add(new CrdConverterFactory.ShiftConverter(new MPoint(-px, -py)));

		return new CrdConverterFactory.ChainConverter(rv);
	}

	protected ILinearConverter getRaster2ProjConverter(BindStruct bstr) throws Exception
	{
		ILinearConverter r2s = getRaster2SyncConverter(bstr);
		r2s.getConverterChain().add(new CrdConverterFactory.InverseConverter(converter.getSrc2SyncConverter()));
		return r2s;
	}

	public ILinearConverter getRaster2DstConverter(BindStruct bstr) throws Exception
	{
		ILinearConverter r2s = getRaster2SyncConverter(bstr);
		r2s.getConverterChain().add(converter.getSync2DstConverter());
		return r2s;
	}

	public Iterator<IBaseGisObject> filterObjs(IBaseFilter filter) throws Exception
	{
		if (filter instanceof IMBBFilter)
		{


			MRect proj_rect = ((IMBBFilter) filter).getRect(); //Это координатный прмоугольник в координатах проекта


//			//++DEBUG
//			{
//			//проверка нулевой точки растра сначала переводим ее бля в точку проекта и обратно в растр
//				BindStruct bindStruct = getCurrentStruct().first;
//				ILinearConverter r2pr = getRaster2ProjConverter(bindStruct);
//				Point2D.Double projp = r2pr.getDstPointByPointD(new MPoint());
//
//				ILinearConverter r2draw = getRaster2DstConverter(bindStruct);
//				Point2D.Double drwp = r2draw.getDstPointByPointD(new MPoint());
//				Point2D.Double drwp2 = converter.getDstPointByPointD(new MPoint(projp));
//
//				double dx=drwp.x-drwp2.x;
//				double dy=drwp.y-drwp2.y;
//				System.out.println("dx = " + dx+" dy = " + dy);
//			}
//			//--DEBUG



			//Гы, теперь получим прямоугольник в координатах растра
			Pair<BindStruct, Integer> pr = getCurrentStruct();
			ILinearConverter rast2proj = getRaster2ProjConverter(pr.first);
			MRect rrect = rast2proj.getRectByDstRect(proj_rect, null);
			MPoint totalsize = pr.first.totalsize;
			int iXstart = (int) Math.floor(Math.max(rrect.p1.x, 0) / pr.first.picsize.x);
			int iXEnd = (int) Math.ceil(Math.min(rrect.p4.x, totalsize.x) / pr.first.picsize.x);

			int iYstart = (int) Math.floor(Math.max(rrect.p1.y, 0) / pr.first.picsize.y);
			int iYEnd = (int) Math.ceil(Math.min(rrect.p4.y, totalsize.y) / pr.first.picsize.y);

			List<IBaseGisObject> rl = new LinkedList<IBaseGisObject>();

			for (int iX = iXstart; iX < iXEnd; iX++)
				for (int jY = iYstart; jY < iYEnd; jY++)
				{
					RasterObject robj = getGisObject(new int[]{pr.second, iX, jY});
					rl.add(robj);
				}
			return rl.iterator();
		}
		throw new UnsupportedOperationException();
	}

	public RasterObject getGisObject(int[] indexobj)
			throws Exception
	{

		BindStruct bstr = bstr_list.get(indexobj[0]);
		ILinearConverter rast2proj = getRaster2ProjConverter(bstr);


		MPoint pt1 = new MPoint(indexobj[1] * bstr.picsize.x, indexobj[2] * bstr.picsize.y);
		MPoint pt2 = new MPoint((indexobj[1] + 1) * bstr.picsize.x, (indexobj[2] + 1) * bstr.picsize.y);

		//Получить точки проекта по точкам растра
		pt1 = new MPoint(rast2proj.getDstPointByPointD(pt1));
		pt2 = new MPoint(rast2proj.getDstPointByPointD(pt2));

//		//++DEBUG
//		{
//			Point2D.Double drwp1 = converter.getDstPointByPointD(new MPoint(pt1));
//			Point2D.Double drwp2 = converter.getDstPointByPointD(new MPoint(pt2));
//		}
//		//--DEBUG

		return createRasterObject(indexobj, pt1, pt2);
	}

	protected RasterObject createRasterObject(int[] indexobj, MPoint pt1, MPoint pt2)
			throws Exception
	{
		return new RasterObject(new MPoint(Math.min(pt1.x, pt2.x), Math.min(pt1.y, pt2.y)),
				new MPoint(Math.max(pt1.x, pt2.x), Math.max(pt1.y, pt2.y)),
				getImageUrl2NameRequest(indexobj), getCurveIdByIndex(indexobj[0], indexobj[1], indexobj[2]),this);
	}

	public Pair<String, String> getImageUrl2NameRequest(int[] indexobj) throws Exception
	{

		if (indexobj[0] < 0 || indexobj[0] > bstr_list.size())
			throw new Exception("Raster images not set or set incorrectly");

		BindStruct bstr = bstr_list.get(indexobj[0]);

		String fname = bstr.flnames[indexobj[2]][indexobj[1]];
		return new Pair<String, String>(bstr.pictdir + "/" + fname, fname);
	}

	protected int[] getIndexByCurveId(String curveId)
	{
		String[] xy = curveId.split(" ");
		return new int[]{Integer.parseInt(xy[0]), Integer.parseInt(xy[1]), Integer.parseInt(xy[2])};
	}

	protected String getCurveIdByIndex(int ci, int iX, int jY)
	{
		return ci + " " + iX + " " + jY;
	}

	public Iterator<IBaseGisObject> getAllObjects()
	{
		try
		{
			List<IBaseGisObject> rl = new LinkedList<IBaseGisObject>();
			Pair<BindStruct, Integer> pr = getCurrentStruct();

			int iXEnd = (int) Math.floor(pr.first.totalsize.x / pr.first.picsize.x);
			int iYEnd = (int) Math.floor(pr.first.totalsize.y / pr.first.picsize.y);


			for (int iX = 0; iX < iXEnd; iX++)
				for (int jY = 0; jY < iYEnd; jY++)
					rl.add(getGisObject(new int[]{pr.second, iX, jY}));
			return rl.iterator();
		}
		catch (Exception e)
		{
			throw new UnsupportedOperationException(e);
		}
	}

	public Iterator<String> getCurvesIds()
	{
		try
		{
			List<String> rl = new LinkedList<String>();
			Pair<BindStruct, Integer> bs = getCurrentStruct();

			for (int jY = 0; jY < bs.first.flnames.length; jY++)
				for (int iX = 0; iX < bs.first.flnames[jY].length; iX++)
					rl.add(getCurveIdByIndex(bs.second, iX, jY));
			return rl.iterator();
		}
		catch (Exception e)
		{
			throw new UnsupportedOperationException(e);
		}
	}

	public Pair<BindStruct, Integer> getCurrentStruct() throws Exception
	{
		MPoint unixOnPixel = converter.getAsScaledConverterCtrl().getUnitsOnPixel();
		double curentscale = Math.max(1.0 / unixOnPixel.x, 1.0 / unixOnPixel.y);
		for (int i = 0; i < bstr_list.size(); i++)
		{
			BindStruct bindStruct = bstr_list.get(i);
			//Начинаем с нижней границы, по достижении верхней границы сразу переключаемся на сл. растр
			if (bindStruct.scaleRange[0] <= curentscale && (curentscale < bindStruct.scaleRange[1] || bindStruct.scaleRange[1] < 0))
				return new Pair<BindStruct, Integer>(bindStruct, i);
		}
		throw new Exception("Can't find appropriate scale range for raster");
	}

	public IBaseStorage getStorageByCurveId(String curveId) throws Exception
	{
		try
		{
			if (getBaseGisByCurveId(curveId)!=null)
				return this;
		}
		catch (NumberFormatException e)
		{//
		}
		return null;
	}

	public String getNodeId()
	{
		return nodeId;
	}

	public INodeStorage getParentStorage()
	{
		return parent;
	}

	public void setParentStorage(INodeStorage parent)
	{
		this.parent = parent;
	}

	public Collection<INodeStorage> getChildStorages()
	{
		throw new UnsupportedOperationException();
	}

	public IAttrs getDefAttrs()
	{
		return new DefaultAttrsImpl();
	}

	public void setDefAttrs(IAttrs defAttrs)
	{
		throw new UnsupportedOperationException();
	}

	public void setNameConverter(INameConverter nmconverter)
	{
	}

	public void setObjAttrsFactory(AObjAttrsFactory attrsfactory)
	{
		throw new UnsupportedOperationException();
	}

	public AObjAttrsFactory getObjAttrsFactory()
	{
		throw new UnsupportedOperationException();
	}

	public void rebindByObjAttrsFactory(AObjAttrsFactory attrsfactory) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	public void setStoragesfactory(IFactory<INodeStorage> storagesfactory)
	{
		throw new UnsupportedOperationException();
	}

	public void setViewProgress(IViewProgress viewProgress)
	{
	}

	public String getObjName()
	{
		return nodeId;
	}


    protected BindStruct currentBindStruct = null;

	public Object[] init(Object... objs) throws Exception
	{
		bstr_list.clear();

		for (Object obj : objs)
		{
			IParam attr=(IParam)obj;
			if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
				this.nodeId = (String) attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
				this.desc=(IXMLObjectDesc)attr.getValue();
			else if (attr.getName().equalsIgnoreCase(RASTER_TAG))
			{
				if (currentBindStruct != null)
					bstr_list.add(currentBindStruct);
				currentBindStruct = createBindStruct();
			} else if (attr.getName().equalsIgnoreCase(PATHDESC_TAG))
				currentBindStruct.pathdesc = ((String) attr.getValue());
			else if (attr.getName().equalsIgnoreCase(SCALE_TAG))
			{
				String scaleString = ((String) attr.getValue());
				String[] splited = scaleString.split(" ");
				try
				{
					currentBindStruct.scaleRange[0] = Double.parseDouble(splited[0]);
					currentBindStruct.scaleRange[1] = Double.parseDouble(splited[1]);
				}
				catch (NumberFormatException e)
				{
					throw new Exception("Error parsing of scale", e);
				}
			} else if (attr.getName().equalsIgnoreCase(PR_0_PAR))
				setPt(currentBindStruct, attr, 0);
			else if (attr.getName().equalsIgnoreCase(PR_1_PAR))
				setPt(currentBindStruct, attr, 1);
			else if (attr.getName().equalsIgnoreCase("converter"))
				converter = ((IRProjectConverter) attr.getValue());
			else
				init(obj);
		}
		bstr_list.add(currentBindStruct);
        currentBindStruct=null;
		for (BindStruct bindStruct : bstr_list)
			bindStruct.loadDesc();
		return null;
	}

    protected BindStruct createBindStruct() {
        return new BindStruct();
    }


    public Object init(Object obj) throws Exception
	{
		return null;
	}

	public IObjectDesc getObjectDescriptor()
	{
		return desc;
	}

	private void setPt(BindStruct bstr, IParam attr, int ipnt)
			throws Exception
	{
		String pointsBind = ((String) attr.getValue());
		String[] splited = pointsBind.split(" ");
		try
		{
			bstr.lpnt[ipnt] = new MPoint(Double.parseDouble(splited[0]), Double.parseDouble(splited[1]));
			bstr.rpnt[ipnt] = new MPoint(Double.parseDouble(splited[2]), Double.parseDouble(splited[3]));
		}
		catch (NumberFormatException e)
		{
			throw new Exception("Error prasing of pr" + ipnt, e);
		}
	}


	public void saveToStream(DataOutputStream dos) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	public void loadFromStream(DataInputStream dis) throws Exception
	{
//		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
//		Reader rd = new InputStreamReader(dis, "WINDOWS-1251");
//		parser.parse(new InputSource(rd), new CurrentHandler(parser.getXMLReader()));
	}


}
