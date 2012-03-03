package com.vorsk.sandshrew;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Thread which listens on the MIC input and passes captured audio to the decoder
 * @author Ian Foster
 */
class DecodeListener extends AsyncTask<Integer, String, Void> {
	private final String TAG = "Decode Listener";
	private final int FREQUENCY = 44100;
	//used to determine is a sound sample was long enough to bother parsing
	private static final int minListLen = 5000;
	//offset to 0, this is a good default value, but I update it so that is is dynamic
	private int offset = 460;
	
	//Gui Var, there should be a better way to do this
	private SandshrewActivity activity;

	//boolean value used to stop the recording if the activity is closed
	private boolean isRecording = true;
	
	//for parsing
	//Parser parser;
	AgeChecker ageChecker;
	
	/** Ctor for the listener
	 * @param callingActivity  the activity to update
	 */
	public DecodeListener(SandshrewActivity callingActivity){
		this.activity = callingActivity;
		this.offset = findZero();
		//Log.v(TAG,"new offset: "+offset);
		
		//parser = new Parser();
		ageChecker = new AgeChecker();
		//ageChecker.calculateLegalYear(22); //makes no sense? TODO
	}

	/**
	 * Main thread method
	 * Listens to the audio input and passes audio data to the Decoder
	 * when it finds any 
	 * @param i not used but here....
	 */
	protected Void doInBackground(Integer... i) {
		//Log.v(TAG, "Thread started");

		// configure stuff
		int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
		int bufferSize = AudioRecord.getMinBufferSize(FREQUENCY, channelConfiguration, audioEncoding);
		AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, FREQUENCY, channelConfiguration,audioEncoding, bufferSize);

		short[] buffer = new short[bufferSize];
		audioRecord.startRecording();

		// A linked-list object to hold our pcm data
		PCMLinkedList pcmList = new PCMLinkedList();

		// variable used to individual PCM data
		short pcmShort;

		// for auto detect; used to determine timestamp of last audio
		long lastFound = 0;
		
		
		//set up the decoder object
		MagDecoder decoder = new MagDecoder();

		while (isRecording) {
			// process new audio data
			int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
			for (int b = 0; b < bufferReadResult; b++) {
				//add the incoming short to the offset
				pcmShort = (short) (buffer[b] + offset);

				//if we detect some non-noise let the timer know
				if (MagDecoder.isOutsideThreshold(pcmShort)) {
					lastFound = System.currentTimeMillis();
				}
				
				//if the timer is counting add the data to the list
				if (lastFound != 0) {
					pcmList.add(pcmShort);
				}
			}

			//if the timer is running and it has been > 1/10th of a second since the last peak
			if (lastFound != 0 && (System.currentTimeMillis() - lastFound) > 100) {
				// make sure that the PCM data is actually of a meaningful length
				if (pcmList.size() > minListLen) {
					// looks like we may have found a card
					audioRecord.stop();
					//Log.v(TAG,"prosessing audio segment");
					
					// reset timer
					lastFound = 0;
					
					//process the PCM data and hope for a card
					String result = decoder.processCard(pcmList);
					if (decoder.isValid() && result != null){
						//publishProgress("Valid Swipe!");
						
						//Log.v(TAG,"LRC PASS");

						//process data
						//publishProgress("age",result);
						this.doAllThethings(result);
						
					}else{
						publishProgress("Invalid Swipe","","false");
						Log.v(TAG,"LRC Fail!!!!!");
					}
					
					//publishProgress("--------------------------------------------------------------");
					audioRecord.startRecording();
				}
				//reset the PCMlist for the next card
				pcmList.clear();
			}

		}

		//we are done recording
		audioRecord.stop();
		//due to the Void (note the capital V) return type
		//the following line must be here.
		return null;
	}
	
	

	
	
	/** listens for a short while and finds what should be the sero offset for the class
	 * @return the new zero offset
	 */
	private int findZero(){
		// configure stuff
		int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
		int bufferSize = AudioRecord.getMinBufferSize(FREQUENCY, channelConfiguration, audioEncoding);
		AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, FREQUENCY, channelConfiguration,audioEncoding, bufferSize);

		short[] buffer = new short[bufferSize];
		audioRecord.startRecording();
		int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
		audioRecord.stop();
		int sum = 0;
		for (int b = 0; b < bufferReadResult; b++) {
			sum += buffer[b];
		}
		if (bufferReadResult == 0){
			return this.offset;
		}
		
		return -1*(sum/bufferReadResult);
	}


	/**
	 * Push a string to the GUI to display
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(String... s) {
		if (s.length >0){
			if (s[0].equals("Birthday")){
				activity.birthdayPopup();
				return;
			}
			activity.setStatus(s[0]);
		}
		if (s.length >1){
			activity.setAge(s[1]);
		}
		if (s.length >2){
			if (s[2].equals("true")){
				activity.updateCircle(true);
			}else{
				activity.updateCircle(false);
			}
		}
		
	}
	
	//not that many things (yet...)
	private void doAllThethings(String result){
		Log.d(TAG, "result: "+result);
		if (!Parser.validID(result)){
			
			publishProgress("Invalid ID","","false");
			return;
		}
		String bday = Parser.getBirthday(result);
		if (bday == null) return;
		Log.d(TAG,"bday: "+bday);
		ageChecker.setBirthday(bday);
		Log.d(TAG,"age: "+ageChecker.getAge());
		publishProgress("Valid Swipe!",""+ageChecker.getAge(),""+ageChecker.isLegal());
		if (ageChecker.isBirthday()){
			publishProgress("Birthday");
		}
	}
	
	/**
	 * Run when the thread completes
	 */
	protected void onPostExecute() {
	}

	
	/**
	 *  Stops the recording, to re-start re-create the object
	 */
	public void stop() {
		isRecording = false;
	}

}