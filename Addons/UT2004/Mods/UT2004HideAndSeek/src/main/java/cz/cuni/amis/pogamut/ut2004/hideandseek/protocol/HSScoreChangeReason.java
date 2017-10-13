package cz.cuni.amis.pogamut.ut2004.hideandseek.protocol;

public enum HSScoreChangeReason {
	
	SEEKER_HAS_CAPTURED_RUNNER(1, "Seeker has captured a runner."),
	RUNNER_REACHED_SAFE_AREA(2, "Runner has reached the safe area."),
	RUNNER_SURVIVED_THE_ROUND(3, "Runner has survived the round without being captured."),
	RUNNER_FAULED_DUE_TO_BEING_IN_RESTRICTED_AREA(4, "Runner has been fauled out due to being in the restricted area at the beginning of the round."),
	RUNNER_SPOTTED_BY_SEEKER(5, "Runner has been spotted by the seeker"),
	SEEKER_SPOTTED_RUNNER(6, "Seeker has spotted runner."),
	SEEKER_LET_RUNNER_ESCAPE(7, "Seeker has let some runner reach safe area."), 
	RUNNER_CAPTURED_BY_SEEKER(8, "Runner has been captured by the seeker."),
	SEEKER_FAULED_DUE_TO_BEING_IN_RESTRICTED_AREA_TOO_LONG(9, "Seeker has been fauled out due to dwelling in the restricted area for too long."),
	;
	
	public final int number;
	public final String message;
	
	private HSScoreChangeReason(int number, String message) {
		this.number = number;
		this.message = message;
	}
	
	public static HSScoreChangeReason getScoreChangeReason(int number) {
		for (HSScoreChangeReason scoreChangeReason : HSScoreChangeReason.values()) {
			if (scoreChangeReason.number == number) return scoreChangeReason;
		}
		return null;
	}

}
