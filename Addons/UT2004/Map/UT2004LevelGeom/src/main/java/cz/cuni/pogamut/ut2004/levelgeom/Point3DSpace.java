package cz.cuni.pogamut.ut2004.levelgeom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.pogamut.ut2004.levelgeom.xml.BspNode;
import cz.cuni.pogamut.ut2004.levelgeom.xml.BspNodes;
import cz.cuni.pogamut.ut2004.levelgeom.xml.StaticMesh;
import cz.cuni.pogamut.ut2004.levelgeom.xml.StaticMeshes;
import cz.cuni.pogamut.ut2004.levelgeom.xml.Terrain;
import cz.cuni.pogamut.ut2004.levelgeom.xml.Vertex;

public class Point3DSpace {

	private int precision;
	private double precision10;
	private double scale;
	
	private Map<Point3D, Integer> points = new HashMap<Point3D, Integer>();
	
	private double minX = Double.POSITIVE_INFINITY;
	private double minY = Double.POSITIVE_INFINITY;
	private double minZ = Double.POSITIVE_INFINITY;
	
	private double maxX = Double.NEGATIVE_INFINITY;
	private double maxY = Double.NEGATIVE_INFINITY;
	private double maxZ = Double.NEGATIVE_INFINITY;
	
	
	public Point3DSpace(int precision) {
		this.precision = precision;
		this.precision10 = Math.pow(10,  precision);
	}
	
	public double round(double num) {
		return Point3D.round(num, precision10);
	}
	
	private Point3D round(double x, double y, double z) {
		return new Point3D(x, y, z, precision10);
	}
	
	public int add(double x, double y, double z) {
		Point3D key = new Point3D(x, y, z, precision10);
		Integer index = points.get(key);
		if (index != null) return index;
		key.spaceIndex = points.size();
		points.put(key, key.spaceIndex);
		
		
		if (key.x < minX) minX = key.x;
		if (key.y < minY) minY = key.y;
		if (key.z < minZ) minZ = key.z;
		
		if (key.x > maxX) maxX = key.x;
		if (key.y > maxY) maxY = key.y;
		if (key.z > maxZ) maxZ = key.z;
		
		return key.spaceIndex;
	}
	
	public int add(Vertex vertex) {
		vertex.spaceIndex = add(vertex.x, vertex.y, vertex.z);
		return vertex.spaceIndex;
	}
	
	public void add(BspNode node) {
		if (node == null || node.vertices == null) return;
		for (Vertex vertex : node.vertices) {
			add(vertex);
		}
	}
	
	public void add(BspNodes nodes) {
		if (nodes == null || nodes.bspNodes == null) return;
		for (BspNode node : nodes.bspNodes) {
			add(node);
		}
	}
	
	public void add(Terrain terrain) {
		if (terrain == null || terrain.vertices == null) return;
		for (Vertex vertex : terrain.vertices) {
			add(vertex);
		}
	}
		
	public int size() {
		return points.size();
	}
	
	public Integer get(double x, double y, double z) {
		Point3D key = new Point3D(x, y, z, precision10);
		return points.get(key);		
	}
	
	public Integer get(Vertex vertex) {
		return vertex.spaceIndex;
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
		if (size() == 0) return 0;
		return Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ));
	}

	public List<Point3D> getPointsSorted() {
		List<Point3D> result = new ArrayList<Point3D>(points.keySet());
		Collections.sort(result, new Comparator<Point3D>() {

			@Override
			public int compare(Point3D o1, Point3D o2) {
				return o1.spaceIndex - o2.spaceIndex;
			}
			
		});		
		return result;
	}

	public void add(StaticMeshes staticMeshes) {
		if (staticMeshes == null || staticMeshes.staticMeshes == null) return;
		for (StaticMesh staticMesh : staticMeshes.staticMeshes) {
			if (staticMesh.vertices == null) continue;
			for (Vertex vertex : staticMesh.vertices) {
				add(vertex);
			}
		}
	}
	
}
