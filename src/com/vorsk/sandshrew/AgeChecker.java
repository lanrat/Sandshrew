package com.vorsk.sandshrew;
import java.util.Date;
//class to do age checking
public class AgeChecker
{	
	//instance variables
	private int month;
	private int day;
	private int year;
	private int legalYear; //this is the important year of legalAge
	private int legalAge = 21; 
	Date date = new Date();
	
	//no arguement constructor
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
	
	//change the birthdays so we dont have to make a new class each time...
	public void setBirthday(String birthdays)
	{
		year = Integer.parseInt(birthdays.substring(0,3));
		legalYear = year + 21;
		
		//checks if the first value of month is 0
		if(birthdays.valueOf(4).equals(0))
		{
			month = Integer.parseInt(birthdays.substring(5,5));
		}
		else
		{
			month = Integer.parseInt(birthdays.substring(4,5));
		}
		
		//checks if the first value of day is 0
		if(birthdays.valueOf(6).equals(0))
		{
			day = Integer.parseInt(birthdays.substring(7,7));
		}
		else
		{
			day = Integer.parseInt(birthdays.substring(6,7));
		}
	}
	
	//private method to setLegalAge, if the default 21 is not desired
	private void setLegalAge(int i)
	{
		legalAge = i;
	}
	
	//recalculate the legalYear if you want to set a different legal age
	//other than 21
	public void calculateLegalYear(int legalAge)
	{
		setLegalAge(legalAge);
		legalYear = year + this.legalAge;
	}
	
	//should calculate current age???
	public int getAge() 
	{
	    Date date = new Date();
	    int nowMonth = date.getMonth()+1;
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

	//Our main show! Checks if the person's birthday is of age
	public boolean isLegal()
	{
		//if the person has already turned legal over some years
		if(date.getYear() > legalYear)
		{
			return true;
		}
		//if the person is not of age( in years)
		else if(date.getYear() < legalYear)
		{
			return false;
		}
		//if not over or under years, then the years have to be the same, onto the months!
		else
		{
			//same logic as years, but in months
			if((date.getMonth() > month))
			{
				return true;
			}
			//if person is not of age(in months)
			else if((date.getMonth() < month))
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