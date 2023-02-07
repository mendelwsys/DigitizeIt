package ru.ts.toykernel.geom.def;

import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.utils.data.Pair;
import ru.ts.toykernel.raster.providers.IRPRovider;

import java.awt.image.BufferedImage;

/**
 * Тип для получения растров с сервера,
 * клиентская часть.
 *
 */
public class RRasterObject  extends RasterObject
{
	private IRPRovider provider;
	private int[] indexreq;

	public Pair<BufferedImage, String> getRawRaster() throws Exception
	{
		return provider.getRawRasterByImgIndex(indexreq, null);
	}

	public RRasterObject(MPoint projP0, MPoint projP1, IRPRovider provider,int[] indexreq, String curveId,IBaseStorage storage)
	{
		super(projP0, projP1, new Pair<String,String>(null,null), curveId,storage);
		this.provider=provider;
		this.indexreq = indexreq;
	}
}
