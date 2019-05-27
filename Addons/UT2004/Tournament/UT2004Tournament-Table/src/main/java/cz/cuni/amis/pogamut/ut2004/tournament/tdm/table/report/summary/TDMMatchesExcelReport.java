package cz.cuni.amis.pogamut.ut2004.tournament.tdm.table.report.summary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.cuni.amis.pogamut.ut2004.tournament.tdm.table.report.one.TDMOneMatchExcelReport;
import cz.cuni.amis.pogamut.ut2004.tournament.tdm.table.report.one.TDMOneMatchResult;
import cz.cuni.amis.pogamut.ut2004.tournament.tdm.table.report.one.TDMOneMatchTableResults;
import cz.cuni.amis.pogamut.ut2004.tournament.utils.ExcelReport;
import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.FileAppender;
import cz.cuni.amis.utils.maps.LazyMap;
import jxl.write.WritableSheet;
import jxl.write.biff.WritableWorkbookImpl;

public class TDMMatchesExcelReport extends ExcelReport {

	private File resultDir;
	private File outputFile;
	
	private static final Pattern DIR_NAME = Pattern.compile("([0-9]*)-(.*)");
	
	public TDMMatchesExcelReport(File resultDir, File outputFile) {
		this.resultDir = resultDir;
		this.outputFile = outputFile;
	}
	
	private FileOutputStream outputStream;
	private WritableWorkbookImpl work;
	
	private TDMMatchesTableResults tableResults;
	private List<TDMMatchesTableTeamResult> players;

	public void info(String msg) {
		System.out.println("[INFO] " + msg);
	}
	
	public void warn(String msg) {
		System.out.println("[WARN] " + msg);
	}
	
	public void error(String msg) {
		System.out.println("[ERROR] " + msg);
	}
	
	private static String getName(File file) {
		if (file.getAbsolutePath().indexOf("/") > 0) {
			return file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/")+1);
		} else {
			return file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("\\")+1);
		}
	}
	
	public synchronized void generate() {
		// GATHER RESUTS FOR ALL MAPS
		List<TDMOneMatchTableResults> mapResults = new ArrayList<TDMOneMatchTableResults>();
		
		for (File dir : resultDir.listFiles()) {
			if (!dir.isDirectory()) continue;
			String name = getName(dir);
			Matcher m = DIR_NAME.matcher(name);
			if (!m.find()) continue;
			String mapNumberStr = m.group(1);
			String mapName = m.group(2);
			int mapNumber = 0;
			try {
				mapNumber = Integer.parseInt(mapNumberStr);
			} catch (Exception e) {
				continue;
			}
			
			TDMOneMatchTableResults mapResult = new TDMOneMatchExcelReport(dir, new File(dir, "Result-" + name + ".xls"), mapName, mapNumber).generate();
			mapResults.add(mapResult);
		}
		
		// FIND ALL TEAM-PAIRS (combination only) FROM ALL MAP RESULTS
		Map<String, Set<String>> teamPairs = new LazyMap<String, Set<String>>() {
			@Override
			protected Set<String> create(String key) {
				return new HashSet<String>();
			}
		};
		for (TDMOneMatchTableResults mapResult : mapResults) {
			for (String teamA : mapResult.teams.keySet()) {
				for (String teamB : mapResult.teams.keySet()) {
					if (teamA.compareTo(teamB) >= 0) continue;
					teamPairs.get(teamA).add(teamB);
				}	
			}
		}		
		// NOW WE KNOW WHO PARTICIPATED
		
		// GO THROUGH RESULTS ACCORDING TO PAIRS
		this.tableResults = new TDMMatchesTableResults();
		
		for (String team1 : teamPairs.keySet()) {
			for (String team2 : teamPairs.get(team1)) {
				int score1 = 0;
				int score2 = 0;
				int frags1 = 0;
				int frags2 = 0;
				int deaths1 = 0;
				int deaths2 = 0;
				int exceptions1 = 0;
				int exceptions2 = 0;
				String exceptions1Trace = "";
				String exceptions2Trace = "";
				
				for (TDMOneMatchTableResults mapResult : mapResults) {
					TDMOneMatchResult oneResult = mapResult.getMatchResult(team1, team2);	
					if (oneResult == null) {
						// no match result
						continue;
					}
					if (oneResult.isException(team1)) {
						++exceptions1;
					}
					if (oneResult.isException(team2)) {
						++exceptions2;
					}
					if (oneResult.isWin(team1)) {
						++score1;
					}
					if (oneResult.isWin(team2)) {
						++score2;
					}					
					frags1 += oneResult.getScore(team1);
					frags2 += oneResult.getScore(team2);
					deaths1 += oneResult.getScore(team2);
					deaths2 += oneResult.getScore(team1);
					
					if (oneResult.getExceptionsTrace(team1) != null && !oneResult.getExceptionsTrace(team1).isEmpty()) {
						if (!exceptions1Trace.isEmpty()) exceptions1Trace += Const.NEW_LINE;
						exceptions1Trace = oneResult.getExceptionsTrace(team1);
					}
					if (oneResult.getExceptionsTrace(team2) != null && !oneResult.getExceptionsTrace(team2).isEmpty()) {
						if (!exceptions2Trace.isEmpty()) exceptions2Trace += Const.NEW_LINE;
						exceptions2Trace = oneResult.getExceptionsTrace(team2);
					}
					
				}
				
				tableResults.addResult(team1, team2, score1, score2, frags1, frags2, deaths1, deaths2, exceptions1, exceptions2, exceptions1Trace, exceptions2Trace);
			}
		}
		
		// RESOLVE THE TABLE
		this.players = tableResults.resolve();
		
		// PRODUCE THE EXCEL
		produceExcel(outputFile);
		
		// SUMMARIZE EXCEPTIONS
		saveExceptions(tableResults);
		
	}
	
	@Override
	protected void produceExcel(File outputFile, WritableWorkbookImpl workbook, WritableSheet sheet) throws Exception {
		info("OUTPUTING RESULTS");
		
		for (TDMMatchesTableTeamResult player : players) {
			info("-- " + player.position + ". " + player.team + " (W" + player.wins + ":D" + player.draws + ":L" + player.loses + ":E" + player.exceptions + ") (F" + player.frags + ":D" + player.deaths + ")");
		}
		
		List<TDMMatchesTableTeamResult> playersAlpha = new ArrayList<TDMMatchesTableTeamResult>(players);
		Collections.sort(playersAlpha, new Comparator<TDMMatchesTableTeamResult>() {
			@Override
			public int compare(TDMMatchesTableTeamResult o1, TDMMatchesTableTeamResult o2) {
				return o1.team.compareTo(o2.team);
			}		
		});		
		
		
		// COLUMNS POSITION | PLAYER | WINS | DRAWS | LOSES | EXCEPTIONS | FRAGS | DEATHS
		
		// ROWS:
		// TABLE NAME
		// Tournament type
		// Empty
		// HEADERS
		// players
		
		sheet.addCell(newStringCell("A", 0, "DeathMatch Tournament"));
		sheet.addCell(newStringCell("A", 1, "Table"));
		sheet.addCell(newStringCell("A", 2, ""));
		
		sheet.addCell(center(newStringCell("A", 3, "No.")));
		sheet.addCell(center(newStringCell("B", 3, "Participant")));
		sheet.addCell(center(newStringCell("C", 3, "Frags")));
		sheet.addCell(center(newStringCell("D", 3, ":")));
		sheet.addCell(center(newStringCell("E", 3, "Deaths")));
		sheet.addCell(center(newStringCell("F", 3, "#Wins")));
		sheet.addCell(center(newStringCell("G", 3, "#Draws")));
		sheet.addCell(center(newStringCell("H", 3, "#Loses")));
		sheet.addCell(center(newStringCell("I", 3, "#Exceptions")));
		
		int col = 0;
		int row = 3;
		
		for (TDMMatchesTableTeamResult player : players) {
			++row;
			
			sheet.addCell(center(newIntCell   ("A", row, player.position)));
			sheet.addCell(newStringCell("B", row, player.team));
			sheet.addCell(center(newIntCell   ("C", row, player.frags)));
			sheet.addCell(center(newStringCell("D", row, ":")));
			sheet.addCell(center(newIntCell   ("E", row, player.deaths)));
			sheet.addCell(center(newIntCell   ("F", row, player.wins)));
			sheet.addCell(center(newIntCell   ("G", row, player.draws)));
			sheet.addCell(center(newIntCell   ("H", row, player.loses)));
			sheet.addCell(center(newIntCell   ("I", row, player.exceptions)));
		}
		
		// =====
		// TABLE
		// =====
		
		int startCol = 10;
		int startRow = 3;
		
		col = startCol+1;
		row = startRow;
		
		// TOP ROW
		
		sheet.addCell(newStringCell(col++, row, "TABLE"));
		
		for (TDMMatchesTableTeamResult player : playersAlpha) {
			sheet.addCell(newStringCell(col++, row, ""));
			sheet.addCell(center(textVertical(newStringCell(col++, row, player.team))));
			sheet.addCell(newStringCell(col++, row, ""));				
		}
		
		sheet.addCell(center(textVertical(newStringCell(col++, row, "Frags"))));
		sheet.addCell(center(textVertical(newStringCell(col++, row, ""))));
		sheet.addCell(center(textVertical(newStringCell(col++, row, "Deaths"))));
		sheet.addCell(center(textVertical(newStringCell(col++, row, "Wins"))));
		sheet.addCell(center(textVertical(newStringCell(col++, row, "Draws"))));
		sheet.addCell(center(textVertical(newStringCell(col++, row, "Loses"))));
		sheet.addCell(center(textVertical(newStringCell(col++, row, "Exceptions"))));
					
		// TABLE
		
		for (TDMMatchesTableTeamResult player1 : playersAlpha) {
			col = startCol+1;
			++row;
			
			sheet.addCell(newStringCell(col++, row, player1.team));
			
			for (TDMMatchesTableTeamResult player2 : playersAlpha) {
				if (player1 == player2) {
					sheet.addCell(newStringCell(col++, row, ""));
					sheet.addCell(center(newStringCell(col++, row, "X")));
					sheet.addCell(newStringCell(col++, row, ""));
					continue;						
				}
				
				TDMMatchesResult match = tableResults.getMatchResult(player1.team, player2.team);
				
				if (match == null) {
					sheet.addCell(center(newStringCell(col++, row, "x")));
					sheet.addCell(center(newStringCell(col++, row, ":")));
					sheet.addCell(center(newStringCell(col++, row, "x")));
					continue;
				}
				
				sheet.addCell(center(newIntCell(col++, row, match.getScore(player1.team))));
				sheet.addCell(center(newStringCell(col++, row, ":")));
				sheet.addCell(center(newIntCell(col++, row, match.getScore(player2.team))));				
			}
			
			sheet.addCell(center(newIntCell(col++, row, player1.frags)));
			sheet.addCell(center(newStringCell(col++, row, ":")));
			sheet.addCell(center(newIntCell(col++, row, player1.deaths)));
			sheet.addCell(center(newIntCell(col++, row, player1.wins)));
			sheet.addCell(center(newIntCell(col++, row, player1.draws)));
			sheet.addCell(center(newIntCell(col++, row, player1.loses)));
			sheet.addCell(center(newIntCell(col++, row, player1.exceptions)));
		}
		
	}
	
	private void saveExceptions(TDMMatchesTableResults results) {
		info("SAVING EXCEPTIONS");
		
		for (TDMMatchesTableTeamResult player : results.teams.values()) {
			if (player.exceptionsStr != null && !player.exceptionsStr.isEmpty()) {
				File file = new File(resultDir, outputFile.getName() + "." + player.team + ".errors");
				PrintWriter writer = null;
				try {
					writer = new PrintWriter(new FileOutputStream(file));
					writer.println(player.exceptionsStr);
				} catch (FileNotFoundException e) {
				} finally {
					if (writer != null) writer.close();
				}
			}
		}
	}
	
}
