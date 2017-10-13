package cz.cuni.amis.pogamut.ut2004.communication.messages.xmlresources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class RewriteMessages {
	
	public static final String WORKING_DIR = "./src/cz/cuni/amis/pogamut/ut2004/communication/messages/xmlresources";
	
	public static final String MESSAGES_DIR = "./src/cz/cuni/amis/pogamut/ut2004/communication/messages/xmlresources/gbinfomessages";
	
	public static final Pattern pattern = Pattern.compile("InfoObject");
	
	public static final String rewrite = "InfoMessage";
	
	public static class Rewriter {
		
		private FileReader reader;
		private FileWriter writer;

		public Rewriter(FileReader reader, FileWriter writer) {
			this.reader = reader;
			this.writer = writer;
		}
		
		public void rewrite() throws IOException {
			BufferedReader reader = new BufferedReader(this.reader);
			PrintWriter writer = new PrintWriter(this.writer);
			while (reader.ready()) {
				String line = reader.readLine();
				line = pattern.matcher(line).replaceAll(rewrite);
				writer.println(line);
			}
			writer.close();
			reader.close();
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		
		File dir = new File(MESSAGES_DIR);
		
		List<File> files = new LinkedList<File>();
		
		for (File f : dir.listFiles()) {
			if (!f.isFile()) continue;
			files.add(f);
		}
		
		int i = 0;
		for (File file : files) {
			++i;
			System.out.println(i + "/" + files.size() + "   " + file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("\\")+1, file.getAbsolutePath().length()));
			FileReader reader = new FileReader(file);
			File output = new File(WORKING_DIR + "\\temp.xml");
			FileWriter writer = new FileWriter(output);
			Rewriter rewriter = new Rewriter(reader, writer);
			rewriter.rewrite();
			file.delete();
			output.renameTo(new File(MESSAGES_DIR + "\\" + file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("\\")+1, file.getAbsolutePath().length())));
		}
		
		System.out.println("DONE");
		
		
	}

}
