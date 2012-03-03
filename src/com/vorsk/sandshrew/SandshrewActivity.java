package com.vorsk.sandshrew;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
//import android.widget.ScrollView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SandshrewActivity extends Activity {
	/** Called when the activity is first created. */
	private final String TAG = "Main Activity";
	private final String pleaseSwipe = "swipe your card";
	private final String ageTitle = "age:";
	private final String tryAgain = "try again!";
	private final String ohNo = "Oh no you dint.";
	private final String expired = "ID HAS EXPIRED";
	private TextView title;
	private TextView textAge;
	private TextView status;
	private TextView ageMsg;
	private TextView expire;
	private Typeface fontPoke;
	private Typeface fontGangsta;
	private Typeface fontGent;
	//private ImageView imageView;
	//private ScrollView scroll;
	private DecodeListener decoder;
	private HeadsetStateReceiver receiver;
	private Paint circleColor;
	public static enum CircleColor {
		CIRCLE_GREEN, CIRCLE_YELLOW, CIRCLE_RED
	}
	
	private boolean soundEnabled = true; //enabled by default
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		fontPoke = Typeface.createFromAsset(getAssets(), "fonts/pokemonsolid.ttf");
		fontGangsta = Typeface.createFromAsset(getAssets(), "fonts/grandstylus.ttf");
		fontGent = Typeface.createFromAsset(getAssets(), "fonts/jfont.ttf");
		
		textAge = (TextView) findViewById(R.id.ageNumber);
		status = (TextView) findViewById(R.id.statusMsg);
		title = (TextView) findViewById(R.id.theTitle);
		ageMsg = (TextView) findViewById(R.id.theAge);
		expire = (TextView) findViewById(R.id.expireMsg);
		
		title.setTypeface(fontPoke);
		status.setTypeface(fontPoke);
		ageMsg.setTypeface(fontPoke);
		ageMsg.setText(pleaseSwipe);
	
		this.updateCircle(CircleColor.CIRCLE_YELLOW);
		
		//Log.v(TAG,"Create ready!");
//		Sounds helper = new Sounds(this);
//		helper.playSexy();

	}
	
	public void onResume(){
		super.onResume();
		//Log.v(TAG,"Resume");
		//this.sendStopBroadcast();
		
		//detect mag-reader-headset
		IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
		receiver = new HeadsetStateReceiver(this);
		registerReceiver( receiver, receiverFilter );
		
	}
	
	public void updateCircle(CircleColor color){
		Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(b);
		circleColor = new Paint();
		
		switch(color) {
		case CIRCLE_YELLOW :
			ageMsg.setText(pleaseSwipe);
			circleColor.setColor(Color.YELLOW);
			break;
		case CIRCLE_GREEN :
			ageMsg.setText(ageTitle);
			circleColor.setColor(Color.GREEN);
			break;
		case CIRCLE_RED :
			if(status.getText().equals("Valid Swipe!")){
				ageMsg.setText(ohNo);
			} else {
				ageMsg.setText(tryAgain);
			}
			circleColor.setColor(Color.RED);
			break;
		}
		ImageView imageView = (ImageView) findViewById(R.id.lightImage);
		canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, canvas.getHeight()/2 - 5, circleColor);
		circleColor.setARGB(255, 0, 0, 0);
		circleColor.setAntiAlias(true);
		circleColor.setStyle(Style.STROKE);
		circleColor.setStrokeWidth(5);
		canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, canvas.getHeight()/2 - 5, circleColor);
		imageView.setImageBitmap(b);
	}
	
	public void flashGreen(){
		new FlashCircle().execute(CircleColor.CIRCLE_GREEN);
	}
	
	public void flashRed(){
		new FlashCircle().execute(CircleColor.CIRCLE_RED);
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
	
	public void setAge(String s){
		//text.append(s+"\n");
		ageMsg.setText(ageTitle);
		textAge.setText(s);
		//scroll.scrollTo(0, text.getHeight());
	}
	
	public void setStatus(String s){
		//text.append(s+"\n");
		status.setText(s);
		//scroll.scrollTo(0, text.getHeight());
	}
	
	public void setExpire(){
		expire.setText(expired);
	}
	
	public void clearExpire(){
		expire.setText("");
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
//		case R.id.menu_sound:
//			this.soundEnabled = !this.soundEnabled;
//			//refresh the networks (the easy way)
//			//this.status.setText("Ready!\n");
//			return true;
		case R.id.menu_age:
			//age intent popup
			this.setAgePopup();			
			return true;
		case R.id.menu_theme:
			setTheme();
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
    /*
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
    }*/
    
    private void setTheme(){
    	final CharSequence[] items = {"Pokemon", "Gangster", "Gentleman"};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Pick a Theme");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        switch(item) {
    	        case 0:
    	        	setPokemonTheme();
    	        	break;
    	        case 1:
    	        	setGangsterTheme();
    	        	break;
    	        case 2:
    	        	setGentlemanTheme();
    	        }
    	    }

    	});
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
    private void setGangsterTheme(){
		title.setTypeface(fontGangsta);
		title.setTextSize(65);
		status.setTypeface(fontGangsta);
		status.setTextSize(40);
		ageMsg.setTypeface(fontGangsta);
		ageMsg.setTextSize(40);
	}

	private void setPokemonTheme(){
		title.setTypeface(fontPoke);
		title.setTextSize(55);
		status.setTypeface(fontPoke);
		status.setTextSize(30);
		ageMsg.setTypeface(fontPoke);
		ageMsg.setTextSize(30);
	}
	
	private void setGentlemanTheme(){
		title.setTypeface(fontGent);
		title.setTextSize(65);
		status.setTypeface(fontGent);
		status.setTextSize(40);
		ageMsg.setTypeface(fontGent);
		ageMsg.setTextSize(40);

	}
    
    private void setAgePopup(){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("Set Age");
    	alert.setMessage("Minimum age required:");

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(this);
    	input.setInputType(InputType.TYPE_CLASS_NUMBER);
    	input.setText(""+AgeChecker.getLeagalAge());
    	alert.setView(input);

    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    	  Editable value = input.getText();
    	  Log.i(TAG,"Got: "+value);
    	  // Do something with value!
    	  AgeChecker.setLegalAge(Integer.parseInt(value.toString()));

    	  }
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    	    // Canceled.
    	  }
    	});

    	alert.show();
    	// see http://androidsnippets.com/prompt-user-input-with-an-alertdialog
    }
    
    
    private class FlashCircle extends AsyncTask<CircleColor, CircleColor, Integer> {
        protected Integer doInBackground(CircleColor... i) {
        	publishProgress(i);
        	Log.e("flashThread","thrad running");
        	//wait for a while
        	try {
				Thread.sleep(2300L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//whatever
			}
        	Log.e("flashThread","thrad done");
        	
            return new Integer(1);
        }

        protected void onProgressUpdate(CircleColor... i) {
        	Log.e("flashThread","color time!");
        	updateCircle(i[0]);;
        }

        protected void onPostExecute(Integer i) {
        	Log.e("flashThread","yellow");
        	textAge.setText("");
        	status.setText("Ready!");
        	clearExpire();
        	updateCircle(CircleColor.CIRCLE_YELLOW);
        }
    }

    public void birthdayPopup(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setIcon(R.drawable.bdaycupcake);
    	builder.setMessage("Happy Birthday! *<:~)")
    			.setTitle("Found a Birthday!")
    	       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
    }

}