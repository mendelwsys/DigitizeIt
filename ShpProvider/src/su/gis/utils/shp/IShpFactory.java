package su.gis.utils.shp;

import shp.core.PolygonShpFile;

/**
 * Created by IntelliJ IDEA.
 * User: Syg
 * Date: 26.06.2012
 * Time: 14:55:43
 * makes SHP file
 */
public interface IShpFactory {
    PolygonShpFile getPolygonMaker(String path, String[] numFlds, String[] TxtFlds);
    IShpPolylineMaker getPolylineMaker();
    IShpPointMaker getPointMaker();
}
