Here we have modified version of Recast made by Mikko Monone from: https://github.com/memononen/recastnavigation

It has been stripped of its GUI and tuned (parametrized) for exporting NavMesh for UT2004
assuming you are using standard bot (with standard dimensions).

navmesh-one.bat

  Creates navmesh out of one set (.obj, .center, .scale) of files. 
  The set **MUST** be located within Meshes/ directory.
  
  USAGE: navmesh-one.bat <file-name>
  
  EXAMPLE: assuming you have DM-1on1-TrainingDay.obj/.scale/.centre within Meshes/ directory
           navmesh-one.bat DM-1on1-TrainingDay.obj

navmesh-all.bat

  Creates navmesh for everything from the Meshes/ directory.    
  
  USAGE: navmesh-all.bat
  
  EXAMPLES: as it does not need any specific arguments ... well ;-)
