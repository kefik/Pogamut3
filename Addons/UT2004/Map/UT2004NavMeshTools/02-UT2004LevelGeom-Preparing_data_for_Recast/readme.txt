transform-one.bat

  Transforms data for RECAST for one file.
  
  It will create two supplement files within the directory of an XML file named <file-name>.envelope and <file-name.jumppads>.
  Envelope file contains x/y/z span of the level and jumppads file contains list of jumppads within the level.
  
  And it will create three files within output/ directory with extensions centre, obj and scale.
  .obj file is ready to be imported and processed by Recast. It contains
  level geometry that has been translated (by values stored within .centre file)
  and scaled (by values stored within .scale file). Note that you will need .centre and .scale
  file later in order for PogamutUT2004 to be able to fit the NavMesh onto the level.
  
  USAGE: transform-one.bat <path-to-xml-file-exported-by-ushock> <path-to-ut2004-home-dir>
  
  EXAMPLE: transform-one.bat input/DM-1on1-Roughinery.xml D:/Games/UT2004

transform-input.bat

  Takes all XML files exported by UShock from intput/ folder.
  You can obtain already exportes XMLs for standard UT2004 maps from:
  svn://artemis.ms.mff.cuni.cz/pogamut/trunk/project/Addons/UT2004NavMeshTools/01-UShock-Exporting_level_geometry/output

  USAGE: transfrom-input.bat <path-to-ut2004-home-dir>
  
  EXAMPLE: transform-input.bat D:/Games/UT2004