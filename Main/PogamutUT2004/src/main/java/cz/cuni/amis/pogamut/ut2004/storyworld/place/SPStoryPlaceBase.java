package cz.cuni.amis.pogamut.ut2004.storyworld.place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;
import cz.cuni.amis.utils.collections.MyCollections;

/**
 * Place that doesn't contain more places - directly related to the virtual world.
 * 
 * @author Jimmy
 */
@XStreamAlias("base")
public class SPStoryPlaceBase extends SPStoryPlace {
	
	@XStreamOmitField
	private static Set immutableEmptySet = Collections.unmodifiableSet(new HashSet());
	
	@XStreamOmitField
	private Set<NavPoint> navPoints = new HashSet<NavPoint>();
	
	@XStreamImplicit(itemFieldName="nav")
	private List<String> navPointIds = new ArrayList<String>();
	
	public SPStoryPlaceBase(String name, SPStoryPlace inside, String[] navPoints) {
		super(name, inside);
		navPointIds.addAll(MyCollections.toList(navPoints));
	}

	/**
	 * Called by XStream after deserialization.
	 */
	private SPStoryPlaceBase readResolve() {
		navPoints = new HashSet<NavPoint>();
		return this;
	}

		
	/**
	 * Returns places inside the virtual world that belongs to this place. Basically
	 * this is binding to the chosen 3D world simulator. It should contains objects upon
	 * whose the real path-finding can run.
	 * @return
	 */
	@Override
	public Set<NavPoint> getNavPoints() {
		return navPoints; 
	}
	
	protected void setVirtualPlaces(NavPoint[] places) {
		navPoints.clear();
		navPoints.addAll(MyCollections.toList(places));
	}
	
	protected void setVirtualPlaces(String[] names) {
		navPointIds.clear();
		navPointIds.addAll(MyCollections.toList(names));
	}
	
	protected void bountNavPoints(MapPointListObtained map) {
		NavPoint[] navPoints = new NavPoint[navPointIds.size()];
		int i = 0;
		for (String id : navPointIds) {
			NavPoint navPoint = map.getNavPoints().get(UnrealId.get(id));
			if (navPoint == null) {
				throw new RuntimeException("nav point " +id + " can't be found in the map");
			}
			navPoints[i++] = navPoint;			
		}
		setVirtualPlaces(navPoints);
	}
	
	/**
	 * Base places don't contains any places... returns immutable empty set.
	 * @return
	 */
	@Override
	public Set<SPStoryPlace> getContainsPlaces() {
		return immutableEmptySet;
	}
	
	/**
	 * Base places don't contains any places... returns immutable empty set.
	 * @return
	 */
	@Override
	public Set<SPStoryPlace> getContainsAllPlaces() {
		return immutableEmptySet;
	}

}