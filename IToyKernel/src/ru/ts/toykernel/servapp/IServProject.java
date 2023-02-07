package ru.ts.toykernel.servapp;

import ru.ts.factory.IInitAble;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.utils.data.Pair;
import ru.ts.toykernel.raster.providers.IRPRovider;
import ru.ts.toykernel.converters.providers.IConvProvider;
import ru.ts.toykernel.converters.IScaleDescBean;
import ru.ts.toykernel.pcntxt.IMetaInfoBean;
import ru.ts.toykernel.plugins.IModule;

import java.util.Map;
import java.util.List;
import java.io.OutputStream;

/**
 * Интерфейс серверного проекта
 */
public interface IServProject extends IInitAble
{

	IMetaInfoBean getMetainfo();

	/**
	 *
	 * @return - набор модулей которые принадлежат прилажению
	 * @throws Exception - ошибка
	 */
	public Map<String, List<IModule>> getPlugIns() throws Exception;


	/**
	 * Отдать первый плагин переданного типа
	 * @param cl - тип плагин которого возвращается
	 * @return - плагин переданного типа
	 * @throws Exception -
	 */
	IModule getFirstPlugInByClass(Class cl) throws Exception;

	/**
	 * Отдать плагины переданного типа
	 * @param cl - тип плагин которого возвращается
	 * @return - плагины переданного типа
	 * @throws Exception -
	 */
	List<IModule> getPlugInsByClass(Class cl) throws Exception;
}
