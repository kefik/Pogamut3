package ut2004.exercises.e02;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.ChangeAttribute;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.KillBot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Bumped;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GlobalChat;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Spawn;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import ut2004.exercises.e02.TagMap.Direction;

/**
 * SHEEP(s) BOT FOR THE EXERCISE 2
 * -------------------------------
 * To be used with the map: DM-TagMap
 * 
 * Start the bot(s) and leave it(them) be within the environment;
 * 
 * @author Jakub Gemrot aka Jimmy aka Kefik
 */
@AgentScoped
public class SheepBot extends UT2004BotModuleController {
    
	private static AtomicInteger INSTANCE = new AtomicInteger(1);
	
	private static Map<Player, Location> wolfs = new HashMap<Player, Location>();
	
	// BOT-SPECIFIC
	
	private int instance = 0;
	
	private int dodge = 0;
	
	/**
     * Here we can modify initializing command for our bot, e.g., sets its name or skin.
     *
     * @return instance of {@link Initialize}
     */
    @Override
    public Initialize getInitializeCommand() {  
    	instance = INSTANCE.getAndIncrement();
    	return new Initialize().setName("SheepBot-" + instance).setSkin("Aliens.AlienFemaleA");
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
    	act.act(new Configuration().setManualSpawn(true).setVisionTime(0.1));
    }
     
    @EventListener(eventClass=GlobalChat.class)
    public void chatReceived(GlobalChat msg) {
    	if (instance == 1) Utils.handleMessage(msg);    	
    	if (msg.getText().toLowerCase().contains("restart")) respawn();
    }   
    
    private void respawn() {
    	body.getAction().respawn();
	}

	@Override
    public void botKilled(BotKilled event) {
    }
    
    @EventListener(eventClass=Bumped.class)
    private void botBumped(Bumped event) {
    	if (event.getId() != null) {
    		synchronized(wolfs) {
    			for (Player player : wolfs.keySet()) {
    				if (player.getId().getStringId().equals(event.getId().getStringId())) {
    					die();
    				}
    			}
    		}
    	}
    }
    
    @ObjectClassEventListener(objectClass=Self.class, eventClass=WorldObjectUpdatedEvent.class)
    public void selfUpdated(WorldObjectUpdatedEvent<Self> event) {
    	logic();
    }

//    private static CountDownLatch cdl = new CountDownLatch(4);
//    
//    private boolean a = true;
    
    /**
     * Main method called 4 times / second. Do your action-selection here.
     */
    @Override
    public void logic() throws PogamutException {    	    	
    	if (!Utils.gameRunning) return;
    	
//    	cdl.countDown();
//    	try {
//			cdl.await();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//    	
//    	if (a) { die(); a = false; }
    	
    	
    	int visibleWolfs = 0;
    	for (Player player : players.getVisiblePlayers().values()) {
    		if (Utils.isWolf(player)) {
    			synchronized(wolfs) {
    				wolfs.put(player, player.getLocation());
    			}
    			++visibleWolfs;
    			if (info.getDistance(player) < Utils.CATCH_DISTANCE) {
    				die();
    				return;
    			}
    		}
    	}
    	
    	if (visibleWolfs == 0) {
    		move.turnHorizontal(60);
    		return;
    	}
    	
    	Location firstWolf = getFirstWolf();
    	Location secondWolf = getSecondWolf();
    	if (firstWolf == secondWolf) secondWolf = null;
    	
    	boolean escapeFirst = firstWolf != null && info.getDistance(firstWolf) < 600;
    	boolean escapeSecond = secondWolf != null && info.getDistance(secondWolf) < 600;
    	    	
    	Location force = new Location(0,0,0);
    	
    	if (info.getDistance(firstWolf) < 600) {
    		Location opposite = info.getLocation().sub(firstWolf).getNormalized();
    		force = Location.add(force, opposite);
    	}
    	
    	if (secondWolf != null) {
	    	if (info.getDistance(secondWolf) < 600) {
	    		Location opposite = info.getLocation().sub(secondWolf).getNormalized();
	    		force = Location.add(force, opposite);
	    	}
    	}
    	
    	boolean north = TagMap.getWallDistance(Direction.NORTH, info.getLocation()) < 300;
    	boolean east  = TagMap.getWallDistance(Direction.EAST, info.getLocation())  < 300;
    	boolean west  = TagMap.getWallDistance(Direction.WEST, info.getLocation())  < 300;
    	boolean south = TagMap.getWallDistance(Direction.SOUTH, info.getLocation()) < 300;
    	    	
    	for (TagMap.Direction side : TagMap.Direction.values()) {
    		if (TagMap.getWallDistance(side, info.getLocation()) < 300) {
    			Location opposite = side.direction().invert().getNormalized();
    			force = Location.add(force, opposite);
    		}
    	}
    	
    	if (force.getLength() > 0.1) {
    		dodge += 1;
    		if (dodge > 5 && (escapeFirst || escapeSecond)) {
				move.dodge(force.getNormalized().setZ(0), true);
				dodge = 0;
    		} else {
    			if (secondWolf != null) {
    				move.strafeTo(info.getLocation().add(force.getNormalized().setZ(0).scale(800)), firstWolf.add(secondWolf).scale(0.5f));
    			} else {
    				move.strafeTo(info.getLocation().add(force.getNormalized().setZ(0).scale(800)), firstWolf);
    			}
    		}
    	} else {
    		if (escapeFirst || escapeSecond) {
    			if (north) {
    				if (TagMap.getEastWallDistance(info.getLocation()) < TagMap.getWestWallDistance(info.getLocation())) {
    					move.dodge(Direction.WEST.direction(), true);
    				} else {
    					move.dodge(Direction.EAST.direction(), true);
    				}
    			}
    			if (south) {
    				if (TagMap.getEastWallDistance(info.getLocation()) < TagMap.getWestWallDistance(info.getLocation())) {
    					move.dodge(Direction.WEST.direction(), true);
    				} else {
    					move.dodge(Direction.EAST.direction(), true);
    				}
    			}
    			if (east) {
    				if (TagMap.getSouthWallDistance(info.getLocation()) < TagMap.getNorthWallDistance(info.getLocation())) {
    					move.dodge(Direction.NORTH.direction(), true);
    				} else {
    					move.dodge(Direction.SOUTH.direction(), true);
    				}
    			}
    			if (west) {
    				if (TagMap.getSouthWallDistance(info.getLocation()) < TagMap.getNorthWallDistance(info.getLocation())) {
    					move.dodge(Direction.NORTH.direction(), true);
    				} else {
    					move.dodge(Direction.SOUTH.direction(), true);
    				}
    			}
    		} else {
	    		if (secondWolf != null) {
	    			move.turnTo(firstWolf.add(secondWolf).scale(0.5f));
	    		} else {
	    			move.turnTo(firstWolf);
	    		}
    		}
    	}
    	
    }
    
    private void die() {
    	serverProvider.getServer().getAct().act(new KillBot(info.getId()));
	}

	private Location getFirstWolf() {
    	synchronized(wolfs) {
    		return DistanceUtils.getNearest(wolfs.values(), info.getLocation());
    	}
    }
    
    private Location getSecondWolf() {
    	synchronized(wolfs) {
    		return DistanceUtils.getFarthest(wolfs.values(), info.getLocation());
    	}
    }

	/**
     * This method is called when the bot is started either from IDE or from command line.
     *
     * @param args
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String args[]) throws PogamutException {
        new UT2004BotRunner(      // class that wrapps logic for bots executions, suitable to run single bot in single JVM
                SheepBot.class,  // which UT2004BotController it should instantiate
                "SheepBot"       // what name the runner should be using
        ).setMain(true)           // tells runner that is is executed inside MAIN method, thus it may block the thread and watch whether agent/s are correctly executed
         .startAgents(12);         // tells the runner to start 1 agent
    }
}
