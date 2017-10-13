POGAMUT RELEASE HELPER
======================

... do not forget to read tips at the end of this file ...

This project makes release of some version much easier as it automate a lot of tasks:

1) it rewrites snapshot versions in poms to their release counterpart
2) deploys release versions to artifactory
3) builds the installer
4) refreshes archetypes / deploys them
5) rewrites release version to new snapshot version

NOTE: you may set POGAMUT_RELEASE_BASEDIR environment property to point to a directory
where you have checkout complete mavenized branch, e.g.:

POGAMUT_RELEASE_BASEDIR
   \-- Addons
   \-- Archetypes
   \-- Main
   \-- Maven
   \-- Poms
   \-- Utils
   
E.g.: in win32 console execute: set POGAMUT_RELEASE_BASEDIR=c:\workspaces\pogamut-maven   

----------------------------

Steps which must be done manually:

0) make sure you know what you're doing ... read PogamutRelease-steps.txt

1) adjust versions rewriting

2) make sure that generated installer is OK

3) manually update artifactory catalog

4) make sure that commit was OK

5) GOAL typically needs to manually download workspace from Jenkins (as you will probably won't have an access to TU-Delft SVN repository)
-- see projects under GOAL tab within Jenkins: http://diana.ms.mff.cuni.cz:8080/view/GOAL/
-- inspect which projects you need within: release-all/config/PogamutRelease-step00-DeployOldSnapshots-Libs.xml
   -- look for comment "GOAL"
-- if required, sync project builds

6) have "mvn" on path

7) have "SVN_HOME" defined (e.g. d:\svn ... WITHOUT /bin)

8) GOOD LUCK :-)

=============
TIPS & TRICKS
=============

1) be sure use java 1.6 for compile / package / deploy

2) site site:deploy can be build with arbitrary java

3) MAVEN_OPTS=-Xmx1350m -Xms128m

WARNING: PogamutUT2004 site site:deploy is demanding ... usage of 64bit java with MAVEN_OPTS=-Xmx3g -Xms256m is advised!

4) if you encounter error "The authenticity of host 'www.whatever.com' can't be established" try to create
empty file at C:\Windows\User\username\.ssh\known_hosts or run deploy / site:deploy of concrete failing project to see
whether it does not ask for you to "confirm the RSA fingerprint"

5) be sure that you have 
<server>
  <id>amis-maven-sites-server</id>
  <username>maven-sites-uploader</username>
  <password>ask jakub gemrot</password>
</server>
within your settings.xml, ask jakub.gemrot@gmail.com for password

6) if you still encounter problem with site:deploy, try to do it for the first project manually, it will ask you whether to trust diana.ms.mff.cuni.cz certificate