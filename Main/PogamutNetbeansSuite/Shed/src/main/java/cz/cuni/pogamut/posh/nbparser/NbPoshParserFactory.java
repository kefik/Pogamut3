package cz.cuni.pogamut.posh.nbparser;

import java.util.Collection;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserFactory;

/**
 * Factory for creating parser of Yaposh plans. The factory creates the parser
 * that detects syntax errors. The result of the parser is later passed to the
 * GUI task that adds error hints to text view of Shed. Instantiated in
 * <code>layer.xml</code> to detect syntax errors in editor.
 *
 * @author Honza
 */
public class NbPoshParserFactory extends ParserFactory {

    @Override
    public Parser createParser(Collection<Snapshot> snapshots) {
        return new NbPoshParser();
    }
}
