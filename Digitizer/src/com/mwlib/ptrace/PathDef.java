package com.mwlib.ptrace;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 22.12.13
 * Time: 18:51
 * Результат оцифровки
 */
public class PathDef
{
    private Param param;

    private int height;
    private int width;

    private int area;                         /* area of the bitmap path */
    private int sign;                         /* '+' or '-', depending on orientation */


    private PathDef next; //Next def
    private PathDef firstChild;
    private PathDef sibling;



    private CurveDef curve; //Current curve


    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    public boolean isDebug() {
        return param != null && param.bPrint;
    }

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }



    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
        if (observer!=null)
            observer.setTraceProgress(this);
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public CurveDef getCurve() {
        return curve;
    }

    public void setCurve(CurveDef curve) {
        this.curve = curve;
    }




    private long childCode=0;
    private long siblingCode=0;

    public PathDef(Param param)
    {
        this.param = param;
        this.curve= new CurveDef();
    }

    public PathDef(Param param,long childCode,long siblingCode)
    {
        this.param = param;
        this.curve= new CurveDef();
        this.childCode=childCode;
        this.siblingCode=siblingCode;
    }

    public PathDef(int segCnt, Param param)
    {
        this.param = param;
        this.curve= CurveDef.createCurveDef(segCnt);
    }


//    public static PathDef createPathDef(int segCnt)
//    {
//        return new PathDef(segCnt, param);
//    }

    public SegmentDef[] setSegmentCnt(int segCnt)
    {
        return this.curve.createSegments(segCnt);
    }


    public void setSegmentCurveTO(int ix)
    {
        setSegmentCmd(ix, SegmentDef.POTRACE_CURVETO);
    }

    public void setSegmentCorner(int ix)
    {
        setSegmentCmd(ix, SegmentDef.POTRACE_CORNER);
    }

    public void setSegmentCmd(int ix, int cmd)
    {
        SegmentDef segmentDef = this.curve.getSegmentDefs()[ix];
        if (cmd==SegmentDef.POTRACE_CORNER)
            segmentDef.setCmdMoveTO();
        else if (cmd==SegmentDef.POTRACE_CURVETO)
            segmentDef.setCmdCurveTO();
    }


    public void setSegmentPoint(int ix,int ixp,double xs,double ys)
    {
        SegmentDef segmentDef = this.curve.getSegmentDefs()[ix];
        segmentDef.setXY(ixp, xs, ys);
    }


    public PathDef getNext() {
        return next;
    }

    public PathDef getFirstChild() {
        return firstChild;
    }

    public PathDef getSibling() {
        return sibling;
    }

    public void setNext(PathDef next) {
        this.next = next;
    }

    static private Map<Long,PathDef> mp2def=new HashMap<Long, PathDef>();

    public static IProgressObserver getObserver() {
        return observer;
    }

    static void setObserver(IProgressObserver observer)
    {
        PathDef.observer = observer;
    }

    static private IProgressObserver observer;
//    static private int totalObjCnt=0;

    public PathDef createNextL(long thisCode,long childCode,long siblingCode)
    {
        next = new PathDef(param,childCode,siblingCode);
        mp2def.put(thisCode,next);
        return next;
    }

//    public static int getTotalObjCnt() {
//        return totalObjCnt;
//    }

    public void reInitAllByCodes()
    {
        PathDef def = this;
        while (def!=null)
        {
            if (def.childCode!=0)
                def.setFirstChild(mp2def.get(def.childCode));
            if (def.siblingCode!=0)
                def.setSibling(mp2def.get(def.siblingCode));
            def=def.next;
        }
//        totalObjCnt=mp2def.size();
        mp2def.clear();
    }

    public PathDef createNext()
    {
        return (next = new PathDef(param));
    }

    public void setFirstChild(PathDef firstChild) {
        this.firstChild = firstChild;
    }

    public void setSibling(PathDef sibling) {
        this.sibling = sibling;
    }




}
