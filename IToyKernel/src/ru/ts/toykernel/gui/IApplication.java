package ru.ts.toykernel.gui;

import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.factory.IInitAble;
import ru.ts.toykernel.converters.IProjConverter;
import ru.ts.utils.data.InParams;
import ru.ts.utils.gui.elems.IViewProgress;

import java.util.List;


/**
 * class for accessing application business attributes
 */
public interface IApplication extends IInitAble
{
	/**
	 * Set Project context in view control
	 * @param project - project context
	 * @param fconverter
	 * @throws Exception - error while setting context
	 */
	void addProjectContext(IProjContext project, IProjConverter fconverter)
			throws Exception;

	/**
	 * get ViewControl by project context
	 * @param project - project context
	 * @return view control for the context
	 * @throws Exception - error while getting context
	 */
	IViewControl getViewControl(IProjContext project) throws Exception;

	/**
	 * @return get project context list associated with this application
	 * @throws Exception - error while getting context
	 */
	List<IProjContext> getIProjContexts()  throws Exception;


	void startApp(InParams params, IViewProgress progress)
			throws Exception;

    InParams getStartParams();

    String getAppName();
    String[] getAppInfo();

}
