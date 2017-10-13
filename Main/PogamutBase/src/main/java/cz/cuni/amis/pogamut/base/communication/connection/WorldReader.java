package cz.cuni.amis.pogamut.base.communication.connection;

import java.io.IOException;
import java.io.Reader;

import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.utils.exception.PogamutIOException;

public abstract class WorldReader extends Reader {

	public boolean ready() throws PogamutIOException {
		try {
			return super.ready();
		} catch (IOException e) {
			throw new PogamutIOException(e, this);
		}
	}
	
	@Override
	public abstract void close() throws PogamutIOException;

	@Override
	public abstract int read(char[] arg0, int arg1, int arg2) throws PogamutIOException, ComponentNotRunningException, ComponentPausedException;

	public static class WorldReaderWrapper extends WorldReader {

		private Reader reader;

		public WorldReaderWrapper(Reader reader) {
			this.reader = reader;
		}
		
		@Override
		public void close() throws PogamutIOException {
			try {
				reader.close();
			} catch (IOException e) {
				throw new PogamutIOException(e, this);
			}
		}

		@Override
		public int read(char[] arg0, int arg1, int arg2)
				throws PogamutIOException, ComponentNotRunningException {
			try {
				return reader.read(arg0, arg1, arg2);
			} catch (IOException e) {
				throw new PogamutIOException(e, this);
			}
		}
		
	}
	
}
