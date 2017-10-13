package cz.cuni.amis.pogamut.ut2004.bot.command;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Jump;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Rotate;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SetWalk;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Stop;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.TurnTo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 * Class providing Pogamut2 UT2004 simple locomotion commands for the bot -
 * basic movement and turning.
 * 
 * @author Michal 'Knight' Bida
 */
public class SimpleLocomotion extends BotCommands {

	/**
	 * Bot will move to supplied location. (issues GB MOVE command)
	 * 
	 * @param locatedTarget
	 *            Object with location. We will move to this location.
	 * 
	 * @see stopMovement()
	 */
	public void moveTo(ILocated location) {
		Move move = new Move().setFirstLocation(location.getLocation());
		agent.getAct().act(move);
	}

	/**
	 * Makes the bot to stop all movement or turning. (Does not stop shooting.)
	 * 
	 * (issues GB STOP command)
	 * 
	 * @see moveTo(ILocated)
	 */
	public void stopMovement() {
		agent.getAct().act(new Stop());
	}

	/**
	 * Bot will turn to face supported location (issues GB TURNTO command)
	 * 
	 * @param location
	 *            Location we will face.
	 * 
	 * @see turnHorizontal(int)
	 * @see turnVertical(int)
	 */
	public void turnTo(ILocated location) {
		TurnTo turnTo = new TurnTo();
		turnTo.setLocation(location.getLocation());
		agent.getAct().act(turnTo);
	}
	
	/**
	 * Bot will turn to face 'player' (isseus GB TURNTO command), the bot will face the player even if it or the player moves.
	 * 
	 * @param player
	 * 			Player we will face.
	 * 
	 */
	public void turnTo(Player player) {
    	TurnTo turnTo = new TurnTo();
		turnTo.setTarget(player.getId());
		agent.getAct().act(turnTo);
    }
    
	/**
	 * Bot will turn to face 'item' (isseus GB TURNTO command), the bot will face the item even if it or the item moves.
	 * 
	 * @param item
	 * 			Item we will face.
	 */
    public void turnTo(Item item) {
    	TurnTo turnTo = new TurnTo();
		turnTo.setTarget(item.getId());
		agent.getAct().act(turnTo);
    }

	/**
	 * Rotates the bot by the supported amount (in degrees) in left/right
	 * direction (issues GB ROTATE command)
	 * 
	 * @param amount
	 *            Amount of rotation in degrees.
	 * 
	 * @see turnVertical(int)
	 * @see turnTo(ILocated)
	 */
	public void turnHorizontal(int amount) {
		Rotate rotate = new Rotate();
		// full rotation in UT units is 65535
		rotate.setAmount(amount * 65535 / 360);
		agent.getAct().act(rotate);
	}

	/**
	 * Rotates the bot by the supported amount (in degrees) in up/down direction.	
	 * Note: This moves bot's head which is unable to rotate freely in 360 degrees.
	 * There is a certain angle limit (close to look perpendicular to the floor plane).
	 * Going beyond this limit results in bot turning horizontaly in place and looking
	 * to desired position under complementary angle.
	 * (issues GB ROTATE command)
	 * 
	 * @param amount
	 *            Amount of rotation in degrees.
	 * 
	 * @see turnHorizontal(int)
	 * @see turnTo(ILocated)
	 */
	public void turnVertical(int amount) {
		Rotate rotate = new Rotate();
		// full rotation in UT units is 65535
		rotate.setAmount(amount * 65535 / 360);
		rotate.setAxis("Vertical");
		agent.getAct().act(rotate);
	}

	/**
	 * Bot will make simple jump (Issues GB JUMP command)
	 */
	public void jump() {
		agent.getAct().act(new Jump());
	}

	/**
	 * Sets the walking speed for the bot movement commands. (issues GB SETWALK
	 * command)
	 * 
	 * @see setRun()
	 */
	public void setWalk() {
		agent.getAct().act(new SetWalk().setWalk(true));
	}

	/**
	 * Sets the running speed for the bot movement commands. (issues GB SETWALK
	 * command)
	 * 
	 * @see setWalk()
	 */
	public void setRun() {
		agent.getAct().act(new SetWalk().setWalk(false));
	}

	/**
	 * Constructor. Setups the command module based on given agent and logger.
	 * 
	 * @param agent
	 *            AbstractUT2004Bot we will send commands for
	 * @param log
	 *            Logger to be used for logging runtime/debug info.
	 */
	public SimpleLocomotion(UT2004Bot agent, Logger log) {
		super(agent, log);
	}
}
