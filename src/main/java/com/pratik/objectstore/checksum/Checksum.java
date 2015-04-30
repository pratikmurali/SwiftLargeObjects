package com.pratik.objectstore.checksum;

import java.io.IOException;

import org.springframework.stereotype.Component;

@Component
public interface Checksum {
	public String createChecksumString(String filename) throws IOException;
}
