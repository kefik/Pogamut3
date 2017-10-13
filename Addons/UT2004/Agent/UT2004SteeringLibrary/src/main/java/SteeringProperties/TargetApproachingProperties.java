package SteeringProperties;

import SteeringStuff.SteeringType;
import XMLSteeringProperties.XMLTargetApproachingProperties;
import XMLSteeringProperties.XMLTarget_packet;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.ArrayList;

/**
 * The steering properties for the steering Target Approaching. These properties are rather special, because it contains the list of Target_packets.
 * It means that one bot can have more locations (and it's special defined forces fo each of them), which attract or repuls him.
 * @author Marki
 */
public class TargetApproachingProperties extends SteeringProperties {

    /**Steering properties: the list of targets and its forces*/
    private ArrayList<Target_packet> targets = new ArrayList<Target_packet>();

    public TargetApproachingProperties() {
        super(SteeringType.TARGET_APPROACHING);
        targets.add(new Target_packet());
    }

    public TargetApproachingProperties(BehaviorType behaviorType) {
        super(SteeringType.TARGET_APPROACHING, behaviorType);
        targets.add(new Target_packet());
        setNewBehaviorType(behaviorType);
    }

    public TargetApproachingProperties(XMLTargetApproachingProperties xml) {
        super(SteeringType.TARGET_APPROACHING, xml.active, xml.weight, xml.behavior);
        for(XMLTarget_packet tp : xml.targets) {
            targets.add(new Target_packet(tp));
        }
    }

    public TargetApproachingProperties(int attractiveForce, Location endLocation) {
        super(SteeringType.TARGET_APPROACHING);
        targets.add(new Target_packet(endLocation, new Force_packet(attractiveForce)));
    }

    protected void setNewBehaviorType(BehaviorType behaviorType) {
        if (behaviorType.equals(BehaviorType.BASIC)) {
            Target_packet first = targets.get(0);
            first.setForce_packet(new Force_packet(first.getAttractiveForce()));    //Sets just the first force_packet.
            targets.clear();
            targets.add(new Target_packet(first.getTargetLocation(), first.getForce_packet()));
        }
    }


    public int getAttractiveForce() {
        if (!targets.isEmpty() && !targets.get(0).getForce_packet().getForcePoints().isEmpty()) {
            return targets.get(0).getForce_packet().getForcePoints().get(0).forceValue;
        } else return 100;
    }

    public void setAttractiveForce(int attractiveForce) { 
        if (!targets.isEmpty() && !targets.get(0).getForce_packet().getForcePoints().isEmpty()) {
            targets.get(0).getForce_packet().getForcePoints().get(0).forceValue = attractiveForce;
        }
    }

    public ArrayList<Target_packet> getTargets() {
        return targets;
    }

    public void setTargets(ArrayList<Target_packet> targets) {
        this.targets = targets;
    }

    public Target_packet getTarget_packet(int index) {
        return targets.get(index);
    }

    /**Changes the target packet of the index.*/
    public void setTarget_packet(int index, Target_packet tp) {
        if (index >= 0 && index < targets.size()) {
            //System.out.println("We set packet "+index);
            targets.get(index).setTarget_Packet(tp);
        }
    }

    /**Removes the item at index index of the targets*/
    public void removeTarget_packet(int index) {
        if (index >= 0 && index < targets.size()) {
            targets.remove(index);
        }
    }

    public void setTargetLocation(int index, Location loc) {
        if (index >= 0 && index < targets.size()) {
            targets.get(index).setTargetLocation(loc);
        }
    }

    public void addTarget_packet(Target_packet tp) {
        targets.add(tp);
    }
    
    @Override
    public String getSpecialText() {
        String text = "";
        int index = 0;
        for(Target_packet t : targets) {
            text += "  * Target Number " + index + ":\n" + t.getSpecialText();
            index++;
        }
        return text;
    }

    @Override
    public void setProperties(SteeringProperties newProperties) {
        this.targets = ((TargetApproachingProperties)newProperties).getTargets(); 
    }

    public XMLTargetApproachingProperties getXMLProperties() {
        XMLTargetApproachingProperties xmlProp = new XMLTargetApproachingProperties();
        xmlProp.targets = new ArrayList<XMLTarget_packet>();
        for(Target_packet t : targets) {
            xmlProp.targets.add(t.getXMLProperties());
        }
        xmlProp.active = active;
        xmlProp.weight = weight;
        xmlProp.behavior = behaviorType;
        return xmlProp;
    }
}
