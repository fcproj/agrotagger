package org.fao.oekc.autotagger.main;

import java.util.HashSet;

import org.fao.oekc.autotagger.support.Path;

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
	 * @param vocabulary name of the SKOS vocabulary, with the .gz file in the "vocabularies" directory
	 * @param modelName name of the file with the model
	 * 
	 * @throws Exception
	 */
	public void termAssignmentSkosAgrovocEn(String vocabulary, String modelName) {
		topicExtractor = new MauiTopicExtractor();
		setGeneralOptions();

		HashSet<String> fileNames;

		// Settings for topic extractor
		topicExtractor.inputDirectoryName = sourceDir;
		topicExtractor.modelName = modelName;
		topicExtractor.vocabularyName = vocabulary;
		topicExtractor.vocabularyFormat = "skos";

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
	 * Default vocabulary and model: agrovoc_en, fao780.
	 * @param args
	 */
	public static void main(String[] args){
		if(args.length<1){
			System.out.println("You have to specify the directory with the TXT files to be parsed by MAUI. Optionally, you can also specify" +
					"a vocabulary [agrovoc_en] and a model [fao780]");
		}
		else {
			MauiAutoTaggerKey tagger = new MauiAutoTaggerKey(args[0]);
			//check if the vocabulary and the model are given by command line
			if(args.length>=3)
				tagger.termAssignmentSkosAgrovocEn(args[1], args[2]);
			else
				tagger.termAssignmentSkosAgrovocEn("agrovoc_en", "fao780");
		}

	}
}
