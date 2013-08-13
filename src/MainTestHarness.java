import java.util.ArrayList;

import Jama.Matrix;


public class MainTestHarness {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("java lst poly regul");

		//Line A details
		double px1 = 1.25;
		double px2 = 2;
		double py1 = 1.5;
		double py2 = 3;

		double qx1 = 3.25;
		double qx2 = 4.25;
		double qy1 = 3.5;
		double qy2 = 4;

		//clsq(A,2,n,c);

		double[] p0 = new double[3];
		double[] p1 = new double[3];
		double[] p2 = new double[3];
		double[] p3 = new double[3];

		/*
		//Line A
		p0[0] = px1;
		p0[1] = py1;
		p0[2] = 0;

		p1[0] = px2;
		p1[1] = py2;
		p1[2] = 0;

		//Line B
		p2[0] = qx1;
		p2[1] = qy1;
		p2[2] = 0;

		p3[0] = qx2;
		p3[1] = qy2;
		p3[2] = 0;
		 */

		p0[0] = px1;
		p0[1] = py1;
		p0[2] = 0;

		p1[0] = px2;
		p1[1] = py2;
		p1[2] = 0;


		p2[0] = qx2;
		p2[1] = qy2;
		p2[2] = 0;


		JLineConstrain  jLineConstrain = new JLineConstrain(p0,p1,p2);

		System.out.println("after jls line constrain");

		System.out.println("line1 " );
		printMatrix(jLineConstrain.line1);
		System.out.println("line2 " );
		printMatrix(jLineConstrain.line2);		


		System.out.println("Corrected line pts: " );
		System.out.println("correctedLineAPoint " + jLineConstrain.correctedLineAPoint[0] + " " + jLineConstrain.correctedLineAPoint[1]);
		System.out.println("correctedLineBPoint: " + jLineConstrain.correctedLineBPoint[0] + " " + jLineConstrain.correctedLineBPoint[1]);
		System.out.println("correctedLineSharedPoint: "  + jLineConstrain.correctedSharedPoint[0] + " " + jLineConstrain.correctedSharedPoint[1]);
		//Geometry.findLineSegmentIntersection(x0, y0, x1, y1, x2, y2, x3, y3, intersection)



		System.out.println("Polygon constrain test");
		//double[] xList= new double[4];
		//double[] yList= new double[4];

		ArrayList<Double> xList = new ArrayList();
		ArrayList<Double> yList = new ArrayList();


		//double[] xListArr = {-2,3,5,1};
		//double[] yListArr = {1,4,2,-2};

		double[] xListArr = new double[]{-2,3,5,1};
		double[] yListArr = new double[]{1,4,2,-2};

		xList.add((double) -2);
		xList.add((double) 3);
		xList.add((double) 5);
		xList.add((double) 1);

		
		yList.add((double) 1);
		yList.add((double) 4);
		yList.add((double) 2);
		yList.add((double) -2);

		System.out.println("xList is: " + xList.get(0));

		JPolygonConstrain  jPolygonConstrain = new JPolygonConstrain(xList,yList,true,false);
		System.out.println("after jls poly constrain");

		System.out.println("Open polyline constrain test");
		//double[] xList= new double[4];
		//double[] yList= new double[4];

		ArrayList<Double> xListPolyline = new ArrayList();
		ArrayList<Double> yListPolyline = new ArrayList();


		xListPolyline.add((double) -2);
		xListPolyline.add((double) 3);
		xListPolyline.add((double) 5);
		xListPolyline.add((double) 1);
		xListPolyline.add((double) 2);

		yListPolyline.add((double) 1);
		yListPolyline.add((double) 4);
		yListPolyline.add((double) 2);
		yListPolyline.add((double) -2);
		yListPolyline.add((double) -3);

		System.out.println("xListPolyline is: " + xListPolyline.get(0));

		JPolygonConstrain  jPolygonConstrain2 = new JPolygonConstrain(xListPolyline,yListPolyline,false,true);
		System.out.println("after jls poly constrain");

	}


	public static void printMatrix(Matrix A) {
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
}
