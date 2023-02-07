package com.mwlib.app;

import com.mwlib.app.utils.ApplicationStarter;
import com.mwlib.app.utils.InitAbleUtils;
import com.mwlib.app.utils.PathUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.mwlib.utils.Enc;
import ru.ts.toykernel.pcntxt.gui.defmetainfo.MainformMonitor;
import ru.ts.toykernel.gui.IApplication;
import ru.ts.toykernel.app.xml.IXMLBuilderContext;
import ru.ts.utils.Files;
import ru.ts.utils.data.Pair;

/**
 *  Простейшей построитель приложения из описателя.
 *  Производится инициализация всех дескрипторов, описанных в xml, во время их парсинга
 */
public class TBuilderProject0
{

    public static final String DEF_XML = "default.xml";
    public static final String DEF_CONF_XML = "config.xml";
    public static final String DEF_RES = "/res/"+ DEF_XML;
    public static final String DEF_CONF_RES = "/res/"+ DEF_CONF_XML;
    public static final String CONFIG_TAG = "config";

    public static void main(String[] args) throws Exception
	{
        MainformMonitor.workDir = System.getProperty("user.dir");
        String defaultXml = MainformMonitor.workDir + File.separator + new File(DEF_XML).getName();
        InParamsApp.defarr[InParamsApp.O_wfl]= defaultXml;
        InParamsApp.defarr[InParamsApp.O_cfg]= MainformMonitor.workDir+File.separator+new File(DEF_CONF_RES).getName();




		InParamsApp params = new InParamsApp();
		params.translateOptions(args);


        String lng=params.get(InParamsApp.optarr[InParamsApp.O_lng]);
        Enc.initEncoder(ConfigApp.class,lng);



        ConfigApp.configPath=params.get(InParamsApp.optarr[InParamsApp.O_cfg]);
        if (getResource(ConfigApp.configPath,DEF_CONF_RES))
            return;
        {
            //Здесь идет разбор файла конфигурации, если текущий проект пуст -тогда читаем слежующий входной параметр файл проекта

            InputStream is = null;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                String str;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is= new FileInputStream(ConfigApp.configPath), "WINDOWS-1251"));
                while((str= bufferedReader.readLine())!=null)
                     stringBuilder.append(str);
            }
            finally
            {
                if (is!=null)
                    is.close();
            }

            IXMLBuilderContext ctx = InitAbleUtils.getIXMLBuilderContext(stringBuilder.toString());
            List<ConfigApp> cfgApps=ctx.getBuilderByTagName(CONFIG_TAG).getLT();
            if (cfgApps!=null && cfgApps.size()>0 && ConfigApp.startApp!=null && ConfigApp.startApp.length()>0)
            {
                ConfigApp.startApp=PathUtils.getAbsolutePath(ConfigApp.startApp,MainformMonitor.workDir);
                params.addParam(new Pair<String, Object>(InParamsApp.optarr[InParamsApp.O_wfl],ConfigApp.startApp));
            }
        }

		String xmlFilePath=params.get(InParamsApp.optarr[InParamsApp.O_wfl]);
        if (getResource(xmlFilePath,DEF_RES))
        {
            if (!new File(defaultXml).isFile())
                return;
            xmlFilePath=defaultXml;
        }

        if (PathUtils.isAbsolutePath(xmlFilePath,MainformMonitor.workDir))
            MainformMonitor.workDir = new File(Files.getDirectory(xmlFilePath)).getPath();
        List<IApplication> apps= ApplicationStarter.parseApp(xmlFilePath);
        if (apps!=null && apps.size()>0)
            ApplicationStarter.startApp(params,apps,0);
	}

//    private static void initEncoder(String lng) throws IOException {
//        Map<String, Properties> lang2Names = new HashMap<String, Properties>();
//        InputStream ri = TBuilderProject0.class.getResourceAsStream("/res/"+lng + "Lng.properties");
//        Properties properties = new Properties();
//        properties.load(new InputStreamReader(ri,"UTF8"));
//        ri.close();
//        lang2Names.put(lng,properties);
//        Enc.setEnc(lang2Names, lng);
//    }

    public static boolean getResource(String xmlfilepath,String resName) {
        if (xmlfilepath==null || xmlfilepath.length()==0 || !new File(xmlfilepath).isFile())
        {
                try {
                    loadFileFromRes(getDefaultOutputStream(resName),resName);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return true;
                }
        }
        return false;
    }

    public static void loadFileFromRes(OutputStream out,String resName) throws Exception
    {
        InputStream in;
        in = TBuilderProject0.class.getResourceAsStream(resName);
        if (in==null)
            throw new Exception("Can't find default resource: "+resName);
        copyFile(out, in);

    }

    public static void copyFile(OutputStream out, InputStream in) throws IOException {
        //Copy the file to working dir and load from that file
        {
            int available = in.available();
            if (available<=0)
                available=1024*1024;
            byte[] buffer=new byte[available];
            int incnt;
            while((incnt=in.read(buffer))>0)
                out.write(buffer,0,incnt);
            out.close();
            in.close();
        }
    }

    public static OutputStream getDefaultOutputStream(String resName) throws Exception {
        String xmlfilepathTarget= MainformMonitor.workDir+ File.separator+new File(resName).getName();
        if (!new File(xmlfilepathTarget).createNewFile())
            throw new Exception("Can't create new file: " +xmlfilepathTarget);
        return new FileOutputStream(xmlfilepathTarget);
    }

}