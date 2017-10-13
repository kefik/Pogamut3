package cz.cuni.amis.netbeans.publicpkgs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.DomDriver;

import cz.cuni.amis.utils.Const;

@XStreamAlias(value = "NetBeansPublicPackages")
public class NetBeansPublicPackagesConfig {
	
	@XStreamImplicit(itemFieldName="excludeDir")
	private List<String> excludeDirs;
	
	@XStreamImplicit(itemFieldName="include")
	private List<IncludeDir> dirs;
	
	@XStreamAlias(value="targetPom")
	private String targetPom;
	
	@XStreamImplicit(itemFieldName="packagePrefix")
	private List<String> packagePrefixes;
	
	public static NetBeansPublicPackagesConfig loadXML(File xmlFile) {
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
		xstream.alias(NetBeansPublicPackagesConfig.class.getAnnotation(XStreamAlias.class).value(), NetBeansPublicPackagesConfig.class);
		Object obj = xstream.fromXML(reader);
		try {
			reader.close();
		} catch (IOException e) {
		}
		if (obj == null || !(obj instanceof NetBeansPublicPackagesConfig)) {
			throw new RuntimeException("file " + xmlFile.getAbsolutePath() + " doesn't contain a xml with RewriteFilesConfig");
		}
		return (NetBeansPublicPackagesConfig)obj;
	}
	
	public NetBeansPublicPackagesConfig() {
		excludeDirs = new ArrayList<String>();
		dirs = new ArrayList<IncludeDir>();
	}
	
	public NetBeansPublicPackagesConfig readResolve() {
		if (excludeDirs == null) {
			excludeDirs = new ArrayList<String>();
		}
		if (dirs == null) {
			dirs = new ArrayList<IncludeDir>();
		}
		for (IncludeDir dir : dirs) {
			if (dir.getExcludeDirs() == null) dir.setExcludeDirs(excludeDirs);
			else {
				dir.getExcludeDirs().addAll(excludeDirs);
			}
		}
		if (packagePrefixes == null) {
			packagePrefixes = new ArrayList<String>();			
		}
		return this;
	}
	
	public void initialize() {
		if (packagePrefixes.size() == 0) {
			packagePrefixes.add("");
		}
	}
	
	public List<String> getExcludeDirs() {
		return excludeDirs;
	}

	public void setExcludeDirs(List<String> excludeDirs) {
		this.excludeDirs = excludeDirs;
	}

	public List<IncludeDir> getDirs() {
		return dirs;
	}

	public void setDirs(List<IncludeDir> dirs) {
		this.dirs = dirs;
	}
	
	public String getTargetPom() {
		return targetPom;
	}

	public void setTargetPom(String targetPom) {
		this.targetPom = targetPom;
	}
	
	public List<String> getPackagePrefixes() {
		return packagePrefixes;
	}

	public void setPackagePrefixes(List<String> packagePrefixes) {
		this.packagePrefixes = packagePrefixes;
	}

	@Override
	public String toString() {
		return toString("");
	}
	
	public String toString(String prefix) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(prefix + "PublicPackagesConfig[");
		
		sb.append(Const.NEW_LINE + prefix + "  targetPom = " + targetPom);
		
		sb.append(Const.NEW_LINE + prefix + "  replace packge with prefixes = ");
		if (packagePrefixes == null || packagePrefixes.size() == 0) {
			sb.append(Const.NEW_LINE + prefix + "    <everything>");
		} else {
			for (String pkg : packagePrefixes) {
				sb.append(Const.NEW_LINE + prefix + "    " + pkg);
			}
		}
		
		sb.append(Const.NEW_LINE + prefix + "  excludeDirs =");
		if (excludeDirs == null || excludeDirs.size() == 0) {
			sb.append(" <none>");
		} else {
			for (String excludeDir : excludeDirs) {
				sb.append(Const.NEW_LINE + prefix + "    " + excludeDir);
			}
		}
		
		for (IncludeDir dir : dirs) {
			sb.append(Const.NEW_LINE + dir.toString("Dir", prefix + "  "));
		}
		
		sb.append(Const.NEW_LINE + prefix + "]");
		return sb.toString();
	}

}
