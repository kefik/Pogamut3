package XMLSteeringProperties;

import SteeringProperties.SteeringProperties.BehaviorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marki
 */
@XmlRootElement
public class XMLPeopleAvoidanceProperties {

    @XmlElement
    public int repulsiveForce;
    
    @XmlElement
    public int distance;

    @XmlElement
    public boolean circumvention;

    @XmlElement
    public boolean deceleration;

    @XmlElement
    public boolean acceleration;

    @XmlElement
    public double projection;

    @XmlElement
    public boolean active;

    @XmlElement
    public double weight;

    @XmlElement
    public BehaviorType behavior;
}
