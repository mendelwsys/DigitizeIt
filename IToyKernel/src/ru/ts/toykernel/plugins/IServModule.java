package ru.ts.toykernel.plugins;

/**
 * Серверный модуль
 */
public interface IServModule extends IModule
{
	/**
	 * @return команды которые обрабатывает модуль формат CMD1#CMD2#CMD3
	 */
	String getCommands();


}
