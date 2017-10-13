package XMLSteeringProperties;

import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Marki
 */
public class XMLForcePoint {

    @XmlElement
    public int distance;

    @XmlElement
    public int forceValue;

    @XmlElement
    public boolean continues;

    
}
