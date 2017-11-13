package cz.cuni.amis.pogamut.ut2004.examples.navmeshdebugbot;

import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.INavMeshDraw;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.NewNavMeshDraw;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GlobalChat;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Various routines for debugging NAV MESH...
 *
 * @author Jakub Gemrot aka Jimmy
 */
@AgentScoped
public class NavMeshDebugBot extends UT2004BotModuleController {

    private long   lastLogicTime        = -1;
    private long   logicIterationNumber = 0;
    
    private INavMeshDraw navMeshDraw;
    
    /**
     * Flip by chat message 'next'.
     */
    private boolean restartNavigation = true;
    
    /**
     * Flip by chat message 'drawNavMesh'.
     */
    private boolean drawNavMesh = true;

    @Override
    public void initializeController(UT2004Bot bot) {
    	super.initializeController(bot);
    	navMeshDraw = new NewNavMeshDraw(navMeshModule.getNavMesh(), log, serverProvider);
    }
    
    @Override
    public Initialize getInitializeCommand() {  
    	return new Initialize().setName("NavMeshDebugBot");
    }
    
    private void sayGlobal(String msg) {
    	// Simple way to send msg into the UT2004 chat
    	body.getCommunication().sendGlobalTextMessage(msg);
    	// And user log as well
    	log.info(msg);
    }
    
    @EventListener(eventClass=GlobalChat.class)
    private void teamChat(GlobalChat msg) {
    	if (msg.getText().toLowerCase().equals("next")) {
    		restartNavigation = true;
    	} else 
		if (msg.getText().toLowerCase().startsWith("drawnavmesh")) {
			String[] parts = msg.getText().split(" ");
			navMeshDraw.draw(true, parts.length > 1 ? Boolean.parseBoolean(parts[1]) : false);
    	}
    }
   
    @Override
    public void logic() throws PogamutException {
    	log.info("---LOGIC: " + (++logicIterationNumber) + "---");
    
    	if (!navMeshModule.isInitialized()) {
    		sayGlobal("NAV MESH MODULE NOT INITIALIZED?");
    		sayGlobal("Missing navmesh for: " + game.getMapName() + " ?");
    		return;
    	}
    	
    	if (navigation.isNavigating()) return;
    	
    	if (!restartNavigation) return;
    	restartNavigation = false;
    	
    	sayGlobal("NEW NAVIGATION REQUEST");
    	
    	NavPoint navPoint = navPoints.getRandomNavPoint();
    	navigation.navigate(navPoint.getLocation());
    	
    	if (navigation.isNavigating()) {
    		sayGlobal("NAVIGATING TO: " + navPoint.getId().getStringId());
    		draw.clearAll();
    		draw.drawPolyLine(navigation.getCurrentPathDirect());
    	} else {
    		sayGlobal("NO PATH TO: " + navPoint.getId().getStringId());
    	}

    }

    /**
     * This method is called when the bot is started either from IDE or from
     * command line.
     *
     * @param args
     */
    public static void main(String args[]) throws PogamutException {
        new UT2004BotRunner(     // class that wrapps logic for bots executions, suitable to run single bot in single JVM
                NavMeshDebugBot.class,  // which UT2004BotController it should instantiate
                "EmptyBot"       // what name the runner should be using
        ).setMain(true)          // tells runner that is is executed inside MAIN method, thus it may block the thread and watch whether agent/s are correctly executed
         .startAgents(1);        // tells the runner to start 1 agent

        // It is easy to start multiple bots of the same class, comment runner above and uncomment following
        // new UT2004BotRunner(EmptyBot.class, "EmptyBot").setMain(true).startAgents(3); // tells the runner to start 3 agents at once
    }
}
