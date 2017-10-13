package cz.cuni.amis.netbeans.publicpkgs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.FilePath;

@XStreamAlias(value="includeDir")
public class IncludeDir {
	
	@XStreamAlias(value="dir")
	@XStreamAsAttribute
	private File dir;
	
	@XStreamAlias(value="subdirs")
	@XStreamAsAttribute
	private Boolean subdirs;
	
	@XStreamImplicit(itemFieldName="excludeDir")
	private List<String> excludeDirs;
	
	/////////////////////////////////////////////
	
	@XStreamOmitField
	private MyDirectoryWalker directoryWalker;
	
	@XStreamOmitField
	private transient WildcardFileFilter includeFileFilter;
	
	@XStreamOmitField
	private transient WildcardFileFilter excludeFileFilter;
	
	public IncludeDir() {		
		subdirs = true;
		excludeDirs = new ArrayList<String>();
	}
	
	public IncludeDir(IncludeDir orig, IncludeDir globals) {
		this();
		this.dir = orig.dir;
		if (this.dir == null) {
			this.dir = globals.dir;
		}
		this.subdirs = orig.subdirs;
		if (this.subdirs == null) {
			this.subdirs = globals.subdirs;
		}
		if (orig.excludeDirs != null) {
			for (String path : orig.excludeDirs) {
				this.excludeDirs.add(path);
			}
		}
		if (globals.excludeDirs != null) {
			for (String path : globals.excludeDirs) {
				this.excludeDirs.add(path);
			}
		}
	}
	
	private IncludeDir readResolve() {
		if (subdirs == null) subdirs = true;
		if (excludeDirs == null) excludeDirs = new ArrayList<String>(0);
		return this;
	}
	
	public File getDir() {
		return dir;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}

	public Boolean getSubdirs() {
		return subdirs;
	}

	public void setSubdirs(Boolean subdirs) {
		this.subdirs = subdirs;
	}

	public List<String> getExcludeDirs() {
		return excludeDirs;
	}

	public void setExcludeDirs(List<String> excludeDirs) {
		this.excludeDirs = excludeDirs;
	}

	public MyDirectoryWalker getDirectoryWalker() {
		if (directoryWalker == null) initDirectoryWalker();
		return directoryWalker;
	}
	
	/////////////////////////////////
	
	private void initDirectoryWalker() {
		directoryWalker = new MyDirectoryWalker() {

			@Override
			protected boolean handleDirectory(File directory, int depth, Collection results) throws IOException {
				if (acceptDir(directory)) {
					results.add(directory);
					return true;
				} else {
					return false;
				}
			}
			
			@Override
			protected void handleFile(File file, int depth, Collection results) throws IOException {
				if (acceptFile(file)) {
					results.add(file);
				}
			}

		};
	}
	
	private boolean acceptDir(File pathToDir) {
		String absolute = FilePath.makeUniform(pathToDir.getAbsolutePath());
		for (String exclude : excludeDirs) {
			if (absolute.endsWith(exclude)) return false;
		}
		return true;
	}
	
	private boolean acceptFile(File pathToFile) {
		return false;
	}
	
	public void initialize() {
		if (subdirs == null) subdirs = true;
		if (excludeDirs  == null) excludeDirs  = new ArrayList<String>(0);
		for (int i = 0; i < excludeDirs.size(); ) {
			if (excludeDirs.get(i) == null) {
				excludeDirs.remove(i);
			} else {
				excludeDirs.set(i, FilePath.makeUniform(excludeDirs.get(i)));
				if (excludeDirs.get(i) == null) {
					excludeDirs.remove(i);
				} else {
					++i;
				}
			}
		}		
	}
	
	@Override
	public String toString() {
		return toString("IncludeDir", "");
	}
	
	public String toString(String name, String prefix) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		
		sb.append(prefix + name + "[");
		
		sb.append(Const.NEW_LINE + prefix + "  dir           = " + dir + (dir != null ? " --> " + dir.getAbsolutePath() : ""));
		
		sb.append(Const.NEW_LINE + prefix + "  subdirs       = " + subdirs);
		
		sb.append(Const.NEW_LINE + prefix + "  excludeDirs   = ");
		if (excludeDirs == null) {
			sb.append("null");
		} else {
			for (String file : excludeDirs) {
				if (first) first = false;
				else sb.append(", ");
				sb.append(file);
			}
		}
			
		sb.append(Const.NEW_LINE + prefix + "]");
		return sb.toString();
	}
	
}
