package SocialSteeringsBeta;

import SteeringProperties.SteeringProperties;
import SteeringStuff.SteeringType;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;

/**
 *
 * @author Petr
 */
public class TriangleSteeringProperties extends SteeringProperties {

    protected Interval fstDistance;
    protected Interval sndDistance;
    protected Interval angle;
    protected UT2004Bot fst;
    protected UT2004Bot snd;

    public TriangleSteeringProperties() {
        super(SteeringType.TRIANGLE);
    }
// <editor-fold defaultstate="collapsed" desc="getters and setters">

    public Interval getAngle() {
        return angle;
    }

    public Interval getFstDistance() {
        return fstDistance;
    }

    public Interval getSndDistance() {
        return sndDistance;
    }

    public void SetAngle(Interval angle) {
        this.angle = angle;
    }

    public void SetFstDistance(Interval fstDistance) {
        this.fstDistance = fstDistance;
    }

    public void SetSndDistance(Interval sndDistance) {
        this.sndDistance = sndDistance;
    }

    public UT2004Bot getFstBot() {
        return fst;
    }

    public UT2004Bot getSndBot() {
        return snd;
    }

    @Override
    public String getSpecialText() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void setBasicProperties(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setProperties(SteeringProperties sp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    // </editor-fold>
//these members could be included also in other steerings...
    protected String headingType;
    protected Interval headingValue;

    public String getHeadingType() {
        return headingType;
    }

    public void setHeadingType(String headingType) {
        this.headingType = headingType;
    }

    public Interval getHeadingValue() {
        return headingValue;
    }

    public void setHeadingValue(Interval headingValue) {
        this.headingValue = headingValue;
    }

    public void SetOtherAgents(UT2004Bot fst, UT2004Bot snd) {
        this.fst = fst;
        this.snd = snd;
    }

    @Override
    protected void setNewBehaviorType(BehaviorType behaviorType) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
