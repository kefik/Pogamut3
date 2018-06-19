package cz.cuni.amis.pogamut.ut2004.tournament.utils;

import java.io.File;
import java.io.FileOutputStream;

import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Orientation;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.WritableWorkbookImpl;

public abstract class ExcelReport {

	public ExcelReport() {
	}
	
	public void info(String msg) {
		System.out.println("[INFO] " + msg);
	}
	
	public void warn(String msg) {
		System.out.println("[WARN] " + msg);
	}
	
	public void error(String msg) {
		System.out.println("[ERROR] " + msg);
	}

	protected void produceExcel(File outputFile) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(outputFile);
			
			WorkbookSettings settings = new WorkbookSettings();
			
			WritableWorkbookImpl workbook = new WritableWorkbookImpl(outputStream, false, settings);
			
			// =============
			// TABLE RESULTS
			// =============
			
			WritableSheet sheet = workbook.createSheet("TABLE", 0);
			
			produceExcel(outputFile, workbook, sheet);
			
			// ======
			// FINISH
			// ======
			
			workbook.write();
			workbook.close();
			
		} catch (Exception e) {
			try {
				if (outputStream != null) outputStream.close();
			} catch (Exception e1) {				
			}
			outputFile.delete();
			throw new RuntimeException("Failed to generate the result.", e);			
		}
			
	}
	

	protected abstract void produceExcel(File outputFile2, WritableWorkbookImpl workbook, WritableSheet sheet) throws Exception;

	protected Label newStringCell(String column, int row, String content) {
		int col = getColumnNumber(column);		
		return newStringCell(col, row, content);
	}
	
	
	protected Label newStringCell(int col, int row, String content) {
		return new Label(col, row, content);
	}
	
	protected jxl.write.Number newIntCell(String column, int row, int num) throws WriteException {
		int col = getColumnNumber(column);		
		return newIntCell(col, row, num);
	}
	
	protected jxl.write.Number newIntCell(int col, int row, int num) throws WriteException {
		WritableCellFormat integerFormat = new WritableCellFormat (NumberFormats.INTEGER);
		integerFormat.setShrinkToFit(true);
		return new jxl.write.Number(col, row, (double)num, integerFormat);
	}
	
	protected WritableCell textVertical(WritableCell cell) throws WriteException {
		WritableCellFormat format = new WritableCellFormat(cell.getCellFormat());
		format.setOrientation(Orientation.PLUS_90);
		cell.setCellFormat(format);
		return cell;
	}
	
	protected WritableCell center(WritableCell cell) throws WriteException {
		WritableCellFormat format = new WritableCellFormat(cell.getCellFormat());
		format.setAlignment(Alignment.CENTRE);
		cell.setCellFormat(format);
		return cell;
	}
	
	protected int getColumnNumber(String column) {
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
