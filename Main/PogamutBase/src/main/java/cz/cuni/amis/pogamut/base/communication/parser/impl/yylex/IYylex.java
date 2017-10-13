package cz.cuni.amis.pogamut.base.communication.parser.impl.yylex;

import java.io.IOException;
import java.io.Reader;

import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;


/**
 * "Wrapper interface" for Yylex, you should wrap your yylex implementation with this
 * interface allowing the parser to set the reader into the yylex (that triggers creation
 * of new instance of your Yylex usually) + providing a method for parsing messages. 
 * 
 * @author Jimmy
 */
public interface IYylex {
	
	public InfoMessage yylex() throws IOException;
	
	public void close() throws IOException;
	
	public void setReader(Reader reader);
	
	public void setObserver(IYylexObserver observer);
	
}
