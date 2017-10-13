package XMLSteeringProperties;

import SteeringProperties.SteeringProperties.BehaviorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marki
 */
@XmlRootElement
public class XMLWalkAlongProperties {

    @XmlElement
    public int attractiveForce;

    @XmlElement
    public String partnerName;

    @XmlElement
    public int xTargetLocation;

    @XmlElement
    public int yTargetLocation;

    @XmlElement
    public int zTargetLocation;

    @XmlElement
    public int distance;

    @XmlElement
    public boolean giveWayToPartner;

    @XmlElement
    public boolean waitForPartner;

    @XmlElement
    public boolean active;

    @XmlElement
    public double weight;

    @XmlElement
    public BehaviorType behavior;
}
