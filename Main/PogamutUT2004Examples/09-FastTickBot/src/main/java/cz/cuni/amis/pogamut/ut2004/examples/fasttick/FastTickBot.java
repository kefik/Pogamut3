package cz.cuni.amis.pogamut.ut2004.examples.fasttick;

import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavigationGraphBuilder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.NavigationState;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004MapTweaks.IMapTweak;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SendMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.Cooldown;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;

/**
 * This examples show you how to speed your vision updates.
 *
 * @author Jakub Gemrot aka Jimmy
 * @author Michal Bida aka Knight
 * @author Rudolf Kadlec aka ik
 */
@AgentScoped
public class FastTickBot extends UT2004BotModuleController {

    private long lastLogicTime        = -1;
    private long logicIterationNumber = 0;
    
    private long lastSelfTime = -1;
    private long selfIterationNumber = 0;

    @Override
    public Initialize getInitializeCommand() {  
    	return new Initialize().setName("FastTickBot");    	       
    }

    @Override
    public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init) {
    	act.act(new Configuration().setVisionTime(0.1));
    }
    
    @Override
    public void beforeFirstLogic() {
    }
    
    @ObjectClassEventListener(eventClass = WorldObjectUpdatedEvent.class, objectClass = Self.class)
    public void selfUpdate(WorldObjectUpdatedEvent<Self> event) {
    	log.info("---SELF UPDATE: " + (++selfIterationNumber) + "---");
    	if (lastSelfTime > 0) {
    		log.info("SELF DELTA: " + (System.currentTimeMillis() - lastSelfTime));
    	}
    	lastSelfTime = System.currentTimeMillis();
    }
       
    @Override
    public void logic() throws PogamutException {
    	log.info("---LOGIC: " + (++logicIterationNumber) + "---");
    
    	if (lastLogicTime > 0) {
    		log.info("LOGIC DELTA: " + (System.currentTimeMillis() - lastLogicTime));
    	}	
    	lastLogicTime = System.currentTimeMillis();
    	
    	move.jump(); // we need to be jumping in order to simulate changes in "Self"
    }

    /**
     * This method is called when the bot is started either from IDE or from
     * command line.
     *
     * @param args
     */
    public static void main(String args[]) throws PogamutException {
        new UT2004BotRunner(     // class that wrapps logic for bots executions, suitable to run single bot in single JVM
                FastTickBot.class,  // which UT2004BotController it should instantiate
                "FastTickBot"       // what name the runner should be using
        ).setMain(true)          // tells runner that is is executed inside MAIN method, thus it may block the thread and watch whether agent/s are correctly executed
         .startAgents(1);        // tells the runner to start 1 agent

        // It is easy to start multiple bots of the same class, comment runner above and uncomment following
        // new UT2004BotRunner(EmptyBot.class, "EmptyBot").setMain(true).startAgents(3); // tells the runner to start 3 agents at once
    }
}
