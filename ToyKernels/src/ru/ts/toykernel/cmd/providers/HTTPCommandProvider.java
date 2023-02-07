package ru.ts.toykernel.cmd.providers;

import ru.ts.toykernel.plugins.*;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.factory.IFactory;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.util.Map;

import su.web.WebUtils;

/**
 * Обеспечивает передачу комманд на плагины
 * ru.ts.toykernel.cmd.providers.HTTPCommandProvider
 */
public class HTTPCommandProvider extends BaseInitAble implements IModule
{

	protected String providerBaseUrl;
	protected URL urlDivider;

	public HTTPCommandProvider(String providerBaseUrl) throws Exception
	{
		this.providerBaseUrl = providerBaseUrl;
		initUrl();
	}

	private void initUrl()
			throws MalformedURLException
	{
		urlDivider = new URL(providerBaseUrl +"/cmdprocessor.jsp");
	}


	public HTTPCommandProvider()
	{

	}

	public String getModuleName()
	{
		return "HTTPCommandProvider";
	}

	public IAnswerBean execute(ICommandBean cmd) throws Exception
	{
		InputStream is=null;
		try
		{


			HttpURLConnection urlConnection = WebUtils.getUrlConnection(urlDivider);
			urlConnection.setRequestProperty("Connection", "keep-alive");
			urlConnection.setRequestMethod("POST");

			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);

			OutputStream os = urlConnection.getOutputStream();

			os.write(("sess="+cmd.getSessionId()+"&cmd="+cmd.getCommand()).getBytes());

			Map<String,byte[]> mp2param=cmd.getParamMap();
			if (mp2param!=null)
			{
				for (String nms : mp2param.keySet())
				{
					os.write(("&"+nms+"=").getBytes());
					byte[] bpar = mp2param.get(nms);
					os.write(URLEncoder.encode(new String(bpar),"UTF8").getBytes());
				}
			}
			os.flush();

			DataInputStream dis = new DataInputStream(is = urlConnection.getInputStream());
			String rstatus = dis.readUTF();
			if (rstatus.equals("SUCCESS"))
			{
				byte[] buff=new byte[dis.readInt()];
				dis.readFully(buff);
				return new AnswerBean(cmd,"",buff);
			}
			return new AnswerBean(cmd,rstatus,rstatus.getBytes());
		}
		finally
		{
			if (is!=null)
				is.close();
		}

	}

	public void registerNameConverter(INameConverter nameConverter, IFactory<INameConverter> factory) throws Exception
	{
	}

	public void unload()
	{
	}


	public Object[] init(Object ... obj) throws Exception
	{
		super.init(obj);
		initUrl();
		return null;
	}

	public Object init(Object obj) throws Exception
	{
		IDefAttr attr=(IDefAttr)obj;
		if (attr.getName().equalsIgnoreCase("providerBaseUrl"))
			providerBaseUrl =(String)attr.getValue();
		return null;
	}
}