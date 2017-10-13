package cz.cuni.amis.pogamut.ut2004.tournament;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;

import cz.cuni.amis.utils.IniFile;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Ordinary {@link IniFile} that loads its defaults from classpath:/cz/cuni/amis/pogamut/ut2004/tournament/deathmatch/GameBots2004-Deathmatch.ini 
 * if not specified.
 * <p><p>
 * Additionally it provides definitions of common constants that applies to the GameBots2004.ini as well as handy shortcuts for setting
 * various interesting properties such as time limit or frag limit, etc.
 * 
 * DOES NOT WORK !!! 
 * 
 * UT2004.ini file contains multi-map of keys-values !!!
 * 
 * @author Jimmy
 */
public class UT2004Ini extends IniFile {

	//
	// SECTION
	// 
	
	public static final String Section_URL = "URL";
	public static final String Section_Engine_GameReplicationInfo = "Engine.GameReplicationInfo";
	public static final String Section_Engine_DemoRecDriver = "Engine.DemoRecDriver";
	
	//
	// PROPERTY KEYS
	//
	
	public static final String Key_Port = "Port";
	
	public static final String Key_ServerName = "ServerName";
	public static final String Key_ShortName = "ShortName";
	
	public static final String Key_DemoSpectatorClass = "DemoSpectatorClass";
	
	//
	// PROPERTY VALUES
	//
	
	public static final String Value_DemoSpectatorClass = "GameBots2004.GBDemoRecSpectator";
	
	/**
	 * Constructs Ini file with defaults taken from 'classpath:/cz/cuni/amis/pogamut/ut2004/tournament/deathmatch/GameBots2004-Deathmatch.ini'.
	 */
	public UT2004Ini() {
		this(true);
		
	}
	
	public UT2004Ini(boolean loadDefaults) {
		if (loadDefaults) {
			InputStream defaults = UT2004Ini.class.getResourceAsStream("/cz/cuni/amis/pogamut/ut2004/tournament/UT2004.ini");
			load(defaults);
		}		
	}
	
	/**
	 * Constructs GameBots2004Ini with defaults taken 'source' (file must exists!).
	 * 
	 * @param source
	 */
	public UT2004Ini(File source) {
		if (!source.exists()) {
			throw new PogamutException("File with defaults for UT2004.ini does not exist at: " + source.getAbsolutePath() + ".", this);
		}
		load(source);
	}
	
	public UT2004Ini(UT2004Ini ut2004Ini) {
		super(ut2004Ini);
	}

	/**
	 * Returns "server name", string that will appear when advertising the server via LAN.
	 * @return
	 */
	public String getServerName() {
		return getSection(Section_Engine_GameReplicationInfo).getOne(Key_ServerName);
	}
	
	/**
	 * Returns "short server name", dunno when is that used.
	 * @return
	 */
	public String getServerShortName() {
		return getSection(Section_Engine_GameReplicationInfo).getOne(Key_ShortName);
	}
	
	/**
	 * Sets (short) server name for the server that will get advertised via LAN.
	 * @param serverName
	 * @param shortName
	 */
	public void setServerName(String serverName, String shortName) {
		getSection(Section_Engine_GameReplicationInfo).set(Key_ServerName, serverName);
		getSection(Section_Engine_GameReplicationInfo).set(Key_ShortName,  shortName);
	}
	
	/**
	 * Returns port where UT2004 dedicated server will be listening at / announcing itself.
	 * @return
	 */
	public int getPort() {
		String port = getSection(Section_URL).getOne(Key_Port);
		try {
			return Integer.parseInt(port);
		} catch (Exception e) {
			return 0;
		}	
	}
	
	/**
	 * Sets port where UT2004 dedicated server will be listening at / announcing itself.
	 * @param port
	 */
	public void setPort(int port) {
		getSection(Section_URL).set(Key_Port, String.valueOf(port));
	}
	
	/**
	 * Returns UnrealScript class that is used for demo recording. 
	 * @return
	 */
	public String getDemoSpectatorClass() {
		return getSection(Section_Engine_DemoRecDriver).getOne(Key_DemoSpectatorClass);
	}
	
	/**
	 * Sets UnrealScript class that is used for demo recording. 
	 * @return
	 */
	public void setDemoSpectatorClass(String value) {
		getSection(Section_Engine_DemoRecDriver).set(Key_DemoSpectatorClass, value);
	}
	
}
