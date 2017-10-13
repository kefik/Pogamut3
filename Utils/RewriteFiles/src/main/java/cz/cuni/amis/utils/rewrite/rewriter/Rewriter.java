package cz.cuni.amis.utils.rewrite.rewriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;


public class Rewriter {
	
	private static String NEW_LINE = System.getProperty("line.separator");
	
	private List<ISubstitution> substitutions;
	
	private Logger log = null;

	public Rewriter() {
		substitutions = new ArrayList<ISubstitution>();
	}
	
	@SuppressWarnings("unchecked")
	public Rewriter(List<? extends ISubstitution> substitutions) {
		if (substitutions == null) throw new IllegalArgumentException("'substitutions' can't be null!");
		this.substitutions = (List<ISubstitution>) substitutions;
	}
	
	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public List<ISubstitution> getISubstitutions() {
		return substitutions;
	}
	
	public boolean hasMultiLineISubstitution() {
		for (ISubstitution subst : substitutions) {
			if (subst.isMultiLine()) return true;
		}
		return false;
	}

	public Rewriter addISubstitution(ISubstitution subst) {
		if (subst == null) return this;
		this.substitutions.add(subst);
		return this;
	}
	
	public synchronized String rewrite(String text, boolean multiLine) {
		if (substitutions.size() == 0) return text;
		if (text == null) return null;
		String result = text;
		if (multiLine) {
			// result contains the whole file! 
			for (ISubstitution subst : substitutions) {
				if (subst.isMultiLine()) {
					result = subst.substitute(result);
				} else {
					// substitution works per-line basis!
					// read line-by-line
					String processing = result;
					StringBuffer sb = new StringBuffer(); // collecting result line by line into this
					int index = 0;
					while (index < processing.length()) {
						int newLineIndex = processing.indexOf("\n", index);
						if (newLineIndex < 0) {
							// no new line
							String part = processing.substring(index);
							sb.append(subst.substitute(part));
							index = processing.length();
						} else {
							// new line found
							String part = null;
							if (newLineIndex == 0 || processing.charAt(newLineIndex-1) != '\r') {
								part = processing.substring(index, newLineIndex); 
							} else {
								part = processing.substring(index, newLineIndex-1);
							}
							sb.append(subst.substitute(part));
							index = newLineIndex+1;
							sb.append(NEW_LINE);
						}
					}
				}
			}
		} else {
			// result contains just one line
			for (ISubstitution subst : substitutions) {
				if (subst.isMultiLine()) {
					throw new RuntimeException("Rewriter.rewrite() called with multiLine == false, but it was configured with multi-line substitution! Invalid!");
				}
				result = subst.substitute(result);
			}		
		}
		return result;
	}
	
	public synchronized boolean rewriteFile(File input, File output, String encoding) throws IOException {
		int lineNumber = 1;
		
		boolean changed = false;
		
		if (hasMultiLineISubstitution()) {
			log.warning("Multi-line substitution(s) detected, buffering whole file into the memory.");
			String file;
			if (encoding != null) file = FileUtils.readFileToString(input, encoding);
			else file = FileUtils.readFileToString(input);
			String rewritten = rewrite(file, true);
			if (!file.equals(rewritten)) {
				log.info("Some substitutions were applied.");
				changed = true;
			}
			
			PrintWriter writer;
			if (encoding != null) writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(output), encoding));  
			else writer = new PrintWriter(new FileWriter(output));
			
			try {
				writer.print(rewritten);
			} finally {
				writer.close();
			}			
		} else {
			log.info("No multi-line patterns present, rewriting file line-by-line (as if streaming).");
			BufferedReader reader;
			if (encoding != null) reader = new BufferedReader(new InputStreamReader(new FileInputStream(input), encoding));
			else reader = new BufferedReader(new FileReader(input));
			try {
				PrintWriter writer;
				if (encoding != null) writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(output), encoding));  
				else writer = new PrintWriter(new FileWriter(output));
				try {
					while (reader.ready()) {
						String line = reader.readLine();
						String rewritten = rewrite(line, false);
						
						if (!line.equals(rewritten)) {
							changed = true;
							if (log != null) {
								log.info(       "Replacing" + "[" + lineNumber + "]" + ": " + line.trim() + Const.NEW_LINE 
										+ "       With:        " + Const.whitespaces(String.valueOf(lineNumber).length()) + rewritten.trim());
							}
						}
						
						writer.println(rewritten);
						++lineNumber;
					}
				} finally {
					writer.close();
				}
			} finally {
				reader.close();
			}
		}
		
		return changed;
	}
	
	public synchronized boolean rewriteFile(File file, String encoding) throws IOException {
		File tempFile = new File("temp.rewrite.txt");
		if (rewriteFile(file, tempFile, encoding)) {
			// file was changed
			file.delete();
			FileUtils.moveFile(tempFile, file);
			tempFile.delete();
			return true;
		} else {
			// no changes
			tempFile.delete();
			return false;
		}
	}
	
}
