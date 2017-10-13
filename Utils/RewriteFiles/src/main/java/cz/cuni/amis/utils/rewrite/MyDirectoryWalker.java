package cz.cuni.amis.utils.rewrite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;

public class MyDirectoryWalker extends DirectoryWalker {
	
	private File dir;

	public MyDirectoryWalker(File dir) {
		this.dir = dir;
	}
	
	public List<File> walk()  {
		List<File> result = new ArrayList<File>();
		try {
			walk(dir, result);
		} catch (IOException e) {
			throw new RuntimeException("IOException: " + e.getMessage(), e);
		}
		return result;
	}

}
