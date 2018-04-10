package cz.cuni.amis.utils.process;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class JarExecution extends ProcessExecution {
	
	public JarExecution(JarExecutionConfig config, Logger log) {
		super(config, log);
	}
	
	protected JarExecutionConfig getConfig() {
		return (JarExecutionConfig) config;
	}
	
	@Override
	protected List<String> getCommandParts() {
		List<String> commandParts = new ArrayList<String>();
		commandParts.add(config.getPathToProgram());
		
		if (config.getArgs() != null) {
			for (String arg : config.getArgs()) {
				commandParts.add(arg);
			}
		}
		
		commandParts.add("-jar");
		commandParts.add(getConfig().getJar());
		
		if (getConfig().getJavaArgs() != null) {
			for (String javaArg : getConfig().getJavaArgs()) {
				commandParts.add(javaArg);
			}
		}
		
		return commandParts;
	}

}
