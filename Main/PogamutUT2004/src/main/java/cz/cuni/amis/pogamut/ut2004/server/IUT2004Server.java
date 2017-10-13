package cz.cuni.amis.pogamut.ut2004.server;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import cz.cuni.amis.pogamut.unreal.server.IUnrealServer;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.pogamut.unreal.bot.impl.NativeUnrealBotAdapter;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MapList;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Mutator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.map.UT2004Map;
import cz.cuni.amis.pogamut.unreal.server.exception.MapChangeException;
import cz.cuni.amis.utils.collections.ObservableCollection;
import cz.cuni.amis.utils.flag.Flag;

// TODO: [Ruda] specify the UT2004 server interface
public interface IUT2004Server extends IUnrealServer<IUT2004Bot> {
    
	/**
	 * Sets the address of the server to different location - does not automatically reconnect,
	 * use {@link IUT2004Server#stop()} and {@link IUT2004Server#start()}.
	 * @param address
	 */
	public void setAddress(String host, int port);
	
    /**
     * @return List of all maps available on the UT server
     */
    public Collection<MapList> getAvailableMaps();
  
// GameInfo doesn't exist anymore, JSimlo changed it into something else
//    public GameInfo getGameInfo();
    
    /**
     * The flag raises events even when the game speed was changed by another 
     * UTServer instance or directly in game.
     * @return Speed of the game.
     */
    public Flag<Double> getGameSpeedFlag();
    
    /** 
     * @return Name of the current map.
     */
    public String getMapName();
    
    /**
     * Method that initiates map-change. It returns future that describes the result.
     * <p><p>
     * Note that the object must restart itself in order to reconnect to the new server. 
     * 
     * @param map
     */
    public Future<Boolean> setGameMap(String map) throws MapChangeException;
    
    /**
     * Returns list of all players connected to the game server. The difference 
     * compared to the getAgents() method is that this method can return even the
     * native bots, human players etc. 
     * <p><p>
     * Custom-bots (created by Pogamut platform) can be recognized. They have non-null+non-empty {@link Player#getJmx()} field.
     * 
     * @return List of all players on the server.
     */
    public ObservableCollection<Player> getPlayers();


    /**
     * Returns list of all non pogamut players connected to the game server. Collection
     * contains NativeBotAdapter classes, this means that you can deal with the
     * players like with Pogamut agents.
     * @
     * @return List of all players on the server.
     */
    public ObservableCollection<? extends NativeUnrealBotAdapter> getNativeAgents();

    /**
     * Reeturns list of all mutators available on the server. Mutators can be 
     * used to modify the game (eg. disable weapons, change game speed).
     * @return List of all mutators available on the server.
     */
    public List<Mutator> getMutators();

    /**
     * Connects a UT native bot to the current map.
     * @param botName
     */
    public void connectNativeBot(String botName, String botType, int team);

    /**
     * Get current map from the server
     * @return Map of current level.
     */
    public UT2004Map getMap();

}