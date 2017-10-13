package cz.cuni.amis.pogamut.ut2004.agent.module.sensor.visibility;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.visibility.model.VisibilityLocation;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.visibility.model.VisibilityMatrix;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;
import cz.cuni.amis.pogamut.ut2004.utils.PogamutUT2004Property;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.SafeEquals;

/**
 * Module that provides visibility information for the map.
 * 
 * The visibility is approximated from {@link VisibilityLocation}s for which we have {@link VisibilityMatrix} built.
 * 
 * Note that the module expects "VisibilityMatrix-" + mapName + "-All.bin" file present
 * in the current directory of the process "." (preferred) or directory specified in PogamutUT2004.properties under 
 * {@link PogamutUT2004Property#POGAMUT_UT2004_VISIBILITY_DIRECTORY}. This file can be generated utilizing 
 * {@link VisibilityCreator} and its {@link VisibilityCreator#main(String[])} method.
 * 
 * @author Jimmy
 */
public class Visibility extends SensorModule<UT2004Bot>
{
	/**
	 * Tells you whether the module is initialized, e.g., visibility matrix information is available for the level.
	 * 
	 * See {@link VisibilityCreator} that contains {@link VisibilityCreator#main(String[])} method implemented for quick generation
	 * of {@link VisibilityMatrix} that is used for obtaining all visibility information for the level. 
	 * 
	 * @return
	 */
	public boolean isInitialized() {
		return matrix != null;
	}
	
	/**
	 * Returns underlying {@link VisibilityMatrix} that is neede for custom advanced computation.
	 * @return
	 */
	public VisibilityMatrix getMatrix() {
		return matrix;
	}
		
	/**
	 * Nearest {@link VisibilityLocation} to 'located' present in matrix. 
	 * 
	 * I.e., nearest {@link VisibilityLocation} we're having visibility information for. 
	 * 
	 * @param located
	 * @return
	 */
	public VisibilityLocation getNearestVisibilityLocationTo(ILocated located) {
		if (!isInitialized()) return null;
		return matrix.getNearest(located);
	}
	
	/**
	 * Nearest {@link VisibilityLocation} to BOT CURRENT LOCATION. 
	 * 
	 * I.e., nearest {@link VisibilityLocation} we're having visibility information for.
	 * 
	 * @return
	 */
	public VisibilityLocation getNearestVisibilityLocation() {
		return getNearestVisibilityLocationTo(info.getLocation());
	}
	
	/**
	 * Nearest {@link NavPoint} to 'located' present in the matrix, this should equal (== 99.99%) to nearest navpoint in the map.
	 * 
	 * @param located
	 * @return
	 */
	public NavPoint getNearestNavPointTo(ILocated located) {
		if (!isInitialized()) return null;
		return matrix.getNearestNavPoint(located);
	}
	
	/**
	 * Nearest {@link NavPoint} to BOT CURRENT LOCATION present in the matrix, this should equal (== 99.99%) to nearest navpoint (to bot) in the map.
	 *
	 * @return
	 */
	public NavPoint getNearestNavPopint() {
		return getNearestNavPointTo(info.getLocation());
	}
	
	/**
	 * Returns whether 'loc1' is visible from 'loc2' (and vice versa == symmetric info).
	 * 
	 * Note that the information is only approximated from nearest known {@link VisibilityLocation}.
	 * The information is accurate for navpoints and very accurate for points on links between navpoints. 
	 * 
	 * If module is not {@link Visibility#isInitialized()}, returns false.
	 * 
	 * @param loc1
	 * @param loc2
	 * @return
	 */
	public boolean isVisible(ILocated loc1, ILocated loc2) {
		if (!isInitialized()) return false;
		return matrix.isVisible(loc1, loc2);
	}
	
	/**
	 * Returns whether BOT can see 'target' (and vice versa == symmetric info).
	 * 
	 * Note that the information is only approximated from nearest known {@link VisibilityLocation}.
	 * The information is accurate for navpoints and very accurate for points on links between navpoints.
	 * 
	 * If module is not {@link Visibility#isInitialized()}, returns false.
	 * 
	 * @param target
	 * @return
	 */
	public boolean isVisible(ILocated target) {
		return isVisible(info.getLocation(), target);
	}
	
	/**
	 * Returns set of {@link VisibilityLocation} that are not visible from "loc".
	 * @param loc
	 * @return
	 */
	public Set<VisibilityLocation> getCoverPointsFrom(ILocated loc) {
		if (!isInitialized()) return null;
		return matrix.getCoverPoints(loc);
	}
	
	/**
	 * Returns set of {@link VisibilityLocation} that are not visible FROM BOT CURRENT LOCATION.
	 * @return
	 */
	public Set<VisibilityLocation> getHiddenPoints() {
		return getCoverPointsFrom(info.getLocation());
	}
	
	/**
	 * Returns nearest cover point for BOT where to hide from 'enemy'.
	 * @param enemy
	 * @return
	 */
	public VisibilityLocation getNearestCoverPointFrom(ILocated enemy) {
		if (!isInitialized()) return null;
		return matrix.getNearestCoverPoint(info.getLocation(), enemy);
	}
	
	
	/**
	 * Returns set of {@link VisibilityLocation} that are visible from "loc".
	 * 
	 * @param loc
	 * @return
	 */
	public Set<VisibilityLocation> getVisiblePointsFrom(ILocated loc) {
		if (!isInitialized()) return null;
		return matrix.getVisiblePoints(loc);
	}
	
	/**
	 * Returns set of {@link VisibilityLocation} that are visible FROM CURRENT BOT LOCATION.
	 * @return
	 */
	public Set<VisibilityLocation> getVisiblePoints() {
		return getVisiblePointsFrom(info.getLocation());
	}
		
	/**
	 * Returns set of {@link NavPoint} that are not visible from "loc".
	 * @param loc
	 * @return
	 */
	public Set<NavPoint> getCoverNavPointsFrom(ILocated loc) {
		if (!isInitialized()) return null;
		return matrix.getCoverNavPoints(loc);
	}
	
	/**
	 * Returns set of {@link NavPoint} that are not visible FROM CURRENT BOT LOCATION.
	 * @return
	 */
	public Set<NavPoint> getHiddenNavPoints() {
		return getCoverNavPointsFrom(info.getLocation());
	}
	
	/**
	 * Returns nearest cover {@link NavPoint} for BOT where to hide from 'enemy'.
	 * @param enemy
	 * @return
	 */
	public NavPoint getNearestCoverNavPointFrom(ILocated enemy) {
		if (!isInitialized()) return null;
		return matrix.getNearestCoverNavPoint(enemy);
	}
	
	/**
	 * Returns set of {@link NavPoint} that are visible from "loc".
	 * 
	 * @param loc
	 * @return
	 */
	public Set<NavPoint> getVisibleNavPointsFrom(ILocated loc) {
		if (!isInitialized()) return null;
		return matrix.getVisibleNavPoints(loc);
	}
	
	/**
	 * Returns set of {@link NavPoint} that are visible FROM CURRENT BOT LOCATION.
	 * @return
	 */
	public Set<NavPoint> getVisibleNavPoints() {
		if (!isInitialized()) return null;
		return matrix.getVisibleNavPoints(info.getLocation());
	}
	
	/**
	 * Returns set of {@link VisibilityLocation} that are not visible from any 'enemies'.
	 * @param enemies
	 * @return
	 */
	public Set<VisibilityLocation> getCoverPointsFromN(ILocated... enemies) {
		if (!isInitialized()) return null;
		return matrix.getCoverPointsN(enemies);		
	}
	
	/**
	 * Returns nearest cover point for 'target' that is hidden from all 'enemies'.
	 * @param target
	 * @param enemies
	 * @return
	 */
	public VisibilityLocation getNearestCoverPointFromN(ILocated target, ILocated... enemies) {
		if (!isInitialized()) return null;
		return matrix.getNearestCoverPointN(target, enemies);
	}
	
	/**
	 * Returns nearest cover point for BOT that is hidden from all 'enemies'.
	 * @param target
	 * @param enemies
	 * @return
	 */
	public VisibilityLocation getNearestCoverPointN(ILocated... enemies) {
		return getNearestCoverPointFromN(info.getLocation(), enemies);
	}
	
	/**
	 * Returns set of {@link NavPoint} that are not visible from any 'enemies'.
	 * @param enemies
	 * @return
	 */
	public Set<NavPoint> getCoverNavPointsFromN(ILocated... enemies) {
		if (!isInitialized()) return null;
		return matrix.getCoverNavPointsN(enemies);
	}
	
	/**
	 * Returns nearest cover nav point for 'target' that is hidden from all 'enemies'.
	 * @param target
	 * @param enemies
	 * @return
	 */
	public NavPoint getNearestCoverNavPointFromN(ILocated target, ILocated... enemies) {
		if (!isInitialized()) return null;
		return matrix.getNearestCoverNavPointN(target, enemies);
	}
	
	/**
	 * Returns nearest cover nav point for BOT that is hidden from all 'enemies'.
	 * @param enemies
	 * @return
	 */
	public NavPoint getNearestCoverNavPointN(ILocated... enemies) {
		return getNearestCoverNavPointFromN(info.getLocation(), enemies);
	}

		
	//
	// =======================================
	// LIFECYCLE STUFF
	// =======================================
	//
	
	/**
	 * GameInfo listener.
	 */
	private class GameInfoListener implements IWorldObjectEventListener<GameInfo, IWorldObjectEvent<GameInfo>>
	{		
		@Override
		public void notify(IWorldObjectEvent<GameInfo> event)
		{
			lastGameInfo = event.getObject();
		}
		
		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public GameInfoListener(IWorldView worldView)
		{
			worldView.addObjectListener(GameInfo.class, this);
		}
	}
	
	/**
	 * GameInfo listener.
	 */
	private class MapPointListener implements IWorldEventListener<MapPointListObtained>
	{		
		@Override
		public void notify(MapPointListObtained event)
		{
			init(lastGameInfo, event);
		}
		
		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public MapPointListener(IWorldView worldView)
		{
			worldView.addEventListener(MapPointListObtained.class, this);
		}
	}


	GameInfoListener gameInfoListener;

	GameInfo lastGameInfo;
	
	MapPointListener mapPointListener;

	/**
	 * Underlying instance that is used for all getters. Initialized inside {@link Visibility#init(String)} that is called by
	 * {@link MapPointListener} whenever {@link GameInfo} and {@link MapPointListObtained} message is obtained.
	 */
	VisibilityMatrix matrix = null;

	/**
	 * Agent info modul needed.
	 */
	AgentInfo info;
	
	/**
	 * Called from within {@link MapPointListener} whenever {@link GameInfo} and {@link MapPointListObtained} message is obtained.
	 * @param gameInfo
	 * @param event 
	 */
	void init(GameInfo gameInfo, MapPointListObtained event) {
		if (gameInfo == null || event == null) {
			log.warning("Cannot initialize." + (gameInfo == null ? " GameInfo is NULL." : "") + (event == null ? " MapPoints is NULL." : ""));
			return;
		}
		
		Map<UnrealId, NavPoint> navPoints = event.getNavPoints();		
		if (navPoints == null) {
			log.warning("Cannot map visibility locations to NavPoints and Links, nav points not exported by GameBots2004...");
			log.warning("Module cannot be initialized.");
			return;
		}
		
		String mapName = gameInfo.getLevel();
		log.warning("Initializing visibility matrix for map: " + mapName);
		
		File file1 = VisibilityMatrix.getFile_All(new File("."), mapName);
		log.info("Looking for file: " + file1.getAbsolutePath());

		VisibilityMatrix visibilityMatrix = tryToLoadVisibilityMatrix(new File("."), mapName);
		if (visibilityMatrix == null) {
			// try property
			String dir = Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UT2004_VISIBILITY_DIRECTORY.getKey());
			if (dir == null) {
				log.warning("Could not load visibility information for " + mapName + ". File from local dir " + file1.getAbsolutePath() + " not found and property " + PogamutUT2004Property.POGAMUT_UT2004_VISIBILITY_DIRECTORY.getKey() + " not set.");
			} else {
				File dirFile = new File(dir);
				if (dirFile.exists() && dirFile.isDirectory()) {
					File file2 = VisibilityMatrix.getFile_All(dirFile, mapName);
					log.info("Looking for file: " + file2.getAbsolutePath());
					visibilityMatrix = tryToLoadVisibilityMatrix(new File("."), mapName);
					if (visibilityMatrix == null) {
						log.warning("Could not load visibility information for " + mapName + ". File from local dir " + file1.getAbsolutePath() + " not found and file from configured dir " + file2.getAbsolutePath() + " was not found as well.");						
					}
				} else {
					log.warning("Could not load visibility information for " + mapName + ". File from local dir " + file1.getAbsolutePath() + " not found and property " + PogamutUT2004Property.POGAMUT_UT2004_VISIBILITY_DIRECTORY.getKey() + " leads to non-dir " + dirFile.getAbsolutePath() + ".");
				}
			}
		}
		
		if (visibilityMatrix == null) {
			log.warning("Visibility matrix was not loaded, module could not be initialized.");
			return;
		}
		
		log.warning("Visibility matrix loaded successfully.");

		log.info("Mapping navpoints and links...");
		
		for (Entry<Integer, VisibilityLocation> vLocEntry : visibilityMatrix.getLocations().entrySet()) {
			VisibilityLocation vLoc = vLocEntry.getValue();
			if (vLoc.navPoint1Id != null) {
				if (vLoc.navPoint2Id != null) {
					// search for link
					String navPoint1Id = mapName + "." + vLoc.navPoint1Id;
					NavPoint np1 = navPoints.get(UnrealId.get(navPoint1Id));
					if (np1 == null) {
						log.warning("Could not find navpoint (map changed? / old visibility matrix file?): " + navPoint1Id);
						continue;
					}
					String navPoint2Id = mapName + "." + vLoc.navPoint2Id;
					NavPoint np2 = navPoints.get(UnrealId.get(navPoint2Id));
					if (np2 == null) {
						log.warning("Could not find navpoint (map changed? / old visibility matrix file?): " + navPoint2Id);
						continue;
					}
					NavPointNeighbourLink link = np1.getOutgoingEdges().get(np2.getId());
					if (link == null) {
						link = np2.getOutgoingEdges().get(np1.getId());
						log.warning("Could not find navpoint link (map changed? / old visibility matrix file?): " + navPoint1Id + " <-> " + navPoint2Id);
						continue;
					}
					vLoc.link = link;
					continue;
				} else {
					// search for navpoint
					String navPointId = mapName + "." + vLoc.navPoint1Id;
					vLoc.navPoint = navPoints.get(UnrealId.get(navPointId));
					if (vLoc.navPoint == null) {
						log.warning("Could not find navpoint (map changed? / old visibility matrix file?): " + navPointId);
						continue;
					}
					continue;
				}
			} else {
				log.warning("Malformed VisibilityLocation under index " + vLocEntry.getKey() + ", it does not have navPoint1Id specified!");
			}
		}
		
		this.matrix = visibilityMatrix;
		log.warning("Navpoints and links mapped, module is ready to be used.");		
	}
	
	private VisibilityMatrix tryToLoadVisibilityMatrix(File directory, String mapName) {
		File file = VisibilityMatrix.getFile_All(directory, mapName);
		if (file.exists() && file.isFile()) {
			try {
				VisibilityMatrix visibilityMatrix = VisibilityMatrix.load(directory, mapName);
				if (!SafeEquals.equals(mapName, visibilityMatrix.getMapName())) {
					log.warning("Expected to load visibility matrix for map " + mapName + ", but matrix for " + visibilityMatrix.getMapName() + " loaded instead! (Misplaced file?) Module cannot be used.");
					return null;
				}
				return visibilityMatrix;
			} catch (Exception e) {		
				log.warning(ExceptionToString.process("Failed to load visibility matrix from existing file " + file.getAbsolutePath() + ".", e));
			}
		}
		return null;
	}

	/*========================================================================*/

	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module that is using it
	 * @param info {@link AgentInfo} module to be used
	 */
	public Visibility(UT2004Bot bot, AgentInfo info) {
		this(bot, info, null);
	}
	
	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module that is using it
	 * @param info {@link AgentInfo} module to be used
	 * @param log Logger to be used for logging runtime/debug info. If <i>null</i>, the module creates its own logger.
	 */
	public Visibility(UT2004Bot bot, AgentInfo info, Logger log)
	{
		super(bot, log);

		this.info = info;
		NullCheck.check(this.info, "agentInfo");
		
		// create listeners
		gameInfoListener = new GameInfoListener(worldView);
		mapPointListener = new MapPointListener(worldView);
	    
        cleanUp();
	}
	
	@Override
	protected void cleanUp() {
		super.cleanUp();
		matrix = null;
	}
	
}
