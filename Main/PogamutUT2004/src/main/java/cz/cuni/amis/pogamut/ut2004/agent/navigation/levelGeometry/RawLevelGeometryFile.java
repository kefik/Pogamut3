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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.inject.internal.Lists;

import math.geom3d.Point3D;

/** Raw level geometry file
 */
public class RawLevelGeometryFile {

	/** Triangles read from the file
	 */
	public ArrayList<RawTriangle> triangles = Lists.newArrayList();

	/** Constructor
	 * 
	 * @param coreFilename filename of files to load without the extension
	 */
	public RawLevelGeometryFile(String coreFilename, Logger log) throws IOException {     
        
		BufferedReader bufferedReader = null;
        
        try {
	        // read scale
	        
        	bufferedReader = new BufferedReader(new FileReader(coreFilename+".scale"));
	        double scale = Double.parseDouble( bufferedReader.readLine() );
	        bufferedReader.close();
	        
	        // read center
	        
	        bufferedReader = new BufferedReader(new FileReader(coreFilename + ".centre"));
	        String[] sc = bufferedReader.readLine().split("[ \\t]");
	        if ( sc.length < 3 ) {
	        	throw new IOException("Centre file invalid.");
	        }
	        double[] center = new double[3];
	        
	        for(int i=0; i<3; i++) {
	            center[i] = Double.parseDouble(sc[i]);
	        }
	        bufferedReader.close();
	        
	        // read all vertices and triangles from file
	        
	        ArrayList<Point3D> vertices = Lists.newArrayList();
	        bufferedReader = new BufferedReader(new FileReader(coreFilename + ".obj"));
	        for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
	            String[] words = line.split("[ \\t]");
	            if( words[0].equals("v") ) {
	            	if ( words.length < 4 ) {
	            		throw new IOException("Invalid vertex.");
	            	}
	            	
	                double[] v = new double[3];
	                v[0] = Double.parseDouble(words[1])*scale + center[0];
	                v[1] = Double.parseDouble(words[3])*scale + center[1];
	                v[2] = Double.parseDouble(words[2])*scale + center[2];
	                
                	vertices.add( new Point3D(v[0], v[1], v[2]) );	                
	            } else if ( words[0].equals("f") ) {
	            	if ( words.length < 4 ) {
	            		throw new IOException("Invalid triangle.");
	            	}
	            	
	                int[] t = new int[3];
	                t[0] = Integer.parseInt(words[1])-1;// vertices are indexed from 1
	                t[1] = Integer.parseInt(words[2])-1;
	                t[2] = Integer.parseInt(words[3])-1;
	                
	                RawTriangle triangle = new RawTriangle(
	       				vertices.get(t[0]),
	       				vertices.get(t[1]),
	       				vertices.get(t[2])
	       			);
	                
	                triangles.add( triangle );
	            } else {
	            	throw new IOException("Unrecognized line.");
	            }
	        }
        } catch ( FileNotFoundException e ) {
        	throw new IOException( "Required raw geometry file not found.", e );
        } catch ( NumberFormatException e ) {
        	throw new IOException( "Invalid raw geometry file format.", e );
        } finally {
        	if ( bufferedReader != null ) {
        		bufferedReader.close();
        	}
        }
	}
	
	public static class RawTriangle {
		public final Point3D[] vertices;
		
		public RawTriangle( Point3D a, Point3D b, Point3D c) {
			vertices = new Point3D[]{ a, b, c};
		}
	}
}

