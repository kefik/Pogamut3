package cz.cuni.amis.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class GenericLoader<T> {
	
	/**
	 * Loads the object of type T from input stream.
	 * @param in
	 * @return null (failure) || loaded object
	 */
	public T loadObject(InputStream in) {
		try {
			return (T) ((new ObjectInputStream(in)).readObject());
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * Loads the object of type T from file 'file'.
	 * @param file
	 * @return null (failure) || loaded object
	 */
	public T loadObject(File file) {
		try {
			return loadObject(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * Loads the object of type T from the file at path 'pathAndFileName'.
	 * @param pathAndFileName
	 * @return null (failure) || loaded object
	 */
	public T loadObject(String pathAndFileName) {
		return loadObject(new File(pathAndFileName));
	}
	
	/**
	 * Writes object to the output stream, returns success
	 * @param object
	 * @param out
	 * @return success
	 */
	public boolean saveObject(T object, ObjectOutputStream out) {
		try {
			out.writeObject(object);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Writes object to the output stream, returns success.
	 * @param object
	 * @param out
	 * @return success
	 */
	public boolean saveObject(T object, OutputStream out) {
		try {
			return saveObject(object, new ObjectOutputStream(out));
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Writes object to the file;
	 * @param object
	 * @param file
	 * @return success
	 */
	public boolean saveObject(T object, File file) {
		try {
			return saveObject(object, new FileOutputStream(file));
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Writes object to the file at 'pathAndFileName';
	 * @param object
	 * @param pathAndFileName
	 * @return success
	 */
	public boolean saveObject(T object, String pathAndFileName) {
		return saveObject(object, new File(pathAndFileName));
	}

}
