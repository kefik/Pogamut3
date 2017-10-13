package cz.cuni.amis.pogamut.base.communication.connection;

import java.io.Writer;

import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.utils.exception.PogamutIOException;

public abstract class WorldWriter extends Writer {

	@Override
	public abstract void close() throws PogamutIOException;

	@Override
	public abstract void flush() throws PogamutIOException;

	@Override
	public abstract void write(char[] arg0, int arg1, int arg2) throws PogamutIOException, ComponentNotRunningException, ComponentPausedException;

	/**
	 * Whether the writer is ready.
	 * @return
	 * @throws PogamutIOException
	 */
	public abstract boolean ready() throws PogamutIOException;

}
