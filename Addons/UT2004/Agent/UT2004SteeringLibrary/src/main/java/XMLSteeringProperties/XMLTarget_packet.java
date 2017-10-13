package XMLSteeringProperties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marki
 */
@XmlRootElement
public class XMLTarget_packet {

    @XmlElement
    public int xTargetLocation;

    @XmlElement
    public int yTargetLocation;

    @XmlElement
    public int zTargetLocation;

    @XmlElement
    public XMLForce_packet force_packet;
}
