package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.LevelGeometryModule;

public class LevelGeometryVisibilityAdapter implements IVisibilityAdapter {

	private LevelGeometryModule levelGeometry;

	public LevelGeometryVisibilityAdapter(LevelGeometryModule levelGeometry) {
		this.levelGeometry = levelGeometry;
	}
	
	@Override
	public boolean isInitialized() {
		return levelGeometry.isInitialized();
	}

	@Override
	public boolean isVisible(ILocated from, ILocated target) {
		return !levelGeometry.getLevelGeometry().rayCast(from.getLocation(), target.getLocation()).isHit();
	}

}
