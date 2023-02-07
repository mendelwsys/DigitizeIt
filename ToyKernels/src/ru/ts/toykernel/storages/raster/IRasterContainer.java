package ru.ts.toykernel.storages.raster;

import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.utils.data.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 04.01.14
 * Time: 21:39
 * To change this template use File | Settings | File Templates.
 */
public interface IRasterContainer {
    BindStruct reInitByRaster(File raster) throws Exception;

    double[] getScaleRange();

    Pair<BindStruct, Integer> getCurrentStruct() throws Exception;

    public MPoint getImageSize() throws Exception;

    BufferedImage getImageByRectangle(Rectangle rectangle) throws Exception;
}
