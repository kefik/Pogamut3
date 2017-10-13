package cz.cuni.amis.pogamut.base.utils.math;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;

import javax.vecmath.Vector2d;

/**
 * A for Algebra...
 * @author Jimmy
 *
 */
public class A {
	
	public static final double DEG_TO_RAD = (Math.PI / 180);
	public static final double RAD_TO_DEG = (180 / Math.PI);
	
	
	public static Point2D projection(Location location) {
		return projection(location.getPoint3d());
	}
	
	public static Point2D projection(Velocity velocity) {
		return projection(velocity.getVector3d());
	}
	
	public static Point2D projection(Vector3d vector) {
		return new Point2D.Double(vector.getX(), vector.getY());
	}
	
	public static Point2D projection(Point3d point) {
		return new Point2D.Double(point.getX(), point.getY());
	}
	
	public static Point2D plus(Point2D p1, Point2D p2) {
		return new Point2D.Double(p1.getX()+p2.getX(), p1.getY()+p2.getY());		
	}
	
	public static Point2D multi(Point2D p, double multi) {
		return new Point2D.Double(p.getX() * multi, p.getY() * multi);
	}
	
	public static Point2D rotate(Point2D point, double rad){
		  // R(q) = ( cos q   sin q)
	      //        (-sin q   cos q)
		return new Point2D.Double( Math.cos(rad)*point.getX() - Math.sin(rad)*point.getY(),
			  			           Math.sin(rad)*point.getX() + Math.cos(rad)*point.getY());
	}
	
	public static double deg(double rad) {
		return rad * RAD_TO_DEG;
	}
	
	public static double rad(double deg) {
		return deg * DEG_TO_RAD;
	}
		
	public static double distanceFromRunningVector(Location agentLocation, Velocity runningVector, Location object) {
		Point2D location = projection(agentLocation);
		Point2D runVector = projection(runningVector.normalize());
		Line2D running = new Line2D.Double(location, plus(location, runVector));
		return running.ptLineDist(projection(object));
	}
	

	
	public static Point2D vectorSum(Point2D[] vectors) {
		if (vectors == null || vectors.length == 0) return null;
		Point2D result = vectors[0];
		for (int i = 1; i < vectors.length; ++i) {
			result = plus(result, vectors[i]);
		}
		return result;
	}

        	/**
	 * Returns degrees!
	 * @param agentLocation
	 * @param agentRotation in degrees
	 * @param object
	 * @return
	 */
	public static double lineOfSightAngle(Location agentLocation, double agentRotationRollRad, Location object) {
                Vector2d sight = new Vector2d(Math.cos(agentRotationRollRad), Math.sin(agentRotationRollRad));
		Vector2d toTarget = new Vector2d(object.x - agentLocation.x, object.y - agentLocation.y);
		double lineOfSightAngle = A.deg(sight.angle(toTarget));
		return lineOfSightAngle;
	}
}