package ru.ts.toykernel.plugins;

import org.apache.xerces.utils.Base64;

import java.util.Map;
import java.io.UnsupportedEncodingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * Серверная команда которая приходит от клиента
 */
public class CommandBean implements ICommandBean
{
	private String command;
	private Map parmap;
	private int actCode;
	private String sessionid;


	public CommandBean(String command, Map parmap, int actCode, String sessionid)
	{
		this.command = command;
		this.parmap = parmap;
		this.actCode = actCode;
		this.sessionid = sessionid;
	}


	public Map getParamMap()
	{
		return parmap;
	}

	public void setSessionId(String sessionid)
	{
		this.sessionid=sessionid;
	}

	public String getSessionId()
	{
		return sessionid;
	}

	public  void setCommand(String command)
	{
		this.command=command;
	}

	public String getCommand()
	{
		return command;
	}

	public String getParamByName(String paramname)
	{
		if (parmap!=null)
		{
			String[] parvals = (String[]) parmap.get(paramname);
			if (parvals!=null)
				return  parvals[0];
		}
		return null;
	}

	public byte[] getbinParams(String paramname)
	{
			String rv=getParamByName(paramname);
			if (rv!=null)
			{
//				try
//				{
//					FileOutputStream fos = new FileOutputStream("C:/serv.tmp");
//					fos.write(rv.getBytes());
//					fos.close();
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
				return Base64.decode(rv.getBytes());
			}
		return null;
	}

	public int getCodeActivator()
	{
		return actCode;
	}
}
