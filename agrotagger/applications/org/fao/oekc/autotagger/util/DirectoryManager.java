package org.fao.oekc.autotagger.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Manage the creation of a directory
 * @author celli
 *
 */
public class DirectoryManager {

	/**
	 * Check if the directory exists. If it exists, cleans the content. Otherwise, creates the directory.
	 * @param directoryName
	 * @return false if an error occurred (permissions errors), true if the process finishes
	 */
	public boolean checkCleanCreateDir(String directoryName){
		File theDir = new File(directoryName);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			try{
				theDir.mkdir();
			} catch(SecurityException se){
				//handle it
				System.out.println("ERROR1: You don't have permissions to write the directory: " + directoryName);
				return false;
			}      
		} else {
			//clean the content
			try{
				FileUtils.cleanDirectory(theDir); 
			} catch(IOException se){
				//handle it
				System.out.println("ERROR2: Can't clean the directory: " + directoryName);
				return false;
			} 
		}
		
		return true;
	}
	
	/**
	 * Check if the directory exists. If it does not exist, creates the directory.
	 * @param directoryName
	 * @return false if an error occurred (permissions errors), true if the process finishes
	 */
	public boolean checkCreateDir(String directoryName){
		File theDir = new File(directoryName);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			try{
				theDir.mkdir();
			} catch(SecurityException se){
				//handle it
				System.out.println("ERROR3: You don't have permissions to write the directory: " + directoryName);
				return false;
			}      
		} 
		
		return true;
	}
	
	/**
	 * Recursively delete the directory and all its content (files and nested directories)
	 * @param path the full path of the directory to delete
	 * @return true, if the directory have been correctly deleted
	 */
	public boolean deleteDirectory(File path) {
		if(path.exists()) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		} else {
			System.out.println("ERROR4:"+path.getName()+" does not exist...");
		}
		return(path.delete());
	}
	
	/**
	 * Recursively clean the directory content
	 * @param path the full path of the directory to delete
	 * @return true, if the directory have been correctly cleaned
	 */
	public boolean cleanDirectory(File path) {
		if(path.exists()) {
			//clean the content
			try{
				FileUtils.cleanDirectory(path); 
			} catch(IOException se){
				//handle it
				se.printStackTrace();
				return false;
			} 
			return true;
		}
		return false;
	}

}
