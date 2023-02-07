package com.mwlib.app.plugins.shp;

import com.mwlib.ptrace.PathDef;
import com.mwlib.ptrace.Utils;
import ru.ts.common.misc.Text;
import ru.ts.utils.Files;
import ru.ts.utils.data.Pair;
import shp.core.PolygonShpFile;
import su.gis.utils.shp.ShpFactory;
import su.utils.tab.TabReader;
import su.utils.tab.TabSolverJAMA;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 04.01.14
 * Time: 20:32
 * Convert potrace object to shape file
 */
public class PoTrace2Shp_bu extends ShapeObj2Shp
{
    public static final String NAME_AREA = "Area";
    public static final String NAME_SIGN = "Sign";


    public String[] getNumNames()
    {
        return new String[]{ NAME_ID, NAME_POLY_CNT, NAME_POINT_CNT, DEF_NAME_COLOR,NAME_AREA,NAME_SIGN};
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

    private int blackColor =0xFF000000;
    private int whiteColor =0xFFFFFFFF;

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


    public void createShapeFile(String shpName, PathDef def) throws Exception
    {
        fillShapeFile(ShpFactory.getPolygonMaker(shpName, getNumNames(),getDoubleNames(),getTxtNames()), def);
    }

    public void fillShapeFile(PolygonShpFile shpFile, final PathDef def) throws Exception
    {
        final Utils u= new Utils();
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


                Pair<Shape, PathDef> path = u.getPath(_current);

                Map<String, Integer> numVals = new HashMap<String, Integer>();
                if (path.getValue().getSign()=='+')
                    numVals.put(DEF_NAME_COLOR, blackColor);
                else
                    numVals.put(DEF_NAME_COLOR, whiteColor);
                numVals.put(NAME_AREA,path.getValue().getArea());
                numVals.put(NAME_SIGN, path.getValue().getSign());

                return new ShapeObject(path.getKey(),numVals,new HashMap<String, Double>(),new HashMap<String, String>());
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
        fillShapeFile(shpFile, it,true);
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
        if ( !trd.isTABInGeoprojection() )
        {
            Text.serr( "TAB can't be used for translations" );
            return;
        }
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
        matrix[1]=atX[1];

        matrix[2]=atY[0];
        matrix[3]=atY[1];

        matrix[4]=atX[2];
        matrix[5]=atY[2];

        this.setAf(new AffineTransform(matrix));

    }

}
