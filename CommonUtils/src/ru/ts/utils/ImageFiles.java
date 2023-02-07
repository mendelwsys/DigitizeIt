package ru.ts.utils;

import ru.ts.utils.Files;

import javax.imageio.ImageIO;
import java.util.LinkedList;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 30.12.2008
 * Time: 15:17:12
 * Class for working with images files
 */
public class ImageFiles
{
    public static String[] getImageNames(String files)
    {
        java.util.List<String> ll=new LinkedList<String>();
        if (files!=null)
        {
            String[] images=files.split(";");
            for (String image : images)
                if (image != null && image.length() > 0)
                    ll.add(image);
        }
        return ll.toArray(new String[ll.size()]);
    }

    public static BufferedImage getImageByName(String pathname) throws IOException
    {
        File fi = new File(pathname);
        if (fi.exists() && fi.isFile())
            return ImageIO.read(fi);
        return null;
    }

    public static void putImageToFile(String pathname,BufferedImage img) throws IOException
    {
        if (img!=null)
        {
//            String extension = Files.getExtension(pathname);
//            if (extension!=null && extension.length()>0 && extension.indexOf(".")>=0)
//                extension=extension.substring(extension.indexOf(".")+1);
//            else
            String extension="PNG";
			File output = new File(pathname);
			File parent = output.getParentFile();
			if (parent.exists() ||  parent.mkdir())
				ImageIO.write(img, extension , output);
        }
    }

    public static BufferedImage getImageByName(String pathname,String img_name) throws IOException
    {
        File dir = new File(pathname);
        String[] imglist = dir.list();
        img_name=img_name.toUpperCase();
        for (String s : imglist)
        {
            if (s.toUpperCase().contains(img_name))
            {
                return ImageIO.read(new File(pathname + "/" + s));
            }
        }
        return null;
    }

    public static java.util.List<String> getfileNames(String pathname,String img_name) throws IOException
    {
        File dir = new File(pathname);
        String[] imglist = dir.list();
        img_name=img_name.toUpperCase();
        java.util.List<String> rv=new LinkedList<String>();
        for (String s : imglist)
            if (s.toUpperCase().contains(img_name))
                rv.add(s);
        return rv;
    }

    public static String getfileNamesString(String pathname,String img_name) throws IOException
    {
        java.util.List<String> rv= getfileNames(pathname,img_name);
        StringBuffer rstr=new StringBuffer();
        for (String s : rv)
            rstr.append(s).append(";");
        return rstr.toString();
    }
}
