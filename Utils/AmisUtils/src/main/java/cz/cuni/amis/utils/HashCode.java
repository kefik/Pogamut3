package cz.cuni.amis.utils;

/**
 * This class is meant for counting hash codes from any possible type.
 * It declares a two heavily overloaded methods ;-).
 * 
 * 1) hash(whatever) -> returns hash number for 'whatever' (of whatever type)
 * 2) add(whatever)  -> add number to the hashCode for 'whatever' (of whatever type)
 * 
 * Typical usage: 
 * 
 * Usualy you will create a method private countHashCode() which you will call from
 * within the constructors after you've initialized variables from which you want
 * to count the hash code. It will look like this:
 * 
 * private int getHashCode(){
 * 	 HashCode hc = new HashCode(); // creating new HashCode instance
 * 	 hc.add(myFirstIntValue);      // adding first parametr to hash code
 *   hc.add(mySecondIntValue);     // second...
 *   hc.add(myFloatValue);         // third...
 *   return hc.getHash();          // returning the hash
 * }
 * 
 * private final int hashCode;
 * 
 * public int ConstrucotrOfMyClass(){
 *   // initializing variables		
 *   hashCode = getHashCode();
 * }
 * 
 * public int hashCode(){
 * 	 return hashCode;
 * }
 * 
 */
public final class HashCode {
	
	public int hash(boolean b){
		return b ? 0 : 1;
	}
	
	public int hash(byte b){
		return (int) b;
	}
	
	public int hash(char c){
		return (int) c;
	}
	
	public int hash(short s){
		return (int) s;
	}
	
	public int hash(int i){
		return (int) i;
	}
	
	public int hash(long i){
		return (int) i;
	}
	
	public int hash(float f){
		return Float.floatToIntBits(f);
	}
	
	public int hash(double d){		
		long l = Double.doubleToLongBits(d);
		return (int)(l ^ (l >>> 32));
	}
	
	public int hash(Object o){
		return  o == null ? 0 : o.hashCode();
	}
	
	private int hashCode;
	
	public HashCode(){
		hashCode = 17;
	}
		
	private HashCode addNumber(int number){
		hashCode = 37 * hashCode + number;
		return this;
	}
	
	public HashCode add(boolean b){
		addNumber(hash(b));
		return this;
	}
	
	public HashCode add(byte b){
		addNumber(hash(b));
		return this;
	}
	
	public HashCode add(char c){
		addNumber(hash(c));
		return this;
	}
	
	public HashCode add(short s){
		addNumber(hash(s));
		return this;
	}
	
	public HashCode add(int i){
		addNumber(hash(i));
		return this;
	}
	
	public HashCode add(long l){
		addNumber(hash(l));
		return this;
	}
	
	public HashCode add(float f){
		addNumber(hash(f));
		return this;
	}
	
	public HashCode add(double d){
		addNumber(hash(d));
		return this;
	}
	
	public HashCode add(Object o){
		addNumber(hash(o));
		return this;
	}
	
	public int getHash(){
		return hashCode;
	}

}
