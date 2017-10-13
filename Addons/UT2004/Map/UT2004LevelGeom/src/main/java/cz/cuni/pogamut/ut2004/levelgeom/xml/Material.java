package cz.cuni.pogamut.ut2004.levelgeom.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("material")
public class Material {
	
	@XStreamAsAttribute
	@XStreamAlias("usize")
	public int uSize;
	
	@XStreamAsAttribute
	@XStreamAlias("vsize")
	public int vSize;
	

}
