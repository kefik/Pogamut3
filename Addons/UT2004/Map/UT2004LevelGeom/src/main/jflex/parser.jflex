package cz.cuni.pogamut.ut2004.levelgeom.utils;

import java.io.Reader;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.cuni.pogamut.ut2004.levelgeom.xml.*;


%%

%class UShockParser
%public
%unicode
%line
%char
%full
%type Precache
%switch

%eofval{
throw new RuntimeException("EOF");
%eofval}

%{
    public Precache precache = new Precache();
    Terrain terrain = null;
    StaticMeshes staticMeshes = null;
    StaticMesh staticMesh = null;
    BspNodes bspNodes = null;
    BspNode bspNode = null;
    
    private int[] stack = new int[10];
    private int stack_pos = 0;
  
    public void setReader(Reader reader) {
        //yyreset() closes stream and resets scanner to be able to read from next stream
		yyreset(reader);
	}
	
	public void close() throws java.io.IOException {
        //yyclose() closes stream and goes to EOF section
		yyclose();
	}
  
    /**
     * Change state to state and push original state to stack, so parser can return in state_return
     */
    private void state_go(int state)
    {
        //yystate() - returns actual dictionary state
        //yybegin(int STATE) - sets the STATE state in the scanner
  	    stack[stack_pos++] = yystate();
  	    yybegin(state);
    }
  
    /**
     * Return to previous state.
     */
    private void state_return()
    {
        //yybegin(int STATE) - sets the STATE state in the scanner
  	    yybegin(stack[--stack_pos]);
    }
  
  	private static final Pattern VALUE = Pattern.compile("\"([^\"]*)\"");
  
    private static List<String> getValues(String txt) {
    	List<String> result = new ArrayList<String>();
    	Matcher m = VALUE.matcher(txt);
    	if (!m.find()) return result;
    	result.add(txt.substring(m.start(1), m.end(1)));
    	while (m.find(m.end())) {
    		result.add(txt.substring(m.start(1), m.end(1)));
    	}
    	return result;
    }
    
    private static double getDouble(String txt) {
    	return Double.parseDouble(txt);
    }
    
    private static int getInt(String txt) {
    	return Integer.parseInt(txt);
    }
    
    private static double[] getDouble3(String txt) {
    	List<String> values = getValues(txt);
    	return new double[]{ getDouble(values.get(0)), getDouble(values.get(1)), getDouble(values.get(2)) };
    }
    
    private static int[] getInt3(String txt) {
    	List<String> values = getValues(txt);
    	return new int[]{ getInt(values.get(0)), getInt(values.get(1)), getInt(values.get(2)) };
    }
    
    private static int[] getInt2(String txt) {
    	List<String> values = getValues(txt);
    	return new int[]{ getInt(values.get(0)), getInt(values.get(1)) };
    }    
    
  	public Vertex getVertex(String txt) {
  	    Vertex v = new Vertex();
  	    double[] xyz = getDouble3(txt);
  	    v.x = xyz[0];
  	    v.y = xyz[1];
  	    v.z = xyz[2];
  	    return v;
  	}
  	
  	public IndexTriangle getTriangle(String txt) {
  		IndexTriangle t = new IndexTriangle();
  	    int[] xyz = getInt3(txt);
  	    t.i1 = xyz[0];
  	    t.i2 = xyz[1];
  	    t.i3 = xyz[2];
  	    return t;
  	}
  	
  	public Location getLocation(String txt) {
  		Location v = new Location();
  	    double[] xyz = getDouble3(txt);
  	    v.x = xyz[0];
  	    v.y = xyz[1];
  	    v.z = xyz[2];
  	    return v;
  	}
  	
  	public Rotation getRotation(String txt) {
  		Rotation v = new Rotation();
  	    double[] xyz = getDouble3(txt);
  	    v.yaw = xyz[0];
  	    v.roll = xyz[1];
  	    v.pitch = xyz[2];
  	    return v;
  	}
  	
  	public Scale getScale(String txt) {
  		Scale v = new Scale();
  	    double[] xyz = getDouble3(txt);
  	    v.x = xyz[0];
  	    v.y = xyz[1];
  	    v.z = xyz[2];
  	    return v;
    }
    
    public Material getMaterial(String txt) {
    	Material v = new Material();
  	    int[] uv = getInt2(txt);
  	    v.uSize = uv[0];
  	    v.vSize = uv[1];
  	    return v;
    }
%}

ALPHA=[A-Za-z]
ALPHA_NUMERIC={ALPHA}|{DIGIT}
SEP=[_\-.]
ALPHA_NUMERIC_SEP = {ALPHA}|{DIGIT}|{SEP}

DIGIT=[0-9]
NONNEWLINE_WHITE_SPACE_CHAR=[\ \t\b\012]
NEWLINE=\r|\n|\r\n
WHITE_SPACE_CHAR=[\n\r\ \t\b\012]
UINT = {DIGIT}+

FLit1    = {DIGIT}+ \. {DIGIT}* 
FLit2    = \. {DIGIT}+ 
FLit3    = {DIGIT}+
EXPONENT = "e" {INT}

INT = \-? {UINT}
DOUBLE   = \-?({FLit1}|{FLit2}|{FLit3}){EXPONENT}?

%state PRECACHE BSP_NODES BSP_NODE STATIC_MESHES STATIC_MESH TERRAIN

%%

<YYINITIAL> {
	"<precache>" { 
		state_go(PRECACHE); 
	}
}

<PRECACHE> {
	"</precache>" { 
		state_return();
		return precache;
	}
	
	"<staticmeshes>" { 	 
		if (precache.staticMeshes == null) precache.staticMeshes = staticMeshes = new StaticMeshes();
		state_go(STATIC_MESHES);
	}
	
	"<terrain>" { 	
		if (precache.terrains == null) precache.terrains = new ArrayList<Terrain>();
		terrain = new Terrain();
		terrain.vertices = new ArrayList<Vertex>();
		terrain.triangles = new ArrayList<IndexTriangle>();
		precache.terrains.add(terrain);
		state_go(TERRAIN); 
	}
	
	"<bspNodes size=\"" {INT} "\">" { 	
		if (precache.bspNodes == null) precache.bspNodes = bspNodes = new BspNodes();
		state_go(BSP_NODES); 
	}
}

<STATIC_MESHES> {
	"</staticmeshes>" { 
		state_return();
	}
	
	"<staticmesh>" { 	
		if (staticMeshes.staticMeshes == null) staticMeshes.staticMeshes = new ArrayList<StaticMesh>();
		staticMesh = new StaticMesh();
		staticMesh.vertices = new ArrayList<Vertex>();
		staticMesh.triangles = new ArrayList<IndexTriangle>();
		staticMeshes.staticMeshes.add(staticMesh);
		state_go(STATIC_MESH);
	}
}

<TERRAIN> {
	"</terrain>" { 
		state_return();
	}
	
	"<v x=\"" {DOUBLE} "\" y=\"" {DOUBLE} "\" z=\"" {DOUBLE} "\" />" {
		Vertex vertex = getVertex(yytext());
		terrain.vertices.add(vertex);
	}
	
	"<t i1=\"" {INT} "\" i2=\"" {INT} "\" i3=\"" {INT} "\" />" {
		IndexTriangle triangle = getTriangle(yytext());
		terrain.triangles.add(triangle);
	}
	
	"<location x=\"" {DOUBLE} "\" y=\"" {DOUBLE} "\" z=\"" {DOUBLE} "\" />" {
		Location location = getLocation(yytext());
		terrain.location = location;
	}
	
	"<rotation yaw=\"" {DOUBLE} "\" roll=\"" {DOUBLE} "\" pitch=\"" {DOUBLE} "\" />" {
		terrain.rotation = getRotation(yytext());
	}
	
	"<scale x=\"" {DOUBLE} "\" y=\"" {DOUBLE} "\" z=\"" {DOUBLE} "\" />" {
		terrain.scale = getScale(yytext());
	}
	
	"<material usize=\"" {INT} "\" vsize=\"" {INT} "\" />" {
		terrain.material = getMaterial(yytext());
	}
}

<STATIC_MESH> {
	"</staticmesh>" { 
		state_return();
	}
	
	"<v x=\"" {DOUBLE} "\" y=\"" {DOUBLE} "\" z=\"" {DOUBLE} "\" />" {
		Vertex vertex = getVertex(yytext());
		staticMesh.vertices.add(vertex);
	}
	
	"<t i1=\"" {INT} "\" i2=\"" {INT} "\" i3=\"" {INT} "\" />" {
		IndexTriangle triangle = getTriangle(yytext());
		staticMesh.triangles.add(triangle);
	}
	
	"<location x=\"" {DOUBLE} "\" y=\"" {DOUBLE} "\" z=\"" {DOUBLE} "\" />" {
		staticMesh.location = getLocation(yytext());
	}
	
	"<rotation yaw=\"" {DOUBLE} "\" roll=\"" {DOUBLE} "\" pitch=\"" {DOUBLE} "\" />" {
		staticMesh.rotation = getRotation(yytext());
	}
	
	"<scale x=\"" {DOUBLE} "\" y=\"" {DOUBLE} "\" z=\"" {DOUBLE} "\" />" {
		staticMesh.scale = getScale(yytext());
	}
	
}

<BSP_NODES> {
	"</bspNodes>" { 
		state_return();
	}
	
	"<bn n=\"" {INT} "\" vc=\"" {INT} "\">" {
 		if (bspNodes.bspNodes == null) bspNodes.bspNodes = new ArrayList<BspNode>();
    	bspNode = new BspNode();
    	bspNode.vertices = new ArrayList<Vertex>();
    	bspNodes.bspNodes.add(bspNode);
		state_go(BSP_NODE); 
	}
}

<BSP_NODE> {
	"</bn>" {
		state_return();
	}
	
	"<v x=\"" {DOUBLE} "\" y=\"" {DOUBLE} "\" z=\"" {DOUBLE} "\" />" {
		Vertex vertex = getVertex(yytext());
		bspNode.vertices.add(vertex);
	}

}

{WHITE_SPACE_CHAR} { }

. {
//else brach - when none of the above has been matched!
//yystate() - returns actual dictionary state
//yytext() - returns matched string
  System.out.println("Illegal character: <" + yytext() + "> + currentstate "+ yystate());  
}