package com.vorsk.sandshrew;
import android.app.Activity;
import android.content.Context;
import android.media.SoundPool;
import android.media.MediaPlayer;

public class Sounds 
{
	private Context context;
	MediaPlayer sexyTwentyOne;
	public Sounds()
	{
		sexyTwentyOne = MediaPlayer.create(context, R.raw.careless_whisper);
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
