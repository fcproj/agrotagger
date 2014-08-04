package org.fao.oekc.autotagger.main.support;

import java.io.File;

/**
 * Specify patsh in the working directories
 * @author celli
 *
 */
public class Path {
	
	public static final String documentsAndKeys = File.separator + "docKeys";
	public static final String tmpRDF = File.separator + "tmp";
	public static final String mappingSuffix = "_mapping";

	public static void main(String[] args){
		System.out.println();
		System.out.println(Path.documentsAndKeys);
	}
}
