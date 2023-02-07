package com.mwlib.app;

import com.mwlib.app.utils.InitAbleUtils;
import ru.ts.factory.IObjectDesc;
import ru.ts.factory.IParam;
import ru.ts.toykernel.attrs.def.DefAttrImpl;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.proj.xml.IXMLProjBuilder;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 04.05.14
 * Time: 8:42
 * Класс конфигурации
 */
public class ConfigApp extends BaseInitAble
{
    public static final String OPEN_HISTORY="OPEN_HISTORY";
    public static final String HISTORY_MAX_SZ="HISTORY_MAX_SIZE";


    public static final String START_APP="START";

    public static String startApp;

    public static LinkedList<String> getOpenHistory()
    {
        return openHistory;
    }

    private static LinkedList<String> openHistory= new LinkedList<String>();
    private  static int historySize=10;
    private static ConfigApp incarnate;
    private  static IXMLProjBuilder builder;
    public  static String configPath;

    public Object[] init(Object ...objs) throws Exception
    {
        incarnate=this;
        return super.init(objs);
    }

    public Object init(Object obj) throws Exception
    {
        IParam attr=(IParam)obj;
        if (attr.getName().equalsIgnoreCase(START_APP))
            startApp=(String)attr.getValue();
        else if (attr.getName().equalsIgnoreCase(OPEN_HISTORY))
            openHistory.add((String)attr.getValue());
        else if (attr.getName().equalsIgnoreCase(KernelConst.APPBUILDER_TAGNAME))
            builder=(IXMLProjBuilder) attr.getValue();
        else if (attr.getName().equalsIgnoreCase(HISTORY_MAX_SZ))
        {
            try {
                historySize=Integer.parseInt((String) attr.getValue());
            } catch (NumberFormatException e)
            {//
            }
        }
        return null;
    }

    public static void save2File() throws Exception
    {
        openHistory.remove(startApp);

        reOrganizeHistory();
        {
            InitAbleUtils.removeParamFromInitAble(incarnate,new DefAttrImpl(OPEN_HISTORY,""));
            List<IParam> params = new LinkedList<IParam>();
            params.add(new DefAttrImpl(START_APP,startApp));
            params.add(new DefAttrImpl(HISTORY_MAX_SZ,String.valueOf(historySize)));
            InitAbleUtils.setInitAbleByParams(incarnate,params);
            params = new LinkedList<IParam>();
            for (String openPath : openHistory)
                params.add(new DefAttrImpl(OPEN_HISTORY,openPath));
            InitAbleUtils.addInitAbleByParams(incarnate,params);
        }
        InitAbleUtils.saveProject2File(builder,new File(configPath));
    }

    public static void reOrganizeHistory()
    {
        LinkedList<String> openHistory=new LinkedList<String>();
        Set<String> dups = new HashSet<String>();

        for (String s : ConfigApp.openHistory)
        {
            if (new File(s).isFile() && !dups.contains(s))
            {
                dups.add(s);
                openHistory.add(s);
            }
        }

        ConfigApp.openHistory=openHistory;

    }

    public static void saveToHistory(String projPath)
    {
        Set<String> hs=new HashSet<String>(openHistory);
        if (hs.contains(projPath))
            openHistory.remove(projPath);

        while (openHistory.size()>historySize-1)
            openHistory.removeLast();
        openHistory.addFirst(projPath);
    }
}
