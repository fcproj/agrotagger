package org.fao.oekc.autotagger.test;

import java.util.HashSet;

import maui.main.MauiTopicExtractor;

/**
 * Test documents automatic tagging with Agrovoc EN
 * @author celli
 * 
 */
public class TestMauiTagger {
	
	private MauiTopicExtractor topicExtractor;

	public TestMauiTagger ()  {	}
	
	public static void main(String[] args) throws Exception{
		TestMauiTagger tester = new TestMauiTagger();
		tester.testTermAssignment();
	}
	
	/**
	 * Sets general parameters: debugging printout, language specific options
	 * like stemmer, stopwords.
	 * @throws Exception 
	 */
	private void setGeneralOptions()  {
		topicExtractor.debugMode = true;
		//topicExtractor.topicsPerDocument = 10; 
	}

	/**
	 * Demonstrates how to perform term assignment. Applicable to any vocabulary
	 * in SKOS or text format.
	 * 
	 * @throws Exception
	 */
	public void testTermAssignment() throws Exception {
		topicExtractor = new MauiTopicExtractor();
		setGeneralOptions();
		
		// Directories with test data
		String testDir = "data/test";

		// Vocabulary
		String vocabulary = "agrovoc_en";
		String format = "skos";

		// name of the file to save the model
		String modelName = "fao780";
		HashSet<String> fileNames;

		// Settings for topic extractor
		topicExtractor.inputDirectoryName = testDir;
		topicExtractor.modelName = modelName;
		topicExtractor.vocabularyName = vocabulary;
		topicExtractor.vocabularyFormat = format;
		
		// Run topic extractor
		topicExtractor.loadModel();
		fileNames = topicExtractor.collectStems();
		topicExtractor.extractKeyphrases(fileNames);
		
	}

}
