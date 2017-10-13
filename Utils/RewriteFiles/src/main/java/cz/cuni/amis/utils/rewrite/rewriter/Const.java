package cz.cuni.amis.utils.rewrite.rewriter;

public class Const {

	public static final String NEW_LINE = System.getProperty("line.separator");

	public static String whitespaces(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; ++i) sb.append(" ");
		return sb.toString();
	}
	
}
