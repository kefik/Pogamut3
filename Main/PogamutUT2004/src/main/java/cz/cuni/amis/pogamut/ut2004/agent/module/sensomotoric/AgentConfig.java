package cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric;

import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Pick;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;

/**
 * Memory module specialized on the agent's configuration inside UT2004.
 * <p><p>
 * It should be instantiated inside {@link IUT2004BotController#prepareBot(UT2004Bot)()} class and it may be used since
 * {@link IUT2004BotController#botInitialized(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage)}
 * is called.
 * 
 * @author Jimmy
 */
public class AgentConfig extends SensorModule<UT2004Bot> {
	
	/**
	 * True, if you have to spawn the bot manually after each death.
	 */
	public boolean isManualSpawn() {
		if (lastConfig == null) return false;
		return lastConfig.isManualSpawn();
	}
	
	/**
	 * Enables/disables manual spawning.
	 * <p><p>
	 * Sets if the bot will have to be respawned after death
	 * manually by RESPAWN command. If false, the bot will respawn automatically.
	 * @param state
	 */
	public void setManualSpawn(boolean state) {
		bot.getAct().act(new Configuration().setManualSpawn(state));
	}

	/**
	 * True, if the bot is using auto ray tracing (is provided with synchronous
	 * ATR messages). See ATR messages for more details.
	 */
	public boolean isAutoTrace() {
		if (lastConfig == null) return false;
		return lastConfig.isAutoTrace();
	}
	
	/**         
     * Enables/disables auto ray tracing feature.
     */
	public void setAutoTrace(boolean state) {
		bot.getAct().act(new Configuration().setAutoTrace(state));
	}

	/**
	 * The bot's name.
	 */
	public String getName() {
		if (lastConfig == null) return "unknown yet";
		return lastConfig.getName();
	}
	
	/**         
     * You can change the name of the bot in the game.
     */
	public void setName(String newName) {
		bot.getAct().act(new Configuration().setName(newName));
		bot.getComponentId().getName().setFlag(newName);
	}
	
	/**
	 * Bots default speed will be multiplied by this number. Ranges from 0.1 to
	 * 2 (default, can be set in ini in [RemoteBot] MaxSpeed).
	 */
	public double getSpeedMultiplier() {
		if (lastConfig == null) return -1;
		return lastConfig.getSpeedMultiplier();
	}
	
	/**         
     * Bots default speed will be multiplied by this number. Ranges from 0.1 to 2 (default, can be set in ini in [RemoteBot] MaxSpeed).
     */
	public void setSpeedMultiplier(double value) {
		if (value < 0.1) value = 0.1;
		else if (value > 2) value = 2;
		bot.getAct().act(new Configuration().setSpeedMultiplier(value));
	}

	/**
	 * If bot is invulnerable (cannot die) or not.
	 */
	public boolean isInvulnerable() {
		if (lastConfig == null) return false;
		return lastConfig.isInvulnerable();
	}
	
	 /**         
      * Will set godmode for bot on (bot can't be killed). This can
	  * be changed just when cheating is enabled on the server.
	  * (bAllowCheats = True)
      */
	public void setInvulnerability(boolean state) {
		bot.getAct().act(new Configuration().setInvulnerable(state));
	}	

	/**
	 * The delay between two synchronous batches (can range from 0.1 to 2
	 * seconds).
	 */
	public double getVisionTime() {
		if (lastConfig == null) return -1;
		return lastConfig.getVisionTime();
	}
	
	 /**         
	  * Between 0.1 to 2 seconds, it sets the delay between two
	  * synchronous batches.
	  */
	public void setVisionTime(double value) {
		if (value < 0.1) value = 0.1;
		else if (value > 2) value = 2;
		bot.getAct().act(new Configuration().setVisionTime(value));
	}

	/**
	 * If some additional debug information will be shown in the UT2004 server
	 * console window.
	 */
	public boolean isShowDebug() {
		if (lastConfig == null) return false;
		return lastConfig.isShowDebug();
	}
	
	/**         
     * If true some additional debug information will be logged to
	 * UT2004 server console window.
	 */
	public void setShowDebug(boolean state) {
		bot.getAct().act(new Configuration().setShowDebug(state));
	}

	/**
	 * If true an actor visualizing the location the bot is actually looking at
	 * will appear in the game.
	 */
	public boolean isShowFocalPoint() {
		if (lastConfig == null) return false;
		return lastConfig.isShowFocalPoint();
	}
	
	/**         
     * If set to true a marker will appear in the game on the
	 * location the bot is actually looking at.
	 */
	public void setShowFocalPoint(boolean state) {
		bot.getAct().act(new Configuration().setShowFocalPoint(state));
	}

	/**
	 * If the GB should draw lines representing the auto ray traces of the bot
	 * (for more information see ATR message).
	 */
	public boolean isDrawTraceLines() {
		if (lastConfig == null) return false;
		return lastConfig.isDrawTraceLines();
	}
	
	/**
	 * If set to true an actor will appear in the game on the
	 * location the bot is actually looking at.
	 */
	public void setDrawTraceLines(boolean state) {
		bot.getAct().act(new Configuration().setDrawTraceLines(state));
	}

	/**
	 * It informs whether the sending of all GB synchronous messages is enabled or
	 * disabled.
	 */
	public boolean isSynchronousBatchExported() {
		if (lastConfig == null) return false;		
		return !lastConfig.isSynchronousOff();
	}
	
	/**         
     * It enables/disables sending of all GB synchronous messages
	 * for the bot.
     */
	public void setSynchronousBatchExport(boolean state) {
		bot.getAct().act(new Configuration().setSynchronousOff(!state));
	}

	/**
	 * It enables/disables automatic pickup of the bot. If false the items can be
	 * picked up only through {@link Pick} command.
	 */
	public boolean isAutoPickup() {
		if (lastConfig == null) return false;		
		return !lastConfig.isAutoPickupOff();
	}
	
	/**
	 * Returns current vision FOV set in GB2004, in degrees.
	 * @return
	 */
	public double getVisionFOV() {
		if (lastConfig == null) return 170;		
		return lastConfig.getVisionFOV() * 2;
	}
	
	
	/**         
     * 
	 * It enables/disables automatic pickup of the bot. If false is set the items can be picked up through {@link Pick} command.
     */
	public void setAutoPickup(boolean state) {
		bot.getAct().act(new Configuration().setAutoPickupOff(!state));
	}
	
	/**
	 * Tells how fast the bot is rotating in all three directions (pitch, yaw, roll).
	 * @return
	 */
	public Rotation getRotationSpeed() {
		if (lastConfig == null) return null;
		return lastConfig.getRotationRate();
	}
	
	/**
	 * Sets how fast the bot will rotate horizontally (i.e., yaw rotation == left/right).
	 * @param speed desired rotation speed
	 */
	public void setRotationHorizontalSpeed(double speed) {
		Rotation actual = getRotationSpeed();
		if (actual == null) return;
		actual = new Rotation(actual).setYaw(speed);
		bot.getAct().act(new Configuration().setRotationRate(actual));
	}
	
	/**
	 * Sets how fast the bot will rotate vertically (i.e., pitch rotation == up/down).
	 * @param speed desired rotation speed
	 */
	public void setRotationVerticalSpeed(double speed) {
		Rotation actual = getRotationSpeed();
		if (actual == null) return;
		actual = new Rotation(actual).setPitch(speed);
		bot.getAct().act(new Configuration().setRotationRate(actual));
	}
	
	/**
	 * Sets how fast the bot will rotate in all three axes (yaw/pitch/roll).
	 * @param rotationSpeeds desired rotation speeds
	 */
	public void setRotationSpeed(Rotation rotationSpeeds) {
		bot.getAct().act(new Configuration().setRotationRate(rotationSpeeds));
	}
	
	/**
	 * Retrieves the configuration of the bot inside UT2004.
	 * @return Configuration of the bot.
	 */
	public ConfigChange getConfig() {
		return lastConfig;
	}
	
	/*========================================================================*/
	
	/**
	 * {@link ConfigChange} listener.
	 */
	private class ConfigChangeListener implements IWorldObjectEventListener<ConfigChange, IWorldObjectEvent<ConfigChange>>
	{
		private IWorldView worldView;

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public ConfigChangeListener(IWorldView worldView)
		{
			worldView.addObjectListener(ConfigChange.class, WorldObjectUpdatedEvent.class, this);
			this.worldView = worldView;
		}

		@Override
		public void notify(IWorldObjectEvent<ConfigChange> event) {
			lastConfig = event.getObject();			
		}
	}

	/** {@link ConfigChange} listener */
	private ConfigChangeListener configChangeListener;
	
	private ConfigChange lastConfig = null;

	private UT2004Bot bot;


	/*========================================================================*/
	
	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module
	 */
	public AgentConfig(UT2004Bot bot) {
		this(bot, null);		
	}

	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module
	 * @param moduleLog where to log module's messages
	 */
	public AgentConfig(UT2004Bot bot, LogCategory moduleLog) {
		super(bot, moduleLog);
		this.bot = bot;
		configChangeListener = new ConfigChangeListener(bot.getWorldView());
		
		cleanUp();
	}	
		
	@Override
	protected void cleanUp() {
		super.cleanUp();
		lastConfig = null;
	}

	
	
}