package ru.ts.toykernel.servapp;

import ru.ts.utils.data.Pair;

import java.util.*;

/**
 * Сессионный контроллер
 */
public class SessionController
		implements Runnable
{


	private long sesstimeout;
	private long sleeptime;
	private boolean terminatethread=false;

	public SessionController(long sesstimeout,long sleeptime)
	{
		this.sesstimeout = sesstimeout;
		this.sleeptime = sleeptime;
	}

	public long getSesstimeout()
	{
		return sesstimeout;
	}


	public void setSesstimeout(long sesstimeout)
	{
		this.sesstimeout = sesstimeout;
	}

	public void setTerminatethread()
	{
		this.terminatethread = true;
	}

	public void run()
	{
		while (!terminatethread)
		{
			Set<String> sessions;
			synchronized (this)
			{
				sessions=new HashSet<String>(sessionProjects.keySet());
			}
			Pair<IServProject,SessionCtrl> sess;
			for (String sessionId : sessions)
			{
				synchronized (this)
				{
					sess=sessionProjects.get(sessionId);
				}
				if (sess!=null)
				{//Анализируем и если необходимо удаляем объект
					if (System.currentTimeMillis()-sess.second.lastcall >sesstimeout)
						removeSession(sessionId);
				}
				if (terminatethread)
					return;
			}

			try
			{
				Thread.sleep(sleeptime);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

	}

	public class SessionCtrl
	{
		public long tmstart;
		public long lastcall;

		public SessionCtrl(long tmstart)
		{
			this.tmstart = tmstart;
			this.lastcall = tmstart;
		}
	}

	protected Map<String, Pair<IServProject,SessionCtrl>> sessionProjects=new HashMap<String,Pair<IServProject,SessionCtrl>>();


	protected long sesscount=0;
	protected Random random = new Random();
	protected synchronized long getSessCount()
	{
		return sesscount+=(random.nextInt())%7;
	}

	protected String getNewSessionId(int cnt)
	{
		StringBuffer buffer=new StringBuffer();
		for (int i=0;i<cnt;i++)
			buffer.append(random.nextInt()%13);
		buffer.append(getSessCount());
		return buffer.toString();
	}

	public synchronized void  removeSession(String sessionId)
	{
		Pair<IServProject,SessionCtrl> sess=sessionProjects.remove(sessionId);
		//Возможно что-то сделать с IServProject для удаления объектов
	}

	public synchronized String putSession(IServProject project)
	{
		String sessionId;
		do
		{
			sessionId=getNewSessionId(4);
		} 
		while (sessionProjects.containsKey(sessionId));

		System.out.println("sesscount = " + sessionProjects.size());


		sessionProjects.put(sessionId,new Pair<IServProject,SessionCtrl>(project,new SessionCtrl(System.currentTimeMillis())));
		return sessionId;
	}

	public synchronized IServProject getProjectBySessionId(String sessionId)
	{
		Pair<IServProject, SessionCtrl> pr = sessionProjects.get(sessionId);
		if (pr!=null)
		{
			pr.second.lastcall= System.currentTimeMillis();
			return pr.first;
		}
		return null;
	}
}
