package cz.cuni.amis.pogamut.ut2004.tournament.deathmatch;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import cz.cuni.amis.pogamut.ut2004.tournament.match.IUT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004BotConfig;
import cz.cuni.amis.pogamut.ut2004.tournament.match.UT2004NativeBotConfig;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapperConf;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Configuration for the {@link UT2004DeathMatchTournament} class.
 * <p><p>
 * The most interesting method is {@link UT2004DeathMatchTournamentConfig#cleanUp()} that is using recursion to generate all possible
 * matches for enlisted bots.
 * <p><p> 
 * Do not forget to use {@link UT2004DeathMatchTournamentConfig#setNumBotsInOneMatch(int)}!
 * <p><p>
 * THREAD-UNSAFE!
 *
 * @author Jimmy
 */
public class UT2004DeathMatchTournamentConfig {
	
	/**
	 * How many bots should be present in one match.
	 */
	protected int numBotsInOneMatch = 2;
	
	/**
	 * Unique id of the tournament.
	 */
	protected IToken tournamentId;
	
	/**
	 * Used as {@link UT2004DeathMatchConfig#setFragLimit(int)}.
	 */
	protected int fragLimit;
	
	/**
	 * Used as {@link UT2004DeathMatchConfig#setTimeLimit(int)}.
	 */
	protected int timeLimitInMinutes;
	
	protected UCCWrapperConf uccConf = new UCCWrapperConf();	
	
	/**
	 * Used as {@link UT2004DeathMatchConfig#setOutputDirectory(File)}.
	 * <p><p>
	 * Example: "results" + File.separator + "tournament"
	 */
	protected String outputDir;
		
	/**
	 * Custom (Pogamut) bots in the tournament.
	 */
	protected Map<IToken, UT2004BotConfig> bots = new HashMap<IToken, UT2004BotConfig>();
	
	/**
	 * Native bots in the tournament.
	 */
	protected Map<IToken, UT2004NativeBotConfig> nativeBots = new HashMap<IToken, UT2004NativeBotConfig>();

	/**
	 * Parameter-less constructor, don't forget to initialize everything!	 
	 **/
	public UT2004DeathMatchTournamentConfig() {
		uccConf.setGameType("BotDeathMatch");
		uccConf.setStartOnUnusedPort(true);
	}

	/**
	 * How many bots should be present in one match.
	 * @return
	 */
	public int getNumBotsInOneMatch() {
		return numBotsInOneMatch;
	}

	/**
	 * Used as {@link UT2004DeathMatchConfig#setFragLimit(int)}.
	 * 
	 * @return
	 */
	public int getFragLimit() {
		return fragLimit;
	}

	/**
	 * Used as {@link UT2004DeathMatchConfig#setTimeLimit(int)}.
	 * 
	 * @return
	 */
	public int getTimeLimitInMinutes() {
		return timeLimitInMinutes;
	}

	/**
	 * Used for unique identification of the tournament (optional).
	 * 
	 * @return
	 */
	public IToken getTournamentId() {
		return tournamentId;
	}
	
	/**
	 * Used as {@link UT2004DeathMatchConfig#setOutputDirectory(File)}.
	 * 
	 * @return
	 */
	public String getOutputDir() {
		return outputDir;
	}
	
	/**
	 * UccConfiguration used for running ucc.exe for respective matches.
	 * @return
	 */
	public UCCWrapperConf getUccConf() {
		return uccConf;
	}
	
	/**
	 * Map with custom (Pogamut) bots that are enlisted to the tournament.
	 * @return
	 */
	public Map<IToken, UT2004BotConfig> getBots() {
		return bots;
	}
	
	/**
	 * Map with native UT2004 bots that are enlisted to the tournament.
	 * @return
	 */
	public Map<IToken, UT2004NativeBotConfig> getNativeBots() {
		return nativeBots;
	}
	
	public void setNumBotsInOneMatch(int numBotsInOneMatch) {
		this.numBotsInOneMatch = numBotsInOneMatch;
	}

	public UT2004DeathMatchTournamentConfig setTournamentId(IToken id) {
		this.tournamentId = id;
		return this;
	}
	
	public UT2004DeathMatchTournamentConfig setTournamentId(String id) {
		this.tournamentId = Tokens.get(id);
		return this;
	}
	
	public UT2004DeathMatchTournamentConfig setFragLimit(int fragLimit) {
		this.fragLimit = fragLimit;
		return this;
	}

	public UT2004DeathMatchTournamentConfig setTimeLimitInMinutes(int timeLimitInMinutes) {
		this.timeLimitInMinutes = timeLimitInMinutes;
		return this;
	}

	public UT2004DeathMatchTournamentConfig setUnrealHome(String unrealHome) {
		this.uccConf.setUnrealHome(unrealHome);
		return this;
	}

	public UT2004DeathMatchTournamentConfig setMapName(String mapName) {
		this.uccConf.setMapName(mapName);
		return this;
	}

	public UT2004DeathMatchTournamentConfig setOutputDir(String outputDir) {
		this.outputDir = outputDir;
		return this;
	}
	
	public void setUccConf(UCCWrapperConf uccConf) {
		this.uccConf = uccConf;
	}
	
	public UT2004DeathMatchTournamentConfig setBots(Map<IToken, UT2004BotConfig> bots) {
		this.bots = bots;
		return this;
	}
	
	public UT2004DeathMatchTournamentConfig setNativeBots(Map<IToken, UT2004NativeBotConfig> nativeBots) {
		this.nativeBots = nativeBots;
		return this;
	}
	
	public UT2004DeathMatchTournamentConfig clearBots() {
		this.bots.clear();
		return this;
	}
	
	public UT2004DeathMatchTournamentConfig clearNativeBots() {
		this.nativeBots.clear();
		return this;
	}
	
	/**
	 * Adds NEW bot configuration into the object, checks whether there is no BotId clash (if so, throws an exception).
	 * @param bots
	 * @return
	 */
	public UT2004DeathMatchTournamentConfig addBot(UT2004BotConfig... bots) {
		if (bots == null) return this;
		for (UT2004BotConfig bot : bots) {
			NullCheck.check(bot.getBotId(), "bot.getBotId()");
			if (this.bots.containsKey(bot.getBotId())) {
				throw new PogamutException("Can't add another bot under the id " + bot.getBotId().getToken() + ", there is already an existing custom bot configuration under this ID. If you need to override it, use setBot().", this);
			}
			if (this.nativeBots.containsKey(bot.getBotId())) {
				throw new PogamutException("Can't add another bot under the id " + bot.getBotId().getToken() + ", there is already an existing native bot configuration under this ID. If you need to override it, use setBot().", this);
			}
			this.bots.put(bot.getBotId(), bot);
		}
		return this;
	}
	
	/**
	 * Sets bot configuration into the object, does not checks whether there is BotId clash.
	 * 
	 * @param bots
	 * @return
	 */
	public UT2004DeathMatchTournamentConfig setBot(UT2004BotConfig... bots) {
		if (bots == null) return this;
		for (UT2004BotConfig bot : bots) {
			NullCheck.check(bot.getBotId(), "bot.getBotId()");
			if (this.nativeBots.containsKey(bot.getBotId())) {
				throw new PogamutException("Can't add another bot under the id " + bot.getBotId().getToken() + ", there is already an existing native bot configuration under this ID. If you need to override it, use setBot().", this);
			}
			this.bots.put(bot.getBotId(), bot);
		}
		return this;
	}
	
	/**
	 * Adds NEW native bot configuration into the object, checks whether there is no BotId clash (if so, throws an exception).
	 * @param bots
	 * @return
	 */
	public UT2004DeathMatchTournamentConfig addNativeBot(UT2004NativeBotConfig... bots) {
		if (bots == null) return this;
		for (UT2004NativeBotConfig bot : bots) {
			NullCheck.check(bot.getBotId(), "bot.getBotId()");
			if (this.bots.containsKey(bot.getBotId())) {
				throw new PogamutException("Can't add another bot under the id " + bot.getBotId().getToken() + ", there is already an existing custom bot configuration under this ID. If you need to override it, use setBot().", this);
			}
			if (this.nativeBots.containsKey(bot.getBotId())) {
				throw new PogamutException("Can't add another bot under the id " + bot.getBotId().getToken() + ", there is already an existing native bot configuration under this ID. If you need to override it, use setNativeBot().", this);
			}
			this.nativeBots.put(bot.getBotId(), bot);
		}
		return this;
	}
	
	/**
	 * Sets native bot configuration into the object, does not checks whether there is BotId clash.
	 * @param bots
	 * @return
	 */
	public UT2004DeathMatchTournamentConfig setNativeBot(UT2004NativeBotConfig... bots) {
		if (bots == null) return this;
		for (UT2004NativeBotConfig bot : bots) {
			NullCheck.check(bot.getBotId(), "bot.getBotId()");
			if (this.bots.containsKey(bot.getBotId())) {
				throw new PogamutException("Can't add another bot under the id " + bot.getBotId().getToken() + ", there is already an existing custom bot configuration under this ID. If you need to override it, use setBot().", this);
			}
			this.nativeBots.put(bot.getBotId(), bot);
		}
		return this;
	}
	
	public boolean isNativeBot(IToken botId) {
		return nativeBots.containsKey(botId);
	}
	
	/**
	 * Returns bot configuration regardless it is custom (Pogamut) bot id or native UT2004 bot id.
	 * @param botId
	 * @return
	 */
	public IUT2004BotConfig getBotConfig(IToken botId) {
		UT2004BotConfig config = bots.get(botId);
		if (config != null) return config;
		return nativeBots.get(botId);
	}

	/**
	 * WARNING: Removes directory with tournament results. 
	 */
	public void cleanUp() {
		try {
			FileUtils.deleteQuietly(new File(outputDir));
		} catch (Exception e) {			
		}		
	}	
	
	/**
	 * Creates {@link UT2004DeathMatchConfig} for bots of ids from 'chosenBots'.
	 * <p><p>
	 * Created configuration IS NOT VALIDATED!
	 * 
	 * @param chosenBots
	 * @return
	 */
	public UT2004DeathMatchConfig createConfiguration(List<IToken> chosenBots) {
		UT2004DeathMatchConfig matchConfig = new UT2004DeathMatchConfig();
		
		StringBuffer matchId = new StringBuffer();
		boolean first = true;
		for (IToken token : chosenBots) {
			if (first) first = false;
			else matchId.append("-vs-");
			matchId.append(token.getToken());
		}
		matchConfig.setMatchId(matchId.toString());
		matchConfig.setOutputDirectory(new File(outputDir));
				
		matchConfig.setFragLimit(getFragLimit());
		matchConfig.setTimeLimit(getTimeLimitInMinutes()); // in minutes
		matchConfig.setUccConf(new UCCWrapperConf(uccConf));
		
		for (IToken botId : chosenBots) {
			if (isNativeBot(botId)) {
				matchConfig.addNativeBot(new UT2004NativeBotConfig(nativeBots.get(botId)));
			} else {
				matchConfig.addBot(new UT2004BotConfig(bots.get(botId)));
			}
		}
		
		return matchConfig;
	}
	
	protected void generate(List<UT2004DeathMatchConfig> result, int numBots, List<IToken> botIds, int startFrom, List<IToken> chosenBots) {
		if (startFrom >= botIds.size()) {
			// no more bots to use...
			return;
		}
		if (chosenBots.size() + (botIds.size() - startFrom) < numBots) {
			// we can't generate more matches as there is not enough bots for that
			return;
		}
		
		chosenBots.add(botIds.get(startFrom));
		
		if (chosenBots.size() == numBots) {
			// all bots have been chosen, create configuration
			result.add(createConfiguration(chosenBots));
		} else {
			// continue with the recursion
			for (int i = startFrom+1; i < botIds.size(); ++i) {
				generate(result, numBots, botIds, i, chosenBots);
			}
		}
		
		chosenBots.remove(chosenBots.size()-1);
	}
	
	protected UT2004DeathMatchConfig[] generateMatches(int numBots) {
		List<IToken> botIds = new ArrayList<IToken>(bots.size() + nativeBots.size());
		botIds.addAll(bots.keySet());
		Collections.sort(botIds, new Comparator<IToken>() {
			@Override
			public int compare(IToken o1, IToken o2) {
				if (o1 == null) {
					if (o2 == null) return 0;
					else return -1;
				}
				if (o2 == null) return 1;
				return o1.getToken().compareTo(o2.getToken());
			}
		});
		
		List<IToken> nativeBotIds = new ArrayList<IToken>(nativeBots.keySet());
		Collections.sort(nativeBotIds, new Comparator<IToken>() {
			@Override
			public int compare(IToken o1, IToken o2) {
				if (o1 == null) {
					if (o2 == null) return 0;
					else return -1;
				}
				if (o2 == null) return 1;
				return o1.getToken().compareTo(o2.getToken());
			}
		});
		
		botIds.addAll(nativeBotIds);

		List<IToken> chosenBots = new ArrayList<IToken>();
		
		List<UT2004DeathMatchConfig> result = new ArrayList<UT2004DeathMatchConfig>();
		for (int i = 0; i < bots.size(); ++i) {
			generate(result, numBots, botIds, i, chosenBots);
		}
		return result.toArray(new UT2004DeathMatchConfig[result.size()]);
	}
	
	/**
	 * This method will create all combinations of matche configs that consists of 'numBotsInOneMatch' bots, i.e., all 1v1 (numBotsInOneMatch == 2) or all 1v1v1 (numBotsInOneMatch == 3), etc.
	 * <p><p>
	 * It does not create matche configs where there are only native UT2004 bots, there is always at least 1 custom (Pogamut) bot 
	 * in every generated match configs.
	 * <p><p>
	 * Match configurations are NOT VALIDATED!
	 * 
	 * @param numBotsInOneMatch must be &gt;= 2
	 * @return
	 */
	public UT2004DeathMatchConfig[] createMatcheConfigs(int numBotsInOneMatch) {
		if (numBotsInOneMatch < 2) throw new IllegalArgumentException("numBotsInOneMatch = " + numBotsInOneMatch + " < 2 !!!");
		if (numBotsInOneMatch > bots.size() + nativeBots.size()) throw new IllegalArgumentException("numBotsInOneMatch = " + numBotsInOneMatch + " > " + (bots.size() + nativeBots.size()) + " = number of all (custom+native) enlisted bots");
		if (bots.size() == 0) throw new PogamutException("No custom (Pogamut) bots enlisted to the tournament!", this);
		return generateMatches(numBotsInOneMatch);
	}
	
	/**
	 * This method will create all combinations of matche configs that consists of 'numBotsInOneMatch' that is obtained
	 * via {@link UT2004DeathMatchTournamentConfig#getNumBotsInOneMatch()}.
	 * <p><p>
	 * It does not create matche configs where there are only native UT2004 bots, there is always at least 1 custom (Pogamut) bot 
	 * in every generated match configs.
	 * <p><p>
	 * Match configurations are NOT VALIDATED!
	 * 
	 * @return
	 */
	public UT2004DeathMatchConfig[] createMatcheConfigs() {
		return createMatcheConfigs(getNumBotsInOneMatch());
	}
	
}
