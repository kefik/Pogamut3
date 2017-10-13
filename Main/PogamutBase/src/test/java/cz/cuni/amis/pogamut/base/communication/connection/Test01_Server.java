package cz.cuni.amis.pogamut.base.communication.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;



public class Test01_Server extends CommunicationTestCase {
	
	private String[] responses = new String[] {
		"Hello",
		"Bye"
	};
	
	public String[] getResponses() {
		return responses; 
	}
	
	/**
	 * Connects and send GET, check response and send QUIT.
	 */
	@Test
	public void test() {
		
		assertTrue("server not started correctly", getServer().consume("Server started"));
		
		Socket sock = null;
		try {
			sock = new Socket("127.0.0.1", getServer().getPort());
		} catch (UnknownHostException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());			
		}
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		out.println(Server.GET_COMMAND);
		out.flush();
		
		try {
			int i = 0;
			while(in.ready()) {
				if (i >= getResponses().length) {
					fail("server sent too many response lines");
				}
				String line = in.readLine();
				assertTrue("wrong response, EXPECTED: " + getResponses()[i] + ", GOT: " + line, line.equals(getResponses()[i++]));
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		out.println(Server.QUIT_COMMAND);
		out.flush();
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}
		
		try {
			in.close();
		} catch (IOException e) {
		}
		out.close();
		try {
			sock.close();
		} catch (IOException e) {
		}
		
		assertTrue("server didn't receive GET command", getServer().consume("Received: " + Server.GET_COMMAND));
		for (String str : getResponses()) {
			assertTrue("server wrong reply", getServer().consume("Sent: " + str));
		}
		assertTrue("server didn't receive QUIT command", getServer().consume("Received: " + Server.QUIT_COMMAND));
		assertTrue("server didn't terminate", getServer().consume("Server terminated"));
		
		assertTrue("some server output left", getServer().isClear(true));
		
		System.out.println("---/// TEST OK ///---");
	}
	
	public static void main(String[] args) {
		Test01_Server test = new Test01_Server();
		test.setUp();
		test.test();
		test.tearDown();
	}
	
}
