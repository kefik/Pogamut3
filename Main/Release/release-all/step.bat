set MY_DIR=%CD%
cd ..
call step release-all/%1
cd %MY_DIR%