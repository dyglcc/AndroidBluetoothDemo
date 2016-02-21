package com.fairy.mic4pc;

import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class MyAudioRecord {
	public static int bufSize;
	public static int hz;
	public static int bits;
	public static int channel;
	public static byte []     bytes ;
	public static LinkedList<byte[]>  byteList ;
	private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
//	private static int[] mSampleRates = new int[] {  22050,44100 };
	private static  AudioRecord record = null;
	private static MyAudioRecord instance = null;
	public  static MyAudioRecord getInstance(){
		if(instance == null){
			instance = new MyAudioRecord();
		}
		return instance;
	}
	
//	public MyAudioRecord(){
//		super(MediaRecorder.AudioSource.MIC,
//				22050,
//				AudioFormat.CHANNEL_IN_MONO,
//				AudioFormat.ENCODING_PCM_16BIT,
//				640);
//		/*for(int i=22050;i<44102;i++)
//			if (AudioRecord.getMinBufferSize(i,
//				AudioFormat.CHANNEL_IN_MONO,
//                AudioFormat.ENCODING_PCM_16BIT)>640){
//				System.out.println(i-1);
//				break;}*/
//		bytes = new byte [bufSize] ;
//		byteList=new LinkedList<byte[]>();
//
//	}
	public AudioRecord getRecord(){

		if(record!=null){
			return  record;
		}
		for (int rate : mSampleRates) {
			for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
				for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
					try {
						Log.d("micPc", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
								+ channelConfig);
						bufSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

						if (bufSize != AudioRecord.ERROR_BAD_VALUE) {
							// check if we can instantiate and have a success
							AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufSize);

							if (recorder.getState() == AudioRecord.STATE_INITIALIZED){
								bytes = new byte[bufSize];
								hz = rate;
								bits = audioFormat;
								channel = channelConfig;
								return recorder;
							}

						}
					} catch (Exception e) {
						Log.e("micPc", rate + "Exception, keep trying.",e);
					}
				}
			}
		}
		return null;

	}

	public void stop(){
		if(record!=null){
			record.stop();
		}
	}

	public void release() {
		if(record!=null){
			record.release();
			record = null;
		}
	}
}
