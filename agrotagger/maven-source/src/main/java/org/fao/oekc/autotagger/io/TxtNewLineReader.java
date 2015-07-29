package org.fao.oekc.autotagger.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jfcutils.util.StringUtils;

/**
 * Parse a text file whose lines have been grouped by new line and each group ends with a "@" line.
 * The key of each group is the first line
 * @author fabrizio celli
 *
 */
public class TxtNewLineReader {
	
	/**
	 * Parse a text file of lines. Returns the list of URLs.
	 * @param filepath the physical path of the file
	 * @return the mapping of a text file of lines
	 * @throws FileNotFoundException
	 */
	public Set<String> parseTxt(String filepath) throws FileNotFoundException{
		Set<String> result = new HashSet<String>();
		//Note that FileReader is used, not File, since File is not Closeable
		File input = new File(filepath);
	    Scanner scanner = new Scanner(new FileReader(input));
	    try {
	      //first use a Scanner to get each line
	      while (scanner.hasNextLine()){
	        String line = scanner.nextLine();
	        if(line!=null && !line.startsWith("#")){
	        	line = (new StringUtils()).trimLeft(line);
	        	line = (new StringUtils()).trimRight(line);
	        	result.add(line);
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
	
	/**
	 * Parse a text file of lines having a pattern key=value. Returns the mapping.
	 * @param filepath the physical path of the file
	 * @return the mapping of a text file of lines having a pattern key=value
	 * @throws FileNotFoundException
	 */
	public Map<String, String> parseEqualTxt(String filepath) throws FileNotFoundException{
		Map<String, String> result = new HashMap<String, String>();
		//Note that FileReader is used, not File, since File is not Closeable
		File input = new File(filepath);
	    Scanner scanner = new Scanner(new FileReader(input));
	    try {
	      //first use a Scanner to get each line
	      while (scanner.hasNextLine()){
	        String line = scanner.nextLine();
	        if(!line.startsWith("#")){
	        	String[] mapping = line.split("=");
	        	if(mapping.length==2)
	        		result.put(mapping[0], mapping[1]);
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
	
	/**
	 * Parse a text file of lines having a pattern key=value. Returns the reverse mapping value=key.
	 * @param filepath the physical path of the file
	 * @return the reverse mapping of a text file of lines having a pattern key=value
	 * @throws FileNotFoundException
	 */
	public Map<String, String> reverseParseEqualTxt(String filepath) throws FileNotFoundException{
		Map<String, String> result = new HashMap<String, String>();
		//Note that FileReader is used, not File, since File is not Closeable
		File input = new File(filepath);
	    Scanner scanner = new Scanner(new FileReader(input));
	    try {
	      //first use a Scanner to get each line
	      while (scanner.hasNextLine()){
	        String line = scanner.nextLine();
	        if(!line.startsWith("#")){
	        	String[] mapping = line.split("=");
	        	result.put(mapping[1], mapping[0]);
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
	
	/**
	 * Parse the text files obtaind as the output of the Apache Nutch Crawler. Returns the set of URLs.
	 * @param filepath the physical path of the file
	 * @return the mapping of the text files obtaind as the output of the Apache Nutch Crawler
	 * @throws FileNotFoundException
	 */
	public Set<String> parseTxtNutch(String filepath) throws FileNotFoundException{
		Set<String> result = new HashSet<String>();
		//Note that FileReader is used, not File, since File is not Closeable
		File input = new File(filepath);
	    Scanner scanner = new Scanner(new FileReader(input));
	    try {
	      //first use a Scanner to get each line
	      while (scanner.hasNextLine()){
	        String line = scanner.nextLine();
	        if(!line.startsWith("#")){
	        	//work on it
	        	//start http
	        	int httpIndex = line.indexOf("http");
	        	if(httpIndex!=-1){
	        		line = line.substring(httpIndex);
	        		//look for anchor
	        		int anchorIndex = line.indexOf("anchor:");
	        		if(anchorIndex!=-1)
	        			line = line.substring(0, anchorIndex);
	        		//trim
	        		line = (new StringUtils()).trimLeft(line);
		        	line = (new StringUtils()).trimRight(line);
	        		result.add(line);
	        	} 	
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

}
