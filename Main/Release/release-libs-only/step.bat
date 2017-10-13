set MY_DIR=%CD%
cd ..
call step.bat release-all/%1
cd %MY_DIR%