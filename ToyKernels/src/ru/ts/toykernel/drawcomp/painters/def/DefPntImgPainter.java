package ru.ts.toykernel.drawcomp.painters.def;

import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.consts.KernelConst;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.converters.ILinearConverter;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.utils.data.Pair;
import ru.ts.utils.ImageFiles;
import ru.ts.txtwins.Wind;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 12.03.2009
 * Time: 16:02:12
 * Painter image with name of object
 */
public class DefPntImgPainter extends DefPointTextPainter
{

	protected Pair<Pair<String,String>,BufferedImage> curveName2ImageFile2Image=new Pair<Pair<String,String>,BufferedImage>(new Pair<String,String>("",""),null);

	public String getSrc_cacheimg_path()
	{
		return src_cacheimg_path;
	}

	public void setSrc_cacheimg_path(String src_cacheimg_path)
	{
		this.src_cacheimg_path = src_cacheimg_path;
	}

	public String getSrc_img_path()
	{
		return src_img_path;
	}

	public void setSrc_img_path(String src_img_path)
	{
		this.src_img_path = src_img_path;
	}


	protected String src_img_path;
	protected String src_cacheimg_path;
	protected INameConverter storNm2attrNm;


	public static final int IMG_HEIGHT = 100;
	public static final int IMG_WIDTH = 100;

	protected BufferedImage drawPictName(String attrname, String imgfilename, int[] tails)
	{
				BufferedImage rv = null;
				if (curveName2ImageFile2Image.first.first.equals(attrname) && curveName2ImageFile2Image.first.second.equals(imgfilename))
					rv=curveName2ImageFile2Image.second;
				else
				{
					try
					{
						rv= ImageFiles.getImageByName(src_cacheimg_path +"/"+ imgfilename);
						if (rv!=null)
							rv= Wind.drawPictWnd_new(rv,attrname,new int[]{rv.getWidth(), rv.getHeight()}, tails)[0];
						else
						{
						   rv= ImageFiles.getImageByName(src_img_path +"/"+ imgfilename);
						   BufferedImage[] picts = Wind.drawPictWnd_new(rv, attrname, new int[]{IMG_WIDTH, IMG_HEIGHT}, tails);
						   rv=picts[0];
						   ImageFiles.putImageToFile(src_cacheimg_path +"/"+ imgfilename,picts[1]);
						}
					}
					catch (Exception e)
					{//
						e.printStackTrace();
					}
					curveName2ImageFile2Image.first.first=attrname;
					curveName2ImageFile2Image.first.second=imgfilename;
					curveName2ImageFile2Image.second=rv;
			   }
		return rv;
	}

	/*
	Конструктор говорит о том что объект может быть сгенерирован правилом по умолчанию с помощью рефлекшена
	 */
	public DefPntImgPainter()
	{

	}

	/*
		Этот конструктор может быть использован тогда когда все переменный паинтера могут быть заданы в его фабрике
		и устанавливаемые переменные независимы от атрибутов объекта
	 */
	public DefPntImgPainter
	(
			String src_img_path,String src_cacheimg_path, INameConverter storNm2attrNm,
			String text, Font font, Color colorFill, Color colorLine, Color colorText
	)
	{
		super(text, font, colorFill, colorLine, colorText);
		this.src_img_path = src_img_path;
		this.src_cacheimg_path = src_cacheimg_path;
		this.storNm2attrNm=storNm2attrNm;
	}

	/*
		В Этот конструктор выделены те переменные которые логично устанавливливать фабрикой объектов
	 */
	public DefPntImgPainter(String src_img_path,String src_cacheimg_path, INameConverter storNm2attrNm)
	{
		this.src_img_path = src_img_path;
		this.src_cacheimg_path = src_cacheimg_path;
		this.storNm2attrNm=storNm2attrNm;
	}


	public int[] paint(Graphics graphics, IBaseGisObject drawMe, ILinearConverter converter, Point drawSize) throws Exception
	{
		setPainterParams(graphics, drawMe, converter, drawSize);

		{
			double[][][] arrpoint = drawMe.getRawGeometry();
			MPoint centralp=new MPoint(arrpoint[0][0][0],arrpoint[1][0][0]);
			if (text != null && text.length() > 0)
			{
				Point pi = converter.getDstPointByPoint(centralp);
				Color color = graphics.getColor();

				String str_images = null;
				IDefAttr iDefAttr = drawMe.getObjAttrs().get(storNm2attrNm.codeAttrNm2StorAttrNm(KernelConst.ATTR_IMG_REF));
				if (iDefAttr!=null && iDefAttr.getValue()!=null)
					str_images = iDefAttr.getValue().toString();
				String[] images = ImageFiles.getImageNames(str_images);
				int[] tails = {10, 15, 5, 10};
				BufferedImage rv = drawPictName(text, images.length>0?images[0]:"", tails);
				int x_draw = pi.x - tails[2];
				int y_draw = pi.y - rv.getHeight();
//				if (selRects!=null)
//				{
//					Structures.MyPoint lpnt1=converter.getLinearPointByDrawPoint(new Structures.MyPoint(x_draw,y_draw),currentP0);
//					Structures.MyPoint lpnt2=converter.getLinearPointByDrawPoint(new Structures.MyPoint(x_draw+rv.getWidth(),y_draw+rv.getHeight()),currentP0);
//					selRects.add(new Pair<String,Structures.MyRect>(curve.getCurveId(),new Structures.MyRect(new Structures.MyPoint(Math.min(lpnt1.x,lpnt2.x),Math.min(lpnt1.y,lpnt2.y)),
//							new Structures.MyPoint(Math.max(lpnt1.x,lpnt2.x),Math.max(lpnt1.y,lpnt2.y)))));
//				}
				graphics.drawImage
						(rv, x_draw, y_draw, new ImageObserver() {
							public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
								return false;
							}
						});
				graphics.setColor(color);
			}
			else
			{
				Point pobj = converter.getDstPointByPoint(centralp);
				graphics.fillOval(pobj.x - 3, pobj.y - 3, 6, 6);
			}
		}
		return new int[]{1,1,1};
	}

	public MRect getDrawRect(Graphics graphics, IBaseGisObject obj, ILinearConverter converter) throws Exception
	{
		throw new UnsupportedOperationException();
	}

}
