/**
 * 
 */
package com.pratik.objectstore.checksum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author pratikm
 *
 */
@Component
public class Md5Checksum implements Checksum {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public String createChecksumString(String filename) throws IOException {

		FileInputStream fis = new FileInputStream(new File(filename));
		logger.debug("Generating Checksum for file " + filename);
		String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		logger.debug("Generated md5 Checksum is " + md5);
		fis.close();

		return md5;
	}
}