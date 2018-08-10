package com.transcript.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonUtils {

	private CommonUtils() {
	}

	private static final Logger LOGGER = LogManager.getLogger(CommonUtils.class);

	public static String getActuallFileName(String file) {
		Path filePath = Paths.get(file);
		String fileName = filePath.getFileName().toString();
		int pos = fileName.lastIndexOf('.');
		return fileName.substring(0, pos);
	}

	public static void deleteOnExitTempFile(String file) {

		Path tempFilePath = Paths.get(file);
		try {
			Files.delete(tempFilePath);
			LOGGER.info("Temporary file:" + tempFilePath + " removed successfully\n");
		} catch (IOException e) {
			LOGGER.error("Exception occurred while deleting temporary file:" + e);
		}
	}
}
