package cz.cuni.pogamut.ut2004.levelgeom.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.DomDriver;

import cz.cuni.pogamut.ut2004.levelgeom.utils.UShockParser;

@XStreamAlias("precache")
public class Precache {

	@XStreamAlias("staticmeshes")
	public StaticMeshes staticMeshes;
	
	@XStreamAlias("bspNodes")
	public BspNodes bspNodes;
	
	@XStreamImplicit(itemFieldName="terrain")
	public List<Terrain> terrains;
	
	/**
	 * TOO SLOW! Use {@link Precache#loadXML_Use_JFlex(File)} instead! 
	 * @param xmlFile
	 * @return
	 */
	public static Precache loadXML_Use_XStream(File xmlFile) {
		if (xmlFile == null) {
			throw new IllegalArgumentException("'xmlFile' can't be null!");
		}
		FileReader reader;
		try {
			reader = new FileReader(xmlFile);
		} catch (FileNotFoundException e1) {
			throw new RuntimeException("File " + xmlFile.getAbsolutePath() + " not found: " + e1.getMessage(), e1);
		}
		XStream xstream = new XStream(new DomDriver());
		xstream.autodetectAnnotations(true);
		xstream.alias(Precache.class.getAnnotation(XStreamAlias.class).value(), Precache.class);
		xstream.alias(StaticMeshes.class.getAnnotation(XStreamAlias.class).value(), StaticMeshes.class);
		
		Object obj = xstream.fromXML(reader);
		try {
			reader.close();
		} catch (IOException e) {
		}
		if (obj == null || !(obj instanceof Precache)) {
			throw new RuntimeException("file " + xmlFile.getAbsolutePath() + " doesn't contain a xml with Precache");
		}
		return (Precache)obj;
	}
	
	/**
	 * Using JFlex parser (see parser.jflex) to parse input file. 
	 * @param xmlFile
	 * @return
	 */
	public static Precache loadXML_Use_JFlex(File xmlFile) {
		if (xmlFile == null) {
			throw new IllegalArgumentException("'xmlFile' can't be null!");
		}
		FileReader reader;
		try {
			reader = new FileReader(xmlFile);
		} catch (FileNotFoundException e1) {
			throw new RuntimeException("File " + xmlFile.getAbsolutePath() + " not found: " + e1.getMessage(), e1);
		}
		
		UShockParser parser = new UShockParser(reader);
		try {
			return parser.yylex();
		} catch (IOException e) {
			throw new RuntimeException("Could not parser file " + xmlFile.getAbsolutePath());
		}
	}

	@Override
	public String toString() {
		return "Precache[#bspNodes=" + (bspNodes != null ? bspNodes.size : "n/a") + "]";
	}

}
