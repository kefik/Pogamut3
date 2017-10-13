package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol;

import cz.cuni.amis.pogamut.ut2004.hideandseek.observer.HSObserver;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.messages.HSRunnerFouled;


public enum HSGameState {

	/**
	 * Switches to {@link #GAME_STARTED} once {@link UT2004HSServer#startGame(int, int)} is called.
	 */
	NOT_RUNNING(1),     
	
	/**
	 * Announces {@link HSGameStart}, switches to {@link #START_NEXT_ROUND}.
	 */
	GAME_STARTED(2), 
	
	/**
	 * Technical point, before {@link #ROUND_STARTING} that initialize round fields properly. Switches to {@link #ROUND_STARTING} almost immediately if there is any round left.
	 * If all rounds have been finished, ends the game (which will ultimately result in switching into {@link #NOT_RUNNING} state again.
	 */
	START_NEXT_ROUND(3),
	
	/**
	 * Announce {@link HSRoundStarting}, configures all bots to manual spawn, kills all bots, assign SEEKER, announces SEEKER via {@link HSAssignSeeker}, 
	 * start up {@link HSObserver} for the seeker and spawns all {@link HSBotState#RUNNER}s around the safe spot. Switches to {@link #HIDING_TIME}
	 */
	ROUND_STARTING(4), 
	
	/**
	 * When the round state switches into this state, the ROUND has been STARTED.
	 * <p><p>
	 * At this point, all RUNNERs are spawned, but the SEEKER is not present in the environment yet. 
	 * <p><p>
	 * At this point ALL RUNNERS must get out of restricted area that is present around the safe point (preferably hide itself somewhere) to
	 * prevent being {@link HSRunnerFouled}. Note that it is prohibited to linger around safe-area for a certain amount of period after the round hiding time
	 * passes by, see {@link HSGameConfig#getRestrictedAreaTimeUT()}.
	 * <p><p>
	 * Once time hiding-time passes by, switches to {@link #SPAWNING_SEEKER}.
	 */
	HIDING_TIME(5),
	
	/**
	 * Spawns {@link HSBotState#SEEKER} previously advertised, switches to {@link #RESTRICTED_AREA_ACTIVE}. 
	 * <p><p>
	 * Note that we do not provide "restricted area time left" information in this state, but bots should treat this state as if "restricted area was activated".
	 */
	SPAWNING_SEEKER(6),
	
	/**
	 * Round is fully running at this point already, {@link HSBotState#SEEKER} may start {@link HSBotSpotted}ing {@link HSBotState#RUNNER}s and they can be
	 * {@link HSBotTaggedOut}.
	 * <p><p>
	 * Any {@link HSBotState#RUNNER} that gets into the restricted-are will be {@link HSBotFauled} (killed + penalized).
	 * <p><p>
	 * That means that no {@link HSBotState#RUNNER} can reach safe area.
	 * <p><p>
	 * Once restricted-area-active-time passes by, switches to {@link #ROUND_RUNNING}. 
	 * <p><p>
	 * If it happens that all {@link HSBotState#RUNNER}s are tagged or fouled, server switches to {@link #ROUND_ENDED}.
	 */
	RESTRICTED_AREA_ACTIVE(7),
	
	/**
	 * Round is fully running at this point, {@link HSBotState#SEEKER} may start {@link HSBotSpotted}ing {@link HSBotState#RUNNER}s and they can be
	 * {@link HSBotTaggedOut}. 
	 * <p><p>
	 * Whenever {@link HSBotState#RUNNER} gets into the safe-area, it is {@link HSBotSafe}d (killed + scores points).
	 * <p><p>
	 * Whenever all {@link HSBotState#RUNNER}s are tagged, fouled or safe, or round-time-passes server switches to {@link #ROUND_ENDED}.
	 */
	ROUND_RUNNING(8),
	
	/**
	 * Configure all bots to manual spawn + kills all remaining bots (including {@link HSBotState#SEEKER}) in the game.
	 * <p><p>
	 * Switches back to {@link #START_NEXT_ROUND}.
	 */
	ROUND_ENDED(9);
	
	public final int stateNumber;
	
	private HSGameState(int stateNumber) {
		this.stateNumber = stateNumber;
	}
	
	public static HSGameState getGameState(int stateNumber) {
		for (HSGameState gameState : HSGameState.values()) {
			if (gameState.stateNumber == stateNumber) return gameState;
		}
		return null;
	}
	
}
