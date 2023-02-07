package com.mwlib.app.plugins.shp;

import com.mwlib.ptrace.*;
import shp.core.ShpCoreLogger;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 04.01.14
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
public class Test
{

    public static void main(String[] args) throws Exception
    {
        String shpPath="MAPDIR\\SHP\\TestIt";
        final Utils u= new Utils();

        Bitmap bmp = u.getBmpByImage(ImageIO.read(new File("MAPDIR\\girl.png")));

        PoTraceJ poTraceJ = new PoTraceJ();
        Param param = poTraceJ.createDefaultParams();
//        param.bPrint=true;
        param.turdsize=0;

        final PathDef def = poTraceJ.trace(param, bmp);

        ShpCoreLogger.disableLogging();

        new PoTrace2Shp().createShapeFile(shpPath, def, null);

    }
}
