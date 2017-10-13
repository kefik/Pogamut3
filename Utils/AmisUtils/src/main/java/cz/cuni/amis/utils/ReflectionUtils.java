/*
 * Copyright (C) 2013 Martin Cerny
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.cuni.amis.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Utility methods for reading and processing annotations of objects and their members.
 * @author Martin Cerny
 */
public class ReflectionUtils {

    public ReflectionUtils() {
        throw new RuntimeException("Cannot instantiate static class");
    }

    public static interface ProcessAnnotatedMethodCallback<T extends java.lang.annotation.Annotation> {
        void processMethod(Method m, T annotation);
    }
    
    public static interface ProcessAnnotatedFieldCallback<T extends java.lang.annotation.Annotation, E extends Throwable> {
        void processField(Field f,Object fieldValue, T annotation) throws E;
    }

    public static interface ProcessFieldCallback<E extends Throwable> {
        void processField(Field f) throws E;
    }

    public static <E extends Throwable>  void processEachDeclaredNonStaticField(Object targetObject, ProcessFieldCallback<E> callBack) throws E{
        processEachDeclaredNonStaticField(targetObject,Object.class, callBack);
    }
    
    public static <E extends Throwable>  void processEachDeclaredNonStaticField(Object targetObject, Class rootClass,ProcessFieldCallback<E> callBack) throws E{
        if(!rootClass.isAssignableFrom(targetObject.getClass())){
            throw new IllegalArgumentException("TargetObject must be instance of rootClass");
        }
        Class inspectedClass = targetObject.getClass();
        while(inspectedClass != null && rootClass.isAssignableFrom(inspectedClass)){
            for(Field f : inspectedClass.getDeclaredFields()){
                if(Modifier.isStatic(f.getModifiers())){
                    continue;
                }
                callBack.processField(f);
            }
            inspectedClass = inspectedClass.getSuperclass();
        }

    }

    public static <E extends Throwable>  void processEachDeclaredFieldOfClass(Class targetClass, ProcessFieldCallback<E> callBack) throws E{
        processEachDeclaredFieldOfClass(targetClass,Object.class, callBack);
    }
    
    public static <E extends Throwable>  void processEachDeclaredFieldOfClass(Class targetClass, Class rootClass,ProcessFieldCallback<E> callBack) throws E{
        if(!rootClass.isAssignableFrom(targetClass)){
            throw new IllegalArgumentException("TargetClass must be descendant of rootClass");
        }
        Class inspectedClass = targetClass;
        while(inspectedClass != null && rootClass.isAssignableFrom(inspectedClass)){
            for(Field f : inspectedClass.getDeclaredFields()){
                callBack.processField(f);
            }
            inspectedClass = inspectedClass.getSuperclass();
        }

    }
    

    public static <T extends java.lang.annotation.Annotation, E extends Throwable>  void processEachAnnotatedDeclaredField(final Object targetObject, final Class<T> annotationClass, final ProcessAnnotatedFieldCallback<T, E> callBack) throws E{
        processEachAnnotatedDeclaredField(targetObject, Object.class, annotationClass, callBack);
    }

    public static <T extends java.lang.annotation.Annotation, E extends Throwable>  void processEachAnnotatedDeclaredField(final Object targetObject, final Class rootClass, final Class<T> annotationClass, final ProcessAnnotatedFieldCallback<T, E> callBack) throws E{

        processEachDeclaredNonStaticField(targetObject, rootClass, new ProcessFieldCallback<E>() {

            @Override
            public void processField(Field f) throws E {
                if(f.isAnnotationPresent(annotationClass)){
                    Object fieldValue;
                    try {
                         fieldValue = PropertyUtils.getProperty(targetObject, f.getName());
                    } catch (Exception ex) {
                        throw new IllegalStateException("Could not read property value for annotated property '" + f.getName() + "' in class" + f.getDeclaringClass().getName() + "\nThe property should have public getter.", ex);
                    }
                    callBack.processField(f, fieldValue, f.getAnnotation(annotationClass));
                }
            }
        });

        
    }

    
    public static <T extends java.lang.annotation.Annotation>  void processEachAnnotatedDeclaredMethod(final Object targetObject, final Class<T> annotationClass, final ProcessAnnotatedMethodCallback<T> callBack){
        processEachAnnotatedDeclaredMethod(targetObject, Object.class, annotationClass, callBack);
    }
    
    public static <T extends java.lang.annotation.Annotation>  void processEachAnnotatedDeclaredMethod(final Object targetObject, final Class rootClass, final Class<T> annotationClass, final ProcessAnnotatedMethodCallback<T> callBack){

        if(!rootClass.isAssignableFrom(targetObject.getClass())){
            throw new IllegalArgumentException("TargetObject must be instance of rootClass");
        }
        Class inspectedClass = targetObject.getClass();
        while(inspectedClass != null && rootClass.isAssignableFrom(inspectedClass)){
            for(Method f : inspectedClass.getDeclaredMethods()){
                if(f.isAnnotationPresent(annotationClass)){
                    callBack.processMethod(f,f.getAnnotation(annotationClass));
                }
            }
            inspectedClass = inspectedClass.getSuperclass();
        }
        
    }
    
}
