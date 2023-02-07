package com.mwlib.utils.raster;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 02.04.14
 * Time: 18:14
 * Вспомогательный класс частичного чтения растра
 */
public class PartialImageReader
{


    public ImageReader getImageReader() {
        return imageReader;
    }

    public ImageInputStream getImageInputStream() {
        return imageInputStream;
    }

    private ImageReader imageReader;
    private ImageInputStream imageInputStream;

    public PartialImageReader(InputStream inputStream,String extensionName)  throws Exception
    {
        reInit(inputStream,extensionName);
    }

    public Point getImageSize() throws Exception
    {
        return new Point(imageReader.getWidth(0),imageReader.getHeight(0));
    }

    public void reInit(InputStream inputStream,String extensionName)  throws Exception
    {
        free();
        Iterator readers = ImageIO.getImageReadersBySuffix(extensionName);
        imageReader = (ImageReader) readers.next();
        imageInputStream = ImageIO.createImageInputStream(inputStream);
        roundTripBMP(imageReader, imageInputStream);
    }

    public void free()
    {
        if (imageReader!=null)
            imageReader.dispose();

        if (imageInputStream!=null)
        try {
            imageInputStream.close();
        }
        catch (IOException e)
        {//
        }
    }



    private Rectangle old_rectangle;
    private WeakReference<BufferedImage> cacheImg;

    public static final int MAX_CAСHE_PIXEL = 2500 * 2500;

    public synchronized BufferedImage getImageByRectangle(Rectangle rectangle) throws Exception
    {
        BufferedImage cacheImg1;

        if (
                old_rectangle!=null && cacheImg!=null && ((cacheImg1=cacheImg.get())!=null) &&
                (old_rectangle.contains(rectangle) || old_rectangle.equals(rectangle))
           )
        {
            if (old_rectangle.equals(rectangle))
                return cacheImg1;
            else
                //Отдать подизображение
                return cacheImg1.getSubimage
                (
                        (int)Math.round(rectangle.getX()-old_rectangle.getX()),
                        (int)Math.round(rectangle.getY()-old_rectangle.getY()),
                        Math.min((int)Math.round(rectangle.getWidth()),cacheImg1.getWidth()),
                        Math.min((int)Math.round(rectangle.getHeight()),cacheImg1.getHeight())
                );
        }
        else
        {
            roundTripBMP(imageReader, imageInputStream);
            ImageReadParam param = imageReader.getDefaultReadParam();
            param.setSourceRegion(rectangle);

            if (rectangle.getWidth()*rectangle.getHeight()<= MAX_CAСHE_PIXEL)
            {
                cacheImg1 = imageReader.read(0, param);

                old_rectangle=rectangle;
                cacheImg=new WeakReference<BufferedImage>(cacheImg1);
                return cacheImg1;
            }
            else
            {
                old_rectangle=null;
                if (cacheImg!=null)
                    cacheImg.clear();
                cacheImg=null;
                return imageReader.read(0,param);
            }
        }
    }

    private void roundTripBMP(ImageReader imageReader, ImageInputStream imageInputStream) throws IOException
    {
        if ("bmp".equalsIgnoreCase(imageReader.getFormatName()))
        {
                imageInputStream.reset();
                imageInputStream.mark();
                imageReader.setInput(imageInputStream, false);
        }
        else if (imageReader.getInput()==null)
          imageReader.setInput(imageInputStream, false);
    }

}
