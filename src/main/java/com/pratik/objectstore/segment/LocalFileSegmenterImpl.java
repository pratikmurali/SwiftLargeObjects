package com.pratik.objectstore.segment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LocalFileSegmenterImpl implements Segmentable {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Lock lock = new ReentrantLock();

	@Override
	public List<File> segmentFile(File f, int chunkSize, char suffix)
			throws IOException {

		List<File> chunks = new ArrayList<>();
		byte[] byteBuffer = new byte[chunkSize];
		
	    //Ensure multiple threads don't start chunking the file.
		lock.lock();

		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(f))) {
			String name = f.getName();

			int tmp = 0;
			int count=0;

			while ((tmp = bis.read(byteBuffer)) > 0) {
				// write each chunk of data into separate file with an
				// alphabetical suffix
				File chunkFile = new File(f.getParent(), name + "." + generateBase26Prefix(count++));
				logger.debug(" Creating chunk " + chunkFile.getName());
				try (FileOutputStream out = new FileOutputStream(chunkFile)) {
					out.write(byteBuffer, 0, tmp);// tmp is chunk size
				}

				chunks.add(chunkFile);
			}

		}
		//Release the Lock once chunking is complete.
		lock.unlock();

		return chunks;

	}
	
	/**
	 * Generate Excel like prefixes
	 * @param number
	 * @return
	 */
	public static String generateBase26Prefix (int number) 
    {     
        int dividend = number;   
        int i;
        String columnName = "";     
        int modulo;     
        while (dividend > 0)     
        {        
            modulo = (dividend - 1) % 26;         
            i = 65 + modulo;
            columnName = new Character((char)i).toString() + columnName;        
            dividend = (int)((dividend - modulo) / 26);    
        }       
        return columnName; 
    }  

}
