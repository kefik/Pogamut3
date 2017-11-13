package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.DrawStayingDebugLines;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;

public class UT2004Draw {

	protected Logger log;
	
	protected IUT2004ServerProvider serverProvider;

	protected Color defaultColor = Color.WHITE;
	
	protected Location origin = new Location(0,0,0);
	
	protected double scale = 1;
	
	public UT2004Draw(Logger log, IUT2004ServerProvider serverProvider) {
		this.log = log;
		if (log == null) {
			log = new LogCategory("Draw");
		}
		this.serverProvider = serverProvider;
	}
	
	// =====================
	// UT2004 SERVER SHARING
	// =====================
	
	protected UT2004Server getServer() {
		return serverProvider.getServer();
	}
	
	// ============
	// INITIALIZING
	// ============
	
	public void init() {
		serverProvider.getServer();
	}
	
	// ============
	// STATE METHOD
	// ============
	
	public Location getOrigin() {
		return origin;
	}
	
	public void setOrigin(Location location) {
		this.origin = location;
		if (this.origin == null) this.origin = new Location(0,0,0);
	}
	
	public double getScale() {
		return scale;
	}
	
	public void setScale(double scale) {
		this.scale = scale;		
	}
	
	public Color getColor() {
		return defaultColor;
	}
	
	public void setColor(Color color) {
		this.defaultColor = color;
		if (this.defaultColor == null) {
			this.defaultColor = Color.WHITE;
		}
	}
	
	// ===============
	// DRAWING METHODS
	// ===============
	
	// ========
	// ILOCATED
	// ========
	
	public void drawLine(ILocated from, ILocated to) {
		if (from == null || to == null) return;
		drawLine(from.getLocation(), to.getLocation());
	}
	
	public void drawLine(Color color, ILocated from, ILocated to) {
		if (from == null || to == null) return;
		drawLine(color, from.getLocation(), to.getLocation());
	}
	
	/**
	 * Draws a path
	 * @param pathFuture we at least assume its {@link IPathFuture} of {@link ILocated} elements
	 */
	public void drawPath(IPathFuture pathFuture) {
		drawPath(defaultColor, pathFuture);
	}
	
	/**
	 * Draws a path
	 * @param pathFuture we at least assume its {@link IPathFuture} of {@link ILocated} elements
	 */
	public void drawPath(Color color, IPathFuture pathFuture) {
		if (pathFuture == null) return;
		if (!pathFuture.isDone()) return;
		List elements = pathFuture.get();
		drawPolyLine(color, (List<ILocated>)elements);
	}
	
	public void drawPolyLine(Collection<ILocated> points) {
		drawPolyLine(defaultColor, points);
	}
	
	public void drawPolyLine(ILocated... points) {
		drawPolyLine(defaultColor, points);		
	}
	
	public void drawPolyLine(Color color, Collection<ILocated> points) {
		if (points == null) return;
		Location[] locations = new Location[points.size()];
		Iterator<ILocated> iter = points.iterator();
		int i = 0;
		while (iter.hasNext()) {
			locations[i++] = iter.next().getLocation();
		}
		drawPolyLine(color, locations);
	}
	
	public void drawPolyLine(Color color, ILocated... vertices) {
		if (vertices == null) return;
		Location[] locations = new Location[vertices.length];
		for (int i = 0; i < vertices.length; ++i) {
			if (vertices[i] == null) return;
			locations[i] = vertices[i].getLocation();
		}
		drawPolyLine(color, locations);
	}	
	
	public void drawPolygon(ILocated... vertices) {
		if (vertices == null) return;
		Location[] locations = new Location[vertices.length];
		for (int i = 0; i < vertices.length; ++i) {
			if (vertices[i] == null) return;
			locations[i] = vertices[i].getLocation();
		}
		drawPolygon(locations);
	}
	
	public void drawPolygon(Color color, ILocated... vertices) {
		if (vertices == null) return;
		Location[] locations = new Location[vertices.length];
		for (int i = 0; i < vertices.length; ++i) {
			if (vertices[i] == null) return;
			locations[i] = vertices[i].getLocation();
		}
		drawPolygon(color, locations);
	}
	
	public void drawCube(Color color, ILocated location, double size) {
		drawCube(color, location.getLocation(), size);
	}
	
	// ========
	// LOCATION
	// ========
	
	public void drawLine(Location from, Location to) {
		drawLine(defaultColor, from, to);
	}
	
	public void drawLine(Color color, Location from, Location to) {
		if (color == null || from == null || to == null) {
			return;
		}
		
		init();
		
		DrawStayingDebugLines cmd = newDrawCommand(color);
		
		cmd.setVectors(getDrawVectors(from, to));
		
		getServer().getAct().act(cmd);
	}
	
	public void drawPolyLine(Location... points) {
		drawPolyLine(defaultColor, points);
	}
	
	public void drawPolyLine(Color color, Location... vertices) {
		if (color == null || vertices == null || vertices.length < 2) return;
			
		for (int i = 1; i < vertices.length; ++i) {
			Location v1 = vertices[i-1];
			Location v2 = vertices[i];
			drawLine(color, v1, v2);
		}	
	}
	
	public void drawPolygon(Location... vertices) {
		drawPolygon(defaultColor, vertices);
	}
	
	public void drawPolygon(Color color, Location... vertices) {
		if (color == null || vertices == null || vertices.length < 3) return;
			
		init();
		
		DrawStayingDebugLines cmd = newDrawCommand(color);
		
		cmd.setVectors(getDrawPolygon(vertices));
		
		getServer().getAct().act(cmd);		
	}
	
	public void drawCube(Color color, Location location, double size) {
		Location v1 = null;
		Location v2 = null;
		Location v3 = null;
		Location v4 = null;
		
		double minX = location.x - size/2;
		double maxX = location.x + size/2;
		double minY = location.y - size/2;
		double maxY = location.y + size/2;
		double minZ = location.z - size/2;
		double maxZ = location.z + size/2;
		
		v1 = new Location(minX, minY, minZ);
		v2 = new Location(minX, maxY, minZ);
		v3 = new Location(minX, maxY, maxZ);
		v4 = new Location(minX, minY, maxZ);
		
		drawPolygon(color, v1, v2, v3, v4);
		
		v1 = new Location(maxX, minY, minZ);
		v2 = new Location(maxX, maxY, minZ);
		v3 = new Location(maxX, maxY, maxZ);
		v4 = new Location(maxX, minY, maxZ);
		
		drawPolygon(color, v1, v2, v3, v4);
		
		v1 = new Location(minX, minY, minZ);
		v2 = new Location(maxX, minY, minZ);
		drawLine(color, v1, v2);
		
		v1 = new Location(minX, maxY, minZ);
		v2 = new Location(maxX, maxY, minZ);
		drawLine(color, v1, v2);
		
		v1 = new Location(minX, maxY, maxZ);
		v2 = new Location(maxX, maxY, maxZ);
		drawLine(color, v1, v2);
		
		v1 = new Location(minX, minY, maxZ);
		v2 = new Location(maxX, minY, maxZ);
		drawLine(color, v1, v2);
	}

	// ========
	// double[]
	// ========
	
	public void drawLine(double[] from, double[] to) {
		drawLine(defaultColor, from, to);
	}
	
	public void drawLine(Color color, double[] from, double[] to) {
		if (color == null || from == null || to == null) {
			return;
		}
		
		init();
		
		DrawStayingDebugLines cmd = newDrawCommand(color);
		
		cmd.setVectors(getDrawVectors(from, to));
		
		getServer().getAct().act(cmd);
	}
	
	public void drawPolyLine(double[]... points) {
		drawPolyLine(defaultColor, points);
	}
	
	public void drawPolyLine(Color color, double[]... vertices) {
		if (color == null || vertices == null || vertices.length < 2) return;
		
		for (int i = 1; i < vertices.length; ++i) {
			double[] v1 = vertices[i-1];
			double[] v2 = vertices[i];
			drawLine(defaultColor, v1, v2);
		}			
	}
	
	public void drawPolygon(double[]... vertices) {
		drawPolygon(defaultColor, vertices);
	}
	
	public void drawPolygon(Color color, double[]... vertices) {
		if (color == null || vertices == null || vertices.length < 3) return;
			
		init();
		
		DrawStayingDebugLines cmd = newDrawCommand(color);
		
		cmd.setVectors(getDrawPolygon(vertices));
		
		getServer().getAct().act(cmd);		
	}
	
		
	public void clearAll() {
		init();
		
		DrawStayingDebugLines cmd = new DrawStayingDebugLines();
		cmd.setClearAll(true);
		
		getServer().getAct().act(cmd);		
	}
	
	// ========================
	// PRIVATE STUFF - LOCATION
	// ========================
	
	private Location adjustLocation(Location location) {
		return location.scale(scale).add(origin);
	}
	
	private String getDrawVector(Location point) {
		if (point == null) return "";
		point = adjustLocation(point);
		int x = (int)Math.round(point.x);
		int y = (int)Math.round(point.y);
		int z = (int)Math.round(point.z);
		return x + "," + y + "," + z;
	}
	
	private void writeDrawVector(StringBuffer sb, Location point) {
		if (point == null) return;
		point = adjustLocation(point);
		int x = (int)Math.round(point.x);
		int y = (int)Math.round(point.y);
		int z = (int)Math.round(point.z);
		sb.append(x);
		sb.append(",");
		sb.append(y);
		sb.append(",");
		sb.append(z);
	}
	
	private String getDrawVectors(Location... points) {
		if (points == null) return "";
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (Location point : points) {
			if (first) first = false;
			else sb.append(";");
			writeDrawVector(sb, point);			
		}
		return sb.toString();
	}
	
	private String getDrawPolyLine(Location... points) {
		if (points == null || points.length == 0) return "";
		
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		
		Location point = points[0];
		
		for (int i = 1; i < points.length; ++i) {
			if (first) first = false;
			else sb.append(";");
			writeDrawVector(sb, point);
			sb.append(";");
			writeDrawVector(sb, points[i]);
			point = points[i];
		}
		
		return sb.toString();
	}
	
	private String getDrawPolygon(Location... points) {
		if (points == null || points.length < 2) return "";
		
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		
		Location point = points[0];
		
		for (int i = 1; i < points.length; ++i) {
			if (first) first = false;
			else sb.append(";");
			writeDrawVector(sb, point);
			sb.append(";");
			writeDrawVector(sb, points[i]);
			point = points[i];
		}
		
		sb.append(";");
		writeDrawVector(sb, points[points.length-1]);
		sb.append(";");
		writeDrawVector(sb, points[0]);
		
		return sb.toString();
	}
	
	// ========================
	// PRIVATE STUFF - double[]
	// ========================
	
	private double[] adjustLocation(double[] location) {
		double[] result = new double[]{
							location[0] * scale + origin.x,
							location[1] * scale + origin.y,
							location[2] * scale + origin.z
						  };
		return result;
	}
	
	private String getDrawVector(double[] point) {
		if (point == null) return "";
		point = adjustLocation(point);
		int x = (int)Math.round(point[0]);
		int y = (int)Math.round(point[1]);
		int z = (int)Math.round(point[2]);
		return x + "," + y + "," + z;
	}
	
	private void writeDrawVector(StringBuffer sb, double[] point) {
		if (point == null) return;
		point = adjustLocation(point);
		int x = (int)Math.round(point[0]);
		int y = (int)Math.round(point[1]);
		int z = (int)Math.round(point[2]);
		sb.append(x);
		sb.append(",");
		sb.append(y);
		sb.append(",");
		sb.append(z);
	}
	
	private String getDrawVectors(double[]... points) {
		if (points == null) return "";
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (double[] point : points) {
			if (first) first = false;
			else sb.append(";");
			writeDrawVector(sb, point);			
		}
		return sb.toString();
	}
	
	private String getDrawPolyLine(double[]... points) {
		if (points == null || points.length == 0) return "";
		
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		
		double[] point = points[0];
		
		for (int i = 1; i < points.length; ++i) {
			if (first) first = false;
			else sb.append(";");
			writeDrawVector(sb, point);
			sb.append(";");
			writeDrawVector(sb, points[i]);
			point = points[i];
		}
		
		return sb.toString();
	}
	
	private String getDrawPolygon(double[]... points) {
		if (points == null || points.length < 2) return "";
		
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		
		double[] point = points[0];
		
		for (int i = 1; i < points.length; ++i) {
			if (first) first = false;
			else sb.append(";");
			writeDrawVector(sb, point);
			sb.append(";");
			writeDrawVector(sb, points[i]);
			point = points[i];
		}
		
		sb.append(";");
		writeDrawVector(sb, points[points.length-1]);
		sb.append(";");
		writeDrawVector(sb, points[0]);
		
		return sb.toString();
	}
	
	private DrawStayingDebugLines newDrawCommand() {
		return newDrawCommand(defaultColor);
	}
	
	private DrawStayingDebugLines newDrawCommand(Color color) {
		DrawStayingDebugLines cmd = new DrawStayingDebugLines();
		
		cmd.setColor(new Location(color.getRed(), color.getGreen(), color.getBlue()));
		cmd.setClearAll(false);
		
		return cmd;
	}
		
}
