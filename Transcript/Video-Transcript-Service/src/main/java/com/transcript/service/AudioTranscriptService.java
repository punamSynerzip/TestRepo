package com.transcript.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.transcript.audio.AudioTranscript;
import com.transcript.aws.S3BucketOperations;
import com.transcript.gs.GSBucketOperations;
import com.transcript.utils.CommonUtils;

/**
 * @author synerzip:punam
 *
 */

@Service
public class AudioTranscriptService {
	private static final Logger LOGGER = LogManager.getLogger(AudioTranscriptService.class);

	@Value("${google.storage.base.uri}")
	private String gsBaseUri;

	@Value("${s3.bucket.transcript}")
	private String s3TranscriptBucket;

	@Value("${gs.bucket.audios}")
	private String gsBucket;

	@Value("${temp.audio.path}")
	private String tempAudioPath;

	@Value("${temp.transcript.path}")
	private String transcriptFilesPath;

	@Autowired
	AudioTranscript audioTranscript;

	@Autowired
	GSBucketOperations gsBucketOperations;

	@Autowired
	S3BucketOperations s3BucketOperations;

	public boolean audioToTranscriptConversion(String audioFilePath) {
		String transcriptFile = null;
		boolean successStatusResponse = false;

		try {
			gsBucketOperations.uploadObjectToGS(audioFilePath);

			LOGGER.info("Started transcript generation for: " + new File(audioFilePath).getName());

			try {
				transcriptFile = audioTranscript.asyncRecognizeGcs(gsBaseUri + Paths.get(audioFilePath).getFileName());
			} catch (Exception e) {
				LOGGER.error("Exception occurred while transcript generation " + e);
			}
			if (transcriptFile != null) {
				s3BucketOperations.uploadObject(s3TranscriptBucket, transcriptFilesPath + transcriptFile);
				LOGGER.info("Transcript files uploaded to aws s3 successfully");
				successStatusResponse = true;
				cleanUpProcess(Paths.get(audioFilePath).getFileName().toString(), transcriptFile);
			}

		} catch (IOException e) {

			LOGGER.error("Exception occurred while uploading audio files to Google cloud storage " + e);
		} catch (AmazonClientException e) {

			LOGGER.error("Exception occurred while uploading transcript to s3 " + e);
		}
		return successStatusResponse;
	}

	public void cleanUpProcess(String gsAudioFile, String tempTranscriptFile) {
		try {
			gsBucketOperations.deleteObject(gsAudioFile, gsBucket);
			LOGGER.info("Audio files removed successfully from google storage");
		} catch (IOException e) {
			LOGGER.error("Exception occurred while deleting audio from google storage " + e);
		}
		CommonUtils.deleteOnExitTempFile(transcriptFilesPath + tempTranscriptFile);
		if (new File(tempAudioPath + gsAudioFile).exists()) {
			CommonUtils.deleteOnExitTempFile(tempAudioPath + gsAudioFile);
		}
	}
}
