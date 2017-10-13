ALL-IN-ONE-NAVMESH MAKER

Works only under Windows ... depends on UShock.exe and RecastDemo.exe in tools.
You should check first whether these two files can be executed on your system
before carrying on.

DESCRIPTION:

  UT2004-NavMesh-Maker is all-in one tool to extract NavMesh from any/all maps 
  of your UT2004 installation.
  
USAGE:
  
  java -jar ut2004-navmesh-maker-3.6.2-SNAPSHOT.one-jar.jar -u <PATH-TO-UT2004-HOME-DIR> -m <SEMICOLON-SEPARATED-LIST-OF-MAP-NAMES>
  
EXAMPLE:

  java -jar ut2004-navmesh-maker-3.6.2-SNAPSHOT.one-jar.jar -u D:\Games\UT2004 -m DM-TrainingDay -c
  ... exports navmesh for DM-TrainingDay
  
  java -jar ut2004-navmesh-maker-3.6.2-SNAPSHOT.one-jar.jar -u D:\Games\UT2004 -m DM-TrainingDay;DM-Flux2 -c
  ... exports navmesh for DM-TrainingDay, DM-Flux2
  
  java -jar ut2004-navmesh-maker-3.6.2-SNAPSHOT.one-jar.jar -u D:\Games\UT2004 -a -c
  ... exports navmesh for all maps within UT2004