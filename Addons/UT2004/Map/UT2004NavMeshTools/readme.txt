This directory contains all tools you need to export NavMesh
from arbitrary UT2004 map (arbitrary to that extens an UShock can handle,
there are some maps it cannot parse for unknown reason).

ALL UT2004 NAVMESHES CAN BE FOUND WITHIN: 04-NavMeshes
... some may be missing because they need "manual build" with "manually entered Recast parameters"
    or because their maps could not have been read by UShock


============
PREREQUISITS
============

1. UT2004 installation with maps you want to process.

2. Note that you might need to install vcredist_x86.exe first in order
for all tools to be usable.

3. Windows OS


========
SHORTCUT
========

If you are on Windows system you might try automatic NavMesh extraction via UT2004-NavMesh-Maker tool
that you may find within directory 05-NavMeshMaker.

If you encounter any problems along the way, you might need fallback to manual way described below.


======
STEP 1
======

Directory: 01-UShock-Exporting_level_geometry

  In this step, you will export level geometries of all UT2004 maps from
  specified UT2004 installation.
  Note that an XML file holding map geomtry for a large map can be as big as 50-60MB!

  1. make sure that 01-UShock-Exporting_level_geometry/output directory exists
  
  2. go into 01-UShock-Exporting_level_geometry and export-all.bat <path-to-ut2004-home-dir>
  
  Check 01-UShock-Exporting_level_geometry/readme.txt for more info.
  
  
======
STEP 2
======

Directory: 02-UT2004LevelGeom-Preparing_data_for_Recast

  In this step we will parse and transform XML map data from STEP 1 into data
  that can be interpreted by Recast.
  
  In order to do this, we will need not only XML map file but also access
  to UT2004 installation having the map installed as we will need to extract
  additional infromation about the map from UT2004.
  
  1. copy XML files 
     from: 01-UShock-Exporting_level_geometry/output
     into: 02-UT2004LevelGeom-Preparing_data_for_Recast/input
     
  2. make sure that 02-UT2004LevelGeom-Preparing_data_for_Recast/output folder exists
  
  3. go into 02-UT2004LevelGeom-Preparing_data_for_Recast directory and use transform-all.bat <path-to-ut2004-home-dir>
  
  Check 02-UT2004LevelGeom-Preparing_data_for_Recast/readme.txt for more info.
  
  
======
STEP 3
======

Directory: 03-Recast-Obtaining_NavMesh

  In this final step, we will create the navmesh we need.
  
  In order to do this, you will need all files produced during the STEP 2.
  
  1. copy everything
     from: 02-UT2004LevelGeom-Preparing_data_for_Recast/output
     into: 03-Recast-Obtaining_NavMesh/Meshes
     
  2. make sure that 03-Recast-Obtaining_NavMesh/output directory exists
  
  3. go into 03-Recast-Obtaining_NavMesh and run navmesh-all.bat
  
  Check 03-Recast-Obtaining_NavMesh/readme.txt for more info.
  
======
STEP 4
======

CONGRATULATION! You've managed to create NavMesh files for UT2004 maps!
Now how to use them?



  