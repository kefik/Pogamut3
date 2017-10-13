package cz.cuni.amis.pogamut.ut2004.agent.navigation.navgraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navgraph.internal.NavPointJsonParser;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navgraph.internal.NavPointLinkJsonParser;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.utils.exception.PogamutException;

/** Saveable Navigation Graph
 */
public class NavGraph {
	
	public Map<UnrealId, NavPoint> navPointsById;
	public Map<String, NavPoint> navPointsByName;
	private Map<UnrealId, NavPoint> navPointsByIdInternal;
	private Map<String, NavPoint> navPointsByNameInternal;
	
	public NavGraph(IWorldView worldView) {
		this(worldView.getAll(NavPoint.class).values());
	}
	
	public NavGraph(Collection<NavPoint> navPoints) {
		initMaps();
		for (NavPoint navpoint : navPoints) {
			addNavPoint(navpoint);			
		}
	}	
	
	public NavGraph(File file) {
		if (file == null) throw new InvalidParameterException("Invalid file.");
		
		initMaps();
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			
			// READ NAVPOINTS
			int navPointCount = Integer.parseInt(reader.readLine());
			for (int i = 0; i < navPointCount; ++i) {
				String navPointJson = reader.readLine();
				NavPoint navPoint = new NavPointJsonParser(navPointJson).parse();
				addNavPoint(navPoint);
			}
			
			// READ LINKS
			while (reader.ready()) {
				String linksDesc = reader.readLine();
				String[] linksDescParts = linksDesc.split("\\|");
				NavPoint navPoint = navPointsByName.get(linksDescParts[0]);
				int linkCount = Integer.parseInt(linksDescParts[1]);
				
				for (int i = 0; i < linkCount; ++i) {
					String navPointLinkJson = reader.readLine();
					NavPointNeighbourLink link = new NavPointLinkJsonParser(this, navPointLinkJson).parse(navPoint);
					
					navPoint.getOutgoingEdges().put(link.getId(), link);
					link.getToNavPoint().getIncomingEdges().put(navPoint.getId(), link);					
				}				
			}
		} catch (Exception e) {
			throw new PogamutException("Failed to read NavGraph from file " + file.getAbsolutePath(), e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {					
				}
			}
		}
	}
	
	public void saveToFile(File file) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter(file));
			
			boolean first = true;
			
			writer.write( Integer.toString( navPointsById.values().size() ) + "\n" );
			for (NavPoint navPoint : navPointsById.values()) {
				if (first) first = false;
				else writer.write("\n");
				writer.write(navPoint.toJsonLiteral());				
			}
			
			for (NavPoint navPoint : navPointsById.values()) {
				writer.write("\n");
				writer.write(navPoint.getId().getStringId() + "|" + navPoint.getOutgoingEdges().values().size());
				for (NavPointNeighbourLink linkOutgoing : navPoint.getOutgoingEdges().values()) {
					writer.write("\n");
					writer.write( linkOutgoing.toJsonLiteral() );					
				}
			}
			
		} catch (Exception e) {
			throw new PogamutException("Failed to write NavGraph into file " + file.getAbsolutePath(), e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {					
				}
			}
		}		
	}
	
	private void initMaps() {
		navPointsByIdInternal = new HashMap<UnrealId, NavPoint>();
		navPointsByNameInternal = new HashMap<String, NavPoint>();;
		navPointsById = Collections.unmodifiableMap( navPointsByIdInternal );
		navPointsByName = Collections.unmodifiableMap( navPointsByNameInternal );
	}
	
	private void addNavPoint(NavPoint navPoint) {
		this.navPointsByIdInternal.put(navPoint.getId(), navPoint);
		this.navPointsByNameInternal.put(navPoint.getId().getStringId(), navPoint);
		
		String shortId = navPoint.getId().getStringId();
		shortId = shortId.substring(shortId.indexOf(".")+1);
		
		if (navPointsByName.containsKey(shortId)) {
			// ID CLASH...
			// TODO: output warning
			System.out.println("[WARNING] There are multiple navpoints that have the common SUFFIX -> " + navPoint.getId().getStringId() + " vs. " + (navPointsByName.get(shortId).getId().getStringId()));
		}			
		navPointsByNameInternal.put(shortId, navPoint);
	}	
}
