package cz.cuni.pogamut.ut2004.levelgeom.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import cz.cuni.pogamut.ut2004.levelgeom.Point3D;

@XStreamAlias("v")
public class Vertex {
	
	@XStreamAsAttribute
	@XStreamAlias("x")
	public double x;
	
	@XStreamAsAttribute
	@XStreamAlias("y")
	public double y;
	
	@XStreamAsAttribute
	@XStreamAlias("z")
	public double z;
	
	public int spaceIndex;
	
	public Vertex() {		
	}
	
	public Vertex(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vertex sub(Vertex v) {
		return new Vertex(x-v.x, y-v.y, z-v.z);
	}
	
	public double dot(Vertex v) {
		return x * v.x + y * v.y + z * v.z;
	}
	
	public double norm() {
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	@Override
	public String toString() {
		return "V[x=" + x + ",y=" + y + ",z=" + z + "]";
	}

	public math.geom3d.Point3D asJavaGeomPoint3D() {
		return new math.geom3d.Point3D(x, y, z);
	}
	
	public Vertex scale(double multi) {
		return new Vertex(x*multi, y*multi, z*multi);
	}

	public void scaleInPlace(double ratio) {
		x *= ratio;
		y *= ratio;
		z *= ratio;
	}

	public void round(double precision10) {
		x = Point3D.round(x, precision10);
		y = Point3D.round(y, precision10);
		z = Point3D.round(z, precision10);
	}
	
	public void scaleInPlace(Scale scale) {
		x *= scale.x;
		y *= scale.y;
		z *= scale.z;
	}
	
	public void translateInPlace(Location location) {
		x += location.x;
		y += location.y;
		z += location.z;
	}

}
