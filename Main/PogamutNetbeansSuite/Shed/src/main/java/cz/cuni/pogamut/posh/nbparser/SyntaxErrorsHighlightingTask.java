package cz.cuni.pogamut.posh.nbparser;

import cz.cuni.amis.pogamut.sposh.elements.ParseException;
import cz.cuni.amis.pogamut.sposh.elements.Token;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/**
 * Task that adds error hints to the right side of yaposh plan in text view. The
 * syntax errors are provided by {@link NbPoshParser}.
 *
 * @author Honza
 */
class SyntaxErrorsHighlightingTask extends ParserResultTask {

    @Override
    public void run(Result result, SchedulerEvent event) {
        try {
            Collection<ParseException> syntaxErrors = ((NbPoshParser.ParserResult) result).getSyntaxErrors();
            Document document = result.getSnapshot().getSource().getDocument(false);
            List<ErrorDescription> errorHints = new ArrayList<ErrorDescription>();

            for (ParseException syntaxError : syntaxErrors) {
                Token token = syntaxError.currentToken;
                int start = NbDocument.findLineOffset((StyledDocument) document, token.beginLine < 1 ? 0 : token.beginLine - 1) +(token.beginColumn<1?1:token.beginColumn - 1);
                int end = NbDocument.findLineOffset((StyledDocument) document, token.endLine < 1 ? 0 : token.endLine - 1) + token.endColumn;

                errorHints.add(ErrorDescriptionFactory.createErrorDescription(
                        Severity.ERROR,
                        syntaxError.getMessage(),
                        document,
                        document.createPosition(start),
                        document.createPosition(end)));
            }
            HintsController.setErrors(document, "posh-errors", errorHints);
        } catch (BadLocationException ex1) {
            Exceptions.printStackTrace(ex1);
        } catch (org.netbeans.modules.parsing.spi.ParseException ex1) {
            Exceptions.printStackTrace(ex1);
        }
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
    }

    @Override
    public void cancel() {
    }
}
