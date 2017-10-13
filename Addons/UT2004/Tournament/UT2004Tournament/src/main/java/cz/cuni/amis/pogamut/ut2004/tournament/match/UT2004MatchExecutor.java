package cz.cuni.amis.pogamut.ut2004.tournament.match;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.ut2004.tournament.match.result.UT2004MatchResult;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.token.IToken;

/**
 * {@link UT2004Match} executor that will execute one instance of match a time == NO PARALELIZATION HERE!
 * <p><p>
 * Please note that executing more than one match on the same UT2004 instance is not considered to be safe because:
 * <ol>
 * <li>GameBots2004.ini file is overwritten during the match execution == different GameBots2004.ini files in match configurations will pose problems</li>
 * <li>UT2004 (ucc.exe) sometimes hangs if executed multiple times (we do not know why, it should not act that way</li>
 * </ol>
 * <p>
 * If you seek parallelization, consider this:
 * <ol>
 * <li>Separate your matches that has the same GameBots2004.ini configuration (usually you are altering only frag limit and time limit, other things are
 * not configured through GameBots2004.ini</li>
 * <li>Create multiple UT2004MatchExecutors, each configured with matches of the same config</li>
 * <li>Use {@link ThreadPoolExecutor}, note that this class is implementing {@link Callable} so it is easy to create {@link FutureTask} out of it and submit
 * the {@link FutureTask} into {@link ThreadPoolExecutor#execute(Runnable)} and wait for results via {@link FutureTask#get()}.</li>
 * </ol>
 * 
 * @author Jimmy
 *
 * @param <MATCH>
 * @param <RESULT> must be of the same type that the MATCH is producing via {@link UT2004Match#execute()}.
 */
public class UT2004MatchExecutor<MATCH extends UT2004Match, RESULT extends UT2004MatchResult> implements Callable<Map<IToken, RESULT>>, Runnable {
	
	/**
	 * Matches that will be executed.
	 */
	private MATCH[] matches;

	/**
	 * Results of all matches, available through {@link UT2004MatchExecutor#getResults()}.
	 */
	private Map<IToken, RESULT> results = new HashMap<IToken, RESULT>();
	
	/**
	 * Map with exceptions that are stored here for matches which fails.
	 */
	private Map<IToken, Throwable> exceptions = new HashMap<IToken, Throwable>();

	/**
	 * Used for logging information about the match.
	 */
	private Logger log;
	
	public UT2004MatchExecutor(MATCH[] matches, Logger log) {
		this.log = log;		
		this.matches = matches;
		NullCheck.check(this.matches, "matches");
	}
	
	/**
	 * This map holds the results of respective matches. Immutable.
	 * @return
	 */
	public Map<IToken, RESULT> getResults() {
		return Collections.unmodifiableMap(results);
	}

	/**
	 * If some match fails, the exception reported is stored within this map. Immutable.
	 * @return
	 */
	public Map<IToken, Throwable> getExceptions() {
		return Collections.unmodifiableMap(exceptions);
	}

	@Override
	public Map<IToken, RESULT> call() throws Exception {
		run();
		return getResults();
	}

	@Override
	public void run() {
		if (log != null && log.isLoggable(Level.WARNING)) {
			log.warning("Executing " + matches.length + " matches!");
		}
		
		boolean exception = false;
		
		for (MATCH match : matches) {
			try {
				if (log != null && log.isLoggable(Level.WARNING)) {
					log.warning("Executing match: " + match.getMatchId().getToken());
				}
				UT2004MatchResult result = match.call();
				results.put(match.getMatchId(), (RESULT) result);
			} catch (Exception e) {
				if (log != null && log.isLoggable(Level.SEVERE)) {
					log.severe(ExceptionToString.process("Match[" + match.getMatchId().getToken() + "] failed with exception.", e));
				}
				exception = true;
				exceptions.put(match.getMatchId(), e);
			}			
		}
		
		if (exception) {
			if (log != null && log.isLoggable(Level.SEVERE)) {
				log.warning("Execution finished... SOME MATCHES FAILED!!!");
			}
		} else {
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("Execution finished! ALL MATCHES FINISHED SUCCESSFULLY!");
			}
		}
	}
	
}
