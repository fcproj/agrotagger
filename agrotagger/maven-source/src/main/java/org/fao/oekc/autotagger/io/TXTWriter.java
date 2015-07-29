package org.fao.oekc.autotagger.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Write a txt file
 * @author fabrizio celli
 *
 */
public class TXTWriter {
	
	public static void writeTXTString2String(Map<String, String> string2string, String filePath) throws IOException{
		//file writer
		BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
		//timestamp
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		out.write("#"+dateFormat.format(date));
		out.newLine();
		for(String title: string2string.keySet()){
			out.write(title);
			out.write("=");
			out.write(string2string.get(title));
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	/**
	 * Write a set of lines
	 * Add a timestamp at the beginning #yyyy/MM/dd HH:mm:ss
	 * @param lines a set of string
	 * @param filePath the fullpath of the file, together with file name and extension
	 * @throws IOException
	 */
	public static void writeTXTLines(Set<String> lines, String filePath) throws IOException{
		//file writer
		BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
		for(String line: lines){
			out.write(line);
			out.newLine();
		}
		out.flush();
		out.close();
	}
	

}
