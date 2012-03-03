package com.vorsk.sandshrew;

/**
 *  Parser class parses a given string
 */
public class Parser {
	
	/**
	 * Returns the birthday by parsing a given string. The input string must be from an ID.
	 * @param str String to be parsed
	 * @return Birthday represented as the string "YYYYMMDD" or null if not a valid string
	 */
	public static String getBirthday(String str){
		StringBuilder parse = new StringBuilder();
		String subStr;
		
		if (str.indexOf('=') == -1) return null;
		
		subStr = str.substring(str.indexOf('='));
		if (subStr.length() != 14) return null;
		parse.append(subStr.substring(5, 9));
		parse.append(subStr.substring(3, 5));
		parse.append(subStr.substring(11, 13));
		
		for (int i = 0; i < parse.length(); i++)
			if ( !Character.isDigit(parse.charAt(i)))
				return null;
		
		return parse.toString();
		
	}
}
