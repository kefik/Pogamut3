package cz.cuni.pogamut.posh.nblexer;

import cz.cuni.amis.pogamut.sposh.elements.PoshParserConstants;
import java.util.*;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * LanguageHierarchy contains list of token types for our language(posh), and creates
 * a new instances of our Lexer.
 * LanguageHiearchy is identified by MIME.
 * @author Honza
 */
public class PoshLanguageHiearchy extends LanguageHierarchy<PoshTokenId> {
    private static List<PoshTokenId> tokens;
    private static Map<Integer, PoshTokenId> idToToken;

    private static void init() {
        tokens = Arrays.<PoshTokenId>asList(new PoshTokenId[] {
            new PoshTokenId("EOF", "whitespace", PoshParserConstants.EOF),
            new PoshTokenId("SINGLE_LINE_COMMENT", "comment", PoshParserConstants.SINGLE_LINE_COMMENT),
            new PoshTokenId("LBRACE", "brace", PoshParserConstants.LBRACE),
            new PoshTokenId("RBRACE", "brace", PoshParserConstants.RBRACE),
            new PoshTokenId("DC", "keyword", PoshParserConstants.DC),
            new PoshTokenId("AP", "keyword", PoshParserConstants.AP),
            new PoshTokenId("COMPETENCE", "keyword", PoshParserConstants.COMPETENCE),
            new PoshTokenId("ELEMENTS", "keyword", PoshParserConstants.ELEMENTS),
            new PoshTokenId("GOAL", "keyword", PoshParserConstants.GOAL),
            new PoshTokenId("HOURS", "keyword", PoshParserConstants.HOURS),
            new PoshTokenId("MINUTES", "keyword", PoshParserConstants.MINUTES),
            new PoshTokenId("SECONDS", "keyword", PoshParserConstants.SECONDS),
            new PoshTokenId("HZ", "keyword", PoshParserConstants.HZ),
            new PoshTokenId("PM", "keyword", PoshParserConstants.PM),
            new PoshTokenId("NONE", "keyword", PoshParserConstants.NONE),
            new PoshTokenId("TRIGGER", "keyword", PoshParserConstants.TRIGGER),
            new PoshTokenId("NIL", "keyword", PoshParserConstants.NIL),
            new PoshTokenId("DRIVES", "keyword", PoshParserConstants.DRIVES),
            new PoshTokenId("DOCUMENTATION", "keyword", PoshParserConstants.DOCUMENTATION),
            new PoshTokenId("VARS", "keyword", PoshParserConstants.VARS),
            new PoshTokenId("COMMA", "separator", PoshParserConstants.COMMA),
            new PoshTokenId("EQUAL_SIGN", "operator", PoshParserConstants.EQUAL_SIGN),
            new PoshTokenId("PREDICATE", "operator", PoshParserConstants.PREDICATE),
            new PoshTokenId("NUMFLOAT", "number", PoshParserConstants.NUMFLOAT),
            new PoshTokenId("NUMINT", "number", PoshParserConstants.NUMINT),
            new PoshTokenId("NAME", "identifier", PoshParserConstants.NAME),
            new PoshTokenId("STRINGVALUE", "literal", PoshParserConstants.STRINGVALUE),
            new PoshTokenId("COMMENT", "string", PoshParserConstants.COMMENT),
            new PoshTokenId("VARIABLE", "identifier", PoshParserConstants.VARIABLE),
            new PoshTokenId("ERROR", "error", PoshParserConstants.ERROR_CHARS),
        });


        idToToken = new HashMap<Integer, PoshTokenId>();
        for (PoshTokenId token : tokens) {
            idToToken.put(token.ordinal(), token);
        }

    }

    static synchronized PoshTokenId getToken(int id) {
        if (idToToken == null)
            init();
        PoshTokenId token = idToToken.get(id);
        if (token == null) {
            throw new IllegalArgumentException("No token mapping for id " + id);
        }
        return token;
    }

    @Override
    protected Collection<PoshTokenId> createTokenIds() {
        if (tokens == null)
            init();
        return tokens;
    }

    @Override
    protected synchronized Lexer<PoshTokenId> createLexer(LexerRestartInfo<PoshTokenId> info) {
        return new PoshLexar(info);
    }

    @Override
    protected String mimeType() {
        return "text/x-posh";
    }

}
