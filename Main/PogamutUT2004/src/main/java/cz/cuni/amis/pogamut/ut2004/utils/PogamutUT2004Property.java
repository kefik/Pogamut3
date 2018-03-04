package cz.cuni.amis.pogamut.ut2004.utils;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.visibility.model.VisibilityMatrix;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;

public enum PogamutUT2004Property {
	/**
	 * Whether the bot should have its JMX module enabled (must be enabled in order for the NetBeans plugin be working...).
	 * GameBots2004)
	 */
	POGAMUT_UT2004_BOT_JMX_ENABLED("pogamut.ut2004.bot.jmx.enabled", Boolean.class, Boolean.FALSE),
	
	/**
	 * Where the bot should connect to (hostname of the server running
	 * GameBots2004)
	 */
	POGAMUT_UT2004_BOT_HOST("pogamut.ut2004.bot.host", String.class, "localhost"),

	/**
	 * Where the bot should connect to (bot port of the GameBots2004).
	 */
	POGAMUT_UT2004_BOT_PORT("pogamut.ut2004.bot.port", Integer.class, 3000),

	/**
	 * Where the server should connect to (hostname of the server running
	 * GameBots2004)
	 */
	POGAMUT_UT2004_SERVER_HOST("pogamut.ut2004.server.host", String.class, "localhost"),

	/**
	 * Where the server should connect to (server port of the GameBots2004)
	 */
	POGAMUT_UT2004_SERVER_PORT("pogamut.ut2004.server.port", Integer.class, 3001),

	/**
	 * Where the observer should connect to (hostname of the server running
	 * GameBots2004)
	 */
	POGAMUT_UT2004_OBSERVER_HOST("pogamut.ut2004.observer.host", String.class, "localhost"),

	/**
	 * Where the observer should connect to (observer port of the GameBots2004)
	 */
	POGAMUT_UT2004_OBSERVER_PORT("pogamut.ut2004.observer.port", Integer.class, 3002),

	/** Path to the Unreal home dir. */
	POGAMUT_UNREAL_HOME("pogamut.ut2004.home", String.class, null),

	/** Should tests use external UCC instance or they will run internal one? */
	POGAMUT_UNREAL_TEST_EXT_SERVER("pogamut.test.useExternalUCC", Boolean.class, false), 
	
	/**
	 * Whether UT2004PathExecutor is using SetRoute command (causes RED LINE to appear in UT2004 GUI when enablind "display bot routes").
	 */
	POGAMUT_UT2004_PATH_EXECUTOR_SEND_SET_ROUTE("pogamut.ut2004.path_executor.send_set_route", Boolean.class, false),
	
	/**
	 * Directory where to search for {@link VisibilityMatrix}es for respective maps.
	 */
	POGAMUT_UT2004_VISIBILITY_DIRECTORY("pogamut.ut2004.visibility.dir", String.class, "."),
	
	/**
	 * Boolean parameter (true/false) that affects how {@link UT2004Bot} works with {@link IUT2004BotController#getInitializeCommand()}.<br/>
	 * False == provide parameters only if NOT specified<br/>
	 * True == always set the parameters into {@link Initialize} command (i.e., override any existing settings). 
	 */
	POGAMUT_UT2004_BOT_INIT_OVERRIDE_PARAMS("pogamut.ut2004.bot.init.override", Boolean.class, false),
	
	/**
	 * Integer parameter (0, 1, 2, 3, 255) that sets team the bot should join into. See {@link AgentInfo} for team constants (e.g. {@link AgentInfo#TEAM_RED}) 
	 */
	POGAMUT_UT2004_BOT_INIT_TEAM("pogamut.ut2004.bot.init.team", Integer.class, 255),
	
	/**
	 * Integer parameter (0, 1, 2, 3, 4, 5, 6, 7) that sets skill level for the bot, see {@link Initialize#setDesiredSkill(Integer)}.
	 */
	POGAMUT_UT2004_BOT_INIT_SKILL("pogamut.ut2004.bot.init.skill", Integer.class, 5),
	
	/**
	 * String parameter that sets the skin for the bot, see {@link Initialize#setSkin(String)}.
	 */
	POGAMUT_UT2004_BOT_INIT_SKIN("pogamut.ut2004.bot.init.skin", String.class, null),
	
	/**
	 * String parameter that sets the name for the bot, see {@link Initialize#setName(String)}.
	 */
	POGAMUT_UT2004_BOT_INIT_NAME("pogamut.ut2004.bot.init.name", String.class, null),	
	;

	private String key;
	private Class type;
	private Object defaultValue;

	private PogamutUT2004Property(String key, Class type, Object defaultValue) {
		this.key = key;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public String getKey() {
		return key;
	}

	public String toString() {
		return key;
	}
	
	public Object getValue() {
		String value = Pogamut.getPlatform().getProperty(getKey());
		if (value == null) return getDefaultValue();
		if (type == String.class) {
			return value;
		} else
		if (type == Integer.class) {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				return getDefaultValue();
			}
		} else
		if (type == Float.class) {
			try {
				return Float.parseFloat(value);
			} catch (Exception e) {
				return getDefaultValue();
			}
		} else
		if (type == Double.class) {
			try {
				return Double.parseDouble(value);
			} catch (Exception e) {
				return getDefaultValue();
			}
		} else
		if (type == Boolean.class) {
			if (value.toLowerCase().equals("true")) return true;
			return false;
		} else {
			throw new RuntimeException("Unsupported property type: " + type.getSimpleName());
		}
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}
}
