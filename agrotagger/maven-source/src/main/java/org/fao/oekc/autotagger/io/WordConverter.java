package org.fao.oekc.autotagger.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * Convert Word Documents
 * @author celli
 *
 */
public class WordConverter {

	/**
	 * DOCX TO TXT
	 * @param filePath
	 * @param desc
	 */
	public void convertDOCXToText(String filePath, boolean removeSource){
		try{
			//create file inputstream object to read data from file 
			FileInputStream fs = new FileInputStream(filePath);
			//create document object to wrap the file inputstream object
			XWPFDocument docx = new XWPFDocument(fs); 
			//create text extractor object to extract text from the document
			XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
			
			// The text file where you are going to store the extracted data
			String outputPath = filePath.replace(".docx", ".txt");
			//create file writer object to write text to the output file
			FileWriter fw=new FileWriter(outputPath);
			//write text to the output file  
			fw.write(extractor.getText());
			//clear data from memory
			fw.flush();
			//close inputstream and file writer
			extractor.close();
			fs.close();
			fw.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally {
			//remove the file
			if(removeSource){
				File input = new File(filePath);
				input.delete();
			}
		}
	}
	
	/**
	 * DOC TO TXT
	 * @param filePath
	 * @param desc
	 */
	public void convertDOCToText(String filePath, boolean removeSource){
		try{
			//create file inputstream object to read data from file 
			FileInputStream fs = new FileInputStream(filePath);
			//create document object to wrap the file inputstream object
			HWPFDocument docx = new HWPFDocument(fs); 
			//create text extractor object to extract text from the document
			WordExtractor extractor = new WordExtractor(docx);
			
			// The text file where you are going to store the extracted data
			String outputPath = filePath.replace(".doc", ".txt");
			//create file writer object to write text to the output file
			FileWriter fw=new FileWriter(outputPath);
			//write text to the output file  
			fw.write(extractor.getText());
			//clear data from memory
			fw.flush();
			//close inputstream and file writer
			extractor.close();
			fs.close();
			fw.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally {
			//remove the file
			if(removeSource){
				File input = new File(filePath);
				input.delete();
			}
		}
	}

}
