package cz.cuni.amis.utils.rewrite.rewriter;

public interface ISubstitution {
	
	public boolean isMultiLine();
	
	public String substitute(String str);

}
