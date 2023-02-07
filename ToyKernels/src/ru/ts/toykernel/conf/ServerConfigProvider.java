package ru.ts.toykernel.conf;

import ru.ts.toykernel.proj.xml.def.XMLProjBuilder;
import ru.ts.toykernel.xml.IXMLBuilder;
import ru.ts.xml.IXMLObjectDesc;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.app.xml.IXMLBuilderContext;
import ru.ts.toykernel.gui.IApplication;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.servapp.SessionController;
import ru.ts.toykernel.servapp.ServProject;
import ru.ts.toykernel.servapp.IServProject;
import ru.ts.utils.data.Pair;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;

import org.xml.sax.InputSource;

/**
 * Серверный провайдер конфигурации, содержит информацию о всех доступных приложениях на сервере,
 * ответственен за генерацию серверных проектов
 *
 * Один объект этого класса обслуживает всех клиентов
 */
public class ServerConfigProvider extends BaseInitAble
{
	static protected SessionController sessioncontroller;

	public String getProjname()
	{
		return projname;
	}

	private String projname;

	static
	{
		if (sessioncontroller==null)
		{
			sessioncontroller =new SessionController(2*60*1000,500);
			new Thread(sessioncontroller).start();
		}
	}

	public ServerConfigProvider(String projname)
	{
		this.projname = projname;
	}

	protected IXMLBuilderContext bcontext;
	protected Map<String,Pair<Boolean, String>> servXMLs=new HashMap<String,Pair<Boolean, String>>();
	protected String encoder;
	protected List<String> apps=null;

	public void initProjects(InputStream resstream,String encoder) throws Exception
	{

		SAXParser parser= SAXParserFactory.newInstance().newSAXParser();
		Reader rd=new InputStreamReader(resstream,this.encoder=encoder);

		XMLProjBuilder builder = new XMLProjBuilder(true);//Строим только серверное приложение, остальное строительство отдаем на клиента
		parser.parse(new InputSource(rd), builder.getProjBuilderHandler(parser.getXMLReader()));
		bcontext = builder.getBuilderContext();

		IXMLBuilder servapps = bcontext.getBuilderByTagName(KernelConst.SERVAPP_TAGNAME);
		for (int i = 0; i < servapps.getParamDescs().size(); i++)
		{
			IXMLObjectDesc servdesc = (IXMLObjectDesc) servapps.getParamDescs().get(i);
			Pair<Boolean, String> value = bcontext.getFullXML(servdesc);
			value.first=true;//TODO подпорка теперь всегда будем отложеным вызовом пользоваться
			servXMLs.put(servdesc.getObjname(), value);
		}
	}


	public List<String> getServerApplications() throws Exception
	{
		synchronized (this)
		{
			if (apps==null)
			{
				apps=new LinkedList<String>();

				IXMLBuilder appbuilder=bcontext.getBuilderByTagName(KernelConst.SERVAPP_TAGNAME);
				List<IXMLObjectDesc> descs = appbuilder.getParamDescs();
				for (IXMLObjectDesc desc : descs)
					apps.add(desc.getObjname());

			}
		}
		return apps;
	}

	public List<String> getApplications() throws Exception
	{
		synchronized (this)
		{
			if (apps==null)
			{
				apps=new LinkedList<String>();

				IXMLBuilder appbuilder=bcontext.getBuilderByTagName(KernelConst.APPLICATION_TAGNAME);
				List<IXMLObjectDesc> descs = appbuilder.getParamDescs();
				for (IXMLObjectDesc desc : descs)
					apps.add(desc.getObjname());

			}
		}
		return apps;
	}

	/**
	 * Получить XML описатель приложения
	 * @param appInfo - имя приложения
	 * @return <флаг- цикл присутствует, XML описание переданного дескриптора, что бы его можно было собрать на клиенте>
	 * @throws Exception
	 */
	public Pair<Boolean,String> getDescriptorByAppInfo(String appInfo) throws Exception
	{
		IXMLBuilder<IApplication> appbuilder=bcontext.getBuilderByTagName(KernelConst.APPLICATION_TAGNAME);
		List<IXMLObjectDesc> descs = appbuilder.getParamDescs();
		IXMLObjectDesc iParamDescBean = null;
		for (IXMLObjectDesc desc : descs)
		{
			iParamDescBean = desc;
			if (iParamDescBean.getObjname().equalsIgnoreCase(appInfo))
				break;
		}
		return bcontext.getFullXML(iParamDescBean);
	}



	public String openSession(String appInfo) throws Exception
	{
		Pair<Boolean, String> b2xml = servXMLs.get(appInfo);
		if (b2xml==null)
			throw new Exception("Unknown application");

		SAXParser parser= SAXParserFactory.newInstance().newSAXParser();
		Reader rd=new InputStreamReader(new ByteArrayInputStream(b2xml.second.getBytes(this.encoder)),this.encoder);

		XMLProjBuilder builder = new XMLProjBuilder(b2xml.first);
		parser.parse(new InputSource(rd), builder.getProjBuilderHandler(parser.getXMLReader()));
		IXMLBuilderContext lbcontext = builder.getBuilderContext();
		IXMLBuilder<ServProject> servapps = lbcontext.getBuilderByTagName(KernelConst.SERVAPP_TAGNAME);
		if (b2xml.first)
		{
			servapps.clearInitAbles();
			servapps.initByDescriptors();
		}

		ServProject servapp = servapps.getLT().get(0);
		return sessioncontroller.putSession(servapp);
	}

	public  static IServProject getServBySession(String session_id) throws Exception
	{
		IServProject rv = sessioncontroller.getProjectBySessionId(session_id);
		if (rv==null)
			throw new Exception("Can't find server with this session");
		return rv;
	}


	public Object init(Object obj) throws Exception
	{
		return null;
	}
}
