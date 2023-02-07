package com.mwlib.app.utils;

import com.mwlib.app.ConfigApp;
import com.mwlib.app.InParamsApp;
import com.mwlib.utils.Enc;
import org.xml.sax.InputSource;
import ru.ts.forms.StViewProgress;
import ru.ts.toykernel.app.xml.IXMLBuilderContext;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.gui.IApplication;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.proj.xml.def.XMLProjBuilder;
import ru.ts.toykernel.xml.IXMLBuilder;
import ru.ts.utils.data.InParams;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 02.05.14
 * Time: 12:19
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationStarter
{

    public static List<IApplication> parseApp(String xmlfilepath) throws Exception
    {
        return parseApp(new File(xmlfilepath));
    }

    public static List<IApplication> parseApp(File xmlfilepath) throws Exception
    {

        SAXParser parser= SAXParserFactory.newInstance().newSAXParser();
        Reader rd=new InputStreamReader(new FileInputStream(xmlfilepath),"WINDOWS-1251");

        XMLProjBuilder builder = new XMLProjBuilder();
        parser.parse(new InputSource(rd), builder.getProjBuilderHandler(parser.getXMLReader()));

        IXMLBuilderContext bcontext = builder.getBuilderContext();

        List lst = bcontext.getBuilderByTagName(KernelConst.PROJCTXT_TAGNAME).getLT();
        for (Object aLst : lst)
        {
            IProjContext projContext = (IProjContext) aLst;
            projContext.setProjectlocation(xmlfilepath.getPath());
        }
        ConfigApp.startApp=xmlfilepath.getPath();
        //Сразу сохранить в истории (тогда модуль сохраннения загрузит меню переоткрытия)
        ConfigApp.saveToHistory(xmlfilepath.getPath());
        List apps = bcontext.getBuilderByTagName(KernelConst.APPLICATION_TAGNAME).getLT();

//        IXMLBuilder tagbuilder = bcontext.getBuilderByTagName(KernelConst.APPLICATION_TAGNAME);
//        tagbuilder.initByDescriptors();

        if (apps.size()>0)
            return (List<IApplication>)apps;
        return null;
    }

    public static void startApp(InParams params, List<IApplication> apps,int ix) throws Exception
    {
        (apps.get(ix)).startApp(params, new StViewProgress(Enc.get("$170")));
    }

}
