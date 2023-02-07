package ru.ts.toykernel.geom.def;

import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.def.DefaultAttrsImpl;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.utils.data.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

/**
 * Растровый объект
 */
public class RasterObject  implements IBaseGisObject
{
    public static final String RFNAME = "RFNAME";
    protected String rstrname;

    protected IBaseStorage storage;

    public IBaseStorage getStorage()
    {
        return storage;
    }


    public void setStorage(IBaseStorage storage)
    {
        this.storage = storage;
    }

    public Pair<BufferedImage, String> getRawRaster() throws Exception
	{
		Pair<BufferedImage, String> rv=new Pair<BufferedImage, String>(null,url2name.second);
		File input = new File(url2name.first);
		if (url2name.first!=null && input.exists())
			rv.first= ImageIO.read(input);
		return rv;
	}


    public Pair<InputStream,String> getStreamRawRaster() throws Exception
	{
		Pair<InputStream, String> rv=new Pair<InputStream, String>(null,url2name.second);
		File input = new File(url2name.first);
		if (url2name.first!=null && input.exists())
			rv.first= new FileInputStream(input);
		return rv;
	}


	protected Pair<String,String> url2name;
	protected MPoint projP0;
	protected MPoint projP1;
	//Передается квадрат растра в координатах проекта
	public RasterObject(MPoint projP0,MPoint projP1,Pair<String,String> url2name,String curveId,IBaseStorage storage)
	{
		this.rstrname = curveId;
		this.url2name=url2name;
		this.projP0=projP0;
		this.projP1=projP1;
        this.storage = storage;
    }
	public IAttrs getObjAttrs()
	{
        DefaultAttrsImpl defaultAttrs = new DefaultAttrsImpl();
        defaultAttrs.put(RFNAME,new DefAttrImpl(RFNAME,url2name.first));
        return defaultAttrs;
	}

	public MRect getMBB(MRect boundrect)
	{
		MRect brect = new MRect(new MPoint(Math.min(projP0.x, projP1.x), Math.min(projP0.y, projP1.y)), new MPoint(Math.max(projP0.x, projP1.x), Math.max(projP0.y, projP1.y)));
		if (boundrect!=null)
			return new MRect(new MPoint(Math.min(brect.p1.x, boundrect.p1.x), Math.min(brect.p1.y, boundrect.p1.y)), new MPoint(Math.max(brect.p4.x, boundrect.p4.x), Math.max(brect.p4.y, boundrect.p4.y)));
		else
			return brect;
	}

	public String getGeotype()
	{
		return KernelConst.RASTR;
	}

	public double[][][] getRawGeometry()
	{
		throw new UnsupportedOperationException("Can't get raw geometry for Raster Object, use getMBB function");
	}

	public String getCurveId()
	{
		return rstrname;
	}

	public int getDimensions()
	{
		return 2;
	}

	public void setInstance(IBaseGisObject _curve)
	{
		if (_curve instanceof RasterObject)
		{
			RasterObject curve=(RasterObject)_curve;
			projP0 = new MPoint(curve.projP0);
			projP1 = new MPoint(curve.projP1);
		}
		else
		 throw new UnsupportedOperationException("Can't set instance for object of type:"+ _curve.getClass().getCanonicalName());
	}
}
