package com.mwlib.app;

import com.mwlib.utils.Enc;
import org.xml.sax.InputSource;
import ru.ts.forms.StViewProgress;
import ru.ts.toykernel.app.xml.IXMLBuilderContext;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.gui.IApplication;
import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.pcntxt.gui.defmetainfo.MainformMonitor;
import ru.ts.toykernel.proj.xml.def.XMLProjBuilder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.List;

/**
 *  Простейшей построитель приложения из описателя.
 *  Производится инициализация всех дескрипторов, описанных в xml, во время их парсинга
 */
public class TBuilderProject0_bu
{

    public static final String DEF_XML = "default.xml";
    public static final String DEF_RES = "/res/"+ DEF_XML;

    public static void main(String[] args) throws Exception
	{
        MainformMonitor.workDir = System.getProperty("user.dir");
        InParamsApp.defarr[InParamsApp.O_wfl]= MainformMonitor.workDir+File.separator+new File(DEF_XML).getName();


		InParamsApp params = new InParamsApp();
		params.translateOptions(args);
        InputStream in;
		String xmlfilepath=params.get(InParamsApp.optarr[InParamsApp.O_wfl]);
        {
            File file = new File(xmlfilepath);
            if (file.isFile())
                in = new FileInputStream(xmlfilepath);
            else
            {
                try {
                    in = TBuilderProject0_bu.class.getResourceAsStream(DEF_RES);
                    if (in==null)
                        throw new Exception("Can't find default resource: "+DEF_RES);
                    //Copy the file to working dir and load from that file
                    {

                        String xmlfilepathTarget=MainformMonitor.workDir+File.separator+new File(DEF_XML).getName();
                        if (!new File(xmlfilepathTarget).createNewFile())
                            throw new Exception("Can't create new file: " +xmlfilepathTarget);
                        OutputStream out=new FileOutputStream(xmlfilepathTarget);

                        int available = in.available();
                        if (available<=0)
                            available=1024*1024;
                        byte[] buffer=new byte[available];
                        int incnt;
                        while((incnt=in.read(buffer))>0)
                            out.write(buffer,0,incnt);
                        out.close();
                        in.close();
                        in = new FileInputStream(xmlfilepathTarget);
                    }
                }
                catch (Exception e)
                {
//                    System.out.println("Can't find file or resource:"+xmlfilepath);
                    e.printStackTrace();
                    return;
                }
            }
        }



		SAXParser parser= SAXParserFactory.newInstance().newSAXParser();
        Reader rd=new InputStreamReader(in,"WINDOWS-1251");

		XMLProjBuilder builder = new XMLProjBuilder();
		parser.parse(new InputSource(rd), builder.getProjBuilderHandler(parser.getXMLReader()));

		IXMLBuilderContext bcontext = builder.getBuilderContext();


        List lst = bcontext.getBuilderByTagName(KernelConst.PROJCTXT_TAGNAME).getLT();
        for (Object aLst : lst)
        {
            IProjContext projContext = (IProjContext) aLst;
            projContext.setProjectlocation(xmlfilepath);
        }

		List apps = bcontext.getBuilderByTagName(KernelConst.APPLICATION_TAGNAME).getLT();
		if (apps!=null && apps.size()>0)
		{
			((IApplication)apps.get(0)).startApp(params,
					new StViewProgress(Enc.get("$169")));
		}
	}

}