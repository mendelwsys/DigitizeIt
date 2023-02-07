package ru.ts.toykernel.gui;

import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.plugins.IGuiModule;
import ru.ts.factory.IInitAble;

import javax.swing.*;
import java.util.List;

/**
 * View control interface
 */
public interface IViewControl extends IView
{
	/**
	 * @return application interface
	 */
	IApplication getApplication();
	/**
	 * @return underlying component
	 */
	JComponent getComponent();

	/**
	 * Refresh of view control
	 * @param args - aruments of view control
	 * @throws Exception - refresh error
	 */
	void refresh(Object args) throws Exception;

	/**
	 * scroll picture in ViewControl by X and Y
	 * @param dXdY - delta X and dealta Y on draw coordinates
	 */
	void shiftPictureXY(int[] dXdY);

	List<IGuiModule> getGuiModules();

	void setGuiModules(List<IGuiModule> drawmoduls);
}
