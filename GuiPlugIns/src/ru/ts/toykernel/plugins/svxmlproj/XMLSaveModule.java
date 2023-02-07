package ru.ts.toykernel.plugins.svxmlproj;

import com.mwlib.utils.Enc;
import ru.ts.toykernel.plugins.IGuiModule;
import ru.ts.toykernel.gui.IViewControl;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.factory.IFactory;
import ru.ts.factory.IParam;
import ru.ts.toykernel.pcntxt.gui.defmetainfo.MainformMonitor;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.utils.Operation;
import ru.ts.res.ImgResources;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.plugins.ICommandBean;
import ru.ts.toykernel.plugins.IAnswerBean;
import ru.ts.toykernel.proj.xml.IXMLProjBuilder;
import ru.ts.toykernel.app.xml.IXMLBuilderContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;

/**
 * module for saving xml descriptor of project
 * ru.ts.toykernel.plugins.svxmlproj.XMLSaveModule
 */
public class XMLSaveModule extends BaseInitAble implements IGuiModule
{
	public static final String MODULENAME = "SAVEXMLMODULE";

//    public static final String SAVE_TIP_HEADER_DEF = Enc.get("$71");
//    public static final String SAVE_MENU_TITLE_DEF = Enc.get("$72");

    protected String SAVE_TIP_HEADER = Enc.get("$71");
    protected String SAVE_MENU_TITLE  = Enc.get("$72");


    protected IViewControl mainmodule;
	protected IXMLProjBuilder builder;


	public XMLSaveModule()
	{
	}

	public XMLSaveModule(IViewControl mainmodule)
	{
		this.mainmodule = mainmodule;
	}

	public String getMenuName()
	{
		return Enc.get("$73");
	}

	public JMenu addMenu(JMenu inmenu) throws Exception
	{
//    public static final String SAVE_TIP_HEADER_DEF = Enc.get("$71");
//    public static final String SAVE_MENU_TITLE_DEF = Enc.get("$72");

//    protected String SAVE_TIP_HEADER = SAVE_TIP_HEADER_DEF;
//        String SAVE_MENU_TITLE = Enc.get("$72");

		JMenuItem menuItem = new JMenuItem(SAVE_MENU_TITLE, KeyEvent.VK_S);
		inmenu.add(menuItem);
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
                try {
                    saveProject(true);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
		});
		return inmenu;
	}

//	protected void saveProject(boolean withRequest)
//	{
//		OutputStream os = null;
//		try
//		{
//			final IProjContext proj = mainmodule.getProjContext();
//            String projectlocation = proj.getProjectlocation();
//            File xmldesc;
//            xmldesc = getRequestFile(withRequest, projectlocation);
//
//            if (xmldesc != null)
//			{
//				os =  new FileOutputStream(xmldesc);
//				IXMLBuilderContext context = builder.getBuilderContext();
//				final String enc = "WINDOWS-1251";
//				os.write(context.getFullXML(enc, false).getBytes(enc));
//				os.flush();
//			}
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			try
//			{
//				if (os!=null)
//					os.close();
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//			}
//		}
//	}


    protected void saveProject(boolean withRequest) throws Exception
    {
        File xmldesc = getRequestFile(withRequest);
        saveProject2File(xmldesc);
    }

    protected void saveProject2File(File xmldesc) throws Exception
    {
        OutputStream os = null;
        try {
            if (xmldesc != null)
            {
                os =  new FileOutputStream(xmldesc);
                IXMLBuilderContext context = builder.getBuilderContext();
                final String enc = "WINDOWS-1251";
                os.write(context.getFullXML(enc, false).getBytes(enc));
                os.flush();
            }
        }
        finally
        {
            try
            {
                if (os!=null)
                    os.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    protected File getRequestFile(boolean withRequest) throws Exception
    {

        final IProjContext proj = mainmodule.getProjContext();
        String projectlocation = proj.getProjectlocation();

        File xmldesc;
        if (withRequest)
            xmldesc = Operation.getFilePath(MainformMonitor.frame, Enc.get("$74"), Enc.get("$75"), "xml", projectlocation);
        else
            xmldesc=new File(projectlocation);
        return xmldesc;
    }

    public JPopupMenu addPopUpMenu(JPopupMenu inmenu) throws Exception
	{
		return inmenu;
	}

	public void registerListeners(JComponent component) throws Exception
	{
	}

	public JToolBar addInToolBar(JToolBar systemtoolbar) throws Exception
	{

//    public static final String SAVE_TIP_HEADER_DEF = Enc.get("$71");
//    public static final String SAVE_MENU_TITLE_DEF = Enc.get("$72");

//        String SAVE_TIP_HEADER = Enc.get("$71");
//    protected String SAVE_MENU_TITLE = SAVE_MENU_TITLE_DEF;

		JButton button = new JButton();
		ImageIcon icon = ImgResources.getIconByName("images/checkOut.png", "SaveProject");
		if (icon != null)
			button.setIcon(icon);
		button.setToolTipText(SAVE_TIP_HEADER);//TODO воспользоваться конвертором для имен
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
                try {
                    saveProject(false);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
		});
		button.setMargin(new Insets(0, 0, 0, 0));
		systemtoolbar.add(button);

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
		else if (attr.getName().equalsIgnoreCase(KernelConst.APPBUILDER_TAGNAME))
			this.builder=(IXMLProjBuilder) attr.getValue();
		return null;
	}
}