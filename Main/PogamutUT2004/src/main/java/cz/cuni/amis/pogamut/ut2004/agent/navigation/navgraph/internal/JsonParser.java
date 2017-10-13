package cz.cuni.amis.pogamut.ut2004.agent.navigation.navgraph.internal;

import javax.vecmath.Vector3d;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;

public class JsonParser {
	
	private String body;
	private int index;

	public JsonParser(String body) {
		this.body = body;
		this.index = 0;
	}
	
	protected boolean isEnd() {
		return index < 0 || index >= body.length();					
	}
	
	protected void nextChar() {
		index += 1;
		if (index >= body.length()) index = -1;			
	}
	
	protected void skipWhitespace() {
		while (!isEnd() && isWhitespace()) index += 1;
	}
	
	private boolean isWhitespace() {
		char c = body.charAt(index);
		return c == ' ' || c == '\t';
	}
	
	protected boolean startsWith(String prefix) {
		return body.substring(index).startsWith(prefix);
	}

	/** Next input up to the needle string 
	 *
	 * Sets index to the first character of the found needle. If needle is not found, index is set to the length of body.
	 *
	 * @param needle string to look for
	 * @return input from current index up to the needle (exclusively).
	 */
	protected String nextTo(String needle) {
		int indexOfNeedle = body.substring(index).indexOf(needle);
		if (indexOfNeedle >= 0 ) {
			indexOfNeedle += index;
		} else {
			indexOfNeedle = body.length();
		}
		String result = body.substring(index, indexOfNeedle);
		index = indexOfNeedle;
		return result.trim();
	}
	
	/** Next input past the needle string 
	 *
	 * Sets index to the next character past the needle. If needle is not found, index is set to the length of body.
	 *
	 * @param needle string to look for
	 * @return input from current index up to and including the needle
	 */
	protected String nextPast(String needle) {
		String retval = nextTo( needle );
		if ( index < body.length() ) {
			index += needle.length();
		}
		return retval+needle;
	}
	
	protected boolean nextBoolean() {
		String literal = nextTo(",");
		return literal.toLowerCase().equals("true");
	}
	
	protected int nextInt() {
		String literal = nextTo(",");
		return Integer.parseInt(literal);
	}
	
	protected double nextDouble() {
		String literal = nextTo(",");
		return Double.parseDouble(literal);
	}
	
	protected String nextString() {
		skipWhitespace();
		
		if ( startsWith("null") ) {
			nextPast("null");
			return null;
		}
		
		nextPast("\"");
		String literal = nextTo("\"");			
		nextChar();
		return literal;
	}
	
	protected double[] nextNumbers() {
		skipWhitespace();

		if ( startsWith("null") ) {
			nextPast("null");
			return null;
		}
		
		nextPast("[");
		String[] numberStrings = nextTo("]").split(",");
		nextChar();
		
		double[] result = new double[numberStrings.length];
		for (int i = 0; i < numberStrings.length; ++i) {
			result[i] = Double.parseDouble(numberStrings[i]);
		} 
		
		return result;
	}
	
	protected Vector3d nextVector3d() {
		double[] xyz = nextNumbers();
		if (xyz == null) return null;
		return new Vector3d(xyz[0], xyz[1], xyz[2]);
	}
	
	protected UnrealId nextId() {
		String literal = nextString();
		if (literal == null ) {
			return null;
		}
		return UnrealId.get(literal);
	}
	
	protected Location nextLocation() {
		double[] xyz = nextNumbers();
		if (xyz == null) return null;
		return new Location(xyz);
	}
	
	protected Rotation nextRotation() {
		double[] numbers = nextNumbers();
		if (numbers == null) return null;
		return new Rotation(numbers[0], numbers[1], numbers[2]);
	}
	
	protected Velocity nextVelocity() {
		double[] numbers = nextNumbers();
		if (numbers == null) return null;
		return new Velocity(numbers[0], numbers[1], numbers[2]);
	}
	
}
