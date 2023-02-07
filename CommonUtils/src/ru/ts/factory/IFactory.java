package ru.ts.factory;

/**
 * Общий интерфейс фабрики, фабрики могут быть каскадированы,
 * это означает, что если текущая фабрика не знает как инстанцировать переданный тип объекта
 * она вызвает следующею фабрику в цепочке и т.д.
 * Common Factory template
 */
public interface IFactory<T>
{
	/**
	 * cascading filter factory
	 * @param factory - external filte factory
	 */
	void addFactory(IFactory<T> factory);

	/**
	 * get object by type name
	 * @param typeObj - type name of object
	 * @return - generated object
	 * @throws Exception -
	 */
	T createByTypeName(String typeObj) throws Exception;
}
