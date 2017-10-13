package SteeringProperties;

import SteeringStuff.SteeringType;
import XMLSteeringProperties.XMLPeopleAvoidanceProperties;

/**
 * The steering properties of the People Avoidance Steering.
 * @author Marki
 */
public class PeopleAvoidanceProperties extends SteeringProperties {

    /**Steering properties: the magnitude of the repulsive force from other agents.
     * Reasonable values are 0 - 1000, the default value is 200.*/
    
    private int repulsiveForce;
    /**Steering properties: the ideal distance from other people.
     * Reasonable values are 0 - 2000, the default value is 300.
     * The steering doesn't guarantee that other agents won't get closer,
     * but if they get, the agent will be repulsed from them.*/
    private int distance;

    /**This parameter causes that agent is able to go round other agent, if it's reasonable. Recommended value is true.*/
    private boolean circumvention;

    /**This parameter causes that agent is able to decelerate, if it's reasonable. Recommended value is true.*/
    private boolean deceleration;

    /**This parameter causes that agent is able to accelerate, if it's reasonable. Recommended value is true.*/
    private boolean acceleration;

    /**The projection is used in the case of the parameters circumvention, deceleration and acceleration. The motion is projection ahead for projection ticks.*/
    private double projection;

    public PeopleAvoidanceProperties() {
        super(SteeringType.PEOPLE_AVOIDANCE);
        this.repulsiveForce = 200;
        this.distance = 300;
        this.circumvention = false;
        this.deceleration = false;
        this.acceleration = false;
        this.projection = 16;
    }

    public PeopleAvoidanceProperties(BehaviorType behaviorType) {
        super(SteeringType.PEOPLE_AVOIDANCE, behaviorType);
        this.repulsiveForce = 200;
        this.distance = 300;
        this.circumvention = false;
        this.deceleration = false;
        this.acceleration = false;
        this.projection = 16;
        setNewBehaviorType(behaviorType);
    }

    public PeopleAvoidanceProperties(XMLPeopleAvoidanceProperties xml) {
        super(SteeringType.PEOPLE_AVOIDANCE, xml.active, xml.weight, xml.behavior);
        this.repulsiveForce = xml.repulsiveForce;
        this.distance = xml.distance;
        this.circumvention = xml.circumvention;
        this.deceleration = xml.deceleration;
        this.acceleration = xml.acceleration;
        this.projection = xml.projection;
    }

    public PeopleAvoidanceProperties(int forceFromOtherPeople, int distanceFromOtherPeople, boolean goRoundPartner, boolean deceleration, boolean acceleration, double visionInTicks) {
        super(SteeringType.PEOPLE_AVOIDANCE);
        this.repulsiveForce = forceFromOtherPeople;
        this.distance = distanceFromOtherPeople;
        this.circumvention = goRoundPartner;
        this.deceleration = false;
        this.acceleration = false;
        this.projection = visionInTicks;
    }

    protected void setNewBehaviorType(BehaviorType behaviorType) {
        if (behaviorType.equals(BehaviorType.BASIC)) {
            circumvention = false;
            deceleration = false;
            acceleration = false;
            projection = 0;
        }
        else if (behaviorType.equals(BehaviorType.ADVANCED)) {
            circumvention = true;
            deceleration = true;
            acceleration = true;
            projection = 16;
        }
    }

    public int getRepulsiveForce() {
        return repulsiveForce;
    }

    public void setRepulsiveForce(int orderOfTheForce) {
        this.repulsiveForce = orderOfTheForce;
    }

    public int getDistanceFromOtherPeople() {
        return distance;
    }

    public void setDistanceFromOtherPeople(int distanceFromOtherPeople) {
        this.distance = distanceFromOtherPeople;
    }

    public boolean isCircumvention() {
        return circumvention;
    }

    public void setCircumvention(boolean goRoundPartner) {
        this.circumvention = goRoundPartner;
    }

    /**
     *
     * @return projection in ticks (how many ticks ahead we anticipate).
     */
    public double getProjection() {
        return projection;
    }

    /**
     * Sets the projection - how many tick ahead we anticipate.
     * @param projection
     */
    public void setProjection(double projection) {
        this.projection = projection;
    }

    public boolean isAcceleration() {
        return acceleration;
    }

    public void setAcceleration(boolean acceleration) {
        this.acceleration = acceleration;
    }

    public boolean isDeceleration() {
        return deceleration;
    }

    public void setDeceleration(boolean deceleration) {
        this.deceleration = deceleration;
    }

    @Override
    public String getSpecialText() {
        String text = "";
        text += "  * Repulsive Force: " + repulsiveForce + "\n";
        text += "  * Distance: " + distance + "\n";
        text += "  * Circumvention: " + circumvention + "\n";
        text += "  * Deceleration: " + deceleration + "\n";
        text += "  * Acceleration: " + deceleration + "\n";
        text += "  * Projection: " + projection + "\n";
        return text;
    }

    @Override
    public void setProperties(SteeringProperties newProperties) {
        this.repulsiveForce = ((PeopleAvoidanceProperties)newProperties).getRepulsiveForce();
        this.distance = ((PeopleAvoidanceProperties)newProperties).getDistanceFromOtherPeople();
        this.circumvention = ((PeopleAvoidanceProperties)newProperties).isCircumvention();
        this.deceleration = ((PeopleAvoidanceProperties)newProperties).isDeceleration();
        this.acceleration = ((PeopleAvoidanceProperties)newProperties).isAcceleration();
        this.projection = ((PeopleAvoidanceProperties)newProperties).getProjection();
    }
    
    public XMLPeopleAvoidanceProperties getXMLProperties() {
        XMLPeopleAvoidanceProperties xmlProp = new XMLPeopleAvoidanceProperties();
        xmlProp.repulsiveForce = repulsiveForce;
        xmlProp.distance = distance;
        xmlProp.circumvention = circumvention;
        xmlProp.deceleration = deceleration;
        xmlProp.acceleration = acceleration;
        xmlProp.projection = projection;
        xmlProp.active = active;
        xmlProp.weight = weight;
        xmlProp.behavior = behaviorType;
        return xmlProp;
    }
}
