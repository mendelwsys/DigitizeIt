package ru.ts.toykernel.gui.apps;

import com.mwlib.utils.Enc;
import ru.ts.toykernel.plugins.IModule;
import ru.ts.toykernel.plugins.IGuiModule;
import ru.ts.toykernel.pcntxt.gui.defmetainfo.MainformMonitor;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.converters.IProjConverter;
import ru.ts.toykernel.gui.panels.BasePicture;
import ru.ts.toykernel.gui.IApplication;
import ru.ts.toykernel.gui.IViewControl;
import ru.ts.utils.data.InParams;
import ru.ts.utils.gui.elems.IViewProgress;
import ru.ts.utils.gui.elems.EmptyProgress;
import ru.ts.res.ImgResources;
import ru.ts.factory.IParam;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

/**
 * Viewer class
 */
public class SFViewer extends BaseInitAble
		implements IApplication
{

	private JPanel mainPanel;
	private JPanel editPanel;
	private JLabel crdStatus;
	private JLabel scaleStatus;
	private JToolBar maintoolbar;

	protected BasePicture picturePanel;
	static protected final String uiName = UIManager.getSystemLookAndFeelClassName();

	protected List<IGuiModule> modulelist = new LinkedList<IGuiModule>();
	private IViewProgress progress;
    private InParams params;

    public void addProjectContext(IProjContext project, IProjConverter converter) throws Exception
	{
		picturePanel.setAllowDraw(false);
		progress.setCurrentOperation("Init modules");
		progress.setProgress((0.8 / modulelist.size()) * progress.getMaxProgress());
		picturePanel.setProjectContext(project, converter, false);
		for (int i = 0; i < modulelist.size(); i++)
		{
			IModule module = modulelist.get(i);
			module.init(new Object[]{new DefAttrImpl(KernelConst.VIEWCNTRL_TAGNAME,picturePanel)});//TODO ВВести в модули функцию reinit()
			//для переинициализации модулей???
			module.registerNameConverter(picturePanel.getProjContext().getNameConverter(), null);
			progress.setProgress((0.8 * (i + 1) / modulelist.size()) * progress.getMaxProgress());
		}
		MainformMonitor.frame.setTitle(getAppName() + " - [" + project.getProjMetaInfo().getProjName() + "]");
		progress.setProgress(1.1 * progress.getMaxProgress());
		picturePanel.setAllowDraw(true);
		picturePanel.refresh(null);
	}

	public List<IProjContext> getIProjContexts() throws Exception
	{
		return new LinkedList<IProjContext>(Arrays.asList(picturePanel.getProjContext()));
	}

	public IViewControl getViewControl(IProjContext project)
	{
		return picturePanel;
	}


	public SFViewer()
	{

	}

	public SFViewer(BasePicture picturePanel, List<IGuiModule> modulelist)
	{
		picturePanel.setApplication(this);
		this.modulelist = modulelist;
		this.picturePanel = picturePanel;
	}

//	public void startApp(InParams params, IProjContext projectctx, ConvB64Initializer initializer, IViewProgress progress) throws Exception
//	{
//		picturePanel1 = new ViewPicturePanel(this, projectctx, initializer, new LinkedList<IGuiModule>());
//		startApp(params, progress);
//	}
//
//	public void startApp(InParams params, IProjContext projectctx, IProjConverter converter, IViewProgress progress) throws Exception
//	{
//		picturePanel1 = new ViewPicturePanel(this, projectctx, converter, new LinkedList<IGuiModule>());
//		startApp(params, progress);
//	}

	public void startApp(InParams params, IViewProgress progress)
			throws Exception
	{
		this.progress = progress;
        this.params=params;
		if (progress == null)
			progress = new EmptyProgress();

		progress.setCurrentOperation("Init modules");
		progress.setProgress(0);

		try
		{
			UIManager.setLookAndFeel(uiName);
		}
		catch (Exception ex)
		{
			System.err.println("Can't set GUI style:" + ex.getMessage());
		}


		MainformMonitor.frame = new JFrame(getAppName());
		ImageIcon icon = ImgResources.getIconByName("images/poolball.gif", "Title");
		if (icon != null)
			MainformMonitor.frame.setIconImage(icon.getImage());

		MainformMonitor.form = this;


//Установить слушателей мыши
		picturePanel.setPictureListeners(crdStatus, scaleStatus);

		maintoolbar.setMargin(new Insets(0, 0, 0, 0));
		maintoolbar.setBorder(new EmptyBorder(0, 0, 0, 0));


//создаем Меню  исходя из модулей
		JMenuBar menuBar = new JMenuBar();
		JMenuItem menuItem;
		JMenu menu;

		for (IGuiModule plugin : new LinkedList<IGuiModule>(modulelist))
		{
			String menuname = plugin.getMenuName();
			if (menuname!=null)
			{
				menu = getMenuByName(menuBar, menuname);
				if (menu == null)
				{
					menu = new JMenu(menuname);
					menuBar.add(menu);
				}
				plugin.addMenu(menu);
			}
			plugin.addInToolBar(maintoolbar);
			plugin.registerListeners(picturePanel);
			plugin.registerNameConverter(picturePanel.getProjContext().getNameConverter(), null);
			if (!picturePanel.getGuiModules().contains(plugin))
				picturePanel.getGuiModules().add(plugin);
		}




		menu = getMenuByName(menuBar, Enc.get("$171"));
		if (menu == null)
		{
			menu = new JMenu(Enc.get("$172"));
			menuBar.add(menu, 0);
		}
		if (menu.getMenuComponentCount() > 0)
			menu.add(new JSeparator());

		menuItem = new JMenuItem(Enc.get("$173"), KeyEvent.VK_E);
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});


//		menu = getMenuByName(menuBar, "Отображение");
//		if (menu == null)
//		{
//			menu = new JMenu("Отображение");
//			menuBar.add(menu);
//		}

//		final JMenuItem menuItem_map = new JMenuItem("Отключить карту", KeyEvent.VK_C);
//		if (menu.getMenuComponentCount() > 0)
//			menu.add(new JSeparator());
//
//		menu.add(menuItem_map);
//
//
//		menuItem_map.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				if (picturePanel1.mapSwitch())
//					menuItem_map.setText("Отключить карту");
//				else
//					menuItem_map.setText("Включить карту");
//			}
//		});

		menu = new JMenu(Enc.get("$174"));

		menuItem = new JMenuItem(Enc.get("$175"), KeyEvent.VK_F1);
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					ImageIcon icon = ImgResources.getIconByName("images/About24.gif", "About..");
					JOptionPane.showMessageDialog(null,
                            getAppInfo(), Enc.get("$176"),
							JOptionPane.INFORMATION_MESSAGE, icon
					);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});

		menuBar.add(menu);

		editPanel.setLayout(new BorderLayout());
		editPanel.add(picturePanel);

		MainformMonitor.frame.setTitle(getAppName() + " - [" + picturePanel.getProjContext().getProjMetaInfo().getProjName() + "]");
		MainformMonitor.frame.setJMenuBar(menuBar);

		MainformMonitor.frame.setContentPane(mainPanel);
		MainformMonitor.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		picturePanel.setFocusable(true);
		MainformMonitor.frame.pack();

		MainformMonitor.frame.setSize(DEF_DRAWWIDTH, DEF_DRAWWIDTH);
		editPanel.setSize(DEF_DRAWWIDTH, DEF_DRAWWIDTH);
		MainformMonitor.frame.setVisible(true);



        picturePanel.requestFocus();


		progress.setCurrentOperation("End loading projectctx");
		progress.setProgress(1.1 * progress.getMaxProgress());
		if (maintoolbar.getComponentCount()==0)
			maintoolbar.setVisible(false);
	}


    public InParams getStartParams() {
        return params;
    }

    private JMenu getMenuByName(JMenuBar menuBar, String menuname)
	{
		JMenu menu;
		int ln = menuBar.getMenuCount();
		menu = null;
		for (int i = 0; i < ln; i++)
		{
			menu = menuBar.getMenu(i);
			if (menu.getText().equals(menuname))
				break;
			else
				menu = null;
		}
		return menu;
	}

	public static final int DEF_DRAWWIDTH = 1280;//Используется внешне для установки начальных размеров окна
	public static final int DEF_DRAWHEIGHT = 960; //Используется внешне для установки начальных размеров окна


	public String getAppName()
	{
		return Enc.get("$177");
	}

    public String[] getAppInfo() {
        return new String[]{
        "ToyGIS Viewer",
        "Version 1.1.2  MWLiBs Ltd.(c)"};
    }

	public Object init(Object obj) throws Exception
	{
		IParam attr=(IParam)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.VIEWCNTRL_TAGNAME) && attr.getValue() instanceof BasePicture)
		{
			picturePanel = (BasePicture) attr.getValue();
			picturePanel.setApplication(this);
		}
		else if (attr.getName().equalsIgnoreCase(KernelConst.PLUGIN_TAGNAME))
			modulelist.add((IGuiModule) attr.getValue());
		return null;
	}
}

