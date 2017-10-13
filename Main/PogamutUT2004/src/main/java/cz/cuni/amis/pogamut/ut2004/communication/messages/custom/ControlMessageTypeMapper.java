package cz.cuni.amis.pogamut.ut2004.communication.messages.custom;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;

public interface ControlMessageTypeMapper<RESULT> {
	
	public RESULT map(Object value);
	
	// =============
	// DIRECT MAPPER
	// =============
	
	public static class DirectMapper<T> implements ControlMessageTypeMapper<T> {

		@Override
		public T map(Object value) {
			return (T) value;
		}
		
	}
	
	public static final DirectMapper DIRECT_MAPPER = new DirectMapper();
	
	// ===================
	// STRING <-> UREANLID
	// ===================
	
	public static class String2UnrealIdMapper implements ControlMessageTypeMapper<UnrealId> {

		@Override
		public UnrealId map(Object value) {
			if (value instanceof UnrealId) {
				return (UnrealId)value;
			}
			if (value instanceof String) {
				return UnrealId.get((String)value);
			}
			return null;
		}
		
	}
	
	public static final String2UnrealIdMapper STRING_2_UNREAL_ID_MAPPER = new String2UnrealIdMapper();
	
	public static class UnrealId2StringMapper implements ControlMessageTypeMapper<String> {

		@Override
		public String map(Object value) {
			if (value instanceof UnrealId) {
				return ((UnrealId)value).getStringId();
			}
			if (value instanceof String) return (String)value;
			return null;
		}
		
	}
	
	public static final UnrealId2StringMapper UNREAL_ID_2_STRING_MAPPER = new UnrealId2StringMapper();
	
	// ===================
	// STRING <-> LOCATION
	// ===================
	
	public static class String2LocationMapper implements ControlMessageTypeMapper<Location> {

		@Override
		public Location map(Object value) {
			if (value instanceof Location) {
				return (Location)value;
			}
			if (value instanceof String) {
				return new Location((String)value);
			}
			return null;
		}
		
	}
	
	public static final String2LocationMapper STRING_2_LOCATION_MAPPER = new String2LocationMapper();
	
	public static class Location2StringMapper implements ControlMessageTypeMapper<String> {

		@Override
		public String map(Object value) {
			if (value instanceof Location) {
				return ((Location)value).toString();
			}
			if (value instanceof String) return (String)value;
			return null;
		}
		
	}
	
	public static final Location2StringMapper LOCATION_2_STRING_MAPPER = new Location2StringMapper();
	

}
