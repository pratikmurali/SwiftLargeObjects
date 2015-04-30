package com.pratik.objectstore.checksum;

import java.io.IOException;


public interface Checksum {
	public String createChecksumString(String filename) throws IOException;
}
