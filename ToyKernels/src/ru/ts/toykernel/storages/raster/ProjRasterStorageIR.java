package ru.ts.toykernel.storages.raster;

import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.filters.IMBBFilter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.geom.def.RasterObject;
import ru.ts.toykernel.geom.def.RRasterObject;
import ru.ts.toykernel.raster.providers.IRPRovider;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
 * Хранилище в терминах интерфейса IRPRovider для этого
 * хранилища проектные координаты  соответсвуют конечному растру (которые получаются после
 * серверного преобразования растра). Т.е. когда растр уже повернут и растянут/сжат на сервер,
 * получется растровое поле, по которому перемещается вьюпорт
 * ru.ts.toykernel.storages.raster.ProjRasterStorageIR
 */
public class ProjRasterStorageIR extends RasterStorageIR
{
	public ProjRasterStorageIR(String nodeId,IRPRovider provider) throws Exception
	{
		super(nodeId, provider);
	}

	public ProjRasterStorageIR()
	{

	}

	public Iterator<IBaseGisObject> filterObjs(IBaseFilter filter) throws Exception
	{
		if (filter instanceof IMBBFilter)
		{
			//Это координатный прмоугольник в координатах проекта
			//В данном случае координаты проекта это координаты растра
			MRect rrect = ((IMBBFilter) filter).getRect();
			List<IBaseGisObject> rl = new LinkedList<IBaseGisObject>();
			RasterObject robj = getGisObject(
					new int[]
			{
					(int)Math.round(rrect.p1.x),(int)Math.round(rrect.p1.y),
			        (int)Math.round(rrect.getWidth()),(int)Math.round(rrect.getHeight())
			});
			rl.add(robj);
			return rl.iterator();
		}
		throw new UnsupportedOperationException();
	}

	protected RasterObject getGisObject(int[] indexobj)
			throws Exception
	{
		return new RRasterObject(new MPoint(indexobj[0], indexobj[1]),
				new MPoint(indexobj[0]+indexobj[2], indexobj[1]+indexobj[3]), provider,indexobj
				, getCurveIdByIndex(indexobj),this);
	}


	public Iterator<IBaseGisObject> getAllObjects()  throws Exception
	{
		return new LinkedList<IBaseGisObject>().iterator();
	}

	public Iterator<String> getCurvesIds() throws Exception
	{
		return new LinkedList<String>().iterator();
	}

}