package cz.cuni.amis.pogamut.ut2004.tournament.tdm.table.report.summary;

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
import java.util.regex.Pattern;

import cz.cuni.amis.pogamut.ut2004.tournament.utils.CSV;
import cz.cuni.amis.pogamut.ut2004.tournament.utils.CSV.CSVRow;
import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.maps.HashMapMap;
import cz.cuni.amis.utils.maps.LazyMap;

public class TDMMatchesTableResults {
	
	public Map<String, TDMMatchesTableTeamResult> teams = new LazyMap<String, TDMMatchesTableTeamResult>() {

		@Override
		protected TDMMatchesTableTeamResult create(String key) {
			return new TDMMatchesTableTeamResult(key);
		}
		
	};
	
	/**
	 * Team1 -> Team2 -> TDMMatchResult
	 */
	public HashMapMap<String, String, TDMMatchesResult> results = new HashMapMap<String, String, TDMMatchesResult>();
	
	public TDMMatchesTableResults() {
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
	
	public TDMMatchesResult getMatchResult(String plr1, String plr2) {
		if (plr1.compareToIgnoreCase(plr2) > 0) {
			String temp = plr2;
			plr2 = plr1;
			plr1 = temp;
		}
		return results.get(plr1, plr2);
	}
	
	public TDMMatchesResult addResult(String team1, String team2, int score1, int score2, int frags1, int frags2, int deaths1, int deaths2, int exceptions1, int exceptions2, String exceptions1Str, String exceptions2Str) {
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
			
			tempI = frags2;
			frags2 = frags1;
			frags1 = tempI;
			
			tempI = deaths2;
			deaths2 = deaths1;
			deaths1 = tempI;
			
			tempI = exceptions2;
			exceptions2 = exceptions1;
			exceptions1 = tempI;
			
			temp = exceptions1Str;
			exceptions1Str = exceptions1Str;
			exceptions2Str = temp;
		}
		
		TDMMatchesTableTeamResult team1Result = teams.get(team1);
		TDMMatchesTableTeamResult team2Result = teams.get(team2);
		
		TDMMatchesResult result = new TDMMatchesResult(team1, team2, score1, score2, frags1, frags2, deaths1, deaths2, exceptions1, exceptions2, exceptions1Str, exceptions2Str);
		
		TDMMatchesResult old = results.put(team1, team2, result);
		
		if (old != null) {
			throw new RuntimeException("There are more than one result for " + team1 + " vs. " + team2 + "! First result [" + old.score1 + ":" + old.score2 + "], second result [" + score1 + ":" + score2 + "].");
		}		
		
		team1Result.result(result);
		team2Result.result(result);
		
		return result;
	}
	
	public List<TDMMatchesTableTeamResult> resolve() {
		info("RESOLVING TABLE");
		
		List<TDMMatchesTableTeamResult> results = MyCollections.asList(this.teams.values());
		Collections.sort(results, new Comparator<TDMMatchesTableTeamResult>() {

			@Override
			public int compare(TDMMatchesTableTeamResult o1, TDMMatchesTableTeamResult o2) {
				if (o1.wins == o2.wins) {
					TDMMatchesResult result = getMatchResult(o1.team, o2.team);
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
				TDMMatchesTableTeamResult result1 = results.get(start);
				TDMMatchesTableTeamResult result2 = results.get(start+1);
				String player1 = results.get(start).team;
				String player2 = results.get(start+1).team;
				
				TDMMatchesResult match = getMatchResult(player1, player2);
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
				TDMMatchesTableTeamResult result = results.get(candidate);
				String candidatePlr = result.team;
				for (int other = start; other <= end; ++other) {
					if (candidate == other) continue;
					String otherPlr = results.get(other).team;
					TDMMatchesResult match = getMatchResult(candidatePlr, otherPlr);
					if (!match.isWin(candidatePlr)) {
						candidateDominating = false;
						break;
					}						
				}
				if (candidateDominating) {
					// WE HAVE THE BEST OF THEM ALL
					if (candidate != start) {
						// => swap candidate and the start
						TDMMatchesTableTeamResult candidateResult = results.get(candidate);
						TDMMatchesTableTeamResult startResult = results.get(start);
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
	
}
