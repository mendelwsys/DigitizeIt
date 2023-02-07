package ru.ts.toykernel.gui;

import ru.ts.toykernel.converters.IProjConverter;

import java.awt.*;

/**
 * View Port Interface
 */
public interface IViewPort
{
	/**
	 * @return coordinate converter
	 * @throws Exception  - error getting converter
	 */
//	IProjConverter getConverter() throws Exception;

	/**
	 * @return draw size
	 * @throws Exception  - error getting draw size
	 */
	Point getDrawSize() throws Exception;

	IProjConverter getCopyConverter() throws Exception;

	void setCopyConverter(IProjConverter converer) throws Exception;
}
