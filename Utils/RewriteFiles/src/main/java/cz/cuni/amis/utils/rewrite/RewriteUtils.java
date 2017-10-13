package cz.cuni.amis.utils.rewrite;

public class RewriteUtils {

	public static String makeUniform(String file) {
		if (file == null) return null;
		for (int i = 0; i < file.length(); ++i) {
			if (file.charAt(i) == '\\') file = file.substring(0, i) + "/" + file.substring(i+1);
		}
		file = file.replaceAll("//", "/");
		while (file.length() > 0 && file.endsWith("/")) file = file.substring(0, file.length()-1);
		while (file.startsWith("./")) file = file.substring(2);
		if (file.length() == 0) return null;
		
		return file;
	}
	
}
