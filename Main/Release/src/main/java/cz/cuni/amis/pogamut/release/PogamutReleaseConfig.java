package cz.cuni.amis.pogamut.release;

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

import cz.cuni.amis.netbeans.publicpkgs.NetBeansPublicPackagesConfig;
import cz.cuni.amis.pogamut.release.archetype.ArchetypeRefreshConfig;
import cz.cuni.amis.utils.process.ProcessExecutionConfig;
import cz.cuni.amis.utils.rewrite.IncludeDir;
import cz.cuni.amis.utils.rewrite.RewriteFilesConfig;

@XStreamAlias(value="PogamutReleaseConfig")
public class PogamutReleaseConfig {
	
	@XStreamAlias(value="id")
	@XStreamAsAttribute
	private String id;
	
	@XStreamAlias(value="baseDir")
	@XStreamAsAttribute
	private File baseDir;
	
	@XStreamImplicit
	private List<PogamutReleaseStep> steps;
	
	public static PogamutReleaseConfig loadXML(File xmlFile) {
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
		xstream.alias(PogamutReleaseConfig.class.getAnnotation(XStreamAlias.class).value(), PogamutReleaseConfig.class);
		Object obj = xstream.fromXML(reader);
		try {
			reader.close();
		} catch (IOException e) {
		}
		if (obj == null || !(obj instanceof PogamutReleaseConfig)) {
			throw new RuntimeException("file " + xmlFile.getAbsolutePath() + " doesn't contain a xml with PogamutReleaseConfig");
		}
		return (PogamutReleaseConfig)obj;
	}
	
	private PogamutReleaseConfig readResolve() {
		if (id == null) id = "PogamutRelease";
		
		if (baseDir == null) baseDir = new File(".");
		
		String bd = System.getenv("POGAMUT_RELEASE_BASEDIR");
		if (bd != null && bd.length() != 0) {
			baseDir = new File(bd);
		} else {
			bd = System.getProperty("pogamut.release.basedir");
			if (bd != null && bd.length() != 0) {
				baseDir = new File(bd);
			}
		}
		
		if (steps == null) steps = new ArrayList<PogamutReleaseStep>();
		for (PogamutReleaseStep step : steps) {
			if (step.getRewriteFiles() != null) {
				for (RewriteFilesConfig config : step.getRewriteFiles()) {
					if (config.getGlobals().getDir() != null) {
						if (!config.getGlobals().getDir().isAbsolute()) {						
							config.getGlobals().setDir(new File(baseDir, config.getGlobals().getDir().getPath()));
						}
					}
					for (IncludeDir inc : config.getDirs()) {
						if (inc.getDir() != null) {
							if (!inc.getDir().isAbsolute()) {
								inc.setDir(new File(baseDir, inc.getDir().getPath()));
							}
						}
					}
				}
			}
			if (step.getProcessExecution() != null) {
				for (ProcessExecutionConfig config : step.getProcessExecution()) {
					if (config.getExecutionDir() != null) {
						if (!new File(config.getExecutionDir()).isAbsolute()) {
							config.setExecutionDir(new File(baseDir, config.getExecutionDir()).getAbsolutePath());
						}
					}
				}
			}
			if (step.getArchetypeRefresh() != null) {
				for (ArchetypeRefreshConfig config : step.getArchetypeRefresh()) {
					if (config.getSourceDir() != null) {
						if (!new File(config.getSourceDir()).isAbsolute()) {
							config.setSourceDir(new File(baseDir, config.getSourceDir()).getAbsolutePath());
						}
					}
					if (config.getTargetDir() != null) {
						if (!new File(config.getTargetDir()).isAbsolute()) {
							config.setTargetDir(new File(baseDir, config.getTargetDir()).getAbsolutePath());
						}
					}
				}
			}
			if (step.getPublicPackages() != null) {
				for (NetBeansPublicPackagesConfig config : step.getPublicPackages()) {
					if (config.getTargetPom() != null) {
						config.setTargetPom(new File(baseDir, config.getTargetPom()).getAbsolutePath());
					}
					if (config.getDirs() != null) {
						for (cz.cuni.amis.netbeans.publicpkgs.IncludeDir inc : config.getDirs()) {
							if (inc.getDir() != null) {
								if (!inc.getDir().isAbsolute()) {
									inc.setDir(new File(baseDir, inc.getDir().getPath()));
								}
							}
						}
					}
				}
			}
		}
		return this;
	}
	
	public String getId() {
		return id;
	}

	public List<PogamutReleaseStep> getSteps() {
		return steps;
	}

	public void setSteps(List<PogamutReleaseStep> steps) {
		this.steps = steps;
	}
	
}
