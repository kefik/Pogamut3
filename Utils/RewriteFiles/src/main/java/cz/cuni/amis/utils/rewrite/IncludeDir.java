package cz.cuni.amis.utils.rewrite;

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

import cz.cuni.amis.utils.rewrite.rewriter.Const;

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
	
	@XStreamImplicit(itemFieldName="includeFile")
	private List<String> includeFiles;
	
	@XStreamImplicit(itemFieldName="excludeFile")
	private List<String> excludeFiles;
		
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
		includeFiles = new ArrayList<String>();
		excludeFiles = new ArrayList<String>();
	}
	
	public IncludeDir(IncludeDir orig) {
		this();
		if (orig != null) {
			this.dir = orig.dir;
			this.subdirs = orig.subdirs;
			if (orig.excludeDirs != null) {
				for (String path : orig.excludeDirs) {
					this.excludeDirs.add(path);
				}
				if (orig.includeFiles != null) {
					for (String path : orig.includeFiles) {
						this.includeFiles.add(path);
					}
				}
				if (orig.excludeFiles != null) {
					for (String path : orig.excludeFiles) {
						this.excludeFiles.add(path);
					}
				}
			}
		}
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
		if (orig.includeFiles != null) {
			for (String path : orig.includeFiles) {
				this.includeFiles.add(path);
			}
		}
		if (globals.includeFiles != null) {
			for (String path : globals.includeFiles) {
				this.includeFiles.add(path);
			}
		}
		if (orig.excludeFiles != null) {
			for (String path : orig.excludeFiles) {
				this.excludeFiles.add(path);
			}
		}
		if (globals.excludeFiles != null) {
			for (String path : globals.excludeFiles) {
				this.excludeFiles.add(path);
			}
		}
	}
	
	private IncludeDir readResolve() {
		if (subdirs == null) subdirs = true;
		if (excludeDirs == null) excludeDirs = new ArrayList<String>(0);
		if (includeFiles == null) includeFiles = new ArrayList<String>(0);
		if (excludeFiles == null) {
			excludeFiles = new ArrayList<String>(0);
		}
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

	public List<String> getIncludeFiles() {
		return includeFiles;
	}

	public void setIncludeFiles(List<String> includeFiles) {
		this.includeFiles = includeFiles;
	}

	public List<String> getExcludeFiles() {
		return excludeFiles;
	}

	public void setExcludeFiles(List<String> excludeFiles) {
		this.excludeFiles = excludeFiles;
	}
	
	public MyDirectoryWalker getDirectoryWalker() {
		if (directoryWalker == null) initDirectoryWalker();
		return directoryWalker;
	}
	
	/////////////////////////////////
	
	private void initDirectoryWalker() {
		directoryWalker = new MyDirectoryWalker(dir) {

			@Override
			protected boolean handleDirectory(File directory, int depth, Collection results) throws IOException {
				return acceptDir(directory);
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
		String absolute = RewriteUtils.makeUniform(pathToDir.getAbsolutePath());
		for (String exclude : excludeDirs) {
			if (absolute.endsWith(exclude)) return false;
		}
		if (subdirs) return true;
		return pathToDir.equals(dir);
	}
	
	private boolean acceptFile(File pathToFile) {
		if (includeFileFilter.accept(pathToFile)) {
			if (excludeFileFilter != null && excludeFileFilter.accept(pathToFile)) return false;
			return true;
		}
		return false;
	}
	
	public void initialize() {
		directoryWalker = null;
		if (subdirs == null) subdirs = true;
		if (includeFiles == null) includeFiles = new ArrayList<String>(1);
		if (excludeFiles == null) excludeFiles = new ArrayList<String>(0);
		if (excludeDirs  == null) excludeDirs  = new ArrayList<String>(0);
		for (int i = 0; i < includeFiles.size(); ) {
			if (includeFiles.get(i) == null) {
				includeFiles.remove(i);
			} else {
				includeFiles.set(i, RewriteUtils.makeUniform(includeFiles.get(i)));
				if (includeFiles.get(i) == null) {
					includeFiles.remove(i);
				} else {
					++i;
				}				
			}
		}
		for (int i = 0; i < excludeFiles.size(); ) {
			if (excludeFiles.get(i) == null) {
				excludeFiles.remove(i);
			} else {
				excludeFiles.set(i, RewriteUtils.makeUniform(excludeFiles.get(i)));
				if (excludeFiles.get(i) == null) {
					excludeFiles.remove(i);
				} else {
					++i;
				}
			}
		}
		for (int i = 0; i < excludeDirs.size(); ) {
			if (excludeDirs.get(i) == null) {
				excludeDirs.remove(i);
			} else {
				excludeDirs.set(i, RewriteUtils.makeUniform(excludeDirs.get(i)));
				if (excludeDirs.get(i) == null) {
					excludeDirs.remove(i);
				} else {
					++i;
				}
			}
		}
		if (includeFiles.size() == 0) {
			includeFiles.add("*.*");
		}
		includeFileFilter = new WildcardFileFilter(includeFiles);
		if (excludeFiles.size() > 0) {
			excludeFileFilter = new WildcardFileFilter(excludeFiles);
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
		
		sb.append(Const.NEW_LINE + prefix + "  includeFiles  = ");
		if (includeFiles == null) {
			sb.append("null");
		} else {
			for (String file : includeFiles) {
				if (first) first = false;
				else sb.append(", ");
				sb.append(file);
			}
		}
		first = true;
		
		sb.append(Const.NEW_LINE + prefix + "  excludeFiles  = ");
		if (excludeFiles == null) {
			sb.append("null");
		} else {
			for (String file : excludeFiles) {
				if (first) first = false;
				else sb.append(", ");
				sb.append(file);
			}
		}
		first = true;
		
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
		first = true;
				
		sb.append(Const.NEW_LINE + prefix + "]");
		return sb.toString();
	}
	
}
