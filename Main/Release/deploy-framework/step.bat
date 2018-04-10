set MY_DIR=%CD%
cd ..
call step deploy-framework/%1
cd %MY_DIR%