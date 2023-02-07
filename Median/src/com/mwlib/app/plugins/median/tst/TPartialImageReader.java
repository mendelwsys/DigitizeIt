package com.mwlib.app.plugins.median.tst;

import com.mwlib.utils.raster.PartialImageReader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 02.04.14
 * Time: 18:36
 * Тестирование частичного чтения растра
 */
public class TPartialImageReader extends Panel {

    private BufferedImage images[][];


  public TPartialImageReader() throws Exception
  {

    String filename = "C:\\PapaWK\\Projects\\JavaProj\\VICTOR\\DigitizeIt\\MAPDIR\\boloto.png";
    String extensionName = filename.substring(filename.lastIndexOf('.') + 1);
    FileInputStream inputStream = new FileInputStream(filename);
    PartialImageReader pr = new PartialImageReader(inputStream,extensionName);

    Point pt = pr.getImageSize();

    int numX=20;
    int numY=20;

    int width = (int)Math.ceil(pt.getX()/numX);
    int height =(int)Math.ceil(pt.getY()/numY);

    images = new BufferedImage[numX][numY];


      for (int i=0;i<numX;i++)
          for (int j=0;j<numY;j++)
            images[i][j] = pr.getImageByRectangle(new Rectangle(i*width,j*height, Math.min(width,pt.x-i*width),Math.min(height,pt.y-j*height)));

      pr.free();
  }

    public void paint(Graphics g) {
    if (images == null)
      return;

        int x=0;
        int y;

        for (int i = 0; i < images.length; i++)
        {
            int w = 0;
            y=0;
            for (int j = 0; j < images[i].length; j++)
            {
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
    Panel panel = new TPartialImageReader();
    frame.add(panel);
    frame.setSize(400, 400);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
