package cz.cuni.amis.pogamut.ut2004.tag.tournament;

import java.io.File;
import java.util.Map;

import cz.cuni.amis.pogamut.ut2004.tournament.GameBots2004Ini;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004MatchConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004NativeBotConfig;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapperConf;
import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.token.IToken;

public class UT2004TagConfig extends UT2004MatchConfig {
	
	protected int tagTimeLimitInMin = 20;
	
	protected int tagLimit = 20;
	
	protected boolean humanLikeLog = false;
	
	public UT2004TagConfig() {
		super();
		getGb2004Ini().setDMFragLimit(20);
		getGb2004Ini().setDMTimeLimit(tagTimeLimitInMin);
		getGb2004Ini().setControlConnectionUpdateTime(0.1);
		getGb2004Ini().getSection("GameBots2004.ControlServer").set("UpdateTime", "0.1000");
		getGb2004Ini().getSection("GameBots2004.ControlConnection").set("UpdateTime", "0.1000");
		getUccConf().setGameType("BotDeathMatch");
		setMatchId("TagMatch"); //default one
	}
	
	/**
	 * Copy-constructor.
	 * @param orig
	 */
	public UT2004TagConfig(UT2004TagConfig orig) {
		super(orig);
		this.setTagLimit(orig.getTagLimit());
		this.setTagTimeLimit(orig.getTagTimeLimit());
	}
	
	public int getTagLimit() {
		return tagLimit;
	}

	public void setTagLimit(int tagLimit) {
		this.tagLimit = tagLimit;
	}

	/**
	 * Returns time limit of the match in MINUTES.
	 * @return
	 */
	public int getTagTimeLimit() {
		return tagTimeLimitInMin;
	}
	
	@Override
	public UT2004TagConfig clearBots() {
		super.clearBots();
		return this;
	}
	
	@Override
	public UT2004TagConfig clearNativeBots() {
		super.clearNativeBots();
		return this;
	}
	
	@Override
	public UT2004TagConfig setOutputDirectory(File outputDirectory) {
		super.setOutputDirectory(outputDirectory);
		return this;
	}

	/**
	 * Alters GB2004Ini as well.
	 * @param timeLimitInMinutes
	 * @return
	 */
	public UT2004TagConfig setTagTimeLimit(int timeLimitInMinutes) {
		if (timeLimitInMinutes < 1) {
			throw new PogamutException("Time limit can't be " + timeLimitInMinutes + " < 1 min.", this);
		}
		this.tagTimeLimitInMin = timeLimitInMinutes;
		getGb2004Ini().setDMTimeLimit(timeLimitInMinutes);
		return this;
	}


	public UT2004TagConfig setUccConf(UCCWrapperConf uccConf) {
		super.setUccConf(uccConf);
		return this;
	}
	
	/**
	 * Values from current Tag/Time limit are automatically copied into the ini.
	 */
	public UT2004TagConfig setGb2004Ini(GameBots2004Ini gb2004Ini) {
		super.setGb2004Ini(gb2004Ini);
		if (getGb2004Ini() != null) {
			getGb2004Ini().setDMFragLimit(20);
			if (getGb2004Ini().getDMTimeLimit() != null) {
				this.tagTimeLimitInMin = getGb2004Ini().getDMTimeLimit();
			} else {
				getGb2004Ini().setDMTimeLimit(tagTimeLimitInMin);
			}
		}
		return this;
	}

	public UT2004TagConfig setBots(Map<IToken, UT2004BotConfig> bots) {
		super.setBots(bots);
		return this;
	}
	
	public UT2004TagConfig setNativeBots(Map<IToken, UT2004NativeBotConfig> nativeBots) {
		super.setNativeBots(nativeBots);
		return this;
	}
	
	public UT2004TagConfig addBot(UT2004BotConfig... bots) {
		super.addBot(bots);
		return this;
	}
	
	public UT2004MatchConfig setBot(UT2004BotConfig... bots) {
		super.setBot(bots);
		return this;
	}
	
	public UT2004TagConfig addNativeBot(UT2004NativeBotConfig... bots) {
		throw new RuntimeException("Tag! game cannot be played by native bots.");
	}
	
	public UT2004TagConfig setNativeBot(UT2004NativeBotConfig... bots) {
		throw new RuntimeException("Tag! game cannot be played by native bots.");
	}
	
	public UT2004TagConfig setHumanLikeLogEnabled(boolean humanLikeLog) {
		super.setHumanLikeLogEnabled(humanLikeLog);
		return this;
	}

	@Override
	protected void validateInner() {
		super.validateInner();
		if (tagLimit <= 0) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("TagLimit = " + tagLimit + " <= 0");
		}
		if (tagTimeLimitInMin < 1) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("TimeLimit = " + tagTimeLimitInMin + " < 1 min.");
		}
		if (getGb2004Ini().getDMTimeLimit() < 1) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("GameBots2004.ini TimeLimit = " + getGb2004Ini().getDMTimeLimit() + " < 1 min.");
		}
	}

}
