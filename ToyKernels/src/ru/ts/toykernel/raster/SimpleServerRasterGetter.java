package ru.ts.toykernel.raster;

import ru.ts.utils.data.Pair;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.toykernel.converters.IProjConverter;

import java.awt.image.BufferedImage;
import java.awt.*;

/**
 * Серверная часть закрывающая провайдера растров
 */
public class SimpleServerRasterGetter implements IServerRasterGetter
{
	protected IRasterProvider provider;
	protected IProjConverter converter;


	protected void recalcConverter(BindStructure bindstruct) throws Exception
	{
		//Линейная область не может быть повернута она просто масштабируется, поэтому 0,0 d линейной области
		//отображается в 0,0 при масштабировании,
		//Вычислям старые координаты нулевой линейной точки относительно  текущего положения экрана
		Point bindp_old = null;//TODO converter.getDrawPointByLinearPoint(new MPoint());
		//Координаты левого верхнего угла вью порта относительно всего изображения определяются координтами рисования точки 0,0
		//взятой с обратным знаком
		bindp_old.x=-bindp_old.x;
		bindp_old.y=-bindp_old.y;
		converter.getAsShiftConverter().recalcBindPointByDrawDxDy(new double[]{bindp_old.x-bindstruct.bindpt.x,bindp_old.y-bindstruct.bindpt.y});
	}

	/**
	 * Предполагается что провайдер и конвертер проинициализирован
	 * @param provider - провайдер растров
	 * @param converter - конвертер
	 */
	public SimpleServerRasterGetter(IRasterProvider provider, IProjConverter converter)
	{
		this.provider = provider;
		this.converter = converter;
	}

	public int[] getImagesIndices(BindStructure bindstruct, double ddXddY[]) throws Exception
	{
		recalcConverter(bindstruct);
		return provider.getImagesIndices(converter,new int[]{bindstruct.szWindow.x,bindstruct.szWindow.y},ddXddY);
	}


	public Pair<BufferedImage, String> getImageRequest(int[] nXnY) throws Exception
	{
		return provider.getImageRequest(nXnY);
	}


	public void getRasterParamters(BindStructure bindstruct, double scalemult,double[] dXdY, double[] szXszY, int[] nXnY) throws Exception
	{
//Если переданная точка null, тогда производим инициализауцию исходя из точки
		MPoint[] mp12= new MPoint[2];
		MPoint[] pt12= new MPoint[2];
		converter.getAsScaledConverterCtrl().increaseMap(scalemult);
		if (bindstruct.bindpt==null)
		{
			Point bindp = null;//TODO converter.getDrawPointByLinearPoint(new MPoint());
			bindstruct.bindpt=new MPoint(-bindp.x,-bindp.y);
		}
		else
			recalcConverter(bindstruct);
		provider.getRasterParamters(converter.getAsScaledConverter(),mp12, pt12,dXdY,szXszY,nXnY);
	}

	public MPoint getDrawPointByRasterPoint(MPoint point, BindStructure bindstruct, double ddXddY[]) throws Exception
	{
		recalcConverter(bindstruct);
		return provider.getDrawPointByRasterPoint(point,converter);
	}

	public MPoint getRasterPointByDrawPoint(MPoint point, BindStructure bindstruct, double ddXddY[]) throws Exception
	{
		recalcConverter(bindstruct);
		return provider.getRasterPointByDrawPoint(point,converter);
	}

	public String getUrlImageRequest(int[] nXnY) throws Exception
	{
		return provider.getUrlImageRequest(nXnY);
	}

}
