package ru.ts.utils.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 20.08.2008
 * Time: 11:16:26
 */
public class SimpleLogger
{
	private PrintWriter log;

	public String getPath()
	{
		return path;
	}

	private String path;

	public PrintWriter getLog()
	{
		return log;
	}

	public SimpleLogger()
	{
		log = new PrintWriter(System.out, true);

	}

	public SimpleLogger(String path) throws Exception
	{
		this.path = path;
		initpath();
	}

	private void initpath() throws Exception
	{
		File f = new File(path);

		if (f.exists() && f.length() > 1000000)
			f.renameTo(new File(path + System.currentTimeMillis()));

		FileOutputStream fos = new FileOutputStream(path, true);
		log = new PrintWriter(fos, true);
	}

	public void closelog() throws Exception
	{
		if (log != null)
		{
			log.flush();
			log.close();
		}

	}

	public void redirect(String newpath) throws Exception
	{
		closelog();
		this.path = newpath;
		initpath();
	}

	public void redirect() throws Exception
	{
		redirect(this.path);
	}

	public static class Singleton
	{
		static SimpleLogger logger;

		public static SimpleLogger reinitLoger(String path) throws Exception
		{
			if (logger != null)
				logger.closelog();
			return logger = new SimpleLogger(path);
		}

		public static SimpleLogger initLoger(String path) throws Exception
		{
			if (logger == null)
				logger = new SimpleLogger(path);
			return logger;
		}

		public static SimpleLogger getLoger()
		{
			if (logger == null)
				logger = new SimpleLogger();
			return logger;
		}
	}
}
