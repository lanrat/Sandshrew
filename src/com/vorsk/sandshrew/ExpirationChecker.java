package com.vorsk.sandshrew;
import java.util.Date;

import android.util.Log;

public class ExpirationChecker 
{
	//instance variables
	static private int month;
	static private int year;
	static private int day;
	Date date;
	
	//no argument constructor
	public ExpirationChecker()
	{
		month = 0;
		year = 0;
		day = 0;
	}
	
	public ExpirationChecker(String str)
	{
		setExpirationDate(str);
	}
	
	public static void setExpirationDate(String str)
	{
		if (str == null){
			return; //this should be better...
		}
		StringBuilder parse = new StringBuilder();
		//String strParse = str;
		parse.append("20");
		parse.append(str.substring(0, 2));
		year = Integer.parseInt(parse.toString());
		if(str.valueOf(2).equals(0))
		{
			month = Integer.parseInt(str.substring(3,4));
		}
		else
		{
			month = Integer.parseInt(str.substring(2,4));
		}
		
		if(str.valueOf(4).equals(0))
		{
			day = Integer.parseInt(str.substring(5,6));
		}
		else
		{
			day = Integer.parseInt(str.substring(4,6));
		}
	}
	
	public boolean isExpired()
	{
		date = new Date();
		//if the current year is over expired year
		if(date.getYear()+1900 > year)
		{
			return true;
		}
		//if the current year is under expired year
		else if(date.getYear()+1900 < year)
		{
			return false;
		}
		//if not over or under years, then the years have to be the same, onto the months!
		else
		{
			//same logic as years, but in months
			//if the month is equal and greater than, it is expired
			if(date.getMonth()+1 > month)
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
			    if(date.getDate() < day)
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
