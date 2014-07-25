package org.fao.oekc.autotagger.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * HTML Converter
 * @author celli
 *
 */
public class HTMLConverter {

	/**
	 * Remove HTML tags from the file content
	 * @param filePath the path of the file
	 */
	public void removeHTMLTags(String filePath, boolean removeSource){
		StringBuilder sb = new StringBuilder();
		try {
			//read the file in a string and remove HTML tags
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String line;
			while ( (line=br.readLine()) != null) {
				sb.append(line);
			}
			String nohtml = sb.toString().replaceAll("\\<.*?>","");

			//delete the input file
			if(removeSource){
				File input = new File(filePath);
				input.delete();
			}

			//create the new file
			BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
			out.write(nohtml);
			out.close();
			
		} catch(IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
