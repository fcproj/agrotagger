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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fao.oekc.autotagger.io.AgrovocMappingParser;
import org.fao.oekc.autotagger.io.DirectoryManager;
import org.fao.oekc.autotagger.io.DirectoryZipper;
import org.fao.oekc.autotagger.io.TXTWriter;
import org.fao.oekc.autotagger.io.TxtNewLineReader;
import org.fao.oekc.autotagger.support.Path;

/**
 * Create text file of couples, with the symbol "=" as a separator: the key is the URL of the document, the value is a list of keywords with comma-space ", " separated values. 
 * This application requires a command line parameter, that is the same first parameter of DownloadFiles.
 * This application needs to be executed after DownloadFiles (to have the mapping file DOC_URL=DOC_NAME) and MauiAutoTaggerKey (to have the .key files in data/documents)
 * 
 * @author fabrizio celli
 */
public class ProduceMappingTable {

	private final static Logger log = Logger.getLogger(ProduceMappingTable.class.getName());

	//limit for output files
	private final static int _maximunNumberTriplesPerFile = 100000;

	//suffix for the output file, depending on the used algorithm
	private final static String _outputSuffix = ".tar.gz";

	//path of the source TXT: the  file containing the mapping uri->document name is sourceFilePath_mapping
	private String sourceFilePath;
	//directory with .key files
	private String keyFilePath;
	//output directory
	private String outputDir;
	//the output format
	private String outputMode;
	// the outputFileName, can be NULL
	private String outputFileName;
	//flag to determin if we need to extract titles
	private boolean extractTitles;

	//temporary directory
	private String tempDir;

	/**
	 * Constructor
	 * @param sourceFilePath full name of the source file (adding _mapping you can get the mapping filepath)
	 * @param keyFilePath path of the .key files, without trailing slash
	 * @param outputMode the format of the output: txt or rdfnt
	 * @param outputFileName the filename of the output file, the paramenter can be NULL
	 * @param extractTitles true if the application needs to extract also titles from metadata of downloaded documents
	 */
	public ProduceMappingTable(String sourceFilePath, String keyFilePath, String outputMode, String outputFileName, boolean extractTitles){
		this.sourceFilePath = sourceFilePath;
		this.outputDir = keyFilePath;
		this.keyFilePath = keyFilePath+Path.documentsAndKeys;
		this.outputMode = outputMode;
		this.outputFileName = outputFileName;
		this.extractTitles = extractTitles;

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
		File f = new File(this.sourceFilePath+Path.mappingSuffix);
		if(f.exists()){
			this.parseReverseMappingFile(docNames2URL, this.sourceFilePath+Path.mappingSuffix);
			//delete mapping file path
			f.delete();
		}

		//if the flag is true, read the TITLES and DESCRIPTIONS mapping files
		//the mapping file contains a list of DOC_URL=TITLE
		//the mapping file contains a list of DOC_URL=DESCRIPTION
		Map<String, String> url2titles = null;
		Map<String, String> url2descriptions = null;
		if(this.extractTitles){
			//TITLES
			f = new File(this.sourceFilePath+Path.titlesSuffix);
			if(f.exists()){
				url2titles = new HashMap<String, String>();
				this.parseMappingFile(url2titles, this.sourceFilePath+Path.titlesSuffix);
				//delete mapping file path
				f.delete();
			}
			//DESCRIPTIONS
			f = new File(this.sourceFilePath+Path.descriptionSuffix);
			if(f.exists()){
				url2descriptions = new HashMap<String, String>();
				this.parseMappingFile(url2descriptions, this.sourceFilePath+Path.descriptionSuffix);
				//delete mapping file path
				f.delete();
			}
		}

		//parse documents keys
		if(docNames2URL!=null){
			if(this.outputMode.equalsIgnoreCase("rdfnt"))
				//write triples
				this.writeRDF(docNames2URL, url2titles, url2descriptions);
		}

	}

	/*
	 * Write a NT file with the output triples
	 * The format is NTRIPLES, the predicate is dcterms:subject, the subject is the document UTL, the object is an AGROVOC URI 
	 * If the flag to extract titles is set to true, other predicates are added:
	 * URL dcterms:subject AGROV_URI
	 * URL dcterms:title "title"
	 */
	private void writeRDF(Map<String, String> docNames2URL, Map<String, String> url2titles, Map<String, String> url2descriptions){
		TxtNewLineReader reader = new TxtNewLineReader();
		//result
		Set<String> triples = new HashSet<String>();
		//iterator over output RDF files
		int iterator = 0;

		//load agrovoc
		try {
			AgrovocMappingParser amp = new AgrovocMappingParser();
			Map<String, String> agrovoc2uri = amp.mappingAgrovocUri("data/vocabularies/agrovocURILabelMappings.txt", "all");

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
							if(!docURL.contains("<") && !docURL.contains(">") && !docURL.contains("\"")){
								//AGROVOC
								triples.add("<"+docURL+"> <http://purl.org/dc/terms/subject> <"+uri+"> .");
								//titles
								if(this.extractTitles){
									if(url2titles!=null && url2titles.size()>0){
										String title = url2titles.get(docURL);
										if(title!=null) {
											title = title.replaceAll("\"", " ");
											title = title.replaceAll("'", " ");
											triples.add("<"+docURL+"> <http://purl.org/dc/terms/title> \""+title+"\" .");
										}
									}
									if(url2descriptions!=null && url2descriptions.size()>0){
										String desc = url2descriptions.get(docURL);
										if(desc!=null) {
											desc = desc.replaceAll("\"", " ");
											desc = desc.replaceAll("'", " ");
											triples.add("<"+docURL+"> <http://purl.org/dc/terms/description> \""+desc+"\" .");
										}
									}
								}
							}
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
	 * Read the mapping file produced by DownloadFiles, to have the URLs of each document
	 * The mapping file contains a list of DOC_URL=DOC_FILE_NAME
	 */
	private void parseReverseMappingFile(Map<String, String> docNames2URIs, String path){
		TxtNewLineReader reader = new TxtNewLineReader();
		try {
			docNames2URIs.putAll(reader.reverseParseEqualTxt(path));
		} catch (FileNotFoundException e) {
			docNames2URIs.clear();
			log.log(Level.WARNING, "	!!! The system cannot find the file specified "+path);
		}
	}

	/*
	 * Read the mapping file produced by DownloadFiles, to have the URLs of each document
	 * The mapping file contains a list of DOC_URL=titles
	 */
	private void parseMappingFile(Map<String, String> docNames2titles, String path){
		TxtNewLineReader reader = new TxtNewLineReader();
		try {
			docNames2titles.putAll(reader.parseEqualTxt(path));
		} catch (FileNotFoundException e) {
			docNames2titles.clear();
			log.log(Level.WARNING, "	!!! The system cannot find the file specified "+path);
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
			log.log(Level.WARNING, "You have to specify: " +
					"1) the fullpath of the source text file used to download files {the one containing the URLs list}, " +
					"2) the directory with .key MAUI files, " +
					"and 3) the output mode {txt, rdfnt}. " +
					"Optionally, you can specify 4) the filename of the output file, which can be null. " +
			"Optionally, you can specify 5) a boolean flag to state if the tagger should extract also titles (default: true) . ");
		}
		else {
			//don't check the directories, since they were checked in the previous applications of the workflow
			String sourceFilePath = args[0];
			String outputMode = args[2];
			String keyFilePath = args[1];

			//optionally extract titles
			boolean extractTitles = true;
			if(args.length>=5){
				String boleanFlag = args[4];
				if(boleanFlag.equalsIgnoreCase("false"))
					extractTitles = false;
			}

			ProduceMappingTable producer;
			if(args.length<4 || args[3]==null || args[3].equalsIgnoreCase("null"))
				producer = new ProduceMappingTable(sourceFilePath, keyFilePath, outputMode, null, extractTitles);
			else {
				String fileName = args[3];
				//check the extension of the file, ZIP or TAR.GZ depending on the current algorithm
				if(!fileName.endsWith(_outputSuffix))
					fileName = fileName + _outputSuffix;
				producer = new ProduceMappingTable(sourceFilePath, keyFilePath, outputMode, fileName, extractTitles);
			}
			producer.generateOutputFile();
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


}
