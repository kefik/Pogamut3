package cz.cuni.amis.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutIOException;

public class IniFile {
	
	public static abstract class SectionEntry {
		
		private int sectionEntryIndex;
		
		public SectionEntry(int sectionEntryIndex) {
			this.sectionEntryIndex = sectionEntryIndex;
		}
				
		public int getSectionEntryIndex() {
			return sectionEntryIndex;
		}
		
		protected void setSectionEntryIndex(int sectionEntryIndex) {
			this.sectionEntryIndex = sectionEntryIndex;
		}
		
		public abstract String getIniFileLines();
		
		public abstract String getKey();
		
	}
	
	public static class SectionEntryComment extends SectionEntry {
		
		private String comment;
		private String text;
		private String key = null;

		public SectionEntryComment(int sectionEntryIndex, String text, String comment) {
			super(sectionEntryIndex);
			NullCheck.check(text, "text");
			if (!text.startsWith(";")) text = ";" + text;
			this.text = text;
			// HAS KEY?
			int separ = text.indexOf("=");
			if (separ < 0) return;
			key = text.substring(0, separ);
			if (key == null || key.isEmpty()) return;
			while (key.startsWith(";")) key = key.substring(1);			
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		@Override
		public String getIniFileLines() {
			if (comment != null) {
				return comment + "\n" + text;
			}
			return text;
		}

		@Override
		public String getKey() {
			return key;
		}
			
	}
	
	public static class SectionEntryKeyValue extends SectionEntry {
		
		private String comment;
		private String key;
		private List<String> values = new ArrayList<String>();

		public SectionEntryKeyValue(int sectionEntryIndex, String key, String value, String comment) {
			super(sectionEntryIndex);
			this.key = key;
			this.values.add(value);
			this.comment = comment;
			if (this.comment != null) {
				if (!this.comment.startsWith(";")) this.comment = ";" + this.comment;
			}
			NullCheck.check(this.key, "key");
			NullCheck.check(value, "value");
		}
		
		public SectionEntryKeyValue(int sectionEntryIndex, String key, List<String> values, String comment) {
			super(sectionEntryIndex);
			this.key = key;
			NullCheck.check(values, "values");
			this.values.addAll(values);
			this.comment = comment;
			if (this.comment != null) {
				if (!this.comment.startsWith(";")) this.comment = ";" + this.comment;
			}
			NullCheck.check(this.key, "key");
		}
		
		@Override
		public String getKey() {
			return key;
		}

		public String getValue() {
			return values.size() > 0 ? values.get(0) : null;
		}
		
		public List<String> getValues() {
			return values;
		}

		public void addValue(String value) {
			values.add(value);
		}
		
		public void setValue(String value) {
			values.clear();
			values.add(value);
		}
		
		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		@Override
		public String getIniFileLines() {
			StringBuffer sb = new StringBuffer();
			if (comment != null) {
				sb.append(comment);
				sb.append("\n");
			}
			boolean first = false;
			for (String value : values) {				
				if (first) first = false;
				else sb.append("\n");
				sb.append(key);
				sb.append("=");
				sb.append(value);
			}
			return sb.toString();
		}
		
	}

	public static final Comparator<SectionEntry> SECTION_ENTRY_INDEX_COMPARATOR = new Comparator<SectionEntry>() {
		
		@Override
		public int compare(SectionEntry o1, SectionEntry o2) {
			return o1.getSectionEntryIndex() - o2.getSectionEntryIndex();
		}
	};
	
	public static final Comparator<SectionEntry> SECTION_ENTRY_KEY_COMPARATOR = new Comparator<SectionEntry>() {
		
		@Override
		public int compare(SectionEntry o1, SectionEntry o2) {
			int val = o1.getKey().compareTo(o2.getKey());
			if (val != 0) return val;			
			if (o1 instanceof SectionEntryKeyValue) {
				if (o2 instanceof SectionEntryComment) return -1;
				return 0;
			} else
			if (o1 instanceof SectionEntryComment) {
				if (o2 instanceof SectionEntryKeyValue) return 1;
				return 0;
			} else {
				return 0;
			}		
		}
	};
		
	/**
	 * Class representing one section of the ini file.
	 * @author Jimmy
	 */
	public static class Section {
		
		private static int nextSectionIndex = 0;
		private int nextSectionEntryIndex = 0;
		private int sectionIndex;
		private String name;
		
		private List<SectionEntryComment> comments = new ArrayList<SectionEntryComment>();
		
		private Map<String, SectionEntryKeyValue> props = new HashMap<String, SectionEntryKeyValue>();
		
		/**
		 * Creates a section of the given name.
		 * <p><p>
		 * Name can't be null!
		 * 
		 * @param name
		 */
		public Section(String name) {
			this.name = name;			
			NullCheck.check(this.name, "name");
			this.sectionIndex = nextSectionIndex++;
		}

		/**
		 * Copy-constructor.
		 * @param section
		 */
		public Section(Section section) {
			this.name = section.getName();
			this.sectionIndex = nextSectionIndex++;
			this.add(section);
		}

		/**
		 * Returns name of the section.
		 * @return
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Sets a property key=value into the section.
		 * @param key
		 * @param value
		 * @return this
		 */
		public Section put(String key, String value) {
			return put(key, value, null);
		}
		
		/**
		 * Sets a property key=value into the section with comment.
		 * @param key
		 * @param value
		 * @param comment
		 * @return this
		 */
		public Section put(String key, String value, String comment) {
			NullCheck.check(key, "key");
			SectionEntryKeyValue entry = props.get(key);
			if (entry != null) {
				entry.setValue(value);
			} else {
				entry = new SectionEntryKeyValue(nextSectionEntryIndex++, key, value, comment);
				props.put(key, entry);
			}
			return this;
		}
		
		/**
		 * Adds a property key=value into the section.
		 * @param key
		 * @param value
		 * @return this
		 */
		public Section add(String key, String value) {
			return add(key, value, null);
		}
		
		/**
		 * Adds a property key=value into the section with comment.
		 * @param key
		 * @param value
		 * @param comment
		 * @return this
		 */
		public Section add(String key, String value, String comment) {
			NullCheck.check(key, "key");
			SectionEntryKeyValue entry = props.get(key);
			if (entry != null) {
				entry.addValue(value);
			} else {
				entry = new SectionEntryKeyValue(nextSectionEntryIndex++, key, value, comment);
				props.put(key, entry);
			}
			return this;
		}
		
		/**
		 * Returns a value of the propety with 'key'.
		 * @param key
		 * @return
		 */
		public String getOne(String key) {
			SectionEntryKeyValue entry = props.get(key);
			if (entry == null) return null;
			return entry.getValue();
		}
		
		/**
		 * Returns a value of the propety with 'key'.
		 * @param key
		 * @return
		 */
		public List<String> getAll(String key) {
			SectionEntryKeyValue entry = props.get(key);
			if (entry == null) return null;
			return entry.getValues();
		}
		
		/**
		 * Returns full section entry for a 'key'.
		 * @param key
		 * @return
		 */
		public SectionEntryKeyValue getEntry(String key) {
			SectionEntryKeyValue entry = props.get(key);
			if (entry == null) return null;
			return entry;
		}
		
		/**
		 * Whether the section contains property of the given key.
		 * @param key
		 * @return
		 */
		public boolean containsKey(String key) {
			return props.containsKey(key);
		}
		
		/**
		 * Returns all keys stored within the map.
		 * @return
		 */
		public Set<String> getKeys() {
			return props.keySet();
		}
		
		/**
		 * Alias for {@link Section#getKeys()}.
		 * @return
		 */
		public Set<String> keySet() {
			return getKeys();
		}
		
		/**
		 * Removes a property under the 'key' from this section.
		 * 
		 * Time complexity O(n) due to section-entry-index consolidation.
		 * 
		 * @param key
		 * @return removed {@link SectionEntryKeyValue}
		 */
		public SectionEntryKeyValue remove(String key) {
			SectionEntryKeyValue entry = props.remove(key);
			indexDeleted(entry.getSectionEntryIndex());
			return entry;
		}
		
		private void indexDeleted(int sectionEntryIndex) {
			for (SectionEntryComment comment : comments) {
				if (comment.getSectionEntryIndex() > sectionEntryIndex) {
					comment.setSectionEntryIndex(comment.getSectionEntryIndex() - 1);
				}
			}
			for (SectionEntryKeyValue keyValue : props.values()) {
				if (keyValue.getSectionEntryIndex() > sectionEntryIndex) {
					keyValue.setSectionEntryIndex(keyValue.getSectionEntryIndex() - 1);
				}
			}
		}

		/**
		 * Deletes all properties within this section.
		 * @return
		 */
		public Section clear() {
			props.clear();
			comments.clear();
			return this;
		}
		
		/**
		 * Deletes all comments within this section.
		 * @return
		 */
		public Section clearComments() {
			comments.clear();
			return this;
		}

		/**
		 * Alias for {@link Section#put(String, String)}.
		 * @param key
		 * @param value
		 * @return this
		 */
		public Section set(String key, String value) {		
			return put(key, value);
		}
		
		/**
		 * Alias for {@link Section#put(String, Strin, String)}.
		 * @param key
		 * @param value
		 * @param comment
		 * @return this
		 */
		public Section set(String key, String value, String comment) {		
			return put(key, value, comment);
		}
		
		/**
		 * Adds comment to this section.
		 * @param comment
		 */
		public void addComment(String comment) {
			if (!comment.startsWith(";")) comment = ";" + comment;
			if (!isComment(comment)) throw new RuntimeException("'" + comment + "' is not a comment!");
			comments.add(new SectionEntryComment(nextSectionEntryIndex++, comment, null));
		}
		
		public void addComment(String keyValueCommented, String comment) {			
			if (!keyValueCommented.startsWith(";")) keyValueCommented = ";" + keyValueCommented;
			if (comment == null) {
				addComment(keyValueCommented);
				return;
			}
			if (!comment.startsWith(";")) comment = ";" + comment;
			if (!isComment(keyValueCommented)) throw new RuntimeException("'" + keyValueCommented + "' is not a comment!");
			if (!hasCommentKey(keyValueCommented)) throw new RuntimeException("'" + keyValueCommented + "' is not commented out key=value!");
			comments.add(new SectionEntryComment(nextSectionEntryIndex++, keyValueCommented, comment));
		}

		/**
		 * Adds all properties from 'section' into this one.
		 * @param section
		 * @return 
		 * @return this
		 */
		public Section add(Section section) {
			for (SectionEntryKeyValue keyValue : props.values()) {
				put(keyValue.getKey(), keyValue.getValue());
			}
			for (SectionEntryComment comment : section.comments) {
				addComment(comment.getText());				
			}
			return this;
		}

		/**
		 * Writes this section into the writer.
		 * @param writer
		 */
		public void output(PrintWriter writer) {			
			List<SectionEntry> output = new ArrayList<SectionEntry>(props.values());
			
			List<SectionEntryComment> commentsWithKeys = new ArrayList<SectionEntryComment>(comments);
			List<SectionEntryComment> commentsWithoutKeys = new ArrayList<SectionEntryComment>();
			Iterator<SectionEntryComment> iter = commentsWithKeys.iterator();
			while (iter.hasNext()) {
				SectionEntryComment comment = iter.next();
				if (comment.getKey() == null || comment.getKey().length() <= 0) {
					commentsWithoutKeys.add(comment);
					iter.remove();
				} else {
					output.add(comment);
				}
			}
			
			Collections.sort(output, SECTION_ENTRY_KEY_COMPARATOR);
			
			mergeInComments(output, commentsWithoutKeys);
			
			writer.print("[");
			writer.print(name);
			writer.println("]");
			for (SectionEntry entry : output) {
				writer.println(entry.getIniFileLines());
			}
		}
		
		private void mergeInComments(List<SectionEntry> output,	List<SectionEntryComment> commentsWithKeys) {
			if (output.size() == 0) {
				output.addAll(commentsWithKeys);
				Collections.sort(output, SECTION_ENTRY_INDEX_COMPARATOR);
				return;
			}
			while(commentsWithKeys.size() > 0) {
				Iterator<SectionEntryComment> iter = commentsWithKeys.iterator();
				while (iter.hasNext()) {
					SectionEntryComment comment = iter.next();
					if (mergeInComment(output, comment)) {
						iter.remove();
					}
				}
			}			
		}

		private boolean mergeInComment(List<SectionEntry> output, SectionEntryComment comment) {
			if (output.size() == 0) {
				output.add(comment);
				return true;
			}
			for (int i = 0; i < output.size(); ++i) {
				SectionEntry entry = output.get(i);
				if (entry.getSectionEntryIndex()-1 == comment.getSectionEntryIndex()) {
					output.add(i, comment);
					return true;
				}
			}
			if (comment.getSectionEntryIndex()-1 == output.get(output.size()-1).getSectionEntryIndex()) {
				output.add(comment);
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return "IniFile.Section[name=" + name + ", entries=" + props.size() + "]";
		}
		
	}
	
	private Map<String, Section> sections = new TreeMap<String, Section>(new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			if (o1 == null) return -1;
			if (o2 == null) return 1;
			return o1.toLowerCase().compareTo(o2.toLowerCase());
		}
	});
	
	/**
	 * Constructs Ini file with no defaults.
	 */
	public IniFile() {
	}
	
	/**
	 * Initialize object with defaults taken from 'source' (file must exists!).
	 * 
	 * @param source
	 */
	public IniFile(File source) {
		if (!source.exists()) {
			throw new PogamutException("File with defaults does not exist at: " + source.getAbsolutePath() + ".", this);
		}
		load(source);
	}
	
	/**
	 * Initialize object with defaults taken from 'source' (file must exists!).
	 * 
	 * @param source
	 */
	public IniFile(InputStream source) {
		load(source);
	}
		
	/**
	 * Copy-constructor. 
	 *
	 * @param ini
	 */
	public IniFile(IniFile ini) {
		for (Section section : ini.getSections()) {
			addSection(new Section(section));
		}
	}

	/**
	 * Loads {@link IniFile#source} into {@link IniFile#sections}.
	 * <p><p>
	 * Note that his method won't clear anything, it will just load all sections/properties from the given file possibly overwriting existing properties
	 * in existing sections.
	 * 
	 * @param source 
	 */
	public void load(File source) {
		try {
			load(new FileInputStream(source));
		} catch (Exception e) {
			throw new PogamutException("Could not load defaults for GameBots2004.ini from file: " + source.getAbsolutePath() + ", caused by: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Loads {@link IniFile#source} into {@link IniFile#sections}.
	 * <p><p>
	 * Note that his method won't clear anything, it will just load all sections/properties from the given file possibly overwriting existing properties
	 * in existing sections.
	 * 
	 * @param source 
	 */
	public void load(InputStream source) {
		BufferedReader reader = null;
		reader = new BufferedReader(new InputStreamReader(source));
		
		Section currSection = null;
		String currComment = null;
		
		try {
			while (reader.ready()) {
				String line = reader.readLine().trim();
				
				if (line.length() == 0) {
					continue;
				}
				
				if (isComment(line)) {
					if (currSection == null) {
						if (currComment == null) currComment = line.trim();
						else currComment += "\n" + line;
					} else {
						if (hasCommentKey(line)) {
							currSection.addComment(line, currComment);
						} else {
							if (currComment == null) currComment = line.trim();
							else currComment += "\n" + line;
						}
					}					
					continue;
				}
				
				if (line.startsWith("[") && line.endsWith("]")) {
					if (currComment != null && currSection != null) {
						currSection.addComment(currComment);
					}
					if (currSection != null && getSection(currSection.getName()) == null) {
						addSection(currSection);
					}
					String sectionName = line.substring(1, line.length()-1);
					if (hasSection(sectionName)) {
						// ONE SECTION ENCOUNTERED TWICE WITHIN THE INI FILE...
						currSection = getSection(sectionName);
					} else {
						currSection = getSection(sectionName);
					}
					if (currSection == null) currSection = new Section(sectionName);
					continue;
				} 
				
				int separ = line.indexOf("=");
				if (separ < 0) {
					// TODO: [Jimmy] throw an exception? at least log somewhere?
					continue;
				}
				if (currSection == null) {
					throw new PogamutException("There is an entry '" + line + "' inside ini file that does not belong to any section.", this);
				}
				String key = line.substring(0, separ);					
				String value =
					(separ+1 < line.length() ? 
							line.substring(separ+1, line.length())
						:	"");
				currSection.add(key, value, currComment);
				currComment = null;
			}
		} catch (IOException e) {
			throw new PogamutIOException("Could not completely read file with defaults from stream, caused by: " + e.getMessage(), e, this);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
		
		if (currSection != null && getSection(currSection.getName()) == null) {
			addSection(currSection);
		}
	}

	/**
	 * Add all sections from one ini file into this one.
	 * @param iniFile
	 * @return this
	 */
	public IniFile addIniFile(IniFile iniFile) {
		for (Section section : iniFile.sections.values()) {
			addSection(section);
		}
		return this;
	}
	
	/**
	 * Adds a new section into this class (won't overwrite existing one).
	 * 
	 * @param sectionName
	 * @return section that is stored in this class
	 */
	public Section addSection(String sectionName) {
		return addSection(new Section(sectionName));
	}
	
	/**
	 * Adds section into this ini file. If section of the same name exists, it won't be replaced. Instead all properties
	 * from 'section' will be put there.
	 * <p><p>
	 * If 'section' is a new section (i.e., its {@link Section#getName()} is not already present in stored sections), this instance
	 * will be stored here (does not hard-copy the section!). For hard-copy variant, use {@link IniFile#copySection(Section)}.
	 * 
	 * @param section
	 * @return section that is stored in this class
	 */
	public Section addSection(Section section) {
		Section oldSection = sections.get(section.getName());
		if (oldSection != null) {
			oldSection.add(section);
			return oldSection;
		} else {
			sections.put(section.getName(), section);
			return section;
		}
	}
	
	public Section copySection(Section section) {
		Section oldSection = sections.get(section.getName());
		if (oldSection != null) {
			oldSection.add(new Section(section));
			return oldSection;
		} else {
			sections.put(section.getName(), section);
			return section;
		}
	}
	
	public boolean hasSection(String name) {
		return sections.containsKey(name);
	}
	
	public Section getSection(String name) {
		return sections.get(name);
	}
	
	public Set<String> getSectionNames() {
		return sections.keySet();
	}
	
	public Collection<Section> getSections() {
		return sections.values();
	}
	
	public String getOne(String section, String key) {
		Section sec = sections.get(section);
		if (sec == null) return null;
		return sec.getOne(key);
	}
	
	public List<String> getAll(String section, String key) {
		Section sec = sections.get(section);
		if (sec == null) return null;
		return sec.getAll(key);
	}
	
	/**
	 * Sets property key=value into section 'section'
	 * @param section
	 * @param key
	 * @param value
	 * @return section instance that the property was set into
	 */
	public Section set(String section, String key, String value) {
		Section sec = sections.get(section);
		if (sec == null) {
			sec = addSection(section);
		}
		sec.set(key, value);
		return sec;
	}
	
	/**
	 * Set 'values' into this IniFile, alias for {@link IniFile#addIniFile(IniFile)}.
	 * @param values
	 * @return this
	 */
	public IniFile set(IniFile values) {
		return addIniFile(values);
	}

	/**
	 * Set key=values from 'section' into this IniFile. Alias for {@link IniFile#addSection(Section)}.
	 * @param section
	 * @return section that is stored in this class
	 */
	public Section set(Section section) {
		return addSection(section);		
	}

	/**
	 * Whether this line is comment. (Does not check for end lines).
	 * @param text
	 * @return
	 */
	public static boolean isComment(String line) {
		return line.trim().startsWith(";");
	}
	
	/**
	 * Whether this non-comment line is a key=value. (Does not check for end lines).
	 * @param keyValue
	 * @return
	 */
	public static  boolean hasKey(String keyValue) {
		if (isComment(keyValue)) return false;
		return keyValue.indexOf("=") > 0;
	}
	
	/**
	 * Whether this comment is commented key=value pair. (Does not check for end lines).
	 * @param comment
	 * @return
	 */
	public static boolean hasCommentKey(String comment) {
		if (!isComment(comment)) return false;
		while (comment.startsWith(";")) comment = comment.substring(1);
		int separ = comment.indexOf("=");
		if (separ <= 0) return false;
		String key = comment.substring(0, separ);
		return !key.contains(" ");
	}
	
	/**
	 * Similar to {@link #output(String)} but this ensure not to overwrite any file + it appends current "_date_time" to filename.
	 * 
	 * @param file
	 */
	public void backup(String pathToFileToBeCreated) {
		File file = new File(pathToFileToBeCreated);
		String fullpath = file.getAbsolutePath();
		String separator = System.getProperty("file.separator");
		String path = (fullpath.lastIndexOf(separator) >= 0 ? fullpath.substring(0, fullpath.lastIndexOf(separator)) : ".");
		String filename = (fullpath.lastIndexOf(separator) >= 0 ? fullpath.substring(fullpath.lastIndexOf(separator)+1) : "file.ini");
		String name = (filename.lastIndexOf(".") >= 0 ? filename.substring(0, filename.lastIndexOf(".")) : filename);
		String extension = (filename.lastIndexOf(".") >= 0 ? filename.substring(filename.lastIndexOf(".") + 1) : "ini");
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		name += "." + sdf.format(date);
		
		File targetFile = null;
		int i = 0;
		
		while (true) {
			targetFile = new File(path + separator + name + (i > 0 ? "_" + i : "") + "." + extension);
			if (!targetFile.exists()) break; 
			++i;
		}
		
		output(targetFile);		
	}
	
	/**
	 * Outputs GameBots2004.ini stored by this class into 'file'. If 'file' exists, it overwrites it.
	 * 
	 * @param file
	 */
	public void output(String pathToFileToBeCreated) {
		NullCheck.check(pathToFileToBeCreated, "pathToFileToBeCreated");
		output(new File(pathToFileToBeCreated));
	}
	
	
	/**
	 * Outputs GameBots2004.ini stored by this class into 'file'. If 'file' exists, it overwrites it.
	 * 
	 * @param file
	 */
	public void output(File file) {
		NullCheck.check(file, "file");
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter(file));
			output(writer);
		} catch (IOException e) {
			throw new PogamutIOException("Could not write ini file into '" + file.getAbsolutePath() + "', caused by: " + e.getMessage(), e, this);
		} finally {
			writer.close();
		}
	}
	
	/**
	 * Outputs contents of this {@link IniFile} into the 'writer'.
	 * 
	 * @param writer
	 */
	public void output(PrintWriter writer) {
		NullCheck.check(writer, "writer");
		boolean first = true;
		for (Section section : sections.values()) {
			if (first) first = false;
			else writer.println();
			section.output(writer);
		}
	}
	
	/**
	 * Returns contents of this {@link IniFile} as string.
	 * 
	 * @return
	 */
	public String output() {
		StringWriter stringWriter = new StringWriter();
		output(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}
	
	//
	// MERGE INTO
	//
	
	public static void mergeIntoIniFile(IniFile values, File mergeIntoIniFile) {
		IniFile read = new IniFile(mergeIntoIniFile);
		if (read == null) {
			throw new RuntimeException("Failed to read ini file: " + mergeIntoIniFile.getAbsolutePath());
		}
		read.set(values);
		read.output(mergeIntoIniFile);
	}
	
	//
	// FOR TESTS
	// 
	
	/**
	 * Checks, whether THIS {@link IniFile} is the subset of 'other'.
	 * @param other
	 * @param thisName
	 * @param otherName
	 * @param log
	 * @return
	 */
	public boolean isSubset(IniFile other, String thisName, String otherName, Logger log) {
		for (Section thisSection : this.getSections()) {
			log.info("Checking section [" + thisSection.getName() + "]");
			Section otherSection = other.getSection(thisSection.getName());
			if (otherSection == null) {
				if (log != null) log.severe(thisName + " INI contains Section[" + thisSection.getName() + "] that is not present within Read INI (source)!");
				return false;
			}
			for (String key : thisSection.getKeys()) {
				List<String> thisValues = thisSection.getAll(key);
				List<String> otherValues = new ArrayList<String>(otherSection.getAll(key));
				if (log != null) log.info("Checking key: " + key + " (" + thisValues.size() + " values)");
				
				if (thisValues.size() != otherValues.size()) {
					if (log != null) log.severe(thisName + " INI, Section[" + thisSection.getName() + "], Key[" + key + "] contains #values == " + thisValues.size() + " != " + otherValues.size() + " == #values within " + otherName + " INI (source)!");
				}
				
				for (String testValue : thisValues) {
					boolean present = false;
					for (int i = 0; i < otherValues.size(); ++i) {
						String otherValue = otherValues.get(i);
						if (SafeEquals.equals(testValue, otherValue)) {
							present = true;
							otherValues.remove(i);
							break;
						}
					}
					if (present) continue;
					// ERROR!
					if (log != null) log.severe(thisName + " INI, Section[" + thisSection.getName() + "], Key[" + key + "] contains Value[" + testValue + "] that is not present within " + otherName + " section/key!");
					return false;
				}
			}			
		}
		return true;
	}
	
	/**
	 * Checks, whether THIS {@link IniFile} contains the same sections/keys/values as 'other'.
	 * @param other
	 * @param thisName
	 * @param otherName
	 * @param log
	 * @return
	 */
	public boolean isEqual(IniFile other, String thisName, String otherName, Logger log) {
		return isSubset(other, thisName, otherName, log) && other.isSubset(this, otherName, thisName, log);
	}


}
