REM use Java 1.6!
cd ..\..\..
rmdir /S /Q Main\PogamutRelease\target\javadoc-emohawk
mkdir Main\PogamutRelease\target\javadoc-emohawk
javadoc.exe -doclet org.jboss.apiviz.APIviz -docletpath Main/PogamutRelease/release-javadoc/apiviz-1.3.1.GA.jar -classpath "Main/PogamutRelease/release-javadoc/junit-4.8.2.jar;Main/PogamutRelease/release-javadoc/xstream-1.3.1.jar;%JAVA_HOME%/jre/lib/rt.jar;Main/PogamutRelease/release-javadoc/guice-2.0.jar" -d Main/PogamutRelease/target/javadoc-emohawk -subpackages cz -sourcepath Utils/AmisUtils/src/main/java;Utils/AFSM/src/main/java;Utils/JavaGeom/src/main/java;Main/PogamutBase/src/main/java;Main/PogamutUnreal/src/main/java;Main/PogamutUT2004/src/main/java;Main/PogamutEmohawk/src/main/java  -sourceclasspath Utils/AmisUtils/target/classes -sourceclasspath Utils/AFSM/target/classes -sourceclasspath Utils/JavaGeom/target/classes -sourceclasspath Main/PogamutBase/target/classes -sourceclasspath Main/PogamutUnreal/target/classes -sourceclasspath Main/PogamutUT2004/target/classes -sourceclasspath Main/PogamutEmohawk/target/classes
exit
