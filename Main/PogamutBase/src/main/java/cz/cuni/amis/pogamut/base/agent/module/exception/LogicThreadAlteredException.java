package cz.cuni.amis.pogamut.base.agent.module.exception;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.module.LogicModule;
import cz.cuni.amis.pogamut.base.component.exception.ComponentException;

public class LogicThreadAlteredException extends ComponentException {

	public LogicThreadAlteredException(String logicRunnerName, Logger log, LogicModule origin) {
		super(logicRunnerName + ": Logic thread altered! Shutdown not called!", log, origin);
	}

}
