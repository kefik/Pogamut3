package cz.cuni.amis.pogamut.ut2004.vip.bot;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.teamcomm.bot.UT2004TCClient;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSAssignVIP;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSBotStateChanged;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSCounterTerroristsWin;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSRoundEnd;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSRoundStart;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSRoundState;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSSetVIPSafeArea;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSTeamScoreChanged;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSTerroristsWin;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSVIPKilled;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSVIPSafe;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.VIPGameEnd;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.VIPGameStart;

public class UT2004BotVIPController<BOT extends UT2004Bot> extends UT2004BotModuleController<BOT> {

	protected VIPBotModule vip;
	
	protected VIPEvents vipEvents;
	
	protected UT2004TCClient tcClient;
	
	protected void initializeModules(BOT bot) {
		super.initializeModules(bot);
		vip = new VIPBotModule(bot, info, players);
		vipEvents = new VIPEvents(bot.getWorldView()) {
			protected void csAssignVIP(CSAssignVIP event) {
				UT2004BotVIPController.this.csAssignVIP(event);
			}

			protected void csBotStateChanged(CSBotStateChanged event) {
				UT2004BotVIPController.this.csBotStateChanged(event);
			}

			protected void csCounterTerroristsWin(CSCounterTerroristsWin event) {
				UT2004BotVIPController.this.csCounterTerroristsWin(event);
			}

			protected void csRoundEnd(CSRoundEnd event) {
				UT2004BotVIPController.this.csRoundEnd(event);
			}

			protected void csRoundStart(CSRoundStart event) {
				UT2004BotVIPController.this.csRoundStart(event);
			}

			protected void csRoundState(CSRoundState event) {
				UT2004BotVIPController.this.csRoundState(event);
			}

			protected void csSetVIPSafeArea(CSSetVIPSafeArea event) {
				UT2004BotVIPController.this.csSetVIPSafeArea(event);
			}

			protected void csTeamScoreChangedListener(CSTeamScoreChanged event) {
				UT2004BotVIPController.this.csTeamScoreChangedListener(event);
			}

			protected void csTerroristsWin(CSTerroristsWin event) {
				UT2004BotVIPController.this.csTerroristsWin(event);
			}

			protected void csVIPKilled(CSVIPKilled event) {
				UT2004BotVIPController.this.csVIPKilled(event);
			}

			protected void csVIPSafe(CSVIPSafe event) {
				UT2004BotVIPController.this.csVIPSafe(event);
			}

			protected void csVIPGameEnd(VIPGameEnd event) {
				UT2004BotVIPController.this.csVIPGameEnd(event);
			}

			protected void csVIPGameStart(VIPGameStart event) {
				UT2004BotVIPController.this.csVIPGameStart(event);
			}
		};
		vipEvents.enableCSEvents();
		tcClient = new UT2004TCClient(getBot(), getWorldView());
	}
	
	public VIPBotModule getVip() {
		return vip;
	}

	public UT2004TCClient getTCClient() {
		return tcClient;
	}

	protected void csAssignVIP(CSAssignVIP event) {
	}

	protected void csBotStateChanged(CSBotStateChanged event) {
	}

	protected void csCounterTerroristsWin(CSCounterTerroristsWin event) {
	}

	protected void csRoundEnd(CSRoundEnd event) {
	}

	protected void csRoundStart(CSRoundStart event) {
	}

	protected void csRoundState(CSRoundState event) {
	}

	protected void csSetVIPSafeArea(CSSetVIPSafeArea event) {
	}

	protected void csTeamScoreChangedListener(CSTeamScoreChanged event) {
	}

	protected void csTerroristsWin(CSTerroristsWin event) {
	}

	protected void csVIPKilled(CSVIPKilled event) {
	}

	protected void csVIPSafe(CSVIPSafe event) {
	}

	protected void csVIPGameEnd(VIPGameEnd event) {
	}

	protected void csVIPGameStart(VIPGameStart event) {
	}
	
}
