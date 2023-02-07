package ru.ts.toykernel.plugins.defindrivers;

import ru.ts.toykernel.plugins.*;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.proj.ICliConfigProvider;
import ru.ts.factory.IFactory;
import ru.ts.factory.IParam;

import javax.swing.*;
import java.awt.*;

/**
 * Модуль поддержания сессии
 * ru.ts.toykernel.plugins.defindrivers.HeartBeat
 */
public class HeartBeat extends BaseInitAble implements IGuiModule
{

	protected ICliConfigProvider conf_provider;
	protected IModule cmdprovider;

	protected boolean isterminte = false;
	protected long timeout=10000;


	protected Thread heartBeatThread = new Thread()
	{
		public void run()
		{
			try
			{
				while (!isterminte)
					try
					{
						long tm= System.currentTimeMillis();
						execute(new CommandBean("HB", null, ICommandBean.APPCLI, ""));
						long timespan = timeout - (System.currentTimeMillis() - tm);
						sleep(timespan <=0?50:timespan);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
			}
			finally
			{
				isterminte = true;
			}
		}
	};

	public HeartBeat(ICliConfigProvider conf_provider, IModule cmdprovider) throws Exception
	{
		this.conf_provider = conf_provider;
		this.cmdprovider = cmdprovider;
		heartBeatThread.start();
	}

	public HeartBeat()
	{

	}

	public String getModuleName()
	{
		return "HeartBeat";
	}

	public IAnswerBean execute(ICommandBean cmd) throws Exception
	{
		String session = conf_provider.getSession();
		if (session!=null)
		{
			cmd.setSessionId(session);
			return cmdprovider.execute(cmd);
		}
		return new AnswerBean(cmd,"",new byte[]{});
	}


	public void registerNameConverter(INameConverter nameConverter, IFactory<INameConverter> factory) throws Exception
	{
	}

	public void unload()
	{
		isterminte=true;
	}


	public Object[] init(Object... obj) throws Exception
	{
		super.init(obj);
		heartBeatThread.start();
		return null;
	}

	public Object init(Object obj) throws Exception
	{
		IParam attr=(IParam)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.CMDPROVIDER_TAGNAME))
			cmdprovider = (IModule) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(KernelConst.CONFPROVIDERS_TAGNAME))
			conf_provider = (ICliConfigProvider) attr.getValue();
		else if (attr.getName().equalsIgnoreCase("timeout"))
		{
			try
			{
				timeout= Integer.parseInt((String)attr.getValue());
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getMenuName()
	{
		return null;
	}

	public JMenu addMenu(JMenu inmenu) throws Exception
	{
		return inmenu;
	}

	public JPopupMenu addPopUpMenu(JPopupMenu inmenu) throws Exception
	{
		return inmenu;
	}

	public void registerListeners(JComponent component) throws Exception
	{
	}

	public JToolBar addInToolBar(JToolBar systemtoolbar) throws Exception
	{
		return systemtoolbar;
	}

	public void paintMe(Graphics g) throws Exception
	{
	}
}
