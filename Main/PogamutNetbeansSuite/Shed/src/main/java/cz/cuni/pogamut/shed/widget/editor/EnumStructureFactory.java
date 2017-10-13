package cz.cuni.pogamut.shed.widget.editor;

import com.sun.source.tree.ClassTree;
import com.sun.source.util.TreePathScanner;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;

/**
 * We need to get all values of an enum in the NB editor and that is what this
 * factory is for.
 *
 */
public class EnumStructureFactory {

    private EnumStructureFactory() {
    }

    private static JavaSource getJavaSource(String enumFQN) {
        String enumResourcePath = convertFQNToResourcePath(enumFQN);
        FileObject fileObject = getFileObject(enumResourcePath);
        if (fileObject == null) {
            return null;
        }
        return JavaSource.forFileObject(fileObject);
    }

    public static boolean isEnum(String enumFQN) {
        JavaSource js = getJavaSource(enumFQN);
        if (js == null) {
            return false;
        }
        try {
            ConfirmEnumTask confirmEnumTask = new ConfirmEnumTask(enumFQN);
            js.runUserActionTask(confirmEnumTask, true);
            return confirmEnumTask.foundEnum();
        } catch (IOException ex) {
            return false;
        }
    }

    private static class ConfirmEnumTask implements Task<CompilationController> {

        private final String enumFQN;
        private boolean foundEnum  = false;

        public ConfirmEnumTask(String enumFQN) {
            this.enumFQN = enumFQN;
        }

        @Override
        public void run(CompilationController parameter) throws Exception {
            parameter.toPhase(Phase.ELEMENTS_RESOLVED);
            List<? extends TypeElement> tles = parameter.getTopLevelElements();
            for (TypeElement tle : tles) {
                ElementKind kind = tle.getKind();
                boolean isEnum = (kind == ElementKind.ENUM);
                boolean hasSameName = enumFQN.equals(tle.getQualifiedName().toString());
                if (isEnum && hasSameName) {
                    foundEnum = true;
                    break;
                }
            }
        }

        private boolean foundEnum() {
            return foundEnum;
        }
        
        
    }

    /**
     * Get all values of the enum.
     *
     * @param enumFQN FQN name of the enum.
     * @return All values of the enum. Null if not able to find the enum.
     */
    public static List<String> getValues(String enumFQN) {
        JavaSource js = getJavaSource(enumFQN);
        if (js == null) {
            return null;
        }
        try {
            CollectEnumConsts collectorTask = new CollectEnumConsts();
            js.runUserActionTask(collectorTask, true);
            List<String> enumConsts = collectorTask.getCollectedConsts();
            if (enumConsts == null) {
                return null;
            }
            return enumConsts;
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Try to find specified resource in the {@link GlobalPathRegistry}.
     *
     * @param classResource Path to resource file, e.g. cz/cuni/TestEnum.java
     * @return Found object or null if not found
     */
    private static FileObject getFileObject(String classResource) {
        for (FileObject curRoot : GlobalPathRegistry.getDefault().getSourceRoots()) {
            FileObject fileObject = curRoot.getFileObject(classResource);
            if (fileObject != null) {
                return fileObject;
            }
        }
        return null;
    }

    private static String convertFQNToResourcePath(String enumFQN) {
        return enumFQN.replace('.', '/').concat(".java");
    }

    private static class CollectEnumConsts implements Task<CompilationController> {

        private EnumCollector collector;

        @Override
        public void run(CompilationController parameter) throws IOException {
            parameter.toPhase(Phase.ELEMENTS_RESOLVED);
            collector = new EnumCollector(parameter);
            collector.scan(parameter.getCompilationUnit(), null);
        }

        private List<String> getCollectedConsts() {
            return collector.getEnumConstants();
        }
    }

    /**
     * Class is a scanner that goes over the enum and once scan is finished, you
     * can retrieve collected constants from {@link #getEnumConstants() }.
     */
    private static class EnumCollector extends TreePathScanner<Void, Void> {

        private CompilationInfo info;
        private List<String> enumConsts;

        public EnumCollector(CompilationInfo info) {
            this.info = info;
        }

        public synchronized List<String> getEnumConstants() {
            return enumConsts;
        }

        @Override
        public synchronized Void visitClass(ClassTree t, Void v) {
            Element el = info.getTrees().getElement(getCurrentPath());
            if (el == null) {
                return null;
            }
            TypeElement te = (TypeElement) el;
            List<VariableElement> fields = ElementFilter.fieldsIn(te.getEnclosedElements());
            enumConsts = new LinkedList<String>();
            for (VariableElement field : fields) {
                if (field.getKind() == ElementKind.ENUM_CONSTANT) {
                    String name = field.getSimpleName().toString();
                    enumConsts.add(name);
                }
            }
            return null;
        }
    }
}
