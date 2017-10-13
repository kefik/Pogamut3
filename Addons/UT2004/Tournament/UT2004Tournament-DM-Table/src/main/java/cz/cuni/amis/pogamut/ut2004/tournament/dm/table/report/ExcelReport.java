package cz.cuni.amis.pogamut.ut2004.tournament.dm.table.report;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.CellFormat;
import jxl.format.Orientation;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.WritableWorkbookImpl;

public class ExcelReport {

	private File resultDir;
	private File outputFile;
	
	public ExcelReport(File resultDir, File outputFile) {
		this.resultDir = resultDir;
		this.outputFile = outputFile;
	}
	
	private FileOutputStream outputStream;
	private WritableWorkbookImpl work;

	public void info(String msg) {
		System.out.println("[INFO] " + msg);
	}
	
	public void warn(String msg) {
		System.out.println("[WARN] " + msg);
	}
	
	public void error(String msg) {
		System.out.println("[ERROR] " + msg);
	}
	
	public synchronized void generate() {		
		DMTableResults results = gatherResults(resultDir);
		
		List<DMTablePlayerResult> players = results.resolve();
		
		outputExcel(outputFile, players, results);
	}

	

	private DMTableResults gatherResults(File resultDir) {
		info("GATHERING RESULTS");
		DMTableResults results = new DMTableResults();
		results.probeResults(resultDir, true);
		return results;
	}
	
	private void outputExcel(File outputFile, List<DMTablePlayerResult> players, DMTableResults table) {
		info("OUTPUTTING RESULTS");
		
		for (DMTablePlayerResult player : players) {
			info("-- " + player.position + ". " + player.player + " (W" + player.wins + ":D" + player.draws + ":L" + player.loses + ") (F" + player.frags + ":D" + player.deaths + ")");
		}
		
		List<DMTablePlayerResult> playersAlpha = new ArrayList<DMTablePlayerResult>(players);
		Collections.sort(playersAlpha, new Comparator<DMTablePlayerResult>() {
			@Override
			public int compare(DMTablePlayerResult o1, DMTablePlayerResult o2) {
				return o1.player.compareTo(o2.player);
			}		
		});
		
		info("CREATING EXCEL FILE");
		
		try {
			this.outputStream = new FileOutputStream(outputFile);
			
			WorkbookSettings settings = new WorkbookSettings();
			
			this.work = new WritableWorkbookImpl(outputStream, false, settings);
			
			// =============
			// TABLE RESULTS
			// =============
			
			WritableSheet sheet = work.createSheet("TABLE", 0);
			
			// COLUMNS POSITION | PLAYER | WINS | DRAWS | LOSES | FRAGS | DEATHS
			
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
			sheet.addCell(center(newStringCell("F", 3, "W")));
			sheet.addCell(center(newStringCell("G", 3, "D")));
			sheet.addCell(center(newStringCell("H", 3, "L")));
			
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
			}
			
			// =====
			// TABLE
			// =====
			
			int startCol = 9;
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
						
			// TABLE
			
			for (DMTablePlayerResult player1 : playersAlpha) {
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
					
					sheet.addCell(center(newIntCell(col++, row, match.getScore(player1.player))));
					sheet.addCell(center(newStringCell(col++, row, ":")));
					sheet.addCell(center(newIntCell(col++, row, match.getScore(player2.player)))); 										
				}
				
				sheet.addCell(center(newIntCell(col++, row, player1.frags)));
				sheet.addCell(center(newStringCell(col++, row, ":")));
				sheet.addCell(center(newIntCell(col++, row, player1.deaths)));
				sheet.addCell(center(newIntCell(col++, row, player1.wins)));
				sheet.addCell(center(newIntCell(col++, row, player1.draws)));
				sheet.addCell(center(newIntCell(col++, row, player1.loses)));
			}
			
			// ======
			// POLISH
			// ======
			
			
			
			// ======
			// FINISH
			// ======
			
			work.write();
			work.close();
			
		} catch (Exception e) {			
			outputFile.delete();
			throw new RuntimeException("Failed to generate the result.", e);			
		}
	}
	
	private Label newStringCell(String column, int row, String content) {
		int col = getColumnNumber(column);		
		return newStringCell(col, row, content);
	}
	
	
	private Label newStringCell(int col, int row, String content) {
		return new Label(col, row, content);
	}
	
	private jxl.write.Number newIntCell(String column, int row, int num) throws WriteException {
		int col = getColumnNumber(column);		
		return newIntCell(col, row, num);
	}
	
	private jxl.write.Number newIntCell(int col, int row, int num) throws WriteException {
		WritableCellFormat integerFormat = new WritableCellFormat (NumberFormats.INTEGER);
		integerFormat.setShrinkToFit(true);
		return new jxl.write.Number(col, row, (double)num, integerFormat);
	}
	
	private WritableCell textVertical(WritableCell cell) throws WriteException {
		WritableCellFormat format = new WritableCellFormat(cell.getCellFormat());
		format.setOrientation(Orientation.PLUS_90);
		cell.setCellFormat(format);
		return cell;
	}
	
	private WritableCell center(WritableCell cell) throws WriteException {
		WritableCellFormat format = new WritableCellFormat(cell.getCellFormat());
		format.setAlignment(Alignment.CENTRE);
		cell.setCellFormat(format);
		return cell;
	}
	
	private int getColumnNumber(String column) {
		column = column.toUpperCase();
		if (column.length() == 1) {
			char c = column.charAt(0);
			int num = c - 'A';
			return num;
		} else {
			throw new RuntimeException("Invalid column " + column);
		}
	}
	
}
