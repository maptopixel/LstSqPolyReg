

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import java.io.File;

import Jama.Matrix;

public class JPolygonConstrain {

	private double initialLineAPoint1X;
	private double initialLineAPoint1Y;

	private double initialLineAPoint2X;
	private double initialLineAPoint2Y;

	private double initialLineBPoint1X;
	private double initialLineBPoint1Y;

	private double initialLineBPoint2X;
	private double initialLineBPoint2Y;

	private JLSConstrain jls;

	public Matrix theBigA;

	public ArrayList<Double> correctedXList = new ArrayList();
	public ArrayList<Double> correctedYList= new ArrayList();


	//Takes the lists of x and y points that make up a polygon
	public JPolygonConstrain(List x,List y,boolean closedPolygon,boolean removeLastPoint) {

		//Determine dimensions
		int dim = 2; //Probably =2 at the moment


		if (removeLastPoint) {
			System.out.println("About to remove prev point");
			x.remove(x.size()-1);
			y.remove(y.size()-1);
		}

		Matrix A;
		if (closedPolygon){
			System.out.println("Making BigA as a closed polygon ");
			double lastX = (Double) x.get(0);
			double lastY = (Double) y.get(0);
			System.out.println("last X and Y: " + lastX + " " + lastY);
			x.add(lastX);
			y.add(lastY);
			A = theBigAMakerClosed(x,y);
		} else {
			System.out.println("Making BigA as a polyline");
			A = theBigAMakerPolyline(x,y);
		}

		System.out.println("theBigA: ");
		printMatrixFormatted(A);

		//Call the actual JLSConstraint class to find 
		//the constrained lsq solution
		jls = new JLSConstrain(A,dim);

		Matrix c = jls.getC();
		Matrix n = jls.getn();
		System.out.println("final c vector is: ");
		printMatrixFormatted(c);
		System.out.println("n vector is: ");
		printMatrixFormatted(n);

		Matrix n2 = (Matrix) n.clone();
		n2.set(0, 0, -(n.get(1,0)));
		n2.set(1, 0, n.get(0,0));		

		System.out.println("n2 vector is: ");
		printMatrixFormatted(n2);

		List lineMatrices = new ArrayList();

		for (int i=0; i < c.getRowDimension();i++) {

			if (i % 2 ==0) {
				//Matrix m = plotline(lineAPoint1[0], lineAPoint2[0], lineAPoint1[1], lineAPoint2[1], c.get(0,0), n);
				//System.out.println("using n vector");
				//System.out.println("x1: " + x.get(i));
				//System.out.println("x2: " + x.get(i+1));
				//System.out.println("y1: " + y.get(i));
				//System.out.println("y2: " + y.get(i+1));
				Matrix m = plotline((Double) x.get(i),(Double) x.get(i+1), (Double)y.get(i),(Double) y.get(i+1), c.get(i,0), n);
				System.out.println("line matrix " + i);
				printMatrixFormatted(m);
				lineMatrices.add(m);
			}else {
				System.out.println("using n2 vector");
				//System.out.println("x1: " + x.get(i));
				//System.out.println("x2: " + x.get(i+1));
				//System.out.println("y1: " + y.get(i));
				//System.out.println("y2: " + y.get(i+1));
				//Matrix m = plotline(lineAPoint1[0], lineAPoint2[0], lineAPoint1[1], lineAPoint2[1], c.get(0,0), n2);
				Matrix m = plotline((Double) x.get(i),(Double) x.get(i+1), (Double)y.get(i),(Double) y.get(i+1), c.get(i,0), n2);
				System.out.println("line matrix " + i);
				printMatrixFormatted(m);
				lineMatrices.add(m);
			}

			//Oh my, this code is awful
			if (i > 0){
				Matrix line1 = (Matrix) lineMatrices.get(i-1);
				Matrix line2 = (Matrix) lineMatrices.get(i);
				double[] intersection = new double[2];
				int intersectionResult = Geometry.findLineSegmentIntersection(line1.get(0, 0), line1.get(0, 1), line1.get(1, 0), line1.get(1, 1), line2.get(0, 0), line2.get(0, 1), line2.get(1, 0), line2.get(1, 1), intersection);
				System.out.println("Intersection result: " + intersectionResult);
				System.out.println("Intersection0: " + intersection[0]);
				System.out.println("Intersection1: " + intersection[1]);
				
				correctedXList.add(intersection[0]);
				correctedYList.add(intersection[1]);
			}
			
			if (i == c.getRowDimension()-1){ //We're up to the last line segment
				if (closedPolygon) {
					System.out.println("Last line");
					Matrix line1 = (Matrix) lineMatrices.get(i);
					Matrix line2 = (Matrix) lineMatrices.get(0);
					double[] intersection = new double[2];
					int intersectionResult = Geometry.findLineSegmentIntersection(line1.get(0, 0), line1.get(0, 1), line1.get(1, 0), line1.get(1, 1), line2.get(0, 0), line2.get(0, 1), line2.get(1, 0), line2.get(1, 1), intersection);
					System.out.println("Intersection result: " + intersectionResult);
					System.out.println("Intersection0: " + intersection[0]);
					System.out.println("Intersection1: " + intersection[1]);
					
					correctedXList.add(0,intersection[0]); //Stick the point at the front
					correctedYList.add(0,intersection[1]);//Stick the point at the front
				
				} else { 
					//We're on an open polyline, so we dont have an intersections for
					//the first and last points
					Matrix line1 = (Matrix) lineMatrices.get(i);
					correctedXList.add(line1.get(1, 0));
					correctedYList.add(line1.get(1, 1));
					
					Matrix line2 = (Matrix) lineMatrices.get(0);					
					correctedXList.add(0,line2.get(0, 0));
					correctedYList.add(0,line2.get(0, 1));			
					
				}
			}

		}

		
		System.out.println("Final correct xList: " + correctedXList.toString());
		System.out.println("Final correct yList: " + correctedYList.toString());
		/*
		for (int i=0; i < lineMatrices.size()-1;i++) {
			Matrix line1 = (Matrix) lineMatrices.get(i);
			Matrix line2 = (Matrix) lineMatrices.get(i+1);
			double[] intersection = new double[2];
			int intersectionResult = Geometry.findLineSegmentIntersection(line1.get(0, 0), line1.get(0, 1), line1.get(1, 0), line1.get(1, 1), line2.get(0, 0), line2.get(0, 1), line2.get(1, 0), line2.get(1, 1), intersection);
			System.out.println("Intersection result: " + intersectionResult);
			System.out.println("Intersection0: " + intersection[0]);
			System.out.println("Intersection1: " + intersection[1]);

		}

		 */


		/*
		%n2(1) =-n(2); 
		   %n2(2) = n(1)
		   %plotline(Qx,Qy,'+',c(2),n2,'-')
		 */ 


		/*
		Matrix j = plotline(lineBPoint1[0], lineBPoint2[0], lineBPoint1[1], lineBPoint2[1], c.get(1,0), n2);
		System.out.println("line matrix 2");
		printMatrixFormatted(j);
		//jls.plotline(lineAPoint1[0], lineAPoint2[0], lineAPoint1[1], lineAPoint2[1], c.get(0,0), n);
		 */
	}

	public Point[] getCorrectedLineA() {


		return null;

	}

	public Point[] getCorrectedLineB() {
		return null;

	}



	/*
	public Matrix theBigAMaker(double[] x, double[] y) {
		//Take a set of points describing a polygon
		//Return an A matrix for use in JLSConstrain.
		//The A matrix takes the form:
		// [ op zp zp zp Px  Py
		//   zq oq zq zq Qy -Qx ]

		if (x.length != y.length) {
			System.out.println("Big A maker fail. Polyline looks corrupt");
			return null;
		} else {
			//double[][] vals = new Array();
			int numOfLeftPartColumns = x.length; //We want the number line segments, one extra to connect to first point
			int numOfColumns = x.length + 2; //We want the number line segments, one extra to connect to first point
			int numOfRows = numOfLeftPartColumns * 2;
			System.out.println("Big A numColumns: " + numOfColumns);
			System.out.println("Big A numLeftPartColumns: " + numOfLeftPartColumns);
			System.out.println("Big A numRows: " + numOfRows);
			Matrix theBigA = new Matrix(numOfRows,numOfColumns);
			int rowOffset = 0;
			int colOffset = 0;					
			//theBigA.identity(numOfRows, arg1)

			int ptIndex =0;
			boolean negCount = false;

			for (int i=0; i<numOfRows; i++) {
				if(i % 4 == 2 || i % 4 == 3) {
					negCount = true;
				} else {
					negCount = false;
				}

				theBigA.set(i, colOffset, 1.0);
				if ( i % 2 ==0) { //If even number, first vertex of line segment
					System.out.println("inc");
					theBigA.set(i,theBigA.getColumnDimension()-2,x[ptIndex]);
					if (negCount){
						theBigA.set(i,theBigA.getColumnDimension()-1,-(y[ptIndex]));
					}else {
						theBigA.set(i,theBigA.getColumnDimension()-1,y[ptIndex]);
					}
					//					ptIndex++;
				} else { //odd number
					if (ptIndex < x.length-1){
						theBigA.set(i,theBigA.getColumnDimension()-2,x[ptIndex+1]);
						if (negCount){
							theBigA.set(i,theBigA.getColumnDimension()-1,-(y[ptIndex+1]));
						}else {
							theBigA.set(i,theBigA.getColumnDimension()-1,y[ptIndex+1]);
						}
						ptIndex++;
					} else {
						theBigA.set(i,theBigA.getColumnDimension()-2,x[0]);
						if (negCount){
							theBigA.set(i,theBigA.getColumnDimension()-1,-(y[0]));
						}else {
							theBigA.set(i,theBigA.getColumnDimension()-1,y[0]);
						}
					}
					colOffset++;
				}
			}
			return theBigA;
		}
	}


	 */


	public Matrix theBigAMakerClosed(List x, List y) {
		//Take a set of points describing a closed polygon
		//Return an A matrix for use in JLSConstrain.
		//The A matrix takes the form:
		// [ op zp zp zp Px  Py
		//   zq oq zq zq Qy -Qx ]

		if (x.size() != y.size()) {
			System.out.println("Big A maker fail. Poly looks corrupt");
			return null;
		} else {
			//double[][] vals = new Array();

			//Careful here. We want the number line segments, and we 
			//already added the first point to the last point to 
			//make it a closed polygon
			int numOfLeftPartColumns = x.size() - 1 ; 

			int numOfColumns = numOfLeftPartColumns + 2; //Total number of columns in the Matrix. Left Matrix part plus an extra two
			int numOfRows = numOfLeftPartColumns * 2;
			System.out.println("Big A numColumns: " + numOfColumns);
			System.out.println("Big A numLeftPartColumns: " + numOfLeftPartColumns);
			System.out.println("Big A numRows: " + numOfRows);
			Matrix theBigA = new Matrix(numOfRows,numOfColumns);
			int rowOffset = 0;
			int colOffset = 0;					
			//theBigA.identity(numOfRows, arg1)

			int ptIndex =0;
			boolean negCount = false;

			for (int i=0; i<numOfRows; i++) {
				if(i % 4 == 2 || i % 4 == 3) {
					negCount = true;
				} else {
					negCount = false;
				}

				theBigA.set(i, colOffset, 1.0);
				if ( i % 2 ==0) { //If even number, first vertex of line segment
					System.out.println("inc");

					if (negCount){
						theBigA.set(i,theBigA.getColumnDimension()-1, -(Double) x.get(ptIndex));
						theBigA.set(i,theBigA.getColumnDimension()-2,(Double) y.get(ptIndex));
					}else {
						theBigA.set(i,theBigA.getColumnDimension()-2,(Double) x.get(ptIndex));
						theBigA.set(i,theBigA.getColumnDimension()-1,(Double) y.get(ptIndex));
					}
					//					ptIndex++;
				} else { //odd number
					if (ptIndex < x.size()-1){

						if (negCount){
							theBigA.set(i,theBigA.getColumnDimension()-1,-(Double) x.get(ptIndex+1));
							theBigA.set(i,theBigA.getColumnDimension()-2,(Double) y.get(ptIndex+1));
						}else {
							theBigA.set(i,theBigA.getColumnDimension()-2,(Double) x.get(ptIndex+1));
							theBigA.set(i,theBigA.getColumnDimension()-1,(Double) y.get(ptIndex+1));
						}
						ptIndex++;
					} 
					colOffset++;
				}
			}
			return theBigA;
		}
	}
	
	
	public Matrix theBigAMakerPolyline(List x, List y) {
		//Take a set of points describing a closed polygon
		//Return an A matrix for use in JLSConstrain.
		//The A matrix takes the form:
		// [ op zp zp zp Px  Py
		//   zq oq zq zq Qy -Qx 
		//   ... ]
		
		if (x.size() != y.size()) {
			System.out.println("Big A maker fail. Polyline looks corrupt");
			return null;
		} else {
			//double[][] vals = new Array();

			//Careful here. We want the number of line segments, and we 
			//already added the first point to the last point to 
			//make it a closed polygon
			int numOfLeftPartColumns = x.size()-1; 

			int numOfColumns = numOfLeftPartColumns + 2; //Total number of columns in the Matrix. Left Matrix part plus an extra two
			int numOfRows = numOfLeftPartColumns * 2;
			System.out.println("Big A numColumns: " + numOfColumns);
			System.out.println("Big A numLeftPartColumns: " + numOfLeftPartColumns);
			System.out.println("Big A numRows: " + numOfRows);
			Matrix theBigA = new Matrix(numOfRows,numOfColumns);
			int rowOffset = 0;
			int colOffset = 0;					
			//theBigA.identity(numOfRows, arg1)

			int ptIndex =0;
			boolean negCount = false;

			for (int i=0; i<numOfRows; i++) {
				if(i % 4 == 2 || i % 4 == 3) {
					negCount = true;
				} else {
					negCount = false;
				}
				theBigA.set(i, colOffset, 1.0);
				if ( i % 2 ==0) { //If even number, first vertex of line segment
					System.out.println("inc");

					if (negCount){
						theBigA.set(i,theBigA.getColumnDimension()-1, -(Double) x.get(ptIndex));
						theBigA.set(i,theBigA.getColumnDimension()-2,(Double) y.get(ptIndex));
					}else {
						theBigA.set(i,theBigA.getColumnDimension()-2,(Double) x.get(ptIndex));
						theBigA.set(i,theBigA.getColumnDimension()-1,(Double) y.get(ptIndex));
					}
					//					ptIndex++;
				} else { //odd number
					if (ptIndex < x.size()-1){

						if (negCount){
							theBigA.set(i,theBigA.getColumnDimension()-1,-(Double) x.get(ptIndex+1));
							theBigA.set(i,theBigA.getColumnDimension()-2,(Double) y.get(ptIndex+1));
						} else {
							theBigA.set(i,theBigA.getColumnDimension()-2,(Double) x.get(ptIndex+1));
							theBigA.set(i,theBigA.getColumnDimension()-1,(Double) y.get(ptIndex+1));
						}
						ptIndex++;
					} 
					colOffset++;
				}
			}
			return theBigA;
		}
	}
	
	public Matrix plotline(double px1,double px2,double py1,double py2,double c,Matrix n) {
		//plots the set of points (x,y)
		//and plots the straight line c+n1*x+n2*y=0 using
		//the line type defined by t

		System.out.println("plotting line");
		double xRangeMin = Math.min(px1, px2);
		double xRangeMax = Math.max(px1, px2);

		double yRangeMin = Math.min(py1, py2);
		double yRangeMax = Math.max(py1, py2);

		double x1;
		double y1;
		double x2;
		double y2;

		System.out.println("n get");
		System.out.println("nget " + n.get(0,0));

		if (n.get(0, 0) == 0)  { //c+n2*y=0  => y = -c/n(2)
			System.out.println("first if");
			x1 = xRangeMin;
			y1 = -c/n.get(1, 0); //Check for div 0 error
			x2 = xRangeMax;
			y2 = y1;
		} else if (n.get(1, 0) == 0) { //% c+n1*x=0  => x = -c/n(1)
			System.out.println("2nd if");
			y1 = yRangeMin;
			x1  =-c/n.get(0,0); //Check for div 0 error

			y2=yRangeMax;
			x2 = x1;

		} else if (xRangeMax - xRangeMin > yRangeMax - yRangeMin){
			System.out.println("3rd if");
			x1 = xRangeMin;
			y1 = -(c+n.get(0,0)*x1)/n.get(1,0);//Check for div 0 error

			x2 = xRangeMax;
			y2 = -(c+n.get(0,0)*x2)/n.get(1,0);
		} else {
			System.out.println("else");
			y1 = yRangeMin;
			x1 = -(c+n.get(1,0)*y1)/n.get(0,0);

			y2=yRangeMax; 
			x2 = -(c+n.get(1,0)*y2)/n.get(0,0);
		}

		/*
		System.out.println("plotLine x1: "+ x1);
		System.out.println("plotLine y1: "+ y1);
		System.out.println("plotLine x2: "+ x2);
		System.out.println("plotLine y2: "+ y2);
		 */
		double[][] vals = {{x1,y1},{x2,y2}};


		Matrix line = new Matrix(vals);

		return line;
	}

	public void returnCorrectedLineA() {

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
