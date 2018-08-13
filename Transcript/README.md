Synerbot Video Transcript REST Service
--------------------------------------

- Features:
  Can generate transcript for video files
  Can generate transcript for audio files


- Video Transcript flow:

Step1- Pass videofile as a request parameter to the url
       http://hostname:port/api/transcribe/video

This will generate audio file using FFmpeg library and returns   
response as string for audiofile.
	
    E.g. http://localhost:8080/api/transcribe/video?videofile=videos/test.mp4
	
    Where,
    http://localhost:8080/api/transcribe/video : is api base url,

    videofile : is a request parameter name for video file path,

    videos/test.mp4 : path to the video file where it is located.



Step2- Pass the audiofile as a request parameter to the url
---------------------------
http://hostname:port/api/transcribe/audio
--------------------------------------------
The transcript .json file get generated for the audio file with
the help of Google cloud Speech-To-Text Api.
And the transcript file get uploaded to the s3 storage bucket.

    E.g.
    http://localhost:8080/api/transcribe/audio?audiofile=/var/tmp/test.wav

    Where,
    http://localhost:8080/api/transcribe/audio : is api base url,

    audiofile : is a request parameter name for audio file path,

    /var/tmp/test.wav : path to the audio file where it is located.
	



- Audio Transcript flow:
Follow step 2 from Video transcript process.

Transcript.json file format:  

    [ {
      "alternatives" : {
      "fileName" : "test11.mp4",
      "transcript" : "today we will meet Thomas and Sarah",
      "startTime" : {
        "seconds" : 0,
        "nanos" : 0
      },
      "endTime" : {
        "seconds" : 4,
        "nanos" : 500000000
      }
      }
      }, {
      "alternatives" : {
      "fileName" : "test11.mp4",
      "transcript" : " I am Thomas",
      "startTime" : {
      "seconds" : 5,
      "nanos" : 700000000
      },
      "endTime" : {
        "seconds" : 7,
        "nanos" : 200000000
      }
      }
    }, ......
    ]

- Api dependency:
    	FFmpeg library
    	Google cloud Speech-To-Text Api
    	Google cloud project private key (.json file): set as environment variable GOOGLE_APPLICATION_CREDENTIALS
    	Aws s3 credentials to read files from s3 and/or to upload generated transcript files to s3 bucket.


- To Run This Application Follow The Steps
    
      git clone repository_url
      cd path/to/Video-Transcript-Service
      mvn clean install
      run as spring boot application or 
      cd target
       java -jar  Video-Transcript-Service.jar
