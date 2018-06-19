package cz.cuni.amis.pogamut.ut2004.tournament.tdm.table.report.one;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import cz.cuni.amis.pogamut.ut2004.tournament.utils.CSV;
import cz.cuni.amis.pogamut.ut2004.tournament.utils.CSV.CSVRow;
import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.maps.HashMapMap;
import cz.cuni.amis.utils.maps.LazyMap;

public class TDMOneMatchTableResults {
	
	public String mapName;
	
	public int mapNumber;

	public Map<String, TDMOneMatchTableTeamResult> teams = new LazyMap<String, TDMOneMatchTableTeamResult>() {

		@Override
		protected TDMOneMatchTableTeamResult create(String key) {
			return new TDMOneMatchTableTeamResult(key);
		}
		
	};
	
	/**
	 * Player1 -> Player2 -> TDMMatchResult
	 */
	public HashMapMap<String, String, TDMOneMatchResult> results = new HashMapMap<String, String, TDMOneMatchResult>();
	
	public TDMOneMatchTableResults(String mapName, int mapNumber) {
		this.mapName = mapName;
		this.mapNumber = mapNumber;
	}
	
	protected void info(String msg) {
		System.out.println("[INFO] " + msg);
	}
	
	protected void warn(String msg) {
		System.out.println("[WARN] " + msg);
	}
	
	protected void error(String msg) {
		System.out.println("[ERROR] " + msg);
	}
	
	public TDMOneMatchResult getMatchResult(String plr1, String plr2) {
		if (plr1.compareToIgnoreCase(plr2) > 0) {
			String temp = plr2;
			plr2 = plr1;
			plr1 = temp;
		}
		return results.get(plr1, plr2);
	}
	
	public TDMOneMatchResult addResult(String team1, String team2, int score1, int score2, boolean[] botLogicEx, String[] botEx) {
		if (team1.compareToIgnoreCase(team2) == 0) {
			throw new RuntimeException("Could not add result for " + team1 + " vs. " + team2 + " as their names are the same!");
		}
		
		if (team1.compareToIgnoreCase(team2) > 0) {
			String temp = team2;
			team2 = team1;
			team1 = temp;
			
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
		
		TDMOneMatchTableTeamResult team1Result = teams.get(team1);
		TDMOneMatchTableTeamResult team2Result = teams.get(team2);
		
		TDMOneMatchResult result = new TDMOneMatchResult(team1, team2, score1, score2, botLogicEx[0], botLogicEx[1], botEx[0], botEx[1]);
		
		TDMOneMatchResult old = results.put(team1, team2, result);
		
		if (old != null) {
			throw new RuntimeException("There are more than one result for " + team1 + " vs. " + team2 + "! First result [" + old.score1 + ":" + old.score2 + "], second result [" + score1 + ":" + score2 + "].");
		}		
		
		team1Result.result(result);
		team2Result.result(result);
		
		return result;
	}
	
	public List<TDMOneMatchTableTeamResult> resolve() {
		info("RESOLVING TABLE");
		
		List<TDMOneMatchTableTeamResult> results = MyCollections.asList(this.teams.values());
		Collections.sort(results, new Comparator<TDMOneMatchTableTeamResult>() {

			@Override
			public int compare(TDMOneMatchTableTeamResult o1, TDMOneMatchTableTeamResult o2) {
				if (o1.wins == o2.wins) {
					TDMOneMatchResult result = getMatchResult(o1.team, o2.team);
					if (result == null) return 0;
					return result.getScore(o2.team) - result.getScore(o1.team);
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
				TDMOneMatchTableTeamResult result1 = results.get(start);
				TDMOneMatchTableTeamResult result2 = results.get(start+1);
				String player1 = results.get(start).team;
				String player2 = results.get(start+1).team;
				
				TDMOneMatchResult match = getMatchResult(player1, player2);
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
				TDMOneMatchTableTeamResult result = results.get(candidate);
				String candidatePlr = result.team;
				for (int other = start; other <= end; ++other) {
					if (candidate == other) continue;
					String otherPlr = results.get(other).team;
					TDMOneMatchResult match = getMatchResult(candidatePlr, otherPlr);
					if (!match.isWin(candidatePlr)) {
						candidateDominating = false;
						break;
					}						
				}
				if (candidateDominating) {
					// WE HAVE THE BEST OF THEM ALL
					if (candidate != start) {
						// => swap candidate and the start
						TDMOneMatchTableTeamResult candidateResult = results.get(candidate);
						TDMOneMatchTableTeamResult startResult = results.get(start);
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

	private void probeResultFile(File resultFile) throws FileNotFoundException, IOException {
		if (!resultFile.exists() || !resultFile.isFile() || !resultFile.getAbsolutePath().toLowerCase().endsWith("-result.csv")) return;
		
		info("Found result file: " + resultFile.getAbsolutePath());
		
		File teamScoresFile = new File(resultFile.getAbsolutePath().replace("-result.csv", "-team-scores.csv"));
		if (!teamScoresFile.exists()) {
			error("Cannot locate team-scores file at: " + teamScoresFile);
			return;
		}
		info("Found team-scores file: " + teamScoresFile.getAbsolutePath());
		
		File logFile = new File(resultFile.getAbsolutePath().replace("-result.csv", ".log"));
		if (logFile.exists()) {
			info("Found non-zipped log file, zipping: " + logFile.getAbsolutePath());
			zipLogFile(logFile);
		}
		logFile = new File(resultFile.getAbsolutePath().replace("-result.csv", ".log.zip"));
		if (!logFile.exists()) {
			error("Cannot locate zipped-log file at: " + logFile);
			return;
		}
		info("Found zipped-log file: " + logFile.getAbsolutePath());
		
		CSV resultCSV = new CSV(resultFile, ";", true);
		if (resultCSV.rows.size() != 1) {
			warn("-- Result file contains invalid number of data rows (" + resultCSV.rows.size() + "), ignoring.");
			return;
		}
		
		if (!resultCSV.keys.contains("Winner")) {
			warn("-- Result file does not contain column 'Winner'. Ignoring.");
			return;
		}
		String winner = resultCSV.rows.get(0).getString("Winner");
		if (winner.toLowerCase().contains("failure")) {
			error("-- Result file is indicating that the match has FAILED!");
			return;
		}
		
		CSV teamScoresCSV = new CSV(teamScoresFile, ";", true);
		if (teamScoresCSV.rows.size() != 2) {
			warn("-- Team scores contains invalid number of data rows (" + teamScoresCSV.rows.size() + "), ignoring.");
			return;
		}
		
		File botScoresFile = new File(resultFile.getAbsolutePath().replace("-result.csv", "-bot-scores.csv"));
		if (!botScoresFile.exists()) {
			error("Cannot locate bot-scores file at: " + botScoresFile.getAbsolutePath());
			return;
		}
		info("Found bot-scores file: " + botScoresFile.getAbsolutePath());
		CSV botScoresCSV = new CSV(botScoresFile, ";", true);
		if (!botScoresCSV.keys.contains("team")) {
			warn("-- Bot-scores file does not contain column 'team'. Ignoring.");
			return;
		}
		if (!botScoresCSV.keys.contains("botId")) {
			warn("-- Bot-scores file does not contain column 'botId'. Ignoring.");
			return;
		}		
		if (!botScoresCSV.keys.contains("score")) {
			warn("-- Bot-scores file does not contain column 'score'. Ignoring.");
			return;
		}

		// resultFile -> global result
		// teamScoresFile -> contains team IDs
		
		String team1 = teamScoresCSV.rows.get(0).getString("teamId");
		String team2 = teamScoresCSV.rows.get(1).getString("teamId");
		if (team1.compareTo(team2) > 0) {
			String temp = team2;
			team2 = team1;
			team1 = temp;
		}
		
		// PROBE RESULTS
		extractResults(team1, team2, resultFile, resultCSV, teamScoresFile, teamScoresCSV, botScoresFile, botScoresCSV, logFile);		
	}
		
	private void zipLogFile(File logFile) {
		try {
			File targetFile = new File(logFile.getAbsolutePath()+".zip");
			FileOutputStream fos = new FileOutputStream(targetFile);
		    ZipOutputStream zipOut = new ZipOutputStream(fos);
		    FileInputStream fis = new FileInputStream(logFile);
		    ZipEntry zipEntry = new ZipEntry(logFile.getName());
		    zipOut.putNextEntry(zipEntry);
		    final byte[] bytes = new byte[1024];
		    int length;
		    while((length = fis.read(bytes)) >= 0) {
		        zipOut.write(bytes, 0, length);
		    }
		    zipOut.close();
		    fis.close();
		    fos.close();
		} catch (Exception e) {
			throw new RuntimeException("Failed to zip log file.", e);
		}
		logFile.delete();
	}

	private void extractResults(String team1, String team2, File resultFile, CSV resultCSV, File teamScoresFile, CSV teamScoresCSV, File botScoresFile, CSV botScoresCSV, File logFileZip) {
		// EXTRACT SCORES
		int score1 = 0;
		int score2 = 0;		
		for (int i = 0; i < botScoresCSV.rows.size(); ++i) {
			CSVRow row = botScoresCSV.rows.get(i);
			String team = row.getString("team");
			String botId = row.getString("botId");
			int score = row.getInt("score");
			
			if (team.equals(team1)) {
				score1 += score;
			}
			if (team.equals(team2)) {
				score2 += score;
			}			
		}
		
		// CHECK FOR EXCEPTION WITHIN LOG FILE	
		Pattern team1Pattern = Pattern.compile(team1 + "-[0-9]+-StdOut");
		Pattern team2Pattern = Pattern.compile(team2 + "-[0-9]+-StdOut");
		
		boolean[] teamLogicEx = new boolean[] { false, false };
		String[] teamEx = new String[] { "", "" };		
		boolean[] fatalErrorEvent = new boolean[] { false, false };
		
		info("-- reading zipped log file...");
		
		BufferedReader reader = null;
		ZipInputStream zippedIS = null;
		try {
			
			zippedIS = new ZipInputStream(new FileInputStream(logFileZip));
			ZipEntry zipEntry = zippedIS.getNextEntry();
			
			reader = new BufferedReader(new InputStreamReader(zippedIS));
			
			while (reader.ready()) {
				String line = reader.readLine();
				
				String team = null;
				int teamIndex = -1;
				int otherIndex = -1;
				
				if (team1Pattern.matcher(line).find()) {
					teamIndex = 0;
					otherIndex = 1;
					team = team1;
				} else
				if (team2Pattern.matcher(line).find()) {
					teamIndex = 1;
					otherIndex = 0;
					team = team2;
				} else {
					continue;
				}
				
				if (line.endsWith("Logic iteration exception.")) {
					teamLogicEx[teamIndex] = true;
				}
								
				if (line.endsWith(" FatalErrorEvent[")) {
					fatalErrorEvent[teamIndex] = true;
				}
				
				if (fatalErrorEvent[teamIndex]) {
					teamEx[teamIndex] += Const.NEW_LINE + line;
					if (line.endsWith(" " + team + "-StdOut ]")) {
						fatalErrorEvent[teamIndex] = false;
					}
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			error("Failed to probe log file at: " + logFileZip.getAbsolutePath());
			return;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {					
				}
			}
			if (zippedIS != null) {
				try {
					zippedIS.close();
				} catch (Exception e) {					
				}
			}			
		}
		
		TDMOneMatchResult result = addResult(team1, team2, score1, score2, teamLogicEx, teamEx);
		
		info("-- " + result.toString());
	}

	
	
}