package com.vorsk.sandshrew;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import com.vorsk.sandshrew.PCMLinkedList.PCMNode;
//import my individual node object

import android.util.Log;

/**
 * Class to assist in the decoding of Magstripe data
 * @author Ian Foster
 */
class MagDecoder{
	// hard coded constants
	private final String TAG = "Mag Decoder";
	private static final short NOISE = 900;
	private boolean goodRead = false;
	
	//TODO the following vars should be in a card type object, for track 1
	private final static int binSize = 5;
	private final static int charOffset = 48;
	private final static char end = '?';
	
	private static final char badChar = '|';


	//TODO retire this method
	/**
	 * Main method for testing, it should be retired
	 * @param pcmList the list of PCM data to read from
	 * @return the ASCII String from the peak list
	 */
	public String processCard(PCMLinkedList pcmList) {
		//init here...
		this.goodRead = false;

		//get the peaks from the PCM data
		LinkedList<Integer> peakList = findPeaks(pcmList);

		//if the data is too small, return in error
		if (peakList.size() < 50) {
			Log.d(TAG, "invalid swipe; too short");
			this.goodRead = false; //setting again just in case
			//TODO do something better than return null
			return null;
		}

		Log.v(TAG, "Looks like a good card");
		this.goodRead = true; // believe first

		//the first peak is always useless
		peakList.removeFirst();

		// convert the peaks to binary
		CharLinkedList binaryList = peaksToBinary(peakList);
		
		//testing
		//Log.d("magDecode?bin", binaryList.toString());

		//convert the binary to a string
		String str = decodeBinary(binaryList);

		//do a quick check on the validly of the String
		if (!this.goodRead) {
			//the reverse is kinda hackish
			//TODO find a better way to real with reverse
			Log.i(TAG, "decode invalid; atempting reverse");
			binaryList.reverse();
			str = decodeBinary(binaryList);
			if (!this.goodRead){
				Log.i(TAG, "reverse did not help");
			}
		}
		
		//fix returning null reads
		if (str == null){
			this.goodRead = false;
		}
		
		return str;
	}
	
	/**
	 * Returns the validly of the processed string
	 * @return the result
	 */
	public boolean isValid(){
		return this.goodRead;
	}

	/**
	 * Decodes the card binary to an ASCII String
	 * @param binaryList the binary to decode
	 * @return the ASCII string resulting from the binary
	 */
	private String decodeBinary(CharLinkedList binaryList) {
		this.goodRead = true; //reset to be nice

		//variable to hold our calculated LRC
		boolean[] calcLRC = new boolean[binSize];
		//variable to hold read LRC
		char[] readLRC = new char[binSize];
		//TODO put both LRC values into nice data structure for comparing?

		//variable to hold the current char
		char current;

		// skip over prepended 0s and find where the data starts
		int offset = binaryList.find('1');
		//output variable to build our ASCII string
		StringBuilder out = new StringBuilder();

		//while we still have binary data to convert
		while ((offset + binSize) < binaryList.size()) {
			//get the next char
			current = binToChar(binaryList.getSub(offset, binSize), charOffset,calcLRC);
			//add the char to our string builder
			out.append(current);
			//check for the end sentinel
			if (current == end && out.length() > 3) { //should fix the leading 0s problem
				// we have reached the end; read the LRC and break!
				readLRC = binaryList.getSub(offset + binSize, binSize);
				break;
				//offset = binaryList.size(); // breaks the while loop
			}

			//increment offset
			offset += binSize;
		}

		// LRC checking
		if (!Arrays.equals(readLRC, finalizeLRC(calcLRC))) {
			// LRC is invalid
			this.goodRead = false;
		}

		return out.toString();
	}

	/**
	 * Convert a boolean array to char
	 * @param boolArray boolean array
	 * @return char array
	 */
	private char[] boolArrayToChar(boolean[] boolArray) {
		char[] charArray = new char[boolArray.length];

		for (int i = 0; i < boolArray.length; i++) {
			if (boolArray[i]) {
				charArray[i] = '1';
			} else {
				charArray[i] = '0';
			}
		}

		return charArray;
	}

	
	/**
	 * Add the parity bit to the LRC
	 * @param calcLRC the current LRC
	 * @return a char array of the finalized calculated LRC
	 */
	private char[] finalizeLRC(boolean[] calcLRC) {
		int count = 0;
		// count the 1s
		for (int i = 0; i < calcLRC.length - 1; i++) {
			if (calcLRC[i]) {
				count++;
			}
		}
		//set the last parity element
		calcLRC[calcLRC.length - 1] = (count % 2 != 1);
		//convert from boolean values to char 
		return boolArrayToChar(calcLRC);

	}

	
	/**
	 * Determine if the binary sequence has a valid parity bit
	 * @param bin the binary sequence
	 * @return
	 */
	private boolean validChar(char[] bin) {
		int count = 0;
		for (char ch : bin) {
			if (ch == '1') {
				count++;
			}
		}
		 // is count odd?
		return (count % 2 == 1);
	}

	
	/**
	 * Determine the ASCII char represented by the binary
	 * @param binary the binary sequence to decode
	 * @param offset the ASCII offset to use for decoding
	 * @param lrc the LRC array to update with the given bit
	 * @return
	 */
	private char binToChar(char[] binary, int offset, boolean[] lrc) {
		//check for valid paridy bit
		if (!validChar(binary)) {
			this.goodRead = false;
			return badChar;
		}
		
		//update the LRC
		for (int i = 0; i < binary.length; i++) {
			if (binary[i] == '1') { //found a 1
				lrc[i] = !lrc[i]; //flip the bit
			}
		}
		
		//our binary is backwards
		binary = reverseArray(removelast(binary));
		// Return the char from the parsed binary
		return (char) (Integer.parseInt(new String(binary), 2) + offset);
	}

	/**
	 * Returns a char array with the last element removed
	 * @param array input array
	 * @return output array
	 */
	private char[] removelast(char[] array) {
		char[] out = new char[array.length - 1];
		for (int i = 0; i < out.length; i++) {
			out[i] = array[i];
		}
		return out;
	}

	/**
	 * Reverse a char array in place
	 * @param array the array to reverse
	 * @return the reversed array; however the argument pointer will work too
	 */
	private char[] reverseArray(char[] array) {
		char temp;
		for (int i = 0; i < array.length; i += 2) {
			temp = array[i];
			array[i] = array[array.length-1-i];
			array[array.length-1-i] = temp;
		}
		return array;
	}

	//TODO should return a better data type, possibly byte[] if I can find a good size
	/**
	 * Decode peaks to binary
	 * @param peakList the peaks to decode
	 * @return linked list of 1s and 0s
	 */
	private CharLinkedList peaksToBinary(LinkedList<Integer> peakList) {
		//create the output variable
		CharLinkedList binaryList = new CharLinkedList();
		//Initialize the oneClock variable for this swipe 
		oneClock clock = new oneClock((peakList.get(2) - peakList.get(1)) / 2); // (3rd - 2nd) / 2
		
		int lastPeakIdx = peakList.getFirst();
		//variable to hold the current bit we are going to examine
		char currentBit;

		char lastBit = '\0'; //null char

		Iterator<Integer> it = peakList.iterator();
		while (it.hasNext()){
			int currentPeakIdx = it.next();
		
			// ignoring the MAX_BITSTREAM_LEN (=1024), cuz I don't think I need it
			//get the current bit
			currentBit = peakDiffToBin(clock, currentPeakIdx - lastPeakIdx);
			if (currentBit == '0') {
				//if the current bit is a 0; all is good; add it
				binaryList.add(currentBit);
			} else if (currentBit == lastBit) {
				//if not; then if we got two if the same bits, just add one
				//we do this because there will be double the amount of 1 bits because they are 1/2 the size of a 0
				// logic is a little fuzzy
				binaryList.add(currentBit);
				currentBit = '\0'; //reset
			}
			//increment my last bit
			//TODO make a nice data structure for all this, so that I don't need the next 2 lines
			lastBit = currentBit;
			lastPeakIdx = currentPeakIdx;
		}
		//return the finished product
		return binaryList;
	}

	
	/**
	 * Determines if the difference between two peaks is a 1 or 0
	 * @param diff the difference between the two peaks
	 * @return char 1 or 0
	 */
	private char peakDiffToBin(oneClock clock, int diff) {
		//the diff which represents a 1 or 0
		int oneDif = Math.abs(clock.current - diff);
		int zeroDif = Math.abs((clock.current * 2) - diff);

		//if valid; we have a 1
		if (oneDif < zeroDif) {
			//update oneClock for timing
			clock.setClock(diff);
			return '1';
		} else { // got a 0
			//update oneClock for timing
			clock.setClock(diff/2);
			return '0';
		}
	}

	/**
	 * Finds and returns the peaks in the passed PCM data
	 * @param pcmData a List of PCM peaks
	 * @return A linked list of peak values
	 */
	private LinkedList<Integer> findPeaks(PCMLinkedList pcmData) {
		//create output variable
		LinkedList<Integer> peakList = new LinkedList<Integer>();
		//start at the first peak
		PCMNode currentPeak = pcmData.first();
		//are we starting on a positive bit
		boolean positive = (currentPeak.amp > 0);

		//initially set to the current peak
		PCMNode largestPeak = currentPeak;

		while (currentPeak != null) {
			//is the current PCM short not noise?
			if (isOutsideThreshold(currentPeak.amp)) {
				//are we still working on the new peak?
				if ((currentPeak.amp > 0) == positive) {
					//compare the peaks
					if (Math.abs(currentPeak.amp) > Math.abs(largestPeak.amp)) {
						largestPeak = currentPeak;
					}
				} else { //new peak, stop the old
					//the next peak must be on the other side of 0
					positive = !positive;
					//add the largest previous peak
					peakList.add(largestPeak.idx);
					//reset the current largest beck to the current peak
					largestPeak = currentPeak.next;
				}
			}
			// transverse the linked list
			currentPeak = currentPeak.next; 
		}
		//done; return my list
		return peakList;
	}

	/**
	 * Method to determine if the PCM short is of any use
	 * @param s the short to check
	 * @return true if the short is outside the noise threshold; else false
	 */
	public static boolean isOutsideThreshold(int s) {
		if (Math.abs(s) > NOISE) {
			return true;
		}
		return false;
	}
	
	/**
	 * Class instance variable used to store the oneClock
	 * Has the ability to average the past few ones for greater accuracy
	 */
	protected class oneClock{
		public int current;
		private int previous;
		public oneClock(int clock){
			current = clock;
			previous = clock;
		}
		public void setClock(int clock){
			previous = current;
			current = (clock+current+previous)/3;
		}
	}


}