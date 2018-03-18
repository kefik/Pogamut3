package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.analysis.IRawNavMesh;

public class RawNavMeshFile implements IRawNavMesh {
	
	public static final String VERTEX_ENTRY_TYPE = "v";
	public static final String POLYGON_ENTRY_TYPE = "p";

	protected ArrayList<Location> vertices = Lists.newArrayList();
	protected ArrayList<ArrayList<Integer>> polygons = Lists.newArrayList(); // polygons as lists of indices into vertices
	
    public RawNavMeshFile(File file) throws IOException {
    	
    	BufferedReader br = null;
    	
    	try {
    		br = new BufferedReader(new FileReader(file));
    		
    		parseFile(br);
    	} finally {
    		if (br != null) {
    			br.close();
    		}
    	}
    }
    
    protected void parseFile(BufferedReader br) throws IOException {
		for (String line = br.readLine(); line != null; line = br.readLine() ) {
            ArrayList<String> tokens = Lists.newArrayList( Arrays.asList(line.split("[ \\t]")) );
            String entryType = tokens.remove(0);
            
            parseEntry(entryType, tokens, line);	
		}
    }
    
    protected void parseEntry(String entryType, List<String> tokens, String line) throws IOException {
        if (entryType.equals(VERTEX_ENTRY_TYPE)) {
        	parseVertexEntry(tokens, line);
        } else if (entryType.equals(POLYGON_ENTRY_TYPE)) {
        	parsePolygonEntry(tokens, line);
        } else {
        	if (entryType != "" || tokens.size() != 1) {
        		throw new IOException("Invalid file format - unrecognized entry: "+line);
        	} else {
        		// empty line, let it slip :]
        	}
        }
    }
    
    protected void parseVertexEntry(List<String> tokens, String line) throws IOException {
    	if (tokens.size() != 3) {
    		throw new IOException("Invalid file format - vertex entry should contain exactly three coordinates: "+line);
    	}
    	
    	try {
	        vertices.add(
	        	new Location(
	        		Double.parseDouble(tokens.get(0)),
	        		Double.parseDouble(tokens.get(1)),
	        		Double.parseDouble(tokens.get(2))
	        	)
	        );
    	} catch (NumberFormatException e) {
    		throw new IOException("Invalid file format - unreadable coordinate: "+line, e);
    	}
    }
    
    protected void parsePolygonEntry(List<String> tokens, String line) throws IOException {
        ArrayList<Integer> polygon = Lists.newArrayList();
        for (String token : tokens) {
        	int vertexIndex;
        	try {
        		vertexIndex = Integer.parseInt(token);
        	} catch (NumberFormatException e) {
        		throw new IOException("Invalid file format - unreadable vertex: "+token+" in polygon entry: "+line);
        	}
        	
        	if (vertexIndex < 0 || vertices.size() <= vertexIndex) {
        		throw new IOException("Invalid file format - bad vertex index "+vertexIndex+" in polygon entry: "+line);
        	}
        	
        	polygon.add(vertexIndex);
        }
        
        polygons.add(polygon);
    }

	@Override
	public List<? extends Location> getVertices() {
		return Collections.unmodifiableList( vertices );
	}

	@Override
	public List<? extends List<Integer>> getPolygons() {
		return Collections.unmodifiableList( polygons );
	}
}
