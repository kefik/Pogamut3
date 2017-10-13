package cz.cuni.amis.pogamut.base.communication.command.impl;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.logging.Level;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.jmx.IJMXEnabled;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.command.ICommandListener;
import cz.cuni.amis.pogamut.base.communication.command.ICommandSerializer;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldWriterProvider;
import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.communication.messages.CommandMessage;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.controller.ComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.controller.IComponentControlHelper;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.jmx.PogamutJMX;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.ClassUtils;
import cz.cuni.amis.utils.exception.PogamutJMXException;
import cz.cuni.amis.utils.listener.IListener;
import cz.cuni.amis.utils.listener.Listeners;
import cz.cuni.amis.utils.listener.ListenersMap;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * TODO!
 * <p><p>
 * Ignores {@link IComponentControlHelper#startPaused()}, performs {@link IComponentControlHelper#start()} in both start cases.
 *  
 * @author Jimmy
 */
@AgentScoped
public final class Act implements IComponent, IAct, IJMXEnabled {
	
	public static final Token COMPONENT_ID = Tokens.get("Act");
	
	/**
	 * Default termination of commands.
	 */
	public static final String DEFAULT_LINE_END = "\r\n";
	
	private static class CommandMessageListenerNotifier implements Listeners.ListenerNotifier<IListener> {
		
		private CommandMessage msg;
		
		public void setMessage(CommandMessage msg) {
			this.msg = msg;
		}

		@Override
		public void notify(IListener listener) {
			listener.notify(msg);
		}

		@Override
		public Object getEvent() {
			return msg;
		}
	}
	
	private CommandMessageListenerNotifier notifier = new CommandMessageListenerNotifier();

	private IWorldWriterProvider writerProvider;
	
	private Writer writer;
	
	private PrintWriter printWriter;
	
	private ICommandSerializer<String> serializer;
	
	private final LogCategory log;

	private IComponentBus eventBus;
	
	private ComponentController controller;
	
	private ListenersMap<Class> listeners = new ListenersMap<Class>();
	
	@Inject
	public Act(IWorldWriterProvider writerProvider, ICommandSerializer serializer, IComponentBus eventBus, IAgentLogger logger) {
		this.log = logger.getCategory(getComponentId().getToken());
		this.listeners.setLog(log, "Listeners");
		
		this.writerProvider = writerProvider;
		this.serializer = serializer;
		this.writer = null;
		
		this.eventBus = eventBus;
		this.controller = new ComponentController(this, control, eventBus, log, ComponentDependencyType.STARTS_AFTER, writerProvider);
	}
	
	private IComponentControlHelper control = new ComponentControlHelper() {
		
		@Override
		public void stop() {
			writer = null;
			printWriter = null;
		}
		
		@Override
		public void startPaused() {
			start();
		};
		
		@Override
		public void start() {
			if (log.isLoggable(Level.FINE)) log.fine("Getting writer from " + writerProvider + ".");
			writer = writerProvider.getWriter();
			if (writer == null) throw new CommunicationException("Can't get writer, " + writerProvider + ".getWriter() has returned null.", log, this);
			printWriter = new PrintWriter(writer);
		}
		
		@Override
		public void kill() {
			writer = null;
			printWriter = null;
		}

		@Override
		public void reset() {
			writer = null; 
			printWriter = null;
		}

	};
	
	@Override
	public Token getComponentId() {
		return COMPONENT_ID;
	}
	
	public LogCategory getLog() {
		return log;
	}

	/**
	 * Provides the implementation how to send 'command' through 'this.writer'. 
	 */
	protected void sendCommand(CommandMessage command) {
		printWriter.print(serializer.serialize(command) + DEFAULT_LINE_END);
		printWriter.flush();
	}
	
	/**
	 * Sends command through the writer.
	 * 
	 * @param command
	 * @throws CommunicationException
	 */
	public synchronized void act(CommandMessage command) {
		if (!controller.isRunning()) {
			if (log.isLoggable(Level.WARNING)) log.warning("Not running, can't send " + command);
			return;
		}
		if (log.isLoggable(Level.FINE)) log.fine("Sending: " + command);
		
		//log.warning("Sending: " + command);
		
		notifier.setMessage(command);
		Collection<Class> commandClasses = ClassUtils.getSubclasses(command.getClass());
		for (Class cls : commandClasses) {
			listeners.notify(cls, notifier);
		}
		sendCommand(command);
	}
	
	@Override
	public void addCommandListener(Class commandClass, ICommandListener listener) {
		listeners.add(commandClass, listener);
	}

	@Override
	public boolean isCommandListening(Class commandClass, ICommandListener listener) {
		return listeners.isListening(commandClass, listener);
	}
	
	@Override
	public void removeCommandListener(Class commandClass, ICommandListener listener) {
		listeners.remove(commandClass, listener);
	}
	
	@Override
    public void enableJMX(MBeanServer mBeanServer, ObjectName parent) {
        try {
            mBeanServer.registerMBean(this, PogamutJMX.getObjectName(parent, PogamutJMX.ACT_NAME));
        } catch (Exception ex) {
            throw new PogamutJMXException(ex, this);
        }
    }
	
	@Override
	public String toString() {
		return "Act[serializer=" + serializer + "]";
	}

}
