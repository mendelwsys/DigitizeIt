package ru.ts.toykernel.storages;

import java.util.Set;

/**
 *
 * Изменяемое хранилище новая версия
 * 
 */
public interface IEditableStorage extends IEditableStorageOper
{


	/**
 	* Дописать новые объекты в хранилище
 	* @throws Exception -
 	*/
	void commit() throws Exception;

	/**
	 * Откатить изменения в хранилище
	 * @throws Exception -
	 */
	void rollback() throws Exception;
	/**
 	* Удалить из файла пространство которые занимали удаленные объекты
 	* @throws Exception -
 	*/
	void rearange() throws Exception;

	void truncate()  throws Exception;

	/**
	 * @return true - Есть ли не сохраненные объекты
	 * @throws Exception -
	 */
	boolean notcommited() throws Exception;

	/**
	 * get set of not commited objkects
	 * @return set of not commited objects
	 */
	Set<String> getNotCommited();

	int getSizeNotCommited();
}
