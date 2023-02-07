package com.mwlib.app;

import com.mwlib.utils.Enc;
import org.xml.sax.InputSource;
import ru.ts.forms.StViewProgress;
import ru.ts.toykernel.app.xml.IXMLBuilderContext;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.gui.IApplication;
import ru.ts.toykernel.proj.xml.def.XMLProjBuilder;
import ru.ts.toykernel.xml.IXMLBuilder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 *  Более продвинутый построитель приложения из описателя на базе TBuilderProject0.
 *  Инициализация дескрипторов, описанных в xml, производится после парсинга,
 *  при этом инициализируются те и только те классы, описанные в xml, которые учавствуют в
 * 	приложении. Это очень удобно для возможности описания в одном xml приложений исполняемых в разных
 * 	контекстах, с своими наборами классов соответсвенно . Например в одном xml может быть описан как Web приложение для токата,
 * 	так и обычное java приложение.
 */
public class TBuilderProject01_bu
{
	public static void main(String[] args) throws Exception
	{

        InParamsApp params = new InParamsApp();
        params.translateOptions(args);
        String xmlfilepath=params.get(InParamsApp.optarr[InParamsApp.O_wfl]);

        SAXParser parser= SAXParserFactory.newInstance().newSAXParser();
        Reader rd=new InputStreamReader(new FileInputStream(xmlfilepath),"WINDOWS-1251");

        XMLProjBuilder builder = new XMLProjBuilder(true);
        parser.parse(new InputSource(rd), builder.getProjBuilderHandler(parser.getXMLReader()));

        IXMLBuilderContext bcontext = builder.getBuilderContext();


        IXMLBuilder tagbuilder = bcontext.getBuilderByTagName(KernelConst.APPLICATION_TAGNAME);
        tagbuilder.initByDescriptors();

        List apps = tagbuilder.getLT();
        if (apps!=null && apps.size()>0)
        {

            ((IApplication)apps.get(0)).startApp(params,
                    new StViewProgress(Enc.get("$168")));
        }
	}


}