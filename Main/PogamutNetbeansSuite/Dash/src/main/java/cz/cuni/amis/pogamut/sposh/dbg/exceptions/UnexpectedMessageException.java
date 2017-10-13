package cz.cuni.amis.pogamut.sposh.dbg.exceptions;

/**
 * Recieved unexpected message during communication with unreal server.
 * Something was wrong with handshake with UT control server.
 *
 * @author HonzaH
 */
public class UnexpectedMessageException extends Exception {

    public UnexpectedMessageException(String recievedMessage, String expectedMessage) {
        super("Expected '" + expectedMessage + "' but recieved '" + recievedMessage + "'");
    }

    public UnexpectedMessageException(String message) {
        super(message);
    }
}
