package cz.cuni.amis.pogamut.base.communication.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.communication.connection.exception.ConnectionException;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnection;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.controller.ComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.stub.component.ManualCheckComponent;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;

public class Test02_SocketConnection extends CommunicationTestCase {

	private String[] responses = new String[] { "Hello", "Bye" };

	@Override
	public String[] getResponses() {
		return responses;
	}

	/**
	 * Connects and send GET, check response and send QUIT.
	 * <p>
	 * <p>
	 * Using SocketConnection + only once initialized Reader and Writer.
	 * <p>
	 * <p>
	 * Doing 10 iterations of connection/reading/writing/closing while reader
	 * and writer remains the same (also reusing SocketConnection object!).
	 */
	@Test
	public void test() {
		IAgentId agentId = new AgentId("Test02_SocketConnection");
		IAgentLogger logger = new AgentLogger(agentId);
		logger.addDefaultConsoleHandler();
		logger.setLevel(Level.ALL);
		IComponentBus bus = new ComponentBus(logger);
		
		ManualCheckComponent starter = new ManualCheckComponent(logger, bus);
		ComponentController starterCtrl = new ComponentController(starter, new ComponentControlHelper(), bus, logger.getCategory(starter), new ComponentDependencies());
		
		ComponentDependencies connDepend = new ComponentDependencies(ComponentDependencyType.STARTS_WITH, starter);
		
		SocketConnection conn = new SocketConnection(connDepend, bus, logger);
		
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			in = new BufferedReader(conn.getReader());
		} catch (ConnectionException e) {
			fail("can't get reader for the connection: " + e.getMessage());
		}
		try {
			out = new PrintWriter(conn.getWriter());
		} catch (ConnectionException e) {
			fail("can't get writer for the connection: " + e.getMessage());
		}

		for (int iter = 0; iter < 10; ++iter) {
			
			bus.reset();

			reinitServer();

			assertTrue("server not started correctly", getServer().consume("Server started"));
			
			conn.setAddress(new SocketConnectionAddress("127.0.0.1", getServer().getPort()));
			
			starterCtrl.manualStart("manual start");
			
			out.println(Server.GET_COMMAND);
			out.flush();

			try {
				int i = 0;
				while (in.ready()) {
					if (i >= getResponses().length) {
						fail("server sent too many response lines");
					}
					String line = in.readLine();
					assertTrue("wrong response, EXPECTED: " + getResponses()[i]
							+ ", GOT: " + line, line
							.equals(getResponses()[i++]));
				}
			} catch (IOException e) {
				fail("can't read whole response: " + e.getMessage());
			}

			out.println(Server.QUIT_COMMAND);
			out.flush();

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}

			assertTrue("server didn't receive GET command", getServer()
					.consume("Received: " + Server.GET_COMMAND));
			for (String str : getResponses()) {
				assertTrue("server wrong reply", getServer().consume(
						"Sent: " + str));
			}
			assertTrue("server didn't receive QUIT command", getServer()
					.consume("Received: " + Server.QUIT_COMMAND));
			assertTrue("server didn't terminate", getServer().consume(
					"Server terminated"));

			starterCtrl.manualStop("manual stop");

			assertTrue("some server output left", getServer().isClear(true));
		}
		
		System.out.println("---/// TEST OK ///---");
	}

	public static void main(String[] args) {
		Test02_SocketConnection test = new Test02_SocketConnection();
		test.setUp();
		test.test();
		test.tearDown();
	}
	
}
