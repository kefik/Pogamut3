package cz.cuni.amis.pogamut.ut2004.vip.tournament;

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

import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateRunning;
import cz.cuni.amis.pogamut.base.utils.guice.AdaptableProvider;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.execution.UT2004BotExecution;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.analyzer.IUT2004AnalyzerObserver;
import cz.cuni.amis.pogamut.ut2004.analyzer.UT2004Analyzer;
import cz.cuni.amis.pogamut.ut2004.analyzer.UT2004AnalyzerFullObserver;
import cz.cuni.amis.pogamut.ut2004.analyzer.stats.UT2004AnalyzerObsStats;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StartPlayers;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004Match;
import cz.cuni.amis.pogamut.ut2004.tournament.match.result.UT2004MatchResult;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.pogamut.ut2004.vip.protocol.VIPGameResult;
import cz.cuni.amis.pogamut.ut2004.vip.server.CSBotRecord;
import cz.cuni.amis.pogamut.ut2004.vip.server.CSTeamsRecord;
import cz.cuni.amis.pogamut.ut2004.vip.server.UT2004VIPServer;
import cz.cuni.amis.pogamut.ut2004.vip.server.UT2004VIPServerModule;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.FilePath;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutIOException;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.FlagListener;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class UT2004VIP extends UT2004Match<UT2004VIPConfig, UT2004VIPResult> {
	
	private String origFixedVIPNamePrefix = null;

	public UT2004VIP(UT2004VIPConfig config, LogCategory log) {
		super(false, config, log);		
	}
	
	@Override
	protected void changeBotTeam(UT2004Server server, UnrealId botId, int desiredTeam) {
		// THERE IS NO NEED TO CHANGE BOT TEAM IN VIP!
	}

	@Override
	protected UT2004MatchResult waitMatchFinish(UCCWrapper ucc, UT2004Server server, UT2004Analyzer analyzer, Bots bots, long timeoutInMillis) {
		// usually the GB2004 dies out whenever match ends -> just wait till server does not fail + timeout + observe bots
		
		if (log != null && log.isLoggable(Level.WARNING)) {
			log.warning(config.getMatchId().getToken() + ": Waiting for the match to finish...");
		}
		
		// round times + 1 min per round for restart + 5 mins extra
		long minTimeoutMillis = (long)(Math.round(config.getVIPConfig().getRoundCount() * config.getVIPConfig().getRoundTimeUT() * 1000 + config.getVIPConfig().getRoundCount() * 60 * 1000 + 5 * 60 * 1000));
		
		if (minTimeoutMillis > timeoutInMillis) {
			timeoutInMillis = minTimeoutMillis;
			log.warning(config.getMatchId().getToken() + ": match global timeout set to " + (minTimeoutMillis/1000) + " seconds");
		}
		
		Map<IToken, FlagListener<Boolean>> customBotObservers = new HashMap<IToken, FlagListener<Boolean>>(config.getBots().size());
		FlagListener<IAgentState> serverObs = null;
		FlagListener<Boolean> uccObs = null;
		FlagListener<Boolean> hsGameRunning = null;
		
		final CountDownLatch waitLatch = new CountDownLatch(1);
		final AdaptableProvider<Boolean> oneOfBotsDiedOut = new AdaptableProvider<Boolean>(false);
		final AdaptableProvider<Boolean> serverDiedOut = new AdaptableProvider<Boolean>(false);
		
		boolean exception = false;
		
		try {			
			for (UT2004BotConfig botConfig : config.getBots().values()) {
				FlagListener<Boolean> obs = new FlagListener<Boolean>() {
					@Override
					public void flagChanged(Boolean changedValue) {
						if (!changedValue) {
							// bot has died out
							oneOfBotsDiedOut.set(true);
							waitLatch.countDown();
						}
					}
				};
				
				bots.bots.get(botConfig.getBotId()).getRunning().addListener(obs);
				customBotObservers.put(botConfig.getBotId(), obs);
				if (!bots.bots.get(botConfig.getBotId()).getRunning().getFlag()) {
					// bot has died out
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
								
			if (server.notInState(IAgentStateUp.class)) {
				// server has died out ... consider match to be over...
				serverDiedOut.set(true);
				waitLatch.countDown();
				throw new PogamutException("Server is dead from the start, failure!", log, this);
			}
			
			if (!((UT2004VIPServer)server).isGameRunning().getFlag()) {
				serverDiedOut.set(true);
				waitLatch.countDown();
				throw new PogamutException("Hide&Seek game is not running at the beginning, invalid!", log, this);
			}
			
			hsGameRunning = new FlagListener<Boolean>() {

				@Override
				public void flagChanged(Boolean changedValue) {
					if (!changedValue) {
						// TAG GAME HAS ENDED
						waitLatch.countDown();
					}
				}
				
			};
			
			((UT2004VIPServer)server).isGameRunning().addListener(hsGameRunning);
			
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
			if (((UT2004VIPServer)server).getGameFailed().getFlag()) {
				throw new PogamutException("UT2004HSServer reported failure!", log, this);
			}
			if (!serverDiedOut.get() && server.inState(IAgentStateUp.class)) {
				// Server is still running? ... This will likely to always happen as frag limit is targetFragCount+10 !!! 
				// Kill it...
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
			
			// OBTAIN BOT RECORDS
			UT2004VIPServer vipServer = (UT2004VIPServer)server;
			Map<IToken, CSBotRecord<PlayerMessage>> botRecordMap = new HashMap<IToken, CSBotRecord<PlayerMessage>>();
			List<CSBotRecord<PlayerMessage>> botRecords = new ArrayList<CSBotRecord<PlayerMessage>>();
			for (Entry<UnrealId, CSBotRecord<PlayerMessage>> entryRecord : vipServer.getBotRecords().entrySet()) {				
				if (entryRecord.getValue().getPlayer() == null || entryRecord.getValue().getPlayer().getJmx() == null) continue;
				IToken botId = bots.unrealId2BotId.get(entryRecord.getKey());
				botRecordMap.put(botId, entryRecord.getValue());
				botRecords.add(entryRecord.getValue());
			}
			CSTeamsRecord teamsRecord = vipServer.getTeamsRecord();
			VIPGameResult result = teamsRecord.getResult();
			
			// DETERMINE WINNERs
			List<IToken> winners = new ArrayList<IToken>(1);
			int maxScore = teamsRecord.getScore(result.team);
			
			for (Entry<IToken, CSBotRecord<PlayerMessage>> entry : botRecordMap.entrySet()) {
				if (entry.getValue().getPlayer().getTeam() == result.team.ut2004Team) {
					winners.add(entry.getKey());
				}
			}
			
			// CHECK WINNERS
			if (result != VIPGameResult.DRAW && winners.size() == 0) {
				// no one has won??? IMPOSSIBLE!
				throw new PogamutException("Result " + result + ", but no WINNERs, impossible! **puzzled**", log, this);
			}
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning(config.getMatchId().getToken() + ": MATCH FINISHED!");
			}
			
			return processResults(ucc, server, analyzer, bots, winners, botRecordMap, teamsRecord);
			
		} catch (Exception e) {
			exception = true;
			throw new PogamutException("Failed to perform the match!", e, log, this);
		} finally {
			if (hsGameRunning != null) {
				((UT2004VIPServer)server).isGameRunning().removeListener(hsGameRunning);
				hsGameRunning = null;
			}
			for (Entry<IToken, FlagListener<Boolean>> entry : customBotObservers.entrySet()) {
				bots.bots.get(entry.getKey()).getRunning().removeListener(entry.getValue());
			}
			server.getState().removeListener(serverObs);
		}		

	}
	
	protected UT2004VIPResult processResults(UCCWrapper ucc, UT2004Server server, UT2004Analyzer analyzer, Bots bots, List<IToken> winners, Map<IToken, CSBotRecord<PlayerMessage>> botRecords, CSTeamsRecord teamsRecord) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Processing results...");
		}
		
		config.getVIPConfig().setFixedVIPNamePrefix(origFixedVIPNamePrefix);
		
		UT2004VIPResult result = new UT2004VIPResult();

		// OVERALL RESULT
		result.setGameResult(teamsRecord.getResult());
		
		// TEAMS
		result.setTeamsRecord(teamsRecord);
		
		// BOTS
		result.setBots(MyCollections.asList(bots.botId2UnrealId.keySet()));
		
		// BOT IDS
		result.setBotIds(bots.botId2UnrealId);
		
		// BOT OBSERVERS
		for (Entry<IToken, IUT2004AnalyzerObserver> entry : bots.botObservers.entrySet()) {
			if (!(entry.getValue() instanceof UT2004AnalyzerObsStats)) {
				throw new PogamutException("There is an observer of wrong class, expecting UT2004AnalyzerObsStats, got " + entry.getValue().getClass().getSimpleName() + "!", log, this);
			}
			result.getBotObservers().put(entry.getKey(), (UT2004AnalyzerObsStats)entry.getValue());
		}
		
		// WINNERS
		result.setWinnerBotIds(winners);
		
		// MATCH TIME
		result.setMatchTime(((double)bots.matchEnd - (double)bots.matchStart) / (1000));
		
		// SCORES
		result.setScoreDetails(botRecords);
				
		if (log != null && log.isLoggable(Level.WARNING)) {
			log.warning(config.getMatchId().getToken() + ": Results processed, " + result.getGameResult() + ", #Winners = " + result.getWinnerBotIds().size() + ", Winners score = " + result.getWinnerScore());
		}
		
		return result;
	}
	
	protected void outputResults_step1(UT2004VIPResult result, File outputDirectory) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Outputting match result into CSV file...");
		}
		
		File file = new File(outputDirectory.getAbsolutePath() + File.separator + "match-" + config.getMatchId().getToken() + "-result.csv");
		FilePath.makeDirsToFile(file);
		try {
			Formatter writer = new Formatter(file);
			writer.format("MatchId;GameResult;WinnerScore;MatchTimeSecs;" + config.getVIPConfig().getCSVHeader() + "");
			for (int i = 0; i < result.getWinnerBotIds().size(); ++i) {
				writer.format(";Winner" + (i+1));
			}
			writer.format("\n");
			writer.format
					(
						"%s;%d;%.3f;",
						config.getMatchId().getToken(),
						result.getGameResult(),
						result.getWinnerScore(),
						result.getMatchTime()
					);
			config.getVIPConfig().formatCSVLine(writer);
			
			for (IToken winner : result.getWinnerBotIds()) {
				writer.format(";%s", winner.getToken());
			}
			try {
				writer.close();
			} catch (Exception e) {			
			}
		} catch (IOException e) {
			throw new PogamutIOException("Failed to write results!", e, log, this);
		}
		
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": Match result output into " + file.getAbsolutePath() + ".");
		}		
	}
	
	protected void outputResults_step2(UT2004VIPResult result, File outputDirectory) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Outputting match scores into CSV file...");
		}
		
		File file = new File(outputDirectory.getAbsolutePath() + File.separator + "match-" + config.getMatchId().getToken() + "-bot-scores.csv");
		FilePath.makeDirsToFile(file);
		try {
			Formatter writer = new Formatter(file);
			
			List<IToken> bots = new ArrayList<IToken>(result.getBots());
			
			Collections.sort(bots, new Comparator<IToken>() {
				@Override
				public int compare(IToken o1, IToken o2) {
					return o1.getToken().compareTo(o2.getToken());
				}				
			});
			
			writer.format("BotId;Team;Rounds;VIPCount;CounterTerroristCount;TerroristCount");
			
			for (IToken token1 : bots) {
				writer.format("\n");
				writer.format(token1.getToken());
				
				CSBotRecord<PlayerMessage> record = result.getScoreDetails().get(token1);
				
				writer.format(";%d", record.getMyTeam());
				
				writer.format(";%d", record.getVIPCount() + record.getCTCount() + record.getTCount());
				
				writer.format(";%d", record.getVIPCount());
				writer.format(";%d", record.getCTCount());
				writer.format(";%d", record.getTCount());
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
		if (!(result instanceof UT2004VIPResult)) {
			throw new PogamutException("Can't out results! Expected results of class UT2004HideAndSeekResult and got " + result.getClass().getSimpleName() + "!", log, this);
		}
		outputResults_step1((UT2004VIPResult) result, outputDirectory);
		outputResults_step2((UT2004VIPResult) result, outputDirectory);
	}
	
	/**
	 * Usually STEP 4 ... after the UCC has started up, you usually want to connect to it to confirm it is up and running
	 * and be able to observe any changes in the environment / alter the environment, etc.
	 * <p><p>
	 * This method may need to be override to provide custom implementation of {@link UT2004Server} interface, i.e.,
	 * provide your custom control server. Current implementation is using {@link UT2004Server}.
	 * <p><p>
	 * Raises exception in case of any error.
	 * 
	 * @param ucc MUST NOT BE NULL
	 * @return running control server
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected UT2004Server startControlServer(UCCWrapper ucc) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Starting UT2004Server...");
		}
		NullCheck.check(ucc, "ucc");
        UT2004VIPServerModule module = new UT2004VIPServerModule();
        UT2004ServerFactory factory = new UT2004ServerFactory(module);        
        UT2004Server server = (UT2004Server) factory.newAgent(new UT2004AgentParameters().setAgentId(new AgentId(config.getMatchId().getToken()+"-UT2004Server")).setWorldAddress(ucc.getServerAddress()));
        
        server.start();
        
        if (!server.inState(IAgentStateRunning.class)) {
        	throw new PogamutException("Failed to start UT2004VIP!", log, this);
        }
        
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": UT2004VIP started.");
		}
		return server;
	}
	
	/**
	 * STEP 7 ... start the hide-and-seek game
	 * 
	 * @param ucc MUST NOT BE NULL
	 * @param server MUST NOT BE NULL
	 * @param analyzer may be null
	 * @param bots MUST NOT BE NULL
	 */
	protected void matchIsAboutToBegin(UCCWrapper ucc, UT2004Server server, UT2004Analyzer analyzer, Bots bots) {
		super.matchIsAboutToBegin(ucc, server, analyzer, bots);
		
		// WAIT FOR BOTS TO CATCH UP
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e, this);
		}
				
		// CONFIGURE OBSERVER PORT
		config.getVIPConfig().setObserverPort(ucc.getObserverPort());
		
		// START THE TAG GAME
		UT2004VIPServer vipServer = (UT2004VIPServer) server;		
		vipServer.startGame(config.getVIPConfig());
	}		

	@Override
	public UT2004VIPResult execute() {
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
			
			// STEP 5
			bots = startBots(ucc, server);
			
			// STEP 6
			analyzer = startAnalyzer(ucc, bots, getOutputPath("bots"), config.isHumanLikeLogEnabled());
						
			// STEP 8
			restartMatch(server, bots);

			// STEP 9			
			recordReplay(server, recordFileName);
			
			// STEP 7
			matchIsAboutToBegin(ucc, server, analyzer, bots); // <-- this will start VIP game ... do that after the match restart + after start recording the replay!			
			
			// STEP 9.5
			// timeout == 0 -> will be determined within waitMatchFinish
			UT2004VIPResult result = (UT2004VIPResult) waitMatchFinish(ucc, server, analyzer, bots, 0);
			
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
			if (analyzer != null) {
			}
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
