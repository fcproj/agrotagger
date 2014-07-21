package org.fao.oekc.autotagger.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Deque;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This object is responsible of zipping files or directories
 * @author celli
 */
public class DirectoryZipper {
	
	/**
	 * Zip a directory with all its content
	 * @param directory the File of the directory
	 * @param zipfile the output File
	 * @throws IOException
	 */
	public void zipDir(File directory, File zipfile) throws IOException {
		URI base = directory.toURI();
		Deque<File> queue = new LinkedList<File>();
		queue.push(directory);
		OutputStream out = new FileOutputStream(zipfile);
		Closeable res = out;
		try {
			ZipOutputStream zout = new ZipOutputStream(out);
			res = zout;
			while (!queue.isEmpty()) {
				directory = queue.pop();
				for (File kid : directory.listFiles()) {
					String name = base.relativize(kid.toURI()).getPath();
					if (kid.isDirectory()) {
						queue.push(kid);
						name = name.endsWith("/") ? name : name + "/";
						zout.putNextEntry(new ZipEntry(name));
					} else {
						zout.putNextEntry(new ZipEntry(name));
						copy(kid, zout);
						zout.closeEntry();
					}
				}
			}
		} finally {
			res.close();
		}
	}

	private void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		while (true) {
			int readCount = in.read(buffer);
			if (readCount < 0) {
				break;
			}
			out.write(buffer, 0, readCount);
		}
	}

	private void copy(File file, OutputStream out) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			copy(in, out);
		} finally {
			in.close();
		}
	}

	/**
	 * Zip a file
	 * @param filePath the path of the file to zip
	 * @param zipFileName the path of the output zip file
	 * @param deleteSourceFile if true, delete the source zipped file
	 */
	public void zipFile(String filePath, String zipFileName, boolean deleteSourceFile){
		try
		{
			// definiamo l'output previsto che sarà un file in formato zip 
			ZipOutputStream out = new ZipOutputStream(new 
					BufferedOutputStream(new FileOutputStream(zipFileName)));

			// definiamo il buffer per lo stream di bytes 
			byte[] data = new byte[8192]; 

			// indichiamo il nome del file che subirà la compressione 
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(filePath));
			int count;
			
			//cerca il file name
			String[] paths = filePath.split("\\\\");
			if(paths.length<2)
				paths = filePath.split("/");
			String fileName = paths[paths.length-1];

			// processo di compressione 
			out.putNextEntry(new ZipEntry(fileName));
			while((count = in.read(data,0,1000)) != -1) { 
				out.write(data, 0, count);
			}
			in.close();
			out.flush();
			out.close();
			
			//delete source file
			if(deleteSourceFile) {
				File source = new File(filePath);
				source.delete();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
