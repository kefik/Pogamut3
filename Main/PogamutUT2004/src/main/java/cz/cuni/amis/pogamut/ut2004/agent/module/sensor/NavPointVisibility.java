package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectDisappearedEvent;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Items.ItemPickedUpListener;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.UT2004Draw;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Mover;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Spawn;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.StopWatch;

public class NavPointVisibility extends SensorModule<UT2004Bot> {
	
	/**
	 * Navpoints in this distance are always considered in FOV.
	 */
	private static final double ALL_VISIBLE_DISTANCE = 70;

	/**
	 * Draw current state of nav point visibility (takes quite a lot of time) according what THIS object is thinking (not what is within {@link IWorldView}.
	 * You might also consider doing {@link UT2004Draw#clearAll()} first...
	 * 
	 * @param draw
	 */
	public void drawNavPointVisibility(UT2004Draw draw) {
		for (NavPoint navPoint : worldView.getAll(NavPoint.class).values()) {
			Color color;
			if (isNavPointVisible(navPoint)) {
				color = Color.green;
			} else {
				color = Color.red;
			}
			draw.drawCube(color, navPoint.getLocation(), 15);
		}
	}
	
	/**
	 * Draw current state of nav point visibility (takes quite a lot of time) according what {@link IWorldView} is thinking.
	 * You might also consider doing {@link UT2004Draw#clearAll()} first...
	 * 
	 * @param draw
	 */
	public void drawNavPointVisibilityWorldView(UT2004Draw draw) {
		for (NavPoint navPoint : worldView.getAll(NavPoint.class).values()) {
			Color color;
			if (navPoint.isVisible()) {
				color = Color.green;
			} else {
				color = Color.red;
			}
			draw.drawCube(color, navPoint.getLocation(), 15);
		}
	}
	
	
	/*========================================================================*/
	
	/**
	 * Whether bot vision should be capped by FIELD OF VIEW.
	 * @return
	 */
	private boolean useFieldOfView = true;
	
	/**
	 * Field of view of the bot, used only iff {@link #useFieldOfView}. Check handled in {@link #doVisibilityQueryNavPoint(NavPoint)}.
	 */
	private double fovAngleDeg = 120;
	
	/**
	 * TODO: Item visibility is weird in UT2004!
	 */
	private double fovAngleDegItems = 90;
	
	/**
	 * Whether navpoint vision is capped by the field of view?
	 * @return
	 */
	public boolean isUseFOV() {
		return useFieldOfView;		
	}
	
	/**
	 * Field of view of the bot, used only iff {@link #useFieldOfView}.
	 * @return
	 */
	public double getFOVAngleDeg() {
		return fovAngleDeg;
	}
	
	/**
	 * Is 'location' within the bot's field of view given by 'fovAngleDeg'.
	 * 
	 * Note that locations that are close are always visible regarding the FOV...
	 */
	public boolean isInFOV(ILocated location, double fovAngleDeg) {
		if (location == null) return false;
		Location loc = location.getLocation();
		if (loc == null) return false;
		
		if (info.getLocation().getDistance(loc) < ALL_VISIBLE_DISTANCE) return true;
		
		Location dir = loc.add(info.getLocation().invert()).getNormalized();
		Location heading = info.getRotation().toLocation().getNormalized();
		
		double angleDeg = (Math.acos(heading.dot(dir)) / Math.PI) * 180.0d;
		
		if (angleDeg < -180) angleDeg += 360;
		if (angleDeg > 180) angleDeg -= 360;
		
		if (angleDeg < -fovAngleDeg/2) return false;
		if (angleDeg > fovAngleDeg/2) return false;
		
		return true;
	}
	
	/*========================================================================*/
	
	/**
	 * Whether this module should be updating {@link NavPoint#isVisible()} information within agent's {@link WorldView}
	 * according to {@link #visibilityAdapter} and its {@link IVisibilityAdapter#isVisible(cz.cuni.amis.pogamut.base3d.worldview.object.ILocated, cz.cuni.amis.pogamut.base3d.worldview.object.ILocated)}
	 * querries.
	 * @return
	 */
	private boolean enabled = true;
	
	/**
	 * Whether this module should be updating {@link NavPoint#isVisible()} information within agent's {@link WorldView}
	 * according to {@link #visibilityAdapter} and its {@link IVisibilityAdapter#isVisible(cz.cuni.amis.pogamut.base3d.worldview.object.ILocated, cz.cuni.amis.pogamut.base3d.worldview.object.ILocated)}
	 * querries. This will actually work only iff {@link IVisibilityAdapter#isInitialized()}.
	 * @return
	 */
	public boolean isUpdateWorldView() {
		return enabled;
	}

	/**
	 * Sets whether this module should be updating {@link NavPoint#isVisible()} information within agent's {@link WorldView}
	 * according to {@link #visibilityAdapter} and its {@link IVisibilityAdapter#isVisible(cz.cuni.amis.pogamut.base3d.worldview.object.ILocated, cz.cuni.amis.pogamut.base3d.worldview.object.ILocated)}
	 * querries.
	 * @return
	 */
	public void setUpdateWorldView(boolean updateWorldView) {
		this.enabled = updateWorldView;
	}
	
	/**
	 * Whether this module is usable for navpoint raycasting / visibility testing.
	 * @return
	 */
	public boolean isInitialized() {
		return visibilityAdapter != null && visibilityAdapter.isInitialized();
	}
	
	/*========================================================================*/

	/**
	 * How far we can see {@link NavPoint}s.
	 * @return
	 */
	private double navPointVisionDistance = 2000;
	
	/**
	 * How far we can see {@link NavPoint}s. Max vision distance for {@link NavPoint}s.
	 * @return
	 */
	public double getNavPointVisionDistance() {
		return navPointVisionDistance;
	}

	/**
	 * Sets vision distance for {@link NavPoint}s.
	 * @return
	 */
	public void setNavPointVisionDistance(double navPointVisionDistance) {
		this.navPointVisionDistance = navPointVisionDistance;
	}
	
	/*========================================================================*/

	private Boolean navpointsOff = null;
	
	private Set<NavPoint> visibleNavPoints;
	
	/**
	 * Speeds up {@link #visibleNavPoints} update within {@link #refreshNavPointVisibility()}.
	 */
	private Set<NavPoint> nextVisibleNavPoints;
	
	protected void refreshNavPointVisibility() {
		if (visibilityAdapter == null || !visibilityAdapter.isInitialized()) return;
		
		if (enabled && (navpointsOff == null || !navpointsOff)) {
			// DISABLE SYNCHRONOUS NAVPOINTS
			agent.getAct().act(new Configuration().setSyncNavPointsOff(true));
			navpointsOff = true;
		} else
		if (!enabled && (navpointsOff != null && navpointsOff)) {
			// ENABLE SYNCHRONOUS NAVPOINTS
			agent.getAct().act(new Configuration().setSyncNavPointsOff(false));
			navpointsOff = false;
		}
			
		
		// LOG INFO
		if (log != null && log.isLoggable(Level.FINE)) log.fine("Self update => checking navpoint visibility...");
		StopWatch watch = new StopWatch();
		
		Location myLocation = info.getLocation();
		
		// QUERY VISIBLE NAVPOINTS
		int visibilityQueryCount = 0;
		for (NavPoint navPoint : worldView.getAll(NavPoint.class).values()) {
			double navPointDistance = info.getDistance(navPoint);
			if (navPointDistance > navPointVisionDistance) continue;
			
			visibilityQueryCount += 1;
			if (!doVisibilityQueryNavPoint(navPoint)) continue;

			nextVisibleNavPoints.add(navPoint);
		}
		
		// UPDATE WORLDVIEW
		
		// 1. drop visibility flag for navpoints we cannot see
		int navPointsDisappeared = 0;
		for (NavPoint navPoint : visibleNavPoints) {
			if (nextVisibleNavPoints.contains(navPoint)) continue;
			// navPoint not visible!
			++navPointsDisappeared;
			updateNavPointVisibilityInWorldView(navPoint, false);
		}
		
		// 2. set visibility flag for navpoints we can see
		int navPointsAppeared = 0;
		for (NavPoint navPoint : nextVisibleNavPoints) {
			if (visibleNavPoints.contains(navPoint)) continue;
			// navPoint newly visible!
			++navPointsAppeared;
			updateNavPointVisibilityInWorldView(navPoint, true);
		}		
		
		// LOG
		if (log != null && log.isLoggable(Level.FINER)) {
			log.finer("#Navpoint querries = " + visibilityQueryCount + ", #Appeared = " + navPointsAppeared + ", #Disappeared = " + navPointsDisappeared + ", #TotalVisible = " + nextVisibleNavPoints.size());
			log.finer("Total time: " + watch.stopStr());			
		}
		
		// SWAP VISIBLE NAVPOINTS
		Set<NavPoint> temp = visibleNavPoints;
		visibleNavPoints = nextVisibleNavPoints;
		nextVisibleNavPoints = temp;
		
		// CLEAR PREVIOUS VISIBLE NAVPOINTS TO SAVE MEMORY
		nextVisibleNavPoints.clear();
	}

	/**
	 * Performs visibility query, uses {@link #visibilityAdapter} to check whether 'navPoint' is visible from bot's current location.
	 * @param navPoint
	 * @return
	 */
	private boolean doVisibilityQueryNavPoint(ILocated navPoint) {
		if (useFieldOfView) {
			if (!isInFOV(navPoint, fovAngleDeg)) return false;
		}		
		return visibilityAdapter.isVisible(info.getLocation(), navPoint);
	}
	
	/**
	 * Performs visibility query, uses {@link #visibilityAdapter} to check whether 'item' is visible from bot's current location.
	 * @param item
	 * @return
	 */
	private boolean doVisibilityQueryItem(ILocated item) {
		if (useFieldOfView) {
			if (!isInFOV(item, fovAngleDegItems)) return false;
		}		
		return visibilityAdapter.isVisible(info.getLocation(), item);
	}
	
	private void updateNavPointItemSpawnedInWorldView(NavPoint navPoint, boolean itemSpawned) {
		if (!enabled) {
			// DO NOT UPDATE WORLDVIEW
			return;
		}
		NavPointMessage update = new NavPointMessage(navPoint.getId(), navPoint.getLocation(), navPoint.getVelocity(), itemSpawned ? true : navPoint.isVisible(), navPoint.getItem(), navPoint.getItemClass(), itemSpawned, navPoint.isDoorOpened(), navPoint.getMover(), navPoint.getLiftOffset(), navPoint.isLiftJumpExit(), navPoint.isNoDoubleJump(), navPoint.isInvSpot(), navPoint.isPlayerStart(), navPoint.getTeamNumber(), navPoint.isDomPoint(), navPoint.getDomPointController(), navPoint.isDoor(), navPoint.isLiftCenter(), navPoint.isLiftExit(), navPoint.isAIMarker(), navPoint.isJumpSpot(), navPoint.isJumpPad(), navPoint.isJumpDest(), navPoint.isTeleporter(), navPoint.getRotation(), navPoint.isRoamingSpot(), navPoint.isSnipingSpot(), navPoint.getItemInstance(), navPoint.getOutgoingEdges(), navPoint.getIncomingEdges(), navPoint.getPreferedWeapon());;
		worldView.notifyImmediately(update);
	}
	
	private void updateNavPointVisibilityInWorldView(NavPoint navPoint, boolean navPointVisible) {
		if (!enabled) {
			// DO NOT UPDATE WORLDVIEW
			return;
		}
		boolean itemSpawned = navPoint.isItemSpawned();
		Item item = navPoint.getItem() == null ? null : worldView.get(navPoint.getItem(), Item.class);
		
		if (item != null) {
			if (item.isVisible()) {
				itemSpawned = true;
				navPointVisible = true;				
			}
			else 
			// ITEM NOT VISIBLE
			if (info.getLocation().getDistance(navPoint.getLocation()) > ALL_VISIBLE_DISTANCE) {
				if (doVisibilityQueryItem(navPoint) && doVisibilityQueryItem(item.getLocation())) {
					// NAV POINT VISIBLE, ITEM SHOULD BE VISIBLE AS WELL, BUT GB2004 is reporting the item is not ... consider picked-up
					itemSpawned = false;					
				}
			}
		}
		
		updateNavPointVisibilityInWorldView(navPoint, navPointVisible, itemSpawned);
	}	
	
	private void updateNavPointLocationInWorldView(NavPoint navPoint, Location newLocation) {
		if (!enabled) {
			// DO NOT UPDATE WORLDVIEW
			return;
		}
		NavPointMessage update = new NavPointMessage(navPoint.getId(), newLocation, navPoint.getVelocity(), navPoint.isVisible(), navPoint.getItem(), navPoint.getItemClass(), navPoint.isItemSpawned(), navPoint.isDoorOpened(), navPoint.getMover(), navPoint.getLiftOffset(), navPoint.isLiftJumpExit(), navPoint.isNoDoubleJump(), navPoint.isInvSpot(), navPoint.isPlayerStart(), navPoint.getTeamNumber(), navPoint.isDomPoint(), navPoint.getDomPointController(), navPoint.isDoor(), navPoint.isLiftCenter(), navPoint.isLiftExit(), navPoint.isAIMarker(), navPoint.isJumpSpot(), navPoint.isJumpPad(), navPoint.isJumpDest(), navPoint.isTeleporter(), navPoint.getRotation(), navPoint.isRoamingSpot(), navPoint.isSnipingSpot(), navPoint.getItemInstance(), navPoint.getOutgoingEdges(), navPoint.getIncomingEdges(), navPoint.getPreferedWeapon());
		worldView.notifyImmediately(update);
	}
		
	private void updateNavPointVisibilityInWorldView(NavPoint navPoint, boolean navPointVisible, boolean itemSpawned) {
		if (!enabled) {
			// DO NOT UPDATE WORLDVIEW
			return;
		}
		
		if (navPoint.isVisible() != navPointVisible || navPoint.isItemSpawned() != itemSpawned) {
			if (log.isLoggable(Level.FINE)) {
				if (navPoint.isVisible() == navPointVisible)
				{
					log.fine(navPoint.getId() + "[visible == " + navPointVisible + (navPoint.isInvSpot() ? ", itemSpawned -> "  +itemSpawned : "") + "]");
				} else
				if (navPoint.isItemSpawned() == itemSpawned) {
					log.fine(navPoint.getId() + "[visible -> " + navPointVisible + (navPoint.isInvSpot() ? ", itemSpawned == " + itemSpawned : "") + "]");
				} else {
					log.fine(navPoint.getId() + "[visible -> " + navPointVisible + (navPoint.isInvSpot() ? ", itemSpawned -> " + itemSpawned : "") + "]");	
				}	
			}
		} else {
			// NOTHING TO UPDATE
			return;
		}
		
		NavPointMessage update = new NavPointMessage(navPoint.getId(), navPoint.getLocation(), navPoint.getVelocity(), navPointVisible, navPoint.getItem(), navPoint.getItemClass(), itemSpawned, navPoint.isDoorOpened(), navPoint.getMover(), navPoint.getLiftOffset(), navPoint.isLiftJumpExit(), navPoint.isNoDoubleJump(), navPoint.isInvSpot(), navPoint.isPlayerStart(), navPoint.getTeamNumber(), navPoint.isDomPoint(), navPoint.getDomPointController(), navPoint.isDoor(), navPoint.isLiftCenter(), navPoint.isLiftExit(), navPoint.isAIMarker(), navPoint.isJumpSpot(), navPoint.isJumpPad(), navPoint.isJumpDest(), navPoint.isTeleporter(), navPoint.getRotation(), navPoint.isRoamingSpot(), navPoint.isSnipingSpot(), navPoint.getItemInstance(), navPoint.getOutgoingEdges(), navPoint.getIncomingEdges(), navPoint.getPreferedWeapon());;
		worldView.notifyImmediately(update);
	}
	
	private void dropNavPointVisibilityFlags() {
		// DROP VISIBILITY INFO
		for (NavPoint navPoint : visibleNavPoints) {
			updateNavPointVisibilityInWorldView(navPoint, false, navPoint.isItemSpawned());
		}
		visibleNavPoints.clear();		
	}
	
	/**
	 * Checks whether 'navPoint' is deemed visible according to the {@link #visibilityAdapter} of the class.
	 * @param navPoint
	 * @return
	 */
	public boolean isNavPointVisible(NavPoint navPoint) {
		return visibleNavPoints.contains(navPoint);
	}
	
	/*========================================================================*/

	/**
	 * {@link Self} listener.
	 */
	private class SelfListener implements IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>>
	{
		private IWorldView worldView;

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public SelfListener(IWorldView worldView)
		{
			this.worldView = worldView;
			worldView.addObjectListener(Self.class, WorldObjectUpdatedEvent.class, this);
		}

		@Override
		public void notify(WorldObjectUpdatedEvent<Self> event) {
			if (beginMessage) {
				// SELF WITHIN GB2004 VISIBILITY BATCH
				refreshNavPointVisibility();
				beginMessage = false;
			}
		}
	}

	/** {@link Self} listener */
	private SelfListener selfListener;
	
	/*========================================================================*/
	
	/**
	 * {@link ItemPickedUp} event listener for dropping {@link NavPoint#isItemSpawned()} flag.
	 * @author Jimmy
	 */
	private class ItemPickedUpListener implements IWorldEventListener<ItemPickedUp> {

		public ItemPickedUpListener(IWorldView worldView) {
			worldView.addEventListener(ItemPickedUp.class, this);;
		}
		
		@Override
		public void notify(ItemPickedUp event) {
			if (visibilityAdapter == null || !visibilityAdapter.isInitialized()) return;			
			if (!enabled) return;
			Item item = worldView.get(event.getId(), Item.class);
			if (item == null) return;
			NavPoint np = item.getNavPoint();
			if (np == null) return;
			updateNavPointVisibilityInWorldView(np, np.isVisible(), false);
		}
		
	}
	
	/**
	 * {@link WorldObjectAppearedEvent} for items listener.
	 */
	private class ItemAppearedListener implements IWorldObjectEventListener<Item, WorldObjectAppearedEvent<Item>> {

		public ItemAppearedListener(IWorldView worldView) {
			worldView.addObjectListener(Item.class, WorldObjectAppearedEvent.class, this);;
		}

		@Override
		public void notify(WorldObjectAppearedEvent<Item> event) {
			if (visibilityAdapter == null || !visibilityAdapter.isInitialized()) return;		
			if (!enabled) return;
			NavPoint np = event.getObject().getNavPoint();
			if (np != null) {
				updateNavPointVisibilityInWorldView(np, true, true);
			}
		}
		
	}
	
	/**
	 * {@link WorldObjectDisappearedEvent} for items listener.
	 */
	private class ItemDisappearedListener implements IWorldObjectEventListener<Item, WorldObjectDisappearedEvent<Item>> {

		public ItemDisappearedListener(IWorldView worldView) {
			worldView.addObjectListener(Item.class, WorldObjectDisappearedEvent.class, this);;
		}

		@Override
		public void notify(WorldObjectDisappearedEvent<Item> event) {
			if (visibilityAdapter == null || !visibilityAdapter.isInitialized()) return;			
			if (!enabled) return;
			NavPoint navPoint = event.getObject().getNavPoint();
			if (navPoint != null) {
				if (doVisibilityQueryItem(navPoint)) {
					if (doVisibilityQueryItem(event.getObject().getLocation())) {
						// BOTH NAVPOINT & ITEM SHOULD BE VISIBLE, but ITEM is reported that it is not => was picked up 
						updateNavPointVisibilityInWorldView(navPoint, true, false);
					}
				} else {
					// DROP NAVPOINT VISIBILITY
					if (!doVisibilityQueryNavPoint(navPoint)) {
						updateNavPointVisibilityInWorldView(navPoint, false, navPoint.isItemSpawned());
					}
				}
			}
		}
		
	}
	
	/** {@link ItemPickedUp} event listener. */
	ItemPickedUpListener itemPickedUpListener;
	
	/** {@link WorldObjectAppearedEvent} for items listener. */
	ItemAppearedListener itemAppearedListener;
	
	/** {@link WorldObjectDisappearedEvent} for items listener. */
	ItemDisappearedListener itemDisappearedListener;
	
	/*========================================================================*/

	/**
	 * {@link WorldObjectDisappearedEvent} for items listener.
	 */
	private class MoverUpdatedListener implements IWorldObjectEventListener<Mover, WorldObjectUpdatedEvent<Mover>> {

		public MoverUpdatedListener(IWorldView worldView) {
			worldView.addObjectListener(Mover.class, WorldObjectUpdatedEvent.class, this);;
		}

		@Override
		public void notify(WorldObjectUpdatedEvent<Mover> event) {
			if (visibilityAdapter == null || !visibilityAdapter.isInitialized()) return;			
			if (!enabled) return;
			NavPoint navPoint = worldView.get(event.getObject().getNavPointMarker(), NavPoint.class);
			if (navPoint != null) {
				updateNavPointLocationInWorldView(navPoint, event.getObject().getLocation());
			}
			
		}
		
	}
	
	/** {@link ItemPickedUp} event listener. */
	MoverUpdatedListener moverUpdatedListener;
	
	/*========================================================================*/
		
	/**
	 * {@link BotKilled} listener counting the number of suicides.
	 */
	private class BotKilledListener implements IWorldEventListener<BotKilled> {

		public BotKilledListener(IWorldView worldView) {
			worldView.addEventListener(BotKilled.class, this);
		}
		
		@Override
		public void notify(BotKilled event) {
			dropNavPointVisibilityFlags();
		}
		
	}
	
	/** {@link BotKilled} listener. */
	BotKilledListener botKilledListener;
	
	/*========================================================================*/
	
	/**
	 * {@link ConfigChange} listener counting the number of suicides.
	 */
	private class ConfigChangeListener implements IWorldEventListener<ConfigChange> {

		public ConfigChangeListener(IWorldView worldView) {
			worldView.addEventListener(ConfigChange.class, this);
		}
		
		@Override
		public void notify(ConfigChange event) {
			navpointsOff = event.isSyncNavpoints();
//			fovAngleDeg = event.getVisionFOV();
//			fovAngleDeg -= 10;
//			if (fovAngleDeg < 10) fovAngleDeg = 10;
		}
		
	}
	
	/** {@link ConfigChange} listener. */
	ConfigChangeListener configChangeListener;
	
	/*========================================================================*/
	
	/**
	 * {@link Spawn} listener.
	 */
	private class SpawnListener implements IWorldEventListener<Spawn>
	{
		private IWorldView worldView;

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public SpawnListener(IWorldView worldView)
		{
			
			worldView.addEventListener(Spawn.class, this);
			this.worldView = worldView;
		}

		@Override
		public void notify(Spawn event) {
			dropNavPointVisibilityFlags();
		}
	}

	/** {@link Spawn} listener */
	private SpawnListener spawnListener;
	
	/*========================================================================*/
	
	private boolean beginMessage = false;
	
	/**
	 * {@link BeginMessage} listener.
	 */
	private class BeginMessageListener implements IWorldEventListener<BeginMessage>
	{
		private IWorldView worldView;

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public BeginMessageListener(IWorldView worldView)
		{
			
			worldView.addEventListener(BeginMessage.class, this);
			this.worldView = worldView;
		}

		@Override
		public void notify(BeginMessage event) {
			beginMessage = true;
		}
	}

	/** {@link BeginMessage} listener */
	private BeginMessageListener beginMessageListener;
	
	/*========================================================================*/
	
	private AgentInfo info;
	private IVisibilityAdapter visibilityAdapter;
	
	public NavPointVisibility(UT2004Bot bot, AgentInfo info, IVisibilityAdapter visibilityAdapter) {
		super(bot);
		
		this.info = info;
		this.visibilityAdapter = visibilityAdapter;
	
		NullCheck.check(info, "info");
		NullCheck.check(visibilityAdapter, "visibilityAdapter");
		
		selfListener = new SelfListener(worldView);
		botKilledListener = new BotKilledListener(worldView);
		spawnListener = new SpawnListener(worldView);
		beginMessageListener = new BeginMessageListener(worldView);
		configChangeListener = new ConfigChangeListener(worldView);
		itemPickedUpListener = new ItemPickedUpListener(worldView);		
		itemAppearedListener = new ItemAppearedListener(worldView);
		itemDisappearedListener = new ItemDisappearedListener(worldView);
		moverUpdatedListener = new MoverUpdatedListener(worldView);
		
		visibleNavPoints = new HashSet<NavPoint>();
		nextVisibleNavPoints = new HashSet<NavPoint>();
	}
	
	@Override
	protected void cleanUp() {
		super.cleanUp();

		navpointsOff = null;
		
		if (visibleNavPoints == null) visibleNavPoints = new HashSet<NavPoint>();
		else visibleNavPoints.clear();
		if (nextVisibleNavPoints == null) nextVisibleNavPoints = new HashSet<NavPoint>();
		else nextVisibleNavPoints.clear();
	}
	
}
