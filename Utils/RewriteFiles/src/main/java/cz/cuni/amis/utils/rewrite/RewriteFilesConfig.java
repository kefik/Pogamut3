package cz.cuni.amis.utils.rewrite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.DomDriver;

import cz.cuni.amis.utils.rewrite.rewriter.Const;

@XStreamAlias(value = "RewriteFilesConfig")
public class RewriteFilesConfig {
	
	@XStreamAlias(value="globals")
	private IncludeDirForSubstitutions globals;
	
	@XStreamImplicit(itemFieldName="include")
	private List<IncludeDirForSubstitutions> dirs;
	
	@XStreamAsAttribute
	private String encoding;
	
	public static RewriteFilesConfig loadXML(File xmlFile) {
		if (xmlFile == null) {
			throw new IllegalArgumentException("'xmlFile' can't be null!");
		}
		FileReader reader;
		try {
			reader = new FileReader(xmlFile);
		} catch (FileNotFoundException e1) {
			throw new RuntimeException("File " + xmlFile.getAbsolutePath() + " not found: " + e1.getMessage(), e1);
		}
		XStream xstream = new XStream(new DomDriver());
		xstream.autodetectAnnotations(true);
		xstream.alias(RewriteFilesConfig.class.getAnnotation(XStreamAlias.class).value(), RewriteFilesConfig.class);
		Object obj = xstream.fromXML(reader);
		try {
			reader.close();
		} catch (IOException e) {
		}
		if (obj == null || !(obj instanceof RewriteFilesConfig)) {
			throw new RuntimeException("file " + xmlFile.getAbsolutePath() + " doesn't contain a xml with RewriteFilesConfig");
		}
		return (RewriteFilesConfig)obj;
	}
	
	public RewriteFilesConfig() {
		globals = new IncludeDirForSubstitutions();
		dirs = new ArrayList<IncludeDirForSubstitutions>();
	}
	
	public RewriteFilesConfig readResolve() {
		if (globals == null) {
			globals = new IncludeDirForSubstitutions();
		}
		if (dirs == null) {
			dirs = new ArrayList<IncludeDirForSubstitutions>();
		}
		return this;
	}
	
	public void initialize() {
		boolean globalsInclude = globals.getIncludeFiles().size() != 0;
		globals.initialize();
		if (!globalsInclude) {
			globals.getIncludeFiles().clear();
		}
	}
	
	public IncludeDirForSubstitutions getGlobals() {
		return globals;
	}

	public void setGlobals(IncludeDirForSubstitutions globals) {
		this.globals = globals;
	}

	public List<IncludeDirForSubstitutions> getDirs() {
		return dirs;
	}

	public void setDirs(List<IncludeDirForSubstitutions> dirs) {
		this.dirs = dirs;
	}
	
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public String toString() {
		return toString("");
	}
	
	public String toString(String prefix) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(prefix + "RewriteFilesConfig[");
		
		sb.append(Const.NEW_LINE + globals.toString("Globals", prefix + "  "));

		for (IncludeDirForSubstitutions dir : dirs) {
			sb.append(Const.NEW_LINE + dir.toString("Dir", prefix + "  "));
		}
		
		sb.append(Const.NEW_LINE + prefix + "]");
		return sb.toString();
	}

}
