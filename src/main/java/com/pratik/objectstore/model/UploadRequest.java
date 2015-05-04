package com.pratik.objectstore.model;

import org.springframework.stereotype.Component;

@Component
public class UploadRequest {

	private String mimeType;
	private String absFilePath;
	private int noOfChunks;

	public UploadRequest() {

	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @param mimeType
	 *            the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * @return the absFilePath
	 */
	public String getAbsFilePath() {
		return absFilePath;
	}

	/**
	 * @param absFilePath
	 *            the absFilePath to set
	 */
	public void setAbsFilePath(String absFilePath) {
		this.absFilePath = absFilePath;
	}

	/**
	 * @return the noOfChunks
	 */
	public int getNoOfChunks() {
		return noOfChunks;
	}

	/**
	 * @param noOfChunks the noOfChunks to set
	 */
	public void setNoOfChunks(int noOfChunks) {
		this.noOfChunks = noOfChunks;
	}
	
	

}
