package cz.cuni.pogamut.ut2004.levelgeom.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.cuni.pogamut.ut2004.levelgeom.Point3D;
import cz.cuni.pogamut.ut2004.levelgeom.utils.VertexTriangle;
import cz.cuni.pogamut.ut2004.levelgeom.xml.IndexTriangle;

public class Envelope {

	private double minX;
	private double minY;
	private double minZ;
	private double maxX;
	private double maxY;
	private double maxZ;

	private boolean[] inEnvelope;
	private boolean[] keepVertice;

	public Envelope(File envelopeFile, double border) {
		try {
			FileInputStream stream = new FileInputStream(envelopeFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream));

			String line = reader.readLine();
			
			reader.close();

			String[] pointsStrings = line.split(";");
			minX = Double.parseDouble(pointsStrings[0].replace(',', '.')) - border;
			minY = Double.parseDouble(pointsStrings[1].replace(',', '.')) - border;
			minZ = Double.parseDouble(pointsStrings[2].replace(',', '.')) - border;
			maxX = Double.parseDouble(pointsStrings[3].replace(',', '.')) + border;
			maxY = Double.parseDouble(pointsStrings[4].replace(',', '.')) + border;
			maxZ = Double.parseDouble(pointsStrings[5].replace(',', '.')) + border;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private List<Point3D> filteredPoints;
	private List<VertexTriangle> filteredTriangles;
	private List<IndexTriangle> filteredIndexTriangles;

	public List<Point3D> getFilteredPoints() {
		return filteredPoints;
	}

	public List<VertexTriangle> getFilteredTriangles() {
		return filteredTriangles;
	}

	public List<IndexTriangle> getFilteredIndexTriangles() {
		return filteredIndexTriangles;
	}

	public void processData(List<Point3D> points,
			List<VertexTriangle> triangles, List<IndexTriangle> indexTriangles) {
		inEnvelope = new boolean[points.size()];

		info("Checking points in envelope...");

		int i = 0;
		for (Point3D point : points) {
			inEnvelope[i] = isInEnvelope(point);
			++i;

		}

		keepVertice = new boolean[points.size()];
		filteredTriangles = new ArrayList<VertexTriangle>();

		info("Filtering triangles...");

		for (VertexTriangle triangle : triangles) {
			boolean keepTriangle = false;
			keepTriangle |= inEnvelope[triangle.vertices[0].spaceIndex];
			keepTriangle |= inEnvelope[triangle.vertices[1].spaceIndex];
			keepTriangle |= inEnvelope[triangle.vertices[2].spaceIndex];

			if (keepTriangle) {
				filteredTriangles.add(triangle);
				keepVertice[triangle.vertices[0].spaceIndex] = true;
				keepVertice[triangle.vertices[1].spaceIndex] = true;
				keepVertice[triangle.vertices[2].spaceIndex] = true;
			}
		}

		info("Filtered triangles. Before: " + triangles.size() + " After: "
				+ filteredTriangles.size());

		filteredIndexTriangles = new ArrayList<IndexTriangle>();
		info("Filtering index triangles...");
		for (IndexTriangle triangle : indexTriangles) {
			boolean keepTriangle = false;
			keepTriangle |= inEnvelope[triangle.i1];
			keepTriangle |= inEnvelope[triangle.i2];
			keepTriangle |= inEnvelope[triangle.i3];

			if (keepTriangle) {
				filteredIndexTriangles.add(triangle);
				keepVertice[triangle.i1] = true;
				keepVertice[triangle.i2] = true;
				keepVertice[triangle.i3] = true;
			}

		}

		info("Filtered index triangles. Before: " + indexTriangles.size()
				+ " After: " + filteredIndexTriangles.size());

		//int[] newIndexes = new int[points.size()];

		int index = 0;
		//int newIndex = 0;

		info("Filtering points...");

		filteredPoints = points;
		for (Point3D point : filteredPoints) {
			//if (keepVertice[index]) {
				if (!inEnvelope[index]) {

					fitToEnvelope(point);
				}

			//	filteredPoints.add(point);
				//newIndexes[index] = newIndex;
				//++newIndex;

			//}
			++index;
		}
/*
		info("Filtered points. Before: " + points.size() + " After: "
				+ filteredPoints.size());

		info("Updating triangles vertices indexes...");

		for (VertexTriangle triangle : filteredTriangles) {
			triangle.vertices[0].spaceIndex = newIndexes[triangle.vertices[0].spaceIndex];
			triangle.vertices[1].spaceIndex = newIndexes[triangle.vertices[1].spaceIndex];
			triangle.vertices[2].spaceIndex = newIndexes[triangle.vertices[2].spaceIndex];

		}

		info("Updating index triangles vertices indexes...");

		for (IndexTriangle triangle : filteredIndexTriangles) {
			triangle.i1 = newIndexes[triangle.i1];
			triangle.i2 = newIndexes[triangle.i2];
			triangle.i3 = newIndexes[triangle.i3];

		}
		*/

		info("Envelope evaluation done.");

	}

	private void fitToEnvelope(Point3D point) {
		point.x = Math.max(minX, Math.min(point.x, maxX));
		point.y = Math.max(minY, Math.min(point.y, maxY));
		point.z = Math.max(minZ, Math.min(point.z, maxZ));
	}

	private static void info(String str) {
		System.out.println(str);
	}

	private boolean isInEnvelope(Point3D point) {

		return point.x >= minX && point.x <= maxX && point.y >= minY
				&& point.y <= maxY && point.z >= minZ && point.z <= maxZ;
	}
	
	public double getMinX() {
		return minX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMinZ() {
		return minZ;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMaxY() {
		return maxY;
	}

	public double getMaxZ() {
		return maxZ;
	}
	
	public double getMaxRange() {
		return Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ));
	}

}
