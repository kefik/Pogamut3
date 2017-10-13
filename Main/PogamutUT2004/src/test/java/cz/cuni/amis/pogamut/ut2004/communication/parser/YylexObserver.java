package cz.cuni.amis.pogamut.ut2004.communication.parser;

import cz.cuni.amis.pogamut.base.communication.parser.impl.yylex.IYylexObserver;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.TestOutput;

@AgentScoped
public class YylexObserver implements IYylexObserver {
	
	private TestOutput output = new TestOutput("Yylex");

	@Override
	public void exception(Exception e, String info) {
		output.push(ExceptionToString.process(info, e));
	}

	@Override
	public void warning(String info) {
		output.push(info);		
	}
	
	public boolean isClear() {
		return output.isClear(true);
	}
	
	public void printOutput() {
		output.printOutput();
	}

}
