package com.transcript.service;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.transcript.video.VideoConverter;

/**
 * @author synerzip:punam
 *
 */

@Service
public class VideoConversionService {
	private static final Logger LOGGER = LogManager.getLogger(VideoConversionService.class);

	@Autowired
	VideoConverter videoConverter;

	public String videoToAudioConvertion(String videoFilePath) {
		String audioFile = null;

		try {
			audioFile = videoConverter.videoToAudioConversion(videoFilePath);
			LOGGER.info("Video file converted scceefully");
		} catch (IOException | InterruptedException e) {
			LOGGER.error("Exception occured while video to audio conversion", e);
		}
		return audioFile;
	}
}
