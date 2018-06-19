package cz.cuni.amis.pogamut.ut2004.tournament.dm.table.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.cuni.amis.pogamut.ut2004.tournament.utils.ExcelReport;
import cz.cuni.amis.utils.FileAppender;
import jxl.write.WritableSheet;
import jxl.write.biff.WritableWorkbookImpl;

public class DMExcelReport extends ExcelReport {

	private File resultDir;
	private File outputFile;
	
	public DMExcelReport(File resultDir, File outputFile) {
		this.resultDir = resultDir;
		this.outputFile = outputFile;
	}
	
	private DMTableResults table;
	private List<DMTablePlayerResult> players;
	
	public synchronized void generate() {		
		this.table = gatherResults(resultDir);
		
		this.players = table.resolve();
		
		produceExcel(outputFile);
	}

	private DMTableResults gatherResults(File resultDir) {
		info("GATHERING RESULTS");
		DMTableResults results = new DMTableResults();
		results.probeResults(resultDir, true);
		return results;
	}
	
	@Override
	protected void produceExcel(File outputFile, WritableWorkbookImpl workbook, WritableSheet sheet) throws Exception {
		info("OUTPUTING RESULTS");
		
		for (DMTablePlayerResult player : players) {
			info("-- " + player.position + ". " + player.player + " (W" + player.wins + ":D" + player.draws + ":L" + player.loses + ":E" + player.exceptions + ") (F" + player.frags + ":D" + player.deaths + ")");
		}
		
		List<DMTablePlayerResult> playersAlpha = new ArrayList<DMTablePlayerResult>(players);
		Collections.sort(playersAlpha, new Comparator<DMTablePlayerResult>() {
			@Override
			public int compare(DMTablePlayerResult o1, DMTablePlayerResult o2) {
				return o1.player.compareTo(o2.player);
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
		
		for (DMTablePlayerResult player : players) {
			++row;
			
			sheet.addCell(center(newIntCell   ("A", row, player.position)));
			sheet.addCell(newStringCell("B", row, player.player));
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
		
		for (DMTablePlayerResult player : playersAlpha) {
			sheet.addCell(newStringCell(col++, row, ""));
			sheet.addCell(center(textVertical(newStringCell(col++, row, player.player))));
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
		
		for (DMTablePlayerResult player1 : playersAlpha) {
			File exceptions = new File(outputFile.getAbsolutePath() + "." + player1.player + ".errors");
			
			col = startCol+1;
			++row;
			
			sheet.addCell(newStringCell(col++, row, player1.player));
			
			for (DMTablePlayerResult player2 : playersAlpha) {
				if (player1 == player2) {
					sheet.addCell(newStringCell(col++, row, ""));
					sheet.addCell(center(newStringCell(col++, row, "X")));
					sheet.addCell(newStringCell(col++, row, ""));
					continue;						
				}
				
				DMMatchResult match = table.getMatchResult(player1.player, player2.player);
				
				if (match == null) {
					sheet.addCell(center(newStringCell(col++, row, "x")));
					sheet.addCell(center(newStringCell(col++, row, ":")));
					sheet.addCell(center(newStringCell(col++, row, "x")));
					continue;
				}
				
				if (match.isException(player1.player)) {
					sheet.addCell(center(newStringCell(col++, row, "E")));
					FileAppender.appendToFile(exceptions, match.getException(player1.player));
				} else {					
					sheet.addCell(center(newIntCell(col++, row, match.getScore(player1.player))));
				}
				sheet.addCell(center(newStringCell(col++, row, ":")));
				if (match.isException(player2.player)) {
					sheet.addCell(center(newStringCell(col++, row, "E")));
				} else {					
					sheet.addCell(center(newIntCell(col++, row, match.getScore(player2.player))));
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
	
}
