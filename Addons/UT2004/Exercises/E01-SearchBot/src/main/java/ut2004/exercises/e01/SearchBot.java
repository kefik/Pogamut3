package ut2004.exercises.e01;

import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.UT2004Skins;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GlobalChat;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerDamaged;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import ut2004.exercises.e01.checker.CheckerBot;

/**
 * EXERCISE 01
 * -----------
 * 
 * Implement a SearchBot that will be able to find another bot {@link CheckerBot} within the environment and chat with it.
 * 
 * Step:
 * 1. find the bot and approach him (get near him ... distance < 200)
 * 2. greet him by saying "Hello!"
 * 3. upon receiving reply "Hello, my friend!"
 * 4. answer "I'm not your friend."
 * 5. and fire a bit at CheckerBot (do not kill him, just a few bullets)
 * 6. then CheckerBot should tell you "COOL!"
 * 7. then CheckerBot respawns itself
 * 8. repeat 1-6 until CheckerBot replies with "EXERCISE FINISHED"
 * 
 * If you break the protocol, {@link CheckerBot} will respawn at another location saying "RESET".
 * 
 * @author Jakub Gemrot aka Jimmy aka Kefik
 */
@AgentScoped
public class SearchBot extends UT2004BotModuleController {
    
    private int logicIterationNumber;

	/**
     * Here we can modify initializing command for our bot, e.g., sets its name or skin.
     *
     * @return instance of {@link Initialize}
     */
    @Override
    public Initialize getInitializeCommand() {  
    	return new Initialize().setName("SearchBot").setSkin(UT2004Skins.getSkin());
    }

    /**
     * Bot is ready to be spawned into the game; configure last minute stuff in here
     *
     * @param gameInfo information about the game type
     * @param config information about configuration
     * @param init information about configuration
     */
    @Override
    public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init) {
    	// ignore any Yylex whining...
    	bot.getLogger().getCategory("Yylex").setLevel(Level.OFF);
    }
    
    /**
     * This method is called only once, right before actual logic() method is called for the first time.
     */
    @Override
    public void beforeFirstLogic() {
    }
    
    /**
     * Say something through the global channel + log it into the console...    
     * @param msg
     */
    private void sayGlobal(String msg) {
    	// Simple way to send msg into the UT2004 chat
    	body.getCommunication().sendGlobalTextMessage(msg);
    	// And user log as well
    	log.info(msg);
    }
    
    @EventListener(eventClass=GlobalChat.class)
    public void chatReceived(GlobalChat msg) {
    	if (msg.getText().toLowerCase().equals("reset")) {
    		// CheckerBot reset itself...
    	}
    }
    
    /**
     * Some other player/bot has taken damage.
     * @param event
     */
    @EventListener(eventClass=PlayerDamaged.class)
    public void playerDamaged(PlayerDamaged event) {
    }

    /**
     * Main method called 4 times / second. Do your action-selection here.
     */
    @Override
    public void logic() throws PogamutException {
    	log.info("---LOGIC: " + (++logicIterationNumber) + "---");
  
    }

    /**
     * This method is called when the bot is started either from IDE or from command line.
     *
     * @param args
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String args[]) throws PogamutException {
        new UT2004BotRunner(      // class that wrapps logic for bots executions, suitable to run single bot in single JVM
                SearchBot.class,  // which UT2004BotController it should instantiate
                "SearchBot"       // what name the runner should be using
        ).setMain(true)           // tells runner that is is executed inside MAIN method, thus it may block the thread and watch whether agent/s are correctly executed
         .startAgents(1);         // tells the runner to start 1 agent
    }
}
