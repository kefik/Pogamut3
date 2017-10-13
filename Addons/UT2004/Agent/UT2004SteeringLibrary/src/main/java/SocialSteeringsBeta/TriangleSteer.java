package SocialSteeringsBeta;

import SteeringProperties.SteeringProperties;
import SteeringStuff.RefBoolean;
import SteeringStuff.SteeringTools;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import java.util.ArrayList;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

/**
 *
 * @author Petr
 */
public class TriangleSteer implements ISocialSteering {

    protected UT2004Bot botself;
    protected TriangleSteeringProperties properties;
    private static final int KDefaultAttraction = 600;
    private static final double K90deg = 90;
    private static final double K360deg = 360;
    private static final double K180deg = 180;
    private static final double KMinimalDistance = 40;
    private static final String KTowards = "towards";

    public TriangleSteer(UT2004Bot botself) {
        this.botself = botself;

    }

    @Override
    public Vector3d run(Vector3d scaledActualVelocity, RefBoolean wantsToGoFaster, RefBoolean wantsToStop, RefLocation focus) {
        // <editor-fold defaultstate="collapsed" desc="debug">
        if (properties == null) {
            if (SOC_STEER_LOG.DEBUG) {
                SOC_STEER_LOG.AddLogLineWithDate("no properties", "triangleError");

            }
        }// </editor-fold>

        Location newFocus = getFocus();
        if (newFocus != null) {
            focus.data = newFocus;
        }


        //returns ideal place where steered agent wants to stay...
        Location targetLocation = WhereToGo(botself, properties);

        // Supposed velocity in the next tick of logic, after applying various steering forces to the bot.
        SteeringResult nextVelocity = new SteeringResult(new Vector3d(0, 0, 0), 1);

        //we are able to compute ideal place...
        if (targetLocation != null) {


            // A vector from the bot to the target location.
            targetLocation = new Location(targetLocation.x,targetLocation.y, botself.getLocation().z);
            Vector3d vectorToTarget = targetLocation.sub(botself.getLocation()).asVector3d();

            double distFromTarget = vectorToTarget.length();
            nextVelocity.setMult(distFromTarget / 100);
            if (distFromTarget < KMinimalDistance) {
                wantsToStop.setValue(true);
                return new SteeringResult(new Vector3d(0, 0, 0), 1);
            }

            double attractiveForce = KDefaultAttraction;//* (distFromTarget / KDefaultAttractionDistance);

            vectorToTarget.normalize();
            vectorToTarget.scale(attractiveForce);
            nextVelocity.add((Tuple3d) vectorToTarget);
        }else
        {
            nextVelocity.setMult(1);
        }
        
        //no need to scale, scaling is done within method attraction(...)
        int botAttractiveForce = KDefaultAttraction / 6;

        Vector3d attractionFromFst = attraction(botself, properties.getFstBot(), 1.3);
        Vector3d attractionFromSnd = attraction(botself, properties.getSndBot(), 1.3);
        
        attractionFromFst.scale(botAttractiveForce);
        nextVelocity.add((Tuple3d) attractionFromFst);

        attractionFromSnd.scale(botAttractiveForce);
        nextVelocity.add((Tuple3d) attractionFromSnd);



        wantsToGoFaster.setValue(false);
        return nextVelocity;
    }

    @Override
    public void setProperties(SteeringProperties newProperties) {
        properties = (TriangleSteeringProperties) newProperties;
    }

    /**
     *
     * @param me
     * @param propers
     * @return Location which is suitable for all coditions described in parameter propers
     */
    
    private Location WhereToGo(UT2004Bot me, TriangleSteeringProperties propers) {
        UT2004Bot first = propers.getFstBot();
        UT2004Bot second = propers.getSndBot();



        // <editor-fold defaultstate="collapsed" desc="debug">
        if (SOC_STEER_LOG.DEBUG) {
            SOC_STEER_LOG.AddLogLineWithDate(me.getName() + " " + me.getLocation().toString() + " --------------------------------------------", "triangle");
            if (propers == null) {
                SOC_STEER_LOG.AddLogLineWithDate(me.getName() + "I have no properties!!!", "triangleError");
            }
        }
// </editor-fold>

        ArrayList<Location> edgePoints = new ArrayList<Location>();
        Location S1 = first.getLocation();
        Location S2 = second.getLocation();
        int r1min = propers.getFstDistance().getMin();
        int r1max = propers.getFstDistance().getMax();
        int r2min = propers.getSndDistance().getMin();
        int r2max = propers.getSndDistance().getMax();



        if (isSuitable(me.getLocation(), S1, S2, propers, 0)) {
            SOC_STEER_LOG.AddLogLineWithDate(me.getName() + "is in suitable place.", "triangle");
            return me.getLocation();
        }
        // <editor-fold defaultstate="collapsed" desc="heuristics">
        double othersDistance = S1.getDistance2D(S2);
        if (r1max + r2max < othersDistance) {
            SOC_STEER_LOG.AddLogLineWithDate(me.getName() + "see: " + first.getName() + "to far from: " + second.getName(), "triangle");
            float factor = (float) r1max / (float) (r1max + r2max); // rika jak daleko ma byt od 1
            return S1.add((S2.sub(S1)).scale(factor));//bod na usecce S1 S2
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="polomery uhlovych kruznic">
        //vstup je uhel vystup jsou polomery opsanych kruznic
        double r3min = 0;
        double r3max = 0;
        for (int i = 0; i < 2; i++) {
            // <editor-fold defaultstate="collapsed" desc="debug">
            if (SOC_STEER_LOG.DEBUG) {
                if (propers == null) {
                    SOC_STEER_LOG.AddLogLine("nemam TP pro TS", SOC_STEER_LOG.KError);
                }
                if (propers.getAngle() == null) {
                    SOC_STEER_LOG.AddLogLine("nemam uhel v TP", SOC_STEER_LOG.KError);
                }
            }// </editor-fold>
            double angle = i == 0 ? propers.getAngle().getMin() : propers.getAngle().getMax();
            double gamma = angle < K90deg ? 2 * angle : K360deg - 2 * angle;
            double alpha = (K180deg - gamma) / 2;
            double s = S1.getDistance2D(S2);
            double alphaRad = SteeringTools.degreesToRadians(alpha);
            double cosAlpha = Math.cos(alphaRad);
            double b = s / (2 * cosAlpha);
            if (i == 0) {
                r3min = b;
            } else {
                r3max = b;
            }
        }// </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="stredy uhlovych kruznic">
//stredy kruznic co definuji spravny uhel;
        Location S3max1 = null;
        Location S3min1 = null;
        Location S3max2 = null;
        Location S3min2 = null;
        Location[] tmp; // zapisuju do ni vystupy z commonPoints;

        tmp = SteeringTools.commonPoints(S2, r3max, S1, r3max);
        if (tmp[0] != null) {
            S3max1 = tmp[0];
        }
        if (tmp[1] != null) {
            S3max2 = tmp[1];
        }
        tmp = SteeringTools.commonPoints(S2, r3min, S1, r3min);
        if (tmp[0] != null) {
            S3min1 = tmp[0];
        }
        if (tmp[1] != null) {
            S3min2 = tmp[1];// </editor-fold>
        }
        // <editor-fold defaultstate="collapsed" desc="pruniky okrajovych kruznic ">
        // <editor-fold defaultstate="collapsed" desc="S1 a S2 spolu">
        // <editor-fold defaultstate="collapsed" desc="debug">
        int edgeSize = edgePoints.size();
        // </editor-fold>
        tmp = SteeringTools.commonPoints(S1, r1min, S2, r2min);
        if (tmp[0] != null) {
            edgePoints.add(tmp[0]);
        }
        if (tmp[1] != null) {
            edgePoints.add(tmp[1]);
        }

        tmp = SteeringTools.commonPoints(S1, r1max, S2, r2min);
        if (tmp[0] != null) {
            edgePoints.add(tmp[0]);
        }
        if (tmp[1] != null) {
            edgePoints.add(tmp[1]);
        }

        tmp = SteeringTools.commonPoints(S1, r1max, S2, r2max);
        if (tmp[0] != null) {
            edgePoints.add(tmp[0]);
        }
        if (tmp[1] != null) {
            edgePoints.add(tmp[1]);
        }

        tmp = SteeringTools.commonPoints(S1, r1min, S2, r2max);
        if (tmp[0] != null) {
            edgePoints.add(tmp[0]);
        }
        if (tmp[1] != null) {
            edgePoints.add(tmp[1]);
        }
        // <editor-fold defaultstate="collapsed" desc="debug">
        if (SOC_STEER_LOG.DEBUG) {
            if (edgeSize == edgePoints.size()) {
                SOC_STEER_LOG.AddLogLine(me.getName() + " no distance-circles cross", "triangle");
                SOC_STEER_LOG.AddLogLine("-  S1: " + S1 + " S2: " + S2 + " r1: " + r1min + "-" + r1max + " r2: " + r2min + "-" + r2max, "triangle");
            }
        }
        edgeSize = edgePoints.size();
        // </editor-fold>
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="S3min1">

        if (S3min1 != null) {
            tmp = SteeringTools.commonPoints(S3min1, r3min, S2, r2max);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }

            tmp = SteeringTools.commonPoints(S3min1, r3min, S2, r2min);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }

            tmp = SteeringTools.commonPoints(S3min1, r3min, S1, r1min);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }

            tmp = SteeringTools.commonPoints(S3min1, r3min, S1, r1max);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }
            // <editor-fold defaultstate="collapsed" desc="debug">
            if (SOC_STEER_LOG.DEBUG) {
                if (edgeSize == edgePoints.size()) {
                    SOC_STEER_LOG.AddLogLine(me.getName() + " no S3min1 cross", "triangle");
                    SOC_STEER_LOG.AddLogLine("-  S3min1: " + S3min1 + " r3min: " + r3min + " r1: " + r1min + "-" + r1max + " r2: " + r2min + "-" + r2max, "triangle");
                }
            }
            edgeSize = edgePoints.size();
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="debug">
            if (SOC_STEER_LOG.DEBUG) {
                SOC_STEER_LOG.AddLogLine(me.getName() + " no S3min1", "triangle");
            }// </editor-fold>
        }

// </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="S3max1">
        if (S3max1 != null) {
            tmp = SteeringTools.commonPoints(S3max1, r3max, S2, r2max);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }

            tmp = SteeringTools.commonPoints(S3max1, r3max, S2, r2min);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }

            tmp = SteeringTools.commonPoints(S3max1, r3max, S1, r1min);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }

            tmp = SteeringTools.commonPoints(S3max1, r3max, S1, r1max);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }
            // <editor-fold defaultstate="collapsed" desc="debug">
            if (SOC_STEER_LOG.DEBUG) {
                if (edgeSize == edgePoints.size()) {
                    SOC_STEER_LOG.AddLogLine(me.getName() + " no S3max1 cross", "triangle");
                    SOC_STEER_LOG.AddLogLine("-  S3max1: " + S3max1 + " r3max: " + r3max + " r1: " + r1min + "-" + r1max + " r2: " + r2min + "-" + r2max, "triangle");
                }
            }
            edgeSize = edgePoints.size();
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="debug">
            if (SOC_STEER_LOG.DEBUG) {
                SOC_STEER_LOG.AddLogLine(me.getName() + " no S3max1", "triangle");
            }// </editor-fold>
        }

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="S3min2">
        if (S3min2 != null) {
            tmp = SteeringTools.commonPoints(S3min2, r3min, S2, r2max);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }

            tmp = SteeringTools.commonPoints(S3min2, r3min, S2, r2min);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }

            tmp = SteeringTools.commonPoints(S3min2, r3min, S1, r1min);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }

            tmp = SteeringTools.commonPoints(S3min2, r3min, S1, r1max);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }
            // <editor-fold defaultstate="collapsed" desc="debug">
            if (SOC_STEER_LOG.DEBUG) {
                if (edgeSize == edgePoints.size()) {
                    SOC_STEER_LOG.AddLogLine(me.getName() + " no S3min2 cross", "triangle");
                    SOC_STEER_LOG.AddLogLine("-  S3min2: " + S3min2 + " r3min: " + r3min + " r1: " + r1min + "-" + r1max + " r2: " + r2min + "-" + r2max, "triangle");
                }
            }
            edgeSize = edgePoints.size();
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="debug">
            if (SOC_STEER_LOG.DEBUG) {
                SOC_STEER_LOG.AddLogLine(me.getName() + " no S3min2", "triangle");
            }// </editor-fold>
        }

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="S3max2">
        if (S3max2 != null) {
            tmp = SteeringTools.commonPoints(S3max2, r3max, S2, r2max);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }

            tmp = SteeringTools.commonPoints(S3max2, r3max, S2, r2min);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }

            tmp = SteeringTools.commonPoints(S3max2, r3max, S1, r1min);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }

            tmp = SteeringTools.commonPoints(S3max2, r3max, S1, r1max);
            if (tmp[0] != null) {
                edgePoints.add(tmp[0]);
            }
            if (tmp[1] != null) {
                edgePoints.add(tmp[1]);
            }
            // <editor-fold defaultstate="collapsed" desc="debug">
            if (SOC_STEER_LOG.DEBUG) {
                if (edgeSize == edgePoints.size()) {
                    SOC_STEER_LOG.AddLogLine(me.getName() + " no S3max2 cross", "triangle");
                    SOC_STEER_LOG.AddLogLine("-  S3max2: " + S3max2 + " r3max: " + r3max + " r1: " + r1min + "-" + r1max + " r2: " + r2min + "-" + r2max, "triangle");
                }
            }
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="debug">
            if (SOC_STEER_LOG.DEBUG) {
                SOC_STEER_LOG.AddLogLine(me.getName() + " no S3max2", "triangle");
            }// </editor-fold>
        }

        // </editor-fold>
        // </editor-fold>

        for (int i = 0; i < edgePoints.size(); i++) {
            if (!isSuitable(edgePoints.get(i), S1, S2, propers, 8)) {
                edgePoints.set(i, null);
            }
        }

        Location result = calculateBest(edgePoints, me.getLocation(), propers);
        // <editor-fold defaultstate="collapsed" desc="debug">
        if (SOC_STEER_LOG.DEBUG && result == null) {
            SOC_STEER_LOG.AddLogLineWithDate(me.getName() + ": no suitable edge points from total of:" + String.valueOf(edgePoints.size()), "triangleError");
        }
        if (SOC_STEER_LOG.DEBUG && result != null) {
            SOC_STEER_LOG.AddLogLine(me.getName() + ": has best suitable edge point:" + result.toString()
                    + "distance: " + String.valueOf(me.getLocation().getDistance2D(result)), "triangle");
        }// </editor-fold>
        if (result == null) {
            if (Math.min(r1min, r2min) > Math.max(me.getLocation().getDistance2D(S2), me.getLocation().getDistance2D(S1))) {
                SOC_STEER_LOG.AddLogLineWithDate(me.getName() + " is to close to, going away from both", "triangle");
                Location mine = me.getLocation();
                Location middle = S1.add((S2.sub(S1)).scale(1 / 2));//stred usecky S1 S2
                return mine.add(middle.sub(mine).scale(-1));
            }
        }
        // <editor-fold defaultstate="collapsed" desc="debug">
        if (SOC_STEER_LOG.DEBUG) {
            SOC_STEER_LOG.AddLogLine("--------------------------------------------", "triangle");
        }
// </editor-fold>
        return result;

    }

    /**
     * @return true if point is suitable for steering conditions
     */
    private static boolean isSuitable(Location point, Location fst, Location snd, TriangleSteeringProperties props, double tolerance) {
        if (props.getFstDistance().in((int) point.getDistance2D(fst), tolerance)
                && props.getSndDistance().in((int) point.getDistance2D(snd), tolerance)) {
            double a = point.getDistance2D(fst);
            double b = point.getDistance2D(snd);
            double c = fst.getDistance2D(snd);
            double gamma = Math.acos((c * c - a * a - b * b) / (-2 * a * b)) * (180 / Math.PI);
            if (props.getAngle().in((int) gamma, tolerance / 5)) {
                // <editor-fold defaultstate="collapsed" desc="debug">
                if (SOC_STEER_LOG.DEBUG) {
                    //SOC_STEER_LOG.AddLogLine("suitable: " + point.toString(), "triangle");
                }
                // </editor-fold>
                return true;

            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * @return best point in area of suitable points, usually the center of gravity
     */
    private static Location calculateBest(ArrayList<Location> edgePoints, Location me, TriangleSteeringProperties props) {

        Location avgL = new Location(0, 0, 0);
        Location avgR = new Location(0, 0, 0);
        int countL = 0;
        int countR = 0;
        Vector3d divider = new Vector3d(props.getFstBot().getLocation().x - props.getSndBot().getLocation().x,
                props.getFstBot().getLocation().y - props.getSndBot().getLocation().y, 0);
        for (Location ll : edgePoints) {
            if (ll == null) {
                continue;
            }
            Vector3d point = (ll.sub(props.getSndBot().getLocation())).asVector3d();
            if (SteeringTools.pointIsLeftFromTheVector(point, divider)) {
                avgL = avgL.add(ll);
                countL++;
            } else {
                avgR = avgR.add(ll);
                countR++;
            }

        }
        boolean Lsuit = false;
        if (countL != 0) {
            avgL = avgL.scale(1 / (double) countL);
            if (isSuitable(avgL, props.getFstBot().getLocation(), props.getSndBot().getLocation(), props, 1)) {
                // <editor-fold defaultstate="collapsed" desc="debug">
                if (SOC_STEER_LOG.DEBUG) {
                    SOC_STEER_LOG.AddLogLine("suitable těžiště L: " + avgL.toString(), "triangle");
                }
                // </editor-fold>
                Lsuit = true;
            }
        }
        boolean Rsuit = false;
        if (countR != 0) {
            avgR = avgR.scale(1 / (double) countR);
            if (isSuitable(avgR, props.getFstBot().getLocation(), props.getSndBot().getLocation(), props, 1)) {
                // <editor-fold defaultstate="collapsed" desc="debug">
                if (SOC_STEER_LOG.DEBUG) {
                    SOC_STEER_LOG.AddLogLine("suitable těžiště R: " + avgR.toString(), "triangle");
                }
                // </editor-fold>
                Rsuit = true;
            }

        }

        if ((Lsuit && avgL.getDistance2D(me) < avgR.getDistance2D(me) && Rsuit) || (Lsuit && !Rsuit)) {
            return avgL;
        } else if ((Rsuit && !Lsuit) || (Rsuit && avgL.getDistance2D(me) >= avgR.getDistance2D(me) && Lsuit)) {
            return avgR;
        }


        Location best = null;
        double min = Integer.MAX_VALUE;
        for (Location p : edgePoints) {
            if (p == null) {
                continue;
            }
            double next = me.getDistance2D(p);
            if (next < min) {
                best = p;
                min = next;
            }
        }
        if (best == null) {
            // <editor-fold defaultstate="collapsed" desc="debug">
            if (SOC_STEER_LOG.DEBUG) {
                SOC_STEER_LOG.AddLogLine("nenasli jsme vhodny bod, tzn s nami hybou uz jen pritazlivo odpudive sily vuci dalsim postavam ", "triangleError");
            }
            // </editor-fold>
        }
        return best;
    }

    /**
     * returns attractive or distractive force between agents
     *
     * @param treshhold ideally bigger then 1... like 1.3 1.6
     */
    private Vector3d attraction(UT2004Bot botself, UT2004Bot other, double trashhold) {
        trashhold = trashhold < 1 ? 1 : trashhold;
        double max = properties.getFstDistance().getMax();
        double min = properties.getFstDistance().getMin();
        double actual = botself.getLocation().getDistance2D(other.getLocation());
        Vector3d result = new Vector3d(0, 0, 0);
        if (actual > max * trashhold) {
            result = new Vector3d(other.getLocation().x - botself.getLocation().x,
                    other.getLocation().y - botself.getLocation().y, 0);
            result.normalize();
            result.scale(actual / max);
            // <editor-fold defaultstate="collapsed" desc="debug">
            if (SOC_STEER_LOG.DEBUG) {
                SOC_STEER_LOG.AddLogLine("!! " + botself.getName() + ": too far from " + other.getName() + " --strength " + result.length(), "triangle");
            }
            // </editor-fold>
        }
        if (actual < min * trashhold) {
            result = new Vector3d(botself.getLocation().x - other.getLocation().x,
                    botself.getLocation().y - other.getLocation().y, 0);
            result.normalize();
            result.scale(min / actual);
            // <editor-fold defaultstate="collapsed" desc="debug">
            if (SOC_STEER_LOG.DEBUG) {
                SOC_STEER_LOG.AddLogLine("!! " + botself.getName() + ": too close to " + other.getName() + " --strength " + result.length(), "triangle");
            }
            // </editor-fold>
        }
        return result;
    }
    
    private Location getFocus() {


        if (properties.getHeadingType() == null) {
            return null; //impicit behaviour;
        }
        String headingType = properties.getHeadingType();
        UT2004Bot fst = properties.getFstBot();
        UT2004Bot snd = properties.getSndBot();
        Interval headingValue = properties.getHeadingValue();
        int fromFst = 0;
        boolean headingToAgents = false;

        if (headingType.compareTo(fst.getName().substring(0, 1)) == 0) {
            fromFst = 100 - headingValue.avg();
            headingToAgents = true;
        } else if (headingType.compareTo(snd.getName().substring(0, 1)) == 0) {
            fromFst = headingValue.avg();
            headingToAgents = true;
        }
        if (headingToAgents) {
            Location fstL = fst.getLocation();
            Location sndL = snd.getLocation();

            Location shift = (fstL.sub(sndL)).scale(((double) fromFst) / 100);
            Location result = sndL.add(shift);
            return result;
        } else if (headingType.compareTo(KTowards) == 0) {
            return null;
        } else {
            return null;
        }


    }

    @Override
    public SteeringResult runSocial(Vector3d scaledActualVelocity, RefBoolean wantsToGoFaster, RefBoolean wantsToStop, RefLocation focus) {
        return (SteeringResult) this.run(scaledActualVelocity, wantsToGoFaster, wantsToStop, focus);
    }
}
