package com.pratik.objectstore.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pratik.objectstore.api.Upload;

@RestController
@EnableAutoConfiguration
@RequestMapping ("/largeobject")
public class ObjectStoreController {
	
	@Autowired
	Upload uploadService;
	
	@RequestMapping(value = "/version", method = RequestMethod.GET)
	public String getVersion() {

		return "1.0";

	}
	
	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public void putObject(String mimeType, String absoluteFilePath) {
		
		uploadService.uploadLargeObject(mimeType, absoluteFilePath);
		
	}
	
	File getObject(String key) {
		
		return null;
		
	}

}
