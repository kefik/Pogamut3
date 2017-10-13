package cz.cuni.amis.pogamut.ut2004.hideandseek.tournament;

import java.io.File;
import java.util.Map;

import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSGameConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004MatchConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004NativeBotConfig;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapperConf;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.token.IToken;

public class UT2004HideAndSeekConfig extends UT2004MatchConfig {
	
	protected HSGameConfig hsConfig = new HSGameConfig();
	
	protected boolean humanLikeLog = false;
	
	public UT2004HideAndSeekConfig() {
		super();
		getGb2004Ini().setDMFragLimit(999);
		getGb2004Ini().setDMTimeLimit(999);
		getGb2004Ini().getSection("GameBots2004.ControlServer").set("UpdateTime", "0.1500");
		getGb2004Ini().getSection("GameBots2004.ControlConnection").set("UpdateTime", "0.1500");
		getUccConf().setGameType("BotDeathMatch");
		setMatchId("HideAndSeekMatch"); //default one
	}
		
	public HSGameConfig getHsConfig() {
		return hsConfig;
	}

	public void setHsConfig(HSGameConfig hsConfig) {
		this.hsConfig = hsConfig;
	}

	@Override
	public UT2004HideAndSeekConfig clearBots() {
		super.clearBots();
		return this;
	}
	
	@Override
	public UT2004HideAndSeekConfig clearNativeBots() {
		super.clearNativeBots();
		return this;
	}
	
	@Override
	public UT2004HideAndSeekConfig setOutputDirectory(File outputDirectory) {
		super.setOutputDirectory(outputDirectory);
		return this;
	}

	public UT2004HideAndSeekConfig setUccConf(UCCWrapperConf uccConf) {
		super.setUccConf(uccConf);
		return this;
	}
	
	public UT2004HideAndSeekConfig setBots(Map<IToken, UT2004BotConfig> bots) {
		super.setBots(bots);
		return this;
	}
	
	public UT2004HideAndSeekConfig setNativeBots(Map<IToken, UT2004NativeBotConfig> nativeBots) {
		super.setNativeBots(nativeBots);
		return this;
	}
	
	public UT2004HideAndSeekConfig addBot(UT2004BotConfig... bots) {
		super.addBot(bots);
		return this;
	}
	
	public UT2004MatchConfig setBot(UT2004BotConfig... bots) {
		super.setBot(bots);
		return this;
	}
	
	public UT2004HideAndSeekConfig addNativeBot(UT2004NativeBotConfig... bots) {
		throw new RuntimeException("Tag! game cannot be played by native bots.");
	}
	
	public UT2004HideAndSeekConfig setNativeBot(UT2004NativeBotConfig... bots) {
		throw new RuntimeException("Tag! game cannot be played by native bots.");
	}
	
	public UT2004HideAndSeekConfig setHumanLikeLogEnabled(boolean humanLikeLog) {
		super.setHumanLikeLogEnabled(humanLikeLog);
		return this;
	}

	@Override
	protected void validateInner() {
		super.validateInner();
		
		if (hsConfig == null) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("hsConfig is null!");
			return;
			
		}
		
		if (hsConfig.isFixedSeeker() && (hsConfig.getFixedSeekerName() == null || hsConfig.getFixedSeekerName().isEmpty())) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("Fixed seeker configured as TRUE, but no name specified.");
		}
		
		if (hsConfig.getTargetMap() == null || hsConfig.getTargetMap().isEmpty()) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("No targetMap specified.");
		}
		
		if (hsConfig.getHideTimeUT() + hsConfig.getRestrictedAreaTimeSecs() >= hsConfig.getRoundTimeUT()) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("HideTime + RestrictedAreaTime == " + (hsConfig.getHideTimeUT() + hsConfig.getRestrictedAreaTimeSecs()) + " >= " + hsConfig.getRoundTimeUT() + " ==  RoundTime, invalid. HideTime and RestrictedTime are included within RoundTime.");
		}
		
		if (hsConfig.getRoundCount() <= 0) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("RoundCount == " + hsConfig.getRoundCount() + " <= 0, invalid. There must be at least 1 round to be played.");
		}
		
		if (hsConfig.getSafeArea() == null) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("SafeArea not specified, is null.");
		}
		
		if (hsConfig.getSafeAreaRadius() < 25) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("SafeAreaRadius == " + hsConfig.getSafeAreaRadius() + " < 25, invalid, UT2004 sensor snapshots are not that precise to be able to handle small areas.");
		}
		
		if (hsConfig.getRestrictedAreaRadius() < 25) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("RestrictedAreaRadius == " + hsConfig.getRestrictedAreaRadius() + " < 25, invalid, UT2004 sensor snapshots are not that precise to be able to handle small areas.");
		}
		
		if (hsConfig.getSafeAreaRadius() > hsConfig.getRestrictedAreaRadius()) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("RestrictedAreaRadius == " + hsConfig.getRestrictedAreaRadius() + " < " + hsConfig.getSafeAreaRadius() + " == SafeAreaRadius, invalid, restricted area must be greater than safe area.");
		}

		if (hsConfig.getSpawnRadiusForRunners() < UnrealUtils.CHARACTER_COLLISION_RADIUS) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("SpawnRadiusForRunners == " + hsConfig.getSpawnRadiusForRunners() + " < " + UnrealUtils.CHARACTER_COLLISION_RADIUS + " == UnrealUtils.CHARACTER_COLLISION_RADIUS, runners won't have enough place for spawning.");
		}
		
	}

}
