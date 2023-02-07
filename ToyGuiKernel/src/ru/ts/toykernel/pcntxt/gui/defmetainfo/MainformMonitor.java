package ru.ts.toykernel.pcntxt.gui.defmetainfo;

import ru.ts.toykernel.gui.IApplication;

import javax.swing.*;

/**
 * application monitor
 * static class reference to main frame
 */
public class MainformMonitor
{

    public static String workDir;

    private MainformMonitor(){}

	/**
	 * @return frame of application
	 */
	public static JFrame getFrame()
	{
		return frame;
	}

	/**
	 * @return form of application
	 */

	public static IApplication getForm()
	{
		return form;
	}

	public static JFrame frame; //Главное окно окно
	public static IApplication form;//Приложение


}
