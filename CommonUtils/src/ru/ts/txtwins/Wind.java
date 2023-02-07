package ru.ts.txtwins;

import com.mwlib.utils.Enc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.AffineTransformOp;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 21.12.2008
 * Time: 17:23:41
 * Class for
 */
public class Wind
{
	/**
	 * Создать
	 * @param img - изображение которое надо вставить в окошко
	 * @param txt - текст который надо нарисовать в окошко
	 * @param sz - размер окна
	 * @param tailsz - размер основания хвоста,высота хвоста,координата по X вершины хвоста,отступ от границы окна
	 * @return - сформированное изображение
	 */
	public static BufferedImage[] drawPictWnd_new(BufferedImage img,String txt,int sz[],int[] tailsz)
	{

		BufferedImage rv=new BufferedImage(20,20, BufferedImage.TYPE_INT_ARGB);

        Graphics gr = rv.getGraphics();

		Font f = gr.getFont();
		f=f.deriveFont(Font.BOLD);
		FontMetrics fm=gr.getFontMetrics(f);

		String[] strs=txt.split("\n");

        int shadowsz=3;//Размер тени
        int strgapY=5;//Отступ по у между строками и от краев  от краев картинки
        int strgapX=5;//Отступ по X от краев картинки 

        int szX=0;
		int szY=shadowsz;//Место для тени

		for (String str : strs)
		{
			Rectangle2D rect = fm.getStringBounds(str, gr);
			szY+=Math.ceil(rect.getHeight())+strgapY;
			int xz=(int)Math.ceil(rect.getWidth())+10;
			if (szX<xz)
				szX=xz;
		}
        szX+=shadowsz;//Место для тени

        if (img!=null)
        {

            szX=Math.max(sz[0]+shadowsz,szX);
            double k1 = 1.0 * (szX - shadowsz) / img.getWidth();
            double k2 = 1.0 * sz[1] / img.getHeight();

            double k=Math.max(k1,k2);

            sz[0]=(int)Math.ceil(k*img.getWidth());
            sz[1]=(int)Math.ceil(k*img.getHeight());

            szX=Math.max(sz[0]+shadowsz,szX);
            szY+=strgapY+sz[1];

             AffineTransform xformscale = AffineTransform.getScaleInstance(k,k);
             AffineTransformOp tranopscale = new AffineTransformOp(xformscale, AffineTransformOp.TYPE_BILINEAR);
             img = tranopscale.filter(img, null);
        }

		sz[0]=szX;
		sz[1]=szY;
		rv=new BufferedImage(sz[0],sz[1]+tailsz[1], BufferedImage.TYPE_INT_ARGB);
		gr = rv.getGraphics();


        gr.setColor(new Color(0xdd444444,true));
        gr.fillRoundRect(shadowsz,0,sz[0]-shadowsz,sz[1]-shadowsz,10,10);//Квадрат тени

        //Делаем тень "хвоста"
        gr.fillPolygon(new int[]{Math.max(tailsz[2]-tailsz[0]+shadowsz,shadowsz),tailsz[2]+shadowsz,tailsz[2]+tailsz[0]+shadowsz},new int[]{sz[1],sz[1]+tailsz[1],sz[1]},3);

        gr.setColor(new Color(0xddffffff,true));
		gr.fillRoundRect(0,shadowsz,sz[0]-shadowsz,sz[1]-shadowsz,10,10);//Квадрат рабочего поля
        gr.fillPolygon(new int[]{Math.max(tailsz[2]-tailsz[0],0),tailsz[2],tailsz[2]+tailsz[0]},new int[]{sz[1],sz[1]+tailsz[1],sz[1]},3);//


        //Рисуем надпись
        gr.setFont(f);
		gr.setColor(new Color(0xff004444,true));
		int startY=shadowsz;
		for (String str : strs)
		{
			Rectangle2D rect = fm.getStringBounds(str, gr);
			gr.drawString(str,strgapX+shadowsz,startY+(int)Math.ceil(rect.getHeight()));//Base line это линия на которой стоит буква
			startY+=rect.getHeight()+strgapY;
		}

		if (img!=null)
		{
			int dd = strgapY;
            int iY=sz[1]-img.getHeight()-dd;
            gr.drawImage(img,0,iY,
                    new ImageObserver()
            {
                public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
                {
                    return false;
                }
            });
        }
		return new BufferedImage[]{rv,img};
	}


    public static BufferedImage drawPictWnd(BufferedImage img,String txt,int sz[],int[] tailsz)
    {

        BufferedImage rv=new BufferedImage(sz[0],sz[1]+tailsz[1], BufferedImage.TYPE_INT_ARGB);
        Graphics gr = rv.getGraphics();

        Font f = gr.getFont();
        f=f.deriveFont(Font.BOLD);
        FontMetrics fm=gr.getFontMetrics(f);

        String[] strs=txt.split("\n");

        int shadowsz=3;//Размер тени

        int szX=0;
        int szY=shadowsz;//Место для тени

        for (String str : strs)
        {
            Rectangle2D rect = fm.getStringBounds(str, gr);
            szY+=Math.ceil(rect.getHeight())+shadowsz;
            int xz=(int)Math.ceil(rect.getWidth())+10;
            if (szX<xz)
                szX=xz;
        }

        szX+=shadowsz;//Место для тени
        if (szX>=sz[0] || szY>=sz[1])
        {
            sz[0]=szX;
            sz[1]=szY;
            rv=new BufferedImage(sz[0],sz[1]+tailsz[1], BufferedImage.TYPE_INT_ARGB);
            gr = rv.getGraphics();
        }

//		gr.setColor(new Color(0x0000000,true));
//		gr.fillRect(0,0,sz[0],sz[1]+tailsz[1]); //Заполнение всего квадрата

        gr.setColor(new Color(0xdd444444,true));
        gr.fillRoundRect(shadowsz,0,sz[0]-shadowsz,sz[1]-shadowsz,10,10);//Квадрат тени

        //Делаем тень "хвоста"
        gr.fillPolygon(new int[]{Math.max(tailsz[2]-tailsz[0]+shadowsz,shadowsz),tailsz[2]+shadowsz,tailsz[2]+tailsz[0]+shadowsz},new int[]{sz[1],sz[1]+tailsz[1],sz[1]},3);

        gr.setColor(new Color(0xddffffff,true));
        gr.fillRoundRect(0,shadowsz,sz[0]-shadowsz,sz[1]-shadowsz,10,10);//Квадрат рабочего поля
        gr.fillPolygon(new int[]{Math.max(tailsz[2]-tailsz[0],0),tailsz[2],tailsz[2]+tailsz[0]},new int[]{sz[1],sz[1]+tailsz[1],sz[1]},3);//


//        gr.setColor(new Color(0xff888888,true));
//        gr.drawPolyline(new int[]{Math.max(tailsz[2]-tailsz[0]+shadowsz,0),tailsz[2]+shadowsz,tailsz[2]+tailsz[0]+shadowsz},new int[]{sz[1],sz[1]+tailsz[1],sz[1]},3);

        //Рисуем надпись
        gr.setFont(f);
        gr.setColor(new Color(0xff004444,true));
        int startY=sz[1]-szY;
        for (String str : strs)
        {
            Rectangle2D rect = fm.getStringBounds(str, gr);
            gr.drawString(str,5+shadowsz,startY+(int)Math.ceil(rect.getHeight()));
            startY+=rect.getHeight()+5;
        }

        if (img!=null)
        {
            gr.setColor(new Color(0xff880000,true));
            int dd = tailsz[3];
            gr.fillRect(dd, dd,sz[0]- 2*dd,sz[1]- szY-dd);
        }

        return rv;
    }

    public static void main(String[] args) throws IOException
	{
        BufferedImage img = ImageIO.read(new File(Enc.get("C:\\MAPDIR\\VOK_IMG\\БАБАЕВО.jpg")));

//        AffineTransform xformscale = AffineTransform.getScaleInstance(
//                100.0 / img.getWidth(),
//                100.0/ img.getHeight());
//        AffineTransformOp tranopscale = new AffineTransformOp(xformscale, AffineTransformOp.TYPE_BILINEAR);
//        BufferedImage image = tranopscale.filter(img, null);
        ImageIO.write(drawPictWnd_new(img,Enc.get("$1"),new int[]{100,100},new int[]{10,15,5,10})[0],"PNG",new File("D:/wnd.png"));
	}
}
