package org.fao.oekc.autotagger.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class TestDownloadFile {

	public static void main(String[] args) throws MalformedURLException, IOException{
		BufferedInputStream in = new BufferedInputStream(new URL("http://www-wds.worldbank.org/servlet/WDSContentServer/IW3P/IB/2000/05/06/000094946_0004270533342/Rendered/PDF/multi_page.pdf").openStream());
		FileOutputStream fos = new FileOutputStream("data/test/prova.pdf");
		BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
		byte[] data = new byte[1024];
		int x=0;
		while((x=in.read(data,0,1024))>=0){
			bout.write(data,0,x);
		}
		bout.close();
		in.close();
	}
}
