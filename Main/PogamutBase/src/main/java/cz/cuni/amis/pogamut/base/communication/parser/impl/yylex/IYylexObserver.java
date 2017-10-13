package cz.cuni.amis.pogamut.base.communication.parser.impl.yylex;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.ExceptionToString;
import java.util.logging.Level;

/**
 * Interface for reporting of Yylex exceptions.
 * @author Jimmy
 */
public interface IYylexObserver {

	/**
	 * Called whenever exception occures in the Yylex caused by OUR miscoding.
	 * (Note that we're not hacking Yylex code to report all it's exceptions
	 * through this observer - we will call this observer only from our 
	 * parts of the code from yylex.java)
	 * 
	 * @param e never null
	 * @param info never null
	 */
	public void exception(Exception e, String info);
	
	/**
	 * Called when some mischief happens but the parser recovers. (Like
	 * wrong parsing of the message, skipping to next...)
	 * @param info
	 */
	public void warning(String info);
		
	/**
	 * Default implementation of the IYylexObserver logging everything into AgentLogger.platform()
	 * log category.
	 *  
	 * @author Jimmy
	 */
	@AgentScoped
	public static class LogObserver implements IYylexObserver {
		
		private LogCategory log;
		
		@Inject
		public LogObserver(IAgentLogger agentLogger) {
			log = agentLogger.getCategory("Yylex");
		}

		@Override
		public void exception(Exception e, String info) {
			if (log.isLoggable(Level.SEVERE)) log.severe(ExceptionToString.process(info, e));
		}

		@Override
		public void warning(String info) {
			if (log.isLoggable(Level.WARNING)) log.warning(info);
		}
		
	}

}
