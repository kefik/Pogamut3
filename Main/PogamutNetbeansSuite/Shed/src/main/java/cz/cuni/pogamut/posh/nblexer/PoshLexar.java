package cz.cuni.pogamut.posh.nblexer;

import cz.cuni.amis.pogamut.sposh.elements.PoshParserTokenManager;
import cz.cuni.amis.pogamut.sposh.elements.SimpleCharStream;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * Token lexar that reads input and returns tokens for it.
 * @author Honza
 */
public class PoshLexar implements Lexer<PoshTokenId> {

    private LexerRestartInfo<PoshTokenId> info;
    private PoshParserTokenManager tokenManager;

    PoshLexar(LexerRestartInfo<PoshTokenId> info) {
        this.info = info;
        SimpleCharStream stream = new PoshCharStream(info.input());
        tokenManager = new PoshParserTokenManager(stream);
    }

    @Override
    public org.netbeans.api.lexer.Token<PoshTokenId> nextToken() {
        PoshTokenId nbToken = PoshLanguageHiearchy.getToken(tokenManager.getNextToken().kind);
        if (info.input().readLength() < 1) {
            return null;
        }
        return info.tokenFactory().createToken(nbToken);
    }

    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {
    }
}
