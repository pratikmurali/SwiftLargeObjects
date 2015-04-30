package com.pratik.objectstore.segment;

import java.io.*;
import java.util.*;

import org.springframework.stereotype.Component;

@Component
public interface Segmentable {
	List<File> segmentFile(File localFile, int segmentSize, char suffix) throws IOException;
}