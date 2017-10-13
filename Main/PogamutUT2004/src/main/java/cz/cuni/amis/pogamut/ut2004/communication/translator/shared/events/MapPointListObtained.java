package cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.amis.pogamut.base.communication.translator.event.WorldEventIdentityWrapper;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

public class MapPointListObtained extends TranslatorEvent {

	// initialization of following fields is not neccessary but test Test01_WorldMessageTranslator depends on them
	private Map<UnrealId, NavPoint> navPoints;
	private Map<UnrealId, Item> items;

	public MapPointListObtained(Map<UnrealId, NavPoint> navPoints, Map<UnrealId, Item> items, long simTime) {
		super(simTime);
		this.navPoints = navPoints;
		this.items = items;		
	}
	
	private MapPointListObtained readResolve() {
		if (navPoints == null) navPoints = new HashMap<UnrealId, NavPoint>();
		if (items == null) items = new HashMap<UnrealId, Item>();
		return this;
	}

	public Map<UnrealId, NavPoint> getNavPoints() {
		return navPoints;
	}
	
	public Map<UnrealId, Item> getItems() {
		return items;
	}

	public String toString() {
		return "MapPointListObtained[navPoints.size() = " + (navPoints == null ? "null" : navPoints.size()) + ", items.size() = "+ (items == null ? "null" : items.size()) +"]";
	}

}