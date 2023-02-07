package su.web;


import ru.ts.utils.logger.SimpleLogger;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletInputStream;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 23.06.2007
 * Time: 19:05:28
 *
 */
public class WebUtils
{

	public static Map<String, String[]> getParamsByString(String instr)
	{
		Map<String, String[]> rv = new HashMap<String, String[]>();
		if (instr != null && instr.length() > 0)
		{
			String[] respairs = instr.split("&");
			for (String respair : respairs)
			{
				int index = respair.indexOf("=");
				if (index > 0)
				{
					String arg = respair.substring(0, index);
					String param = respair.substring(index + 1);
					String[] al = rv.get(arg);
					if (al == null)
						rv.put(arg, new String[]{param});
					else
					{
						List<String> ll = new LinkedList<String>(Arrays.asList(al));
						ll.add(param);
						rv.put(arg, ll.toArray(new String[ll.size()]));
					}
				}
			}
		}
		return rv;
	}

	public static HttpURLConnection getUrlConnection(URL urlDivider) throws IOException
	{
		HttpURLConnection urlConnection = (HttpURLConnection) urlDivider.openConnection();
		urlConnection.setRequestProperty("Connection", "keep-alive");
		urlConnection.setRequestMethod("POST");
		
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		return urlConnection;
	}

	public static void reqdelay(long deltyme)
	{
		long currtime = System.currentTimeMillis();
//		int deltyme = 5 * 1000;
		do
		{
			try
			{
				Thread.sleep(300);
			}
			catch (InterruptedException e)
			{
			}
		}
		while (System.currentTimeMillis() - currtime < deltyme);
	}

	public static final String TMP_PATH = "tmp";
	public static final String TMP_F = TMP_PATH +"/F_";
	public static final String MIDLET_PATH = "midlets";



//	private static String   appName ="";
//
//	public static String getAppName()
//	{
//		return appName;
//	}
//
//	public static void setAppName(String appName)
//	{
//		WebUtils.appName = appName;
//	}

//	private static String   rootPath = "D:\\Vlad\\JavaProj\\NEW_MYMAP\\MapApp\\exploded\\";
//	public static void setRoot(String root)
//	{
//		rootPath=root;
//	}
//
//	public static String getRootPath()
//	{
//		return rootPath;
//	}

	private static HashMap hm = new HashMap();
	private static final String[] dirs = new String[]{"so", "gn", "gs", "no", "noo"};

	static
	{
		for (int i = 0; i < dirs.length; i++)
			hm.put(String.valueOf(i),dirs[i]);
	}

	public static HashMap getMapDirs()
	{
		return hm;
	}

	public static long copyData(InputStream is, OutputStream os)
			throws IOException
	{
		return copyData(is, os, -1);
	}

	public static long copyData(InputStream is, OutputStream os,int buffsz)
			throws IOException
	{
		return copyData(is, os, -1,buffsz);
	}

	public static long copyData(InputStream is, OutputStream os, long readsize,int buffsz)
			throws IOException
	{
		try
		{
			int redbt = 0;
			long wasread = 0;
			if (buffsz<=0)
				buffsz=1024 * 1024;
			byte[] buff = new byte[buffsz];
			while
					(
							(readsize < 0 || wasread < readsize)
							&&
							(redbt = is.read(buff, 0, (int) Math.min(((readsize >= 0) ? (readsize - wasread) : buff.length), buff.length))) > 0
					)
			{
				os.write(buff, 0, redbt);
				wasread += redbt;
			}

			if (readsize >= 0 && redbt < 0)
				throw new IOException("copyData function error ");
			else
				System.out.println("copyData size: " + (1.0 * wasread) / (1024 * 1024) + " M");
			return wasread;
		}
		finally
		{
			os.flush();
		}
	}


//	public static void copyStreams(InputStream is,OutputStream os,int buffsz) throws Exception
//	{
//		byte[] bt=new byte[buffsz];
//		int rdsz;
//		while ((rdsz=is.read(bt))>0)
//			os.write(bt,0,rdsz);
//
//	}

    private static int cnt=0;

    public synchronized static int getNextInt()
    {
        cnt++;
        return cnt;
    }

	public synchronized static String getRandomString()
	{
		return getNextInt()+"_tmpfile_"+getNextInt();
	}


	/**
	 * Динамическиое формирование файла в потоке
	  * @param response
	 * @param filename
	 * @param byteArray
	 * @throws IOException
	 */
    static public void sendZIPArchive(HttpServletResponse response, String filename,  byte[] byteArray) throws IOException
     {
         BufferedOutputStream buffstream;
         GZIPOutputStream zip_out;
         OutputStream out;
         String content="inline; filename=\""+filename+"\";";
         response.setHeader("Content-Disposition", content);
         response.setContentType("application/x-gzip");
         out=response.getOutputStream();
         zip_out=new GZIPOutputStream(out);
         buffstream=new BufferedOutputStream(zip_out);
         buffstream.write(byteArray);
         buffstream.flush();
//         buffstream.close();
     }

	/**
	 * Динамическиое формирование файла в потоке
	  * @param response
	 * @param filename
	 * @param is
	 * @throws IOException
	 */
    static public void sendZIPArchive(HttpServletResponse response, String filename,InputStream is) throws IOException
     {
         BufferedOutputStream buffstream;
         GZIPOutputStream zip_out;
         OutputStream out;
         String content="inline; filename=\""+filename+"\";";
         response.setHeader("Content-Disposition", content);
         response.setContentType("application/x-gzip");
         out=response.getOutputStream();
         zip_out=new GZIPOutputStream(out);
         buffstream=new BufferedOutputStream(zip_out);
		 byte[] byteArray=new byte[10*1024];
		 int ln=0;
		 while ((ln=is.read(byteArray))>0)
			 buffstream.write(byteArray,0,ln);
         buffstream.flush();
//         buffstream.close();
     }

	/**
	 * Динамическиое формирование файла в потоке
	  * @param response
	 * @param filename
	 * @param respout
	 * @param is
	 * @throws IOException
	 */
    static public void sendZIPArchive(HttpServletResponse response, String filename, OutputStream respout,InputStream is) throws IOException
     {
         BufferedOutputStream buffstream;
         String content="inline; filename=\""+filename+"\";";
         response.setHeader("Content-Disposition", content);
         response.setContentType("application/x-gzip");

         GZIPOutputStream zip_out=new GZIPOutputStream(respout);
         buffstream=new BufferedOutputStream(zip_out);
		 byte[] byteArray=new byte[10*1024];
		 int ln=0;
		 while ((ln=is.read(byteArray))>=0)
			 buffstream.write(byteArray,0,ln);
         buffstream.flush();
//         buffstream.close();
     }

	public static void putFileToResponse(String mimetype, String name, HttpServletResponse response, OutputStream out,InputStream is) throws IOException
	{
		try
		{
			String content = "inline; filename=\"" + name + "\";";
			response.setHeader("Content-Disposition", content);
			response.setContentType(mimetype);
			BufferedOutputStream buffstream = new BufferedOutputStream(out);

			byte[] byteArray=new byte[10*1024];
			int ln=0;
			while ((ln=is.read(byteArray))>=0)
				buffstream.write(byteArray,0,ln);
			buffstream.flush();
//			buffstream.close();
		}
		finally
		{
			if (is!=null)
				is.close();
		}
	}

	public static void putFileToResponse(String mimetype, String name, HttpServletResponse response, InputStream is) throws IOException
	{
		try
		{
			String content = "inline; filename=\"" + name + "\";";
			response.setHeader("Content-Disposition", content);
			response.setContentType(mimetype);
			OutputStream out = response.getOutputStream();
			BufferedOutputStream buffstream = new BufferedOutputStream(out);

			byte[] byteArray=new byte[10*1024];
			int ln=0;
			while ((ln=is.read(byteArray))>=0)
				buffstream.write(byteArray,0,ln);
			buffstream.flush();
		}
		finally
		{
			if (is!=null)
				is.close();
		}
	}

	public static void putFileToResponse(String mimetype, String name, HttpServletResponse response, byte[] byteArray) throws IOException {
        String content = "inline; filename=\"" + name + "\";";
        response.setHeader("Content-Disposition", content);
        response.setContentType(mimetype);
        OutputStream out = response.getOutputStream();
        BufferedOutputStream buffstream = new BufferedOutputStream(out);
        buffstream.write(byteArray);
        buffstream.flush();
//        buffstream.close();
//		out.close();
	}

    /**
     * Читает файл с диска как байтового массива
     *
     * @param s - итмя файла
     * @return byte[]
     * @throws java.io.IOException
     */
    public static byte[] loadFile(String s) throws IOException
    {
        FileInputStream fis = new FileInputStream(s);
        byte[] abyte = new byte[fis.available()];
        fis.read(abyte);
        fis.close();
        return abyte;
    }


	public static String getByteFRomHTTPReques(ServletInputStream is,String boundary,int ln,OutputStream os,byte[] buff) throws Exception
	{
		String filename=null;
		int totalread = 0;

		int cnttrem=-1;

			if (ln > 0)
			{
				String mimetype = "";
				try
				{
					while (true)
					{
						int wred = is.readLine(buff, 0, buff.length);
						if (wred >= 0)
						{
							totalread += wred;

							String sred = new String(buff, 0, wred);
							if (wred<=2 && sred.indexOf('\n')>=0 && ((wred==2 && sred.indexOf('\r')>=0) || wred==1))
								cnttrem=wred;

							int ict = -1;
							if ((ict = sred.indexOf("Content-Type: ")) >= 0)
							{
								mimetype = sred.substring(ict + "Content-Type: ".length());
								totalread += is.readLine(buff, 0, buff.length);
								break;
							}
							else
							{
	//							"Content-Disposition: form-data; name=\"userfile\"; filename=\"C:\\MAPDIR\\LAYERS_NEW_NEW\\all_rzd_bind33_54.lr.gz\"";
								int stname=0;
								if (sred.indexOf("Content-Disposition: ")>=0 && sred.indexOf("name=\"userfile\"")>0 && (stname=sred.indexOf(" filename="))>0)
								{
								   filename=sred.substring(stname+" filename=".length());
									int index=-1;
									do
									{
										index=filename.indexOf("\\");
									  	if (index<0)
										  	index=filename.indexOf("/");
										if (index>=0)
											filename=filename.substring(index+1);
									} while (index>=0);

									index=filename.indexOf("\"");
									if (index>0)
										filename=filename.substring(0,index);
								}
							}
						}
						else
							break;
					}
				}
				catch (IOException e)
				{

				}


				if (boundary != null && mimetype.length() != 0)
				{
							int wred;
							if (cnttrem<0)
								cnttrem=2;
							SimpleLogger.Singleton.getLoger().getLog().println("cnttrem = " + cnttrem);
							while ((wred = is.read(buff, 0, Math.min(buff.length,
									ln - totalread - boundary.length() - cnttrem - cnttrem - cnttrem - cnttrem))) > 0 && totalread < ln - boundary.length() - cnttrem - cnttrem - cnttrem - cnttrem)
							{
								os.write(buff, 0, wred);
								totalread += wred;
							}
				}
			}
			return  filename; 
	}

	public static String getTmpFile(String root)
	{
		String pic_file = TMP_F + WebUtils.getRandomString();
		return (root!=null && root.length()>0)?(root+File.separatorChar+pic_file):pic_file;
	}

}
