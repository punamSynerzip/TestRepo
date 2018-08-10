package com.transcript.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.transcript.service.AudioTranscriptService;
import com.transcript.service.VideoConversionService;

/**
 * @author synerzip:punam
 *
 */

@RestController
@RequestMapping("api")
public class TranscriptController {

	@Autowired
	VideoConversionService videoConversionService;

	@Autowired
	AudioTranscriptService audioTranscriptService;

	@RequestMapping(value = "/transcribe/video", method = RequestMethod.GET)
	public ResponseEntity<String> videoConversion(@RequestParam String videofile) {
		String responseString = null;

		responseString = videoConversionService.videoToAudioConvertion(videofile);

		if (responseString == null) {
			return new ResponseEntity<>(responseString, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(responseString, HttpStatus.OK);
	}

	@RequestMapping(value = "/transcribe/audio", method = RequestMethod.POST)
	public ResponseEntity<String> audioConversion(@RequestParam String audiofile) {
		boolean success = false;

		success = audioTranscriptService.audioToTranscriptConversion(audiofile);

		if (success) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
}