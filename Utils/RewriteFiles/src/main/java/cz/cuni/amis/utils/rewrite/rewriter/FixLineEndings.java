package cz.cuni.amis.utils.rewrite.rewriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author Jimmy
 *
 */
@XStreamAlias(value="fixLineEndings")
public class FixLineEndings {

	@XStreamAlias(value="windowsStyle")
	@XStreamAsAttribute
	private Boolean windowsStyle = true;
	
	@XStreamAlias(value="onlyIfRewritten")
	@XStreamAsAttribute
	private Boolean fixOnlyIfRewritten = true;

	public FixLineEndings() {
	}
	
	/**
	 * @param windows true == CR LF, false (unix) == LF
	 */
	public FixLineEndings(boolean windows) {
		this.windowsStyle = windows;
	}
	
	private FixLineEndings readResolve() {
		if (windowsStyle == null) windowsStyle = true;
		if (fixOnlyIfRewritten == null) fixOnlyIfRewritten = true;
		return this;
	}

	public boolean isFixOnlyIfRewritten() {
		if (fixOnlyIfRewritten == null) return true;
		return fixOnlyIfRewritten;
	}
	
	public Boolean getFixOnlyIfRewritten() {
		return fixOnlyIfRewritten;
	}

	public void setFixOnlyIfRewritten(Boolean fixOnlyIfRewritten) {
		this.fixOnlyIfRewritten = fixOnlyIfRewritten;
	}

	public boolean isWindowsStyle() {
		if (windowsStyle == null) return true;
		return windowsStyle;
	}

	public Boolean getWindowsStyle() {
		return windowsStyle;
	}

	public void setWindowsStyle(Boolean windowsStyle) {
		this.windowsStyle = windowsStyle;
	}

	public boolean fix(File in, File out) {
		BufferedReader reader = null;
		PrintWriter writer = null;
		try {
			reader = new BufferedReader(new FileReader(in));
			try {
				writer = new PrintWriter(new FileWriter(out));
				try {
					while (reader.ready()) {
						String line = reader.readLine();
						writer.print(line);
						if (windowsStyle) {
							writer.print('\r');
							writer.print('\n');
						} else {
							writer.print('\n');
						}
					}
				} catch (Exception e) {
					throw new RuntimeException("Failed to rewrite file " + in.getAbsolutePath() + " into file " + out.getAbsolutePath() + ", because: " + e.getMessage(), e);
				} finally {
					writer.close();
				}
			} catch (Exception e) {
				throw new RuntimeException("Failed to open file for writing: " + out.getAbsolutePath() + " because: " + e.getMessage(), e);
			} finally {
				reader.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to open file for reading: " + in.getAbsolutePath() + " because: " + e.getMessage(), e);
		}
		return true;
		
	}
	
	public synchronized boolean fix(File file) throws IOException {
		File tempFile = new File("temp.line-endings.txt");
		if (fix(file, tempFile)) {
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
	
	@Override
	public String toString() {
		return "FixLineEndings[windowsStyle=" + isWindowsStyle() + ", fixOnlyIfRewritten=" + isFixOnlyIfRewritten() + "]";
	}

}
