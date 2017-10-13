package cz.cuni.amis.pogamut.ut2004.tournament.deathmatch;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.commons.io.FileUtils;

import cz.cuni.amis.pogamut.base.utils.logging.ILogPublisher;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004MatchExecutor;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.token.IToken;

/**
 * Class that performs the tournament as described by {@link UT2004DeathMatchTournamentConfig}.
 * <p><p>
 * THREAD-UNSAFE!
 * 
 * @author Jimmy
 */
public class UT2004DeathMatchTournament implements Callable<Map<IToken, UT2004DeathMatchResult>>, Runnable {

	private LogCategory log;
	
	private UT2004DeathMatchTournamentConfig config;
	
	private UT2004MatchExecutor<UT2004DeathMatch, UT2004DeathMatchResult> executor;

	public UT2004DeathMatchTournament(UT2004DeathMatchTournamentConfig config, LogCategory log) {
		this.log = log;
		this.config = config;
		NullCheck.check(this.config, "config");
	}
	
	/**
	 * This map holds the results of respective matches. Immutable.
	 * @return
	 */
	public Map<IToken, UT2004DeathMatchResult> getResults() {
		if (executor == null) throw new PogamutException("There are no results available, you have to run tournament first!", this);
		return executor.getResults();
	}

	/**
	 * If some match fails, the exception reported is stored within this map. Immutable.
	 * @return
	 */
	public Map<IToken, Throwable> getExceptions() {
		if (executor == null) throw new PogamutException("There are no results available, you have to run tournament first!", this);
		return executor.getExceptions();
	}

	@Override
	public Map<IToken, UT2004DeathMatchResult> call() throws Exception {
		return executor.getResults();
	}

	@Override
	public synchronized void run() {
		UT2004DeathMatchConfig[] configs = config.createMatcheConfigs();
		UT2004DeathMatch[] matches = new UT2004DeathMatch[configs.length];
		for (int i = 0; i < configs.length; ++i) {
			matches[i] = new UT2004DeathMatch(configs[i], new LogCategory(configs[i].getMatchId().getToken()));
			matches[i].getLog().addHandler(new ILogPublisher() {

				@Override
				public void close() throws SecurityException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void flush() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void publish(LogRecord record) {
					if (UT2004DeathMatchTournament.this.log != null) {
						UT2004DeathMatchTournament.this.log.log(record);
					}
				}
				
			});			
		}
		executor = new UT2004MatchExecutor<UT2004DeathMatch, UT2004DeathMatchResult>(matches, log);
		executor.run();
	}

	/**
	 * WARNING: this method will delete the whole directory where results are stored! IT WILL DELETE IT COMPLETELY!
	 * DO NOT USE IT ON A WHIM... be sure you're using separate directories for all matches.
	 */
	public void cleanUp() {
		if (log != null && log.isLoggable(Level.WARNING)) log.warning("Cleaning up! Deleting: " + getOutputPath().getAbsolutePath());
		FileUtils.deleteQuietly(getOutputPath());
	}

	/**
	 * Returns directory where all tournament matches will be output.
	 * @return
	 */
	public File getOutputPath() {
		return new File(config.getOutputDir());
	}
}
