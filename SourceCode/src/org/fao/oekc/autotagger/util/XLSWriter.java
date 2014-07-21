package org.fao.oekc.autotagger.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Creates Excel spreadsheet for the filtered journals
 * @author fabrizio celli
 *
 */
public class XLSWriter {

	/**
	 * Create an Excel file with a column with all elements of a set
	 * @param elements rows of the column
	 * @param filename the full path name of the output file
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public void writeExcelColumn(Set<String> elements, String filename) throws IOException, RowsExceededException, WriteException{
		//create the workbook object
		File f = new File(filename);
		System.out.println(f.getAbsolutePath());
		WritableWorkbook workbook = Workbook.createWorkbook(f);
		//create the first sheet (0) for the workbook
		WritableSheet sheet = workbook.createSheet("sheet", 0);
		//for cell matrix
		int row = 0;
		//scan ISSNs
		for(String s: elements){
			Label label = new Label(0, row, s);
			sheet.addCell(label);
			row++;
		}
		// All sheets and cells added. Now write out the workbook
		workbook.write();
		workbook.close(); 
	}

	/**
	 * Create an Excel file with two columns with all elements of a map
	 * @param elements data map to put in the excel file
	 * @param filename the full path name of the output file
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public void writeExcelTwoColumns(Map<String, String> elements, String filename) throws IOException, RowsExceededException, WriteException{
		//create the workbook object
		File f = new File(filename);
		System.out.println(f.getAbsolutePath());
		WritableWorkbook workbook = Workbook.createWorkbook(f);
		//create the first sheet (0) for the workbook
		WritableSheet sheet = workbook.createSheet("sheet", 0);
		//for cell matrix
		int row = 0;
		//scan ISSNs
		for(String s: elements.keySet()){
			Label label1 = new Label(0, row, s);
			Label label2 = new Label(1, row, elements.get(s));
			sheet.addCell(label1);
			sheet.addCell(label2);
			row++;
		}
		// All sheets and cells added. Now write out the workbook
		workbook.write();
		workbook.close(); 
	}


}
