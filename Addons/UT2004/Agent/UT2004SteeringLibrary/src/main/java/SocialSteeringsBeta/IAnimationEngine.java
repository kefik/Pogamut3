package SocialSteeringsBeta;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;

/**
 *
 * @author Petr
 */
public interface IAnimationEngine {
    boolean canBeInterupt(UT2004Bot bot);
    /**
     @param movementSpeed each type of movement has its own speed
     * 0 is for stand
     * 1 is for slow move
     * 2 is for walk
     * 3 is for run
     @return name of choosen animation
     */
    String playAnimation(int movementSpeed, UT2004Bot bot, boolean loop, int direction);
    
    /**
     force to play animation described in @param animationID
     */
    void playAnimation(String animationID, UT2004Bot bot, boolean loop);
    void planNextAnimation(UT2004Bot agent, String precedingName, char actor);
}
