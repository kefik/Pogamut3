package cz.cuni.amis.pogamut.ut2004.tournament.capturetheflag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.guice.AdaptableProvider;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.execution.UT2004BotExecution;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentStats;
import cz.cuni.amis.pogamut.ut2004.analyzer.IUT2004AnalyzerObserver;
import cz.cuni.amis.pogamut.ut2004.analyzer.UT2004Analyzer;
import cz.cuni.amis.pogamut.ut2004.analyzer.stats.UT2004AnalyzerObsStats;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StartPlayers;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MapFinished;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerScore;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.TeamScore;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.TeamScoreMessage;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004Match;
import cz.cuni.amis.pogamut.ut2004.tournament.match.result.UT2004MatchResult;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.FilePath;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutIOException;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.FlagListener;
import cz.cuni.amis.utils.token.IToken;

public class UT2004CaptureTheFlag extends UT2004Match<UT2004CaptureTheFlagConfig, UT2004CaptureTheFlagResult> {

	protected int targetScoreLimit = 0;
	
	public UT2004CaptureTheFlag(UT2004CaptureTheFlagConfig config, LogCategory log) {
		super(true, config, log);		
	}

	@Override
	protected UT2004MatchResult waitMatchFinish(UCCWrapper ucc, UT2004Server server, UT2004Analyzer analyzer, Bots bots, long timeoutInMillis) {
		// usually the GB2004 dies out whenever match ends -> just wait till server does not fail + timeout + observe bots
		
		if (log != null && log.isLoggable(Level.WARNING)) {
			log.warning(config.getMatchId().getToken() + ": Waiting for the match to finish...");
		}
		
		if (config.getTimeLimit() * 60 * 1000 + 5 * 60 * 1000 > timeoutInMillis) {
			timeoutInMillis = config.getTimeLimit() * 60 * 1000 + 5 * 60 * 1000; // give additional 5 minutes to UT2004 to restart GB2004
		}
		
		Map<IToken, FlagListener<Boolean>> customBotObservers = new HashMap<IToken, FlagListener<Boolean>>(config.getBots().size());
		FlagListener<IAgentState> serverObs = null;
		FlagListener<Boolean> uccObs = null;
		IWorldEventListener<PlayerScore> scoresListener = null;
		IWorldObjectListener<TeamScore> teamScoresListener = null;
		IWorldEventListener<MapFinished> mapFinishedListener = null;
		
		final CountDownLatch waitLatch = new CountDownLatch(1);
		final AdaptableProvider<Boolean> oneOfBotsDiedOut = new AdaptableProvider<Boolean>(false);
		final AdaptableProvider<Boolean> serverDiedOut    = new AdaptableProvider<Boolean>(false);
		final Map<UnrealId, PlayerScore> scores           = new HashMap<UnrealId, PlayerScore>();
		final Map<Integer, TeamScore>    teamScores       = new HashMap<Integer, TeamScore>();
		
		boolean exception = false;
		
		try {
			teamScores.put(0, new TeamScoreMessage(UnrealId.get("TEAM0"), 0, 0));
			teamScores.put(1, new TeamScoreMessage(UnrealId.get("TEAM1"), 1, 0));
			serverDiedOut.set(false);
			
			scoresListener = new IWorldEventListener<PlayerScore>() {

				@Override
				public void notify(PlayerScore event) {
					scores.put(event.getId(), event);
				}
				
			};
			server.getWorldView().addEventListener(PlayerScore.class, scoresListener);
			
			teamScoresListener = new IWorldObjectListener<TeamScore>() {

				@Override
				public void notify(IWorldObjectEvent<TeamScore> event) {
					if (event.getObject() == null) return;
					int team = event.getObject().getTeam();
					teamScores.put(team, event.getObject());
					
					if (event.getObject().getScore() >= targetScoreLimit) {
						// TARGET SCORE REACHED BY ONE OF THE TEAMS!
						waitLatch.countDown();
					}
				}
				
			};
			server.getWorldView().addObjectListener(TeamScore.class, teamScoresListener);

			
			for (final UT2004BotConfig botConfig : config.getBots().values()) {
				FlagListener<Boolean> obs = new FlagListener<Boolean>() {
					@Override
					public void flagChanged(Boolean changedValue) {
						if (!changedValue) {
							// bot has died out
							bots.diedOff.add(botConfig.getBotId());
							oneOfBotsDiedOut.set(true);
							waitLatch.countDown();
						}
					}
				};
				
				bots.bots.get(botConfig.getBotId()).getRunning().addListener(obs);
				customBotObservers.put(botConfig.getBotId(), obs);
				if (!bots.bots.get(botConfig.getBotId()).getRunning().getFlag()) {
					// bot has died out
					bots.diedOff.add(botConfig.getBotId());
					oneOfBotsDiedOut.set(true);
					waitLatch.countDown();
					throw new PogamutException("One of custom bots died out from the start, failure!", log, this);
				}			
			}
			
			serverObs = new FlagListener<IAgentState>() {
	
				@Override
				public void flagChanged(IAgentState changedValue) {
					if (changedValue instanceof IAgentStateDown) {
						// server has died out ... consider match to be over...
						serverDiedOut.set(true);
						waitLatch.countDown();
					}
				}
				
			};
			
			server.getState().addListener(serverObs);
			
			mapFinishedListener = new IWorldEventListener<MapFinished>() {
				@Override
				public void notify(MapFinished event) {
					log.info("MapFinished event received.");
					waitLatch.countDown();
				}
			};
			
			server.getWorldView().addEventListener(MapFinished.class, mapFinishedListener);
			
			if (server.notInState(IAgentStateUp.class)) {
				// server has died out ... consider match to be over...
				serverDiedOut.set(true);
				waitLatch.countDown();
				throw new PogamutException("Server is dead from the start, failure!", log, this);
			}
			
			uccObs = new FlagListener<Boolean>() {

				@Override
				public void flagChanged(Boolean changedValue) {
					if (changedValue) {
						// GAME IS ENDING!
						// Consider match to be over...
						serverDiedOut.set(true);
						waitLatch.countDown();
					}
				}
				
			};
			
			ucc.getGameEnding().addListener(uccObs);
			
			waitLatch.await(timeoutInMillis, TimeUnit.MILLISECONDS);
			if (waitLatch.getCount() > 0) {
				// TIMEOUT!
				throw new PogamutException("TIMEOUT! The match did not end in " + (timeoutInMillis / 1000) + " secs.", log, this);
			}
			
			bots.matchEnd = System.currentTimeMillis();
			
			// RESTORE THE CONFIG...
			getConfig().setScoreLimit(targetScoreLimit); 
			
			// WHAT HAS HAPPENED?
			if (oneOfBotsDiedOut.get()) {
				// check whether the server is down as well... but let GB2004 to process it
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					throw new PogamutInterruptedException("Interrupted while giving GB2004 time to tear down its connection.", log, this);
				}
				try {
					server.getAct().act(new StartPlayers());
				} catch (Exception e) {
					// YEP, server is down
					serverDiedOut.set(true);
				}
				if (!serverDiedOut.get()) {
					// NO SERVER IS STILL RUNNING
					log.warning("ONE OF BOTS HAS DIED OUT, BUT SERVER IS STILL RUNNING ... POSSIBLE MATCH FAILURE!");
				}
			}
			if (!serverDiedOut.get() && server.inState(IAgentStateUp.class)) {
				// server is still running? Kill it...
				server.kill();
			}
			// server is DEAD -> assume that the match has ended
			
			// KILL UCC TO ENSURE NOTHING WILL CHANGE AFTER THAT
			if (ucc != null) {
				try {
					if (log != null && log.isLoggable(Level.INFO)) {
						log.info(config.getMatchId().getToken() + ": Killing UCC...");
					} 
				} catch (Exception e) {				
				}
				try {
					ucc.stop();
				} catch (Exception e) {					
				}
			}
			
			List<Integer> winners = new ArrayList<Integer>(1);
			int maxScore = 0;
			
			// PROCESS THE RESULT
			for (Entry<Integer, TeamScore> entry : teamScores.entrySet()) {
				if (entry.getValue() == null || entry.getValue().getScore() == null) {
					throw new PogamutException("There is a team '" + entry.getKey() + "' that has NULL score!", this);
				}
				if (entry.getValue().getScore() == maxScore) {
					winners.add(entry.getValue().getTeam());
				} else
				if (entry.getValue().getScore() > maxScore) {
					winners.clear();
					winners.add(entry.getValue().getTeam());
					maxScore = entry.getValue().getScore();
				}
			}
							
			if (winners.size() == 0) {
				// no one has reached FragLimit
				throw new PogamutException("There is no winner, impossible! **puzzled**", log, this);
			}
			if (winners.size() > 1) {
				StringBuffer sb = new StringBuffer();
				sb.append("There is more than one team with highest score == " + maxScore + ": ");
				boolean first = true;
				for (Integer id : winners) {
					if (first) first = false;
					else sb.append(", ");
					sb.append("Team[" + id + "]");
				}
				sb.append(".");
				if (log != null && log.isLoggable(Level.WARNING)) {
					log.warning(sb.toString());
				}
			}
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning(config.getMatchId().getToken() + ": MATCH FINISHED!");
			}
			
			return processResults(ucc, server, analyzer, bots, winners, scores, teamScores);
			
		} catch (Exception e) {
			exception = true;
			throw new PogamutException("Failed to perform the match!", e, log, this);
		} finally {
			for (Entry<IToken, FlagListener<Boolean>> entry : customBotObservers.entrySet()) {
				bots.bots.get(entry.getKey()).getRunning().removeListener(entry.getValue());
			}
			server.getState().removeListener(serverObs);
			server.getWorldView().removeEventListener(PlayerScore.class, scoresListener);
		}		

	}
	
	protected UT2004CaptureTheFlagResult processResults(UCCWrapper ucc, UT2004Server server, UT2004Analyzer analyzer, Bots bots, List<Integer> winners, Map<UnrealId, PlayerScore> finalScores, Map<Integer, TeamScore> teamScores) {
		
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Processing results...");
		}
		
		UT2004CaptureTheFlagResult result = new UT2004CaptureTheFlagResult();
		
		result.setMatchTimeEnd(((double)bots.matchEnd - (double)bots.matchStart) / (1000));
		
		for (Entry<Integer, TeamScore> entry : teamScores.entrySet()) {
			result.getTeamScores().put(entry.getKey(), entry.getValue());
		}
		
		for (Entry<UnrealId, PlayerScore> entry : finalScores.entrySet()) {
			result.getFinalScores().put(bots.getBotId(entry.getKey()), entry.getValue());
		}
		
		for (Entry<IToken, IUT2004AnalyzerObserver> entry : bots.botObservers.entrySet()) {
			if (!(entry.getValue() instanceof UT2004AnalyzerObsStats)) {
				throw new PogamutException("There is an observer of wrong class, expecting UT2004AnalyzerObsStats, got " + entry.getValue().getClass().getSimpleName() + "!", log, this);
			}
			result.getBotObservers().put(entry.getKey(), (UT2004AnalyzerObsStats)entry.getValue());
		}
		
		List<IToken> botIds = config.getAllBotIds();
		for (IToken botId1 : botIds) {
			result.getNames().put(botId1, bots.names.get(bots.getUnrealId(botId1)));
			result.getTotalKills().put(botId1, 0);
			result.getWasKilled().put(botId1, 0);
			result.getSuicides().put(botId1, 0);
			for (IToken botId2 : botIds) {
				result.getKillCounts().put(botId1, botId2, 0);
			}
		}
		
		for (Entry<IToken, UT2004AnalyzerObsStats> entry : result.getBotObservers().entrySet()) {
			IToken botId = entry.getKey();
			UT2004AnalyzerObsStats obs = entry.getValue();
			AgentStats stats = obs.getStats();
			for (Entry<UnrealId, Integer> killed : stats.getKilled().entrySet()) {
				result.getKillCounts().get(botId).put(bots.getBotId(killed.getKey()), killed.getValue());				
			}
			for (Entry<UnrealId, Integer> killedBy : stats.getKilledBy().entrySet()) {
				if (bots.isNativeBot(killedBy.getKey())) {
					result.getKillCounts().get(bots.getBotId(killedBy.getKey())).put(botId, killedBy.getValue());
				}
			}
			result.getSuicides().put(botId, stats.getSuicides());
			result.getKillCounts().put(botId, botId, stats.getSuicides());
		}
		
		for (IToken nativeBotId1 : config.getNativeBots().keySet()) {
			for (IToken nativeBotId2 : config.getNativeBots().keySet()) {
				if (nativeBotId1 == nativeBotId2) continue;
				result.getKillCounts().get(nativeBotId1).put(nativeBotId2, 0);
			}
			result.getSuicides().put(nativeBotId1, 0);
		}
		
		for (IToken botId : botIds) {
			int totalKills = 0;
			int totalKilled = 0;
			for (IToken other : botIds) {
				if (botId == other) continue;
				totalKills += result.getKillCounts().get(botId, other);
				totalKilled += result.getKillCounts().get(other, botId);
			}
			result.getTotalKills().put(botId, totalKills);
			result.getWasKilled().put(botId, totalKilled);
			if (config.isNativeBot(botId) || config.isHuman(botId)) {
				result.getSuicides().put(botId, result.getFinalScores().get(botId).getDeaths() - totalKilled);
			}
		}
		
		// CHECK FOR MATCH FAILURE
		// -- WIN-DUE-TO-FRAG-SCORE?
		boolean matchFailed = false;
		if (winners.size() > 0 && teamScores.get(winners.get(0)).getScore()+1 >= config.scoreLimit) {
			// CORRECT WIN
		} else {
			// CHECK TIME LIMIT
			double time = result.matchTimeEnd;
			if (time / 1000 + 15 >= config.getTimeLimit()) {
				// TIME HAS PASSED OUT
			} else {
				// MATCH HAS ENDED PREMATURELY
				matchFailed = true;
			}
		}		
		result.matchFailure = matchFailed;
		
		// DETERMINE WINNER	
		
		if (!result.matchFailure && winners.size() <= 0) {
			throw new PogamutException("There is no winner, impossible! **puzzled**", log, this);
		} else 
		if (!result.matchFailure && winners.size() == 1) {
			result.setWinnerTeam(winners.get(0));
		} else {
			result.setDraw(true);
		}
		
		if (log != null && log.isLoggable(Level.WARNING)) {
			log.warning(config.getMatchId().getToken() + ": Results processed, " + (result.isDraw() ? "DRAW!" : "winner is Team[" + winners.get(0) + "]."));
		}
		
		return result;
	}
	
	protected void outputResults_step1(UT2004CaptureTheFlagResult result, File outputDirectory) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Outputting match result into CSV file...");
		}
		
		File file = new File(outputDirectory.getAbsolutePath() + File.separator + "match-" + config.getMatchId().getToken() + "-result.csv");
		FilePath.makeDirsToFile(file);
		try {
			Formatter writer = new Formatter(file);
			writer.format("MatchId;ScoreLimit;TimeLimit;TimeEnd;Winner\n");
			writer.format
					(
						"%s;%d;%d;%.3f;%s",
						config.getMatchId().getToken(),
						config.getScoreLimit(),
						config.getTimeLimit(),
						result.getMatchTimeEnd(),
						result.matchFailure ?
								"FAILURE"
								:
								result.isDraw() ? "DRAW" : "TEAM" + String.valueOf(result.getWinnerTeam())
					);
			try {
				writer.close();
			} catch (Exception e) {			
			}
		} catch (IOException e) {
			throw new PogamutIOException("Failed to write results!", e, log, this);
		}
		
		if (log != null && log.isLoggable(Level.INFO)) {
			if (result.matchFailure) {
				log.warning(config.getMatchId().getToken() + ": Results processed, FAILURE!");
			} else {
				log.info(config.getMatchId().getToken() + ": Match result output into " + file.getAbsolutePath() + ".");
			}
		}
		
	}
	
	protected void outputResults_step2(UT2004CaptureTheFlagResult result, File outputDirectory) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Outputting match scores into CSV file...");
		}
		
		File file = new File(outputDirectory.getAbsolutePath() + File.separator + "match-" + config.getMatchId().getToken() + "-team-scores.csv");
		FilePath.makeDirsToFile(file);
		try {
			Formatter writer = new Formatter(file);
			
			List<TeamScore> teams = new ArrayList<TeamScore>();
			for (TeamScore score : result.getTeamScores().values()) {
				teams.add(score);
			}
			
			Collections.sort(teams, new Comparator<TeamScore>() {
				@Override
				public int compare(TeamScore o1, TeamScore o2) {
					return o1.getTeam().compareTo(o2.getTeam());
				}				
			});
			
			writer.format("teamId");
			writer.format(";score");
			
			for (TeamScore score : teams) {
				writer.format("\n");
				writer.format("%s", "TEAM" + score.getTeam());
				writer.format(";%d", score.getScore());								
			}
			
			try {
				writer.close();
			} catch (Exception e) {			
			}
		} catch (IOException e) {
			throw new PogamutIOException("Failed to write results!", e, log, this);
		}
		
		file = new File(outputDirectory.getAbsolutePath() + File.separator + "match-" + config.getMatchId().getToken() + "-bot-scores.csv");
		FilePath.makeDirsToFile(file);
		try {
			Formatter writer = new Formatter(file);
			
			List<IToken> bots = new ArrayList<IToken>(config.getBots().keySet());
			List<IToken> nativeBots = new ArrayList<IToken>(config.getNativeBots().keySet());
			List<IToken> humans = new ArrayList<IToken>(config.getHumans().keySet());
			
			Collections.sort(bots, new Comparator<IToken>() {
				@Override
				public int compare(IToken o1, IToken o2) {
					return o1.getToken().compareTo(o2.getToken());
				}				
			});
			Collections.sort(nativeBots, new Comparator<IToken>() {
				@Override
				public int compare(IToken o1, IToken o2) {
					return o1.getToken().compareTo(o2.getToken());
				}				
			});
			Collections.sort(humans, new Comparator<IToken>() {
				@Override
				public int compare(IToken o1, IToken o2) {
					return o1.getToken().compareTo(o2.getToken());
				}				
			});
			result.setBots(bots);
			result.setNativeBots(nativeBots);
			result.setHumans(humans);
			
			writer.format("botId");
			writer.format(";name;score;kills;killedByOthers;deaths;suicides");
			for (IToken token : config.getAllBotIds()) {
				writer.format(";");
				writer.format(token.getToken());
			}
			
			for (IToken token : config.getAllBotIds()) {
				writer.format("\n");
				writer.format(token.getToken());
				writer.format(";%s", result.getNames().get(token));
				writer.format(";%d", result.getFinalScores().get(token).getScore());
				writer.format(";%d", result.getTotalKills().get(token));
				writer.format(";%d", result.getWasKilled().get(token));
				writer.format(";%d", result.getFinalScores().get(token).getDeaths());
				writer.format(";%d", result.getSuicides().get(token));				
				for (IToken token2 : config.getAllBotIds()) {
					writer.format(";%d", result.getKillCounts().get(token).get(token2));
				}				
			}
			
			try {
				writer.close();
			} catch (Exception e) {			
			}
		} catch (IOException e) {
			throw new PogamutIOException("Failed to write results!", e, log, this);
		}
		
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": Match scores output into " + file.getAbsolutePath() + ".");
		}
		
	}
	
	@Override
	protected void outputResults(UCCWrapper ucc, UT2004Server server, UT2004Analyzer analyzer, Bots bots, UT2004MatchResult result,	File outputDirectory) {
		if (!(result instanceof UT2004CaptureTheFlagResult)) {
			throw new PogamutException("Can't out results! Expected results of class UT2004CaptureTheFlagResult and got " + result.getClass().getSimpleName() + "!", log, this);
		}
		outputResults_step1((UT2004CaptureTheFlagResult) result, outputDirectory);
		outputResults_step2((UT2004CaptureTheFlagResult) result, outputDirectory);
	}
	
	@Override
	public UT2004CaptureTheFlagResult execute() {
		try {
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning(config.getMatchId().getToken() + ": Executing!");
			} 
		} catch (Exception e) {				
		}
		
		UCCWrapper ucc = null;
		UT2004Server server = null;
		Bots bots = null;
		UT2004Analyzer analyzer = null;
		String recordFileName = config.getMatchId().getToken() + "-replay-" + UT2004Match.getCurrentDate();
		boolean exception = false;
		
		// HACK!!!
		// We must set frag limit to actually BIGGER NUMBER because otherwise GB2004 would drop the connection sooner before telling us that some bot
		// has achieved required score :-/
		targetScoreLimit = getConfig().getScoreLimit();
		getConfig().setScoreLimit(targetScoreLimit + 10); 

		
		try {
			// STEP 0
			setupLogger();
			
			// STEP 1
			validate();

			// STEP 2.1
			createUT2004Ini();
			
			// STEP 2.2
			createGB2004Ini();
			
			// STEP 3
			ucc = startUCC();
			
			// STEP 4
			server = startControlServer(ucc);
			
			// STEP 5.1
			bots = startBots(ucc, server);
			
			// STEP 5.2
			waitHumanPlayers(server, bots);
			
			// STEP 6
			analyzer = startAnalyzer(ucc, bots, getOutputPath("bots"), false);
			
			// STEP 7
			matchIsAboutToBegin(ucc, server, analyzer, bots);
			
			// STEP 8
			restartMatch(server, bots);
			
			// STEP 9			
			recordReplay(server, recordFileName);
			
			// STEP 9.5
			UT2004CaptureTheFlagResult result = (UT2004CaptureTheFlagResult) waitMatchFinish(ucc, server, analyzer, bots, config.getTimeLimit() * 1000 + 60 * 1000);
			
			// STEP 11
			copyReplay(ucc, recordFileName, getOutputPath());

			// STEP 12
			outputResults(ucc, server, analyzer, bots, result, getOutputPath());
			
			// STEP 13
			shutdownAll(ucc, server, analyzer, bots);
			
			ucc = null;
			server = null;
			analyzer = null;
			bots = null;
			
			// WE'RE DONE! ... all that is left is a possible cleanup...
			return result;
			
		} catch (Exception e) {
			if (log != null && log.isLoggable(Level.SEVERE)) {
				log.severe(ExceptionToString.process(config.getMatchId().getToken() + ": EXCEPTION!", e));
			}
			exception = true;
			if (e instanceof PogamutException) throw (PogamutException)e;
			throw new PogamutException(e, log, this);
		} finally {		
			try {
				if (log != null && log.isLoggable(Level.INFO)) {
					log.info(config.getMatchId().getToken() + ": Cleaning up...");
				} 
			} catch (Exception e) {				
			}
			
			if (ucc != null) {
				try {
					if (log != null && log.isLoggable(Level.INFO)) {
						log.info(config.getMatchId().getToken() + ": Killing UCC...");
					} 
				} catch (Exception e) {				
				}
				try {
					ucc.stop();
				} catch (Exception e) {					
				}
			}
			if (server != null) {
				try {
					if (log != null && log.isLoggable(Level.INFO)) {
						log.info(config.getMatchId().getToken() + ": Killing UT2004Server...");
					} 
				} catch (Exception e) {				
				}
				try {
					server.kill();
				} catch (Exception e) {					
				}
			}
			if (bots != null) {
				try {
					if (log != null && log.isLoggable(Level.INFO)) {
						log.info(config.getMatchId().getToken() + ": Killing Custom bots...");
					} 
				} catch (Exception e) {				
				}
				for (UT2004BotExecution exec : bots.bots.values()) {
					try {
						exec.stop();					
					} catch (Exception e) {					
					}
				}
				try {
					if (log != null && log.isLoggable(Level.INFO)) {
						log.info(config.getMatchId().getToken() + ": Killing Custom bot observers...");
					} 
				} catch (Exception e) {				
				}
				for (IUT2004AnalyzerObserver obs : bots.botObservers.values()) {
					try {
						obs.kill();
					} catch (Exception e) {						
					}
				}
			}
			if (analyzer != null) {
				try {
					if (log != null && log.isLoggable(Level.INFO)) {
						log.info(config.getMatchId().getToken() + ": Killing UT2004Analyzer...");
					} 
				} catch (Exception e) {				
				}
				try {
					analyzer.kill();
				} catch (Exception e) {					
				}
			}	
			
			try {
				// STEP 10.1
				restoreUT2004IniBackup();
			} catch (Exception e) {				
			}
			
			try {
				// STEP 10.2
				restoreGB2004IniBackup();
			} catch (Exception e) {				
			}
			
			try {
				if (log != null && log.isLoggable(Level.WARNING)) {
					if (exception) {
						log.warning(config.getMatchId().getToken() + ": Cleaned up, MATCH FAILED!");
					} else { 
						log.warning(config.getMatchId().getToken() + ": Cleaned up, match finished successfully.");
					}
				} 
			} catch (Exception e) {				
			}
			try {
				closeLogger();
			} catch (Exception e) {
				
			}
		}
		
	}

}
