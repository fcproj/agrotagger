package org.fao.oekc.autotagger.io;

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

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

/**
 * This object is responsible of zipping files or directories
 * @author celli
 */
public class DirectoryZipper {
	
	/**
	 * Tar.gz a directory with all its content
	 * @param directory the File of the directory
	 * @param zipfile the output File
	 * @throws IOException
	 */
	public void tarGzDir(File directory, File zipfile) throws IOException {
		FileOutputStream fOut = null;
		BufferedOutputStream bOut = null;
		GzipCompressorOutputStream gzOut = null;
		TarArchiveOutputStream tOut = null;
		try{
        	fOut = new FileOutputStream(zipfile);
        	bOut = new BufferedOutputStream(fOut);
        	gzOut = new GzipCompressorOutputStream(bOut);
            tOut = new TarArchiveOutputStream(gzOut);
            //start recursive algorithm
            addFileToTarGz(tOut, directory, "");
        } finally {
        	//close streams
        	if(tOut!=null) tOut.finish();
        	if(tOut!=null) tOut.close();
        	if(gzOut!=null) gzOut.close();
        	if(bOut!=null) bOut.close();
        	if(fOut!=null) fOut.close();
        }

    }
	
	/*
	 * Recursive procedure to tar.gz the directory and its content
	 */
    private void addFileToTarGz(TarArchiveOutputStream tOut, File directory, String base) throws IOException {
        String entryName = base + directory.getName();
        TarArchiveEntry tarEntry = new TarArchiveEntry(directory, entryName);
        tOut.putArchiveEntry(tarEntry);

        if (directory.isFile()) {
            IOUtils.copy(new FileInputStream(directory), tOut);
            tOut.closeArchiveEntry();
        } else {
            tOut.closeArchiveEntry();
            File[] children = directory.listFiles();
            if (children != null){
                for (File child : children) {
                    addFileToTarGz(tOut, child, entryName + "/");
                }
            }
        }
    }
	
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
