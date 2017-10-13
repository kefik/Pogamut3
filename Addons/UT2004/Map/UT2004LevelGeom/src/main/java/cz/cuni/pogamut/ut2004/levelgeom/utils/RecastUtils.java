package cz.cuni.pogamut.ut2004.levelgeom.utils;

import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.List;

import cz.cuni.pogamut.ut2004.levelgeom.Point3D;
import cz.cuni.pogamut.ut2004.levelgeom.xml.IndexTriangle;
import cz.cuni.pogamut.ut2004.levelgeom.xml.StaticMesh;
import cz.cuni.pogamut.ut2004.levelgeom.xml.StaticMeshes;
import cz.cuni.pogamut.ut2004.levelgeom.xml.Terrain;

public class RecastUtils {
	
	private static final String NEW_LINE = System.getProperty("line.separator");

	private static NumberFormat nf = NumberFormat.getInstance();
	
	static {
		nf.setMinimumFractionDigits(9);
		nf.setMaximumFractionDigits(9);
	}
	
	public static String toString(double orig) {
		String result = nf.format(orig);
		result = result.replaceAll(",", ".");
		return result;
	}
	

	public static String toString(Point3D vertex) {
		return "v " + vertex.xStr + " " + vertex.zStr + " " + vertex.yStr;
	}
	
	public static void outputVertices(PrintWriter writer, List<Point3D> points) {
		for (Point3D point : points) {
			point.makeStrings();
			writer.println(toString(point));
		}
	}
	
	public static void outputVertexTriangles(PrintWriter writer, List<VertexTriangle> triangles) {
		for (VertexTriangle triangle : triangles) {
			writer.println("f " + (triangle.vertices[2].spaceIndex+1) + " " + (triangle.vertices[1].spaceIndex+1) + " " + (triangle.vertices[0].spaceIndex+1));
		}
	}


	public static void outputTerrainTriangles(PrintWriter writer, Terrain terrain) {
		if (terrain == null || terrain.triangles == null || terrain.vertices == null) return;
		for (IndexTriangle triangle : terrain.triangles) {
			// triangles have reversed order of indices
			writer.println("f " + (terrain.vertices.get(triangle.i3).spaceIndex+1) + " " + (terrain.vertices.get(triangle.i2).spaceIndex+1) + " " + (terrain.vertices.get(triangle.i1).spaceIndex+1));
		}
	}


	public static void outputStaticMeshesTriangles(PrintWriter writer, StaticMeshes staticMeshes) {
		if (staticMeshes == null || staticMeshes.staticMeshes == null) return;
		for (StaticMesh staticMesh : staticMeshes.staticMeshes) {
			outputStaticMeshTriangles(writer, staticMesh);
		}
		
	}

	private static void outputStaticMeshTriangles(PrintWriter writer, StaticMesh staticMesh) {
		if (staticMesh == null || staticMesh.triangles == null || staticMesh.vertices == null) return;
		for (IndexTriangle triangle : staticMesh.triangles) {
			writer.println("f " + (staticMesh.vertices.get(triangle.i1).spaceIndex+1) + " " + (staticMesh.vertices.get(triangle.i2).spaceIndex+1) + " " + (staticMesh.vertices.get(triangle.i3).spaceIndex+1));
		}
	}


	public static void outputIndexTriangles(PrintWriter writer, List<IndexTriangle> indexTriangles) {
		if(indexTriangles == null) return;
		for(IndexTriangle triangle : indexTriangles) {
			
			writer.println("f " + (triangle.i1 + 1) + " " + (triangle.i2 +1) + " " + (triangle.i3 + 1));
		}
	}

}
