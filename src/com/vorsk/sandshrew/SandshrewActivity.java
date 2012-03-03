package com.vorsk.sandshrew;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

public class SandshrewActivity extends Activity {
	/** Called when the activity is first created. */
	private final String TAG = "Main Activity";
	private TextView text;
	private TextView status;
	private ScrollView scroll;
	private DecodeListener decoder;
	private HeadsetStateReceiver receiver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		text = (TextView) findViewById(R.id.ageNumber);
		status = (TextView) findViewById(R.id.statusMsg);
		
		//Log.v(TAG,"Create ready!");

	}
	
	public void onResume(){
		super.onResume();
		//Log.v(TAG,"Resume");
		this.sendStopBroadcast();
		
		//detect mag-reader-headset
		IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
		receiver = new HeadsetStateReceiver(this);
		registerReceiver( receiver, receiverFilter );
		
	}
	
	public void onPause(){
		super.onPause();
		//Unregister the receiver when our activity goes away
		unregisterReceiver(receiver);
	}
	
	public void runThread() {
		stopThread(); //just in case
		decoder = new DecodeListener(this);
		decoder.execute(new Integer(0));
	}
	
	public void stopThread(){
		if (decoder != null){
			decoder.stop();
		}
	}
	
	public void addToGUI(String s){
		text.append(s+"\n");
		scroll.scrollTo(0, text.getHeight());
	}
	
	
	//make the menu work
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
    
    //when a user selects a menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()) {
		case R.id.menu_clear:
			//refresh the networks (the easy way)
			this.status.setText("Ready!\n");
			return true;
		default:
			return false;
		}
    }
    
    public void onStop(){
    	super.onStop();
    	Log.v(TAG,"stopping thread");
    	stopThread();
    }
    
    /** Stops the background media player if it is playing
     * For the most part this code was *stolen* from the sleepTimer app :P
     * */
    @SuppressWarnings("unused")
	private void sendStopBroadcast()
    {
      Intent localIntent1 = new Intent("android.intent.action.MEDIA_BUTTON", null);
      long l1 = SystemClock.uptimeMillis();
      long l2 = SystemClock.uptimeMillis();
      KeyEvent localKeyEvent1 = new KeyEvent(l1, l2, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_STOP, 0);
      Intent localIntent2 = localIntent1.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent1);
      this.sendOrderedBroadcast(localIntent1, null);
      try
      {
        Thread.sleep(100L);
        Intent localIntent3 = new Intent("android.intent.action.MEDIA_BUTTON", null);
        long l3 = SystemClock.uptimeMillis();
        long l4 = SystemClock.uptimeMillis();
        KeyEvent localKeyEvent2 = new KeyEvent(l3, l4, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_STOP, 0);
        Intent localIntent4 = localIntent3.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent2);
        this.sendOrderedBroadcast(localIntent3, null);
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
    	  Log.e(TAG,"error with the pause thread");
      }
    }

}