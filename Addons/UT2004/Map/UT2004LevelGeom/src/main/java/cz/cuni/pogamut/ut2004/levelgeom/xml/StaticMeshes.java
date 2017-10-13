package cz.cuni.pogamut.ut2004.levelgeom.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("staticmeshes")
public class StaticMeshes {
	
	@XStreamImplicit(itemFieldName="staticmesh")
	public List<StaticMesh> staticMeshes;

	public void adjustVertices() {
		if (staticMeshes == null) return;
		for (StaticMesh staticMesh : staticMeshes) {
			staticMesh.adjustVertices();
		}		
	}

}
