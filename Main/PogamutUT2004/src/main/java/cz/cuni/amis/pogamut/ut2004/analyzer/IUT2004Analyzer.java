package cz.cuni.amis.pogamut.ut2004.analyzer;

import java.lang.ref.WeakReference;
import java.util.Map;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;

/**
 * Interface of the analyzer that should hook an {@link IUT2004AnalyzerObserver} agent onto
 * every bot inside UT2004 game sniffing info about the bot. 
 * 
 * @author Jimmy
 */
public interface IUT2004Analyzer extends IUT2004Server {
	
	/**
	 * Returns all observers currently owned by the analyzer.
	 * <p><p>
	 * The id can be obtained for instance from {@link Player#getId()} or {@link Self#getId()}
	 * or new one can be obtained from {@link String} via {@link UnrealId#get(String)}.
	 * 
	 * <p><p>
	 * NOTE: returns unmodifiable map that is a copy of the inner map inside the analyzer.
	 * 
	 * @return
	 */
	public Map<UnrealId, IUT2004AnalyzerObserver> getObservers();
	
	/**
	 * Hooks a listener that watches for creation/deletion of observers. ({@link WeakReference} is used to store the listener reference!)
	 * @param listener
	 */
	public void addListener(IAnalyzerObserverListener listener);

	/**
	 * Removes a listener that watches for creation/deletion of observers.
	 * @param listener
	 */
	public void removeListener(IAnalyzerObserverListener listener);
	
	/**
	 * Tests a listener whether it watches for creation/deletion of observers.
	 * @param listener
	 * @return
	 */
	public boolean isListening(IAnalyzerObserverListener listener);
	
}
