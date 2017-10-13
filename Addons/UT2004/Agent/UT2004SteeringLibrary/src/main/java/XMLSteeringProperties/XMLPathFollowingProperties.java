package XMLSteeringProperties;

import SteeringProperties.SteeringProperties.BehaviorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marki
 */
@XmlRootElement
public class XMLPathFollowingProperties {

    @XmlElement
    public int repulsiveForce;
    
    @XmlElement
    public int distance;

    @XmlElement
    public int xTargetLocation;

    @XmlElement
    public int yTargetLocation;

    @XmlElement
    public int zTargetLocation;

    @XmlElement
    public double regulatingForce;

    @XmlElement
    public int projection;

    @XmlElement
    public boolean active;

    @XmlElement
    public double weight;

    @XmlElement
    public BehaviorType behavior;
}
