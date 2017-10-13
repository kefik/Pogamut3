package cz.cuni.amis.pogamut.release.archetype;

import java.io.File;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import cz.cuni.amis.utils.Const;

@XStreamAlias(value="ArchetypeRefreshConfig")
public class ArchetypeRefreshConfig {

	@XStreamAlias(value="sourceDir")	
	private String sourceDir;
	
	@XStreamAlias(value="targetDir")
	private String targetDir;

	private ArchetypeRefreshConfig readResolve() {
		if (sourceDir != null) sourceDir = sourceDir.trim();
		if (targetDir != null) targetDir = targetDir.trim();
		return this;
	}
	
	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public String getTargetDir() {
		return targetDir;
	}

	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ArchetypeRefreshConfig[");
		sb.append(Const.NEW_LINE + "  sourceDir = " + sourceDir + (sourceDir == null ? "" : " --> " + new File(sourceDir).getAbsolutePath()));
		sb.append(Const.NEW_LINE + "  targetDir = " + targetDir + (targetDir == null ? "" : " --> " + new File(targetDir).getAbsolutePath()));
		sb.append(Const.NEW_LINE + "]");
		return sb.toString();
	}
	
}
