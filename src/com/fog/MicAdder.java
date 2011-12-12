package com.fog;

import java.io.IOException;

import android.media.MediaRecorder;

public class MicAdder {
	private MediaRecorder recorder;
	
	public MicAdder()
	{
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile("/dev/null"); 
        
		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public int getAmplitude() {
		return recorder.getMaxAmplitude();
	}
}
