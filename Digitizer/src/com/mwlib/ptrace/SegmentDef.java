package com.mwlib.ptrace;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 22.12.13
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */
public class SegmentDef
{
    public static final int POTRACE_CURVETO=0;
    public static final int POTRACE_CORNER=1;

    public int cmd=POTRACE_CURVETO; /* tag[n]: POTRACE_CURVETO=0 or POTRACE_CORNER=1 */
    public double[] xs= new double[3]; //
    public double[] ys= new double[3];//

    public void setCmdCurveTO(){ cmd=POTRACE_CURVETO;}
    public void setCmdMoveTO(){ cmd=POTRACE_CORNER;}

    public void setXY(int ix,double xs,double ys){this.xs[ix]=xs;this.ys[ix]=ys;}
    public double[] getXY(int ix){return new double[]{this.xs[ix],this.ys[ix]};}

    public double[] getX(){return xs;}
    public double[] getY(){return ys;}

    public int getCmd(){return cmd;}
}



