package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol;

public enum HSBotState {
	
	/**
	 * Bot that is "IT", have to seek RUNNERs out.
	 */
	SEEKER,
	/**
	 * Bot that should be "hiding" from SEEKER.
	 */
	RUNNER,
	/**
	 * RUNNER that has been already spotted by the seeker.
	 */
	RUNNER_SPOTTED,
	/**
	 * RUNNER that managed to escape by getting to the safe area before the SEEKER spot him/catches him.
	 */
	RUNNER_SAFE,
	/**
	 * RUNNER that has been spotted and tagged in SAFE area before it managed to escape.
	 */
	RUNNER_CAPTURED,
	/**
	 * RUNNER that was found within restricted area whenever the SEEKER has been spawned.
	 */
	RUNNER_FAULED,
	/**
	 * RUNNER has survived this round (that has just ended).
	 */
	RUNNER_SURVIVED,
	/**
	 * SEEKER has been fouled out due to dwelling within restricted area for too long.
	 */
	SEEKER_FOULED,

}
