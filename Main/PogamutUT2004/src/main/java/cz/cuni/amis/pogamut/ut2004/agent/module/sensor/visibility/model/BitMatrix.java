package cz.cuni.amis.pogamut.ut2004.agent.module.sensor.visibility.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;

/**
 * Simple class to store bit-matrix for visibility between {@link VisibilityLocation}s.
 * 
 * THREAD-UNSAFE
 * 
 * @author Jimmy
 */
public class BitMatrix implements Serializable {
    
    /**
	 * Randomly generated. 
	 */
	private static final long serialVersionUID = -6276264641650851997L;

	/**
     * Contains bit matrix.
     * 
     * matrix[colIndex] returns matrix's col (indexing by 'x') first.
     */
    private BitSet[] matrix;
        
    /**
     * Number of rows in matrix (col is determined by matrix.length).
     */
    private int rows;    
    
    public BitMatrix(int columns, int rows) {
        this.rows = rows;
        this.matrix = new BitSet[columns];
        for (int i = 0; i < columns; i++) {
            this.matrix[i] = new BitSet(this.rows);
        }
    }
    
    /**
     * Sets matrix[column, row] to TRUE
     * @param column
     * @param row
     */
    public void set(int column, int row) {
    	matrix[column].set(row);
    }
    
    /**
     * Sets matrix[column, row] to 'state'
     * @param column
     * @param row
     * @param state to set
     */
    public void set(int column, int row, boolean state) {
    	if (state) {
    		set(column, row);
    	} else {
    		unset(column, row);
    	}
    }
    
    /**
     * Flips value in matrix[column, row]
     * @param column
     * @param row
     */
    public void flip(int column, int row) {
    	matrix[column].flip(row);
    }
    
    /**
     * Sets matrix[column, row] to FALSE
     * @param column
     * @param row
     */
    public void unset(int column, int row) {
    	matrix[column].clear(row);
    }
    
    /**
     * Returns 'column' of the matrix.
     * @param column
     * @return
     */
    public BitSet getColumn(int column) {
    	return matrix[column];
    }
    
    /**
     * Returns matrix[column, row]
     */
    public boolean get(int column, int row) {
        return this.matrix[column].get(row);
    }
    
    /**
     * Return logical-and of 'columns'.
     * @param columns
     * @return
     */
    public BitSet and(int... columns) {
    	if (columns == null) return null;
    	if (columns.length == 1) return getColumn(columns[0]);
    	BitSet set = (BitSet) getColumn(columns[0]).clone();
    	for (int i = 1; i < columns.length; ++i) {
    		set.and(getColumn(columns[i]));
    	}
    	return set;
    }
    
    /**
     * Return logical-and of 'columns'.
     * @param columns
     * @return
     */
    public BitSet and(Collection<Integer> columns) {
    	if (columns == null) return null;
    	Iterator<Integer> keyIter = columns.iterator();
    	BitSet set = (BitSet) getColumn(keyIter.next()).clone();
    	while (keyIter.hasNext()) {
    		set.and(getColumn(keyIter.next()));
    	}
    	return set;
    }
    
    /**
     * Return logical-or of 'columns'.
     * @param columns
     * @return
     */
    public BitSet or(int... columns) {
    	if (columns == null) return null;
    	if (columns.length == 1) return getColumn(columns[0]);
    	BitSet set = (BitSet) getColumn(columns[0]).clone();
    	for (int i = 1; i < columns.length; ++i) {
    		set.or(getColumn(columns[i]));
    	}
    	return set;
    }
    
    /**
     * Return logical-and of 'columns'.
     * @param columns
     * @return
     */
    public BitSet or(Collection<Integer> columns) {
    	if (columns == null) return null;
    	Iterator<Integer> keyIter = columns.iterator();
    	BitSet set = (BitSet) getColumn(keyIter.next()).clone();
    	while (keyIter.hasNext()) {
    		set.or(getColumn(keyIter.next()));
    	}
    	return set;
    }
    
    public int columns() {
        return matrix.length;
    }
    
    public int rows() {
    	return rows;
    }
    
    @Override
    public String toString() {
    	return "BitMatrix[" + columns() + "x" + rows + "]";
    }
    
    public void saveToFile(File file) {
    	try {    		
    		ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
    		try {
    			output.writeObject(this);
    		} finally {
    			output.close();
    		}    		
    	} catch (IOException e) {
    		throw new RuntimeException("Failed to save bitmatrix.", e);
    	}
    }
    
    public static BitMatrix loadFromFile(File file) {
    	try {
    		
    		ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
    		BitMatrix matrix;
    		try {
    			matrix = (BitMatrix)input.readObject();
    		} finally {
    			input.close();
    		}
    		return matrix;
    		
    	} catch (IOException e) {
    		throw new RuntimeException("Failed to save bitmatrix.", e);
    	} catch (ClassNotFoundException e) {
			throw new RuntimeException("Failed to load bitmatrix.", e);
		}
    }
    
    /**
     * Flips whole bitset passed.
     * @param bitSet
     */
    public static void flip(BitSet bitSet) {
    	bitSet.flip(0, bitSet.length());
    }
    
}
