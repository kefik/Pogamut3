package cz.cuni.pogamut.posh.explorer;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClassIndex.NameKind;
import org.netbeans.api.java.source.ClassIndex.SearchScope;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.java.classpath.ClassPathProvider;

/**
 * Abstract crawler that is going through the sources and tries to find classes with
 * specified characteristics ().
 * @author Honza
 */
abstract class AbstractCrawler<T> extends Crawler<T> {

    /**
     * Get {@link ClasspathInfo} for project. In most cases, you are interested in {@link ClassIndex} you can retrieve using
     * {@link ClasspathInfo#getClassIndex() }.
     * If you need to owner project of some file, use {@link FileOwnerQuery}.
     * @param project What project should I use to determine classpath?
     * @return classpath for project that contains {@link ClassPath#BOOT}, {@link ClassPath#COMPILE}, and {@link ClassPath#SOURCE}
     */
    protected final ClasspathInfo getClasspathInfo(Project project) {
        ClassPathProvider cpp = project.getLookup().lookup(ClassPathProvider.class);
        assert cpp != null;
        
        // FIXME: should I use sources of project? TEST!!! What about multiple sources?
        Sources sources = project.getLookup().lookup(Sources.class);
        assert sources != null;
        
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        assert groups.length != 0;
        SourceGroup group = groups[0];
        
        ClassPath bootPath = cpp.findClassPath(group.getRootFolder(), ClassPath.BOOT);
        ClassPath compilePath = cpp.findClassPath(group.getRootFolder(), ClassPath.COMPILE);
        ClassPath srcPath = cpp.findClassPath(group.getRootFolder(), ClassPath.SOURCE);

        return ClasspathInfo.create(bootPath, compilePath, srcPath);
    }

    /**
     * Get simple name from fqn of class
     * @param fqn fully qualified name of some class, like  (like java.lang.String)
     * @return simple name, like String
     */
    protected final String getSimpleNameFromFQN(String fqn) {
        int lastIndex = fqn.lastIndexOf(".");
        if (lastIndex == -1) {
            return fqn;
        } else {
            return fqn.substring(lastIndex + 1);
        }
    }

    /**
     * Find the ElementHandle of TypeElement of clazz in the passed ClassIndex.
     * Look in boot, compile and sources.
     * @param ci ClassIndex in which we are looking.
     * @param clazz class for which TypeElement we are looking
     * @return null if no such EH found in ci otherwise first found class with same fqn.
     */
    protected final ElementHandle<TypeElement> getTypeOfClass(ClassIndex ci, String classfqn) {
        String searchedString = getSimpleNameFromFQN(classfqn);
        NameKind kind = NameKind.SIMPLE_NAME;
        Set<SearchScope> searchScope = EnumSet.of(SearchScope.DEPENDENCIES, SearchScope.SOURCE);
        Set<ElementHandle<TypeElement>> res = ci.getDeclaredTypes(searchedString, kind, searchScope);

        // Find if there is a ElementHandle<TypeElement> with same FQN as the ancestor.
        for (ElementHandle<TypeElement> eh : res) {
            String qn = eh.getQualifiedName();
            if (classfqn.equals(qn)) {
                return eh;
            }
        }
        return null;
    }

    /**
     * Find all types that directly (=not transitively) implement ancestor type (extends/implemet).
     * Search in boot, compile and sources.
     * @param ci classindedx in which we are looking
     * @param ancestor ancestor type
     * @return set of implementors
     */
    protected final Set<ElementHandle<TypeElement>> getDirectSubtypes(ClassIndex ci, ElementHandle<TypeElement> ancestor) {
        Set<ClassIndex.SearchKind> sk = EnumSet.of(ClassIndex.SearchKind.IMPLEMENTORS);
        Set<ClassIndex.SearchScope> sc = EnumSet.of(SearchScope.DEPENDENCIES, SearchScope.SOURCE);

        return ci.getElements(ancestor, sk, sc);
    }

    /**
     * Get all subtypes (either implemetors or subtypes) of certain TypeElement in the ClassIndex.
     * I.e. find all {@link TypeElement types} that either (transitively) extend or implement the ancestor.
     * @param ci ClassIndex in which we are loooking
     * @param ancestorTE type element that is the ancestor. 
     * @return set of all types that extend / implement ancestor
     */
    protected final Set<ElementHandle<TypeElement>> getAllSubtypes(ClassIndex ci, ElementHandle<TypeElement> ancestorTE) {
        Set<ElementHandle<TypeElement>> result = new HashSet<ElementHandle<TypeElement>>();
        LinkedList<ElementHandle<TypeElement>> todo = new LinkedList<ElementHandle<TypeElement>>();
        todo.add(ancestorTE);

        while (!todo.isEmpty()) {
            // the element we will process in this loop
            ElementHandle<TypeElement> curr = todo.removeFirst();
            // All extending of the curr
            Set<ElementHandle<TypeElement>> subtypesOfCurr = getDirectSubtypes(ci, curr);

            if (subtypesOfCurr != null) {// can be null for ancellable tasks
                result.addAll(subtypesOfCurr);
                todo.addAll(subtypesOfCurr);
            }
        }
        return result;
    }
}
