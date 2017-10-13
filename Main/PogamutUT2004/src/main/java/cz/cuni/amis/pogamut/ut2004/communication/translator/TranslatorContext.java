package cz.cuni.amis.pogamut.ut2004.communication.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemTranslator;
import java.util.logging.Level;

/**
 * Translator context serves as the context during the FSM work. It provides respective fsm states an access
 * to the instances of:
 * <ul>
 * <li>event queue - object where we're sending new world events</li>
 * <li>item translator - an object with factories of respective items</li>
 * <li>log</li>
 * </ul>
 * <p><p>
 * WARNING: the same context is used for Bot, ControlServer as well as Observer!
 * 
 * @author Jimmy
 */
public class TranslatorContext {
    
    private IWorldEventQueue events;
    private ItemTranslator itemTranslator;
    private Logger log;
    
	private List<NavPointNeighbourLink> neighbours = new ArrayList<NavPointNeighbourLink>();
	private Map<UnrealId, NavPoint> navPoints = new HashMap<UnrealId, NavPoint>();
	private Map<UnrealId, Item> items = new HashMap<UnrealId, Item>();
	private Map<UnrealId, List<NavPointNeighbourLink>> links = new HashMap<UnrealId, List<NavPointNeighbourLink>>();
    
    public TranslatorContext(IWorldEventQueue events, ItemTranslator itemTranslator, Logger log) {
        this.events = events;
        this.itemTranslator = itemTranslator;
        this.log = log;
    }
    
    public void reset() {
    	neighbours = new ArrayList<NavPointNeighbourLink>();
    	navPoints = new HashMap<UnrealId, NavPoint>();
    	items = new HashMap<UnrealId, Item>();
    	links = new HashMap<UnrealId, List<NavPointNeighbourLink>>();
    }
    
    public IWorldEventQueue getEventQueue() {
        return events;
    }
    
    public ItemTranslator getItemTranslator() {
        return itemTranslator;
    }
    
    public Logger getLogger() {
    	return log;
    }
    
    public List<NavPointNeighbourLink> getNeighbours() {
    	return neighbours;
    }
    
    public void setNeighbours(List<NavPointNeighbourLink> neighs) {
    	this.neighbours  = neighs;
    }
    
    public void setNavPointLinks(Map<UnrealId, List<NavPointNeighbourLink>> links) {
    	this.links = links;
    }
    
    public Map<UnrealId, List<NavPointNeighbourLink>> getNavPointLinks() {
		return links;
	}

	public void setNavPoints(Map<UnrealId, NavPoint> navPoints) {
    	this.navPoints = navPoints;
    }
    
    public Map<UnrealId, NavPoint> getNavPoints() {
    	return navPoints ;
    }

	public void setItems(Map<UnrealId, Item> items) {
		this.items = items;		
	}

	public Map<UnrealId, Item> getItems() {
		return items;
	}
	
	/**
	 * Reads getNavPointsLinks() and alters navpoints incoming and outgoing edges.
	 * <p><p>
	 * Does nothing if getNavPoints() or getNavPointsLinks() returns null.
	 */
	public void processNavPointLinks() {
		if (getNavPoints() == null || getNavPointLinks() == null) {
			return;
		}
		
		if (getLogger().isLoggable(Level.FINE)) getLogger().fine("Processing NavPoints<->Links.");
		for (NavPoint navPoint : getNavPoints().values()) {
			navPoint.getIncomingEdges().clear();
			navPoint.getOutgoingEdges().clear();
		}
		for(NavPoint navPoint : getNavPoints().values()) {
			List<NavPointNeighbourLink> links = getNavPointLinks().get(navPoint.getId());
			List<NavPointNeighbourLink> fixedLinks = new ArrayList<NavPointNeighbourLink>(links.size());
			for (NavPointNeighbourLink link : links) {
				NavPoint targetNavPoint = navPoints.get(link.getId());
				NavPointNeighbourLink fixedLink = new NavPointNeighbourLink(link, navPoint, targetNavPoint );
				fixedLinks.add(fixedLink);
				navPoint.getOutgoingEdges().put(fixedLink.getId(), fixedLink);
				targetNavPoint.getIncomingEdges().put(navPoint.getId(), fixedLink);
			}
			getNavPointLinks().put(navPoint.getId(), fixedLinks);
		}
		if (getLogger().isLoggable(Level.FINE)) getLogger().fine("Processing finished.");
	}
	
	/**
	 * Interconnects instances of NavPoint and Item from getNavPoints() and getItems() map.
	 * <p><p>
	 * Note that new instances of nav points are created during this process thus
	 * the getNavPoints() will return a new map after this method finishes.
	 * <p><p>
	 * Does nothing if getNavPoints() or getItems() returns null.
	 */
	public void processNavPointsAndItems() {
		if (getItems() == null || getNavPoints() == null) {
			return;
		}
		
		if (getLogger().isLoggable(Level.FINE)) getLogger().fine("Processing NavPoints<->Items.");
		
		Map<UnrealId, Item> items = getItems();
		
		for (NavPoint navPoint : getNavPoints().values()) {
			if (navPoint.getItem() != null) {
				Item item = items.get(navPoint.getItem());
				if (item == null) {
					if (getLogger().isLoggable(Level.WARNING)) getLogger().warning("Item of id " + navPoint.getItem().getStringId() + " does not exist, referenced from navpoint " + navPoint.getId().getStringId() + ".");
					continue;
				}
				if (navPoint instanceof NavPointMessage) {
					((NavPointMessage)navPoint).setItemInstance(item);
				}
				if (item instanceof ItemMessage) {
					((ItemMessage)item).setNavPoint(navPoint);
				}
			}			
		}
		
		if (getLogger().isLoggable(Level.FINE)) getLogger().fine("Processing finished.");
	}
	
}
