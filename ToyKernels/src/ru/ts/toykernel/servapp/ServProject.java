package ru.ts.toykernel.servapp;

import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.pcntxt.IMetaInfoBean;
import ru.ts.toykernel.plugins.IModule;
import ru.ts.toykernel.plugins.IServModule;
import ru.ts.factory.IParam;

import java.util.*;


/**
 * Серверный проект включает в себя модули
 *
 */
public class ServProject extends BaseInitAble implements IServProject
{

	protected IMetaInfoBean metainfo;
	protected Map<String, List<IModule>> cmds2Module = new HashMap<String, List<IModule>>();//Отображение комманд в модули клиента



	public ServProject()
	{
	}

	public IMetaInfoBean getMetainfo()
	{
		return metainfo;
	}

	public IModule getFirstPlugInByClass(Class cl) throws Exception
	{
		List<IModule> rv= new LinkedList<IModule>();
		for (List<IModule> iModules : cmds2Module.values())
		{
			for (IModule iModule : iModules)
				if (cl.isAssignableFrom(iModule.getClass()))
					return iModule;
		}
		return null;
	}

	public List<IModule> getPlugInsByClass(Class cl) throws Exception
	{
		List<IModule> rv= new LinkedList<IModule>();
		for (List<IModule> iModules : cmds2Module.values())
			for (IModule iModule : iModules)
				if (cl.isAssignableFrom(iModule.getClass()))
					rv.add(iModule);
		return rv;
	}

	public Map<String, List<IModule>> getPlugIns() throws Exception
	{
		return cmds2Module;
	}

	public Object init(Object obj) throws Exception
	{
		IParam attr=(IParam)obj;
		if (attr.getName().equalsIgnoreCase(KernelConst.META_TAGNAME))
			this.metainfo = (IMetaInfoBean) attr.getValue();
		else if (attr.getName().equalsIgnoreCase(KernelConst.PLUGIN_TAGNAME))
		{
			IServModule module = (IServModule) attr.getValue();
			String scmds = module.getCommands();
			String[] arcmds = scmds.split("#");
			for (String cmd : arcmds)
				if (cmd != null && cmd.length() > 0)
				{
					if (!cmds2Module.containsKey(cmd))
					{
						List<IModule> modules=new LinkedList<IModule>();
						cmds2Module.put(cmd, modules);
						modules.add(module);
					}
					else
						cmds2Module.get(cmd).add(module);

//						throw new Exception("Conflict init modules commands:" + cmd + " was module " + cmds2Module.get(cmd).getModuleName() + " set module name:" + module.getModuleName());
				}
		}
		return null;
	}


}
