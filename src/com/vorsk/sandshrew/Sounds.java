package com.vorsk.sandshrew;
import android.app.Activity;
import android.content.Context;
import android.media.SoundPool;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.os.Bundle;


public class Sounds 
{
	private Context context;
	MediaPlayer sexyTwentyOne;
	AudioManager audioManager; //= (AudioManager)Context.getSystemService(Context.AUDIO_SERVICE);
	public Sounds(Context inContext)
	{
		sexyTwentyOne = MediaPlayer.create(inContext, R.raw.careless_whisper);
		audioManager = (AudioManager)inContext.getSystemService(Context.AUDIO_SERVICE);
		//audioManager.setMode(AudioManager.MODE_IN_CALL);
		//audioManager.setSpeakerphoneOn(true);
		if(audioManager.isWiredHeadsetOn())
		{
			System.out.println(audioManager.isWiredHeadsetOn());
		    audioManager.setWiredHeadsetOn(false);
		    audioManager.setSpeakerphoneOn(true); 
		    audioManager.setRouting(AudioManager.MODE_CURRENT, AudioManager.ROUTE_SPEAKER, AudioManager.ROUTE_ALL);  
		    audioManager.setMode(AudioManager.MODE_CURRENT); 
		}
	}
	public void playSexy()
	{
		sexyTwentyOne.start();
	}
	public void stopSexy()
	{
		sexyTwentyOne.stop();
	}
}
