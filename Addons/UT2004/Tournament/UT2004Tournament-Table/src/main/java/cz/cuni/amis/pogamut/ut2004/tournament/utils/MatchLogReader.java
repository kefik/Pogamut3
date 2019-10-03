package cz.cuni.amis.pogamut.ut2004.tournament.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import cz.cuni.amis.utils.Const;

public class MatchLogReader {
	
	public static class PlayerLogResult {
		
		public String playerId;
		public String exceptions = "";
		public boolean exception = false;
		
		public PlayerLogResult(String playerId) {
			super();
			this.playerId = playerId;
		}

		public boolean isException() {
			return exception;
		}
		
		public void addException(String exception) {
			this.exception = true;
			if (!exceptions.isEmpty()) exceptions += Const.NEW_LINE;
			exceptions += exception;
		}
		
		@Override
		public String toString() {
			return "PlayerLogResult[playerId=" + playerId + ",exception=" + exception + (exception ? "\n" + exceptions : "") + "\n]";
		}
	}
	
	public static class MatchLogReaderResult {
		
		/**
		 * Player ID -> {@link PlayerLogResult}.
		 */
		public Map<String, PlayerLogResult> results = new HashMap<String, PlayerLogResult>();
		
	}
	
	/**
	 * @param logFile Can be a zip-file (must ends with .zip then)
	 * @param playerIds
	 * @return
	 */
	public MatchLogReaderResult read(File logFile, String... playerIds) {
		return read(logFile, Arrays.asList(playerIds));
	}
	
	/**
	 * @param logFile Can be a zip-file (must ends with .zip then)
	 * @param playerIds
	 * @return
	 */
	public MatchLogReaderResult read(File logFile, List<String> playerIds) {
		
		MatchLogReaderResult result = new MatchLogReaderResult();
		
		List<Pattern> linePatterns = new ArrayList<Pattern>(playerIds.size());
		List<Pattern> endLinePatterns = new ArrayList<Pattern>(playerIds.size());
		for (String playerId : playerIds) {
			linePatterns.add(Pattern.compile(playerId + "-[0-9]+-StdOut"));
			endLinePatterns.add(Pattern.compile(playerId + "-[0-9]+-StdOut \\]$"));
			result.results.put(playerId, new PlayerLogResult(playerId));
		}				 
		
		boolean[] logicEx = new boolean[playerIds.size()];
		Arrays.fill(logicEx, false);
		
		int[] fatalErrorEvent = new int[playerIds.size()];
		Arrays.fill(fatalErrorEvent, 0);
		
		String[] currExceptions = new String[playerIds.size()];
		Arrays.fill(currExceptions, "");		
		
		// CHECK FOR EXCEPTION WITHIN LOG FILE	
				
		BufferedReader reader = null;
		ZipInputStream zippedIS = null;
		try {
			
			if (logFile.getName().toLowerCase().endsWith(".zip")) {
				zippedIS = new ZipInputStream(new FileInputStream(logFile));
				ZipEntry zipEntry = zippedIS.getNextEntry();
				
				reader = new BufferedReader(new InputStreamReader(zippedIS));	
			} else {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));	
			}
			
			
			int playerIndex = -1;
			int otherIndex = -1;
			
			while (reader.ready()) {
				String line = reader.readLine();
				
				String team = null;
				for (int i = 0; i < linePatterns.size(); ++i) {
					Pattern pattern = linePatterns.get(i);
					if (pattern.matcher(line).find()) {
						otherIndex = playerIndex;
						playerIndex = i;
						team = playerIds.get(i);
						break;
					}					
				}
				if (team == null) continue;
				
				if (line.endsWith(" FatalErrorEvent[")) {
					fatalErrorEvent[playerIndex] += 1;
				}
				
				if (fatalErrorEvent[playerIndex] > 0) {
					if (!currExceptions[playerIndex].isEmpty()) currExceptions[playerIndex] += Const.NEW_LINE;
					currExceptions[playerIndex] += line;					
					if (endLinePatterns.get(playerIndex).matcher(line).find()) {
						fatalErrorEvent[playerIndex] -= 1;
						if (fatalErrorEvent[playerIndex] <= 0) {
							fatalErrorEvent[playerIndex] = 0;
							if (isBotException(currExceptions[playerIndex])) {
								result.results.get(team).addException(currExceptions[playerIndex]);
							}
							currExceptions[playerIndex] = "";
						}
					}
				}				
			}
			
			for (playerIndex = 0; playerIndex < currExceptions.length; ++playerIndex) {
				if (!currExceptions[playerIndex].isEmpty()) { 
					if (isBotException(currExceptions[playerIndex])) {
						result.results.get(playerIds.get(playerIndex)).addException(currExceptions[playerIndex]);
					}
					currExceptions[playerIndex] = "";
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to probe log file at: " + logFile.getAbsolutePath());
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
				
		return result;
	}
	
	private boolean isBotException(String exception) {
		if (exception.contains("class cz.cuni.amis.utils.exception.PogamutIOException: java.net.SocketException: Connection reset by peer: socket write error")) return false;
		if (exception.contains("UT2004Parser: Can't parse next message: java.net.SocketException: Connection reset")) return false;
		if (exception.contains("Message:    agent kill() requested")) return false;
		return true;
	}
	
	public static void main(String[] args) {
		// FOR TESTING PURPOSES
		String team1 = "DenisJudin";
		String team2 = "JanHolan";
		File file = new File("d:\\Workspaces\\MFF\\NAIL068-UmeleBytosti\\Lectures\\AB2019-Labs\\Lab-06-TDM\\Students\\_Results\\1-DM-Rankin-FE\\Match-DenisJudin-vs-JanHolan-1-DM-Rankin-FE\\match-Match-DenisJudin-vs-JanHolan-1-DM-Rankin-FE.log");
		
		MatchLogReader reader = new MatchLogReader();
		
		MatchLogReaderResult result = reader.read(file, team1, team2);
		
		System.out.println(result.results.get(team1));
		System.out.println(result.results.get(team2));
	}

}
