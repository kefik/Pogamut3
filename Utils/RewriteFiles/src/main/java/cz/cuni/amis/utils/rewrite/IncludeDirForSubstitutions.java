package cz.cuni.amis.utils.rewrite;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import cz.cuni.amis.utils.rewrite.rewriter.Const;
import cz.cuni.amis.utils.rewrite.rewriter.FixLineEndings;
import cz.cuni.amis.utils.rewrite.rewriter.Substitution;

@XStreamAlias(value="includeDir")
public class IncludeDirForSubstitutions extends IncludeDir {
	
	@XStreamAlias("substitutions")
	private List<Substitution> substitutions;
	
	@XStreamAlias("fixLineEndings")
	private FixLineEndings lineEndings = null;
	
	@XStreamAsAttribute
	private Boolean applyMaxOneRuleOnly = null;	
		
	public IncludeDirForSubstitutions() {		
		substitutions = new ArrayList<Substitution>();
	}
	
	public IncludeDirForSubstitutions(IncludeDir source) {
		super(source);
		substitutions = new ArrayList<Substitution>();
	}
	
	public IncludeDirForSubstitutions(IncludeDirForSubstitutions orig, IncludeDirForSubstitutions globals) {
		super(orig, globals);
		lineEndings = orig.lineEndings;
		if (lineEndings == null) lineEndings = globals.lineEndings;
		applyMaxOneRuleOnly = orig.applyMaxOneRuleOnly;
		if (applyMaxOneRuleOnly == null) applyMaxOneRuleOnly = globals.applyMaxOneRuleOnly;
		substitutions = new ArrayList<Substitution>();
		if (orig.substitutions != null) {
			for (Substitution subst : orig.substitutions) {
				this.substitutions.add(subst);
			}
		}
		if (globals.substitutions != null) {
			for (Substitution subst : globals.substitutions) {
				this.substitutions.add(subst);
			}
		}
	}
	
	private IncludeDirForSubstitutions readResolve() {
		if (getSubdirs() == null) setSubdirs(true);
		if (getExcludeDirs() == null) setExcludeDirs(new ArrayList<String>(0));
		if (getIncludeFiles() == null) setIncludeFiles(new ArrayList<String>(0));
		if (getExcludeFiles() == null) setExcludeFiles(new ArrayList<String>(0));
		return this;
	}
	
	public FixLineEndings getLineEndings() {
		return lineEndings;
	}

	public void setLineEndings(FixLineEndings lineEndings) {
		this.lineEndings = lineEndings;
	}
	
	public List<Substitution> getSubstitutions() {
		return substitutions;
	}

	public void setSubstitutions(List<Substitution> substitutions) {
		this.substitutions = substitutions;
	}
	
	public Boolean isApplyMaxOneRuleOnly() {
		return applyMaxOneRuleOnly;
	}

	public void setApplyMaxOneRuleOnly(Boolean applyMaxOneRuleOnly) {
		this.applyMaxOneRuleOnly = applyMaxOneRuleOnly;
	}

	@Override
	public void initialize() {
		super.initialize();
		if (substitutions == null) substitutions = new ArrayList<Substitution>(0);
	}
	
	@Override
	public String toString() {
		return toString("IncludeDir", "");
	}
	
	@Override
	public String toString(String name, String prefix) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		
		sb.append(prefix + name + "[");
		
		sb.append(Const.NEW_LINE + prefix + "  dir           = " + getDir() + (getDir() != null ? " --> " + getDir().getAbsolutePath() : ""));
		
		sb.append(Const.NEW_LINE + prefix + "  subdirs       = " + getSubdirs());
		
		sb.append(Const.NEW_LINE + prefix + "  includeFiles  = ");
		if (getIncludeFiles() == null) {
			sb.append("null");
		} else {
			for (String file : getIncludeFiles()) {
				if (first) first = false;
				else sb.append(", ");
				sb.append(file);
			}
		}
		first = true;
		
		sb.append(Const.NEW_LINE + prefix + "  excludeFiles  = ");
		if (getExcludeFiles() == null) {
			sb.append("null");
		} else {
			for (String file : getExcludeFiles()) {
				if (first) first = false;
				else sb.append(", ");
				sb.append(file);
			}
		}
		first = true;
		
		sb.append(Const.NEW_LINE + prefix + "  excludeDirs   = ");
		if (getExcludeDirs() == null) {
			sb.append("null");
		} else {
			for (String file : getExcludeDirs()) {
				if (first) first = false;
				else sb.append(", ");
				sb.append(file);
			}
		}
		first = true;
		
		sb.append(Const.NEW_LINE + prefix + "  substitutions = ");
		if (substitutions == null) {
			sb.append("null");
		} else {
			for (Substitution subst : substitutions) {
				sb.append(Const.NEW_LINE);
				sb.append(prefix);
				sb.append("    ");
				sb.append(subst);
			}
		}
		
		sb.append(Const.NEW_LINE + prefix + "  fixLineEndings = " + String.valueOf(lineEndings));
		
		sb.append(Const.NEW_LINE + prefix + "  applyMaxOneRuleOnly = " + String.valueOf(applyMaxOneRuleOnly));
		
		sb.append(Const.NEW_LINE + prefix + "]");
		return sb.toString();
	}
	
}
