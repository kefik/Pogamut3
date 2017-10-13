package cz.cuni.amis.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Very slow and very dumb string replacer ... do not use extensively!
 * 
 * @author Jimmy
 */
public class StringReplacer {
	
	public static void replace(BufferedReader reader, PrintWriter writer, Map<String, String> replace) throws IOException {
		while(reader.ready()) {
			String line = reader.readLine();
			for(String key : replace.keySet()) {
				if (line.contains(key)) {
					line = line.replace(key, replace.get(key));
				}
			}
			writer.println(line);
		}
	}

}
