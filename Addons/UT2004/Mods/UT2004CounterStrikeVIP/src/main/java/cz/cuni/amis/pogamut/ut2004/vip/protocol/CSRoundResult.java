package cz.cuni.amis.pogamut.ut2004.vip.protocol;

public enum CSRoundResult {
	
	VIP_HAS_BEEN_KILLED(1, "VIP got killed."),
	VIP_ESCAPED(2, "VIP managed to reach safe area."),
	VIP_LEFT(3, "VIP left the game."),
	TERRORISTS_DEAD(4, "All terrorists are dead."),
	ROUND_TIMEOUT(4, "Round timeout, VIP failed to reach safe area in time.")
	;
	
	public final int number;
	public final String message;
	
	private CSRoundResult(int number, String message) {
		this.number = number;
		this.message = message;
	}
	
	public static CSRoundResult getRoundResult(int number) {
		for (CSRoundResult scoreChangeReason : CSRoundResult.values()) {
			if (scoreChangeReason.number == number) return scoreChangeReason;
		}
		return null;
	}

}
