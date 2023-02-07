package ru.ts.toykernel.storages.mem;

import ru.ts.toykernel.attrs.AObjAttrsFactory;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.shared.PathObject;
import ru.ts.xml.IXMLObjectDesc;
import ru.ts.utils.data.Pair;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.factory.IObjectDesc;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * ru.ts.toykernel.storages.mem.MemStorageLr3
 */
public class MemStorageLr3  extends MemStorageLr2
{
	public static final String TYPENAME ="MEM_STORG3";//name of the object
	protected IXMLObjectDesc desc;

	protected MemStorageLr3(AObjAttrsFactory attrsfactory, IAttrs defAttrs, INameConverter storNm2CodeNm,String folderlayers,String nodeId)
	{
		super(attrsfactory, defAttrs, storNm2CodeNm);
		this.folderlayers=folderlayers;
		this.nodeId=nodeId;
	}



	public MemStorageLr3()
	{
		super(null, null, null);
	}


	/**
	 * @param dis - входной поток содержащий слой
	 * @throws Exception - исключения
	 */
	public void loadFromStream(DataInputStream dis)
			throws Exception
	{
		throw new UnsupportedOperationException();
	}

	public void saveToStream(DataOutputStream dos) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	public long getLastModified()
	{
		long ixlm=new File(getIXFileName()).lastModified();
		long dtlm=new File(getDatFileName()).lastModified();
		long gmlm=new File(getGeomFileName()).lastModified();
		return Math.max(Math.max(ixlm,dtlm),gmlm);
	}

	public Object[] init(Object ...objs)
			throws Exception
	{
		long tm= System.currentTimeMillis();
		DataInputStream dis_dat=null;
		DataInputStream dis_ix=null;

		try
		{
			for (Object obj : objs)
			{
				IDefAttr attr=(IDefAttr)obj;
				if (attr.getName().equalsIgnoreCase(KernelConst.OBJNAME))
					this.nodeId= (String) attr.getValue();
				else if (attr.getName().equalsIgnoreCase(KernelConst.DESCRIPTOR))
					this.desc=(IXMLObjectDesc)attr.getValue();
				else if (attr.getName().equalsIgnoreCase(KernelConst.DIR_TAGNAME))
					this.folderlayers=((PathObject)attr.getValue()).getFolderlayers();
				else
					this.folderlayers=((String)attr.getValue());
			}

			releaseStorage();
			fis=null;
			lastindexacc=0;
			flnm = getGeomFileName();//Сохранить на файловую систему
//Читаем индекс
			dis_ix = new DataInputStream(new BufferedInputStream(new FileInputStream(getIXFileName())));
			int size = dis_ix.readInt();
			curveorder = new ArrayList<String>(size);
			Map<Integer,String> index2curveId=new HashMap<Integer,String>();
			while (size > 0)
			{
				String curveid = dis_ix.readUTF();
				int index = dis_ix.readInt();

				double minX = dis_ix.readDouble();
				double minY = dis_ix.readDouble();
				double maxX = dis_ix.readDouble();
				double maxY = dis_ix.readDouble();

				MRect rect = new MRect(new MPoint(minX, minY), new MPoint(maxX, maxY));
				graphobkects.put(nodeId + GROUP_SEPARATOR + curveid, new Pair<Integer, MRect>(index, rect));
				curveorder.add(nodeId + GROUP_SEPARATOR + curveid);

				index2curveId.put(index,nodeId + GROUP_SEPARATOR + curveid);

				size--;
			}
			dis_ix.close();
//Читаем аттрибуты слоев
			dis_dat = new DataInputStream(new BufferedInputStream(new FileInputStream(getDatFileName())));

			int cntattrs = dis_dat.readInt();
			defAttrs = new DefaultAttrsImpl();
			while (cntattrs > 0)
			{
				ObjectInputStream obis = new ObjectInputStream(dis_dat);
				String attrname = obis.readUTF();
				Object val = obis.readObject();
				defAttrs.put(attrname, new DefAttrImpl(attrname, val));
				cntattrs--;
			}

			size = curveorder.size();
			while (size > 0)
			{
				int attrsz = dis_dat.readInt();
				if (attrsz > 0)
				{
	//				StreamEditableCurve curve = incarnateByIndex(dis_dat.readInt()); //TODO Все таки приходися инкарнировать объекты бля УБРАТЬ СРОЧНО!!!!
	//				String curveId=curve.getCurveId();
					String curveId=index2curveId.get(dis_dat.readInt());
					if (curveId==null)
					{
						throw new Exception("Error init of MemStorgeLr:"+this.getClass().getCanonicalName());
					}
					IAttrs attrs = attrsfactory.createByGisObjId(curveId,parentstor,defAttrs);
					while (attrsz > 0)
					{
						ObjectInputStream objis = new ObjectInputStream(dis_dat);
						String key = objis.readUTF();
						Object obj = objis.readObject();
						attrs.put(key, new DefAttrImpl(key, obj));
						attrsz--;
					}
					pool.put(curveId, attrs);
				}
				size--;
			}
		}
		finally
		{
			if (dis_ix!=null)
				dis_ix.close();
			if (dis_dat!=null)
				dis_dat.close();

			System.out.println(nodeId+" time init:"+(System.currentTimeMillis()-tm));
		}
		return null;
	}

	public IObjectDesc getObjectDescriptor()
	{
		return desc;
	}

	private String getDatFileName()
	{
		return folderlayers+ "/" + nodeId+".dat";
	}

	private String getGeomFileName()
	{
		return folderlayers + "/" + nodeId+".gm";
	}

	private String getIXFileName()
	{
		return folderlayers+ "/" + nodeId+".ix";
	}

}
