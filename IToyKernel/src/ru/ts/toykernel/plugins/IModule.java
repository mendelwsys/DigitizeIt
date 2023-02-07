package ru.ts.toykernel.plugins;

import ru.ts.toykernel.consts.INameConverter;
import ru.ts.factory.IFactory;
import ru.ts.factory.IInitAble;

/**
 * Module interface
 */
public interface IModule extends IInitAble
{


	/**
	 * @return module name
	 */
	String getModuleName();

	/**
	 * execute module operation
	 * @param cmd - command for execute
	 * @return - module answer
	 * @throws Exception - execute error
	 */
	IAnswerBean execute(ICommandBean cmd) throws Exception;

	/**
	 * Register name converter
	 * @param nameConverter - name converter
	 * @param factory - factory of names converters
	 * @throws Exception - error of rigistration
	 */
	void registerNameConverter(INameConverter nameConverter, IFactory<INameConverter> factory) throws Exception;

	/**
	 * Выгрузить модуль
	 */
	void unload();
}
