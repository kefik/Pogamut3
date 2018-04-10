package cz.cuni.amis.pogamut.ut2004.utils;

import java.io.Serializable;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.utils.Pogamut;

/**
 * Configuration object of the UCC wrapper instance.
 */
public class UCCWrapperConf implements Serializable {

	/**
	 * Auto-generated. 
	 */
	private static final long serialVersionUID = -8577056681683055775L;
	
	protected UCCWrapperPatterns patterns = new UCCWrapperPatterns();
	protected String unrealHome = null;
	protected String mapName = "DM-TrainingDay";
	protected String gameBotsPack = "GameBots2004";
	protected String gameType = "BotDeathMatch";
	protected String mutators = "";
	protected String options = "";
    /** Port for human player - UT2004 client uses this port to connect to the server. */    	
	protected int playerPort = -1;
	protected boolean startOnUnusedPort = true;
    transient Logger log = null;

    public UCCWrapperConf() {        	
    }
    
    public UCCWrapperConf(UCCWrapperConf uccConf) {
    	this.patterns = new UCCWrapperPatterns(uccConf.getPatterns());
		this.unrealHome = uccConf.getUnrealHome();
		this.mapName = uccConf.getMapName();
		this.gameBotsPack = uccConf.getGameBotsPack();
		this.gameType = uccConf.getGameType();
		this.mutators = uccConf.getMutators();
		this.options = uccConf.getOptions();
		this.playerPort = uccConf.getPlayerPort();
		this.startOnUnusedPort = uccConf.isStartOnUnusedPort();
		this.log = uccConf.getLog();
    }

    /** Returns the port for human player - UT2004 client uses this port to connect to the server. */
	public int getPlayerPort() {
		return playerPort;
	}

	/** Sets the port for human player - UT2004 client uses this port to connect to the server. */
	public void setPlayerPort(int playerPort) {
		this.playerPort = playerPort;
	}

	@Override
    public String toString() {
    	if (this == null) return "UCCWrapperConf";
    	return getClass().getSimpleName() + "[unrealHome=" + unrealHome + ", gameBotsPack=" + gameBotsPack + ", gameType=" + gameType + ", mutators=" + mutators + ", options=" + options + ", startOnUnusedPorts=" + startOnUnusedPort + "]";
    }
    
	/**
	 * Sets patterns that recognizes UCC output for successful startup.		
	 * @return
	 */
    public UCCWrapperPatterns getPatterns() {
		return patterns;
	}

    /**
     * Sets patterns that recognizes UCC output for successful startup.
     * @param patterns
     */
	public void setPatterns(UCCWrapperPatterns patterns) {
		this.patterns = patterns;
	}

	/**
     * Returns Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UNREAL_HOME) if not specified.
     * @return
     */
    public String getUnrealHome() {
    	if (unrealHome == null) {
            return Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UNREAL_HOME.getKey());
        } else {
            return unrealHome;
        }
	}

    /**
     * Sets path to the UT2004 directory, e.g., 'd:\\games\\ut2004'.
     * <p><p>
     * Should not need to be set if provided via property file.
     * 
     * @param unrealHome
     */
	public UCCWrapperConf setUnrealHome(String unrealHome) {
		this.unrealHome = unrealHome;
		return this;
	}

	/**
     * Forces UCC to find free port and start on it, otherwise it will start on ports 3000 + 3001.
     * @param startOnUnusedPort
     */
    public UCCWrapperConf setStartOnUnusedPort(boolean startOnUnusedPort) {
        this.startOnUnusedPort = startOnUnusedPort;
        return this;
    }

    /**
     * Eg. GameBots2004, GBSceanrio etc.
     * @param gameBotsPack
     */
    public UCCWrapperConf setGameBotsPack(String gameBotsPack) {
        this.gameBotsPack = gameBotsPack;
        return this;
    }

    public UCCWrapperConf setMapName(String mapName) {
        this.mapName = mapName;
        return this;
    }

    /**
     * Eg. BotDeathMatch, BotCTFGame etc. Consult GameBots documentation for
     * complete list available game types.
     */
    public UCCWrapperConf setGameType(String gameType) {
        this.gameType = gameType;
        return this;
    }
    
    /**
     * Sets a game type from an enum so you do not have to remember concrete strings.
     * @param gameType
     * @return
     */
    public UCCWrapperConf setGameType(UCCGameType gameType) {
    	this.gameType = gameType.gameType;
    	return this;
    }

    /**
     * Can be used for setting mutators etc.
     * @param options
     */
    public UCCWrapperConf setOptions(String options) {
        this.options = options;
        return this;
    }

    /**
     * Logger used by the UCC.
     * @param log
     */
    public UCCWrapperConf setLogger(Logger log) {
        this.log = log;
        return this;
    }

	public String getMutators() {
		return mutators;
	}

	public void setMutators(String mutators) {
		this.mutators = mutators;
	}

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public String getMapName() {
		return mapName;
	}

	public String getGameBotsPack() {
		return gameBotsPack;
	}

	public String getGameType() {
		return gameType;
	}

	public String getOptions() {
		return options;
	}

	public boolean isStartOnUnusedPort() {
		return startOnUnusedPort;
	}
    
}