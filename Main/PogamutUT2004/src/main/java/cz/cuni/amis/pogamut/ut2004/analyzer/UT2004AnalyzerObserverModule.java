package cz.cuni.amis.pogamut.ut2004.analyzer;

import com.google.inject.AbstractModule;

import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ObserverModule;
import cz.cuni.amis.pogamut.ut2004.observer.IUT2004Observer;

@Deprecated
public class UT2004AnalyzerObserverModule extends UT2004ObserverModule {
	
	@Override
	protected void configureModules() {
		super.configureModules();
		addModule(new AbstractModule() {

			@Override
			protected void configure() {
				bind(IUT2004Observer.class).to(IUT2004AnalyzerObserver.class);
				bind(IUT2004AnalyzerObserver.class).to(UT2004AnalyzerObserver.class);
				bind(UT2004AnalyzerObserverParameters.class).toProvider(getAgentParamsProvider());
			}
			
		});
	}

}
