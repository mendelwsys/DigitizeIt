package com.mwlib.app.plugins.shp;

import com.mwlib.app.plugins.common.ExporterData;
import com.mwlib.app.plugins.digitizer.ParamEx;
import com.mwlib.ptrace.IProgressObserver;
import com.mwlib.ptrace.PathDef;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.utils.data.Pair;
import shp.core.PolygonShpFile;
import shp.core.ShpFileHeader;
import shp.core.ShpPoint;
import shp.utils.GeomContainer;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 20.04.14
 * Time: 19:08
 * To change this template use File | Settings | File Templates.
 */
public class PoTrace2ShpIO
extends PoTrace2Shp
{
    private long fileLength;
    private int recordNumber;




    public PoTrace2ShpIO()
    {
        fileLength   = ShpFileHeader.INITIAL_FILE_LENGTH;
        recordNumber=1;
    }
    public void createShapeFile(String shpName, PathDef def, IProgressObserver progress) throws Exception
    {
        fileLength   = ShpFileHeader.INITIAL_FILE_LENGTH;
        recordNumber=1;
        super.createShapeFile(shpName,def, progress);
    }

    public void createShapeFile(Pair<ExporterData, PathDef>[] defs, MPoint totalSize, IProgressObserver progress) throws Exception
    {
        fileLength   = ShpFileHeader.INITIAL_FILE_LENGTH;
        recordNumber=1;
        super.createShapeFile(defs,totalSize, progress);
    }



    protected int fillShapeFile33(PolygonShpFile shpFile,Vector<Vector<ShpPoint>> pgons, PathDef defPlus, AffineTransform af,int id) throws Exception
    {
        return this.fillShapeFile33(shpFile,(GeomContainer)null,defPlus, af,id);
    }


    protected int fillShapeFile33(PolygonShpFile shpFile,GeomContainer container, PathDef defPlus, AffineTransform af,int id) throws Exception
    {
        boolean isFirstLevel=(container==null);
        if (isFirstLevel)
            container = new GeomContainer();

        try {
            while (defPlus!=null)
            {
                if (defPlus.getSign()=='-')
                    throw new IllegalArgumentException();
                id=fillShapeFileWithHoles3(container, defPlus,af,id);
                if (isFirstLevel)
                {
                    Pair<Map<String, Integer>, Map<String, Double>> iAttr = createIAttr2(defPlus, container, id);
                    fileLength=PolygonShpFile2.addPolygon2(shpFile.getShpBase(),fileLength,container, iAttr.first,iAttr.second,createSAttr());
                    recordNumber++;
                }
                defPlus=defPlus.getSibling();
            }
            return id;
        }
        finally
        {
            if (isFirstLevel)
                container.reset();
        }
    }



    protected int fillShapeFileWithHoles3(GeomContainer container, final PathDef defPlus, AffineTransform af,int id) throws Exception
    {
        if (defPlus==null)
            return id;

        id=add2ShapeFileWithSibling3(container, defPlus, af, id);

        PathDef defMinus = defPlus.getFirstChild();
        while (defMinus!=null)
        {
            if (defMinus.getSign()=='+')
                throw new IllegalArgumentException();
            id=fillShapeFile33(null,container, defMinus.getFirstChild(), af, id);

            defMinus=defMinus.getSibling();
        }
        return id;
    }


    protected  int add2ShapeFileWithSibling3(GeomContainer container, final PathDef defPlus, AffineTransform af,int id) throws Exception
    {
        if (defPlus.getSign()=='-')
            throw new IllegalArgumentException();

        Pair<Shape, PathDef> pathPair = poTraceUtils.getPath(defPlus);
        PolygonShpFile2.add2Vector(container, pathPair.first,af,flatness);

        PathDef defMinus = defPlus.getFirstChild();
        while (defMinus!=null)
        {
            if (defMinus.getSign()=='+')
                throw new IllegalArgumentException();
            Pair<Shape, PathDef> sibPair = poTraceUtils.getPath(defMinus);
            PolygonShpFile2.add2Vector(container, sibPair.first,af,flatness);
            id++;
            defMinus=defMinus.getSibling();
        }
        return id+1;
    }


    protected  int add2ShapeFileWithSibling(
            PolygonShpFile shpFile, final PathDef defPlus, AffineTransform af,int id

    )
            throws Exception
    {
        if (defPlus.getSign()=='-')
            throw new IllegalArgumentException();

        GeomContainer container = null;
        try
        {
            Pair<Shape, PathDef> pathPair = poTraceUtils.getPath(defPlus);

            container = new GeomContainer();
            container.setRecordNumber(recordNumber);
            PolygonShpFile2.add2Vector(container, pathPair.first,af,flatness);

            PathDef defMinus = defPlus.getFirstChild();
            while (defMinus!=null)
            {
                if (defMinus.getSign()=='+')
                    throw new IllegalArgumentException();
                Pair<Shape, PathDef> sibPair = poTraceUtils.getPath(defMinus);
                PolygonShpFile2.add2Vector(container, sibPair.first, af, flatness);
                defMinus=defMinus.getSibling();
            }

            Pair<Map<String, Integer>, Map<String, Double>> iAttr = createIAttr2(pathPair.second, container, id);
            fileLength=PolygonShpFile2.addPolygon2(shpFile.getShpBase(),fileLength,container, iAttr.first,iAttr.second,createSAttr());
            recordNumber++;
            return id+1;
        }
        finally
        {
            if (container!=null)
                container.reset();
        }
    }

    private Pair<Map<String, Integer>,Map<String, Double>> createIAttr2(PathDef path, GeomContainer container, int id)
    {
        int pntCnt=container.numOfPoints;


        Map<String, Double> doubleVals = new HashMap<String, Double>();
        Map<String, Integer> numVals = new HashMap<String, Integer>();

        if (path.getSign()=='+')
            numVals.put(colorName, blackColor);
        else
            numVals.put(colorName, whiteColor);
        numVals.put(NAME_AREA,path.getArea());

        if (resolution !=0)
            doubleVals.put(DEF_NAME_GAAREA, ParamEx.getGaByPixel(path.getArea(), resolution));

        numVals.put(NAME_SIGN, path.getSign());

        numVals.put(NAME_ID, id);
        numVals.put(NAME_POLY_CNT, container.numOfParts);
        numVals.put(NAME_POINT_CNT, pntCnt);

        if (resolution !=0)
            doubleVals.put(totalSquareName,totalArea);

        return new Pair<Map<String, Integer>, Map<String, Double>>(numVals,doubleVals);
    }


}
