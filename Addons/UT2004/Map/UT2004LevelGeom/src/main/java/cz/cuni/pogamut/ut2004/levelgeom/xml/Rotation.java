package cz.cuni.pogamut.ut2004.levelgeom.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("rotation")
public class Rotation {
	
	@XStreamAsAttribute
	@XStreamAlias("yaw")
	public double yaw;
	
	@XStreamAsAttribute
	@XStreamAlias("pitch")
	public double pitch;
	
	@XStreamAsAttribute
	@XStreamAlias("roll")
	public double roll;

}
