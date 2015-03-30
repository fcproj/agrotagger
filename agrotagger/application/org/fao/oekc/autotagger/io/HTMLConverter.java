package org.fao.oekc.autotagger.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import jfcutils.util.StringUtils;

import org.fao.oekc.autotagger.main.executor.DownloadConvertRunnable;

/**
 * HTML Converter
 * @author celli
 *
 */
public class HTMLConverter {

	/**
	 * Remove HTML tags from the file content
	 * @param filePath the path of the file
	 * @param removeSource if true remove the source PDF after conversion
	 * @param url the URL of the resource to download
	 * @param url2doctitle map to store titles, can be null
	 * @param url2description map to store descriptions, can be null
	 * @param extractTitles true if we need to extract titles
	 */
	public void removeHTMLTags(String filePath, boolean removeSource, String url, Map<String, String> url2doctitle, Map<String, String> url2description, boolean extractTitles){
		StringBuilder sb = new StringBuilder();

		try {
			//read the file in a string and remove HTML tags
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String line;
			while ( (line=br.readLine()) != null) {
				sb.append(line);
			}
			//extract the title and then remove HTML tags
			if(extractTitles) {
				String doctitle = this.extractHTMLTitle(sb.toString());
				if(doctitle!=null){
					doctitle = (new StringUtils()).trimLeft(doctitle);
					doctitle = (new StringUtils()).trimRight(doctitle);
					if(doctitle.length()>3)
						DownloadConvertRunnable.addTitleToMap(url2doctitle, url, doctitle);
				}	
				//check description availability
				String docDesc = this.extractHTMLDescription(sb.toString());
				if(docDesc!=null && docDesc.trim().length()>0){
					docDesc = (new StringUtils()).trimLeft(docDesc);
					docDesc = (new StringUtils()).trimRight(docDesc);
					if(docDesc.length()>3)
						DownloadConvertRunnable.addDescriptionToMap(url2description, url, docDesc);
				}
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

	/*
	 * Apply REGEX to extract HTML title
	 */
	private String extractHTMLTitle(String content){
		String title = null;
		int start = content.indexOf("<title>");
		int end = content.indexOf("</title>");
		if(start!=-1 && end!=-1 && (start+7)<end && end<content.length())
			title = content.substring(start+7, end);
		return title;
	}

	/*
	 * Apply REGEX to extract HTML description
	 */
	private String extractHTMLDescription(String content){
		String description = null;
		int start = content.indexOf("meta name=\"description\"");
		if(start!=-1 && start<content.length()){
			content = content.substring(start);
			int data = content.indexOf("content=");
			int close = content.indexOf(">");
			if(data!=-1 && close!=-1 && (close-1)<content.length() && (data+8)<(close-1)){
				description = content.substring(data+8, close-1);
			}
		}
		return description;
	}

}
