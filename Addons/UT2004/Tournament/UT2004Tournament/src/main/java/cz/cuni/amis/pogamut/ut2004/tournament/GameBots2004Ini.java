package cz.cuni.amis.pogamut.ut2004.tournament;

import java.io.File;
import java.io.InputStream;

import cz.cuni.amis.utils.IniFile;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Ordinary {@link IniFile} that loads its defaults from classpath:/cz/cuni/amis/pogamut/ut2004/tournament/deathmatch/GameBots2004-Deathmatch.ini 
 * if not specified.
 * <p><p>
 * Additionally it provides definitions of common constants that applies to the GameBots2004.ini as well as handy shortcuts for setting
 * various interesting properties such as time limit or frag limit, etc.
 * 
 * @author Jimmy
 */
public class GameBots2004Ini extends IniFile {

	//
	// SECTION
	// 
	
	public static final String Section_GameBots2004_BotConnection = "GameBots2004.BotConnection";
	public static final String Section_GameBots2004_RemoteBot = "GameBots2004.RemoteBot";
	public static final String Section_GameBots2004_GBHUD = "GameBots2004.GBHUD";
	public static final String Section_GameBots2004_ControlConnection = "GameBots2004.ControlConnection";
	public static final String Section_GameBots2004_ControlServer = "GameBots2004.ControlServer";
	public static final String Section_GameBots2004_BotScenario = "GameBots2004.BotScenario";
	public static final String Section_GameBots2004_BotDeathMatch = "GameBots2004.BotDeathMatch";
	public static final String Section_GameBots2004_BotTeamGame = "GameBots2004.BotTeamGame";
	public static final String Section_GameBots2004_BotCTFGame = "GameBots2004.BotCTFGame";
	public static final String Section_GameBots2004_BotDoubleDomination = "GameBots2004.BotDoubleDomination";
	public static final String Section_Engine_GameInfo = "Engine.GameInfo";
	public static final String Section_UnrealGame_UnrealMPGameInfo = "UnrealGame.UnrealMPGameInfo";
	public static final String Section_UnrealGame_DeathMatch = "UnrealGame.DeathMatch";
	public static final String Section_GameBots2004_GBScenarioMutator = "GameBots2004.GBScenarioMutator";
	
	//
	// PROPERTY KEYS
	//
	
	public static final String Key_DM_TimeLimit = "TimeLimit";
	public static final String Key_DM_FragLimit = "GoalScore";
	
	public static final String Key_TDM_TimeLimit = "TimeLimit";
	public static final String Key_TDM_FragLimit = "GoalScore";
	public static final String Key_TDM_WeaponStay = "bWeaponStay";
	
	public static final String Key_CTF_TimeLimit = "TimeLimit";
	public static final String Key_CTF_ScoreLimit = "GoalScore";
	public static final String Key_CTF_WeaponStay = "bWeaponStay";
	
	public static final String Key_CC_UpdateTime = "UpdateTime";	
	
	/**
	 * Constructs Ini file with defaults taken from 'classpath:/cz/cuni/amis/pogamut/ut2004/tournament/deathmatch/GameBots2004-Deathmatch.ini'.
	 */
	public GameBots2004Ini() {
		InputStream defaults = GameBots2004Ini.class.getResourceAsStream("/cz/cuni/amis/pogamut/ut2004/tournament/deathmatch/GameBots2004-Deathmatch.ini");
		load(defaults);
	}
	
	/**
	 * Constructs GameBots2004Ini with defaults taken 'source' (file must exists!).
	 * 
	 * @param source
	 */
	public GameBots2004Ini(File source) {
		if (!source.exists()) {
			throw new PogamutException("File with defaults for GameBots2004.ini does not exist at: " + source.getAbsolutePath() + ".", this);
		}
		load(source);
	}
	
	public GameBots2004Ini(GameBots2004Ini gb2004Ini) {
		super(gb2004Ini);
	}
	
	/**
	 * Returns time limit of the team death match game in minutes (or null if not specified).
	 * @return
	 */
	public Integer getTDMTimeLimit() {
		String value = getOne(Section_GameBots2004_BotTeamGame, Key_TDM_TimeLimit);
		if (value == null) return null;
		return Integer.parseInt(value);
	}
	
	/**
	 * Sets time limit of the team death match game in minutes.
	 * 
	 * @param timeLimitInMin
	 */
	public void setTDMTimeLimit(int timeLimitInMin) {
		set(Section_GameBots2004_BotTeamGame, Key_TDM_TimeLimit, String.valueOf(timeLimitInMin));
	}
	
	/**
	 * Gets frag limit of the team death match game (or null if not specified).
	 * @return
	 */
	public Integer getTDMFragLimit() {
		String value = getOne(Section_GameBots2004_BotTeamGame, Key_TDM_FragLimit);
		if (value == null) return null;
		return Integer.parseInt(value);
	}
	
	/**
	 * Returns  TeamDeathMatch weapon stay config.
	 * @return
	 */
	public Boolean getTDMWeaponStay() {
		String value = getOne(Section_GameBots2004_BotTeamGame, Key_TDM_WeaponStay);
		if (value == null) return null;
		return value.trim().toLowerCase().equals("true");
	}
	
	/**
	 * Sets TeamDeathMatch weapon stay.
	 * 
	 * @param weaponStay
	 */
	public void setTDMWeaponStay(boolean weaponStay) {
		set(Section_GameBots2004_BotTeamGame, Key_TDM_WeaponStay, String.valueOf(weaponStay));
	}
	
	/**
	 * Sets frag limit of the team death match game.
	 * @param fragLimitInSecs
	 */
	public void setTDMFragLimit(int fragLimit) {
		set(Section_GameBots2004_BotTeamGame, Key_TDM_FragLimit, String.valueOf(fragLimit));
	}

	/**
	 * Returns time limit of the death match game in minutes (or null if not specified).
	 * @return
	 */
	public Integer getDMTimeLimit() {
		String value = getOne(Section_GameBots2004_BotDeathMatch, Key_DM_TimeLimit);
		if (value == null) return null;
		return Integer.parseInt(value);
	}
	
	/**
	 * Sets time limit of the death match game in minutes.
	 * 
	 * @param timeLimitInMin
	 */
	public void setDMTimeLimit(int timeLimitInMin) {
		set(Section_GameBots2004_BotDeathMatch, Key_DM_TimeLimit, String.valueOf(timeLimitInMin));
	}
	
	/**
	 * Gets frag limit of the death match game (or null if not specified).
	 * @return
	 */
	public Integer getDMFragLimit() {
		String value = getOne(Section_GameBots2004_BotDeathMatch, Key_DM_FragLimit);
		if (value == null) return null;
		return Integer.parseInt(value);
	}
	
	/**
	 * Sets frag limit of the death match game.
	 * @param fragLimitInSecs
	 */
	public void setDMFragLimit(int fragLimit) {
		set(Section_GameBots2004_BotDeathMatch, Key_DM_FragLimit, String.valueOf(fragLimit));
	}
	
	/**
	 * Returns time limit of the capture-the-flag game in minutes (or null if not specified).
	 * @return
	 */
	public Integer getCTFTimeLimit() {
		String value = getOne(Section_GameBots2004_BotCTFGame, Key_CTF_TimeLimit);
		if (value == null) return null;
		return Integer.parseInt(value);
	}
	
	/**
	 * Sets time limit of the capture-the-flag game in minutes.
	 * 
	 * @param timeLimitInMin
	 */
	public void setCTFTimeLimit(int timeLimitInMin) {
		set(Section_GameBots2004_BotCTFGame, Key_CTF_TimeLimit, String.valueOf(timeLimitInMin));
	}
	
	/**
	 * Gets frag limit of the capture-the-flag game (or null if not specified).
	 * @return
	 */
	public Integer getCTFScoreLimit() {
		String value = getOne(Section_GameBots2004_BotCTFGame, Key_CTF_ScoreLimit);
		if (value == null) return null;
		return Integer.parseInt(value);
	}
	
	/**
	 * Sets score limit of the capture-the-flag game.
	 * @param fragLimitInSecs
	 */
	public void setCTFScoreLimit(int scoreLimit) {
		set(Section_GameBots2004_BotCTFGame, Key_CTF_ScoreLimit, String.valueOf(scoreLimit));
	}
	
	/**
	 * Returns update time (in seconds) set for ControlConnection. 
	 * @return
	 */
	public Double getControlConnectionUpdateTime() {
		String value = getOne(Section_GameBots2004_BotCTFGame, Key_CTF_ScoreLimit);
		if (value == null) return null;
		return Double.parseDouble(value);
	}

	/**
	 * Sets frequency of updates for control connection.
	 * @param timeInSeconds
	 */
	public void setControlConnectionUpdateTime(double timeInSeconds) {
		set(Section_GameBots2004_ControlConnection, Key_CC_UpdateTime, String.valueOf(timeInSeconds));		
	}
	
}
