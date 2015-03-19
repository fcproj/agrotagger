package org.fao.oekc.autotagger.support;

import java.io.IOException;

import jfcutils.files.FileSplitter;
import jfcutils.util.StringUtils;

/**
 * Split the input file (i.e. the output of the crawler or a list of URLs) in many files.
 * The splitting is based on the number of lines in the output files, given as parameter of the application.
 * Output files will be placed in the directory "splitted", created in the directory containing the input file.
 * @author celli
 *
 */
public class CrawlerFileSplit {
	
	/**
	 * 
	 * Reads the parameters [number of lines] [filePathToSplit] and run the splitting
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: Split [number of lines] [filePathToSplit]");
			return;
		}
		
		if(!(new StringUtils()).isInteger(args[0]) || Integer.parseInt(args[0])<0){
			System.err.println("Usage: Split [number of lines] [filePathToSplit]. The first parameter must be a positive Integer.");
			return;
		} 

		String filePath = args[1];
		int lines = Integer.parseInt(args[0]);
		FileSplitter splitter = new FileSplitter();
		
		try {
			splitter.getData(filePath, lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
