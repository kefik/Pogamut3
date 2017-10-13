package cz.cuni.amis.pogamut.unreal.server;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import cz.cuni.amis.pogamut.base.agent.IGhostAgent;
import cz.cuni.amis.pogamut.base.server.IWorldServer;
import cz.cuni.amis.pogamut.unreal.bot.IUnrealBot;
import cz.cuni.amis.pogamut.unreal.communication.messages.gbinfomessages.IMapList;
import cz.cuni.amis.pogamut.unreal.communication.messages.gbinfomessages.IMutator;
import cz.cuni.amis.pogamut.unreal.communication.messages.gbinfomessages.IPlayer;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealMap;
import cz.cuni.amis.pogamut.unreal.server.exception.MapChangeException;

import cz.cuni.amis.utils.collections.ObservableCollection;
import cz.cuni.amis.utils.flag.Flag;
import java.io.IOException;

public interface IUnrealServer<BOT extends IUnrealBot> extends IWorldServer<BOT>, IGhostAgent {
    
	/**
	 * Sets the address of the server to different location - does not automatically reconnect,
	 * use {@link IUT2004Server#stop()} and {@link IUT2004Server#start()}.
	 * @param address
	 */
	public void setAddress(String host, int port);
	
    /**
     * @return List of all maps available on the UT server
     */
    public Collection<? extends IMapList> getAvailableMaps();
  
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
     * @return List of all players on the server.
     */
    public ObservableCollection<? extends IPlayer> getPlayers();

    /**
     * Returns list of all non pogamut players connected to the game server. Collection
     * contains NativeBotAdapter classes, this means that you can deal with the
     * players like with Pogamut agents.
     * @
     * @return List of all players on the server.
     */
    public ObservableCollection<? extends IUnrealBot> getNativeAgents();

    /**
     * Reeturns list of all mutators available on the server. Mutators can be 
     * used to modify the game (eg. disable weapons, change game speed).
     * @return List of all mutators available on the server.
     */
    public List<? extends IMutator> getMutators();

    /**
     * Connects a UT native bot to the current map.
     * @param botName
     */
    public void connectNativeBot(String botName, String botType);

    /**
     * Get current map from the server
     * @return Map of current level.
     */
    public IUnrealMap getMap();

}