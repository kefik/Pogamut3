package cz.cuni.amis.pogamut.ut2004.tournament.dm.table.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.pogamut.ut2004.tournament.utils.CSV;
import cz.cuni.amis.pogamut.ut2004.tournament.utils.CSV.CSVRow;
import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.maps.HashMapMap;
import cz.cuni.amis.utils.maps.LazyMap;

public class DMTableResults {

	public Map<String, DMTablePlayerResult> players = new LazyMap<String, DMTablePlayerResult>() {

		@Override
		protected DMTablePlayerResult create(String key) {
			return new DMTablePlayerResult(key);
		}
		
	};
	
	/**
	 * Player1 -> Player2 -> DMMatchResult
	 */
	public HashMapMap<String, String, DMMatchResult> results = new HashMapMap<String, String, DMMatchResult>();
	
	protected void info(String msg) {
		System.out.println("[INFO] " + msg);
	}
	
	protected void warn(String msg) {
		System.out.println("[WARN] " + msg);
	}
	
	protected void error(String msg) {
		System.out.println("[ERROR] " + msg);
	}
	
	public DMMatchResult getMatchResult(String plr1, String plr2) {
		if (plr1.compareToIgnoreCase(plr2) > 0) {
			String temp = plr2;
			plr2 = plr1;
			plr1 = temp;
		}
		return results.get(plr1, plr2);
	}
	
	public DMMatchResult addResult(String plr1, String plr2, int score1, int score2, boolean[] botLogicEx, String[] botEx) {
		if (plr1.compareToIgnoreCase(plr2) == 0) {
			throw new RuntimeException("Could not add result for " + plr1 + " vs. " + plr2 + " as their names are the same!");
		}
		
		if (plr1.compareToIgnoreCase(plr2) > 0) {
			String temp = plr2;
			plr2 = plr1;
			plr1 = temp;
			
			int tempI = score2;
			score2 = score1;
			score1 = tempI;
			
			boolean tempB = botLogicEx[1];
			botLogicEx[1] = botLogicEx[0];
			botLogicEx[0] = tempB;
			
			String tempS = botEx[1];
			botEx[1] = botEx[0];
			botEx[0] = tempS;
		}
		
		DMTablePlayerResult player1 = players.get(plr1);
		DMTablePlayerResult player2 = players.get(plr2);
		
		DMMatchResult result = new DMMatchResult(plr1, plr2, score1, score2, botLogicEx[0], botLogicEx[1], botEx[0], botEx[1]);
		
		DMMatchResult old = results.put(plr1, plr2, result);
		
		if (old != null) {
			throw new RuntimeException("There are more than one result for " + plr1 + " vs. " + plr2 + "! First result [" + old.score1 + ":" + old.score2 + "], second result [" + score1 + ":" + score2 + "].");
		}		
		
		player1.result(result);
		player2.result(result);
		
		return result;
	}
	
	public List<DMTablePlayerResult> resolve() {
		info("RESOLVING TABLE");
		
		List<DMTablePlayerResult> results = MyCollections.asList(this.players.values());
		Collections.sort(results, new Comparator<DMTablePlayerResult>() {

			@Override
			public int compare(DMTablePlayerResult o1, DMTablePlayerResult o2) {
				if (o1.wins == o2.wins) {
					DMMatchResult result = getMatchResult(o1.player, o2.player);
					if (result == null) return 0;
					return result.getScore(o2.player) - result.getScore(o1.player);
				}
				return o2.wins - o1.wins;
			}
			
		});
		
		for (int i = 0; i < results.size(); ) {
			int start = i;
			int end = results.size()-1;
			for (int j = i+1; j < results.size(); ++j) {
				if (results.get(i).wins == results.get(j).wins) continue;
				end = j-1;
				break;
			}
			i = end + 1;
			
			if (start == end) {
				results.get(start).position = start+1;
				continue;
			}
			
			// WE HAVE MORE PLAYERS WITH THE SAME NUMBER OF WINS
			
			if (start + 1 == end) {
				// THERE ARE TWO PLAYERS WITH THE SAME NUMBER OF WINS
				DMTablePlayerResult result1 = results.get(start);
				DMTablePlayerResult result2 = results.get(start+1);
				String player1 = results.get(start).player;
				String player2 = results.get(start+1).player;
				
				DMMatchResult match = getMatchResult(player1, player2);
				if (match != null) {
					if (match.isWin(player1)) {
						result1.position = start + 1;
						result2.position = start + 2;
						continue;
					} else 
					if (match.isWin(player2)) {
						result1.position = start + 2;
						result2.position = start + 1;
						continue;
					}
				}
				
				// NO MATCH INFO
				result1.position = end + 1;
				result2.position = end + 1;
			}
			
			// DO NOT RESOLVE MORE PLAYERS
			for (int j = start; j <= end; ++j) {
				results.get(j).position = end+1;
			}
		}
		
		// NOW GO RESOLVE "THE SAME WINS"
		
		for (int start = 0; start < results.size(); ) {
			int position = results.get(start).position;
			int end;
			for (end = start+1; end < results.size() && position == results.get(end).position; ++end);
			--end;
			
			if (start == end) {
				++start;
				continue;
			}
			
			// RESOLVE THE SITUATION
			// Do we have a player that dominates them all?		
			boolean dominating = false;
			for (int candidate = start; candidate <= end; ++candidate) {
				boolean candidateDominating = true;
				DMTablePlayerResult result = results.get(candidate);
				String candidatePlr = result.player;
				for (int other = start; other <= end; ++other) {
					if (candidate == other) continue;
					String otherPlr = results.get(other).player;
					DMMatchResult match = getMatchResult(candidatePlr, otherPlr);
					if (match == null) continue;
					if (!match.isWin(candidatePlr)) {
						candidateDominating = false;
						break;
					}						
				}
				if (candidateDominating) {
					// WE HAVE THE BEST OF THEM ALL
					if (candidate != start) {
						// => swap candidate and the start
						DMTablePlayerResult candidateResult = results.get(candidate);
						DMTablePlayerResult startResult = results.get(start);
						results.set(start, candidateResult);
						results.set(candidate, startResult);
					}
					// => renumber the positions
					results.get(start).position = results.get(start).position - (end - start); 
					dominating = true;
					break;
				}
			}
			if (dominating) {
				// WE HAVE FOUND DOMINATING CANDIDATE
				++start;
				continue;
			} else {
				// WE HAVE NOT
				start = end+1;
				continue;
			}			
		}
		
		
		return results;
	}
	
	/**
	 * Search the dir for *-bot-scores.csv and tries to read it. Does not peek into sub-dirs, use {@link #probeResults(File, boolean)} instead.
	 * @param dir
	 */
	public void probeResults(File dir) {
		probeResults(dir);
	}
	
	public void probeResults(File dir, boolean recursive) {
		for (File file : dir.listFiles()) {
			if (!file.exists()) continue;
			if (file.isDirectory() && recursive) probeResults(file, recursive);
			if (file.exists() && file.isFile()) {
				try {
					probeResultFile(file);
				} catch (Exception e) {
					throw new RuntimeException("Failed to process file: " + file.getAbsolutePath(), e);
				}
			}
		}
		
	}

	private void probeResultFile(File file) throws FileNotFoundException, IOException {
		if (!file.exists() || !file.isFile() || !file.getAbsolutePath().toLowerCase().endsWith("-result.csv")) return;
		
		info("Found result file: " + file.getAbsolutePath());
		
		File botScoresFile = new File(file.getAbsolutePath().replace("-result.csv", "-bot-scores.csv"));
		if (!botScoresFile.exists()) {
			error("Cannot locate bot-scores file at: " + botScoresFile);
			return;
		}
		info("Found bot-scores file: " + botScoresFile.getAbsolutePath());
		
		File logFile = new File(file.getAbsolutePath().replace("-result.csv", ".log"));
		if (!logFile.exists()) {
			error("Cannot locate log file at: " + logFile);
			return;
		}
		info("Found log file: " + logFile.getAbsolutePath());
		
		CSV csv = new CSV(file, ";", true);
		if (csv.rows.size() != 1) {
			warn("-- Result file contains invalid number of data rows (" + csv.rows.size() + "), ignoring.");
			return;
		}
		
		if (!csv.keys.contains("Winner")) {
			warn("-- Result file does not contain column 'Winner'. Ignoring.");
			return;
		}
		String winner = csv.rows.get(0).getString("Winner");
		if (winner.toLowerCase().contains("failure")) {
			error("-- Result file is indicating that the match has FAILED!");
			return;
		}
		
		probeBotScoresAndLogFile(botScoresFile, logFile);
	}
		
	private void probeBotScoresAndLogFile(File botScoresFile, File logFile) throws FileNotFoundException, IOException { 
		if (!botScoresFile.exists() || !botScoresFile.isFile() || !botScoresFile.getAbsolutePath().toLowerCase().endsWith("-bot-scores.csv")) return;
		
		CSV csv;
		
		csv = new CSV(botScoresFile, ";", true);
		
		if (csv.rows.size() != 2) {
			warn("-- Bot-scores file contains invalid number of data rows (" + csv.rows.size() + "), ignoring.");
			return;
		}
		
		if (!csv.keys.contains("botId")) {
			warn("-- Bot-scores file does not contain column 'botId'. Ignoring.");
			return;
		}
		
		if (!csv.keys.contains("score")) {
			warn("-- Bot-scores file does not contain column 'score'. Ignoring.");
			return;
		}
		
		CSVRow row1 = csv.rows.get(0);
		String player1 = row1.getString("botId");
		int score1 = row1.getInt("score");
		
		CSVRow row2 = csv.rows.get(1);
		String player2 = row2.getString("botId");
		int score2 = row2.getInt("score");
		
		// CHECK LOG FILE
		boolean[] botLogicEx = new boolean[] { false, false };
		String[] botEx = new String[] { "", "" };		
		boolean[] fatalErrorEvent = new boolean[] { false, false };
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(logFile));
			
			while (reader.ready()) {
				String line = reader.readLine();
				
				String player = null;
				int plrIndex = -1;
				int otherIndex = -1;
				
				if (line.contains(" " + player1 + "-StdOut ")) {
					plrIndex = 0;
					otherIndex = 1;
					player = player1;
				} else
				if (line.contains(" " + player2 + "-StdOut ")) {
					plrIndex = 1;
					otherIndex = 0;
					player = player2;
				} else {
					continue;
				}
				
				if (line.endsWith("Logic iteration exception.")) {
					botLogicEx[plrIndex] = true;
				}
								
				if (line.endsWith(" FatalErrorEvent[")) {
					fatalErrorEvent[plrIndex] = true;
				}
				
				if (fatalErrorEvent[plrIndex]) {
					botEx[plrIndex] += Const.NEW_LINE + line;
					if (line.endsWith(" " + player + "-StdOut ]")) {
						fatalErrorEvent[plrIndex] = false;
					}
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			error("Failed to probe log file at: " + logFile.getAbsolutePath());
			return;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {					
				}
			}
		}
		
		DMMatchResult result = addResult(player1, player2, score1, score2, botLogicEx, botEx);
		
		info("-- " + result.toString());
	}

	
	
}
