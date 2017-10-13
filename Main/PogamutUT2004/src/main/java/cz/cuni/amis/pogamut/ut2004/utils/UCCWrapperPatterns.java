package cz.cuni.amis.pogamut.ut2004.utils;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Patterns serves {@link UCCWrapper} to parse out information needed for successfull UCC startup.
 * @author Jimmy
 */
public class UCCWrapperPatterns implements Serializable {

	public final static Pattern BotPortPattern = Pattern.compile("BotServerPort:(\\d*)");
    public final static Pattern ControlPortPattern = Pattern.compile("ControlServerPort:(\\d*)");
    public final static Pattern ObserverPortPattern = Pattern.compile("ObservingServerPort:(\\d*)");
    public final static Pattern CommandletNotFoundPattern = Pattern.compile("Commandlet server not found");
    public final static Pattern MapNotFoundPattern = Pattern.compile("No maplist entries found matching the current command line.*");
    public final static Pattern MatchStartedPattern = Pattern.compile("START MATCH");
    public final static Pattern ExitingErrorPattern = Pattern.compile("Exiting due to error");
    public final static Pattern GameEndingPattern = Pattern.compile("^In EndGame$");

	
	/**
	 * Auto-generated. 
	 */
	private static final long serialVersionUID = 6964965802258744930L;
	
	protected Pattern botPortPattern = BotPortPattern;
    protected Pattern controlPortPattern = ControlPortPattern;
    protected Pattern observerPortPattern = ObserverPortPattern;
    protected Pattern commandletNotFoundPattern = CommandletNotFoundPattern;
    protected Pattern mapNotFoundPattern = MapNotFoundPattern;
    protected Pattern matchStartedPattern = MatchStartedPattern;
    protected Pattern exitingErrorPattern = ExitingErrorPattern;
    protected Pattern gameEndingPattern = GameEndingPattern;

    public UCCWrapperPatterns() {
    	
    }
    
	public UCCWrapperPatterns(Pattern botPortPattern,
			Pattern controlPortPattern, Pattern observerPortPattern,
			Pattern commandletNotFoundPattern, Pattern mapNotFoundPattern,
			Pattern matchStartedPattern, Pattern exitingErrorPattern) {
		super();
		this.botPortPattern = botPortPattern;
		this.controlPortPattern = controlPortPattern;
		this.observerPortPattern = observerPortPattern;
		this.commandletNotFoundPattern = commandletNotFoundPattern;
		this.mapNotFoundPattern = mapNotFoundPattern;
		this.matchStartedPattern = matchStartedPattern;
		this.exitingErrorPattern = exitingErrorPattern;
	}
	
	public UCCWrapperPatterns(UCCWrapperPatterns source) {
		this.botPortPattern = source.BotPortPattern;
		this.controlPortPattern = source.ControlPortPattern;
		this.observerPortPattern = source.ObserverPortPattern;
		this.commandletNotFoundPattern = source.CommandletNotFoundPattern;
		this.mapNotFoundPattern = source.MapNotFoundPattern;
		this.matchStartedPattern = source.MatchStartedPattern;
		this.exitingErrorPattern = source.ExitingErrorPattern;
	}

	public Pattern getBotPortPattern() {
		return botPortPattern;
	}

	public void setBotPortPattern(Pattern botPortPattern) {
		this.botPortPattern = botPortPattern;
	}

	public Pattern getControlPortPattern() {
		return controlPortPattern;
	}

	public void setControlPortPattern(Pattern controlPortPattern) {
		this.controlPortPattern = controlPortPattern;
	}

	public Pattern getObserverPortPattern() {
		return observerPortPattern;
	}

	public void setObserverPortPattern(Pattern observerPortPattern) {
		this.observerPortPattern = observerPortPattern;
	}

	public Pattern getCommandletNotFoundPattern() {
		return commandletNotFoundPattern;
	}

	public void setCommandletNotFoundPattern(Pattern commandletNotFoundPattern) {
		this.commandletNotFoundPattern = commandletNotFoundPattern;
	}

	public Pattern getMapNotFoundPattern() {
		return mapNotFoundPattern;
	}

	public void setMapNotFoundPattern(Pattern mapNotFoundPattern) {
		this.mapNotFoundPattern = mapNotFoundPattern;
	}

	public Pattern getMatchStartedPattern() {
		return matchStartedPattern;
	}

	public void setMatchStartedPattern(Pattern matchStartedPattern) {
		this.matchStartedPattern = matchStartedPattern;
	}

	public Pattern getExitingErrorPattern() {
		return exitingErrorPattern;
	}

	public void setExitingErrorPattern(Pattern exitingErrorPattern) {
		this.exitingErrorPattern = exitingErrorPattern;
	}

	public Pattern getGameEndingPattern() {
		return gameEndingPattern;
	}

	public void setGameEndingPattern(Pattern gameEndingPattern) {
		this.gameEndingPattern = gameEndingPattern;
	}
	
}