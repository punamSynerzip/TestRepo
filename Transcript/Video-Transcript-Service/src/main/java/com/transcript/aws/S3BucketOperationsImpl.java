package com.transcript.aws;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * @author synerzip:punam
 *
 */

@Service
public class S3BucketOperationsImpl implements S3BucketOperations {
	private static final Logger LOGGER = LogManager.getLogger(S3BucketOperationsImpl.class);

	@Autowired
	AmazonS3 s3Client;

	/**
	 * Demonstrates to upload objects(files) in bucket on aws s3
	 * 
	 * @param bucket_name
	 *            name of bucket on s3 to upload files
	 * @param filesPath
	 *            location of files to be uploaded
	 */
	@Override
	public void uploadObject(String bucketName, String transcriptFilePath) {
		File uploadFile = new File(transcriptFilePath);
		LOGGER.info("Uploading transcript file to S3 bucket ...");
		s3Client.putObject(new PutObjectRequest(bucketName, uploadFile.getName(), uploadFile));
	}

	/**
	 * Demonstrates to get object(files) list from bucket on aws s3
	 */
	@Override
	public List<S3ObjectSummary> getObjectList(String bucketName) {
		ListObjectsV2Result result = s3Client.listObjectsV2(bucketName);
		List<S3ObjectSummary> objects = result.getObjectSummaries();

		for (S3ObjectSummary s3ObjectSummary : objects) {
			LOGGER.info("* " + s3ObjectSummary.getKey());
		}
		return objects;
	}
}
