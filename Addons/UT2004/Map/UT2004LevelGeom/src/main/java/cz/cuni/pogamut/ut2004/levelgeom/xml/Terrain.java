package cz.cuni.pogamut.ut2004.levelgeom.xml;

import java.util.List;

import javax.vecmath.Matrix3d;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cz.cuni.pogamut.ut2004.levelgeom.utils.PogamutLocation;
import cz.cuni.pogamut.ut2004.levelgeom.utils.PogamutRotation;

@XStreamAlias("terrain")
public class Terrain {

	public Scale scale;
	
	public Location location;
	
	public Rotation rotation;
	
	public Material material;
	
	@XStreamImplicit(itemFieldName="v")
	public List<Vertex> vertices;
	
	@XStreamImplicit(itemFieldName="t")
	public List<IndexTriangle> triangles;

	public void adjustVertices() {
		if (vertices == null) return;		
		
		// SCALATION
		// from UShock: TScale.Z *= (1.0f/256.0f); // hack
		Scale usedScale = new Scale(scale);
		usedScale.z *= ((double)1)/((double)256);

		// ROTATION
		PogamutRotation pogamutRotation = new PogamutRotation(rotation.pitch, rotation.yaw, rotation.roll);

		// TRANSLATION
		// from UShock: Matrix.Translate( Actor->Location - FVector( USize, VSize, 256 >> 1 ) * Info->TerrainScale );
		Location usedTranslation = new Location(location);
		usedTranslation.x -= material.uSize * scale.x;
		usedTranslation.y -= material.vSize * scale.y;
		usedTranslation.z -= ((double)(256 >> 1)) * scale.z;
		PogamutLocation pogamutTranslation = new PogamutLocation(usedTranslation.x, usedTranslation.y, usedTranslation.z);
	
		for (Vertex vertex : vertices) {
			
			PogamutLocation targetLocation = new PogamutLocation(vertex.x, vertex.y, vertex.z);
			
			// ROTATING
			
//			Matrix3d yaw = PogamutRotation.constructXYRot(pogamutRotation.getYaw() / 32768 * Math.PI);
//			Matrix3d pitch = PogamutRotation.constructXZRot(pogamutRotation.getPitch() / 32768 * Math.PI);
//			Matrix3d roll = PogamutRotation.constructYZRot(pogamutRotation.getRoll() / 32768 * Math.PI);
//			
//			targetLocation = targetLocation.mul(roll).mul(pitch).mul(yaw);
			
			// SCALING
			
			targetLocation = targetLocation.scaleXYZ(usedScale.x, usedScale.y, usedScale.z);
			
			// TRANSLATING
			
			targetLocation = targetLocation.add(pogamutTranslation);
			
			// OUTPUT RESULT
			vertex.x = targetLocation.x;
			vertex.y = targetLocation.y;
			vertex.z = targetLocation.z;
			
		}
		
	}
	
//	public void adjustVertices() {
//		if (vertices == null) return;		
//		
//		// from UShock: TScale.Z *= (1.0f/256.0f); // hack
//		Scale usedScale = new Scale(scale);
//		usedScale.z *= ((double)1)/((double)256);
//		
//		// from UShock: Matrix.Translate( Actor->Location - FVector( USize, VSize, 256 >> 1 ) * Info->TerrainScale );
//		Location usedLocation = new Location(location);
//		usedLocation.x -= material.uSize * scale.x;
//		usedLocation.y -= material.vSize * scale.y;
//		usedLocation.z -= ((double)(256 >> 1)) * scale.z;
//		
//		for (Vertex vertex : vertices) {
//			vertex.scaleInPlace(usedScale);
//			vertex.translateInPlace(usedLocation);
//			// rotation should not be needed according to UShock
//		}
//	}
	
}
