import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


import org.nocrala.tools.gis.data.esri.shapefile.ShapeFileReader;
import org.nocrala.tools.gis.data.esri.shapefile.exception.InvalidShapeFileException;
import org.nocrala.tools.gis.data.esri.shapefile.header.ShapeFileHeader;
import org.nocrala.tools.gis.data.esri.shapefile.shape.AbstractShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.PointData;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.MultiPointZShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PointShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonZShape;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory;
import com.vividsolutions.jts.io.WKTWriter;




public class ConstrainAShapefile {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InvalidShapeFileException 
	 */
	public static void main(String[] args) throws InvalidShapeFileException, IOException {
		// TODO Auto-generated method stub

		//Get the exterior footprint
		FileInputStream is = null;
		try {

			is = new FileInputStream(
					" set shapefile location");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		ShapeFileReader r = new ShapeFileReader(is);		
		ShapeFileHeader h = r.getHeader();
		System.out.println("The shape type of this files is " + h.getShapeType());


		int total = 0;
		AbstractShape s;
		while ((s = r.next()) != null) {
			switch (s.getShapeType()) {
			case POLYGON:

				PolygonShape aPolygon = (PolygonShape) s;
				System.out.println("I read a Polygon with "
						+ aPolygon.getNumberOfParts() + " parts and "
						+ aPolygon.getNumberOfPoints() + " points");

				//For each part of a multi-part polygon
				for (int i = 0; i < aPolygon.getNumberOfParts(); i++) {
					PointData[] points = aPolygon.getPointsOfPart(i);
					System.out.println("- part " + i + " has " + points.length
							+ " points.");


					ArrayList<Double> xListPolyline = new ArrayList();
					ArrayList<Double> yListPolyline = new ArrayList();

					//For the vertices in this part of the polygon
					for(int c=0; c < points.length -1 ; c++){
						PointData currentPoint = points[c];

						xListPolyline.add(currentPoint.getX());
						yListPolyline.add(currentPoint.getY());
					}				


					System.out.println("Open polyline constrain test");

					System.out.println("xListPolyline is: " + xListPolyline.get(0));

					JPolygonConstrain  jPolygonConstrain2 = new JPolygonConstrain(xListPolyline,yListPolyline,true,false);
					System.out.println("after jls poly constrain");


					//CoojPolygonConstrain2.correctedXList
			        // declares an array of integers
	
					Coordinate[] anArray = createPolyFromList( jPolygonConstrain2.correctedXList, jPolygonConstrain2.correctedYList);
					//Coordinate[] anArray = createPolyFromList( xListPolyline, xListPolyline);
					
					
					System.out.println("anArray size " + anArray.length);
					wktWriter(anArray);
				}
				break;
			default:
				System.out.println("Read other type of shape.");
			}
			total++;
		}
		System.out.println("Total shapes read: " + total);

		is.close();


	}
	public static Coordinate[] createPolyFromList(ArrayList<Double> correctedXList, ArrayList<Double> correctedYList) {
        Coordinate[] anArray;
		int sizeOfPoly = correctedXList.size();
		System.out.println("sizeOfPoly " + sizeOfPoly );
        anArray = new Coordinate[sizeOfPoly+1];		
		for(int i = 0; i < sizeOfPoly ; i = i+1) {			
		      Coordinate coord = new Coordinate( correctedXList.get(i), correctedYList.get(i));
		      anArray[i] =coord;
		}	
	 Coordinate coord = new Coordinate( correctedXList.get(0), correctedYList.get(0));
	 anArray[sizeOfPoly] =coord;		 	 
	 return anArray;	
	}
	

	public void createPoint(){
		GeometryFactory gf = new GeometryFactory();

		Coordinate coord = new Coordinate( 1, 1 );
		Point point = gf.createPoint( coord );

		System.out.println( point );
	}

	public static void wktWriter(Coordinate[] c ){
		GeometryFactory gf = new GeometryFactory();
		//Coordinate coord = new Coordinate( 1, 1 );
		//Point point = gf.createPoint( coord );		
		LinearRing lr = gf.createLinearRing(c);				
		Polygon poly = gf.createPolygon(lr, null);				
		StringWriter writer = new StringWriter();
		WKTWriter wktWriter = new WKTWriter(2);
		
		try {
			wktWriter.write( poly, writer );
		} catch (IOException e) {            
		}

		String wkt = writer.toString();

		System.out.println( wkt );
	

		BufferedWriter bWriter = null;
		try
		{
			bWriter  = new BufferedWriter( new FileWriter( "wktout.wkt"));
			bWriter.write( wkt);

		}
		catch ( IOException e)
		{
		}
		finally
		{
			try
			{
				if ( bWriter  != null)
					bWriter.close( );
			}
			catch ( IOException e)
			{
			}
		}

	}
}
