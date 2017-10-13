package SteeringStuff;

import javax.vecmath.Vector3d;

/**
 * This class provides the data of one ray.
 * @author Marki
 */
public class SteeringRay {

    public String id;
    public Vector3d direction;
    public int length;

    public SteeringRay(String id, Vector3d direction, int length) {
        this.id = id;
        this.direction = direction;
        this.length = length;
    }    
}
