package ru.ts.toykernel.proj;


/**
 * Клиентский провайдер конфигурации
 */
public interface ICliConfigProvider extends IConfigProvider
{
	/**
	 * @return сессию через которую работать
	 */
	String getSession();
}
