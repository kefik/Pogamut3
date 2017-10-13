package cz.cuni.pogamut.ut2004.levelgeom;

import java.text.NumberFormat;

import javax.swing.text.NumberFormatter;

import cz.cuni.amis.utils.HashCode;
import cz.cuni.pogamut.ut2004.levelgeom.utils.RecastUtils;

public class Point3D {
	
	private static NumberFormat nf = NumberFormat.getInstance();
	
	static {
		nf.setMinimumFractionDigits(9);
		nf.setMaximumFractionDigits(9);
		nf.setGroupingUsed(false);
	}
	
	public static double round(double num, double precision10) {
		double result = 0;
		if (num > 0) {
			result = Math.floor(num * precision10) / precision10;
		} else {
			result = Math.ceil(num * precision10) / precision10;
		}		 
		return result;
	}
	
	public double x;
	public double y;
	public double z;
	
	private int hashCode;
	
	public String xStr;
	public String yStr;
	public String zStr;
	
	public int spaceIndex;
	
	public Point3D(boolean unrounded, double x, double y, double z, double scale) {
		this.x = x * scale;
		this.y = y * scale;
		this.z = z * scale;
		HashCode hc = new HashCode();
		hc.add(x);
		hc.add(y);
		hc.add(z);
		this.hashCode = hc.getHash();
	}
	
	public Point3D(double x, double y, double z, double precision10) {
		this.x = round(x, precision10);
		this.y = round(y, precision10);
		this.z = round(z, precision10);
		HashCode hc = new HashCode();
		hc.add(x);
		hc.add(y);
		hc.add(z);
		this.hashCode = hc.getHash();
	}
	
	public Point3D(double x, double y, double z, double precision10, double scale) {
		this.x = round(x*scale, precision10);
		this.y = round(y*scale, precision10);
		this.z = round(z*scale, precision10);
		HashCode hc = new HashCode();
		hc.add(x);
		hc.add(y);
		hc.add(z);
		this.hashCode = hc.getHash();
	}
	
	public void makeStrings() {
		this.xStr = toString(this.x);
		this.yStr = toString(this.y);
		this.zStr = toString(this.z);		
	}
	
	private String toString(double num) {
		String result = nf.format(num);
		result = result.replaceAll(",", ".");
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point3D other = (Point3D) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
	
	public String toRecast() {
		return RecastUtils.toString(this);
	}
	
	public void scale(double scale) {
		x *= scale;
		y *= scale;
		z *= scale;
	}
        
        public void translate(double[] vector) {
            x += vector[0];
            y += vector[1];
            z += vector[2];
        }

}

