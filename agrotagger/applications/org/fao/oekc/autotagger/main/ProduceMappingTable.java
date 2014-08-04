package org.fao.oekc.autotagger.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fao.oekc.autotagger.main.support.Path;
import org.fao.oekc.autotagger.util.AgrovocMappingParser;
import org.fao.oekc.autotagger.util.DirectoryManager;
import org.fao.oekc.autotagger.util.DirectoryZipper;
import org.fao.oekc.autotagger.util.TXTWriter;
import org.fao.oekc.autotagger.util.TxtNewLineReader;

/**
 * Create text file of couples, with the symbol "=" as a separator: the key is the URL of the document, the value is a list of keywords with comma-space ", " separated values. 
 * This application requires a command line parameter, that is the same first parameter of DownloadFiles.
 * This application needs to be executed after DownloadFiles (to have the mapping file DOC_URL=DOC_NAME) and MauiAutoTaggerKey (to have the .key files in data/documents)
 * 
 * @author fabrizio celli
 */
public class ProduceMappingTable {

	//limit for output files
	private final static int _maximunNumberTriplesPerFile = 100000;
	
	//suffix for the output file, depending on the used algorithm
	private final static String _outputSuffix = ".tar.gz";
	
	//path of the TXT file containing the mapping uri->document name
	private String mappingFilePath;
	//directory with .key files
	private String keyFilePath;
	//output directory
	private String outputDir;
	//the output format
	private String outputMode;
	// the outputFileName, can be NULL
	private String outputFileName;
	
	//temporary directory
	private String tempDir;

	/**
	 * Constructor
	 * @param mappingFilePath full name of the mapping file
	 * @param keyFilePath path of the .key files, without trailing slash
	 * @param outputMode the format of the output: txt or rdfnt
	 * @param outputFileName the filename of the output file, the paramenter can be NULL
	 */
	public ProduceMappingTable(String mappingFilePath, String keyFilePath, String outputMode, String outputFileName){
		this.mappingFilePath = mappingFilePath;
		this.outputDir = keyFilePath;
		this.keyFilePath = keyFilePath+Path.documentsAndKeys;
		this.outputMode = outputMode;
		this.outputFileName = outputFileName;
		
		//check and creates temporary dir
		this.tempDir = this.outputDir + Path.tmpRDF;
		(new DirectoryManager()).checkCreateDir(this.tempDir);
	}

	/**
	 * Read the mapping file URI->DOC and, for each DOC, read the DOC.key file.
	 * Then, writes a text file of couples, with the symbol "=" as a separator: the key is the URL of the document, the value 
	 * is a list of keywords with comma-space ", " separated values
	 */
	public void generateOutputFile(){

		//read the mapping file, to have the URLs of each document
		//the mapping file contains a list of DOC_URL=DOC_FILE_NAME
		Map<String, String> docNames2URL = new HashMap<String, String>();
		this.parseMappingFile(docNames2URL);

		//delete mapping file path
		(new File(mappingFilePath)).delete();

		//parse documents keys
		if(docNames2URL!=null){
			if(this.outputMode.equalsIgnoreCase("rdfnt"))
				//write triples
				this.writeRDF(docNames2URL);
			/*else
				//default: write text file
				this.writeTXT(docNames2URL);
			 */
		}

	}

	/*
	 * Write a text file with the output
	 * For each DOCUMENT, read the DOCUMENT.key file.
	 * Then, writes a text file of couples, with the symbol "=" as a separator: the key is the URL of the document, the value is a list of keywords with comma-space ", " separated values
	 */
	/*
	 private void writeTXT(Map<String, String> docNames2URL){
		TxtNewLineReader reader = new TxtNewLineReader();
		//result
		Map<String, String> docURL2AgString = new HashMap<String, String>();
		//parse documents
		for(String doc: docNames2URL.keySet()){
			String keyFileName = keyFilePath+"/"+doc+".key";
			System.out.println(keyFileName);
			try {
				//set keywords for this file
				Set<String> keywords = reader.parseTxt(keyFileName);
				//genetare keyword string, comma separated
				String keyString = "";
				for(String key: keywords)
					keyString += key+", ";
				if(keyString.endsWith(", "))
					keyString = keyString.substring(0, keyString.lastIndexOf(", "));
				//if there are some keywords
				if(keyString.length()>0)
					docURL2AgString.put(docNames2URL.get(doc), keyString);
			} catch (Exception e) {
				//no keywords, don't worry!
			}
		}

		//write mapping on the disk
		String outputPathName = mappingFilePath+".txt";
		try {
			TXTWriter.writeTXTString2String(docURL2AgString, outputPathName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	 */

	/*
	 * Write a NT file with the output triples
	 * The format is NTRIPLES, the predicate is dcterms:subject, the subject is the document UTL, the object is an AGROVOC URI 
	 */
	private void writeRDF(Map<String, String> docNames2URL){
		TxtNewLineReader reader = new TxtNewLineReader();
		//result
		Set<String> triples = new HashSet<String>();
		//iterator over output RDF files
		int iterator = 0;

		//load agrovoc
		try {
			Map<String, String> agrovoc2uri = AgrovocMappingParser.mappingAgrovocUri("data/vocabularies/agrovocURILabelMappings.txt", "en");
			
			//parse documents
			for(String doc: docNames2URL.keySet()){
				String keyFileName = keyFilePath+"/"+doc+".key";
				try {
					//set keywords for this file
					Set<String> keywords = reader.parseTxt(keyFileName);
					//generate triples
					for(String k: keywords){
						String uri = agrovoc2uri.get(k.toLowerCase());
						if(uri!=null) {
							String docURL = docNames2URL.get(doc);
							if(!docURL.contains("<") && !docURL.contains(">") && !docURL.contains("\""))
								triples.add("<"+docURL+"> <http://purl.org/dc/terms/subject> <"+uri+"> .");
						}
					}

					//check dimension of the set
					if(triples.size() >= ProduceMappingTable._maximunNumberTriplesPerFile){
						this.writeTriples(triples, iterator);
						iterator++;
					}
				} catch (Exception e) {
					//no keywords, don't worry!
				}
			}
		} catch (Exception e1) {
			//e1.printStackTrace();
		}	

		//write last triples
		this.writeTriples(triples, iterator);

		//clean the output directory
		(new DirectoryManager()).deleteDirectory(new File(this.keyFilePath));

		try {
			//zip the tmp directory, move the content to the output directory
			File tempDir = new File(this.tempDir);
			//check output filename
			if(this.outputFileName==null){
				String date = new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
				this.outputFileName = date+"_"+"RDF"+_outputSuffix;
			}
			//(new DirectoryZipper()).zipDir(tempDir, new File(this.outputDir+File.separator+this.outputFileName));
			(new DirectoryZipper()).tarGzDir(tempDir, new File(this.outputDir+File.separator+this.outputFileName));
			(new DirectoryManager()).deleteDirectory(tempDir);
		} catch (Exception e1) {
			//e1.printStackTrace();
		}

	}

	private void writeTriples(Set<String> triples, int iterator) {
		String date = new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
		String outputPathName = this.tempDir+File.separator+date+"_"+iterator+".nt";
		try {
			TXTWriter.writeTXTLines(triples, outputPathName);
		} catch (IOException e) {
			// error...
		}
		triples.clear(); 
	}

	/*
	 * Read the mapping file produced by MauiAutoTaggerKey, to have the URLs of each document
	 * The mapping file contains a list of DOC_URL=DOC_FILE_NAME
	 */
	private void parseMappingFile(Map<String, String> docNames2URIs){
		TxtNewLineReader reader = new TxtNewLineReader();
		try {
			docNames2URIs.putAll(reader.reverseParseEqualTxt(mappingFilePath));
		} catch (FileNotFoundException e) {
			docNames2URIs.clear();
			System.out.println("	!!! The system cannot find the file specified "+mappingFilePath);
		}
	}

	/**
	 * Create text file of couples, with the symbol "=" as a separator: the key is the URL of the document, the value 
	 * is a list of keywords with comma-space ", " separated values. 
	 * This application requires a command line parameter, that is the same first parameter of DownloadFiles class. Thus,
	 * the command line parameter should be the fullpath of the text file containing the list of URLs of files.
	 * The application needs to find in the internal directory data/documents the .key files produced by MAUI
	 * @param args
	 */
	public static void main(String[] args){

		if(args.length<3){
			System.out.println("You have to specify: " +
					"1) the fullpath of the source text file used to download files {the one containing the URLs list}, " +
					"2) the directory with .key MAUI files, " +
					"and 3) the output mode {txt, rdfnt}." +
					"Optionally, you can specify 4) the filename of the output file");
		}
		else {
			//don't check the directories, since they were checked in the previous applications of the workflow
			String mappingFilePath = args[0]+Path.mappingSuffix;
			String outputMode = args[2];
			String keyFilePath = args[1];
			
			ProduceMappingTable producer;
			if(args.length<4)
				producer = new ProduceMappingTable(mappingFilePath, keyFilePath, outputMode, null);
			else {
				String fileName = args[3];
				//check the extension of the file, ZIP or TAR.GZ depending on the current algorithm
				if(!fileName.endsWith(_outputSuffix))
					fileName = fileName + _outputSuffix;
				producer = new ProduceMappingTable(mappingFilePath, keyFilePath, outputMode, fileName);
			}
			producer.generateOutputFile();
		}
	}


}
