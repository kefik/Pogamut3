package cz.cuni.amis.pogamut.lectures.linetagger.tagger;

import cz.cuni.amis.utils.rewrite.rewriter.Substitution;

public class TagSubstitution extends Substitution {

	public static final String COMMENT_START = "/*T:";
	
	public static final String COMMENT_END = "*/";
	
	public static final String WHITESPACES_PATTERN = "\\s*";
	
	public static int INT_LENGTH = String.valueOf(Integer.MAX_VALUE).length();
	
	@Override
	public boolean isMultiLine() {
		return false;
	}
	
	public static int countHash(String str) {
		String whiteSpaceFree = str.replaceAll(WHITESPACES_PATTERN, "");
		if (whiteSpaceFree.length() == 0) return 0;
		return whiteSpaceFree.hashCode();
	}

	
	@Override
	public String substitute(String str) {
		int hash = countHash(str);
		if (hash == 0) return str;
		String result = String.valueOf(hash);
		if (result.startsWith("-")) {
			result = result.substring(1);
			while (result.length() < INT_LENGTH) result = "0" + result;
			result = "-" + result;
		} else {
			while (result.length() < INT_LENGTH) result = "0" + result;
			result = "+" + result;
		}	
		if (result.length() > INT_LENGTH + 1) {
			// HUPS!
			System.out.println("Weird length...");
		}
		return COMMENT_START + result + COMMENT_END + str;
	}
	
	@Override
	public String toString() {
		return "TagSubstitution[single-line pattern]";
	}
}
