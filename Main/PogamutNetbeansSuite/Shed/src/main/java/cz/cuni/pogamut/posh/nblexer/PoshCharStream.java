package cz.cuni.pogamut.posh.nblexer;

import cz.cuni.amis.pogamut.sposh.elements.SimpleCharStream;
import java.io.IOException;
import java.io.Reader;
import org.netbeans.spi.lexer.LexerInput;

/**
 * An implementation of interface CharStream, where the stream is assumed to
 * contain only ASCII characters (with java-like unicode escape processing).
 */
@SuppressWarnings("deprecation")
public class PoshCharStream extends SimpleCharStream {

    private LexerInput input;

    public PoshCharStream(LexerInput input) {
        super((Reader) null, 1, 1, 0);
        this.input = input;
    }

    @Override
    public char BeginToken() throws IOException {
        int chr = input.read();
        if (chr == LexerInput.EOF) {
            throw new IOException();
        }
        return (char) chr;
    }

    @Override
    public String GetImage() {
        return input.readText().toString();
    }

    @Override
    public char[] GetSuffix(int len) {
        if (len > input.readLength()) {
            throw new IllegalArgumentException();
        }
        return input.readText(input.readLength() - len, input.readLength()).toString().toCharArray();
    }

    @Override
    public void backup(int i) {
        input.backup(i);
    }

    @Override
    public int getBeginColumn() {
        return 0;
    }

    @Override
    public int getBeginLine() {
        return 0;
    }

    @Override
    public int getEndColumn() {
        return 0;
    }

    @Override
    public int getEndLine() {
        return 0;
    }

    @Override
    public char readChar() throws IOException {
        int read = input.read();

        if (read == LexerInput.EOF) {
            throw new IOException("EOF reached.");
        }
        return (char) read;
    }

    @Override
    @Deprecated
    public int getColumn() {
        throw new UnsupportedOperationException("Deprecated - Not supported.");
    }

    @Override
    @Deprecated
    public int getLine() {
        throw new UnsupportedOperationException("Deprecated - Not supported.");
    }

    @Override
    public void Done() {
    }
}
