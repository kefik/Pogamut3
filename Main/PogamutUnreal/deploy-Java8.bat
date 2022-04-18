set JAVA_HOME=c:\Program Files\Java\jdk1.8.0_281\
set PATH=c:\Program Files\Java\jdk1.8.0_281\bin\;%PATH%
java -version
mvn deploy -Dmaven.test.skip=true
