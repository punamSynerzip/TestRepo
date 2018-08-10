/**
 * 
 */
package com.transcript.audio;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1p1beta1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1p1beta1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1p1beta1.RecognitionAudio;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult;
import com.google.cloud.speech.v1p1beta1.SpeechSettings;
import com.google.cloud.speech.v1p1beta1.WordInfo;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
import com.transcript.utils.CommonUtils;

/**
 * @author synerzip:punam
 *
 */

@Service
public class AudioTranscript {
	private static final Logger LOGGER = LogManager.getLogger(AudioTranscript.class);

	@Value("${temp.transcript.path}")
	private String transcriptFilesPath;

	@Autowired
	SpeechSettings speechSettings;

	ObjectMapper mapper = new ObjectMapper();

	/**
	 * Demonstrates using the Speech API to transcribe an audio file.
	 * 
	 * @throws IOException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	public String asyncRecognizeGcs(String gsUri) throws IOException, InterruptedException, ExecutionException {
		String transcriptFile = null;

		try (SpeechClient speech = SpeechClient.create(speechSettings)) {

			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16)
					.setLanguageCode("en-US").setSampleRateHertz(16000).setEnableWordTimeOffsets(true).build();
			RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(gsUri).build();

			OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response = speech
					.longRunningRecognizeAsync(config, audio);

			while (!response.isDone()) {
				LOGGER.info("Waiting for response...");
				Thread.sleep(10000);
			}

			List<SpeechRecognitionResult> results = response.get().getResultsList();
			JSONArray transcriptArray = new JSONArray();

			for (SpeechRecognitionResult result : results) {

				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);

				List<WordInfo> wordInfo = alternative.getWordsList();

				String transcriptStatement = alternative.getTranscript();

				JSONObject startTime = new JSONObject();
				JSONObject endTime = new JSONObject();

				JSONObject alternativesJson = new JSONObject();
				JSONObject jsonObject = new JSONObject();

				startTime.put(TranscriptConstants.SECONDS, wordInfo.get(0).getStartTime().getSeconds());
				startTime.put(TranscriptConstants.NANOS, wordInfo.get(0).getStartTime().getNanos());

				endTime.put(TranscriptConstants.SECONDS, wordInfo.get(wordInfo.size() - 1).getEndTime().getSeconds());
				endTime.put(TranscriptConstants.NANOS, wordInfo.get(wordInfo.size() - 1).getEndTime().getNanos());

				jsonObject.put(TranscriptConstants.FILENAME,
						CommonUtils.getActuallFileName(gsUri) + TranscriptConstants.VIDEO_FILE_EXTENSION);
				jsonObject.put(TranscriptConstants.TRANSCRIPT, transcriptStatement);
				jsonObject.put(TranscriptConstants.START_TIME, startTime);
				jsonObject.put(TranscriptConstants.END_TIME, endTime);

				alternativesJson.put(TranscriptConstants.ALTERNATIVES, jsonObject);

				transcriptArray.add(alternativesJson);
			}
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			transcriptFile = CommonUtils.getActuallFileName(gsUri) + TranscriptConstants.JSON_FILE_EXTENSION;

			try (FileWriter file = new FileWriter(transcriptFilesPath + transcriptFile)) {
				writer.writeValue(file, transcriptArray);
				LOGGER.info("Trancript file created: " + transcriptFile);
			}
		}
		return transcriptFile;
	}
}
