package org.fao.oekc.autotagger.support;

import java.io.File;

/**
 * This class checks parameters given by the users to some scripts, like the bash scripts provided with the application
 * @author celli
 *
 */
public class CheckParameters {
	
	/**
	 * Throws an Exeption if paths don't exist. Check existance of model and vocabulary, for MauiAutoTaggerKey application
	 * @param args vocabulary_name; model_name
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		//check existance of model and vocabulary, for MauiAutoTaggerKey application
		if(args.length>=2){
			
			File vocabF = new File("./data/vocabularies/"+args[0]+".rdf.gz");
			File modelF = new File(args[1]);
			if(!vocabF.exists())
				throw new Exception();
			if(!modelF.exists())
				throw new Exception();
		}
	}

}
