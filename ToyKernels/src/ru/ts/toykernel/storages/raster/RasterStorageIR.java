package ru.ts.toykernel.storages.raster;

import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.filters.IMBBFilter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.geom.def.RasterObject;
import ru.ts.toykernel.geom.def.RRasterObject;
import ru.ts.toykernel.raster.providers.IRPRovider;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.AObjAttrsFactory;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.factory.IFactory;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;
import ru.ts.utils.gui.elems.IViewProgress;
import ru.ts.xml.IXMLObjectDesc;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Хранилище в терминах интерфейса IRPRovider для этого
 * хранилища проектные координаты  соответсвуют конечному растру (которые получаются после
 * серверного преобразования растра). Т.е. когда растр уже повернут и растянут/сжат на сервер,
 * получется растровое поле, по которому перемещается вьюпорт
 * ru.ts.toykernel.storages.raster.RasterStorageIR 
 */
public class RasterStorageIR implements INodeStorage
{

	public IAttrs getObjAttrs(String curveid) throws Exception
	{
		return getBaseGisByCurveId(curveid).getObjAttrs();
	}
	
	protected IRPRovider provider;

	protected  double[] dXdY=new double[2]; //Размер сегмента растра
	protected  double[] szXszY=new double[4]; //Размеры всего растра (координаты начала растра x,y и координаты  )
	protected int[] nXnY=new int[2];//Кол-во элементов растра

	protected  String nodeId;
	protected  INodeStorage parent;
	protected IXMLObjectDesc desc;

	public RasterStorageIR()
	{

	}
	public RasterStorageIR(String nodeId,IRPRovider provider) throws Exception
	{
		this.nodeId = nodeId;
		this.provider = provider;
		getRasterParamters();
	}

	public int getObjectsCount()
	{
		return nXnY[0]*nXnY[1];
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

	protected int[] getIndexByCurveId(String curveId)
	{
		String[] xy = curveId.split(" ");
		int[] rv = new int[xy.length];
		for (int i = 0; i < rv.length; i++)
			rv[i]=Integer.parseInt(xy[i]);
		return rv;
	}

	public Iterator<IBaseGisObject> filterObjs(IBaseFilter filter) throws Exception
	{
		if (filter instanceof IMBBFilter)
		{

			getRasterParamters();

			//Это координатный прмоугольник в координатах проекта
			//В данном случае координаты проекта это координаты растра
			MRect rrect = ((IMBBFilter) filter).getRect();

			int iXstart = (int) Math.floor(Math.max(rrect.p1.x-szXszY[0], 0) / dXdY[0]);
			int iXEnd = (int) Math.ceil(Math.min(rrect.p4.x-szXszY[0], szXszY[2]) / dXdY[0]);

			int jYstart = (int) Math.floor(Math.max(rrect.p1.y-szXszY[1], 0) / dXdY[1]);
			int jYEnd = (int) Math.ceil(Math.min(rrect.p4.y-szXszY[1], szXszY[3]) /dXdY[1]);

			List<IBaseGisObject> rl = new LinkedList<IBaseGisObject>();

			for (int iX = iXstart; iX < iXEnd; iX++)
				for (int jY = jYstart; jY < jYEnd; jY++)
				{
					RasterObject robj = getGisObject(new int[]{iX, jY});
					rl.add(robj);
				}
			return rl.iterator();
		}
		throw new UnsupportedOperationException();
	}

	protected void getRasterParamters()
			throws Exception
	{
		provider.getRasterParameters(dXdY,szXszY,nXnY); //Точки растровых слоев
	}

	protected RasterObject getGisObject(int[] indexobj)
			throws Exception
	{

		int iCrdX = indexobj[0];
		int jCrdY = indexobj[1];

		MPoint pt1 = new MPoint(iCrdX * dXdY[0]+szXszY[0], jCrdY *dXdY[1]+szXszY[1]);
		MPoint pt2 = new MPoint((iCrdX + 1) * dXdY[0]+szXszY[0], (jCrdY + 1) * dXdY[1]+szXszY[1]);

		return new RRasterObject(new MPoint(Math.min(pt1.x, pt2.x), Math.min(pt1.y, pt2.y)),
				new MPoint(Math.max(pt1.x, pt2.x), Math.max(pt1.y, pt2.y)), provider,indexobj
				, getCurveIdByIndex(indexobj),this);
	}

	protected String getCurveIdByIndex(int[] index)
	{
		String rv="";
		for (int i = 0; i < index.length; i++)
		{
			if (i==0)
				rv+=index[i];
			else
				rv+=" "+index[i];

		}
		return rv;
	}

	public Iterator<IBaseGisObject> getAllObjects()  throws Exception
	{
		getRasterParamters();
		List<IBaseGisObject> rl = new LinkedList<IBaseGisObject>();
		for (int i = 0; i < nXnY[0]; i++)
			for (int j = 0; j < nXnY[1]; j++)
			{
				try
				{
					rl.add(getGisObject(new int[]{i,j}));

				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		return rl.iterator();
	}

	public Iterator<String> getCurvesIds() throws Exception
	{

		getRasterParamters();
		List<String> rl = new LinkedList<String>();
		for (int i = 0; i < nXnY[0]; i++)
			for (int j = 0; j < nXnY[1]; j++)
				rl.add(getCurveIdByIndex(new int[]{i,j}));
		return rl.iterator();
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

	public Object[] init(Object... objs) throws Exception
	{
		for (Object obj : objs)
		{
			IParam attr=(IParam)obj;
			if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
				this.nodeId = (String) attr.getValue();
			else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
				this.desc=(IXMLObjectDesc)attr.getValue();
			else
				init(obj);
		}
		return null;
	}


	public Object init(Object obj) throws Exception
	{
		IParam attr=(IParam)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.STORPROVIDER_TAGNAME))
			this.provider = (IRPRovider) attr.getValue();
		return null;
	}

	public IObjectDesc getObjectDescriptor()
	{
		return desc;
	}

	public void saveToStream(DataOutputStream dos) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	public void loadFromStream(DataInputStream dis) throws Exception
	{
		throw new UnsupportedOperationException();
	}

}
