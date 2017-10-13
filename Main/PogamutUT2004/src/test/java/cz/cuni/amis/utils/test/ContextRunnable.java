package cz.cuni.amis.utils.test;

public interface ContextRunnable<CONTEXT extends TestContext> {
	
	public void run(CONTEXT ctx);

}
