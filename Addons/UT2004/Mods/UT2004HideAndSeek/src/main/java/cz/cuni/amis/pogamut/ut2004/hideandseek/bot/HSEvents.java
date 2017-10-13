package cz.cuni.amis.pogamut.ut2004.hideandseek.bot;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ControlMessage;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSMessagesTranslator;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSAssignSeeker;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSBotStateChanged;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSGameEnd;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSGameStart;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSMessage;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSPlayerScoreChanged;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRoundEnd;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRoundStart;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRoundState;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerCaptured;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerFouled;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerSafe;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerSpotted;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerSurvived;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSSeekerFouled;

/**
 * HideAndSeekEvents
 * <p><p>
 * 
 * Listens to {@link IWorldView} for {@link HSMessage} descendant events (note that {@link HSMessagesTranslator} must be enabled from the outside to translate
 * {@link ControlMessage} into {@link HSMessage}s).
 * <p><p>
 * 
 * Intended to be subclass and appropriate methods {@link #hsGameStart(HSGameStart)}, {@link #hsGameEnd(HSGameEnd)}, {@link #hsRoundEnd(HSPlayerImmunity)},
 * {@link #hsPlayerScoreChanged(HSPlayerScoreChanged)}, {@link #hsRunnerCaptured(HSPlayerStatusChanged)} overridden.
 * <p><p>  
 * 
 * Default state: DISABLED, must be {@link #enableHSEvents()}ed manually in order to translate {@link HSMessage} events into method calls.
 * 
 * @author Jimmy
 */
public class HSEvents {
	
	protected boolean enabled = false;

	protected IWorldView worldView;
	
	protected IWorldEventListener<HSAssignSeeker> hsAssignSeekerListener = new IWorldEventListener<HSAssignSeeker>() {

		@Override
		public void notify(HSAssignSeeker event) {
			hsAssignSeeker(event);
		}
		
	};
	
	protected IWorldEventListener<HSBotStateChanged> hsBotStateChangedListener = new IWorldEventListener<HSBotStateChanged>() {

		@Override
		public void notify(HSBotStateChanged event) {
			hsBotStateChanged(event);
		}
		
	};
	
	protected IWorldEventListener<HSGameEnd> hsGameEndListener = new IWorldEventListener<HSGameEnd>() {

		@Override
		public void notify(HSGameEnd event) {
			hsGameEnd(event);
		}
		
	};
	
	protected IWorldEventListener<HSGameStart> hsGameStartListener = new IWorldEventListener<HSGameStart>() {

		@Override
		public void notify(HSGameStart event) {
			hsGameStart(event);
		}
		
	};
	
	protected IWorldEventListener<HSPlayerScoreChanged> hsPlayerScoreChangedListener = new IWorldEventListener<HSPlayerScoreChanged>() {

		@Override
		public void notify(HSPlayerScoreChanged event) {
			hsPlayerScoreChanged(event);
		}
		
	};

	
	protected IWorldEventListener<HSRoundEnd> hsRoundEndListener = new IWorldEventListener<HSRoundEnd>() {

		@Override
		public void notify(HSRoundEnd event) {
			hsRoundEnd(event);
		}
		
	};
	
	protected IWorldEventListener<HSRoundStart> hsRoundStartListener = new IWorldEventListener<HSRoundStart>() {

		@Override
		public void notify(HSRoundStart event) {
			hsRoundStart(event);
		}
		
	};
	
	protected IWorldEventListener<HSRoundState> hsRoundStateListener = new IWorldEventListener<HSRoundState>() {

		@Override
		public void notify(HSRoundState event) {
			hsRoundState(event);
		}
		
	};
	
	protected IWorldEventListener<HSRunnerCaptured> hsRunnerCapturedListener = new IWorldEventListener<HSRunnerCaptured>() {

		@Override
		public void notify(HSRunnerCaptured event) {
			hsRunnerCaptured(event);
		}
		
	};

	protected IWorldEventListener<HSRunnerFouled> hsRunnerFouledListener = new IWorldEventListener<HSRunnerFouled>() {

		@Override
		public void notify(HSRunnerFouled event) {
			hsRunnerFouled(event);
		}
		
	};
	
	protected IWorldEventListener<HSRunnerSafe> hsRunnerSafeListener = new IWorldEventListener<HSRunnerSafe>() {

		@Override
		public void notify(HSRunnerSafe event) {
			hsRunnerSafe(event);
		}
		
	};
	
	protected IWorldEventListener<HSRunnerSpotted> hsRunnerSpottedListener = new IWorldEventListener<HSRunnerSpotted>() {

		@Override
		public void notify(HSRunnerSpotted event) {
			hsRunnerSpotted(event);
		}
		
	};
	
	protected IWorldEventListener<HSRunnerSurvived> hsRunnerSurvivedListener = new IWorldEventListener<HSRunnerSurvived>() {

		@Override
		public void notify(HSRunnerSurvived event) {
			hsRunnerSurvived(event);
		}
		
	};
	
	protected IWorldEventListener<HSSeekerFouled> hsSeekerFouledListener = new IWorldEventListener<HSSeekerFouled>() {

		@Override
		public void notify(HSSeekerFouled event) {
			hsSeekerFouled(event);
		}
		
	};

	public HSEvents(IWorldView worldView) {
		this.worldView = worldView;
	}
	
	public void enableHSEvents() {
		if (enabled) return;
		enabled = true;
		
		worldView.addEventListener(HSAssignSeeker.class, hsAssignSeekerListener);
		worldView.addEventListener(HSBotStateChanged.class, hsBotStateChangedListener);
		worldView.addEventListener(HSGameStart.class, hsGameStartListener);
		worldView.addEventListener(HSGameEnd.class, hsGameEndListener);
		worldView.addEventListener(HSPlayerScoreChanged.class, hsPlayerScoreChangedListener);
		worldView.addEventListener(HSRoundEnd.class, hsRoundEndListener);
		worldView.addEventListener(HSRoundStart.class, hsRoundStartListener);		
		worldView.addEventListener(HSRoundState.class, hsRoundStateListener);
		worldView.addEventListener(HSRunnerCaptured.class, hsRunnerCapturedListener);		
		worldView.addEventListener(HSRunnerFouled.class, hsRunnerFouledListener);
		worldView.addEventListener(HSRunnerSafe.class, hsRunnerSafeListener);
		worldView.addEventListener(HSRunnerSpotted.class, hsRunnerSpottedListener);
		worldView.addEventListener(HSRunnerSurvived.class, hsRunnerSurvivedListener);
		worldView.addEventListener(HSSeekerFouled.class, hsSeekerFouledListener);	
	}
	
	public void disableTagEvents() {
		if (!enabled) return;
		enabled = false;
		
		worldView.removeEventListener(HSAssignSeeker.class, hsAssignSeekerListener);
		worldView.removeEventListener(HSGameStart.class, hsGameStartListener);
		worldView.removeEventListener(HSGameEnd.class, hsGameEndListener);
		worldView.removeEventListener(HSPlayerScoreChanged.class, hsPlayerScoreChangedListener);
		worldView.removeEventListener(HSRoundEnd.class, hsRoundEndListener);
		worldView.removeEventListener(HSRoundStart.class, hsRoundStartListener);		
		worldView.removeEventListener(HSRoundState.class, hsRoundStateListener);
		worldView.removeEventListener(HSRunnerCaptured.class, hsRunnerCapturedListener);		
		worldView.removeEventListener(HSRunnerFouled.class, hsRunnerFouledListener);
		worldView.removeEventListener(HSRunnerSafe.class, hsRunnerSafeListener);
		worldView.removeEventListener(HSRunnerSpotted.class, hsRunnerSpottedListener);
		worldView.removeEventListener(HSRunnerSurvived.class, hsRunnerSurvivedListener);
		worldView.removeEventListener(HSSeekerFouled.class, hsSeekerFouledListener);
	}
	
	// ================
	// EVENTS TO HANDLE
	// ================
	
	public void hsAssignSeeker(HSAssignSeeker event) {		
	}
	
	public void hsBotStateChanged(HSBotStateChanged event) {		
	}
	
	public void hsGameEnd(HSGameEnd event) {
	}
	
	public void hsGameStart(HSGameStart event) {
	}
	
	public void hsPlayerScoreChanged(HSPlayerScoreChanged event) {
	}
	
	public void hsRoundEnd(HSRoundEnd event) {
	}
	
	public void hsRoundStart(HSRoundStart event) {
	}
	
	public void hsRoundState(HSRoundState event) {
	}
	
	public void hsRunnerCaptured(HSRunnerCaptured event) {
	}
	
	public void hsRunnerFouled(HSRunnerFouled event) {
	}
	
	public void hsRunnerSafe(HSRunnerSafe event) {
	}
	
	public void hsRunnerSpotted(HSRunnerSpotted event) {
	}
	
	public void hsRunnerSurvived(HSRunnerSurvived event) {
	}
	
	public void hsSeekerFouled(HSSeekerFouled event) {		
	}
	
	
}
