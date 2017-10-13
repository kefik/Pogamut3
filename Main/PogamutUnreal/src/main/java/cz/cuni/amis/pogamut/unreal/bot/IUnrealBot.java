package cz.cuni.amis.pogamut.unreal.bot;

import cz.cuni.amis.pogamut.base3d.agent.IAgent3D;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 *
 * @author ik
 */
public interface IUnrealBot extends IAgent3D {


    /**
     * Restarts the bot in the game. Issues RESPAWN command.
     * @throws cz.cuni.amis.pogamut.base.exceptions.PogamutException
     */
    public void respawn() throws PogamutException;


}
