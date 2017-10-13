package cz.cuni.amis.utils.normalizer;

import java.text.Normalizer;

public class NormalizerAscii {

	public static String toAscii(String str) {
		if (str == null) return null;
		return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]","_");
	}
	
	public static String stripQuotes(String str) {
		return str.replaceAll("\\\"", "'");
	}
	
}
