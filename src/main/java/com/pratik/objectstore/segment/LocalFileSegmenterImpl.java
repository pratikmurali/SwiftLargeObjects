package com.pratik.objectstore.segment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalFileSegmenterImpl implements Segmentable {

	@Override
	public List<File> segmentFile(File f, int chunkSize, char suffix)
			throws IOException {

		List<File> chunks = new ArrayList<>();
		byte[] byteBuffer = new byte[chunkSize];

		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(f))) {
			String name = f.getName();

			int tmp = 0;

			while ((tmp = bis.read(byteBuffer)) > 0) {
				// write each chunk of data into separate file with an
				// alphabetical suffix
				File chunkFile = new File(f.getParent(), name + "." + suffix++);
				try (FileOutputStream out = new FileOutputStream(chunkFile)) {
					out.write(byteBuffer, 0, tmp);// tmp is chunk size
				}

				chunks.add(chunkFile);
			}

		}

		return chunks;

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
