package SteeringProperties;

import SteeringStuff.SteeringType;
import XMLSteeringProperties.XMLPathFollowingProperties;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavigationGraphHelper;

/**
 * The steering properties of the Stick To Path Steering.
 * @author Jimmy
 */
public class StickToPathProperties extends SteeringProperties {

    /**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = 7084968068794870946L;

	/**Steering properties: the magnitude of the repulsive force, to repulse agent from the side of the corridor.
     * Reasonable values are 0 - 1000, the default value is 200.*/
    private int repulsiveForce;

    /**The maximal distance from the axe of the path. Reasonable values are 200 - 2000.*/
    private int distance;

    /**The list of ilocated elements - vertices of the path.*/
    transient IPathFuture<ILocated> path;
    
    /** TargetLocation - if we want to compute path later, we can store here the targetLocation of the path.*/
    private Location targetLocation;

    /** Regulating Force - helps the bot to keep the direction of the path. Recommended value is 50.*/
    private double regulatingForce;

    /** The length of the projection - how much ahead we project our motion. Recommended values are 5-15.*/
    private int projection;

    public StickToPathProperties() {
        super(SteeringType.STICK_TO_PATH);
        this.repulsiveForce = 200;
        this.distance = 400;
        this.targetLocation = new Location(9440,-9500,-3446.65);
        this.path = null;
        this.regulatingForce = 50;
        this.projection = 5;
    }
    
    public StickToPathProperties(StickToPathProperties values) {
    	this();
    	this.repulsiveForce = values.repulsiveForce;
        this.distance = values.distance;
        this.targetLocation = values.targetLocation;
        this.path = values.path;
        this.regulatingForce = values.regulatingForce;
        this.projection = values.projection;
    }

    protected void setNewBehaviorType(BehaviorType behaviorType) {
        if (behaviorType.equals(BehaviorType.BASIC)) {
            regulatingForce = 0;
            projection = 5;
        } else if (behaviorType.equals(BehaviorType.ADVANCED)) {
            regulatingForce = 50;
            projection = 5;
        } 
    }

	public int getRepulsiveForce() {
        return repulsiveForce;
    }

    public void setRepulsiveForce(int orderOfTheForce) {
        this.repulsiveForce = orderOfTheForce;
    }


    public int getDistanceFromThePath() {
        return distance;
    }

    public void setDistanceFromThePath(int distanceFromThePath) {
        this.distance = distanceFromThePath;
    }

    public IPathFuture<ILocated> getPath() {
        return path;
    }

    public void setPath(IPathFuture<ILocated> path) {
        this.path = path;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    public double getRegulatingForce() {
        return regulatingForce;
    }

    public void setRegulatingForce(double regulatingForce) {
        this.regulatingForce = regulatingForce;
    }

    public int getProjection() {
        return projection;
    }

    public void setProjection(int projection) {
        this.projection = projection;
    }

    @Override
    public String getSpecialText() {
        String text = "";
        text += "  * Repulsive Force: " + repulsiveForce + "\n";
        text += "  * Target Location: " + targetLocation.toString() + "\n";
        text += "  * Distance: " + distance + "\n";
        text += "  * Regulation: " + regulatingForce + "\n";
        text += "  * Projection: " + projection + "\n";
        return text;
    }

    @Override
    public void setProperties(SteeringProperties newProperties) {
        this.repulsiveForce = ((StickToPathProperties)newProperties).getRepulsiveForce();
        this.distance = ((StickToPathProperties)newProperties).getDistanceFromThePath();
        this.targetLocation = ((StickToPathProperties)newProperties).getTargetLocation();
        this.path = ((StickToPathProperties)newProperties).getPath();
        this.regulatingForce = ((StickToPathProperties)newProperties).getRegulatingForce();
        this.projection = ((StickToPathProperties)newProperties).getProjection();
    }

    public XMLPathFollowingProperties getXMLProperties() {
        XMLPathFollowingProperties xmlProp = new XMLPathFollowingProperties();
        xmlProp.repulsiveForce = repulsiveForce;
        xmlProp.distance = distance;
        xmlProp.xTargetLocation = (int) targetLocation.x;
        xmlProp.yTargetLocation = (int) targetLocation.y;
        xmlProp.zTargetLocation = (int) targetLocation.z;
        xmlProp.regulatingForce = regulatingForce;
        xmlProp.projection = projection;
        xmlProp.active = active;
        xmlProp.weight = weight;
        xmlProp.behavior = behaviorType;
        return xmlProp;
    }
}
