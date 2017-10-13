package cz.cuni.pogamut.posh.nbparser;

import java.util.Collection;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserFactory;

/**
 * Factory that is creating parser for Posh .lap files.
 * Used in layer.xml to detect syntax errors in editor.
 * @author Honza
 */
public class NbPoshParserFactory extends ParserFactory {

    @Override
    public Parser createParser(Collection<Snapshot> snapshots) {
        return new NbPoshParser();
    }

}
