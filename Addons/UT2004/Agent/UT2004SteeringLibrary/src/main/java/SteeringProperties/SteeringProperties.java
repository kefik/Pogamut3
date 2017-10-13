package SteeringProperties;

import SteeringStuff.SteeringType;
import java.io.Serializable;

/**
 * All the specific steering properties extend this class.
 * @author Marki
 */
public abstract class SteeringProperties implements Serializable {

    /**These types are especially for SteeringGui or other similar aplication. They distinguish the type of behavior to show user features of the basic bahavior
     * (just reimplemented steerings of Craig W. Reynolds), advanced behavior, etc. For other usage of this steering library use the type OWN (which is also default),
     * which allows to set any values you want.
     * BASIC type sets the advanced features false, 0 etc. ADVANCED type sets the full behavior with values of atributes best for majority of situations.
     * OWN doesn't change anything, but the SteeringGui sets all the properties editable.
     * (Even if the BASIC type is set, it allows you to change and use some advanced features.)*/
    public enum BehaviorType {BASIC, ADVANCED, OWN};

    /**The type of the steering.*/
    protected SteeringType type;

    /**Whether the steering is active. Steering manager doesn't use this value, but the user can.*/
    protected boolean active;

    /**The weight of the steering. The return steering force will be multiplied by this value in the combination of the steerings forces.*/
    protected double weight;

    /**The behavior type. Steering manager doesn't use this value, but user can.*/
    protected BehaviorType behaviorType;

    public SteeringProperties(SteeringType type) {
        this.type = type;
        active = false;
        weight = 1;
        behaviorType = BehaviorType.OWN;
    }

    public SteeringProperties(SteeringType type, BehaviorType behaviorType) {
        this.type = type;
        active = false;
        weight = 1;
        this.behaviorType = behaviorType;
    }

    public SteeringProperties(SteeringType type, boolean active, double weight) {
        this.type = type;
        this.active = active;
        this.weight = weight;
        behaviorType = BehaviorType.OWN;
    }

    public SteeringProperties(SteeringType type, boolean active, double weight, BehaviorType behaviorType) {
        this.type = type;
        this.active = active;
        this.weight = weight;
        this.behaviorType = behaviorType;
    }
    
    public SteeringType getType() {
        return type;
    }

    public void setType(SteeringType type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public BehaviorType getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(BehaviorType behaviorType) {
        if (this.behaviorType != behaviorType && behaviorType != BehaviorType.OWN) {
            setNewBehaviorType(behaviorType);
        }
        this.behaviorType = behaviorType;
    }

    public String getText() {
        String text = "* " + type.getName() + "\n";
        text += "  * Weight: " + weight + "\n";
        text += getSpecialText();
        return text;
    }

    protected abstract void setNewBehaviorType(BehaviorType behaviorType);

    public abstract void setProperties(SteeringProperties newProperties);

    public abstract String getSpecialText();
}
