package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.awt.Color;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.module.SensomotoricModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.ManualControlWindow.IManualControlCallback;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.LevelGeometryModule;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.RayCastResult;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMeshClearanceComputer.ClearanceLimit;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMeshModule;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.UT2004Draw;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import cz.cuni.amis.pogamut.ut2004.bot.command.CompleteBotCommandsWrapper;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.utils.StopWatch;
import math.geom2d.Vector2D;

public class ManualControl extends SensomotoricModule<UT2004Bot> {
	
	private ManualControlWindow window;
	
	private IManualControlCallback manualControlCallback = new IManualControlCallback() {
		
		@Override
		public void raycast() {
			ManualControl.this.raycast();
		}
		
		@Override
		public void jump() {
			ManualControl.this.jump();
		}

		@Override
		public void drawLevelGeometry() {
			ManualControl.this.drawLevelGeometry();
		}

		@Override
		public void drawLevelGeometryBSP() {
			ManualControl.this.drawLevelGeometryBSP();
		}

		@Override
		public void drawClear() {
			ManualControl.this.drawClear();			
		}

		@Override
		public void drawNavPointVisibility() {
			ManualControl.this.drawNavPointVisibility();			
		}
		
		@Override
		public void drawItemsVisibility() {
			ManualControl.this.drawItemsVisibility();			
		}

		@Override
		public void drawNavPointVisibilityWorldView() {
			ManualControl.this.drawNavPointVisibilityGB2004();
		}

		@Override
		public void drawNavMesh() {
			ManualControl.this.drawNavMesh();
		}

		@Override
		public void drawNavMeshWithLinks() {
			ManualControl.this.drawNavMeshWithLinks();
		}

		@Override
		public void sendGlobalMessage(String msg) {
			ManualControl.this.sendGlobalMessage(msg);
		}

		@Override
		public boolean isActive() {
			return ManualControl.this.isActive();
		}

		@Override
		public void toggleActive() {
			ManualControl.this.toggleActive();			
		}

		@Override
		public void raycastNavMesh() {
			ManualControl.this.raycastNavMesh();
		}
	};
	
	public void showWindow() {
		if (window == null) {
			createWindow();
		}
		window.setVisible(true);
		window.setLocation(10, 10);
	}
	
	private void createWindow() {
		window = new ManualControlWindow();
		window.callback = manualControlCallback;
	}

	public void hideWindow() {
		if (window == null) return;
		window.setVisible(false);
	}
	
	/*========================================================================*/
	
	private boolean active = true;
	
	protected void tick() {
		if (window == null) return;
		window.setTitle(worldView.getSingle(Self.class).getName());		
		if (!active) {
			log.setLevel(Level.OFF);
			return;
		}
		log.setLevel(Level.ALL);
		if (window.forward) {
    		body.getLocomotion().moveContinuos();
    	}    	
    	if (window.backward) {
    		body.getLocomotion().strafeTo(
    				info.getLocation().add(info.getRotation().toLocation().invert().scale(300)), 
    				info.getLocation().add(info.getRotation().toLocation().scale(300))
    		);
    	}
    	if (window.left) {
    		body.getLocomotion().turnHorizontal(-15);
    	}
    	if (window.right) {
    		body.getLocomotion().turnHorizontal(15);
    	}
    	if (!window.forward && !window.left && !window.right && !window.backward) {
    		body.getLocomotion().stopMovement();
    	}
	}
	
	public void toggleActive() {
		active = !active;
	}

	public boolean isActive() {
		return active;
	}
	
	public void drawClear() {
		if (draw == null) return;
		draw.clearAll();
		say("ALL DRAWING CLEARED");
	}

	public void drawLevelGeometryBSP() {
		if (levelGeometryModule == null) return;
		if (!levelGeometryModule.isInitialized()) {
			say("Level geometry not initialized, cannot draw BSP.");
			return;
		}
		say("Drawing level geometry BSP...");
		levelGeometryModule.getDraw().drawBSP();
		say("LEVEL GEOMETRY BSP DRAWN");
	}

	public void drawLevelGeometry() {
		if (levelGeometryModule == null) return;
		if (!levelGeometryModule.isInitialized()) {
			say("Level geometry not initialized, cannot draw.");
			return;
		}
		say("Drawing level geometry... This will take a long time, like 10-20 minutes.");
		levelGeometryModule.getDraw().draw();
		say("LEVEL GEOMETRY DRAWN");
	}

	public void jump() {
		body.getLocomotion().jump();
	}

	public void raycast() {
		if (levelGeometryModule == null) return;
		if (!levelGeometryModule.isInitialized()) {
			say("Level geometry not initialized, cannot raycast.");
			return;
		}
    	
    	int raycastDistance = 1000;
		
		say("Let's do some raycasting from here (client-side of course) up to " + raycastDistance + " UT units distance. ");
		
		StopWatch watch = new StopWatch();
		watch.start();
		
		Location rayVector = info.getRotation().toLocation().getNormalized().scale(raycastDistance);
		RayCastResult raycast = levelGeometryModule.getLevelGeometry().rayCast(info.getLocation(), info.getLocation().add(rayVector));
		
		watch.stop();
		
		log.info("RAYCAST RESULT");
		log.info("    +-- RAY:  " + raycast.ray.getOrigin() + " -> " + raycast.ray.getExamplePoint2());
		log.info("    +-- VEC:  " + raycast.ray.getVector());
		log.info("    +-- HIT:  " + raycast.isHit());
		if (raycast.isHit()){
			log.info("        +-- LOCATION: " + raycast.hitLocation);
			log.info("        +-- DISTANCE: " + raycast.hitDistance);
		}
			
		say("1 Raycast, performed in " + watch.timeStr() + ", result: " + (raycast.isHit() ? "HIT" : "NO-HIT"));
		
		levelGeometryModule.getDraw().drawRaycast(raycast);
	}
	
	protected void raycastNavMesh() {
		if (navMeshModule == null) return;
		if (!navMeshModule.isInitialized()) {
			say("NavMeshModule not initialized, cannot raycast.");
			return;
		}
		
		int raycastDistance = 1000;
		
		say("Let's do some NAVMESH raycasting from here (client-side of course) up to " + raycastDistance + " UT units distance. ");
		
		StopWatch watch = new StopWatch();
		watch.start();
		
		// GROUNDING
		NavMeshPolygon nmPoly = navMeshModule.getDropGrounder().tryGround(info.getLocation());
    	if (nmPoly == null) {
    		say("CANNOT FIND NAVMESH UNDER MY LOCATION: " + info.getLocation());
    		return;
    	}
    	Location nmLoc = new Location(nmPoly.getShape().project(info.getLocation().asPoint3D()));
		
		Location rayVector = info.getRotation().toLocation().getNormalized();
    	ClearanceLimit result = navMeshModule.getClearanceComputer().findEdge(info.getLocation(), new Vector2D(rayVector.x, rayVector.y), raycastDistance, 1000);
		
		watch.stop();
		
		boolean hit = result != null;
		
		log.info("RAYCAST RESULT");
		log.info("    +-- RAY:  " + info.getLocation() + " -> " + info.getLocation().add(rayVector.scale(1000)));
		log.info("    +-- VEC:  " + rayVector);
		log.info("    +-- HIT:  " + hit);
		if (hit) {
			log.info("        +-- LOCATION: " + result.getLocation());
			log.info("        +-- DISTANCE: " + result.getLocation().sub(nmLoc).getLength());
		}
			
		say("1 NavMesh Raycast, performed in " + watch.timeStr() + ", result: " + (hit ? "HIT" : "NO-HIT"));
		
		if (hit) {
			draw.drawLine(Color.red, nmLoc, result.getLocation());	
		} else {
			draw.drawLine(Color.blue, nmLoc, nmLoc.add(rayVector.scale(1000)));
		}		
	}
	
	public void drawNavPointVisibility() {
		if (draw == null) return;
		if (navPointVisibility == null) return;
		if (!navPointVisibility.isInitialized()) {
			say("Visibility matrix not loaded, cannot draw.");
			return;
		}
		
		say("Drawing nav point visibility (according to NavPointVisibility)...");
		navPointVisibility.drawNavPointVisibility(draw);
		say("Drawing DONE!");
	}
	
	public void drawNavPointVisibilityGB2004() {
		if (draw == null) return;
		if (navPointVisibility == null) return;
		if (!navPointVisibility.isInitialized()) {
			say("Visibility matrix not loaded, cannot draw.");
			return;
		}
		
		say("Drawing nav point visibility (according to WorldView)...");
		navPointVisibility.drawNavPointVisibilityWorldView(draw);
		say("Drawing DONE!");
	}
	
	protected void drawItemsVisibility() {
		if (draw == null) return;
		
		say("Drawing items visibility (according to WorldView)...");
		for (Item item : worldView.getAll(Item.class).values()) {
			Color color;
			if (item.isVisible()) {
				color = Color.green;
			} else {
				color = Color.red;
			}
			draw.drawCube(color, item.getLocation(), 15);			
		}
		say("Drawing DONE!");
	}


	private void say(String text) {
		body.getCommunication().sendGlobalTextMessage(text);
	}
	
	protected void sendGlobalMessage(String msg) {
		say(msg);
	}

	public void drawNavMeshWithLinks() {
		if (navMeshModule == null) return;
		if (!navMeshModule.isInitialized()) {
			say("NavMeshModule not initialized, cannot draw.");
			return;
		}
		navMeshModule.getNavMeshDraw().draw(true, true);
	}

	public void drawNavMesh() {
		if (navMeshModule == null) return;
		if (!navMeshModule.isInitialized()) {
			say("NavMeshModule not initialized, cannot draw.");
			return;
		}
		navMeshModule.getNavMeshDraw().draw(true, false);
	}
	
	/*========================================================================*/
	
	private class SelfMessageListener implements IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>> 
	{
		
		private IWorldView worldView;

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public SelfMessageListener(IWorldView worldView)
		{
			
			worldView.addObjectListener(Self.class, WorldObjectUpdatedEvent.class, this);
			this.worldView = worldView;
		}

		@Override
		public void notify(WorldObjectUpdatedEvent<Self> event) {
			tick();
		}
		
	}
	
	private SelfMessageListener selfMessageListener;
	
	/*========================================================================*/
	
	/**
	 * {@link EndMessage} listener.
	 */
	private class EndMessageListener implements IWorldEventListener<EndMessage>
	{
		private IWorldView worldView;

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public EndMessageListener(IWorldView worldView)
		{
			
			worldView.addEventListener(EndMessage.class, this);
			this.worldView = worldView;
		}

		@Override
		public void notify(EndMessage event) {
			tick();
		}
	}

	/** {@link EndMessage} listener */
	private EndMessageListener endMessageListener;
	
	/*========================================================================*/
	
	private AgentInfo info;
	
	private CompleteBotCommandsWrapper body;
	
	private LevelGeometryModule levelGeometryModule;

	private UT2004Draw draw;

	private NavPointVisibility navPointVisibility;

	private NavMeshModule navMeshModule;

	
	/**
	 * 
	 * @param bot
	 * @param info
	 * @param body
	 * @param levelGeometryModule may be null
	 * @param draw may be null
	 * @param navPointVisibility may be null
	 */
	public ManualControl(UT2004Bot bot, AgentInfo info, CompleteBotCommandsWrapper body, LevelGeometryModule levelGeometryModule, UT2004Draw draw, NavPointVisibility navPointVisibility, NavMeshModule navMeshModule) {
		super(bot);
		
		this.info = info;
		this.body = body;
		this.levelGeometryModule = levelGeometryModule;
		this.draw = draw;
		this.navPointVisibility = navPointVisibility;
		this.navMeshModule = navMeshModule;
		
		this.endMessageListener = new EndMessageListener(worldView);
		this.selfMessageListener = new SelfMessageListener(worldView);
	}
	
	@Override
	protected void start(boolean startToPaused) {
		super.start(startToPaused);
		showWindow();
	}
	
	@Override
	protected void cleanUp() {
		super.cleanUp();
		hideWindow();
	}

}
