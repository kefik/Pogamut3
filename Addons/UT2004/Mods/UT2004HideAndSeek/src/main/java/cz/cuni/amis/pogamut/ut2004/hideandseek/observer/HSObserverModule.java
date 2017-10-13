package cz.cuni.amis.pogamut.ut2004.hideandseek.observer;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.communication.translator.IWorldMessageTranslator;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.ut2004.communication.translator.observer.ObserverFSM;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ObserverModule;
import cz.cuni.amis.pogamut.ut2004.observer.IUT2004Observer;

public class HSObserverModule extends UT2004ObserverModule {

	@Override
	protected void configureModules() {
		super.configureModules();
		addModule(new AbstractModule() {

			@Override
			public void configure() {
				bind(IWorldMessageTranslator.class).to(ObserverFSM.class);
				bind(IWorldView.class).to(IVisionWorldView.class);
				bind(IVisionWorldView.class).to(UT2004WorldView.class);
				bind(ComponentDependencies.class).annotatedWith(Names.named(UT2004WorldView.WORLDVIEW_DEPENDENCY)).toProvider(worldViewDependenciesProvider);
				bind(IAgent.class).to(IUT2004Observer.class);
				
				// THIS tells guice it should instantiate our class and not default one
				bind(IUT2004Observer.class).to(HSObserver.class); 
			}

		});
	}
}