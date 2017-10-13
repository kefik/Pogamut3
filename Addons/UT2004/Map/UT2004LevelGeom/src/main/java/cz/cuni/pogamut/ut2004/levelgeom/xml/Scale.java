package cz.cuni.pogamut.ut2004.levelgeom.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("scale")
public class Scale {
	
	public Scale() {		
	}
	
	public Scale(Scale scale) {
		x = scale.x;
		y = scale.y;
		z = scale.z;
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
