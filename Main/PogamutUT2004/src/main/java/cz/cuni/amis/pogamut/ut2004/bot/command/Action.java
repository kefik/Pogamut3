package cz.cuni.amis.pogamut.ut2004.bot.command;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Combo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Pick;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.PlayAnimation;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Respawn;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Throw;

/**
 * Class providing Pogamut2 UT2004 bot action commands - throwing weapon,
 * issuing combos, item interactions and other commands that didn't fit in other
 * categories.
 * 
 * @author Michal 'Knight' Bida
 */
public class Action extends BotCommands {

	/**
	 * Causes bot to perform desired adrenaline combo (only if it has enough
	 * adrenaline usually >= 100). Combo cannot be turned off when triggered.
	 * Combo will consume bot adrenaline slowly, when it reaches 0 combo stops.
	 * Combo can modify bots abilities or appearance. Combos can be
	 * xGame.ComboBerserk (bigger damage), xGame.ComboDefensive (every few
	 * seconds adds health), xGame.ComboInvis (bot is invisible and is very hard
	 * to spot) or xGame.ComboSpeed (bots speed is increased).
	 * 
	 * (issues GB COMBO command)
	 * 
	 * @param desiredCombo
	 *            Holds the class name of the desired adrenaline combo (can be
	 *            xGame.ComboBerserk, xGame.ComboDefensive, xGame.ComboInvis or
	 *            xGame.ComboSpeed).
	 */
	public void startCombo(String desiredCombo) {
		agent.getAct().act(new Combo(desiredCombo));
	}

	/**
	 * Throws out bots current weapon (just if he is allowed to throw this kind
	 * of weapon out - some weapons cannot be thrown e.g. ShieldGun) and will
	 * change to best weapon available.
	 * 
	 * (issues GB THROW command)
	 */
	public void throwWeapon() {
		agent.getAct().act(new Throw());
	}

	/**
	 * This function will kill the bot and force him to respawn. He will respawn
	 * on some randomly chosen starting point in the map.
	 * 
	 * (issues GB RESPAWN command)
	 * 
	 * @see respawn(ILocated)
	 * @see respawn(ILocated,Rotation)
	 */
	public void respawn() {
		agent.getAct().act(new Respawn());
	}

	/**
	 * This function will kill the bot and force him to respawn. He will respawn
	 * at the location supplied (if he is allowed to respawn there). Be carefull
	 * when supporting some object in the game that is ILocated, may kill other
	 * player if respawned at his positions.
	 * 
	 * (issues GB RESPAWN command)
	 * 
	 * @param location
	 *            Bot will be respawned at this location (if he can). Be
	 *            carefull may kill players this way. Usefull when wanted to
	 *            specify NavPoint you want the bot to have respawned at.
	 * 
	 * @see respawn()
	 * @see respawn(ILocated,Rotation)
	 * 
	 */
	public void respawn(ILocated location) {
		Respawn respawn = new Respawn();

		respawn.setStartLocation(location.getLocation());
		agent.getAct().act(respawn);
	}

	/**
	 * This function will kill the bot and force him to respawn. He will respawn
	 * at the location specified (if he is allowed to respawn there). Be
	 * carefull when supporting some object in the game that is ILocated, may
	 * kill other player if respawned at his positions. He will be respawned
	 * with supplied rotation set.
	 * 
	 * (issues GB RESPAWN command)
	 * 
	 * @param location
	 *            Location where the bot will be respawned.
	 * @param rotation
	 *            Initial rotation of the bot.
	 * 
	 * @see respawn()
	 * @see respawn(ILocated)
	 * 
	 */
	public void respawn(ILocated location, Rotation rotation) {
		Respawn respawn = new Respawn();

		respawn.setStartLocation(location.getLocation());
		respawn.setStartRotation(rotation);
		agent.getAct().act(respawn);
	}

	/**
	 * If the items are set to be picked up manualy, this command can be used to
	 * pick up the items. Note that the bot must be touching the item, when this
	 * command is issued to picked it up.
	 * 
	 * To disable auto pickup (so the bots will pick items manually by this
	 * command) set in GameBots ini file found in UT_HOME/System directory
	 * variable bDisableAutoPickup to true (the variable should be in .RemoteBot
	 * section).
	 * 
	 * @param id
	 *            UnrealId of the item we want to pick up.
	 */
	public void pick(UnrealId id) {
		agent.getAct().act(new Pick().setId(id));
	}

	/**
	 * This command can be used to play some available bot animation. To see
	 * which animations are available, go to UnrealEd Actor Class browser,
	 * select Animations tab and open the .ukx file containing the animations of
	 * the model of the bot you are using in UT.
	 * 
	 * Note that movement animations will always override animations issued by
	 * this command. The bot must stand still for the issued animation to
	 * finish.
	 * 
	 * @param animName
	 *            name of the animation we want to run
	 */
	public void playAnimation(String animName) {
		agent.getAct().act(new PlayAnimation().setName(animName));
	}

	/**
	 * Constructor. Setups the command module based on given agent and logger.
	 * 
	 * @param agent
	 *            AbstractUT2004Bot we will send commands for
	 * @param log
	 *            Logger to be used for logging runtime/debug info.
	 */
	public Action(UT2004Bot agent, Logger log) {
		super(agent, log);
	}
}
