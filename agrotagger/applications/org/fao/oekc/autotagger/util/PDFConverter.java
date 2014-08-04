package org.fao.oekc.autotagger.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 * PDF converter
 * @author celli
 *
 */
public class PDFConverter {

	//to convert to TXT
	private PDDocument pd;
	private BufferedWriter wr;

	/**
	 * Covert a PDF to a TXT file and remove the PDF
	 * @param filePath the fullpath of the PDF file
	 * @param removePDF if true remove the source PDF after conversion
	 */
	public void convertPdfToTxt(String filePath, boolean removePDF){
		//the created pdf
		File input = new File(filePath);

		// The text file where you are going to store the extracted data
		filePath = filePath.replace(".pdf", ".txt");
		File output = new File(filePath); 

		try {
			System.out.println("	++ Loading PDF: "+filePath);
			pd = PDDocument.load(input);
			int number_of_pages = pd.getNumberOfPages();
			
			//limit to 300 pages
			PDFTextStripper stripper = new PDFTextStripper();
			if(number_of_pages>300){
				stripper.setStartPage(10);
				stripper.setEndPage(300);
			}
			wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
			stripper.writeText(pd, wr);
		}
		catch(IOException e){
			//System.out.println(e.getMessage());
		}
		catch(Exception e){
			//System.out.println(e.getMessage());
		}
		finally {
			//remove the PDF
			if(removePDF)
				input.delete();
			
			//I use close() to flush the stream.
			try {
				wr.close();
			} catch (IOException e) {
				//System.out.println(e.getMessage());
			}
			
			//close the PDDocument stream
			if (pd != null) {
				try {
					pd.close();
				} catch (IOException e) {
					//System.out.println(e.getMessage());
				}
			} 
			System.out.println("		++ Converted: "+filePath+" and removed PDF");
		}
	}

}
