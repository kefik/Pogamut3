package cz.cuni.amis.pogamut.base.communication.parser.exception;

public class ParserEOFException extends ParserException {

	public ParserEOFException(Object origin) {
		super("EOF met, assuming that underlying reader has been closed.", origin);
	}

}
