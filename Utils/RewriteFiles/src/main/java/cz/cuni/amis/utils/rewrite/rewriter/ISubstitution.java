package cz.cuni.amis.utils.rewrite.rewriter;

public interface ISubstitution {
	
	public boolean isMultiLine();
	
	public SubstitutionResult substitute(String str);

}
