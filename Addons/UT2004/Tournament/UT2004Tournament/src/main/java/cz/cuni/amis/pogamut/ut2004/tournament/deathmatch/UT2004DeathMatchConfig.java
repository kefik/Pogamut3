package cz.cuni.amis.pogamut.ut2004.tournament.deathmatch;

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

public class UT2004DeathMatchConfig extends UT2004MatchConfig {
	
	protected int fragLimit = 20;
	
	protected int timeLimitInMin = 20;
	
	protected boolean humanLikeLog = false;
	
	public UT2004DeathMatchConfig() {
		super();
		getGb2004Ini().setDMFragLimit(fragLimit);
		getGb2004Ini().setDMTimeLimit(timeLimitInMin);
		getUccConf().setGameType("BotDeathMatch");
	}
	
	/**
	 * Copy-constructor.
	 * @param orig
	 */
	public UT2004DeathMatchConfig(UT2004DeathMatchConfig orig) {
		super(orig);
		this.setFragLimit(orig.getFragLimit());
		this.setTimeLimit(orig.getTimeLimit());
	}
	
	/**
	 * Returns frag limit (score limit) of the match.
	 * @return
	 */
	public int getFragLimit() {
		return fragLimit;
	}

	/**
	 * Returns time limit of the match in seconds.
	 * @return
	 */
	public int getTimeLimit() {
		return timeLimitInMin;
	}
	
	@Override
	public UT2004DeathMatchConfig clearBots() {
		super.clearBots();
		return this;
	}
	
	@Override
	public UT2004DeathMatchConfig clearNativeBots() {
		super.clearNativeBots();
		return this;
	}
	
	@Override
	public UT2004DeathMatchConfig setOutputDirectory(File outputDirectory) {
		super.setOutputDirectory(outputDirectory);
		return this;
	}

	/**
	 * Alters GB2004Ini as well.
	 * @param fragLimit
	 * @return
	 */
	public UT2004DeathMatchConfig setFragLimit(int fragLimit) {
		if (fragLimit <= 0) {
			throw new PogamutException("Frag limit can't be " + fragLimit + " <= 0.", this);
		}
		this.fragLimit = fragLimit;
		getGb2004Ini().setDMFragLimit(fragLimit);
		return this;
	}

	/**
	 * Alters GB2004Ini as well.
	 * @param timeLimitInMinutes
	 * @return
	 */
	public UT2004DeathMatchConfig setTimeLimit(int timeLimitInMinutes) {
		if (timeLimitInMinutes < 1) {
			throw new PogamutException("Time limit can't be " + timeLimitInMinutes + " < 1 min.", this);
		}
		this.timeLimitInMin = timeLimitInMinutes;
		getGb2004Ini().setDMTimeLimit(timeLimitInMinutes);
		return this;
	}


	public UT2004DeathMatchConfig setUccConf(UCCWrapperConf uccConf) {
		super.setUccConf(uccConf);
		return this;
	}
	
	/**
	 * Values from current Frag/Time limit are automatically copied into the ini.
	 */
	public UT2004DeathMatchConfig setGb2004Ini(GameBots2004Ini gb2004Ini) {
		super.setGb2004Ini(gb2004Ini);
		if (getGb2004Ini() != null) {
			if (getGb2004Ini().getDMFragLimit() != null) {
				this.fragLimit = getGb2004Ini().getDMFragLimit();
			} else {
				getGb2004Ini().setDMFragLimit(fragLimit);
			}
			if (getGb2004Ini().getDMTimeLimit() != null) {
				this.timeLimitInMin = getGb2004Ini().getDMTimeLimit();
			} else {
				getGb2004Ini().setDMTimeLimit(timeLimitInMin);
			}
		}
		return this;
	}

	public UT2004DeathMatchConfig setBots(Map<IToken, UT2004BotConfig> bots) {
		super.setBots(bots);
		return this;
	}
	
	public UT2004DeathMatchConfig setNativeBots(Map<IToken, UT2004NativeBotConfig> nativeBots) {
		super.setNativeBots(nativeBots);
		return this;
	}
	
	public UT2004DeathMatchConfig addBot(UT2004BotConfig... bots) {
		super.addBot(bots);
		return this;
	}
	
	public UT2004MatchConfig setBot(UT2004BotConfig... bots) {
		super.setBot(bots);
		return this;
	}
	
	public UT2004DeathMatchConfig addNativeBot(UT2004NativeBotConfig... bots) {
		super.addNativeBot(bots);
		return this;
	}
	
	public UT2004DeathMatchConfig setNativeBot(UT2004NativeBotConfig... bots) {
		super.setNativeBot(bots);
		return this;
	}
	
	public UT2004DeathMatchConfig setHumanLikeLogEnabled(boolean humanLikeLog) {
		super.setHumanLikeLogEnabled(humanLikeLog);
		return this;
	}

	@Override
	protected void validateInner() {
		super.validateInner();
		if (fragLimit <= 0) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("FragLimit = " + fragLimit + " <= 0");
		}
		if (timeLimitInMin < 1) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("TimeLimit = " + timeLimitInMin + " < 1 min.");
		}
		if (getGb2004Ini().getDMFragLimit() <= 0) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("GameBots2004.ini FragLimit = " + getGb2004Ini().getDMFragLimit() + " <= 0.");
		}
		if (getGb2004Ini().getDMTimeLimit() < 1) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("GameBots2004.ini TimeLimit = " + getGb2004Ini().getDMTimeLimit() + " < 1 min.");
		}
	}

}
