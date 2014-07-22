package org.fao.oekc.autotagger.util;

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
	 * @param language the language of agrovoc, that can be EN, IT, ES, PT. The default is EN
	 * @return the map with the mapping agrovoc_key_lower_case -> URI
	 * @throws FileNotFoundException
	 */
	public static Map<String, String> mappingAgrovocUri(String filepath, String language) throws FileNotFoundException{
		Map<String, String> result = new HashMap<String, String>();
		//Note that FileReader is used, not File, since File is not Closeable
		Scanner scanner = new Scanner(new FileReader(filepath));
		try {
			//first use a Scanner to get each line
			while (scanner.hasNextLine()){
				String line = scanner.nextLine();
				String[] agrUri = line.split("\\|");
				String key = "";
				if(language.equalsIgnoreCase("IT") && agrUri.length>=3)
					key = agrUri[2].toLowerCase();
				else if(language.equalsIgnoreCase("ES") && agrUri.length>=4)
					key = agrUri[3].toLowerCase();
				else if(language.equalsIgnoreCase("PT") && agrUri.length>=5)
					key = agrUri[4].toLowerCase();
				else if(language.equalsIgnoreCase("FR") && agrUri.length>=6)
					key = agrUri[5].toLowerCase();
				else
					//default: EN
					if(agrUri.length>=2)
						key = agrUri[1].toLowerCase();
				if(key!=null && key.trim().length()>0)
					result.put(key, agrUri[0]);
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

	public static void main(String[] args) throws FileNotFoundException{
		Map<String, String> result = AgrovocMappingParser.mappingAgrovocUri("../agris_journals/input/agrovocURILabelMappings.txt", "en");
		System.out.println(result.size());
	}

}
