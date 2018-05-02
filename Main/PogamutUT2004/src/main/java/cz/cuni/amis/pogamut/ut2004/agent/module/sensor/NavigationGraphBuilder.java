package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.vecmath.Vector3d;

import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotLogicController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.utils.LinkFlag;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * This class can be used to manually improve the navigation graph of the UT2004 by manually adding/removing edges from it.
 * <p><p>
 * Note that NavigationGraphBuilder is automatically prefixing all navpoint ids with "mapName.", which means, that you do not
 * need to specify the id of navpoints as (e.g.) "DM-1on1-Albatross.PathNode2", "PathNode2" suffices. If you want to change this behavior
 * call {@link NavigationGraphBuilder#setAutoPrefix(boolean)} with "false". Autoprefixing is good as it solves the problem with case-sensitivity
 * of navpoint ids (i.e., you may run map dm-1on1-albatross as well as DM-1on1-Albatross!), it also makes the work faster as you do not have to repeat
 * yourself.
 * <p><p>
 * Note that even if auto-prefixing enabled you may prefix ids of navpoints with map name, the auto-prefixing implemented by {@link NavigationGraphBuilder#autoPrefix(String)} 
 * will detects that and auto-correct upper/lower case of this existing prefix if needed. Also you may use it as a validation feature because
 * the {@link NavigationGraphBuilder#autoPrefix(String)} will raise an exception if the prefix does not match the current map name.
 * <p><p>
 * As all {@link SensorModule} it should be initialized in {@link IUT2004BotController#prepareBot(UT2004Bot)}. Note
 * that {@link UT2004BotModuleController} has it auto-initialized inside {@link UT2004BotModuleController#initializeModules(UT2004Bot)}.
 * <p><p>
 * Best utilized in {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)} method.
 * 
 * 
 * @author Jimmy
 *
 */
public class NavigationGraphBuilder extends SensorModule<UT2004Bot> {

	/**
	 * Builder encloses the creation of the new navpoint.
	 * <p><p>
	 * You must call {@link NewNavPointBuilder#createNavPoint()} after you set it up to truly create the navpoint inside bot's {@link IWorldView}.
	 * 
	 * @author Jimmy
	 */
	public class NewNavPointBuilder {
		
		private String id;
		private Location location;
		private List<NewNavPointEdgeBuilder> edges = new ArrayList<NewNavPointEdgeBuilder>();

		protected NewNavPointBuilder() {			
		}
		
		/**
		 * Sets the ID to be used for the new navpoint, corresponds to {@link NavPoint#getId()}.
		 * @param id will be auto-prefixed (if enabled, which is default)
		 * @return
		 */
		public NewNavPointBuilder setId(String id) {
			NullCheck.check(id, "id");
			this.id = autoPrefix(id);
			return this;
		}
		
		/**
		 * Sets the location of the new navpoint, corresponds to {@link NavPoint#getLocation()}.
		 * 
		 * @param x
		 * @param y
		 * @param z
		 * @return
		 */
		public NewNavPointBuilder setLocation(double x, double y, double z) {
			this.location = new Location(x,y,z);
			return this;
		}
		
		/**
		 * Finalizing method that will insert the navpoint into the underlying {@link IWorldView} of the bot.
		 * <p><p>
		 * You must have ID/Location set via {@link NewNavPointBuilder#setId(String)} and {@link NewNavPointBuilder#setLocation(double, double, double)}
		 * otherwise an exception will be thrown.
		 * <p><p>
		 * WARNING: IF USED INSIDE THE {@link IUT2004BotLogicController#logic()}, THAN THE NEW NAVPOINT WON'T BE ACCESSIBLE IMMEDIATELLY, IT WILL BE ACCESSIBLE FROM THE NEXT LOGIC ITERATION!
		 */
		public void createNavPoint() {
			if (id == null) throw new PogamutException("Could not create navpoint, id is null, you must set it using setId() before calling this method.", this);
			if (location == null) throw new PogamutException("Could not create navpoint (" + id + "), location is null, you must set it using setLocation() before calling this method.", this);
			NavPoint newNavPoint = 
				new NavPointMessage(UnrealId.get(id), location, null, false, null, null, false, false, null, null, false, false, false, false, 255, false, 255, false, false, false, false, false, false, false, false, new Rotation(0,0,0), false, false, null, new HashMap<UnrealId, NavPointNeighbourLink>(), new HashMap<UnrealId, NavPointNeighbourLink>(), null);
			
			for (NewNavPointEdgeBuilder edge : edges) {
				Object np = agent.getWorldView().get(edge.toNavPointId);
				if (np == null) {
					throw new PogamutException("Could not create navpoint (" + id + ") as the remote end (" + edge.toNavPointId + ") of one of its edges could not be found in the bot's worldview. Warning, id is case-sensitive the upper/lower cases of the id depends on the concrete spelling of the map that was passed to the GB2004 during startup (either from the command line or by the UT2004).", this);
				}
				if (!(np instanceof NavPoint)) {
					throw new PogamutException("Could not create navpoint (" + id + ") as the remote end (" + edge.toNavPointId + ") of one of its edges is not an instance of NavPoint, but " + np.getClass().getSimpleName() + ". Wrong id used?", this);
				}
				NavPoint toNavPoint = (NavPoint)np; 
				NavPointNeighbourLink link = new NavPointNeighbourLink(UnrealId.get(id), edge.flags, edge.collisionR, edge.collisionH, 0, null, false, edge.forceDoubleJump, edge.neededJump, false, false, 0, newNavPoint, toNavPoint);
				newNavPoint.getOutgoingEdges().put(link.getToNavPoint().getId(), link);
				link.getToNavPoint().getIncomingEdges().put(newNavPoint.getId(), link);
			}
			
			agent.getWorldView().notifyImmediately(newNavPoint);
		}
		
		/**
		 * Creates new edge builder for the navpoint you're creating.
		 * <p><p>
		 * After setting up the edge properties, use {@link NewNavPointEdgeBuilder#createEdge()} to return to this object (and truly creates the edge!).
		 * 
		 * @return edge builder
		 */
		public NewNavPointEdgeBuilder<NewNavPointBuilder> newEdge() {
			return new NewNavPointEdgeBuilder<NewNavPointBuilder>(this);
		}
		
		/**
		 * Creates new edge (to 'navPointId') builder for the navpoint you're creating.
		 * <p><p>
		 * After setting up the edge properties, use {@link NewNavPointEdgeBuilder#createEdge()} to return to this object (and truly creates the edge!).
		 * 
		 * @param navPointId will be auto-prefixed
		 * @return edge builder
		 */
		public NewNavPointEdgeBuilder<NewNavPointBuilder> newEdgeTo(String navPointId) {
			NullCheck.check(navPointId, "navPointId");
			NewNavPointEdgeBuilder<NewNavPointBuilder> edgeBuilder = new NewNavPointEdgeBuilder<NewNavPointBuilder>(this);
			edgeBuilder.setTo(navPointId);
			return edgeBuilder;
		}
		
		/**
		 * Creates simple edge that leads from the navpoint you're currently creating to 'navPointId'
		 * @param navPointId will be auto-prefixed (if enabled, which is default)
		 */
		public void createSimpleEdgeTo(String navPointId) {
			NullCheck.check(navPointId, "navPointId");
			newEdgeTo(navPointId).createEdge();
		}

		/**
		 * Adds new edge into {@link NewNavPointBuilder#edges}. Their true creation is postponed until {@link NewNavPointBuilder#createNavPoint()}.
		 * 
		 * @param newNavPointEdgeBuilder
		 */
		protected void addEdge(NewNavPointEdgeBuilder newNavPointEdgeBuilder) {
			this.edges.add(newNavPointEdgeBuilder);
		}
		
	}
	
	/**
	 * Represents the edge of the navpoint you're newly creating.
	 * <p><p>
	 * WARNING: the created edge is oriented! Its counterpart (from the remote navpoint to newly created one) must be created manually! (If needed.)
	 * 
	 * @author Jimmy
	 */
	public class NewNavPointEdgeBuilder<OWNER> {
		
		protected OWNER owner;
		protected UnrealId toNavPointId;
		protected int collisionR = (int)UnrealUtils.CHARACTER_COLLISION_RADIUS * 2;
		protected int collisionH = (int)UnrealUtils.CHARACTER_HEIGHT_STANDING + 10;
		protected boolean forceDoubleJump = false;
		protected Vector3d neededJump;
		protected int flags = 0;
		
		protected NewNavPointEdgeBuilder(OWNER owner) {
			this.owner = owner;	
			NullCheck.check(this.owner, "owner");
		}
		
		/**
		 * Sets the remote end of the edge (i.e., navpoint id where the edge is leading to), corresponds to {@link NavPointNeighbourLink#getToNavPoint()}.
		 * 
		 * @param navPointId will be auto-prefixed (if enabled, which is default)
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setTo(String navPointId) {
			NullCheck.check(navPointId, "navPointId");
			this.toNavPointId = UnrealId.get(autoPrefix(navPointId));
			return this;
		}
		
		/**
		 * Sets the remote end of the edge (i.e., navpoint id where the edge is leading to), corresponds to {@link NavPointNeighbourLink#getToNavPoint()}.
		 * 
		 * @param navPointId WON'T BE AUTO-PREFIXED AS IT IS ALREADY EXISTING ID!!!
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setTo(UnrealId navPointId) {
			this.toNavPointId = navPointId;
			return this;
		}
		
		/**
		 * Sets collision radius of the edge, corresponds to {@link NavPointNeighbourLink#getCollisionR()}.
		 * 
		 * @param collisionRadius
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setCollisionRadius(int collisionRadius) {
			this.collisionR = collisionRadius;
			return this;
		}
		
		/**
		 * Sets collision height of the edge, corresponds to {@link NavPointNeighbourLink#getCollisionH()}.
		 * 
		 * @param collisionHeight
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setCollisionHeight(int collisionHeight) {
			this.collisionH = collisionHeight;
			return this;
		}
		
		/**
		 * Sets the location from where the bot should jump to reach the target, corresponds to {@link NavPointNeighbourLink#getNeededJump()}.
		 * 
		 * @param x
		 * @param y
		 * @param z
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setNeededJump(double x, double y, double z) {
			this.neededJump = new Vector3d(x,y,z);
			return this;
		}
		
		/**
		 * Sets the flag "double jump is needed" to true, corresponds to {@link NavPointNeighbourLink#isForceDoubleJump()}.
		 * 
		 * @param neededJump
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setDoubleJump() {
			this.forceDoubleJump = true;
			return this;
		}
		
		
		/**
		 * Sets {@link LinkFlag#WALK} flag into {@link NewNavPointEdgeBuilder#flags} of the new navpoint edge,
		 * corresponds to {@link NavPointNeighbourLink#getFlags()}.
		 * 
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setWalkFlag() {
			flags |= LinkFlag.WALK.get();
			return this;
		}
		
		/**
		 * Sets {@link LinkFlag#FLY} flag into {@link NewNavPointEdgeBuilder#flags} of the new navpoint edge,
		 * corresponds to {@link NavPointNeighbourLink#getFlags()}.
		 * 
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setFlyFlag() {
			flags |= LinkFlag.FLY.get();
			return this;
		}
		
		/**
		 * Sets {@link LinkFlag#SWIM} flag into {@link NewNavPointEdgeBuilder#flags} of the new navpoint edge,
		 * corresponds to {@link NavPointNeighbourLink#getFlags()}.
		 * 
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setSwimFlag() {
			flags |= LinkFlag.SWIM.get();
			return this;
		}
		
		/**
		 * Sets {@link LinkFlag#JUMP} flag into {@link NewNavPointEdgeBuilder#flags} of the new navpoint edge,
		 * corresponds to {@link NavPointNeighbourLink#getFlags()}.
		 * 
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setJumpFlag() {
			flags |= LinkFlag.JUMP.get();
			return this;
		}
		
		/**
		 * Sets {@link LinkFlag#DOOR} flag into {@link NewNavPointEdgeBuilder#flags} of the new navpoint edge,
		 * corresponds to {@link NavPointNeighbourLink#getFlags()}.
		 * 
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setDoorFlag() {
			flags |= LinkFlag.DOOR.get();
			return this;
		}
		
		/**
		 * Sets {@link LinkFlag#SPECIAL} flag into {@link NewNavPointEdgeBuilder#flags} of the new navpoint edge,
		 * corresponds to {@link NavPointNeighbourLink#getFlags()}.
		 * 
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setSpecialFlag() {
			flags |= LinkFlag.SPECIAL.get();
			return this;
		}
		
		/**
		 * Sets {@link LinkFlag#LADDER} flag into {@link NewNavPointEdgeBuilder#flags} of the new navpoint edge,
		 * corresponds to {@link NavPointNeighbourLink#getFlags()}.
		 * 
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setLadderFlag() {
			flags |= LinkFlag.LADDER.get();
			return this;
		}
		
		/**
		 * Sets {@link LinkFlag#PROSCRIBED} flag into {@link NewNavPointEdgeBuilder#flags} of the new navpoint edge,
		 * corresponds to {@link NavPointNeighbourLink#getFlags()}.
		 * 
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setProscribedFlag() {
			flags |= LinkFlag.PROSCRIBED.get();
			return this;
		}
		
		/**
		 * Sets {@link LinkFlag#FORCED} flag into {@link NewNavPointEdgeBuilder#flags} of the new navpoint edge,
		 * corresponds to {@link NavPointNeighbourLink#getFlags()}.
		 * 
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setForcedFlag() {
			flags |= LinkFlag.FORCED.get();
			return this;
		}
		
		/**
		 * Sets {@link LinkFlag#PLAYERONLYK} flag into {@link NewNavPointEdgeBuilder#flags} of the new navpoint edge,
		 * corresponds to {@link NavPointNeighbourLink#getFlags()}.
		 * 
		 * @return
		 */
		public NewNavPointEdgeBuilder<OWNER> setPlayerOnlyFlag() {
			flags |= LinkFlag.PLAYERONLY.get();
			return this;
		}
		
		/**
		 * Finalizes the creation of the edge.
		 * <p><p>
		 * Edge remote end must be set via {@link NewNavPointEdgeBuilder#setTo(String)} before otherwise an exception is thrown.
		 * <p><p>
		 * WARNING: the created edge is oriented! Its counterpart (from the remote navpoint to newly created one) must be created manually! (If needed.)
		 * 
		 * @return nav point the edge is going from (i.e., one you're building)
		 */
		public OWNER createEdge() {
			if (toNavPointId == null) {
				throw new PogamutException("Could not create edge - toNavPoint not specified, you must call setTo() with non-null argument (to specify the other end of the edge) before calling this method.", this);
			}
			((NewNavPointBuilder)owner).addEdge(this);
			return ((OWNER)owner);
		}

	}
	
	/**
	 * Builder that allows you to modify edges of existing navpoint using {@link ExistingNavPointModifier#createEdge()}, 
	 * {@link ExistingNavPointModifier#createEdgeTo(String)}, {@link ExistingNavPointModifier#modifyEdgeTo(String)} methods.
	 * 
	 * @author Jimmy
	 */
	public class ExistingNavPointModifier {
		
		private NavPoint navPoint;
		
		private double x;
		private double y;
		private double z;

		protected ExistingNavPointModifier(NavPoint navPoint) {
			this.navPoint = navPoint;
			this.x = navPoint.getLocation().x;
			this.y = navPoint.getLocation().y;
			this.z = navPoint.getLocation().z;
			NullCheck.check(this.navPoint, "navPoint");
		}
		
		public ExistingNavPointModifier addX(double deltaX) {
			x += deltaX;
			return this;
		}
		
		public ExistingNavPointModifier addY(double deltaY) {
			y += deltaY;
			return this;
		}
		
		public ExistingNavPointModifier addZ(double deltaZ) {
			z += deltaZ;
			return this;
		}
		
		public void apply() {
			if (!(navPoint instanceof NavPointMessage)) {
				throw new PogamutException("Could not alter NavPoint[" + navPoint.getId().getStringId() + "] as it is not of type NavPointMessage.", this);
			}
			NavPointMessage np = (NavPointMessage)navPoint;
			try {
				Field f = np.getClass().getDeclaredField("Location");
				f.setAccessible(true);
				Location l = new Location(x, y, z);
				f.set(np, l);
			} catch (Exception e) {
				throw new PogamutException("Failed to modify NavPoint[" + navPoint.getId().getStringId() + "].", e, this);
			}
			
		}
		
		/**
		 * Removes edge that is leading from this navpoint to 'navPointId'. Does not do anything if such edge does not exist.
		 * <p><p>
		 * Removes only one edge, if the edge with opposite direction exists, it leaves it there.
		 * 
		 * @param navPointId will be auto-prefixed (if enabled, which is default)
		 */
		public void removeEdgeTo(String navPointId) {
			NullCheck.check(navPointId, "navPointId");
			navPointId = autoPrefix(navPointId);
			UnrealId navPointUnrealId = UnrealId.get(navPointId);
			navPoint.getOutgoingEdges().remove(navPointUnrealId);
			Object np = agent.getWorldView().get(navPointUnrealId);
			if (np != null && (np instanceof NavPoint)) {
				((NavPoint)np).getIncomingEdges().remove(navPoint.getId());
			}
		}
		
		/**
		 * Removes edge that is leading from this navpoint to 'navPointId'. Does not do anything if such edge does not exist.
		 * <p><p>
		 * Removes both edges, if the edge with opposite direction exists, it is deleted as well.
		 * 
		 * @param navPointId will be auto-prefixed (if enabled, which is default)
		 */
		public void removeEdgesBetween(String navPointId) {
			NullCheck.check(navPointId, "navPointId");
			navPointId = autoPrefix(navPointId);
			UnrealId toNavPointUnrealId = UnrealId.get(navPointId);
			
			Object np = agent.getWorldView().get(toNavPointUnrealId);
			NavPoint toNavPoint = null;
			if (np != null && (np instanceof NavPoint)) {
				toNavPoint = (NavPoint)np;
			}
			
			navPoint.getOutgoingEdges().remove(toNavPointUnrealId);
			navPoint.getIncomingEdges().remove(toNavPointUnrealId);
			
			if (toNavPoint != null) {
				toNavPoint.getOutgoingEdges().remove(navPoint.getId());
				toNavPoint.getIncomingEdges().remove(navPoint.getId());
			}
		}
		
		/**
		 * Returns a builder that will allow you to modify properties of the edge that is leading to 'navPointId'.
		 * <p><p>
		 * If no previous edge (leading to the same navpoint) exists, new one is created automatically.
		 * <p><p>
		 * Call {@link ExistingNavPointEdgeBuilder#modifyEdge()} when done specifying edge properties.
		 * 
		 * @param navPointId will be auto-prefixed (if enabled, which is default)
		 * @return
		 */
		public ExistingNavPointEdgeBuilder modifyEdgeTo(String navPointId) {
			NullCheck.check(navPointId, "navPointId");
			navPointId = autoPrefix(navPointId);
			UnrealId navPointUnrealId = UnrealId.get(navPointId);
			NavPointNeighbourLink link = navPoint.getOutgoingEdges().get(navPointUnrealId);
			if (link != null) {
				return new ExistingNavPointEdgeBuilder(this, link);
			} else {
				return new ExistingNavPointEdgeBuilder(this).setTo(navPointId);
			}
		}
		
		/**
		 * Returns a builder that will allow you to create new edge that is leading to 'navPointId'. Note that the returned {@link ExistingNavPointEdgeBuilder}
		 * will have only {@link ExistingNavPointEdgeBuilder#setTo(String)} filled. It also has different behavior than in the case
		 * of {@link ExistingNavPointModifier#modifyEdgeTo(String)} as this new edge builder won't have any properties (except {@link ExistingNavPointEdgeBuilder#setTo(String)}) filled.
		 * <p><p>
		 * If same edge (leading to the same navpoint) exists, it is replaced automatically.
		 * <p><p>
		 * Call {@link ExistingNavPointEdgeBuilder#createEdge()} when done specifying edge properties.
		 * 
		 * @param navPointId will be auto-prefixed (if enabled, which is default)
		 * @return
		 */
		public ExistingNavPointEdgeBuilder createEdgeTo(String navPointId) {
			NullCheck.check(navPointId, "navPointId");
			navPointId = autoPrefix(navPointId);
			ExistingNavPointEdgeBuilder builder = new ExistingNavPointEdgeBuilder(this, null);
			builder.setTo(navPointId);
			return builder;
		}
		
		/**
		 * Returns a builder that will allow you to create new edge. Note that the returned {@link ExistingNavPointEdgeBuilder}
		 * is empty, you must set the remote end of the edge manually using {@link ExistingNavPointEdgeBuilder#setTo(String)}.
		 * It also has different behavior than in the case
		 * of {@link ExistingNavPointModifier#modifyEdgeTo(String)} as this new edge builder won't have any properties (except {@link ExistingNavPointEdgeBuilder#setTo(String)}) filled.
		 * <p><p>
		 * If same edge (leading to the same navpoint) exists, it is replaced automatically.
		 * <p><p>
		 * Call {@link ExistingNavPointEdgeBuilder#createEdge()} when done specifying edge properties.
		 * 
		 * @return
		 */
		public ExistingNavPointEdgeBuilder createEdge() {
			return new ExistingNavPointEdgeBuilder(this, null);
		}
		
		/**
		 * Creates simple edge that leads from the navpoint you're currently creating to 'navPointId'.
		 * <p><p>
		 * If edge exists, it won't be modified.
		 * 
		 * @param navPointId will be auto-prefixed (if enabled, which is default)
		 */
		public void createSimpleEdgeTo(String navPointId) {
			NullCheck.check(navPointId, "navPointId");
			createEdgeTo(navPointId).createEdge();
		}
		
		/**
		 * Creates two simple edges between this navpoint and the navpoint with 'navPointId'.
		 * <p><p>
		 * If any of those two edges exists, it won't be modified.
		 * 
		 * @param navPointId will be auto-prefixed (if enabled, which is default)
		 */
		public void createSimpleEdgesBetween(String navPointId) {
			NullCheck.check(navPointId, "navPointId");
			createEdgeTo(navPointId).createEdge();
			modifyNavPoint(navPointId).createSimpleEdgeTo(navPoint.getId().getStringId());
		}
		
	}
	
	public class ExistingNavPointEdgeBuilder extends NewNavPointEdgeBuilder<ExistingNavPointModifier> {

		private NavPointNeighbourLink parentLink;

		protected ExistingNavPointEdgeBuilder(ExistingNavPointModifier navPointModifier) {
			super(navPointModifier);
		}
		
		protected ExistingNavPointEdgeBuilder(ExistingNavPointModifier navPointModifier, NavPointNeighbourLink parent) {
			super(navPointModifier);
			this.parentLink = parent;
			if (this.parentLink != null) {
				this.collisionH = this.parentLink.getCollisionH();
				this.collisionR = this.parentLink.getCollisionR();
				this.flags = this.parentLink.getFlags();
				this.forceDoubleJump = this.parentLink.isForceDoubleJump();
				this.neededJump = this.parentLink.getNeededJump();
				this.toNavPointId = this.parentLink.getToNavPoint().getId();
			}
		}
		
		@Override
		public ExistingNavPointEdgeBuilder setTo(String navPointId) {
			super.setTo(navPointId);
			return this;
		}
		
		@Override
		public ExistingNavPointEdgeBuilder setTo(UnrealId navPointId) {
			super.setTo(navPointId);
			return this;
		}
		
		@Override
		public ExistingNavPointEdgeBuilder setCollisionRadius(int collisionRadius) {
			super.setCollisionRadius(collisionRadius);
			return this;
		}
		
		@Override
		public ExistingNavPointEdgeBuilder setCollisionHeight(int collisionHeight) {
			super.setCollisionHeight(collisionHeight);
			return this;
		}
		
		@Override
		public ExistingNavPointEdgeBuilder setNeededJump(double x, double y, double z) {
			super.setNeededJump(x, y, z);
			return this;
		}
		
		/**
		 * Removes "needed jump at location" from the edge.
		 * @return
		 */
		public ExistingNavPointEdgeBuilder removeNeededJump() {
			this.neededJump = null;
			return this;
		}
		
		@Override
		public ExistingNavPointEdgeBuilder setDoubleJump() {
			super.setDoubleJump();
			return this;
		}
		
		/**
		 * Removes "requires double jump" from the edge.
		 * @return
		 */
		public ExistingNavPointEdgeBuilder removeDoubleJump() {
			this.forceDoubleJump = false;
			return this;
		}
		
		
		@Override
		public ExistingNavPointEdgeBuilder setWalkFlag() {
			super.setWalkFlag();
			return this;
		}
		
		/**
		 * Removes {@link LinkFlag#WALK} flag from edge flags.
		 * 
		 * @return
		 */
		public ExistingNavPointEdgeBuilder removeWalkFlag() {
			this.flags = (this.flags | LinkFlag.WALK.get()) ^ LinkFlag.WALK.get(); 
			return this;
		}
		
		@Override
		public ExistingNavPointEdgeBuilder setFlyFlag() {
			super.setFlyFlag();
			return this;
		}
		
		/**
		 * Removes {@link LinkFlag#FLY} flag from edge flags.
		 * 
		 * @return
		 */
		public ExistingNavPointEdgeBuilder removeFlyFlag() {
			this.flags = (this.flags | LinkFlag.FLY.get()) ^ LinkFlag.FLY.get(); 
			return this;
		}
		
		@Override
		public ExistingNavPointEdgeBuilder setSwimFlag() {
			super.setSwimFlag();
			return this;
		}
		
		/**
		 * Removes {@link LinkFlag#SWIM} flag from edge flags.
		 * 
		 * @return
		 */
		public ExistingNavPointEdgeBuilder removeSwimFlag() {
			this.flags = (this.flags | LinkFlag.SWIM.get()) ^ LinkFlag.SWIM.get(); 
			return this;
		}
		
		@Override
		public ExistingNavPointEdgeBuilder setJumpFlag() {
			super.setJumpFlag();
			return this;
		}
		
		/**
		 * Removes {@link LinkFlag#JUMP} flag from edge flags.
		 * 
		 * @return
		 */
		public ExistingNavPointEdgeBuilder removeJumpFlag() {
			this.flags = (this.flags | LinkFlag.JUMP.get()) ^ LinkFlag.JUMP.get(); 
			return this;
		}
		
		@Override
		public ExistingNavPointEdgeBuilder setDoorFlag() {
			super.setDoorFlag();
			return this;
		}
		
		/**
		 * Removes {@link LinkFlag#DOOR} flag from edge flags.
		 * 
		 * @return
		 */
		public ExistingNavPointEdgeBuilder removeDoorFlag() {
			this.flags = (this.flags | LinkFlag.DOOR.get()) ^ LinkFlag.DOOR.get(); 
			return this;
		}
		
		@Override
		public ExistingNavPointEdgeBuilder setSpecialFlag() {
			super.setSpecialFlag();
			return this;
		}
		
		/**
		 * Removes {@link LinkFlag#SPECIAL} flag from edge flags.
		 * 
		 * @return
		 */
		public ExistingNavPointEdgeBuilder removeSpecialFlag() {
			this.flags = (this.flags | LinkFlag.SPECIAL.get()) ^ LinkFlag.SPECIAL.get(); 
			return this;
		}
		
		@Override
		public ExistingNavPointEdgeBuilder setLadderFlag() {
			super.setLadderFlag();
			return this;
		}
		
		/**
		 * Removes {@link LinkFlag#LADDER} flag from edge flags.
		 * 
		 * @return
		 */
		public ExistingNavPointEdgeBuilder removeLadderFlag() {
			this.flags = (this.flags | LinkFlag.LADDER.get()) ^ LinkFlag.LADDER.get(); 
			return this;
		}
		
		@Override
		public ExistingNavPointEdgeBuilder setProscribedFlag() {
			super.setProscribedFlag();
			return this;
		}
		
		/**
		 * Removes {@link LinkFlag#PROSCRIBED} flag from edge flags.
		 * 
		 * @return
		 */
		public ExistingNavPointEdgeBuilder removeProscribedFlag() {
			this.flags = (this.flags | LinkFlag.PROSCRIBED.get()) ^ LinkFlag.PROSCRIBED.get(); 
			return this;
		}
		
		@Override
		public ExistingNavPointEdgeBuilder setForcedFlag() {
			super.setForcedFlag();
			return this;
		}
		
		/**
		 * Removes {@link LinkFlag#FORCED} flag from edge flags.
		 * 
		 * @return
		 */
		public ExistingNavPointEdgeBuilder removeForcedFlag() {
			this.flags = (this.flags | LinkFlag.FORCED.get()) ^ LinkFlag.FORCED.get(); 
			return this;
		}
		
		@Override
		public ExistingNavPointEdgeBuilder setPlayerOnlyFlag() {
			super.setPlayerOnlyFlag();
			return this;
		}
		
		/**
		 * Removes {@link LinkFlag#PLAYERONLY} flag from edge flags.
		 * 
		 * @return
		 */
		public ExistingNavPointEdgeBuilder removePlayerOnlyFlag() {
			this.flags = (this.flags | LinkFlag.PLAYERONLY.get()) ^ LinkFlag.PLAYERONLY.get(); 
			return this;
		}
		
		public ExistingNavPointEdgeBuilder setFlags(Integer flags) {
			this.flags = flags;
			return this;
		}
		
		/**
		 * Clears all flags to 0.
		 * 
		 * @return
		 */
		public ExistingNavPointEdgeBuilder clearFlags() {
			this.flags = 0;
			return this;
		}
		
		/**
		 * Immediately creates a new edge. Checks whether the same edge does not already exist (if so, replaces it).
		 * <p><p>
		 * WARNING: the created edge is oriented! Its counterpart (from the remote navpoint to one that is being modified) must be created manually! (If needed.)
		 *
		 * @return previously used navpoint modifier
		 */
		@Override
		public ExistingNavPointModifier createEdge() {
			if (toNavPointId == null) {
				throw new PogamutException("Could not create/modify edge from navpoint '" + owner.navPoint.getId().getStringId() + "' as toNavPoint not specified, you must call setTo() with non-null argument (to specify the other end of the edge) before calling this method.", this);
			}
			Object np = agent.getWorldView().get(toNavPointId);
			if (np == null) {
				throw new PogamutException("Could not create/modify navpoint edge from '" + owner.navPoint.getId().getStringId() + "' as the remote end (" + toNavPointId.getStringId() + ") could not be found in the bot's worldview. Warning, id is case-sensitive the upper/lower cases of the id depends on the concrete spelling of the map that was passed to the GB2004 during startup (either from the command line or by the UT2004).", this);
			}
			if (!(np instanceof NavPoint)) {
				throw new PogamutException("Could not create/modify navpoint edge from '" + owner.navPoint.getId().getStringId() + "' as the remote end '" + toNavPointId.getStringId() + "' is not an instance of NavPoint but " + np.getClass().getSimpleName() + ". Wrong id specified?", this);
			}
			
			NavPoint toNavPoint = (NavPoint)np;
			
			NavPointNeighbourLink link = null;
			
			if (parentLink == null) {
				link = new NavPointNeighbourLink(owner.navPoint.getId(), flags, collisionR, collisionH, 0, null, false, forceDoubleJump, neededJump, false, false, 0, owner.navPoint, toNavPoint);				
			} else {
				link = new NavPointNeighbourLink(owner.navPoint.getId(), flags, collisionR, collisionH, parentLink.getTranslocZOffset(), parentLink.getTranslocTargetTag(), parentLink.isOnlyTranslocator(), forceDoubleJump, neededJump, parentLink.isNeverImpactJump(), parentLink.isNoLowGrav(), parentLink.getCalculatedGravityZ(), owner.navPoint, toNavPoint);
			}
			
			owner.navPoint.getOutgoingEdges().put(link.getToNavPoint().getId(), link);
			link.getToNavPoint().getIncomingEdges().put(owner.navPoint.getId(), link);
			
			return owner;
		}
		
		/**
		 * Alias for {@link ExistingNavPointEdgeBuilder#createEdge()}. 
		 * <p><p>
		 * WARNING: the modify edge is oriented! Its counterpart (from the remote navpoint to one that is being modifier) must be modified manually! (If needed.)
		 *
		 * @return previously used navpoint modifier
		 */
		public ExistingNavPointModifier modifyEdge() {
			return createEdge();
		}

	}
	
	/**
	 * GameInfo listener.
	 */
	private class GameInfoListener implements IWorldObjectEventListener<GameInfo, IWorldObjectEvent<GameInfo>>
	{
		@Override
		public void notify(IWorldObjectEvent<GameInfo> event)
		{
			lastGameInfo = event.getObject();
			if (lastGameInfo.getLevel() == null) {
				throw new PogamutException("GameInfo.getLevel() is null!!!", this);
			}
			mapNameLowerChar = lastGameInfo.getLevel().toLowerCase();
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

	/** GameInfo listener */
	GameInfoListener gameInfoListener;
	
	/** Las info about the game the bot is in */
	GameInfo lastGameInfo = null;
	
	String mapNameLowerChar = null;
	
	public NavigationGraphBuilder(UT2004Bot bot) {
		this(bot, null);
	}
	
	public NavigationGraphBuilder(UT2004Bot bot, Logger log) {
		this(bot, log, null);
	}
	
	public NavigationGraphBuilder(UT2004Bot bot, Logger log, ComponentDependencies dependencies) {
		super(bot, log, dependencies);
		gameInfoListener = new GameInfoListener(bot.getWorldView());
	}
	
	@Override
	protected void cleanUp() {
		super.cleanUp();
		lastGameInfo = null;
		mapNameLowerChar = null;
	}
	
	/**
	 * Returns name of the map the UT2004 is currently running.
	 * <p><p>
	 * The name is used as a prefix to all IDs in the game. IDs in the world view are case-sensitive!
	 * <p><p>
	 * Note that NavigationGraphBuilder is automatically prefixing all navpoint ids with "mapName.", which means, that you do not
	 * need to specify the id of navpoints as (e.g.) "DM-1on1-Albatross.PathNode2", "PathNode2" suffices. If you want to change this behavior
	 * call {@link NavigationGraphBuilder#setAutoPrefix(boolean)} with "false".
	 * 
	 * @return
	 */
	public String getMapName() {
		if (lastGameInfo == null) return null;
		return lastGameInfo.getLevel();
	}
	
	/**
	 * Tells, whether the UT2004 is currently running map with name 'name'.
	 * @param name
	 * @return
	 */
	public boolean isMapName(String name) {
		if (lastGameInfo == null) return false;
		if (name == null) return false;
		return lastGameInfo.getLevel().toLowerCase().equals(name.toLowerCase());
	}
	
	private boolean autoPrefix = true;

	/**
	 * Whether this instance has been used to alter the navigation graph.
	 */
	private boolean used;
	
	/**
	 * Whether {@link NavigationGraphBuilder} is auto prefixing all navpoint ids with current map name.
	 * <p><p>
	 * As default, auto-prefixing is enabled.
	 * <p><p>
     * Note that even if auto-prefixing enabled you may prefix ids of navpoints with map name, the auto-prefixing implemented by {@link NavigationGraphBuilder#autoPrefix(String)} 
     * will detects that and auto-correct upper/lower case of this existing prefix if needed. Also you may use it as a validation feature because
     * the {@link NavigationGraphBuilder#autoPrefix(String)} will raise an exception if the prefix does not match the current map name.
     * 
	 * @return whether auto-prefixing is enabled
	 */
	public boolean isAutoPrefix() {
		return autoPrefix;
	}

	/**
	 * Enables (== true), disables (== false) navpoint ids auto prefixing feature.
	 * <p><p>
	 * As default, auto-prefixing is enabled.
	 * <p><p>
     * Note that even if auto-prefixing enabled you may prefix ids of navpoints with map name, the auto-prefixing implemented by {@link NavigationGraphBuilder#autoPrefix(String)} 
     * will detects that and auto-correct upper/lower case of this existing prefix if needed. Also you may use it as a validation feature because
     * the {@link NavigationGraphBuilder#autoPrefix(String)} will raise an exception if the prefix does not match the current map name.
     * 
	 * @param autoPrefix
	 */
	public void setAutoPrefix(boolean autoPrefix) {
		this.autoPrefix = autoPrefix;
	}
	
	/**
	 * It returns 'navPointId' prefixed with "{@link NavigationGraphBuilder#getMapName()}.".
	 * <p><p>
     * Note that you may pass prefixed navPointId into this method, it will detect it that and auto-correct upper/lower case of this existing prefix if needed. 
     * Also you may use it as a validation feature because
     * the {@link NavigationGraphBuilder#autoPrefix(String)} will raise an exception if the prefix does not match the current map name.
	 * 
	 * @param navPointId will be auto-prefixed (if enabled, which is default)
	 * @return
	 */
	public String getPrefixed(String navPointId) {
		// auto prefixing is enabled
		if (getMapName() == null) {
			throw new PogamutException("GameInfo was not received yet, can't auto-prefix name of the navpoint '" + navPointId + "'.", this);
		}
		if (navPointId.toLowerCase().startsWith(mapNameLowerChar + ".")) {
			// already prefixed!
			if (!navPointId.startsWith(getMapName())) {
				// but wrong upper/lower case detected, replace!
				navPointId = getMapName() + navPointId.substring(mapNameLowerChar.length());
			}
			// correctly prefixed, just return it
			return navPointId;
		} else {
			// not correctly prefixed, check whether there is any prefix at all?
			if (navPointId.contains(".")) {
				// yes there is -> map name validation fails!
				throw new PogamutException("navPointId '" + navPointId + "' is already prefixed with '" + navPointId.substring(0, navPointId.indexOf(".")) + "' which is different from current map name '" + getMapName() + "', map name validation fails!", this);
			}
			// no there is not ... so prefix it!
			return getMapName() + "." + navPointId;
		}
	}
	
	/**
	 * If {@link NavigationGraphBuilder#isAutoPrefix()} is on (== true), it returns 'navPointId' prefixed with "{@link NavigationGraphBuilder#getMapName()}.".
	 * Otherwise it just returns 'navPointId' as is.
	 * <p><p>
     * Uses {@link NavigationGraphBuilder#getPrefixed(String)} for prefixing.
	 * 
	 * @param navPointId will be auto-prefixed (if enabled, which is default)
	 * @return
	 */
	public String autoPrefix(String navPointId) {
		NullCheck.check(navPointId, "navPointId");
		if (autoPrefix) {
			return getPrefixed(navPointId);
		} else {
			// auto prefixing is disabled
			return navPointId;
		}
	}

	/**
	 * Creates a builder for the specification of the new navpoint you want to create and insert into the worldview.
	 * <p><p>
	 * Use {@link NewNavPointBuilder#createNavPoint()} when done specifying the navpoint.
	 * 
	 * @return navpoint builder
	 */
	public NewNavPointBuilder newNavPoint() {
		used = true;
		return new NewNavPointBuilder();
	}
	
	/**
	 * Creates a builder for the specification of the new navpoint you want to create and insert into the worldview. The builder
	 * will have the id of the navpoint filled (i.e., it calls {@link NewNavPointBuilder#setId(String)} for you.
	 * <p><p>
	 * Use {@link NewNavPointBuilder#createNavPoint()} when done specifying the navpoint.
	 * 
	 * @param navPointId will be auto-prefixed (if enabled, which is default)
	 * @return navpoint builder
	 */
	public NewNavPointBuilder newNavPoint(String navPointId) {
		used = true;
		NullCheck.check(navPointId, "navPointId");
		return new NewNavPointBuilder().setId(navPointId);
	}
	
	/**
	 * Creates a modifier for already existing {@link NavPoint} instance, if navpoint of specified id is not found, an exception is thrown.
	 * <p><p>
	 * The modifier allows you to change existing edges or add new ones.
	 * 
	 * @param navPointId will be auto-prefixed (if enabled, which is default)
	 * @return navpoint modifier for the 'navPointId'
	 */
	public ExistingNavPointModifier modifyNavPoint(String navPointId) {
		used = true;
		NullCheck.check(navPointId, "navPointId");
		navPointId = autoPrefix(navPointId);
		
		Object np = agent.getWorldView().get(UnrealId.get(navPointId));
		if (np == null) {
			throw new PogamutException("Could not modify navpoint '" + navPointId + "' as it was not found in the worldview. No object under this id exists in worldview. Warning, id is case-sensitive the upper/lower cases of the id depends on the concrete spelling of the map that was passed to the GB2004 during startup (either from the command line or by the UT2004).", this);
		}
		if (!(np instanceof NavPoint)) {
			throw new PogamutException("Could not modify navpoint '" + navPointId + "' it does not point to an NavPoint instance but " + np.getClass().getSimpleName() + ". Wrong id specified?", this);
		}
		return new ExistingNavPointModifier((NavPoint)np);
	}
	
	/**
	 * Creates simple (non-altered == no flags == no needed jump, etc.) leading 'fromNavPointId' to 'toNavPointId' (only one edge is created).
	 * <p><p>
	 * If uses {@link ExistingNavPointModifier#modifyEdgeTo(String)} for creation of new edge, so it won't replace an existing edge if such exist.
	 * 
	 * @param fromNavPointId will be auto-prefixed (if enabled, which is default)
	 * @param toNavPointId will be auto-prefixed (if enabled, which is default)
	 */
	public void createSimpleEdge(String fromNavPointId, String toNavPointId) {
		used = true;
		NullCheck.check(fromNavPointId, "fromNavPointId");
		NullCheck.check(toNavPointId, "toNavPointId");
		fromNavPointId = autoPrefix(fromNavPointId);
		toNavPointId = autoPrefix(toNavPointId);
		modifyNavPoint(fromNavPointId).modifyEdgeTo(toNavPointId).createEdge();
	}
	
	/**
	 * Creates simple (non-altered == no flags == no needed jump, etc.) edges between specified navpoints (in both directions).
	 * <p><p>
	 * If uses {@link ExistingNavPointModifier#modifyEdgeTo(String)} for creation of new edge, so it won't replace an existing edge if such exist.
	 * 
	 * @param firstNavPointId will be auto-prefixed (if enabled, which is default)
	 * @param secondNavPointId will be auto-prefixed (if enabled, which is default)
	 */
	public void createSimpleEdgesBetween(String firstNavPointId, String secondNavPointId) {
		used = true;
		NullCheck.check(firstNavPointId, "firstNavPointId");
		NullCheck.check(secondNavPointId, "secondNavPointId");
		firstNavPointId = autoPrefix(firstNavPointId);
		secondNavPointId = autoPrefix(secondNavPointId);
		modifyNavPoint(firstNavPointId).modifyEdgeTo(secondNavPointId).createEdge();
		modifyNavPoint(secondNavPointId).modifyEdgeTo(firstNavPointId).createEdge();
	}
	
	/**
	 * Deletes edge that is leading from 'fromNavPointId' to 'toNavPointId'.
	 * @param fromNavPointId will be auto-prefixed (if enabled, which is default)
	 * @param toNavPointId will be auto-prefixed (if enabled, which is default)
	 */
	public void removeEdge(String fromNavPointId, String toNavPointId) {
		used = true;
		NullCheck.check(fromNavPointId, "fromNavPointId");
		NullCheck.check(toNavPointId, "toNavPointId");
		fromNavPointId = autoPrefix(fromNavPointId);
		toNavPointId = autoPrefix(toNavPointId);
		modifyNavPoint(fromNavPointId).removeEdgeTo(toNavPointId);
	}
	
	/**
	 * Goes through all navpoints deleting edges that leads 'toNavPointId' except when originating from 'exceptNavPointIds'.
	 * @param toNavPointId
	 */
	public void removeEdgesTo(String toNavPointId, String... exceptNavPointIds) {
		NullCheck.check(toNavPointId, "toNavPointId");
		Set<String> except = new HashSet<String>();
		
		toNavPointId = autoPrefix(toNavPointId);
		if (exceptNavPointIds != null) {
			for (String exceptId : exceptNavPointIds) {
				except.add(autoPrefix(exceptId));
			}
		}
		
		for (NavPoint navPoint : worldView.getAll(NavPoint.class).values()) {
			if (except.contains(navPoint.getId().getStringId())) continue;
			modifyNavPoint(navPoint.getId().getStringId()).removeEdgeTo(toNavPointId);
		}		
	}
	
	/**
	 * Removes both edges between two specified navpoints.
	 * @param firstNavPointId will be auto-prefixed (if enabled, which is default)
	 * @param secondNavPointId will be auto-prefixed (if enabled, which is default)
	 */
	public void removeEdgesBetween(String firstNavPointId, String secondNavPointId) {
		used = true;
		NullCheck.check(firstNavPointId, "firstNavPointId");
		NullCheck.check(secondNavPointId, "secondNavPointId");
		firstNavPointId = autoPrefix(firstNavPointId);
		secondNavPointId = autoPrefix(secondNavPointId);
		modifyNavPoint(firstNavPointId).removeEdgeTo(secondNavPointId);
		modifyNavPoint(secondNavPointId).removeEdgeTo(firstNavPointId);
	}

	/**
	 * Whether this instance has been used to alter navigation graph. This might interest you in case you're using {@link FloydWarshallMap}
	 * as you will need to {@link FloydWarshallMap#refreshPathMatrix()} after all changes done to the navigation graph.
	 * @return
	 */
	public boolean isUsed() {
		return used;
	}

	/**
	 * Raises / drops "used" flag, see {@link NavigationGraphBuilder#isUsed()}.
	 * @param used
	 */
	public void setUsed(boolean used) {
		this.used = used;
	}
	
	//
	// EXPORT
	//
	
	/**
	 * Export full navigation graph into XML into Target File ... for more options see {@link MapExport} where you can use {@link MapExport#getXStream()}.
	 * @param targetXMLFile
	 */
	public void exportAsXML(File targetXMLFile) {
		MapExport map = new MapExport(getMapName(), worldView.getAll(NavPoint.class).values());
		map.saveXML(targetXMLFile);
	}
	
	/**
	 * Loads navigation graph data ... DOES NOT APPLY THEM YET TO WORLDVIEW! See {@link NavigationGraphBuilder#apply(MapExport)}.
	 * @param sourceXMLFile
	 */
	public MapExport importFromXML(File sourceXMLFile) {
		return MapExport.loadXML(sourceXMLFile);
	}
	
	public void apply(MapExport export) {
		List<NavPointExport> newNavPoints = new ArrayList<NavPointExport>();
		// FIRST CHECK / CREATE NAV POINTS ONLY
		for (NavPointExport sourceNav : export.navPoints) {
			NavPoint targetNav = (NavPoint)worldView.get(sourceNav.getUnrealId());
			
			if (targetNav == null) {
				NewNavPointBuilder builder = newNavPoint();	
				builder.setId(sourceNav.Id);
				if (sourceNav.Location != null) {
					Location loc = new Location(sourceNav.Location);
					builder.setLocation(loc.x, loc.y, loc.z);
				}
				builder.createNavPoint();
				
				newNavPoints.add(sourceNav);
			} else {
				ExistingNavPointModifier builder = modifyNavPoint(sourceNav.Id);
				// NOT SUPPORTED YET!				
			}						
		}
		// THEN CHECK / CREATE NAV POINT LINKS
		for (NavPointExport sourceNav : export.navPoints) {
			addLinks(sourceNav);
		}
	}

	private void addLinks(NavPointExport newNav) {
		NavPoint targetNav = (NavPoint)worldView.get(newNav.getUnrealId());
		for (NavPointLinkExport newLink : newNav.outgoingEdges) {
			if (newLink.ToNavPoint == null) {
				// IGNORE!
				continue;
			}
			
			NavPointNeighbourLink link = targetNav.getOutgoingEdges().get(newLink.getUnrealId());			
			
			ExistingNavPointModifier navBuilder = modifyNavPoint(newNav.Id);
			ExistingNavPointEdgeBuilder edgeBuilder;
			
			if (link == null) {
				// NEW LINK
				edgeBuilder = navBuilder.createEdgeTo(newLink.ToNavPoint);
			} else {
				// MODIFY LINK
				edgeBuilder = navBuilder.modifyEdgeTo(newLink.ToNavPoint);				
			}
			
			if (newLink.CollisionH != null) edgeBuilder.setCollisionHeight(newLink.CollisionH);
			if (newLink.CollisionR != null) edgeBuilder.setCollisionRadius(newLink.CollisionR);
			if (newLink.NeededJump != null) {
				Location neededJump = new Location(newLink.NeededJump);
				edgeBuilder.setNeededJump(neededJump.x, neededJump.y, neededJump.z);
				
			}
			if (newLink.Flags != null) edgeBuilder.setFlags(newLink.Flags);
			edgeBuilder.setTo(newLink.getUnrealId());
			
			if (link == null) {
				// NEW LINK
				edgeBuilder.createEdge();
			} else {
				// MODIFY LINK
				edgeBuilder.modifyEdge();
			}
		}
	}

	private void add(NavPointExport sourceNav) {
		Location loc = new Location(sourceNav.Location);
		NewNavPointBuilder builder = newNavPoint();
		builder.setId(sourceNav.Id);
		builder.setLocation(loc.x, loc.y, loc.z);
		builder.createNavPoint();
	}
	
}

