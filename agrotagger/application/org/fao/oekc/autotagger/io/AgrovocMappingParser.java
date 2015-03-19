package org.fao.oekc.autotagger.io;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is responsible to parse the mapping agrovoc_english -> URI
 * @author celli
 *
 */
public class AgrovocMappingParser {

	/**
	 * Builds the map with the mapping agrovoc_key_lower_case -> URI
	 * @param filepath the path of the txt mapping file
	 * @param language the language of agrovoc, that can be EN, IT, ES, PT. The default is ALL
	 * @return the map with the mapping agrovoc_key_lower_case -> URI
	 * @throws FileNotFoundException
	 */
	public Map<String, String> mappingAgrovocUri(String filepath, String language) throws FileNotFoundException{
		Map<String, String> result = new HashMap<String, String>();
		//Note that FileReader is used, not File, since File is not Closeable
		Scanner scanner = new Scanner(new FileReader(filepath));
		try {
			//first use a Scanner to get each line
			while (scanner.hasNextLine()){
				String line = scanner.nextLine();
				String[] agrUri = line.split("\\|");
				
				//check languages
				if(language.equalsIgnoreCase("IT") && agrUri.length>=3)
					this.addKeyToMapping(agrUri[2], agrUri[0], result);
				else if(language.equalsIgnoreCase("ES") && agrUri.length>=4)
					this.addKeyToMapping(agrUri[3], agrUri[0], result);
				else if(language.equalsIgnoreCase("PT") && agrUri.length>=5)
					this.addKeyToMapping(agrUri[4], agrUri[0], result);
				else if(language.equalsIgnoreCase("FR") && agrUri.length>=6)
					this.addKeyToMapping(agrUri[5], agrUri[0], result);
				else if(language.equalsIgnoreCase("EN") && agrUri.length>=2)
					this.addKeyToMapping(agrUri[1], agrUri[0], result);
				else {
					//default: ALL
					if(agrUri.length>=2)
						for(int i=1; i<agrUri.length; i++)
							this.addKeyToMapping(agrUri[i], agrUri[0], result);
				}
			}
		}
		finally {
			//ensure the underlying stream is always closed
			//this only has any effect if the item passed to the Scanner
			//constructor implements Closeable (which it does in this case).
			scanner.close();
		}
		return result;
	}
	
	private void addKeyToMapping(String key, String uri, Map<String, String> result){
		if(key!=null && key.trim().length()>0){
			result.put(key.toLowerCase(), uri);
		}
	}

	public static void main(String[] args) throws FileNotFoundException{
		AgrovocMappingParser amp = new AgrovocMappingParser();
		Map<String, String> result = amp.mappingAgrovocUri("../agris_journals/input/agrovocURILabelMappings.txt", "en");
		System.out.println(result.size());
	}

}
