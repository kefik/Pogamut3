package cz.cuni.amis.pogamut.ut2004.agent.navigation.loquenavigator;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathRunner;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.WallCollision;
import cz.cuni.amis.pogamut.ut2004.utils.LinkFlag;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.utils.NullCheck;

/**
 * Responsible for direct running to location.
 *
 * <p>This class commands the agent directly to the given location. Silently
 * tries to resolve incidental collisions, troubling pits, obstacles, etc.
 * In other words, give me a destination and you'll be there in no time.</p>
 *
 * <h4>Precise jumper</h4>
 *
 * Most of the incident running problems and troubles can be solved by precise
 * single-jumping or double-jumping. This class calculates the best spots for
 * initiating such jumps and then follows jump sequences in order to nicely
 * jump and then land exactly as it was desired.
 *
 * <h4>Pogamut troubles</h4>
 *
 * This class was supposed to use autotrace rays to scan the space and ground
 * in from of the agent. However, results of depending on these traces were
 * much worst than jumping whenever possible. Therefore, no autotrace is being
 * used and the agent simply jumps a lot. Some human players do that as well.
 * See {@link #runToLocation } for details.
 *
 * <h4>Speed</h4>
 *
 * The agent does not ever try to run faster than with speed of <i>1.0</i> as
 * it is used by most of <i>body.runTo*()</i> methods. Anyway, speeding is not
 * available to common players (AFAIK), so why should this agent cheat?
 *
 * <h4>Focus</h4>
 *
 * This class works with destination location as well as agent focal point.
 * Since the agent can look at something else rather than the destination,
 * this running API is also suitable for engaging in combat or escaping from
 * battles.
 * 
 * <h4>Assumptions</h4>
 * Following constants have been found out by playing around with UT2004:
 * <ul>
 * <li>single jump can jump 60 units up at max</li>
 * <li>double jump can jump 120 units up at max, UT2004 can overcome obstacle of 5 units while jumping, so we should be able to get 125 units UP</li>
 * <li>if not jumping, bot can overcome step 37 units high at max</li>
 * <li>full speed is 440 units/sec</li>
 * <li>second jump will always give us full speed of 445 units/sec</li>
 * <li>single jump (if no falling/step is involved) takes 1 secs, i.e., it moves us forward the same amount of units as is our current velocity</li>
 * <li>double jump (if no falling/step is involved) takes 1.5 secs, i.e., it moves us forward delay*our velocity + 1*445 forward</li>
 * <li>how high we can jump with single/double does not depends on our velocity - we will always reach the peak of the jump</li>
 * <li>we reach peak of the single jump in 0.5s</li>
 * </ul> 
 *
 * @author Jimmy, Knight
 * @author Juraj Simlovic [jsimlo@matfyz.cz]
 */
public class KefikRunner implements IUT2004PathRunner {
    
	// MAINTAINED CONTEXT
	
	/**
     * Number of steps we have taken.
     */
    private int runnerStep = 0;

    /**
     * Jumping sequence of a single-jumps.
     */
    private int jumpStep = 0;

    /**
     * Collision counter.
     */
    private int collisionNum = 0;
    
    /**
     * Collision location.
     */
    private Location collisionSpot = null;
    
    // COMPUTED CONTEXT OF THE runToLocation
    
    /**
     * Current distance to the target, recalculated every {@link KefikRunner#runToLocation(Location, Location, ILocated, NavPointNeighbourLink, boolean)} invocation.
     */
    private double distance;
    
    /**
     * Current 2D distance (only in x,y) to the target, recalculated every {@link KefikRunner#runToLocation(Location, Location, ILocated, NavPointNeighbourLink, boolean)} invocation.
     */
    private double distance2D;

    /**
     * Current Z distance to the target (positive => target is higher than us, negative => target is lower than us), recalculated every {@link KefikRunner#runToLocation(Location, Location, ILocated, NavPointNeighbourLink, boolean)} invocation.
     */
    private double distanceZ;
    
    /**
     * Current velocity of the bot, recalculated every {@link KefikRunner#runToLocation(Location, Location, ILocated, NavPointNeighbourLink, boolean)} invocation.
     */
    private double velocity;
    
    /**
     * Current velocity in Z-coord (positive, we're going up / negative, we're going down), recalculated every {@link KefikRunner#runToLocation(Location, Location, ILocated, NavPointNeighbourLink, boolean)} invocation.
     */
    private double velocityZ;
    
    /**
     * Whether the jump is required somewhere along the link, recalculated every {@link KefikRunner#runToLocation(Location, Location, ILocated, NavPointNeighbourLink, boolean)} invocation.
     */
    private boolean jumpRequired;
    
    /**
     * In case of fall ({@link KefikRunner#distanceZ} < 0), how far can we get with normal fall.
     */
    private double fallDistance;
    
    // CONTEXT PASSED INTO runToLocation
    
    /**
     * Current context of the {@link KefikRunner#runToLocation(Location, Location, Location, ILocated, NavPointNeighbourLink, boolean)}.
     */
    private Location runningFrom;
    
    /**
     * Current context of the {@link KefikRunner#runToLocation(Location, Location, Location, ILocated, NavPointNeighbourLink, boolean)}.
     */
    private Location firstLocation;
    
    /**
     * Current context of the {@link KefikRunner#runToLocation(Location, Location, Location, ILocated, NavPointNeighbourLink, boolean)}.
     */
    private Location secondLocation;
    
    /**
     * Current context of the {@link KefikRunner#runToLocation(Location, Location, Location, ILocated, NavPointNeighbourLink, boolean)}.
     */
    private ILocated focus;
    
    /**
     * Current context of the {@link KefikRunner#runToLocation(Location, Location, Location, ILocated, NavPointNeighbourLink, boolean)}.
     */
    private NavPointNeighbourLink link;
    
    /**
     * Current context of the {@link KefikRunner#runToLocation(Location, Location, Location, ILocated, NavPointNeighbourLink, boolean)}.
     */
    private boolean reachable;
    
    private boolean forceNoJump;    
    
    /** Last received wall colliding event */
    protected WallCollision lastCollidingEvent = null;

	/** If we have collided in last second we will signal it */
    private static final double WALL_COLLISION_THRESHOLD = 1;
    
    /*========================================================================*/
    
    /**
     * Returns link the bot is currently running on ... might not exist, always check against NULL!
     */
    public NavPointNeighbourLink getLink() {
    	return link;
    }
    
    /*========================================================================*/

    /**
     * Initializes direct running to the given destination.
     */
    public void reset()
    {
        // reset working info
        runnerStep = 0;
        jumpStep = 0;
        collisionNum = 0;
        collisionSpot = null;
        lastCollidingEvent = null;
        distance = 0;
        distance2D = 0;
        distanceZ = 0;
        velocity = 0;
        velocityZ = 0;
        jumpRequired = false;
        forceNoJump = false;
    }

    /*========================================================================*/
   
    private void debug(String message) {
    	if (log.isLoggable(Level.FINER)) log.finer("Runner: " + message);
    }
    
    /**
     * Return how far the normal falling will get us. (Using guessed consts...)
     * @param distanceZ
     * @return
     */
    private double getFallDistance(double distanceZ) {
    	distanceZ = Math.abs(distanceZ);
    	if (distanceZ == 60) return 160;
    	if (distanceZ < 60) return 2.66667*distanceZ;
    	return 1.3714 * distanceZ + 35.527;
    }
    
    /**
     * Returns how far the jump will get at max.
     * 
     * @param doubleJump
     * @param jumpDelay
     * @param jumpForce
     * @param distanceZ to be jumped to (fallen to)
     * @return
     */
    private double getMaxJumpDistance(boolean doubleJump, double jumpDelay, double jumpForce, double distanceZ, double velocity) {
    	if (doubleJump) {
    		jumpForce = Math.min(UnrealUtils.FULL_DOUBLEJUMP_FORCE, jumpForce);
    	} else {
    		jumpForce = Math.min(UnrealUtils.FULL_JUMP_FORCE, jumpForce);
    	}
    	jumpDelay = Math.min(0.75, jumpDelay);
    	
    	if (distanceZ >= -5) {
    		// jumping up
    		if (doubleJump) {
    			return velocity * jumpDelay + (jumpForce / UnrealUtils.FULL_DOUBLEJUMP_FORCE) * 400 * (1 + jumpDelay);
    		} else {
    			return velocity * jumpForce;
    		}
    	} else {
    		// falling down
    		return getFallDistance(distanceZ) + getMaxJumpDistance(doubleJump, jumpDelay, jumpForce, 0, velocity);
    	}
    }
    
    /**
     * Returns how far the jump will get us when we want to jump to the height of 'distanceZ'.
     * <p><p>
     * Assumes 'distanceZ' > 0
     * 
     * @param doubleJump whether we're using double jump
     * @param jumpDelay (max 0.75)
     * @param jumpForce (max {@link UnrealUtils#FULL_DOUBLEJUMP_FORCE} / {@link UnrealUtils#FULL_JUMP_FORCE})
     * @param distanceZ to be jumped to (must be > 0)
     * @return
     */
    private double getJumpUpDistance(boolean doubleJump, double jumpDelay, double jumpForce, double distanceZ, double velocity) {
    	double jumpForceHeight;
    	
    	double result;
    	
    	if (doubleJump) {
    		// COUNTING FOR DOUBLE JUMP
    		
    		jumpDelay = Math.min(0.75, jumpDelay);
    		jumpForce = Math.min(UnrealUtils.FULL_DOUBLEJUMP_FORCE, jumpForce);
    		
    		// how high the jump force can get us (in distanceZ units)
    		jumpForceHeight = (jumpForce / UnrealUtils.FULL_DOUBLEJUMP_FORCE) * 125;
    		
    		// total time of the jump in case of jumping to 'distanceZ = 0'
    		double totalTimeOfTheJump = (jumpForce / UnrealUtils.FULL_DOUBLEJUMP_FORCE) + jumpDelay;
    		
    		if (jumpForceHeight > distanceZ) {
    			// we're OK
    			result = 
    				// distance traveled when ascending with first jump
    				velocity * jumpDelay
    				// distance traveled when ascending with second jump
      			  + UnrealUtils.MAX_VELOCITY * ((totalTimeOfTheJump-jumpDelay)/2)
    			    // distance traveled when falling to 'distanceZ'
    			  + UnrealUtils.MAX_VELOCITY * (((totalTimeOfTheJump-jumpDelay)/2) * (1-distanceZ/jumpForceHeight)); 
    		} else {
    			// we're doomed, we should return the distance of the peak of the jump
    			result =
    				// distance traveled when ascending with first jump
    				velocity * jumpDelay
    				// distance traveled when ascending with second jump
    			  + ((totalTimeOfTheJump-jumpDelay)/2) * UnrealUtils.MAX_VELOCITY;
    		}
    	
    	} else {
    		// COUNTING FOR SINGLE JUMP
    	
    		jumpForce = Math.min(UnrealUtils.FULL_JUMP_FORCE, jumpForce);
    		
    		// how high the jump force can get us (in distanceZ units)
    		jumpForceHeight = (jumpForce / UnrealUtils.FULL_JUMP_FORCE) * 55;
    		
    		// total time of the jump in case of jumping to 'distanceZ = 0'
    		double totalTimeOfTheJump = jumpForce / UnrealUtils.FULL_JUMP_FORCE;
    		
    		if (jumpForceHeight > distanceZ) {
    			// we're OK
    			result = 
        			   // distance we will travel when ascending
    				   velocity * (totalTimeOfTheJump/2)
    				   // distance we will travel when falling to the 'distanceZ' height
    			     + velocity * ((totalTimeOfTheJump/2) * (1 - (distanceZ / jumpForceHeight)));
    		} else {
    			// we're doomed, we should return the PEAK of the jump
    			result = velocity * (totalTimeOfTheJump/2);
    		}
    	}
    	
    	return result;
    }
    
    /**
     * Handles running directly to the specified location.
     *
     * <h4>Pogamut troubles</h4>
     *
     * <p>Reachchecks are buggy (they ignore most of the pits). Autotrace rays
     * are buggy (they can not be used to scan the ground). Now, how's the agent
     * supposed to travel along a map full of traps, when he is all blind, his
     * guide-dogs are stupid and blind as well and his white walking stick is
     * twisted?</p>
     *
     * <p>There is only one thing certain here (besides death and taxes): No
     * navpoint is ever placed above a pit or inside map geometry. But, navpoint
     * positions are usually the only places where we know the ground is safe.
     * So, due to all this, the agent tries to jump whenever possible and still
     * suitable for landing each jump on a navpoint. This still helps overcome
     * most of the map troubles. Though it is counter-productive at times.</p>
     *
     * @param firstLocation Location to which to run.
     * @param secondLocation Location where to continue (may be null).
     * @param focus Location to which to look.
     * @param reachable Whether the location is reachable.
     * @param forceNoJump wheter NOT TO jump
     * @return True, if no problem occured.
     */
    @Override
    public boolean runToLocation(Location runningFrom, Location firstLocation, Location secondLocation, ILocated focus, NavPointNeighbourLink navPointsLink, boolean reachable, boolean forceNoJump)
    {
        // take another step
        runnerStep++;
    	
    	// save context
    	this.runningFrom = runningFrom;
    	this.firstLocation = firstLocation;
    	this.secondLocation = secondLocation;
    	this.focus = focus;
    	this.link = navPointsLink;
    	this.reachable = reachable;
    	this.forceNoJump = forceNoJump;
    	
        
        // compute additional context
        distance = memory.getLocation().getDistance(firstLocation);
        distance2D = memory.getLocation().getDistance2D(firstLocation);
        distanceZ = firstLocation.getDistanceZ(memory.getLocation());
        if (distanceZ >= 0) fallDistance = 0;
        else fallDistance = getFallDistance(distanceZ);
        velocity = memory.getVelocity().size();
        velocityZ = memory.getVelocity().z;
        jumpRequired = 	
        				!reachable ||
        				(link != null 
                          && (((link.getFlags() & LinkFlag.JUMP.get()) != 0) 
          		              || (link.isForceDoubleJump())
          		              || (link.getNeededJump() != null)
          		             )
          		        )
        ; 
    	
        // DEBUG LOG
        
        if (log != null && log.isLoggable(Level.FINER)) {
        	debug("KefikRunner!");
        	debug("running to    = " + firstLocation + " and than to " + secondLocation + " and focusing to " + focus);
        	debug("bot position  = " + memory.getLocation());
        	debug("distance      = " + distance);
        	debug("distance2D    = " + distance2D);
    		debug("distanceZ     = " + distanceZ);
    		debug("fallDistance  = " + fallDistance);
    		debug("velocity      = " + velocity);
    		debug("velocityZ     = " + velocityZ);
    		debug("jumpRequired  = " + jumpRequired 
    									+ (!reachable ? " NOT_REACHABLE" : "") 
    									+ (link == null ? 
    											"" 
    										  : ( 
    											    (link.getFlags() & LinkFlag.JUMP.get()) != 0 ? " JUMP_FLAG" : "") + (link.isForceDoubleJump() ? " DOUBLE_JUMP_FORCED" : "") + (link.getNeededJump() != null ? " AT[" + link.getNeededJump() + "]" : ""
    											)
    									  )   
    			 );
    		debug("reachable     = " + reachable);
    		if (link != null) {
    			debug("link          = " + link);
    		} else {
    			debug("LINK NOT PRESENT");
    		}
    		debug("collisionNum  = " + collisionNum);
    		debug("collisionSpot = " + collisionSpot);
    		debug("jumpStep      = " + jumpStep);
    		debug("runnerStep    = " + runnerStep);
        }
        
        // DELIBERATION
        
        if (runnerStep <= 1) {
        	debug("FIRST STEP - start running towards new location");
            move(firstLocation, secondLocation, focus);
        }
        
        // are we jumping already?
        if (jumpStep > 0)
        {
        	debug("we're already jumping");
            return iterateJumpSequence();
        }
        
        // collision experienced?
        if (isColliding())
        {
        	debug("sensing collision");
            // try to resolve it
            return resolveCollision();
        } else {
        	if (collisionSpot != null || collisionNum != 0) {
        		debug("no collision, clearing collision data");
        		collisionNum = 0;
        		collisionSpot = null;
        	}
        }
        
        if (velocity < 5 && runnerStep > 1) {
        	debug("velocity is zero and we're in the middle of running");
        	if (link != null && (link.getFromNavPoint().isLiftCenter() || link.getFromNavPoint().isLiftExit())) {
        		if (link.getFromNavPoint().isLiftCenter()) {
        			debug("we're standing on the lift center, ok");
        		} else {
        			debug("we're standing on the lift exit, ok");
        		}
        	} else {
        		debug("and we're not standing on the lift center");
        		return initJump(true);
        	}
        }
        
        // check jump
        if (jumpRequired) {
        	debug("jump is required");
	        return resolveJump();
        }
        
        // just continue with ordinary run
        debug("keeping running to the target");
        move(firstLocation, secondLocation, focus);
        
        return true;
    }
    
    /*========================================================================*/

    /**
     * Decision has been made, we need to jump ({@link KefikRunner#jumpRequired} is true (!reachable || jump flag / needed jump / force double jump) and no collision has interrupted us) ... 
     * but we do not know from where/when and even if we should jump yet.
     * <p><p>
     * This methods checks whether it is a right time to initiate a jump sequence based on various distances.
     * <p><p>
     * Due to inevitability of ensuring of landing on destination locations,
     * jumps may only be started, when it is appropriate. This method decides,
     * whether jump is appropriate.
     *
     * @return True, if no problem occured.
     */
    private boolean resolveJump()
    {    		
    	debug("resolveJump(): called");
    	
    	// cut the jumping distance2D of the next jump, this is to allow to
        // jump more than once per one runner request, while ensuring that
        // the last jump will always land exactly on the destination..
        int jumpDistance2D = ((int)distance2D) % 1000;
                
	    debug("resolveJump(): jumpDistance2D = " + jumpDistance2D);
	    
	    // follow the deliberation about the situation we're currently in
	    boolean jumpIndicated = false;      // whether we should jump now
	    boolean mustJumpIfIndicated = false; // whether we MUST jump NOW
	            
        boolean goingToJump = false;
        
        // deliberation, whether we may jump
        
        if (link != null &&
        	(	((link.getFlags() & LinkFlag.JUMP.get()) != 0) 
          		              || (link.isForceDoubleJump())
          		              || (link.getNeededJump() != null)
           )) {
        	debug("resolveJump(): deliberation - jumping condition present");
        	jumpIndicated = true && !forceNoJump;
        }
        
        if (jumpDistance2D < 250) {
        	debug("resolveJump(): we've missed all jumping opportunities (jumpDistance2D < 250)");
        	if (runnerStep > 1) {
        		debug("resolveJump(): and runnerStep > 1, if indicated we will be forced to jump right now");
                mustJumpIfIndicated = true;
            } else {            	
            	debug("resolveJump(): but runnerStep <= 1, can't force jump yet");
            }
        }
        
        debug("resolveJump(): jumpIndicated       = " + jumpIndicated);
        debug("resolveJump(): mustJumpIfIndicated = " + mustJumpIfIndicated);
        
        if (jumpIndicated && mustJumpIfIndicated) {
        	if (distanceZ > 0) {
        		debug("resolveJump(): we MUST jump!");
        		return prepareJump(true); // true == forced jump
        	} else {
        		debug("resolveJump(): we MUST fall down with a jump!");
        		return prepareJump(true); // true == forced jump
        	}
        } else
        if (jumpIndicated) {
        	debug("resolveJump(): we should jump");
        	return prepareJump(false); // false == we're not forcing to jump immediately         	
        } else {
        	debug("resolveJump(): we do not need to jump, waiting to reach the right spot to jump from");
        	// otherwise, wait for the right double-jump distance2D
        	// meanwhile: keep running to the location..
        	move(firstLocation, secondLocation, focus);
        	return true;
        }
    }
    
    /*========================================================================*/
    
    /**
     * This method is called from {@link KefikRunner#resolveJump()} that has decided that the jump is necessary to reach the 
     * the target (it is already known that distanceZ > 0).
     * <p><p>
     * jumpForced == true ... we will try to run no matter what
     * <p><p>
     * jumpForced == false ... we will check whether the time is right for jumping assessing the {@link KefikRunner#distanceZ}.
     * 
     * @return whether we should reach the target
     */
    private boolean prepareJump(boolean jumpForced) {
    	debug("prepareJump(): called");    	
    	
    	Location direction = Location.sub(firstLocation, memory.getLocation()).setZ(0);
    	direction = direction.getNormalized();
	    Location velocityDir = new Location(memory.getVelocity().asVector3d()).setZ(0);
	    velocityDir = velocityDir.getNormalized();
	    Double jumpAngleDeviation = Math.acos(direction.dot(velocityDir));
	    
	    boolean angleSuitable = !jumpAngleDeviation.isNaN() && jumpAngleDeviation < (Math.PI / 9);
	    
	    debug("prepareJump(): jumpAngleDeviation = " + jumpAngleDeviation);
	    debug("prepareJump(): angleSuitable      = " + angleSuitable);
    	
    	if (jumpForced) {
    		debug("prepareJump(): jump is forced, bypassing jump checks!");
    	} else {
	    	debug("prepareJump(): jump is not forced, checking jump conditions");
	    	
	    	
	    	if (velocity < 200 && distance2D > getMaxJumpDistance(true, UnrealUtils.FULL_DOUBLEJUMP_DELAY, UnrealUtils.FULL_DOUBLEJUMP_FORCE, distanceZ, velocity)) {
	    		debug("prepareJump(): velocity is too low for jump (velocity < 200) and target is too far away to jump there with double jump");
	    		debug("prepareJump(): proceeding with the straight movement to gain speed");
	    		move(firstLocation, secondLocation, focus);
	    		return true;
	    	}
	    	
	    	if (!angleSuitable) {
	    		debug("prepareJump(): angle is not suitable for jumping (angle > 20 degrees)");
	    		debug("prepareJump(): proceeding with the straight movement to gain speed");
	    		move(firstLocation, secondLocation, focus);
	    		return true;
	    	}
	    	
	    	debug("prepareJump(): velocity & angle is OK!");
	    }
    	
		if (distanceZ >= 0) {
    		debug("prepareJump(): JUMP (distanceZ >= 0)");
        	return initJump(jumpForced);
    	} else {
    		debug("prepareFall(): FALL (distanceZ < 0)");
        	return initFall(jumpForced);
    	}
    }
    
    /*========================================================================*/

    private double adjustJumpForce(double distanceSafeZone, boolean doubleJump, double jumpForce, double jumpDelay) {
    	double distanceJumped = getJumpUpDistance(doubleJump, jumpDelay, jumpForce, distanceZ, velocity);
    	debug("initJump(): adjusting jumpForce...");
		while (distanceJumped-distanceSafeZone < distance2D && // jump distance is still not enough 
			   (    (doubleJump && jumpForce < UnrealUtils.FULL_DOUBLEJUMP_FORCE) 
			    || (!doubleJump && jumpForce < UnrealUtils.FULL_JUMP_FORCE)) // and we still have a room to make the jump longer
			   ) {
			jumpForce += 10;
			distanceJumped = getJumpUpDistance(doubleJump, jumpDelay, jumpForce, distanceZ, velocity);
		}
		// clamp the jumpForce
		if (doubleJump) {
			jumpForce = Math.min(jumpForce, UnrealUtils.FULL_DOUBLEJUMP_FORCE);
		} else {
			jumpForce = Math.min(jumpForce, UnrealUtils.FULL_JUMP_FORCE);
		}
		debug("initJump(): jumpForce = " + jumpForce);
		return jumpForce;
    }
    
    /**
     * We have to jump up (distanceZ > 0) if there is a possibility that we get there by jumping
     * (i.e., params for jump exists that should get us there) or 'jumpForced is true'.
     * <p><p>
     * Decides whether we need single / double jump and computes
     * the best args for jump command according to current velocity.
     * 
     * @param jumpForced
     */
    private boolean initJump(boolean jumpForced) {
    	debug("initJump(): called");
    	
    	boolean shouldJump = true;
    	
    	boolean doubleJump = true;
    	double jumpForce = UnrealUtils.FULL_DOUBLEJUMP_FORCE;
    	double jumpDelay = UnrealUtils.FULL_DOUBLEJUMP_DELAY;
    	
    	if (distanceZ > 130) {
    		debug("initJump(): jump could not be made (distanceZ = " + distanceZ + " > 130)");
    		if (jumpForced) {
    			debug("initJump(): but jump is being forced!");
    		} else {
    			debug("initJump(): jump is not forced ... we will wait till the bot reach the right jumping spot");
    			move(firstLocation, secondLocation, focus);
            	jumpStep = 0; // we have not performed the JUMP
            	return true;
    		}
    	}
    	
    	
    	//here we try to determine if single jump is enough - we check if we are colliding, distanceZ and NeededJump.getZ attribute!
    	if (collisionNum == 0 && distanceZ < 55 && distance2D < velocity * 0.85 
    			&& (link == null || link.getNeededJump() == null || link.getNeededJump().getZ() <= UnrealUtils.FULL_JUMP_FORCE)  ) {
    		debug("initJump(): single jump suffices (distanceZ < 55 && distance2D = " + distance2D + " < " + (velocity*0.85) +" = velocity * 0.85) && (link.getNeededJump == null ||  link.getNeededJump().getZ() <= UnrealUtils.FULL_JUMP_FORCE ))");
    		doubleJump = false;
    		jumpForce = UnrealUtils.FULL_JUMP_FORCE;
    	}
    	
    	double jumpUp_force = 0;
    	if (doubleJump) {
    		if (collisionNum != 0) //when colliding, make maximum double jump!
    			jumpUp_force = UnrealUtils.FULL_DOUBLEJUMP_FORCE;
    		else
    			jumpUp_force = UnrealUtils.FULL_DOUBLEJUMP_FORCE * ((distanceZ+5) / 110);//this constant 110 makes magic! careful!
    		jumpUp_force = Math.min(jumpUp_force, UnrealUtils.FULL_DOUBLEJUMP_FORCE);
    	} else {
    		jumpUp_force = UnrealUtils.FULL_JUMP_FORCE * ((distanceZ+5) / 55);
    		// JUMP FORCE SHOULD BE ALWAYS OK HERE AS WE'VE CHECKED distanceZ BEFORE!
    		jumpUp_force = Math.min(jumpUp_force, UnrealUtils.FULL_JUMP_FORCE);
    	}
    	//Potential new Heuristics - if NeededJump is not null we will set our jump according to it 
    	//(and increase it by 100 otherwise the bot will not make it)!
    	//it didn't seem to help, commented for now
    	/*if (collisionNum == 0 && (link != null && link.getNeededJump() != null)) {    		
    		jumpUp_force = link.getNeededJump().getZ() + 100;
    		if (jumpUp_force > UnrealUtils.FULL_JUMP_FORCE)
    			doubleJump = true;    		
    	}*/   	
    	
    	debug("initJump(): minimum force to jump to height " + distanceZ + " with " + (doubleJump ? "double" : "single") + " is " + jumpUp_force);
    	double distanceSafeZone = 0;    	
    	debug("initJump(): adjusting force to match jumping distance = " + distance2D + " = distance2D (safe zone = " + distanceSafeZone + ")");
    	jumpForce = adjustJumpForce(distanceSafeZone, doubleJump, jumpUp_force, jumpDelay);
    	double distanceJumped = getJumpUpDistance(doubleJump, jumpDelay, jumpForce, distanceZ, velocity);    	
		if (distanceJumped-distanceSafeZone < distance2D) {
			debug("initJump(): too short! (distanceJumped-" + distanceSafeZone + " = " + (distanceJumped-distanceSafeZone) + " < " + distance2D + " = distance2D)");
   			if (!doubleJump) {
   				debug("initJump(): trying double jump");
   				doubleJump = true;
				jumpUp_force = UnrealUtils.FULL_DOUBLEJUMP_FORCE * ((distanceZ+5) / 125);
	    		jumpUp_force = Math.min(jumpUp_force, UnrealUtils.FULL_DOUBLEJUMP_FORCE);
    	    	debug("initJump(): minimum force to jump to height " + distanceZ + " with double jump is " + jumpUp_force);
    	    	debug("initJump(): adjusting force to match jumping distance = " + distance2D + " = distance2D (safe zone = " + distanceSafeZone + ")");
    	    	jumpForce = adjustJumpForce(distanceSafeZone, doubleJump, jumpUp_force, jumpDelay);
    	    	distanceJumped = getMaxJumpDistance(doubleJump, jumpDelay, jumpForce, distanceZ, velocity);    
    	    	if (distanceJumped-distanceSafeZone < distance2D) {
    	    		debug("initJump(): still too short! (distanceJumped-" + distanceSafeZone + " = " + (distanceJumped-distanceSafeZone) + " < " + distance2D + " = distance2D)");
    	    		shouldJump = false;
    	    	} else {
    	    		debug("initJump(): distance ok (distanceJumped-" + distanceSafeZone + " = " + (distanceJumped-distanceSafeZone) + " >= " + distance2D + " = distance2D)");
    	    		shouldJump = true;
    	    	}
    		} else {
    			shouldJump = false;
    		}	    		
		} else {
			debug("initJump(): distance ok (distanceJumped-" + distanceSafeZone + " = " + (distanceJumped-distanceSafeZone) + " >= " + distance2D + " = distance2D)");
    		shouldJump = true;
		}
		
		if (shouldJump || jumpForced) {
			if (jumpForced && !shouldJump) {
				debug("initJump(): we should not be jumping, but jump is FORCED!");
			}
			jumpStep = 1; // we have performed the JUMP
	   		return jump(true, jumpDelay, jumpForce);
		} else {
			debug("initJump(): jump is not forced ... we will wait till the bot reach the right jumping spot");
			move(firstLocation, secondLocation, focus);
        	jumpStep = 0; // we have not performed the JUMP
        	return true;
		}
    }
    
    /**
     * We have to jump to fall down (distanceZ < 0) right now. Decides whether we need single / double jump and computes
     * the best args for jump command according to current velocity.
     */
    private boolean initFall(boolean jumpForced) {
    	debug("initFall(): called");
    	
    	jumpStep = 1;
    	
    	log.finer("Runner.initDoubleJumpSequence(): FALLING DOWN! Adjusting parameters of the jump for falling...");
    	// we're going to fall, thus we have to be careful not to overjump the target
    	
    	// remainind distance for which we need jumping
    	double remainingDistance2D = distance2D - fallDistance;
    	
    	debug("initFall(): distance2D          = " + distance2D);
    	debug("initFall(): falling will get us = " + fallDistance + " further");
    	debug("initFall(): remainingDistance2D = " + remainingDistance2D);
    	
    	// FULL DOUBLE JUMP
    	boolean doubleJump = true;
    	double jumpZ = 705;    		
    	
    	// single jump will get us about 300 forward
    	// double jump will get us about 450 forward
    	// -- note that above two constants taking into account also a jump itself (it gets us higher so falling down will take us further),
    	//    theoretically, we should compute much more complex equation but this seems to work OK
    	if (remainingDistance2D < velocity) {
    		debug("initFall(): single jump suffices (remainingDistance2D < velocity)");
    		doubleJump = false;
    		jumpZ = 340 * remainingDistance2D / 300;
    	} else
    	if (remainingDistance2D < 450) {
    		log.finer("initFall(): smaller double jump is needed (remainingDistance2D < 450)");
    		doubleJump = true;
    		jumpZ = 340 + 365 * (remainingDistance2D - 220) * 150;
    	} else {
    		log.finer("Runner.initDoubleJumpSequence(): full double jump is needed (remainingDistance2D > 450)");
    		doubleJump = true;
    		jumpZ = 705; 
    	}
    	
    	return jump(doubleJump, 0.39, jumpZ);
    }
    
    /*========================================================================*/    
    
    /**
     * Perform jump right here and now with provided args.
     */
    private boolean jump(boolean doubleJump, double delay, double force) {
    	if (doubleJump) {
    		debug("DOUBLE JUMPING (delay = " + delay + ", force = " + force + ")");
    	} else {
    		debug("JUMPING (delay = " + delay + ", force = " + force + ")");
    	}
    	body.jump(doubleJump, delay, force);
    	
    	return true;
    }
    
    private void move(ILocated firstLocation, ILocated secondLocation, ILocated focus) {
        Move move = new Move();
        if (firstLocation != null) {
            Location currentLocation = memory.getLocation();
            //Extend the first point too, as it will slow down here else.
            
            move.setFirstLocation(firstLocation.getLocation());
            if (secondLocation == null || secondLocation.equals(firstLocation)) {
                //We want to reach end of the path, we won't extend the second location.
                move.setSecondLocation(firstLocation.getLocation());
            } else {
                //Extend the second location so the bot doesn't slow down, when it's near the original target.
                double dist = firstLocation.getLocation().getDistance(secondLocation.getLocation());
                double quantifier = 1 + (200 / dist);
                
                Location extendedSecondLocation = firstLocation.getLocation().interpolate(secondLocation.getLocation(), quantifier);
                move.setSecondLocation(extendedSecondLocation);
            }
        } else if (secondLocation != null) {
            //First location was not set
            move.setSecondLocation(secondLocation.getLocation());
        }

        if (focus != null) {
            if (focus instanceof Player) {
                move.setFocusTarget((UnrealId) ((IWorldObject) focus).getId());
            } else {
                move.setFocusLocation(focus.getLocation());
            }
        }

        log.finer("MOVING: " + move);
        bot.getAct().act(move);
    }
    
    /*========================================================================*/
    
    /**
     * Tries to resolve collisions.
     *
     * <p>Only continuous collisions are resolved, first by a double jump, then
     * by a single-jump.</p>
     *
     * @return True, if no problem occured.
     */
    private boolean resolveCollision()
    {
        // are we colliding at a new spot?
        if (
            // no collision yet
            (collisionSpot == null)
            // or the last collision is far away
            || (memory.getLocation().getDistance2D(collisionSpot) > 120)
        ) {
            // setup new collision spot info
        	if (log != null && log.isLoggable(Level.FINER)) log.finer("Runner.resolveCollision(): collision");
            collisionSpot = memory.getLocation();
            collisionNum = 1;
            // meanwhile: keep running to the location..
            move(firstLocation, secondLocation, focus);
            return true;
        }
        // so, we were already colliding here before..
        // try to solve the problem according to how long we're here..
        else { 
            return initJump(true);
        }
    }

    /*========================================================================*/

    /**
     * Follows single-jump sequence steps.
     * @return True, if no problem occured.
     */
    private boolean iterateJumpSequence()
    {
    	debug("iterateJumpSequence(): called");
        // what phase of the jump sequence?
        switch (jumpStep) {
            // the first phase: wait for the jump
            case 1:
                // did the agent started the jump already?
                if (velocityZ > 100)
                {
                	debug("iterateJumpSequence(): jumping in progress (velocityZ > 100), increasing jumpStep");
                    jumpStep++;
                }
                // meanwhile: just wait for the jump to start
                debug("iterateJumpSequence(): issuing move command to the target (just to be sure)");
                move(firstLocation, secondLocation, focus);
                return true;

            //  the last phase: finish the jump
            default:
                // did the agent started to fall already
                if (velocityZ <= 0.01)
                {
                	debug("iterateJumpSequence(): jump ascension has ended (velocityZ < 0.01)");
                    jumpStep = 0;
                }
                debug("iterateJumpSequence(): continuing movement to the target");
                move(firstLocation, secondLocation, focus);
                return true;
        }
        
        
    }

    /*========================================================================*/
    
    protected boolean isColliding() {
    	if (lastCollidingEvent == null) return false;
    	debug("isColliding():"+"(memory.getTime():" + memory.getTime() + " - (lastCollidingEvent.getSimTime() / 1000):" + (lastCollidingEvent.getSimTime() / 1000) +" <= WALL_COLLISION_THRESHOLD:" + WALL_COLLISION_THRESHOLD +  " )");
    	if (memory.getTime() - (lastCollidingEvent.getSimTime() / 1000) <= WALL_COLLISION_THRESHOLD ) {
    		debug("isColliding():return true;");
    		return true;
    	}
    	
    	return false;
    }

    
    /**
     * Our custom listener for WallCollision messages.      
     */
    IWorldEventListener<WallCollision> myCollisionsListener = new IWorldEventListener<WallCollision>() {
		@Override
		public void notify(WallCollision event) {
			lastCollidingEvent = event;						
		}		
		
	};
    
    
    /*========================================================================*/

    /** Agent's bot. */
    protected UT2004Bot bot;
    /** Loque memory. */
    protected AgentInfo memory;
    /** Agent's body. */
    protected AdvancedLocomotion body;
    /** Agent's log. */
    protected Logger log;


    /*========================================================================*/

    /**
     * Constructor.
     * 
     * @param bot Agent's bot.
     * @param memory Loque memory.
     */
    public KefikRunner(UT2004Bot bot, AgentInfo agentInfo, AdvancedLocomotion locomotion, Logger log) {
        // setup reference to agent
    	NullCheck.check(bot, "bot");
    	this.bot = bot;
        NullCheck.check(agentInfo, "agentInfo");
        this.memory = agentInfo;
        NullCheck.check(locomotion, "locomotion");
        this.body = locomotion;        
        
        //registering listener for wall collisions
        bot.getWorldView().addEventListener(WallCollision.class, myCollisionsListener);
        
        this.log = log;
        if (this.log == null) {
        	log = bot.getLogger().getCategory(this.getClass().getSimpleName());
        }
    }
    
}
