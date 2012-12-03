

import java.util.Vector;


import Jama.*;

public class JLSConstrain {
	public boolean DEBUG_SHOW_LOG = false; 
	
	private int dim;

	private Matrix nOut;
	private Matrix cOut;


	public JLSConstrain(Matrix A,int dim) {

		int mRows = A.getRowDimension();
		int pColumns = A.getColumnDimension();

		System.out.println("mRows: " + mRows);
		System.out.println("pColumns: " + pColumns);

		if (pColumns < dim+1) {
			System.out.println("Not enough unknowns");
			//return false;
		}
		if (mRows < dim) {
			System.out.println("Not enough equations");
			//return false;
		}

		int m = Math.min(mRows, pColumns);

		System.out.println("qr R");
		QRDecomposition qrTest = A.qr();
		Matrix R = qrTest.getR(); //R = triu (qr (A));

		if (DEBUG_SHOW_LOG) printMatrix(qrTest.getR());
		System.out.println("qr Q");
		if (DEBUG_SHOW_LOG) printMatrix(qrTest.getQ());

		//R(p-2+1:3,p-2+1:p);

		System.out.println("Subsection: ");
		
		//returnRSubsection(R,pColumns,mRows,dim);
		System.out.println("param1: " + String.valueOf(pColumns-dim+1));
		System.out.println("param2: " + String.valueOf(m));
		System.out.println("param3: " + String.valueOf(pColumns-dim+1));
		System.out.println("param4: " + String.valueOf(pColumns));
		
		if (DEBUG_SHOW_LOG) printMatrixFormatted(R);
		if (DEBUG_SHOW_LOG) printMatrix(R.getMatrix(pColumns-dim+1-1, m-1, pColumns-dim+1-1, pColumns-1));

		SingularValueDecomposition svd = (R.getMatrix(pColumns-dim+1-1, m-1, pColumns-dim+1-1, pColumns-1).svd());

		System.out.println("svd U");
		if (DEBUG_SHOW_LOG) printMatrix(svd.getU());

		System.out.println("svd S");
		if (DEBUG_SHOW_LOG) printMatrix(svd.getS());

		System.out.println("svd V");
		Matrix V = svd.getV();
		if (DEBUG_SHOW_LOG) printMatrix(V);

		System.out.println("n");
		if (DEBUG_SHOW_LOG) printMatrix(svd.getV().getMatrix(0, V.getRowDimension()-1, dim-1, dim-1)); //Get entire column according to dim number
		Matrix n = svd.getV().getMatrix(0, V.getRowDimension()-1, dim-1, dim-1); //Technically a vector I suppose

		System.out.println("c first term");
		Matrix cFirst = (R.getMatrix(0, pColumns-dim-1, 0, pColumns-dim-1)).uminus();
		if (DEBUG_SHOW_LOG) printMatrix(cFirst);


		System.out.println("c second term");
		Matrix cSecond = (R.getMatrix(0,pColumns-dim-1,pColumns-dim,pColumns-1));
		if (DEBUG_SHOW_LOG) printMatrix(cSecond);

		System.out.println("c second term * n");
		Matrix cSecondTimesN = cSecond.times(n);
		if (DEBUG_SHOW_LOG) printMatrix(cSecondTimesN);

		System.out.println("c first left division csecond*n");
		Matrix c = cFirst.solve(cSecondTimesN);
		if (DEBUG_SHOW_LOG) printMatrix(c);

		nOut = n;
		cOut = c;

	}

	public void printMatrix(Matrix A) {
		//for (int i =0; i<A.)
		//for (int i; )
		double[][] arrayMat= A.getArray();
		for(int i=0;i<arrayMat.length;i++) {
			for(int j=0;j<arrayMat[i].length;j++) {
				//System.out.print(arrayMat[i][j]);
				System.out.println("row: " + i + " " + arrayMat[i][j]);
			}
			//System.out.println();
		}

	}


	public Matrix getC() {
		return cOut;
	}

	public Matrix getn() {
		return nOut;
	}
	
	
	public void printMatrixFormatted(Matrix A) {
		//for (int i =0; i<A.)
		//for (int i; )
		double[][] arrayMat= A.getArray();
		for(int i=0;i<arrayMat.length;i++) {
			StringBuilder builder = new StringBuilder("");
			for(int j=0;j<arrayMat[i].length;j++) {
				//System.out.print(arrayMat[i][j]);
				//System.out.println("row: " + i + " " + arrayMat[i][j]);
				builder.append(arrayMat[i][j] + "  ");
			}
			System.out.println("" + builder.toString());
			//System.out.println();
		}
		//System.out.println("line break: \n e \n end builder: ");

	}

}
