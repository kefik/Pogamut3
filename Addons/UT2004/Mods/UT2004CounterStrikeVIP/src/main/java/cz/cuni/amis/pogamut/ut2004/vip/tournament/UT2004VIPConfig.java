package cz.cuni.amis.pogamut.ut2004.vip.tournament;

import java.io.File;
import java.util.Map;

import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004MatchConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004NativeBotConfig;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapperConf;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.VIPGameConfig;
import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.token.IToken;

public class UT2004VIPConfig extends UT2004MatchConfig {
	
	protected VIPGameConfig vipConfig = new VIPGameConfig();
	
	protected boolean humanLikeLog = false;
	
	public UT2004VIPConfig() {
		super();
		getGb2004Ini().setTDMFragLimit(999);
		getGb2004Ini().setTDMTimeLimit(999);
		getGb2004Ini().setTDMWeaponStay(false);
		getGb2004Ini().getSection("GameBots2004.ControlServer").set("UpdateTime", "0.200");
		getGb2004Ini().getSection("GameBots2004.ControlConnection").set("UpdateTime", "0.250");		
		getUccConf().setGameType("BotTeamGame");
		setMatchId("VIP"); //default one
	}
		
	public VIPGameConfig getVIPConfig() {
		return vipConfig;
	}

	public void setHsConfig(VIPGameConfig hsConfig) {
		this.vipConfig = hsConfig;
	}

	@Override
	public UT2004VIPConfig clearBots() {
		super.clearBots();
		return this;
	}
	
	@Override
	public UT2004VIPConfig clearNativeBots() {
		super.clearNativeBots();
		return this;
	}
	
	@Override
	public UT2004VIPConfig setOutputDirectory(File outputDirectory) {
		super.setOutputDirectory(outputDirectory);
		return this;
	}

	public UT2004VIPConfig setUccConf(UCCWrapperConf uccConf) {
		super.setUccConf(uccConf);
		return this;
	}
	
	public UT2004VIPConfig setBots(Map<IToken, UT2004BotConfig> bots) {
		super.setBots(bots);
		return this;
	}
	
	public UT2004VIPConfig setNativeBots(Map<IToken, UT2004NativeBotConfig> nativeBots) {
		super.setNativeBots(nativeBots);
		return this;
	}
	
	public UT2004VIPConfig addBot(UT2004BotConfig... bots) {
		super.addBot(bots);
		return this;
	}
	
	public UT2004MatchConfig setBot(UT2004BotConfig... bots) {
		super.setBot(bots);
		return this;
	}
	
	public UT2004VIPConfig addNativeBot(UT2004NativeBotConfig... bots) {
		throw new RuntimeException("VIP! game cannot be played by native bots.");
	}
	
	public UT2004VIPConfig setNativeBot(UT2004NativeBotConfig... bots) {
		throw new RuntimeException("VIP! game cannot be played by native bots.");
	}
	
	public UT2004VIPConfig setHumanLikeLogEnabled(boolean humanLikeLog) {
		super.setHumanLikeLogEnabled(humanLikeLog);
		return this;
	}

	@Override
	protected void validateInner() {
		super.validateInner();
		
		if (vipConfig == null) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("hsConfig is null!");
			return;
			
		}
		
		if (vipConfig.isFixedVIP() && (vipConfig.getFixedVIPNamePrefix() == null || vipConfig.getFixedVIPNamePrefix().isEmpty())) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("Fixed VIP configured as TRUE, but no name prefix specified.");
		}
		
		if (vipConfig.getTargetMap() == null || vipConfig.getTargetMap().isEmpty()) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("No targetMap specified.");
		}
		
		if (vipConfig.getRoundCount() <= 0) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("RoundCount == " + vipConfig.getRoundCount() + " <= 0, invalid. There must be at least 1 round to be played.");
		}
		
		if (vipConfig.getCtsSpawnAreas() == null || vipConfig.getCtsSpawnAreas().length <= 0) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("No CtsSpawnArea specified.");
		}
		
		if (vipConfig.getTsSpawnAreas() == null || vipConfig.getTsSpawnAreas().length <= 0) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("No TsSpawnArea specified.");
		}
		
		if (vipConfig.getVipSafeAreas() == null || vipConfig.getVipSafeAreas().length <= 0) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("N VipSafeArea specified.");
		}
		
		if (vipConfig.getVipSafeAreaRadius() < 25) {
			validationError = true;
			validationBuffer.append(Const.NEW_LINE);
			validationBuffer.append("SafeAreaRadius == " + vipConfig.getVipSafeAreaRadius() + " < 25, invalid, UT2004 sensor snapshots are not that precise to be able to handle small areas.");
		}
				
	}

}
