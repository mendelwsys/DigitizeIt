package ru.ts.toykernel.plugins;

import ru.ts.toykernel.plugins.IModule;

import javax.swing.*;
import java.awt.*;

/**
 * Gui module
 */
public interface IGuiModule extends IModule
{
    public static final String MENU_NAME_PARAM = "MENU_NAME";

    /**
     * @return name of upper level menu for allocate menu controls of this module
     */
	public String getMenuName();

	/**
	 * update menus
	 * @param inmenu - input menu
	 * @return updated menu
	 * @throws Exception - throw when error occurs
	 */
	JMenu addMenu(JMenu inmenu) throws Exception;

	/**
	 * update popups menu 
	 * @param inmenu - input menu
	 * @return updated menu
	 * @throws Exception - throw when error occurs
	 */
	JPopupMenu addPopUpMenu(JPopupMenu inmenu) throws Exception;

	/**
	 * Register module listeners in panel
	 * @param component - component for listner
	 * @throws Exception - throw when error occurs
	 */
	void registerListeners(JComponent component) throws Exception;

	/**
	 * register buttons in tool bar
	 * @param systemtoolbar - tool bar of project where the module was loaded
	 * @throws Exception - throw when error occurs
	 * @return - changed toolbar
	 */
	JToolBar addInToolBar(JToolBar systemtoolbar) throws Exception;
	/**
	 * paint module information in graphic context
	 * @param g - graphic context
	 * @throws Exception - error painting module
	 */
	void paintMe(Graphics g) throws Exception;

}
