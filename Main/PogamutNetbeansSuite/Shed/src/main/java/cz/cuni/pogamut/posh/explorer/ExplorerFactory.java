package cz.cuni.pogamut.posh.explorer;

import cz.cuni.amis.pogamut.sposh.executor.PrimitiveData;
import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import cz.cuni.pogamut.shed.NewPrimitiveWizardIterator;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.project.Project;
import org.netbeans.modules.project.uiapi.ProjectChooserFactory;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.datatransfer.ExTransferable;

/**
 * Factory for creating the crawler explorers. Explorers are GUI elements that
 * display crawled items.
 *
 * @author Honza
 */
public final class ExplorerFactory {

    private ExplorerFactory() {
    }

    /**
     * Create explorer for actions (classes implementing IAction) on classpath
     * of the project. The crawler for actions is automatically started, no need
     * to start it.
     *
     * @param paletteActions Actions for the palette created explorer belongs to
     * @param project Project whose classpath is searched for actions and used
     * by the new action wizard.
     * @param listeners Listeners to be registered with crawler, they are
     * automatically removed, once crawling is finished.
     * @return Created explorer.
     */
    public static Explorer<PrimitiveData> createActionsExplorer(IPaletteActions paletteActions, Project project, CrawlerListener<PrimitiveData>... listeners) {
        ClassCrawler crawler = new IActionCrawler(project);
        Explorer<PrimitiveData> explorer = new Explorer(paletteActions, new ActionsExplorerActions(crawler, project));

        crawler.addListener(explorer);
        for (CrawlerListener<PrimitiveData> listener : listeners) {
            crawler.addListener(listener);
        }
        crawler.crawl();
        // Note: once crawling is finished, explorer will remove itself as listener of crawler.
        return explorer;
    }

    /**
     * Create explorer for sense (classes implementing ISense) on classpath of
     * the project. The crawler for sense is automatically started, no need to
     * start it.
     *
     * @param paletteActions Actions for the palette created explorer belongs to
     * @param project Project whose classpath is searched for sense.
     * @param listeners Listeners to be registered with crawler, they are
     * automatically removed, once crawling is finished.
     * @return Created explorer.
     */
    public static Explorer<PrimitiveData> createSensesExplorer(IPaletteActions paletteActions, Project project, CrawlerListener<PrimitiveData>... listeners) {
        ClassCrawler crawler = new ISenseCrawler(project);
        Explorer<PrimitiveData> explorer = new Explorer(paletteActions, new SensesExplorerActions(crawler, project));

        crawler.addListener(explorer);
        for (CrawlerListener<PrimitiveData> listener : listeners) {
            crawler.addListener(listener);
        }
        crawler.crawl();
        // Note: once crawling is finished, explorer will remove itself as listener of crawler.
        return explorer;
    }

    /**
     * Create explorer for {@link Competence competences} in the @plan.
     *
     * @param paletteActions Actions for the palette created explorer belongs to
     * @param plan Plan in which explorer looks for competences.
     * @return Created explorer
     */
    public static Explorer<Competence> createCompetenceExplorer(IPaletteActions paletteActions, PoshPlan plan) {
        Crawler<Competence> crawler = CrawlerFactory.createCompetenceCrawler(plan);
        Explorer<Competence> explorer = new Explorer(paletteActions, new CompetenceExplorerActions(plan, crawler));

        crawler.addListener(explorer);
        crawler.crawl();
        return explorer;
    }

    /**
     * Create explorer for {@link ActionPattern action patterns} in the @plan.
     *
     * @param paletteActions Actions for the palette created explorer belongs to
     * @param plan Plan in which explorer looks for action patterns.
     * @return Created explorer
     */
    public static Explorer<ActionPattern> createAPExplorer(IPaletteActions paletteActions, PoshPlan lapTree) {
        Crawler<ActionPattern> crawler = CrawlerFactory.createAPCrawler(lapTree);
        Explorer<ActionPattern> explorer = new Explorer<ActionPattern>(paletteActions, new APExplorerActions(lapTree, crawler));

        crawler.addListener(explorer);
        crawler.crawl();
        return explorer;
    }
    
    final static class ActionsExplorerActions extends PrimitiveExplorerActions {

        private final Project project;

        public ActionsExplorerActions(Crawler<PrimitiveData> refreshCrawler, Project project) {
            super(refreshCrawler);
            this.project = project;
        }

        @Override
        public String getNewItemLabel() {
            return "New action (drag and drop)";
        }

        @Override
        public Transferable createNewTransferable() {
            return new PrimitiveWizardTransferable(TriggeredAction.dataFlavor, project, NewPrimitiveWizardIterator.ACTION_TEMPLATE_FILE) {

                @Override
                Object createPrimitive(String FQN) {
                    return LapElementsFactory.createAction(FQN);
                }
            };
        }

        @Override
        public Transferable createTransferable(PrimitiveData data) {
            if (data == null) {
                return null;
            }
            String actionName = data.classFQN;
            return new NodeTransferable<TriggeredAction>(LapElementsFactory.createAction(actionName));
        }
    }

    final static class SensesExplorerActions extends PrimitiveExplorerActions {

        private final Project project;

        public SensesExplorerActions(Crawler<PrimitiveData> refreshCrawler, Project project) {
            super(refreshCrawler);
            this.project = project;
        }

        @Override
        public String getNewItemLabel() {
            return "New sense (drag and drop)";
        }

        @Override
        public Transferable createNewTransferable() {
            return new PrimitiveWizardTransferable(Sense.dataFlavor, project, NewPrimitiveWizardIterator.SENSE_TEMPLATE_FILE) {

                @Override
                Object createPrimitive(String FQN) {
                    return LapElementsFactory.createSense(FQN);
                }
            };
        }

        @Override
        public Transferable createTransferable(PrimitiveData data) {
            if (data == null) {
                return null;
            }
            String senseName = data.classFQN;
            return new NodeTransferable<Sense>(LapElementsFactory.createSense(senseName));
        }
    }

    abstract static class PrimitiveExplorerActions implements IExplorerActions<PrimitiveData> {

        private final Crawler<PrimitiveData> refreshCrawler;

        public PrimitiveExplorerActions(Crawler<PrimitiveData> refreshCrawler) {
            this.refreshCrawler = refreshCrawler;
        }

        @Override
        public final String getDisplayName(PrimitiveData primitive) {
            String displayName = primitive.name;
            if (displayName == null) {
                displayName = primitive.classFQN.replaceFirst("^.*\\.", "");
            }
            return "<html><b>" + displayName + "</b> (" + primitive.classFQN + ")</html>";
        }

        @Override
        public final String getDescription(PrimitiveData metadata) {
            return metadata.getHtmlDescription();
        }

        @Override
        public final boolean filter(String query, boolean caseSensitive, PrimitiveData metadata) {
            // TODO: Better query analysis.
            if (metadata.classFQN.contains(query)) {
                return false;
            }
            if (metadata.name != null && metadata.name.contains(query)) {
                return false;
            }
            for (String tag : metadata.tags) {
                if (tag.contains(query)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public final void refresh(Explorer<PrimitiveData> explorer) {
            refreshCrawler.addListener(explorer);
            refreshCrawler.crawl();
        }

        @Override
        public boolean delete(PrimitiveData item) {
            PGSupport.message("Primitives can't be deleted this way, you have to delete the class in the project and press refresh.");
            return false;
        }

        @Override
        public void openEditor(PrimitiveData metadata) {
            String javaFilePath = metadata.classFQN.replace('.', '/') + ".java";
            for (FileObject curRoot : GlobalPathRegistry.getDefault().getSourceRoots()) {
                FileObject fileObject = curRoot.getFileObject(javaFilePath);
                if (fileObject != null) {
                    DataObject dobj = null;
                    try {
                        dobj = DataObject.find(fileObject);
                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    if (dobj != null) {
                        EditorCookie ec = (EditorCookie) dobj.getCookie(EditorCookie.class);
                        if (ec != null) {
                            ec.open();
                        }
                    }
                }
            }
        }
    }

    /**
     * Transferable that when asked for the data creates a wizard, askes user to
     */
    private abstract static class PrimitiveWizardTransferable<PRIMITIVE> extends ExTransferable.Single {

        private final Project project;
        private final String templateFile;

        /**
         *
         * @param flavor
         * @param project Project in which will be the new primitive created.
         * @param templateFile Template file in the layer.xml filesystem
         */
        public PrimitiveWizardTransferable(DataFlavor flavor, Project project, String templateFile) {
            super(flavor);
            this.project = project;
            this.templateFile = templateFile;
        }

        /**
         * Create new primitive from FQN
         *
         * @param FQN Fully qualified name of the primitive class.
         */
        abstract PRIMITIVE createPrimitive(String FQN);

        @Override
        protected Object getData() {
            FileObject template = FileUtil.getConfigFile(templateFile);
            NewPrimitiveWizardIterator iter = new NewPrimitiveWizardIterator();
            WizardDescriptor wd = new WizardDescriptor(iter);
            wd.putProperty(ProjectChooserFactory.WIZARD_KEY_TEMPLATE, template);
            wd.putProperty(ProjectChooserFactory.WIZARD_KEY_PROJECT, project);
            iter.initialize(wd);

            Object dialogResult = DialogDisplayer.getDefault().notify(wd);
            if (!dialogResult.equals(NotifyDescriptor.YES_OPTION)) {
                return null;
            }
            return createPrimitive(getNewClassFQN(wd));
        }

        private String getNewClassFQN(WizardDescriptor wd) {
            String className = Templates.getTargetName(wd);
            FileObject targetFolder = Templates.getTargetFolder(wd);
            ClassPath targetFolderClasspath = ClassPath.getClassPath(targetFolder, ClassPath.SOURCE);
            String pkg = targetFolderClasspath.getResourceName(targetFolder, '.', false);

            if (!pkg.isEmpty()) {
                return pkg + '.' + className;
            } else {
                return className;
            }
        }
    }

    abstract static class ElementsExplorerActions<T extends PoshElement & INamedElement> implements IExplorerActions<T> {

        private final Crawler<T> refreshCrawler;

        ElementsExplorerActions(Crawler<T> refreshCrawler) {
            this.refreshCrawler = refreshCrawler;
        }

        @Override
        public final String getDisplayName(T item) {
            return "<html><b>" + item.getName() + "</b></html>";
        }

        @Override
        public final boolean filter(String query, boolean caseSensitive, T item) {
            if (caseSensitive) {
                return !item.toString().contains(query);
            }
            return !item.toString().toLowerCase().contains(query.toLowerCase());
        }

        @Override
        public final void refresh(Explorer<T> explorer) {
            refreshCrawler.addListener(explorer);
            refreshCrawler.crawl();
        }

        @Override
        public final void openEditor(T item) {
            // Do nothing, elements are edited in the scene;
        }

        @Override
        public final Transferable createTransferable(T data) {
            return new NodeTransferable<T>(data);
        }
    }

    final static class CompetenceExplorerActions extends ElementsExplorerActions<Competence> {

        private final PoshPlan plan;

        CompetenceExplorerActions(PoshPlan plan, Crawler<Competence> refreshCrawler) {
            super(refreshCrawler);
            this.plan = plan;
        }

        @Override
        public String getNewItemLabel() {
            return "New competence (drag and drop)";
        }

        @Override
        public String getDescription(Competence competence) {
            return competence.getHtmlDescription();
        }

        @Override
        public boolean delete(Competence competence) {
            boolean confirmed = PGSupport.confirm("Are you sure you want to delete competence " + competence.getName());
            if (confirmed) {
                plan.removeCompetence(competence);
                return true;
            }
            return false;
        }

        @Override
        public Transferable createNewTransferable() {
            return new ExTransferable.Single(Competence.dataFlavor) {

                @Override
                protected Object getData() throws IOException, UnsupportedFlavorException {
                    String competenceName = PGSupport.getIdentifierFromDialog("Name of competence");
                    if (competenceName == null) {
                        return null;
                    }

                    String elementName = PGSupport.getIdentifierFromDialog("Name of competence atom");
                    if (elementName == null) {
                        return null;
                    }
                    try {
                        return LapElementsFactory.createCompetence(competenceName, elementName);
                    } catch (DuplicateNameException ex) {
                        throw new FubarException("Creating new competence with only one name, what duplicate?", ex);
                    }
                }
            };
        }
    }

    final static class APExplorerActions extends ElementsExplorerActions<ActionPattern> {

        private final PoshPlan plan;

        APExplorerActions(PoshPlan plan, Crawler<ActionPattern> refreshCrawler) {
            super(refreshCrawler);
            this.plan = plan;
        }

        @Override
        public String getNewItemLabel() {
            return "New action pattern (drag and drop)";
        }

        @Override
        public String getDescription(ActionPattern ap) {
            return ap.getHtmlDescription();
        }

        @Override
        public boolean delete(ActionPattern actionPattern) {
            boolean confirmed = PGSupport.confirm("Are you sure you want to delete action pattern " + actionPattern.getName());
            if (confirmed) {
                plan.removeActionPattern(actionPattern);
                return true;
            }
            return false;
        }

        @Override
        public Transferable createNewTransferable() {
            return new ExTransferable.Single(ActionPattern.dataFlavor) {

                @Override
                protected Object getData() throws IOException, UnsupportedFlavorException {
                    String name = PGSupport.getIdentifierFromDialog("Name of new action pattern.");
                    if (name == null) {
                        return null;
                    }

                    return LapElementsFactory.createActionPattern(name);
                }
            };
        }
    }

    /**
     * Simple transferable (used for drag and drop), that will take {@link PoshElement}
     * and encapsulate it.
     *
     * @param <NODE> Class of transfered node.
     * @author HonzaH
     */
    final static class NodeTransferable<NODE extends PoshElement> extends ExTransferable.Single {

        private NODE dataNode;

        /**
         * Create transferable for a node. The data flavor will be taken from
         * dataNode.
         *
         * @param dataNode Transfered data node.
         */
        NodeTransferable(NODE dataNode) {
            super(dataNode.getDataFlavor());
            this.dataNode = dataNode;
        }

        /**
         * Get transfered node.
         */
        @Override
        protected NODE getData() {
            return dataNode;
        }
    }
}
