package su.utils.tab;

import ru.ts.common.misc.Text;
import su.matrix.solver.Matrix;
/**
 * <pre>
 * Created by IntelliJ IDEA.
 * User: sigolaev_va
 * Date: 20.01.2014
 * Time: 17:27:08
 * Original package: su.utils.tab
 * *
 * See <A>
 * *
 * <pre>
 */
public class TabSolverJAMA
        //implements IMatrixSolver
{
	public double[] solve( double[][] arrA, double[] arrB, double[] arrX )
	{
		final int M = arrA.length;
		final int N = arrA[0].length;
		if ( arrB.length  != M )
		{
			throw new IllegalArgumentException(
				String.format( "TabSolverJAMA: Matrix A[%d][%d] doesn't coinside with Vector B[%d]", M, N, arrB.length ) );
		}
		Matrix A = new Matrix( arrA );
		Matrix B = new Matrix ( M, 1 );
		for(int i = 0; i < M; i++) // assign vector B
			B.set( i, 0, arrB[i] );
		Matrix X;
		//QRDecomposition qd = new QRDecomposition( A );
		try
		{
			//X = qd.solve( B );
			X = A.solve( B );
		}
		catch ( Exception e )
		{
			e.printStackTrace(  );
			Text.serr("TabSolverJAMA A.solve(B) error: " + e.getMessage());
			return null;
		}
/*
		if ( A.det() == 0.0D )
			return null;
*/

		if ( arrX == null || arrX.length != N )
			arrX = new double[N];
		for(int i =0; i < N; i++) // assign vector B
			arrX[ i ] = X.get( i, 0 );
		return arrX;
	}
}
