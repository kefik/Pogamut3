package cz.cuni.pogamut.ut2004.levelgeom.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector3d;

import math.geom3d.line.StraightLine3D;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cz.cuni.pogamut.ut2004.levelgeom.utils.VertexTriangle;

@XStreamAlias("bn")
public class BspNode {

	private static final double ZERO = 0.00000001;

	@XStreamAsAttribute
	@XStreamAlias("n")
	public int num;
	
	@XStreamAsAttribute
	@XStreamAlias("vc")
	public int numVertices;
	
	@XStreamImplicit(itemFieldName="v")
	public List<Vertex> vertices;
	
	public List<VertexTriangle> toTriangles() {
		if (vertices == null || vertices.size() < 3) return new ArrayList<VertexTriangle>();
		Vertex a = vertices.get(0);
		Vertex b = vertices.get(1);
		Vertex c;
		
		List<VertexTriangle> result = new ArrayList<VertexTriangle>(vertices.size()-2);
		for (int i = 2; i < vertices.size(); ++i) {
			c = vertices.get(i);
			result.add(new VertexTriangle(a,b,c));
		}
		
		return result;
	}
	
	public void toTriangles(Collection<VertexTriangle> result) {
		if (vertices == null || vertices.size() < 3) return;
		
		if (!isConvex()) {
			throw new RuntimeException("BSPNode[num=" + num + "] is not convex!");
		}
		
		Vertex a = vertices.get(0);
		Vertex b;
		Vertex c;
		
		for (int i = 1; i < vertices.size()-1; ++i) {
			b = vertices.get(i);
			c = vertices.get(i+1);
			result.add(new VertexTriangle(a,b,c));
		}
	}

	private boolean isConvex() {
		return true;
//		if (vertices == null || vertices.size() < 3) return true;
//		
//		Vertex a = vertices.get(vertices.size()-2);
//		Vertex b = vertices.get(vertices.size()-1);
//		Vertex c = vertices.get(0);		
//		double angleSum = 0;		
//		angleSum += angle(a,b,c);
//		
//		a = b;
//		b = c;
//		c = vertices.get(1);
//		angleSum += angle(a,b,c);
//		
//		a = b;
//		b = c;
//		c = vertices.get(2);
//		angleSum += angle(a,b,c);
//		
//		for (int i = 3; i < vertices.size(); ++i) {
//			a = b;
//			b = c;
//			c = vertices.get(i);			
//			angleSum += angle(a, b, c);
//		}
//		
//		double targetAngle = Math.PI/2 * (vertices.size()-2);
//		
//		return Math.abs(angleSum - targetAngle) < ZERO;
	}

	private double angle(Vertex p2, Vertex p1, Vertex p3) {
		
		StraightLine3D line3D = new StraightLine3D(p1.asJavaGeomPoint3D(), p2.asJavaGeomPoint3D());
		if (line3D.contains(p3.asJavaGeomPoint3D())) {
			return Math.PI/2;
		}
		
		Vertex v1 = p1.sub(p2);
		Vertex v2 = p1.sub(p3);
		
		
		double dot = v1.dot(v2);
		if (Math.abs(dot) < ZERO) {
			
			return Math.PI/4;
		}
		
		double v1Norm = v1.norm();
		double v2Norm = v2.norm();
		double norm = v1Norm * v2Norm;
		
		double angle = Math.acos(dot / norm);
		
		return angle;
		
		//return Math.acos(v1.dot(v2) / (v1.norm() * v2.norm()));
	}

	public void scale(double ratio) {
		if (vertices == null) return;
		for (Vertex vertex : vertices) {
			vertex.scaleInPlace(ratio);
		}
	}

	public double[] getMaxRange() {
		
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double minZ = Double.POSITIVE_INFINITY;
		
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double maxZ = Double.NEGATIVE_INFINITY;

		if (vertices != null) {
			for (Vertex vertex : vertices) {
				if (vertex.x < minX) minX = vertex.x;
				if (vertex.y < minY) minY = vertex.y;
				if (vertex.z < minZ) minZ = vertex.z;
				
				if (vertex.x > maxX) maxX = vertex.x;
				if (vertex.y > maxY) maxY = vertex.y;
				if (vertex.z > maxZ) maxZ = vertex.z;
			}
		}
		
		return new double[]{minX,maxX,minY,maxY,minZ,maxZ};
	}

	public int pruneVertices() {
		int pruned = 0;
		for (int i = 0; i < vertices.size(); ) {
			int index1 = i-1;
			int index2 = i;
			int index3 = i+1;
			if (index1 < 0) index1 = vertices.size()-1;
			if (index3 >= vertices.size()) index3 = 0;
			Vertex v1 = vertices.get(index1);
			Vertex v2 = vertices.get(index2);
			Vertex v3 = vertices.get(index3);
			StraightLine3D line3D = new StraightLine3D(v1.asJavaGeomPoint3D(), v3.asJavaGeomPoint3D());
			if (line3D.contains(v2.asJavaGeomPoint3D())) {
				// v2 DOES lie inside line v1-v3
				vertices.remove(i);
				++pruned;
			} else {
				// v2 does not lie inside line v1-v3
				++i;
			}
		}
		numVertices = vertices.size();
		return pruned;
	}

	public void round(int bspNodesPrecision) {
		double precision10 = Math.pow(10, bspNodesPrecision);
		for (Vertex vertex : vertices) {
			vertex.round(precision10);
		}
	}
	
}
