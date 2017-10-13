package cz.cuni.pogamut.shed;

import java.awt.Component;
import java.io.IOException;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
import org.netbeans.spi.java.project.support.ui.templates.JavaTemplates;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle.Messages;

/**
 * This class is an iterator used either by <tt>New File</tt> wizard or by
 * wizard invoked by the user.
 */
@TemplateRegistrations({
    @TemplateRegistration(folder = "Classes",
    position = 625,
    content = "Action.java.template",
    scriptEngine = "freemarker",
    displayName = "#Action.java",
    iconBase = JavaTemplates.JAVA_ICON,
    description = "ActionDescription.html",
    category = {"java-classes", "java-classes-basic"}),
    @TemplateRegistration(folder = "Classes",
    position = 626,
    content = "Sense.java.template",
    scriptEngine = "freemarker",
    displayName = "#Sense.java",
    iconBase = JavaTemplates.JAVA_ICON,
    description = "SenseDescription.html",
    category = {"java-classes", "java-classes-basic"})
})
@Messages({
    "Action.java=Yaposh Action",
    "Sense.java=Yaposh Sense"})
public final class NewPrimitiveWizardIterator implements WizardDescriptor.InstantiatingIterator<WizardDescriptor> {
    /**
     * Path in the layer.xml filesystem to the template for the action. Must be 
     * same as the content in the {@link TemplateRegistration#content() } above.
     */
    public static final String ACTION_TEMPLATE_FILE = "Templates/Classes/Action.java"; // NO18N
    /**
     * Path in the layer.xml filesystem to the template for the sense. Must be 
     * same as the content in the {@link TemplateRegistration#content() } above.
     */
    public static final String SENSE_TEMPLATE_FILE = "Templates/Classes/Sense.java"; // NO18N

    private int index;
    private WizardDescriptor wizard;
    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;

    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {
        if (panels == null) {

            Project project = Templates.getProject(wizard);
            Sources sources = ProjectUtils.getSources(project);
            SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(JavaTemplates.createPackageChooser(project, groups));
            String[] steps = createSteps();

            for (int i = 0; i < panels.size(); i++) {
                Component c = panels.get(i).getComponent();
                if (steps[i] == null) {
                    // Default step name to component name of panel. Mainly
                    // useful for getting the name of the target chooser to
                    // appear in the list of steps.
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    jc.putClientProperty(WizardDescriptor.PROP_TITLE, "New primitive");
                    // Sets highlighted step
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    // Set steps names shown on the left side of the wizard
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                    // Turn on numbering of all steps
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                }
            }
        }
        return panels;
    }

    @Override
    public Set<?> instantiate() throws IOException {
        // TODO: Use RequestProcessor for instantiation, this takes too much time.
        String className = Templates.getTargetName(wizard);
        FileObject pkg = Templates.getTargetFolder(wizard);
        DataFolder targetFolder = DataFolder.findFolder(pkg);
        FileObject templateFile = Templates.getTemplate(wizard);
        DataObject templateDataObject = DataObject.find(templateFile);
        DataObject createdDataObject = templateDataObject.createFromTemplate(targetFolder, className);

        OpenCookie open = (OpenCookie) createdDataObject.getCookie(OpenCookie.class);
        if (open != null) {
            open.open();
        }
        return Collections.singleton(createdDataObject);
    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    @Override
    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }

    @Override
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        return getPanels().get(index);
    }

    @Override
    public String name() {
        return index + 1 + ". from " + panels.size();
    }

    @Override
    public boolean hasNext() {
        return index < getPanels().size() - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }
    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then use
    // ChangeSupport to implement add/removeChangeListener and call fireChange
    // when needed

    // You could safely ignore this method. Is is here to keep steps which were
    // there before this wizard was instantiated. It should be better handled
    // by NetBeans Wizard API itself rather than needed to be implemented by a
    // client code.
    private String[] createSteps() {
        String[] beforeSteps = (String[]) wizard.getProperty(WizardDescriptor.PROP_CONTENT_DATA);
        if (beforeSteps == null) {
            beforeSteps = new String[1];
        }
        String[] res = new String[(beforeSteps.length - 1) + panels.size()];
        for (int i = 0; i < res.length; i++) {
            if (i < (beforeSteps.length - 1)) {
                res[i] = beforeSteps[i];
            } else {
                res[i] = panels.get(i - beforeSteps.length + 1).getComponent().getName();
            }
        }
        return res;
    }
}
