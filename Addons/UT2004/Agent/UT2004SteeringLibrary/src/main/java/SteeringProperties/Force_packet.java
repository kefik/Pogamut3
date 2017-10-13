package SteeringProperties;

import XMLSteeringProperties.XMLForcePoint;
import XMLSteeringProperties.XMLForce_packet;
import java.util.ArrayList;
import java.util.LinkedList;


/**
 * The packet of all things which belog to one force of one location in the Target Approaching Steering.
 * It includes mainly the forcePoints, which is list of ForcePoint. It defines the force.
 * @author Marki
 */
public class Force_packet {

    private LinkedList<ForcePoint> forcePoints;
    private int LAST_DISTANCE = 4000;

    //<editor-fold defaultstate="collapsed" desc="Constructors">

    public Force_packet(LinkedList<ForcePoint> forcePoints) {
        this.forcePoints = forcePoints;
    }

    public Force_packet(int forceValue) {
        forcePoints = new LinkedList<ForcePoint>();
        forcePoints.add(new ForcePoint(0, forceValue, true));
    }

    public Force_packet(int firstForceValue, int lastForceValue) {
        forcePoints = new LinkedList<ForcePoint>();
        forcePoints.add(new ForcePoint(0, firstForceValue, true));
        forcePoints.add(new ForcePoint(LAST_DISTANCE, lastForceValue, true));
    }

    public Force_packet(XMLForce_packet xmlPacket) {
        forcePoints = new LinkedList<ForcePoint>();
        for(XMLForcePoint x : xmlPacket.forces) {
            forcePoints.add(new ForcePoint(x));
        }
    }

    /**Copy constructor.*/
    public Force_packet(Force_packet force_packet) {
        forcePoints = new LinkedList<ForcePoint>();
        setForcePacket(force_packet);
    }

    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters and setters">

    public void setForcePacket(Force_packet f_packet) {
        this.forcePoints.clear();
        for(ForcePoint fPoint : f_packet.forcePoints) {
            forcePoints.add(new ForcePoint(fPoint));
        }
        LAST_DISTANCE = f_packet.LAST_DISTANCE;
    }
    
    public LinkedList<ForcePoint> getForcePoints() {
        return forcePoints;
    }

    public int getLAST_DISTANCE() {
        return LAST_DISTANCE;
    }

    /**Adds the forcePoint after the fp with the nearest but smaller (or same) distance.*/
    public void addForcePoint(ForcePoint forcePoint) {
        int index = 0;
        for(ForcePoint fp : forcePoints) {
            if (fp.distance < forcePoint.distance) index++;
        }
        forcePoints.add(index, forcePoint);
    }

    /**Deletes the force point - if it's not the first force point.*/
    public void deleteForcePoint(ForcePoint forcePoint) {
        if (forcePoints.getFirst() != forcePoint) forcePoints.remove(forcePoint);
    }

    public int getValueOfTheDistance(double distance) {
        int value = 0;
        ForcePoint lowerFP = getLowerFP(distance);
        ForcePoint higherFP = getHigherFP(distance);
        if (lowerFP != null) {
            if (lowerFP.continues) {        //Pokud toto není splněné, tak je síla v tomto úseku nulová.
                if (higherFP == null) {     //Pokud není žádný vyšší bod, tak se použije sklon z předchozího úseku nebo konstantní sklon, pokud je to zároveň první úsek.
                    higherFP = getInifinityPoint();
                }
                float direction;
                if (higherFP.distance != lowerFP.distance) {
                    direction = ((float)(higherFP.forceValue - lowerFP.forceValue))/(higherFP.distance - lowerFP.distance);
                } else {
                    direction = 0;
                }
                value = getValueOfTheLine(lowerFP.distance, lowerFP.forceValue, distance, direction);
            }
        }
        return value;
    }

    public String getSpecialText() {
        String result = "";
        for(ForcePoint fp : forcePoints) {
            result += fp.getSpecialText();
        }
        return result;
    }

    public XMLForce_packet getXMLForce_packet() {
        XMLForce_packet xmlPacket = new XMLForce_packet();
        ArrayList<XMLForcePoint> xmlForces = new ArrayList<XMLForcePoint>();
        for(ForcePoint fp : forcePoints) {
            xmlForces.add(fp.getXMLForcePoint());
        }
        xmlPacket.forces = xmlForces;
        return xmlPacket;
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Inner methods - work with the list of forcePoints.">

    /**Gets the ForcePoint, which's distance is the nearest smaller (or equal) than the distance of the coord.*/
    public ForcePoint getLowerFP(double distance) {
        ForcePoint result = null;
        for(ForcePoint fp : forcePoints) {
            if (fp.distance <= distance) result = fp;
        }
        return result;
    }

    /**Gets the ForcePoint, which's distance is the nearest smaller (or equal) than the distance of the coord.*/
    public ForcePoint getHigherFP(double distance) {
        ForcePoint result = null;
        for(int index = forcePoints.size()-1; index>= 0; index--) {
            ForcePoint fp = forcePoints.get(index);
            if (fp.distance >= distance) result = fp;
        }
        return result;
    }

    /**Returns the y-value of point on the line defined by 2 points.*/
    public int getValueOfTheLine(int fromDistance, int fromValue, double toDistance, float direction) {
        return Math.round(fromValue + ((float)(toDistance - fromDistance)*direction)) ;
    }


    /**Returns some ForcePoint with very distant distance.*/
    public ForcePoint getInifinityPoint() {
        ForcePoint fp1 = null;
        ForcePoint fp2 = null;
        int size = forcePoints.size();
        if (size > 1) {
            fp1 = forcePoints.get(size - 2);
        }
        if (size > 0) {
            fp2 = forcePoints.get(size - 1);
        }
        return getInifinityPoint(fp1, fp2);
    }

    /**Returns some ForcePoint with very distant distance - uses the two last ForcePoints of the list.*/
    public ForcePoint getInifinityPoint(ForcePoint fp1, ForcePoint fp2) {
        ForcePoint result = null;
        if (fp1 != null) {
            int infinityDistance = LAST_DISTANCE;
            int valueOfInfinityDistance = getValueOfTheLine(fp2.distance, fp2.forceValue, infinityDistance, ((float)(fp2.forceValue - fp1.forceValue))/(fp2.distance - fp1.distance)) ;
            result = new ForcePoint(infinityDistance, valueOfInfinityDistance, false);
        } else {
            int infinityDistance = LAST_DISTANCE;
            int valueOfInfinityDistance = fp2.forceValue;
            result = new ForcePoint(infinityDistance, valueOfInfinityDistance, false);
        }
        return result;
    }
    // </editor-fold>
}
