package XMLSteeringProperties;

import SteeringProperties.SteeringProperties.BehaviorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marki
 */
@XmlRootElement
public class XMLTargetApproachingProperties1 {

    @XmlElement
    public int attractiveForce;

    @XmlElement
    public int targetType;

    @XmlElement
    public int xTargetLocation;

    @XmlElement
    public int yTargetLocation;

    @XmlElement
    public int zTargetLocation;
    
    @XmlElement
    public boolean active;

    @XmlElement
    public double weight;

    @XmlElement
    public BehaviorType behavior;
}
