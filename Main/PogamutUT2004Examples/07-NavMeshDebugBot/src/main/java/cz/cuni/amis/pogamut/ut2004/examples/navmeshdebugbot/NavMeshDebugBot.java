package cz.cuni.amis.pogamut.ut2004.examples.navmeshdebugbot;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

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
import cz.cuni.amis.utils.IFilter;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Various routines for debugging NAV MESH...
 * 
 * Commands: 
 * -- next ... to start new navigation request
 * -- auto ... to start continual navigation
 * -- autoclear ... to clear everything on new path; drawing navmesh stops this
 * -- drawnavmesh ... draws navmesh
 * -- reset ... reset list of visited navpoints
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
     * Whether to run "auto-navigation", i.e., trying to run around all navpoints.
     */
    private boolean auto = true;
    
    /**
     * Auto wipes drawn debug stuff at the beginning of the next navigation request.
     */
    private boolean autoclear = true;
    
    /**
     * Flip by chat message 'drawNavMesh'.
     */
    private boolean drawNavMesh = true;
    
    /**
     * Navpoint we are running to.
     */
    private NavPoint targetNavpoint;
    
    /**
     * Set of visited navpoints; such a navpoints are not chosen to navigate to...
     */
    private Set<NavPoint> visited = new HashSet<NavPoint>();

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
    		sayGlobal("Requesting new navigation target...");
    	} else 
		if (msg.getText().toLowerCase().startsWith("drawnavmesh")) {
			String[] parts = msg.getText().split(" ");
			navMeshDraw.draw(true, parts.length > 1 ? Boolean.parseBoolean(parts[1]) : false);
			sayGlobal("NAVMESH DRAWN");
			if (autoclear) {
				autoclear = false;
				sayGlobal("AUTO-CLEAR OFF, say autoclear to re-enable");
			}
    	} else
    	if (msg.getText().toLowerCase().startsWith("autoclear")) {
    		autoclear = !autoclear;
			sayGlobal("Flipping AUTO-CLEAR, new value is " + autoclear);
    	} else
    	if (msg.getText().toLowerCase().startsWith("auto")) {
    		auto = !auto;
			sayGlobal("Flipping AUTO navigation, new value is " + auto);
    	} else
    	if (msg.getText().toLowerCase().startsWith("reset")) {
    		visited.clear();
    		sayGlobal("VISITED NAVPOINTS CLEARED");
    	}
    
    }
    
    @Override
    public void beforeFirstLogic() {
    	sayGlobal("AUTO NAVIGATION: " + auto);
    	sayGlobal("AUTO-CLEAR: " + autoclear);
    	sayGlobal("LET'S GO!");
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
    	else {
    		if (targetNavpoint != null) {
    			if (info.atLocation(targetNavpoint)) {
    				sayGlobal("ARRIVED TO TARGET NAVPOINT");
    			} else {
    				sayGlobal("NAVIGATION FAILED");
    			}
    			targetNavpoint = null;
    		}
    	}
    	
    	if (!auto && !restartNavigation) return;
    	
    	if (restartNavigation) {
    		restartNavigation = false;    		
    	}
    	
    	sayGlobal("New navigation request...");
    	
    	if (navPoints.getNavPoints().size() == visited.size()) {
    		sayGlobal("Visited all navpoints - restarting...");
    		visited.clear();
    	}
    	
    	targetNavpoint = MyCollections.getRandom(MyCollections.getFiltered(navPoints.getNavPoints().values(), new IFilter<NavPoint>() {
			@Override
			public boolean isAccepted(NavPoint object) {
				return !visited.contains(object);
			}    		
    	}));
    	visited.add(targetNavpoint);
    	
    	navigation.navigate(targetNavpoint.getLocation());
    	
    	if (navigation.isNavigating()) {
    		sayGlobal("NEW target: " + targetNavpoint.getId().getStringId());
    		if (autoclear) {
    			draw.clearAll();
    		}
    		draw.drawPolyLine( Color.RED, navigation.getCurrentPathDirect());
    		draw.drawCube( Color.RED, targetNavpoint, 20);
    	} else {
    		sayGlobal("NO PATH TO: " + targetNavpoint.getId().getStringId());
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
