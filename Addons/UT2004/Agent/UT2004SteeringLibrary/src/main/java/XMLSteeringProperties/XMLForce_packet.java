package XMLSteeringProperties;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marki
 */
@XmlRootElement
public class XMLForce_packet {

    @XmlElement
    public ArrayList<XMLForcePoint> forces;
}
