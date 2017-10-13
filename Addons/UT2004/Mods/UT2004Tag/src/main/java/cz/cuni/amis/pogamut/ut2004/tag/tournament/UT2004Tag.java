package cz.cuni.amis.pogamut.ut2004.tag.tournament;

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
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateFailed;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateRunning;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.utils.guice.AdaptableProvider;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.execution.UT2004BotExecution;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.analyzer.IUT2004AnalyzerObserver;
import cz.cuni.amis.pogamut.ut2004.analyzer.UT2004Analyzer;
import cz.cuni.amis.pogamut.ut2004.analyzer.stats.UT2004AnalyzerObsStats;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StartPlayers;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerScore;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPlayerScoreChanged;
import cz.cuni.amis.pogamut.ut2004.tag.server.BotTagRecord;
import cz.cuni.amis.pogamut.ut2004.tag.server.UT2004TagServer;
import cz.cuni.amis.pogamut.ut2004.tag.server.UT2004TagServerModule;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004Match;
import cz.cuni.amis.pogamut.ut2004.tournament.match.result.UT2004MatchResult;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004ServerRunner;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.FilePath;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutIOException;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.FlagListener;
import cz.cuni.amis.utils.maps.HashMapMap;
import cz.cuni.amis.utils.token.IToken;

public class UT2004Tag extends UT2004Match<UT2004TagConfig, UT2004TagResult> {
	
	private int targetTagCount;

	public UT2004Tag(UT2004TagConfig config, LogCategory log) {
		super(false, config, log);		
	}
	
	@Override
	protected void changeBotTeam(UT2004Server server, UnrealId botId, int desiredTeam) {
		// THERE IS NO NEED TO CHANGE BOT TEAM IN DEATHMATCH!
	}

	@Override
	protected UT2004MatchResult waitMatchFinish(UCCWrapper ucc, UT2004Server server, UT2004Analyzer analyzer, Bots bots, long timeoutInMillis) {
		// usually the GB2004 dies out whenever match ends -> just wait till server does not fail + timeout + observe bots
		
		if (log != null && log.isLoggable(Level.WARNING)) {
			log.warning(config.getMatchId().getToken() + ": Waiting for the match to finish...");
		}
		
		if (config.getTagTimeLimit() * 60 * 1000 + 5 * 60 * 1000 > timeoutInMillis) {
			timeoutInMillis = config.getTagTimeLimit() * 60 * 1000 + 5 * 60 * 1000; // give additional 5 minutes to UT2004 to restart GB2004
		}
		
		Map<IToken, FlagListener<Boolean>> customBotObservers = new HashMap<IToken, FlagListener<Boolean>>(config.getBots().size());
		FlagListener<IAgentState> serverObs = null;
		FlagListener<Boolean> uccObs = null;
		FlagListener<Boolean> tagGameRunning = null;
		IWorldEventListener<TagPlayerScoreChanged> scoresListener = null;
		
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
			
			if (!((UT2004TagServer)server).isGameRunning().getFlag()) {
				serverDiedOut.set(true);
				waitLatch.countDown();
				throw new PogamutException("Tag! game is not running at the beginning, invalid!", log, this);
			}
			
			tagGameRunning = new FlagListener<Boolean>() {

				@Override
				public void flagChanged(Boolean changedValue) {
					if (!changedValue) {
						// TAG GAME HAS ENDED
						waitLatch.countDown();
					}
				}
				
			};
			
			((UT2004TagServer)server).isGameRunning().addListener(tagGameRunning);
			
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
			UT2004TagServer tagServer = (UT2004TagServer)server;
			Map<IToken, BotTagRecord<PlayerMessage>> botRecordMap = new HashMap<IToken, BotTagRecord<PlayerMessage>>();
			List<BotTagRecord<PlayerMessage>> botRecords = new ArrayList<BotTagRecord<PlayerMessage>>();
			for (Entry<UnrealId, BotTagRecord<PlayerMessage>> entryRecord : tagServer.getBotRecords().entrySet()) {				
				if (entryRecord.getValue().getPlayer() == null || entryRecord.getValue().getPlayer().getJmx() == null) continue;
				IToken botId = bots.unrealId2BotId.get(entryRecord.getKey());
				botRecordMap.put(botId, entryRecord.getValue());
				botRecords.add(entryRecord.getValue());
			}
			
			if (botRecords.size() == 0) {
				throw new PogamutException("There are not bots results, it seems like the match has not been even played ???", log, this);
			}
			
			// SORT botRecords DESCENDING
			Collections.sort(botRecords, new Comparator<BotTagRecord<PlayerMessage>>() {
				@Override
				public int compare(BotTagRecord<PlayerMessage> o1, BotTagRecord<PlayerMessage> o2) {
					return o2.getScore() - o1.getScore();
				}
			});
			
			// DETERMINE LOSERs
			List<IToken> losers = new ArrayList<IToken>(1);			
			int maxTags = botRecords.get(0).getScore();			
			for (BotTagRecord<PlayerMessage> botRecord : botRecords) {
				if (maxTags == botRecord.getScore()) {
					IToken botId = bots.unrealId2BotId.get(botRecord.getBotId());
					losers.add(botId);
				} else {
					// maxTags contains MAX SCORE indeed (botRecords are sorted ~ descending)
					// => maxTags > botRecord.getScore()
					break;
				}
			}
			
			// CHECK LOSERS
			if (losers.size() == 0) {
				// no one has lost??? IMPOSSIBLE!
				throw new PogamutException("There is no LOSER, impossible! **puzzled**", log, this);
			}
			if (losers.size() > 1) {
				StringBuffer sb = new StringBuffer();
				sb.append("There is more than one loser with highest tag-count == " + maxTags + ": ");
				boolean first = true;
				for (IToken id : losers) {
					if (first) first = false;
					else sb.append(", ");
					sb.append("Bot[botId=" + id + ", unrealId=" + bots.botId2UnrealId.get(id) + ", score=" + botRecordMap.get(id).getScore() + "]");
				}
				sb.append(".");
				if (log != null && log.isLoggable(Level.WARNING)) {
					log.warning(sb.toString());
				}
			}
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning(config.getMatchId().getToken() + ": MATCH FINISHED!");
			}
			
			return processResults(ucc, server, analyzer, bots, losers, botRecordMap);
			
		} catch (Exception e) {
			exception = true;
			throw new PogamutException("Failed to perform the match!", e, log, this);
		} finally {
			if (tagGameRunning != null) {
				((UT2004TagServer)server).isGameRunning().removeListener(tagGameRunning);
				tagGameRunning = null;
			}
			for (Entry<IToken, FlagListener<Boolean>> entry : customBotObservers.entrySet()) {
				bots.bots.get(entry.getKey()).getRunning().removeListener(entry.getValue());
			}
			server.getState().removeListener(serverObs);
			server.getWorldView().removeEventListener(PlayerScore.class, scoresListener);			
		}		

	}
	
	protected UT2004TagResult processResults(UCCWrapper ucc, UT2004Server server, UT2004Analyzer analyzer, Bots bots, List<IToken> losers, Map<IToken, BotTagRecord<PlayerMessage>> botRecords) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Processing results...");
		}
		
		UT2004TagResult result = new UT2004TagResult();
		
		
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
		
		// LOSERS
		result.setLosers(losers);
		
		// MATCH TIME
		result.setMatchTime(((double)bots.matchEnd - (double)bots.matchStart) / (1000));
		
		// SCORES
		result.setScores(botRecords);
		
		// TAG COUNTS
		for (Entry<IToken, BotTagRecord<PlayerMessage>> entry : botRecords.entrySet()) {
			result.getTagCounts().put(entry.getKey(), entry.getValue().getScore());
		}
		
		// TAG OTHERS COUNTS
		// TAG PASSED COUNTS
		for (IToken bot1Id : bots.botId2UnrealId.keySet()) {
			int passCount = 0;
			for (IToken bot2Id : bots.botId2UnrealId.keySet()) {
				if (bot1Id == bot2Id) {
					result.getTagPassedCounts().put(bot1Id, bot2Id, 0);
					continue;
				}
				
				UnrealId bot2UnrealId = bots.botId2UnrealId.get(bot2Id);
				int tagPassed = botRecords.get(bot1Id).getTagPassedMillis().get(bot2UnrealId).size();
				result.getTagPassedCounts().put(bot1Id, bot2Id, tagPassed);
				passCount += tagPassed;				
			}
			result.getTagOthersCounts().put(bot1Id, passCount);
		}
		
		if (log != null && log.isLoggable(Level.WARNING)) {
			log.warning(config.getMatchId().getToken() + ": Results processed, #Losers = " + result.getLosers().size() + ", Loser tag count = " + result.getLoserTagCount());
		}
		
		return result;
	}
	
	protected void outputResults_step1(UT2004TagResult result, File outputDirectory) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Outputting match result into CSV file...");
		}
		
		File file = new File(outputDirectory.getAbsolutePath() + File.separator + "match-" + config.getMatchId().getToken() + "-result.csv");
		FilePath.makeDirsToFile(file);
		try {
			Formatter writer = new Formatter(file);
			writer.format("MatchId;TagLimit;TimeLimitMinutes;LoserTagCount;MatchTimeSecs");
			for (int i = 0; i < result.getLosers().size(); ++i) {
				writer.format(";Loser" + (i+1));
			}
			writer.format("\n");
			writer.format
					(
						"%s;%d;%d;%d;%.3f",
						config.getMatchId().getToken(),
						config.getTagLimit(),
						config.getTagTimeLimit(),
						result.getLoserTagCount(),
						result.getMatchTime()
					);
			for (IToken loser : result.getLosers()) {
				writer.format(";%s", loser.getToken());
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
	
	protected void outputResults_step2(UT2004TagResult result, File outputDirectory) {
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
			
			writer.format("BotId");
			writer.format(";ReceivedTagCount;PassedTagCount;TotalTaggedTimeMillis");
			for (IToken token : bots) {
				writer.format(";");
				writer.format(token.getToken());
			}
			
			for (IToken token1 : bots) {
				writer.format("\n");
				writer.format(token1.getToken());
				
				writer.format(";%d", result.getTagCounts().get(token1));
				writer.format(";%d", result.getTagOthersCounts().get(token1));
				writer.format(";%d", result.getScores().get(token1).getTotalTagTimeMillis());
				
				for (IToken token2 : bots) {
					writer.format(";%d", result.getTagPassedCounts().get(token1).get(token2));
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
		if (!(result instanceof UT2004TagResult)) {
			throw new PogamutException("Can't out results! Expected results of class UT2004TagResult and got " + result.getClass().getSimpleName() + "!", log, this);
		}
		outputResults_step1((UT2004TagResult) result, outputDirectory);
		outputResults_step2((UT2004TagResult) result, outputDirectory);
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
	@Override
	protected UT2004Server startControlServer(UCCWrapper ucc) {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(config.getMatchId().getToken() + ": Starting UT2004Server...");
		}
		NullCheck.check(ucc, "ucc");
        UT2004TagServerModule module = new UT2004TagServerModule();
        UT2004ServerFactory factory = new UT2004ServerFactory(module);        
        UT2004Server server = (UT2004Server) factory.newAgent(new UT2004AgentParameters().setAgentId(new AgentId(config.getMatchId().getToken()+"-UT2004Server")).setWorldAddress(ucc.getServerAddress()));
        
        server.start();
        
        if (!server.inState(IAgentStateRunning.class)) {
        	throw new PogamutException("Failed to start UT2004TagServer!", log, this);
        }
        
		if (log != null && log.isLoggable(Level.INFO)) {
			log.info(config.getMatchId().getToken() + ": UT2004Server started.");
		}
		return server;
	}
	
	/**
	 * STEP 7 ... start the tag game
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
		
	    // START THE TAG GAME
		UT2004TagServer tagServer = (UT2004TagServer) server;		
		tagServer.startGame(config.getTagTimeLimit() * 60, config.getTagLimit());
	}		

	@Override
	public UT2004TagResult execute() {
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
		targetTagCount = getConfig().getTagLimit();
				
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
			matchIsAboutToBegin(ucc, server, analyzer, bots); // <-- this will start TagGame ... do that after the match restart + after start recording the replay!			
			
			// STEP 9.5
			UT2004TagResult result = (UT2004TagResult) waitMatchFinish(ucc, server, analyzer, bots, config.getTagTimeLimit() * 1000 + 60 * 1000);
			
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
