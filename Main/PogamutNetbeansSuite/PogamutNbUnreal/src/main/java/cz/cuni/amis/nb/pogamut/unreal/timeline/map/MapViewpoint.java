package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.Box;
import java.util.HashSet;
import java.util.Set;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

/**
 * Representation of where is observer in map and where does he look, what is its POV angle ect.
 * @author Honza
 */
public class MapViewpoint {

    private Location center;
    private Location eye;
    private Vector3d up;

    public MapViewpoint() {
        this(0, 0, 0);

    }

    public MapViewpoint(double x, double y, double z) {
        center = new Location(x, y, z);
        eye = new Location(0,0,0);
        up = new Vector3d(1, 0, 0);
    }

    public void moveEye(double deltaX, double deltaY, double deltaZ) {
        eye = new Location(eye.x + deltaX, eye.y + deltaY, eye.z + deltaZ);
        
        emitChangedViewport();
    }

    public Location getLocation() {
        return new Location(center);
    }

    public Location getEye() {
        return new Location(eye);
    }

    public Vector3d getUp() {
        return new Vector3d(up);
    }

    public Location getEye2Center() {
        return Location.sub(center, eye);
    }

    /**
     * Get distance from eye point to center point.
     * @return distance from eye to center.
     */
    public double getCenter2EyeDistance() {
        return Location.getDistance(eye, center);
    }

    public Location getRightVector() {
        Location moveUpVec = new Location(up.x, up.y, up.z);
        Location center2eye = Location.sub(getEye(), getLocation());
        Location moveRightVec = center2eye.cross(moveUpVec).getNormalized();

        return moveRightVec;
    }

    public void setLocation(double x, double y, double z) {
        this.center = new Location(x,y,z);

        emitChangedViewport();
    }

    public void setFromViewedBox(Box box) {
        this.center = new Location(box.getCenterX(), box.getCenterY(), box.getCenterZ());

        double max = Math.max(box.getDeltaX(), box.getDeltaY());

        this.eye = new Location(box.getCenterX(), box.getCenterY(), box.getCenterZ() - max);

        this.up.x = 0;
        this.up.y = 1;
        this.up.z = 0;
    }

    /**
     * Rotate eye and up vectors around axis with origin in center by angle angle.
     * @param axis angle how much rotate, in degress, by right hand, thumb direction of axis, plam in direction of angle
     * @param angle
     */
    public void rotateEye(Location axis, double angle) {
        // get vector from center to eye
        Location eyeVector = Location.sub(eye, center);

        Location newEyeVector = rotateVectorAroundAxis(eyeVector, axis, angle);
        Location newUpVector = rotateVectorAroundAxis(new Location(up.x, up.y, up.z), axis, angle);

        eye = Location.add(center, newEyeVector);
        up = new Vector3d(newUpVector.x, newUpVector.y, newUpVector.z);

        emitChangedViewport();
    }

    public void rotateCenter(Location axis, double angle) {
        ///System.out.println("RotateCenter " + angle);
        Location eyeVector = Location.sub(center, eye);

        Location newEye2Center = rotateVectorAroundAxis(eyeVector, axis, angle);
        Location newUpVector = rotateVectorAroundAxis(new Location(up.x, up.y, up.z), axis, angle);
  
        center = Location.add(eye, newEye2Center);
        up = new Vector3d(newUpVector.x, newUpVector.y, newUpVector.z);
 
//        System.out.println("RotateCenter axis: " + axis + " angle " +angle + " " + getRightVector());
//        System.out.println("RotateCenter center: " + center + " up " + up);
        emitChangedViewport();
        
    }

    /**
     * Rotate by angle around vector axis. Thumb of right hand in same direction as
     * axis vector and closing palm shows direction of rotation.
     * @param vector
     * @param axis
     * @param angle angle in degrees
     */
    public static Location rotateVectorAroundAxis(Location vector, Location axis, double angle) {
        // move eye stuff back
        double x = axis.x;
        double y = axis.y;
        double z = axis.z;

        double radianAngle = angle * Math.PI / 180;
        double c = Math.cos(radianAngle);
        double s = Math.sin(radianAngle);

        /*
        ( xx(1-c)+c 	xy(1-c)-zs  xz(1-c)+ys	 0  )
        | yx(1-c)+zs	yy(1-c)+c   yz(1-c)-xs	 0  |
        | xz(1-c)-ys	yz(1-c)+xs  zz(1-c)+c	 0  |
        (	 0          0           0            1  )
         */

        Matrix4d rotateMatrix = new Matrix4d(
                x * x * (1 - c) + c, x * y * (1 - c) - z * s, x * z * (1 - c) + y * s, 0,
                y * x * (1 - c) + z * s, y * y * (1 - c) + c, y * z * (1 - c) - x * s, 0,
                x * z * (1 - c) - y * s, y * z * (1 - c) + x * s, z * z * (1 - c) + c, 0,
                0, 0, 0, 1);

        Vector3d vec = new Vector3d(vector.x, vector.y, vector.z);

        rotateMatrix.transform(vec);

        return new Location(vec.x, vec.y, vec.z);
    }
    private Set<ViewpointListener> listeners = new HashSet<ViewpointListener>();


    // perpendicular to x axis
    public void setFrontView(Box box) {
        this.center = new Location(box.getCenterX(), box.getCenterY(), box.getCenterZ());

        double maxDelta = Math.max(box.getDeltaX(), box.getDeltaZ());

        double halfAngleRad = Math.PI * ((getViewAngle() / 2) / 180);
        double distance = (maxDelta / 2) / (Math.tan(halfAngleRad));

        this.eye = new Location(box.getCenterX(), (box.getCenterY() - (box.getDeltaY() / 2)) - distance, box.getCenterZ());

        this.up.x = 0;
        this.up.y = 0;
        this.up.z = 1;

        emitChangedViewport();
    }

    public void setSideView(Box box) {
        this.center = new Location(box.getCenterX(), box.getCenterY(), box.getCenterZ());

        double maxDelta = Math.max(box.getDeltaZ(), box.getDeltaY());

        double halfAngleRad = Math.PI * ((getViewAngle() / 2) / 180);
        double distance = (maxDelta / 2) / (Math.tan(halfAngleRad));

        this.eye = new Location(box.getCenterX() + box.getDeltaX()/2 + distance, box.getCenterY(), box.getCenterZ());

        this.up.x = 0;
        this.up.y = 0;
        this.up.z = 1;

        emitChangedViewport();
    }

    public void setTopView(Box box) {
        this.center = new Location(box.getCenterX(), box.getCenterY(), box.getCenterZ());

        double maxDelta = Math.max(box.getDeltaX(), box.getDeltaY());

        double halfAngleRad = Math.PI * ((getViewAngle() / 2) / 180);
        double distance = (maxDelta / 2) / (Math.tan(halfAngleRad));

        this.eye = new Location(box.getCenterX(), box.getCenterY(), box.getCenterZ() + box.getDeltaZ()/2 + distance);

        this.up.x = 0;
        this.up.y = 1;
        this.up.z = 0;

        emitChangedViewport();
    }

    public double getViewAngle() {
        return 45.0;
    }

    /**
     * Move center and eye in worldspace by the vector.
     *
     * @param moveVec how much and in which direction should eye and center move
     */
    public void move(Location moveVec) {
        center = this.center.add(moveVec);
        eye = this.eye.add(moveVec);

        emitChangedViewport();
    }

    /**
     * Move center and eye by deltaX,Y,Z in worldspace.
     * @param deltaX how much should eye and center move along world x axis
     * @param deltaY how much should eye and center move along world y axis
     * @param deltaZ how much should eye and center move along world z axis
     */
    public void move(double deltaX, double deltaY, double deltaZ) {
        move(new Location(deltaX, deltaY, deltaZ));
    }

    /**
     * Zoom in or out. Basically move the eye according to factor.
     * If factor if e.g. 1.1, distance of eye from center will be 10% greater than is now.
     * 
     * @param factor percentage of new distance from center to eye compared to current state
     */
    void zoom(double factor, double minDist) {
        Location center2eye = Location.sub(eye, center);

        eye = Location.add(center, center2eye.scale(factor));

        emitChangedViewport();
    }

    private void emitChangedViewport() {
        ViewpointListener[] listenersArray = listeners.toArray(new ViewpointListener[]{});

        for (ViewpointListener listener : listenersArray) {
            listener.onChangedViewpoint(this);
        }
    }

    public void addViewpointListener(ViewpointListener listener) {
        listeners.add(listener);
    }

    public boolean isViewpointListener(ViewpointListener listener) {
        return listeners.contains(listener);
    }

    public void removeViewpointListener(ViewpointListener listener) {
        listeners.remove(listener);
    }


    /**
     * Set the viewpoint so it looks at specified location.
     * Set new center and update up angle.
     * @param location location that should be in the middle of screen
     * @param axis x = 0, y = 1, z = 2, which axis is up direction?
     * @param upFlag is up at positive infinity of the axis (true) or negative infinity (false);
     */
    public void lookAt(Location location, int axis, boolean upFlag) {
        System.out.println("Look at target: " + location);

        Vector3d newEye2Center = new Vector3d();
        newEye2Center.x = location.x - eye.x;
        newEye2Center.y = location.y - eye.y;
        newEye2Center.z = location.z - eye.z;

        Vector3d currentEye2Center = new Vector3d(
                getEye2Center().x,
                getEye2Center().y,
                getEye2Center().z
                );

        double angle = currentEye2Center.angle(newEye2Center);
        System.out.println("Angle " + angle + " " + angle*180/Math.PI);
        
/*        Vector3d upVec;
        double upDir = upFlag ? 1: -1;
        center = new Location(location);

        switch (axis) {
            case 0:
                upVec = new Vector3d(upDir, 0, 0);
                break;
            case 1:
                upVec = new Vector3d(0, upDir, 0);
                break;
            case 2:
                upVec = new Vector3d(0, 0, upDir);
                break;
            default:
                throw new IllegalArgumentException("Invalid axis: " + axis);
        }

        Location eye2CenterLoc = getEye2Center();
        Vector3d eye2Center = new Vector3d();
        eye2Center.x = eye2CenterLoc.x;
        eye2Center.y = eye2CenterLoc.y;
        eye2Center.z = eye2CenterLoc.z;

        eye2Center.normalize();
        // from look direction and up vector compute "right" vector
        Vector3d right = new Vector3d();
        right.cross(eye2Center, upVec);

        up.cross(right, eye2Center);
        
        emitChangedViewport();*/
    }

    public interface ViewpointListener {

        public void onChangedViewpoint(MapViewpoint viewpoint);
    }
}
