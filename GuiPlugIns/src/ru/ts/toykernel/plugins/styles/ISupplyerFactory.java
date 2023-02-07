package ru.ts.toykernel.plugins.styles;

import ru.ts.utils.gui.tables.IHeaderSupplyer;

/**
 * header supplyer factory
 */
public interface ISupplyerFactory
{
	/**
	 * @return header suppler
	 * @throws Exception - error while getting supplyer
	 */
	IHeaderSupplyer getHeaderSupplyer() throws Exception;
}
