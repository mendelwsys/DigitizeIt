package com.mwlib.app.plugins.svldproj;

import com.mwlib.app.ConfigApp;
import com.mwlib.app.InParamsApp;
import com.mwlib.app.TBuilderProject0;
import com.mwlib.app.storages.mem.DigitizedStorage;
import com.mwlib.app.storages.mem.IPathDefContainer;
import com.mwlib.app.utils.ApplicationStarter;
import com.mwlib.app.utils.PathUtils;
import com.mwlib.ptrace.IOUtils;
import com.mwlib.ptrace.PathDef;
import com.mwlib.utils.Enc;
import ru.ts.factory.IParam;
import ru.ts.res.ImgResources;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.gui.IApplication;
import ru.ts.toykernel.gui.IViewControl;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.pcntxt.gui.defmetainfo.MainformMonitor;
import ru.ts.toykernel.plugins.svxmlproj.XMLSaveModule;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.utils.Files;
import ru.ts.utils.Operation;
import ru.ts.utils.data.InParams;
import ru.ts.utils.data.Pair;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.io.*;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 11.01.14
 * Time: 16:01
 * com.mwlib.app.plugins.svldproj.SaveProj
 */
public class SaveProj extends XMLSaveModule
{

//    public static final String SAVE_TIP_HEADER_DEF_O = Enc.get("$146");
//    public static final String SAVE_MENU_TITLE_DEF_O = Enc.get("$147");
//    public static final String LOAD_TIP_HEADER_DEF_O = Enc.get("$148");
    private static String LOAD_MENU_TITLE_DEF_O = Enc.get("$149");

//    public static final String NEW_TIP_HEADER_DEF_O = Enc.get("$150");
    private static String  NEW_MENU_TITLE_DEF_O = Enc.get("$151");


//    public static final String REOPEN_TIP_HEADER_DEF_O = Enc.get("$152");
    private static String  REOPEN_MENU_TITLE_DEF_O = Enc.get("$153");

    public SaveProj()
    {
        SAVE_TIP_HEADER = Enc.get("$146");
        SAVE_MENU_TITLE = Enc.get("$147");
//        SAVE_TIP_HEADER = SAVE_TIP_HEADER_DEF_O;
//        SAVE_MENU_TITLE = SAVE_MENU_TITLE_DEF_O;
    }

    public SaveProj(IViewControl mainmodule) {
        super(mainmodule);
        SAVE_TIP_HEADER = Enc.get("$146");
        SAVE_MENU_TITLE = Enc.get("$147");
//        SAVE_TIP_HEADER = SAVE_TIP_HEADER_DEF_O;
//        SAVE_MENU_TITLE = SAVE_MENU_TITLE_DEF_O;
    }

    public JToolBar addInToolBar(JToolBar systemtoolbar) throws Exception
    {

        SAVE_TIP_HEADER = Enc.get("$146");
        SAVE_MENU_TITLE = Enc.get("$147");
        LOAD_MENU_TITLE_DEF_O = Enc.get("$149");
        NEW_MENU_TITLE_DEF_O = Enc.get("$151");
        REOPEN_MENU_TITLE_DEF_O = Enc.get("$153");

        JToolBar rv = super.addInToolBar(systemtoolbar);

        MainformMonitor.frame.addWindowListener
                (
                new WindowAdapter(){
        public void windowClosing(WindowEvent we)
        {
            try
            {
                saveProject(false);//Сохранить текущий проект, и если он произошел без ошибок -
                ConfigApp.save2File();//Сохранение текущей конфигурации
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        });
        return rv;
    }

    protected IViewControl mainmodule2;

    public Object init(Object obj) throws Exception
	{
		IParam attr=(IParam)obj;

        if (attr.getName().equalsIgnoreCase(KernelConst.VIEWCNTRL_TAGNAME) && this.mainmodule!=null)
            this.mainmodule2=(IViewControl) attr.getValue();
		else
            return super.init(obj);
		return null;
	}

    public JMenu addMenu(JMenu inmenu) throws Exception
    {
        SAVE_TIP_HEADER = Enc.get("$146");
        SAVE_MENU_TITLE = Enc.get("$147");
        LOAD_MENU_TITLE_DEF_O = Enc.get("$149");
        NEW_MENU_TITLE_DEF_O = Enc.get("$151");
        REOPEN_MENU_TITLE_DEF_O = Enc.get("$153");

        {
            JMenuItem menuItem = new JMenuItem(NEW_MENU_TITLE_DEF_O, KeyEvent.VK_S);
            inmenu.add(menuItem);
            menuItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    try {
                        reInitProject();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }

        {
            JMenuItem menuItem = new JMenuItem(LOAD_MENU_TITLE_DEF_O, KeyEvent.VK_S);
            inmenu.add(menuItem);
            menuItem.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    try {
                        saveProject(false);
                        loadProject(true);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }

        inmenu=super.addMenu(inmenu);
        {
            final JMenu menu = new JMenu(REOPEN_MENU_TITLE_DEF_O);
            reopenMenu=menu;
            menu.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    try {
                        reOrganizeMenu();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
            inmenu.add(menu);
        }
        return inmenu;
    }
    private JMenu reopenMenu;

    private void reOrganizeMenu() throws Exception
    {
        reopenMenu.removeAll();
        ConfigApp.reOrganizeHistory();
        List<String> history= ConfigApp.getOpenHistory();
        for (final String path : history)
        {
            if (mainmodule.getProjContext().getProjectlocation().equalsIgnoreCase(path))
                continue;
            JMenuItem mPath=new JMenuItem(path);
            mPath.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        saveProject(false);
                        loadProject(new File(path));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
            reopenMenu.add(mPath);
        }
        if (reopenMenu.getItemCount()>0)
        {
//            reopenMenu.setEnabled(true);
            reopenMenu.addSeparator();
            final JMenuItem clearList=new JMenuItem(Enc.get("$154"));
            clearList.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    ConfigApp.getOpenHistory().clear();
                    reopenMenu.removeAll();
                }
            });
            reopenMenu.add(clearList);
        }
//        else
//            reopenMenu.setEnabled(false);
    }

    private void reInitProject() throws Exception
    {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TBuilderProject0.loadFileFromRes(out,TBuilderProject0.DEF_RES);

        //проверяем что мы не переоткрываем проект умолчания
        String defXml;
        final String enc = "WINDOWS-1251";
        {
            String currentXml=builder.getBuilderContext().getFullXML(enc, false);

//            FileOutputStream fileOutputStream = new FileOutputStream("D:\\PapaWK\\Projects\\JavaProj\\Victor\\DigitizeIt\\src\\res\\default.xml");
//            fileOutputStream.write(currentXml.getBytes(enc));
//            fileOutputStream.close();
            defXml=new String(out.toByteArray(),enc);
//            for (int i=0;i<Math.min(defXml.length(),currentXml.length());i++)
//            {
//                if (defXml.charAt(i)!=currentXml.charAt(i))
//                {
//                    System.out.println("i = " + i);
//                }
//            }
            if (defXml.equals(currentXml))
                return;
        }

        final IProjContext proj = mainmodule.getProjContext();
        String projectLocation = proj.getProjectlocation();


        String workDir=PathUtils.getDefaultWorkDir();
        String defProjectName=workDir+File.separator+new File(TBuilderProject0.DEF_XML).getName();


        int cnt=0;
        if (defProjectName.equals(projectLocation))
            while (new File(projectLocation).isFile())
            {
                String ext=Files.getExtension(projectLocation);
                String newName=Files.getNameNoExt(projectLocation)+(cnt<10?("0"+cnt):(""+cnt));
                projectLocation=workDir+File.separator+newName+ext;
                cnt++;
            }


        ImageIcon icon1 = ImgResources.getIconByName("images/breakpoint.png", "Warning");
        for (;;)
        {
            File xmldesc = Operation.getFilePath(MainformMonitor.frame, Enc.get("$155"), Enc.get("$156"), "xml",projectLocation);
            if (xmldesc != null)
            {
                xmldesc = checkProjectExtention(xmldesc);
                //проверка  того что имя не совпадает с именем по умолчанию
                if (!defProjectName.equals(xmldesc.getPath()))
                {

                    saveProject2File(xmldesc);
                    break;
                }
                else if
                    ((JOptionPane.showConfirmDialog(null, new String[]
                    {
                            Enc.get("$157")+Files.getNameNoExt(xmldesc.getName()),
                            Enc.get("$158")+workDir+File.separator,
                            Enc.get("$159"),
                            Enc.get("$160")

                    },  Enc.get("$161"),JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE, icon1)!=JOptionPane.YES_OPTION))
                {
                    FileOutputStream fileOutputStream = new FileOutputStream(defProjectName);
                    fileOutputStream.write(out.toByteArray());
                    fileOutputStream.close();
                    loadProject(new File(defProjectName));
                    return;
                }

            }
            else
                break;
        }

        if (new File(defProjectName).isFile())
        {
            ByteArrayOutputStream bosExist = new ByteArrayOutputStream();
            TBuilderProject0.copyFile(bosExist,new FileInputStream(defProjectName));
            if (!defXml.equals(new String(bosExist.toByteArray(),enc)))
            {
                if ((JOptionPane.showConfirmDialog(null, new String[]
                {
                        Enc.get("$162")+Files.getNameNoExt(defProjectName),
                        Enc.get("$163")+Files.getDirectory(defProjectName),
                        Enc.get("$164"),
                },  Enc.get("$165"),JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, icon1)==JOptionPane.YES_OPTION))
                {
                    cnt=0;
                    int i=0;
                    while (new File(defProjectName).isFile())
                    {
                        String ext=Files.getExtension(defProjectName);

                        String nameNoExt = Files.getNameNoExt(defProjectName);
                        int length = nameNoExt.length();
                        if (length >2)
                        {
                            try {
                                cnt=Integer.parseInt(nameNoExt.substring(length -2, length))+1;
                                if (i<100)
                                    nameNoExt=nameNoExt.substring(0,length-2);
                            } catch (NumberFormatException e) {
                               //
                            }
                        }
                        String newName= nameNoExt +(cnt<10?("0"+cnt):(""+cnt));
                        defProjectName=workDir+File.separator+newName+ext;
                        cnt++;
                        i++;
                    }
                }
            }
        }

        {
            FileOutputStream fileOutputStream = new FileOutputStream(defProjectName);
            fileOutputStream.write(out.toByteArray());
            fileOutputStream.close();
            loadProject(new File(defProjectName));
        }

    }

    protected void loadProject(boolean withRequest) throws Exception
    {
        final IProjContext proj = mainmodule.getProjContext();
        String projectlocation = proj.getProjectlocation();
        File xmldesc = Operation.getFilePath(MainformMonitor.frame, Enc.get("$166"), Enc.get("$167"), "xml", projectlocation);
        if (xmldesc != null && xmldesc.isFile())
            loadProject(xmldesc);
    }

    protected void loadProject(File xmldesc) throws Exception
    {
        String oldDir= MainformMonitor.workDir;
        MainformMonitor.workDir= Files.getDirectory(xmldesc.getPath());

        if (
                MainformMonitor.workDir.startsWith("null\\") ||
                MainformMonitor.workDir.startsWith("null/")
            )
             MainformMonitor.workDir= Files.getRunDirectory();


        List<IApplication> apps= ApplicationStarter.parseApp(xmldesc);
        if (apps!=null && apps.size()>0)
        {
            InParams startParams = MainformMonitor.getForm().getStartParams();

            startParams.addParam(new Pair<String, Object>(InParamsApp.optarr[InParamsApp.O_wfl],xmldesc.getPath()));

            MainformMonitor.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            MainformMonitor.getFrame().setVisible(false);
            MainformMonitor.getFrame().dispose();

            ApplicationStarter.startApp(startParams,apps,0);
        }
        else
            MainformMonitor.workDir=oldDir;
    }

    protected void saveProject(boolean withRequest) throws Exception
    {
        File xmldesc = getRequestFile(withRequest);

        xmldesc = checkProjectExtention(xmldesc);

        saveProject2File(xmldesc);
        if (withRequest)
            MainformMonitor.frame.setTitle(MainformMonitor.form.getAppName() + " - [" + mainmodule.getProjContext().getProjectlocation() + "]");
    }

    private File checkProjectExtention(File xmldesc) {
        String ext= Files.getExtension(xmldesc.getName());
        if (!".xml".equalsIgnoreCase(ext))
            xmldesc=new File(xmldesc.getPath()+".xml");
        return xmldesc;
    }

    protected void saveProject2File(File xmldesc) throws Exception
    {
        try {
            if (xmldesc!=null)
            {
                ConfigApp.saveToHistory(xmldesc.getPath());

                String workDir=MainformMonitor.workDir;
                String projName=Files.getNameNoExt(xmldesc.getName());
                try
                {
                    MainformMonitor.workDir=Files.getDirectory(xmldesc.getPath());
                    if (
                            MainformMonitor.workDir.startsWith("null\\") ||
                            MainformMonitor.workDir.startsWith("null/")
                        )
                        MainformMonitor.workDir= Files.getRunDirectory();


                    INodeStorage storage = ((INodeStorage)mainmodule.getProjContext().getStorage());
                    saveStorages(storage,projName);
                    if (mainmodule2!=null)
                    {
                        storage = ((INodeStorage)mainmodule2.getProjContext().getStorage());
                        saveStorages(storage,projName);
                    }
                    super.saveProject2File(xmldesc);

                    IProjContext proj = mainmodule.getProjContext();
                    proj.setProjectlocation(xmldesc.getPath());
                    if (mainmodule2!=null)
                    {
                        proj = mainmodule2.getProjContext();
                        if (proj!=null)
                            proj.setProjectlocation(xmldesc.getPath());
                    }
                    ConfigApp.startApp=xmldesc.getPath(); //Сохранить как стартовое приложение
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    MainformMonitor.workDir=workDir;
                }
    //        finally
    //        {
    //            MainformMonitor.workDir=workDir;
    //        }
            }
        }
        finally
        {
            reOrganizeMenu();
        }
    }

//    private void renameStorages(INodeStorage storage,) throws IOException {
//        Collection<INodeStorage> stors = storage.getChildStorages();
//        for (INodeStorage stor : stors)
//        {
//            if (stor instanceof IPathDefContainer)
//            {
//                InitAbleUtils.replaceParam(
//
//                        new DefAttrImpl()
//                        DigitizedStorage.PO_TRACE_FILE_EXT,
//
//                        );
//            }
//
//        }
//    }


    private void saveStorages(INodeStorage storage,String projName) throws IOException {
        Collection<INodeStorage> stors = storage.getChildStorages();
        for (INodeStorage stor : stors)
        {
            if (stor instanceof IPathDefContainer)
            {
                List<IParam> params = stor.getObjectDescriptor().getParams();

                String storPaths = PathUtils.getStoragesPaths(projName);
                File file = new File(PathUtils.getAbsolutePath(storPaths,MainformMonitor.workDir));
                if (!file.exists())
                    file.mkdir();

                String  potraceFileName= storPaths +File.separator+stor.getObjName()+ DigitizedStorage.PO_TRACE_FILE_EXT;
                br:
                {
                    for (IParam param : params)
                        if (param.getName().equalsIgnoreCase(DigitizedStorage.PO_TRACE_FILE))
                        {
                            //potraceFileName=(String)param.getValue();
                            param.setValue(potraceFileName);
                            break br;
                        }
                    params.add(new DefAttrImpl(DigitizedStorage.PO_TRACE_FILE,potraceFileName));
                }
                PathDef pathDef = ((IPathDefContainer) stor).getPathDef();
                if (pathDef!=null)
                {
                    DataOutputStream fos = null;
                    try
                    {
                        fos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(PathUtils.getAbsolutePath(potraceFileName,MainformMonitor.workDir))));
                        new IOUtils().saveAllToStream(pathDef,fos);
                        fos.flush();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    } finally {
                        if (fos!=null)
                            fos.close();
                    }
                }
            }
        }
    }


}
