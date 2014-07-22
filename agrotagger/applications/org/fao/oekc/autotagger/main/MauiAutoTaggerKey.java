package org.fao.oekc.autotagger.main;

import java.util.HashSet;

import org.fao.oekc.autotagger.main.support.Path;

import maui.main.MauiTopicExtractor;

/**
 * Given the path with the source text files, generates the .key files with agrovoc en tagging
 * @author fabrizio celli
 *
 */
public class MauiAutoTaggerKey {

	//the extractor
	private MauiTopicExtractor topicExtractor;

	// Directories with data
	private String sourceDir;

	/**
	 * Constructor
	 * @param dir the fullpath of the directory with text files to parse and tag
	 */
	public MauiAutoTaggerKey(String dir){
		this.sourceDir = dir + Path.documentsAndKeys;
	}

	/*
	 * Sets general parameters: debugging printout, language specific options
	 * like stemmer, stopwords.
	 */
	private void setGeneralOptions()  {
		topicExtractor.debugMode = false;
	}

	/**
	 * Demonstrates how to perform term assignment. Applicable to any vocabulary
	 * in SKOS or text format.
	 * 
	 * @throws Exception
	 */
	public void termAssignmentSkosAgrovocEn() {
		topicExtractor = new MauiTopicExtractor();
		setGeneralOptions();

		// Vocabulary
		String vocabulary = "agrovoc_en";
		String format = "skos";

		// name of the file to save the model
		String modelName = "fao780";
		HashSet<String> fileNames;

		// Settings for topic extractor
		topicExtractor.inputDirectoryName = sourceDir;
		topicExtractor.modelName = modelName;
		topicExtractor.vocabularyName = vocabulary;
		topicExtractor.vocabularyFormat = format;

		// Run topic extractor
		try {
			topicExtractor.loadModel();
			fileNames = topicExtractor.collectStems();
			topicExtractor.extractKeyphrases(fileNames);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Given a set of TXT files, located at the directory given by the user, generates a .key file for each of them, containing the (English) Agrovoc extracted keywords
	 * @param args
	 */
	public static void main(String[] args){
		if(args.length<1){
			System.out.println("You have to specify the directory with the TXT files to be parsed by MAUI");
		}
		else {
			MauiAutoTaggerKey tagger = new MauiAutoTaggerKey(args[0]);
			tagger.termAssignmentSkosAgrovocEn();

		}

	}
}
