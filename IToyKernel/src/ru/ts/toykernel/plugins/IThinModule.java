package ru.ts.toykernel.plugins;

import java.util.Map;
import java.io.InputStream;
import java.io.IOException;

/**
 * Модуль инициализации для тонкого клиента
 * TODO Здесь нужно не только писать какие команды,
 * TODO но и инициализировать меню, устанваливать мышиные слушатели
 * и т.д. 
 */
public interface IThinModule extends IBaseThinModule
{

//Контсанты всех модулей для доступа к общим элементам
	final String MENU2MENUSTRUCT="menu2menusstruct";
	final String MENUSTRUCT="menustruct";

	final String MMAP="mmap"; //имя карты
	final String MSLIDER="mslider";//имя слайдера
	final String REQMANAGER="rmanager";//имя менеджера
	final String PLUGDIR="plugdir";//относительный путь к директории

	/**
	 * Получить ссылку на JS код модуля (Код вставляется в секцию <script type="text/javascript" src='clickmod.js'></script>)
 	 * @param session_id - идентификатор сессии модуля
	 * @return - JS код модуля который встраивается в заголовок секции
	 * @throws Exception -
	 */
	String getJSSrcModuleRef(String session_id) throws Exception;

	/**
	 * Отдать сам JS код модуля (исходник модуля на JS)
	 * @return - JS код модуля
	 * @throws Exception -
	 */
	String getJSSrc() throws Exception;


	/**
	 * Установить отображение имен в коде в имена html элементов
	 * @param mapnames2JSelemnts - устанаваливаемое отображение
	 */
	void initJSParams(Map<String, String> mapnames2JSelemnts);

	/**
	 * Отдать сериализованый ресурс по имени
	 * @param resname - имя ресурса
	 * @param typename - тип ресурса
	 * @return входной поток ресурса
	 */
	InputStream getResourceStreamByParemters(String resname, String typename);

}
