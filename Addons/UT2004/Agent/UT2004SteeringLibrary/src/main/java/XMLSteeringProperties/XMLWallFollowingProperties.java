package XMLSteeringProperties;

import SteeringProperties.SteeringProperties.BehaviorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marki
 */
@XmlRootElement
public class XMLWallFollowingProperties {

    @XmlElement
    public int wallForce;

    @XmlElement
    public int forceOrder;

    @XmlElement
    public double attractiveForceWeight;

    @XmlElement
    public double repulsiveForceWeight;

    @XmlElement
    public double convexEdgesForceWeight;

    @XmlElement
    public double concaveEdgesForceWeight;
    
    @XmlElement
    public boolean justMySide;

    @XmlElement
    public boolean specialDetection;
    
    @XmlElement
    public boolean frontCollisions;

    /*@XmlElement
    public int distanceFromTheWall;*/

    @XmlElement
    public boolean active;

    @XmlElement
    public double weight;

    @XmlElement
    public BehaviorType behavior;
}
