package com.transcript.gs;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1p1beta1.SpeechSettings;
import com.transcript.audio.AudioTranscript;

@Configuration
public class GSConfiguration {

	@Value("${google.storage.keys.path}")
	String credentials;

	@Bean
	@ConditionalOnClass(AudioTranscript.class)
	SpeechSettings buildSpeechService() throws IOException {
		List<String> scopes = new ArrayList<>();
		scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);

		CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(
				ServiceAccountCredentials.fromStream(getClass().getClassLoader().getResourceAsStream(credentials)));

		return SpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
	}

	@Bean
	@ConditionalOnClass(GSBucketOperations.class)
	Storage buildService() throws IOException, GeneralSecurityException {
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		List<String> scopes = new ArrayList<>();
		scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);

		GoogleCredential credential = GoogleCredential
				.fromStream(getClass().getClassLoader().getResourceAsStream(credentials)).createScoped(scopes);

		return new Storage.Builder(httpTransport, jsonFactory, credential).setApplicationName("SpeechTrancript")
				.build();
	}

}