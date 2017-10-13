package cz.cuni.amis.pogamut.release;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cz.cuni.amis.netbeans.publicpkgs.NetBeansPublicPackagesConfig;
import cz.cuni.amis.pogamut.release.archetype.ArchetypeRefreshConfig;
import cz.cuni.amis.utils.process.ProcessExecutionConfig;
import cz.cuni.amis.utils.rewrite.RewriteFilesConfig;

@XStreamAlias(value="step")
public class PogamutReleaseStep {
	
	@XStreamAlias(value="id")
	@XStreamAsAttribute
	private String id;
	
	@XStreamAlias(value="failStop")
	@XStreamAsAttribute
	private Boolean stopOnFail;
	
	@XStreamImplicit(itemFieldName="rewrite")
	private List<RewriteFilesConfig> rewriteFiles;
	
	@XStreamImplicit(itemFieldName="process")
	private List<ProcessExecutionConfig> processExecution;
	
	@XStreamImplicit(itemFieldName="archetype")
	private List<ArchetypeRefreshConfig> archetypeRefresh;
	
	@XStreamImplicit(itemFieldName="netbeansPublicPackages")
	private List<NetBeansPublicPackagesConfig> publicPackages;

	public PogamutReleaseStep readResolve() {
		if (id == null) id = "unnamed-step";
		if (stopOnFail == null) stopOnFail = true;
		if (rewriteFiles == null) rewriteFiles = new ArrayList<RewriteFilesConfig>();
		if (processExecution == null) processExecution = new ArrayList<ProcessExecutionConfig>();
		if (archetypeRefresh == null) archetypeRefresh = new ArrayList<ArchetypeRefreshConfig>();
		if (publicPackages == null) publicPackages = new ArrayList<NetBeansPublicPackagesConfig>();
		return this;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Boolean getStopOnFail() {
		return stopOnFail;
	}
	
	public boolean isStopOnFail() {
		return stopOnFail == null ? true : stopOnFail;
	}

	public void setStopOnFail(Boolean stopOnFail) {
		this.stopOnFail = stopOnFail;
	}

	public List<RewriteFilesConfig> getRewriteFiles() {
		return rewriteFiles;
	}

	public void setRewriteFiles(List<RewriteFilesConfig> rewriteFiles) {
		this.rewriteFiles = rewriteFiles;
	}

	public List<ProcessExecutionConfig> getProcessExecution() {
		return processExecution;
	}

	public void setProcessExecution(List<ProcessExecutionConfig> processExecution) {
		this.processExecution = processExecution;
	}

	public List<ArchetypeRefreshConfig> getArchetypeRefresh() {
		return archetypeRefresh;
	}

	public void setArchetypeRefresh(List<ArchetypeRefreshConfig> archetypeRefresh) {
		this.archetypeRefresh = archetypeRefresh;
	}

	public List<NetBeansPublicPackagesConfig> getPublicPackages() {
		return publicPackages;
	}

	public void setPublicPackages(List<NetBeansPublicPackagesConfig> publicPackages) {
		this.publicPackages = publicPackages;
	}
	
}
