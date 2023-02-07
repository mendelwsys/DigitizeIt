package ru.ts.toykernel.storages;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.attrs.IAttrs;

import java.util.Iterator;

/**
 * Базовый интерфейс для хранилища, доступ осуществляется в терминах gis объектов (IBaseGisObject)
 * или их идентификаторов
 * Base interface for Storages
 */
public interface IBaseStorage
{
	/**
	 * @return - кол-во объектов в хранилище, может не поддерживаться хранилищем 
	 */
	int getObjectsCount();

	/**
	 * @return Вернуть временную метку последнего изменения в хранилище
	 */
	 long getLastModified();
	/**
	 * get storage with objects that meet filter
	 * @param filter - base filter
	 * @return - filtered storage
	 * @throws Exception - Storage error while getting base filter
	 */
	IBaseStorage filter(IBaseFilter filter)  throws Exception;

	/**
	 * get gis object by id of object
	 * @param curvId - id of object
	 * @return gis object
	 * @throws Exception - Storage error while getting gis object
	 */
	IBaseGisObject getBaseGisByCurveId(String curvId) throws Exception;

	/**
	 * get list inerator of filtered objects
	 * @param filter - filterObjs of objects
	 * @return - get list inerator of objects
	 * @throws Exception -
	 */
	Iterator<IBaseGisObject> filterObjs(IBaseFilter filter) throws Exception;

	/**
	 * @return  all object in storage
	 * @throws Exception -
	 */
	Iterator<IBaseGisObject> getAllObjects() throws Exception;

	/**
	 * @return  all object Ids in storage
	 * @throws Exception -
	 */
	Iterator<String> getCurvesIds() throws Exception;

	/**
	 * return object attribute by curevID
	 * @param curveid -
	 * @return return object attribute by curevID
	 * @throws Exception -
	 */
	IAttrs getObjAttrs(String curveid)  throws Exception;
}
