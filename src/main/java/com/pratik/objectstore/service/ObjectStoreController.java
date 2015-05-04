package com.pratik.objectstore.service;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pratik.objectstore.api.Upload;
import com.pratik.objectstore.model.UploadRequest;
import com.pratik.objectstore.model.UploadResponse;
import com.pratik.objectstore.model.Version;

@RestController
@EnableAutoConfiguration
public class ObjectStoreController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	Upload uploadService;
	
	//Spring boot automatically uses Jackson to marshal out JSON instances.
	@RequestMapping(value = "/version", method = RequestMethod.GET)
	public Version getVersion() {

		return new Version("1.0");

	}
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public UploadResponse putObject(@RequestBody UploadRequest request) {
		logger.debug("User sent Mime Type = "+request.getMimeType());
		logger.debug("User sent Absolute File Path = "+StringEscapeUtils.escapeJson(request.getAbsFilePath()));
		logger.debug("User sent number of chunks = "+request.getNoOfChunks());
		return uploadService.uploadLargeObject(request.getMimeType(), request.getAbsFilePath(), request.getNoOfChunks());
		
	}

}
