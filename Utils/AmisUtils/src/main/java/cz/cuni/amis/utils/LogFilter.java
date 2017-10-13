package cz.cuni.amis.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

/**
 * Simple way how to filter your log files...
 */
public class LogFilter {
	
	private File source;
	private File destination;
	private Pattern[] accept;
	private Pattern[] remove;



	/**
	 * Opens 'source', writes to 'destination'.
	 * <p><p>
	 * Every single line from 'source' is matched against 'accept'. If some pattern matches the line, it goes to 'destination'.<p>
	 * If not, it is matched against 'remove'. If some pattern matches the line, it is discarded.<p>
	 * If no pattern matches, the line goes to 'destination'.
	 * 
	 * @param source
	 * @param destination
	 * @param accept
	 * @param remove
	 */
	public LogFilter(File source, File destination, Pattern[] accept, Pattern[] remove) {
		this.source = source;
		this.destination = destination;
		this.accept = accept;
		this.remove = remove;
	}

	public void filter() throws IOException {
		FileReader fileReader = new FileReader(source);
		try {
			BufferedReader reader = new BufferedReader(fileReader);
			FileWriter fileWriter = new FileWriter(destination);
			try {
				PrintWriter writer = new PrintWriter(fileWriter);
				
				System.out.println("-= Log filtering =-");
				System.out.println("Source: " + source.getAbsolutePath());
				System.out.println("Destin: " + destination.getAbsolutePath());				
				
				int lineCount = 0;
				while (reader.ready()) {
					boolean next = false;
					
					System.out.print(".");
					++lineCount;
					if (lineCount % 100 == 0) System.out.println();
					
					String line = reader.readLine();
					
					for (Pattern a : this.accept) {
						if (a.matcher(line).find()) {
							writer.println(line);
							next = true;
							break;
						}
					}
					if (next) continue;
					
					for (Pattern r : this.remove) {
						if (r.matcher(line).find()) {						
							next = true;
							break;
						}
					}
					if (next) continue;
										
					writer.println(line);					
				}
				System.out.println();
				System.out.println("FINISHED");
			} finally {
				fileWriter.close();
			}
		} finally {
			fileReader.close();
		}
		
		
	}
	
}
