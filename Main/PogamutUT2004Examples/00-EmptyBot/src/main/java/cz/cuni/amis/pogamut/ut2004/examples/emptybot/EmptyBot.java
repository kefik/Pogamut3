package cz.cuni.amis.pogamut.ut2004.examples.emptybot;

import java.util.Collection;
import java.util.logging.Level;

import cz.cuni.amis.introspection.java.JProp;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavigationGraphBuilder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.NavigationState;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004MapTweaks.IMapTweak;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SendMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectile;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.Cooldown;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;

/**
 * Pogamut "Hello world!" example showing few extra things such as introspection and various bot-initializing methods.
 * <p>
 * We advise you to use simple: DM-TrainingDay map with this example.
 * 
 * <p><p>
 * First, try to run the bot and kill it. If you are a NetBeans user, the bot can be either run by right-clicking the project and choosing Run
 * or by directly running this file. If you are an Eclipse user, run directly this file.
 * 
 * <p><p>
 * Then uncomment the line inside {@link EmptyBot#botKilled(BotKilled)} and run the bot again kill it to see the difference.
 * 
 * <p><p>
 * Then uncomment all lines within {@link EmptyBot#logic()}, run the bot and approach it ... then try to escape from it.
 * 
 * <p><p>
 * Hard to escape? Try the same on map DM-1on1-Albatross
 * 
 * <p><p>
 * If you're using Pogamut NetBeans plugin, try to run the bot, click on its node within Services/UT2004 tab and open properties window (Ctrl+Shift+7).
 * You will see that all fields flagged with {@link JProp} are exported to this window. You can also change their values at runtime.
 * 
 * <p><p>
 * Check out the MAIN method at the end of this file, it contains a hint how to run multiple agents of the same kind at once.
 *
 * @author Jakub Gemrot aka Jimmy
 * @author Michal Bida aka Knight
 * @author Rudolf Kadlec aka ik
 */
@AgentScoped
public class EmptyBot extends UT2004BotModuleController {

    //
    // Following properties are exported into "Properties" tab inside NetBeans if you have Pogamut NetBeans plugin installed.
	// Note that these field must be annotated with @JProp annotations.
    //
	
	@JProp
    public String stringProp = "Hello bot example";
    @JProp
    public boolean boolProp = true;
    @JProp
    public int intProp = 2;
    @JProp
    public double doubleProp = 1.0;
    
    //
    // Follwing fields are required only iff code inside {@link EmptyBot#logic()} is uncommented.
    //
    
    private Player lastPlayer;
    private long   lastLogicTime        = -1;
    private long   logicIterationNumber = 0;    

    /**
     * Initialize all necessary variables here, before the bot actually receives 
     * anything from the environment.
     */
    @Override
    public void prepareBot(UT2004Bot bot) {
        // By uncommenting following line, you can make the bot to do the file logging of all its components
        //bot.getLogger().addDefaultFileHandler(new File("EmptyBot.log"));
    }
    
    /**
     * Here we can modify initializing command for our bot, e.g., sets its name or skin.
     *
     * @return instance of {@link Initialize}
     */
    @Override
    public Initialize getInitializeCommand() {  
    	return new Initialize().setName("EmptyBot");
    	// By commenting out the line above and uncommenting line below, you will change the skin of your bot.
    	//return new Initialize().setName("SkinBot").setSkin("Dominator");        
    }

    /**
     * Handshake with GameBots2004 is over - bot has information about the map
     * in its world view. Many agent modules are usable since this method is
     * called.
     *
     * @param gameInfo informaton about the game type
     * @param config information about configuration
     * @param init information about configuration
     */
    @Override
    public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init) {
    	// By uncommenting line below, you will see all messages that goes trough GB2004 parser (GB2004 -> BOT communication)
    	//bot.getLogger().getCategory("Parser").setLevel(Level.ALL);
    }
    
    @Override
    public void mapInfoObtained() {
    	mapTweaks.register("DM-1on1-Albatross", new IMapTweak() {

			@Override
			public void tweak(NavigationGraphBuilder builder) {
				// alter the navgraph here
			}
    		
    	});
    	navMeshModule.setReloadNavMesh(true);
    }

    /**
     * The bot is initilized in the environment - a physical representation of
     * the bot is present in the game.
     *
     * @param gameInfo informaton about the game type
     * @param config information about configuration
     * @param init information about configuration
     * @param self information about the agent
     */
    @Override
    public void botFirstSpawn(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self) {
        // Display a welcome message in the game engine
        // right in the time when the bot appears in the environment, i.e., his body has just been spawned 
        // into the UT2004 for the first time.    	
        body.getCommunication().sendGlobalTextMessage("Hello world! I am alive!");

        // alternatively, you may use getAct() method for issuing arbitrary {@link CommandMessage} for the bot's body
        // inside UT2004
        act.act(new SendMessage().setGlobal(true).setText("And I can speak! Hurray!"));
        
        navigation.addStrongNavigationListener(new FlagListener<NavigationState>() {

			@Override
			public void flagChanged(NavigationState changedValue) {
				switch(changedValue) {
				case PATH_COMPUTATION_FAILED:
					break;
				case STUCK:
					break;
				}
			}
        	
        });

    }
    
    /**
     * This method is called only once, right before actual logic() method is
     * called for the first time.
     * 
     * Similar to {@link EmptyBot#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self)}.
     */
    @Override
    public void beforeFirstLogic() {
    }
    
    private void sayGlobal(String msg) {
    	// Simple way to send msg into the UT2004 chat
    	body.getCommunication().sendGlobalTextMessage(msg);
    	// And user log as well
    	log.info(msg);
    }
    
    /**
     * Called each time the bot dies. Good for reseting all bot's state
     * dependent variables.
     *
     * @param event
     */
    @Override
    public void botKilled(BotKilled event) {
        // Uncomment this line to have the bot comment on its death.
        //sayGlobal("I was KILLED!");
    }
    
    @EventListener(eventClass=BotDamaged.class)
    public void botDamaged(BotDamaged event) {
    	// Uncomment this line to gain information about damage the bot receives
    	//sayGlobal("GOT DAMAGE: " + event.getDamage() + ", HEALTH: " + info.getHealth());
    	// Notice that "HEALTH" does not fully match the damage received, because "HEALTH" is updated periodically
    	// but BotDamaged message comes as event, therefore the "HEALTH" number lags behind a bit (250ms at max)
    }

    int num;
    
    Player currentTarget;
    
    long shootTimeMillis;
    
    Cooldown shootLightningGun = new Cooldown(1500);
    
    /**
     * Main method that controls the bot - makes decisions what to do next. It
     * is called iteratively by Pogamut engine every time a synchronous batch
     * from the environment is received. This is usually 4 times per second - it
     * is affected by visionTime variable, that can be adjusted in ini file
     * inside UT2004/System/GameBots2004.ini
     *
     * @throws cz.cuni.amis.pogamut.base.exceptions.PogamutException
     */
    @Override
    public void logic() throws PogamutException {
    	log.info("---LOGIC: " + (++logicIterationNumber) + "---");
    	

    }

    /**
     * This method is called when the bot is started either from IDE or from
     * command line.
     *
     * @param args
     */
    public static void main(String args[]) throws PogamutException {
        new UT2004BotRunner(     // class that wrapps logic for bots executions, suitable to run single bot in single JVM
                EmptyBot.class,  // which UT2004BotController it should instantiate
                "EmptyBot"       // what name the runner should be using
        ).setMain(true)          // tells runner that is is executed inside MAIN method, thus it may block the thread and watch whether agent/s are correctly executed
         .startAgents(1);        // tells the runner to start 1 agent

        // It is easy to start multiple bots of the same class, comment runner above and uncomment following
        // new UT2004BotRunner(EmptyBot.class, "EmptyBot").setMain(true).startAgents(3); // tells the runner to start 3 agents at once
    }
}
