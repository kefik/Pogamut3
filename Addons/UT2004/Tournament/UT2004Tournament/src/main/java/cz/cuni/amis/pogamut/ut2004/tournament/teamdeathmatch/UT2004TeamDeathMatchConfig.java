package cz.cuni.amis.pogamut.ut2004.tournament.teamdeathmatch;

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

public class UT2004TeamDeathMatchConfig extends UT2004MatchConfig {
	
	protected int scoreLimit = 20;
	
	protected int timeLimitInMin = 20;
	
	public UT2004TeamDeathMatchConfig() {
		super();
		getGb2004Ini().setCTFScoreLimit(scoreLimit);
		getGb2004Ini().setCTFTimeLimit(timeLimitInMin);
		getUccConf().setGameType("BotTeamGame");
	}
	
	/**
	 * Copy-constructor.
	 * @param orig
	 */
	public UT2004TeamDeathMatchConfig(UT2004TeamDeathMatchConfig orig) {
		super(orig);
		this.setScoreLimit(orig.getScoreLimit());
		this.setTimeLimit(orig.getTimeLimit());
	}
	
	/**
	 * Returns score limit of the match.
	 * @return
	 */
	public int getScoreLimit() {
		return scoreLimit;
	}

	/**
	 * Returns time limit of the match in seconds.
	 * @return
	 */
	public int getTimeLimit() {
		return timeLimitInMin;
	}
	
	@Override
	public UT2004TeamDeathMatchConfig clearBots() {
		super.clearBots();
		return this;
	}
	
	@Override
	public UT2004TeamDeathMatchConfig clearNativeBots() {
		super.clearNativeBots();
		return this;
	}
	
	@Override
	public UT2004TeamDeathMatchConfig setOutputDirectory(File outputDirectory) {
		super.setOutputDirectory(outputDirectory);
		return this;
	}

	/**
	 * Alters GB2004Ini as well.
	 * @param scoreLimit
	 * @return
	 */
	public UT2004TeamDeathMatchConfig setScoreLimit(int scoreLimit) {
		if (scoreLimit <= 0) {
			throw new PogamutException("Score limit can't be " + scoreLimit + " <= 0.", this);
		}
		this.scoreLimit = scoreLimit;
		getGb2004Ini().setCTFScoreLimit(scoreLimit);
		return this;
	}

	/**
	 * Alters GB2004Ini as well.
	 * @param timeLimitInMinutes
	 * @return
	 */
	public UT2004TeamDeathMatchConfig setTimeLimit(int timeLimitInMinutes) {
		if (timeLimitInMinutes < 1) {
			throw new PogamutException("Time limit can't be " + timeLimitInMinutes + " < 1 min.", this);
		}
		this.timeLimitInMin = timeLimitInMinutes;
		getGb2004Ini().setCTFTimeLimit(timeLimitInMinutes);
		return this;
	}


	public UT2004TeamDeathMatchConfig setUccConf(UCCWrapperConf uccConf) {
		super.setUccConf(uccConf);
		return this;
	}
	
	/**
	 * Values from current Frag/Time limit are automatically copied into the ini.
	 */
	public UT2004TeamDeathMatchConfig setGb2004Ini(GameBots2004Ini gb2004Ini) {
		super.setGb2004Ini(gb2004Ini);
		if (getGb2004Ini() != null) {
			if (getGb2004Ini().getCTFScoreLimit() != null) {
				this.scoreLimit = getGb2004Ini().getCTFScoreLimit();
			} else {
				getGb2004Ini().setCTFScoreLimit(scoreLimit);
			}
			if (getGb2004Ini().getCTFTimeLimit() != null) {
				this.timeLimitInMin = getGb2004Ini().getCTFTimeLimit();
			} else {
				getGb2004Ini().setCTFTimeLimit(timeLimitInMin);
			}
		}
		return this;
	}

	public UT2004TeamDeathMatchConfig setBots(Map<IToken, UT2004BotConfig> bots) {
		super.setBots(bots);
		return this;
	}
	
	public UT2004TeamDeathMatchConfig setNativeBots(Map<IToken, UT2004NativeBotConfig> nativeBots) {
		super.setNativeBots(nativeBots);
		return this;
	}
	
	public UT2004TeamDeathMatchConfig addBot(UT2004BotConfig... bots) {
		super.addBot(bots);
		return this;
	}
	
	public UT2004MatchConfig setBot(UT2004BotConfig... bots) {
		super.setBot(bots);
		return this;
	}
	
	public UT2004TeamDeathMatchConfig addNativeBot(UT2004NativeBotConfig... bots) {
		super.addNativeBot(bots);
		return this;
	}
	
	public UT2004MatchConfig setNativeBot(UT2004NativeBotConfig... bots) {
		super.setNativeBot(bots);
		return this;
	}
	
	@Override
	protected void validateInner() {
		super.validateInner();
		if (scoreLimit <= 0) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("ScoreLimit = " + scoreLimit + " <= 0");
		}
		if (timeLimitInMin < 1) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("TimeLimit = " + timeLimitInMin + " < 1 min.");
		}
		if (getGb2004Ini().getCTFScoreLimit() <= 0) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("GameBots2004.ini ScoreLimit = " + getGb2004Ini().getCTFScoreLimit() + " <= 0.");
		}
		if (getGb2004Ini().getCTFTimeLimit() < 1) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("GameBots2004.ini TimeLimit = " + getGb2004Ini().getCTFTimeLimit() + " < 1 min.");
		}
	}

}
