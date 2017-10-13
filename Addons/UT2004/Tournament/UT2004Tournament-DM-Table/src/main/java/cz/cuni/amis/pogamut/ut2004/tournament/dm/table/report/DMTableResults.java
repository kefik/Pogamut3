package cz.cuni.amis.pogamut.ut2004.tournament.dm.table.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.pogamut.ut2004.tournament.dm.table.report.CSV.CSVRow;
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
	
	public DMMatchResult addResult(String plr1, String plr2, int score1, int score2) {
		if (plr1.compareToIgnoreCase(plr2) == 0) {
			throw new RuntimeException("Could not add result for " + plr1 + " vs. " + plr2 + " as their names are the same!");
		}
		
		if (plr1.compareToIgnoreCase(plr2) > 0) {
			String temp = plr2;
			plr2 = plr1;
			plr1 = temp;
		}
		
		DMTablePlayerResult player1 = players.get(plr1);
		DMTablePlayerResult player2 = players.get(plr2);
		
		DMMatchResult result = new DMMatchResult(plr1, plr2, score1, score2);
		
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
		
		// RESORT PLAYERS ACCORDING TO THEIR REAL POSITION
		Collections.sort(results, new Comparator<DMTablePlayerResult>() {

			@Override
			public int compare(DMTablePlayerResult o1, DMTablePlayerResult o2) {
				return o1.position - o1.position;
			}
			
		});
		
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
					probeFile(file);
				} catch (Exception e) {
					throw new RuntimeException("Failed to process file: " + file.getAbsolutePath(), e);
				}
			}
		}
		
	}

	private void probeFile(File file) throws FileNotFoundException, IOException {
		if (!file.exists() || !file.isFile() || !file.getAbsolutePath().toLowerCase().endsWith("-bot-scores.csv")) return;
		info("Found results: " + file.getAbsolutePath());
		
		CSV csv;
		
		csv = new CSV(file, ";", true);
		
		if (csv.rows.size() != 2) {
			warn("-- Result file contains invalid number of data rows (" + csv.rows.size() + "), ignoring.");
			return;
		}
		
		if (!csv.keys.contains("botId")) {
			warn("-- Result file does not contain column 'botId'. Ignoring.");
			return;
		}
		
		if (!csv.keys.contains("score")) {
			warn("-- Result file does not contain column 'score'. Ignoring.");
			return;
		}
		
		CSVRow row1 = csv.rows.get(0);
		String player1 = row1.getString("botId");
		int score1 = row1.getInt("score");
		
		CSVRow row2 = csv.rows.get(1);
		String player2 = row2.getString("botId");
		int score2 = row2.getInt("score");
		
		DMMatchResult result = addResult(player1, player2, score1, score2);
		
		info("-- " + result.toString());
	}

	
	
}
