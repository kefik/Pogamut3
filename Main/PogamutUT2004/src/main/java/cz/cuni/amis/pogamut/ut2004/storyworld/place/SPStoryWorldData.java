package cz.cuni.amis.pogamut.ut2004.storyworld.place;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.io.xml.DomDriver;

import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

@XStreamAlias("storyworld")
public class SPStoryWorldData {

	@XStreamAlias("places")
	private List<SPStoryPlace> places;
	
	@XStreamAlias("bases")
	private List<SPStoryPlaceBase> bases;
	
	@XStreamOmitField
	private Map<Token, SPStoryPlace> placesMap;
	
	@XStreamOmitField
	private Map<Token, SPStoryPlaceBase> basesMap;
	
	@XStreamOmitField
	private String file = "<generated in code>";
	
	public SPStoryWorldData() {		
	}
	
	public static SPStoryWorldData loadXML(File xmlFile) throws FileNotFoundException {
		
		
		FileReader reader = new FileReader(xmlFile);
		XStream xstream = new XStream(new DomDriver());
		xstream.autodetectAnnotations(true);
		xstream.alias(SPStoryWorldData.class.getAnnotation(XStreamAlias.class).value(), SPStoryWorldData.class);
		Object obj = xstream.fromXML(reader);
		try {
			reader.close();
		} catch (IOException e) {
		}
		if (!(obj instanceof SPStoryWorldData)) throw new RuntimeException("file " + xmlFile.getAbsolutePath() + " doesn't contain a xml with SPStoryWorldData");
		SPStoryWorldData data = (SPStoryWorldData) obj;
		data.file = xmlFile.getAbsolutePath();
		return data;
	}
	
	public SPStoryWorldData(SPStoryPlace[] places, SPStoryPlaceBase[] bases) {
		this.places = MyCollections.toList(places);
		this.bases = MyCollections.toList(bases);
		readResolve();
	}
	
	/**
	 * Used by XStream after deserialization.
	 * @return
	 */
	private SPStoryWorldData readResolve() {
		placesMap = new HashMap<Token, SPStoryPlace>();
		basesMap = new HashMap<Token, SPStoryPlaceBase>();
		for (SPStoryPlace place : places) {
			placesMap.put(place.getName(), place);			
		}
		for (SPStoryPlaceBase base : bases) {
			basesMap.put(base.getName(), base);			
		}
		for (SPStoryPlace place : places) {
			if (place.getInsidePlaceName() == null) continue;
			SPStoryPlace inside = placesMap.get(Tokens.get(place.getInsidePlaceName()));
			if (inside == null) {
				String notice = basesMap.get(Tokens.get(place.getInsidePlaceName())) != null ? " Notice that provided name points to the /base/ not /place/, but /base/ places can't contain any other places!" : "";
				throw new RuntimeException("story place '" + place.getName().getToken() + "' reference unknown place '" + place.getInsidePlaceName() + "'" + notice);
			}
			place.setInsidePlace(inside);
		}
		for (SPStoryPlaceBase base : bases) {
			basesMap.put(base.getName(), base);			
		}
		for (SPStoryPlaceBase base : bases) {
			if (base.getInsidePlaceName() == null) continue;
			SPStoryPlace inside = placesMap.get(Tokens.get(base.getInsidePlaceName()));
			if (inside == null) {
				String notice = basesMap.get(Tokens.get(base.getInsidePlaceName())) != null ? " Notice that provided name points to the /base/ not /place/, but /base/ places can't contain any other places!" : "";
				throw new RuntimeException("story place '" + base.getName().getToken() + "' reference unknown place '" + base.getInsidePlaceName() + "'" + notice);
			}
			base.setInsidePlace(inside);			
		}		
		return this;
	}

	public Map<Token,SPStoryPlace> getPlaces() {
		return placesMap;
	}

	public Map<Token, SPStoryPlaceBase> getBases() {
		return basesMap;
	}
	
	public String getFile() {
		return file;
	}

	@Override
	public String toString() {
		return "SPStoryWorldData[places=" + places.size() + ", bases=" + bases.size() + "]";
	}
	
}
