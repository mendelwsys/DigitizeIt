package com.mwlib.app.plugins;

import com.mwlib.utils.Enc;
import ru.ts.factory.IFactory;
import ru.ts.factory.IParam;
import ru.ts.res.ImgResources;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.gui.IViewControl;
import ru.ts.toykernel.plugins.IAnswerBean;
import ru.ts.toykernel.plugins.ICommandBean;
import ru.ts.toykernel.plugins.IGuiModule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Open Project module
 */
abstract public class OpenModule extends BaseInitAble implements IGuiModule
{
	public static final String MODULENAME = "OPENMODULE";

    public static final String OPENIT = Enc.get("$112");

    public static final String HEADER = Enc.get("$113");

    public static final String CLEANIT=Enc.get("$114");

    protected String getCleanCaption()
    {
        return CLEANIT;
    }

    protected String getOpenCaption()
    {
        return OPENIT;
    }

    protected String getOpenHeader()
    {
        return HEADER;
    }

    protected IViewControl mainmodule;

	public OpenModule()
	{
	}

	public OpenModule(IViewControl mainmodule)
	{
		this.mainmodule = mainmodule;
	}

	public String getMenuName()
	{
		return Enc.get("$115");
	}

	public JMenu addMenu(JMenu inmenu) throws Exception
	{
        if (inmenu.getMenuComponentCount()!=0)
            inmenu.addSeparator();
        {
            JMenuItem menuItem = new JMenuItem(getOpenCaption(), KeyEvent.VK_O);
            inmenu.add(menuItem);
            menuItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    loadFile(getOpenHeader());
                }
            });
        }
        {
            JMenuItem menuItem = new JMenuItem(getCleanCaption(), KeyEvent.VK_R);
            inmenu.add(menuItem);
            menuItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    cleanAll();
                }
            });
        }
		return inmenu;
	}

	abstract protected void loadFile(String header);

    abstract protected void cleanAll();

	public JPopupMenu addPopUpMenu(JPopupMenu inmenu) throws Exception
	{
		return inmenu;
	}

	public void registerListeners(JComponent component) throws Exception
	{
	}

	public JToolBar addInToolBar(JToolBar systemtoolbar) throws Exception
	{
        if (systemtoolbar.getComponentCount()!=0)
            systemtoolbar.addSeparator();
        {
            JButton button = new JButton();
            ImageIcon icon = ImgResources.getIconByName("images/menu-open.png", "Open");
            if (icon != null)
                button.setIcon(icon);
            button.setToolTipText(getOpenCaption());//TODO воспользоваться конвертором для имен
            button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    loadFile(getOpenHeader());
                }
            });
            button.setMargin(new Insets(0, 0, 0, 0));
            systemtoolbar.add(button);
        }


        {
            JButton button = new JButton();
            ImageIcon icon = ImgResources.getIconByName("images/clean.png", "Clean");
            if (icon != null)
                button.setIcon(icon);
            button.setToolTipText(getCleanCaption());//TODO воспользоваться конвертором для имен
            button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    cleanAll();
                }
            });
            button.setMargin(new Insets(0, 0, 0, 0));
            systemtoolbar.add(button);
        }

		return systemtoolbar;
	}

	public void paintMe(Graphics g)
	{
	}

	public void registerNameConverter(INameConverter nameConverter, IFactory<INameConverter> factory)
	{
	}

	public void unload()
	{
	}

	public String getModuleName()
	{
		return MODULENAME;
	}

	public IAnswerBean execute(ICommandBean cmd)
	{
		throw new UnsupportedOperationException();
	}


	public Object init(Object obj) throws Exception
	{
		IParam attr=(IParam)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.VIEWCNTRL_TAGNAME))
			this.mainmodule = (IViewControl) attr.getValue();

		return null;
	}
}
