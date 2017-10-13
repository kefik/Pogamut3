package XMLSteeringProperties;

import SteeringProperties.SteeringProperties.BehaviorType;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marki
 */
@XmlRootElement
public class XMLTargetApproachingProperties {

    @XmlElement
    public ArrayList<XMLTarget_packet> targets;
    
    @XmlElement
    public boolean active;

    @XmlElement
    public double weight;

    @XmlElement
    public BehaviorType behavior;
}
