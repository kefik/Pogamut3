package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.pathPlanner.polygonPathFunnel;

import java.awt.Color;
import java.util.List;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.UT2004Draw;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshBoundary;
import math.geom3d.Point3D;
import math.geom3d.line.LineSegment3D;

/**
 * Used by {@link Funnel} if {@link #debug} and {@link #draw} is set.
 * @author Jimmy
 *
 */
public class FunnelDebug {
	
	public static boolean debug = false;
	
	public static UT2004Draw draw = null;
	
	public static final Color INIT_COLOR = Color.BLUE;
	
	private static Location lastLoc;
	
	public static boolean isDebugEnabled() {
		return debug && draw != null;
	}
	
	public static boolean isDebugDisabled() {
		return !isDebugEnabled();
	}
	
	public static void debugFunnel_DrawInit(ILocated leadIn, List<NavMeshBoundary> boundaries, ILocated leadOut) {
		if (isDebugDisabled()) return;
		
		draw.clearAll();
		
		lastLoc = leadIn.getLocation();
		
		draw.drawCube(Color.CYAN, leadIn.getLocation(), 20);
		
		for (NavMeshBoundary boundary : boundaries) {
			LineSegment3D segment = boundary.asLineSegment3D();
			draw.drawLine(INIT_COLOR, asLoc(segment.getFirstPoint()), asLoc(segment.getLastPoint()));
		}
		
		draw.drawCube(Color.PINK, leadOut.getLocation(), 20);
		
	}
	
	private static Location asLoc(Point3D point) {
		return new Location(point.getX(), point.getY(), point.getZ());
	}

	public static void debugFunnel_NewCrossing(Location crossing) {
		if (isDebugDisabled()) return;
		
		draw.drawLine(Color.RED, lastLoc, crossing);
		lastLoc = crossing;
	}
	
	public static void debugFunnel_drawGateway(Funnel gatewayFunnel) {
		debugFunnel_drawGateway(gatewayFunnel, Color.GREEN, Color.MAGENTA);
	}

	public static void debugFunnel_NextStep(Funnel gateway, Funnel newGateway, ILocated leadIn, List<NavMeshBoundary> boundaries, ILocated leadOut, List<ILocated> crossings) {
		if (isDebugDisabled()) return;
		
		debugFunnel_DrawInit(leadIn, boundaries, leadOut);
		
		for (ILocated crossing : crossings) {
			debugFunnel_NewCrossing(crossing.getLocation());
		}
		
		debugFunnel_drawGateway(newGateway, Color.GREEN, Color.MAGENTA);
		debugFunnel_drawGateway(gateway, Color.LIGHT_GRAY, Color.DARK_GRAY);			
	}
	
	private static void debugFunnel_drawGateway(Funnel funnel, Color colorLeft, Color colorRight) {
		if (isDebugDisabled()) return;
		
		debugFunnel_DrawRay(funnel.leftRay, colorLeft);
		debugFunnel_DrawRay(funnel.rightRay, colorRight);		
	}
	
	public static void debugFunnel_DrawRay(FunnelRay ray, Color colorRay) {
		if (isDebugDisabled()) return;
		
		draw.drawCube(colorRay, ray.vantagePoint.getLocation(), 20);
		draw.drawCube(colorRay, ray.getVertex().getLocation(), 25);
		draw.drawLine(colorRay, ray.vantagePoint.getLocation(), ray.getVertex().getLocation());		
	}

	
	
}
