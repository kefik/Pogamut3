package cz.cuni.amis.utils;

public class StringIdifier {
	
	public static final String allowed = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_"; 
	
	public static final String number = "0123456789";
	
	public static String idify(String str) {
		return idify(str, "", "_");
	}
	
	public static String idify(String str, String allowedExtraChars) {
		return idify(str, allowedExtraChars, "_");
	}
	
	public static String idify(String str, String allowedExtraChars, String replaceChar) {
		if (str == null) return replaceChar;
		if (str.length() == 0) return replaceChar;
		StringBuffer result = new StringBuffer(str.length());
		
		if (number.contains(str.substring(0,1))) {
			result.append(replaceChar);
		}
		
		for (int i = 0; i < str.length(); ++i) {
			String c = str.substring(i,i+1);
			if (allowed.contains(c) || allowedExtraChars.contains(c)) {
				result.append(c);
			} else {
				result.append(replaceChar);
			}
		}
		
		return result.toString();
	}

}
