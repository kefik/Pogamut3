package ut2004.exercises.e01.checker;

import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.UT2004Skins;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GlobalChat;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Spawn;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import ut2004.exercises.e01.SearchBot;

/**
 * CHECK BOT FOR THE EXERCISE 01
 * -----------------------------
 * Start the bot and leave it be within the environment;
 * 
 * Follows the protocol as specified within {@link SearchBot}.
 * 
 * @author Jakub Gemrot aka Jimmy aka Kefik
 */
@AgentScoped
public class CheckerBot extends UT2004BotModuleController {
    
    private int logicIterationNumber;

	/**
     * Here we can modify initializing command for our bot, e.g., sets its name or skin.
     *
     * @return instance of {@link Initialize}
     */
    @Override
    public Initialize getInitializeCommand() {  
    	return new Initialize().setName("CheckerBot").setSkin(UT2004Skins.getRandomSkin());
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
    	if (msg.getId() == info.getId()) return;
    	if (fixedPlayer == null) return;    	
    	if (msg.getId().equals(fixedPlayer.getId())) {
    		lastPlayerMessage = msg.getText();
    	}
    }   
    
    private int state = 0;
    
    private Player fixedPlayer = null;
    
    private String lastPlayerMessage = null;
    
    private int lastHealth = 100;
    
    private long lastHit = 0;
    
    private int iteration = 0;
    
    @Override
    public void botKilled(BotKilled event) {
    	state = 0;
    	fixedPlayer = null;
    	lastPlayerMessage = null;
    	lastHealth = 100;
    	lastHit = 0;
    }
    
    @EventListener(eventClass=Spawn.class)
    private void botSpawned(Spawn event) {
    	state = 0;
    	fixedPlayer = null;
    	lastPlayerMessage = null;
    	lastHealth = 100;
    	lastHit = 0;
    }

    /**
     * Main method called 4 times / second. Do your action-selection here.
     */
    @Override
    public void logic() throws PogamutException {
    	if (info.atLocation("PlayerStart0")) {
    		body.getAction().respawn();
    		return;
    	}    	
    	switch (state) {
    	// 1. find the bot and approach him (get near him ... distance < 200)
    	case 0: waitForPlayer(); break;
    	// 2. greet him by saying "Hello!"
    	case 1: waitForHello(); break;
    	// 3. upon receiving reply "Hello, my friend!"
    	case 2: replyHello(); break;
    	// 4. answer "I'm not your friend."
    	case 3: wailNotYourFriend(); break;
    	// 5. and fire a bit at CheckerBot (do not kill him, just a few bullets)
    	case 4: expectWound(); break;
    	// 6. then CheckerBot should tell you "COOL!"
    	case 5: sayCool(); break;
    	// 7. then CheckerBot respawns itself
    	// 8. repeat 1-6 until CheckerBot replies with "EXERCISE FINISHED!"
    	case 6: iterateOrFinish(); break;
    	}
    }

    private void waitForPlayer() {
		if (players.canSeePlayers()) {
			log.info("STATE " + state + ": can see player");
			body.getLocomotion().turnTo(players.getNearestVisiblePlayer());
			if (info.getDistance(players.getNearestVisiblePlayer()) < 200) {
				log.info("STATE " + state + ": player distance < 200");
				log.info("STATE " + state + ": fixing player " + players.getNearestVisiblePlayer());
				fixedPlayer = players.getNearestVisiblePlayer();
				log.info("STATE " + state + ": switching to state 1");
				state = 1;	
				return;
			}
			return;
		}
		
		log.info("STATE " + state + ": searching for player");
		body.getLocomotion().turnHorizontal(30);
	}

	private void waitForHello() {
		if (info.getHealth() < 100) {
			sayGlobal("That hurt my feelings!");
			reset();
			return;
		}
		if (lastPlayerMessage != null) {
			if (lastPlayerMessage.toLowerCase().equals("hello!")) {
				log.info("STATE " + state + ": hello! received");
				log.info("STATE " + state + ": switching to state 2");
				state = 2;
				return;
			} else {
				sayGlobal("Invalid message received! Expected: Hello!");
				reset();
				return;
			}
		}
	}

	private void replyHello() {
		lastPlayerMessage = null;
		sayGlobal("Hello, my friend!");
		log.info("STATE " + state + ": hello! replied");
		log.info("STATE " + state + ": switching to state 3");
		state = 3;
	}

	private void wailNotYourFriend() {
		if (info.getHealth() < 100) {
			sayGlobal("That hurt my feelings!");
			reset();
			return;
		}
		if (lastPlayerMessage != null) {
			if (lastPlayerMessage.toLowerCase().equals("i'm not your friend.")) {
				log.info("STATE " + state + ": not your friend received");
				log.info("STATE " + state + ": switching to state 4");
				lastHit = System.currentTimeMillis();
				state = 4;
				return;
			} else {
				sayGlobal("Invalid message received! Expected: I'm not your friend.");
				reset();
				return;
			}
		}
	}

	private void expectWound() {
		if (info.getHealth() != lastHealth) {
			lastHit = System.currentTimeMillis();
			lastHealth = info.getHealth();
		}
		
		if (System.currentTimeMillis() - lastHit > 1000) {
			if (info.getHealth() >= 100) {
				sayGlobal("You should have slapped me!");
				reset();
				return;
			}
			
			log.info("STATE " + state + ": got hit!");
			log.info("STATE " + state + ": switching to state 5");
			state = 5;
			return;
		}
	}

	private void sayCool() {
		sayGlobal("COOL!");
		log.info("STATE " + state + ": COOL! replied");
		log.info("STATE " + state + ": switching to state 6");
		state = 6;
	}

	private void iterateOrFinish() {
		if (iteration == 2) {
			sayGlobal("EXERCISE FINISHED");
			throw new RuntimeException("CONGRATULATIONS!");
		}		
		++iteration;
		body.getAction().respawn();
	}
	
	private void reset() {
		sayGlobal("RESET");
		body.getAction().respawn();
		iteration = 0;
	}

	/**
     * This method is called when the bot is started either from IDE or from command line.
     *
     * @param args
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String args[]) throws PogamutException {
        new UT2004BotRunner(      // class that wrapps logic for bots executions, suitable to run single bot in single JVM
                CheckerBot.class,  // which UT2004BotController it should instantiate
                "SearchBot"       // what name the runner should be using
        ).setMain(true)           // tells runner that is is executed inside MAIN method, thus it may block the thread and watch whether agent/s are correctly executed
         .startAgents(1);         // tells the runner to start 1 agent
    }
}
