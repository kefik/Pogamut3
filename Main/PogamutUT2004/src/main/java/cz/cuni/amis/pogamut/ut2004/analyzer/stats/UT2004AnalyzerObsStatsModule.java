package cz.cuni.amis.pogamut.ut2004.analyzer.stats;

import com.google.inject.AbstractModule;

import cz.cuni.amis.pogamut.ut2004.analyzer.IUT2004AnalyzerObserver;
import cz.cuni.amis.pogamut.ut2004.analyzer.UT2004AnalyzerFullObserverModule;

public class UT2004AnalyzerObsStatsModule extends UT2004AnalyzerFullObserverModule {
	
	@Override
	protected void configureModules() {
		super.configureModules();
		addModule(new AbstractModule() {

			@Override
			protected void configure() {
				bind(IUT2004AnalyzerObserver.class).to(UT2004AnalyzerObsStats.class);
			}
			
		});
	}

}
