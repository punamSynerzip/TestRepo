package com.transcript.video;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.transcript.utils.CommonUtils;

/**
 * @author synerzip:punam
 * 
 */

@Service
public class VideoConverter {
	private static final Logger LOGGER = LogManager.getLogger(VideoConverter.class);

	@Value("${temp.audio.path}")
	private String tempAudioPath;

	/**
	 * Demonstrates using the FFMPEG to convert Video files from s3 to Audio.
	 * 
	 * @param tempAudioPath
	 * @throws InterruptedException
	 */
	public String videoToAudioConversion(String mp4FileName) throws IOException, InterruptedException {

		String mp3File = tempAudioPath + CommonUtils.getActuallFileName(mp4FileName) + ".wav";

		String cmd = "ffmpeg -i " + mp4FileName + " -ab 160k -ac 1 -ar 16000 -vn " + mp3File;

		LOGGER.info("Strated video conversion for: " + CommonUtils.getActuallFileName(mp4FileName));

		Process p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		return mp3File;
	}
}
