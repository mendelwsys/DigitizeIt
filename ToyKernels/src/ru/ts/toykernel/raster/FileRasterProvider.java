package ru.ts.toykernel.raster;

import ru.ts.stream.ISerializer;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.toykernel.converters.IProjConverter;
import ru.ts.toykernel.converters.IScaledConverterCtrl;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.utils.data.Pair;
import ru.ts.utils.ParserUtils;
import ru.ts.xml.HandlerEx;

import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.LinkedList;
import java.io.*;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import javax.imageio.ImageIO;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * Файловый провайдер растров, обеспечивает загрузку из файлов
 * серий растров их привязку и сохранение привязки растров
 */
public class FileRasterProvider implements IRasterProvider, ISerializer
{

	//Структура привзяки растра
	static class BindStruct
	{
		String pathdesc;//путь к описателю
		MPoint[] lpnt = new MPoint[2];//Линейные точки проекта
		MPoint[] rpnt = new MPoint[2];//Точки растра
		double[] scaleRange = new double[]{-1, -1};//Диапазаон масштабов

		String[][] flnames=new String[0][0]; //имена файлов
		MPoint totalsize= new MPoint();//Размер растрового поля
		MPoint picsize= new MPoint(-1,-1);//Рамзер картинки
		String pictdir;//путь к картинкам

		void loadDesc(BufferedReader in)
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

					if (i==0 && j == 0)
						picsize = new MPoint(totalsize.x / flnames[0].length, totalsize.y / flnames.length);
				}
			}

			in.readLine();
			in.readLine();
			pictdir=in.readLine();
		}
	}

	protected List<BindStruct> bstr_list = new LinkedList<BindStruct>();
	protected  int currentStruct=-1;

	public class CurrentHandler extends HandlerEx
	{

		public CurrentHandler(XMLReader reader)
		{
			super(reader);
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			if (qName.startsWith("desc"))
			{
				BindStruct bstr = new BindStruct();
				int index = attributes.getIndex("path");
				if (index < 0)
					throw new SAXException("Can't find description of path");
				bstr.pathdesc = attributes.getValue(index);

				index = attributes.getIndex("scale");
				if (index >= 0)
				{
					String scaleString = attributes.getValue(index);
					String[] splited = scaleString.split(" ");
					try
					{
						bstr.scaleRange[0] = Double.parseDouble(splited[0]);
						bstr.scaleRange[1] = Double.parseDouble(splited[1]);
					}
					catch (NumberFormatException e)
					{
						throw new SAXException("Error parsing of scale", e);
					}
				}
				parseBindsPoint(attributes, bstr,0);
				parseBindsPoint(attributes, bstr,1);

				bstr_list.add(bstr);
			}
		}

		private void parseBindsPoint(Attributes attributes, BindStruct bstr,int ipnt)
				throws SAXException
		{
			int index = attributes.getIndex("pr"+ipnt);
			if (index < 0)
				throw new SAXException("Can't find description pr0");
			String pointsBind = attributes.getValue(index);
			String[] splited = pointsBind.split(" ");
			try
			{
				bstr.lpnt[ipnt] = new MPoint(Double.parseDouble(splited[0]), Double.parseDouble(splited[1]));
				bstr.rpnt[ipnt] = new MPoint(Double.parseDouble(splited[2]), Double.parseDouble(splited[3]));
			}
			catch (NumberFormatException e)
			{
				throw new SAXException("Error prasing of pr"+ipnt, e);
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			if (qName.startsWith("rlayer"))
			{
				for (BindStruct bindStruct : bstr_list)
				{
					try
					{
						BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(bindStruct.pathdesc)));
						bindStruct.loadDesc(in);
					} catch (Exception e)
					{
						throw new SAXException("Can't load description of raster by path "+bindStruct.pathdesc,e);
					}
				}
				ret2callerHadnler();
			}
		}
	}

	public MPoint getDrawPointByRasterPoint(MPoint point, ILinearConverter converter) throws Exception
	{
		MPoint[] mp12= new MPoint[2];
		MPoint[] pt12= new MPoint[2];
		fillParamByBindStruct(currentStruct, mp12, pt12, null, null, null);

		//Точки рисования (в экранных координатах)
		Point2D drwp1 = null; //TODO converter.getDrawPointByLinearPointD(mp12[0]);
		Point2D drwp2 = null; //TODO converter.getDrawPointByLinearPointD(mp12[1]);

		//Масштаб растр/(координатам изображения)
		MPoint scale = new MPoint((pt12[0].x - pt12[1].x) / (drwp1.getX() - drwp2.getX()),
				(pt12[0].y - pt12[1].y) / (drwp1.getY() - drwp2.getY()));

		//Точка растра pt12 соответствует точке рисования drwp1
		double ddrwX = (point.x - pt12[0].x) / scale.x;
		double ddrwY = (point.y - pt12[0].y) / scale.y;

		return new MPoint(drwp1.getX() + ddrwX,drwp1.getY() + ddrwY);//точка рисования соответсвующая переданной растровой точке
	}

	public MPoint getRasterPointByDrawPoint(MPoint point, ILinearConverter converter
	) throws Exception
	{

		MPoint[] mp12= new MPoint[2];
		MPoint[] pt12= new MPoint[2];
		fillParamByBindStruct(currentStruct, mp12, pt12, null, null, null);

		Point2D drwp1  = null; //TODO converter.getDrawPointByLinearPointD(mp12[0]);
		Point2D drwp2  = null; //TODO  converter.getDrawPointByLinearPointD(mp12[1]);
		MPoint scale = new MPoint((pt12[0].x - pt12[1].x) / (drwp1.getX() - drwp2.getX()),
				(pt12[0].y - pt12[1].y) / (drwp1.getY() - drwp2.getY()));//Масштаб длинна на растре/Длинна на панели в координатах рисования
		return new MPoint((pt12[0].x + (point.getX() - drwp1.getX()) * scale.x),
				(pt12[0].y + (point.getY() - drwp1.getY()) * scale.y));
	}

	public int[] getImagesIndices(ILinearConverter converter, int[] drwWindos,double ddXddY[]) throws Exception
	{
		double[] dXdY=new double[2];
		fillParamByBindStruct(currentStruct, null,null, dXdY, null, null, converter);

		MPoint rp0=getRasterPointByDrawPoint(new MPoint(),converter);
		MPoint rp1=getRasterPointByDrawPoint(new MPoint(drwWindos[0],drwWindos[1]),converter);

		int[] rv=new int[]{0,0,0,0};

		rv[0]=(int)Math.floor(rp0.x/dXdY[0]);
		rv[1]=(int)Math.floor(rp0.y/dXdY[1]);

		rv[2]=(int)Math.ceil(rp1.x/dXdY[0]);
		rv[3]=(int)Math.ceil(rp1.y/dXdY[1]);

		return rv;
	}

	public String getUrlImageRequest(int[] nXnY) throws Exception
	{

		if (currentStruct<0 || currentStruct>bstr_list.size())
			throw new Exception("Raster images not set or set incorrectly");

		BindStruct bstr=bstr_list.get(currentStruct);

		String fname=bstr.flnames[nXnY[1]][nXnY[0]];

		if (fname!=null)
			return bstr.pictdir+"/"+fname;
		else
			return null;
	}

	public Pair<BufferedImage, String> getImageRequest(int[] nXnY) throws Exception
	{

		if (currentStruct<0 || currentStruct>bstr_list.size())
			throw new Exception("Raster images not set or set incorrectly");

		BindStruct bstr=bstr_list.get(currentStruct);

		String fname=bstr.flnames[nXnY[1]][nXnY[0]];

		Pair<BufferedImage, String> rv= new Pair<BufferedImage, String>(null,fname);
		if (fname!=null)
			rv.first=ImageIO.read(new File(bstr.pictdir+"/"+fname));
		return rv;
	}

	public void getRasterParamters(ILinearConverter converter, MPoint[] mp12, MPoint[] pt12, double[] dXdY, double[] szXszY, int[] nXnY) throws Exception
	{
		IScaledConverterCtrl converter_p=null;
		if (converter instanceof IScaledConverterCtrl)
			converter_p=(IScaledConverterCtrl)converter;
		else if (converter instanceof IProjConverter)
			converter_p=((IProjConverter)converter_p).getAsScaledConverterCtrl();
		else
			throw new UnsupportedOperationException("Unknown converter: "+converter.getClass().getCanonicalName());

		double curentscale=Math.max(1.0/converter_p.getUnitsOnPixel().x,1.0/converter_p.getUnitsOnPixel().y);
		for (currentStruct = 0; currentStruct < bstr_list.size(); currentStruct++)
		{
			BindStruct bindStruct = bstr_list.get(currentStruct);
			if (bindStruct.scaleRange[0] <= curentscale && (curentscale <= bindStruct.scaleRange[1] || bindStruct.scaleRange[1] < 0))
			{
				fillParamByBindStruct(currentStruct,mp12, pt12, dXdY, szXszY, nXnY, converter);
				return;
			}
		}
		currentStruct=-1;
		throw new Exception("Can't find appropriate scale range for raster");
	}

	private void fillParamByBindStruct(int currentStruct, MPoint[] mp12, MPoint[] pt12, double[] dXdY, double[] szXszY, int[] nXnY, ILinearConverter converter) throws Exception
	{
		if (currentStruct<0 || currentStruct>bstr_list.size())
			throw new Exception("Raster images not set or set incorrectly");

		BindStruct bindStruct = bstr_list.get(currentStruct);

		if (mp12!=null)
		{
			mp12[0] = new MPoint(bindStruct.lpnt[0].getX(), bindStruct.lpnt[0].getY());
			mp12[1] = new MPoint(bindStruct.lpnt[1].getX(), bindStruct.lpnt[1].getY());
		}
		if (pt12!=null)
		{
			pt12[0] = new MPoint(bindStruct.rpnt[0].getX(), bindStruct.rpnt[0].getY());
			pt12[1] = new MPoint(bindStruct.rpnt[1].getX(), bindStruct.rpnt[1].getY());
		}


		if (dXdY!=null)
		{
			MPoint szP=getDrawPointByRasterPoint(bindStruct.picsize,converter);
			dXdY[0]=szP.x;
			dXdY[1]=szP.y;
		}

		if (szXszY!=null)
		{
			MPoint szP=getDrawPointByRasterPoint(bindStruct.totalsize,converter);
			szXszY[0] = szP.x;
			szXszY[1] = szP.y;
		}

		if (nXnY!=null)
		{
			nXnY[1]=bindStruct.flnames.length;
			if (nXnY[1]>0)
				nXnY[0]=bindStruct.flnames[0].length;
		}
	}

	private void fillParamByBindStruct(int currentStruct, MPoint[] mp12, MPoint[] pt12, double[] dXdY, double[] szXszY, int[] nXnY) throws Exception
	{
		if (currentStruct<0 || currentStruct>bstr_list.size())
			throw new Exception("Raster images not set or set incorrectly");

		BindStruct bindStruct = bstr_list.get(currentStruct);

		if (mp12!=null)
		{
			mp12[0] = new MPoint(bindStruct.lpnt[0].getX(), bindStruct.lpnt[0].getY());
			mp12[1] = new MPoint(bindStruct.lpnt[1].getX(), bindStruct.lpnt[1].getY());
		}
		if (pt12!=null)
		{
			pt12[0] = new MPoint(bindStruct.rpnt[0].getX(), bindStruct.rpnt[0].getY());
			pt12[1] = new MPoint(bindStruct.rpnt[1].getX(), bindStruct.rpnt[1].getY());
		}


		if (dXdY!=null)
		{
			dXdY[0]=bindStruct.picsize.x;
			dXdY[1]=bindStruct.picsize.y;
		}

		if (szXszY!=null)
		{
			szXszY[0] = bindStruct.totalsize.x;
			szXszY[1] = bindStruct.totalsize.y;
		}

		if (nXnY!=null)
		{
			nXnY[1]=bindStruct.flnames.length;
			if (nXnY[1]>0)
				nXnY[0]=bindStruct.flnames[0].length;
		}
	}

	public void bindRasterProvider(ILinearConverter converter, MPoint[] mp12, MPoint[] pt12)
	{
		throw new UnsupportedOperationException();
	}

	public void saveToStream(DataOutputStream dos) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	public void loadFromStream(DataInputStream dis) throws Exception
	{
		SAXParser parser= SAXParserFactory.newInstance().newSAXParser();
		Reader rd=new InputStreamReader(dis,"WINDOWS-1251");
		parser.parse(new InputSource(rd),new CurrentHandler(parser.getXMLReader()));
	}
}
