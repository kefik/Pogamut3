package SteeringProperties;

import SteeringStuff.SteeringType;
import XMLSteeringProperties.XMLObstacleAvoidanceProperties;

/**
 * The steering properties of the Obstacle Avoidance Steering.
 * @author Marki
 */
public class ObstacleAvoidanceProperties extends SteeringProperties {

    /**The magnitude of the repulsive force from the obstacles.
     * Reasonable values are 0 - 1000, the default value is 240.*/
    private int repulsiveForce;

    /**The order of the force. Possible values are 1 - 10, the default value is 1.
     * The curve of reactions to obstacles according to the order 1 is linear, 2 quadratic, etc.
     * It means that with higher order, the bot reacts less to dsitant obstacles and more to near obstacles.
     * But the value 1 is most usefull value. Other values can cause strange behavior alongside walls etc.*/
    private int forceOrder;

    /**Special solution of head-on collisions (front collisions). Basic behaviour leads to rebounding from the obstacles
     * (when the bot aims to the obstacle head-on, he turns nearly 180° round just in front of the obstacle).
     * When this parameter is on, bot turns and continues alongside the side of the obstacle. Recommended value is true.*/
    private boolean frontCollisions;

    /**Tree collisions. The default value (in basic baheviour) is false. Recommended value is true.
     * Special solution of collisions with trees and other narrow obstacles (so narrow, that just one of the rays will hit them).
     * In basic behaviour (when the switch is off), when the bot aims to the tree that just the front side rays hits, he avoids the tree from the worse side.
     * When the switch is on, he avoids the obstacle from the right (nearer) side.*/
    private boolean treeCollisions;

    /**Creates the default ObstacleAvoidanceProperties. The order is 1, front collisions true and tree collision also true.*/
    public ObstacleAvoidanceProperties() {
        super(SteeringType.OBSTACLE_AVOIDANCE);
        repulsiveForce = 240;
        forceOrder = 1;
        frontCollisions = true;
        treeCollisions = true;
    }

    /**Creates the ObstacleAvoidanceProperties - BASIC/ADVANCED.*/
    public ObstacleAvoidanceProperties(BehaviorType behaviorType) {
        super(SteeringType.OBSTACLE_AVOIDANCE, behaviorType);
        repulsiveForce = 240;
        forceOrder = 1;
        frontCollisions = true;
        treeCollisions = true;
        setNewBehaviorType(behaviorType);
    }

    /**Creates the ObstacleAvoidanceProperties from the XMLObstacleAvoidanceProperties.*/
    public ObstacleAvoidanceProperties(XMLObstacleAvoidanceProperties xml) {
        super(SteeringType.OBSTACLE_AVOIDANCE, xml.active, xml.weight, xml.behavior);
        repulsiveForce = xml.repulsiveForce;
        forceOrder = xml.forceOrder;
        frontCollisions = xml.frontCollisions;
        treeCollisions = xml.treeCollisions;
    }

    /**
     * Creates the ObstacleAvoidanceProperties.
     * @param repulsiveForce The magnitude of the repulsive force from the obstacles.
     * Reasonable values are 0 - 1000, the default value is 240.
     * @param orderOfTheForce The order of the force. Possible values are 1 - 10, the default value is 1.
     * The curve of reactions to obstacles according to the order 1 is linear, 2 quadratic, etc.
     * It means that with higher order, the bot reacts less to dsitant obstacles and more to near obstacles.
     * But the value 1 is most usefull value. Other values can cause strange behavior alongside walls etc.
     * @param frontCollisions Special solution of head-on collisions (front collisions). Basic behaviour leads to rebounding from the obstacles
     * (when the bot aims to the obstacle head-on, he turns nearly 180° round just in front of the obstacle).
     * When this parameter is on, bot turns and continues alongside the side of the obstacle. Recommended value is true.
     * @param treeCollisions Tree collisions. The default value (in basic baheviour) is false. Recommended value is true.
     * Special solution of collisions with trees and other narrow obstacles (so narrow, that just one of the rays will hit them).
     * In basic behaviour (when the switch is off), when the bot aims to the tree that just the front side rays hits, he avoids the tree from the worse side.
     * When the switch is on, he avoids the obstacle from the right (nearer) side.
     */
    public ObstacleAvoidanceProperties(int repulsiveForce, int orderOfTheForce, boolean frontCollisions, boolean treeCollisions) {
        super(SteeringType.OBSTACLE_AVOIDANCE);
        this.repulsiveForce = repulsiveForce;
        this.forceOrder = orderOfTheForce;
        this.frontCollisions = frontCollisions;
        this.treeCollisions = treeCollisions;
    }

    protected void setNewBehaviorType(BehaviorType behaviorType) {
        if (behaviorType.equals(BehaviorType.BASIC)) {
            frontCollisions = false;
            treeCollisions = false;
        } else if (behaviorType.equals(BehaviorType.ADVANCED)) {
            frontCollisions = true;
            treeCollisions = true;
        }
    }

    public int getRepulsiveForce() {
        return repulsiveForce;
    }

    public void setRepulsiveForce(int repulsiveForce) {
        this.repulsiveForce = repulsiveForce;
    }

    public int getForceOrder() {
        return forceOrder;
    }

    public void setForceOrder(int orderOfTheForce) {
        this.forceOrder = orderOfTheForce;
    }

    public boolean isFrontCollisions() {
        return frontCollisions;
    }

    public void setFrontCollisions(boolean frontCollisions) {
        this.frontCollisions = frontCollisions;
    }

    public boolean isTreeCollisions() {
        return treeCollisions;
    }

    public void setTreeCollisions(boolean treeCollisions) {
        this.treeCollisions = treeCollisions;
    }

    @Override
    public String getSpecialText() {
        String text = "";
        text += "  * Repulsive Force: " + repulsiveForce + "\n";
        text += "  * Force Order: " + forceOrder + "\n";
        text += "  * Front Collisions: " + frontCollisions + "\n";
        text += "  * Tree Collisions: " + treeCollisions + "\n";
        return text;
    }

    @Override
    public void setProperties(SteeringProperties newProperties) {
        this.repulsiveForce = ((ObstacleAvoidanceProperties)newProperties).getRepulsiveForce();
        this.forceOrder = ((ObstacleAvoidanceProperties)newProperties).getForceOrder();
        this.frontCollisions = ((ObstacleAvoidanceProperties)newProperties).isFrontCollisions();
        this.treeCollisions = ((ObstacleAvoidanceProperties)newProperties).isTreeCollisions();
    }

    public XMLObstacleAvoidanceProperties getXMLProperties() {
        XMLObstacleAvoidanceProperties xmlProp = new XMLObstacleAvoidanceProperties();
        xmlProp.repulsiveForce = repulsiveForce;
        xmlProp.frontCollisions = frontCollisions;
        xmlProp.forceOrder = forceOrder;
        xmlProp.treeCollisions = treeCollisions;
        xmlProp.active = active;
        xmlProp.weight = weight;
        xmlProp.behavior = behaviorType;
        return xmlProp;
    }
}
