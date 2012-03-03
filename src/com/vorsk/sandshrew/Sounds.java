package com.vorsk.sandshrew;


import android.app.Activity;
import android.content.Context;
import android.media.SoundPool;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;


public class Sounds 
{
	//private Context context;
	MediaPlayer sexyTwentyOne;
	AudioManager audioManager; //= (AudioManager)Context.getSystemService(Context.AUDIO_SERVICE);
	public Sounds(Context inContext)
	{
		sexyTwentyOne = MediaPlayer.create(inContext, R.raw.careless_whisper);
		//sexyTwentyOne.setAudioStreamType(AudioManager.STREAM_SYSTEM);
		
		audioManager = (AudioManager)inContext.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setMode(AudioManager.MODE_IN_CALL);
		//audioManager.setMode(AudioManager.MODE_NORMAL);
		audioManager.setSpeakerphoneOn(true);
		audioManager.setWiredHeadsetOn(false);
		audioManager.setMicrophoneMute(true);
		//audioManager.setRouting(AudioManager.MODE_CURRENT, AudioManager.ROUTE_SPEAKER, AudioManager.ROUTE_ALL);  
		audioManager.setMode(AudioManager.MODE_CURRENT);
		//audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		Log.e("Soudnds",""+audioManager.isWiredHeadsetOn());
	}
	public void playSexy()
	{
		sexyTwentyOne.start();
	}
	public void stopSexy()
	{
		sexyTwentyOne.stop();
	}
	public void sexyKill()
	{
		sexyTwentyOne.release();
	}
}
