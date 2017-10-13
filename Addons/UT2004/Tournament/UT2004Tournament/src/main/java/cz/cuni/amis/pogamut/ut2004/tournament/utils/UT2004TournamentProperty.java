package cz.cuni.amis.pogamut.ut2004.tournament.utils;

/**
 * The key is returned via {@link UT2004TournamentProperty#toString()} method.
 * 
 * @author Jimmy
 */
public enum UT2004TournamentProperty {
	

	/**
	 * Directory containing compiled Pogamut classes for UT2004. It is a root directory containing various directories with revisions
	 * of certain Pogamut projects (Core/Unreal/UT2004/SPOSHs/ACTrs).
	 */
	UT2004_DIR("pogamut.ut2004.tournament.ut2004.dir");

	private String key;

	private UT2004TournamentProperty(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
	public String toString() {
		return key;
	}
	
}
