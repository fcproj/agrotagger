package org.fao.oekc.autotagger.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;
import org.fao.oekc.autotagger.main.executor.DownloadConvertRunnable;

/**
 * PDF converter
 * @author celli
 *
 */
public class PDFConverter {
	
	private final static Logger log = Logger.getLogger(PDFConverter.class.getName());

	//to convert to TXT
	private PDDocument pd;
	private BufferedWriter wr;

	/**
	 * Covert a PDF to a TXT file and remove the PDF
	 * @param filePath the fullpath of the PDF file
	 * @param removePDF if true remove the source PDF after conversion
	 * @param url the URL of the resource to download
	 * @param url2doctitle map to store titles, can be null
	 * @param url2description map to store descriptions, can be null
	 * @param extractTitles true if we need to extract titles
	 */
	public void convertPdfToTxt(String filePath, boolean removePDF, String url, Map<String, String> url2doctitle, Map<String, String> url2description, boolean extractTitles){
		//the created pdf
		File input = new File(filePath);

		// The text file where you are going to store the extracted data
		filePath = filePath.replace(".pdf", ".txt");
		File output = new File(filePath); 

		try {
			log.log(Level.INFO, "	++ Loading PDF: "+filePath);
			pd = PDDocument.load(input);

			if(!pd.isEncrypted()){
				//get the title
				if(extractTitles){
					PDDocumentInformation docInfo = pd.getDocumentInformation();
					//check title availability
					String doctitle = docInfo.getTitle();
					if(doctitle!=null){
						DownloadConvertRunnable.addTitleToMap(url2doctitle, url, doctitle);
					}
					//check description availability
					String docDesc = docInfo.getKeywords();
					if(docDesc==null)
						docDesc = docInfo.getSubject();
					if(docDesc!=null){
						DownloadConvertRunnable.addDescriptionToMap(url2description, url, docDesc);
					}
				}

				//limit to 300 pages
				int number_of_pages = pd.getNumberOfPages();
				PDFTextStripper stripper = new PDFTextStripper();
				if(number_of_pages>300){
					stripper.setStartPage(10);
					stripper.setEndPage(300);
				}
				wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
				stripper.writeText(pd, wr);
			}	
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
			log.log(Level.INFO, "		++ Converted: "+filePath+" and removed PDF");
		}
	}

}
