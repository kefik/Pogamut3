package SteeringStuff;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * This class provides usefull tool for steerings, especially common mathematical calculations.
 * @author Marki
 */
public class SteeringTools {

    public enum LineType {STRAIGHT_LINE, HALF_LINE, ABSCISSA};

    /**Computes the intersection of the lines A and B (line has the start point and its direction).
     * If this intersection doesn't exist, we return null.
     * The types set if they are straight lines, half lines or abscissas. If the intersection doesn't lie in the right section
     * (i.e. in the abscissa), we return null. Otherwise we return the point of intersection.*/
    public static Vector2d getIntersection(Vector2d sA, Vector2d dA, Vector2d sB, Vector2d dB, LineType typeA, LineType typeB) {
        Vector2d result = null;
        double lengthA = dA.length();
        double lengthB = dB.length();
        dA.normalize();
        dB.normalize();
        if (!dA.equals(dB)) {
            if (dA.x == 0) dA.x = 0.001;
            if (dB.x == 0) dB.x = 0.001;
            if (dA.y == 0) dA.y = 0.001;
            if (dB.y == 0) dB.y = 0.001;
            double tB = ( (sA.y - sB.y) / dB.y ) + ( ( dA.y * (sB.x - sA.x) ) / (dA.x * dB.y) );
            tB = tB / (1 - ((dB.x * dA.y)/(dA.x * dB.y)) );
            double tA = ( sB.x - sA.x + (tB*dB.x) );
            tA = tA / dA.x;
            double pointX = sA.x + tA * dA.x;
            double pointY = sA.y + tA * dA.y;
            
            result = new Vector2d(pointX, pointY);            
            switch (typeA) {
                case HALF_LINE: if (tA < 0) result = null;
                    break;
                case ABSCISSA: if (tA < 0 || tA > lengthA) result = null;
                    break;
            }
            switch (typeB) {
                case HALF_LINE: if (tB < 0) result = null;
                    break;
                case ABSCISSA: if (tB < 0 || tB > lengthB) result = null;
                    break;
            }
        }
        return result;
    }

    /**Gets the intersection of the half-lines A and B (line has the start point and its direction.
     * If there isn't the intersection of the half-lines, or the direction is the same, we return null.*/
    public static Vector2d getIntersectionOld(Vector2d sA, Vector2d dA, Vector2d sB, Vector2d dB) {
        Vector2d result = null;
        dA.normalize();
        dB.normalize();
        if (!dA.equals(dB)) {
            if (dA.x == 0) dA.x = 0.001;
            if (dB.x == 0) dB.x = 0.001;
            if (dA.y == 0) dA.y = 0.001;
            if (dB.y == 0) dB.y = 0.001;
            double tB = ( (sA.y - sB.y) / dB.y ) + ( ( dA.y * (sB.x - sA.x) ) / (dA.x * dB.y) );
            tB = tB / (1 - ((dB.x * dA.y)/(dA.x * dB.y)) );
            double tA = ( sB.x - sA.x + (tB*dB.x) );
            tA = tA / dA.x;
            double pointX = sA.x + tA * dA.x;
            double pointY = sA.y + tA * dA.y;
            if (tA >= 0 && tB >= 0) {    //The intersection of the lines lies also on the half-lines.
                result = new Vector2d(pointX, pointY);
            }
        }
        return result;
    }


    /**Gets the intersection of the half-lines A and B (line has the start point and its direction.
     * If there isn't the intersection of the half-lines, or the direction is the same, we return null.*/
    public static boolean haveSameDirection(Vector2d sA, Vector2d dA, Vector2d sB, Vector2d dB) {        
        dA.normalize();
        dB.normalize();
        if (dA.equals(dB)) {
            return true;
        } else {
            return false;
        }
    }

    /* Computes the nearest point of the line segment to the pointP.*/
    public static Vector2d getNearestPoint(Vector2d start, Vector2d end, Vector2d pointP, boolean justAbscissa) {

        //Now we need an equation for the line on which the points start and end lie.
        double a;
        double b;
        double c;
        Vector2d abscissa = new Vector2d(end.x - start.x, end.y - start.y);
        //Coefficients in the equation are normal vector of tmp.
        a = abscissa.y;
        b = -abscissa.x;
        //start point lies on the line, therefore we can use it to get c.
        c = -a * start.x - b * start.y;

        //Special cases solving.
        if (a == 0) {
            a = 0.001; //In case something messes up and a ends up being zero, we need to fix it, otherwise we'd divide by zero later.
        }
        if (a * a + b * b == 0) {
            a = a + 0.001; //Similar for a^2+b^2==0
        }

        //Coefficients of the equation for the line perpendicular to our line are -b, a, d; d will be counted. PointHeading lies on it, therefore we use its coordinates.
        double d = b * pointP.x - a * pointP.y;

        //Now we have to get the intersection of linex ax+by+c=0 and -bx+ ay+d=0, the foot point.
        //This is a general solution of system of equations consisting of ax+by+c=0 and -bx+ay+d=0.
        //We could use some Gaussian solver as well, but since we don't need to solve general systems of equations, this should be faster.
        double footXCor = (b * ((a * d + b * c) / (a * a + b * b)) - c) / a;
        double footYCor = (-a * d - b * c) / (a * a + b * b);

        /** The nearest point on the line to the pointP.*/
        Vector2d foot = new Vector2d(footXCor, footYCor);

        /*The point in the middle of the abscissa.*/
        Vector2d middlePoint = new Vector2d((start.x + end.x) / 2,(start.y + end.y) / 2);

        Vector2d footToMiddlePoint = new Vector2d(foot.x - middlePoint.x, foot.y - middlePoint.y);

        /** The nearest point of the abscissa to the pointP.*/
        Vector2d nearestPoint = new Vector2d(foot.x, foot.y);

        if (justAbscissa) {
            /* The foot point doesn't lie between start and end. Therefore we will choose start or end point - that which is nearer to the pointP.*/
            if (footToMiddlePoint.length() > abscissa.length()) {
                Vector2d startToPointP = new Vector2d(start.x - pointP.x,start.y - pointP.y);
                Vector2d endToPointP = new Vector2d(end.x - pointP.x,end.y - pointP.y);
                if (startToPointP.length() < endToPointP.length()) {
                    nearestPoint = start;
                } else {
                    nearestPoint = end;
                }
            }
        }
        return nearestPoint;
    }

    public static boolean pointIsLeftFromTheVector(Vector3d vector, Vector3d point) {
        double a = vector.x;
        double b = vector.y;
        //if (SteeringManager.DEBUG) System.out.println("Rovnice "+b+"*"+point.x+" - "+a+"*"+point.y+" = "+(b*point.x - a*point.y));
        return b*point.x - a*point.y  >= 0; //Equation of the half-plane is b*x - a*y <= 0. That means that the point is on the left side of the vector.
    }

    /**Returns the rotation vector, that after combining with the actualVelocity the vector on the left or right side will be created.*/
    public static Vector3d getTurningVector(Vector3d actualVelocity, boolean left) {
        Vector3d turningVector;
        if (left)
            turningVector = new Vector3d(actualVelocity.y, -actualVelocity.x, 0);   //Turns 45° left.
        else
            turningVector = new Vector3d(-actualVelocity.y, actualVelocity.x, 0);   //Turns 45° right.
        turningVector.scale(1 / (Math.sqrt(2)));
        Vector3d negativeVector = new Vector3d(-actualVelocity.x, -actualVelocity.y, 0);
        negativeVector.scale(1 - 1 / Math.sqrt(2));
        turningVector.add((Tuple3d) negativeVector);
        return turningVector;
    }

    /**Returns the rotation vector perpendicular to the actualVelocity.*/
    public static Vector3d getTurningVector2(Vector3d actualVelocity, boolean left) {
        Vector3d turningVector;
        if (left)
            turningVector = new Vector3d(actualVelocity.y, -actualVelocity.x, 0);   //Turns 45° left.
        else
            turningVector = new Vector3d(-actualVelocity.y, actualVelocity.x, 0);   //Turns 45° right.
        return turningVector;
    }

    public static double radiansToDegrees(double rad) {
        return ((180*rad / Math.PI) % 360);
    }

    public static double degreesToRadians(double deg) {
        return ( Math.PI*deg / 180);
    }

    /**
     * @return all points of intersection between circles
     * @param P0 center of first circle
     * @param r0 radius of first circle
     * @param P1
     * @param r1

     */
    public static Location[] commonPoints(Location P0, double r0, Location P1, double r1) {
        Location[] result = new Location[2];
        result[0] = null;
        result[1] = null;

        int d = (int) P1.getDistance2D(P0);
        // no commonpoints
        if (d > r0 + r1 || d < Math.abs(r0 - r1)) {
            return result;
        }

        double a = (double) (r0 * r0 - r1 * r1 + d * d) / (double) (2 * d);
        Location P2 = P0.add((P1.sub(P0)).scale(a / d));

        double h = Math.sqrt(r0 * r0 - a * a);

        int x3 = (int) (P2.x - h * (P1.y - P0.y) / d);
        int y3 = (int) (P2.y + h * (P1.x - P0.x) / d);

        int x32 = (int) (P2.x + h * (P1.y - P0.y) / d);
        int y32 = (int) (P2.y - h * (P1.x - P0.x) / d);
        result[0] = new Location(x3, y3);
        result[1] = new Location(x32, y32);
        return result;
    }
    @Deprecated
    public static double getAngleOld(Location botLocation, Location focus, Location point)
    {
        
        Vector2d foot = getNearestPoint(new Vector2d(botLocation.x,botLocation.y),new Vector2d(focus.x,focus.y),new Vector2d(point.x,point.y),false);
        Location footL = new Location(foot.x,foot.y,botLocation.z);
        Location pointZ = new Location(point.x,point.y,botLocation.z);
        
        
        
        
        return Math.asin(pointZ.sub(footL).getLength() / pointZ.sub(botLocation).getLength());
        
    }
    public static double getAngle(Location botLocation, Rotation botRotation, Location point)
    {
        Location moveVec = point.sub(botLocation).getNormalized();
        
        return Math.acos(botRotation.toLocation().getNormalized().dot2D(moveVec));
        
    }

}
