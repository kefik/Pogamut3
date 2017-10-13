package cz.cuni.amis.netbeans.publicpkgs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;

public class MyDirectoryWalker extends DirectoryWalker {
	
	public List<File> walk(File startDirectory)  {
		List<File> result = new ArrayList<File>();
		try {
			walk(startDirectory, result);
		} catch (IOException e) {
			throw new RuntimeException("IOException: " + e.getMessage(), e);
		}
		return result;
	}

}
