package cz.cuni.amis.pogamut.ut2004.hideandseek.bot;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSScoreChangeReason;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSAssignSeeker;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSBotStateChanged;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSGameEnd;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSGameStart;
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

public class UT2004BotHSController<BOT extends UT2004Bot> extends UT2004BotModuleController<BOT> {

	protected HSBotModule hide;
	
	protected HSEvents hsEvents;
	
	protected void initializeModules(BOT bot) {
		super.initializeModules(bot);
		hide = new HSBotModule(bot, info, players);
		hsEvents = new HSEvents(bot.getWorldView()) {
			@Override
			public void hsAssignSeeker(HSAssignSeeker event) {
				UT2004BotHSController.this.hsAssignSeeker(event, event.getBotId() == hide.getId());
			}
			
			@Override
			public void hsBotStateChanged(HSBotStateChanged event) {
				UT2004BotHSController.this.hsBotStateChanged(event, event.getBotId() == hide.getId());
			}
			
			@Override
			public void hsPlayerScoreChanged(HSPlayerScoreChanged event) {
				UT2004BotHSController.this.hsPlayerScoreChanged(event, event.getBotId() == hide.getId());
			}
			
			@Override
			public void hsRoundEnd(HSRoundEnd event) {
				UT2004BotHSController.this.hsRoundEnd(event);
			}
			
			@Override
			public void hsRoundStart(HSRoundStart event) {
				UT2004BotHSController.this.hsRoundStart(event);
			}
			
			@Override
			public void hsRoundState(HSRoundState event) {
				UT2004BotHSController.this.hsRoundState(event);
			}
			
			@Override
			public void hsRunnerCaptured(HSRunnerCaptured event) {
				UT2004BotHSController.this.hsRunnerCaptured(event, event.getBotId() == hide.getId());
			}
			
			@Override
			public void hsRunnerFouled(HSRunnerFouled event) {
				UT2004BotHSController.this.hsRunnerFouled(event, event.getBotId() == hide.getId());
			}
			
			@Override
			public void hsRunnerSafe(HSRunnerSafe event) {
				UT2004BotHSController.this.hsRunnerSafe(event, event.getBotId() == hide.getId());
			}
			
			@Override
			public void hsRunnerSpotted(HSRunnerSpotted event) {
				UT2004BotHSController.this.hsRunnerSpotted(event, event.getBotId() == hide.getId());
			}
			
			@Override
			public void hsRunnerSurvived(HSRunnerSurvived event) {
				UT2004BotHSController.this.hsRunnerSurvived(event, event.getBotId() == hide.getId());
			}
			@Override
		    public void hsSeekerFouled(HSSeekerFouled event) {
				UT2004BotHSController.this.hsSeekerFouled(event, event.getBotId() == hide.getId());
		    }
		};
		hsEvents.enableHSEvents();
	}

	/**
	 * Some RUNNER has just survived the round.
	 * <p><p>
	 * This event is triggered at the end of the round for every RUNNER that has not been FOULED, CAPTURED and did not make it to SAFE area.
	 * 
	 * @param event
	 */
	protected void hsRunnerSurvived(HSRunnerSurvived event, boolean me) {
	}

	/**
	 * Some RUNNER has just been spotted by the SEEKER.
	 * @param event
	 */
	protected void hsRunnerSpotted(HSRunnerSpotted event, boolean me) {
	}

	/**
	 * Some RUNNER has just made it to the safe-area.
	 * @param event
	 */
	protected void hsRunnerSafe(HSRunnerSafe event, boolean me) {		
	}

	/**
	 * Some RUNNER has just been fouled-out because it stepped into the restricted-area while it was activated.
	 * @param event
	 * @param me whether this event speaks about your bot
	 */
	protected void hsRunnerFouled(HSRunnerFouled event, boolean me) {
	}

	/**
	 * Some RUNNER has just been captured by the SEEKER, i.e., seeker has spotted the runner and made it to the safe-area before
	 * the runner.
	 * 
	 * @param event
	 * @param me whether this event speaks about your bot
	 */
	protected void hsRunnerCaptured(HSRunnerCaptured event, boolean me) {
	}

	/**
	 * Round state update has been received.
	 * @param event
	 */
	protected void hsRoundState(HSRoundState event) {
	}

	/**
	 * New round has just started, you may use this event to initialize your data-structures.
	 * @param event
	 */
	protected void hsRoundStart(HSRoundStart event) {
	}

	/**
	 * Round has just ended, you may use this event to cleanup your data-structures.
	 * @param event
	 */
	protected void hsRoundEnd(HSRoundEnd event) {
	}

	/**
	 * Some player (runner/seeker) score has just changed, you may examine the reason within {@link HSPlayerScoreChanged#getScoreChangeReason()}, see
	 * {@link HSScoreChangeReason}.
	 * 
	 * @param event
	 * @param me whether this event speaks about your bot
	 */
	protected void hsPlayerScoreChanged(HSPlayerScoreChanged event, boolean me) {
	}
	
	/**
	 * Some bot {@link HSBotState} has been updated.
	 * @param event
	 * @param me whether this event speaks about your bot
	 */
	protected void hsBotStateChanged(HSBotStateChanged event, boolean me) {		
	}	

	/**
	 * SEEKER role has been just assigned to some bot.
	 * @param event
	 * @param me whether this event speaks about your bot
	 */
	protected void hsAssignSeeker(HSAssignSeeker event, boolean me) {	
	}
	
	/**
     * SEEKER has been fouled out due to dwelling within restricted area for too long.
     */
    protected void hsSeekerFouled(HSSeekerFouled event, boolean me) {    
    }

	/**
	 * Hide and seek module providing details about the game.
	 * @return
	 */
	public HSBotModule getHideAndSeek() {
		return hide;
	};
	
}
