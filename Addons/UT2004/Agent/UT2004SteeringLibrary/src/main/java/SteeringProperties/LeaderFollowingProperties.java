package SteeringProperties;

import SteeringStuff.SteeringType;
import XMLSteeringProperties.XMLLeaderFollowingProperties;

/**
 * The steering properties of the Leader Following Steering.
 * @author Marki
 */
public class LeaderFollowingProperties extends SteeringProperties {

    public enum LFtype {BASIC, FORMATION};

    /**The magnitude of the force of this steering. (It is used in attractive force to the leader, repulsive force from the leader.)*/
    private int leaderForce;

    /**The name of the leader bot.*/
    private String leaderName;

    /**The ideal distance from the leader.*/
    private int distance;
    
    /**V této vzdálenosti od ideální pozice bude na agenta působit síla k pozici velikostí leaderForce. O každých forceDistance dál na něj působí síla o leaderForce větší (lineární závislost).
     * Je-li tato vzdálenost větší, na agenta tedy obecně působí menší síla k pozici. Nemusí se tedy na ni dostat hned, či může být vždy kus za ní. Nicémě jeho pohyb bude plynulejší a lépe se
     * budou plnit potřeby i dalších steeringů. Je-li tato vzdálenost menší (třeba 80), agentovi se bude lépe dařit být na správné pozici, avšak jeho pohyb bude vrtkavější (více se budou zvelišovat
     * změny pohybu vůdce apod.) a např. se bude hůře vyhýbat překážkám, neboť OA nebude mít takový vliv. I kdyby měl (např. vyšší váhou), tak pohyb nebude tak plynulý.
     * Tento parametr funguje stejně pro základní i formační typ. Rozdíl je ovšem v tom, co to je ideální pozice. U základního je to místo na spojnici agenta a vůdce ve vzdálenosti
     * distanceFromTheLeader od vůdce. U formačního typu je to přesně dané úhlem a opět vzdáleností od vůdce.*/

    /**At this distance the attractive force to the leader has the magnitude leaderForce.*/
    private int forceDistance;

    /**The type of the LF steering. The BASIC type is default. The FORMATION type enables to form simple formations. You can use angl to set the ideal position of the follower.*/
    private LFtype myLFtype;

    /**This parameter helps the agent to decelerate, when it is reasonable. Used just in BASIC type. Recommended value is true.*/
    private boolean deceleration;

    /**The angle, which sets the position of the follower. The value Math.PI/2 means right from the leader, Matgh.PI behind the leader, -Math.PI left from the leader, 0 in front of the leader, etc.*/
    private double angleFromTheLeader;

    /**This parameter help to the more fluent motion in the case of formation type. Recommended value is true.*/
    private boolean velocityMemory;

    /**The size of the memory of leaders velocities (in the case of velocityMemory). Recommended value is 5.*/
    private int sizeOfMemory;

    /**This parameter helps the agent to go round the leader, if it's reasonable. Just in the case of the FORMATION type. Recommended value is true.*/
    private boolean circumvention;

    public LeaderFollowingProperties() {
        super(SteeringType.LEADER_FOLLOWING);
        leaderForce = 200;
        leaderName = "Leader";
        distance = 300;
        forceDistance = 50; //200
        myLFtype = LFtype.BASIC;

        deceleration = true;

        angleFromTheLeader = Math.PI;
        velocityMemory = true;
        sizeOfMemory = 5;
        circumvention = true;
    }

    public LeaderFollowingProperties(BehaviorType behaviorType) {
        super(SteeringType.LEADER_FOLLOWING, behaviorType);
        leaderForce = 200;
        leaderName = "Leader";
        distance = 300;
        forceDistance = 50; //200
        myLFtype = LFtype.BASIC;

        deceleration = true;

        angleFromTheLeader = Math.PI;
        velocityMemory = true;
        sizeOfMemory = 5;
        circumvention = true;
        setNewBehaviorType(behaviorType);
    }

    public LeaderFollowingProperties(XMLLeaderFollowingProperties xml) {
        super(SteeringType.LEADER_FOLLOWING, xml.active, xml.weight, xml.behavior);
        leaderForce = xml.leaderForce;
        leaderName = xml.leaderName;
        distance = xml.distance;
        forceDistance = xml.forceDistance;
        myLFtype = xml.myLFtype;
        deceleration = xml.deceleration;
        angleFromTheLeader = xml.angle;
        velocityMemory = xml.velocityMemory;
        sizeOfMemory = xml.sizeOfMemory;
        circumvention = xml.circumvention;
    }

    /**
     * 
     * @param leaderForce
     * @param leaderName
     * @param distanceFromTheLeader
     * @param forceDistance
     * @param myLFtype
     * @param deceleration
     * @param angleFromTheLeader
     * @param velocityMemory
     * @param sizeOfMemory
     * @param goRound
     */
    public LeaderFollowingProperties(int leaderForce, String leaderName, int distanceFromTheLeader, int forceDistance, LFtype myLFtype, boolean deceleration, double angleFromTheLeader, boolean velocityMemory, int sizeOfMemory, boolean goRound) {
        super(SteeringType.LEADER_FOLLOWING);
        this.leaderForce = leaderForce;
        this.leaderName = leaderName;
        this.distance = distanceFromTheLeader;
        this.forceDistance = forceDistance;
        this.myLFtype = myLFtype;
        this.deceleration = deceleration;
        this.angleFromTheLeader = angleFromTheLeader;
        this.velocityMemory = velocityMemory;
        this.sizeOfMemory = sizeOfMemory;
        this.circumvention = goRound;
    }

    protected void setNewBehaviorType(BehaviorType behaviorType) {
        if (behaviorType.equals(BehaviorType.BASIC)) {
            myLFtype = LFtype.BASIC;
            sizeOfMemory = 0;
            deceleration = false;
            velocityMemory = false;
            circumvention = false;
        } else if (behaviorType.equals(BehaviorType.ADVANCED)) {
            myLFtype = LFtype.FORMATION;
            sizeOfMemory = 5;
            deceleration = true;
            velocityMemory = true;
            circumvention = true;
        }
    }

    public double getAngleFromTheLeader() {
        return angleFromTheLeader;
    }

    /**The angle, which sets the position of the follower. The value Math.PI/2 means right from the leader, Matgh.PI behind the leader, -Math.PI left from the leader, 0 in front of the leader, etc.*/
    public void setAngleFromTheLeader(double angleFromTheLeader) {
        this.angleFromTheLeader = angleFromTheLeader;
    }

    public int getDistanceFromTheLeader() {
        return distance;
    }

    public void setDistanceFromTheLeader(int distanceFromTheLeader) {
        this.distance = distanceFromTheLeader;
    }

    public int getForceDistance() {
        return forceDistance;
    }

    public void setForceDistance(int forceDistance) {
        this.forceDistance = forceDistance;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public int getSizeOfMemory() {
        return sizeOfMemory;
    }

    public void setSizeOfMemory(int sizeOfMemory) {
        this.sizeOfMemory = sizeOfMemory;
    }

    public boolean isVelocityMemory() {
        return velocityMemory;
    }

    public void setVelocityMemory(boolean velocityMemory) {
        this.velocityMemory = velocityMemory;
    }

    public boolean isDeceleration() {
        return deceleration;
    }

    public void setDeceleration(boolean deceleration) {
        this.deceleration = deceleration;
    }

    public boolean isCircumvention() {
        return circumvention;
    }

    public void setCircumvention(boolean circumvention) {
        this.circumvention = circumvention;
    }

    public int getLeaderForce() {
        return leaderForce;
    }

    public void setLeaderForce(int leaderForce) {
        this.leaderForce = leaderForce;
    }

    public LFtype getMyLFtype() {
        return myLFtype;
    }

    public void setMyLFtype(LFtype myLFtype) {
        this.myLFtype = myLFtype;
    }

    @Override
    public String getSpecialText() {
        String text = "";
        text += "  * Leader Force: " + leaderName + "\n";
        text += "  * Leader: " + leaderName + "\n";
        text += "  * Distance: " + distance + "\n";
        text += "  * Force Distance: " + forceDistance + "\n";
        text += "  * LF type: " + myLFtype + "\n";
        if (myLFtype.equals(LFtype.BASIC)) {
            text += "  * Deceleration: " + deceleration + "\n";
        } else {
            text += "  * Angle: " + angleFromTheLeader + "\n";
            text += "  * Memory: " + velocityMemory + "\n";
            if (velocityMemory) {
                text += "  * Memory size: " + sizeOfMemory + "\n";
            }
            text += "  * Circumvention: " + circumvention + "\n";
        }
        return text;
    }

    @Override
    public void setProperties(SteeringProperties newProperties) {
        this.leaderForce = ((LeaderFollowingProperties)newProperties).getLeaderForce();
        this.leaderName = ((LeaderFollowingProperties)newProperties).getLeaderName();
        this.distance = ((LeaderFollowingProperties)newProperties).getDistanceFromTheLeader();
        this.forceDistance = ((LeaderFollowingProperties)newProperties).getForceDistance();
        this.myLFtype = ((LeaderFollowingProperties)newProperties).getMyLFtype();
        this.deceleration = ((LeaderFollowingProperties)newProperties).isDeceleration();
        this.angleFromTheLeader = ((LeaderFollowingProperties)newProperties).getAngleFromTheLeader();
        this.velocityMemory = ((LeaderFollowingProperties)newProperties).isVelocityMemory();
        this.sizeOfMemory = ((LeaderFollowingProperties)newProperties).getSizeOfMemory();
        this.circumvention = ((LeaderFollowingProperties)newProperties).isCircumvention();
    }

    public XMLLeaderFollowingProperties getXMLProperties() {
        XMLLeaderFollowingProperties xmlProp = new XMLLeaderFollowingProperties();
        xmlProp.leaderForce = leaderForce;
        xmlProp.leaderName = leaderName;
        xmlProp.distance = distance;
        xmlProp.forceDistance = forceDistance;
        xmlProp.myLFtype = myLFtype;
        xmlProp.deceleration = deceleration;
        xmlProp.angle = angleFromTheLeader;
        xmlProp.sizeOfMemory = sizeOfMemory;
        xmlProp.velocityMemory = velocityMemory;
        xmlProp.circumvention = circumvention;
        xmlProp.active = active;
        xmlProp.weight = weight;
        xmlProp.behavior = behaviorType;
        return xmlProp;
    }
}
