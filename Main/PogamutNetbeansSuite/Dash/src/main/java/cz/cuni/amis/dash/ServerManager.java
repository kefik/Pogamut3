package cz.cuni.amis.dash;

import cz.cuni.amis.pogamut.sposh.dbg.engine.IDebugEngine;
import cz.cuni.amis.pogamut.sposh.dbg.exceptions.UnexpectedMessageException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.openide.util.Exceptions;

/**
 * Manager of UT2004 server. It can pause or resume server.
 *
 * @author Honza
 */
/*final class ServerManager {

    public void connect() {
        // TODO: Implement
        OutputWriter writer = IOProvider.getDefault().getIO("Dash", false).getOut();
        writer.println("ServerManager> Connect to server");
    }

    public void pause() {
        // TODO: Implement
    }

    public void resume() {
        // TODO: Implement
    }

    public void disconnect() {
        // TODO: Implement
        OutputWriter writer = IOProvider.getDefault().getIO("Dash", false).getOut();
        writer.println("ServerManager> Disconnect to server");
    }
}*/



/**
 * Tool for pausing/resuming the UT environment that happens when
 * {@link YaposhEngine} encounters node breakpoint. Basically, every time a
 * node breakpoint is encountered by the engine, we want to stop the UT and when
 * the engine is running again, we want to resume the UT.
 * <p/>
 * That presents few problems: 1)How to detect that engine is running again? 2)
 * What about multiple engines of one UT environment, freeze, resume...What to
 * do? 3) How to get host:port of the UT for the logic?
 * <p/>
 * Basically it is something like semaphor.
 *
 * @author HonzaH
 */
public final class ServerManager {

    void connect() {
        // TODO: Implement from static to instance
    }

    void disconnect() {
        // TODO: Implement from static to instance
    }

    /**
     * Class holding info about connection to the UT server.
     *
     * XXX: Will memory input buffer (ALIVE, BEG, END) keep on growing if not
     * closed?
     */
    private static class PausedConnection {

        /**
         * Which engines are asking server to be paused.
         */
        public final Set<IDebugEngine> blocingEngines;
        /**
         * Opened socket to the paused server.
         */
        public final Socket socket;
        /**
         * How many messages can the connection recieve during handshake without
         * throwing an error.
         */
        private static final int HANDSHAKE_MESSAGE_LIMIT = 100000;
        /**
         * How many milisec to wait before closing the control connection to the
         * GB. Some time needs to be elapsed for already send commands
         * (pause/resume) to take effect.
         */
        private static final int CLOSE_CONTROL_CONNECTION_DELAY_MS = 3000;

        /**
         * Private constructor for new paused connection (used by factory
         * {@link #create(cz.cuni.amis.pogamut.sposh.dbg.engine.SposhDebugEngine, java.net.InetSocketAddress)}).
         *
         * @param initialBlockingEngine Initial engine that asked for server to
         * be paused
         * @param socket Socket connected to the UT that has already finished
         * handshake (HELLO, READY, SHS...EHS).
         */
        private PausedConnection(IDebugEngine initialBlockingEngine, Socket socket) {
            this.blocingEngines = new HashSet<IDebugEngine>();
            this.blocingEngines.add(initialBlockingEngine);
            this.socket = socket;
        }

        /**
         * Send command to the frozen server.
         *
         * @param command command to send
         * @throws IOException When there are trouble sending data over the
         * network
         */
        public void sendTextCommand(String command) throws IOException {
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream());
            serverWriter.println(command);
            serverWriter.flush();
        }

        /**
         * Open connection to the UT server and go throught the greeting phase.
         *
         * @param freezingEngine The engine that asked for new frozen connection
         * @param serverAddress Address of UT server
         * @return opened socket of the UT server that can be supplied with
         * commands
         */
        public static PausedConnection create(IDebugEngine freezingEngine, InetSocketAddress serverAddress) throws IOException, UnexpectedMessageException {
            Socket socket = new Socket();
            socket.connect(serverAddress);

            BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            recieveMessage(socketReader, "HELLO_CONTROL_SERVER");

            PrintWriter socketWriter = new PrintWriter(socket.getOutputStream());
            sendCommand(socketWriter, "READY");

            recieveHandshake(socketReader);

            return new PausedConnection(freezingEngine, socket);
        }

        private static void recieveHandshake(BufferedReader reader) throws IOException, UnexpectedMessageException {
            recieveMessage(reader, "SHS");
            int messageCount = 0;
            String message;
            do {
                message = reader.readLine();
                ++messageCount;
                if (messageCount > HANDSHAKE_MESSAGE_LIMIT) {
                    throw new UnexpectedMessageException("Handshake over limit " + HANDSHAKE_MESSAGE_LIMIT);
                }
            } while (!message.equals("EHS"));
        }

        /**
         * Recieve message from the reader, check that it is what we expected
         * and return the message.
         *
         * @param reader Reader from which to read the message
         * @param expectedMessage Message we are expecting w/o new line
         * @return recieved message
         * @throws IOException When there is a problem with reading data
         * @throws UnexpectedMessageException Recieved message is not what we
         * expected
         */
        private static String recieveMessage(BufferedReader reader, String expectedMessage) throws IOException, UnexpectedMessageException {
            String recievedMessage = reader.readLine();
            if (!recievedMessage.equals(expectedMessage)) {
                throw new UnexpectedMessageException(recievedMessage, expectedMessage);
            }
            return recievedMessage;
        }

        private static void sendCommand(PrintWriter writer, String command) {
            writer.println(command);
            writer.flush();
        }

        /**
         * Close the server. <em>NOTE:</em> This doesn't resume server. That has
         * to be done separately.
         *
         * The socket is not immediately close, I have to wait a while because
         * if a pause/resuem commands were send just before {@link #close() }
         * call, the GB wouldn't have time to process them. I am creating a
         * thread that waits {@link #CLOSE_CONTROL_CONNECTION_DELAY_MS} milisec.
         */
        public void close() {
            Thread closeConnectionThread = new Thread() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(CLOSE_CONTROL_CONNECTION_DELAY_MS);
                        socket.close();
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IOException ex) {
                        // Well, what can we do, nothing. 
                        Exceptions.printStackTrace(ex);
                    }
                }
            };
            closeConnectionThread.start();
        }
    }
    /**
     * All UT servers that are currently frozen, all frozen server have opened
     * sockets. Once server is unfrozen, socket is closed and server is removed
     * from this map.
     */
    private static final Map<InetSocketAddress, PausedConnection> pausedServers = new HashMap<InetSocketAddress, PausedConnection>();
    private static final String PAUSE_COMMAND = "PAUSE {PauseAll True}";
    private static final String RESUME_COMMAND = "PAUSE {PauseAll False}";

    /**
     * Pause a UT server at specified address.
     *
     * @param blockingEngine Engine that is requesting the pause state of UT.
     * @param serverAddress Address and port of the UT server
     */
    public static synchronized void pause(IDebugEngine blockingEngine, InetSocketAddress serverAddress) throws IOException, UnexpectedMessageException {
        assert serverAddress != null;

        PausedConnection pausedConnection = pausedServers.get(serverAddress);

        if (pausedConnection == null) {
            pausedConnection = PausedConnection.create(blockingEngine, serverAddress);
            pausedServers.put(serverAddress, pausedConnection);
        }
        pausedConnection.sendTextCommand(PAUSE_COMMAND);
    }

    /**
     * A UT server is blocked, until all engines that are asking for it to be
     * paused say, that it may resume execution. By calling this method, engine
     * is saying that it is no longer requesting for a UT server to be paused.
     *
     * @param resumingEngine Engine that is no longer requesting a pause
     * (nothing wrong happens if called many times over or if engine hasn't even
     * requested pause).
     * @param serverAddress Address of UT server the engine is using.
     */
    public static synchronized void resume(IDebugEngine resumingEngine, InetSocketAddress serverAddress) throws IOException {
        assert serverAddress != null;

        if (pausedServers.containsKey(serverAddress)) {
            final PausedConnection frozenConnection = pausedServers.get(serverAddress);
            if (frozenConnection.blocingEngines.contains(resumingEngine)) {
                frozenConnection.blocingEngines.remove(resumingEngine);
            }
            if (frozenConnection.blocingEngines.isEmpty()) {
                frozenConnection.sendTextCommand(RESUME_COMMAND);
                frozenConnection.close();
                pausedServers.remove(serverAddress);
            }
        }
    }

    /**
     * Clear all frozen servers that have as initiator passed engine.
     *
     * @param engine
     */
    public static synchronized void clear(IDebugEngine engine) throws IOException {
        Set<InetSocketAddress> nonblockedPausedServers = new HashSet<InetSocketAddress>();

        for (Map.Entry<InetSocketAddress, PausedConnection> pausedServer : pausedServers.entrySet()) {
            PausedConnection pausedConnection = pausedServer.getValue();
            if (pausedConnection.blocingEngines.contains(engine)) {
                pausedConnection.blocingEngines.remove(engine);
            }
            if (pausedConnection.blocingEngines.isEmpty()) {
                nonblockedPausedServers.add(pausedServer.getKey());
            }
        }

        for (InetSocketAddress nonblockedPausedServer : nonblockedPausedServers) {
            pausedServers.remove(nonblockedPausedServer);
        }
    }
}
