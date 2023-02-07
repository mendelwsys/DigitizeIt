package ru.ts.csvreader;

import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 25.09.2008
 * Time: 12:02:21
 * To change this template use File | Settings | File Templates.
 */
public class ReaderUtils
{
	static public Map<String, List<String>> readNCI(String args[], String del, List<String> headers,
													String charsetName) throws Exception
	{
		BufferedReader br = null;
		String filename = args[0];
		br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), charsetName));

		return readNCI(del, headers, br);
	}

	public static Map<String, List<String>> readNCI(String del, List<String> headers, BufferedReader br)
			throws IOException
	{
		String bln = null;
		{
			bln = br.readLine();//HEADERS
			{
				StringTokenizer st = new StringTokenizer(bln, del);
				try
				{
					while (st.hasMoreTokens())
						headers.add(st.nextToken().trim());
				}
				catch (NoSuchElementException e)
				{//
				}
			}
		}


		br.readLine();//MOS
		br.readLine();//ЛЕН

		//Формат Название столбца ->Столбец
		Map<String, List<String>> mp = new HashMap<String, List<String>>();

		readTuples(del, headers, br, mp);

		Set<String> stringSet = new HashSet<String>(mp.keySet());
		for (String s : stringSet)
			mp.put(s, new ArrayList<String>(mp.get(s)));
		return mp;
	}

	public static void readTuples(String del, List<String> headers, BufferedReader br, Map<String, List<String>> mp)
			throws IOException
	{
		String bln;
		while ((bln = br.readLine()) != null)
		{
			if (bln.length() == 0)
				continue;
			StringTokenizer st = new StringTokenizer(bln, del, true);
			try
			{
                for (int i = 0; i < headers.size(); i++)
                {
//                    if (i==headers.size()-1)
//                        System.out.println("Test");
                    String header = headers.get(i);

                    String token = "";
                    try
                    {
                        token = st.nextToken();
                    }
                    catch (NoSuchElementException e)
                    {//

                    }

                    boolean isdel = token.equals(del);
                    if (isdel)
                        token = "";

                    List<String> col = mp.get(header);
                    if (col == null) {
                        col = new LinkedList<String>();
                        mp.put(header, col);
                    }
                    col.add(token);

                    if (!isdel)
                        st.nextToken();
                }
			}
			catch (NoSuchElementException e)
			{//
			}
		}
	}


	public static void readTuples2(String del, BufferedReader br, List<List<String>> tuples)
			throws IOException
	{
		String bln;
		while ((bln = br.readLine()) != null)
		{
			if (bln.length() == 0)
				continue;
			List<String> tuple=new LinkedList<String>();
			readTuple(bln,del,tuple);
			tuples.add(new ArrayList<String>(tuple));
		}
	}

	public static void readTuple(String bln, String del, List<String> tuple)
			throws IOException
	{
		if (bln.length() == 0)
			return;
		StringTokenizer st = new StringTokenizer(bln, del, true);
		try
		{
			for (; ;)
			{
				String token = st.nextToken();
				if (token.equals(del))
				{
					token = "";
					tuple.add(token);
				}
				else
				{
					tuple.add(token);
					st.nextToken();
				}

			}
		}
		catch (NoSuchElementException e)
		{//

		}
	}

}
