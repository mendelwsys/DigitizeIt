package com.mwlib.app.plugins.shp;

import com.mwlib.app.plugins.common.ExporterData;
import com.mwlib.app.plugins.digitizer.ParamEx;
import com.mwlib.ptrace.IProgressObserver;
import com.mwlib.ptrace.PathDef;
import com.mwlib.ptrace.Utils;
import ru.ts.common.misc.Text;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.utils.Files;
import ru.ts.utils.data.Pair;
import shp.core.PolygonShpFile;
import shp.core.ShpCoreLogger;
import shp.core.ShpPoint;
import su.gis.utils.shp.ShpFactory;
import su.gis.utils.shp.ShpPgon;
import su.utils.tab.TabReader;
import su.utils.tab.TabSolverJAMA;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 04.01.14
 * Time: 20:32
 * Convert potrace object to shape file
 */
public class PoTrace2Shp extends ShapeObj2Shp
{
    public static final String NAME_AREA = "OBJPX";
    public static final String DEF_NAME_GAAREA = "OBJGA";
    public static final String DEF_NAME_TGAAREA = "TOTGA";

    public static final String NAME_SIGN = "Sign";


    public String[] getNumNames()
    {
        return new String[]{ NAME_ID, NAME_POLY_CNT, NAME_POINT_CNT, colorName,NAME_AREA,NAME_SIGN};
    }

    public String[] getDoubleNames()
    {
        return new String[]{DEF_NAME_GAAREA, totalSquareName};
    }

    public String[] getTxtNames()
    {
        if (name2attribute.first!=null)
            return new String[]{name2attribute.first};
        else
            return new String[]{};
    }


    public int getBlackColor() {
        return blackColor;
    }

    public void setBlackColor(int blackColor) {
        this.blackColor = blackColor;
    }

    public int getWhiteColor() {
        return whiteColor;
    }

    public void setWhiteColor(int whiteColor) {
        this.whiteColor = whiteColor;
    }


    public static final int DEF_BLACK_COLOR = 0xFF000000;
    public static final int DEF_WHITE_COLOR = 0xFFFFFFFF;

    protected String colorName = DEF_NAME_COLOR;
    protected  int blackColor =DEF_BLACK_COLOR;
    protected int whiteColor =DEF_WHITE_COLOR;

    protected Pair<String,String> name2attribute = new Pair<String, String>(null,null);

    protected  double totalArea=0;
    protected  String totalSquareName =DEF_NAME_TGAAREA;

    protected double resolution =0;

    public boolean isConvert() {
        return isConvert;
    }

    public void setConvert(boolean convert) {
        isConvert = convert;
    }

    public String getfTabName() {
        return fTabName;
    }

    public void setfTabName(String fTabName) {
        this.fTabName = fTabName;
    }

    private boolean isConvert;
    private String fTabName;

    public int gethImg() {
        return hImg;
    }

    public void sethImg(int hImg) {
        this.hImg = hImg;
    }

    public int getwImg() {
        return wImg;
    }

    public void setwImg(int wImg) {
        this.wImg = wImg;
    }

    private int hImg=-1;
    private int wImg=-1;

    final Utils poTraceUtils = new Utils();

    public void createShapeFile(String shpName, PathDef def, IProgressObserver progress) throws Exception
    {
        this.totalArea= ParamEx.getGaByPixel(poTraceUtils.getTotalArea(def),resolution);

        PolygonShpFile shpFile =null;
        try
        {
            String asShapeObject1 = getAsShapeObject();
            shpFile = ShpFactory.getPolygonMaker(shpName, getNumNames(),getDoubleNames(),getTxtNames());
            if (WHOLE_OBJECT.equalsIgnoreCase(asShapeObject1))
                fillShapeFile3(shpFile, def, true, progress);
            else if (POLY_WITH_HOLES.equalsIgnoreCase(asShapeObject1))
                fillShapeFile2(shpFile, def, true, progress);
            else
                fillShapeFile(shpFile, def, true,progress);
        }
        finally
        {
            if (shpFile!=null)
                shpFile.close();
        }
    }

    public String setByExportData(ExporterData exporterData,MPoint totalSize) throws Exception
    {
        setFlatness(Double.parseDouble(exporterData.getFlatness()));
        setBlackColor((int) Long.parseLong(exporterData.getBlackColor(), 16));
        setWhiteColor((int) Long.parseLong(exporterData.getWhiteColor(), 16));
        setAsShapeObject(exporterData.getAsShapeObject());

        colorName = exporterData.getColorName();
        totalSquareName = exporterData.getSquareName();


        name2attribute = new Pair<String, String>(exporterData.getAttributeName(),exporterData.getAttribute());
        try
        {
           resolution = Double.parseDouble(exporterData.getResolution());
        }
        catch (Exception e)
        {
            throw new Exception(e);
        }

        if (exporterData.isTranslate())
        {
            setConvert(true);
            setfTabName(exporterData.getTabFile());
            setwImg((int) Math.round(totalSize.getX()));
            sethImg((int) Math.round(totalSize.getY()));
        }
        else
        {
            setConvert(false);
            setfTabName(null);
            setwImg(-1);
            sethImg(-1);
        }
        ShpCoreLogger.disableLogging();
        if (exporterData.isUnionAll())
            return exporterData.getPathName() + File.separator + exporterData.getCommonName();
        else
            return exporterData.getPathName() + File.separator + exporterData.getFname();
    }

    public void createShapeFile(Pair<ExporterData, PathDef>[] defs, MPoint totalSize, IProgressObserver progress) throws Exception
    {
        PolygonShpFile shpFile = null;
        String shpName = null;
        try
        {
            //Формируем
            Set<String> textFiles= new HashSet<String>();
            Set<String> numfields= new HashSet<String>();
            Set<String> dobfields= new HashSet<String>();

            for (int i = 0, defsLength = defs.length; i < defsLength; i++)
            {
                setByExportData(defs[i].first,totalSize);
                textFiles.addAll(Arrays.asList(getTxtNames()));
                numfields.addAll(Arrays.asList(getNumNames()));
                dobfields.addAll(Arrays.asList(getDoubleNames()));
            }

            String[] numFlds = numfields.toArray(new String[numfields.size()]);
            String[] dublFlds = dobfields.toArray(new String[dobfields.size()]);
            String[] txtFlds = textFiles.toArray(new String[textFiles.size()]);

            for (int i = 0, defsLength = defs.length; i < defsLength; i++)
            {
                PathDef def = defs[i].second;
                String _shpName=setByExportData(defs[i].first,totalSize);
                boolean  isCreate=(shpFile==null || !_shpName.equalsIgnoreCase(shpName));

                this.totalArea= ParamEx.getGaByPixel(poTraceUtils.getTotalArea(def),resolution);

                if (isCreate)
                {
                    shpName=_shpName;
                    if (shpFile!=null)
                        shpFile.close();
                    shpFile = ShpFactory.getPolygonMaker(shpName, numFlds, dublFlds, txtFlds);
                }

                String asShapeObject1 = getAsShapeObject();
                if (WHOLE_OBJECT.equalsIgnoreCase(asShapeObject1))
                    fillShapeFile3(shpFile, def, isCreate, progress);
                else if (POLY_WITH_HOLES.equalsIgnoreCase(asShapeObject1))
                    fillShapeFile2(shpFile, def,isCreate, progress);
                else
                    fillShapeFile(shpFile, def,isCreate, progress);
            }
        }
        finally
        {
            if (shpFile!=null)
                shpFile.close();
        }
    }



    protected void fillShapeFile2(PolygonShpFile shpFile, PathDef defPlus, boolean isCreate, IProgressObserver progress) throws Exception
    {
        if (isConvert())
        {
            try {
                this.setAf();

            } catch (Exception e) {
                throw new IllegalArgumentException("Can't create converter with tab file: "+this.getfTabName(),e);
            }
        }
        else
            this.setAf(null);

        AffineTransform af = getAf();
        if (af==null)
        {
            af = AffineTransform.getTranslateInstance(0, 0);
            af.setToScale(1,-1);

        }

        if (isCreate)
            shpFile.create();
        int id=0;

        id=fillShapeFile22(shpFile,defPlus, af,id,progress);
        System.out.println("id = " + id);
    }


    protected int fillShapeFile22(PolygonShpFile shpFile, PathDef defPlus, AffineTransform af,int id, IProgressObserver progress) throws Exception
    {
        while (defPlus!=null)
        {
            if (defPlus.getSign()=='-')
                throw new IllegalArgumentException();
            id=fillShapeFileWithHoles(shpFile, defPlus,af,id,progress);
            defPlus=defPlus.getSibling();
        }
        return id;
    }


    protected int fillShapeFileWithHoles(PolygonShpFile shpFile, final PathDef defPlus, AffineTransform af,int id, IProgressObserver progress) throws Exception
    {
        if (defPlus==null)
            return id;

        id=add2ShapeFileWithSibling(shpFile,defPlus,af,id,progress);

        PathDef defMinus = defPlus.getFirstChild();
        while (defMinus!=null)
        {
            if (defMinus.getSign()=='+')
                throw new IllegalArgumentException();
            id=fillShapeFile22(shpFile, defMinus.getFirstChild(), af, id,progress);
            defMinus=defMinus.getSibling();
        }
        return id;
    }



    protected  int add2ShapeFileWithSibling(PolygonShpFile shpFile, final PathDef defPlus, AffineTransform af,int id,IProgressObserver progress) throws Exception
    {
        if (defPlus.getSign()=='-')
            throw new IllegalArgumentException();


        Vector<Vector<ShpPoint>> pgons = new Vector<Vector<ShpPoint>>();

        Pair<Shape, PathDef> pathPair = poTraceUtils.getPath(defPlus);
        add2Vector(pgons, pathPair.first,af, progress);

        PathDef defMinus = defPlus.getFirstChild();
        while (defMinus!=null)
        {
            if (defMinus.getSign()=='+')
                throw new IllegalArgumentException();
            Pair<Shape, PathDef> sibPair = poTraceUtils.getPath(defMinus);
            add2Vector(pgons, sibPair.first,af, progress);
            defMinus=defMinus.getSibling();
        }

        Pair<Map<String, Integer>, Map<String, Double>> iAttr = createIAttr(pathPair.second, pgons, id);
        shpFile.addPolygon2(pgons, iAttr.first,iAttr.second,createSAttr());
        return id+1;
    }


    protected void fillShapeFile3(PolygonShpFile shpFile, PathDef defPlus, boolean isCreate, IProgressObserver progress) throws Exception
    {
        if (isConvert())
        {
            try {
                this.setAf();

            } catch (Exception e) {
                throw new IllegalArgumentException("Can't create converter with tab file: "+this.getfTabName(),e);
            }
        }
        else
            this.setAf(null);

        AffineTransform af = getAf();
        if (af==null)
        {
            af = AffineTransform.getTranslateInstance(0, 0);
            af.setToScale(1,-1);

        }
        if (isCreate)
            shpFile.create();


        int id=0;

        id=fillShapeFile33(shpFile, null, defPlus, af, id,progress);
        System.out.println("id = " + id);
    }


    protected int fillShapeFile33(PolygonShpFile shpFile,Vector<Vector<ShpPoint>> pgons, PathDef defPlus, AffineTransform af,int id, IProgressObserver progress) throws Exception
    {
        boolean isFirstLevel=(pgons==null);
        if (isFirstLevel)
            pgons = new Vector<Vector<ShpPoint>>();

        while (defPlus!=null)
        {
            if (defPlus.getSign()=='-')
                throw new IllegalArgumentException();
            id=fillShapeFileWithHoles3(pgons, defPlus,af,id,progress);
            if (isFirstLevel)
            {
                Pair<Map<String, Integer>, Map<String, Double>> iAttr = createIAttr(defPlus, pgons, id);
                shpFile.addPolygon2(pgons, iAttr.first, iAttr.second, createSAttr());
                pgons.clear();
            }
            defPlus=defPlus.getSibling();
        }
        return id;
    }

    protected int fillShapeFileWithHoles3(Vector<Vector<ShpPoint>> pgons, final PathDef defPlus, AffineTransform af,int id,IProgressObserver progress) throws Exception
    {
        if (defPlus==null)
            return id;

        id=add2ShapeFileWithSibling3(pgons, defPlus, af, id,progress);

        PathDef defMinus = defPlus.getFirstChild();
        while (defMinus!=null)
        {
            if (defMinus.getSign()=='+')
                throw new IllegalArgumentException();
            id=fillShapeFile33(null,pgons, defMinus.getFirstChild(), af, id,progress);
            defMinus=defMinus.getSibling();
        }
        return id;
    }


    protected  int add2ShapeFileWithSibling3(Vector<Vector<ShpPoint>> pgons, final PathDef defPlus, AffineTransform af,int id,IProgressObserver progress) throws Exception
    {
        if (defPlus.getSign()=='-')
            throw new IllegalArgumentException();

        Pair<Shape, PathDef> pathPair = poTraceUtils.getPath(defPlus);
        add2Vector(pgons, pathPair.first,af, progress);

        PathDef defMinus = defPlus.getFirstChild();
        while (defMinus!=null)
        {
            if (defMinus.getSign()=='+')
                throw new IllegalArgumentException();
            Pair<Shape, PathDef> sibPair = poTraceUtils.getPath(defMinus);
            add2Vector(pgons, sibPair.first,af, progress);
            id++;
            defMinus=defMinus.getSibling();
        }
        return id+1;
    }




    protected Map<String, String>  createSAttr()
    {
        Map<String, String> numVals = new HashMap<String, String>();
        if (name2attribute.first!=null && name2attribute.second!=null)
            numVals.put(name2attribute.first, name2attribute.second);
        return numVals;
    }


    private Pair<Map<String, Integer>,Map<String, Double>> createIAttr(PathDef path, Vector<Vector<ShpPoint>> pgons, int id)
    {
        int pntCnt=0;

        for (int i = 0; i < pgons.size(); i++) {
            Vector<ShpPoint> shpPoints = pgons.elementAt(i);
            if (shpPoints!=null)
                pntCnt+= shpPoints.size();
        }

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
        numVals.put(NAME_POLY_CNT, pgons.size());
        numVals.put(NAME_POINT_CNT, pntCnt);

        if (resolution !=0)
            doubleVals.put(totalSquareName,totalArea);

        return new Pair<Map<String, Integer>, Map<String, Double>>(numVals,doubleVals);
    }

    private void add2Vector(Vector<Vector<ShpPoint>> pgons, Shape shape, AffineTransform af, IProgressObserver progress) {


        if (progress!=null)
            progress.showProgress(1);

        PathIterator pathItr = shape.getPathIterator(af, getPixelFlatness());
        double[] coords = new double[6];
        ShpPgon pgon =null;
        int i =0;

        int isz=pgons.size();

        try {
            while (!pathItr.isDone())
            {
                int segType = pathItr.currentSegment(coords);
                pgon=processPathPart(segType, coords,pgon,pgons);

                if (isz!=pgons.size())
                {
                    isz=pgons.size();
                    i+=pgon.size();
                }

                pathItr.next();
            }

        } catch (OutOfMemoryError e) {
            System.out.println("sz:"+i);
        }
    }

    private double getPixelFlatness()
    {
        double flatness=DEF_PIXEL_FLATNESS;
        if (resolution>0)
        {
            flatness=this.flatness/resolution;
            if (Double.isInfinite(flatness) || Double.isNaN(flatness) || flatness<DEF_PIXEL_FLATNESS)
                flatness=DEF_PIXEL_FLATNESS;
        }
        return flatness;
    }


    public void fillShapeFile(PolygonShpFile shpFile, final PathDef def, boolean isCreate, final IProgressObserver progress) throws Exception
    {
        Iterator<ShapeObject> it = new Iterator<ShapeObject>()
        {
            private PathDef current=def;

            public boolean hasNext() {
                return current!=null;
            }


            public ShapeObject next()
            {
                PathDef _current=current;
                current=current.getNext();

                if (progress!=null)
                    progress.showProgress(1);

                Pair<Shape, PathDef> path = poTraceUtils.getPath(_current);

//                Map<String, Integer> numVals = new HashMap<String, Integer>();
//                if (path.getValue().getSign()=='+')
//                    numVals.put(colorName, blackColor);
//                else
//                    numVals.put(colorName, whiteColor);
//                numVals.put(NAME_AREA,path.second.getArea());
//                numVals.put(NAME_SIGN, path.getValue().getSign());

                Vector<Vector<ShpPoint>> pgons = new Vector<Vector<ShpPoint>>();
                Pair<Map<String, Integer>, Map<String, Double>> iAttr = createIAttr(path.second, pgons,0);
                return new ShapeObject(path.getKey(), iAttr.first,iAttr.second,createSAttr());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

        if (isConvert())
        {
            try {
                this.setAf();

            } catch (Exception e) {
                throw new IllegalArgumentException("Can't create converter with tab file: "+this.getfTabName(),e);
            }
        }
        else
            this.setAf(null);
        fillShapeFile(shpFile, it,isCreate);
    }

    public void setAf() throws IOException
    {
        TabReader trd = new TabReader(this.getfTabName());
        Text.sout("\n+++++++++++++\nTAB file read from \"" + trd.getTabPath() + "\"");
        Text.sout( "TAB info:" + trd.toString() );
        if ( Files.fileExists(trd.getImagePath()) )
        {
            Text.sout( "File with designated raster image exists. Not checked to be raster really" );
        }
        else
        {
            Text.serr( "Failure to detect image file" );
        }

/*+++ SYG 03-FEB-2014: Now this check must be removed as new TAB mode is created after ADEL_Pre
        if ( !trd.isTABInGeoprojection() )
        {
            Text.serr( "TAB can't be used for translations" );
            return;
        }
*/

        // check for reverse affines coefficients
        Text.sout( "Test reverse affine coefficients receiving" );
        final int equationCnt = trd.pntCount();
        double[][] matA_x = new double[equationCnt][3];
        double[][] matA_y = new double[equationCnt][3];
        double[] vecB_x = new double[equationCnt];
        double[] vecB_y = new double[equationCnt];
        double[] vecX_x = new double[3];
        double[] vecX_y = new double[3];
        for ( int m = 0; m < equationCnt; m++ )
        {
            final Point2D projPnt = trd.getProjectedPoint( m );
            final Point imgPnt = trd.getImagePoint( m );
            matA_x[ m ][ 0 ] = matA_y[ m ][ 0 ] = imgPnt.x;
            matA_x[ m ][ 1 ] = matA_y[ m ][ 1 ] = imgPnt.y;
            matA_x[ m ][ 2 ] = matA_y[ m ][ 2 ] = 1.0d;
            vecB_x[ m ] = projPnt.getX();
            vecB_y[ m ] = projPnt.getY();
            //Text.sout( String.format( "TAB: img point %s, prj point %s ", imgPnt.toString(), projPnt.toString()  ) );
        }
        TabReader.printMat(matA_x, "Matrix A(x)");
        TabReader.printMat(vecB_x, "Vector B(x)");
        TabReader.printMat(matA_y, "Matrix A(y)");
        TabReader.printMat(vecB_y, "Vector B(y)");

        TabSolverJAMA ts = new TabSolverJAMA();
        double[] atX = ts.solve( matA_x, vecB_x, vecX_x );
        double[] atY = ts.solve( matA_y, vecB_y, vecX_y );
        if ( atX == null || atY == null)
        {
            Text.serr("Can't solve 1st or 2nd matrix: singularity found");
            return;
        }
        else
            Text.sout( String.format( Locale.ENGLISH, "AT:\nX= %14.10f * x + %14.10f * y + %14.10f\nY= %14.10f * x + %14.10f * y + %14.10f", atX[ 0 ], atX[ 1 ], atX[ 2 ], atY[ 0], atY[ 1 ], atY[ 2 ] ) );


    // create main correct transformation
        double[] matrix = new double[6];
        matrix[0]=atX[0];
        matrix[1]=atY[0];

        matrix[2]=atX[1];
        matrix[3]=atY[1];

        matrix[4]=atX[2];
        matrix[5]=atY[2];

        this.setAf(new AffineTransform(matrix));

    }

}
