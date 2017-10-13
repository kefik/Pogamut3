package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import cz.cuni.amis.pogamut.base.agent.module.SensomotoricModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.ManualControlWindow.IManualControlCallback;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.LevelGeometryModule;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.RayCastResult;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.UT2004Draw;
import cz.cuni.amis.pogamut.ut2004.bot.command.CompleteBotCommandsWrapper;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.utils.StopWatch;

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
		public void drawNavPointVisibilityWorldView() {
			ManualControl.this.drawNavPointVisibilityGB2004();
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
	
	protected void tick() {
		if (window == null) return;
		if (window.forward) {
    		body.getLocomotion().moveContinuos();
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
	
	protected void drawClear() {
		if (draw == null) return;
		draw.clearAll();
		say("ALL DRAWING CLEARED");
	}

	protected void drawLevelGeometryBSP() {
		if (levelGeometryModule == null) return;
		if (!levelGeometryModule.isInitialized()) return;
		say("Drawing level geometry BSP...");
		levelGeometryModule.getDraw().drawBSP();
		say("LEVEL GEOMETRY BSP DRAWN");
	}

	protected void drawLevelGeometry() {
		if (levelGeometryModule == null) return;
		if (!levelGeometryModule.isInitialized()) return;
		say("Drawing level geometry... This will take a long time, like 10-20 minutes.");
		levelGeometryModule.getDraw().draw();
		say("LEVEL GEOMETRY DRAWN");
	}

	protected void jump() {
		body.getLocomotion().jump();
	}

	protected void raycast() {
		if (levelGeometryModule == null) return;
		if (!levelGeometryModule.isInitialized()) return;
    	
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
	
	protected void drawNavPointVisibility() {
		if (draw == null) return;
		if (navPointVisibility == null) return;
		
		say("Drawing nav point visibility (according to NavPointVisibility)...");
		navPointVisibility.drawNavPointVisibility(draw);
		say("Drawing DONE!");
	}
	
	protected void drawNavPointVisibilityGB2004() {
		if (draw == null) return;
		if (navPointVisibility == null) return;
		
		say("Drawing nav point visibility (according to WorldView)...");
		navPointVisibility.drawNavPointVisibilityWorldView(draw);
		say("Drawing DONE!");
	}

	private void say(String text) {
		body.getCommunication().sendGlobalTextMessage(text);
	}
	
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
	
	/**
	 * 
	 * @param bot
	 * @param info
	 * @param body
	 * @param levelGeometryModule may be null
	 * @param draw may be null
	 * @param navPointVisibility may be null
	 */
	public ManualControl(UT2004Bot bot, AgentInfo info, CompleteBotCommandsWrapper body, LevelGeometryModule levelGeometryModule, UT2004Draw draw, NavPointVisibility navPointVisibility) {
		super(bot);
		
		this.info = info;
		this.body = body;
		this.levelGeometryModule = levelGeometryModule;
		this.draw = draw;
		this.navPointVisibility = navPointVisibility;
		
		this.endMessageListener = new EndMessageListener(worldView);
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
