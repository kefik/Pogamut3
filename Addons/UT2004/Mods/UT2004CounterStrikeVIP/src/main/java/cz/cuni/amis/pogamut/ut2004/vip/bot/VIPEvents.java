package cz.cuni.amis.pogamut.ut2004.vip.bot;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ControlMessage;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.CSMessagesTranslator;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSAssignVIP;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSBotStateChanged;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSCounterTerroristsWin;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSMessage;
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

/**
 * VIPEvents
 * <p><p>
 * 
 * Listens to {@link IWorldView} for {@link CSMessage} descendant events (note that {@link CSMessagesTranslator} must be enabled from the outside to translate
 * {@link ControlMessage} into {@link CSMessage}s).
 * <p><p>
 * 
 * Intended to be subclass and appropriate methods {@link #hsGameStart(VIPGameStart)}, {@link #csVIPGameEnd(VIPGameEnd)}, {@link #csRoundEnd(HSPlayerImmunity)},
 * {@link #csPlayerScoreChanged(CSTeamScoreChanged)}, {@link #csVIPKilled(HSPlayerStatusChanged)} overridden.
 * <p><p>  
 * 
 * Default state: DISABLED, must be {@link #enableCSEvents()}ed manually in order to translate {@link CSMessage} events into method calls.
 * 
 * @author Jimmy
 */
public class VIPEvents {
	
	protected boolean enabled = false;

	protected IWorldView worldView;
	
	protected IWorldEventListener<CSAssignVIP> csAssignVIPListener = new IWorldEventListener<CSAssignVIP>() {

		@Override
		public void notify(CSAssignVIP event) {
			csAssignVIP(event);
		}
		
	};
	
	protected IWorldEventListener<CSBotStateChanged> csBotStateChangedListener = new IWorldEventListener<CSBotStateChanged>() {

		@Override
		public void notify(CSBotStateChanged event) {
			csBotStateChanged(event);
		}
		
	};
	
	private IWorldEventListener<CSCounterTerroristsWin> csCounterTerroristsWinListener = new IWorldEventListener<CSCounterTerroristsWin>() {

		@Override
		public void notify(CSCounterTerroristsWin event) {
			csCounterTerroristsWin(event);
		}
		
	};
	
	protected IWorldEventListener<CSRoundEnd> csRoundEndListener = new IWorldEventListener<CSRoundEnd>() {

		@Override
		public void notify(CSRoundEnd event) {
			csRoundEnd(event);
		}
		
	};
	
	protected IWorldEventListener<CSRoundStart> csRoundStartListener = new IWorldEventListener<CSRoundStart>() {

		@Override
		public void notify(CSRoundStart event) {
			csRoundStart(event);
		}
		
	};
	
	protected IWorldEventListener<CSRoundState> csRoundStateListener = new IWorldEventListener<CSRoundState>() {

		@Override
		public void notify(CSRoundState event) {
			csRoundState(event);
		}
		
	};
	
	protected IWorldEventListener<CSSetVIPSafeArea> csSetVIPSafeAreaListener = new IWorldEventListener<CSSetVIPSafeArea>() {

		@Override
		public void notify(CSSetVIPSafeArea event) {
			csSetVIPSafeArea(event);
		}
		
	};
	
	private IWorldEventListener<CSTeamScoreChanged> csTeamScoreChangedListener = new IWorldEventListener<CSTeamScoreChanged>() {

		@Override
		public void notify(CSTeamScoreChanged event) {
			csTeamScoreChangedListener(event);
		}
		
	};
	
	private IWorldEventListener<CSTerroristsWin> csTerroristsWinListener = new IWorldEventListener<CSTerroristsWin>() {

		@Override
		public void notify(CSTerroristsWin event) {
			csTerroristsWin(event);
		}
		
	};
	
	protected IWorldEventListener<CSVIPKilled> csVIPKilledListener = new IWorldEventListener<CSVIPKilled>() {

		@Override
		public void notify(CSVIPKilled event) {
			csVIPKilled(event);
		}
		
	};
	
	protected IWorldEventListener<CSVIPSafe> csVIPSafeListener = new IWorldEventListener<CSVIPSafe>() {

		@Override
		public void notify(CSVIPSafe event) {
			csVIPSafe(event);
		}
		
	};
	
	protected IWorldEventListener<VIPGameEnd> csVIPGameEndListener = new IWorldEventListener<VIPGameEnd>() {

		@Override
		public void notify(VIPGameEnd event) {
			csVIPGameEnd(event);
		}
		
	};
	
	protected IWorldEventListener<VIPGameStart> csVIPGameStartListener = new IWorldEventListener<VIPGameStart>() {

		@Override
		public void notify(VIPGameStart event) {
			csVIPGameStart(event);
		}
		
	};

	public VIPEvents(IWorldView worldView) {
		this.worldView = worldView;
	}
	
	

	public void enableCSEvents() {
		if (enabled) return;
		enabled = true;
		
		worldView.addEventListener(CSAssignVIP.class, csAssignVIPListener);
		worldView.addEventListener(CSBotStateChanged.class, csBotStateChangedListener);
		worldView.addEventListener(CSCounterTerroristsWin.class, csCounterTerroristsWinListener);
		worldView.addEventListener(CSRoundEnd.class, csRoundEndListener);
		worldView.addEventListener(CSRoundStart.class, csRoundStartListener);		
		worldView.addEventListener(CSRoundState.class, csRoundStateListener);
		worldView.addEventListener(CSSetVIPSafeArea.class, csSetVIPSafeAreaListener);
		worldView.addEventListener(CSTeamScoreChanged.class, csTeamScoreChangedListener);
		worldView.addEventListener(CSTerroristsWin.class, csTerroristsWinListener);
		worldView.addEventListener(CSVIPKilled.class, csVIPKilledListener);		
		worldView.addEventListener(CSVIPSafe.class, csVIPSafeListener);
		worldView.addEventListener(VIPGameEnd.class, csVIPGameEndListener);
		worldView.addEventListener(VIPGameStart.class, csVIPGameStartListener);
	}
	
	public void disableTagEvents() {
		if (!enabled) return;
		enabled = false;
		
		worldView.removeEventListener(CSAssignVIP.class, csAssignVIPListener);
		worldView.removeEventListener(CSBotStateChanged.class, csBotStateChangedListener);
		worldView.removeEventListener(CSCounterTerroristsWin.class, csCounterTerroristsWinListener);
		worldView.removeEventListener(CSRoundEnd.class, csRoundEndListener);
		worldView.removeEventListener(CSRoundStart.class, csRoundStartListener);		
		worldView.removeEventListener(CSRoundState.class, csRoundStateListener);
		worldView.removeEventListener(CSSetVIPSafeArea.class, csSetVIPSafeAreaListener);
		worldView.removeEventListener(CSTeamScoreChanged.class, csTeamScoreChangedListener);
		worldView.removeEventListener(CSTerroristsWin.class, csTerroristsWinListener);
		worldView.removeEventListener(CSVIPKilled.class, csVIPKilledListener);		
		worldView.removeEventListener(CSVIPSafe.class, csVIPSafeListener);
		worldView.removeEventListener(VIPGameEnd.class, csVIPGameEndListener);
		worldView.removeEventListener(VIPGameStart.class, csVIPGameStartListener);
	}
	
	// ================
	// EVENTS TO HANDLE
	// ================	
	
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
