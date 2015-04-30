package com.pratik.objectstore.segment;

import java.io.*;
import java.util.*;

public interface Segmentable {
	List<File> segmentFile(File localFile, int segmentSize, char suffix) throws IOException;
}