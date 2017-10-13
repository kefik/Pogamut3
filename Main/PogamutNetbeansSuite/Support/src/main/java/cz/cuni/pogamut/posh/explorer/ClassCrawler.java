package cz.cuni.pogamut.posh.explorer;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.executor.IAction;
import cz.cuni.amis.pogamut.sposh.executor.ISense;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
import java.io.IOException;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.project.Project;

/**
 * Crawler looking for instances of {@link IAction}
 * @author Honza
 */
class IActionCrawler extends ClassCrawler {

    public IActionCrawler(Project project) {
        // TODO: connect sposh core to this project and use IAction.class.getCanonicalName() instaed of fqn string
        super(project, "cz.cuni.amis.pogamut.sposh.executor.IAction");
    }

    @Override
    public String getName() {
        return "Action";
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

/**
 * Crawler looking for instances of {@link ISense}
 * @author Honza
 */
class ISenseCrawler extends ClassCrawler {

    public ISenseCrawler(Project project) {
        // TODO: connect sposh core to this project and use ISense.class.getCanonicalName() instaed of fqn string
        super(project, ISense.class.getCanonicalName());
    }

    @Override
    public String getName() {
        return "Sense";
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

/**
 * Crawl through all class and find the ones that fit the filter.
 * This crawler doesn't invoke any extra threads, all crawling is done in 
 * the {@link #crawl() } method.
 * @author Honza
 */
abstract class ClassCrawler extends AbstractCrawler<PrimitiveData> {

    /**
     * Project we will crawl over, source of classpath.
     */
    protected final Project project;
    protected final String fqn;

    /**
     * Create new crawler that will go through project
     * @param project project in which (sources and dependencies) we will crawl
     */
    protected ClassCrawler(Project project, String fqn) {
        this.project = project;
        this.fqn = fqn;
    }

    @Override
    public final synchronized void crawl() {
        ClasspathInfo cpi = getClasspathInfo(project);
        ClassIndex ci = cpi.getClassIndex();
        ElementHandle<TypeElement> ancestor = getTypeOfClass(ci, fqn);

        // if ancestor in not on the classpath, don't do anything.
        if (ancestor == null) {
            return;
        }

        notifyStarted();
        // get all implementors
        Set<ElementHandle<TypeElement>> list = getAllSubtypes(ci, ancestor);

        JavaSource js = JavaSource.create(cpi);
        try {
            FilterAbstract filter = new FilterAbstract(list);
            // Filter will remove some elements from list (inplace)
            js.runUserActionTask(filter, true);

            // notify listeners about new data
            notifyCrawledData(filter.getResult());
            notifyFinished(false);
            return;
        } catch (IOException ex) {
            // TODO: What exact error?
            list.clear();
            notifyFinished(true);
            return;
        }
    }

    /**
     * Allow only non-abstract types
     */
    private static class FilterAbstract implements Task<CompilationController> {

        /** Set of elements to be filtered */
        private final Set<ElementHandle<TypeElement>> set;
        /** Result, only valid entries are put here */
        private Set<PrimitiveData> result;
        private static final Logger log = Logger.getLogger(FilterAbstract.class.getName());

        FilterAbstract(Set<ElementHandle<TypeElement>> set) {
            this.set = set;
        }

        /**
         * Get result, i.e. filtered data.
         * @return
         */
        public Set<PrimitiveData> getResult() {
            assert result != null;
            return result;
        }

        /**
         * Is the class public?
         * @param type type of passed type
         * @return true if type is public
         */
        private boolean isPublic(TypeElement type) {
            return type.getModifiers().contains(Modifier.PUBLIC);
        }

        /**
         * Is the type abstract?
         * @param type type of passed type
         * @return true if type is abstract
         */
        private boolean isAbstract(TypeElement type) {
            return type.getModifiers().contains(Modifier.ABSTRACT);
        }

        /**
         * Get type mirror for the class with specified fqn.
         * @param cc Compilation controller
         * @param classFQN fully qualified name of the class
         * @return {@link TypeMirror} of the class or null, if not available.
         */
        private TypeMirror getTypeMirror4Class(CompilationController cc, String classFQN) {
            TypeElement te = cc.getElements().getTypeElement(classFQN);
            if (te == null) {
                return null;
            }
            return te.asType();
        }

        /**
         * Find specified annotation for some element.
         * @param element element where we look for annotation.
         * @param annotationType Type of annotation we are looking for.
         * @return the annotation mirror of same type as annotationType or null if doesn't exist
         */
        private AnnotationMirror findAnnotationMirror(Element element, TypeMirror annotationType) {
            for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
                // FIXME: use proper comparison of TypeMirror, Types.isSameType, but no idea how to instantiate it
                if (annotationType.toString().equals(annotationMirror.getAnnotationType().toString())) {
                    return annotationMirror;
                }
            }
            return null;
        }

        /**
         * Get value of an annotation and return it.
         * @param <T> Class of object we expect to find as the value
         * @param cls Parameter to specify the class of an object in the annotation.
         * @param elementName name of element the annotation is annotating.
         * @param attributeName Name of an annotation attribute
         * @param annotationValue object that stores the annotation value.
         * @return annotation object or null, if annotation value is not of a correct type.
         */
        private <T> T getAnnotationValue(Class<T> cls, String elementName, String attributeName, AnnotationValue annotationValue) {
            Object value = annotationValue.getValue();
            if (cls.isInstance(value)) {
                return (T) value;
            } 
            log.warning("Element " + elementName + " has an annotation with with attribute \"" + attributeName + "\", that should be " + cls.getCanonicalName() + ", but it " + value.getClass().getCanonicalName() + ".");
            return null;
        }

        private PrimitiveData getInfo(String classFQN, AnnotationMirror annotation) {
            String name = null;
            String description = null;
            List<String> tags = Collections.EMPTY_LIST;

            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues().entrySet()) {
                // name
                Name entryName = entry.getKey().getSimpleName();
                AnnotationValue entryValue = entry.getValue();
                if (entryName.contentEquals("name")) {
                    name = getAnnotationValue(String.class, classFQN, "name", entryValue);
                } else if (entryName.contentEquals("description")) {
                    description = getAnnotationValue(String.class, classFQN, "description", entryValue);
                } else if (entryName.contentEquals("tags")) {
                    List<AnnotationValue> tagList = getAnnotationValue(List.class, classFQN, "tags", entryValue) ;
                    if (tagList != null) {
                        tags = new ArrayList<String>(tagList.size());
                        for (AnnotationValue tagValue : tagList) {
                            String tag = getAnnotationValue(String.class, classFQN, "tags", tagValue);
                            if (tag != null) {
                                tags.add(tag);
                            }
                        }
                    }
                }
            }
            return new PrimitiveData(classFQN, name, description, tags.toArray(new String[tags.size()]));
        }

        /**
         * Go through all classes in the set and check which ones are acceptable to be filtered out.
         * In this case, we require classes to be
         * <ul>
         *  <li>nonabstract</li>
         *  <li>public</li>
         * </ul>
         * @param cc
         * @throws Exception
         */
        @Override
        public void run(CompilationController cc) throws Exception {
            String annotationFQN = PrimitiveInfo.class.getCanonicalName();
            TypeMirror annotationType = getTypeMirror4Class(cc, annotationFQN);

            if (annotationType == null) {
                throw new ClassNotFoundException("Unable to find class " + annotationFQN);
            }

            result = new HashSet<PrimitiveData>();

            for (ElementHandle<TypeElement> element : set) {
                TypeElement type = element.resolve(cc);
                if (!isAbstract(type) && isPublic(type)) {
                    String classFQN = element.getQualifiedName();

                    // Go through annotation of class if exists and fill the data
                    AnnotationMirror annotation = findAnnotationMirror(type, annotationType);
                    if (annotation != null) {
                        result.add(getInfo(classFQN, annotation));
                    } else {
                        result.add(new PrimitiveData(classFQN, null, null, new String[0]));
                    }
                }
            }
        }
    }

    @Override
    public void die() {
        // no need, everything is done 
    }
}

final class CompCrawler extends AbstractCrawler<Competence> {

    private PoshPlan plan;

    CompCrawler(PoshPlan plan) {
        this.plan = plan;
    }

    @Override
    public String getName() {
        return "competence";
    }

    @Override
    public String getDescription() {
        return "Competence is an inner tree of posh decision tree.";
    }

    @Override
    public void crawl() {
        notifyStarted();
        notifyCrawledData(plan.getCompetences());
        notifyFinished(false);
    }

    @Override
    public void die() {
    }
}

final class APCrawler extends AbstractCrawler<ActionPattern> {

    private PoshPlan plan;

    APCrawler(PoshPlan plan) {
        this.plan = plan;
    }

    @Override
    public String getName() {
        return "action pattern";
    }

    @Override
    public String getDescription() {
        return "Pattern is a sequence of actions that are executed in specified order.";
    }

    @Override
    public void crawl() {
        notifyStarted();
        notifyCrawledData(plan.getActionPatterns());
        notifyFinished(false);
    }

    @Override
    public void die() {
    }
}
