package cz.cuni.pogamut.posh.nbparser;

import cz.cuni.amis.pogamut.sposh.elements.PoshParser;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;

/**
 * NetBeans parser of Yaposh plans. The parser parses the text in the editor and
 * stores results in {@link ParserResult} class. The results are later passed to
 * the {@link SyntaxErrorsHighlightingTask}. This parser is instantiated by
 * <code>layer.xml</code>.
 *
 * @author Honza
 */
class NbPoshParser extends Parser {

    private PoshParser parser;
    private Snapshot snapshot;
    private List<cz.cuni.amis.pogamut.sposh.elements.ParseException> syntaxExceptions;

    @Override
    public void parse(Snapshot snapshot, Task task, SourceModificationEvent sme) throws ParseException {
        this.syntaxExceptions = new ArrayList<cz.cuni.amis.pogamut.sposh.elements.ParseException>();
        this.snapshot = snapshot;
        this.parser = new PoshParser(new StringReader(snapshot.getText().toString()));

        try {
            parser.parsePlan();
        } catch (cz.cuni.amis.pogamut.sposh.elements.ParseException ex) {
            syntaxExceptions.add(ex);
        }
    }

    @Override
    public Result getResult(Task task) throws ParseException {
        return new NbPoshParser.ParserResult(snapshot, syntaxExceptions);
    }

    @Override
    public void cancel() {
        // We don't allow cancel.
    }

    @Override
    public void addChangeListener(ChangeListener cl) {
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
    }

    static class ParserResult extends Parser.Result {

        private boolean valid = true;
        private Collection<cz.cuni.amis.pogamut.sposh.elements.ParseException> syntaxExceptions;

        public ParserResult(Snapshot snapshot, Collection<cz.cuni.amis.pogamut.sposh.elements.ParseException> syntaxExceptions) {
            super(snapshot);
            this.syntaxExceptions = syntaxExceptions;
        }

        public Collection<cz.cuni.amis.pogamut.sposh.elements.ParseException> getSyntaxErrors()
                throws org.netbeans.modules.parsing.spi.ParseException {
            if (!valid) {
                throw new org.netbeans.modules.parsing.spi.ParseException("Task is already finished. This result has been invalidated.");
            }
            return syntaxExceptions;
        }

        @Override
        protected void invalidate() {
            valid = false;
        }
    }
}
