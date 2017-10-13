package cz.cuni.amis.pogamut.ut2004.bot.command;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Shoot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 * Class providing Pogamut2 UT2004 simple shooting commands for the bot
 * 
 * @author Michal 'Knight' Bida
 */
public class SimpleShooting extends BotCommands {

	/**
	 * Bot will start shooting his current weapon (Issues GB SHOOT command)
	 * 
	 * @see stopShoot()
	 * @see shoot(UnrealId)
	 * @see shoot(ILocated)
	 */
	public void shoot() {
		agent.getAct().act(new Shoot());
	}

	/**
	 * Bot will start shooting his current weapon to a specified location (Issues GB SHOOT command).
	 * 
	 * @param location
	 * 
	 * @see stopShoot()
	 * @see shoot(UnrealId);
	 */
	public void shoot(ILocated location) {
		if (location instanceof Player) {
			shoot((Player)location);
		} else {
			agent.getAct().act(new Shoot().setLocation(location.getLocation()));
		}
	}
	
	/**
	 * Bot will start shooting with his current weapon at the target provided.
	 * (Issues GB SHOOT command) Note that the bot will track the target while
	 * shooting. If not interrupted by other command that will change bot target
	 * or that will change bot focus too much.
	 * 
	 * @param target
	 *            Target (that should be ILocated) the bot will shoot at. Bot
	 *            will track the target, but see note above.
	 * 
	 * @see stopShoot()
	 * @see shoot()
	 */
	public void shoot(UnrealId target) {
		agent.getAct().act(new Shoot().setTarget(target));
	}
	
	/**
	 * Bot will start shooting with his current weapon at the target provided.
	 * (Issues GB SHOOT command) Note that the bot will track the target while
	 * shooting. If not interrupted by other command that will change bot target
	 * or that will change bot focus too much.
	 * 
	 * @param target
	 *            Player the bot wants to shoot at.
	 * 
	 * @see stopShoot()
	 * @see shoot()
	 */
	public void shoot(Player target) {
		agent.getAct().act(new Shoot().setTarget(target.getId()));
	}

	/**
	 * Bot will stop shooting his current weapon (Issues GB STOPSHOOT command)
	 * 
	 * Use {@link SimpleShooting#stopShooting()} instead!
	 * 
	 * @see shoot()
	 * @see shoot(UnrealId)
	 */
	@Deprecated
	public void stopShoot() {
		agent.getAct().act(new StopShooting());
	}
	
	/**
	 * Bot will stop shooting his current weapon (Issues GB STOPSHOOT command)
	 * 
	 * @see shoot()
	 * @see shoot(UnrealId)
	 */
	public void stopShooting() {
		agent.getAct().act(new StopShooting());
	}

	/**
	 * Constructor. Setups the command module based on given agent and logger.
	 * 
	 * @param agent
	 *            AbstractUT2004Bot we will send commands for
	 * @param log
	 *            Logger to be used for logging runtime/debug info.
	 */
	public SimpleShooting(UT2004Bot agent, Logger log) {
		super(agent, log);
	}

}