package org.fao.oekc.autotagger.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class TestReadPDF {

	public static void main(String[] args){
		PDDocument pd;
		BufferedWriter wr;
		try {
			// The PDF file from where you would like to extract
			File input = new File("data/test/k8734e.pdf");  
			// The text file where you are going to store the extracted data
			File output = new File("data/test/Document1.txt"); 
			
			pd = PDDocument.load(input);
			System.out.println(pd.getNumberOfPages());
			System.out.println(pd.isEncrypted());
			//pd.save("CopyOfInvoice.pdf"); // Creates a copy called "CopyOfInvoice.pdf"
			
			PDFTextStripper stripper = new PDFTextStripper();
			wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
			stripper.writeText(pd, wr);
			if (pd != null) {
				pd.close();
			}
			// I use close() to flush the stream.
			wr.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
