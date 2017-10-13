package cz.cuni.amis.pogamut.ut2004.hideandseek.server;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.communication.translator.IWorldMessageTranslator;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.server.IWorldServer;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.ut2004.communication.translator.server.ServerFSM;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerModule;

import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;

/**
 * UT2004HideAndSeekServerModule
 * 
 * @author Jimmy
 */
public class UT2004HSServerModule extends UT2004ServerModule {

	@Override
	protected void configureModules() {
		super.configureModules();
		addModule(new AbstractModule() {

			@Override
			public void configure() {
				bind(IWorldMessageTranslator.class).to(ServerFSM.class);
				bind(IWorldView.class).to(IVisionWorldView.class);
				bind(IVisionWorldView.class).to(UT2004WorldView.class);
				bind(ComponentDependencies.class).annotatedWith(Names.named(UT2004WorldView.WORLDVIEW_DEPENDENCY)).toProvider(worldViewDependenciesProvider);
				bind(IAgent.class).to(IWorldServer.class);
				bind(IWorldServer.class).to(IUT2004Server.class);
				bind(IUT2004Server.class).to(UT2004HSServer.class);
			}

		});
	}
}
