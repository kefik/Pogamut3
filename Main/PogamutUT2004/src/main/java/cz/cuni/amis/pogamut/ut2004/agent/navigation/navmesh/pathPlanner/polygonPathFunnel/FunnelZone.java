package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.polygonPathFunnel;

/** The three zones the funnel angle.
 * <p>
 * As determined by a plane projection (usually the XY plane).
 * <p>
 * Technically there should be an OUTSIDE_BEHIND zone, but for the {@link PolygonPathSmoothingFunnelAlgorithm} such zone is not significant.
 */
public enum FunnelZone {
	OUTSIDE_LEFT,
	INSIDE,
	OUTSIDE_RIGHT
}