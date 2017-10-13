package cz.cuni.pogamut.ut2004.levelgeom.xml;

import java.util.List;

import javax.vecmath.Matrix3d;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cz.cuni.pogamut.ut2004.levelgeom.utils.PogamutLocation;
import cz.cuni.pogamut.ut2004.levelgeom.utils.PogamutRotation;

@XStreamAlias("staticmesh")
public class StaticMesh {
	
	@XStreamAlias("location")
	public Location location;
	
	@XStreamAlias("rotation")
	public Rotation rotation;
	
	@XStreamAlias("scale")
	public Scale scale;
	
	@XStreamImplicit(itemFieldName="v")
	public List<Vertex> vertices;
	
	@XStreamImplicit(itemFieldName="t")
	public List<IndexTriangle> triangles;

	public void adjustVertices() {
		if (vertices == null) return;
	
		// from UShock code
		//		Matrix.Rotate( Actor->Rotation );
		//			-> rotates in order YAW, PITCH, ROLL, see UShock sources at UnMath.h:1332
		//		Matrix.Translate( Actor->Location );
		//          -> just simple addition x, y, z, see UShock sources at UnMath.h:1809
		//		Matrix.Scale( DrawScale );
		//          -> just simple multiplication, see UShock sources at UnMath.h:1815
		// => that means (unfortunately) 'rotate' then 'scale' then 'translate' :-) 
	
		PogamutRotation pogamutRotation = new PogamutRotation(rotation.pitch, rotation.yaw, rotation.roll);
		PogamutLocation pogamutTranslation = new PogamutLocation(location.x, location.y, location.z);
	
		for (Vertex vertex : vertices) {
			
			PogamutLocation targetLocation = new PogamutLocation(vertex.x, vertex.y, vertex.z);
			
			// SCALING
			
			targetLocation = targetLocation.scaleXYZ(scale.x, scale.y, scale.z);
			
			// ROTATING
			
			Matrix3d yaw = PogamutRotation.constructXYRot(pogamutRotation.getYaw() / 32768 * Math.PI);
			Matrix3d pitch = PogamutRotation.constructXZRot(pogamutRotation.getPitch() / 32768 * Math.PI);
			Matrix3d roll = PogamutRotation.constructYZRot(pogamutRotation.getRoll() / 32768 * Math.PI);
			
			targetLocation = targetLocation.mul(roll).mul(pitch).mul(yaw);
			
			
			
			// TRANSLATING
			
			targetLocation = targetLocation.add(pogamutTranslation);
			
			// OUTPUT RESULT
			vertex.x = targetLocation.x;
			vertex.y = targetLocation.y;
			vertex.z = targetLocation.z;
			
		}
		
	}

}
