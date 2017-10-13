package math.geom3d.transform;

import static org.junit.Assert.*;

import math.geom3d.Point3D;
import math.geom3d.Vector3D;

import org.junit.Test;

public class AffineTransform3DTest {

    @Test
    public void testCreateTranslation() {
        Vector3D translationVector = new Vector3D( 1, 2, 3);
        AffineTransform3D translation = AffineTransform3D.createTranslation( translationVector );
        Point3D original = new Point3D( 3.14, 1.41, 10000);
        Point3D translated = translation.transformPoint(original);
        assertEquals( original.plus(translationVector), translated);
    }

    @Test
    public void testCreateRotation() {
        double angle = Math.PI / 4;
        AffineTransform3D rotationX = AffineTransform3D.createRotationOx( angle );
        AffineTransform3D rotationY = AffineTransform3D.createRotationOy( angle );
        AffineTransform3D rotationZ = AffineTransform3D.createRotationOz( angle );
        double originalCoordOnAxis = 5;
        double originalCoordAlpha = 12;
        double originalCoordBeta = 3.14;
        double rotatedCoordAlpha = originalCoordAlpha*Math.cos(angle) - originalCoordBeta*Math.sin(angle);
        double rotatedCoordBeta = originalCoordAlpha*Math.sin(angle) + originalCoordBeta*Math.cos(angle);
        Point3D originalX = new Point3D( originalCoordOnAxis, originalCoordAlpha, originalCoordBeta);
        Point3D originalY = new Point3D( originalCoordBeta, originalCoordOnAxis,  originalCoordAlpha);
        Point3D originalZ = new Point3D( originalCoordAlpha, originalCoordBeta, originalCoordOnAxis);
        Point3D rotatedAroundX = rotationX.transformPoint(originalX);
        Point3D rotatedAroundY = rotationY.transformPoint(originalY);
        Point3D rotatedAroundZ = rotationZ.transformPoint(originalZ);
        assertEquals( new Point3D( originalCoordOnAxis, rotatedCoordAlpha, rotatedCoordBeta), rotatedAroundX );
        assertEquals( new Point3D( rotatedCoordBeta, originalCoordOnAxis, rotatedCoordAlpha), rotatedAroundY );
        assertEquals( new Point3D( rotatedCoordAlpha, rotatedCoordBeta, originalCoordOnAxis), rotatedAroundZ );
    }

    @Test
    public void testCreateScaling() {
        Vector3D scalingVector = new Vector3D( 1, 2, 3);
        AffineTransform3D scaling = AffineTransform3D.createScaling( scalingVector );
        Point3D original = new Point3D( 3, 4, 5);
        Point3D scaled = scaling.transformPoint(original);
        assertEquals(
            new Point3D( 
                original.getX()*scalingVector.getX(),
                original.getY()*scalingVector.getY(),
                original.getZ()*scalingVector.getZ()
            ), 
            scaled
        );
    }
    
    @Test
    public void testConcatenate() {
        
        Point3D original = new Point3D(  12, 3.14, 5 );
        Point3D expectedTransformed = original;
        
        AffineTransform3D transformation = new AffineTransform3D();
        assertEquals( expectedTransformed, transformation.transformPoint(original) );
        
        {
            double angle = Math.PI / 4;
            double rotatedCoordAlpha = original.getX()*Math.cos(angle) - original.getY()*Math.sin(angle);
            double rotatedCoordBeta = original.getX()*Math.sin(angle) + original.getY()*Math.cos(angle);
            expectedTransformed = new Point3D( rotatedCoordAlpha, rotatedCoordBeta, original.getZ() );
            transformation = transformation.concatenate( AffineTransform3D.createRotationOz(angle) );
        }
        assertEquals( expectedTransformed, transformation.transformPoint(original) );
        
        {
            Vector3D offset = new Vector3D( 74, -20.5, -0.1 );
            expectedTransformed = expectedTransformed.plus(offset);
            transformation = transformation.concatenate( AffineTransform3D.createTranslation(offset) );
        }
        assertEquals( expectedTransformed, transformation.transformPoint(original) );
        
        {
            Vector3D scaling = new Vector3D( 1, 2, 0.5 );
            expectedTransformed = new Point3D(
                expectedTransformed.getX()*scaling.getX(),
                expectedTransformed.getY()*scaling.getY(),
                expectedTransformed.getZ()*scaling.getZ()
            );
            transformation = transformation.concatenate( AffineTransform3D.createScaling(scaling) );
        }
        assertEquals( expectedTransformed, transformation.transformPoint(original) );
    }
}
