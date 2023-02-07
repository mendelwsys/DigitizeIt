package ru.ts.toykernel.gui;

import ru.ts.factory.IInitAble;
import ru.ts.toykernel.pcntxt.IProjContext;

/**
 * Created by IntelliJ IDEA.
 * User: VMendelevich
 * Date: 14.03.2012
 * Time: 15:18:31
 * Common interface for view project context 
 */
public interface IView extends IInitAble
{
	/**
	 * @return project context
	 * @throws Exception - error getting context
	 */
	IProjContext getProjContext() throws Exception;

	IViewPort getViewPort() throws Exception;
}
