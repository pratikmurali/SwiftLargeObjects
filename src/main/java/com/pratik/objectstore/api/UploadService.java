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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pratik.objectstore.checksum.Checksum;
import com.pratik.objectstore.model.UploadResponse;
import com.pratik.objectstore.segment.Segmentable;

/**
 * @author pratikm
 *
 */
@Service
public class UploadService implements Upload {

	@Autowired
	Segmentable myFileSegmenter;
	
	@Autowired
	Checksum myMd5Calculator;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public UploadResponse uploadLargeObject(final String mimeType,
			final String absFilePath, final int noOfChunks) {

		UploadResponse response = new UploadResponse();
		
		int processors = Runtime.getRuntime().availableProcessors();

		try {
			char suffix = 'a';

			logger.debug("The absolute File path from the request is"
					+ absFilePath);
			logger.debug("The mime type from the request is" + mimeType);
			logger.debug("The number of chunks from the request is "
					+ noOfChunks);
			;
			File file = new File(absFilePath);
			final String fileName = file.getName();

			// Get File Length in bytes.
			long fileSize = file.length();
			int sizeOfEachSegment = (int) (fileSize / noOfChunks);
			logger.debug("Will setup parallel uploads of size "
					+ sizeOfEachSegment / 1000 + " KB");

			// split into 100 MB chunks.
			List<File> listOfSegments = myFileSegmenter.segmentFile(file,
					sizeOfEachSegment, suffix);

			// List the segments
			for (File segment : listOfSegments) {
				logger.debug("Segment Name: " + segment.getName()
						+ "\t Segment Size: " + segment.length());
			}

			final CloudStorage myConnection = createCloudConnection();

			// Create a Container
			UUID guid = UUID.randomUUID();
			final String containerName = "pratik_" + guid;

			response.setContainerName(containerName);

			myConnection.createContainer(containerName);

			List<Container> containers = myConnection.listContainers();
			logger.debug("Found the following containers on the Account");
			for (Container c : containers) {
				logger.debug("Container: " + c.getName());
			}

			logger.debug("Uploading File Chunks ...");

			final Map<String, String> fileHashesMap = new LinkedHashMap<>();

			// Upload segments to a container called pratik_<some_guid>
			// A cachedThreadPool to have the ability to reuse threads.
			ExecutorService parallelUploadExecutorService = Executors
					.newFixedThreadPool(processors*10);
			CompletionService<Object> completionService = new ExecutorCompletionService<>(
					parallelUploadExecutorService);

			for (final File segment : listOfSegments) {
				completionService.submit(new Runnable() {
					public void run() {
						try {
							FileInputStream segmentFis = new FileInputStream(
									segment);
							String md5Hash = myMd5Calculator
									.createChecksumString(StringUtils
											.substringBeforeLast(absFilePath,
													"/")
											+ "/" + segment.getName());
							fileHashesMap.put(segment.getName(), md5Hash);
							logger.debug("Uploading Segment "
									+ segment.getName());
							myConnection.storeObject(containerName,
									segment.getName(), mimeType, segmentFis);
							segmentFis.close();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, myConnection);

			}

			try {
				for (int i = 0; i < listOfSegments.size(); i++) {

					// Retrieve the Future<CloudStorage> from the completion
					// service.
					CloudStorage connection = (CloudStorage) completionService
							.take().get();
					if (connection != null) {
						logger.debug("Segment " + i + " finished uploading.");
					}

				}
			} catch (ExecutionException ex) {
				ex.printStackTrace();
			} catch (InterruptedException ie) {
				// Keep state and don't swallow the InterruptedException.
				Thread.currentThread().interrupt();

			} finally {
				if (parallelUploadExecutorService != null) {
					final List<Runnable> rejected = parallelUploadExecutorService
							.shutdownNow();
					logger.debug("Rejected tasks(uploads) {}:", rejected.size());
				}
			}

			// Store the Manifest after uploading the segments.
			myConnection.storeObjectManifest(containerName, fileName
					+ ".manifest", mimeType, containerName, "" + fileName,
					fileHashesMap);
			logger.debug("Uploaded Segments have been mapped together");

			String originalFileMd5 = myMd5Calculator
					.createChecksumString(absFilePath);
			logger.debug("Original file MD5 hash =" + originalFileMd5);
			response.setMd5Hash(originalFileMd5);
			response.setManifestFileName(fileName + ".manifest");

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return response;
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
		

