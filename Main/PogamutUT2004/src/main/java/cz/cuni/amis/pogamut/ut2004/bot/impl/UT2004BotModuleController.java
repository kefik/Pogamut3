package cz.cuni.amis.pogamut.ut2004.bot.impl;

import java.util.Random;

import cz.cuni.amis.pathfinding.map.IPFMapView;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.AnnotationListenerRegistrator;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectListener;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.AdrenalineCombo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.AgentConfig;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Raycasting;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.UT2004Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentStats;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.CTF;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Game;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.IVisibilityAdapter;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.ItemDescriptors;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Items;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.LevelGeometryVisibilityAdapter;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavPointVisibility;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavPoints;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavigationGraphBuilder;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.PathBuilder;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Players;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.UT2004AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.UT2004Items;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.WeaponPrefs;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.visibility.Visibility;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004AStarPathPlanner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004MapTweaks;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.astar.UT2004AStar;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.LevelGeometry;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.LevelGeometryModule;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.IUT2004ServerProvider;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.UT2004Draw;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.UT2004ServerProvider;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old.OldNavMesh;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old.OldNavMeshModule;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathfollowing.NavMeshNavigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathfollowing.UT2004AcceleratedPathExecutor;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.command.CompleteBotCommandsWrapper;
import cz.cuni.amis.pogamut.ut2004.bot.command.ImprovedShooting;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.DrawStayingDebugLines;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.GetPath;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PathList;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemDescriptor;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.utils.flag.FlagListener;

/**
 * The most advanced controller that is available. This controller contains all useful modules pre-instantiated.
 *
 * @author Jimmy
 *
 * @param <BOT>
 */
public class UT2004BotModuleController<BOT extends UT2004Bot> extends UT2004BotLogicController<BOT> {

	/**
	 * Random number generator that is usually useful to have during decision making.
	 */
	protected Random random = new Random(System.currentTimeMillis());
	
	/**
	 * Command module that is internally using {@link UT2004AcceleratedPathExecutor} for path-following and {@link FloydWarshallMap}
	 * for path planning resulting in unified class that can solely handle navigation of the bot within the environment.
	 * <p><p> 
	 * In contrast to {@link UT2004AcceleratedPathExecutor} methods
	 * of this module may be recalled every {@link UT2004BotModuleController#logic()} iteration even with 
	 * the same argument (which is not true for {@link UT2004AcceleratedPathExecutor#followPath(cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture)}.
	 * <p><p>
	 * May be used since first {@link UT2004BotModuleController#logic()} is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializePathFinding(UT2004Bot)}.
	 */
	protected IUT2004Navigation navigation;
	
	/**
	 * Command module that is internally using {@link OldNavMesh} and {@link OldNavMeshModule} for the path-finding and {@link UT2004AcceleratedPathExecutor} for the
	 * path execution. It uses own instance of {@link UT2004AcceleratedPathExecutor} !
	 * <p><p>
	 * Note that ".navmesh" file for concrete UT2004 map needs to be present inside ${BOT_DIR}/navmesh directory in order for this module to be working.
	 * <p><p>
	 * Download preprocessed navmesh files from: svn://artemis.ms.mff.cuni.cz/pogamut/trunk/project/Addons/UT2004NavMeshTools/04-NavMeshes
	 * <p><p>
	 * If in doubt, consult example available from svn://artemis.ms.mff.cuni.cz/pogamut/trunk/project/Main/PogamutUT2004Examples/28-NavMeshBot
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializePathFinding(UT2004Bot)}.
	 */
	protected NavMeshNavigation nmNav;
	
	/**
	 * Module that serves for accumulating knowledge about UT2004 maps, it contains a list of map-fixes (typically removal of invalid nav links).
	 * You can register your own map tweaks via {@link UT2004MapTweaks#register(String, cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004MapTweaks.IMapTweak)}.
	 * <p><p>
	 * Good place to register your tweaks is inside {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     * <p><p>
     * Used inside {@link #mapInfoObtainedInternal()} where {@link UT2004MapTweaks#tweak(NavigationGraphBuilder)} method is called with argument {@link #navBuilder}.
	 */
	protected UT2004MapTweaks mapTweaks;
	
	/**
	 * Memory module specialized on general info about the game - game type, time limit, frag limit, etc.
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected Game game;
	
	/**
	 * Memory module specialized on general info about the agent whereabouts - location, rotation, health, current weapon, who is enemy/friend, etc.
	 * <p><p>
	 * May be used since first {@link Self} message is received, i.e, since the first {@link IUT2004BotController#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected AgentInfo info;
	
	/**
	 * Memory module specialized on whereabouts of other players - who is visible, enemy / friend, whether bot can see anybody, etc.
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected Players players;
	
	/**
	 * Sensory module that provides mapping between {@link ItemType} and {@link ItemDescriptor} providing
     * an easy way to obtain item descriptors for various items in UT2004.
     * <p><p>
     * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected ItemDescriptors descriptors;
	
	/**
	 * Memory module specialized on items on the map - which are visible and which are probably spawned.
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected Items items;
	
	/**
	 * Memory module specialized on agent's senses - whether the bot has been recently killed, collide with level's geometry, etc.
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected Senses senses;
	
	/**
	 * Memory module specialized on info about the bot's weapon and ammo inventory - it can tell you which weapons are loaded, melee/ranged, etc.
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected Weaponry weaponry;
	
	/**
	 * Memory module specialized on the agent's configuration inside UT2004 - name, vision time, manual spawn, cheats (if enabled at GB2004).
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected AgentConfig config;
	
	/**
	 * Support for creating rays used for raycasting (see {@link AutoTraceRay} that is being utilized).
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected Raycasting raycasting;
	
	/**
	 * Wraps all available commands that can be issued to the virtual body of the bot inside UT2004.
     * <p><p>
     * May be used since since the first {@link IUT2004BotController#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected CompleteBotCommandsWrapper body;
	
	/**
	 * Shortcut for <i>body.getAdvancedShooting()</i> that allows you to shoot at opponent.
	 * <p><p>
	 * Note: more weapon-handling methods are available through {@link UT2004BotModuleControllerNew#weaponry}.
	 * <p><p>
	 * May be used since since the first {@link IUT2004BotController#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)} 
     * is called.
     * <p><p>
	 * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected ImprovedShooting shoot;
	
	/**
	 * Shortcut for <i>body.getAdvancedLocomotion()</i> that allows you to manually steer the movement through the environment.
	 * <p><p>
	 * Note: navigation is done via {@link UT2004BotModuleControllerNew#pathExecutor} that needs {@link PathHandle} from the {@link UT2004BotModuleControllerNew#pathPlanner}.
	 * <p><p>
	 * May be used since since the first {@link IUT2004BotController#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)} 
     * is called.
     * <p><p>
	 * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected AdvancedLocomotion move;
	
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
     * Internal planner of UT2004. DO NOT USE THIS - It has many issues, like it won't return path longer than 32 nav points (including starting point).
     * <p><p>
     * May be used since since the first {@link IUT2004BotController#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage, Self)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializePathFinding(UT2004Bot)}.
     */
    protected UT2004AStarPathPlanner ut2004PathPlanner = null;
    
    /**
     * Navigation graph builder that may be used to manually extend the navigation graph of the UT2004.
     * <p><p>
     * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
     */
    protected NavigationGraphBuilder navBuilder = null;
    
    /**
     * Listener registrator that probes declared methods for the presence of {@link EventListener}, {@link ObjectClassEventListener},
     * {@link ObjectClassListener}, {@link ObjectEventListener} and {@link ObjectListener} annotations and automatically registers
     * them as listeners on a specific events.
     * <p><p>
     * Note that this registrator is usable for 'this' object only! It will work only for 'this' object.
     */
    protected AnnotationListenerRegistrator listenerRegistrator;
    
    /**
     * Weapon preferences for your bot. See {@link WeaponPrefs} class javadoc. It allows you to define preferences for
     * weapons to be used at given distance (together with their firing modes).
     */
    protected WeaponPrefs weaponPrefs;
    
    /**
     * Shortcut for the {@link UT2004Bot#getWorldView()}.
     */
    protected IVisionWorldView world;
    
    /**
     * Shortcut for the {@link UT2004Bot#getAct()}.
     */
    protected IAct act;
    
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
	 * Module that provides visibility/cover information for the map.
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
	 */
	protected Visibility visibility;
	
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
	 * Class providing {@link OldNavMesh} instance via {@link OldNavMeshModule#getNavMesh()} method.
	 * <p><p>
	 * Note that ".navmesh" file for concrete UT2004 map needs to be present inside local directory of the bot in order for this module to be working.
	 * <p><p>
	 * Download preprocessed navmesh files from: svn://artemis.ms.mff.cuni.cz/pogamut/trunk/project/Addons/UT2004NavMeshTools/04-NavMeshes
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}. 
	 */
	protected OldNavMeshModule navMeshModule;
	
	/**
	 * Class providing {@link LevelGeometry} instance via {@link LevelGeometryModule#getLevelGeometry()} method.
	 * <p><p>
	 * Note that ".obj", ".centre", ".scale" files must be present inside local directory of the bot in order for this module to be working.
	 * <p><p>
	 * Download preprocessed level geometry from: svn://artemis.ms.mff.cuni.cz/pogamut/trunk/project/Addons/UT2004NavMeshTools/04-NavMeshes 
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}. 
	 */
	protected LevelGeometryModule levelGeometryModule;
	
	/**
	 * Utility class that provides shared instance of {@link UT2004Server}.
	 */
	private IUT2004ServerProvider serverProvider;
	
	/**
	 * Class providing interface for the use of {@link DrawStayingDebugLines} inside UT2004. Can be used to draw debug stuff (lines) right into UT2004.
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}. 
	 */
	protected UT2004Draw draw;
	
	/**
	 * NavMesh path builder that can be used to incrementally plan the path.
	 * <p><p>
	 * Can be used only iff {@link OldNavMeshModule#isInitialized()}.
	 * <p><p>
	 * May be used since {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, InitedMessage)} 
     * is called.
     * <p><p>
     * Initialized inside {@link UT2004BotModuleController#initializePathFinding(UT2004Bot)}. 
	 */
	protected PathBuilder nmPathBuilder;
	
	/**
	 * Object we're using for determing visibility of two points, wraps raycasting object.
	 * Initialized within {@link #initializeModules(UT2004Bot)}.
	 */
	protected IVisibilityAdapter visibilityAdapter;
	
	/**
	 * Module that uses {@link #visibilityAdapter} and performs visibility queries via {@link IVisibilityAdapter#isVisible(cz.cuni.amis.pogamut.base3d.worldview.object.ILocated, cz.cuni.amis.pogamut.base3d.worldview.object.ILocated)}
	 * and updates bot's {@link IWorldView} accordingly. 
	 * Initialized within {@link #initializeModules(UT2004Bot)}.
	 * Works automatically, once you properly initialize {@link #levelGeometryModule} by providing correct files to it,
	 * this module will automatically turn off GB2004 navpoint raytracing and start performing own traces using {@link #visibilityAdapter}.
	 */
	protected NavPointVisibility navPointVisibility;

	private FlagListener<IAgentState> botStateListener = new FlagListener<IAgentState>() {

		@Override
		public void flagChanged(IAgentState changedValue) {			
			if (changedValue instanceof IAgentStateDown) {
				botAgentDown();
			}
		}
	};
	
    @Override
	public void initializeController(BOT bot) {    	
		super.initializeController(bot);
		
		bot.getState().addListener(botStateListener);
		
		world = getWorldView();
		act = getAct();
		initializeModules(bot);
		initializePathFinding(bot);
		initializeListeners(bot);
	}
	
    /**
     * Initializes {@link UT2004BotModuleControllerNew#listenerRegistrator} and calls {@link AnnotationListenerRegistrator#addListeners()} method
     * to probe all declared methods for event-annotation presence.
     * @param bot
     */
	protected void initializeListeners(BOT bot) {
		listenerRegistrator = new AnnotationListenerRegistrator(this, getWorldView(), bot.getLogger().getCategory("Listeners"));
		listenerRegistrator.addListeners();
	}

	/**
	 * Initializes path-finding modules: {@link UT2004BotModuleControllerNew#pathPlanner}, {@link UT2004BotModuleController#fwMap} and {@link UT2004BotModuleControllerNew#pathExecutor}.
	 * If you need different path planner / path executor - override this method and initialize your own modules.
	 * @param bot
	 */
	protected void initializePathFinding(BOT bot) {
		ut2004PathPlanner = new UT2004AStarPathPlanner(bot);
		fwMap             = new FloydWarshallMap(bot);
		navMeshModule.setFwMap(fwMap); // FW Map must be used because of TEPORTERS, which cannot be handled correctly by current A* implementation
		items.setPathPlanner(fwMap); // FW Map is used for path-distance queries
		aStar             = new UT2004AStar(bot);		
		navigation        = new UT2004Navigation(bot, info, move);       
		nmNav             = new NavMeshNavigation(bot, info, move, navMeshModule);
		nmPathBuilder     = new PathBuilder(bot, info, navMeshModule.getNavMesh());
	}

	/**
	 * Initializes memory/command modules of the bot.
	 * 
	 * @param bot
	 */
	protected void initializeModules(BOT bot) {
		game                = new Game(bot);
		navPoints           = new NavPoints(bot);
		players             = new Players(bot);
		descriptors         = new ItemDescriptors(bot);
		config              = new AgentConfig(bot);
		raycasting          = new Raycasting(bot);
		stats               = new AgentStats(bot);
		navBuilder          = new NavigationGraphBuilder(bot);
		mapTweaks           = new UT2004MapTweaks(bot);
		info                = new UT2004AgentInfo(bot, game);
		visibility          = new Visibility(bot, info);
		ctf                 = new CTF(bot, info);
		weaponry            = new UT2004Weaponry(bot, descriptors);
		items               = new UT2004Items(bot, info, game, weaponry, null);
		senses              = new Senses(bot, info, players);
		body                = new CompleteBotCommandsWrapper(bot, weaponry, null);		
		shoot               = body.getImprovedShooting();
		move                = body.getLocomotion();
		weaponPrefs         = new WeaponPrefs(weaponry, bot);
		combo               = new AdrenalineCombo(bot, info);
		serverProvider      = new UT2004ServerProvider();
		navMeshModule       = new OldNavMeshModule(serverProvider, getWorldView(), bot.getLogger());
		levelGeometryModule = new LevelGeometryModule(serverProvider, getWorldView(), bot.getLogger());		 
		draw                = new UT2004Draw(bot.getLogger().getCategory("Draw"), serverProvider);
		visibilityAdapter   = new LevelGeometryVisibilityAdapter(levelGeometryModule);
    	navPointVisibility  = new NavPointVisibility(bot, info, visibilityAdapter);    	
	}
	
	@Override
	void mapInfoObtainedInternal() {
		super.mapInfoObtainedInternal();
		mapTweaks.tweak(navBuilder);		
	}
	
	@Override
	public void finishControllerInitialization() {		
		if (navBuilder.isUsed()) {
			log.info("Navigation graph has been altered by 'navBuilder', triggering recomputation of Floyd-Warshall path matrix...");
			//Level oldLevel = fwMap.getLog().getLevel();
			//fwMap.getLog().setLevel(Level.FINER);
			fwMap.refreshPathMatrix();
			//fwMap.getLog().setLevel(oldLevel);
			
			aStar.mapChanged();
		}
		
		if (navMeshModule.isInitialized()) {
			log.info("Navigation mesh available; switching to nmNav by default setting: navigation = nmNav");
			navigation = nmNav;
		}
	}

	//
	//
	// MODULE GETTERS
	//
	//
	
	public UT2004Draw getDraw() {
		return draw;
	}
	
	public Random getRandom() {
		return random;
	}
	
	public UT2004AStar getAStar() {
		return aStar;
	}
	
	public Game getGame() {
		return game;
	}

	public AgentInfo getInfo() {
		return info;
	}
	
	public NavPoints getNavPoints() {
		return navPoints;
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

	public Senses getSenses() {
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

	public UT2004AStarPathPlanner getUT2004AStarPathPlanner() {
		return ut2004PathPlanner;
	}
	
	public AdrenalineCombo getCombo() {
		return combo;
	}

	public NavigationGraphBuilder getNavBuilder() {
		return navBuilder;
	}
	
	public UT2004MapTweaks getMapTweaks() {
		return mapTweaks;
	}

	public WeaponPrefs getWeaponPrefs() {
		return weaponPrefs;
	}

	public IVisionWorldView getWorld() {
		return world;
	}

	public AgentStats getStats() {
		return stats;
	}

	public FloydWarshallMap getFwMap() {
		return fwMap;
	}
	
	public IUT2004Navigation getNavigation() {
		return navigation;
	}
	
	public NavMeshNavigation getNMNav() {
		return nmNav;
	}

	public Visibility getVisibility() {
		return visibility;
	}
	
	public OldNavMeshModule getNavMeshModule() {
		return navMeshModule;
	}
	
	public OldNavMesh getNavMesh() {
		if (!navMeshModule.isInitialized()) log.warning("NavMeshModule has not been initialized (yet?)! You are either calling this method too early or missing .navmesh file in the local directory of your bot!");
		return navMeshModule.getNavMesh();
	}
	
	public LevelGeometryModule getLevelGeometryModule() {
		return levelGeometryModule;
	}
	
	public LevelGeometry getLevelGeometry() {
		if (!levelGeometryModule.isInitialized()) log.warning("LevelGeometryModule has not been initialized (yet?)! You are either calling this method too early or missing .obj, .scale, .center files in the local directory of your bot!");
		return levelGeometryModule.getLevelGeometry();
	}
	
	public PathBuilder getNMPathBuilder() {
		if (!navMeshModule.isInitialized()) log.warning("NavMesh has not been initialized (yet?)! You are either calling this method too early or missing .navmesh file in the local directory of your bot!");
		return nmPathBuilder;
	}
	
	public CTF getCTF() {
		return ctf;
	}
	
	public IVisibilityAdapter getNavPointVisibilityAdapter() {
		return visibilityAdapter;
	}
	
	public NavPointVisibility getNavPointVisibility() {
		return navPointVisibility;
	}
	
	// ==========
	// LIFE-CYCLE	 
	// ==========
	
	protected void botAgentDown() {
		if (serverProvider != null) {
			serverProvider.killServer();
		}
	}
	
}
