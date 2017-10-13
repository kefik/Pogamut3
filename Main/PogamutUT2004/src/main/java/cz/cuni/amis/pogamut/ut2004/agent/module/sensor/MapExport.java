package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.DomDriver;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.utils.exception.PogamutIOException;

@XStreamAlias("UT2004Map")
public class MapExport {
	
	private static final Comparator<NavPointExport> NAVPOINT_ID_COMPARATOR = new Comparator<NavPointExport>() {

		@Override
		public int compare(NavPointExport o1, NavPointExport o2) {
			if (o1.Id == null) {
				if (o2.Id == null) return 0;
				return -1;
			} else {
				if (o2.Id == null) return 1;
				return o1.Id.compareTo(o2.Id);
			}
		}
	};

	private static XStream xstream;
	
	public static XStream getXStream() {
		if (xstream != null) return xstream;
		
		xstream = new XStream(new DomDriver());
		xstream.autodetectAnnotations(true);
		xstream.alias(MapExport.class.getAnnotation(XStreamAlias.class).value(), MapExport.class);
		
		return xstream;
	}
	
	@XStreamAsAttribute
	public String name;
	
	@XStreamAsAttribute
	public long timestamp;
	
	@XStreamImplicit(itemFieldName="NavPoint")
	public List<NavPointExport> navPoints;
	
	public MapExport() {
	}
	
	public MapExport(String name, Collection<NavPoint> navPoints) {
		this.name = name;
		this.timestamp = System.currentTimeMillis();
		this.navPoints = new ArrayList<NavPointExport>(navPoints.size());
		for (NavPoint navPoint : navPoints) { 
			this.navPoints.add(new NavPointExport(navPoint));
		}
		Collections.sort(this.navPoints, NAVPOINT_ID_COMPARATOR);
	}
	
	public static MapExport loadXML(File xmlFile) {
		if (xmlFile == null) {
			throw new IllegalArgumentException("'xmlFile' can't be null!");
		}
		FileReader reader;
		try {
			reader = new FileReader(xmlFile);
		} catch (FileNotFoundException e1) {
			throw new RuntimeException("File " + xmlFile.getAbsolutePath() + " not found: " + e1.getMessage(), e1);
		}
		XStream xstream = getXStream();
		Object obj = xstream.fromXML(reader);
		try {
			reader.close();
		} catch (IOException e) {
		}
		if (obj == null || !(obj instanceof MapExport)) {
			throw new RuntimeException("file " + xmlFile.getAbsolutePath() + " doesn't contain a xml with MapExport");
		}
		return (MapExport)obj;
	}
	
	public void saveXML(File xmlFile) {		
		XStream xstream = getXStream();
		
		PrintWriter writer;
		
		try {
			writer = new PrintWriter(new FileWriter(xmlFile));
		} catch (IOException e) {
			throw new PogamutIOException("Failed to open file " + xmlFile.getAbsolutePath() + " for writing.", e);
		}
		
		xstream.toXML(this, writer);	
		
		writer.close();
	}

}
