POGAMUT RELEASE HELPER
======================

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

5) GOOD LUCK :-)