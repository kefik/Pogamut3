package SteeringProperties;

import SteeringStuff.SteeringType;
import XMLSteeringProperties.XMLWallFollowingProperties;

/**
 * The steering properties of the Wall Following steering.
 * @author Marki
 */
public class WallFollowingProperties extends SteeringProperties {

    /** The force of the wall. Reasonable value is around 100. */
    private int wallForce;
    /** The order of the force in the range 1-10. */
    private int forceOrder;
    /**The weight of the arractive force. Default value is 1.*/
    private double attractiveForceWeight;
    /**The weight of the repulsive force. Default value is 1.*/
    private double repulsiveForceWeight;
    /**The weight of the force of the convex edges. Default value is 1.*/
    private double convexEdgesForceWeight;
    /**The weight of the force of the concave edges. Default value is 1.*/
    private double concaveEdgesForceWeight;
    /**This parameter causes, that just the forces from the actual side will be noticed. The bot goes more fluently then. Recommended value is true.*/
    private boolean justMySide;
    /**The special detection of the convex edges, helps for fluent motion. Recommended value is true.*/
    private boolean specialDetection;
    /**The good solution of the front collisions. Recommended value is true.*/
    private boolean frontCollisions;


    public WallFollowingProperties() {
        super(SteeringType.WALL_FOLLOWING);
        wallForce = 100; //73
        forceOrder = 1;
        attractiveForceWeight = 1;
        repulsiveForceWeight = 1;
        concaveEdgesForceWeight = 1;
        convexEdgesForceWeight = 1;
        justMySide = true;
        specialDetection = true;
        frontCollisions = true;
    }

    public WallFollowingProperties(BehaviorType behaviorType) {
        super(SteeringType.WALL_FOLLOWING, behaviorType);
        wallForce = 100;
        forceOrder = 1;
        attractiveForceWeight = 1;
        repulsiveForceWeight = 1;
        concaveEdgesForceWeight = 1;
        convexEdgesForceWeight = 1;
        justMySide = true;
        specialDetection = true;
        frontCollisions = true;
        setNewBehaviorType(behaviorType);
    }

    public WallFollowingProperties(XMLWallFollowingProperties xml) {
        super(SteeringType.WALL_FOLLOWING, xml.active, xml.weight, xml.behavior);
        wallForce = xml.wallForce;
        forceOrder = xml.forceOrder;
        attractiveForceWeight = xml.attractiveForceWeight;
        repulsiveForceWeight = xml.repulsiveForceWeight;
        concaveEdgesForceWeight = xml.concaveEdgesForceWeight;
        convexEdgesForceWeight = xml.convexEdgesForceWeight;
        justMySide = xml.justMySide;
        specialDetection = xml.specialDetection;
        frontCollisions = xml.frontCollisions;
    }

    public WallFollowingProperties(int wallForce, int orderOfTheForce, int attractiveForceWeight,
            int repulsiveForceWeight,  int concaveEdgesForceWeight,  int convexEdgesForceWeight,
            boolean justMySide, boolean specialDetection, boolean frontCollisions) {
        super(SteeringType.WALL_FOLLOWING);
        this.wallForce = wallForce;
        this.forceOrder = orderOfTheForce;
        this.attractiveForceWeight = attractiveForceWeight;
        this.repulsiveForceWeight = repulsiveForceWeight;
        this.concaveEdgesForceWeight = concaveEdgesForceWeight;
        this.convexEdgesForceWeight = convexEdgesForceWeight;
        this.justMySide = justMySide;
        this.specialDetection = specialDetection;
        this.frontCollisions = frontCollisions;
    }

    protected void setNewBehaviorType(BehaviorType behaviorType) {
        if (behaviorType.equals(BehaviorType.BASIC)) {
            attractiveForceWeight = 1;
            repulsiveForceWeight = 1;
            concaveEdgesForceWeight = 1;
            convexEdgesForceWeight = 1;
            justMySide = false;
            specialDetection = false;
            frontCollisions = false;
        } else if (behaviorType.equals(BehaviorType.ADVANCED)) {
            justMySide = true;
            specialDetection = true;
            frontCollisions = true;
        }
    }

    public int getWallForce() {
        return wallForce;
    }

    public void setWallForce(int force) {
        this.wallForce = force;
    }

    public int getOrderOfTheForce() {
        return forceOrder;
    }

    public void setOrderOfTheForce(int orderOfTheForce) {
        this.forceOrder = orderOfTheForce;
    }

    public double getAttractiveForceWeight() {
        return attractiveForceWeight;
    }

    public void setAttractiveForceWeight(double attractiveForceWeight) {
        this.attractiveForceWeight = attractiveForceWeight;
    }

    public double getConcaveEdgesForceWeight() {
        return concaveEdgesForceWeight;
    }

    public void setConcaveEdgesForceWeight(double concaveEdgesForceWeight) {
        this.concaveEdgesForceWeight = concaveEdgesForceWeight;
    }

    public double getConvexEdgesForceWeight() {
        return convexEdgesForceWeight;
    }

    public void setConvexEdgesForceWeight(double convexEdgesForceWeight) {
        this.convexEdgesForceWeight = convexEdgesForceWeight;
    }

    public double getRepulsiveForceWeight() {
        return repulsiveForceWeight;
    }

    public void setRepulsiveForceWeight(double repulsiveForceWeight) {
        this.repulsiveForceWeight = repulsiveForceWeight;
    }

    public boolean isFrontCollisions() {
        return frontCollisions;
    }

    public void setFrontCollisions(boolean frontCollisions) {
        this.frontCollisions = frontCollisions;
    }

    public boolean isJustMySide() {
        return justMySide;
    }

    public void setJustMySide(boolean justMySide) {
        this.justMySide = justMySide;
    }

    public boolean isSpecialDetection() {
        return specialDetection;
    }

    public void setSpecialDetection(boolean specialDetection) {
        this.specialDetection = specialDetection;
    }

    @Override
    public String getSpecialText() {
        String text = "";
        text += "  * Wall Force: " + wallForce + "\n";
        text += "  * Attractive Weight: " + attractiveForceWeight + "\n";
        text += "  * Repulsive Weight: " + repulsiveForceWeight + "\n";
        text += "  * Concave Weight: " + concaveEdgesForceWeight + "\n";
        text += "  * Convex Weight: " + convexEdgesForceWeight + "\n";
        text += "  * Just My Side: " + justMySide + "\n";
        text += "  * Special Detection: " + specialDetection + "\n";
        text += "  * Front Collisions: " + frontCollisions + "\n";
        return text;
    }

    @Override
    public void setProperties(SteeringProperties newProperties) {
        this.wallForce = ((WallFollowingProperties)newProperties).getWallForce();
        this.forceOrder = ((WallFollowingProperties)newProperties).getOrderOfTheForce();
        this.attractiveForceWeight = ((WallFollowingProperties)newProperties).getAttractiveForceWeight();
        this.repulsiveForceWeight = ((WallFollowingProperties)newProperties).getRepulsiveForceWeight();
        this.concaveEdgesForceWeight = ((WallFollowingProperties)newProperties).getConcaveEdgesForceWeight();
        this.convexEdgesForceWeight = ((WallFollowingProperties)newProperties).getConvexEdgesForceWeight();
        this.justMySide = ((WallFollowingProperties)newProperties).isJustMySide();
        this.specialDetection = ((WallFollowingProperties)newProperties).isSpecialDetection();
        this.frontCollisions = ((WallFollowingProperties)newProperties).isFrontCollisions();
    }

    public XMLWallFollowingProperties getXMLProperties() {
        XMLWallFollowingProperties xmlProp = new XMLWallFollowingProperties();
        xmlProp.wallForce = wallForce;
        xmlProp.forceOrder = forceOrder;
        xmlProp.attractiveForceWeight = attractiveForceWeight;
        xmlProp.repulsiveForceWeight = repulsiveForceWeight;
        xmlProp.concaveEdgesForceWeight = concaveEdgesForceWeight;
        xmlProp.convexEdgesForceWeight = convexEdgesForceWeight;
        xmlProp.justMySide = justMySide;
        xmlProp.specialDetection = specialDetection;
        xmlProp.frontCollisions = frontCollisions;
        xmlProp.active = active;
        xmlProp.weight = weight;
        xmlProp.behavior = behaviorType;
        return xmlProp;
    }
}



    /*public int getDistanceFromTheWall() {
        return distanceFromTheWall;
    }

    public void setDistanceFromTheWall(int distanceFromTheWall) {
        this.distanceFromTheWall = distanceFromTheWall;
    }*/