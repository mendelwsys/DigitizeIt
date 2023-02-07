package ru.ts.toykernel.storages.providers;

import ru.ts.toykernel.storages.raster.BindStruct;
import ru.ts.toykernel.storages.raster.RasterStorage;
import ru.ts.utils.data.Pair;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.toykernel.raster.providers.IRPRovider;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.factory.IParam;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.util.Map;


/**
 * Провайдер растров выполненный на основе хранилища
 * Серверная часть, в терминах IRPRovider осуществляется доступ к растрам через HTTP адаптер
 * ru.ts.toykernel.storages.providers.RasterServerProvider
 */
public class RasterServerProvider extends BaseInitAble
		implements IRPRovider
{
	private RasterStorage rasterstor;
	protected String urlBase;//Базовый каталог для файлов
	
	public RasterServerProvider()
	{//Конструктор для инициализации через загрузчик xml
	}

	public RasterServerProvider(RasterStorage  rasterstor,String urlBase)
	{
		this.rasterstor = rasterstor;
		this.urlBase = urlBase;
	}

	public void getRasterParameters(double[] dXdY, double[] szXszY, int[] nXnY) throws Exception
	{
		Pair<BindStruct, Integer> pr = rasterstor.getCurrentStruct();
		BindStruct bstr = pr.first;
		ILinearConverter r2draw = rasterstor.getRaster2DstConverter(bstr);

		nXnY[0]=-1;
		nXnY[1]= bstr.flnames.length;
		if (nXnY[1]>0)
			nXnY[0]= bstr.flnames[0].length;

		MRect drwrect = r2draw.getDstRectByRect(new MRect(new MPoint(), bstr.picsize));
		dXdY[0]=drwrect.getWidth();
		dXdY[1]=drwrect.getHeight();

 		drwrect = r2draw.getDstRectByRect(new MRect(new MPoint(), bstr.totalsize));
		szXszY[0]=drwrect.p1.x;
		szXszY[1]=drwrect.p1.y;
		szXszY[2]=drwrect.getWidth();
		szXszY[3]=drwrect.getHeight();
	}

	public Pair<BufferedImage, String> getRawRasterByImgIndex(int[] imgindex, Map<String, String[]> args) throws Exception
	{
		return getRawRaster(getObjIndexByIndexImage(imgindex), args);
	}

	public Pair<BufferedImage, String> getRawRaster(int[] iXiY, Map<String, String[]> args) throws Exception
	{
		return rasterstor.getGisObject(iXiY).getRawRaster(); //Растр надо растягивать или сжимать на клиенте
		// (для этого надо знать размер целевого прямоугольнику (на сколько его собственно растягивать),
		// она передается во время getRasterParameters)
	}

	public Pair<InputStream,String> getStreamRawRaster(int[] iXiY, Map<String, String[]> args) throws Exception
	{
		return rasterstor.getGisObject(iXiY).getStreamRawRaster(); //Растр надо растягивать или сжимать на клиенте
	}

	public Pair<Boolean, String> getStreamRawRaster(int[] iXiY, OutputStream os, Map<String, String[]> args) throws Exception
	{
		Pair<InputStream,String> is2str=rasterstor.getGisObject(iXiY).getStreamRawRaster();
		try
		{
			Pair<Boolean, String> rv=new Pair<Boolean, String>(false,is2str.second);
			if (is2str.first!=null)
			{
				BufferedInputStream buffstream = new BufferedInputStream(is2str.first);
				byte[] byteArray=new byte[10*1024];
				int ln=0;
				while ((ln=buffstream.read(byteArray))>0)
					os.write(byteArray,0,ln);
				os.flush();
				rv.first=true;
			}
			return rv;
		}
		finally
		{
			if (is2str!=null && is2str.first!=null)
				is2str.first.close();
		}
	}

	public int[] getObjIndexByIndexImage(int[] iXiY) throws Exception
	{
		Pair<BindStruct, Integer> pr = rasterstor.getCurrentStruct();
		int[] rv = new int[iXiY.length+1];
		System.arraycopy(iXiY, 0, rv, 1, iXiY.length);
		rv[0]=pr.second;
		return rv;
	}

	public String getInitScript(double[] dXdY, double[] szXszY, int[] nXnY, int lrindex, Point rCenter, Point rSize) throws Exception
	{
		String script = "\n";
		script += "mapstruct.layerarr["+lrindex+"]=new rlayer(mapdiv,"+lrindex+");\n ";
		script += "mapstruct.layerarr["+lrindex+"].nXY.setXY(" + (nXnY[0]) + "," + (nXnY[1]) + ");\n";

		script += "mapstruct.layerarr["+lrindex+"].p_wh.setXY(" +dXdY[0] + "," + dXdY[1] + ");\n";
		script += "mapstruct.layerarr["+lrindex+"].m_p1.setXY(" +szXszY[0]+ "," + szXszY[1] + ");\n";
		script += "mapstruct.layerarr["+lrindex+"].m_wh.setXY(" +szXszY[2] + "," + szXszY[3] + ");\n";

//		script += "mapstruct.layerarr["+lrindex+"].p_wh.setXY(" +((int)Math.round(dXdY[0])) + "," + ((int)Math.round(dXdY[1])) + ");\n";
//		script += "mapstruct.layerarr["+lrindex+"].m_p1.setXY(" +((int)Math.round(szXszY[0])) + "," + ((int)Math.round(szXszY[1])) + ");\n";
//		script += "mapstruct.layerarr["+lrindex+"].m_wh.setXY(" +((int)Math.round(szXszY[2])) + "," + ((int)Math.round(szXszY[3])) + ");\n";


		return script;
	}

	public double[] getScaleRange()
	{
		return rasterstor.getScaleRange();
	}

//	protected RasterObject getRasterObject(int[] iXiY)
//			throws Exception
//	{
//		Pair<RasterStorage.BindStruct, Integer> pr = rasterstor.getCurrentStruct();
//		return rasterstor.getGisObject(new int[]{pr.second, iXiY[1], iXiY[2]});
//	}

//	public String getUrlImageRequest(int[] iXiY) throws Exception
//	{
//		Pair<RasterStorage.BindStruct, Integer> pr = rasterstor.getCurrentStruct();
//		return urlBase+"?ii="+pr.second+"&ix="+ iXiY[1]+"&iy="+ iXiY[2];
//	}

	public Object init(Object obj) throws Exception
	{
		IParam attr=(IParam)obj;
		if (attr.getName().equalsIgnoreCase(URL_BASE))
			urlBase=(String)attr.getValue();
		else
		if (attr.getName().equalsIgnoreCase(KernelConst.STORAGE_TAGNAME))
			rasterstor=(RasterStorage)attr.getValue();
		return null;
	}
}
