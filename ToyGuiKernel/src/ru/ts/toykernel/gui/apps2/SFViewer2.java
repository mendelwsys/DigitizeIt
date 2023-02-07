package ru.ts.toykernel.gui.apps2;

import com.mwlib.utils.Enc;
import ru.ts.factory.IParam;
import ru.ts.res.ImgResources;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.converters.IProjConverter;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.gui.IApplication;
import ru.ts.toykernel.gui.IViewControl;
import ru.ts.toykernel.gui.panels.BasePicture;
import ru.ts.toykernel.lang.LangCtrlModule;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.pcntxt.gui.defmetainfo.MainformMonitor;
import ru.ts.toykernel.plugins.IGuiModule;
import ru.ts.utils.data.InParams;
import ru.ts.utils.gui.elems.EmptyProgress;
import ru.ts.utils.gui.elems.IViewProgress;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Viewer class
 */
public class SFViewer2 extends BaseInitAble
		implements IApplication
{

	private JPanel mainPanel;

	private JLabel crdStatus;
	private JLabel scaleStatus;
	private JToolBar maintoolbar;
    private JSplitPane editPanel;

    protected BasePicture picturePanel1;
    protected BasePicture picturePanel2;

    static protected final String uiName = UIManager.getSystemLookAndFeelClassName();

    public InParams getStartParams() {
        return params;
    }

    protected List<IGuiModule> modulelist = new LinkedList<IGuiModule>();
	private IViewProgress progress;
    private InParams params;

    public void addProjectContext(IProjContext project, IProjConverter converter) throws Exception
	{
//		picturePanel1.setAllowDraw(false);
//		progress.setCurrentOperation("Init modules");
//		progress.setProgress((0.8 / modulelist.size()) * progress.getMaxProgress());
//		picturePanel1.setProjectContext(project, converter, false);
//		for (int i = 0; i < modulelist.size(); i++)
//		{
//			IModule module = modulelist.get(i);
//			module.init(new Object[]{new DefAttrImpl(KernelConst.VIEWCNTRL_TAGNAME, picturePanel1)});//TODO ВВести в модули функцию reinit()
//			//для переинициализации модулей???
//			module.registerNameConverter(picturePanel1.getProjContext().getNameConverter(), null);
//			progress.setProgress((0.8 * (i + 1) / modulelist.size()) * progress.getMaxProgress());
//		}
//		MainformMonitor.frame.setTitle(getAppCaption() + " - [" + project.getProjMetaInfo().getProjName() + "]");
//		progress.setProgress(1.1 * progress.getMaxProgress());
//		picturePanel1.setAllowDraw(true);
//		picturePanel1.refresh(null);
	}

	public List<IProjContext> getIProjContexts() throws Exception
	{
		return new LinkedList<IProjContext>(Arrays.asList(picturePanel1.getProjContext()));
	}

	public IViewControl getViewControl(IProjContext project)
	{
		return picturePanel1;
	}


	public SFViewer2()
	{
//        System.out.println("mainPanel = " + mainPanel);
	}

	public SFViewer2(BasePicture picturePanel1,BasePicture picturePanel2, List<IGuiModule> modulelist)
	{
		picturePanel1.setApplication(this);
		this.modulelist = modulelist;
		this.picturePanel1 = picturePanel1;
        this.picturePanel2 = picturePanel2;
	}


	public void startApp(InParams params, IViewProgress progress)
			throws Exception
	{
        this.params=params;
		this.progress = progress;
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
		picturePanel1.setPictureListeners(crdStatus, scaleStatus);

        setMenu();


        //editPanel.setLayout(new BorderLayout());
		editPanel.setLeftComponent(picturePanel1);
        editPanel.setRightComponent(picturePanel2);


		MainformMonitor.frame.setContentPane(mainPanel);
		MainformMonitor.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		picturePanel1.setFocusable(true);
		MainformMonitor.frame.pack();

		MainformMonitor.frame.setSize(DEF_DRAWWIDTH, DEF_DRAWWIDTH);
		editPanel.setSize(DEF_DRAWWIDTH, DEF_DRAWWIDTH);
		MainformMonitor.frame.setVisible(true);

        editPanel.setDividerLocation(0.85);


		progress.setCurrentOperation("End loading projectctx");
		progress.setProgress(1.1 * progress.getMaxProgress());
		if (maintoolbar.getComponentCount()==0)
			maintoolbar.setVisible(false);

        MainformMonitor.frame.requestFocus();
        picturePanel1.requestFocus();
        MainformMonitor.frame.repaint();
	}

    public void setMenu() throws Exception
    {
        maintoolbar.removeAll();
        maintoolbar.setMargin(new Insets(0, 0, 0, 0));
        maintoolbar.setBorder(new EmptyBorder(0, 0, 0, 0));

        //создаем Меню  исходя из модулей
        JMenuBar menuBar = new JMenuBar();
        JMenuItem menuItem;
        JMenu menu;

        for (IGuiModule plugin : new LinkedList<IGuiModule>(modulelist))
        {
            insertPlugin(menuBar, plugin);
        }

        br:
        {
            for (IGuiModule iGuiModule : modulelist)
            {
                if (iGuiModule.getModuleName().equals(LangCtrlModule.MODULENAME))
                    break br;
            }
            insertPlugin(menuBar, new LangCtrlModule(this));
        }


        menu = getMenuByName(menuBar, Enc.get("$178"));
        if (menu == null)
        {
            menu = new JMenu(Enc.get("$179"));
            menuBar.add(menu, 0);
        }
        if (menu.getMenuComponentCount() > 0)
            menu.add(new JSeparator());

        menuItem = new JMenuItem(Enc.get("$180"), KeyEvent.VK_E);
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });


        menu = new JMenu(Enc.get("$181"));

        menuItem = new JMenuItem(Enc.get("$182"), KeyEvent.VK_F1);
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    ImageIcon icon = ImgResources.getIconByName("images/About24.gif", "About..");
                    JOptionPane.showMessageDialog(null, getAppInfo(), Enc.get("$183"),
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

        MainformMonitor.frame.setTitle(getAppName() + " - [" + picturePanel1.getProjContext().getProjectlocation() + "]");
        MainformMonitor.frame.setJMenuBar(menuBar);

        maintoolbar.updateUI();
        MainformMonitor.frame.repaint();
    }

    private void insertPlugin(JMenuBar menuBar, IGuiModule plugin) throws Exception {
        JMenu menu;
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
        plugin.registerListeners(picturePanel1);
        plugin.registerNameConverter(picturePanel1.getProjContext().getNameConverter(), null);
        if (!picturePanel1.getGuiModules().contains(plugin))
            picturePanel1.getGuiModules().add(plugin);
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


    public String version = "2.2";


	public String getAppName()
	{
		return Enc.get("$184")+version;
	}

    public String[] getAppInfo() {
        return new String[]{
                Enc.get("$185")+version,
                //"MWLiBs Ltd.(c)"
        };
    }

	public Object init(Object obj) throws Exception
	{
		IParam attr=(IParam)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.VIEWCNTRL_TAGNAME) && attr.getValue() instanceof BasePicture)
		{
            if (picturePanel1== null)
            {
			    picturePanel1 = (BasePicture) attr.getValue();
			    picturePanel1.setApplication(this);
            }
            else
            {
                picturePanel2 = (BasePicture) attr.getValue();
                picturePanel2.setApplication(this);
            }
		}
		else if (attr.getName().equalsIgnoreCase(KernelConst.PLUGIN_TAGNAME))
			modulelist.add((IGuiModule) attr.getValue());
		return null;
	}
}

