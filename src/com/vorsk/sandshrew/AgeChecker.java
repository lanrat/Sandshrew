package com.vorsk.sandshrew;
import java.util.Date;

import android.util.Log;
//class to do age checking
public class AgeChecker
{	
	//instance variables
	static private int month;
	static private int day;
	static private int year;
	static private int legalYear; //this is the important year of legalAge
	static private int legalAge = 21; 
	Date date;
	
	//no argument constructor
	public AgeChecker()
	{
		month = 0;
		year = 0;
		day = 0;
		legalYear = 0;
	}
	
	//constructor to initialize day, month, year, and legalYear
	public AgeChecker(String birthdays)
	{
		setBirthday(birthdays);
	}
	
	//change the birthdays so we don't have to make a new class each time...
	public void setBirthday(String birthdays)
	{
		year = Integer.parseInt(birthdays.substring(0,4));
		legalYear = year + 21;
		
		//checks if the first value of month is 0
		if(birthdays.valueOf(4).equals(0))
		{
			month = Integer.parseInt(birthdays.substring(5,6));
		}
		else
		{
			month = Integer.parseInt(birthdays.substring(4,6));
		}
		
		//checks if the first value of day is 0
		if(birthdays.valueOf(6).equals(0))
		{
			day = Integer.parseInt(birthdays.substring(7,8));
		}
		else
		{
			day = Integer.parseInt(birthdays.substring(6,8));
		}
	}
	
	//private method to setLegalAge, if the default 21 is not desired
	static public void setLegalAge(int i)
	{
		Log.w("ageChecker","old age: "+legalAge);
		Log.w("ageChecker","new age: "+i);
		legalAge = i;
		calculateLegalYear(i);
	}
	
	static public int getLeagalAge(){
		return legalAge;
	}
	
	//recalculate the legalYear if you want to set a different legal age
	//other than 21
	static private void calculateLegalYear(int legalAge)
	{
		Log.w("ageChecker","old year: "+legalYear);
		Log.w("ageChecker","new year: "+legalYear);
		legalYear = year + legalAge;
	}
	
	//should calculate current age???
	public int getAge() 
	{
	    date = new Date();
	    int nowMonth = date.getMonth();
	    int nowYear = date.getYear()+1900;
	    int result = nowYear - year;

	    if (month > nowMonth) {
	        result--;
	    }
	    else if (month == nowMonth) {
	        int nowDay = date.getDate();

	        if (day > nowDay) {
	            result--;
	        }
	    }
	    return result;
	}
	
	
	public boolean isLegal(){
		if (this.getAge() >= AgeChecker.legalAge){
			return true;
		}
		return false;
	}
	
	
	//Our main show! Checks if the person's birthday is of age
	public boolean isLegalBorked()
	{
		date = new Date();
		//if the person has already turned legal over some years
		if(date.getYear()+1900 > legalYear)
		{
			return true;
		}
		//if the person is not of age( in years)
		else if(date.getYear()+1900 < legalYear)
		{
			return false;
		}
		//if not over or under years, then the years have to be the same, onto the months!
		else
		{
			//same logic as years, but in months
			if((date.getMonth()+1 > month))
			{
				return true;
			}
			//if person is not of age(in months)
			else if((date.getMonth()+1 < month))
		    {
			    return false;
		    }
			//if its not greater than or less than, it must be equals, onto the days!
		    else
		    {
		    	//if the person is not of age (in days)
			    if(date.getDay() < day)
			    {
				    return false;
			    }
			    //person's birthday is today or has passed
			    else
		  	    {
				    return true;
			    }
		    }
		}
	}
}