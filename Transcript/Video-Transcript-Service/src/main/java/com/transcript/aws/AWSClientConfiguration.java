package com.transcript.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AWSClientConfiguration {

	@Bean
	@ConditionalOnProperty("aws.access-key-id")
	AmazonS3 createAmazonS3Client(@Value("${aws.access-key-id}") String accessKeyId,
			@Value("${aws.secret-access-key}") String secretAccessKey, @Value("${aws.region}") String region) {

		BasicAWSCredentials awsCred = new BasicAWSCredentials(accessKeyId, secretAccessKey);

		return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCred))
				.withRegion(region).build();
	}
}
