package cz.cuni.amis.pogamut.ut2004.bot.killbot;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Respawn;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.utils.flag.FlagInteger;

public class KillBotController extends UT2004BotModuleController<UT2004Bot> {
	
	KillBotParameters params;
	
	boolean turned = false;
	
	boolean utilRespawn = false;
	
	FlagInteger killed = new FlagInteger(0);
	
	@Override
	public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig,
			InitedMessage init) {
		super.botInitialized(gameInfo, currentConfig, init);
		params = (KillBotParameters) bot.getParams();
		config.setManualSpawn(true);
	}
	
	@Override
	public void logic() {
		if (info.getLocation().getDistance(params.getSpawningLocation()) > 100) {
			respawn();
			utilRespawn = true;
			return;
		}
		if (!turned) body.getLocomotion().turnTo(params.getTurnToLocation());
		if (players.canSeeEnemies()) {
			body.getShooting().shoot(players.getNearestVisibleEnemy());
		} else {
			body.getShooting().stopShoot();
		}
	}
	
	private void respawn() {
		getAct().act(new Respawn().setStartLocation(params.getSpawningLocation()));
	}
	
	@Override
	public void botKilled(BotKilled event) {
		turned = false;
		if (utilRespawn) {
			utilRespawn = false;
			return;
		}
		killed.increment(1);		
		getLog().warning("Bot KILLED (" + killed.getFlag() + "x)");
		respawn();
	}

	public FlagInteger getKilled() {
		return killed;
	}
	
}
