package cz.cuni.amis.pogamut.ut2004.vip.protocol;

import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSAssignVIP;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSSetVIPSafeArea;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.messages.CSVIPSafe;


public enum VIPGameState {

	/**
	 * Switches to {@link #GAME_STARTING} once {@link UT2004HSServer#startGame(int, int)} is called.
	 */
	NOT_RUNNING(1),     
	
	/**
	 * Switches to {@link #GAME_STARTED} once all players are received through periodical update.
	 */
	GAME_STARTING(2),
	
	/**
	 * Announces {@link CSGameStart}, switches to {@link #START_NEXT_ROUND}.
	 */
	GAME_STARTED(3), 
	
	/**
	 * Technical point, before {@link #ROUND_STARTING} that initialize round fields properly. Switches to {@link #ROUND_STARTING} almost immediately if there is any round left.
	 * If all rounds have been finished, ends the game (which will ultimately result in switching into {@link #NOT_RUNNING} state again.
	 */
	START_NEXT_ROUND(4),
	
	/**
	 * Announce {@link CSRoundStarting}, configures all bots to manual spawn, kills all bots, assign VIP, announces VIP via {@link CSAssignVIP}, 
	 * announces VIP safe area via {@link CSSetVIPSafeArea}, start up {@link VIPObserver} for the VIP and spawns all bots.
	 */
	ROUND_STARTING(5), 
	
	/**
	 * Round is fully running at this point, {@link CSBotState#VIP} may start running towards safe location.
	 * <p><p>
	 * Whenever {@link CSBotState#VIP} gets into the safe-area, it is {@link CSVIPSafe}d and counter-terrorists score.
	 * <p><p>
	 * Whenever {@link CSBotState#VIP} gets killed, terrorists score.
	 */
	ROUND_RUNNING(6),
	
	/**
	 * Round ended regularly.
	 */
	ROUND_ENDED(7),
	
	/**
	 * Configure all bots to manual spawn + kills all remaining bots in the game.
	 * <p><p>
	 * Switches back to {@link #START_NEXT_ROUND}.
	 */
	ROUND_RESET(8),
	;
	
	public final int stateNumber;
	
	private VIPGameState(int stateNumber) {
		this.stateNumber = stateNumber;
	}
	
	public static VIPGameState getGameState(int stateNumber) {
		for (VIPGameState gameState : VIPGameState.values()) {
			if (gameState.stateNumber == stateNumber) return gameState;
		}
		return null;
	}
	
}
