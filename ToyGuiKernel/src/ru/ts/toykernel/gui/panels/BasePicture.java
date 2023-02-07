package ru.ts.toykernel.gui.panels;

import ru.ts.toykernel.gui.IViewControl;
import ru.ts.toykernel.gui.IApplication;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.converters.IProjConverter;

import javax.swing.*;

/**
 * Абстрактный класс для рисовалки  
 */
public abstract class BasePicture extends JPanel
		implements IViewControl

{
	public abstract void setProjectContext(IProjContext project, IProjConverter converter,boolean isinit)
			throws Exception;

	protected abstract void showMetersOnPixel() throws Exception;

	public abstract void setApplication(IApplication application);

	public abstract void setPictureListeners(JLabel crdStatus, JLabel scaleStatus);

	public abstract void setAllowDraw(boolean b);
}
