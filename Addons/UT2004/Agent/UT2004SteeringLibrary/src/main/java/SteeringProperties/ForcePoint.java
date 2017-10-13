package SteeringProperties;

import XMLSteeringProperties.XMLForcePoint;

/**
 * The class which defines one segment of the force-function. It says which value has the force at the concrete distance - and which value is to the next segment.
 * @author Marki
 */
public class ForcePoint {

    /**Distance of the agent's location from the target location.*/
    public int distance;

    /**The value of the force-function at the distance distance.*/
    public int forceValue;
    
    /**If it's false, the continuing segment has 0 value of the force. Otherwise it has the value computed from this and next boundary.*/
    public boolean continues;

    public ForcePoint(int distance, int forceValue, boolean continues) {
        this.distance = distance;
        this.forceValue = forceValue;
        this.continues = continues;
    }

    public ForcePoint(XMLForcePoint xml) {
        this.distance = xml.distance;
        this.forceValue = xml.forceValue;
        this.continues = xml.continues;
    }

    public ForcePoint(ForcePoint fPoint) {
        this.distance = fPoint.distance;
        this.forceValue = fPoint.forceValue;
        this.continues = fPoint.continues;
    }

    public String getSpecialText() {
        String result = "";
        result += "    * Distance: " + distance + "\n";
        result += "    * Value: " + forceValue + "\n";
        result += "    * Continues: " + continues + "\n";
        return result;
    }

    public XMLForcePoint getXMLForcePoint() {
        XMLForcePoint xmlForce = new XMLForcePoint();
        xmlForce.distance = distance;
        xmlForce.forceValue = forceValue;
        xmlForce.continues = continues;
        return xmlForce;
    }
}
