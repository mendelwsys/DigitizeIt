package ru.ts.toykernel.storages;

import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.AObjAttrsFactory;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.factory.IFactory;
import ru.ts.factory.IInitAble;
import ru.ts.stream.ISerializer;
import ru.ts.utils.gui.elems.IViewProgress;

import java.util.Collection;

/**
 * Interface forstorages which logicaly devided on layes
 * base for tree building ofstorages
 */
public interface INodeStorage extends IBaseStorage , ISerializer, IInitAble
{
	static final String GROUP_SEPARATOR = "#$"; //Separator splits groupId and curveId in identifier of curve in thesestorages

	/**
	 * Вернуть хранилище в котором содержится объект с данным идентификатором
	 * @param curveId - идентификатор по которому осуществляется поиск объекта
	 * @return хранилище
	 * @throws Exception -
	 */
	IBaseStorage getStorageByCurveId(String curveId) throws Exception;
	

	/**
	 * @return id of storgae node
	 */
	public String getNodeId();

	/**
	 * Get parent storage of
	 * @return parent storage
	 */
	public INodeStorage getParentStorage();

	/**
	 * set parent storage
	 * @param parent - parent storage
	 */
	public void setParentStorage(INodeStorage parent);

	/**
	 * get collection of childstorages
	 * @return colleaction of childstorages
	 */
	public Collection<INodeStorage> getChildStorages();

	/**
	 * @return storage default attributes
	 */
	IAttrs getDefAttrs();

	/**
	 * set storage default attribute
	 * @param defAttrs - default attribute
	 */
	void setDefAttrs(IAttrs defAttrs);

	/**
	 * set name converter storage
	 * @param nmconverter - name converter
	 */
	void setNameConverter(INameConverter nmconverter);

	/**
	 * set object attribute factory
	 * @param attrsfactory - attribute factory
	 */
	void setObjAttrsFactory(AObjAttrsFactory attrsfactory);
	/**
	 * get object attribute factory
	 * @return object attribute factory
	 */
	AObjAttrsFactory getObjAttrsFactory();

	/**
	 * rebind object attributes by object factory
	 * @param attrsfactory - attribute factory
	 * @throws Exception - exception while rebind object
	 */
	void rebindByObjAttrsFactory(AObjAttrsFactory attrsfactory) throws Exception;

	/**
	 * set factory of storage
	 * @param storagesfactory- storage factory
	 */
	void setStoragesfactory(IFactory<INodeStorage> storagesfactory);

	/**
	 * set view progress for long time operation
	 * @param viewProgress - view progress
	 */
	void setViewProgress(IViewProgress viewProgress);

}
