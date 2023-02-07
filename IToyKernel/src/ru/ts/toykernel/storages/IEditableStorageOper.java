package ru.ts.toykernel.storages;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.geom.IEditableGisObject;

/**
 * Интерфейс операций над хранилищем
 */
public interface IEditableStorageOper extends IBaseStorage
{
	void addObject(IBaseGisObject object) throws Exception;

	void clearAll() throws Exception;

	/**
 * Создать новый редактируемый объект, и вернуть на него ссылку
 * @return ссылка на объект
 * @throws Exception -
 * @param geotype - тип объекта
 */
	IEditableGisObject createObject(String geotype) throws Exception;

	/**
	 * Удалить объект по идентификатору
	 * @param curveId - идентфикатор объекта
	 * @return - удаленный объект
	 * @throws Exception -
	 */
	IBaseGisObject removeObject(String curveId) throws Exception;

	/**
	 * Взять объект на редактирование
 	* @param curveId - идентифкатор объекта на редактирование
 	* @return объект на редактироание (null если объект невозможно взять на редактирование)
 	* @throws Exception -
 */
	IEditableGisObject getEditableObject(String curveId) throws Exception;
}
