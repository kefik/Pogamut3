package cz.cuni.pogamut.posh.nblexer;

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

/**
 * Class representing token, contains
 * <ul>
 *  <li>name - unique name of the token in the parser</li>
 *  <li>id - unique integer id of the token</li>
 *  <li>primaryCategory - category of the token</li>
 * </ul>
 * @author Honza
 */
public class PoshTokenId implements TokenId {
    private final String name;
    private final int id;
    private final String primaryCategory;


    public PoshTokenId(String name, String primaryCategory, int id) {
        this.name = name;
        this.id = id;
        this.primaryCategory = primaryCategory;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int ordinal() {
        return id;
    }

    @Override
    public String primaryCategory() {
        return primaryCategory;
    }

    private static final Language<PoshTokenId> language = new PoshLanguageHiearchy().language();

    public static Language<PoshTokenId> getLanguage() {
        return language;
    }

}
