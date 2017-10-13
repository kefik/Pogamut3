package cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.amis.pogamut.base.communication.translator.event.WorldEventIdentityWrapper;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PathList;

public class Path extends TranslatorEvent {
	
	private String pathId;
	
	private List<PathList> path;
	
	public Path(String pathId, List<PathList> path, long simTime) {
		super(simTime);
		this.pathId = pathId;
		if (this.pathId == null) throw new IllegalArgumentException("'pathId' can't be null");
		this.path = new LinkedList<PathList>(path);
		if (this.path == null) throw new IllegalArgumentException("'path' can't be null");
	}

	/**
	 * Returns a path id (as requested by the GETPATH command).
	 * @return
	 */
	public String getPathId() {
		return pathId;
	}

	/**
	 * Returns list of navpoints you have to follow
	 * @return
	 */
	public List<PathList> getPath() {		
		return path;
	}
	
	@Override
	public String toString() {
		return "Path[pathId = '"+pathId+"', path.size() = "+path.size()+"]";
	}


}
