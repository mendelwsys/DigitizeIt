package ru.ts.toykernel.storages.mem.share;

import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.utils.data.Pair;
import ru.ts.toykernel.attrs.IAttrsPool;
import ru.ts.toykernel.attrs.IAttrsPoolImpl;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.AObjAttrsFactory;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.toykernel.storages.IBaseStorage;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 07.07.2011
 * Time: 18:27:50
 * To change this template use File | Settings | File Templates.
 */
public class AccessorContainer
{
	protected Map<String, Pair<Integer, MRect>> graphobkects = new HashMap<String, Pair<Integer, MRect>>();//Набор объектов входящих в индекс curveId-><curve_array_index,project_rect>
	protected List<String> curveorder = new ArrayList<String>();//Сохраняется на внешний носитель согласно порядку в этом списке
	protected IAttrsPool pool = new IAttrsPoolImpl();//Implementation of pool storage
	protected IAttrs defAttrs;//Default attributes for all objects of this storage

	//getIXFileName(),getDatFileName()

	public void initIt
		(
			String ixfnname,String datfname,String nodeId,
			AObjAttrsFactory attrsfactory,
	        IBaseStorage parentstor
		)
			throws Exception
	{

		//AccessorContainer container = new IAccessorImpl.AccessorContainer();


		curveorder.clear();
		graphobkects.clear();
		pool.clear();

		DataInputStream dis_dat = null;
		DataInputStream dis_ix = null;
		long tm = System.currentTimeMillis();
		try
		{
//Читаем индекс
			File fileix = new File(ixfnname);
			int size = 0;
			Map<Integer, String> index2curveId = null;
			if (fileix.exists())
			{
				dis_ix = new DataInputStream(new BufferedInputStream(new FileInputStream(fileix)));
				size = dis_ix.readInt();
				curveorder = new ArrayList<String>(size);
				index2curveId = new HashMap<Integer, String>();
				while (size > 0)
				{
					String curveid = dis_ix.readUTF();
					int index = dis_ix.readInt();

					double minX = dis_ix.readDouble();
					double minY = dis_ix.readDouble();
					double maxX = dis_ix.readDouble();
					double maxY = dis_ix.readDouble();

					MRect rect = new MRect(new MPoint(minX, minY), new MPoint(maxX, maxY));
					graphobkects.put(nodeId + INodeStorage.GROUP_SEPARATOR + curveid, new Pair<Integer, MRect>(index, rect));
					curveorder.add(nodeId + INodeStorage.GROUP_SEPARATOR + curveid);

					index2curveId.put(index, nodeId + INodeStorage.GROUP_SEPARATOR+ curveid);

					size--;
				}
				dis_ix.close();
			}
//Читаем аттрибуты слоев  (Даже если ix не существует тогда файл dat может быть для хранения аттрибутов по умолчанию)
			File filedat = new File(datfname);
			if (!filedat.exists())
				fileix.delete();
			else
			{
				dis_dat = new DataInputStream(new BufferedInputStream(new FileInputStream(filedat)));
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
						String curveId = index2curveId.get(dis_dat.readInt());
						if (curveId == null)
							throw new Exception("Error init of MemStorgeLr:" + this.getClass().getCanonicalName());
						IAttrs attrs = attrsfactory.createByGisObjId(curveId, parentstor, defAttrs);
						pool.put(curveId, attrs);

						while (attrsz > 0)
						{
							ObjectInputStream objis = new ObjectInputStream(dis_dat);
							String key = objis.readUTF();
							Object obj = objis.readObject();
							attrs.put(key, new DefAttrImpl(key, obj));
							attrsz--;
						}

					}
					size--;
				}
			}
		}
		finally
		{
			if (dis_ix != null)
				dis_ix.close();
			if (dis_dat != null)
				dis_dat.close();

			System.out.println(nodeId + " time init:" + (System.currentTimeMillis() - tm));
		}
	}

}
