package ru.ts.gisutils.algs.transform;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: yugl
 * Date: 08.09.2008
 * Time: 17:42:12
 * Some algebra operation.
 * The first index of matrices means an index of rows.
 * There suppposes that matrix element aij is kept as A[i-1][j-1],
 * absolute term ai0 (if any) is kept as A[i-1][n].  
 */
public class Algebra 
{
//  static public boolean _trace = true;
  static public boolean _trace = false;
  //++ -------- precision --------------
  static final public double EPS = 0.000001;
  protected double _eps = EPS;
  public void setEps(double eps) {
    _eps = Math.abs(eps);
  }

  static public boolean near(double v1, double v2, double eps) {
    if (Math.abs(v1-v2) > eps) return false;
    return true;
  }
  public boolean near(double v1, double v2) {
    return near(v1, v2, _eps);
  }

  static public boolean nearZero(double v, double eps) {
    return near(v, 0, eps);
  }
  public boolean nearZero(double v) {
    return nearZero(v, _eps);
  }

  //-- -------- precision --------------

  //++ -------- constructor --------------
  public Algebra() {
  }
  public Algebra(double eps) {
    setEps(eps);
  }
  //-- -------- constructor --------------

  //++ -------- conversion --------------
  // converts Collection of rows into matrix
  public double[][] convertRowsToMatrix(Collection<double[]> rows){
    return rowsToMatrix(rows);
  }
  // converts Collection of rows to matrix
  static public double[][] rowsToMatrix(Collection<double[]> rows) {
    double[][] arr = new double[rows.size()][];
    int i=0;
    for (double[] row : rows) arr[i++] = row;
//    for (int i=0; i<lst.size(); i++) arr[i] = lst.get(i);
    return arr;
  }
  // converts matrix to List of rows
  static public List<double[]> matrixToRows(double[][] arr) {
    ArrayList<double[]> lst = new ArrayList<double[]>(arr.length);
    for (int i=0; i<arr.length; i++) lst.add(arr[i]);
    return lst;
  }
  //-- -------- conversion --------------

  //++ -------- trace --------------
//  static public void dblLstOut(List<double[]> lst) {
//    dblArrOut(rowsToMatrix(lst));
//  }
  static public void dblArrOut(double[][] arr) {
    for (int i=0; i<arr.length; i++) {
      double row[] = arr[i];
      for (int j=0; j<row.length; j++)
        System.out.print(String.format("%8.1f ", row[j]));
      System.out.println("");
    }
  }
  //-- -------- trace --------------

  //++ -------- preparation --------------
  static public double[][] prepareSqrMatrix(int nrows, double A[][]) {
    return prepareMatrix(nrows, nrows, A);
  }

  static public double[][] prepareMatrix(int nrows, int mcols, double A[][]) {
    double B[][] = new double[nrows][];
    for (int irow=0; irow<nrows; irow++) {
      B[irow] = new double[mcols];
      for (int icol=0; icol<mcols; icol++) B[irow][icol] = 0;
      int k = A[irow].length;
      if (k > mcols) k = mcols;
      for (int icol=0; icol<k; icol++) B[irow][icol] = A[irow][icol];
    }
    return B;
  }

  //-- -------- preparation --------------

  //++ -------- determinant --------------
  // A is a matrix n x n (first index is a row)
  public double determinant(double A[][]) {
    return determinant(A, _eps);
  }
  // A is a matrix n x n (list of rows)
  public double determinant(List<double[]> A) {
    return determinant(rowsToMatrix(A), _eps);
  }

  // A is a matrix n x n (first index is a row)
  static public double determinant(double A[][], double eps) {
    int nrows = A.length;
    double B[][] = prepareSqrMatrix(nrows, A);
    return determinantOfSqrMatrix(B, eps);
  }

  // matrix A is changed
  static protected double determinantOfSqrMatrix(double A[][], double eps) {
    int nrows = A.length;
    // bring the matrix to a scalar (diagonal) one
    double cur[] = null;
    for (int iact=0; iact<(nrows-1); iact++) {
      double act[] = A[iact];
      if (nearZero(act[iact], eps)) { //need a row with a nonzero member
        int sign = 1;
        for (int icur=iact+1; icur<nrows; icur++) {
          cur = A[icur];
          sign = -sign;
          if (nearZero(cur[iact], eps)) continue;
          // replace act and cur
          A[iact] = cur;
          A[icur] = act;
          for (int icol=iact; icol<nrows; icol++)
            act[icol] *= sign;  // change sign for rows exchange
          act = A[iact];
          cur = null;
          break;
        }
        if (cur != null) { // there is a zero in all rows
          return 0;
        }
      } // ok, A[iact][iact] is nonzero
      for (int icur=iact+1; icur<nrows; icur++) {
        cur = A[icur];
        double ratio = cur[iact] / act[iact];
        for (int jcol=iact; jcol<nrows; jcol++) {
          cur[jcol] -= ratio * act[jcol];
        }
      }
    }
    // make a product of diagonal members
    double res = 1;
    for (int iact=0; iact<nrows; iact++) {
      res *= A[iact][iact];
    }
    return res;
  }
  //-- -------- determinant --------------

  //++ -------- scalar (diagonal) form --------------
  // bring matrix A to a scalar (diagonal) form
  public void makeDiagonal(int nrows, int mcols, double A[][]) {
    makeDiagonal(nrows, mcols, A, _eps);
  }

  // bring matrix A to a scalar (diagonal) form
  static public void makeDiagonal(int nrows, int mcols, double A[][], double eps) {
    double cur[] = null;
    for (int iact=0; iact<(nrows-1); iact++) {
//      if (_trace) {
//        System.out.println("Algebra.makeDiagonal: step " + (iact+1));
//        dblArrOut(A);
//      }
      double act[] = A[iact];
      if (nearZero(act[iact], eps)) { //need a row with a nonzero member
        for (int icur=iact+1; icur<nrows; icur++) {
          cur = A[icur];
          if (nearZero(cur[iact], eps)) continue;
          // replace act and cur
          A[iact] = cur;
          A[icur] = act;
          act = A[iact];
          cur = null;
          break;
        }
        if (cur != null) { // there is a zero in all rows
          continue; // degenerative matrix, but let's go on
        }
      } // ok, A[iact][iact] is nonzero
      for (int icur=iact+1; icur<nrows; icur++) {
        cur = A[icur];
        double ratio = cur[iact] / act[iact];
        for (int jcol=iact; jcol<mcols; jcol++) {
          cur[jcol] -= ratio * act[jcol];
        }
      }
    }
  }
  //-- -------- scalar (diagonal) form --------------

  //++ -------- linear system solving --------------
  // solve a system described by matrix A (n x n+1)
  // the i-th row of matrix corresponds to a linear equation:
  // ai1*x1 + ai2*x2 + ... + ain*xn + ai0 = 0
  public double[] solveLinearSystem(double[][] A) {
    return solveLinearSystem(A, _eps);
  }

  // solve a system described by matrix A (n x n+1)
  // the i-th row of matrix corresponds to a linear equation:
  // ai1*x1 + ai2*x2 + ... + ain*xn + ai0 = 0
  static public double[] solveLinearSystem(double[][] A, double eps) {
    int nrows = A.length;
    double[] res = null;
    double[][] B = prepareMatrix(nrows, nrows+1, A);
    double det = determinant(B, eps);
    if (_trace) System.out.println("Algebra.solveLinearSystem: determinant = " + det);
    if (nearZero(det, eps)) {
      if (_trace) System.out.println("Algebra.solveLinearSystem: no unique solution");
      return res; // no unique solution
    }

    makeDiagonal(nrows, nrows+1, B, eps);
    if (_trace) {
      System.out.println("Algebra.solveLinearSystem: diagonal form ");
      dblArrOut(B);
    }
    res = new double[nrows];
    double cur[] = null;
    for (int iact=nrows-1; iact>=0; iact--) {
      // calculate absolute term
      double term = B[iact][nrows];
      for (int jcol=iact+1; jcol<nrows; jcol++) {
        term += B[iact][jcol]*res[jcol];
      }
      // calculate next variable
      res[iact] = (-term) / B[iact][iact];
    }
    if (_trace) {
      System.out.println("Algebra.solveLinearSystem: ");
      for (int iact=0;  iact<nrows; iact++) {
        System.out.println(" x" + (iact+1) + " = " + String.format("%12.5f", res[iact]));
      }
    }
    return res; // the unique solution
  }
  //-- -------- linear system solving --------------

  //++ -------- the least squares --------------
  // solve a system described by matrix A (neqs x nvars+1),
  // using the least squares method.
  // the i-th row of matrix corresponds to a linear equation:
  // ai1*x1 + ai2*x2 + ... + ain*xn + ai0 = 0
  public double[] applyLeastSquares(int nvars, double[][] A) {
    return applyLeastSquares(nvars, A, _eps);
  }

  // solve a system described by matrix A (neqs x nvars+1),
  // using the least squares method.
  // the i-th row of matrix corresponds to a linear equation:
  // ai1*x1 + ai2*x2 + ... + ain*xn + ai0 = 0
  static public double[] applyLeastSquares(int nvars, double[][] A, double eps) {
    int neqs = A.length;
    // prepare normal equations
    int nrows = nvars;
    double[][] B = new double[nrows][nrows+1];
    for (int irow=0; irow<nrows; irow++) {
      for (int icol=0; icol<=nvars; icol++) {
        B[irow][icol] = 0;
        for (int ieq=0; ieq<neqs; ieq++) {
          B[irow][icol] += A[ieq][irow] * A[ieq][icol];
        }
      }
    }
    if (_trace) {
      System.out.println("Algebra.applyLeastSquares: normal equations ");
      dblArrOut(B);
    }
    // solve the normal equations
    return solveLinearSystem(B, eps);
  }

  // substitute the decision in the system and calculate the squared error.
  // the i-th row of matrix A corresponds to a linear equation:
  // ai1*x1 + ai2*x2 + ... + ain*xn + ai0 = 0.
  // array res contains the solution.
  public double calcLinearSystemError2(double[][] A, double[] decision) {
    return calcLinearSystemErr2(A, decision);
  }
  // the same as previous one but get the mean value of squared error.
  public double calcLinearSystemMeanError2(double[][] A, double[] decision) {
    return calcLinearSystemMeanErr2(A, decision);
  }

  // substitute the decision in the system and calculate the squared error.
  // the i-th row of matrix A corresponds to a linear equation:
  // ai1*x1 + ai2*x2 + ... + ain*xn + ai0 = 0.
  // array res contains the solution.
  static public double calcLinearSystemErr2(double[][] A, double[] decision) {
    int neqs = A.length;
    int nvars = decision.length;
    // calculate sum of squares
    double err2 = 0;
    for (int ieq=0; ieq<neqs; ieq++) {
      double err = calcLinearEqErr(A[ieq], decision);
      err2 += err*err;
    }
    if (_trace) {
      System.out.println("Algebra.calcLinearSystemErr2: squared error = " + err2);
    }
    return err2;
  }
  // the same as previous one but get the mean value of squared error.
  static public double calcLinearSystemMeanErr2(double[][] A, double[] decision) {
    int neqs = A.length;
    double err2 = calcLinearSystemErr2(A, decision);
    err2 /= neqs;
    if (_trace) {
      System.out.println("Algebra.calcLinearSystemMeanErr2: mean squared error = " + err2);
    }
    return err2;
  }

  // substitute the solution in the equation and calculate the error.
  // the row represents a linear equation:
  // a1*x1 + a2*x2 + ... + an*xn + a0 = 0.
  // array res contains the solution.
  static public double calcLinearEqErr(double[] eqrow, double[] solution) {
    int nvars = solution.length;
    double err = eqrow[nvars]; // absolute term
    for (int icol=0; icol<nvars; icol++) {
      err += eqrow[icol] * solution[icol];  // substitute solution
    }
    return err;
  }
  //-- -------- the least squares --------------

  //-------------------------------------
  // Main

  public static void main(String[] args) throws Exception {
    Algebra alg = new Algebra(0.0000001);
    double A[][] = {
        { 1, 1, 1, 1, 0 },
        { 1, 1, 0, 1, 1 },
        { 1, 0, 0, 1, 1 },
        { 2, 0, 2, 1, 1 }
    };
//    double det = alg.determinant(4, A);
//    System.out.println("Algebra.main: determinant = " + det);
//    double[][] B = prepareMatrix(3, 4, A);
//    alg.makeDiagonal(3, 4, B);
//    System.out.println("Algebra.main: diagonal form ");
//    dblArrOut(B);
    double[] res = alg.solveLinearSystem(A);
    double LS[][] = {
        { 1, 1, 1, 1, 1 },
        { 1, 1, 0, 1, 2 },
        { 1, 0, 0, 1, 2 },
        { 2, 0, 2, 1, 2 },
        { 1, 1, 1, 1, 0 },
        { 1, 1, 0, 1, 1 },
        { 1, 0, 0, 1, 1 },
        { 2, 0, 2, 1, 1 }
    };
    double[] res1 = alg.applyLeastSquares(4, A);
    double err1 = alg.calcLinearSystemMeanError2(A, res1);
    double[] res2 = alg.applyLeastSquares(4, LS);
    double err2 = alg.calcLinearSystemMeanError2(LS, res2);
  }
}
