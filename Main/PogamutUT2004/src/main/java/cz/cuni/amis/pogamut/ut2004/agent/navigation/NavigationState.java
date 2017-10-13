package cz.cuni.amis.pogamut.ut2004.agent.navigation;

public enum NavigationState {
	/** Navigation was stopped for some reason. */
	STOPPED,
	/** Navigation reached target (and is stopped). */
	TARGET_REACHED,
	/** For some reason path to target could not be computed. */
	PATH_COMPUTATION_FAILED,
	/** The bot is stucked for some reason when attempting to reach the target */
	STUCK,
	/** Navigation is working and running somewhere. */
	NAVIGATING	
}