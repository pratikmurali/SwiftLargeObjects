/**
 * 
 */
package com.pratik.objectstore.api;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import oracle.cloud.storage.CloudStorage;
import oracle.cloud.storage.CloudStorageConfig;
import oracle.cloud.storage.CloudStorageFactory;
import oracle.cloud.storage.model.Container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pratik.objectstore.checksum.Checksum;
import com.pratik.objectstore.checksum.Md5Checksum;
import com.pratik.objectstore.segment.Segmentable;

/**
 * @author pratikm
 *
 */
public class UploadService implements Upload {

	@Autowired
	Segmentable myFileSegmenter;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void uploadLargeObject(final String mimeType, final String absFilePath) {

		int processors = Runtime.getRuntime().availableProcessors();

		try {
			char suffix = 'a';
			final Checksum myMd5Calculator = new Md5Checksum();

			// split into 100 MB chunks.
			List<File> listOfSegments = myFileSegmenter.segmentFile(new File(
					absFilePath), 104857600, suffix);

			// List the segments
			for (File segment : listOfSegments) {
				logger.debug("Segment Name: " + segment.getName()
						+ "\t Segment Size: " + segment.length());
			}

			final CloudStorage myConnection = createCloudConnection();

			// Create a Container
			UUID guid = UUID.randomUUID();
			
			
			myConnection.createContainer("demo-container4");

			List<Container> containers = myConnection.listContainers();
			logger.debug("Found the following containers on the Account");
			for (Container c : containers) {
				logger.debug("Container: " + c.getName());
			}

			logger.debug("Uploading File Chunks ...");

			final Map<String, String> fileHashesMap = new LinkedHashMap<>();

			// Upload segments to a container called demo-container4

			ExecutorService parallelUploadExecutorService = Executors
					.newFixedThreadPool(processors * 10);
			CompletionService<Object> completionService = new ExecutorCompletionService<>(
					parallelUploadExecutorService);

			for (final File segment : listOfSegments) {
				completionService.submit(new Runnable() {
					public void run() {
						try {
							FileInputStream segmentFis = new FileInputStream(
									segment);
							String md5Hash = myMd5Calculator
									.createChecksumString(absFilePath
											+ segment.getName());
							fileHashesMap.put(segment.getName(), md5Hash);
							System.out.println("Uploading Segment "
									+ segment.getName());
							myConnection.storeObject("demo-container4",
									segment.getName(), mimeType,
									segmentFis);
							segmentFis.close();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, myConnection);

			}

			try {
				for (int i = 0; i < listOfSegments.size(); i++) {

					CloudStorage connection = (CloudStorage) completionService
							.take().get();
					if (connection != null) {
						logger.debug("Segment " + i + " finished uploading.");
					}

				}
			} catch (InterruptedException | ExecutionException ex) {
				ex.printStackTrace();
			} finally {
				if (parallelUploadExecutorService != null) {
					parallelUploadExecutorService.shutdownNow();
				}
			}

			// Store the Manifest after uploading the segments.
			myConnection.storeObjectManifest("demo-container4",
					"TechSenateDemoHD.manifest", "video/quicktime",
					"demo-container4", "" + "TechSenateDemoHD.mov",
					fileHashesMap);
			logger.debug("Uploaded Segments have been mapped together");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 * @throws MalformedURLException
	 */
	private CloudStorage createCloudConnection() throws MalformedURLException {
		// Connect to Oracle Cloud Storage
		CloudStorageConfig myConfig = new CloudStorageConfig();

		myConfig.setServiceName("storagetrial1800-usnetapptrial32134")
				.setUsername("pratik79@me.com")
				.setPassword("Gold>Rush49".toCharArray())
				.setServiceUrl(
						"https://usnetapptrial32134.storage.oraclecloud.com");

		final CloudStorage myConnection = CloudStorageFactory
				.getStorage(myConfig);
		return myConnection;
	}
}
		

