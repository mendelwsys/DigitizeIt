package com.mwlib.ptrace.test;

import com.mwlib.ptrace.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 22.12.13
 * Time: 20:45
 * To change this template use File | Settings | File Templates.
 */
public class t
{
    public static void main(String[] args) throws Exception
    {
        Utils u= new Utils();

//        String in = "girl.png";
//        String out="girl_res.png";
        String in = "proba2color.bmp";
        String out="proba2color.png";
        String pathName = "C:\\PapaWK\\Projects\\JavaProj\\Victor\\TestJava\\img\\";

        Bitmap bmp = u.getBmpByImage(ImageIO.read(new File(pathName+in)));



        PoTraceJ poTraceJ = new PoTraceJ();
        Param param = poTraceJ.createDefaultParams();

//        param.bPrint=true;
        param.turdsize=0;

        PathDef def = poTraceJ.trace(param, bmp);

        {
            double scale=3.0;
            BufferedImage result = new BufferedImage((int)(scale*bmp.getWidth()), (int)(scale*bmp.getHeight()), BufferedImage.TYPE_INT_RGB);

        //            u.drawBmp(bmp,result);
//            if (1==1)
//                return;

//            u.drawByNext(def, scale, result);
            u.drawBySibling(def, scale, result);

            ImageIO.write(result,"PNG",new File(pathName+out));
        }
    }


}
