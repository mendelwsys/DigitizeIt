package ru.ts.toykernel.storages.raster;

import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.utils.ParserUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 04.01.14
 * Time: 21:44
 *
 */
public class BindStruct
	{
		public String pathdesc;//путь к описателю
		public MPoint[] lpnt = new MPoint[2];//Точки синхронизации проекта
		public MPoint[] rpnt = new MPoint[2];//Точки растра
		public double[] scaleRange = new double[]{-1, -1};//Диапазаон масштабов

		public String[][] flnames = new String[0][0]; //имена файлов (размер по Y размер по X)
		public MPoint totalsize = new MPoint();//Размер растрового поля
		public MPoint picsize = new MPoint(-1, -1);//Рамзер картинки
		public String pictdir;//путь к картинкам

		public BindStruct()
		{
		}

		public BindStruct(String pathdesc, MPoint[] rpnt, MPoint[] lpnt, double[] scaleRange)
		{
			this.pathdesc = pathdesc;
			this.rpnt = rpnt;
			this.lpnt = lpnt;
			this.scaleRange = scaleRange;
		}

		public void loadDesc()
				throws Exception
		{
			try
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathdesc)));
				loadDesc(in);
			} catch (Exception e)
			{
				throw new Exception("Can't load description of raster by path " + pathdesc, e);
			}
		}
		protected void loadDesc(BufferedReader in)
				throws Exception
		{
			in.readLine();

			String readedstr = in.readLine().trim();
			{
				String lexem = ParserUtils.getLexem(readedstr);
				int szy = Integer.parseInt(lexem);
				readedstr = ParserUtils.getNextSubstring(lexem, readedstr);
				lexem = ParserUtils.getLexem(readedstr).trim();
				int szx = Integer.parseInt(lexem);
				flnames = new String[szy][szx];
			}

			readedstr = in.readLine().trim();
			{
				String lexem = ParserUtils.getLexem(readedstr);
				int szy = Integer.parseInt(lexem);
				readedstr = ParserUtils.getNextSubstring(lexem, readedstr);
				lexem = ParserUtils.getLexem(readedstr).trim();
				int szx = Integer.parseInt(lexem);
				totalsize = new MPoint(szx, szy);
			}

			for (int i = 0; i < flnames.length; i++)
			{
				String[] flname = flnames[i];
				readedstr = in.readLine().trim();
				for (int j = 0; j < flname.length; j++)
				{
					String lexem = ParserUtils.getLexem(readedstr);
					if (!lexem.equals("nul") && !lexem.equals("null"))
						flname[j] = lexem;
					readedstr = ParserUtils.getNextSubstring(lexem, readedstr).trim();

					if (i == 0 && j == 0)
						picsize = new MPoint(totalsize.x / flnames[0].length, totalsize.y / flnames.length);
				}
			}

			in.readLine();
			in.readLine();
			pictdir = in.readLine();
		}
	}