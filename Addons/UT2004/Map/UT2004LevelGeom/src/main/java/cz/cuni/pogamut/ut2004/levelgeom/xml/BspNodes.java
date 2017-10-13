package cz.cuni.pogamut.ut2004.levelgeom.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cz.cuni.pogamut.ut2004.levelgeom.utils.VertexTriangle;

@XStreamAlias("bspNodes")
public class BspNodes {

	@XStreamAsAttribute
	@XStreamAlias("size")
	public int size;
	
	@XStreamImplicit(itemFieldName="bn")
	public List<BspNode> bspNodes;
	
	public List<VertexTriangle> toTriangles() {
		List<VertexTriangle> result = new ArrayList<VertexTriangle>();
		
		for (BspNode node : bspNodes) {
			node.toTriangles(result);
		}
		
		return result;
	}
	
	public void scale(double ratio) {
		for (BspNode node : bspNodes) {
			node.scale(ratio);
		}
	}
	
	public double getMaxRange() {
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double minZ = Double.POSITIVE_INFINITY;
		
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double maxZ = Double.NEGATIVE_INFINITY;
		
		for (BspNode node : bspNodes) {
			double[] minMaxes = node.getMaxRange();
			if (minMaxes[0] < minX) minX = minMaxes[0];
			if (minMaxes[2] < minY) minY = minMaxes[2];
			if (minMaxes[4] < minZ) minZ = minMaxes[4];
			
			if (minMaxes[1] > maxX) maxX = minMaxes[1];
			if (minMaxes[3] > maxY) maxY = minMaxes[3];
			if (minMaxes[5] > maxZ) maxZ = minMaxes[5];
		}
		return Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ));
	}
	
	public int pruneVertices() {
		int pruned = 0;
		for (BspNode node : bspNodes) {
			pruned += node.pruneVertices();
		}
		return pruned;
	}

	public void round(int bspNodesPrecision) {
		for (BspNode node : bspNodes) {
			node.round(bspNodesPrecision);
		}		
	}

	public int removeInvalidNodes() {
		int removed = 0;
		Iterator<BspNode> iterator = bspNodes.iterator();
		while (iterator.hasNext()) {
			BspNode node = iterator.next();
			if (node.vertices == null || node.vertices.size() < 3) {
				iterator.remove();
				++removed;
			}
		}
		return removed;
	}
	
}
