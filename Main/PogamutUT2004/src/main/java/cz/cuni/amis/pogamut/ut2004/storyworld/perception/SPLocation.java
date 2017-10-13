package cz.cuni.amis.pogamut.ut2004.storyworld.perception;

import javax.vecmath.Point3d;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;


public class SPLocation {
	
	private double x;
	
	private double y;
	
	private double z;
	
	private Object point3DMutex = new Object();
	
	private Point3d point3D = null;

	private Object locationMutex = new Object();

	private Location location = null;
	
	public SPLocation() {
		this(0,0,0);
	}

	public SPLocation(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public SPLocation(Location location) {
		this(location.x, location.y, location.z);
	}
	
	/**
	 * Used by XStream after deserialization.
	 * @return
	 */
	private SPLocation readResolve() {
		point3DMutex = new Object();
		return this;
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof SPLocation)) return false;
		SPLocation location = (SPLocation) obj;
		return asPoint3d().epsilonEquals(location.asPoint3d(), 1);
	}
	
	public Point3d asPoint3d() {
		synchronized(point3DMutex) {
			if (point3D != null) return point3D;
			point3D = new Point3d(x, y, z);
			return point3D;				
		}
	}
	
	public Location asLocation() {
		synchronized(locationMutex ) {
			if (location != null) return location;
			location = new Location(x, y, z);
			return location;				
		}
	}

	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;		
	}

	public void setZ(double z) {
		this.z = z;
	}
	
	public double distance(SPLocation location) {
		return asPoint3d().distance(location.asPoint3d());
	}
	
	public String toString() {
		return "SPLocation[" + x + ", " + y + ", " + z + "]";
	}
	
}