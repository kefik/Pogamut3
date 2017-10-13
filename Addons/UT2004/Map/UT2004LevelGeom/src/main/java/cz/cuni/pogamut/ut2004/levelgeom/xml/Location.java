package cz.cuni.pogamut.ut2004.levelgeom.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("location")
public class Location {
	
	public Location() {		
	}
	
	public Location(Location location) {
		x = location.x;
		y = location.y;
		z = location.z;
	}

	@XStreamAsAttribute
	@XStreamAlias("x")
	public double x;
	
	@XStreamAsAttribute
	@XStreamAlias("y")
	public double y;

	@XStreamAsAttribute
	@XStreamAlias("z")
	public double z;
	
}
