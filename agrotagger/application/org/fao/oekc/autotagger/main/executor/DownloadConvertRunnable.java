package org.fao.oekc.autotagger.main.executor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fao.oekc.autotagger.io.HTMLConverter;
import org.fao.oekc.autotagger.io.PDFConverter;

/**
 * This Runnable is responsible to download a resurce from a given URL, converting it in a text file
 * @author celli
 *
 */
public class DownloadConvertRunnable implements Callable<String> {
	
	private final static Logger log = Logger.getLogger(DownloadConvertRunnable.class.getName());
	
	//the filename of the downloaded file
	private String fileName;
	//the URL of the resource to download
	private String url;
	//output directory
	private String outputDirectory;
	//mapping URL-> document title
	private Map<String, String> url2doctitle;
	//mapping URL-> document description
	private Map<String, String> url2description;
	//flag to determin if we need to extract titles
	private boolean extractTitles;
	
	/**
	 * Constructor: creates the runnable for a single URL
	 * @param fileName the filename of the text downloaded file
	 * @param url the URL of the resource to download
	 * @param outputDirectory the output directory where the file will be stored
	 * @param url2doctitle map to store titles, can be null
	 * @param url2description map to store descriptions, can be null
	 * @param extractTitles true if we need to extract titles
	 */
	public DownloadConvertRunnable(String fileName, String url, String outputDirectory, Map<String, String> url2doctitle, Map<String, String> url2description, boolean extractTitles){
		this.fileName = fileName;
		this.url = url;
		this.outputDirectory = outputDirectory;
		this.url2doctitle = url2doctitle;
		this.url2description = url2description;
		this.extractTitles = extractTitles;
	}
	
	/**
	 * Download the file and convert to TXT
	 */
	public String call() throws Exception {
		try {
			log.log(Level.INFO, "+ Start downloading: "+url);
			//create connection, setting a timeout to 5s
			URLConnection urlConn = new URL(url).openConnection();
			urlConn.setReadTimeout(5*1000);

			BufferedInputStream in = new BufferedInputStream(urlConn.getInputStream()); 
			String outputFile = outputDirectory+"/"+fileName;

			//read the input stream to download the file
			this.readInputStreamAndDownload(outputFile, in);

			//transform to TXT if PDF or HTML
			if(fileName.contains(".pdf"))
				(new PDFConverter()).convertPdfToTxt(outputFile, true, this.url, this.url2doctitle, this.url2description, this.extractTitles);
			else 
				//remove HTML tags
				(new HTMLConverter()).removeHTMLTags(outputFile, true, this.url, this.url2doctitle, this.url2description, this.extractTitles);
		}
		catch(IOException e){
			//System.out.println(e.getMessage());
		}
		catch(Exception e){
			//System.out.println(e.getMessage());
		}
		return Thread.currentThread().getName();
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
		log.log(Level.INFO, "  ++ Downloaded: "+outputFile);
	}
	
	public static synchronized void addTitleToMap(Map<String, String> url2doctitle, String url, String title){
		url2doctitle.put(url, title);
	}
	
	public static synchronized void addDescriptionToMap(Map<String, String> url2description, String url, String desc){
		url2description.put(url, desc);
	}

}
