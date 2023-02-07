package ru.ts.toykernel.plugins;

import java.util.Map;
/**
 * Базовый интерфейс тонкого клиента
 */
public interface IBaseThinModule  extends IServModule
{
	/**
	 * Добавить листенер модуля
	 * @param modelistener - листенер
	 * @throws Exception -
	 */
	void addModuleListener(Listeners.IModuleListener modelistener) throws Exception;

	/**
	 * Возвратить код инициализации модуля
	 * в тонком клиенте.
	 * @return - JS код модуля
	 * @throws Exception -
	 * @param params - параметры используемые для генерации кода инициализации
	 */
	String getJSInitCode(Map<String, String> params) throws Exception;

}
