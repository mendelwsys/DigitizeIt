package com.mwlib.app.geom;

import com.mwlib.ptrace.PathDef;
import ru.ts.gisutils.algs.common.MPoint;
import ru.ts.gisutils.algs.common.MRect;
import ru.ts.stream.ISerializer;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.geom.IBaseGisObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 03.01.14
 * Time: 14:35
 * Wrapper for digitized path object
 */
public class DigitizedWrapperGeom implements IBaseGisObject
//        ,
//        ISerializer

{
    public static final String PATHDEF = "PATHDEF";
    private String curveid;

    public PathDef getPathDef()
    {
        return pathDef;
    }

    private PathDef pathDef;
    public DigitizedWrapperGeom(String curveid, PathDef pathDef)
    {
        this.curveid = curveid;
        this.pathDef=pathDef;
    }

    public IAttrs getObjAttrs() {
        return null;
    }

    public MRect getMBB(MRect boundrect)
    {
        return new MRect(new MPoint(0,0),new MPoint(pathDef.getWidth(),pathDef.getHeight()));
    }

    public String getGeotype() {
        return PATHDEF;
    }

    public double[][][] getRawGeometry()
    {
        if (pathDef!=null)
            return pathDef.getCurve().getGeom();
        return new double[2][][];
    }

    public String getCurveId() {
        return curveid;
    }

    public int getDimensions() {
        return 2;
    }

    public void setInstance(IBaseGisObject _curve) throws Exception {
        throw new UnsupportedOperationException();
    }
}
