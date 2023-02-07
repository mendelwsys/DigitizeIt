package su.gis.utils.shp;

import ru.ts.utils.Files;
import shp.core.PolygonShpFile;

import java.io.File;
//import ru.ts.common.misc.Files;
//import ru.ts.common.misc.IFilePath;
//import ru.ts.common.misc.Text;

/**
 * Created by IntelliJ IDEA.
 * User: Syg
 * Date: 26.06.2012
 * Time: 14:57:59
 * To change this template use File | Settings | File Templates.
 */
public class ShpFactory
{
    public static PolygonShpFile getPolygonMaker(String path, String[] numFlds,String[] dublFlds, String[] TxtFlds)
    {
      return new PolygonShpFile(removeExtension(path), numFlds,dublFlds, TxtFlds);
    }

    private static String removeExtension(String path)
    {

      // check if input has an extension
//      final IFilePath fp = new IFilePath.Impl(path);
//      String ext = fp.getExtension();
//      if ( Text.notEmpty(ext) )
//      {
//        fp.setExtension( "" );
//        path = fp.getPath();
//      }
        File fl = new File(path);
        String fileName=fl.getName();
        int i=fileName.lastIndexOf('.');
        if (i>=0)
        {
            String p_ext=fileName.substring(i);
            path=path.substring(0, path.length() -p_ext.length());
        }
        return path;
    }

    public static void main(String[] args)
    {
        System.out.println("pathname = " + removeExtension("C:\\PapaWK\\Projects\\JavaProj\\Victor\\DigitizeIt\\ShpProvider\\src\\su\\gis\\utils\\shp\\IShpFactory.java"));
    }

    public IShpPolylineMaker getPolylineMaker()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IShpPointMaker getPointMaker()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
