package cz.cuni.amis.pogamut.base.communication.connection;

import java.io.IOException;

import junit.framework.TestCase;

public abstract class CommunicationTestCase extends TestCase {
		
	private int port = 1234;
		
	public abstract String[] getResponses();
	
	private Server server = null;
	
	public Server getServer() {
		return server;
	}
	
	public void tearDown() {
		tearDownServer();
	}
	
	public void setUp() {
		initServer();
	}
	
	protected void tearDownServer() {
		if (server != null) server.tearDown();
	}
	
	protected void initServer() {
		int count = 0;
		while (true) {
			try {
				server = new Server(port, getResponses());
				break;
			} catch (IOException e) {
				++port;
				++count;
			}
			if (count > 100) fail("Can't create server or find a free port...");
		}
		try {
			Thread.sleep(100); 
			// to give the Server's thread a chance to initialize...
			// not nice I know...
		} catch (InterruptedException e1) {
		}

	}
	
	protected void reinitServer() {
		tearDownServer();
		initServer();
	}

}
