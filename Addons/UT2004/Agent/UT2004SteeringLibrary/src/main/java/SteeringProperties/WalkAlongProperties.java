package SteeringProperties;

import SteeringStuff.SteeringType;
import XMLSteeringProperties.XMLWalkAlongProperties;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

/**
 * The steering properties of the Walk Along Steering.
 * @author Marki
 */
public class WalkAlongProperties extends SteeringProperties {

    /**The magnitude of the main force of the steering. It is used for the attractive force to the target, attractive to/repulsive from the partner, etc.*/
    private int partnerForce;

    /**The name of the partner bot.*/
    private String partnerName;

    /**The target location. Should be same for both partners.*/
    private Location targetLocation;

    /**Ideal distance between both partners.*/
    private int distance;

    /**This parameter causes better bahevior. Partners are able to go round the other one, even if the partner is between the agent and the target. Recommended value is true.*/
    private boolean giveWayToPartner;

    /**With this parameter, the agent doesn't every time run exactly to the partner, but waits for him, if it's more reasonable.*/
    private boolean waitForPartner;

    public WalkAlongProperties() {
        super(SteeringType.WALK_ALONG);
        partnerForce = 200;
        this.partnerName = "Partner";
        targetLocation = new Location(9440,-10500,-3446.65);//Na rohu.
        distance = 500;
        giveWayToPartner = false;
        waitForPartner = false;
    }

    public WalkAlongProperties(BehaviorType behaviorType) {
        super(SteeringType.WALK_ALONG, behaviorType);
        partnerForce = 200;
        this.partnerName = "Partner";
        targetLocation = new Location(9440,-10500,-3446.65);//Na rohu.
        distance = 500;
        giveWayToPartner = false;
        waitForPartner = false;
        setNewBehaviorType(behaviorType);
    }
    
    public WalkAlongProperties(XMLWalkAlongProperties xml) {
        super(SteeringType.WALK_ALONG, xml.active, xml.weight, xml.behavior);
        partnerForce = xml.attractiveForce;
        partnerName = xml.partnerName;
        targetLocation = new Location(xml.xTargetLocation,xml.yTargetLocation,xml.zTargetLocation);
        distance = xml.distance;
        giveWayToPartner = xml.giveWayToPartner;
        waitForPartner = xml.waitForPartner;
    }

    public WalkAlongProperties(int partnerForce, String partnerName, Location targetLocation, int distanceFromThePartner, boolean giveWayToPartner, boolean waitForPartner) {
        super(SteeringType.WALK_ALONG);
        this.partnerForce = partnerForce;
        this.partnerName = partnerName;
        this.targetLocation = targetLocation;
        this.distance = distanceFromThePartner;
        this.giveWayToPartner = giveWayToPartner;
        this.waitForPartner = waitForPartner;
    }

    protected void setNewBehaviorType(BehaviorType behaviorType) {
        if (behaviorType.equals(BehaviorType.BASIC)) {
            giveWayToPartner = false;
            waitForPartner = false;
        } else if (behaviorType.equals(BehaviorType.ADVANCED)) {
            giveWayToPartner = true;
            waitForPartner = true;  //This is not necessary.
        }
    }

    public int getPartnerForce() {
        return partnerForce;
    }

    public void setPartnerForce(int attractiveForce) {
        this.partnerForce = attractiveForce;
    }

    public int getDistanceFromThePartner() {
        return distance;
    }

    public void setDistanceFromThePartner(int distanceFromThePartner) {
        this.distance = distanceFromThePartner;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public boolean isGiveWayToPartner() {
        return giveWayToPartner;
    }

    public void setGiveWayToPartner(boolean giveWayToPartner) {
        this.giveWayToPartner = giveWayToPartner;
    }

    public boolean isWaitForPartner() {
        return waitForPartner;
    }

    public void setWaitForPartner(boolean waitForPartner) {
        this.waitForPartner = waitForPartner;
    }

    @Override
    public String getSpecialText() {
        String text = "";
        text += "  * Partner Force: " + partnerForce + "\n";
        text += "  * Partner: " + partnerName + "\n";
        text += "  * Target Location: " + targetLocation.toString() + "\n";
        text += "  * Distance: " + distance + "\n";
        text += "  * Give Way: " + giveWayToPartner + "\n";
        text += "  * Wait for Partner: " + waitForPartner + "\n";
        return text;
    }

    @Override
    public void setProperties(SteeringProperties newProperties) {
        this.partnerForce = ((WalkAlongProperties)newProperties).getPartnerForce();
        this.partnerName = ((WalkAlongProperties)newProperties).getPartnerName();
        this.targetLocation = ((WalkAlongProperties)newProperties).getTargetLocation();
        this.distance = ((WalkAlongProperties)newProperties).getDistanceFromThePartner();
        this.giveWayToPartner = ((WalkAlongProperties)newProperties).isGiveWayToPartner();
        this.waitForPartner = ((WalkAlongProperties)newProperties).isWaitForPartner();
    }

    public XMLWalkAlongProperties getXMLProperties() {
        XMLWalkAlongProperties xmlProp = new XMLWalkAlongProperties();
        xmlProp.attractiveForce = partnerForce;
        xmlProp.partnerName = partnerName;
        xmlProp.distance = distance;
        xmlProp.xTargetLocation = (int) targetLocation.x;
        xmlProp.yTargetLocation = (int) targetLocation.y;
        xmlProp.zTargetLocation = (int) targetLocation.z;
        xmlProp.giveWayToPartner = giveWayToPartner;
        xmlProp.waitForPartner = waitForPartner;
        xmlProp.active = active;
        xmlProp.weight = weight;
        xmlProp.behavior = behaviorType;
        return xmlProp;
    }
}
