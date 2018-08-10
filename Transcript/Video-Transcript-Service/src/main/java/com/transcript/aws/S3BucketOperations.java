package com.transcript.aws;

import java.util.List;

import com.amazonaws.services.s3.model.S3ObjectSummary;

public interface S3BucketOperations {

	/**
	 * @param bucket_name
	 * @param filesPath
	 */
	void uploadObject(String bucketName, String filesPath);

	/**
	 * @param bucket_name
	 */
	List<S3ObjectSummary> getObjectList(String bucketName);

}
