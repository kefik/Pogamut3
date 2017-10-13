package cz.cuni.amis.pogamut.ut2004.analyzer;

import com.google.inject.AbstractModule;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerModule;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;

public class UT2004AnalyzerModule extends UT2004ServerModule {

	@Override
	protected void configureModules() {
		super.configureModules();
		addModule(new AbstractModule() {

			@Override
			protected void configure() {
				bind(IUT2004Server.class).to(IUT2004Analyzer.class);
				bind(IUT2004Analyzer.class).to(UT2004Analyzer.class);
				bind(UT2004AnalyzerParameters.class).toProvider(getAgentParamsProvider());
			}
			
		});
	}
	
}
