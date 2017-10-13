package cz.cuni.amis.pogamut.ut2004.bot;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.PasswordReply;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MapListEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapListObtained;

public interface IUT2004BotController<BOT extends UT2004Bot> {
	
	/**
	 * Returns user log of the controller.
	 * @return
	 */
	public Logger getLog();
	
	/**
	 * Returns underlying controlled {@link UT2004Bot} class that is managing life-cycle of the bot.
	 * @return
	 */
	public BOT getBot();
	
	/**
	 * Returns mean for sending commands through bot's GB2004 communication channel. 
	 * @return
	 */
	public IAct getAct();
	
	/**
	 * Returns world view of the controlled that providing access to all objects/events the bot has.
	 * @return
	 */
	public IVisionWorldView getWorldView();
	
	/**
	 * Called during the construction of the {@link UT2004Bot} before the GameBots2004 greets the bot even before
	 * {@link IUT2004BotController#prepareBot(UT2004Bot)} method.
	 * <p><p>
	 * <b>NOTE:</b> This is Pogamut's developers reserved method - do not override it and if you do, always use 'super' 
	 * to call parent's initializeController.
     */
	public void initializeController(BOT bot);
	
	/**
	 * Called during the construction of the {@link UT2004Bot} before the GameBots2004 greets the bot.
     */
    public void prepareBot(BOT bot);
	
	
	/**
	 * Returns password that should be used to access the GameBots2004 server.
	 * <p><p>
	 * Called only if the bot is challenged by the password request.
	 * 
	 * @return
	 */
	public PasswordReply getPassword();
	
	/**
	 * Called whenever {@link MapPointListObtained} event is broadcast, means that the GB2004 finished sending information about the map (list of {@link NavPoint}s and {@link Item}s).  
	 */
	public void mapInfoObtained();
	
    /**
     * This method is called after handshake with GameBots2004 is over and the GameBots2004
     * is awaiting the INIT command (Initialize class). Here you have to construct the
     * Initialize message where you may specify many starting parameters of the bot including:
     * <ul>
     * <li>bot's name</li>
     * <li>bot skill level (aim precision)</li>
     * <li>bot skin</li>
     * <li>raycasting</li>
     * <li>etc. - for complete list see Initialize command</li>
     * </ul>
     * This message is then saved to private field initalizeCommand and is accessible
     * via getInitializeCommand() method if required to probe the starting parameters (even though
     * they can be changed during the bot's lifetime!).
     */
    public Initialize getInitializeCommand();

    /**
     * This method is called whenever {@link InitedMessage} is received. Various agent modules are usable since this
     * method is called.
     * 
     * @param gameInfo
     * @param config
     * @param init
     * @param self
     */
    public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init);
    
    /**
     * This method is called only once whenever first batch of information what the bot can see is received.
     * <i><i>
     * It is sort of "first-logic-method" where you may issue commands for the first time and handle everything
     * else in bot's logic then. It eliminates the need to have 'boolean firstLogic' field inside your bot.
     * <p><p>
     * Note that this method has advantage over the {@link IUT2004BotController#botInitialized(GameInfo, ConfigChange, InitedMessage)}
     * that you already have {@link Self} object.
     * 
     * @param gameInfo
     * @param config
     * @param init
     * @param self
     */
    public void botFirstSpawn(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init, Self self);
    
    /**
	 * Called after {@link IUT2004BotController#botFirstSpawn(GameInfo, ConfigChange, InitedMessage, Self)} as a hook for Pogamut's core developers
	 * to finalize initialization of various modules.
	 * <p><p>
	 * <b>NOTE:</b> This is Pogamut's developers reserved method - do not override it and if you do, always use 'super' 
	 * to call parent's finishControllerInitialization.
     */
	public void finishControllerInitialization();
    
    /**
     * Called whenever the bot gets killed inside the game.
     * 
     * @param event
     */
    public void botKilled(BotKilled event);
    
    /**
     * Called whenever the bot is shutdown (has finished) or killed (not in the game but as the instance).
     * <p><p>
     * Use the method to save your work / data collected during the run of the agent.
     * <p><p>
     * Pogamut's guarantee that this method is called even if exception happens inside your previous code.
     */
    public void botShutdown();

}
