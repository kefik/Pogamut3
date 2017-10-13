package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.utils.HashCode;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.SafeEquals;

/**
 * NavLinkPair representing info about links between two navpoints, such info comprises of
 * 1 or 2 {@link NavPointNeighbourLink}.
 * 
 * {@link NavLinkPair#hashCode()} and {@link NavLinkPair#equals(Object)} overridden.
 * 
 * @author Jimmy
 */
public class NavLinkPair {
	
	private NavPointNeighbourLink link1;
	private NavPointNeighbourLink link2;

	private NavPoint nav1;
	private NavPoint nav2;
	
	private Location x1, x2;
	
	private int hashCode;
	
	public NavLinkPair(NavPointNeighbourLink first) {
		link1 = first;
		
		NullCheck.check(link1, "first");
		
		HashCode hc = new HashCode();
		hc.add(link1.hashCode());
		hashCode = hc.getHash();
		
		nav1 = link1.getFromNavPoint();
		nav2 = link1.getToNavPoint();
		x1 = link1.getFromNavPoint().getLocation();
		x2 = link1.getToNavPoint().getLocation();
		
//		NullCheck.check(nav1, "first.getFromNavPoint()");
//		NullCheck.check(nav2, "first.getToNavPoint()");
//		NullCheck.check(nav1.getId(), "first.getFromNavPoint().getId()");
//		NullCheck.check(nav2.getId(), "first.getToNavPoint().getId()");
//		NullCheck.check(x1, "first.getFromNavPoint().getLocation()");
//		NullCheck.check(x2, "first.getToNavPoint().getLocation()");
	}
	
	/**
	 * If 'first' is NULL and 'second' is NULL ... throws {@link IllegalArgumentException}.
	 * 
	 * If 'first' is NULL and 'second' is NOT ... it swaps 'first' and 'second'.
	 * 
	 * If 'first' is NOT NULL and 'second' is NOT NULL ... it may swap them to satisfy {@link NavLinkPair#hashCode()} implementation.
	 * 
	 * @param first
	 * @param second
	 */
	public NavLinkPair(NavPointNeighbourLink first, NavPointNeighbourLink second) {
		if (first == null) {
			first = second;
			second = null;
		} else
		if (second != null) {
			if (first.hashCode() > second.hashCode()) {
				NavPointNeighbourLink temp = second;
				second = first;
				first = temp;
			}
		}
		
		link1 = first;
		link2 = second;
		
		NullCheck.check(link1, "'first' and 'second'");
		
		HashCode hc = new HashCode();
		hc.add(link1.hashCode());
		if (link2 != null) {
			hc.add(link2.hashCode());
		}
		hashCode = hc.getHash();
		
		nav1 = link1.getFromNavPoint();
		nav2 = link1.getToNavPoint();
		x1 = nav1.getLocation();
		x2 = nav2.getLocation();	
		
//		NullCheck.check(nav1, "first/second.getFromNavPoint()");
//		NullCheck.check(nav2, "first/second.getToNavPoint()");
//		NullCheck.check(nav1.getId(), "first/second.getFromNavPoint().getId()");
//		NullCheck.check(nav2.getId(), "first/second.getToNavPoint().getId()");
//		NullCheck.check(x1, "first/second.getFromNavPoint().getLocation()");
//		NullCheck.check(x2, "first/second.getToNavPoint().getLocation()");
	} 
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof NavLinkPair)) return false;
		if (hashCode != obj.hashCode()) return false;
		NavLinkPair pair = (NavLinkPair)obj;
		return SafeEquals.equals(link1, pair.getNavLink1()) && SafeEquals.equals(link2, pair.getNavLink2());
	}
	
	/**
	 * Returns first {@link NavPointNeighbourLink}, this is NEVER NULL.
	 * @return
	 */
	public NavPointNeighbourLink getNavLink1() {
		return link1;
	}
	
	/**
	 * Returns second {@link NavPointNeighbourLink}, may be null.
	 * @return
	 */
	public NavPointNeighbourLink getNavLink2() {
		return link2;
	}
	
	/**
	 * Returns first's {@link NavPointNeighbourLink#getFromNavPoint()}, this is NEVER NULL.
	 * @return
	 */
	public NavPoint getNavPoint1() {
		return nav1;
	}
	
	/**
	 * Returns first's {@link NavPointNeighbourLink#getToNavPoint()}, this is NEVER NULL.
	 * @return
	 */
	public NavPoint getNavPoint2() {
		return nav2;
	}
	
	/**
	 * Does this {@link NavLinkPair} contains (as either end) 'navPoint'.
	 * @param navPoint
	 * @return
	 */
	public boolean isLinkNavPoint(NavPoint navPoint) {
		if (navPoint == null) return false;
		return SafeEquals.equals(navPoint.getId(), nav1.getId()) || SafeEquals.equals(navPoint.getId(), nav2.getId());
	}
	
	/**
	 * Does this {@link NavLinkPair} contains (as either end) 'navPoint'.
	 * @param navPoint
	 * @return
	 */
	public boolean isLinkNavPoint(UnrealId navPointId) {
		if (navPointId == null) return false;
		return SafeEquals.equals(navPointId, nav1.getId()) || SafeEquals.equals(navPointId, nav2.getId());
	}
	
	/**
	 * Returns link that ends in 'navPointLinkEndsInId', if such exists.
	 * 
	 * @param navPointLinkEndsInId
	 * @return
	 */
	public NavPointNeighbourLink getLinkLeadingTo(UnrealId navPointLinkEndsInId) {
		if (navPointLinkEndsInId == null) return null;
		if (link1 != null && link1.getToNavPoint() != null) {
			if (link1.getToNavPoint().getId().equals(navPointLinkEndsInId)) return link1;
		}
		if (link2 != null && link2.getToNavPoint() != null) {
			if (link2.getToNavPoint().getId().equals(navPointLinkEndsInId)) return link1;
		}
		return null;
	}
	
	/**
	 * Returns link that originates in 'navPointLinkIsComingFromId', if such exists.
	 * 
	 * @param navPointLinkIsComingFromId
	 * @return
	 */
	public NavPointNeighbourLink getLinkComingFrom(UnrealId navPointLinkIsComingFromId) {
		if (navPointLinkIsComingFromId == null) return null;
		if (link1 != null && link1.getFromNavPoint() != null) {
			if (link1.getFromNavPoint().getId().equals(navPointLinkIsComingFromId)) return link1;
		}
		if (link2 != null && link2.getFromNavPoint() != null) {
			if (link2.getFromNavPoint().getId().equals(navPointLinkIsComingFromId)) return link1;
		}
		return null;
	}
	
	/**
	 * Returns link that ends in 'navPointLinkEndsIn', if such exists.
	 * 
	 * @param navPointLinkEndsIn
	 * @return
	 */
	public NavPointNeighbourLink getLinkLeadingTo(NavPoint navPointLinkEndsIn) {
		if (navPointLinkEndsIn == null) return null;
		if (link1 != null && link1.getToNavPoint() != null) {
			if (link1.getToNavPoint().getId().equals(navPointLinkEndsIn.getId())) return link1;
		}
		if (link2 != null && link2.getToNavPoint() != null) {
			if (link2.getToNavPoint().getId().equals(navPointLinkEndsIn.getId())) return link1;
		}
		return null;
	}
	
	/**
	 * Returns link that originates in 'navPointLinkIsComingFrom', if such exists.
	 * 
	 * @param navPointLinkIsComingFrom
	 * @return
	 */
	public NavPointNeighbourLink getLinkComingFrom(NavPoint navPointLinkIsComingFrom) {
		if (navPointLinkIsComingFrom == null) return null;
		if (link1 != null && link1.getFromNavPoint() != null) {
			if (link1.getFromNavPoint().getId().equals(navPointLinkIsComingFrom.getId())) return link1;
		}
		if (link2 != null && link2.getFromNavPoint() != null) {
			if (link2.getFromNavPoint().getId().equals(navPointLinkIsComingFrom.getId())) return link1;
		}
		return null;
	}
	
	/**
	 * Get vector of the first link.
	 * @return
	 */
	public Location getFirstVector() {			
		if (link1 == null) return null;
		return link1.getToNavPoint().getLocation().sub(link1.getFromNavPoint().getLocation());
	}
	
	/**
	 * Get vector of the second link.
	 * @return
	 */
	public Location getSecondVector() {			
		if (link2 == null) return null;
		return link2.getToNavPoint().getLocation().sub(link2.getFromNavPoint().getLocation());
	}
	
	/**
	 * Distance "point" from "link". 
	 * 
	 * See: http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
	 * 
	 * @param point
	 * @return
	 */
	public double getDistance(ILocated point) {
		if (point == null) return Double.MAX_VALUE;
		if (x1 == null || x2 == null) return Double.MAX_VALUE;
		
		// SEE: http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
		Location x0 = point.getLocation();
		double distance = x0.sub(x1).cross(x0.sub(x2)).getLength() / x2.sub(x1).getLength();
		
		return distance;
	}

}
