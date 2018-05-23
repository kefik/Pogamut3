package cz.cuni.amis.pogamut.ut2004.examples.navmeshdebugbot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.vecmath.Vector3d;

import cz.cuni.amis.pogamut.base.agent.navigation.impl.PrecomputedPathFuture;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.UT2004Skins;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.INavMeshDraw;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.polygonPathFunnel.FunnelDebug;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GlobalChat;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.IFilter;
import cz.cuni.amis.utils.Tuple2;
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

	private static int INSTANCE = 0;
	
    private long   lastLogicTime        = -1;
    private long   logicIterationNumber =  0;
    
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
     * Whether to be running between inventory spots only; flipped by saying 'items'
     */
    private boolean navigateItems = true;
    
    /**
     * Auto wipes drawn debug stuff at the beginning of the next navigation request.
     */
    private boolean autoclear = false;
    
    /**
     * Flip by chat message 'drawNavMesh'.
     */
    private boolean drawNavMesh = false;
    
    /**
     * Perform syntehetic test from the start ... probing many paths, stress-testing navmesh path-finder...
     */
    private boolean synthTest = false;
    
    /**
     * Navpoint we are running to.
     */
    private NavPoint targetNavpoint;
    
    /**
     * Set of visited navpoints; such a navpoints are not chosen to navigate to...
     */
    private Set<NavPoint> visited = new HashSet<NavPoint>();
    
    /**
     * List of different path colors to use.
     */
    private List<Color> pathColors = new ArrayList<Color>();
    private int lastPathColor = -1;

    @Override
    public void prepareBot(UT2004Bot bot) {
    	super.prepareBot(bot);
    	
    	for (int shade = 0; shade <= 255; shade += 10) {
    		float[] hsb = new float[3];
    		Color.RGBtoHSB(255, shade, shade, hsb);
    		pathColors.add(Color.getHSBColor(hsb[0], hsb[1], hsb[2]));    		
    	}
    	Collections.shuffle(pathColors);
    }
    
    @Override
    public void initializeController(UT2004Bot bot) {
    	super.initializeController(bot);
    	navMeshDraw = navMeshModule.getNavMeshDraw();
    }
    
    @Override
    public Initialize getInitializeCommand() {  
    	return new Initialize().setName("NavMeshDebugBot-" + (INSTANCE++)).setSkin(UT2004Skins.getSkin());
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
		if (msg.getText().toLowerCase().startsWith("items")) {
    		navigateItems = !navigateItems;
			sayGlobal("Flipping ITEMS navigation, new value is " + navigateItems);
    	} else
    	if (msg.getText().toLowerCase().startsWith("reset")) {
    		visited.clear();
    		sayGlobal("VISITED NAVPOINTS CLEARED");
    	} else
    	if (msg.getText().toLowerCase().startsWith("clear")) {
    		draw.clearAll();
    		sayGlobal("ALL CLEARED!");
    	} else 
		if (msg.getText().toLowerCase().startsWith("synthtest")) {
    		draw.clearAll();
    		navigation.stopNavigation();
    		pathFindingTest();
    	}
    }
    
	@Override
    public void beforeFirstLogic() {
    	sayGlobal("AUTO NAVIGATION: " + auto);
    	sayGlobal("NAVIGATE ONLY ITEMS: " + navigateItems);
    	sayGlobal("AUTO-DRAW-CLEAR: " + autoclear);
    	sayGlobal("DRAW-NAVMESH: " + drawNavMesh);
    	if (drawNavMesh) {
    		navMeshDraw.draw(drawNavMesh, true);
    	}
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
    	
    	if (synthTest) {
    		synthTest = false;
    		pathFindingTest();
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
				return !visited.contains(object) && (!navigateItems || object.isInvSpot());
			}    		
    	}));
    	visited.add(targetNavpoint);
    	
    	navigation.navigate(targetNavpoint.getLocation());
    	
    	if (navigation.isNavigating()) {
    		sayGlobal("NEW target: " + targetNavpoint.getId().getStringId());
    		if (autoclear) {
    			draw.clearAll();
    		}
    		Color pathColor = pathColors.get((++lastPathColor) % pathColors.size());
    		draw.drawPolyLine( pathColor, navigation.getCurrentPathDirect());
    		draw.drawCube( pathColor, targetNavpoint, 20);
    	} else {
    		sayGlobal("NO PATH TO: " + targetNavpoint.getId().getStringId());
    	}

    }
    
    private void pathFindingTest() {
		sayGlobal("SYNTH TESTING! Hanging up the bot logic... see logs.");
		
    	log.warning("===========================");
		log.warning("PATH-FINDING STRESS TESTING");
		log.warning("===========================");
		
		log.warning("  +-- map: " + game.getMapName());	
		log.warning("  +-- computing extense...");
		Vector3d mins = new Vector3d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		Vector3d maxs = new Vector3d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
				
		for (NavPoint np : navPoints.getNavPoints().values()) {
			if (np.getLocation().getX() > maxs.x) maxs.x = np.getLocation().getX();
			if (np.getLocation().getY() > maxs.y) maxs.y = np.getLocation().getY();
			if (np.getLocation().getZ() > maxs.z) maxs.z = np.getLocation().getZ();
			
			if (np.getLocation().getX() < mins.x) mins.x = np.getLocation().getX();
			if (np.getLocation().getY() < mins.y) mins.y = np.getLocation().getY();
			if (np.getLocation().getZ() < mins.z) mins.z = np.getLocation().getZ();			
		}
		
		int randomSeed = 1;
		double step = 31;
		
		log.warning("  +-- CUBE[ " + mins + " - " + maxs + "]");		
		log.warning("  +-- TESTING! Step is: " + step + "; random seed is " + randomSeed);
		int extenseX = (int)Math.floor((maxs.x - mins.x) / step);
		int extenseY = (int)Math.floor((maxs.y - mins.y) / step);
		int extenseZ = (int)Math.floor((maxs.z - mins.z) / step);
		log.warning("  +-- Start locations (2D)    about: " + extenseX + "*" + extenseY + " = " + (extenseX*extenseY));
		log.warning("  +-- Start locations (total) about: " + extenseX + "*" + extenseY + "*" + extenseZ + " = " + (extenseX*extenseY*extenseZ));
		
		Random random = new Random(randomSeed);
		
		int tests = 0;
		int fails = 0;
		int notExists = 0;
		
		List<Tuple2<Location, Location>> failures = new ArrayList<Tuple2<Location, Location>>();		
		
		for (double x = mins.x; x < maxs.x + step; x += step) {
			for (double y = mins.y; y < maxs.y + step; y += step) {
				for (double z = mins.z; z < maxs.z + step; z += step) {
					// CHECK START
					Location start = new Location(x, y, z);
					if (navMeshModule.getDropGrounder().tryGround(start) == null) {
						// INVALID LOCATION
						continue;						
					}
					
					// FIND RANDOM TARGET
					Location target = null;
					while (target == null || navMeshModule.getDropGrounder().tryGround(target) == null) {
						target = new Location(random.nextDouble() * (maxs.x - mins.x) + mins.x, random.nextDouble() * (maxs.y - mins.y) + mins.y, random.nextDouble() * (maxs.z - mins.z) + mins.z);
					}
					
					// PERFORM THE PATH-FINDING
					log.info("  +-- Path-finding: " + start + " -> " + target + " ...");
					
					++tests;
					
					try {
						PrecomputedPathFuture pf = navMeshModule.getAStarPathPlanner().computePath(start, target);
						if (pf.get() == null) {
							log.info("    +-- NOT EXISTS!");
							++notExists;
						} else {
							log.info("    +-- Path-length " + pf.get().size());
						}
					} catch (Exception e) {
						log.info(ExceptionToString.process("AT " + game.getMapName() + " FAILED TO COMPUTE THE PATH: " + start + " -> " + target, e));
						++fails;
						failures.add(new Tuple2<Location, Location>(start, target));
					}
				}	
			}				
		}
		
		for (NavPoint np1 : navPoints.getNavPoints().values()) {
			for (NavPoint np2 : navPoints.getNavPoints().values()) {
				log.info("  +-- Path-finding: " + np1.getId().getStringId() + " -> " + np2.getId().getStringId() + " ...");
				
				++tests;
				
				try {
					PrecomputedPathFuture pf = navMeshModule.getAStarPathPlanner().computePath(np1, np2);
					if (pf.get() == null) {
						log.info("    +-- NOT EXISTS!");
						++notExists;
					} else {
						log.info("    +-- Path-length " + pf.get().size());
					}
				} catch (Exception e) {
					log.info(ExceptionToString.process("AT " + game.getMapName() + " FAILED TO COMPUTE THE PATH: " + np1.getId().getStringId() + " -> " + np2.getId().getStringId(), e));
					++fails;
					failures.add(new Tuple2<Location, Location>(np1.getLocation(), np2.getLocation()));
				}
			}
		}
		
		log.warning("RESULT for " + game.getMapName() + ": " + (tests - fails) + " / " + tests + " SUCCEEDED, " + notExists + " / " + tests + " paths not-exist");
		
		if (fails > 0) {
			if (fails == 1) {
				log.warning("THERE WAS 1 FAILURE!");
			}else {
				log.warning("THERE WERE " + fails + " FAILURES!");
			}
			for (Tuple2<Location, Location> failure : failures) {
				log.warning("  +-- FAILED TO COMPUTE THE PATH: " + failure.getFirst() + " -> " + failure.getSecond());
				
				// DEBUG HOOK
				FunnelDebug.debug = true;
				FunnelDebug.draw = draw;				
				
				// PUT A BREAKPOINT HERE TO RUN THE PATH-FINDING AGAIN USING BREAKPOINTS...
				Location start = failure.getFirst();
				Location target = failure.getSecond();
				navMeshModule.getAStarPathPlanner().computePath(start, target);				
			}
			throw new RuntimeException("PATH-FINDING IS FAILING FOR THIS MAP !!!");
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
                "NavBot  "       // what name the runner should be using
        ).setMain(true)          // tells runner that is is executed inside MAIN method, thus it may block the thread and watch whether agent/s are correctly executed
         .startAgents(1);        // tells the runner to start 1 agent
    }
}
