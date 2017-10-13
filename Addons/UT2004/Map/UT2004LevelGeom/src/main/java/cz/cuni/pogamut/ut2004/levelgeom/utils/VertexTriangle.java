package cz.cuni.pogamut.ut2004.levelgeom.utils;

import cz.cuni.pogamut.ut2004.levelgeom.xml.Vertex;

public class VertexTriangle {
	
	public Vertex[] vertices;
	
	public VertexTriangle(Vertex a, Vertex b, Vertex c) {
		vertices = new Vertex[]{a,b,c};
	}

}
