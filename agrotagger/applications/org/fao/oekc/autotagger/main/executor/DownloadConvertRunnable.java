package org.fao.oekc.autotagger.main.executor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.fao.oekc.autotagger.util.HTMLConverter;
import org.fao.oekc.autotagger.util.PDFConverter;

/**
 * This Runnable is responsible to download a resurce from a given URL, converting it in a text file
 * @author celli
 *
 */
public class DownloadConvertRunnable implements Runnable {
	
	//the filename of the downloaded file
	private String fileName;
	//the URL of the resource to download
	private String url;
	//output directory
	private String outputDirectory;
	
	/**
	 * Constructor: creates the runnable for a single URL
	 * @param fileName the filename of the text downloaded file
	 * @param url the URL of the resource to download
	 * @param outputDirectory the output directory where the file will be stored
	 */
	public DownloadConvertRunnable(String fileName, String url, String outputDirectory){
		this.fileName = fileName;
		this.url = url;
		this.outputDirectory = outputDirectory;
	}
	
	/**
	 * Download the file and convert to TXT
	 */
	public void run() {
		try {
			System.out.println("+ Start downloading: "+url);
			//create connection, setting a timeout to 5s
			URLConnection urlConn = new URL(url).openConnection();
			urlConn.setReadTimeout(5*1000);

			BufferedInputStream in = new BufferedInputStream(urlConn.getInputStream()); 
			String outputFile = outputDirectory+"/"+fileName;

			//read the input stream to download the file
			this.readInputStreamAndDownload(outputFile, in);

			//transform to TXT if PDF or HTML
			if(fileName.contains(".pdf"))
				(new PDFConverter()).convertPdfToTxt(outputFile, true);
			else 
				//remove HTML tags
				(new HTMLConverter()).removeHTMLTags(outputFile, true);
		}
		catch(IOException e){
			//System.out.println(e.getMessage());
		}
		catch(Exception e){
			//System.out.println(e.getMessage());
		}
	}
	
	/*
	 * Read the input stream and download the file
	 */
	private void readInputStreamAndDownload(String outputFile, BufferedInputStream in) {
		try {
			FileOutputStream fos = new FileOutputStream(outputFile);
			BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
			byte[] data = new byte[1024];
			int x=0;
			while((x=in.read(data,0,1024))>=0){
				bout.write(data,0,x);
			}
			bout.close();
		} catch (Exception e){
			//System.out.println(e.getMessage());
		} finally {	
			try {
				in.close();
			} catch (IOException e) {
				//System.out.println(e.getMessage());
			}
		}
		System.out.println("  ++ Downloaded: "+outputFile);
	}

}
