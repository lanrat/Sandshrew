package com.vorsk.sandshrew;

/**
 *  Parser class parses a given string
 */
public class Parser {
	
	/**
	 * Returns the birthday by parsing a given string of the Parser class
	 * @param str String to be parsed
	 * @return Birthday represented as the string "YYYYMMDD"
	 */
	public static String getBirthday(String str){
		StringBuilder parse = new StringBuilder();
		String subStr;
		
		subStr = str.substring(str.indexOf("="));
		parse.append(subStr.substring(5, 9));
		parse.append(subStr.substring(3, 5));
		parse.append(subStr.substring(11, 13));
		
		return parse.toString();
		
	}
}
