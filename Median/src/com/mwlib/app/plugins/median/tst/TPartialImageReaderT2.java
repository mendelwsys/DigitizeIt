package com.mwlib.app.plugins.median.tst;

import com.mwlib.utils.raster.PartialImageReader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 02.04.14
 * Time: 18:36
 * Тестирование частичного чтения растра
 */
public class TPartialImageReaderT2 extends Panel {

    private BufferedImage images[][];


  public TPartialImageReaderT2() throws Exception
  {

    String filename = "C:\\PapaWK\\Projects\\JavaProj\\VICTOR\\DigitizeItRes\\MAPDIR\\aaa.bmp";
    String extensionName = filename.substring(filename.lastIndexOf('.') + 1);
    InputStream inputStream = new FileInputStream(filename);





    PartialImageReader pr = new PartialImageReader(inputStream,extensionName);

    Point pt = pr.getImageSize();

    int numX=20;
    int numY=20;

    int width = (int)Math.ceil(pt.getX()/numX);
    int height =(int)Math.ceil(pt.getY()/numY);

    images = new BufferedImage[numX][numY];


      for (int i=0;i<numX;i++)
      {
          long tm=System.currentTimeMillis();
          for (int j=0;j<numY;j++)
            images[i][j] = pr.getImageByRectangle(new Rectangle(i*width,j*height, Math.min(width,pt.x-i*width),Math.min(height,pt.y-j*height)));
          System.out.println("i= "+i+"Load Time = "+ (System.currentTimeMillis()-tm)*1.0/(numY*1000));
      }

//      br:
//      for (int i = 0; i < images.length; i++)
//          for (int j = 0; j < images[i].length; j++)
//          {
//                BufferedImage image = images[i][j];
//                int iw=image.getWidth();
//                int ih=image.getHeight();
//
//              long tm=System.currentTimeMillis();
//
//              for (int iiw=0;iiw<iw;iiw++)
//                for(int iih=0;iih<ih;iih++)
//                {
//                    int rres = image.getRGB(iiw, iih);
//                    if (rres!=-8355712)
//                    {
//                        System.out.println("i = " + i);
//                        System.out.println("j = " + j);
//                        break br;
//                    }
//                }
//                System.out.println("i= "+i+"Chek_Time = "+ (System.currentTimeMillis()-tm)*1.0/(numY*1000));
//
//
//          }


      pr.free();
  }

    public void paint(Graphics g) {
    if (images == null)
      return;

        int x=0;
        int y;

        for (int i = 3; i < images.length; i++)
        {
            int w = 0;
            y=0;
            for (int j = 5; j < images[i].length; j++)
            {
                long tm=System.currentTimeMillis();
                BufferedImage image = images[i][j];
                g.drawImage(image, x, y, null);
                w = image.getWidth();
                y += image.getHeight();
            }
            x += w;
        }
  }

  static public void main(String args[]) throws Exception {
    JFrame frame = new JFrame();
    Panel panel = new TPartialImageReaderT2();
    frame.add(panel);
    frame.setSize(400, 400);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
