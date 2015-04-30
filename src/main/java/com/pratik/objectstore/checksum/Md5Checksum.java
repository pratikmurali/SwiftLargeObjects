/**
 * 
 */
package com.pratik.objectstore.checksum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author pratikm
 *
 */
public class Md5Checksum implements Checksum {
	
	public String createChecksumString(String filename) throws IOException  {

		FileInputStream fis = new FileInputStream(new File(filename));
		String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		fis.close();

		return md5;
	}
}