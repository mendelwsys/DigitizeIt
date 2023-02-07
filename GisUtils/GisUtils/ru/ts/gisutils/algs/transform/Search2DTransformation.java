package ru.ts.gisutils.algs.transform;

import ru.ts.gisutils.geometry.IGetXY;
import ru.ts.gisutils.geometry.IXY;
import ru.ts.gisutils.geometry.XY;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: YUGL
 * Date: 21.10.2008
 * Time: 17:12:06
 * Calculation of a polynomial transformation parameters, using some training data.
 * Training data contains a number of pairs of 2D points. Every pair consist of
 * an original point and a transformed one. Supposing that the training data define
 * some 'ideal' transformation, we are searching the polynomial transformation that
 * is close to the ideal one and that is not too complex at the same time.  
 */
public class Search2DTransformation
{
  static public boolean _trace = true;
//  static public boolean _trace = false;

  //++ -------- ITrainingElem --------------
  public interface ITrainingElem
  {
    // original point
    IGetXY  origin();
    // transformed point
    IGetXY  result();

    // base implementation
    static public class Base implements ITrainingElem
    {
      protected IGetXY  _origin;
      protected IGetXY  _result;
      Base(){}
      public Base(IGetXY origin, IGetXY result){
        _origin = origin;
        _result = result;
      }
      public IGetXY  origin() { return _origin; }
      public IGetXY  result() { return _result; }
    }
  }
  //-- -------- ITrainingElem --------------

  //++ -------- ITransformer --------------
 public interface ITransformer
  {
    // return number of transformation parameters
    int getNumberOfParams();
    // return a short designation for a parameter
    String getParamName(int prmIndex);

    // form a matrix with 2 rows defining 2 linear equations that corresponds to
    // the element of training data (an original point and a transformed one).
    // their length is equal to n+1 = getNumberOfParams()+1.
    // every row represents a linear equation as follows:
    // a1*x1 + a2*x2 + ... + an*xn + a0 = 0   (ai kept in row[i-1], a0 - in row[n]).
    double[][] formEquations(ITrainingElem elem);
    void formEquations(ITrainingElem elem, double[][] eqrows);

    // set values for transformation parameters
    void setParameters(double[] decision);
    // get values of transformation parameters
    double[] getParameters();

    // make transformation of point for current parameters values.
    IXY transform(IGetXY origin);
    void transform(IGetXY origin, IXY result);

    // calculate the linear expression (it should be consistent with result of
    // the formEquations method) for current parameters values.
    double calculate(double[] eqrow);
    // calculate the squared error for the givem training element (2 equations)
    // and current parameters values.
    double calculateError2(ITrainingElem elem);
    // calculate the mean value of squared error for the givem training element
    // (2 equations) and current parameters values.
    double calculateMeanError2(ITrainingElem elem);

//    // form a matrix defining a system of linear equations that corresponds to
//    // the training data (a list of original points and a list of transformed ones)
//    double[][] formMatrix(List<ITrainingElem> data);

    //++ -------- base implementation --------------
    static public class Base implements ITransformer
    {
      // number of transformation parameters
      protected int _nparams;
      protected void setNumberOfParams(int nvars) {
        _nparams = nvars;
        if (_nparams < 0) _nparams = 0;   // an absence of transformation (x=x, y=y).
      }
      //-- array of variables
      protected double[] _params;

      // constructors
      protected Base(){ }
      Base(int nparams){ init(nparams); }

      protected void init(int nparams) { setNumberOfParams(nparams); _params = null; }

      // return transformation name
      public String toString() {
        return formName("ITransformer.Base");
      }
      protected String formName(String className) {
        return className + "(" + _nparams + ")";
      }
      //++ ITransformer members
      public int getNumberOfParams() { return _nparams; }
      public String getParamName(int prmIndex) { return "v" + (prmIndex); }

      public double[][] formEquations(ITrainingElem elem) {
        double[][] eqrows = buildEqRows();
        formEquations(elem, eqrows);
        return eqrows;
      }
      public void formEquations(ITrainingElem elem, double[][] eqrows) {
        if (eqrows[0] == null) eqrows[0] = buildEqRow();
        if (eqrows[1] == null) eqrows[1] = buildEqRow();
        clearEqRows(eqrows);
        formEquations(elem.origin(), eqrows);
        eqrows[0][_nparams] = -elem.result().getX();
        eqrows[1][_nparams] = -elem.result().getY();
      }

      public void setParameters(double[] decision) { _params = decision; }
      public double[] getParameters() { return _params; }

      public IXY transform(IGetXY origin) {
        IXY result = new XY();
        transform(origin, result);
        return result;
      }
      public void transform(IGetXY origin, IXY result) {
        if (_nparams == 0 || _params == null) {
          result.setXY(origin.getX(), origin.getY());
          return;
        }
        double[][] eqrows = buildEqRows();
        clearEqRows(eqrows);
        formEquations(origin, eqrows);
        double x = calculate(eqrows[0]);
        double y = calculate(eqrows[1]);
        result.setXY(x, y);
      }

      public double calculate(double[] eqrow) {
        return Algebra.calcLinearEqErr(eqrow, _params);
      }
      public double calculateError2(ITrainingElem elem) {
        double[][] eqrows = formEquations(elem);
        double err = calculate(eqrows[0]);
        double err2 = err * err;
        err = calculate(eqrows[1]);
        err2 += err * err;
        return err2;
      }
      public double calculateMeanError2(ITrainingElem elem) {
        double err2 = calculateError2(elem);
        return err2 / 2;
      }
      //-- ITransformer members

      // implementation
      // build rows
      protected double[][] buildEqRows() {
        double[][] eqrows = new double[2][];
        eqrows[0] = buildEqRow();
        eqrows[1] = buildEqRow();
        return eqrows;
      }
      protected double[] buildEqRow() {
        return new double[_nparams +1];
      }
      // clear rows
      protected void clearEqRows(double[][] eqrows) {
        for (int i=0; i<= _nparams; i++) {
          eqrows[0][i] = 0;
          eqrows[1][i] = 0;
        }
      }

      //++ to override
      protected void formEquations(IGetXY origin, double[][] eqrows) {
      }
      //-- to override
    }
    //-- -------- base implementation --------------

    //++ -------- another base implementation --------------
    // Some parameters may be viewed as X- and some as Y-ones
    static public class XYParams extends Base //implements ITransformer
    {
      //++ number of transformation parameters
      protected int _nparamsX, _nparamsY;
      protected void setNumberOfParams(int nparamsX, int nparamsY) {
        _nparamsX = nparamsX;
        _nparamsY = nparamsY;
        if (_nparamsX < 0) _nparamsX = 0;   // (0, 0) - special case, it corresponds to
        if (_nparamsY < 0) _nparamsY = 0;   // an absence of a transformation (x=x, y=y).
        _nparams = _nparamsX + _nparamsY;
      }
      public int getNumberOfXParams() { return _nparamsX; }
      public int getNumberOfYParams() { return _nparamsY; }
      //-- number of transformation parameters

      // constructors
      protected XYParams(){ }
      XYParams(int nvarsX, int nvarsY){ init(nvarsX, nvarsY); }

      protected void init(int nvarsX, int nvarsY) { super.init(0); setNumberOfParams(nvarsX, nvarsY); }

      // return transformation name
      public String toString() {
//        return formName(this.getClass().getName());
        return formName("ITransformer.XYParams");
      }
      protected String formName(String className) {
        return className + "(" + _nparamsX + "," + _nparamsY + ")";
      }
      //++ ITransformer members
      public String getParamName(int prmIndex) {
        if (prmIndex < _nparamsX)
          return "vx" + prmIndex;
        prmIndex -= _nparamsX;
        return "vy" + prmIndex;
      }
      // form equations independently
      protected void formEquations(IGetXY origin, double[][] eqrows) {
        formAbsoluteTerm(origin, eqrows, 0);
        formEquationX(origin, eqrows[0]);
        formEquationY(origin, eqrows[1]);
      }
      //-- ITransformer members

      // implementation for independent x- and y-
      // an absolute term
      protected void formAbsoluteTerm(IGetXY origin, double[][] eqrows, int k0) {
        eqrows[0][k0] = 1;
        eqrows[1][_nparamsX +k0] = 1;
      }
      // usual linear part
      protected int formLinearPart(IGetXY origin, double[][] eqrows, int k0) {
        eqrows[0][k0] = origin.getX();
        eqrows[0][k0+1] = origin.getY();
        eqrows[1][_nparamsX +k0] = origin.getX();
        eqrows[1][_nparamsX +k0+1] = origin.getY();
        return k0+2;
      }
      protected void formEquationX(IGetXY origin, double[] eqrow) {
        for (int i=1; i< _nparamsX; i++)
          eqrow[0 + i] = formMemberX(i, origin);
      }
      protected void formEquationY(IGetXY origin, double[] eqrow) {
        for (int i=1; i< _nparamsY; i++)
          eqrow[_nparamsX + i] = formMemberY(i, origin);
      }

      //++ to override
      protected double formMemberX(int idx, IGetXY xy) {
        return 0;
      }
      protected double formMemberY(int idx, IGetXY xy) {
        return 0;
      }
      //-- to override
    }
    //-- -------- base implementation for independet X and Y --------------
  }
  //-- -------- ITransformer --------------

  //++ -------- PolynomialTransformer --------------
  // used 2 (for x and for y) polygons
  static public class PolynomialTransformer extends ITransformer.XYParams
  {
    protected PolynomialTransformer(){ }
    PolynomialTransformer(int nparamsX, int nparamsY){ super(nparamsX, nparamsY); }

    // useful to get an instance (instead of constructor)
    static private PolynomialTransformer _identic;
    static { _identic = new PolynomialTransformer(0, 0); }
    static public PolynomialTransformer identic() { return _identic; }
    static public PolynomialTransformer meanresult() { return new PolynomialTransformer(1, 1); }
    static public PolynomialTransformer linear() { return new PolynomialTransformer(3, 3); }
    static public PolynomialTransformer quadratic() { return new PolynomialTransformer(6, 6); }
    static public PolynomialTransformer cubic() { return new PolynomialTransformer(10, 10); }
    static public PolynomialTransformer degree(int n) {
      if (n < 0) n = 0;
      int nv = (n+2)*(n+1) / 2;
      return new PolynomialTransformer(nv, nv);
    }

    public String toString() {
//        return formName(this.getClass().getName());
      return formName("PolynomialTransformer");
    }
    //++ overriden
    protected double formMemberX(int idx, IGetXY xy) {
      return formMember(idx, xy);
    }
    protected double formMemberY(int idx, IGetXY xy) {
      return formMember(idx, xy);
    }
    //-- overriden

    // implementation
    protected double formMember(int idx, IGetXY xy) {
      if (idx == 0) return 1;
      idx--;
      int degree = 1;  // 2 members: x, y
      while (idx > degree) {
        degree++;       // next degree needs one more member
        idx -= degree;  // f.i., degree=2 needs 3 members: x2, xy, y2
      }
      // calculate a member
      double x = xy.getX();
      double member = 1;
      for (int i=0; i<(degree-idx); i++) member *= x;
      double y = xy.getY();
      for (int i=0; i<idx; i++) member *= y;
      return member;
    }

  }
  //-- -------- PolynomialTransformer --------------

  //++ -------- MixedPolymixedTransformer --------------
  // used a linear transformation and some complex members (monomials and roots)
  static public class MixedPolymixedTransformer extends ITransformer.XYParams
  {
    // transformation degree
    protected int _ndegree;
    // to make complex calculations
    protected Complex _z;
    protected Complex _member;

    protected MixedPolymixedTransformer(){ }
    // ndegree should be >= 0
    MixedPolymixedTransformer(int ndegree){ init(ndegree); }

    protected void init(int ndegree) {
      super.init(0);
      _ndegree = ndegree;
      if (_ndegree < 0) _ndegree = 0;
      setNumberOfParams();
      // to make complex calculations
      _z = new Complex();
      _member = new Complex();
    }
    protected void setNumberOfParams() {
      int nv = 1 + 2 * _ndegree;
      setNumberOfParams(nv, nv);
    }

    public String toString() {
//        return formName(this.getClass().getName());
      return formName("MixedPolymixedTransformer");
    }
    protected String formName(String className) {
      return className + "(" + _ndegree + ")";
    }

    // implementation for complex equations with real x- and imaginary y-
    protected void formEquations(IGetXY origin, double[][] eqrows) {
      formAbsoluteTerm(origin, eqrows, 0);
      if (_ndegree < 1) return;
      int k0 = formLinearPart(origin, eqrows, 1);
      if (_ndegree < 2) return;
      k0 = formHighDegreePart(origin, eqrows, k0);
      formRootDegreePart(origin, eqrows, k0);
    }
    protected int formLinearPart(IGetXY origin, double[][] eqrows, int k0) {
      return formRLinearPart(origin, eqrows, k0);
    }
    protected int formHighDegreePart(IGetXY origin, double[][] eqrows, int k0) {
      return formCHighDegreePart(origin, eqrows, k0);
    }
    protected int formRootDegreePart(IGetXY origin, double[][] eqrows, int k0) {
      return formCRootDegreePart(origin, eqrows, k0);
    }

    // usual linear part
    protected int formRLinearPart(IGetXY origin, double[][] eqrows, int k0) {
      return super.formLinearPart(origin, eqrows, k0);
    }
    // complex linear part (return number of members)
    protected int formCLinearPart(IGetXY origin, double[][] eqrows, int k0) {
      eqrows[0][k0] = origin.getX();
      eqrows[0][_nparamsX +k0] = -origin.getY();
      eqrows[1][k0] = origin.getY();
      eqrows[1][_nparamsX +k0] = origin.getX();
      return k0+1;
    }
    protected int formCHighDegreePart(IGetXY origin, double[][] eqrows, int k0) {
      _z.set(origin.getX(), origin.getY());
      _member.set(_z);
      int k2 = k0 - 2;
      for (int i=2; i<=_ndegree; i++) {
        _member.mul(_z);
        eqrows[0][k2+i] = _member.x;
        eqrows[0][_nparamsX +k2+i] = -_member.y;
        eqrows[1][k2+i] = _member.y;
        eqrows[1][_nparamsX +k2+i] = _member.x;
      }
      return k0+(_ndegree-1);
    }
    protected int formCRootDegreePart(IGetXY origin, double[][] eqrows, int k0) {
      _z.set(origin.getX(), origin.getY());
      int k2 = k0 - 2;
      for (int i=2; i<=_ndegree; i++) {
        Complex.pow(_z, 1./i, _member);
        eqrows[0][k2+i] = _member.x;
        eqrows[0][_nparamsX +k2+i] = -_member.y;
        eqrows[1][k2+i] = _member.y;
        eqrows[1][_nparamsX +k2+i] = _member.x;
      }
      return k0+(_ndegree-1);
    }
  }
  //-- -------- MixedPolymixedTransformer --------------

  //++ -------- ComplexPolynomialTransformer --------------
  // used a complex polygon
  static public class ComplexPolynomialTransformer extends MixedPolymixedTransformer
  {
//    ComplexPolynomialTransformer(){ super(); }
    ComplexPolynomialTransformer(int ndegree){ init(ndegree); }

    protected void setNumberOfParams() {
      int nv = 1 + _ndegree;
      setNumberOfParams(nv, nv);
    }

    public String toString() {
      return formName("ComplexPolynomialTransformer");
    }

    protected int formLinearPart(IGetXY origin, double[][] eqrows, int k0) {
      return formCLinearPart(origin, eqrows, k0);
    }
    protected int formHighDegreePart(IGetXY origin, double[][] eqrows, int k0) {
      return formCHighDegreePart(origin, eqrows, k0);
    }
    protected int formRootDegreePart(IGetXY origin, double[][] eqrows, int k0) {
      return k0;
//      formCRootDegreePart(origin, eqrows, k0);
    }
  }
  //-- -------- ComplexPolynomialTransformer --------------

//  //++ -------- ComplexPolymixedTransformer --------------
//  // used a complex polygon and some complex roots as approximation members
//  static public class ComplexPolymixedTransformer extends MixedPolymixedTransformer
//  {
//    ComplexPolymixedTransformer(){ super(); }
//    // ndegree should be >= 0
//    ComplexPolymixedTransformer(int ndegree){ init(ndegree); }
//
//    protected void setNumberOfParams() {
//      int nv = 0;
//      if (_ndegree == 0) nv = 1;
//      else  nv = 2 * _ndegree;
//      setNumberOfParams(nv, nv);
//    }
//
//    public String toString() {
//      return formName("ComplexPolymixedTransformer");
//    }
//
//    protected int formLinearPart(IGetXY origin, double[][] eqrows, int k0) {
//      return formCLinearPart(origin, eqrows, k0);
//    }
//    protected int formHighDegreePart(IGetXY origin, double[][] eqrows, int k0) {
//      return formCHighDegreePart(origin, eqrows, k0);
//    }
//    protected int formRootDegreePart(IGetXY origin, double[][] eqrows, int k0) {
//      return formCRootDegreePart(origin, eqrows, k0);
//    }
//  }
//  //-- -------- ComplexPolymixedTransformer --------------

  //++ -------- MixedPolyRootsTransformer --------------
  // used a linear transformation and some complex root members
  static public class MixedPolynomialTransformer extends MixedPolymixedTransformer
  {
//    MixedPolynomialTransformer(){ super(); }
    MixedPolynomialTransformer(int ndegree){ init(ndegree); }

    protected void setNumberOfParams() {
      int nv = 2 + _ndegree;
      setNumberOfParams(nv, nv);
    }

    public String toString() {
      return formName("MixedPolynomialTransformer");
    }

    protected int formLinearPart(IGetXY origin, double[][] eqrows, int k0) {
      return formRLinearPart(origin, eqrows, k0);
    }
    protected int formHighDegreePart(IGetXY origin, double[][] eqrows, int k0) {
//      return k0;
      return formCHighDegreePart(origin, eqrows, k0);
    }
    protected int formRootDegreePart(IGetXY origin, double[][] eqrows, int k0) {
      return k0;
//      formCRootDegreePart(origin, eqrows, k0);
    }
  }
  //-- -------- MixedPolyRootsTransformer --------------

  //++ -------- MixedPolyRootsTransformer --------------
  // used a linear transformation and some complex root members
  static public class MixedPolyRootsTransformer extends MixedPolymixedTransformer
  {
//    MixedPolyRootsTransformer(){ super(); }
    MixedPolyRootsTransformer(int ndegree){ init(ndegree); }

    protected void setNumberOfParams() {
      int nv = 2 + _ndegree;
      setNumberOfParams(nv, nv);
    }

    public String toString() {
      return formName("MixedPolyRootsTransformer");
    }

    protected int formLinearPart(IGetXY origin, double[][] eqrows, int k0) {
      return formRLinearPart(origin, eqrows, k0);
    }
    protected int formHighDegreePart(IGetXY origin, double[][] eqrows, int k0) {
      return k0;
//      return formCHighDegreePart(origin, eqrows, k0);
    }
    protected int formRootDegreePart(IGetXY origin, double[][] eqrows, int k0) {
      return formCRootDegreePart(origin, eqrows, k0);
    }
  }
  //-- -------- MixedPolyRootsTransformer --------------

  //++ -------- MixedFurieTransformer --------------
  // used a linear transformation and some trigonometric members
  static public class MixedFurieTransformer extends ITransformer.XYParams
  {
    // a maximum index for a trigonometric member
    protected int _nk;
    // rectangle for transformation
    protected double _x1, _x2;
    protected double _y1, _y2;

    protected MixedFurieTransformer() {}
    MixedFurieTransformer(double x1, double x2, double y1, double y2, int maxk){
      init(x1, x2, y1, y2, maxk);
    }

    protected void init(double x1, double x2, double y1, double y2, int maxk) {
      super.init(0, 0);
      _x1 = Math.min(x1, x2);
      _x2 = Math.max(x1, x2);
      _y1 = Math.min(y1, y2);
      _y2 = Math.max(y1, y2);
      _nk = maxk;
      if (_nk < 0) _nk = 0;
      setNumberOfParams();
    }
    protected void setNumberOfParams() {
      int nv = 3 + 2 * _nk;
      setNumberOfParams(nv, nv);
    }

    public String toString() {
      return formName("MixedFurieTransformer");
    }
    protected String formName(String className) {
      return className + "(" + _nk + ")";
    }

    // implementation for complex equations with real x- and imaginary y-
    protected void formEquations(IGetXY origin, double[][] eqrows) {
      formAbsoluteTerm(origin, eqrows, 0);
      int k0 = formLinearPart(origin, eqrows, 1);
      formSinCosPart(origin, eqrows, k0);
    }
    protected int formSinCosPart(IGetXY origin, double[][] eqrows, int k0) {
      double x = origin.getX();
      double y = origin.getY();
      x =  2 * Math.PI * (x - _x1) / (_x2 - _x1);
      y =  2 * Math.PI * (y - _y1) / (_y2 - _y1);
      int k = k0;
      for (int i=1; i<=_nk; i++) {
        double sinx = Math.sin(i*x);
        double cosx = Math.cos(i*x);
        eqrows[1][_nparamsX +k] = sinx;
        eqrows[0][k++] = sinx;
        eqrows[1][_nparamsX +k] = cosx;
        eqrows[0][k++] = cosx;
      }
      return k0 +_nk +_nk;
    }
  }
  //-- -------- MixedFurieTransformer --------------

  //++ -------- FurieTransformer --------------
  // used some trigonometric transformations
  static public class FurieTransformer extends MixedFurieTransformer
  {
    protected FurieTransformer() {}
    FurieTransformer(double x1, double x2, double y1, double y2, int maxk){ init(x1, x2, y1, y2, maxk); }

    protected void setNumberOfParams() {
      int nv = 1 + 2 * _nk;
      setNumberOfParams(nv, nv);
    }

    public String toString() {
      return formName("FurieTransformer");
    }

    // implementation for complex equations with real x- and imaginary y-
    protected void formEquations(IGetXY origin, double[][] eqrows) {
      formAbsoluteTerm(origin, eqrows, 0);
//      int k0 = formLinearPart(origin, eqrows, 1);
      int k0 = 1;
      formSinCosPart(origin, eqrows, k0);
    }
  }
  //-- -------- FurieTransformer  --------------

  //++ -------- TransformerFactory --------------
  static public class TransformerFactory {
    static public ITransformer get(String type, int degree) {
      if (type.toLowerCase() == "poly")
        return PolynomialTransformer.degree(degree);

      if (type.toLowerCase() == "cpoly")
        return new ComplexPolynomialTransformer(degree);
//      if (type.toLowerCase() == "cpolym")
//        return new ComplexPolymixedTransformer(degree);
      if (type.toLowerCase() == "mpoly")
        return new MixedPolynomialTransformer(degree);
      if (type.toLowerCase() == "mpolyr")
        return new MixedPolyRootsTransformer(degree);
      if (type.toLowerCase() == "mpolym")
        return new MixedPolymixedTransformer(degree);

      if (type.toLowerCase() == "furie")
        return new FurieTransformer(0, 6, 0, 6, degree);
      if (type.toLowerCase() == "mfurie")
        return new MixedFurieTransformer(0, 6, 0, 6, degree);
      return null;
    }
  }
  //-- -------- TransformerFactory --------------

  //++ -------- IEvaluation --------------
  public interface IEvaluation
  {
    double meanError();
    double meanTime();
    boolean isAbnormal();

    // base implementation
    static public class Base implements IEvaluation
    {
      protected double  _error;
      protected double  _time;
      protected boolean  _isbad;
      Base(){
        _error = -1;
        _time = -1;
        _isbad = true;
      }
      Base(double error, double time){
        _error = error;
        _time = time;
        _isbad = false;
      }
      public double  meanError() { return _error; }
      public double  meanTime() { return _time; }
      public boolean  isAbnormal() { return _isbad; }

      static private IEvaluation _abnormal;
      static { _abnormal = new Base(); }
      static public IEvaluation abnormal() { return _abnormal; }
    }
  }
  //-- -------- IEvaluation --------------

  // evaluate a number of transformers, using some training data
  static public Map<ITransformer, IEvaluation>
  evaluateTransformers(Collection<ITransformer> tformers, Collection<ITrainingElem> tdata, double eps) {
    Map<ITransformer, IEvaluation> evas = new HashMap<ITransformer, IEvaluation>();
    for (ITransformer tformer : tformers) {
      IEvaluation eva = trainTransformer(tformer, tdata, eps);
      evas.put(tformer, eva);
    }
    return evas;
  }
  // train the transformer, using some training data, and evaluate a result
  static public IEvaluation trainTransformer(ITransformer tformer, Collection<ITrainingElem> tdata, double eps) {
    Date dt1 = new Date();
    int nels = tdata.size();
    // build a matrix
    int neqs = 2 * tdata.size();
    List eqs = new LinkedList<double[]>();
    double[][] eqrows = new double[2][];
    for (ITrainingElem elem : tdata) {
      eqrows = tformer.formEquations(elem);
      for (double[] eqrow : eqrows) eqs.add(eqrow);
    }
    double[][] A = Algebra.rowsToMatrix(eqs);
    // search decision using the least squares method
    double[] decision = Algebra.applyLeastSquares(tformer.getNumberOfParams(), A, eps);
    tformer.setParameters(decision);
    if (decision == null) { // abnormal situation - no decision
      return IEvaluation.Base.abnormal();
    }
    // make evaluation
    double merr2 = Algebra.calcLinearSystemMeanErr2(A, decision);
    Date dt2 = new Date();
    double meanT = dt2.getTime() - dt1.getTime();
    meanT /= nels;
    IEvaluation eva = new IEvaluation.Base(merr2, meanT);
    return eva;
  }

  // find the best transformer of class PolynomialTransformer for the given training set.
  static public ITransformer  findPolynomialTransformer(
      Collection<ITrainingElem> tdata, double eps, double maxMeanErr) {
    return findPolynomialTransformer(tdata, eps, maxMeanErr, 0);
  }
  // find the best transformer of class PolynomialTransformer for the given training set.
  static public ITransformer  findPolynomialTransformer(
      Collection<ITrainingElem> tdata, double eps, double maxMeanErr, int maxDegree) {
    return findPolyBasedTransformer("poly", tdata, eps, maxMeanErr, maxDegree);
  }

  // find the best transformer of class ComplexPolynomialTransformer for the given training set.
  static public ITransformer  findCPolynomialTransformer(
      Collection<ITrainingElem> tdata, double eps, double maxMeanErr) {
    return findCPolynomialTransformer(tdata, eps, maxMeanErr, 0);
  }
  // find the best transformer of class ComplexPolynomialTransformer for the given training set.
  static public ITransformer  findCPolynomialTransformer(
      Collection<ITrainingElem> tdata, double eps, double maxMeanErr, int maxDegree) {
    return findPolyBasedTransformer("cpoly", tdata, eps, maxMeanErr, maxDegree);
  }

  // find the best transformer of class MixedPolyRootsTransformer for the given training set.
  static public ITransformer  findMPolynomialTransformer(
      Collection<ITrainingElem> tdata, double eps, double maxMeanErr) {
    return findMPolynomialTransformer(tdata, eps, maxMeanErr, 0);
  }
  // find the best transformer of class MixedPolyRootsTransformer for the given training set.
  static public ITransformer  findMPolynomialTransformer(
      Collection<ITrainingElem> tdata, double eps, double maxMeanErr, int maxDegree) {
    return findPolyBasedTransformer("mpoly", tdata, eps, maxMeanErr, maxDegree);
  }

  // find the best transformer of class MixedPolyRootsTransformer for the given training set.
  static public ITransformer  findMPolyRootsTransformer(
      Collection<ITrainingElem> tdata, double eps, double maxMeanErr) {
    return findMPolyRootsTransformer(tdata, eps, maxMeanErr, 0);
  }
  // find the best transformer of class MixedPolyRootsTransformer for the given training set.
  static public ITransformer  findMPolyRootsTransformer(
      Collection<ITrainingElem> tdata, double eps, double maxMeanErr, int maxDegree) {
    return findPolyBasedTransformer("mpolyr", tdata, eps, maxMeanErr, maxDegree);
  }

  // find the best transformer of class ComplexPolymixedTransformer for the given training set.
  static public ITransformer findMPolymixedTransformer(
      Collection<ITrainingElem> tdata, double eps, double maxMeanErr) {
    return findMPolymixedTransformer(tdata, eps, maxMeanErr, 0);
  }
  // find the best transformer of class ComplexPolymixedTransformer for the given training set.
  static public ITransformer  findMPolymixedTransformer(
      Collection<ITrainingElem> tdata, double eps, double maxMeanErr, int maxDegree) {
    return findPolyBasedTransformer("mpolym", tdata, eps, maxMeanErr, maxDegree);
  }

  // build polynomial based transformers, increasing the degree on every step up to given value,
  // to find the one which gives the accepted error on the training set.
  static public ITransformer findPolyBasedTransformer(
      String type, Collection<ITrainingElem> tdata, double eps, double maxMeanErr, int maxDegree) {
    // check parameters of search
    if (tdata == null) tdata = new LinkedList<ITrainingElem>();
    if (maxMeanErr <= 0) maxMeanErr = Algebra.EPS;
    boolean unlimited = false;
    if (maxDegree < 1) unlimited = true;
    // prepare a search
    int degree = 1;
    if (unlimited) maxDegree = degree + 1;
//    int nvars = 3;
    ITransformer found = null;
    IEvaluation eva = null;
    while (degree <= maxDegree) {
      ITransformer tf = TransformerFactory.get(type, degree);
      if (tdata.size() < tf.getNumberOfParams()) break;
      eva = trainTransformer(tf, tdata, eps);
      if (eva.isAbnormal()) break;
      if (eva.meanError() <= maxMeanErr) {
        found = tf;
        break;
      }
      degree++;
//      nvars += (degree+1);
      if (unlimited) maxDegree = degree + 1;
    }
    if (_trace) {
      if (found == null)
        System.out.println("findPolyBasedTransformer: no found transformer of type " + type);
      else
        System.out.println("findPolyBasedTransformer: " + found + ", meanError = " + eva.meanError());
    }
    return found;
  }


  //-------------------------------------
  // Main
  private static void outRes(String title, List<ITransformer> tformers, List<ITrainingElem> tdata1) {
    System.out.println("----------------");
    System.out.println("next evaluation: " + title);
    System.out.println("----------------");
    Map<ITransformer, IEvaluation> evas = evaluateTransformers(tformers, tdata1, 1E-275);
    for ( ITransformer tformer : tformers ) {
//    for ( Map.Entry<ITransformer, IEvaluation> entry : evas.entrySet() ) {
//      ITransformer tformer = entry.getKey();
      IEvaluation eva = evas.get(tformer);
      double[] decision = tformer.getParameters();
      String serr = String.format("%12.3f", eva.meanError());
      System.out.println(tformer + ": meanError = " + eva.meanError() + ",  meanTime = " + eva.meanTime());
      if (decision == null) System.out.println("no decision ");
      else {
  //        System.out.println("parameters: ");
  //        for (int i=0; i<decision.length; i++) System.out.println(String.format("%12.3f", decision[i]));
        for (int i=0; i<tformer.getNumberOfParams(); i++)
          System.out.println(tformer.getParamName(i) + " = " + String.format("%12.3f", decision[i]));
      }
    }
  }

  public static void main(String[] args) throws Exception
  {
    double origin[][] = {
        { 0, 0 }, { 1, 0 }, { 2, 0 }, { 3, 0 }, { 4, 0 }, { 5, 0 },
        { 0, 1 }, { 1, 1 }, { 2, 1 }, { 3, 1 }, { 4, 1 }, { 5, 1 },
        { 0, 2 }, { 1, 2 }, { 2, 2 }, { 3, 2 }, { 4, 2 }, { 5, 2 },
        { 0, 3 }, { 1, 3 }, { 2, 3 }, { 3, 3 }, { 4, 3 }, { 5, 3 },
        { 0, 4 }, { 1, 4 }, { 2, 4 }, { 3, 4 }, { 4, 4 }, { 5, 4 },
        { 0, 5 }, { 1, 5 }, { 2, 5 }, { 3, 5 }, { 4, 5 }, { 5, 5 },
        { -1, -1 }
    };
    // form training data
    List<ITrainingElem> tdata1 = new LinkedList<ITrainingElem>();
    List<ITrainingElem> tdata2 = new LinkedList<ITrainingElem>();
    for (double[] proto : origin ) {
      double x = proto[0];
      double y = proto[1];
      XY xy1 = new XY(x, y);
//      double x2 = 1 + 1*x - 1*y + 0.7*x*x + 2.5*x*y + 1.5*y*y + 1*x*x*x;
//      double y2 = 2 + 2*x + 3*y + 2.3*x*x + 1.71*x*y + 3.1*y*y + 2*y*y*y;
      double x2 = 1 + 1*x + 1*y + 1*x*x + 1*x*y + 1*y*y;
      double y2 = 2 + 2*x - 2*y + 2*x*x + 2*x*y - 2*y*y;
//      double x2 = 1 + 1*x + 1*y;
//      double y2 = 2 + 2*x - 2*y;
      XY xy2 = new XY(x2, y2);
      tdata1.add(new ITrainingElem.Base(xy1, xy2));
      tdata2.add(new ITrainingElem.Base(xy2, xy1));
    }
    List<ITransformer> tformers = new LinkedList<ITransformer>();
//    tformers.add(PolynomialTransformer.identic());
//    tformers.add(PolynomialTransformer.meanresult());
//    tformers.add(PolynomialTransformer.linear());
//    tformers.add(PolynomialTransformer.quadratic());
//    tformers.add(PolynomialTransformer.cubic());

//    tformers.add(new ComplexPolynomialTransformer(3));
//    tformers.add(new ComplexPolynomialTransformer(4));
//    tformers.add(new ComplexPolynomialTransformer(5));

//    tformers.add(new ComplexPolymixedTransformer(3));
//    tformers.add(new ComplexPolymixedTransformer(4));
//    tformers.add(new ComplexPolymixedTransformer(5));

//    tformers.add(tformers.size(), new MixedPolyRootsTransformer(2));
//    tformers.add(tformers.size(), new MixedPolyRootsTransformer(3));
//    tformers.add(tformers.size(), new MixedPolyRootsTransformer(4));
//    tformers.add(tformers.size(), new MixedPolyRootsTransformer(5));
//    tformers.add(tformers.size(), new MixedPolyRootsTransformer(6));
//    tformers.add(tformers.size(), new MixedPolyRootsTransformer(7));

    tformers.add(tformers.size(), new FurieTransformer(0, 6.1, 0, 6.1, 2));
    tformers.add(tformers.size(), new FurieTransformer(0, 6.1, 0, 6.1, 3));
    tformers.add(tformers.size(), new FurieTransformer(0, 6.1, 0, 6.1, 4));
    tformers.add(tformers.size(), new FurieTransformer(0, 6.1, 0, 6.1, 5));
    tformers.add(tformers.size(), new FurieTransformer(0, 6.1, 0, 6.1, 6));
    tformers.add(tformers.size(), new FurieTransformer(0, 6.1, 0, 6.1, 7));
    tformers.add(tformers.size(), new FurieTransformer(0, 6.1, 0, 6.1, 8));

//    tformers.add(tformers.size(), new MixedFurieTransformer(0, 6.1, 0, 6.1, 2));
//    tformers.add(tformers.size(), new MixedFurieTransformer(0, 6.1, 0, 6.1, 3));
//    tformers.add(tformers.size(), new MixedFurieTransformer(0, 6.1, 0, 6.1, 4));
//    tformers.add(tformers.size(), new MixedFurieTransformer(0, 6.1, 0, 6.1, 5));
//    tformers.add(tformers.size(), new MixedFurieTransformer(0, 6.1, 0, 6.1, 6));

    outRes("direct1", tformers, tdata1);
    outRes("reverse1", tformers, tdata2);

    // form other training data
    List<ITrainingElem> tdata3 = new LinkedList<ITrainingElem>();
    List<ITrainingElem> tdata4 = new LinkedList<ITrainingElem>();
    for (double[] proto : origin ) {
      double x = proto[0];
      double y = proto[1];
      XY xy1 = new XY(x, y);
//      double x2 = 1 + 1*x + 1*y + 1*x*x + 1*x*y + 1*y*y;
//      double y2 = 2 + 2*x - 2*y + 2*x*x + 2*x*y - 2*y*y;
//      double x2 = 1 + x + x*x; // + y*Math.cos(x);
//      double y2 = 2 + y; // + x*Math.sin(y);
      double x2 = 1 + x + x*x + 2*y*Math.cos(2*x);
      double y2 = 2 + y + 2*x*Math.sin(3*y);
      XY xy2 = new XY(x2, y2);
      tdata3.add(new ITrainingElem.Base(xy1, xy2));
      tdata4.add(new ITrainingElem.Base(xy2, xy1));
    }
    tformers = new LinkedList<ITransformer>();
    ITransformer tf;
    System.out.println("direct 2");
    tf = findPolynomialTransformer(tdata3, 1E-15, 10, 3);
    if (tf == null) System.out.println("no decicision for findPolynomialTransformer");
    else tformers.add(tformers.size(), tf);

//    tf = findCPolynomialTransformer(tdata3, 1E-15, 33, 7);
//    if (tf == null) System.out.println("no decicision for findCPolynomialTransformer");
//    else tformers.add(tformers.size(), tf);

    tf = findMPolynomialTransformer(tdata3, 1E-15, 12, 7);
    if (tf == null) System.out.println("no decicision for findMPolynomialTransformer");
    else tformers.add(tformers.size(), tf);

//    tf = findMPolyRootsTransformer(tdata3, 1E-15, 12.99, 7);
//    if (tf == null) System.out.println("no decicision for findMPolyRootsTransformer");
//    else tformers.add(tformers.size(), tf);

    tf = findMPolymixedTransformer(tdata3, 1E-15, 12, 5);
    if (tf == null) System.out.println("no decicision for findMPolymixedTransformer");
    else tformers.add(tformers.size(), tf);

    outRes("direct2", tformers, tdata3);

    System.out.println("reverse 2");
    tf = findPolynomialTransformer(tdata4, 1E-15, 1, 4);
    if (tf == null) System.out.println("no decicision for findPolynomialTransformer");
    else tformers.add(tformers.size(), tf);

//    tf = findCPolynomialTransformer(tdata4, 1E-15, 0.2, 7);
//    if (tf == null) System.out.println("no decicision for findCPolynomialTransformer");
//    else tformers.add(tformers.size(), tf);

    tf = findMPolynomialTransformer(tdata4, 1E-15, 3, 7);
    if (tf == null) System.out.println("no decicision for findMPolynomialTransformer");
    else tformers.add(tformers.size(), tf);

//    tf = findMPolyRootsTransformer(tdata4, 1E-15, 3, 7);
//    if (tf == null) System.out.println("no decicision for findMPolyRootsTransformer");
//    else tformers.add(tformers.size(), tf);

    tf = findMPolymixedTransformer(tdata4, 1E-15, 3, 5);
    if (tf == null) System.out.println("no decicision for findMPolymixedTransformer");
    else tformers.add(tformers.size(), tf);

    outRes("reverse 2", tformers, tdata3);

//    if (tfbest == null) {
//      System.out.println("no decision");
//    }
//    else {
//      System.out.println("the best transformer is " + tfbest);
//      for (int i=0; i<tfbest.getNumberOfParams(); i++)
//        System.out.println(tfbest.getParamName(i) + " = " + String.format("%12.3f", tfbest.getParameters()[i]));
//      System.out.println("transform(0,0): " + tfbest.transform(new XY(0,0)));
//      System.out.println("transform(1,0): " + tfbest.transform(new XY(1,0)));
//      System.out.println("transform(0,1): " + tfbest.transform(new XY(0,1)));
//      System.out.println("transform(1,1): " + tfbest.transform(new XY(1,1)));
//    }
//    System.out.println("");
//    System.out.println("teq(0,0): " + teq.transform(new XY(0,0)));
//    System.out.println("teq(1,0): " + teq.transform(new XY(1,0)));
//    System.out.println("teq(0,1): " + teq.transform(new XY(0,1)));
//    System.out.println("teq(1,1): " + teq.transform(new XY(1,1)));
  }

}
