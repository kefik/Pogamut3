package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;

/**
 * CTF module is encapsulating basic CTF-game logic. That is it tracks state of our/enemy flag and can quickly tells you
 * whether you can score / your team can score / enemy team can score, etc... 
 * 
 * This info is available only for CTF games.
 * 
 * @author Jimmy
 */
public class CTF extends SensorModule<UT2004Bot> {
	
	protected Self self;
	
	protected FlagInfo ourFlag;
	
	protected NavPoint ourBase;

	protected FlagInfo enemyFlag;
	
	protected NavPoint enemyBase;
	
	/**
	 * Module is enabled ONLY for CTF games.
	 * 
	 * INITIALLY SET TO: false
	 */
	protected boolean enabled = false;
	
	/**
	 * Enabled/disabled based on the game that is currently running, see {@link GameInfoListener}.
	 * @param value
	 */
	protected void setEnabled(boolean value) {
		if (value == this.enabled) return;
		this.enabled = value;		
		if (this.enabled) {
			log.info("Module enabled, registering event listeners.");
		} else {
			log.info("Module disabled, removing event listeners.");			
		}		
	}
	
	// =======================
	// ENEMY FLAG/BASE METHODS
	// =======================
	
	/**
	 * Returns ENEMY flag. Available since first logic().
	 * @return
	 */
	public FlagInfo getEnemyFlag() {
		return enemyFlag;
	}

	/**
	 * Returns ENEMY BASE navpoint. Available since first logic().
	 * @return
	 */
	public NavPoint getEnemyBase() {
		return enemyBase;
	}
	
	/**
	 * Whether enemy team may score == enemy flag is at home, alias for {@link CTF#isEnemyFlagHome()}.
	 * @return
	 */
	public boolean canEnemyTeamPossiblyScore() {
		return isEnemyFlagHome();
	}
	
	/**
	 * Whether enemy team can currently SCORE by carrying our flag into enemy (their) base == enemy flag is at home && some of enemy player is carrying our flag.
	 * @return
	 */
	public boolean canEnemyTeamScore() {
		return isEnemyFlagHome() && isEnemyTeamCarryingOurFlag();
	}
	
	/**
	 * Whether enemy team is currently carrying our flag, alias for {@link CTF#isOurFlagHeld()}.
	 * @return
	 */
	public boolean isEnemyTeamCarryingOurFlag() {
		return isOurFlagHeld();
	}
	
	/**
	 * ENEMY FLAG is safe at enemy home.
	 * @return
	 */
	public boolean isEnemyFlagHome() {
		return enemyFlag != null && enemyFlag.getState().toLowerCase().contains("home");
	}
	
	/**
	 * ENEMY FLAG IS LAYING SOMEWHERE!
	 * @return
	 */
	public boolean isEnemyFlagDropped() {
		return enemyFlag != null && enemyFlag.getState().toLowerCase().contains("dropped");
	}
	
	/**
	 * ENEMY FLAG is being carried by some team-mate.
	 * @return
	 */
	public boolean isEnemyFlagHeld() {
		return enemyFlag != null && enemyFlag.getState().toLowerCase().contains("held");
	}
	
	// =====================
	// OUR FLAG/BASE METHODS
	// =====================
	
	/**
	 * Returns OUR flag. Available since first logic().
	 * @return
	 */
	public FlagInfo getOurFlag() {
		return ourFlag;
	}

	/**
	 * Returns OUR BASE navpoint. Available since first logic().
	 * @return
	 */
	public NavPoint getOurBase() {
		return ourBase;
	}
	
	/**
	 * Whether your team may score == your flag is at home, alias for {@link CTF#isOurFlagHome()}.
	 * @return
	 */
	public boolean canOurTeamPossiblyScore() {
		return isOurFlagHome();
	}
	
	/**
	 * Whether our team can currently SCORE by carrying enemy flag into our base == our flag is at home && some of my team-mate is carrying enemy flag.
	 * @return
	 */
	public boolean canOurTeamScore() {
		return isOurFlagHome() && isOurTeamCarryingEnemyFlag();
	}
	
	/**
	 * Whether this bot can currently SCORE == our flag is at home AND this bot is carrying the flag.
	 * @return
	 */
	public boolean canBotScore() {
		return isOurFlagHome() && isBotCarryingEnemyFlag();
	}

	/**
	 * Whether this bot (you) is carrying enemy flag.
	 * @return
	 */
	public boolean isBotCarryingEnemyFlag() {
		return enemyFlag != null && isEnemyFlagHeld() && enemyFlag.getHolder() != null && enemyFlag.getHolder().equals(info.getId());
	}
	
	/**
	 * Whether our team is currently carrying enemy flag, alias for {@link CTF#isEnemyFlagHeld()}.
	 * @return
	 */
	public boolean isOurTeamCarryingEnemyFlag() {
		return isEnemyFlagHeld();
	}
	
	/**
	 * OUR FLAG is safe at home.
	 * @return
	 */
	public boolean isOurFlagHome() {
		return ourFlag != null && ourFlag.getState().toLowerCase().contains("home");
	}
	
	/**
	 * OUR FLAG IS LAYING SOMEWHERE!
	 * @return
	 */
	public boolean isOurFlagDropped() {
		return ourFlag != null && ourFlag.getState().toLowerCase().contains("dropped");
	}
	
	/**
	 * OUR FLAG is being carried by some enemy player.
	 * @return
	 */
	public boolean isOurFlagHeld() {
		return ourFlag != null && ourFlag.getState().toLowerCase().contains("held");
	}

	/**
	 * Check whether the module is enabled. The module is enabled only for CTF games.
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}	
	
	/////////////////////////////////////////
	// GAME LISTENER
	/////////////////////////////////////////
	
	/**
	 * {@link ItemPickedUp} listener.
	 */
	protected class GameInfoListener implements IWorldEventListener<GameInfo>
	{
		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public GameInfoListener(IWorldView worldView)
		{
			worldView.addEventListener(GameInfo.class, this);
		}

		@Override
		public void notify(GameInfo event)
		{
			gameInfoSensed(event);
		}

	}

	public void gameInfoSensed(GameInfo gameInfo) {
		setEnabled(gameInfo.getGametype() != null && gameInfo.getGametype().toLowerCase().contains("ctf"));		
	}
	
	protected GameInfoListener gameInfoListener;
	
	/*========================================================================*/
	
	/////////////////////////////////////////
	// FLAG APPEARED LISTENER
	/////////////////////////////////////////
	
	protected class FlagUpdatedListener implements IWorldObjectEventListener<FlagInfo, WorldObjectUpdatedEvent<FlagInfo>>
	{

		@Override
		public void notify(WorldObjectUpdatedEvent<FlagInfo> event)
		{
			flagAppeared(event.getObject());
		}
		
	}

	public void flagAppeared(FlagInfo flag) {
		if (flag.getTeam() == info.getTeam()) {
			ourFlag = flag;
			log.info("Got our flag: " + flag);
		} else {
			enemyFlag = flag;
			log.info("Got enemy flag: " + flag);
		}		
		if (ourFlag != null && enemyFlag != null) {
			worldView.removeObjectListener(FlagInfo.class, WorldObjectUpdatedEvent.class, flagUpdatedListener);
		}
	}
	
	protected FlagUpdatedListener flagUpdatedListener;

	/*========================================================================*/
	
	/////////////////////////////////////////
	// SELF APPEARED LISTENER
	/////////////////////////////////////////
	
	protected class SelfUpdatedListener implements IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>>
	{

		@Override
		public void notify(WorldObjectUpdatedEvent<Self> event)
		{
			selfAppeared(event.getObject());
		}
		
	}

	public void selfAppeared(Self self) {
		this.self = self;	
		
		ourBase = null;
		enemyBase = null;
		
		for (NavPoint np : worldView.getAll(NavPoint.class).values()) {
			if (np.getId().getStringId().toLowerCase().contains("redflagbase")) {
				if (self.getTeam() == AgentInfo.TEAM_RED) {
					ourBase = np;
				} else {
					enemyBase = np;
				}
			} else
			if (np.getId().getStringId().toLowerCase().contains("blueflagbase")) {
				if (self.getTeam() == AgentInfo.TEAM_BLUE) {
					ourBase = np;
				} else {
					enemyBase = np;
				}
			}
			if (ourBase != null && enemyBase != null) break; 
		}
		
		worldView.removeObjectListener(Self.class, WorldObjectUpdatedEvent.class, selfAppearedListener);
	}
	
	protected SelfUpdatedListener selfAppearedListener;
	
	/*========================================================================*/

	/** Agent info module. */
	public AgentInfo info;
	
	
	/**
	 * Constructor. Setups the memory module based on bot's world view.
	 * @param bot owner of the module
	 * @param info agent info module
	 */
	public CTF(UT2004Bot bot, AgentInfo info) {
		super(bot);
		this.info = info;
		gameInfoListener = new GameInfoListener(bot.getWorldView());
		selfAppearedListener = new SelfUpdatedListener();
		worldView.addObjectListener(Self.class, WorldObjectUpdatedEvent.class, selfAppearedListener);
		flagUpdatedListener = new FlagUpdatedListener();
		worldView.addObjectListener(FlagInfo.class, WorldObjectUpdatedEvent.class, flagUpdatedListener);
	}
	
	@Override
	protected void start(boolean startToPaused) {
		super.start(startToPaused);
		if (!worldView.isListening(Self.class, WorldObjectUpdatedEvent.class, selfAppearedListener)) {
			worldView.addObjectListener(Self.class, WorldObjectUpdatedEvent.class, selfAppearedListener);
		}
		if (!worldView.isListening(FlagInfo.class, WorldObjectUpdatedEvent.class, flagUpdatedListener)) {
			worldView.addObjectListener(FlagInfo.class, WorldObjectUpdatedEvent.class, flagUpdatedListener);
		}
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();
		ourFlag = null;
		enemyFlag = null;
		ourBase = null;
		enemyBase = null;
		setEnabled(false);
		if (!worldView.isListening(Self.class, WorldObjectUpdatedEvent.class, selfAppearedListener)) {
			worldView.addObjectListener(Self.class, WorldObjectUpdatedEvent.class, selfAppearedListener);
		}
		if (!worldView.isListening(FlagInfo.class, WorldObjectUpdatedEvent.class, flagUpdatedListener)) {
			worldView.addObjectListener(FlagInfo.class, WorldObjectUpdatedEvent.class, flagUpdatedListener);
		}
	}

}
