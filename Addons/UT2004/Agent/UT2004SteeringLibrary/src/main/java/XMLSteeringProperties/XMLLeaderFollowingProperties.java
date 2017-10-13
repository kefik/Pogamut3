package XMLSteeringProperties;

import SteeringProperties.LeaderFollowingProperties.LFtype;
import SteeringProperties.SteeringProperties.BehaviorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marki
 */
@XmlRootElement
public class XMLLeaderFollowingProperties {

    @XmlElement
    public int leaderForce;

    @XmlElement
    public String leaderName;

    @XmlElement
    public int distance;

    @XmlElement
    public int forceDistance;

    @XmlElement
    public LFtype myLFtype;

    @XmlElement
    public boolean deceleration;

    @XmlElement
    public double angle;

    @XmlElement
    public boolean velocityMemory;

    @XmlElement
    public int sizeOfMemory;

    @XmlElement
    public boolean circumvention;

    @XmlElement
    public boolean active;

    @XmlElement
    public double weight;

    @XmlElement
    public BehaviorType behavior;
}