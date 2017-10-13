package cz.cuni.amis.nb.pogamut.ut2004.project.template;

import cz.cuni.amis.nb.pogamut.base.project.template.utils.TemplateBotProjectWizardPanel;
import cz.cuni.amis.nb.pogamut.base.project.template.utils.ProjectCreationUtils;
import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

public class PogamutGroovyBotWizardIterator implements WizardDescriptor.InstantiatingIterator {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wiz;

    public PogamutGroovyBotWizardIterator() {
    }

    public static PogamutGroovyBotWizardIterator createIterator() {
        return new PogamutGroovyBotWizardIterator();
    }

    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[]{
                    new TemplateBotProjectWizardPanel(),};
    }

    private String[] createSteps() {
        return new String[]{
                    NbBundle.getMessage(PogamutGroovyBotWizardIterator.class, "LBL_CreateProjectStep")
                };
    }

    @Override
    public Set/*<FileObject>*/ instantiate() throws IOException {
        ProjectCreationUtils.handleWizardDescriptor(wiz);

        Set resultSet = new LinkedHashSet();
        File dirF = FileUtil.normalizeFile((File) wiz.getProperty("projdir"));
        dirF.mkdirs();

        FileObject template = Templates.getTemplate(wiz);
        FileObject dir = FileUtil.toFileObject(dirF);
        ProjectCreationUtils.unZipFile(template.getInputStream(), dir);

        String projName = ((String) wiz.getProperty("name"));



        // rename package in Main.java
        //adaptSourceFile(dirF, projName);

        // substitute "javabottemplate" in all sources
        File sourceDir = new File(dirF, "src/groovybottemplate/");
        for (File file : sourceDir.listFiles()) {
            if (file.isFile()) {
                ProjectCreationUtils.substituteInFile(
                        file,
                        Pattern.compile("groovybottemplate"),
                        projName.toLowerCase());
                ProjectCreationUtils.substituteInFile(
                        file,
                        Pattern.compile("__PROJECT_NAME__"),
                        projName);
                if (file.getName().equals("Main.groovy")) {
                    ProjectCreationUtils.substituteInFile(
                            file,
                            Pattern.compile("Main"),
                            projName);
                    file.renameTo(new File(file.getParentFile().getAbsolutePath() + File.separator + projName + ".groovy"));
                }
            }
        }


        // remove all javabottemplate references from properties file and
        // substitute it with new project name
        ProjectCreationUtils.substituteInFile(
                new File(dirF, "nbproject/project.properties"),
                Pattern.compile("groovybottemplate"),
                projName.toLowerCase());

        ProjectCreationUtils.substituteInFile(
                new File(dirF, "nbproject/project.properties"),
                Pattern.compile("GroovyBotTemplate"),
                projName);
        ProjectCreationUtils.substituteInFile(
                new File(dirF, "nbproject/project.properties"),
                Pattern.compile("Main"),
                projName);

        // rename the main package from pogamutjavabot to the name of project
        File rootPackage = new File(dirF, "src/groovybottemplate");
        rootPackage.renameTo(new File(dirF, "src/" + projName.toLowerCase()));

        ProjectCreationUtils.substituteInFile(
                new File(dirF, "nbproject/project.xml"),
                Pattern.compile("<name>GroovyBotTemplate</name>"),
                "<name>" + projName + "</name>");

        // Always open top dir as a project:
        resultSet.add(dir);
        // Look for nested projects to open as well:
        Enumeration e = dir.getFolders(true);
        while (e.hasMoreElements()) {
            FileObject subfolder = (FileObject) e.nextElement();
            if (ProjectManager.getDefault().isProject(subfolder)) {
                resultSet.add(subfolder);
            }
        }

        File parent = dirF.getParentFile();
        if (parent != null && parent.exists()) {
            ProjectChooser.setProjectsFolder(parent);
        }

        return resultSet;
    }

    @Override
    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;

        index = 0;
        panels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                // Step #.
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps);
            }
        }
    }

    @Override
    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty("projdir", null);
        this.wiz.putProperty("name", null);
        this.wiz = null;
        panels = null;
    }

    @Override
    public String name() {
        return MessageFormat.format("{0} of {1}",
                new Object[]{new Integer(index + 1), new Integer(panels.length)});
    }

    @Override
    public boolean hasNext() {
        return index < panels.length - 1;
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

    @Override
    public WizardDescriptor.Panel current() {
        return panels[index];
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public final void addChangeListener(ChangeListener l) {
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
    }

    
}
