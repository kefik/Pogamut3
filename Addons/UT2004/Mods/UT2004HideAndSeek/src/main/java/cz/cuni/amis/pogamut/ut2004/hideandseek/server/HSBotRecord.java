package cz.cuni.amis.pogamut.ut2004.hideandseek.server;

import java.util.Map;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSBotState;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.utils.maps.LazyMap;

/**
 * BotHideAndSeekRecord
 * 
 * @author Jimmy
 *
 * @param <PLAYER_CONTAINER>
 */
public class HSBotRecord<PLAYER_CONTAINER> {
	
	private final UnrealId botId;
	
	private long initTime;
	
	private long finishTime = -1;	
	
	private boolean spawned = true;
	
	private HSBotState botState = HSBotState.RUNNER;
	
	/**
	 * How many times this bot has been SEEKER.
	 */
	private int seekerCount = 0;
	
	/**
	 * How many times this bot has been RUNNER.
	 */
	private int runnerCount = 0;
	
	// ============
	// SCORE FIELDS
	// ============
	
	/**
	 * Total score of this bot, consists of sum {@link #seekerCapturedRunnerScore}, {@link #seekerSpottedRunnerScore}, {@link #seekerLetRunnerSurviveScore}, {@link #seekerLetRunnerEscapeScore}
	 * {@link #runnerSpottedBySeekerScore}, {@link #runnerSafeScore}, {@link #runnerSurvivedScore}, {@link #runnerFoulScore}, {@link #runnerCapturedBySeekerScore}.
	 */
	private int score = 0;
	
	private int runnerCapturedBySeekerScore = 0;
	
	/**
	 * How many times THIS BOT has been RUNNER and was CAPTURED BY SEEKER while the seeker has been 'key'.
	 */
	private Map<UnrealId, Integer> runnerCapturedBySeekerCount = new LazyMap<UnrealId, Integer>() {
		@Override
		protected Integer create(UnrealId key) {
			return 0;
		}		
	};
	
	private int runnerSpottedBySeekerScore = 0;
	
	/**
	 * How may times THIS BOT has been RUNNER and was SPOTTED by the SEEKER while the seeker has been 'key'.
	 */
	private Map<UnrealId, Integer> runnerSpottedBySeekerCount = new LazyMap<UnrealId, Integer>() {
		@Override
		protected Integer create(UnrealId key) {
			return 0;
		}		
	};
	
	private int runnerFoulScore = 0;
	
	/**
	 * How many times THIS BOT has been RUNNER and was FOULED OUT while the seeker has been 'key'.
	 */
	private Map<UnrealId, Integer> runnerFoulCount = new LazyMap<UnrealId, Integer>() {
		@Override
		protected Integer create(UnrealId key) {
			return 0;
		}		
	};
	
	private int runnerSafeScore = 0;
	
	/**
	 * How many times THIS BOT has been RUNNER and made it to the SAFE AREA while the seeker has been 'key'.
	 */
	private Map<UnrealId, Integer> runnerSafeCount = new LazyMap<UnrealId, Integer>() {
		@Override
		protected Integer create(UnrealId key) {
			return 0;
		}		
	};
	
	private int runnerSurvivedScore = 0;
	
	/**
	 * How many times THIS BOT has been RUNNER and survived the round while the seeker has been 'key'.
	 */
	private Map<UnrealId, Integer> runnerSurvivedCount = new LazyMap<UnrealId, Integer>() {
		@Override
		protected Integer create(UnrealId key) {
			return 0;
		}		
	};
	
	private int seekerCapturedRunnerScore = 0;
	
	/**
	 * How many times THIS BOT has been SEEKER and CAPTURED runner under 'key'.
	 */
	private Map<UnrealId, Integer> seekerCapturedRunnerCount = new LazyMap<UnrealId, Integer>() {
		@Override
		protected Integer create(UnrealId key) {
			return 0;
		}		
	};
	
	private int seekerLetRunnerSurviveScore = 0;
	
	/**
	 * How many times THIS BOT has been SEEKER and LET SURVIVE runner under 'key'.
	 */
	private Map<UnrealId, Integer> seekerLetRunnerSurviveCount = new LazyMap<UnrealId, Integer>() {
		@Override
		protected Integer create(UnrealId key) {
			return 0;
		}		
	};
	
	private int seekerLetRunnerEscapeScore = 0;
	
	/**
	 * How many times THIS BOT has been SEEKER and LET REACH SAFE AREA runner under 'key'.
	 */
	private Map<UnrealId, Integer> seekerLetRunnerEscapeCount = new LazyMap<UnrealId, Integer>() {
		@Override
		protected Integer create(UnrealId key) {
			return 0;
		}		
	};
	
	private int seekerSpottedRunnerScore = 0;
	
	/**
	 * How many times THIS BOT has been SEEKER and SPOTTED runner under 'key'.
	 */
	private Map<UnrealId, Integer> seekerSpottedRunnerCount = new LazyMap<UnrealId, Integer>() {
		@Override
		protected Integer create(UnrealId key) {
			return 0;
		}		
	};
	
	private int seekerFoulScore = 0;
	
	/**
	 * How many times THIS BOT has been SEEKER and was FOULED OUT.
	 */
	private int seekerFoulCount;
	
	/**
	 * How long is seeker dwelling within restricted area.
	 */
	private double timeAtRestrictedArea;
	
	// =========
	// UTILITIES
	// =========
	
	private boolean inGame = false;
	
	private PLAYER_CONTAINER player;
	
	// ==============
	// IMPLEMENTATION
	// ==============
	
	public HSBotRecord(UnrealId botId) {
		this.botId = botId;
	}

	public long getInitTime() {
		return initTime;
	}

	public void setInitTime(long initTime) {
		this.initTime = initTime;
	}

	public long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	public HSBotState getBotState() {
		return botState;
	}

	public void setBotState(HSBotState botState) {
		this.botState = botState;
	}

	public boolean isInGame() {
		return inGame;
	}

	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}
	
	public boolean isSpawned() {
		return spawned;
	}

	public void setSpawned(boolean spawned) {
		this.spawned = spawned;
	}

	public UnrealId getBotId() {
		return botId;
	}
	
	public boolean isBot() {
		return UnrealUtils.isBotId(botId);
	}
	
	public String getBotName() {
		if (player instanceof PlayerMessage ) return ((PlayerMessage)player).getName();
		if (player instanceof Player) return ((Player)player).getName();		
		return null;
	}
	
	// ==============
	// SCORE & COUNTS
	// ==============

	public int getSeekerCount() {
		return seekerCount;
	}

	public int getRunnerCount() {
		return runnerCount;
	}

	public int getScore() {
		return score;
	}
	
	public void setScore(int value) {
		this.score = value;
	}

	public int getRunnerCapturedBySeekerScore() {
		return runnerCapturedBySeekerScore;
	}

	public Map<UnrealId, Integer> getRunnerCapturedBySeekerCount() {
		return runnerCapturedBySeekerCount;
	}

	public int getRunnerSpottedBySeekerScore() {
		return runnerSpottedBySeekerScore;
	}

	public Map<UnrealId, Integer> getRunnerSpottedBySeekerCount() {
		return runnerSpottedBySeekerCount;
	}

	public int getRunnerFoulScore() {
		return runnerFoulScore;
	}

	public Map<UnrealId, Integer> getRunnerFoulCount() {
		return runnerFoulCount;
	}

	public int getRunnerSafeScore() {
		return runnerSafeScore;
	}

	public Map<UnrealId, Integer> getRunnerSafeCount() {
		return runnerSafeCount;
	}

	public int getRunnerSurvivedScore() {
		return runnerSurvivedScore;
	}

	public Map<UnrealId, Integer> getRunnerSurvivedCount() {
		return runnerSurvivedCount;
	}

	public int getSeekerCapturedRunnerScore() {
		return seekerCapturedRunnerScore;
	}

	public Map<UnrealId, Integer> getSeekerCapturedRunnerCount() {
		return seekerCapturedRunnerCount;
	}

	public int getSeekerLetRunnerEscapeScore() {
		return seekerLetRunnerEscapeScore;
	}

	public Map<UnrealId, Integer> getSeekerLetRunnerEscapeCount() {
		return seekerLetRunnerEscapeCount;
	}

	public int getSeekerLetRunnerSurviveScore() {
		return seekerLetRunnerSurviveScore;
	}

	public Map<UnrealId, Integer> getSeekerLetRunnerSurviveCount() {
		return seekerLetRunnerSurviveCount;
	}

	public int getSeekerSpottedRunnerScore() {
		return seekerSpottedRunnerScore;
	}

	public Map<UnrealId, Integer> getSeekerSpottedRunnerCount() {
		return seekerSpottedRunnerCount;
	}
	
	public double getTimeAtRestrictedArea() {
		return timeAtRestrictedArea;
	}

	public void setTimeAtRestrictedArea(double timeAtRestrictedArea) {
		this.timeAtRestrictedArea = timeAtRestrictedArea;
	}
	
	// ======
	// EVENTS
	// ======
	
	/**
	 * THIS BOT HAS BEEN SET AS SEEKER.
	 */
	public void setSeekerForThisRound() {
		botState = HSBotState.SEEKER;
		++seekerCount;
	}
	
	/**
	 * THIS BOT HAS BEEN SET AS RUNNER.
	 */
	public void setRunnerForThisRound() {
		botState = HSBotState.RUNNER;
		++runnerCount;
	}

	/**
	 * THIS bot is RUNNER and was captured by OTHER SEEKER.
	 * @param scoreDelta
	 * @param seekerId
	 */
	public void runnerCapturedBySeeker(int scoreDelta, UnrealId seekerId) {
		if (botState == HSBotState.SEEKER) {
			throw new RuntimeException("SEEKER cannot be captured!");
		}
		botState = HSBotState.RUNNER_CAPTURED;
		score += scoreDelta;
		runnerCapturedBySeekerScore += scoreDelta;
		runnerCapturedBySeekerCount.put(seekerId, runnerCapturedBySeekerCount.get(seekerId) + 1);
	}

	/**
	 * THIS bot is RUNNER and was fauled out.
	 * @param scoreDelta
	 * @param seekerId
	 */
	public void runnerFauled(int scoreDelta, UnrealId seekerId) {
		if (botState == HSBotState.SEEKER) {
			throw new RuntimeException("SEEKER cannot be runner-fauled!");
		}
		botState = HSBotState.RUNNER_FAULED;
		score += scoreDelta;
		runnerFoulScore += scoreDelta;
		runnerFoulCount.put(seekerId, runnerFoulCount.get(seekerId) + 1);
	}	
	
	/**
	 * THIS BOT is RUNNER and made it SAFE to the safe-area before the seeker.
	 * @param scoreDelta
	 * @param seekerId
	 */
	public void runnerSafe(int scoreDelta, UnrealId seekerId) {
		if (botState == HSBotState.SEEKER) {
			throw new RuntimeException("SEEKER cannot be safe!");
		}
		botState = HSBotState.RUNNER_SAFE;
		score += scoreDelta;
		runnerSafeScore += scoreDelta;
		runnerSafeCount.put(seekerId, runnerSafeCount.get(seekerId) + 1);
	}
	
	/**
	 * THIS BOT is RUNNER and has been spotted by SEEKER.
	 * @param scoreDelta
	 * @param seekerId
	 */
	public void runnerSpottedBySeeker(int scoreDelta, UnrealId seekerId) {
		if (botState == HSBotState.SEEKER) {
			throw new RuntimeException("SEEKER cannot be spotted!");
		}
		botState = HSBotState.RUNNER_SPOTTED;
		score += scoreDelta;
		runnerSpottedBySeekerScore += scoreDelta;
		runnerSpottedBySeekerCount.put(seekerId, runnerSpottedBySeekerCount.get(seekerId) + 1);
	}
	
	/**
	 * THIS BOT is RUNNER and survived the round without being captured by SEEKER.
	 * @param scoreDelta
	 * @param seekerId
	 */
	public void runnerSurvived(int scoreDelta, UnrealId seekerId) {
		if (botState == HSBotState.SEEKER) {
			throw new RuntimeException("SEEKER cannot survive!");
		}
		botState = HSBotState.RUNNER_SURVIVED;
		score += scoreDelta;
		runnerSurvivedScore += scoreDelta;		
		runnerSurvivedCount.put(seekerId, runnerSurvivedCount.get(seekerId) + 1);
	}
	
	/**
	 * THIS BOT is SEEKER and CAPTURED other RUNNER.
	 * @param scoreDelta
	 * @param runnerId
	 */
	public void seekerCapturedRunner(int scoreDelta, UnrealId runnerId) {
		if (botState != HSBotState.SEEKER) throw new RuntimeException("RUNNER cannot capture!");
		score += scoreDelta;
		seekerCapturedRunnerScore += scoreDelta;
		seekerCapturedRunnerCount.put(runnerId, seekerCapturedRunnerCount.get(runnerId) + 1);
	}
	
	/**
	 * THIS BOT is SEEKER and LET other RUNNER REACH SAFE AREA.
	 * @param scoreDelta
	 * @param runnerId
	 */
	public void seekerLetRunnerEscape(int scoreDelta, UnrealId runnerId) {
		if (botState != HSBotState.SEEKER) throw new RuntimeException("RUNNER cannot let other runner escape!");
		score += scoreDelta;
		seekerLetRunnerEscapeScore += scoreDelta;
		seekerLetRunnerEscapeCount.put(runnerId, seekerLetRunnerEscapeCount.get(runnerId) + 1);
	}
	
	/**
	 * THIS BOT is SEEKER and LET SURVIVE other RUNNER.
	 * @param scoreDelta
	 * @param runnerId
	 */
	public void seekerLetRunnerSurvive(int scoreDelta, UnrealId runnerId) {
		if (botState != HSBotState.SEEKER) throw new RuntimeException("RUNNER cannot let other runner survive!");
		score += scoreDelta;
		seekerLetRunnerSurviveScore += scoreDelta;
		seekerLetRunnerSurviveCount.put(runnerId, seekerLetRunnerSurviveCount.get(runnerId) + 1);
	}
	
	/**
	 * THIS BOT is SEEKER and SPOTTED other RUNNER.
	 * @param scoreDelta
	 * @param runnerId
	 */
	public void seekerSpottedRunner(int scoreDelta, UnrealId runnerId) {
		if (botState != HSBotState.SEEKER) throw new RuntimeException("RUNNER cannot spot other runners!");
		score += scoreDelta;
		seekerSpottedRunnerScore += scoreDelta;
		seekerSpottedRunnerCount.put(runnerId, seekerSpottedRunnerCount.get(runnerId) + 1);
	}
	
	/**
	 * THIS bot is SEEKER and was fauled out.
	 * @param scoreDelta
	 * @param seekerId
	 */
	public void seekerFauled(int scoreDelta, UnrealId seekerId) {
		if (botState == HSBotState.SEEKER_FOULED) return;
		if (botState != HSBotState.SEEKER) throw new RuntimeException(botState.name() + " cannot be seeker-fauled!");
		botState = HSBotState.SEEKER_FOULED;
		score += scoreDelta;
		seekerFoulScore += scoreDelta;
		seekerFoulCount += 1;
	}	
	
	public void reset() {
		initTime = System.currentTimeMillis();
		finishTime = -1;
		botState = HSBotState.RUNNER;
		
		spawned = true;
		
		runnerCount = 0;
		seekerCount = 0;
		
		score = 0;
		
		runnerCapturedBySeekerCount.clear();
		runnerCapturedBySeekerScore = 0;
		
		runnerFoulCount.clear();
		runnerFoulScore = 0;
		
		runnerSafeCount.clear();
		runnerSafeScore = 0;
		
		runnerSpottedBySeekerCount.clear();
		runnerSpottedBySeekerScore = 0;
		
		runnerSurvivedCount.clear();
		runnerSurvivedScore = 0;
		
		seekerCapturedRunnerCount.clear();
		seekerCapturedRunnerScore = 0;
		
		seekerLetRunnerEscapeCount.clear();
		seekerLetRunnerEscapeScore = 0;
		
		seekerLetRunnerSurviveCount.clear();
		seekerLetRunnerSurviveScore = 0;
		
		seekerSpottedRunnerCount.clear();
		seekerSpottedRunnerScore = 0;
				
		seekerFoulCount = 0;
		seekerFoulScore = 0;
	}

	public PLAYER_CONTAINER getPlayer() {
		return player;
	}

	public void setPlayer(PLAYER_CONTAINER player) {
		this.player = player;
	}

}
