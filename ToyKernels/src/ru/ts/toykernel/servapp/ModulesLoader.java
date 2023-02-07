package ru.ts.toykernel.servapp;

import ru.ts.toykernel.proj.xml.def.XMLProjBuilder;
import ru.ts.toykernel.xml.IXMLBuilder;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.app.xml.IXMLBuilderContext;
import ru.ts.toykernel.plugins.IModule;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.List;

import org.xml.sax.InputSource;

/**
 * Загрузчик проекта
 *
 *
 */
public class ModulesLoader
{
	public static int countl=0;

	protected IXMLBuilderContext bcontext;
	protected String encoder;

	protected String rootname="projs";

	/**
	 * @return - загрузить и отдать тестовый модуль
	 * @throws Exception -
	 */
	public static List<IModule> getTestModules() throws Exception
	{
		//return new ModulesLoader().initModules("gener_r_thin02_g_mapcomp");
		return new ModulesLoader().initModules("gener_r_thin02_g");
//		return new ModulesLoader().initModules("psk");
	}

	public void setRoot(String rootname)
	{
		this.rootname=rootname;
	}

	public String getRoot()
	{
		return this.rootname;
	}


	public List<IModule> initModules(String projname) throws Exception
	{
		return initModules(projname, "WINDOWS-1251");
	}

	public List<IModule> initModules(String projname,String encoder) throws Exception
	{

		String path=rootname+"/"+projname+"/"+projname+".xml";
		return initModules(new FileInputStream(path), encoder);//TODO брать из описателя приложения
	}

	public List<IModule> initModules(InputStream resstream,String encoder) throws Exception
	{
		countl++;
		SAXParser parser= SAXParserFactory.newInstance().newSAXParser();
		Reader rd=new InputStreamReader(resstream,this.encoder=encoder);

		XMLProjBuilder builder = new XMLProjBuilder(true);//Строим только серверное приложение, остальное строительство отдаем на клиента
		parser.parse(new InputSource(rd), builder.getProjBuilderHandler(parser.getXMLReader()));
		bcontext = builder.getBuilderContext();

		IXMLBuilder<IModule> plugins = bcontext.getBuilderByTagName(KernelConst.PLUGIN_TAGNAME);
		plugins.initByDescriptors();
		return plugins.getLT();
	}
}
