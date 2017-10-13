package cz.cuni.amis.pogamut.base.communication.mediator.testevents;

import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;

/**
 * Asynchronous message. Sent as response to READY command. Information about
 * the game. What type of game is it going to be, number of teams, maximum size
 * of teams etc.
 * 
 * Corresponding GameBots message is NFO.
 */

public class StubGameInfo extends InfoMessage

implements IWorldChangeEvent, IWorldObject

{

	/**
	 * Creates new instance of command GameInfo.
	 * 
	 * Asynchronous message. Sent as response to READY command. Information
	 * about the game. What type of game is it going to be, number of teams,
	 * maximum size of teams etc. Corresponding GameBots message for this
	 * command is .
	 * 
	 * @param Gametype
	 *            What you are playing (BotDeathMatch, BotTeamGame,
	 *            BotCTFGame,BotDoubleDomination).
	 * @param Level
	 *            Name of map in game.
	 * @param WeaponStay
	 *            If true respawned weapons will stay on the ground after picked
	 *            up (but bot cannot pickup same weapon twice).
	 * @param TimeLimit
	 *            Maximum time game will last (if tied at end may goe to
	 *            "sudden death overtime" - depends on the game type).
	 * @param FragLimit
	 *            Number of kills needed to win game (BotDeathMatch only).
	 * @param GoalTeamScore
	 *            Number of points a team needs to win the game (BotTeamGame,
	 *            BotCTFGame, BotDoubleDomination).
	 * @param MaxTeams
	 *            Max number of teams. Valid team range will be 0 to (MaxTeams -
	 *            1) (BotTeamGame, BotCTFGame, BotDoubleDomination). Usually
	 *            there will be two teams - 0 and 1.
	 * @param MaxTeamSize
	 *            Max number of players per side (BotTeamGame, BotCTFGame,
	 *            BotDoubleDomination).
	 * @param GamePaused
	 *            If the game is paused - nobody can move.
	 * @param BotsPaused
	 *            If the game is paused just for bots - human controlled players
	 *            can normally move.
	 */
	public StubGameInfo(String Gametype, String Level, boolean WeaponStay,
			double TimeLimit, int FragLimit, double GoalTeamScore,
			int MaxTeams, int MaxTeamSize, boolean GamePaused,
			boolean BotsPaused) {

		this.Gametype = Gametype;

		this.Level = Level;

		this.WeaponStay = WeaponStay;

		this.TimeLimit = TimeLimit;

		this.FragLimit = FragLimit;

		this.GoalTeamScore = GoalTeamScore;

		this.MaxTeams = MaxTeams;

		this.MaxTeamSize = MaxTeamSize;

		this.GamePaused = GamePaused;

		this.BotsPaused = BotsPaused;

	}

	/** Example how the message looks like - used during parser tests. */
	public static final String PROTOTYPE = "NFO {Gametype text} {Level text} {WeaponStay False} {TimeLimit 0} {FragLimit 0} {GoalTeamScore 0} {MaxTeams 0} {MaxTeamSize 0} {GamePaused False} {BotsPaused False}";

	// ///// Properties BEGIN

	/**
	 * What you are playing (BotDeathMatch, BotTeamGame,
	 * BotCTFGame,BotDoubleDomination).
	 */
	protected String Gametype = null;

	/**
	 * What you are playing (BotDeathMatch, BotTeamGame,
	 * BotCTFGame,BotDoubleDomination).
	 */
	public String getGametype() {
		return Gametype;
	}

	/**
	 * Name of map in game.
	 */
	protected String Level = null;

	/**
	 * Name of map in game.
	 */
	public String getLevel() {
		return Level;
	}

	/**
	 * If true respawned weapons will stay on the ground after picked up (but
	 * bot cannot pickup same weapon twice).
	 */
	protected boolean WeaponStay = false;

	/**
	 * If true respawned weapons will stay on the ground after picked up (but
	 * bot cannot pickup same weapon twice).
	 */
	public boolean isWeaponStay() {
		return WeaponStay;
	}

	/**
	 * Maximum time game will last (if tied at end may goe to
	 * "sudden death overtime" - depends on the game type).
	 */
	protected double TimeLimit = 0;

	/**
	 * Maximum time game will last (if tied at end may goe to
	 * "sudden death overtime" - depends on the game type).
	 */
	public double getTimeLimit() {
		return TimeLimit;
	}

	/**
	 * Number of kills needed to win game (BotDeathMatch only).
	 */
	protected int FragLimit = 0;

	/**
	 * Number of kills needed to win game (BotDeathMatch only).
	 */
	public int getFragLimit() {
		return FragLimit;
	}

	/**
	 * Number of points a team needs to win the game (BotTeamGame, BotCTFGame,
	 * BotDoubleDomination).
	 */
	protected double GoalTeamScore = 0;

	/**
	 * Number of points a team needs to win the game (BotTeamGame, BotCTFGame,
	 * BotDoubleDomination).
	 */
	public double getGoalTeamScore() {
		return GoalTeamScore;
	}

	/**
	 * Max number of teams. Valid team range will be 0 to (MaxTeams - 1)
	 * (BotTeamGame, BotCTFGame, BotDoubleDomination). Usually there will be two
	 * teams - 0 and 1.
	 */
	protected int MaxTeams = 0;

	/**
	 * Max number of teams. Valid team range will be 0 to (MaxTeams - 1)
	 * (BotTeamGame, BotCTFGame, BotDoubleDomination). Usually there will be two
	 * teams - 0 and 1.
	 */
	public int getMaxTeams() {
		return MaxTeams;
	}

	/**
	 * Max number of players per side (BotTeamGame, BotCTFGame,
	 * BotDoubleDomination).
	 */
	protected int MaxTeamSize = 0;

	/**
	 * Max number of players per side (BotTeamGame, BotCTFGame,
	 * BotDoubleDomination).
	 */
	public int getMaxTeamSize() {
		return MaxTeamSize;
	}

	/**
	 * If the game is paused - nobody can move.
	 */
	protected boolean GamePaused = false;

	/**
	 * If the game is paused - nobody can move.
	 */
	public boolean isGamePaused() {
		return GamePaused;
	}

	/**
	 * If the game is paused just for bots - human controlled players can
	 * normally move.
	 */
	protected boolean BotsPaused = false;

	/**
	 * If the game is paused just for bots - human controlled players can
	 * normally move.
	 */
	public boolean isBotsPaused() {
		return BotsPaused;
	}

	// ///// Properties END

	// ///// Extra Java code BEGIN

	// ///// Additional code from xslt BEGIN

	public static final WorldObjectId GameInfoId = WorldObjectId.get("GameInfoId");

	public WorldObjectId getId() {
		return GameInfoId;
	}

	protected long Time = 0;

	public long getSimTime() {
		return Time;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof StubGameInfo))
			return false;
		StubGameInfo cast = (StubGameInfo) obj;
		if (this.getId() != null)
			return this.getId().equals(cast.getId());
		else
			return cast.getId() == null;
	}

	public int hashCode() {
		if (getId() != null)
			return getId().hashCode();
		return 0;
	}

	// ///// Additional code from xslt END

	// ///// Extra Java from XML BEGIN

	// ///// Extra Java from XML END

	// ///// Extra Java code END

	/**
	 * Cloning constructor.
	 */
	public StubGameInfo(StubGameInfo original) {

		this.Gametype = original.Gametype;

		this.Level = original.Level;

		this.WeaponStay = original.WeaponStay;

		this.TimeLimit = original.TimeLimit;

		this.FragLimit = original.FragLimit;

		this.GoalTeamScore = original.GoalTeamScore;

		this.MaxTeams = original.MaxTeams;

		this.MaxTeamSize = original.MaxTeamSize;

		this.GamePaused = original.GamePaused;

		this.BotsPaused = original.BotsPaused;

	}

	/**
	 * Used by Yylex to create empty message then to fill it's protected fields
	 * (Yylex is in the same package).
	 */
	public StubGameInfo() {
	}

	/**
	 * Here we save the original object for which this object is an update.
	 */
	private IWorldObject orig = null;

	public IWorldObject update(IWorldObject obj) {
		if (obj == null) {
			orig = this;
			return this;
		}
		orig = obj;
		// typecast
		StubGameInfo o = (StubGameInfo) obj;

		o.Level = Level;

		o.WeaponStay = WeaponStay;

		o.TimeLimit = TimeLimit;

		o.FragLimit = FragLimit;

		o.GoalTeamScore = GoalTeamScore;

		o.MaxTeams = MaxTeams;

		o.MaxTeamSize = MaxTeamSize;

		o.GamePaused = GamePaused;

		o.BotsPaused = BotsPaused;

		o.Time = Time;

		return o;
	}

	/**
	 * Returns original object (if method update() has already been called, for
	 * bot-programmer that is always true as the original object is updated and
	 * then the event is propagated).
	 */
	public IWorldObject getObject() {
		if (orig == null)
			return this;
		return orig;
	}

	public String toString() {
		return

		super.toString() + " | " +

		"Gametype = " + String.valueOf(Gametype) + " | " +

		"Level = " + String.valueOf(Level) + " | " +

		"WeaponStay = " + String.valueOf(WeaponStay) + " | " +

		"TimeLimit = " + String.valueOf(TimeLimit) + " | " +

		"FragLimit = " + String.valueOf(FragLimit) + " | " +

		"GoalTeamScore = " + String.valueOf(GoalTeamScore) + " | " +

		"MaxTeams = " + String.valueOf(MaxTeams) + " | " +

		"MaxTeamSize = " + String.valueOf(MaxTeamSize) + " | " +

		"GamePaused = " + String.valueOf(GamePaused) + " | " +

		"BotsPaused = " + String.valueOf(BotsPaused) + " | " + "";

	}

	public String toHtmlString() {
		return super.toString() +

		"<b>Gametype</b> : " + String.valueOf(Gametype) + " <br/> " +

		"<b>Level</b> : " + String.valueOf(Level) + " <br/> " +

		"<b>WeaponStay</b> : " + String.valueOf(WeaponStay) + " <br/> " +

		"<b>TimeLimit</b> : " + String.valueOf(TimeLimit) + " <br/> " +

		"<b>FragLimit</b> : " + String.valueOf(FragLimit) + " <br/> " +

		"<b>GoalTeamScore</b> : " + String.valueOf(GoalTeamScore) + " <br/> " +

		"<b>MaxTeams</b> : " + String.valueOf(MaxTeams) + " <br/> " +

		"<b>MaxTeamSize</b> : " + String.valueOf(MaxTeamSize) + " <br/> " +

		"<b>GamePaused</b> : " + String.valueOf(GamePaused) + " <br/> " +

		"<b>BotsPaused</b> : " + String.valueOf(BotsPaused) + " <br/> " + "";
	}

}
