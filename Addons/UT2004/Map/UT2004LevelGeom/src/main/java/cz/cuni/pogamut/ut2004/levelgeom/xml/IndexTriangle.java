package cz.cuni.pogamut.ut2004.levelgeom.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("triangle")
public class IndexTriangle {

	@XStreamAsAttribute
	@XStreamAlias("i1")
	public int i1;
	
	@XStreamAsAttribute
	@XStreamAlias("i2")
	public int i2;
	
	@XStreamAsAttribute
	@XStreamAlias("i3")
	public int i3;
	
}
