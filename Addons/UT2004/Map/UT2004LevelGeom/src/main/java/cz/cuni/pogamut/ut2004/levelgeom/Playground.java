package cz.cuni.pogamut.ut2004.levelgeom;

import java.io.File;

import cz.cuni.pogamut.ut2004.levelgeom.xml.BspNode;
import cz.cuni.pogamut.ut2004.levelgeom.xml.Precache;
import cz.cuni.pogamut.ut2004.levelgeom.xml.StaticMesh;

public class Playground {
	public static void main(String[] args) {
		
		Precache uShock = Precache.loadXML_Use_JFlex(new File("CTF-January.xml"));
		
		long vertices = 0;
		long triangles = 0;
		
		for (BspNode node : uShock.bspNodes.bspNodes) {
			vertices += node.vertices.size();
			triangles += node.toTriangles().size();
		}
		
		for (StaticMesh mesh : uShock.staticMeshes.staticMeshes) {
			vertices += mesh.vertices.size();
			triangles += mesh.triangles.size();
		}
		
		System.out.println("Vertices: " + vertices);
		System.out.println("Triangles: " + triangles);
		
		
	}
}
