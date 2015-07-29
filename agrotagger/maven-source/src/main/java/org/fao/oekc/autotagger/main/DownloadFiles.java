package org.fao.oekc.autotagger.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fao.oekc.autotagger.io.DirectoryManager;
import org.fao.oekc.autotagger.io.TXTWriter;
import org.fao.oekc.autotagger.io.TxtNewLineReader;
import org.fao.oekc.autotagger.main.executor.DownloadConvertRunnable;
import org.fao.oekc.autotagger.support.Path;

/**
 * Starting from a text file containing the list of files to be downloaded (NewLine separator), downloads these files in a specific 
 * directory and converts the file to TXT (supported files: PDF, TXT, HTML).
 * The application generates a mapping file DOC_URL=DOC_NAME, located in the same directory of the input text file (param. 1), 
 * with the same name and a suffix "_mapping".
 * 
 * @author fabrizio celli
 *
 */
public class DownloadFiles {

	private final static Logger log = Logger.getLogger(DownloadFiles.class.getName());

	//path of the TXT file containing the list of documents to be downloaded
	private String sourceFilePath;
	//output directory
	private String outputDirectory;
	//download mode
	private String mode;
	//flag to determin if we need to extract titles
	private boolean extractTitles;

	/**
	 * @param sourceFilePath the file path of the source text file containing new line separator URLs
	 * @param outputDirectory the output directory without the ending slash
	 * @param mode a mode equals to "listURL" means that the source file contains a list of URLs, one per line; a mode equals to "nutchOutput" means that the source file is the output file of a Nutch Crawler
	 * @param extractTitles true if the application needs to extract also titles from metadata of downloaded documents
	 */
	public DownloadFiles(String sourceFilePath, String outputDirectory, String mode, boolean extractTitles){
		this.sourceFilePath = sourceFilePath;
		this.outputDirectory = outputDirectory;
		this.mode = mode;
		this.extractTitles = extractTitles;
	}

	/**
	 * Starting from a text file containing the list of files to be downloaded (NewLine separator), downloads these files in a specific directory
	 * This application requires three command line parameters:
	 * - input text file location (e,g, data/sources/crawler_result.txt)
	 * - the output directory without the ending slash
	 * - download mode; a mode equals to "listURL" means that the source file contains a list of URLs, one per line; a mode equals to "nutchOutput" means that the source file is the output file of a Nutch Crawler
	 * The default output directory is the internal directory data/documents
	 * The application generates a mapping file DOC_URL=DOC_NAME, located in the same directory of the input text file (param. 1), with the same name and a suffix "_mapping".
	 * 
	 * @param args should contain 2 parameters, the input text file location and the download mode
	 */
	public static void main(String[] args) {
		if(args.length<3){
			log.log(Level.WARNING, "You have to specify the source text files, " +
					"the output directory, and the mode {listURL; nutchOutput}. " +
			"Optionally, a last boolean flag {true; false} can be used to declare if you want to extract titles too (default is true)");	
		}
		else {
			//data/sources/input.txt
			String inputTxtPath = args[0];
			//listurl
			String mode = args[2];

			//duplicates removal
			boolean extractTitles = true;
			if(args.length>=4){
				String boleanFlag = args[3];
				if(boleanFlag.equalsIgnoreCase("false"))
					extractTitles = false;
			}

			//data/documents
			String outputPath = args[1];
			if(!(new DirectoryManager()).checkCreateDir(outputPath)
					|| !(new DirectoryManager()).checkCleanCreateDir(outputPath+ Path.documentsAndKeys))
				log.log(Level.WARNING, "ERROR: You don't have permissions to write the directory: " + outputPath);
			else {
				DownloadFiles downloader = new DownloadFiles(inputTxtPath, outputPath+ Path.documentsAndKeys, mode, extractTitles);
				downloader.startDownload();
			}
		}
	}

	/**
	 * Download all the files, generating a filename with a counter and adding a suffix if the file is a PDF
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public void startDownload(){

		//parse the list of URLs to be downloades
		TxtNewLineReader reader = new TxtNewLineReader();
		Set<String> urls = new HashSet<String>();
		try {
			if(mode.equalsIgnoreCase("listurl"))
				urls = reader.parseTxt(sourceFilePath);
			else
				if(mode.equalsIgnoreCase("nutchoutput"))
					urls = reader.parseTxtNutch(sourceFilePath);
			//LOG
			log.log(Level.INFO, "-- found "+urls.size()+" URLs");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		// process URLs
		Map<String, String> url2filename = new HashMap<String, String>();
		Map<String, String> url2doctitle = null;
		Map<String, String> url2description = null;
		if(this.extractTitles){
			url2doctitle = new HashMap<String, String>();
			url2description = new HashMap<String, String>();
		}
		this.parallelProcessingURLs(urls, url2filename, url2doctitle, url2description);

		//write mapping on the disk
		try {
			TXTWriter.writeTXTString2String(url2filename, sourceFilePath+Path.mappingSuffix);
			if(this.extractTitles){
				TXTWriter.writeTXTString2String(url2doctitle, sourceFilePath+Path.titlesSuffix);
				TXTWriter.writeTXTString2String(url2description, sourceFilePath+Path.descriptionSuffix);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Parallel processing of URLs, to allow multiple downloads
	 */
	private Map<String, String> parallelProcessingURLs(Set<String> urls, Map<String, String> url2filename, Map<String, String> url2doctitle, Map<String, String> url2description){
		//counter for file downloaded prefixes
		int counter = 0;

		//threads pool for parsing source XMLs
		ExecutorService executor = Executors.newFixedThreadPool(8);

		//for each URL, download the file
		for(String url: urls){
			try {
				//generate filename
				String fileName = this.generateFilenameAndMapping2URL(counter, url, url2filename);
				counter++;
				//DO THE JOB: download the file and convert to TXT, extracting the document title, if available in the metadata
				Callable<String> worker = new DownloadConvertRunnable(fileName, url, this.outputDirectory, url2doctitle, url2description, this.extractTitles);
				executor.submit(worker);
			} catch (Exception e) {
				log.log(Level.WARNING, "ERROR URL: "+url +e.getMessage());
			}
		}

		// This will make the executor accept no new threads
		// and finish all existing threads in the queue
		executor.shutdown();
		// Wait until all threads are finish
		while (!executor.isTerminated()) {}

		return url2filename;
	}

	/*
	 * Generate filename with extension and mapping url to filename
	 */
	private String generateFilenameAndMapping2URL(int counter, String url, Map<String, String> url2filename){
		String fileName = "document"+counter;
		String fileNamePrefix = fileName;
		if(url.toLowerCase().endsWith("pdf"))
			fileName += ".pdf";
		else
			if(url.toLowerCase().endsWith("docx"))
				fileName += ".docx";
			else 
				if(url.toLowerCase().endsWith("doc"))
					fileName += ".doc";
				else
					//default: txt
					fileName += ".txt";
		url2filename.put(url, fileNamePrefix);
		return fileName;
	}

}
