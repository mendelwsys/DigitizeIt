package com.mwlib.ptrace;

import javax.swing.text.Segment;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 22.12.13
 * Time: 18:31
 * To change this template use File | Settings | File Templates.
 */
public class CurveDef
{
    public SegmentDef[] getSegmentDefs() {
        return segmentDefs;
    }

    public double [][][] getGeom()
    {
        if (segmentDefs==null)
            return new double[2][][];

        double [][] rvX=new double[segmentDefs.length][];
        double [][] rvY=new double[segmentDefs.length][];

        for (int i = 0; i < segmentDefs.length; i++) {
            SegmentDef segmentDef = segmentDefs[i];
            rvX[i]=segmentDef.getX();
            rvY[i]=segmentDef.getY();
        }
        return new double[][][]{rvX,rvY};
    }

    private SegmentDef[] segmentDefs;

    public CurveDef(){}

    public CurveDef(SegmentDef[] segmentDefs)
    {
        this.segmentDefs = segmentDefs;
        for (int i = 0; i < segmentDefs.length; i++) {
            segmentDefs[i]= new SegmentDef();
        }
    }

    public SegmentDef[] createSegments(int segCnt)
    {
        this.segmentDefs=new SegmentDef[segCnt];
        for (int i = 0; i < segmentDefs.length; i++) {
            segmentDefs[i]= new SegmentDef();
        }
      return this.segmentDefs;
    }

    public static CurveDef createCurveDef( int segCnt)
    {
        return new CurveDef(new SegmentDef[segCnt]);
    }
}
