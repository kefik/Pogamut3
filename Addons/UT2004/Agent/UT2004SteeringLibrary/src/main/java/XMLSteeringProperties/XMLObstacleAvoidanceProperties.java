package XMLSteeringProperties;

import SteeringProperties.SteeringProperties.BehaviorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marki
 */
@XmlRootElement
public class XMLObstacleAvoidanceProperties {

    @XmlElement
    public int repulsiveForce;
    
    @XmlElement
    public int forceOrder;

    @XmlElement
    public boolean frontCollisions;

    @XmlElement
    public boolean treeCollisions;

    @XmlElement
    public boolean active;

    @XmlElement
    public double weight;

    @XmlElement
    public BehaviorType behavior;
}
