/*
 * Copyright (C) 2016 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry;

import java.io.ObjectStreamException;
import java.io.Serializable;

import com.google.common.collect.Lists;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.RawLevelGeometryFile.RawTriangle;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.DrawStayingDebugLines;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import math.geom3d.Point3D;
import math.geom3d.polygon.SimplePlanarPolygon3D;

/** Level geometry triangle
 * 
 * Immutable.
 */
public class Triangle implements Serializable {
	
	private static final long serialVersionUID = 3L;
	
	public final Point3D vertices[] = new Point3D[3];
	public final transient Location verticesAsLoc[] = new Location[3];
	
	public final transient SimplePlanarPolygon3D planarPolygon;
	public final transient double signedAreaIn2dProjection;
	
	public Triangle(RawTriangle rawTriangle) {
		this( rawTriangle.vertices[0], rawTriangle.vertices[1], rawTriangle.vertices[2] );
	}
	
	public Triangle( Point3D vertexA, Point3D vertexB, Point3D vertexC) {
		if ( areVerticesOrderedClockwiseIn2dProjection( vertexA, vertexB, vertexC ) ) {
			// order vertices counter-clockwise in the 2D projection
			Point3D tmp = vertexB;
			vertexB = vertexC;
			vertexC = tmp;
		}
		
		vertices[0] = vertexA;
		vertices[1] = vertexB;
		vertices[2] = vertexC;
		verticesAsLoc[0] = new Location(vertexA);
		verticesAsLoc[1] = new Location(vertexB);
		verticesAsLoc[2] = new Location(vertexC);
		
		planarPolygon = new SimplePlanarPolygon3D( 
			Lists.newArrayList(
				vertices[0],
				vertices[1],
				vertices[2]
			)
		);
		signedAreaIn2dProjection = planarPolygon.getPolygonIn2d().getSignedArea();
	}
	
	public void draw(UT2004Server server, Location color) {
		DrawStayingDebugLines d = new DrawStayingDebugLines();
		String lines = toDebugString();
		d.setVectors(lines);
		d.setColor(color);
		d.setClearAll(false);                
		server.getAct().act(d);                           
	}

	protected String toDebugString() {
		StringBuilder debugString = new StringBuilder("");
		
		for(int i = 0; i<vertices.length; i++) {
			if ( debugString.length() > 0 ) {
				debugString.append(";");
			}
			
			Point3D v1 = vertices[i];
			Point3D v2 = vertices[(i+1)%vertices.length];
			
			debugString.append(v1.getX()+","+v1.getY()+","+v1.getZ()+";"+v2.getX()+","+v2.getY()+","+v2.getZ());
		}
		return debugString.toString(); 
	}
	
	protected static boolean areVerticesOrderedClockwiseIn2dProjection( Point3D vertexA, Point3D vertexB, Point3D vertexC ) {
		SimplePlanarPolygon3D polygon = new SimplePlanarPolygon3D( 
			Lists.newArrayList(
				vertexA,
				vertexB,
				vertexC
			)
		);
		
		return polygon.getPolygonIn2d().getSignedArea() < 0;
	}
	
	private Object readResolve() throws ObjectStreamException {
		// this correctly initializes final transient fields
		return new Triangle( vertices[0], vertices[1], vertices[2]);		
	}
}
