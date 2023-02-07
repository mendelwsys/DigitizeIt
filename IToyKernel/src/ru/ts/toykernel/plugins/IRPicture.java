package ru.ts.toykernel.plugins;

import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.utils.data.Pair;
import ru.ts.toykernel.raster.providers.IRPRovider;
import ru.ts.toykernel.converters.providers.IConvProvider;
import ru.ts.toykernel.converters.IScaleDescBean;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Интерфейс удаленной растровой картинки
 * Будь-то карта или график
 */
public interface IRPicture
{
	MPoint getCurrentPoint();

	void getRasterParamters(String provname,double[] dXdY, double[] szXszY, int[] nXnY) throws Exception;

	Pair<Boolean, String> getImmage(int index, int[] imgiXiY, OutputStream os, Map<String,String[]> args) throws Exception;

	Pair<Boolean, String> getImmage(String provname, int[] imgiXiY, OutputStream os, Map<String, String[]> args) throws Exception;

	Integer getIndexProviderByName(String provname)
			throws Exception;

	List<IRPRovider> getImageProviders() throws Exception;

	IRPRovider getImageProvider(int index) throws Exception;

	IConvProvider getConverterProvider()  throws Exception;

	IScaleDescBean getScaleGradBean() throws Exception;

	void reloadMap();

	void updateMap();

	int addImageProvider(IRPRovider provider) throws Exception;

	void removeImageProvider(int index) throws Exception;

	/**
	 * Обновить картинку по имени провайдера
	 * @param provname - имя провайдреа растров
	 * @throws Exception -
	 */
	void updateByProviderName(String provname) throws Exception;

	/**
	 * Обновить картинку по имени провайдера
	 * @param ixProvider - имя провайдреа растров
	 * @throws Exception - если ixProvider is null or out of range of providers index
	 */
	void updateByIxProvider(Integer ixProvider) throws Exception;
}