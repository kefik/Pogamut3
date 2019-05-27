package cz.cuni.amis.pogamut.ut2004.tournament.tdm.table.report.one;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.cuni.amis.pogamut.ut2004.tournament.utils.ExcelReport;
import cz.cuni.amis.utils.FileAppender;
import jxl.write.WritableSheet;
import jxl.write.biff.WritableWorkbookImpl;

public class TDMOneMatchExcelReport extends ExcelReport {

	private File resultDir;
	private File outputFile;
	private String mapName;
	private int mapNumber;
	
	public TDMOneMatchExcelReport(File resultDir, File outputFile, String mapName, int mapNumber) {
		this.resultDir = resultDir;
		this.outputFile = outputFile;
		this.mapName = mapName;
		this.mapNumber = mapNumber;
	}
	
	private TDMOneMatchTableResults table;
	private List<TDMOneMatchTableTeamResult> teams;

	public synchronized TDMOneMatchTableResults generate() {		
		this.table = gatherResults(resultDir);
		
		this.teams = table.resolve();
		
		produceExcel(outputFile);
		
		return table;
	}

	private TDMOneMatchTableResults gatherResults(File resultDir) {
		info("GATHERING RESULTS");
		TDMOneMatchTableResults results = new TDMOneMatchTableResults(mapName, mapNumber);
		results.probeResults(resultDir, true);
		return results;
	}
	
	@Override
	protected void produceExcel(File outputFile, WritableWorkbookImpl workbook, WritableSheet sheet) throws Exception {		
		info("OUTPUTING RESULTS");
		
		for (TDMOneMatchTableTeamResult team : teams) {
			info("-- " + team.position + ". " + team.team + " (W" + team.wins + ":D" + team.draws + ":L" + team.loses + ":E" + team.exceptions + ") (F" + team.frags + ":D" + team.deaths + ")");
		}
		
		List<TDMOneMatchTableTeamResult> teamsAlpha = new ArrayList<TDMOneMatchTableTeamResult>(teams);
		Collections.sort(teamsAlpha, new Comparator<TDMOneMatchTableTeamResult>() {
			@Override
			public int compare(TDMOneMatchTableTeamResult o1, TDMOneMatchTableTeamResult o2) {
				return o1.team.compareTo(o2.team);
			}		
		});		
		
		info("CREATING EXCEL FILE");
		
		// COLUMNS POSITION | PLAYER | WINS | DRAWS | LOSES | EXCEPTIONS | FRAGS | DEATHS
		
		// ROWS:
		// TABLE NAME
		// Tournament type
		// Empty
		// HEADERS
		// players
		
		sheet.addCell(newStringCell("A", 0, "TeamDeathMatch Tournament"));
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
		
		for (TDMOneMatchTableTeamResult team : teams) {
			++row;
			
			sheet.addCell(center(newIntCell   ("A", row, team.position)));
			sheet.addCell(newStringCell("B", row, team.team));
			sheet.addCell(center(newIntCell   ("C", row, team.frags)));
			sheet.addCell(center(newStringCell("D", row, ":")));
			sheet.addCell(center(newIntCell   ("E", row, team.deaths)));
			sheet.addCell(center(newIntCell   ("F", row, team.wins)));
			sheet.addCell(center(newIntCell   ("G", row, team.draws)));
			sheet.addCell(center(newIntCell   ("H", row, team.loses)));
			sheet.addCell(center(newIntCell   ("I", row, team.exceptions)));
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
		
		for (TDMOneMatchTableTeamResult player : teamsAlpha) {
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
		
		for (TDMOneMatchTableTeamResult player1 : teamsAlpha) {
			File exceptions = new File(outputFile.getAbsolutePath() + "." + player1.team + ".errors");
			
			col = startCol+1;
			++row;
			
			sheet.addCell(newStringCell(col++, row, player1.team));
			
			for (TDMOneMatchTableTeamResult player2 : teamsAlpha) {
				if (player1 == player2) {
					sheet.addCell(newStringCell(col++, row, ""));
					sheet.addCell(center(newStringCell(col++, row, "X")));
					sheet.addCell(newStringCell(col++, row, ""));
					continue;						
				}
				
				TDMOneMatchResult match = table.getMatchResult(player1.team, player2.team);
				
				if (match == null) {
					sheet.addCell(center(newStringCell(col++, row, "x")));
					sheet.addCell(center(newStringCell(col++, row, ":")));
					sheet.addCell(center(newStringCell(col++, row, "x")));
					continue;
				}
				
				if (match.isException(player1.team)) {
					sheet.addCell(center(newStringCell(col++, row, "E")));
					FileAppender.appendToFile(exceptions, match.getExceptionsTrace(player1.team));
				} else {					
					sheet.addCell(center(newIntCell(col++, row, match.getScore(player1.team))));
				}
				sheet.addCell(center(newStringCell(col++, row, ":")));
				if (match.isException(player2.team)) {
					sheet.addCell(center(newStringCell(col++, row, "E")));
				} else {					
					sheet.addCell(center(newIntCell(col++, row, match.getScore(player2.team))));
				}
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
	
	public static void main(String[] args) {
		File resultDir = new File("d:\\Workspaces\\MFF\\NAIL068-UmeleBytosti\\Lectures\\AB2018-Labs\\Lab-06-TDM\\Students\\_Results\\1-DM-Rankin-FE\\");
		
		File outputFile = new File(resultDir, "Result.xls");
		
		new TDMOneMatchExcelReport(resultDir, outputFile, "DM-Rankin-FE", 1).generate();
		
		System.out.println("---// DONE //---");
	}
	
}
