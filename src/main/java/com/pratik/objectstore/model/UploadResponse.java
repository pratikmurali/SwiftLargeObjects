package com.pratik.objectstore.model;


public class UploadResponse {

	private String md5Hash;
	private String containerName;
	private String manifestFileName;

	public UploadResponse() {

	}

	/**
	 * @return the md5Hash
	 */
	public String getMd5Hash() {
		return md5Hash;
	}

	/**
	 * @param md5Hash
	 *            the md5Hash to set
	 */
	public void setMd5Hash(String md5Hash) {
		this.md5Hash = md5Hash;
	}

	/**
	 * @return the containerName
	 */
	public String getContainerName() {
		return containerName;
	}

	/**
	 * @param containerName
	 *            the containerName to set
	 */
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	/**
	 * @return the manifestFileName
	 */
	public String getManifestFileName() {
		return manifestFileName;
	}

	/**
	 * @param manifestFileName
	 *            the manifestFileName to set
	 */
	public void setManifestFileName(String manifestFileName) {
		this.manifestFileName = manifestFileName;
	}

}
