package SteeringProperties;

import XMLSteeringProperties.XMLTarget_packet;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

/**
 * This is one packet of the location and it's definition of force. The class TargetApproachingProperties uses this Target_packet.
 * @author Marki
 */
public class Target_packet {

    /**The target location. Bot is attracted to or repulsed from this location - accordingly to the force, which is defined in the force_packet.*/
    private Location targetLocation;
    
    /**this packet contains the whole function of the force to the target.*/
    private Force_packet force_packet;

    public Target_packet() {
        force_packet = new Force_packet(100);
        targetLocation = new Location(9440,-10500,-3446.65); //At the edge.
    }

    public Target_packet(XMLTarget_packet xml_packet) {
        force_packet = new Force_packet(xml_packet.force_packet);
        targetLocation = new Location(xml_packet.xTargetLocation,xml_packet.yTargetLocation,xml_packet.zTargetLocation);
    }

    public Target_packet(Location targetLocation, Force_packet force_packet) {
        this.targetLocation = targetLocation;
        this.force_packet = force_packet;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location endLocation) {
        this.targetLocation = endLocation;
    }

    public void setTarget_Packet(Target_packet tp) {
        if (!this.equals(tp)) {
            if (!this.force_packet.equals(tp.force_packet)) {
                this.force_packet.setForcePacket(tp.force_packet);
            }
            this.targetLocation = tp.targetLocation;
        }
    }

    public Force_packet getForce_packet() {
        return force_packet;
    }

    public void setForce_packet(Force_packet f_packet) {
        if (!this.force_packet.equals(f_packet)) {
            this.force_packet.setForcePacket(f_packet);
        }
    }

    public int getAttractiveForce(double distance) {
        return force_packet.getValueOfTheDistance(distance);
    }

    /**Sets the attractive force at the distance 0.*/
    public void setAttractiveForce(int force) {
        force_packet.getForcePoints().get(0).forceValue = force;
    }

    /**Returns the attractive force at the distance 0.*/
    public int getAttractiveForce() {
        if (force_packet.getForcePoints().size() > 0) return force_packet.getForcePoints().get(0).forceValue;
        else {
            return 0;
        }
    }

    public String getSpecialText() {
        String result = "";
        result += "  * Target Location: " + targetLocation.toString() + "\n";
        result += force_packet.getSpecialText();
        return result;
    }

    public XMLTarget_packet getXMLProperties() {
        XMLTarget_packet xmlProp = new XMLTarget_packet();
        xmlProp.force_packet = force_packet.getXMLForce_packet();
        xmlProp.xTargetLocation = (int) targetLocation.x;
        xmlProp.yTargetLocation = (int) targetLocation.y;
        xmlProp.zTargetLocation = (int) targetLocation.z;
        return xmlProp;
    }
}
