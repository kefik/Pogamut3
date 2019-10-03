package cz.cuni.amis.utils.rewrite.rewriter;

public class SubstitutionResult {
	
	private String result;
	
	private boolean applied;

	public SubstitutionResult(String result, boolean applied) {
		super();
		this.result = result;
		this.applied = applied;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public boolean isApplied() {
		return applied;
	}

	public void setApplied(boolean applied) {
		this.applied = applied;
	}

}
