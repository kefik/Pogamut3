package cz.cuni.amis.pogamut.base.communication.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import cz.cuni.amis.utils.TestOutput;

public class Server extends TestOutput implements Runnable {

	public static final String GET_COMMAND = "get";
	public static final String QUIT_COMMAND = "quit";

	private int port;

	private String[] response;
	
	private ServerSocket serverSocket = null;
	
	private Socket socket = null;
	
	private boolean tearedDown = false;
	
	public Server(int port, String[] response) throws IOException {
		super("Server");
		this.port = port;
		this.response = response;
		this.serverSocket = new ServerSocket(this.port);
		new Thread(this, "Server").start();
	}
	
	private void sendResponse(PrintWriter out) {
		for (String s : response) {
			if (s == null) continue;
			out.println(s);
			push("Sent: " + s);
		}
		out.flush();
	}

	@Override
	public void run() {
		try {
			push("Server started");
			this.socket = serverSocket.accept();
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			while (true) {
				String command = in.readLine();
				push("Received: " + command);
				if (command.equals(QUIT_COMMAND)) {					
					break;
				}
				if (command.equals(GET_COMMAND)) {
					sendResponse(out);
				}
			}
			in.close();
			out.close();
			socket.close();
		} catch (Exception e) {
			if (!tearedDown) e.printStackTrace();
		}
		push("Server terminated");

	}
	
	public void tearDown() {
		tearedDown = true;
		try {
			if (serverSocket != null) serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();			
		}
		try {
			if (socket != null) this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getPort() {
		return port;
	}
	
}
