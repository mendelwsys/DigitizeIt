package ru.ts.toykernel.proj;

import ru.ts.utils.data.Pair;
import ru.ts.factory.IInitAble;

import java.util.List;

/**
 * Конфигурационный провайдер
 * Предназначен для получения конфигурации серверного приложения
 */
public interface IConfigProvider extends IInitAble
{
	/**
	 * отдать список проектов размещенных на сервере
	 * @return - список серверных проектов
	 * @throws Exception -
	 */
	List<String> getProjects() throws Exception;	
	/**
	 * @param projname - имя проекта
	 * @return list of applications descriptor
	 * @throws Exception -
	 */
	List<String> getApplications(String projname) throws Exception;


	/**
	 * @param projname -имя проекта
	 * @param appname - имя приложения
	 * @return <флаг- цикл присутствует, XML описание переданного дескриптора, что бы его можно было собрать на клиенте>
	 * @throws Exception -
	 */
	public Pair<Boolean, String> getDescriptorByAppInfo(String projname, String appname) throws Exception;

	/**
	 * Открыть сессию по информации о приложении
	 *
	 * @param projname - имя проекта
	 * @param appname  - имя приложения
	 * @return - идентификатор сессии
	 * @throws Exception -
	 */
	String openSession(String projname, String appname) throws Exception;
}
