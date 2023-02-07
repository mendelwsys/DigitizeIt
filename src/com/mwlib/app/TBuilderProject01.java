package com.mwlib.app;

import com.mwlib.app.utils.ApplicationStarter;
import com.sun.glass.ui.Application;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.List;

import ru.ts.toykernel.proj.xml.def.XMLProjBuilder;
import ru.ts.toykernel.gui.IApplication;
import ru.ts.toykernel.app.xml.IXMLBuilderContext;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.xml.IXMLBuilder;
import ru.ts.forms.StViewProgress;

/**
 *  Более продвинутый построитель приложения из описателя на базе TBuilderProject0.
 *  Инициализация дескрипторов, описанных в xml, производится после парсинга,
 *  при этом инициализируются те и только те классы, описанные в xml, которые учавствуют в
 * 	приложении. Это очень удобно для возможности описания в одном xml приложений исполняемых в разных
 * 	контекстах, с своими наборами классов соответсвенно . Например в одном xml может быть описан как Web приложение для токата,
 * 	так и обычное java приложение.
 */
public class TBuilderProject01
{
	public static void main(String[] args) throws Exception
	{
        InParamsApp params = new InParamsApp();
        params.translateOptions(args);
        String xmlfilepath=params.get(InParamsApp.optarr[InParamsApp.O_wfl]);
        List<IApplication> apps=ApplicationStarter.parseApp(xmlfilepath);
        if (apps!=null && apps.size()>0)
            ApplicationStarter.startApp(params,apps,0);
	}

}