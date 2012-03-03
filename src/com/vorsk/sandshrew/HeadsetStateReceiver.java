package com.vorsk.sandshrew;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class HeadsetStateReceiver extends BroadcastReceiver {
	
	private SandshrewActivity activity;
	
	public HeadsetStateReceiver(SandshrewActivity activity){
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle extras = intent.getExtras();
		int state = extras.getInt("state");
		int mic = extras.getInt("microphone");
		
		//we have a headset with mic
		if (state == 1 && mic == 1){
			//stop the thread just to be safe, then start a new instance
			activity.runThread();
			//update status message
			activity.setStatus("Ready!");
			
		}else if (state == 1 && mic == 0){
			//headphones are plugged in, not a reader (or mic)
			//update status to insert reader, like below
			
		}else{
			//stop the thread
			activity.stopThread();
			//update status message
			activity.setStatus("Reader Unpluged");
			activity.updateCircle(false);
		}
	}

}
