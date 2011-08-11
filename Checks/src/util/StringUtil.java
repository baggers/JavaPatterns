package util;

public class StringUtil {
	
	/**
	 * Takes string name from AST of form name[colxrow] and converts to name
	 * @param s the string in AST notation
	 * @return string name without [colxrow] appended
	 */
	public static String fixName(String s)
	{
		return s.substring(0, s.indexOf('['));
	}
	

}
