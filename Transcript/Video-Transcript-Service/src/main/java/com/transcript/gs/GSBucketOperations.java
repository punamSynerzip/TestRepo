package com.transcript.gs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;

/**
 * @author synerzip:punam
 *
 */

@Service
public class GSBucketOperations {
	private static final Logger LOGGER = LogManager.getLogger(GSBucketOperations.class);

	@Value("${gs.bucket.audios}")
	private String googleStorageBucket;

	@Autowired
	Storage storage;

	public void uploadObjectToGS(String audioFilePath) throws IOException {
		File audioFile = new File(audioFilePath);
		LOGGER.info("Uploading " + audioFilePath + " to google storage bucket ...");
		uploadFile(audioFile.getName(), "audio/wav", audioFile, googleStorageBucket);
	}

	/**
	 * Uploads data to an object in a bucket.
	 *
	 * @param name
	 *            the name of the destination object or file.
	 * @param contentType
	 *            the MIME type of the data.
	 * @param file
	 *            the file to upload.
	 * @param bucketName
	 *            the name of the google storage bucket to create the object in.
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	private void uploadFile(String name, String contentType, File file, String bucketName) throws IOException {
		InputStreamContent contentStream = new InputStreamContent(contentType, new FileInputStream(file));

		contentStream.setLength(file.length());

		StorageObject objectMetadata = new StorageObject().setName(name)
				.setAcl(Arrays.asList(new ObjectAccessControl().setEntity("allUsers").setRole("READER")));

		Storage.Objects.Insert insertRequest = storage.objects().insert(bucketName, objectMetadata, contentStream);
		insertRequest.execute();
	}

	/**
	 * Fetch a list of the objects within the given bucket.
	 *
	 * @param bucketName
	 *            the name of the bucket to list.
	 * @return a list of the contents of the specified bucket.
	 * @throws IOException
	 */
	public List<StorageObject> listBucket(String bucketName) throws IOException {
		Storage.Objects.List listRequest = storage.objects().list(bucketName);

		List<StorageObject> results = new ArrayList<>();

		Objects objects;
		do {
			objects = listRequest.execute();
			results.addAll(objects.getItems());

			listRequest.setPageToken(objects.getNextPageToken());
		} while (null != objects.getNextPageToken());

		return results;
	}

	/**
	 * Deletes an object in a bucket.
	 *
	 * @param path
	 *            the path to the object to delete.
	 * @param bucketName
	 *            the bucket the object is contained in.
	 * @throws IOException
	 */
	public void deleteObject(String path, String bucketName) throws IOException {
		storage.objects().delete(bucketName, path).execute();
	}
}
