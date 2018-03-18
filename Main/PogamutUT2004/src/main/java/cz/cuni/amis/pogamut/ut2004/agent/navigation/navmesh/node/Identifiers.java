package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node;

import java.io.Serializable;

public class Identifiers {
	
	private static class AbstractId implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private int value;
		
		public AbstractId( int value )
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return value;
		}
		
		@Override
		public int hashCode() {
			return Integer.hashCode(value);
		}
		
		@Override
		public boolean equals(Object otherObject) {
			if ( otherObject == null || getClass() != otherObject.getClass() ) {
				return false;
			}
			
			AbstractId other = (AbstractId) otherObject;
			return value == other.value;
		}
		
		@Override
		public String toString() {
			return "#"+Integer.toString(value);
		}
	}
	
	public static final class VertexId extends AbstractId {
		
		private static final long serialVersionUID = 1L;

		public VertexId( int value )
		{
			super(value);
		}
	}
	
	public static final class PolygonId extends AbstractId {
		
		private static final long serialVersionUID = 1L;

		public PolygonId( int value )
		{
			super(value);
		}
	}
	
	public static final class EdgeId extends AbstractId {
		
		private static final long serialVersionUID = 1L;

		public EdgeId( int value )
		{
			super(value);
		}
	}
}
