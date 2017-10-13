package SocialSteeringsBeta;

import javax.vecmath.Vector3d;


/**
 *
 * @author petr
 * Wrapper, mainly for social steerings to hold some other information
 * @see speed/accurancy problem
 */
public class SteeringResult extends Vector3d {

    private double accurancyMultiplier;
    
    public SteeringResult(Vector3d force, double accurancyMPL)
    {
        x = force.x;
        y = force.y;
        z = force.z;
        this.accurancyMultiplier = accurancyMPL;
    }
    public Vector3d getForce()
    {
        return (Vector3d)this;
    }
    public double getAccurancyMultiplier()
    {
        return this.accurancyMultiplier;
    }

    void setMult(double d) {
        this.accurancyMultiplier = d;
    }
}
