package ru.ts.toykernel.drawcomp.rules.def;

import javax.imageio.ImageIO;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.*;

/**
 * Стандартный стиль, фактически бин со стилями рисования
 */
public class CommonStyle
{


	public static final String HI_RANGE = "HiRange";
	public static final String LOW_RANGE = "LowRange";
	public static final String COLOR_LINE = "ColorLine";
	public static final String COLOR_FILL = "ColorFill";
	public static final String LINE_STYLE = "LineStyle";
	public static final String RAD_PNT = "RadPnt";


	public static final String LINE_THICKNESS = "LineThickness";
	public static final String SCALE_LINE_THICKNESS = "ScaleLineThickness";



	public static final String END_CAP = "EndCap";
	public static final String LINE_JOIN = "LineJoin";
	public static final String MITER_LIMIT = "MiterLimit";

	public static final String DASH_ARRAY = "DashArray";
	public static final String DASH_PHASE = "DashPhase";

	//Типы заполнений
	public static final int FILL_COLOR = 0;
	public static final int FILL_IMG = 1;

	public static final String SDOTTED = "dotted";
	public static final String SSOLID = "solid";

	public static final int DOTTED = 1;
	public static final float[] DOTTEDSTYLE = new float[]{5,5};
	public static final int DOTTEDPHASE = 0;
	public static final int SOLID = 0;


	public static final String TEXTURE_IMAGE_PATH = "texture_image_path";
	public static final String TEXTURE_RECT = "texture_rect";

	//Введение отдельных опций связано с тем что теоретически в одном слое могут нахордится
	//объекты разных типов, соответственно изображения могут быть разными для точек и полигонов
	public static final String POINT_IMAGE_PATH = "point_image_path";
	public static final String POINT_IMAGE_RECT = "point_image_rect";
	public static final String POINT_IMAGE_СENTRAL = "point_image_central";

	public static final String AUTO_IMAGE_СENTRAL = "auto_image_central";



	

	public int getColorLine()
	{
		return colorLine;
	}

	public void setColorLine(int colorLine)
	{
		this.colorLine = colorLine;
	}

	public int getColorFill()
	{
		return colorFill;
	}

	public void setColorFill(int colorFill)
	{
		this.colorFill = colorFill;
	}

	public float getLinethickness()
	{
		return linethickness;
	}

	public void setLineWidth(float linethickness)
	{
		this.linethickness = linethickness;
	}

	public double[] getScaleRange()
	{
		return scaleRange;
	}

	public void setScaleRange(double[] scaleRange)
	{
		this.scaleRange = scaleRange;
	}

	private int colorLine; //цвет границы
	private int lineStyle = SOLID; //стиль линии



	public int getTypeFill()
	{
		return typeFill;
	}

	public void setTypeFill(int typeFill)
	{
		this.typeFill = typeFill;
	}

	private int typeFill = FILL_COLOR;
	private int colorFill; //цвет заполнения



	private String textureImagePath;//
	private String textureRect;//
	private BufferedImage textureImg;//изображение для текстуры


	public String getPointRect()
	{
		return pointRect;
	}

	public void setPointRect(String pointRect)
	{
		this.pointRect = pointRect;
	}

	public String getPointImagePath()
	{
		return pointImagePath;
	}

	public void setPointImagePath(String pointImagePath)
	{
		this.pointImagePath = pointImagePath;
	}


	public boolean isAutocenral()
	{
		return autocenral;
	}

	public void setAutocenral(boolean autocenral)
	{
		this.autocenral = autocenral;
	}

	private boolean autocenral =true; //Устанавливать автоматом центральную точку на центр изображения

	private String pointImagePath;//
	private String pointRect;//

	private Point centralpoint;//

	public Point getPointCentralAsPoint()
	{
		return centralpoint;
	}

	public String getPointCentral()
	{
		if (centralpoint !=null)
			return centralpoint.x+";"+ centralpoint.y;
		return null;
	}

	public void setScaleLineThickness(boolean scaleLineThickness)
	{
		this.scaleLineThickness = scaleLineThickness;
	}

	public boolean isScaleLineThickness()
	{
		return this.scaleLineThickness;
	}

	public void setPointCentral(String pointCentral)
	{
		try
		{
			String[] coords = pointCentral.split("[\\,,;]");
			if (coords.length == 2)
				centralpoint = new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
	}



	private BufferedImage pointImg;//изображение для текстуры


	public BufferedImage getTextureImg()
	{
		try
		{
			if (textureImg == null && typeFill == FILL_IMG && textureImagePath != null)
				textureImg=loadImage(textureImagePath,textureRect);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return textureImg;
	}



	public BufferedImage getPointImg()
	{
		try
		{
			if (pointImg == null && pointImagePath != null)
				pointImg=loadImage(pointImagePath,pointRect);
		}
		catch (IOException e)
		{
			System.out.println("pointImagePath = " + pointImagePath);
//			e.printStackTrace();
		}
		return pointImg;
	}

	private BufferedImage loadImage(String imagePath,String imageRect)
			throws IOException
	{
		BufferedImage textureImg = ImageIO.read(new File(imagePath));
		if (imageRect != null)
		{
			String[] coords = imageRect.split("[\\,,;]");
			if (coords != null)
			{
				try
				{
					if (coords.length == 4)
						textureImg = textureImg.getSubimage(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));
					else if (coords.length == 2)
						textureImg = textureImg.getSubimage(0, 0, Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
		}
		return textureImg;
	}

	public String getTextureImagePath()
	{

		return textureImagePath;
	}

	public void setTextureImagePath(String textureImagePath)
	{
		if (this.textureImagePath==null || !this.textureImagePath.equals(textureImagePath))
		{
			this.textureImagePath = textureImagePath;
			if (textureImagePath!=null)
				typeFill = FILL_IMG;
			textureImg = null;
		}
	}

	public String getTextureRect()
	{
		return textureRect;
	}

	public void setTextureRect(String textureRect)
	{
		if (this.textureRect!=null || !this.textureRect.equals(textureRect))
		{
			this.textureRect = textureRect;
			textureImg = null;
		}
	}



	private Integer radPnt = null;//Радиус точки линии или полигона

	//Stroke data
	private float linethickness = 1;//Толщина линий показывающей дороги
	private boolean scaleLineThickness = false;//Является ли толщина линии масштабируемой величиной ( исползуется для отображения кадовских форматов)
	private int endCap; //
	private int lineJoin;//
	private float miterLimit;//
	private float[] dash;
	private float dashphase;

	public float[] getDash()
	{
		return dash;
	}

	public void setDashArray(float[] dash)
	{
		this.dash = dash;
	}

	public float getDashphase()
	{
		return dashphase;
	}

	public void setDashPhase(float dashphase)
	{
		this.dashphase = dashphase;
	}

	public float getMiterLimit()
	{
		return miterLimit;
	}

	public void setMiterLimit(float miterLimit)
	{
		this.miterLimit = miterLimit;
	}

	public int getLineJoin()
	{
		return lineJoin;
	}

	public void setLineJoin(int lineJoin)
	{
		this.lineJoin = lineJoin;
	}

	public int getEndCap()
	{
		return endCap;
	}

	public void setEndCap(int endCap)
	{
		this.endCap = endCap;
	}

	public int getLineStyle()
	{
		return lineStyle;
	}

	public void setLineStyle(int lineStyle)
	{
		this.lineStyle = lineStyle;
	}


	public String getsLineThickness()
	{
		return String.valueOf(linethickness);
	}

	public void setsLineThickness(String linethickness)
	{
		try
		{
			this.linethickness = Float.parseFloat(linethickness);
		}
		catch (NumberFormatException e)
		{
			//
		}
	}


	public double[] scaleRange = new double[]{-1, -1}; //диапазон масштабов между которыми показывается слой


	public String getScaleLowRange()
	{
		return String.valueOf(scaleRange[0]);
	}

	public double getScaleLow()
	{
		return scaleRange[0];
	}

	public double getScaleHi()
	{
		return scaleRange[1];
	}


	public void setScaleLow(double low)
	{
		scaleRange[0] = low;
	}

	public void setsRadPnt(String radPnt)
	{
		try
		{
			this.radPnt = Integer.parseInt(radPnt);
		}
		catch (NumberFormatException e)
		{//
			this.radPnt = null;
		}
	}

	public String getsRadPnt()
	{
		if (this.radPnt != null)
			return String.valueOf(this.radPnt);
		return "";
	}

	public void setRadPnt(Integer radPnt)
	{
		this.radPnt = radPnt;
	}

	public Integer getRadPnt()
	{
		return this.radPnt;
	}

	public void setScaleHi(double hi)
	{
		scaleRange[1] = hi;
	}

	public void setLowRange(String scaleRange)
	{
		this.scaleRange[0] = Double.parseDouble(scaleRange);
	}

	public String getScaleHiRange()
	{
		return String.valueOf(scaleRange[1]);
	}

	public void setHiRange(String scaleRange)
	{
		this.scaleRange[1] = Double.parseDouble(scaleRange);
	}


	public String getsHexColorFill()
	{
		return Integer.toHexString(colorFill);
	}

	public void setsHexColorFill(String colorFill)
	{
		try
		{
			this.colorFill = (int) Long.parseLong(colorFill, 16);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
	}

	public String getsHexLineStyle()
	{
		return Integer.toHexString(lineStyle);
	}

	public void setsHexLineStyle(String lineStyle)
	{
		try
		{
			this.lineStyle = Integer.parseInt(lineStyle, 16);
		}
		catch (NumberFormatException e)
		{
			//
		}
	}

//	public String getsHexLineThickness()
//	{
//		return Integer.toHexString((int) linethickness);
//	}

//	public void setsHexLineThickness(String linethickness)
//	{
//		try
//		{
//			this.linethickness = Integer.parseInt(linethickness,16);
//		}
//		catch (NumberFormatException e)
//		{
//			//
//		}
//	}

	public String getsHexColorLine()
	{
		return Integer.toHexString(colorLine);
	}

	public void setsHexColorLine(String colorLine)
	{
		this.colorLine = (int) Long.parseLong(colorLine, 16);
	}


	public CommonStyle()
	{

	}


	public CommonStyle(int colorLine, int colorFill)
	{
		this.colorLine = colorLine;
		this.colorFill = colorFill;
	}

	public CommonStyle(int colorLine, int colorFill, double scaleLow, double scaleHi, int linethickness)
	{
		this.linethickness = linethickness;
		this.colorLine = colorLine;
		this.colorFill = colorFill;
		this.scaleRange[0] = scaleLow;
		this.scaleRange[1] = scaleHi;
	}

	public CommonStyle(CommonStyle cs)
	{
		this.linethickness = cs.linethickness;
		this.colorLine = cs.colorLine;
		this.colorFill = cs.colorFill;
		this.lineStyle = cs.lineStyle;
		this.scaleRange = new double[cs.scaleRange.length];
		System.arraycopy(cs.scaleRange, 0, this.scaleRange, 0, cs.scaleRange.length);
	}

	public void savetoStream(DataOutputStream dos) throws IOException
	{
		dos.writeInt(colorLine); //цвет границы
		dos.writeInt(lineStyle); //стиль линии
		dos.writeInt(colorFill); //цвет заполнения
		dos.writeInt((int) linethickness); //толщина линии

		dos.writeInt(scaleRange.length);
		for (double aScaleRange : scaleRange)
			dos.writeDouble(aScaleRange);
	}

	public static CommonStyle loadFromStream(DataInputStream dis) throws IOException
	{
		CommonStyle cs = new CommonStyle();
		cs.colorLine = dis.readInt(); //цвет границы
		cs.lineStyle = dis.readInt(); //стиль линии
		cs.colorFill = dis.readInt(); //цвет заполнения
		cs.linethickness = dis.readInt(); //толщина линии
		if (cs.linethickness <= 0) cs.linethickness = 1;


		cs.scaleRange = new double[dis.readInt()];
		for (int i = 0; i < cs.scaleRange.length; i++)
			cs.scaleRange[i] = dis.readDouble();
		return cs;
	}
}
