/**
 * 
 */
package com.pratik.objectstore.api;

import org.springframework.stereotype.Service;

import com.pratik.objectstore.model.UploadResponse;

/**
 * @author pratikm
 *
 */
@Service
public interface Upload {

	public UploadResponse uploadLargeObject(final String mimeType,
			final String absFilePath, final int noOfChunks);

}
