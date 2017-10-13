package cz.cuni.amis.pogamut.ut2004.utils;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Jump;

/**
 * Class with utility methods for converting to Unreal units plus some handy constants.<p>
 * Measures are in Unreal Engine native units called Unreal units (UU).
 * @author ik
 */
public class UnrealUtils {

	public static final int iNT_NONE = Integer.MAX_VALUE;
	
	public static final long lONG_NONE = Long.MAX_VALUE;
	
	public static final float fLOAT_NONE = Float.MAX_VALUE;
	
	public static final double dOUBLE_NONE = Double.MAX_VALUE;
	
	public static final Integer INT_NONE = Integer.MAX_VALUE;
	
	public static final Long LONG_NONE = Long.MAX_VALUE;
	
	public static final Float FLOAT_NONE = Float.MAX_VALUE;
	
	public static final Double DOUBLE_NONE = Double.MAX_VALUE;
	
	public static final String STRING_NONE = "@@NONE@@";
	
	public static final Point3d POINT3D_NONE = new Point3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
	
	public static final Vector3d VECTOR3D_NONE = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
	
    /**
     * Radius in UU of the bounding cylinder used for collision detection.
     */
    public static final double CHARACTER_COLLISION_RADIUS = 25;
    /**
     * Height in UU of the bounding cylinder used for collision detection.
     */
    public static final double CHARACTER_COLLISION_HEIGHT = 44;
    /**
     * Height in UU of the bot when stading.
     */
    public static final double CHARACTER_HEIGHT_STANDING = 96;
    /**
     * Height in UU of the bot when crouching.
     */
    public static final double CHARACTER_HEIGHT_CROUCHING = 64;
    /**
     * Speed of the bot while running.
     * TODO: fill after the reception of INIT message.
     * @deprecated you should not use this as it is not reliable in multi-bot scenario
     */
    public static Velocity CHARACTER_RUN_SPEED;
    /**
     * Speed of the bot while walking.
     * TODO: fill after the reception of INIT message.
     * @deprecated you should not use this as it is not reliable in multi-bot scenario
     */
    public static Velocity CHARACTER_WALK_SPEED;
    /**
     * Center of gravity - distance from the floor in UU.
     * TODO: estimate
     */
    public static final double BOT_CENTER_OF_GRAVITY_HEIGHT = 50;
    /**
     * NavPoint distance from the floor.
     * TODO: estimate
     */
    public static final double NAV_POINT_HEIGHT = 10;

    public static final double FULL_ANGLE_IN_UNREAL_DEGREES = 65536;
    
    public static final double ONE_DEGREE_IN_UNREAL_DEGREES = ((double)FULL_ANGLE_IN_UNREAL_DEGREES) / ((double)360);
    
    public static final double ONE_RAD_IN_UNREAL_DEGREES = ((double)FULL_ANGLE_IN_UNREAL_DEGREES) / ((double)2*Math.PI);
    
    public static final double ONE_UNREAL_DEGREE_IN_DEGREES = ((double)360) / ((double)FULL_ANGLE_IN_UNREAL_DEGREES);
    
    public static final double ONE_UNREAD_DEGREE_IN_RAD = ((double)2*Math.PI) / ((double)FULL_ANGLE_IN_UNREAL_DEGREES);
    
    /**
     * @deprecated use {@link UnrealUtils#ONE_UNREAD_DEGREE_IN_RAD}
     */
    public static final double UT_ANGLE_TO_RAD = 2*Math.PI / ((double)FULL_ANGLE_IN_UNREAL_DEGREES);

    /**
     * @deprecated use {@link UnrealUtils#ONE_DEGREE_IN_UNREAL_DEGREES}
     */
    public static final double DEG_TO_UT_ANGLE = ((double)FULL_ANGLE_IN_UNREAL_DEGREES) / ((double)360);

    /**
     * @deprecated 
     */
    public static final double UT_ANGLE_TO_DEG =  FULL_ANGLE_IN_UNREAL_DEGREES * 360;
    
    /**
     * @deprecated use {@link UnrealUtils#ONE_RAD_IN_UNREAL_DEGREES}
     */
    public static final double RAD_TO_UT_ANGLE =  ((double)FULL_ANGLE_IN_UNREAL_DEGREES) / (2 * Math.PI);
    
    /**
     * UT2004 time is running 110% of normal time. I.e., when 1 sec pass according to {@link System#currentTimeMillis()} than 1.1 secs pass according
     * to UT2004.
     */
    public static final double UT2004_TIME_SPEED = 1.1;
    
    /**
     * Force to be applied to {@link Jump#setForce(Double)} if full jump is needed.
     * <p><p>
     * Note that you actually do not need to set this value directly as GB2004 always assumes you want to do full jump. 
     */
    public static final int FULL_JUMP_FORCE = 340;
    
    /**
     * Force to be applied to {@link Jump#setForce(Double)} if full double jump is needed.
     * <p><p>
     * Note that you actually do not need to set this value directly as GB2004 always assumes you want to do full double jump.
     * We raised this value by 50 (was 705) in GameBots of Pogamut 3.3.1 because of problems of accessing spots 
     * on the verge of reachability with double jump. 
     */
    public static final int FULL_DOUBLEJUMP_FORCE = 755;
    
    /**
     * Delay to be made between two jumps in double jumping ({@link Jump#setDelay(Double)}) if full double jump is needed.
     * <p><p>
     * Note that you actually do not need to set this value directly as GB2004 always assumes you want to do full double jump.
     * <p><p>
     * In seconds. 
     */
    public static final double FULL_DOUBLEJUMP_DELAY = 0.39;
    
    /**
     * Standard max. velocity of bots (it's actually almost 440 but it is better to have this number lower as you will check whether
     * you're running at max speed with it...).
     */
    public static final double MAX_VELOCITY = 439.5;
    
    /**
     * Converts angle in degrees (0-360) to Unreal units used for angles (0 - 65536).
     * @param degrees Angle in degrees
     * @return corresponding angle in Unreal units
     */
    public static int degreeToUnrealDegrees(double degrees) {
        return (int)Math.round((degrees * (double)ONE_DEGREE_IN_UNREAL_DEGREES));
    }

    /**
     * Converts Unreal degrees (0-65536) to normal degrees (0 - 360).
     * @param unrealDegrees
     * @return
     */
    public static double unrealDegreeToDegree(int unrealDegrees) {
        return ((double)unrealDegrees) * ((double)ONE_UNREAL_DEGREE_IN_DEGREES);
    }

    /**
     * Converts Unreal degrees (0-65536) to radians (0 - 2*PI)
     * @param unrealDegrees
     * @return
     */
    public static double unrealDegreeToRad(double unrealDegrees) {
        return ((double)unrealDegrees) * ((double)ONE_UNREAD_DEGREE_IN_RAD);
    }

    /**
     * Tells whether UnrealId belongs to POGAMUT-CONTROLLED-BOT.
     * @param botId
     * @return
     */
    public static boolean isBotId(UnrealId id) {
    	return id.getStringId().contains("RemoteBot");
    }
    
}