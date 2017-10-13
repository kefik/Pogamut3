package cz.cuni.amis.pogamut.base.agent.module;

public interface IAgentLogic<LOGIC_MODULE extends LogicModule> {

	/**
	 * Returns upper-estimation of {@link IAgentLogic#initializeLogic()} method running time in millis.
	 * @return
	 */
	public long getLogicInitializeTime();
	
	/**
	 * Called when the agent is started and just before the first {@link IAgentLogic#logic()} invocation.
	 * @param logicModule
	 */
	public void logicInitialize(LOGIC_MODULE logicModule);
	
	/**
	 * Method that is called only once before actual {@link IAgentLogic#logic()} is called.
	 */
	public void beforeFirstLogic();
	
	/**
	 * Called to perform the logic of the agent. If performed by the {@link LogicModule} or its descendant then
	 * it is called periodically.
	 */
	public void logic();
	
	/**
	 * Called whenever the {@link LogicModule} is stopping to end the logic.
	 */
	public void logicShutdown();
	
	/**
	 * Returns upper-estimation of {@link IAgentLogic#logicShutdown()} method running time in millis.
	 * @return
	 */
	public long getLogicShutdownTime();
	
}
