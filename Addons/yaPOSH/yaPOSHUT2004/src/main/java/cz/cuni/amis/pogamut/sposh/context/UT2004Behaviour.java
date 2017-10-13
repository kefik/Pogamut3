package cz.cuni.amis.pogamut.sposh.context;

import java.util.Random;
import java.util.logging.Level;

import cz.cuni.amis.pathfinding.map.IPFMapView;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.AnnotationListenerRegistrator;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectListener;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.sposh.JavaBehaviour;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.AdrenalineCombo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.AgentConfig;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentStats;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.CTF;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Game;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.ItemDescriptors;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Items;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavPoints;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavigationGraphBuilder;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Players;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.UT2004AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.UT2004Items;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPrefs;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.visibility.Visibility;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004AStarPathPlanner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004GetBackToNavGraph;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004RunStraight;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.astar.UT2004AStar;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.loquenavigator.KefikRunner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.loquenavigator.LoqueNavigator;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.command.CompleteBotCommandsWrapper;
import cz.cuni.amis.pogamut.ut2004.bot.command.ImprovedShooting;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.GetPath;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PathList;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemDescriptor;


//
// 3rd change
//

public class UT2004Behaviour<BOT extends UT2004Bot> extends JavaBehaviour<BOT> {
	
	/**
	 * Random number generator that is usually useful to have during decision making.
	 */
	protected Random random = new Random(System.currentTimeMillis());
	
	/**
     * User log - it's log-level is initially set to {@link Level#ALL}.
     * @deprecated use {@link UT2004BotController#log} instead
     */
    protected LogCategory user = null;
    
    /**
     * Alias for user's log.
     */
    protected LogCategory log = null;
	
	/**
	 * Memory module specialized on general info about the game - game type, time limit, frag limit, etc.
	 * <p><p>
	 * May be used since {@link SposhBotLogic#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004Behaviour#initializeModules(UT2004Bot)}.
	 */
	protected Game game;
	
	/**
	 * Memory module specialized on general info about the agent whereabouts - location, rotation, health, current weapon, who is enemy/friend, etc.
	 * <p><p>
	 * May be used since first {@link Self} message is received, i.e, since the first {@link SposhBotLogic#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004Behaviour#initializeModules(UT2004Bot)}.
	 */
	protected AgentInfo info;
	
	/**
	 * Memory module specialized on whereabouts of other players - who is visible, enemy / friend, whether bot can see anybody, etc.
	 * <p><p>
	 * May be used since {@link SposhBotLogic#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004Behaviour#initializeModules(UT2004Bot)}.
	 */
	protected Players players;
	
	/**
	 * Sensory module that provides mapping between {@link ItemType} and {@link ItemDescriptor} providing
     * an easy way to obtain item descriptors for various items in UT2004.
     * <p><p>
     * May be used since {@link SposhBotLogic#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004Behaviour#initializeModules(UT2004Bot)}.
	 */
	protected ItemDescriptors descriptors;
	
	/**
	 * Memory module specialized on items on the map - which are visible and which are probably spawned.
	 * <p><p>
	 * May be used since {@link SposhBotLogic#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004Behaviour#initializeModules(UT2004Bot)}.
	 */
	protected Items items;
	
	/**
	 * Memory module specialized on agent's senses - whether the bot has been recently killed, collide with level's geometry, etc.
	 * <p><p>
	 * May be used since {@link SposhBotLogic#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004Behaviour#initializeModules(UT2004Bot)}.
	 */
	protected Senses senses;
	
	/**
	 * Memory module specialized on info about the bot's weapon and ammo inventory - it can tell you which weapons are loaded, melee/ranged, etc.
	 * <p><p>
	 * May be used since {@link SposhBotLogic#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004Behaviour#initializeModules(UT2004Bot)}.
	 */
	protected Weaponry weaponry;
	
	/**
	 * Memory module specialized on the agent's configuration inside UT2004 - name, vision time, manual spawn, cheats (if enabled at GB2004).
	 * <p><p>
	 * May be used since {@link SposhBotLogic#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004Behaviour#initializeModules(UT2004Bot)}.
	 */
	protected AgentConfig config;
	
	/**
	 * Support for creating rays used for raycasting (see {@link AutoTraceRay} that is being utilized).
	 * <p><p>
	 * May be used since {@link SposhBotLogic#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
	 */
	protected Raycasting raycasting;
	
	/**
	 * Creates and wraps all available commands that can be issued to the virtual body of the bot inside UT2004.
     * <p><p>
     * May be used since since the first {@link SposhBotLogic#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004Behaviour#initializeModules(UT2004Bot)}.
	 */
	protected CompleteBotCommandsWrapper body;
	
	/**
	 * Shortcut for <i>body.getImprovedShooting()</i> that allows you to shoot at opponent.
	 * <p><p>
	 * Note: more weapon-handling methods are available through {@link UT2004Behaviour#weaponry}.
	 * <p><p>
	 * May be used since since the first {@link SposhBotLogic#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)} 
     * is called.
     * <p><p>
	 * Initialized inside {@link UT2004Behaviour#initializeModules(UT2004Bot)}.
	 */
	protected ImprovedShooting shoot;
	
	/**
	 * Shortcut for <i>body.getAdvancedLocomotion()</i> that allows you to manually steer the movement through the environment.
	 * <p><p>
	 * Note: navigation is done via {@link UT2004Behaviour#pathExecutor} that needs {@link PathHandle} from the {@link UT2004Behaviour#pathPlanner}.
	 * <p><p>
	 * May be used since since the first {@link SposhBotLogic#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)} 
     * is called.
     * <p><p>
	 * Initialized inside {@link UT2004Behaviour#initializeModules(UT2004Bot)}.
	 */
	protected AdvancedLocomotion move;
	
	/**
     * Executor is used for following a path in the environment.
     * <p><p>
     * May be used since since the first {@link IUT2004BotController#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleControllerNew#initializePathFinding(UT2004Bot)}.
     */
	protected UT2004PathExecutor<ILocated> pathExecutor = null;

    /**
     * Planner used to compute the path (consisting of navigation points) inside the map.
     * <p><p>
     * May be used since since the first {@link IUT2004BotController#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleControllerNew#initializePathFinding(UT2004Bot)}.
     */
    protected IPathPlanner<ILocated> pathPlanner = null;
    
    /**
     * Weapon preferences for your bot. See {@link WeaponPrefs} class javadoc. It allows you to define preferences for
     * weapons to be used at given distance (together with their firing mode).
     */
    protected WeaponPrefs weaponPrefs;
    
    /**
     * Navigation graph builder that may be used to manually extend the navigation graph of the UT2004.
     * <p><p>
     * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    protected NavigationGraphBuilder navBuilder;
    
    /**
     * Module that is providing various statistics about the bot. You may also used it to output these stats (in CSV format)
     * into some file using {@link AgentStats#startOutput(String)} or {@link AgentStats#startOutput(String, boolean)}.
     */
    protected AgentStats stats;

    /**
     * Path-planner ({@link IPathPlanner} using {@link NavPoint}s), you may use it to find paths inside the environment wihtout
     * waiting for round-trip of {@link GetPath} command and {@link PathList}s response from UT2004. It is much faster than 
     * {@link UT2004BotModuleController#pathPlanner} but you need to pass {@link NavPoint} instances to planner instead of
     * {@link ILocated} ... to find the nearest {@link NavPoint} instance, {@link DistanceUtils} is a handy, check especially
     * {@link DistanceUtils#getNearest(java.util.Collection, ILocated)}.
     */
	protected FloydWarshallMap fwMap;
	
	/**
	 * Navigation helper that is able to get your bot back to the nearest navigation graph so you can use {@link UT2004BotModuleController#navigation} 
	 * without fear of catastrophe.
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializePathFinding(UT2004Bot)}.
	 */
	protected UT2004GetBackToNavGraph getBackToNavGraph;
	
	/**
	 * Navigation helper that can run-straight to some point with stuck detectors.
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializePathFinding(UT2004Bot)}.
	 */
	protected UT2004RunStraight runStraight;
	
	/**
	 * Command module that is internally using {@link UT2004PathExecutor} for path-following and {@link FloydWarshallMap}
	 * for path planning resulting in unified class that can solely handle navigation of the bot within the environment.
	 * <p><p> 
	 * In contrast to {@link UT2004PathExecutor} methods
	 * of this module may be recalled every {@link UT2004BotModuleController#logic()} iteration even with 
	 * the same argument (which is not true for {@link UT2004PathExecutor#followPath(cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture)}.
	 * <p><p>
	 * Note that this class is actually initialized with instances of {@link UT2004BotModuleController#pathExecutor} and {@link UT2004BotModuleController#fwMap}
	 * so you must take care if using add/remove stuck detectors or reinitilize this property to your liking (you can do that in {@link UT2004BotModuleController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)} 
	 * method.
	 * <p><p>
	 * May be used since first {@link UT2004BotModuleController#logic()} is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializePathFinding(UT2004Bot)}.
	 */
	protected UT2004Navigation navigation;
	
	/**
	 * Module that provides visibility/cover information for the map.
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected Visibility visibility;
    
    /**
     * Listener registrator that probes declared methods for the presence of {@link EventListener}, {@link ObjectClassEventListener},
     * {@link ObjectClassListener}, {@link ObjectEventListener} and {@link ObjectListener} annotations and automatically registers
     * them as listeners on a specific events.
     * <p><p>
     * Note that this registrator is usable for 'this' object only! It will work only for 'this' object.
     */
    protected AnnotationListenerRegistrator listenerRegistrator;
    
    /**
     * Shortcut for the {@link UT2004BotModuleController#getWorldView()}.
     */
    protected IVisionWorldView world;
    
    /**
	 * Module that provides shortcut for getting {@link NavPoint}s out of {@link IWorldView}.
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected NavPoints navPoints;
	
	/**
	 * Class providing A-Star algorithm over the navpoints as they are present within the {@link IWorldView}.
	 * <p><p>
	 * You may provide custom {@link IPFMapView} over the map in-order to greatly customize the A-Star search.
	 */
	protected UT2004AStar aStar;
	
	/**
	 * Module specialized on CTF games. Enabled only for CTF games, check {@link CTF#isEnabled()}.
	 * <p><p>
	 * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected CTF ctf;
	
	/**
	 * Module for adrenaline combos.
	 */
	protected AdrenalineCombo combo;
    
    /**
     * Shortcut for the {@link UT2004BotModuleController#getAct()}.
     */
    protected IAct act;

	public UT2004Behaviour(String name, BOT bot) {
		super(name, bot);
        log = bot.getLogger().getCategory(UT2004BotController.USER_LOG_CATEGORY_ID);
        log.setLevel(Level.ALL);
        user = log;
	}

	/**
	 * Called after the construction of the {@link UT2004Behaviour} before the GameBots2004 greets the bot even before
	 * {@link UT2004Behaviour#prepareBehaviour(UT2004Bot)} method.
	 * <p><p>
	 * <b>NOTE:</b> This is Pogamut's developers reserved method - do not override it and if you do, always use 'super' 
	 * to call parent's initializeBehaviour.
     */
	protected void initializeBehaviour(BOT bot) {
		initializeModules(bot);
		initializePathFinding(bot);
		initializeListeners(bot);
	}
	
	/**
     * Initializes {@link UT2004Behaviour#listenerRegistrator} and calls {@link AnnotationListenerRegistrator#addListeners()} method
     * to probe all declared methods for event-annotation presence.
     * @param bot
     */
	protected void initializeListeners(BOT bot) {
		listenerRegistrator = new AnnotationListenerRegistrator(this, getWorldView(), bot.getLogger().getCategory("Listeners"));
		listenerRegistrator.addListeners();
	}

	/**
	 * Initializes path-finding modules: {@link UT2004BotModuleControllerNew#pathPlanner} and {@link UT2004BotModuleControllerNew#pathExecutor}.
	 * If you need different path planner / path executor - override this method and initialize your own modules.
	 * @param bot
	 */
	protected void initializePathFinding(BOT bot) {
		pathPlanner  = new UT2004AStarPathPlanner(bot);
		fwMap        = new FloydWarshallMap(bot);
		aStar        = new UT2004AStar(bot);
		pathExecutor = 
        	new UT2004PathExecutor<ILocated>(
        		bot, 
        		info,
        		move,
        		new LoqueNavigator<ILocated>(bot, info, move, bot.getLog())
        	);   
		getBackToNavGraph = new UT2004GetBackToNavGraph(bot, info, move);
		runStraight = new UT2004RunStraight(bot, info, move);
		navigation = new UT2004Navigation(bot, pathExecutor, fwMap, getBackToNavGraph, runStraight);
	}

	/**
	 * Initializes memory/command modules of the bot.
	 * 
	 * @param bot
	 */
	protected void initializeModules(BOT bot) {
		world       = getWorldView();
		act         = getAct();
		
		game        = new Game(bot);
		navPoints   = new NavPoints(bot);
		players     = new Players(bot);
		descriptors = new ItemDescriptors(bot);
		config      = new AgentConfig(bot);
		raycasting  = new Raycasting(bot);
		stats       = new AgentStats(bot);
		navBuilder  = new NavigationGraphBuilder(bot);
		info        = new UT2004AgentInfo(bot, game);
		visibility  = new Visibility(bot, info);
		ctf         = new CTF(bot, info);
		weaponry    = new Weaponry(bot, descriptors);
		items       = new UT2004Items(bot, info, game, weaponry, null);
		senses      = new Senses(bot, info, players);
		body        = new CompleteBotCommandsWrapper(bot, weaponry, null);		
		shoot       = body.getImprovedShooting();
		move        = body.getLocomotion();
		weaponPrefs = new WeaponPrefs(weaponry, bot);
		combo       = new AdrenalineCombo(bot, info);
	}
	
	/**
	 * Called after the behaviour construction to initialize user's data structures.
	 * @param bot
	 */
	protected void prepareBehaviour(BOT bot) {		
	}	
	
	/**
     * This method is called whenever {@link InitedMessage} is received. Various agent modules are usable since this
     * method is called.
     * 
     * @param gameInfo
     * @param config
     * @param init
     * @param self
     */
    public void botInitialized(GameInfo info, ConfigChange config, InitedMessage init) {
    }

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
    public void botSpawned(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self) {
    }
    
    /**
	 * Called after {@link UT2004Behaviour#botSpawned(GameInfo, ConfigChange, InitedMessage, Self)} as a hook for Pogamut's core developers
	 * to finalize initialization of various modules.
	 * <p><p>
	 * <b>NOTE:</b> This is Pogamut's developers reserved method - do not override it and if you do, always use 'super' 
	 * to call parent's finishControllerInitialization.
     */
    public void finishBehaviourInitialization() {
    	if (navBuilder.isUsed()) {
			log.info("Navigation graph has been altered by 'navBuilder', triggering recomputation of Floyd-Warshall path matrix...");
			Level oldLevel = fwMap.getLog().getLevel();
			fwMap.getLog().setLevel(Level.FINER);
			fwMap.refreshPathMatrix();
			fwMap.getLog().setLevel(oldLevel);
		}
    }
    
    /**
     * Called whenever the bot gets killed inside the game.
     * 
     * @param event
     */
	public void botKilled(BotKilled event) {		
	}
	
	public IVisionWorldView getWorldView() {
		return bot.getWorldView();
	}
	
	public IAct getAct() {
		return bot.getAct();
	}

	public UT2004GetBackToNavGraph getGetBackToNavGraph() {
		return getBackToNavGraph;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public Random getRandom() {
		return random;
	}

	public LogCategory getLog() {
		return log;
	}

	public Game getGame() {
		return game;
	}

	public AgentInfo getInfo() {
		return info;
	}

	public Players getPlayers() {
		return players;
	}

	public ItemDescriptors getDescriptors() {
		return descriptors;
	}

	public Items getItems() {
		return items;
	}

	public Senses getSensesModule() {
		return senses;
	}

	public Weaponry getWeaponry() {
		return weaponry;
	}

	public AgentConfig getConfig() {
		return config;
	}

	public Raycasting getRaycasting() {
		return raycasting;
	}

	public CompleteBotCommandsWrapper getBody() {
		return body;
	}

	public ImprovedShooting getShoot() {
		return shoot;
	}

	public AdvancedLocomotion getMove() {
		return move;
	}

	public UT2004PathExecutor<ILocated> getPathExecutor() {
		return pathExecutor;
	}

	public IPathPlanner<ILocated> getPathPlanner() {
		return pathPlanner;
	}

	public WeaponPrefs getWeaponPrefs() {
		return weaponPrefs;
	}

	public NavigationGraphBuilder getNavBuilder() {
		return navBuilder;
	}

	public AgentStats getStats() {
		return stats;
	}

	public FloydWarshallMap getFwMap() {
		return fwMap;
	}

	public UT2004RunStraight getRunStraight() {
		return runStraight;
	}

	public UT2004Navigation getNavigation() {
		return navigation;
	}

	public IVisionWorldView getWorld() {
		return world;
	}

	public NavPoints getNavPoints() {
		return navPoints;
	}

	public UT2004AStar getaStar() {
		return aStar;
	}

	public CTF getCtf() {
		return ctf;
	}

	public AdrenalineCombo getCombo() {
		return combo;
	}

}
