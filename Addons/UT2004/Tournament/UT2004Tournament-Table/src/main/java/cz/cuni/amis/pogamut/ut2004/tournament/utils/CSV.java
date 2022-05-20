/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.ut2004.tournament.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.utils.IFilter;
import cz.cuni.amis.utils.collections.MyCollections;

/**
 * @author Jimmy
 */
public class CSV {

	private static class StringComparator implements Comparator<String> {

		@Override
		public int compare(String o1, String o2) {
			return o1.compareTo(o2);
		}
		
	}
	
	public File file;
	    
    private String delimiter;
    
    public List<String> keys;
    
    public List<CSVRow> rows = new ArrayList<CSVRow>();

    /**
     *
     * @param filename
     * @param delimiter
     * @throws FileNotFoundException
     * @throws IOException
     */
    public CSV(File file, String delimiter, boolean containsHeaderRow) throws FileNotFoundException, IOException {
        super();
        
        this.file = file;
        
        this.delimiter = delimiter;
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        
        try {
	        String[] line = nextLine(reader);
	        
	        if (line == null) {
	        	throw new RuntimeException("File '" + file.getAbsolutePath() + "' does not contain any line!");
	        }
	        
	        if (containsHeaderRow) {
	        	keys = MyCollections.asList(line, (IFilter)null);
	        } else {
	        	keys = new ArrayList<String>();
	        	for (int i = 0; i < line.length; ++i) {
	        		keys.add(String.valueOf(i));
	        	}
	        	processLine(line);
	        }
	        
	        while (reader.ready()) {
	        	processLine(nextLine(reader));
	        }
        } finally {
        	reader.close();
        }
    }
    
//    public void save(File file) {
//    	try {
//    		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//    		
//    	} catch (Exception e) {
//    		throw RuntimeException("Failed to save CSV into " + file.getAbsolutePath(), e);
//    	}
//    }
    
    private String[] nextLine(BufferedReader reader) throws IOException {
    	if (reader.ready()) {
    		String line = reader.readLine();
    		return line.split(delimiter);
    	}
    	return null;
    }
    
    private CSVRow processLine(String[] line) {
    	CSVRow result = new CSVRow();
    	for (int i = 0; i < line.length; ++i) {
    		if (i >= keys.size()) {
    			keys.add(String.valueOf(i));
    		}
    		String key = keys.get(i);
    		String value = line[i];
    		result.add(key, value);
    	}
    	rows.add(result);
    	return result;
	}

    public static class CSVRow {

        private Map<String, String> row;

        /**
         *
         * @param row
         */
        public CSVRow() {
            this.row = new HashMap<String, String>();
        }
        
        public void add(String key, String value) {
        	row.put(key, value);
        }

        public String getString(String name) {
            String val = row.get(name);
            if (val == null) {
                return "";
            }
            return val;
        }

        public Integer getInt(String name) {
            try {
                int number = Integer.parseInt(getString(name));
                return number;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        public Double getDouble(String name) {
            try {
                double number = Double.parseDouble(getString(name));
                return number;
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        @Override
        public String toString() {
        	StringBuffer result = new StringBuffer();
        	for (String key : row.keySet()) {
        		result.append("|" + key + " => " + row.get(key));
        	}
        	return result.toString();
        }
        
    }
}
