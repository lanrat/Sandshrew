package com.vorsk.sandshrew;

/**
 *  Parser class parses a given string
 */
public class Parser {
	private String str; // string to parse
	
	/**
	 * Constructor that takes a string and stores it as the string to parse
	 * @param str - string to parse
	 */
	public Parser(String str){ 
		this.str = str;
	}
	
	
	/**
	 * Returns the birthday by parsing the backing string of the Parser class
	 * @return Birthday represented as the string "YYYYMMDD"
	 */
	public String getBirthday(){
		StringBuilder parse = new StringBuilder();
		String subStr;
		
		subStr = str.substring(str.indexOf("="));
		parse.append(subStr.substring(5, 9));
		parse.append(subStr.substring(3, 5));
		parse.append(subStr.substring(11, 13));
		
		return parse.toString();
		
	}
}
