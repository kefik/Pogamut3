package cz.cuni.amis.pogamut.ut2004.tournament.deathmatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import cz.cuni.amis.pogamut.base.utils.logging.ILogPublisher;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004Match;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004MatchConfig;
import cz.cuni.amis.utils.FilePath;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutIOException;
import cz.cuni.amis.utils.maps.HashMapMap;
import cz.cuni.amis.utils.maps.LazyMap;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Simple class that allows to run 1 match multiple times automatically suffixing ID of the match (name of the output directory)
 * with numbers.
 * <p><p>
 * THREAD-UNSAFE!
 * 
 * @author Jimmy
 */
public class UT2004DeathMatchRepeater implements Callable<List<UT2004DeathMatchResult>>, Runnable {

	protected UT2004DeathMatchConfig matchConfig;
	protected int repeats;

	protected List<UT2004DeathMatchResult> results = new ArrayList<UT2004DeathMatchResult>();
	protected List<Throwable> exceptions = new ArrayList<Throwable>();
	private LogCategory log;
	
	/**
	 * Parameter-less constructor, don't forget to initialize {@link UT2004DeathMatchRepeater#setMatchConfig(UT2004Match)} and
	 * {@link UT2004DeathMatchRepeater#setRepeats(int)}.
	 */
	public UT2004DeathMatchRepeater() {
	}
	
	/**
	 * Parameter-less constructor, don't forget to initialize {@link UT2004DeathMatchRepeater#setMatchConfig(UT2004Match)} and
	 * {@link UT2004DeathMatchRepeater#setRepeats(int)}.
	 */
	public UT2004DeathMatchRepeater(LogCategory log) {
		this.log = log;
	}
	
	public UT2004DeathMatchRepeater(UT2004DeathMatchConfig match, int repeats, LogCategory log) {
		NullCheck.check(match, "match");
		this.matchConfig = match;
		this.repeats = repeats;
		if (this.repeats < 0) {
			throw new IllegalArgumentException("repeats = " + repeats + " < 0, can't be!");
		}		
		this.log = log;
	}

	protected String getNum(int i, int max) {
		String result = String.valueOf(i);
		String maxStr = String.valueOf(max);
		while (result.length() < maxStr.length()) {
			result = "0" + result;
		}
		return result;
	}
	
	protected Token getToken(IToken orig, int i, int max) {
		return Tokens.get(orig.getToken() + "-" + getNum(i+1, max) + "_of_" + max);
	}

	/**
	 * Log that is being used.
	 * @return
	 */
	public LogCategory getLog() {
		return log;
	}

	public void setLog(LogCategory log) {
		this.log = log;
	}

	/**
	 * Which match we're going to evaluate.
	 * @return
	 */
	public UT2004DeathMatchConfig getMatchConfig() {
		return matchConfig;
	}

	public void setMatchConfig(UT2004DeathMatchConfig matchConfig) {
		this.matchConfig = matchConfig;
	}

	/**
	 * How many times we will repeat the {@link UT2004DeathMatchRepeater#getMatchConfig()}.
	 * @return
	 */
	public int getRepeats() {
		return repeats;
	}

	public void setRepeats(int repeats) {
		this.repeats = repeats;
	}

	/**
	 * After the {@link UT2004DeathMatchRepeater#run()} or {@link UT2004DeathMatchRepeater#call()} returns list of exceptions that
	 * had happened during matches (Array is guaranteed to have the length of {@link UT2004DeathMatchRepeater#repeats}, some 
	 * items may be null. Usually all.)
	 * <p><p>
	 * Immutable.
	 * 
	 * @return
	 */
	public List<Throwable> getExceptions() {
		return Collections.unmodifiableList(exceptions);
	}

	/**
	 * After the {@link UT2004DeathMatchRepeater#run()} or {@link UT2004DeathMatchRepeater#call()} returns list of match results 
	 * (Array is guaranteed to have the length of {@link UT2004DeathMatchRepeater#repeats}, some items may be null == exception
	 * happened).
	 * <p><p>
	 * Immutable.
	 * 
	 * @return
	 */
	public List<UT2004DeathMatchResult> getResults() {
		return Collections.unmodifiableList(results);
	}

	@Override
	public List<UT2004DeathMatchResult> call() throws Exception {
		call();
		return getResults();
	}

	@Override
	public void run() {
		if (this.matchConfig == null) {
			throw new PogamutException("No match set into the repeater!", this);
		}
		if (this.repeats < 0) {
			throw new PogamutException("repeats = " + repeats + " < 0, can't be!", this);
		}
		IToken token = matchConfig.getMatchId();
		for (int i = 0; i < repeats; ++i) {
			if (log != null && log.isLoggable(Level.INFO)) {
				log.info("Running " + token.getToken() + " match " + (i+1) + " / " + repeats + " ...");
			}
			matchConfig.setMatchId(getToken(token, i, repeats));
			UT2004DeathMatch match = new UT2004DeathMatch(matchConfig, new LogCategory(matchConfig.getMatchId().getToken()));
			match.getLog().addHandler(new ILogPublisher() {
				@Override
				public void close() throws SecurityException {
				}
				@Override
				public void flush() {
				}
				@Override
				public void publish(LogRecord record) {
					if (UT2004DeathMatchRepeater.this.log != null) {
						UT2004DeathMatchRepeater.this.log.log(record);
					}
				}
			});	
			match.cleanUp();
			boolean exception = false;
			UT2004DeathMatchResult result = null;
			try {
				result = (UT2004DeathMatchResult) match.call();
			} catch (Exception e) {
				exception = true;
				results.add(null);
				exceptions.add(e);
			}
			if (!exception) {
				results.add(result);
				exceptions.add(null);
			}
		}
		matchConfig.setMatchId(token);
		outputAggregatedResults();
	}

	protected void outputAggregatedResults() {
		if (log != null && log.isLoggable(Level.FINE)) {
			log.fine(matchConfig.getMatchId().getToken() + ": Outputting aggregated match results into CSV files...");
		}
		IToken token = matchConfig.getMatchId();
		File outputDirectory = new File(matchConfig.getOutputDirectory().getAbsolutePath() + File.separator + matchConfig.getMatchId().getToken() + "-results");
		outputDirectory.mkdirs();
		outputAggregatedResults(outputDirectory);
		if (log != null && log.isLoggable(Level.INFO)) {
			UT2004MatchConfig config = matchConfig;
			log.info(config.getMatchId().getToken() + ": Aggregated match results output into CSV files.");
		}
	}
	
	protected void outputAggregatedResults(File outputDirectory) {
		UT2004MatchConfig config = matchConfig;
		
		// BOT IDS
		
		List<IToken> botIds = config.getAllBotIds();
		
		// WINS / DRAWS
		
		Map<IToken, Integer> wins = new LazyMap<IToken, Integer>() {
			@Override
			protected Integer create(IToken key) { return 0; }
		};
		Map<IToken, Integer> draws = new LazyMap<IToken, Integer>() {
			@Override
			protected Integer create(IToken key) { return 0; }
		};
		
		int matchFinished = 0; // HOW MANY MATCHES FINISHED WITHOUT EXCEPTION...
		for (UT2004DeathMatchResult result : results) {
			if (result == null) continue;
			++matchFinished;
			if (result.isDraw()) {
				for (IToken botId : botIds) {
					draws.put(botId, 1+draws.get(botId));
				}
			} else {
				wins.put(result.getWinnerBot(), 1+wins.get(result.getWinnerBot()));
			}
		}

		// KILLS
		
		HashMapMap<IToken, IToken, Integer> kills = new HashMapMap<IToken, IToken, Integer>();
		HashMap<IToken, Integer> scores = new HashMap<IToken, Integer>();
		HashMap<IToken, Integer> deaths = new HashMap<IToken, Integer>();
		HashMap<IToken, Integer> totalKills = new HashMap<IToken, Integer>();
		HashMap<IToken, Integer> totalKilled = new HashMap<IToken, Integer>();
		for (IToken botId1 : botIds) {
			scores.put(botId1, 0);
			deaths.put(botId1, 0);
			totalKills.put(botId1, 0);
			for (IToken botId2 : botIds) {
				kills.put(botId1, botId2, 0);
			}
		}
		for (UT2004DeathMatchResult result : results) {
			if (result == null) continue;
			for (IToken botId1 : botIds) {
				scores.put(botId1, scores.get(botId1) + result.getFinalScores().get(botId1).getScore());
				deaths.put(botId1, deaths.get(botId1) + result.getFinalScores().get(botId1).getDeaths());
				totalKills.put(botId1, totalKills.get(botId1) + result.getTotalKills().get(botId1));
				for (IToken botId2 : botIds) {
					kills.put(botId1, botId2, kills.get(botId1, botId2) + result.getKillCounts().get(botId1, botId2));					
				}
			}
		}
		
		//
		// OUTPUTTING AGGREGATED DATA
		//
		
		File resultFile = new File(outputDirectory.getAbsolutePath() + File.separator + "match-" + repeats + "x-" + config.getMatchId().getToken() + "-scores-aggregated.csv");
		FilePath.makeDirsToFile(resultFile);
		try {
			Formatter writer = new Formatter(resultFile);
			writer.format("botId;matches;matchFinished;win;winRatio;draw;drawRatio;lose;loseRatio;score;scoreAvg;kills;killsAvg;killedByOthers;killedByOthersAvg;deaths;deathsAvg;suicides;suicidesAvg");
			if (matchFinished > 0) {
				for (IToken botId : botIds) {
					writer.format(";");
					writer.format(botId.getToken());
					writer.format(";");
					writer.format(botId.getToken() + "Avg");
				}
				for (IToken botId : botIds) {
					writer.format
					(
						"\n%s;%d;%d;%d;%.3f;%d;%.3f;%d;%.3f;%d;%.3f;%d;%.3f;%d;%.3f;%d;%.3f;%d;%.3f",
						// BOT ID
						botId.getToken(),
						// MATCHES
						repeats,
						// MATCH FINISHED,
						matchFinished,
						// WIN
						wins.get(botId),
						((double)wins.get(botId)) / ((double)matchFinished),
						// DRAW
						draws.get(botId),
						((double)draws.get(botId)) / ((double)matchFinished),
						// LOSE					
						matchFinished - wins.get(botId) - draws.get(botId),
						((double)(matchFinished - wins.get(botId) - draws.get(botId))) / ((double)matchFinished),
						// SCORE
						scores.get(botId), 
						((double)scores.get(botId))/((double)matchFinished),
						// KILLS
						totalKills.get(botId),
						((double)totalKills.get(botId))/matchFinished,
						// KILLED BY OTHERS
						(deaths.get(botId) - kills.get(botId, botId)), // killed by others = how many times we died - suicides
						((double)(deaths.get(botId) - kills.get(botId, botId))) / ((double)matchFinished),
						// DEATHS
						deaths.get(botId),
						((double)deaths.get(botId)) / ((double)matchFinished),
						// SUICIDES
						kills.get(botId, botId), 
						((double)kills.get(botId, botId)) /((double)matchFinished)
					);
					for (IToken botId2 : botIds) {
						writer.format(";%d",   kills.get(botId).get(botId2));
						writer.format(";%.3f", ((double)kills.get(botId).get(botId2))/((double)matchFinished));
					}
				}
			} else {
				writer.format("NO MATCH FINISHED, ALL HAVE ENDED WITH AN EXCEPTION!");
			}
			try {
				writer.close();
			} catch (Exception e) {			
			}
		} catch (IOException e) {
			throw new PogamutIOException("Failed to write results!", e, log, this);
		}
		
		//
		// OUTPUTTING DATA per BOT
		//
		
		for (IToken botId1 : botIds) {
			File botFile = new File(outputDirectory.getAbsolutePath() + File.separator + "match-" + repeats + "x-" + config.getMatchId().getToken() + "-scores-" + botId1.getToken() + ".csv");
			FilePath.makeDirsToFile(botFile);
			try {
				Formatter writer = new Formatter(botFile);
				writer.format("match;matches;matchFinished;score;kills;killedByOthers;deaths;suicides");
				for (IToken botId2 : botIds) {
					writer.format(";");
					writer.format(botId2.getToken());
				}
				int i = 0;
				for (UT2004DeathMatchResult result : results) {
					++i;
					if (result == null) continue;					
					writer.format
					(
						"\n%d;%d;%d;%d;%d;%d;%d;%d",
						// MATCH NUMBER
						i,
						// MATCHES
						repeats,
						// MATCH FINISHED
						matchFinished,
						// SCORE
						result.getFinalScores().get(botId1).getScore(), 
						// KILLS
						result.getTotalKills().get(botId1),
						// KILLED BY OTHERS
						result.getWasKilled().get(botId1),
						// DEATHS
						result.getFinalScores().get(botId1).getDeaths(),
						// SUICIDES
						result.getSuicides().get(botId1)
					);
					for (IToken botId2 : botIds) {
						writer.format(";%d", result.getKillCounts().get(botId1).get(botId2));
					}
				}
				try {
					writer.close();
				} catch (Exception e) {			
				}
			} catch (IOException e) {
				throw new PogamutIOException("Failed to write results!", e, log, this);
			}
		}
		
		//
		// OUTPUTTING DATA per NATIVE BOT
		//
		
		List<IToken> customBots = new ArrayList<IToken>(config.getBots().keySet());
		Collections.sort(customBots, new Comparator<IToken>() {
			@Override
			public int compare(IToken o1, IToken o2) {
				return o1.getToken().compareTo(o2.getToken());
			}
		});
		
		if (results.size() > 0) {
			for (IToken botId1 : customBots) {
				File botFile = new File(outputDirectory.getAbsolutePath() + File.separator + "match-" + repeats + "x-" + config.getMatchId().getToken() + "-stats-" + botId1.getToken() + ".csv");
				FilePath.makeDirsToFile(botFile);
				try {
					Formatter writer = new Formatter(botFile);
					writer.format("match;");
					results.get(0).getBotObservers().get(botId1).getStats().outputHeader(writer);
					int i = 0;
					for (UT2004DeathMatchResult result : results) {
						++i;
						if (result == null) continue;
						writer.format("%d;", i);
						result.getBotObservers().get(botId1).getStats().outputStatLine(writer, result.getMatchTimeEnd());
					}
					try {
						writer.close();
					} catch (Exception e) {			
					}
				} catch (IOException e) {
					throw new PogamutIOException("Failed to write results!", e, log, this);
				}
			}
		}
	}
	
	
	
}
