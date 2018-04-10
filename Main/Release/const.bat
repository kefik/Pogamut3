REM Points to directory that contains GIT from https://github.com/kefik/Pogamut3.git master branch cloned
set POGAMUT_RELEASE_BASEDIR=E:\W\PogRelGit

REM Current snapshot version of artifacts
set POGAMUT_CURRENT_VERSION=3.8.1-SNAPSHOT

REM Previous release version
set POGAMUT_PREVIOUS_RELEASE_VERSION=3.8.0

REM Version we want to release
set POGAMUT_RELEASE_VERSION=3.8.1

REM New snapshot version to which we want Pogamut artifacts to elevate after release
set POGAMUT_NEW_SNAPSHOT_VERSION=3.8.2-SNAPSHOT

REM Sets JAVA_HOME to JDK 7
set JAVA_HOME=c:\Program Files\Java\jdk1.8.0_65\

REM Updates path to JAVA
set PATH=c:\Program Files\Java\jdk1.8.0_65\bin\;%PATH%

REM Ensure correct Maven opts...
set MAVEN_OPTS=-Xmx3g -Xms256m

REM Ensure Java version
java -version
